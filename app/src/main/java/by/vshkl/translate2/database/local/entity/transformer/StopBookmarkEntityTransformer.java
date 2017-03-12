package by.vshkl.translate2.database.local.entity.transformer;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopBookmarkEntity;
import by.vshkl.translate2.mvp.model.StopBookmark;

public class StopBookmarkEntityTransformer {

    public static StopBookmarkEntity transform(StopBookmark stopBookmark) {
        StopBookmarkEntity stopBookmarkEntity = new StopBookmarkEntity();
        stopBookmarkEntity.id = stopBookmark.getId();
        stopBookmarkEntity.name = stopBookmark.getName();
        return stopBookmarkEntity;
    }

    public static List<StopBookmarkEntity> transform(List<StopBookmark> stopBookmarkList) {
        List<StopBookmarkEntity> stopBookmarkEntityList = new ArrayList<>(stopBookmarkList.size());
        for (StopBookmark stopBookmark : stopBookmarkList) {
            stopBookmarkEntityList.add(transform(stopBookmark));
        }
        return stopBookmarkEntityList;
    }
}
