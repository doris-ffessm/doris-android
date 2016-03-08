/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.DetailsFiche_ElementViewActivity.OnImageClickListener;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//Start of user code protected additional ListeFicheAvecFiltre_Adapter imports
// additional imports

import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.j256.ormlite.dao.GenericRawResults;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.text.StrBuilder;

import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.Textes_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

//End of user code

public class ListeImageFicheAvecFiltre_Adapter extends BaseAdapter   implements Filterable{
	
	private Context context;

	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	private static final String LOG_TAG = ListeImageFicheAvecFiltre_Adapter.class.getCanonicalName();

    private List<Integer> ficheIdList;
    public List<Integer> filteredFicheIdList;
	LruCache<Integer, Fiche> ficheCache =  new LruCache<Integer, Fiche>(100);
	private final Object mLock = new Object();
	private SimpleFilter mFilter;
	SharedPreferences prefs;
	//Start of user code protected additional ListeImageFicheAvecFiltre_Adapter attributes
	// additional attributes

	protected Groupe filtreGroupe;
	protected Reseau_Outils reseauOutils;
	protected Photos_Outils photosOutils;
	protected Fiches_Outils fichesOutils;
	
	// vide signifie que l'on accepte tout
	protected ArrayList<Integer> acceptedGroupeId = new ArrayList<Integer>();
	int filteredZoneGeoId = -1;
	int filteredGroupeId = 1;

	public ListeImageFicheAvecFiltre_Adapter(Context context, DorisDBHelper contextDB, int filteredZoneGeoId, int filteredGroupeId) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		this.filteredZoneGeoId = filteredZoneGeoId;
		this.filteredGroupeId = filteredGroupeId;
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		reseauOutils = new Reseau_Outils(context);
		photosOutils = new Photos_Outils(context);
		fichesOutils = new Fiches_Outils(context);
		
