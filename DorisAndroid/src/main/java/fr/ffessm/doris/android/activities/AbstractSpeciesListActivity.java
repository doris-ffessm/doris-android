package fr.ffessm.doris.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.tools.SortModesTools;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

/**
 * Abstract gathering code common to all list views related to species
 */
public class AbstractSpeciesListActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {
    private static final String LOG_TAG = AbstractSpeciesListActivity.class.getSimpleName();

    private final String activityName = getClass().getSimpleName();

    private ActivityResultLauncher<Intent> selectionActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // --- 2. Register the ActivityResultContract and its callback ---
        // This MUST be done before the Activity is created (e.g., in onCreate or as an instance variable initializer)
        selectionActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(activityName, "ActivityResult received. ResultCode: " + result.getResultCode());
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                String action = data.getStringExtra("action");
                                Log.d(activityName, "Action from SelectionActivity: " + action);

                                if ("restart_current".equals(action)) {
                                    Log.i(activityName, "Action: Restarting self.");
                                    recreate();
                                } else if ("replace_with_new".equals(action)) {
                                    String targetActivityClassName = data.getStringExtra("target_activity_class_name");
                                    Log.i(activityName, "Action: Replacing self with " + targetActivityClassName);
                                    if (targetActivityClassName != null) {
                                        try {
                                            Class<?> targetClass = Class.forName(targetActivityClassName);
                                            Intent replaceIntent = new Intent(AbstractSpeciesListActivity.this, targetClass);


                                            startActivity(replaceIntent);
                                            finish(); // Finish the current AbstractSpeciesListActivity
                                        } catch (ClassNotFoundException e) {
                                            Log.e(activityName, "Could not find class for replacement: " + targetActivityClassName, e);
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(activityName, "DisplayModeSelectionActivity returned with code: " + result.getResultCode());
                        }
                    }
                });
    }

    /**
     * Open a popup window showing the filter and display mode options and allowing to change them
     */
    public void showFilterPopup() {

        View menuItemView = this.findViewById(R.id.listeficheavecfiltre_classlistview_action_filterpopup);
        // menuItemView may be null if there is not enough space in the action bar
        RelativeLayout viewGroup = this.findViewById(R.id.listeavecfiltre_filtrespopup);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.listeficheavecfiltre_filtrespopup, viewGroup);

        int popupWidth = this.getResources().getDimensionPixelSize(R.dimen.listeficheavecfiltre_popup_width);
        int popupHeight = this.getResources().getDimensionPixelSize(R.dimen.listeficheavecfiltre_popup_height);
        //Log.d(LOG_TAG,"showPopup() - width="+popupWidth+", height="+popupHeight);

        final PopupWindow popup = new PopupWindow(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);

        //popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        if (menuItemView != null) {
            menuItemView.getLocationOnScreen(location);
            Log.d(LOG_TAG, "menuitem pos =" + location[0] + " " + location[1]);

            popup.showAsDropDown(menuItemView, 0, 0);
        } else {
            Log.d(LOG_TAG, "menuitem pos not available, anchor to top of the listview");
            //popup.showAsDropDown(findViewById(R.id.listeficheavecfiltre_listview),0,0);
            View containerView = this.findViewById(R.id.listeimagegroupeavecfiltre_listview);
            containerView.getLocationOnScreen(location);
            Log.d(LOG_TAG, "menuitem pos =" + location[0] + " " + location[1] + " ");
            popup.showAtLocation(layout, Gravity.TOP | Gravity.END, 0, location[1]);
        }
        // species filter button
        Button btnFiltreEspece = layout.findViewById(R.id.listeavecfiltre_filtrespopup_GroupeButton);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int filtreCourantId = prefs.getInt(this.getString(R.string.pref_key_filtre_groupe), 1);
        if (filtreCourantId == 1) {
            btnFiltreEspece.setText(this.getString(R.string.listeficheavecfiltre_popup_filtreEspece_sans));
        } else {
            Groupe groupeFiltreCourant = this.getHelper().getGroupeDao().queryForId(filtreCourantId);
            btnFiltreEspece.setText(this.getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec) + " " + groupeFiltreCourant.getNomGroupe().trim());
        }

        btnFiltreEspece.setOnClickListener(v -> {
            popup.setFocusable(true);
            popup.dismiss();

            //Permet de revenir à cette liste après choix du groupe, True on retournerait à l'accueil
            Intent toGroupeSelectionView = new Intent(this, GroupeSelection_ClassListViewActivity.class);
            Bundle b = new Bundle();
            b.putBoolean("GroupeSelection_depuisAccueil", false);
            toGroupeSelectionView.putExtras(b);
            this.startActivity(toGroupeSelectionView);
        });

        // geographic zone button
        Button btnZoneGeo = layout.findViewById(R.id.listeavecfiltre_filtrespopup_ZoneGeoButton);
        int currentFilterId = prefs.getInt(this.getString(R.string.pref_key_filtre_zonegeo), -1);
        if (currentFilterId == -1) {
            btnZoneGeo.setText(this.getString(R.string.listeficheavecfiltre_popup_filtreGeographique_sans));
        } else {
            ZoneGeographique currentZoneFilter = this.getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
            btnZoneGeo.setText(this.getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec) + " " + currentZoneFilter.getNom().trim());
        }

        btnZoneGeo.setOnClickListener(v -> {
            popup.setFocusable(true);
            popup.dismiss();

            //Toast.makeText(getApplicationContext(), "Zone géographique", Toast.LENGTH_LONG).show();
            this.startActivity(new Intent(this, ZoneGeoSelection_ClassListViewActivity.class));
        });

        // display mode button
        Button btnDisplayMode = layout.findViewById(R.id.listeavecfiltre_filtrespopup_DisplayModeButton);
        String currentDisplayMode = prefs.getString(this.getString(R.string.pref_key_current_mode_affichage), getResources().getString(
                R.string.current_mode_affichage_default));

        btnDisplayMode.setText(this.getString(R.string.listeficheavecfiltre_popup_displaymode) + " " + currentDisplayMode);

        btnDisplayMode.setOnClickListener(v -> {
            popup.setFocusable(true);
            popup.dismiss();

            Intent intent = new Intent(this, DisplayModeSelection_ViewActivity.class);
            intent.putExtra("current_list_activity_class_name", getClass().getName());
            selectionActivityLauncher.launch(intent);
        });
    }


}
