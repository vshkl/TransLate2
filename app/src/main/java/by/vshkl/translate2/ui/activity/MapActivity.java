package by.vshkl.translate2.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.translate2.R;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.presenter.MapPresenter;
import by.vshkl.translate2.mvp.view.MapView;
import by.vshkl.translate2.util.CookieUtils;
import by.vshkl.translate2.util.DialogUtils;
import by.vshkl.translate2.util.DimensionUtils;
import by.vshkl.translate2.util.Navigation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapActivity extends MvpAppCompatActivity implements MapView, OnMapReadyCallback {

    private static final float ZOOM_CITY = 11F;
    private static final float ZOOM_STREET = 15F;
    private static final double DEFAULT_LATITUDE = 53.9024429;
    private static final double DEFAULT_LONGITUDE = 27.5614649;
    private static final String URL_DASHBOARD = "http://www.minsktrans.by/lookout_yard/Home/Index/minsk?stopsearch&s=";

    @BindView(R.id.root) CoordinatorLayout clRoot;
    @BindView(R.id.fl_bottom_sheet) FrameLayout flBottomSheet;
    @BindView(R.id.fb_location) FloatingActionButton fabLocation;
    @BindView(R.id.tv_stop_name) TextView tvStopName;
    @BindView(R.id.wv_dashboard) WebView wvDashboard;

    @InjectPresenter MapPresenter presenter;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private HashMap<Integer, Marker> visibleMarkers = new HashMap<>();
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(MapActivity.this);
        initializeMap();
        initializeBottomSheet();
        initializeGoogleApiClient();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(MapActivity.this, requestCode, grantResults);
    }

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.fb_location)
    void onLocationClicked() {
        MapActivityPermissionsDispatcher.updateCoordinatesWithCheck(MapActivity.this);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setBuildingsEnabled(true);
        UiSettings settings = map.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setMyLocationButtonEnabled(false);
        settings.setMapToolbarEnabled(false);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), ZOOM_CITY));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (bottomSheetBehavior != null) {
                    bottomSheetBehavior.setPeekHeight(0);
                    showFab();
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (visibleMarkers.containsValue(marker)) {
                    for (Map.Entry<Integer, Marker> entry : visibleMarkers.entrySet()) {
                        if (Objects.equals(marker, entry.getValue())) {
                            presenter.getStopById(entry.getKey());
                        }
                    }
                }
                return false;
            }
        });
        presenter.checkStopsUpdate();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showUpdateStopsSnackbar() {
        final Snackbar snackbar = Snackbar.make(clRoot, R.string.map_update_stops_message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.map_update_stops_update, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getAppStopsFromRemoteDatabase();
            }
        });
        snackbar.show();
    }

    @Override
    public void placeMarkers(final List<Stop> stopList) {
        if (map != null) {
            map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    addItemsToMap(stopList, map.getCameraPosition().zoom);
                }
            });

        }
    }

    @Override
    public void showFab() {
        fabLocation.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFab() {
        fabLocation.setVisibility(View.GONE);
    }

    @Override
    public void showSelectedStop(final Stop stop) {
        bottomSheetBehavior.setPeekHeight(DimensionUtils.dp2px(getResources().getDisplayMetrics(), 56));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        tvStopName.setText(stop.getName());
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    String url = URL_DASHBOARD + stop.getId();
                    CookieManager.getInstance().setCookie(url, CookieUtils.getCookies(getApplicationContext()));
                    wvDashboard.clearHistory();
                    if (Build.VERSION.SDK_INT >= 19) {
                        wvDashboard.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    } else {
                        wvDashboard.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                    wvDashboard.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    wvDashboard.getSettings().setAppCacheEnabled(true);
                    wvDashboard.getSettings().setDomStorageEnabled(true);
                    wvDashboard.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                    wvDashboard.getSettings().setJavaScriptEnabled(true);
                    wvDashboard.getSettings().setGeolocationEnabled(true);
                    wvDashboard.loadUrl(url);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void updateCoordinates() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean networkProviderEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProviderEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (networkProviderEnabled || gpsProviderEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                map.setMyLocationEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_STREET));
            }
        } else {
            DialogUtils.showLocationTurnOnDialog(getApplicationContext());
        }
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationaleForLocation(final PermissionRequest request) {
        DialogUtils.showLocationRationaleDialog(getApplicationContext(), request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onDeniedForLocation() {
        Snackbar.make(clRoot, R.string.map_permission_denied_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.map_permission_denied_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.navigateToAppSettings(getApplicationContext());
                    }
                })
                .show();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, MapActivity.class);
        return intent;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fr_map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void initializeGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MapActivity.this).addApi(LocationServices.API).build();
        }
    }

    private void initializeBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(flBottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void addItemsToMap(List<Stop> stopList, float zoom) {
        LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
        for (Stop stop : stopList) {
            LatLng latLng = new LatLng(stop.getLatitude(), stop.getLongitude());
            if (zoom >= ZOOM_STREET) {
                if (latLngBounds.contains(latLng)) {
                    if (!visibleMarkers.containsKey(stop.getId())) {
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place)));
                        visibleMarkers.put(stop.getId(), marker);
                    }
                } else {
                    if (visibleMarkers.containsKey(stop.getId())) {
                        visibleMarkers.get(stop.getId()).remove();
                        visibleMarkers.remove(stop.getId());
                    }
                }
            } else {
                if (visibleMarkers.containsKey(stop.getId())) {
                    visibleMarkers.get(stop.getId()).remove();
                    visibleMarkers.remove(stop.getId());
                }
            }
        }
    }
}
