package by.vshkl.translate2.util;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;

import by.vshkl.translate2.R;
import by.vshkl.translate2.ui.StopBookmarkListener;
import permissions.dispatcher.PermissionRequest;

public class DialogUtils {

    public static void showLocationRationaleDialog(Context context, PermissionRequest request) {
        new MaterialDialog.Builder(context)
                .title(R.string.map_permission_rationale_title)
                .content(R.string.map_permission_rationale_message)
                .positiveText(R.string.map_location_ok)
                .negativeText(R.string.map_location_cancel)
                .onPositive((dialog, which) -> request.proceed())
                .onNegative(((dialog, which) -> request.cancel()))
                .show();
    }

    public static void showLocationTurnOnDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.map_permission_rationale_title)
                .content(R.string.map_location_message)
                .positiveText(R.string.map_location_ok)
                .negativeText(R.string.map_location_cancel)
                .onPositive((dialog, which) -> Navigation.navigateToLocationSettings(context))
                .show();
    }

    public static void showBookmarkActionsDialog(Context context, StopBookmarkListener listener) {
        new MaterialDialog.Builder(context)
                .items(R.array.bookmarks_dialog_actions)
                .itemsColorRes(R.color.colorPrimaryText)
                .itemsCallback((dialog, itemView, position, text) -> {
                    if (position == 0) {
                        listener.onEditBookmark();
                    } else if (position == 1) {
                        listener.onDeleteBookmark();
                    }
                })
                .show();
    }

    public static void showBookmarkDeleteConfirmationDialog(Context context, StopBookmarkListener listener) {
        new MaterialDialog.Builder(context)
                .title(R.string.bookmark_delete_message)
                .positiveText(R.string.bookmark_delete_ok)
                .negativeText(R.string.bookmark_delete_cancel)
                .onPositive((dialog, which) -> listener.onDeleteConfirmed())
                .show();
    }

    public static void shoeBookmarkRenameDialog(Context context, String stopName, StopBookmarkListener listener) {
        new MaterialDialog.Builder(context)
                .title(R.string.bookmark_dialog_rename_title)
                .positiveText(R.string.bookmark_dialog_rename_ok)
                .negativeText(R.string.bookmark_dialog_rename_cancel)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(context.getString(R.string.bookmark_dialog_rename_hint), stopName, false,
                        (dialog, input) -> {
                        })
                .onPositive(
                        (dialog, which) -> listener.onRenameConfirmed(dialog.getInputEditText().getText().toString()))
                .show();
    }
}
