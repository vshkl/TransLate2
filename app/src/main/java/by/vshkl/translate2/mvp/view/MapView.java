package by.vshkl.translate2.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.translate2.mvp.model.Stop;
import by.vshkl.translate2.mvp.model.StopBookmark;
import by.vshkl.translate2.mvp.model.Version;

public interface MapView extends MvpView {

    void showUpdateStopsSnackbar();

    void showNewVersionAvailable(Version version);

    void initializeMap();

    void showMessage(int messageId);

    void showToast(int messageId);

    void placeMarkers(List<Stop> stops);

    void showSelectedStop(Stop stop, boolean bookmarked, boolean fromNavDrawer);

    void showSearchResult(List<Stop> stops);

    void showStopBookmarks(List<StopBookmark> stopBookmarks);

    void showProgressBar();

    void hideProgressBar();
}
