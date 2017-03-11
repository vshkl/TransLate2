package by.vshkl.translate2.database.local.entity.transformer;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.mvp.model.Stop;

public class StopEntityTransformer {

    public static StopEntity transform(Stop stop) {
        StopEntity stopEntity = new StopEntity();
        stopEntity.id = stop.getId();
        stopEntity.name = stop.getName();
        stopEntity.latitude = stop.getLatitude();
        stopEntity.longitude = stop.getLongitude();
        stopEntity.bearing = stop.getBearing();
        return stopEntity;
    }

    public static List<StopEntity> transform(List<Stop> stopList) {
        List<StopEntity> stopEntityList = new ArrayList<>(stopList.size());
        stopList.forEach(stop -> stopEntityList.add(transform(stop)));
        return stopEntityList;
    }
}
