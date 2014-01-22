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
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
// End of user code

public class ListeFicheAvecFiltre_ClassListViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> implements OnItemClickListener , ActivityWithIndexBar{
	
	private static final String LOG_TAG = ListeFicheAvecFiltre_ClassListViewActivity.class.getSimpleName();

	//Start of user code constants ListeFicheAvecFiltre_ClassListViewActivity
	
    MenuItem searchButtonMenuItem;
    
    
    final Context context = this;
	//End of user code
	// Search EditText
    //EditText inputSearch;
    ListeFicheAvecFiltre_Adapter adapter;

	Handler mHandler;
    HashMap<Character, Integer> alphabetToIndex;
	int number_of_alphabets=-1;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.listeficheavecfiltre_listview);

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

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

		// Get the intent, verify the action and get the query
        handleIntent(getIntent());

		// add handler for indexBar
        mHandler = new IndexBarHandler(this);
		//Start of user code onCreate additions ListeFicheAvecFiltre_ClassListViewActivity
        
		//End of user code
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Start of user code onResume additions ListeFicheAvecFiltre_ClassListViewActivity
		Log.d(LOG_TAG, "ListeFicheAvecFiltre_ClassListViewActivity - onResume");
		// refresh on resume, the preferences and filter may have changed 
		// TODO peut être qu'il y a moyen de s'abonner aux changements de préférence et de ne le faire que dans ce cas ?
		ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.refreshFilter(); 
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	updateFilterInActionBar();

		//End of user code
		populateIndexBarHashMap();
	}

	@Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }
	
	private void handleIntent(Intent intent) {
		//Log.d(LOG_TAG,"Intent received");
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
           // handles a click on a search suggestion; launches activity to show word
           //  Intent wordIntent = new Intent(this, WordActivity.class);
           // wordIntent.setData(intent.getData());
           // startActivity(wordIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
    		Log.d(LOG_TAG,"ACTION_SEARCH Intent received for "+query);
            ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(query);
        }
    }	

	@Override
	public boolean onSearchRequested() {
		Log.d(LOG_TAG,"onSearchRequested received");
	    return super.onSearchRequested();
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
			if(	newPosition != null){	
				Toast.makeText(this, selected_alpahbet, Toast.LENGTH_SHORT).show();
				ListView listview=(ListView)findViewById(R.id.listeficheavecfiltre_listview);
				listview.setSelection(newPosition);
			}
		}
    }

	//Start of user code additional  ListeFicheAvecFiltre_ClassListViewActivity methods
	
	//End of user code

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.listeficheavecfiltre_classlistview_actions, menu);
		// Associate searchable configuration with the SearchView
		// deal with compat
		MenuItem  menuItem = (MenuItem ) menu.findItem(R.id.listeficheavecfiltre_classlistview_action_search);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false);
    	searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					// already done by normal
				}
				else{
					ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(arg0);
				}
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO must be careful if the request might be long
				// action on text change
				ListeFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(arg0);
				return false;
			}
		});
	    
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu ListeFicheAvecFiltre_ClassListViewActivity
    	searchButtonMenuItem = (MenuItem ) menu.findItem(R.id.listeficheavecfiltre_classlistview_action_filterpopup);
    	updateFilterInActionBar();
    	//searchPopupButtonManager = new SearchPopupButtonManager(this);
		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// behavior of option menu
        switch (item.getItemId()) {
			case R.id.listeficheavecfiltre_classlistview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action ListeFicheAvecFiltre_ClassListViewActivity
			case R.id.listeficheavecfiltre_classlistview_action_filterpopup:
				//showToast("searchPopupButtonManager.onClickFilterBtn(MenuItemCompat.getActionView(item))");
			//	searchPopupButtonManager.onClickFilterBtn(MenuItemCompat.getActionView(item));
				View menuItemView = findViewById(R.id.listeficheavecfiltre_classlistview_action_filterpopup); // SAME ID AS MENU ID
				// crée le manager de popup
		        //searchPopupButtonManager = new SearchPopupButtonManager(this);
				//showFilterPopupMenu(menuItemView);
				//searchPopupButtonManager.onClickFilterBtn(menuItemView);
				showPopup();
	            return true;
			//End of user code
			default:
                return super.onOptionsItemSelected(item);
        }
    }

	//  ------------ dealing with Up button
	@Override
	public Intent getSupportParentActivityIntent() {
		//Start of user code getSupportParentActivityIntent ListeFicheAvecFiltre_ClassListViewActivity
		// navigates to the parent activity
		return new Intent(this, Accueil_CustomViewActivity.class);
		//End of user code
	}
	@Override
	public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
		//Start of user code onCreateSupportNavigateUpTaskStack ListeFicheAvecFiltre_ClassListViewActivity
		super.onCreateSupportNavigateUpTaskStack(builder);
		//End of user code
	}
	// -------------- handler (for indexBar)
	@Override
	public Handler getHandler() {
		return mHandler;
	}
	
	public void populateIndexBarHashMap() {
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
			for(int i=index+1  ; i< 27 ;i++){		//start from next character to last character
				char searchAlphabet=alphabets[i].charAt(0);   
				
				/*If we find the position of F character, then on click event on E should take the user to F*/	
				if(  alphabetToIndex.containsKey(searchAlphabet)){
					alphabetToIndex.put(alpha, alphabetToIndex.get(searchAlphabet));
					break;
				}
				else
					if(i==26) /*If there are no entries after E, then on click event on E should take the user to end of the list*/
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
	
	public void updateFilterInActionBar(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if((prefs.getInt(getString(R.string.pref_key_filtre_groupe), 1) != 1) ||
			   (prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1) != -1)){       
			// mise à jour de l'image du bouton de filtre
			if(searchButtonMenuItem!=null)
	    	   searchButtonMenuItem.setIcon(R.drawable.app_filter_settings_actif_32);
		}
		else{
			// pas de filtre actif
			// remet l'imaged efiltre inactif
	        //searchButton.setImageResource(R.drawable.filter_settings_32);
			if(searchButtonMenuItem!=null)
				searchButtonMenuItem.setIcon(R.drawable.app_filter_settings_32);
	        
		}
	}
	
	public  void showPopup() {

		View menuItemView = findViewById(R.id.listeficheavecfiltre_classlistview_action_filterpopup);
		// peut être null si pas visible, ex dans actionbar overfloww si pas assez de place dans l'action bar 
		LinearLayout viewGroup = (LinearLayout) findViewById(R.id.listeavecfiltre_filtrespopup);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = layoutInflater.inflate(R.layout.listeficheavecfiltre_filtrespopup, viewGroup);
		
		int popupWidth =  getResources().getDimensionPixelSize(R.dimen.listeficheavecfiltre_popup_width); 
		int popupHeight = getResources().getDimensionPixelSize(R.dimen.listeficheavecfiltre_popup_height); 
		//Log.d(LOG_TAG,"showPopup() - width="+popupWidth+", height="+popupHeight);
		
		final PopupWindow popup = new PopupWindow(layout);
		popup.setWidth(popupWidth);
		popup.setHeight(popupHeight);

		//popup.setOutsideTouchable(true);
		popup.setFocusable(true);

		popup.setBackgroundDrawable(new BitmapDrawable());
		int[]  location = new int[2];
		if(menuItemView != null){
			menuItemView.getLocationOnScreen(location);
			Log.d(LOG_TAG, "menuitem pos ="+location[0]+" "+location[1]);

			popup.showAsDropDown(menuItemView,0,0);
		}
		else{
			Log.d(LOG_TAG, "menuitem pos not available, anchor to top of the listview");
			//popup.showAsDropDown(findViewById(R.id.listeficheavecfiltre_listview),0,0);
			View containerView = findViewById(R.id.listeficheavecfiltre_listview);
			containerView.getLocationOnScreen(location);
			Log.d(LOG_TAG, "menuitem pos ="+location[0]+" "+location[1]+ " ");
			popup.showAtLocation(layout,Gravity.TOP|Gravity.RIGHT,0,location[1]);
		}
		// bouton filtre espèce 
		Button btnFiltreEspece = (Button) layout.findViewById(R.id.listeavecfiltre_filtrespopup_GroupeButton);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int filtreCourantId = prefs.getInt(this.getString(R.string.pref_key_filtre_groupe), 1);	        
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
				popup.dismiss();
				
				//Permet de revenir à cette liste après choix du groupe, True on retournerait à l'accueil
				Intent toGroupeSelectionView = new Intent(ListeFicheAvecFiltre_ClassListViewActivity.this, GroupeSelection_ClassListViewActivity.class);
		        Bundle b = new Bundle();
		        b.putBoolean("GroupeSelection_depuisAccueil", false);
		        toGroupeSelectionView.putExtras(b);
		        startActivity(toGroupeSelectionView);
			  }
			});

		// bouton filtre zone géographique
		Button btnZoneGeo = (Button) layout.findViewById(R.id.listeavecfiltre_filtrespopup_ZoneGeoButton);
		int currentFilterId = prefs.getInt(ListeFicheAvecFiltre_ClassListViewActivity.this.getString(R.string.pref_key_filtre_zonegeo), -1);
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
			popup.dismiss();

			//Toast.makeText(getApplicationContext(), "Zone géographique", Toast.LENGTH_LONG).show();
			startActivity(new Intent(ListeFicheAvecFiltre_ClassListViewActivity.this, ZoneGeoSelection_ClassListViewActivity.class));
			}
			});
		   
	}
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
