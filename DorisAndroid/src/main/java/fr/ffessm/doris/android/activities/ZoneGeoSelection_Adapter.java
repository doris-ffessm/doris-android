/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android.activities;

import java.sql.SQLException;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

//Start of user code protected additional ZoneGeoSelection_Adapter imports
// additional imports
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import fr.ffessm.doris.android.tools.Fiches_Outils;
//End of user code
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Zones_Outils;

public class ZoneGeoSelection_Adapter extends BaseAdapter {

    private Context context;

    /**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
     */
    protected DorisDBHelper _contextDB = null;

    private static final String LOG_TAG = ZoneGeoSelection_Adapter.class.getCanonicalName();

    private List<ZoneGeographique> zoneGeographiqueList;
    public List<ZoneGeographique> filteredZoneGeographiqueList;
    SharedPreferences prefs;
    //Start of user code protected additional ZoneGeoSelection_Adapter attributes
    // additional attributes
    Param_Outils paramOutils;

    //End of user code

    public ZoneGeoSelection_Adapter(Context context, DorisDBHelper contextDB) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Start of user code protected ZoneGeoSelection_Adapter constructor
        // End of user code
        updateList();
    }

    protected void updateList() {
        // Start of user code protected ZoneGeoSelection_Adapter updateList
        // TODO find a way to query in a lazier way
        try {
            this.zoneGeographiqueList = _contextDB.zoneGeographiqueDao.queryForAll();
            this.filteredZoneGeographiqueList = this.zoneGeographiqueList;
        } catch (java.sql.SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        // End of user code
    }

    @Override
    public int getCount() {
        return filteredZoneGeographiqueList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredZoneGeographiqueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return zoneGeographiqueList.get(position).getId();
        //return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Start of user code protected additional ZoneGeoSelection_Adapter getView_assign code
        //if (convertView == null) { // due to indentation generation, avoid reuse of view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.zonegeoselection_listviewrow, null);
        //}
        final ZoneGeographique entry = filteredZoneGeographiqueList.get(position);
        if (_contextDB != null) entry.setContextDB(_contextDB);

        // set data in the row
        TextView tvLabel = (TextView) convertView.findViewById(R.id.zonegeoselection_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
        labelSB.append(entry.getNom());
        labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

        if (ScreenTools.getScreenWidth(context) > 500) { // TODO devra probablement être adapté lorsque l'on aura des fragments
            TextView tvDetails = (TextView) convertView.findViewById(R.id.zonegeoselection_listviewrow_details);
            StringBuilder detailsSB = new StringBuilder();
            detailsSB.append(entry.getDescription());
            detailsSB.append(" ");
            tvDetails.setText(detailsSB.toString());
        } else {
            convertView.findViewById(R.id.zonegeoselection_listviewrow_details).setVisibility(View.GONE);
        }

        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout) convertView.findViewById(R.id.zonegeoselection_listviewrow);
        llRow.setTag(entry);

        // Start of user code protected additional ZoneGeoSelection_Adapter getView code

        RadioButton imgBtnH = (RadioButton) convertView.findViewById(R.id.zonegeoselection_selectBtn_radio);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentZoneFilterId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
        imgBtnH.setChecked(entry.getId() == currentZoneFilterId);
        imgBtnH.setOnClickListener(v -> {
            if (((ZoneGeoSelection_ClassListViewActivity) context).isActivityDestroyed() || ((ZoneGeoSelection_ClassListViewActivity) context).isFinishing())
                return;
            Toast.makeText(context, "Filtre zone géographique : " + entry.getNom(), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
            ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), entry.getId());
            ed.apply();
            ((ZoneGeoSelection_ClassListViewActivity) context).finish();
        });

        ImageView ivIcone = (ImageView) convertView.findViewById(R.id.zonegeoselection_listviewrow_icon);

        LinearLayout treeNodeZone = (LinearLayout)convertView.findViewById(R.id.zonegeoselection_tree_nodes);

        try {
        int zoneDepth = Zones_Outils.getZoneLevel(entry);
        if(treeNodeZone.getChildCount() == 1) {
            for (int i = 0; i < zoneDepth; i++) {
                ImageView image = new ImageView(this.context);
                //image.setAdjustViewBounds(true);
                image.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                if (Zones_Outils.isLastChild(entry)) {
                    image.setImageResource(R.drawable.ic_app_treenode_last_child);
                } else {
                    image.setImageResource(R.drawable.ic_app_treenode_middle_child);
                }
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                // Adds the view to the layout
                treeNodeZone.addView(image, 0);
            }
        }
        } catch (SQLException throwables) {
            Log.e(LOG_TAG, "Error determining zonegeo sibling", throwables);
            throwables.printStackTrace();
        }
        //Log.d(LOG_TAG, "getView() - R.string.pref_key_accueil_icone_taille : " + context.getString(R.string.pref_key_accueil_icone_taille) );
        //Log.d(LOG_TAG, "getView() - R.string.accueil_icone_taille_defaut : " + context.getString(R.string.accueil_icone_taille_defaut) );
        int defaultIconSize = getParamOutils().getParamInt(
                R.string.pref_key_accueil_icone_taille,
                Integer.parseInt(context.getString(R.string.accueil_icone_taille_defaut)));
        //Log.d(LOG_TAG, "getView() - defaultIconSize : " + defaultIconSize );

        int iconeTaille = ScreenTools.dp2px(context, defaultIconSize);
        ivIcone.setMaxHeight(iconeTaille);
        ivIcone.setMaxWidth(iconeTaille);

        // TODO : pas très propre mais fonctionne => Modifier Outils ... vers entry.getIcone qd sera dispo
        Fiches_Outils fichesOutils = new Fiches_Outils(context);
        String uri = fichesOutils.getZoneIcone(entry.getZoneGeoKind());
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        ivIcone.setImageResource(imageResource);

        // End of user code

        return convertView;

    }


    //Start of user code protected additional ZoneGeoSelection_Adapter methods
    // additional methods
    private Param_Outils getParamOutils() {
        if (paramOutils == null) paramOutils = new Param_Outils(context);
        return paramOutils;
    }
    //End of user code
}
