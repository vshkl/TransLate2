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
            stopList.stream()
                    .filter(stop -> stop.getId() == stopId)
                    .forEach(stop -> getViewState().showSelectedStop(stop, isStopBookmarked()));
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
            stopList.stream()
                    .filter(stop -> stop.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                    .forEach(stop -> stopMap.put(stop.getName(), stop));
            getViewState().showSearchResult(new ArrayList<>(stopMap.values()));
        }
    }

    public void saveStopBookmark() {
        stopList.stream()
                .filter(stop -> stop.getId() == selectedStopId)
                .findFirst()
                .ifPresent(this::saveStopBookmark);
    }

    public void removeStopBookmark() {
        stopList.stream()
                .filter(stop -> stop.getId() == selectedStopId)
                .findFirst()
                .ifPresent(this::removeStopBookmark);
    }

    private void saveStopBookmark(Stop stop) {
        disposable = DbUtils.saveStopBookmark(stop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> getViewState().showMessage(aBoolean
                        ? R.string.bookmark_save_success
                        : R.string.bookmark_save_fail));
    }

    private void removeStopBookmark(Stop stop) {
        disposable = DbUtils.removeStopBookmark(stop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> getViewState().showMessage(aBoolean
                        ? R.string.bookmark_remove_fail
                        : R.string.bookmark_remove_success));
    }

    private void saveAllStopsToLocalDatabase() {
        disposable = DbUtils.saveAllStops(stopList, updatedTimestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                });
    }

    private boolean isStopBookmarked() {
        return stopBookmarkList.stream()
                .filter(stopBookmark -> stopBookmark.getId() == selectedStopId)
                .findAny()
                .isPresent();
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