		updateList();
	} 
	
	
	//End of user code

	public ListeImageFicheAvecFiltre_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Start of user code protected ListeImageFicheAvecFiltre_Adapter constructor
		
		reseauOutils = new Reseau_Outils(context);
		photosOutils = new Photos_Outils(context);
		fichesOutils = new Fiches_Outils(context);
		
		// End of user code
		updateList();
	}
	
	protected void updateList(){
		// Start of user code protected ListeImageFicheAvecFiltre_Adapter updateList
		
		// TODO : Bizarre que ce soit passé ainsi ....
		int filtreGroupe = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), 1);
				
		this.filteredFicheIdList = fichesOutils.getListeIdFichesFiltrees(context, _contextDB, filteredZoneGeoId, filtreGroupe);
		this.ficheIdList = fichesOutils.getListeIdFiches();
		// End of user code
	}

	@Override
	public int getCount() {
		if(filteredFicheIdList.size() == 0){
			return 1;	// will create a dummy entry to invite changing the filters
        }
		return filteredFicheIdList.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredFicheIdList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		// Start of user code protected additional ListeFicheAvecFiltre_Adapter getView_assign code
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listeimageficheavecfiltre_listviewrow, null);
        }
		if(filteredFicheIdList.size() == 0){
        	return getNoResultSubstitute(convertView);
        }
		final Fiche entry = getFicheForId(filteredFicheIdList.get(position));
		if(entry == null) return convertView;
		
		TextView tvDetails = (TextView) convertView.findViewById(R.id.listeimageavecfiltre_listviewrow_details);
		tvDetails.setVisibility(View.GONE);
		
		TextView tvLabel = (TextView) convertView.findViewById(R.id.listeimageficheavecfiltre_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
        
        if (entry.getNomCommunNeverEmpty() != "") {
        	labelSB.append(entry.getNomCommunNeverEmpty());
        } else {
        	labelSB.append(entry.getNomScientifiqueTxt());
        	tvLabel.setTypeface(tvLabel.getTypeface(), Typeface.ITALIC);
        }
		labelSB.append(" ");
        tvLabel.setText(labelSB.toString());
        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.listeimageficheavecfiltre_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional ListeFicheAvecFiltre_Adapter getView code
		//	additional code
        HorizontalScrollView hsGallery = (HorizontalScrollView) convertView.findViewById(R.id.listeimageficheavecfiltre_elementview_gallery_horizontalScrollView);
        hsGallery.setVisibility(View.VISIBLE);
        
        String defaultIconSizeString = prefs.getString(context.getString(R.string.pref_key_list_icon_size), "48");
        int defaultIconSize = 48;
        try{
        	defaultIconSize = Integer.parseInt(defaultIconSizeString);
        }catch(Exception e){}

    	
        Collection<PhotoFiche> photosFiche = entry.getPhotosFiche(); 
		if(photosFiche!=null){
			//sbDebugText.append("\nnbPhoto="+photosFiche.size()+"\n");

			LinearLayout photoGallery = (LinearLayout) convertView.findViewById(R.id.listeimageficheavecfiltre_elementview_photogallery);
			photoGallery.removeAllViews();

			
			int pos = 0;
			for (PhotoFiche photoFiche : photosFiche) {
				View photoView = insertPhoto(photoFiche);
				//photoView.setOnClickListener(new OnImageClickListener(entry.getId(),pos,this));
				//photoView.setOnClickListener(Log.d(LOG_TAG,"ListeImageFicheAvecFiltre_Adapter - getView"));
				photoView.setPadding(0, 0, 2, 0);
				photoGallery.addView(photoView);
				
				photoGallery.setClickable(true);
				View.OnClickListener ficheLauncher = new View.OnClickListener()
					{ @Override
						public void onClick(View v) {
							Log.d(LOG_TAG,"ListeImageFicheAvecFiltre_Adapter - onClick");

							DorisApplicationContext.getInstance().setIntentPourRetour(((Activity) context).getIntent());
							
							Intent toDetailView = new Intent(context, DetailsFiche_ElementViewActivity.class);
							toDetailView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							Bundle b = new Bundle();
					        b.putInt("ficheId", entry.getId());
							toDetailView.putExtras(b);
							context.getApplicationContext().startActivity(toDetailView);
						};
					};
				
				photoView.setOnClickListener(ficheLauncher);

				
				final int posImageCourante = pos; 
				View.OnLongClickListener photoLauncher = new View.OnLongClickListener()
				{ @Override
					public boolean onLongClick(View v) {
						Log.d(LOG_TAG,"ListeImageFicheAvecFiltre_Adapter - onLongClick");
						
						DorisApplicationContext.getInstance().setIntentPourRetour(((Activity) context).getIntent());
						
						Intent toImageView = new Intent(context, ImagePleinEcran_CustomViewActivity.class);
						toImageView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						toImageView.putExtra("position", posImageCourante);
						toImageView.putExtra("ficheId", entry.getId());
						context.startActivity(toImageView);
			            
						return true;
					};
				};
			
				photoView.setOnLongClickListener(photoLauncher);
				
				pos++;
			}
			
		}
       
        
		// End of user code

        return convertView;

	}

	protected View getNoResultSubstitute(View convertView){
		TextView tvLabel = (TextView) convertView.findViewById(R.id.listeimageficheavecfiltre_listviewrow_label);
		tvLabel.setText(R.string.listeficheavecfiltre_classlistview_no_result);
		// Start of user code protected additional ListeFicheAvecFiltre_Adapter getNoResultSubstitute code
		try{
			StringBuilder sbRechercheCourante = new StringBuilder();
	        int filtreCourantId = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), 1);	        
			if(filtreCourantId==1){
				sbRechercheCourante.append(context.getString(R.string.accueil_recherche_precedente_filtreEspece_sans));
	        }
			else {
				Groupe groupeFiltreCourant = _contextDB.groupeDao.queryForId(filtreCourantId);
				sbRechercheCourante.append(context.getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec)+" "+groupeFiltreCourant.getNomGroupe().trim());
			}
			sbRechercheCourante.append(" ; ");
			int currentFilterId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
	        if(currentFilterId == -1 || currentFilterId == 0){ // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
	        	sbRechercheCourante.append(context.getString(R.string.accueil_recherche_precedente_filtreGeographique_sans));
	        }
	        else{
	        	ZoneGeographique currentZoneFilter= _contextDB.zoneGeographiqueDao.queryForId(currentFilterId);
	        	sbRechercheCourante.append(context.getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec)+" "+currentZoneFilter.getNom().trim());
	        }
	        // TODO ajouter le filtre textuel courant qui lui aussi peut impliquer de ne retourner aucun résultats
	        TextView tvDetails = (TextView) convertView.findViewById(R.id.listeimageavecfiltre_listviewrow_details);
			tvDetails.setText( sbRechercheCourante.toString() );
			tvDetails.setVisibility(View.VISIBLE);
			
			HorizontalScrollView hsGallery = (HorizontalScrollView) convertView.findViewById(R.id.listeimageficheavecfiltre_elementview_gallery_horizontalScrollView);
			hsGallery.setVisibility(View.GONE);
		} catch (SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		// End of user code

		return convertView;
	}
	protected Fiche getFicheForId(Integer ficheId){
		Fiche f = ficheCache.get(ficheId);
		if(f != null) return f;
		try {
			f = _contextDB.ficheDao.queryForId(ficheId);
			ficheCache.put(ficheId, f);
			if(_contextDB != null) f.setContextDB(_contextDB);
			return f;
		} catch (SQLException e1) {
			Log.e(LOG_TAG, "Cannot retreive fiche with _id = "+ficheId+" "+e1.getMessage(), e1);
			return null;
		}
	}

	public HashMap<Character, Integer> getUsedAlphabetHashMap(){
		HashMap<Character, Integer> alphabetToIndex = new HashMap<Character, Integer>();
		Log.d(LOG_TAG,"getUsedAlphabetHashMap - début");
		int base_list_length=filteredFicheIdList.size();
		if(base_list_length < 100 ){
			// the base has been filtered so return the element from the filtered one
			alphabetToIndex=new HashMap<Character, Integer>();
			
			
			for(int i=0; i < base_list_length; i++){
				Fiche entry = getFicheForId(filteredFicheIdList.get(i));
				char firstCharacter=getFirstCharForIndex(entry);
				boolean presentOrNot=alphabetToIndex.containsKey(firstCharacter);
				if(!presentOrNot){
					alphabetToIndex.put(firstCharacter, i);
					//Log.d(TAG,"Character="+firstCharacter+"  position="+i);
				}
			}
			
		}
		else{
			// large list
			// use binarysearch if large list
			String alphabet_list[]= context.getResources().getStringArray(R.array.alphabet_array);
			int startSearchPos = 0;
			for (int i = 0; i < alphabet_list.length; i++) {
				int foundPosition = binarySearch(alphabet_list[i].charAt(0), startSearchPos, base_list_length-1);
				if(foundPosition != -1){
					alphabetToIndex.put(alphabet_list[i].charAt(0), foundPosition);
					startSearchPos = foundPosition; // mini optimisation, no need to look before for former chars
				}
			}
		}
		Log.d(LOG_TAG,"getUsedAlphabetHashMap - fin");
		return alphabetToIndex;
	}
	
	protected char getFirstCharForIndex(Fiche entry){
		//Start of user code protected ListeFicheAvecFiltre_Adapter binarySearch custom
		if(entry.getNomCommunNeverEmpty().length() == 0) return '#';
    	return entry.getNomCommunNeverEmpty().charAt(0);
	  	//End of user code
	}


	/**
	 * 
	 * @param key to be searched
	 * @param startBottom initial value for bottom, default = 0
	 * @param startTop initial top value, default = array.length -1
	 * @return
	 */
	public int binarySearch( char key, int startBottom, int startTop) {
	   int bot = startBottom;
	   int top = startTop;
	   int mid =  startBottom;
	   boolean found = false;
	   while (bot <= top) {
	      mid = bot + (top - bot) / 2;
		  Fiche entry = getFicheForId(filteredFicheIdList.get(mid));		  
	      char midCharacter=getFirstCharForIndex(entry);
	      if      (key < midCharacter) top = mid - 1;
	      else if (key > midCharacter) bot = mid + 1;
	      else {
	    	  found = true;
	    	  break;
	      };
	   }
	   if(found){
		  // search for the first occurence
		  int best= mid;
		  for (int i = mid; i > startBottom; i--) {
		  	  Fiche entry = getFicheForId(filteredFicheIdList.get(i));		  
		      char midCharacter=getFirstCharForIndex(entry);
			  if(midCharacter == key){
				  best = i;
			  }
			  else {
				  //previous is differents so we stop here
				  break;
			  }
			
		  }
		  return best;
	   }
	   else return -1;
	} 
		
	
	//Start of user code protected additional ListeFicheAvecFiltre_Adapter methods
	// additional methods
	public void refreshFilter(){
		
		int oldFilteredZoneGeoId = filteredZoneGeoId;
		filteredZoneGeoId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
		int oldFilteredGroupeId = filteredGroupeId;
		filteredGroupeId = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), 1);
		if((oldFilteredZoneGeoId != filteredZoneGeoId) | (oldFilteredGroupeId != filteredGroupeId)){
			//need full query
			updateList();
			if (filteredFicheIdList.size() > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
	
	//End of user code
	protected boolean sortAfterFilter() {
		return false;
	}
	
	public int filter(int position, Fiche entry, String pattern){
		// Start of user code protected additional ListeFicheAvecFiltre_Adapter filter code
		// chercher séparement les mots (séparés par un blanc) et faire un "ET" 
		String[] patterns = pattern.split(" ");
		boolean isValid = true;
		for (String patt : patterns) {
			if(patt.isEmpty()) continue; // en cas de blanc multiples
			if(patt.equals("*")) break;  // accepte tout; aussi utilisé pour le filtre en retour de sélection de filtre
			if(entry.getTextePourRechercheRapide().contains(patt))
				continue;
			else isValid = false;
		}		
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
					ArrayList<Integer> list = new ArrayList<Integer>(ficheIdList);
					results.values = list;
					results.count = list.size();
				}
			} else {
		// Start of user code protected ListeFicheAvecFiltre_Adapter filter prefix customisation
				Common_Outils commonOutils = new Common_Outils();
				final String prefixString = commonOutils.formatStringNormalizer(prefix.toString().toLowerCase(Locale.FRENCH));
				//
		// End of user code
				boolean sort = sortAfterFilter();
				final List<Integer> values = ficheIdList;
				final int count = values.size();
		
				final ArrayList<Integer> newValues = new ArrayList<Integer>(count);
				final int[] orders = sort ? new int[count] : null;

				for (int i = 0; i < count; i++) {
					final Integer valueId =  values.get(i);
					Fiche value = getFicheForId(valueId);
					if(value != null){
						int order = ListeImageFicheAvecFiltre_Adapter.this.filter(i, value, prefixString);
						if (order >= 0) {
							if (sort)
								orders[newValues.size()] = order;
							newValues.add(valueId);
						}
					}
				}
				/* TODO implement a sort
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
				*/
				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count > 0) {
				filteredFicheIdList = (List<Integer>) results.values;
				notifyDataSetChanged();
			} else {
				filteredFicheIdList = new ArrayList<Integer>();
				notifyDataSetInvalidated();
			}
			// update hashmap for index
			((ActivityWithIndexBar)context).populateIndexBarHashMap();
		}
	}
	
    View insertPhoto(PhotoFiche photoFiche){
    	LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LayoutParams(200, 200));
        layout.setGravity(Gravity.CENTER);
        
        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        if(photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.VIGNETTE)){
    		try {
				Picasso.with(context).load(photosOutils.getPhotoFile(photoFiche.getCleURLNomFichier(), ImageType.VIGNETTE))
					.fit()
					.centerInside()
					.into(imageView);
			} catch (IOException e) {
			}
    	}
    	else{
    		// pas préchargée en local pour l'instant, cherche sur internet si c'est autorisé
    		
    		if (reseauOutils.isTelechargementsModeConnectePossible()) {

	            final PhotoFiche photoFicheFinal = photoFiche;

	    		Picasso.with(context)
	    			.load(Constants.IMAGE_BASE_URL + "/"
        					+ photoFiche.getCleURL().replaceAll(
							Constants.IMAGE_BASE_URL_SUFFIXE, Constants.PETITE_BASE_URL_SUFFIXE))
					.placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
					.error(R.drawable.doris_icone_doris_large_pas_connecte)
					.fit()
					.centerInside()
	    			.into(imageView,
							new com.squareup.picasso.Callback() {
				        @Override
				        public void onSuccess() {
				            //Success image already loaded into the view
				        }

			        @Override
			        public void onError() {
		        		Picasso.with(context)
	        			.load(Constants.IMAGE_BASE_URL + "/"
	        					+ photoFicheFinal.getCleURL().replaceAll(
	        							Constants.IMAGE_BASE_URL_SUFFIXE, Constants.PETITE_BASE_URL_SUFFIXE))
						.placeholder(R.drawable.app_ic_launcher)  // utilisation de l'image par defaut pour commencer
						.fit()
						.centerInside()
						.error(R.drawable.doris_icone_doris_large_pas_connecte)
	        			.into(imageView);
			        }

		        });
    		} else {
    			imageView.setImageResource(R.drawable.doris_icone_doris_large_pas_connecte);
    		}
    	}
        
        layout.addView(imageView);
        return layout;
   
    }
    
    
}
