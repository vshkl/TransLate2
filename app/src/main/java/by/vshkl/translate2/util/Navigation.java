package by.vshkl.translate2.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import by.vshkl.translate2.ui.activity.LoginActivity;
import by.vshkl.translate2.ui.activity.MapActivity;

public class Navigation {

    public static void navigateToLogin(Context context) {
        context.startActivity(LoginActivity.newIntent(context));
    }

    public static void navigateToMap(Context context) {
        context.startActivity(MapActivity.newIntent(context));
    }

    public static void navigateToAppSettings(Context context) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    public static void navigateToLocationSettings(Context context) {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }
}