package by.vshkl.translate2.database.local;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopBookmarkEntity;
import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.database.local.entity.UpdatedEntity;
import by.vshkl.translate2.database.local.entity.transformer.StopBookmarkEntityTransformer;
import by.vshkl.translate2.database.local.entity.transformer.StopEntityTransformer;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

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

    public static Observable<List<StopBookmarkEntity>> getAllStopBookmarks() {
        return Observable.create(emitter -> {
            List<StopBookmarkEntity> stopBookmarkEntityList = new Select()
                    .from(StopBookmarkEntity.class)
                    .execute();
            emitter.onNext(stopBookmarkEntityList != null ? stopBookmarkEntityList : Collections.emptyList());
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

    public static Observable<Boolean> saveStopBookmark(final Stop stop) {
        return Observable.create(emitter -> {
            StopBookmark stopBookmark = new StopBookmark();
            stopBookmark.setId(stop.getId());
            stopBookmark.setName(stop.getName());
            StopBookmarkEntityTransformer.transform(stopBookmark).save();
            emitter.onNext(new Select().from(StopBookmarkEntity.class).where("Id = ?", stop.getId()).exists());
        });
    }

    public static Observable<Boolean> removeStopBookmark(final Stop stop) {
        return Observable.create(emitter -> {
            new Delete().from(StopBookmarkEntity.class).where("Id = ?", stop.getId()).execute();
            emitter.onNext(new Select().from(StopBookmarkEntity.class).where("Id = ?", stop.getId()).exists());
        });
    }
}
