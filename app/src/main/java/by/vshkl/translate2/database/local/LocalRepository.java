package by.vshkl.translate2.database.local;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopBookmarkEntity;
import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.database.local.entity.UpdatedEntity;
import by.vshkl.translate2.database.local.mapper.StopBookmarkEntityMapper;
import by.vshkl.translate2.database.local.mapper.StopEntityMapper;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import io.reactivex.Observable;

public class LocalRepository {

    private static final String WHERE_ID = "Id = ?";
    private static final String WHERE_NAME = "Name = ?";

    public static Observable<Boolean> isOutdatedOrNotExists(long updatedTimestamp) {
        return Observable.create(emitter -> {
            UpdatedEntity updatedEntity = new Select().from(UpdatedEntity.class).executeSingle();
            emitter.onNext(updatedEntity == null || updatedTimestamp > updatedEntity.undatedTimestamp);
        });
    }

    public static Observable<List<StopEntity>> loadStops() {
        return Observable.create(emitter -> {
            List<StopEntity> stopEntities = new Select().from(StopEntity.class).execute();
            emitter.onNext(stopEntities != null ? stopEntities : Collections.emptyList());
        });
    }

    public static Observable<List<StopBookmarkEntity>> loadStopBookmarks() {
        return Observable.create(emitter -> {
            List<StopBookmarkEntity> stopBookmarks = new Select().from(StopBookmarkEntity.class).execute();
            emitter.onNext(stopBookmarks != null ? stopBookmarks : Collections.emptyList());
        });
    }

    public static Observable<Boolean> saveAllStops(List<Stop> stops, long updatedTimestamp) {
        return Observable.create(emitter -> {
            ActiveAndroid.beginTransaction();
            try {
                for (Stop stop : stops) {
                    StopEntityMapper.transform(stop).save();
                }
                new Delete().from(UpdatedEntity.class).execute();
                new UpdatedEntity(updatedTimestamp).save();
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
                emitter.onNext(true);
            }
        });
    }

    public static Observable<Boolean> saveStopBookmark(Stop stop) {
        return Observable.create(emitter -> {
            StopBookmark stopBookmark = new StopBookmark();
            stopBookmark.setId(stop.getId());
            stopBookmark.setName(stop.getName());
            StopBookmarkEntityMapper.transform(stopBookmark).save();
            emitter.onNext(new Select().from(StopBookmarkEntity.class).where(WHERE_ID, stop.getId()).exists());
        });
    }

    public static Observable<Boolean> removeStopBookmark(Stop stop) {
        return Observable.create(emitter -> {
            new Delete().from(StopBookmarkEntity.class).where(WHERE_ID, stop.getId()).execute();
            emitter.onNext(new Select().from(StopBookmarkEntity.class).where(WHERE_ID, stop.getId()).exists());
        });
    }

    public static Observable<Boolean> removeStopBookmarks() {
        return Observable.create(emitter -> {
            new Delete().from(StopBookmarkEntity.class).execute();
            emitter.onNext(new Select().from(StopBookmarkEntity.class).exists());
        });
    }

    public static Observable<Boolean> renameStopBookmark(int stopId, String newStopName) {
        return Observable.create(emitter -> {
            StopBookmarkEntity stop = new Select().from(StopBookmarkEntity.class).where(WHERE_ID, stopId).executeSingle();
            if (stop != null) {
                stop.name = newStopName;
                stop.save();
            }
            emitter.onNext(new Select().from(StopBookmarkEntity.class).where(WHERE_NAME, newStopName).exists());
        });
    }
}
