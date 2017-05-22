package by.vshkl.translate2.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import by.vshkl.translate2.mvp.model.Version;

public interface MapView extends MvpView {

    void showProgressBar();

    void hideProgressBar();

    void showUpdateStopsSnackbar();

    void showNewVersionAvailable(Version version);

    void showMessage(int messageId);

    void showToast(int messageId);

    void setupGoogleMap();

    void animateMapCamera(LatLng latLng, float zoomLevel);

    void placeMarkers(List<Stop> stops);

    void showSelectedStop(Stop stop, boolean bookmarked, boolean fromNavDrawer);

    void showSearchResult(List<Stop> stops);

    void showStopBookmarks(List<StopBookmark> stopBookmarks);
}
