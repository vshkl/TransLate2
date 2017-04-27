package by.vshkl.translate2.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import by.vshkl.translate2.R;
import by.vshkl.translate2.database.local.LocalRepository;
import by.vshkl.translate2.mvp.model.Version;
import by.vshkl.translate2.ui.listener.AppUpdateListener;
import by.vshkl.translate2.ui.listener.StopBookmarkListener;
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

    public static void showWriteExternalStorageRationaleDialog(Context context, PermissionRequest request) {
        new MaterialDialog.Builder(context)
                .title(R.string.write_external_storage_rationale_title)
                .content(R.string.write_external_storage_rationale_message)
                .positiveText(R.string.write_external_storage_rationale_ok)
                .negativeText(R.string.write_external_storage_rationale_cancel)
                .onPositive((dialog, which) -> request.proceed())
                .onNegative(((dialog, which) -> request.cancel()))
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

    public static void showBookmarksDeleteConfirmationDialog(Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(R.string.bookmarks_delete_title)
                .content(R.string.bookmarks_delete_message)
                .positiveText(R.string.bookmark_delete_ok)
                .negativeText(R.string.bookmark_delete_cancel)
                .onPositive((dialog, which) -> LocalRepository.removeStopBookmarks()
                        .compose(RxUtils.applySchedulers())
                        .subscribe(aBoolean -> {
                            if (!aBoolean) {
                                Toast.makeText(activity, activity.getString(R.string.bookmarks_delete_success),
                                        Toast.LENGTH_SHORT).show();
                                Navigation.navigateToMap(activity.getBaseContext());
                            }
                        }))
                .show();
    }

    public static void showBookmarkRenameDialog(Context context, String stopName, StopBookmarkListener listener) {
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

    public static void showLogoutConfirmationDialog(Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(R.string.logout_title)
                .positiveText(R.string.logout_confirm)
                .negativeText(R.string.logout_decline)
                .onPositive((dialog, which) -> {
                    CookieUtils.deleteCookies(activity.getBaseContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        CookieManager.getInstance().removeAllCookies(null);
                        CookieManager.getInstance().flush();
                    } else {
                        CookieManager.getInstance().removeAllCookie();
                    }
                    Navigation.restartApp(activity.getBaseContext());
                })
                .show();
    }

    public static void showNewVersionAvailableDialog(Context context, AppUpdateListener listener, Version version) {
        new MaterialDialog.Builder(context)
                .title(R.string.update_dialog_title)
                .content(R.string.update_dialog_content, version.getVersionName(), version.getSize())
                .positiveText(R.string.update_dialog_ok)
                .negativeText(R.string.update_dialog_cancel)
                .neutralText(R.string.update_dialog_neutral)
                .onPositive((dialog, which) -> listener.onDownloadUpdate(version))
                .onNeutral((dialog, which) -> listener.onSkipThisUpdate())
                .show();
    }
}
