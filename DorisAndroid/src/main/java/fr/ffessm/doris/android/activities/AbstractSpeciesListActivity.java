package fr.ffessm.doris.android.activities;

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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.preference.PreferenceManager;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

public class AbstractSpeciesListActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {
    private static final String LOG_TAG = AbstractSpeciesListActivity.class.getSimpleName();

    public void showPopup() {

        View menuItemView = this.findViewById(R.id.listeficheavecfiltre_classlistview_action_filterpopup);
        // peut être null si pas visible, ex: dans actionbar overflow si pas assez de place dans l'action bar
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
        // bouton filtre espèce
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

        // bouton filtre zone géographique
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

    }
}
