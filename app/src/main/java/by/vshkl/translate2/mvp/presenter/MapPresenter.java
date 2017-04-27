package by.vshkl.translate2.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.vshkl.translate2.R;
import by.vshkl.translate2.database.local.LocalRepository;
import by.vshkl.translate2.database.remote.FirebaseRepository;
import by.vshkl.translate2.mvp.mapper.StopBookmarkMapper;
import by.vshkl.translate2.mvp.mapper.StopMapper;
import by.vshkl.translate2.mvp.model.MarkerWrapper;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import by.vshkl.translate2.mvp.model.Version;
import by.vshkl.translate2.mvp.view.MapView;
import by.vshkl.translate2.util.RxUtils;
import io.reactivex.disposables.Disposable;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> {

    private Disposable disposable;
    private List<Stop> stops;
    private List<StopBookmark> stopBookmarks;
    private HashMap<Integer, MarkerWrapper> visibleMarkers;
    private long updatedTimestamp;
    private int selectedStopId;

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    public void downloadUpdate(DownloadManager downloadManager, Version version) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(version.getLink()));
        request.setTitle("TransLate")
                .setDescription(version.getFilename())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, version.getFilename());

        downloadManager.enqueue(request);
    }

    public void getUpdatedTimestampFromRemoteDatabase() {
        disposable = FirebaseRepository.getUpdatedTimestamp()
                .compose(RxUtils.applySchedulers())
                .subscribe(updated -> {
                    updatedTimestamp = updated.getUpdatedTimestamp();
                    isOutdatedOrNotExists(updatedTimestamp);
                });
    }

    public void getLatestVersionInfoFromRemoteDatabase(int ignoreUpdateVersion) {
        disposable = FirebaseRepository.getLatestVersion()
                .compose(RxUtils.applySchedulers())
                .subscribe(version -> {
                    if (version.getVersionCode() > ignoreUpdateVersion) {
                        getViewState().showNewVersionAvailable(version);
                    }
                });
    }

    public void getAllStopsFromRemoteDatabase() {
        getViewState().showProgressBar();
        disposable = FirebaseRepository.getAllStops()
                .compose(RxUtils.applySchedulers())
                .subscribe(stops -> {
                    getViewState().hideProgressBar();
                    this.stops = stops;
                    saveAllStopsToLocalDatabase();
                    placeMarkers();
                    getViewState().showMessage(R.string.map_message_stops_updated);
                });
    }

    public void getAllStopsFromLocalDatabase() {
        disposable = LocalRepository.loadStops()
                .compose(RxUtils.applySchedulers())
                .subscribe(stopEntities -> {
                    stops = StopMapper.transform(stopEntities);
                    placeMarkers();
                });
    }

    public void getStopById(int stopId, boolean fromNavDrawer) {
        selectedStopId = stopId;
        if (stops != null && stopBookmarks != null) {
            for (Stop stop : stops) {
                if (stop.getId() == stopId) {
                    System.out.println();
                    getViewState().showSelectedStop(stop,
                            stopBookmarks.contains(new StopBookmark(stop.getId(), stop.getName())),
                            fromNavDrawer);
                }
            }
        }
    }

    public void getAllStopBookmarksFromLocalDatabase() {
        disposable = LocalRepository.loadStopBookmarks()
                .compose(RxUtils.applySchedulers())
                .subscribe(stopBookmarkEntities -> {
                    stopBookmarks = StopBookmarkMapper.transform(stopBookmarkEntities);
                    getViewState().showStopBookmarks(stopBookmarks);
                });
    }

    public void searchStops(String searchQuery) {
        if (stops != null) {
            Map<String, Stop> stopMap = new HashMap<>();
            for (Stop stop : stops) {
                if (stop.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    stopMap.put(stop.getName(), stop);
                }
            }
            getViewState().showSearchResult(new ArrayList<>(stopMap.values()));
        }
    }

    public void saveStopBookmark() {
        for (Stop stop : stops) {
            if (stop.getId() == selectedStopId) {
                saveStopBookmark(stop);
            }
        }
    }

    public void removeStopBookmark() {
        for (Stop stop : stops) {
            if (stop.getId() == selectedStopId) {
                removeStopBookmark(stop);
            }
        }
    }

    private void saveStopBookmark(Stop stop) {
        disposable = LocalRepository.saveStopBookmark(stop)
                .compose(RxUtils.applySchedulers())
                .subscribe(aBoolean -> {
                    getViewState().showToast(aBoolean
                            ? R.string.bookmark_save_success
                            : R.string.bookmark_save_fail);
                    getAllStopBookmarksFromLocalDatabase();
                });
    }

    private void removeStopBookmark(Stop stop) {
        disposable = LocalRepository.removeStopBookmark(stop)
                .compose(RxUtils.applySchedulers())
                .subscribe(aBoolean -> {
                    getViewState().showToast(aBoolean
                            ? R.string.bookmark_remove_fail
                            : R.string.bookmark_remove_success);
                    getAllStopBookmarksFromLocalDatabase();
                });
    }

    public void renameStopBookmark(String newStopName) {
        disposable = LocalRepository.renameStopBookmark(selectedStopId, newStopName)
                .compose(RxUtils.applySchedulers())
                .subscribe(aBoolean -> {
                    getViewState().showToast(aBoolean
                            ? R.string.bookmark_rename_success
                            : R.string.bookmark_rename_fail);
                    getAllStopBookmarksFromLocalDatabase();
                });
    }

    private void saveAllStopsToLocalDatabase() {
        disposable = LocalRepository.saveAllStops(stops, updatedTimestamp)
                .compose(RxUtils.applySchedulers())
                .subscribe(aBoolean -> {
                });
    }

    private void isOutdatedOrNotExists(final long updatedTimestamp) {
        disposable = LocalRepository.isOutdatedOrNotExists(updatedTimestamp)
                .compose(RxUtils.applySchedulers())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        getViewState().showUpdateStopsSnackbar();
                    }
                });
    }

    public void onMarkerClicked(MarkerWrapper markerWrapper) {
        if (visibleMarkers.containsValue(markerWrapper)) {
            for (Integer key : visibleMarkers.keySet()) {
                if (visibleMarkers.get(key).equals(markerWrapper)) {
                    getStopById(key, false);
                }
            }
        }
    }

    @SuppressLint("UseSparseArrays")
    private void placeMarkers() {
        if (stops != null) {
            visibleMarkers = new HashMap<>();
            getViewState().placeMarkers(stops);
        }
    }

    public void addVisibleMarker(int stopId, MarkerWrapper marker) {
        visibleMarkers.put(stopId, marker);
    }

    public void removeVisibleMarker(int stopId) {
        visibleMarkers.get(stopId).getMarker().remove();
        visibleMarkers.remove(stopId);
    }

    public boolean containsMarker(int stopId) {
        return visibleMarkers.containsKey(stopId);
    }

    public boolean isMarkerSelected(int stopId) {
        return visibleMarkers.get(stopId).isSelected();
    }

    public void selectMarker(int stopId) {
        visibleMarkers.get(stopId).setSelected(true);
        visibleMarkers.get(stopId).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_selected));
    }

    public void dropMarkerHighlight(float currentZoom, float thresholdZoom) {
        if (currentZoom < thresholdZoom) {
            for (Integer key : visibleMarkers.keySet()) {
                visibleMarkers.get(key).getMarker().remove();
                visibleMarkers.remove(key);
            }
        }
        for (Integer key : visibleMarkers.keySet()) {
            if (visibleMarkers.get(key).isSelected()) {
                visibleMarkers.get(key).setSelected(false);
                visibleMarkers.get(key).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place));
            }
        }
    }

    public void setSelectedStopId(int selectedStopId) {
        this.selectedStopId = selectedStopId;
    }

    public String getSelectedStopBookmarkName() {
        String stopBookmarkName = "";
        for (StopBookmark stopBookmark : stopBookmarks) {
            if (stopBookmark.getId() == selectedStopId) {
                stopBookmarkName = stopBookmark.getName();
            }
        }
        return stopBookmarkName;
    }
}
