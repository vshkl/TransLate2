package by.vshkl.translate2.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.FloatingSearchView.OnFocusChangeListener;
import com.arlib.floatingsearchview.FloatingSearchView.OnMenuItemClickListener;
import com.arlib.floatingsearchview.FloatingSearchView.OnQueryChangeListener;
import com.arlib.floatingsearchview.FloatingSearchView.OnSearchListener;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter.OnBindSuggestionCallback;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener;
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemLongClickListener;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.translate2.App;
import by.vshkl.translate2.R;
import by.vshkl.translate2.mvp.model.MarkerWrapper;
import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import by.vshkl.translate2.mvp.model.Version;
import by.vshkl.translate2.mvp.presenter.MapPresenter;
import by.vshkl.translate2.mvp.view.MapView;
import by.vshkl.translate2.ui.listener.AppUpdateListener;
import by.vshkl.translate2.ui.listener.StopBookmarkListener;
import by.vshkl.translate2.util.Constants;
import by.vshkl.translate2.util.CookieUtils;
import by.vshkl.translate2.util.DialogUtils;
import by.vshkl.translate2.util.LocaleUtils;
import by.vshkl.translate2.util.Navigation;
import by.vshkl.translate2.util.PreferenceUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapActivity extends MvpAppCompatActivity implements MapView, ConnectionCallbacks, OnMapReadyCallback,
        OnMapClickListener, OnMarkerClickListener, OnQueryChangeListener, OnSearchListener, OnBindSuggestionCallback,
        OnFocusChangeListener, OnMenuItemClickListener, OnDrawerItemClickListener, OnDrawerItemLongClickListener,
        StopBookmarkListener, AppUpdateListener, LocationListener {

    private static final String TAG = "MapActivity";

    @BindView(R.id.root) CoordinatorLayout clRoot;
    @BindView(R.id.sv_search) FloatingSearchView svSearch;
    @BindView(R.id.fl_bottom_sheet) FrameLayout flBottomSheet;
    @BindView(R.id.fb_location) FloatingActionButton fabLocation;
    @BindView(R.id.tv_stop_name) TextView tvStopName;
    @BindView(R.id.wv_dashboard) WebView wvDashboard;
    @BindView(R.id.pb_loading) ProgressBar pbLoading;
    @BindView(R.id.cb_bookmark) CheckBox cbBookmark;

    @InjectPresenter(type = PresenterType.LOCAL) MapPresenter presenter;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest = null;
    private BottomSheetBehavior bottomSheetBehavior;
    private Drawer ndStopBookmarks;
    private boolean firstConnect = true;
    private boolean requestLocationUpdates = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        LocaleUtils.setLocale(getBaseContext());
        initializeGoogleMap();
        initializeBottomSheet();
        initializeGoogleApiClient();
        initializeNavigationDrawer();
        initializeSearchView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        googleApiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
        App.getRefWatcher(this).watch(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressed() {
        switch (bottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_EXPANDED:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.fb_location)
    void onLocationClicked() {
        requestLocationUpdates = true;
        MapActivityPermissionsDispatcher.updateCoordinatesWithCheck(this);
    }

    @OnClick(R.id.cb_bookmark)
    void onBookmarkClicked() {
        if (cbBookmark.isChecked()) {
            presenter.saveStopBookmark();
        } else {
            presenter.removeStopBookmark();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (firstConnect) {
            firstConnect = false;
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setupMap();
        presenter.getUpdatedTimestampFromRemoteDatabase();
        presenter.getLatestVersionInfoFromRemoteDatabase(PreferenceUtils.getIgnoreUpdateVersion(this));
        presenter.getAllStopsFromLocalDatabase();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        dropMarkerHighlight(map.getCameraPosition().zoom);
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            fabLocation.show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        presenter.onMarkerClicked(new MarkerWrapper(marker));
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.setMyLocationEnabled(true);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.ZOOM_POSITION));
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        if (!oldQuery.isEmpty() && newQuery.isEmpty()) {
            svSearch.clearSuggestions();
        }
        if (newQuery.length() > 2) {
            presenter.searchStops(newQuery);
        }
    }

    @Override
    public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView,
                                 SearchSuggestion item, int itemPosition) {
        leftIcon.setImageResource(R.drawable.ic_place_suggestion);
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        Stop stop = (Stop) searchSuggestion;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(stop.getLatitude(), stop.getLongitude()), Constants.ZOOM_POSITION));
    }

    @Override
    public void onSearchAction(String currentQuery) {

    }

    @Override
    public void onFocus() {
        fabLocation.hide();
    }

    @Override
    public void onFocusCleared() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            fabLocation.show();
        }
    }

    @Override
    public void onActionMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Navigation.navigateToSettings(this);
        }
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        presenter.getStopById((int) drawerItem.getIdentifier(), true);
        return false;
    }

    @Override
    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
        presenter.setSelectedStopId((int) drawerItem.getIdentifier());
        DialogUtils.showBookmarkActionsDialog(this, this);
        return false;
    }

    @Override
    public void onEditBookmark() {
        DialogUtils.showBookmarkRenameDialog(this, presenter.getSelectedStopBookmarkName(), this);
    }

    @Override
    public void onDeleteBookmark() {
        DialogUtils.showBookmarkDeleteConfirmationDialog(this, this);
    }

    @Override
    public void onDeleteConfirmed() {
        presenter.removeStopBookmark();
    }

    @Override
    public void onRenameConfirmed(String newStopName) {
        presenter.renameStopBookmark(newStopName);
    }

    @Override
    public void onDownloadUpdate(Version version) {
        MapActivityPermissionsDispatcher.downloadUpdateWithCheck(this, version);
    }

    @Override
    public void onSkipThisUpdate(Version version) {
        PreferenceUtils.setIgnoreUpdateVersion(this, version.getVersionCode());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showUpdateStopsSnackbar() {
        Snackbar.make(clRoot, R.string.map_update_stops_message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.map_update_stops_update, view -> presenter.getAllStopsFromRemoteDatabase())
                .show();
    }

    @Override
    public void showNewVersionAvailable(Version version) {
        DialogUtils.showNewVersionAvailableDialog(this, this, version);
    }

    @Override
    public void initializeMap() {
        initializeGoogleMap();
    }

    @Override
    public void showMessage(int messageId) {
        Snackbar.make(clRoot, messageId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void placeMarkers(final List<Stop> stops) {
        if (map != null) {
            map.setOnCameraMoveListener(() -> {
                addItemsToMap(stops, map.getCameraPosition().zoom);
                requestLocationUpdates = false;
                stopLocationUpdates();
            });
        }
    }

    @Override
    public void showSelectedStop(final Stop stop, final boolean bookmarked, final boolean fromNavDrawer) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stop.getLatitude(), stop.getLongitude()),
                Constants.ZOOM_POSITION),
                new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        moveToAndShowSelectedStop(stop, bookmarked, fromNavDrawer);
                    }

                    @Override
                    public void onCancel() {
                        moveToAndShowSelectedStop(stop, bookmarked, fromNavDrawer);
                    }
                });
    }

    @Override
    public void showSearchResult(List<Stop> stops) {
        svSearch.swapSuggestions(stops);
    }

    @Override
    public void showStopBookmarks(List<StopBookmark> stopBookmarks) {
        ndStopBookmarks.removeAllItems();
        ndStopBookmarks.addItem(new SectionDrawerItem().withName(R.string.nav_drawer_section_bookmarks));
        for (StopBookmark stopBookmark : stopBookmarks) {
            ndStopBookmarks.addItem(new PrimaryDrawerItem()
                    .withIdentifier(stopBookmark.getId())
                    .withIcon(R.drawable.ic_stop_normal)
                    .withSelectedIcon(R.drawable.ic_stop_selected)
                    .withSelectedTextColor(ContextCompat.getColor(MapActivity.this, R.color.colorAccentText))
                    .withName(stopBookmark.getName()));
        }
    }

    @Override
    public void showProgressBar() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        pbLoading.setVisibility(View.GONE);
    }

    //------------------------------------------------------------------------------------------------------------------

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void updateCoordinates() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean hasProvider = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (hasProvider) {
            startLocationUpdates();
        } else {
            turnOnLocation();
        }
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationaleForLocation(final PermissionRequest request) {
        DialogUtils.showLocationRationaleDialog(this, request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onDeniedForLocation() {
        Snackbar.make(clRoot, R.string.map_permission_denied_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, view -> Navigation.navigateToAppSettings(this))
                .show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void downloadUpdate(Version version) {
        presenter.downloadUpdate((DownloadManager) getSystemService(DOWNLOAD_SERVICE), version);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForWriteExternalStorage(final PermissionRequest request) {
        DialogUtils.showWriteExternalStorageRationaleDialog(this, request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onDeniedForWriteExternalStorage() {
        Snackbar.make(clRoot, R.string.write_external_storage_denied_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, view -> Navigation.navigateToAppSettings(this))
                .show();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Intent newIntent(Context context) {
        return new Intent(context, MapActivity.class);
    }

    private void initializeGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fr_map);
        mapFragment.getMapAsync(this);
    }

    private void initializeGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API).build();
        }
    }

    private void initializeBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(flBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    fabLocation.show();
                    dropMarkerHighlight(map.getCameraPosition().zoom);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        initializeWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        WebSettings settings = wvDashboard.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setJavaScriptEnabled(true);
        wvDashboard.setOnTouchListener((v, event) -> true);
        wvDashboard.setWebViewClient(new WebViewClient());
    }

    private void initializeSearchView() {
        svSearch.setSearchHint(getString(R.string.search_view_hint));
        svSearch.setOnQueryChangeListener(this);
        svSearch.setOnSearchListener(this);
        svSearch.setOnBindSuggestionCallback(this);
        svSearch.setOnFocusChangeListener(this);
        svSearch.setOnMenuItemClickListener(this);
        svSearch.attachNavigationDrawerToMenuButton(ndStopBookmarks.getDrawerLayout());
    }

    private void initializeNavigationDrawer() {
        ndStopBookmarks = new DrawerBuilder()
                .withActivity(MapActivity.this)
                .withOnDrawerItemClickListener(this)
                .withOnDrawerItemLongClickListener(this)
                .build();
        presenter.getAllStopBookmarksFromLocalDatabase();
    }

    private void setupMap() {
        UiSettings settings = map.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setMyLocationButtonEnabled(false);
        settings.setMapToolbarEnabled(false);
        map.setBuildingsEnabled(true);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE), Constants.ZOOM_CITY));
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    private void loadWebView(String url) {
        CookieManager.getInstance().setCookie("", CookieUtils.getCookies(getApplicationContext()));
        wvDashboard.clearHistory();
        wvDashboard.loadUrl(url);
    }

    private void addItemsToMap(List<Stop> stops, float zoom) {
        LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
        LatLng latLng;
        int stopId;

        for (Stop stop : stops) {
            stopId = stop.getId();
            latLng = new LatLng(stop.getLatitude(), stop.getLongitude());
            if (zoom >= Constants.ZOOM_OVERVIEW) {
                if (latLngBounds.contains(latLng)) {
                    if (!presenter.containsMarker(stopId)) {
                        MarkerWrapper marker = new MarkerWrapper(map.addMarker(new MarkerOptions()
                                .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place))));
                        presenter.addVisibleMarker(stopId, marker);
                    }
                } else if (presenter.containsMarker(stopId) && !presenter.isMarkerSelected(stopId)) {
                    presenter.removeVisibleMarker(stopId);
                }
            } else if (presenter.containsMarker(stopId) && !presenter.isMarkerSelected(stopId)) {
                presenter.removeVisibleMarker(stopId);
            }
        }
    }

    private void highlightSelectedMarker(Integer key) {
        float zoom = map.getCameraPosition().zoom;
        if (zoom < Constants.ZOOM_OVERVIEW) {
            return;
        }
        dropMarkerHighlight(zoom);
        presenter.selectMarker(key);
    }

    private void moveToAndShowSelectedStop(Stop stop, boolean bookmarked, boolean fromNavDrawer) {
        highlightSelectedMarker(stop.getId());
        tvStopName.setText(stop.getName());
        cbBookmark.setChecked(bookmarked);
        loadWebView(Constants.URL_DASHBOARD + stop.getId());
        boolean shouldShowExpanded = fromNavDrawer
                && PreferenceUtils.getScheduleBehaviour(MapActivity.this.getApplicationContext());
        if (fabLocation.isShown()) {
            fabLocation.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    bottomSheetBehavior.setState(shouldShowExpanded
                            ? BottomSheetBehavior.STATE_EXPANDED
                            : BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        } else if (shouldShowExpanded) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void dropMarkerHighlight(float zoom) {
        presenter.dropMarkerHighlight(zoom, Constants.ZOOM_OVERVIEW);
    }

    private void turnOnLocation() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(getLocationRequest());
        builder.setAlwaysShow(true);

        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
                .setResultCallback(locationResult -> {
                    final Status status = locationResult.getStatus();
                    if (locationResult.getStatus().getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            status.startResolutionForResult(this, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, e.toString());
                        }

                    }
                });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (googleApiClient.isConnected() && requestLocationUpdates) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, getLocationRequest(), this);
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private LocationRequest getLocationRequest() {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);
        }
        return locationRequest;
    }
}
