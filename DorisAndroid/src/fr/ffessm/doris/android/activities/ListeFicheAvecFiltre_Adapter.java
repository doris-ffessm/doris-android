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
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code protected additional ListeFicheAvecFiltre_Adapter imports
// additional imports

import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.sql.SQLException;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.OutilsGroupe;


//End of user code

public class ListeFicheAvecFiltre_Adapter extends BaseAdapter   implements Filterable{
	
	private Context context;

	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	private static final String LOG_TAG = ListeFicheAvecFiltre_Adapter.class.getCanonicalName();

    private List<Fiche> ficheList;
    private List<Fiche> filteredFicheList;
	private final Object mLock = new Object();
	private SimpleFilter mFilter;
	//Start of user code protected additional ListeFicheAvecFiltre_Adapter attributes
	// additional attributes

	SharedPreferences prefs;
	protected Groupe filtreGroupe;
	
	// vide signifie que l'on accepte tout
	protected ArrayList<Integer> acceptedGroupeId = new ArrayList<Integer>();
	int filteredZoneGeoId = 0;

	//End of user code

	public ListeFicheAvecFiltre_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		// TODO find a way to query in a lazy way
		updateList();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	protected void  updateList(){
		try{
			
			if(filteredZoneGeoId == 0){
				this.ficheList = _contextDB.ficheDao.queryForAll();
			}
			else{
				//Log.d(LOG_TAG,  "_contextDB= "+_contextDB);
				//Log.d(LOG_TAG,  "_contextDB.fiches_ZonesGeographiquesDao= "+_contextDB.fiches_ZonesGeographiquesDao);
				List<Fiches_ZonesGeographiques> listeAssoc= _contextDB.fiches_ZonesGeographiquesDao.queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, filteredZoneGeoId);
				this.ficheList = new ArrayList<Fiche>(listeAssoc.size());
				if(listeAssoc !=  null)	for (Fiches_ZonesGeographiques fiches_ZonesGeographiques : listeAssoc) {
					if(_contextDB !=null) fiches_ZonesGeographiques.setContextDB(_contextDB);
					Fiche fiche = fiches_ZonesGeographiques.getFiche();
					fiche.getPhotoPrincipale(); // bizarre besoin de faire cela ici pour s'assurer que la photo principâle soit bien chargée !?
					this.ficheList.add(fiche);
				}
			}
			this.filteredFicheList = this.ficheList;
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
	}
	
