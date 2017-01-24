package by.vshkl.translate2.database.local.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Stops", id = "_id")
public class StopEntity extends Model {

    @Column(name = "Id")
    public int id;
    @Column(name = "Name")
    public String name;
    @Column(name = "Latitude")
    public float latitude;
    @Column(name = "Longitude")
    public float longitude;
    @Column(name = "Bearing")
    public int bearing;
}
