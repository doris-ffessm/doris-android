package fr.ffessm.doris.android.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.preferences.PreferenceViewHelper;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String ARG_SCREEN_TITLE = "screen_title";

    private static final String LOG_TAG = SettingsFragment.class.getCanonicalName();

    // Keys for the preferences inside the sub-screen whose values we monitor
    Context context;
    private Param_Outils paramOutils;
    private Photos_Outils photosOutils;
    private Disque_Outils disqueOutils;
    private CharSequence currentTitle;
    private PreferenceViewHelper preferenceViewHelper;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey); // Use your converted preference.xml

        context = requireContext(); // Get context once

        preferenceViewHelper = PreferenceViewHelper.getInstance(context);

        // Determine the title for the current screen
        if (getArguments() != null && getArguments().containsKey(ARG_SCREEN_TITLE)) {
            currentTitle = getArguments().getCharSequence(ARG_SCREEN_TITLE);
        } else if (!TextUtils.isEmpty(rootKey)) {
            // If navigating to a sub-screen using rootKey, find that PreferenceScreen
            // and use its title.
            PreferenceScreen subScreen = findPreference(rootKey);
            if (subScreen != null && subScreen.getTitle() != null) {
                currentTitle = subScreen.getTitle();
            }
        }

        // If still no title, try to get it from the root PreferenceScreen of the loaded XML
        if (TextUtils.isEmpty(currentTitle)) {
            PreferenceScreen root = getPreferenceScreen();
            if (root != null && root.getTitle() != null) {
                currentTitle = root.getTitle();
            }
        }

        // Fallback to a default title if none is found
        if (TextUtils.isEmpty(currentTitle)) {
            currentTitle = getString(R.string.preference_title); // Your default settings title
        }

        // Find the ListPreference instances that need the custom summary
        updateEntries();
        updateBtnQualiteImagesZonesSummary();
        updateBtnQualiteImagesZonesSummary();

    }

    private void setLibelleModePrechargPhotoZone(ListPreference lp, Constants.ZoneGeographiqueKind zoneGeoKind) {
        CharSequence summary = "";
        CharSequence[] entries = lp.getEntries();
        CharSequence[] entryValues = lp.getEntryValues();

        if (!getPhotosOutils(context).isPhotosParFicheInitialized)
            getPhotosOutils(context).initNbPhotosParFiche();

        for (int i = 0; i < entries.length; i++) {
            long volumeNecessaire = getPhotosOutils(context).getEstimVolPhotosParZone(
                    Photos_Outils.PrecharMode.valueOf(entryValues[i].toString()),
                    zoneGeoKind
            );
            entries[i] = entries[i].toString().replace("@size", getDisqueOutils(context).getHumanDiskUsage(volumeNecessaire));

            if (entryValues[i].toString().equals(lp.getValue())) {
                summary = entries[i];
            }

        }

        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
        lp.setSummary(summary);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateActivityTitle(); // Set the title when the view is created/restored
    }

    @Override
    public void onResume() {
        super.onResume();
        // Also update title onResume, as the fragment might be shown again after being paused
        // or after returning from a sub-screen via back press.
        updateActivityTitle();
        updateBtnEtatModeHorsLigne();
        updateEntries();
        updateBtnQualiteImagesZonesSummary();
    }


    private void updateActivityTitle() {
        if (getActivity() instanceof SettingsActivity && !TextUtils.isEmpty(currentTitle)) {
            ((SettingsActivity) getActivity()).setActivityTitle(currentTitle);
        }
    }

    private void updateEntries() {


        for (Integer keyResId : preferenceViewHelper.getPreferenceKeyResIds()) {
            ListPreference listPreference = findPreference(getString(keyResId));
            if (listPreference != null) {
                //  totalRequiredVolume += preferenceViewHelper.getEstimatedRequiredVolumeForPhotoZone(listPreference, PreferenceViewHelper.getKeyToZoneMap(context).get(listPreference.getKey()));
                setLibelleModePrechargPhotoZone(listPreference, preferenceViewHelper.getKeyToZoneMap().get(listPreference.getKey()));
            }
        }
    }

    private void updateBtnEtatModeHorsLigne() {
        // link to EtatModeHorsLigne and compute summary
        Preference button = findPreference(getString(R.string.pref_key_precharg_status));
        if (button != null) {
            ZoneGeographique zoneToutesZones = new ZoneGeographique();
            zoneToutesZones.setId(-1);
            zoneToutesZones.setNom(this.getString(R.string.avancement_touteszones_titre));

            String sb = getPhotosOutils(context).getCurrentPhotosDiskUsageShortSummary(context) +
                    "; ";

            button.setSummary(EtatModeHorsLigne_CustomViewActivity.updateProgressBarZone(context, zoneToutesZones, null, sb));
            button.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
                return true;
            });
        }
    }

    private void updateBtnQualiteImagesZonesSummary() {
        final Preference btnQualiteImagesZonesKey = findPreference("button_qualite_images_zones_key");

        if (btnQualiteImagesZonesKey != null) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            long totalRequiredVolume = 0;

            // This logic is similar to updateEntries() but based on the shared preferences values in order to update on backward navigation

            for (String regionKey : preferenceViewHelper.getPreferenceKeyString()) {
                String selectedValue = prefs.getString(regionKey, null /* default value if not found */);
                if (selectedValue != null) {
                    Constants.ZoneGeographiqueKind zone = preferenceViewHelper.getKeyToZoneMap().get(regionKey);
                    if (zone != null) {
                        try {
                            Photos_Outils.PrecharMode mode = Photos_Outils.PrecharMode.valueOf(selectedValue);
                            totalRequiredVolume += photosOutils.getEstimVolPhotosParZone(mode, zone);
                        } catch (IllegalArgumentException e) {
                            Log.e(LOG_TAG, "Invalid PrecharMode value '" + selectedValue + "' for key: " + regionKey);
                        }
                    }
                }
            }

            String summaryText = getString(R.string.mode_precharg_photo_region_summary) +
                    disqueOutils.getHumanDiskUsage(totalRequiredVolume);
            btnQualiteImagesZonesKey.setSummary(summaryText);
        }
    }

    private Photos_Outils getPhotosOutils(Context context) {
        if (photosOutils == null) photosOutils = new Photos_Outils(context);
        return photosOutils;
    }

    private Param_Outils getParamOutils(Context context) {
        if (paramOutils == null) paramOutils = new Param_Outils(context);
        return paramOutils;
    }

    private Disque_Outils getDisqueOutils(Context context) {
        if (disqueOutils == null) disqueOutils = new Disque_Outils(context);
        return disqueOutils;
    }
}