package by.vshkl.translate2.util;

import android.content.Context;
import android.preference.PreferenceManager;

import by.vshkl.translate2.R;

public class PreferenceUtils {

    public static boolean getScheduleBehaviour(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_nav_stop_open_key), false);
    }

    public static int getIgnoreUpdateVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.pref_update_ignore_version), -1);
    }

    public static void setIgnoreUpdateVersion(Context context, int versionCodeToIgnore) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(context.getString(R.string.pref_update_ignore_version), versionCodeToIgnore).apply();
    }
}
