package by.vshkl.translate2.mvp.model.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import by.vshkl.translate2.database.local.entity.StopBookmarkEntity;
import by.vshkl.translate2.mvp.model.StopBookmark;

public class StopBookmarkTransformer {

    public static StopBookmark transform(StopBookmarkEntity stopBookmarkEntity) {
        StopBookmark stopBookmark = new StopBookmark();
        stopBookmark.setId(stopBookmarkEntity.id);
        stopBookmark.setName(stopBookmarkEntity.name);
        return stopBookmark;
    }

    public static List<StopBookmark> transform(List<StopBookmarkEntity> stopBookmarkEntityList) {
        List<StopBookmark> stopBookmarkList = new ArrayList<>(stopBookmarkEntityList.size());
        stopBookmarkEntityList.forEach(stopBookmarkEntity -> stopBookmarkList.add(transform(stopBookmarkEntity)));
        return stopBookmarkList;
    }
}
