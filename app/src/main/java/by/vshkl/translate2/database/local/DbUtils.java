package by.vshkl.translate2.database.local;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.database.local.entity.transformer.StopEntityTransformer;
import by.vshkl.translate2.mvp.model.Stop;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class DbUtils {

    public static Observable<Boolean> isStopsTableEmpty() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                List<StopEntity> stopEntityList = new Select()
                        .from(StopEntity.class)
                        .execute();
                emitter.onNext(stopEntityList.isEmpty());
            }
        });
    }

    public static Observable<List<StopEntity>> getAllStops() {
        return Observable.create(new ObservableOnSubscribe<List<StopEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<StopEntity>> emitter) throws Exception {
                List<StopEntity> stopEntityList = new Select()
                        .from(StopEntity.class)
                        .execute();
                emitter.onNext(stopEntityList != null ? stopEntityList : Collections.<StopEntity>emptyList());
            }
        });
    }

    public static Observable<Boolean> saveAllStops(final List<Stop> stopList) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                ActiveAndroid.beginTransaction();
                try {
                    for (Stop stop : stopList) {
                        StopEntityTransformer.transform(stop).save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                    emitter.onNext(true);
                }
            }
        });
    }
}
