package fr.ffessm.doris.android.activities;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import fr.ffessm.doris.android.R; // Assuming your R file is here

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey); // Use your converted preference.xml

        // You can add listeners or find preferences here if needed, for example:
        // Preference prechargStatus = findPreference(getString(R.string.pref_key_precharg_status));
        // if (prechargStatus != null) {
        // prechargStatus.setOnPreferenceClickListener(preference -> {
        // // Handle click on "Avancement précharg. et espace de stockage"
        // // Maybe launch a new activity or show a dialog
        // return true;
        // });
        // }
    }
}