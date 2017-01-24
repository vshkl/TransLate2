package by.vshkl.translate2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.fb_location)
    void onLocationClicked() {

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
        final Snackbar snackbar = Snackbar.make(flRoot, "Update stops", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Update", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getAppStopsFromRemoteDatabase();
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
