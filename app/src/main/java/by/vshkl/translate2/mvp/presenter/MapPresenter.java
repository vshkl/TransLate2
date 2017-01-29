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

    @Override
    public void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    public void checkStopsUpdate() {
        disposable = DbUtils.isStopsTableEmpty()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        getViewState().showUpdateStopsSnackbar();
                    } else {
                        getAllStopsFromLocalDatabase();
                        placeMarkers();
                    }
                });
    }

    private void getAllStopsFromLocalDatabase() {
        disposable = DbUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stopEntities -> {
                    stopList = StopTransformer.transform(stopEntities);
                    placeMarkers();
                });
    }

    private void saveAllStopsToLocalDatabase() {
        disposable = DbUtils.saveAllStops(stopList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                });
    }

    public void getAppStopsFromRemoteDatabase() {
        disposable = FirebaseUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
                .subscribe(stops -> {
                    stopList = stops;
                    saveAllStopsToLocalDatabase();
                    getViewState().showMessage(R.string.map_message_stops_updated);
                    getViewState().initializeMap();
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
                    getViewState().hideFab();
                    getViewState().showSelectedStop(stop);
                }
            }
        }
    }
}
