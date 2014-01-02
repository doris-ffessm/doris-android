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


import java.util.HashMap;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.activities.view.indexbar.IndexBarHandler;
import fr.ffessm.doris.android.datamodel.*;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
// Start of user code protectedListeFicheAvecFiltre_ClassListViewActivity_additionalimports
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
// End of user code

public class ListeFicheAvecFiltre_ClassListViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper> implements OnItemClickListener , ActivityWithIndexBar{
	
	//Start of user code constants ListeFicheAvecFiltre_ClassListViewActivity
	SearchPopupButtonManager searchPopupButtonManager;
	private static final String LOG_TAG = ListeFicheAvecFiltre_ClassListViewActivity.class.getSimpleName();
    ImageButton searchButton;
	//End of user code
	// Search EditText
    EditText inputSearch;
    ListeFicheAvecFiltre_Adapter adapter;

	Handler mHandler;
    HashMap<Character, Integer> alphabetToIndex;
	int number_of_alphabets=-1;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.listeficheavecfiltre_listview);

		ListView list = (ListView) findViewById(R.id.listeficheavecfiltre_listview);
        list.setClickable(true);
		//Start of user code onCreate ListeFicheAvecFiltre_ClassListViewActivity adapter creation
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        adapter = new ListeFicheAvecFiltre_Adapter(this, getHelper().getDorisDBHelper(), prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1));
        //End of user code
		// avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);
		inputSearch = (EditText) findViewById(R.id.inputSearch_listeficheavecfiltre_listviewsearchrow);

		/**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {
             
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(cs);  
            }
             
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                 
            }
             
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub                         
            }
        });
		// add handler for indexBar
        mHandler = new IndexBarHandler(this);
		//Start of user code onCreate additions ListeFicheAvecFiltre_ClassListViewActivity
        searchButton = (ImageButton) findViewById(R.id.btnOtherFilter_listeficheavecfiltre_listviewsearchrow);
        // crée le manager de popup
        searchPopupButtonManager = new SearchPopupButtonManager(this);
		//End of user code
	}
	


	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
		Log.d(LOG_TAG, "onItemClick "+view);
		if(view instanceof LinearLayout && view.getId() == R.id.listeficheavecfiltre_listviewrow){
			// normal case on main item
	        Intent toDetailView = new Intent(this, DetailsFiche_ElementViewActivity.class);
	        Bundle b = new Bundle();
	        b.putInt("ficheId", ((Fiche)view.getTag()).getId());
			toDetailView.putExtras(b);
	        startActivity(toDetailView);
		}
		else if(view instanceof TextView && view.getId() == R.id.indexbar_alphabtes_row_textview){
			// click on indexBar
			TextView rowview=(TextView)view;
			
			CharSequence alpahbet=rowview.getText();
			
			if(alpahbet==null || alpahbet.equals(""))
				return;
			
			String selected_alpahbet=alpahbet.toString().trim();
			Integer newPosition=alphabetToIndex.get(selected_alpahbet.charAt(0));
			Log.d(LOG_TAG, "Selected Alphabet is:"+selected_alpahbet+"   position is:"+newPosition);
					
			ListView listview=(ListView)findViewById(R.id.listeficheavecfiltre_listview);
			listview.setSelection(newPosition);
		}
    }

	//Start of user code additional  ListeFicheAvecFiltre_ClassListViewActivity methods
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "ListeFicheAvecFiltre_ClassListViewActivity - onResume");
		// refresh on resume, the preferences and filter may have changed 
		// TODO peut être qu'il y a moyen de s'abonner aux changements de préférence et de ne le faire que dans ce cas ?
		ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.refreshFilter(); 
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if((prefs.getInt(getString(R.string.pref_key_filtre_groupe), 1) != 1) ||
		   (prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1) != -1)){
			// on a un filtre actif
	    	String searchedText = inputSearch.getText().toString();
	    	if(!searchedText.isEmpty()){
	    		ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(searchedText);
	    	}	        
	        // mise à jour de l'image du bouton de filtre
	        searchButton.setImageResource(R.drawable.filter_settings_actif_32);
		}
		else{
			// pas de filtre actif
			// remet l'imaged efiltre inactif
	        searchButton.setImageResource(R.drawable.filter_settings_32);
	        
		}
		populateIndexBarHashMap();
	}
	//End of user code

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu ListeFicheAvecFiltre_ClassListViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
			case 777:
		            startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
		            return true;
		
		//Start of user code additional menu action ListeFicheAvecFiltre_ClassListViewActivity

		//End of user code
        }
        return false;
    }

	@Override
	public Handler getHandler() {
		return mHandler;
	}
	
	private void populateIndexBarHashMap() {
		alphabetToIndex= adapter.getUsedAlphabetHashMap();
		number_of_alphabets=alphabetToIndex.size();		//Number of enteries in the map is equal to number of letters that would necessarily display on the right.
		
		/*Now I am making an entry of those alphabets which are not there in the Map*/
		String alphabets[]=getResources().getStringArray(R.array.alphabtes_array);
		int index=-1;
		
		for(String alpha1: alphabets){
			char alpha=alpha1.charAt(0);
			index++;
			
			if(alphabetToIndex.containsKey(alpha))
				continue;

			/*Start searching the next character position. Example, here alpha is E. Since there is no entry for E, we need to find the position of next Character, F.*/
			for(int i=index+1  ; i< 26 ;i++){		//start from next character to last character
				char searchAlphabet=alphabets[i].charAt(0);   
				
				/*If we find the position of F character, then on click event on E should take the user to F*/	
				if(  alphabetToIndex.containsKey(searchAlphabet)){
					alphabetToIndex.put(alpha, alphabetToIndex.get(searchAlphabet));
					break;
				}
				else
					if(i==25) /*If there are no entries after E, then on click event on E should take the user to end of the list*/
						alphabetToIndex.put(alpha, adapter.filteredFicheIdList.size()-1);
					else
						continue;
					
			}//
		}//
	}
	
	@Override
	public ListView getAlphabetListView() {
		return (ListView)findViewById(R.id.listeficheavecfiltre_listView_alphabets);
	}
	public View getAlphabetRowView(){
		return findViewById(R.id.alphabet_row_layout);
	}

	// Start of user code protectedListeFicheAvecFiltre_ClassListViewActivity
	public void onClickFilterBtn(View view){
		//showToast("filter button pressed. \nFeature under development ;-)");
		//startActivity(new Intent(this, GroupSelection_CustomViewActivity.class));
		searchPopupButtonManager.onClickFilterBtn(view);
    }
	
	class SearchPopupButtonManager {
		Activity context;
		int searchbuttonstatus=0;
		PopupWindow popup;
		
		public SearchPopupButtonManager(Activity context){
			this.context = context;
		}
		
		public void onClickFilterBtn(View view){
			if(view == searchButton){
				if(searchbuttonstatus==0){;
					showPopup();
				}
				else{
					hidePopup();
				}
			}
	    }
		
		public  void showPopup() {
			
			//WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		    //Display display1 = getWindowManager().getDefaultDisplay();
		    //int Twidth = display1.getWidth();
		    //int Theight = display1.getHeight();
			 
			 
			//int popupWidth = 200;
			//int popupHeight =300;
			LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.listeavecfiltre_filtrespopup);
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = layoutInflater.inflate(R.layout.listeficheavecfiltre_filtrespopup, viewGroup);
			
			popup = new PopupWindow(layout, 300, 300);
			//popup.setContentView(layout);
			
			//int popupWidth = layout.getWidth(); 
			//int popupHeight = layout.getHeight();
			//Log.d(LOG_TAG," width="+popupWidth+", height="+popupHeight);
			searchbuttonstatus=1;
			//popup.setWidth(popupWidth);
			//popup.setHeight(popupHeight);
			popup.setFocusable(false);
	
			//int OFFSET_X =(Twidth);
			//int OFFSET_Y =Theight-(Theight-100);
			//Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_LONG).show();
			popup.setBackgroundDrawable(new BitmapDrawable());
			//popup.showAsDropDown(layout,OFFSET_X,OFFSET_Y);
			popup.showAsDropDown(searchButton,0,0);
			
			
			// bouton filtre espèce 
			Button btnFiltreEspece = (Button) layout.findViewById(R.id.listeavecfiltre_filtrespopup_GroupeButton);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	        int filtreCourantId = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), 1);	        
			if(filtreCourantId==1){
				btnFiltreEspece.setText(getString(R.string.listeficheavecfiltre_popup_filtreEspece_sans));
	        }
			else{
				Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);
				btnFiltreEspece.setText(getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec)+" "+groupeFiltreCourant.getNomGroupe().trim());
			}
			btnFiltreEspece.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					popup.setFocusable(true);
					searchbuttonstatus=0;
					popup.dismiss();
					startActivity(new Intent(context, GroupeSelection_ClassListViewActivity.class));
				  }
				});
	
			// bouton filtre zone géographique
			Button btnZoneGeo = (Button) layout.findViewById(R.id.listeavecfiltre_filtrespopup_ZoneGeoButton);
			int currentFilterId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
	        if(currentFilterId == -1){
	        	btnZoneGeo.setText(getString(R.string.listeficheavecfiltre_popup_filtreGeographique_sans));
	        }
	        else{
	        	ZoneGeographique currentZoneFilter= getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
	        	btnZoneGeo.setText(getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec)+" "+currentZoneFilter.getNom().trim());
	        }
	        
	        btnZoneGeo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				popup.setFocusable(true);
				searchbuttonstatus=0;
				popup.dismiss();
	
				//Toast.makeText(getApplicationContext(), "Zone géographique", Toast.LENGTH_LONG).show();
				startActivity(new Intent(context, ZoneGeoSelection_ClassListViewActivity.class));
				}
				});
			   
		}
		
		public void hidePopup(){

			  searchbuttonstatus=0;
			  popup.dismiss();
		}
	}
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
