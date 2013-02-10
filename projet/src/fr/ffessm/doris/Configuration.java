/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
Copyright du Code : Guillaume Moynard  ([29/05/2011]) 

Guillaume Moynard : gmo7942@gmail.com

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
* ********************************************************************
* ********************************************************************* */

package fr.ffessm.doris;

/* *********************************************************************
 * Déclaration des imports
   *********************************************************************/
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;



public class Configuration extends Activity 
{
    private final static String TAG = "Configuration";
    private final static Boolean LOG = false;
	
    private final static String PREFS_NAME = "DorisParam";
    
    Spinner SPTypeLibelle;
    Spinner SPFiltreTypeFiche;
    Spinner SPFiltreZoneGeo;
	Spinner SPCache;
	Button BTNConfQuitter;
	Button	BTNVideCache;
	TextView TV_Detail_Cache2;
	
	SharedPreferences preferences;
	
	private Context appContext;
	
    @Override
	protected void onCreate(Bundle inSavedInstanceState)
    {
        super.onCreate(inSavedInstanceState);
        if (LOG) Log.d(TAG, "onCreate() - Début");
        
        appContext = getApplicationContext();
        
        setContentView(R.layout.config);
        preferences = getSharedPreferences(PREFS_NAME, 0);
        
        // Quitter la Configuration
        BTNConfQuitter = (Button) findViewById(R.id.Btn_Quitter);
        BTNConfQuitter.setOnClickListener(
        	new OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			if (LOG) Log.d(TAG, "BTNConfQuitter.setOnClickListener.OnClickListener().onClick() - Début");
        			//Quitter la fenetre de configuration
        			finish();
      			
					if (LOG) Log.d(TAG, "BTNConfQuitter.setOnClickListener.OnClickListener().onClick() - Fin");
	        	}
			}
        );

        // Initialisation Liste des Types de Nom pour l'affichage des résultats
        SPTypeLibelle = (Spinner) findViewById(R.id.Spin_Type_Libelle);

        ArrayAdapter<CharSequence> adapterTypeLibelle = ArrayAdapter.createFromResource(
                this, R.array.res_liste_config_type_lib, android.R.layout.simple_spinner_item);
        adapterTypeLibelle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPTypeLibelle.setAdapter(adapterTypeLibelle);

        
        int aff_res_type_libelle = preferences.getInt("aff_res_type_libelle", 0);

        SPTypeLibelle.setSelection(aff_res_type_libelle);
        

        // Initialisation Liste de Choix Restriction du Type de fiches affiché
        SPFiltreTypeFiche = (Spinner) findViewById(R.id.Spin_Filtre_Type_Fiche);

        ArrayAdapter<CharSequence> adapterFiltreTypeFiche = ArrayAdapter.createFromResource(
                this, R.array.res_liste_config_restriction_type_fiche, android.R.layout.simple_spinner_item);
        adapterFiltreTypeFiche.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPFiltreTypeFiche.setAdapter(adapterFiltreTypeFiche);

        
        int filtre_type_fiche = preferences.getInt("filtre_type_fiche", 0);

        SPFiltreTypeFiche.setSelection(filtre_type_fiche);
        
        // Initialisation Liste de Choix Restriction par la Zone géographique
        SPFiltreZoneGeo = (Spinner) findViewById(R.id.Spin_Filtre_Zone_Geo);

        ArrayAdapter<CharSequence> adapterFiltreZoneGeo = ArrayAdapter.createFromResource(
                this, R.array.res_liste_config_restriction_zone_geo, android.R.layout.simple_spinner_item);
        adapterFiltreZoneGeo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPFiltreZoneGeo.setAdapter(adapterFiltreZoneGeo);

        
        int filtre_zone_geo = preferences.getInt("filtre_zone_geo", 0);

        SPFiltreZoneGeo.setSelection(filtre_zone_geo);
        
        
        // Initialisation Liste de Choix Cache
        SPCache = (Spinner) findViewById(R.id.Spin_Cache);

        ArrayAdapter<CharSequence> adapterCache = ArrayAdapter.createFromResource(
                this, R.array.res_liste_config_cache_duree, android.R.layout.simple_spinner_item);
        adapterCache.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPCache.setAdapter(adapterCache);

        int type_cache = preferences.getInt("type_cache", 2);

        SPCache.setSelection(type_cache);
        
        // Vide Cache
        BTNVideCache = (Button) findViewById(R.id.Btn_Vide_Cache);
        BTNVideCache.setOnClickListener(
        	new OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			if (LOG) Log.d(TAG, "BTNVideCache.setOnClickListener.OnClickListener().onClick() - Début");
        			
        			int nbFichiersEffaces = Outils.clearFolder(appContext.getCacheDir(), 0);
        			if (LOG) Log.v(TAG, "BTNVideCache.setOnClickListener.OnClickListener().onClick() - nbFichiersEffaces : " + nbFichiersEffaces);
        			
        			TV_Detail_Cache2.setText(appContext.getString(R.string.txt_config_cache_vider_det)+String.valueOf( Outils.sizeFolder(appContext.getCacheDir())/1024 )+" Ko" );
        			
					if (LOG) Log.d(TAG, "BTNVideCache.setOnClickListener.OnClickListener().onClick() - Fin");
	        	}
			}
        );

        //Affichage de la taille actuelle du cache
        TV_Detail_Cache2 = (TextView) findViewById(R.id.TV_Detail_Cache2);
        String tailleCacheAvecUnite = "";
        float tailleCache = Outils.sizeFolder(appContext.getCacheDir())/1024;
        if ( tailleCache < 1000 ) {
        	tailleCacheAvecUnite = String.valueOf(Math.round(tailleCache)) + " Ko";
        } else {
        	tailleCache = tailleCache / 1024;
        	tailleCacheAvecUnite = String.valueOf(Math.round(tailleCache)) + " Mo";
        }
        TV_Detail_Cache2.setText(appContext.getString(R.string.txt_config_cache_vider_det) + tailleCacheAvecUnite );
        
		if (LOG) Log.d(TAG, "onCreate() - Fin");
    }
    
    @Override
	protected void onPause()
    {
    	super.onPause();
    	if (LOG) Log.d(TAG, "onPause() - Début");
    	
    	SharedPreferences.Editor prefEdit = preferences.edit();  
    	
    	prefEdit.putInt("aff_res_type_libelle", SPTypeLibelle.getSelectedItemPosition());
    	prefEdit.putInt("filtre_type_fiche", SPFiltreTypeFiche.getSelectedItemPosition());
    	prefEdit.putInt("filtre_zone_geo", SPFiltreZoneGeo.getSelectedItemPosition());
    	prefEdit.putInt("type_cache", SPCache.getSelectedItemPosition());  
    	
    	prefEdit.commit(); 
    	
    	if (LOG) Log.d(TAG, "onPause() - Fin");
    }
}
	
