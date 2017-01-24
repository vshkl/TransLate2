package by.vshkl.translate2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import by.vshkl.translate2.R;

public class MainActivity extends MvpAppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeMap();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
