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
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;


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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code protected additional ListeParticipantAvecFiltre_Adapter imports
// additional imports
//End of user code

public class ListeParticipantAvecFiltre_Adapter extends BaseAdapter   implements Filterable{
	
	private Context context;

	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	private static final String LOG_TAG = ListeParticipantAvecFiltre_Adapter.class.getCanonicalName();

    private List<Participant> participantList;
    public List<Participant> filteredParticipantList;
	private final Object mLock = new Object();
	private SimpleFilter mFilter;
	SharedPreferences prefs;
	//Start of user code protected additional ListeParticipantAvecFiltre_Adapter attributes
	// additional attributes
	//End of user code

	public ListeParticipantAvecFiltre_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateList();
	}
	
	protected void updateList(){
		// Start of user code protected ListeParticipantAvecFiltre_Adapter updateList
		// TODO find a way to query in a lazier way
		try{
			this.participantList = _contextDB.participantDao.queryForAll();
			this.filteredParticipantList = this.participantList;
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		// End of user code
	}

	@Override
	public int getCount() {
		return filteredParticipantList.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredParticipantList.get(position);
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
            convertView = inflater.inflate(R.layout.listeparticipantavecfiltre_listviewrow, null);
        }

		final Participant entry = filteredParticipantList.get(position);
		if(_contextDB != null) entry.setContextDB(_contextDB); 		
       
		// set data in the row 
		TextView tvLabel = (TextView) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
		labelSB.append(entry.getNom());
		labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

		
        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional ListeParticipantAvecFiltre_Adapter getView code
		//	additional code
		// End of user code

        return convertView;

	}

	public HashMap<Character, Integer> getUsedAlphabetHashMap(){
		HashMap<Character, Integer> alphabetToIndex = new HashMap<Character, Integer>();
		Log.d(LOG_TAG,"getUsedAlphabetHashMap - début");
		int base_list_length=filteredParticipantList.size();
		if(base_list_length < 100 ){
			// the base has been filtered so return the element from the filtered one
			alphabetToIndex=new HashMap<Character, Integer>();
			
			
			for(int i=0; i < base_list_length; i++){
				Participant entry = filteredParticipantList.get(i);
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
			String alphabet_list[]= context.getResources().getStringArray(R.array.alphabtes_array);
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
	
	protected char getFirstCharForIndex(Participant entry){
		//Start of user code protected ListeParticipantAvecFiltre_Adapter binarySearch custom
    	return entry.getNom().trim().charAt(0); // il y a un blanc au début, devrait être nettoyé dans le prefecth
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
		  Participant entry = filteredParticipantList.get(mid);
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
		      Participant entry = filteredParticipantList.get(i);
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
		
	
	//Start of user code protected additional ListeParticipantAvecFiltre_Adapter methods
	// additional methods
	//End of user code
	protected boolean sortAfterFilter() {
		return false;
	}
	
	public int filter(int position, Participant entry, String pattern){
		// Start of user code protected additional ListeParticipantAvecFiltre_Adapter filter code
		StringBuilder labelSB = new StringBuilder();
		labelSB.append(entry.getNom());
		labelSB.append(" ");
		if(labelSB.toString().toLowerCase().contains(pattern)) return 1;
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
					ArrayList<Participant> list = new ArrayList<Participant>(participantList);
					results.values = list;
					results.count = list.size();
				}
			} else {
		// Start of user code protected ListeParticipantAvecFiltre_Adapter filter prefix customisation
				String prefixString = prefix.toString().toLowerCase();
		// End of user code
				boolean sort = sortAfterFilter();
				final List<Participant> values = participantList;
				final int count = values.size();
		
				final ArrayList<Participant> newValues = new ArrayList<Participant>(count);
				final int[] orders = sort ? new int[count] : null;

				for (int i = 0; i < count; i++) {
					final Participant value = values.get(i);
					int order = ListeParticipantAvecFiltre_Adapter.this.filter(i, value, prefixString);
					if (order >= 0) {
						if (sort)
							orders[newValues.size()] = order;
						newValues.add(value);
					}
				}
				/* TODO implement a sort
				if (sort) {
					Comparator<Participant> c = new Comparator<Participant>() {
						public int compare(Participant object1, Participant object2) {
							// Start of user code protected additional ListeParticipantAvecFiltre_Adapter compare code
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
				filteredParticipantList = (List<Participant>) results.values;
				notifyDataSetChanged();
			} else {
				filteredParticipantList = new ArrayList<Participant>();
				notifyDataSetInvalidated();
			}
			// update hashmap for index
			((ActivityWithIndexBar)context).populateIndexBarHashMap();
		}
	}
}