package by.vshkl.translate2.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LocationUtils {

    public static LatLng getAnyLastKnownLatLng(Context context, GoogleApiClient googleApiClient) {
        Location location = getAnyLastKnownLocation(context, googleApiClient);
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static boolean hasAnyLocationProvides(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        return locationRequest;
    }

    private static Location getAnyLastKnownLocation(Context context, GoogleApiClient googleApiClient) {
        Location defaultLocation = new Location("default");
        defaultLocation.setLatitude(Constants.DEFAULT_LATITUDE);
        defaultLocation.setLongitude(Constants.DEFAULT_LONGITUDE);

        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return defaultLocation;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        return location == null ? defaultLocation : location;
    }
}
