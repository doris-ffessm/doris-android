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
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import android.preference.PreferenceManager;
//Start of user code additional imports
import fr.ffessm.doris.android.async.TelechargeFiches_BgActivity;
import fr.ffessm.doris.android.async.VerifieNouvellesFiches_BgActivity;
//End of user code
public class Accueil_CustomViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>{
	
	//Start of user code constants
	static final int TELECHARGE_FICHE_MENU_ID = 1;	
	static final int TELECHARGE_PHOTO_FICHES_MENU_ID = 2;
	static final int VERIFIE_MAJ_FICHES_MENU_ID = 3;
	static final int VERIFIE_NOUVELLES_FICHES_MENU_ID = 4;
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
			PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        setContentView(R.layout.accueil_customview);
        //Start of user code onCreate
		//End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
		//Start of user code onResume
		//End of user code
	}
    //Start of user code additional code
	public void onClickBtnSample(View view){
		showToast("sample button pressed. \nPlease customize ;-)");
    }
	//End of user code

    /** refresh screen from data 
     */
    private void refreshScreenData() {
    	//Start of user code action when refreshing the screen
		//End of user code
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);
		
		//Start of user code additional onCreateOptionsMenu
		menu.add(Menu.NONE, TELECHARGE_FICHE_MENU_ID, 1, R.string.telecharge_fiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, VERIFIE_NOUVELLES_FICHES_MENU_ID, 2, R.string.verifie_nouvelles_fiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
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
		
		//Start of user code additional menu action
			case TELECHARGE_FICHE_MENU_ID:
				new TelechargeFiches_BgActivity(getApplicationContext(), this.getHelper()).execute("");
				break;
			case VERIFIE_NOUVELLES_FICHES_MENU_ID:
				new VerifieNouvellesFiches_BgActivity(getApplicationContext(), this.getHelper()).execute("");
				break;
		//End of user code
        }
        return false;
    }

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
