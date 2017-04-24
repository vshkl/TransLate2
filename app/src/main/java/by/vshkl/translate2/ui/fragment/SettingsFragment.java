package by.vshkl.translate2.ui.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import by.vshkl.translate2.R;
import by.vshkl.translate2.ui.activity.SettingsActivity;
import by.vshkl.translate2.util.DialogUtils;
import by.vshkl.translate2.util.Navigation;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener,
        OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_delete_bookmarks_key)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_logout_key)).setOnPreferenceClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_language_key))) {
            sharedPreferences.edit().putBoolean(getString(R.string.pref_key_custom_locale), true).apply();
            Navigation.restartApp(getActivity().getBaseContext());
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_delete_bookmarks_key))) {
            DialogUtils.showBookmarksDeleteConfirmationDialog(getActivity());
            return true;
        } else if (preference.getKey().equals(getString(R.string.pref_logout_key))) {
            DialogUtils.showLogoutConfirmationDialog(getActivity());
            return true;
        } else {
            return false;
        }
    }
}
