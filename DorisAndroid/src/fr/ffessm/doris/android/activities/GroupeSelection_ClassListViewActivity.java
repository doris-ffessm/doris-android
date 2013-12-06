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


import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.*;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
// Start of user code protectedGroupeSelection_ClassListViewActivity_additionalimports
import android.widget.RelativeLayout;
import android.widget.TextView;
// End of user code

public class GroupeSelection_ClassListViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper> implements OnItemClickListener{
	
	//Start of user code constants GroupeSelection_ClassListViewActivity
	//End of user code
    GroupeSelection_Adapter adapter;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.groupeselection_listview);

		ListView list = (ListView) findViewById(R.id.groupeselection_listview);
        list.setClickable(false);
        adapter = new GroupeSelection_Adapter(this, getHelper().getDorisDBHelper());

		// avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);
		//Start of user code onCreate additions GroupeSelection_ClassListViewActivity
        // affiche ou cahce le filtre espèce actuel (+ son bouton de suppression)
        RelativeLayout currentFilterInfoLayout = (RelativeLayout)findViewById(R.id.groupselection_listview_filtre_espece_courant_layout);
    	
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int filtreCourantId = prefs.getInt(this.getString(R.string.pref_key_filtre_groupe), 1);
        Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);
        if(filtreCourantId==1){
        	currentFilterInfoLayout.setVisibility(View.GONE);
        }
        else{
        	TextView filtreCourantTV = (TextView)findViewById(R.id.groupselection_listview_filtre_espece_courant_textView);
        	currentFilterInfoLayout.setVisibility(View.VISIBLE);
        	filtreCourantTV.setText(getString(R.string.groupselection_customview_filtre_espece_courant_label)+groupeFiltreCourant.getNomGroupe());
        }
		//End of user code
	}
	


	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
		//Start of user code onItemClick additions GroupeSelection_ClassListViewActivity
		//showToast(view.toString() + ", "+ view.getId());
		
		GroupeSelection_Adapter groupeSelection_adapter = (GroupeSelection_Adapter)arg0.getAdapter();
		
		
		
		Groupe clickedGroupe = groupeSelection_adapter.getGroupeFromPosition(position);
		if(clickedGroupe.getContextDB() == null){
			Log.w(this.getClass().getSimpleName(),"workaround clickedGroupe.getContextDB() == null "+clickedGroupe.getId());
			clickedGroupe.setContextDB(getHelper().getDorisDBHelper());
		}
		if(clickedGroupe.getGroupesFils().size() > 0){	
			for(Groupe g : clickedGroupe.getGroupesFils()){
				if(g.getContextDB() == null){
					Log.w(this.getClass().getSimpleName(),"workaround Groupe.fils.getContextDB() == null "+g.getId());
					g.setContextDB(getHelper().getDorisDBHelper());
				}
			}
			groupeSelection_adapter.currentRootGroupe = clickedGroupe;
			groupeSelection_adapter.updateList();
			groupeSelection_adapter.notifyDataSetChanged();
		}
	
		//End of user code		
    }

	//Start of user code additional  GroupeSelection_ClassListViewActivity methods

	public void onRemoveCurrentFilterClick(View view){
    	Toast.makeText(this, "Filtre espèces supprimé", Toast.LENGTH_SHORT).show();
		SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
		ed.putInt(this.getString(R.string.pref_key_filtre_groupe), 1);
        ed.commit();
		finish();
    }
	//End of user code

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu GroupeSelection_ClassListViewActivity

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
		
		//Start of user code additional menu action GroupeSelection_ClassListViewActivity

		//End of user code
        }
        return false;
    }


	// Start of user code protectedGroupeSelection_ClassListViewActivity
	public void onClickFilterBtn(View view){
		showToast("filter button pressed. \nPlease customize ;-)");
    }
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
