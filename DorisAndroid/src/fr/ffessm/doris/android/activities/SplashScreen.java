package fr.ffessm.doris.android.activities;

import java.io.IOException;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.SQLiteDataBaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashScreen extends Activity {
	
	boolean isUpdate = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the content view for your splash screen you defined in an xml
		// file
		setContentView(R.layout.splashscreen_view);

		// perform other stuff you need to do
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
				String versionCodeActuel = Integer.toString(pInfo.versionCode);
				if(pInfo.versionCode != derniereVersionExecutee){
					// changement de version, on doit aussi mettre à jour la base
					((LinearLayout) findViewById(R.id.splashscreen_progressLayout)).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.splashscreen_textView)).setText(R.string.splashscreen_maj_base);
					isUpdate = true;
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

		// execute your xml news feed loader
		new AsyncInitialize(this).execute();

	}

	private class AsyncInitialize extends AsyncTask<Void, Void, Void> {
		SplashScreen context;

		public AsyncInitialize(SplashScreen splashScreen) {
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
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			// dismiss your dialog
			// démarrer la vrai activité accueil
			Intent intent = new Intent(SplashScreen.this,
					Accueil_CustomViewActivity.class);
			startActivity(intent);

			// close this activity
			finish();
		}

	}
}
