package fr.ffessm.doris.android.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

import fr.ffessm.doris.android.R;

public class SortModesTools {

    public static Drawable getDrawable(Context context, String sortModeValue) {
        Map<String, Drawable> map = getDrawableMap(context);
        Drawable res = map.get(sortModeValue);
        if (res != null) {
            return res;
        } else {
            // if not found return the first one
            return map.values().iterator().next();
        }
    }

    public static Map<String, Drawable> getDrawableMap(Context context) {
        TypedArray icons = context.getResources().obtainTypedArray(R.array.current_mode_affichage_icons);

        String[] values = context.getResources().getStringArray(R.array.current_mode_affichage_values);
        Map<String, Drawable> map = new HashMap<>();
        for (int i = 0; i < Math.min(values.length, values.length); i++) {
            map.put(values[i], icons.getDrawable(i));
        }
        return map;
    }
    public static Map<String, String> getLabelMap(Context context) {
        String[] labels = context.getResources().getStringArray(R.array.current_mode_affichage_libelle);

        String[] values = context.getResources().getStringArray(R.array.current_mode_affichage_values);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < Math.min(values.length, values.length); i++) {
            map.put(values[i], labels[i]);
        }
        return map;
    }
}
