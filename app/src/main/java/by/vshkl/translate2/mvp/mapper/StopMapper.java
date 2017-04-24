package by.vshkl.translate2.mvp.mapper;

import java.util.ArrayList;
import java.util.List;

import by.vshkl.translate2.database.local.entity.StopEntity;
import by.vshkl.translate2.mvp.model.Stop;

public class StopMapper {

    public static Stop transform(StopEntity stopEntity) {
        Stop stop = new Stop();
        stop.setId(stopEntity.id);
        stop.setName(stopEntity.name);
        stop.setLatitude(stopEntity.latitude);
        stop.setLongitude(stopEntity.longitude);
        stop.setBearing(stopEntity.bearing);
        return stop;
    }

    public static List<Stop> transform(List<StopEntity> stopEntities) {
        List<Stop> stops = new ArrayList<>(stopEntities.size());
        for (StopEntity stopEntity : stopEntities) {
            stops.add(transform(stopEntity));
        }
        return stops;
    }
}
