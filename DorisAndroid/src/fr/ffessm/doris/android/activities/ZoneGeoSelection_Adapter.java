/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code protected additional ZoneGeoSelection_Adapter imports
// additional imports
import android.widget.ImageButton;
import android.widget.ImageView;
import fr.ffessm.doris.android.tools.Outils;
//End of user code

public class ZoneGeoSelection_Adapter extends BaseAdapter  {
	
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
	//End of user code

	public ZoneGeoSelection_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateList();
	}
	
	protected void updateList(){
		// Start of user code protected ZoneGeoSelection_Adapter updateList
		// TODO find a way to query in a lazier way
		try{
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.zonegeoselection_listviewrow, null);
        }
		final ZoneGeographique entry = filteredZoneGeographiqueList.get(position);
		if(_contextDB != null) entry.setContextDB(_contextDB); 		
       
		// set data in the row 
		TextView tvLabel = (TextView) convertView.findViewById(R.id.zonegeoselection_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
		labelSB.append(entry.getNom());
		labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

		
        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.zonegeoselection_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional ZoneGeoSelection_Adapter getView code
		
        ImageButton selectbutton = (ImageButton) convertView.findViewById(R.id.zonegeoselection__selectBtn);
        selectbutton.setFocusable(false);
        selectbutton.setClickable(true);
        selectbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Filtre zone géographique : "+entry.getNom(), Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
				ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), entry.getId());
		        ed.commit();
				((ZoneGeoSelection_ClassListViewActivity) context).finish();
			}
		});
        
        
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.zonegeoselection_listviewrow_icon);
        String defaultIconSizeString = prefs.getString(context.getString(R.string.pref_key_accueil_icon_size), "128");
        int defaultIconSize = 128;
        try{
        	defaultIconSize = Integer.parseInt(defaultIconSizeString);
        }catch(Exception e){}
    	ivIcon.setMaxHeight(defaultIconSize);
    	
        // TODO : pas trés propre mais fonctionne => Modifier Outils ... vers entry.getIcone qd sera dispo
    	String uri = Outils.getZoneIcone(context, entry.getId()); 
    	int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
    	ivIcon.setImageResource(imageResource);
    	
		// End of user code

        return convertView;

	}

	
	//Start of user code protected additional ZoneGeoSelection_Adapter methods
	// additional methods
	//End of user code
}
