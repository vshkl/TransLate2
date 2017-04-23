package by.vshkl.translate2.mvp.presenter;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.vshkl.translate2.R;
import by.vshkl.translate2.database.local.DbUtils;
import by.vshkl.translate2.database.remote.FirebaseUtils;
import by.vshkl.translate2.mvp.model.MarkerWrapper;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import by.vshkl.translate2.mvp.model.transformer.StopBookmarkTransformer;
import by.vshkl.translate2.mvp.model.transformer.StopTransformer;
import by.vshkl.translate2.mvp.view.MapView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> {

    private Disposable disposable;
    private List<Stop> stopList;
    private List<StopBookmark> stopBookmarkList;
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

    public void getUpdatedTimestampFromRemoteDatabase() {
        disposable = FirebaseUtils.getUpdatedTimestamp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updated -> {
                    updatedTimestamp = updated.getUpdatedTimestamp();
                    isOutdatedOrNotExists(updatedTimestamp);
                });
    }

    public void getAllStopsFromRemoteDatabase() {
        getViewState().showProgressBar();
        disposable = FirebaseUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stops -> {
                    getViewState().hideProgressBar();
                    stopList = stops;
                    saveAllStopsToLocalDatabase();
                    placeMarkers();
                    getViewState().showMessage(R.string.map_message_stops_updated);
                });
    }

    public void getAllStopsFromLocalDatabase() {
        disposable = DbUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stopEntities -> {
                    stopList = StopTransformer.transform(stopEntities);
                    placeMarkers();
                });
    }

    public void getStopById(final int stopId, final boolean fromNavDrawer) {
        selectedStopId = stopId;
        if (stopList != null && stopBookmarkList != null) {
            for (Stop stop : stopList) {
                if (stop.getId() == stopId) {
                    System.out.println();
                    getViewState().showSelectedStop(stop,
                            stopBookmarkList.contains(new StopBookmark(stop.getId(), stop.getName())),
                            fromNavDrawer);
                }
            }
        }
    }

    public void getAllStopBookmarksFromLocalDatabase() {
        disposable = DbUtils.getAllStopBookmarks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stopBookmarkEntityList -> {
                    stopBookmarkList = StopBookmarkTransformer.transform(stopBookmarkEntityList);
                    getViewState().showStopBookmarks(stopBookmarkList);
                });
    }

    public void searchStops(final String searchQuery) {
        if (stopList != null) {
            Map<String, Stop> stopMap = new HashMap<>();
            for (Stop stop : stopList) {
                if (stop.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    stopMap.put(stop.getName(), stop);
                }
            }
            getViewState().showSearchResult(new ArrayList<>(stopMap.values()));
        }
    }

    public void saveStopBookmark() {
        for (Stop stop : stopList) {
            if (stop.getId() == selectedStopId) {
                saveStopBookmark(stop);
            }
        }
    }

    public void removeStopBookmark() {
        for (Stop stop : stopList) {
            if (stop.getId() == selectedStopId) {
                removeStopBookmark(stop);
            }
        }
    }

    private void saveStopBookmark(Stop stop) {
        disposable = DbUtils.saveStopBookmark(stop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    getViewState().showToast(aBoolean
                            ? R.string.bookmark_save_success
                            : R.string.bookmark_save_fail);
                    getAllStopBookmarksFromLocalDatabase();
                });
    }

    private void removeStopBookmark(Stop stop) {
        disposable = DbUtils.removeStopBookmark(stop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    getViewState().showToast(aBoolean
                            ? R.string.bookmark_remove_fail
                            : R.string.bookmark_remove_success);
                    getAllStopBookmarksFromLocalDatabase();
                });
    }

    public void renameStopBookmark(String newStopName) {
        disposable = DbUtils.renameStopBookmark(selectedStopId, newStopName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    getViewState().showToast(aBoolean
                            ? R.string.bookmark_rename_success
                            : R.string.bookmark_rename_fail);
                    getAllStopBookmarksFromLocalDatabase();
                });
    }

    private void saveAllStopsToLocalDatabase() {
        disposable = DbUtils.saveAllStops(stopList, updatedTimestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                });
    }

    private void isOutdatedOrNotExists(final long updatedTimestamp) {
        disposable = DbUtils.isOutdatedOrNotExists(updatedTimestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
        if (stopList != null) {
            visibleMarkers = new HashMap<>();
            getViewState().placeMarkers(stopList);
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
        for (StopBookmark stopBookmark : stopBookmarkList) {
            if (stopBookmark.getId() == selectedStopId) {
                stopBookmarkName = stopBookmark.getName();
            }
        }
        return stopBookmarkName;
    }
}
