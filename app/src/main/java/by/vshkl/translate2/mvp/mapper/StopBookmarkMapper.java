package by.vshkl.translate2.mvp.mapper;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopBookmarkEntity;
import by.vshkl.translate2.mvp.model.StopBookmark;

public class StopBookmarkMapper {

    public static StopBookmark transform(StopBookmarkEntity stopBookmarkEntity) {
        StopBookmark stopBookmark = new StopBookmark();
        stopBookmark.setId(stopBookmarkEntity.id);
        stopBookmark.setName(stopBookmarkEntity.name);
        return stopBookmark;
    }

    public static List<StopBookmark> transform(List<StopBookmarkEntity> stopBookmarkEntities) {
        List<StopBookmark> stopBookmarks = new ArrayList<>(stopBookmarkEntities.size());
        for (StopBookmarkEntity stopBookmarkEntity : stopBookmarkEntities) {
            stopBookmarks.add(transform(stopBookmarkEntity));
        }
        return stopBookmarks;
    }
}
