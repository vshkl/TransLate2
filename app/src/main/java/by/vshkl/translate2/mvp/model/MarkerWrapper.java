package by.vshkl.translate2.mvp.model;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;

public class MarkerWrapper {

    private Marker marker;
    private boolean selected;

    public MarkerWrapper(Marker marker) {
        this.marker = marker;
        this.selected = false;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setIcon(@Nullable BitmapDescriptor bitmapDescriptor) {
        marker.setIcon(bitmapDescriptor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarkerWrapper that = (MarkerWrapper) o;

        return marker.equals(that.marker);

    }

    @Override
    public int hashCode() {
        return marker.hashCode();
    }
}
