package by.vshkl.translate2.database.local.mapper;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopBookmarkEntity;
import by.vshkl.translate2.mvp.model.StopBookmark;

public class StopBookmarkEntityMapper {

    public static StopBookmarkEntity transform(StopBookmark stopBookmark) {
        StopBookmarkEntity stopBookmarkEntity = new StopBookmarkEntity();
        stopBookmarkEntity.id = stopBookmark.getId();
        stopBookmarkEntity.name = stopBookmark.getName();
        return stopBookmarkEntity;
    }

    public static List<StopBookmarkEntity> transform(List<StopBookmark> stopBookmarks) {
        List<StopBookmarkEntity> stopBookmarkEntities = new ArrayList<>(stopBookmarks.size());
        for (StopBookmark stopBookmark : stopBookmarks) {
            stopBookmarkEntities.add(transform(stopBookmark));
        }
        return stopBookmarkEntities;
    }
}
