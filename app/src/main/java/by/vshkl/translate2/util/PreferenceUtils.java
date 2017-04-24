package by.vshkl.translate2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import by.vshkl.translate2.R;

public class PreferenceUtils {

    public static boolean getScheduleBehaviour(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pref_nav_stop_open_key), false);
    }
}
