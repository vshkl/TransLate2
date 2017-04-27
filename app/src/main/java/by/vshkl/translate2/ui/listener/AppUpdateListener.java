package by.vshkl.translate2.ui.listener;

import by.vshkl.translate2.mvp.model.Version;

public interface AppUpdateListener {

    void onDownloadUpdate(Version version);

    void onSkipThisUpdate(Version version);
}
