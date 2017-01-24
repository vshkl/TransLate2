package by.vshkl.translate2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import by.vshkl.translate2.database.local.DbUtils;
import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.database.remote.FirebaseUtils;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.transformer.StopTransformer;
import by.vshkl.translate2.mvp.view.MapView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            getViewState().showUpdateStopsSnackbar();
                        } else {
                            getAllStopsFromLocalDatabase();
                        }
                    }
                });
    }

    private void getAllStopsFromLocalDatabase() {
        disposable = DbUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<StopEntity>>() {
                    @Override
                    public void accept(List<StopEntity> stopEntities) throws Exception {
                        stopList = StopTransformer.transform(stopEntities);
                    }
                });
    }

    private void saveAllStopsToLocalDatabase() {
        disposable = DbUtils.saveAllStops(stopList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
    }

    public void getAppStopsFromRemoteDatabase() {
        disposable = FirebaseUtils.getAllStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(new Function<Throwable, List<Stop>>() {
                    @Override
                    public List<Stop> apply(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        return null;
                    }
                })
                .subscribe(new Consumer<List<Stop>>() {
                    @Override
                    public void accept(List<Stop> stops) throws Exception {
                        stopList = stops;
                        saveAllStopsToLocalDatabase();
                    }
                });

    }
}
