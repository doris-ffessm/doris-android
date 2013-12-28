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

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import fr.ffessm.doris.android.R;

//Start of user code Preference preference activity additional imports
import java.io.File;
import java.util.Date;
import java.util.List;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Builder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.format.DateUtils;
import android.util.Log;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.tools.Outils;

//End of user code

public class Preference_PreferenceViewActivity  extends android.preference.PreferenceActivity {

	
	//Start of user code Preference preference activity additional attributes
	private static final String LOG_TAG = Outils.class.getCanonicalName();

	private SharedPreferences prefs;
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference); 
		//Start of user code Preference preference activity additional onCreate
        
        
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
	        String typeParam = bundle.getString("type_parametre");
	        String param = bundle.getString("parametre");
	        
	        if (typeParam != null) {
	        	if (typeParam.equals("mode_precharg_region")) {
	        		if (param != null) {
			        	PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("button_qualite_images_zones_key");
			        	int pos = findPreference("mode_precharg_region_"+param).getOrder();
			        	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - pos "+param+" : "+pos);
			        	preferenceScreen.onItemClick( null, null, pos, 0 ); 
	        		}
	        	}
	        	if (typeParam.equals("mode_precharg_toutesregions")) {
		        	PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("pref_racine");
			        /*int pos = findPreference("button_qualite_images_zones_key").getOrder();
			        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - pos : "+pos); */
		        	// TODO : Je pensais utiliser  Preference findPreferenceInHierarchy mais je n'y arrive pas
		        	// Alors 5 en dur :-/
			        preferenceScreen.onItemClick( null, null, 5, 0 ); 
	        	}
	        }
        }
        
        final Preference btnVideVig = (Preference)getPreferenceManager().findPreference("btn_reset_vig");
        if(btnVideVig != null) {
	        btnVideVig.setSummary(getVigSummary());
        
        	btnVideVig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 Outils.clearFolder(Outils.getImageFolderVignette(getApplicationContext()), 0);

                	 btnVideVig.setSummary(getVigSummary());
                	 return true;
                 }
             });     
         }
        
        final Preference btnVideMedRes = (Preference)getPreferenceManager().findPreference("btn_reset_med_res");      
        if(btnVideMedRes != null) {
	        btnVideMedRes.setSummary(getMedResSummary());

        	btnVideMedRes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 Outils.clearFolder(Outils.getImageFolderMedRes(getApplicationContext()), 0);

                	 btnVideMedRes.setSummary(getMedResSummary());
                	 return true;
                 }
             });     
         }
        
        final Preference btnVideHiRes = (Preference)getPreferenceManager().findPreference("btn_reset_hi_res");      
        if(btnVideHiRes != null) {
        	btnVideHiRes.setSummary(getHiResSummary());
        
        	btnVideHiRes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 Outils.clearFolder(Outils.getImageFolderHiRes(getApplicationContext()), 0);

                	 btnVideHiRes.setSummary(getHiResSummary());
                	 return true;
                 }
             });     
         }
        
        final Preference btnVideCache = (Preference)getPreferenceManager().findPreference("btn_reset_cache");      
        if(btnVideCache != null) {
        	
	        btnVideCache.setSummary(getCacheSummary());

        	btnVideCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 //TODO : Picasso.with(getApplicationContext()).evictAll()
                	 LruCache cache = new LruCache(getApplicationContext());
                	
                	 //cache.evictAll();
                	 cache.clear();
                	 //Picasso.with(getApplicationContext()).
                	 // Pas compris comment faire ici : https://github.com/square/picasso/pull/77
                	 
                	 btnVideCache.setSummary(getCacheSummary());
                 	 Toast.makeText(getApplicationContext(), "Ne marche pas pour l'instant :-(", Toast.LENGTH_LONG).show();
                 	
                	return true;
                 }
             });     
         }
        
        /* Ne peut pas fonctionner comme si dessous avec API10 */
        /*
        String uri = Outils.getZoneIcone(getApplicationContext(), 1); 
    	int imageResource = getApplicationContext().getResources().getIdentifier(uri, null, getApplicationContext().getPackageName());
    	Preference btnPrechargRegionFrance = (Preference)getPreferenceManager().findPreference("pref_key_mode_precharg_region_france");   
    	btnPrechargRegionFrance.setIcon(imageResource);
        */
        
		//End of user code
    }

    @Override
	protected void onResume() {
		super.onResume(); 
		//Start of user code Preference preference activity additional onResume
		//End of user code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//Start of user code preference specific menu definition
        // menu.add(Menu.NONE, 0, 0, "Back to main menu");
    	
   
		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

		//Start of user code preference specific menu action
    	String message = ""+item.getItemId()+" - "+item.getGroupId()+" - "+item.toString();
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onOptionsItemSelected() - menu : "+message);  
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    	
        /* switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, AndroidDiveManagerMainActivity.class));
                return true;
        } */
		//End of user code
        return false;
    }

	
	//Start of user code Preference preference activity additional operations
    

    
    private String getVigSummary() {
    	String txt = getApplicationContext().getString(R.string.mode_precharg_reset_vig_summary); 
    	txt = txt.replace("@nbPh", ""+Outils.getVignetteCount(getApplicationContext()) ) ;
    	txt = txt.replace("@size", ""+Outils.getHumanDiskUsage(Outils.getVignettesDiskUsage(getApplicationContext()) ) ) ;
    	return txt;
    }
    private String getMedResSummary() {
        String txt = getApplicationContext().getString(R.string.mode_precharg_reset_med_res_summary); 
        txt = txt.replace("@nbPh", ""+Outils.getMedResCount(getApplicationContext()) ) ;
        txt = txt.replace("@size", ""+Outils.getHumanDiskUsage(Outils.getMedResDiskUsage(getApplicationContext()) ) ) ;
    	return txt;
    }
    private String getHiResSummary() {
        String txt = getApplicationContext().getString(R.string.mode_precharg_reset_hi_res_summary); 
        txt = txt.replace("@nbPh", ""+Outils.getHiResCount(getApplicationContext()) ) ;
        txt = txt.replace("@size", ""+Outils.getHumanDiskUsage(Outils.getHiResDiskUsage(getApplicationContext()) ) ) ;
    	return txt;
    }
    private String getCacheSummary() {
    	int nbFichiersDansCache = 0;
    	for (File child:getApplicationContext().getCacheDir().listFiles()) {
     		if (child.getName().equals("picasso-cache") ) {
     			nbFichiersDansCache = child.listFiles().length;
  	     		break;
     		}
     	}
    	String txt = getApplicationContext().getString(R.string.mode_precharg_reset_cache_summary);
    	// La division par 2 est très sale mais c'est bien le plus rapide :-)
    	// En le dossier est bizarrement structué avec des dossiers renommés, mais en gros il y en a 2 par fichiers en cache
     	txt = txt.replace("@nbPh", ""+ Math.round(nbFichiersDansCache/2) );
     	txt = txt.replace("@size", ""+Outils.getHumanDiskUsage(Outils.getDiskUsage(getApplicationContext(), getApplicationContext().getCacheDir() ) ) ) ;
     	return txt;
    }
    
    
	//End of user code
}
