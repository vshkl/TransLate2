package by.vshkl.translate2.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;

public interface MapView extends MvpView {

    void showUpdateStopsSnackbar();

    void initializeMap();

    void showMessage(int messageId);

    void showToast(int messageId);

    void placeMarkers(List<Stop> stopList);

    void showSelectedStop(Stop stop, boolean bookmarked, boolean fromNavDrawer);

    void showSearchResult(List<Stop> stopList);

    void showStopBookmarks(List<StopBookmark> stopBookmarkList);

    void showProgressBar();

    void hideProgressBar();
}
