package by.vshkl.translate2.database;

import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

import by.vshkl.translate2.database.entity.StopEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class DbUtils {

    public static Observable<Boolean> isStopsExists() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(new Select()
                        .from(StopEntity.class)
                        .exists());
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
}