	@Override
	public int getCount() {
		return filteredFicheList.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredFicheList.get(position);

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Fiche entry = filteredFicheList.get(position);
		//Log.d(LOG_TAG, "getView entry="+entry.getId());
		//Log.d(LOG_TAG, "getView entry.getContextDB()"+entry.getContextDB());
		if(_contextDB != null) entry.setContextDB(_contextDB);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listeficheavecfiltre_listviewrow, null);
        }
       
		// set data in the row 
		TextView tvLabel = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
				labelSB.append(entry.getNomCommun());
			
			labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

        TextView tvDetails = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_details);
		StringBuilder detailsSB = new StringBuilder();
		detailsSB.append(entry.getNomScientifique().toString());
		detailsSB.append(" ");
        tvDetails.setText(detailsSB.toString());
		
        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.listeficheavecfiltre_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional ListeFicheAvecFiltre_Adapter getView code
		//	additional code
        
        String defaultIconSizeString = prefs.getString(context.getString(R.string.pref_key_list_icon_size), "48");
        int defaultIconSize = 48;
        try{
        	defaultIconSize = Integer.parseInt(defaultIconSizeString);
        }catch(Exception e){}
        
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_icon);
    	ivIcon.setMaxHeight(defaultIconSize);
    	
    	PhotoFiche photoPrincipale = entry.getPhotoPrincipale();
    	if(photoPrincipale == null){
	    	//try {
	    		Log.w(LOG_TAG, "bizarre photoprincipale="+photoPrincipale.getCleURL()+" application d'un workaround temporaire");
	    		//((ListeFicheAvecFiltre_ClassListViewActivity)context).getHelper().getFicheDao()
	    		Fiche fiche =((ListeFicheAvecFiltre_ClassListViewActivity)context).getHelper().getFicheDao().queryForId(entry.getId());
	    		fiche.setContextDB(_contextDB);
	    		((ListeFicheAvecFiltre_ClassListViewActivity)context).getHelper().getFicheDao().refresh(fiche);
	    		//fiche.
	    		photoPrincipale =fiche.getPhotoPrincipale();
				//_contextDB.ficheDao.refresh(entry);
			/*} catch (SQLException e1) {
				Log.e(LOG_TAG, e1.getMessage(),e1);
			}*/
    	}
        if(photoPrincipale != null){
        	photoPrincipale.setContextDB(_contextDB);

    		Log.d(LOG_TAG, "getView photoprincipale="+photoPrincipale.getCleURL());
        	if(Outils.isAvailableVignettePhotoFiche(context, photoPrincipale)){
        		try {
        			//Log.d(LOG_TAG, "from disk "+photoPrincipale.getCleURL());
					Picasso.with(context)
						.load(Outils
						.getVignetteFile(context, photoPrincipale))
						.placeholder(R.drawable.ic_launcher)  // utilisation de l'image par defaut pour commencer
						.into(ivIcon);
				} catch (IOException e) {
				}
        	}
        	else{
        		// pas préchargée en local pour l'instant, cherche sur internet
        		Picasso.with(context)
        			.load(PhotoFiche.VIGNETTE_BASE_URL+photoPrincipale.getCleURL())
					.placeholder(R.drawable.ic_launcher)  // utilisation de l'image par defaut pour commencer
					.error(R.drawable.doris_large_pas_connecte)
        			.into(ivIcon);
        	}
        }
       
        
        TextView btnEtatFiche = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow__btnEtatFiche);
        switch(entry.getEtatFiche()){
        case 1:
        	btnEtatFiche.setVisibility(View.VISIBLE);
        	btnEtatFiche.setText(" R ");
        	btnEtatFiche.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, R.string.ficheredaction_explications, Toast.LENGTH_LONG).show();
				}
			});
        	break;
        case 5:
        	btnEtatFiche.setVisibility(View.VISIBLE);
        	btnEtatFiche.setText(" P ");
        	btnEtatFiche.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, R.string.ficheproposee_explications, Toast.LENGTH_LONG).show();
				}
			});
        	break;
        case 4:
        	btnEtatFiche.setVisibility(View.GONE);
        	
        	break;
        default:
        	btnEtatFiche.setVisibility(View.VISIBLE);
        	btnEtatFiche.setText(" "+entry.getEtatFiche()+" ");
        }
        
		// End of user code

        return convertView;

	}

	//Start of user code protected additional ListeFicheAvecFiltre_Adapter methods
	// additional methods
	public void refreshFilter(){
		try {
			Groupe searchedGroupe = _contextDB.groupeDao.queryForId(prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), 1));
			//Log.d(LOG_TAG, "filter _contextDB="+_contextDB);
			searchedGroupe.setContextDB(_contextDB);
			acceptedGroupeId = new ArrayList<Integer>();
			for (Groupe groupe : OutilsGroupe.getAllSubGroupesForGroupe(searchedGroupe)) {
				acceptedGroupeId.add(groupe.getId());
			}
			int oldFilteredZoneGeoId = filteredZoneGeoId;
			filteredZoneGeoId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), 0);
			if(oldFilteredZoneGeoId != filteredZoneGeoId){
				//need full query
				updateList();
			}
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage(),e);
		}
	}
	
	//End of user code
	protected boolean sortAfterFilter() {
		return false;
	}
	
	public int filter(int position, Fiche fiche, String pattern){
		// Start of user code protected additional ListeFicheAvecFiltre_Adapter filter code
		// TODO probablement faire en sorte d'ignorer les accents pour la recherche
		// chercher séparement les mots (séparés par un blanc) et faire un "ET" 
		String[] patterns = pattern.split(" ");
		boolean isValid = true;
		for (String patt : patterns) {
			if(patt.isEmpty()) continue; // en cas de blanc multiples
			if(patt.equals("*")) break;  // accepte tout; aussi utilisé pour le filtre en retour de sélection de filtre
			if(fiche.getNomCommun().toLowerCase().contains(patt))
				continue;
			else if(fiche.getNomScientifique().toLowerCase().contains(pattern)) 
				continue;
			//else if(fiche.getAutresDenominations().contains(pattern)) return 1; 
			else isValid = false;
		}
		
		if(isValid){
			Groupe groupeFiche = fiche.getGroupe();
			if(groupeFiche != null)
			{
				groupeFiche.setContextDB(_contextDB);
				if(!acceptedGroupeId.isEmpty() && !acceptedGroupeId.contains(Integer.valueOf(groupeFiche.getId()))){
					isValid = false;
				}
			}
		}
		/*if(isValid){
			// vérifie si la zone doit être filtrée
			
			if(filteredZoneGeoId != 0){
				isValid = false;
				for(ZoneGeographique zoneFiche : fiche.getZonesGeographiques()){
					if(zoneFiche.getId() == filteredZoneGeoId) isValid = true;
					break;
				}
			}
		}*/
		if(isValid) return 1;		
		else return -1;
		// End of user code
	}
	
	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new SimpleFilter();
		}
		return mFilter;
	}
	
	private class SimpleFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			/*if (ficheList == null) {
				synchronized (mLock) {
					ficheList = new ArrayList<Fiches>(mObjects);
				}
			}*/

			if (prefix == null || prefix.length() == 0) {
				synchronized (mLock) {
					ArrayList<Fiche> list = new ArrayList<Fiche>(ficheList);
					results.values = list;
					results.count = list.size();
				}
			} else {
				String prefixString = prefix.toString().toLowerCase();
				boolean sort = sortAfterFilter();

				final List<Fiche> values = ficheList;
				final int count = values.size();
				
				final ArrayList<Fiche> newValues = new ArrayList<Fiche>(count);
				final int[] orders = sort ? new int[count] : null;

				for (int i = 0; i < count; i++) {
					final Fiche value = values.get(i);
					int order = ListeFicheAvecFiltre_Adapter.this.filter(i, value, prefixString);
					if (order >= 0) {
						if (sort)
							orders[newValues.size()] = order;
						newValues.add(value);
					}
				}
				
				if (sort) {
					Comparator<Fiche> c = new Comparator<Fiche>() {
						public int compare(Fiche object1, Fiche object2) {
							// Start of user code protected additional ListeFicheAvecFiltre_Adapter compare code
							int i1 = newValues.indexOf(object1);
							int i2 = newValues.indexOf(object2);
							return orders[i1] - orders[i2];
							// End of user code
						}
					};
					Collections.sort(newValues, c);
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count > 0) {
				filteredFicheList = (List<Fiche>) results.values;
				notifyDataSetChanged();
			} else {
				filteredFicheList = new ArrayList<Fiche>();
				notifyDataSetInvalidated();
			}
		}
	}
}
