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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//Start of user code protected additional Glossaire_Adapter imports
// additional imports
import android.view.ViewGroup.LayoutParams;
import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.*;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import java.io.IOException;
import java.util.Locale;
//End of user code

public class Glossaire_Adapter extends BaseAdapter   implements Filterable{
	
	private Context context;

	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	private static final String LOG_TAG = Glossaire_Adapter.class.getCanonicalName();

    private List<DefinitionGlossaire> definitionGlossaireList;
    public List<DefinitionGlossaire> filteredDefinitionGlossaireList;
	private final Object mLock = new Object();
	private SimpleFilter mFilter;
	SharedPreferences prefs;
	//Start of user code protected additional Glossaire_Adapter attributes
	// additional attributes
	
	private Param_Outils paramOutils;
	private Textes_Outils textesOutils;
	protected Reseau_Outils reseauOutils;
	//End of user code

	public Glossaire_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Start of user code protected Glossaire_Adapter constructor
		paramOutils = new Param_Outils(context);
		textesOutils = new Textes_Outils(context);
		reseauOutils = new Reseau_Outils(context);
		// End of user code
		updateList();
	}
	
	protected void updateList(){
		// Start of user code protected Glossaire_Adapter updateList
		// TODO find a way to query in a lazier way
		try{
            // En V3, la table était chargée dans l'ordre alphabétique, pas en V4
            // On revient donc au tri par la 1ère lettre même si quelques définitions ne seront pas bien triées É pas avec les E

			//this.definitionGlossaireList = _contextDB.definitionGlossaireDao.queryForAll();

			// ANNULÉE EN V4 : Ne sera pas trié correctement à cause des accents, il aurait fallu un champ tri
			this.definitionGlossaireList = _contextDB.definitionGlossaireDao.query(_contextDB.definitionGlossaireDao.queryBuilder().orderBy("terme", true).prepare());
			this.filteredDefinitionGlossaireList = this.definitionGlossaireList;
			

		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		// End of user code
	}

	@Override
	public int getCount() {
		if(filteredDefinitionGlossaireList.size() == 0){
			return 1;	// will create a dummy entry to invite changing the filters
        }
		return filteredDefinitionGlossaireList.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredDefinitionGlossaireList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		// Start of user code protected additional Glossaire_Adapter getView_assign code
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.glossaire_listviewrow, null);
        }
		if(filteredDefinitionGlossaireList.size() == 0){
        	return getNoResultSubstitute(convertView);
        }
		final DefinitionGlossaire entry = filteredDefinitionGlossaireList.get(position);
		if(_contextDB != null) entry.setContextDB(_contextDB); 		
       
		// set data in the row 
		TextView tvLabel = (TextView) convertView.findViewById(R.id.glossaire_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
		labelSB.append(entry.getTerme());
		labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

        TextView tvDetails = (TextView) convertView.findViewById(R.id.glossaire_listviewrow_details);
        tvDetails.setText(
        	textesOutils.raccourcir( entry.getDefinition().toString()
        								.replaceAll("^[^\\)]*\\)\\.", "").replaceAll("\\{\\{[^\\}]*\\}\\}", "").trim(), 
    								Integer.parseInt(context.getString(R.string.detailentreeglossaire_elementview_details_nbcarmax))
        		) );
        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.glossaire_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional Glossaire_Adapter getView code
		//	additional code
        
        // Affichage 1ère image de la définition qd disponible
        ImageView imageView = (ImageView) convertView.findViewById(R.id.glossaire_listviewrow_icon);
		int defaultIconSize = paramOutils.getParamInt(R.string.pref_key_fiche_icone_taille, Integer.parseInt(context.getString(R.string.fiche_icone_taille_defaut)) );
        imageView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        imageView.getLayoutParams().width = ScreenTools.dp2px(context, defaultIconSize);
        
        Log.i(LOG_TAG, "getView() - entry.getCleURLIllustration() : " +entry.getCleURLIllustration());
        
        if ( !entry.getCleURLIllustration().isEmpty() ) {
        	String[] listePhotos = entry.getCleURLIllustration().split(";");
			String[] url_et_description =listePhotos[0].split("\\|");
            String photoUrl = url_et_description[0];
            String photoDescription = url_et_description.length > 1 ? url_et_description[1] : "";
            Log.i(LOG_TAG, "getView() - photo : " + photoUrl + "-" + photoDescription);

        	String nom1erePhoto = photoUrl.split("/")[photoUrl.split("/").length - 1];
        	Log.i(LOG_TAG, "getView() - nomPhoto1erePhoto : " +nom1erePhoto);
        	
        	Photos_Outils photosOutils = new Photos_Outils(context);
	        if(photosOutils.isAvailableInFolderPhoto(nom1erePhoto, ImageType.ILLUSTRATION_DEFINITION)){
	    		try {
					Picasso.with(context).load(photosOutils.getPhotoFile(nom1erePhoto, ImageType.ILLUSTRATION_DEFINITION))
						.resize(defaultIconSize, defaultIconSize)
						.centerInside()
						.into(imageView);
				} catch (IOException e) {
				}
	    	} else {
	        	// utilise la version en ligne si possible
				if (reseauOutils.isTelechargementsModeConnectePossible()) {
					Log.i(LOG_TAG, "getView() - tentative téléchargement : " +Constants.IMAGE_BASE_URL +"/"+ photoUrl);
					Picasso.with(context).load(Constants.IMAGE_BASE_URL +"/"+ photoUrl)
							.placeholder(R.drawable.app_glossaire_indisponible)
							.resize(defaultIconSize, defaultIconSize)
							.centerInside()
							.into(imageView);
				}
			}
        }
        else{
        	// remet l'image par défaut (nécessaire à cause de recyclage des widgets)

        	imageView.setImageResource(R.drawable.app_ic_launcher);
        }
        

		// End of user code

        return convertView;

	}

	protected View getNoResultSubstitute(View convertView){
		TextView tvLabel = (TextView) convertView.findViewById(R.id.glossaire_listviewrow_label);
		tvLabel.setText(R.string.glossaire_classlistview_no_result);
		// Start of user code protected additional Glossaire_Adapter getNoResultSubstitute code
		StringBuilder sbRechercheCourante = new StringBuilder();
	        
        // TODO ajouter le filtre textuel courant qui lui aussi peut impliquer de ne retourner aucun résultats
        TextView tvDetails = (TextView) convertView.findViewById(R.id.glossaire_listviewrow_details);
		tvDetails.setText( sbRechercheCourante.toString() );
	
		// End of user code
		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.glossaire_listviewrow_icon);
    	ivIcon.setImageResource(R.drawable.app_ic_launcher);
		return convertView;
	}
	public HashMap<Character, Integer> getUsedAlphabetHashMap(){
		HashMap<Character, Integer> alphabetToIndex = new HashMap<Character, Integer>();
		Log.d(LOG_TAG,"getUsedAlphabetHashMap - début");
		int base_list_length=filteredDefinitionGlossaireList.size();
		if(base_list_length < 100 ){
			// the base has been filtered so return the element from the filtered one
			alphabetToIndex=new HashMap<Character, Integer>();
			
			
			for(int i=0; i < base_list_length; i++){
				DefinitionGlossaire entry = filteredDefinitionGlossaireList.get(i);
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
	
	protected char getFirstCharForIndex(DefinitionGlossaire entry){
		//Start of user code protected Glossaire_Adapter binarySearch custom
    	return entry.getTerme().charAt(0);
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
		  DefinitionGlossaire entry = filteredDefinitionGlossaireList.get(mid);
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
		      DefinitionGlossaire entry = filteredDefinitionGlossaireList.get(i);
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
		
	
	//Start of user code protected additional Glossaire_Adapter methods
	// additional methods
	//End of user code
	protected boolean sortAfterFilter() {
		return false;
	}
	
	public int filter(int position, DefinitionGlossaire entry, String pattern){
		// Start of user code protected additional Glossaire_Adapter filter code
		StringBuilder labelSB = new StringBuilder();
		labelSB.append(entry.getTerme());
		labelSB.append(" ");
		if(labelSB.toString().toLowerCase(Locale.FRENCH).contains(pattern)) return 1;
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
					ArrayList<DefinitionGlossaire> list = new ArrayList<DefinitionGlossaire>(definitionGlossaireList);
					results.values = list;
					results.count = list.size();
				}
			} else {
		// Start of user code protected Glossaire_Adapter filter prefix customisation
				String prefixString = prefix.toString().toLowerCase(Locale.FRENCH);
		// End of user code
				boolean sort = sortAfterFilter();
				final List<DefinitionGlossaire> values = definitionGlossaireList;
				final int count = values.size();
		
				final ArrayList<DefinitionGlossaire> newValues = new ArrayList<DefinitionGlossaire>(count);
				final int[] orders = sort ? new int[count] : null;

				for (int i = 0; i < count; i++) {
					final DefinitionGlossaire value = values.get(i);
					int order = Glossaire_Adapter.this.filter(i, value, prefixString);
					if (order >= 0) {
						if (sort)
							orders[newValues.size()] = order;
						newValues.add(value);
					}
				}
				/* TODO implement a sort
				if (sort) {
					Comparator<DefinitionGlossaire> c = new Comparator<DefinitionGlossaire>() {
						public int compare(DefinitionGlossaire object1, DefinitionGlossaire object2) {
							// Start of user code protected additional Glossaire_Adapter compare code
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
				filteredDefinitionGlossaireList = (List<DefinitionGlossaire>) results.values;
				notifyDataSetChanged();
			} else {
				filteredDefinitionGlossaireList = new ArrayList<DefinitionGlossaire>();
				notifyDataSetInvalidated();
			}
			// update hashmap for index
			((ActivityWithIndexBar)context).populateIndexBarHashMap();
		}
	}
}
