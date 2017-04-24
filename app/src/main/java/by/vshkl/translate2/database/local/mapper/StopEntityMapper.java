package by.vshkl.translate2.database.local.mapper;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.mvp.model.Stop;

public class StopEntityMapper {

    public static StopEntity transform(Stop stop) {
        StopEntity stopEntity = new StopEntity();
        stopEntity.id = stop.getId();
        stopEntity.name = stop.getName();
        stopEntity.latitude = stop.getLatitude();
        stopEntity.longitude = stop.getLongitude();
        stopEntity.bearing = stop.getBearing();
        return stopEntity;
    }

    public static List<StopEntity> transform(List<Stop> stops) {
        List<StopEntity> stopEntities = new ArrayList<>(stops.size());
        for (Stop stop : stops) {
            stopEntities.add(transform(stop));
        }
        return stopEntities;
    }
}
