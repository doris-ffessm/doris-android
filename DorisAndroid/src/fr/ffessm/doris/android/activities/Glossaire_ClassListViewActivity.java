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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
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
// Start of user code protectedGlossaire_ClassListViewActivity_additionalimports
// End of user code

public class Glossaire_ClassListViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> implements OnItemClickListener , ActivityWithIndexBar{
	
	private static final String LOG_TAG = Glossaire_ClassListViewActivity.class.getSimpleName();

	//Start of user code constants Glossaire_ClassListViewActivity
	//End of user code
	// Search EditText
    EditText inputSearch;
    Glossaire_Adapter adapter;

	Handler mHandler;
    HashMap<Character, Integer> alphabetToIndex;
	int number_of_alphabets=-1;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.glossaire_listview);

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

		ListView list = (ListView) findViewById(R.id.glossaire_listview);
        list.setClickable(true);
		//Start of user code onCreate Glossaire_ClassListViewActivity adapter creation
        adapter = new Glossaire_Adapter(this, getHelper().getDorisDBHelper());		
		//End of user code
		// avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);
		inputSearch = (EditText) findViewById(R.id.inputSearch_glossaire_listviewsearchrow);

		/**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {
             
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Glossaire_ClassListViewActivity.this.adapter.getFilter().filter(cs);  
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
		//Start of user code onCreate additions Glossaire_ClassListViewActivity
		//End of user code
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Start of user code onResume additions Glossaire_ClassListViewActivity
		//End of user code
		populateIndexBarHashMap();
	}

	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
		Log.d(LOG_TAG, "onItemClick "+view);
		if(view instanceof LinearLayout && view.getId() == R.id.glossaire_listviewrow){
			// normal case on main item
	        Intent toDetailView = new Intent(this, DetailEntreeGlossaire_ElementViewActivity.class);
	        Bundle b = new Bundle();
	        b.putInt("definitionGlossaireId", ((DefinitionGlossaire)view.getTag()).getId());
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
				ListView listview=(ListView)findViewById(R.id.glossaire_listview);
				listview.setSelection(newPosition);
			}
		}
    }

	//Start of user code additional  Glossaire_ClassListViewActivity methods

	//End of user code

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.glossaire_classlistview_actions, menu);
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu Glossaire_ClassListViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// behavior of option menu
        switch (item.getItemId()) {
			case R.id.glossaire_classlistview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action Glossaire_ClassListViewActivity

		//End of user code
			default:
                return super.onOptionsItemSelected(item);
        }
    }

	//  ------------ dealing with Up button
	@Override
	public Intent getSupportParentActivityIntent() {
		//Start of user code getSupportParentActivityIntent Glossaire_ClassListViewActivity
		// navigates to the parent activity
		return new Intent(this, Accueil_CustomViewActivity.class);
		//End of user code
	}
	@Override
	public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
		//Start of user code onCreateSupportNavigateUpTaskStack Glossaire_ClassListViewActivity
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
						alphabetToIndex.put(alpha, adapter.filteredDefinitionGlossaireList.size()-1);
					else
						continue;
					
			}//
		}//
	}
	
	@Override
	public ListView getAlphabetListView() {
		return (ListView)findViewById(R.id.glossaire_listView_alphabets);
	}
	public View getAlphabetRowView(){
		return findViewById(R.id.alphabet_row_layout);
	}

	// Start of user code protectedGlossaire_ClassListViewActivity
	public void onClickFilterBtn(View view){
		showToast("filter button pressed. \nPlease customize ;-)");
    }
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
