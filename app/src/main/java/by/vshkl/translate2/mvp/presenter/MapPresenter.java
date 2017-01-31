package by.vshkl.translate2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import by.vshkl.translate2.R;
import by.vshkl.translate2.database.local.DbUtils;
import by.vshkl.translate2.database.remote.FirebaseUtils;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.transformer.StopTransformer;
import by.vshkl.translate2.mvp.view.MapView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> {

    private Disposable disposable;
    private List<Stop> stopList;
    private long updatedTimestamp;

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

    public void getAllStopsFromLocalDatabase() {
        disposable = DbUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stopEntities -> {
                    stopList = StopTransformer.transform(stopEntities);
                    placeMarkers();
                });
    }

    private void saveAllStopsToLocalDatabase() {
        disposable = DbUtils.saveAllStops(stopList, updatedTimestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                });
    }

    private void placeMarkers() {
        if (stopList != null) {
            getViewState().placeMarkers(stopList);
        }
    }

    public void getStopById(int stopId) {
        if (stopList != null) {
            for (Stop stop : stopList) {
                if (stop.getId() == stopId) {
                    getViewState().showSelectedStop(stop);
                }
            }
        }
    }
}
