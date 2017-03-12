package by.vshkl.translate2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.vshkl.translate2.R;
import by.vshkl.translate2.database.local.DbUtils;
import by.vshkl.translate2.database.remote.FirebaseUtils;
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
    private long updatedTimestamp;
    private int selectedStopId;

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
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

    public void getStopById(final int stopId) {
        selectedStopId = stopId;
        if (stopList != null && stopBookmarkList != null) {
            for (Stop stop : stopList) {
                if (stop.getId() == stopId) {
                    getViewState().showSelectedStop(stop, isStopBookmarked());
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

    private boolean isStopBookmarked() {
        for (StopBookmark stopBookmark : stopBookmarkList) {
            return stopBookmark.getId() == selectedStopId;
        }
        return false;
    }

    private void placeMarkers() {
        if (stopList != null) {
            getViewState().placeMarkers(stopList);
        }
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
}
