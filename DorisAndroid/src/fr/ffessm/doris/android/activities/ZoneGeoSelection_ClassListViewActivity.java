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


import fr.ffessm.doris.android.datamodel.*;
import fr.ffessm.doris.android.R;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
// Start of user code protectedZoneGeoSelection_ClassListViewActivity_additionalimports
import android.widget.TextView;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.tools.ScreenTools;
// End of user code

public class ZoneGeoSelection_ClassListViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> implements OnItemClickListener {
	
	private static final String LOG_TAG = ZoneGeoSelection_ClassListViewActivity.class.getSimpleName();

	//Start of user code constants ZoneGeoSelection_ClassListViewActivity
	//End of user code
    ZoneGeoSelection_Adapter adapter;


	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.zonegeoselection_listview);

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

		ListView list = (ListView) findViewById(R.id.zonegeoselection_listview);
        list.setClickable(false);
		//Start of user code onCreate ZoneGeoSelection_ClassListViewActivity adapter creation
        adapter = new ZoneGeoSelection_Adapter(this, getHelper().getDorisDBHelper());		
		//End of user code
		// avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);

		//Start of user code onCreate additions ZoneGeoSelection_ClassListViewActivity
        if(ScreenTools.getScreenWidth(this) > 700)
        	actionBar.setTitle(getString(R.string.zonegeoselection_listview_title_large));
        else
        	actionBar.setTitle(getString(R.string.zonegeoselection_listview_title));
		//End of user code
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Start of user code onResume additions ZoneGeoSelection_ClassListViewActivity
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		TextView currentFilterText = (TextView) findViewById(R.id.zonegeoselection_listview_filtre_courant);
		int currentFilterId = pref.getInt(this.getString(R.string.pref_key_filtre_zonegeo), 0);
		
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onResume() - currentFilterId : "+currentFilterId);

        if(currentFilterId == -1){
        	currentFilterText.setText("");
        	findViewById(R.id.zonegeoselection_listview_filtre_courant__suppFiltreBtn).setVisibility(View.GONE);
        }
        else{
        	ZoneGeographique currentZoneFilter = getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
        	currentFilterText.setText(currentZoneFilter.getNom());
        	findViewById(R.id.zonegeoselection_listview_filtre_courant__suppFiltreBtn).setVisibility(View.VISIBLE);
        }
		//End of user code
	}


	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
			//Start of user code onItemClick additions ZoneGeoSelection_ClassListViewActivity
		//showToast(view.toString() + ", "+ view.getId());
		//End of user code		
    }

	//Start of user code additional  ZoneGeoSelection_ClassListViewActivity methods
	
	public void onRemoveCurrentFilterClick(View view){
    	Toast.makeText(this, R.string.zonegeoselection_filtre_supprime, Toast.LENGTH_SHORT).show();
		SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
		ed.putInt(this.getString(R.string.pref_key_filtre_zonegeo), -1);
        ed.commit();
		finish();
    }
	//End of user code

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.zonegeoselection_classlistview_actions, menu);
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu ZoneGeoSelection_ClassListViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// behavior of option menu
        switch (item.getItemId()) {
			case R.id.zonegeoselection_classlistview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action ZoneGeoSelection_ClassListViewActivity

		//End of user code
			default:
                return super.onOptionsItemSelected(item);
        }
    }

	//  ------------ dealing with Up button
	@Override
	public Intent getSupportParentActivityIntent() {
		//Start of user code getSupportParentActivityIntent ZoneGeoSelection_ClassListViewActivity
		// navigates to the parent activity
		return new Intent(this, Accueil_CustomViewActivity.class);
		//End of user code
	}
	@Override
	public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
		//Start of user code onCreateSupportNavigateUpTaskStack ZoneGeoSelection_ClassListViewActivity
		super.onCreateSupportNavigateUpTaskStack(builder);
		//End of user code
	}

	// Start of user code protectedZoneGeoSelection_ClassListViewActivity
	public void onClickFilterBtn(View view){
		showToast("filter button pressed. \nPlease customize ;-)");
    }
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
