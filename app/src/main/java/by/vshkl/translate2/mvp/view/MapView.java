package by.vshkl.translate2.mvp.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import by.vshkl.translate2.mvp.model.Stop;

public interface MapView extends MvpView {

    void showUpdateStopsSnackbar();

    void initializeMap();

    void showMessage(int messageId);

    void placeMarkers(List<Stop> stopList);

    void showFab();

    void hideFab();

    void showSelectedStop(Stop stop);
}
