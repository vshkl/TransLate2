package by.vshkl.translate2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.OnClick;
import by.vshkl.translate2.R;

public class MainActivity extends MvpAppCompatActivity implements OnMapReadyCallback {

    private static final float ZOOM_CITY = 11F;
    private static final float ZOOM_STREET = 11F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeMap();
    }

    //------------------------------------------------------------------------------------------------------------------

    @OnClick(R.id.fb_location)
    void onLocationClicked() {

    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.9024429, 27.5614649), ZOOM_CITY));
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_map);
        mapFragment.getMapAsync(MainActivity.this);
    }
}
