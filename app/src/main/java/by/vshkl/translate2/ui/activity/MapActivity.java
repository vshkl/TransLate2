package by.vshkl.translate2.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.vshkl.translate2.R;
import by.vshkl.translate2.mvp.presenter.MapPresenter;
import by.vshkl.translate2.mvp.view.MapView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapActivity extends MvpAppCompatActivity implements MapView, OnMapReadyCallback {

    private static final float ZOOM_CITY = 11F;
    private static final float ZOOM_STREET = 15F;

    @BindView(R.id.root) FrameLayout flRoot;

    @InjectPresenter MapPresenter presenter;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(MapActivity.this);
        initializeMap();
        presenter.checkStopsUpdate();
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
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.9024429, 27.5614649), ZOOM_CITY));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void showUpdateStopsSnackbar() {
        final Snackbar snackbar = Snackbar.make(flRoot, R.string.map_update_stops_message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.map_update_stops_update, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getAppStopsFromRemoteDatabase();
            }
        });
        snackbar.show();
    }

    //------------------------------------------------------------------------------------------------------------------

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void updateCoordinates() {

    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationaleForLocation(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.map_permission_rationale_title)
                .setMessage(R.string.map_permission_rationale_message)
                .setPositiveButton(R.string.map_permission_rationale_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.map_permission_rationale_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onDeniedForLocation() {
        final Snackbar snackbar = Snackbar.make(flRoot, R.string.map_permission_denied_message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.map_permission_denied_settings, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.show();
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        return intent;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_map);
        mapFragment.getMapAsync(MapActivity.this);
    }
}
