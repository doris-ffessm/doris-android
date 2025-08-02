package fr.ffessm.doris.android.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;

public class DisplayModeUtils {

    public static List<DisplayModeRecord> getDisplayModeRecords(Context context) {
        List<DisplayModeRecord> displayModeRecords = new ArrayList<>();
        String[] modesValues = context.getResources().getStringArray(R.array.current_mode_affichage_values);
        String[] modesLibelles = context.getResources().getStringArray(R.array.current_mode_affichage_libelle);
        String[] modesDetails = context.getResources().getStringArray(R.array.current_mode_affichage_details);
        for (int i = 0; i < modesValues.length; i++) {
            displayModeRecords.add(new DisplayModeRecord(modesValues[i], modesLibelles[i], modesDetails[i]));
        }
        return displayModeRecords;
    }
}
