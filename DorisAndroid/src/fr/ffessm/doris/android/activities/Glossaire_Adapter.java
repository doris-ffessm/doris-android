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
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;


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

//Start of user code protected additional Glossaire_Adapter imports
// additional imports
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
    private List<DefinitionGlossaire> filteredDefinitionGlossaireList;
	private final Object mLock = new Object();
	private SimpleFilter mFilter;
	SharedPreferences prefs;
	//Start of user code protected additional Glossaire_Adapter attributes
	// additional attributes
	//End of user code

	public Glossaire_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateList();
	}
	
	protected void updateList(){
		// Start of user code protected Glossaire_Adapter updateList
		// TODO find a way to query in a lazier way
		try{
			this.definitionGlossaireList = _contextDB.definitionGlossaireDao.queryForAll();
			this.filteredDefinitionGlossaireList = this.definitionGlossaireList;
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		// End of user code
	}

	@Override
	public int getCount() {
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
		
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.glossaire_listviewrow, null);
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
		StringBuilder detailsSB = new StringBuilder();
		detailsSB.append(entry.getDefinition().toString());
		detailsSB.append(" ");
        tvDetails.setText(detailsSB.toString());
		
        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.glossaire_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional Glossaire_Adapter getView code
		//	additional code
        int longueurMax = 80;
        String texteRow = entry.getDefinition().toString().replaceAll("[^\\)]*\\)\\.", "").trim();
        if (texteRow.length() > longueurMax ) {
        	detailsSB = new StringBuilder(texteRow.substring(0, longueurMax)+" ...");
        } else {
        	detailsSB = new StringBuilder(texteRow);
        }
        tvDetails.setText(detailsSB.toString());
        
		// End of user code

        return convertView;

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
					ArrayList<DefinitionGlossaire> list = new ArrayList<DefinitionGlossaire>(definitionGlossaireList);
					results.values = list;
					results.count = list.size();
				}
			} else {
		// Start of user code protected Glossaire_Adapter filter prefix customisation
				String prefixString = prefix.toString().toLowerCase();
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
		}
	}
}
