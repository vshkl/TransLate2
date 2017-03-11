package by.vshkl.translate2.database.local;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.database.local.entity.UpdatedEntity;
import by.vshkl.translate2.database.local.entity.transformer.StopEntityTransformer;
import by.vshkl.translate2.mvp.model.Stop;
import io.reactivex.Observable;

public class DbUtils {

    public static Observable<Boolean> isOutdatedOrNotExists(final long updatedTimestamp) {
        return Observable.create(emitter -> {
            UpdatedEntity updatedEntity = new Select()
                    .from(UpdatedEntity.class)
                    .executeSingle();
            emitter.onNext(updatedEntity == null || updatedTimestamp > updatedEntity.undatedTimestamp);
        });
    }

    public static Observable<List<StopEntity>> getAllStops() {
        return Observable.create(emitter -> {
            List<StopEntity> stopEntityList = new Select()
                    .from(StopEntity.class)
                    .execute();
            emitter.onNext(stopEntityList != null ? stopEntityList : Collections.emptyList());
        });
    }

    public static Observable<Boolean> saveAllStops(final List<Stop> stopList, final long updatedTimestamp) {
        return Observable.create(emitter -> {
            ActiveAndroid.beginTransaction();
            try {
                stopList.forEach(stop -> StopEntityTransformer.transform(stop).save());
                new Delete().from(UpdatedEntity.class).execute();
                UpdatedEntity updatedEntity = new UpdatedEntity();
                updatedEntity.undatedTimestamp = updatedTimestamp;
                updatedEntity.save();
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
                emitter.onNext(true);
            }
        });
    }
}
