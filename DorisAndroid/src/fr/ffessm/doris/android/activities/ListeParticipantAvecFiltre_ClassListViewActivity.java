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


import java.util.HashMap;

import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.activities.view.indexbar.IndexBarHandler;
import fr.ffessm.doris.android.datamodel.*;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
// Start of user code protectedListeParticipantAvecFiltre_ClassListViewActivity_additionalimports

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
// End of user code

public class ListeParticipantAvecFiltre_ClassListViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> implements OnItemClickListener , ActivityWithIndexBar{
	
	private static final String LOG_TAG = ListeParticipantAvecFiltre_ClassListViewActivity.class.getSimpleName();

	//Start of user code constants ListeParticipantAvecFiltre_ClassListViewActivity
	String iconSizeString = "48";
	//End of user code
	
    ListeParticipantAvecFiltre_Adapter adapter;

	Handler mHandler;
    HashMap<Character, Integer> alphabetToIndex;
	int number_of_alphabets=-1;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		ThemeUtil.onActivityCreateSetTheme(this);
		setContentView(R.layout.listeparticipantavecfiltre_listview);

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

		ListView list = (ListView) findViewById(R.id.listeparticipantavecfiltre_listview);
        list.setClickable(true);
		//Start of user code onCreate ListeParticipantAvecFiltre_ClassListViewActivity adapter creation
        adapter = new ListeParticipantAvecFiltre_Adapter(this, getHelper().getDorisDBHelper());	
        
		//End of user code
		// avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);

		// Get the intent, verify the action and get the query
        handleIntent(getIntent());

		// add handler for indexBar
        mHandler = new IndexBarHandler(this);
		//Start of user code onCreate additions ListeParticipantAvecFiltre_ClassListViewActivity
	    actionBar.setSubtitle("Participants");
		//End of user code
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Start of user code onResume additions ListeParticipantAvecFiltre_ClassListViewActivity
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	if(!prefs.getString(getString(R.string.pref_key_list_icon_size), "48").equals(iconSizeString)){
    		iconSizeString = prefs.getString(getString(R.string.pref_key_list_icon_size), "48");
    		ListView list = (ListView) findViewById(R.id.listeparticipantavecfiltre_listview);
    		list.invalidateViews();
    	}
    	
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
            ListeParticipantAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(query);
        }
    }	

	@Override
	public boolean onSearchRequested() {
		Log.d(LOG_TAG,"onSearchRequested received");
	    return super.onSearchRequested();
	}

	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
		Log.d(LOG_TAG, "onItemClick "+view);
		if(view instanceof LinearLayout && view.getId() == R.id.listeparticipantavecfiltre_listviewrow){
			// normal case on main item
	        Intent toDetailView = new Intent(this, DetailsParticipant_ElementViewActivity.class);
	        Bundle b = new Bundle();
	        b.putInt("participantId", ((Participant)view.getTag()).getId());
			toDetailView.putExtras(b);
	        startActivity(toDetailView);
		}
		else if(view instanceof TextView && view.getId() == R.id.indexbar_alphabet_row_textview){
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
				ListView listview=(ListView)findViewById(R.id.listeparticipantavecfiltre_listview);
				listview.setSelection(newPosition);
			}
		}
    }

	//Start of user code additional  ListeParticipantAvecFiltre_ClassListViewActivity methods
	@Override
	protected void onDestroy(){
		Log.d(LOG_TAG, "onDestroy()");
	
		//On vide le cache des infos de la Bibliographie
		getHelper().getParticipantDao().clearObjectCache();
		
		
		super.onDestroy();
	}
	//End of user code

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.listeparticipantavecfiltre_classlistview_actions, menu);
		// Associate searchable configuration with the SearchView
		// deal with compat
		MenuItem  menuItem = (MenuItem ) menu.findItem(R.id.listeparticipantavecfiltre_classlistview_action_search);
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
					ListeParticipantAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(arg0);
				}
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO must be careful if the request might be long
				// action on text change
				ListeParticipantAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(arg0);
				return false;
			}
		});
	    
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu ListeParticipantAvecFiltre_ClassListViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// behavior of option menu
        switch (item.getItemId()) {
			case R.id.listeparticipantavecfiltre_classlistview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action ListeParticipantAvecFiltre_ClassListViewActivity

		//End of user code
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//TODO : Intent upIntent = DorisApplicationContext.getInstance().retourNiveau3Intent;
				Intent upIntent = null;
				Log.d(LOG_TAG, "onOptionsItemSelected() - upIntent : "+upIntent.getComponent().toString());
				
		        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
		            // This activity is NOT part of this app's task, so create a new task
		            // when navigating up, with a synthesized back stack.
		            TaskStackBuilder.create(this)
		                    // Add all of this activity's parents to the back stack
		                    .addNextIntentWithParentStack(upIntent)
		                    // Navigate up to the closest parent
		                    .startActivities();
		        } else {
		            // This activity is part of this app's task, so simply
		            // navigate up to the logical parent activity.
		            NavUtils.navigateUpTo(this, upIntent);
		        }
	            return true;
			default:
                return super.onOptionsItemSelected(item);
        }
    }

	//  ------------ dealing with Up button
	@Override
	public Intent getSupportParentActivityIntent() {
		//Start of user code getSupportParentActivityIntent ListeParticipantAvecFiltre_ClassListViewActivity
		// navigates to the parent activity
		return new Intent(this, Accueil_CustomViewActivity.class);
		//End of user code
	}
	@Override
	public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
		//Start of user code onCreateSupportNavigateUpTaskStack ListeParticipantAvecFiltre_ClassListViewActivity
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
		String alphabets[]=getResources().getStringArray(R.array.alphabet_array);
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
						alphabetToIndex.put(alpha, adapter.filteredParticipantList.size()-1);
					else
						continue;
					
			}//
		}//
	}
	
	@Override
	public ListView getAlphabetListView() {
		return (ListView)findViewById(R.id.listeparticipantavecfiltre_listView_alphabets);
	}
	public View getAlphabetRowView(){
		return findViewById(R.id.alphabet_row_layout);
	}

	// Start of user code protectedListeParticipantAvecFiltre_ClassListViewActivity
	public void onClickFilterBtn(View view){
		showToast("filter button pressed. \nPlease customize ;-)");
    }
	// End of user code

	
}
