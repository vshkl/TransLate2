package by.vshkl.translate2.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class Stop implements SearchSuggestion {

    private int id;
    private String name;
    private float latitude;
    private float longitude;
    private int bearing;

    private Stop(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        latitude = parcel.readFloat();
        longitude = parcel.readFloat();
        bearing = parcel.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stop stop = (Stop) o;

        return id == stop.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stop{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", [").append(latitude);
        sb.append(", ").append(longitude);
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public String getBody() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeInt(bearing);
    }

    public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
        @Override
        public Stop createFromParcel(Parcel parcel) {
            return new Stop(parcel);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[0];
        }
    };
}
