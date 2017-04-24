package by.vshkl.translate2.ui.listener;

public interface StopBookmarkListener {

    void onEditBookmark();

    void onDeleteBookmark();

    void onDeleteConfirmed();

    void onRenameConfirmed(String newStopName);
}
