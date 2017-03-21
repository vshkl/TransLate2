package by.vshkl.translate2.ui;

public interface StopBookmarkListener {

    void onEditBookmark();

    void onDeleteBookmark();

    void onDeleteConfirmed();

    void onRenameConfirmed(String newStopName);
}
