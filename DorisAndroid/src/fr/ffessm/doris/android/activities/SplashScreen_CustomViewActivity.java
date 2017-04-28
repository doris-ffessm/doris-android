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


import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import android.preference.PreferenceManager;
//Start of user code additional imports SplashScreen_CustomViewActivity
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.SQLiteDataBaseHelper;
import fr.ffessm.doris.android.tools.Photos_Outils;
//End of user code
public class SplashScreen_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements SplashScreen_CustomViewActivity
//End of user code
{
	
	//Start of user code constants SplashScreen_CustomViewActivity

	boolean isUpdate = false;
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
			PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        setContentView(R.layout.splashscreen_customview);
        //Start of user code onCreate SplashScreen_CustomViewActivity
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(!SQLiteDataBaseHelper.checkDataBase()){
			// la base n'existe pas
			// on va la créer
			((LinearLayout) findViewById(R.id.splashscreen_progressLayout)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.splashscreen_textView)).setText(R.string.splashscreen_initialisation_base);
		}
		else{
			// la base existe, doit'on la mettre à jour ? (nouvelle version de l'application ?)
			int derniereVersionExecutee = prefs.getInt(getString(R.string.pref_key_last_version_run), 0);
			try {
				// récupère les info sur la version actuelle
				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				
				// Pour les développeurs : permet de forcer la mise à jour de la base de données
				boolean maj_base_prochain_demarrage = prefs.getBoolean(getString(R.string.pref_key_debug_maj_base_prochain_demarrage), false);

				if(pInfo.versionCode != derniereVersionExecutee || maj_base_prochain_demarrage){
					// changement de version, on doit aussi mettre à jour la base
					((LinearLayout) findViewById(R.id.splashscreen_progressLayout)).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.splashscreen_textView)).setText(R.string.splashscreen_maj_base);
					isUpdate = true;
					
					SharedPreferences.Editor ed = prefs.edit();
					ed.putBoolean(getString(R.string.pref_key_debug_maj_base_prochain_demarrage), false);
					ed.commit();
				}
				
			} catch (NameNotFoundException e) {
				
			}
		}
		
		try {
			// récupère les info sur la version actuelle
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			// sauve ces info dans les sharedPReferences
			SharedPreferences.Editor ed = prefs.edit();
			ed.putInt(getString(R.string.pref_key_last_version_run), pInfo.versionCode);
			ed.commit();
			
		} catch (NameNotFoundException e) {
			
		}
		// Use SharedPreferences to store something like "LAST_VERSION_RUN" = pInfo.versionName

		// execute the database initialization in a thread
		new AsyncInitialize(this).execute();
		
		// ensure that defaut preferences are set according to screen size
		DorisApplicationContext.getInstance().ensureDefaultPreferencesInitialization(this);
		//End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
		//Start of user code onResume SplashScreen_CustomViewActivity
		//End of user code
	}
    //Start of user code additional code SplashScreen_CustomViewActivity

    private class AsyncInitialize extends AsyncTask<Void, Void, Void> {
    	SplashScreen_CustomViewActivity context;

		public AsyncInitialize(SplashScreen_CustomViewActivity splashScreen) {
			super();
			context = splashScreen;
		}

		@Override
		protected void onPreExecute() {
			// show your progress dialog

		}

		@Override
		protected Void doInBackground(Void... voids) {
			// assure le chargement de la base de manière asynchrone
			if(isUpdate){
				// efface la base précédente si elle existe
				SQLiteDataBaseHelper.removeOldDataBase();
			}
			// The following initialize the DB from file
			SQLiteDataBaseHelper myDbHelper = new SQLiteDataBaseHelper(this.context);
			// myDbHelper = new DataBaseHelper(this);

			try {
				myDbHelper.createDataBase();

			} catch (IOException ioe) {

				throw new Error("Unable to create database");

			}
			
			// Création d'un fichier “.nomedia” à la racine des dossiers photos afin qu'elles ne soient pas
			// indexées par le moteur d'Android. 
			// Elles n'apparaissent ainsi pas dans "Photos", on gagne bcp en temps d'indexation et bcp de place
			// en thumbnails
			Photos_Outils photosOutils = new Photos_Outils(this.context);
			
			File folderPreferedLocation = photosOutils.getFolderFromPreferedLocation("");
			if (folderPreferedLocation != null && !folderPreferedLocation.exists() && !folderPreferedLocation.mkdirs()) {
	            try {
					throw new IOException("Cannot create dir " + photosOutils.getFolderFromPreferedLocation("").getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
			
			File nomediaFile = new File(folderPreferedLocation, ".nomedia");
			if ( nomediaFile != null && !nomediaFile.exists() ) {
				try {
					FileOutputStream noMediaOutStream = new FileOutputStream ( nomediaFile );
		            noMediaOutStream.write ( 0 );
		            noMediaOutStream.close ( );
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
			
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			// dismiss your dialog
			// démarrer la vrai activité accueil
			Intent intent = new Intent(SplashScreen_CustomViewActivity.this,
					Accueil_CustomViewActivity.class);
			startActivity(intent);

			// close this activity
			finish();
		}

	}
    
	//End of user code

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen SplashScreen_CustomViewActivity
		//End of user code
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.splashscreen_customview_actions, menu);
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu SplashScreen_CustomViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
			case R.id.splashscreen_customview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action SplashScreen_CustomViewActivity

		//End of user code
			default:
                return super.onOptionsItemSelected(item);
        }
    }

}
