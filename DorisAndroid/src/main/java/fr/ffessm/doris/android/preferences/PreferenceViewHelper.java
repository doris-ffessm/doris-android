package fr.ffessm.doris.android.preferences;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.sitedoris.Constants;

/**
 * Helper class to map preference view keys to geographic zones.
 */
public class PreferenceViewHelper {
    private static final String LOG_TAG = PreferenceViewHelper.class.getCanonicalName();

    private static volatile PreferenceViewHelper sInstance; // volatile for thread safety

    /**
     * Map from preference key to geographic zone
     */
    public Map<String, Constants.ZoneGeographiqueKind> getKeyToZoneMap() {
        return keyToZoneMap;
    }

    /**
     * Map from preference key to geographic zone
     */
    private final Map<String, Constants.ZoneGeographiqueKind> keyToZoneMap;

    public List<Integer> getPreferenceKeyResIds() {
        return preferenceKeyResIds;
    }

    private final List<Integer> preferenceKeyResIds;

    public List<String> getPreferenceKeyString() {
        return preferenceKeyString;
    }

    private final List<String> preferenceKeyString;


    private PreferenceViewHelper(Context context) {
        // Initialize the map here, ensuring context is available
        // Use application context to avoid leaks if this helper lives long
        Context appContext = context.getApplicationContext();
        keyToZoneMap = new HashMap<>();
        preferenceKeyResIds = new ArrayList<>();
        preferenceKeyString = new ArrayList<>();
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_france, Constants.ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_atlantne, Constants.ZoneGeographiqueKind.FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_mediter, Constants.ZoneGeographiqueKind.FAUNE_FLORE_MEDITERRANEE_FRANCAISE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_eaudouce, Constants.ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_atlantno, Constants.ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_indopac, Constants.ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_antarctique, Constants.ZoneGeographiqueKind.FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_caraibes, Constants.ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_guyanne, Constants.ZoneGeographiqueKind.FAUNE_FLORE_GUYANNE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_merrouge, Constants.ZoneGeographiqueKind.FAUNE_FLORE_MER_ROUGE);
        addMapping(appContext, R.string.pref_key_mode_precharg_photo_region_habitat, Constants.ZoneGeographiqueKind.FAUNE_FLORE_HABITAT);

    }

    private void addMapping(Context context, int keyResId, Constants.ZoneGeographiqueKind zone) {
        String key = context.getString(keyResId);
        keyToZoneMap.put(key, zone);
        preferenceKeyResIds.add(keyResId);
        preferenceKeyString.add(key);
    }

    /**
     * Gets the singleton instance of PreferenceViewHelper.
     *
     * @param context Context used for initialization (application context is preferred).
     *                The context is only used on the first call to create the instance.
     * @return The singleton instance.
     */
    public static PreferenceViewHelper getInstance(Context context) {
        if (sInstance == null) { // First check (no locking)
            synchronized (PreferenceViewHelper.class) { // Synchronized block
                if (sInstance == null) { // Second check (with locking)
                    sInstance = new PreferenceViewHelper(context);
                }
            }
        }
        return sInstance;
    }

}
