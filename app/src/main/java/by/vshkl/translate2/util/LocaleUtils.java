package by.vshkl.translate2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

import by.vshkl.translate2.R;

public class LocaleUtils {

    public static void setLocaleSettings(final Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean customLocale = preferences.getBoolean(context.getString(R.string.pref_key_custom_locale), false);
        if (customLocale) {
            return;
        }

        String countryName = Locale.getDefault().getCountry().toLowerCase();
        switch (countryName) {
            case "ru":
                preferences.edit().putString(context.getString(R.string.pref_language_key), "ru").apply();
                break;
            case "be":
                preferences.edit().putString(context.getString(R.string.pref_language_key), "be").apply();
                break;
            default:
                preferences.edit().putString(context.getString(R.string.pref_language_key), "en").apply();
                break;
        }
    }

    public static void setLocale(final Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean customLocale = preferences.getBoolean(context.getString(R.string.pref_key_custom_locale), false);
        if (!customLocale) {
            return;
        }

        String language = preferences.getString(
                context.getString(R.string.pref_language_key),
                context.getString(R.string.pref_language_default));
        setLocale(context, language);
    }

    private static void setLocale(final Context context, final String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
