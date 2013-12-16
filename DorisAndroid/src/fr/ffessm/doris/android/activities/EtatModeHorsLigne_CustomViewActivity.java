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
import java.util.List;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.activities.view.MultiProgressBar;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code additional imports EtatModeHorsLigne_CustomViewActivity
//End of user code
public class EtatModeHorsLigne_CustomViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>
//Start of user code additional implements EtatModeHorsLigne_CustomViewActivity
	implements DataChangedListener
//End of user code
{
	
	//Start of user code constants EtatModeHorsLigne_CustomViewActivity

	private static final String LOG_TAG = EtatModeHorsLigne_CustomViewActivity.class.getCanonicalName();
	Handler mHandler;
	
	protected SparseArray< MultiProgressBar> progressBarZones = new SparseArray< MultiProgressBar>(); 
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etatmodehorsligne_customview);
        //Start of user code onCreate EtatModeHorsLigne_CustomViewActivity
        
        
        
        createProgressZone();
        
        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
        	/*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
            	if(EtatModeHorsLigne_CustomViewActivity.this.isFinishing()) return;
            	if(inputMessage.obj != null ){
            		showToast((String) inputMessage.obj);
            	}
            	refreshScreenData();
            }

        };

        DorisApplicationContext.getInstance().addDataChangeListeners(this);
		//End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
		//Start of user code onResume EtatModeHorsLigne_CustomViewActivity
		//End of user code
	}
    //Start of user code additional code EtatModeHorsLigne_CustomViewActivity
    public void dataHasChanged(String textmessage){
		 Message completeMessage = mHandler.obtainMessage(1, textmessage);
        completeMessage.sendToTarget();
	}
    
    public Context getContext(){
		return this;
	}
    
    protected void updateProgressBarZone(ZoneGeographique inZoneGeo, MultiProgressBar progressBarZone){
		   //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - Début");
		   
		   String uri = Outils.getZoneIcone(this.getApplicationContext(), inZoneGeo.getId());
		   //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - uri icone : "+uri);  
		   int imageZone = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
		   
		   boolean affichageBarrePhotoPrinc;
		   boolean affichageBarrePhoto;
		   String summaryTexte = "";
		   int nbFichesZoneGeo = 0;
		   int avancementPhotoPrinc =0;
		   int avancementPhoto =0;
		   
		   if (inZoneGeo.getId() == -1){
			   nbFichesZoneGeo = (int)getHelper().getFicheDao().countOf();
		   } else {
			   nbFichesZoneGeo = getHelper().getFiches_ZonesGeographiquesDao().queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, inZoneGeo.getId()).size();
		   }
		   
		   Outils.PrecharMode precharModeZoneGeo = Outils.getPrecharModeZoneGeo(getContext(), inZoneGeo.getId());
		   
		   if ( precharModeZoneGeo == Outils.PrecharMode.P0 ) {

			   affichageBarrePhotoPrinc = false;
			   affichageBarrePhoto = false;
			   
			   summaryTexte = getContext().getString(R.string.avancement_progressbar_aucune_summary);
			   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
			   
		   } else {
			   int nbPhotosPrincATelecharger = Outils.getAPrecharQteZoneGeo(getContext(), inZoneGeo.getId(), true);
			   int nbPhotosATelecharger = Outils.getAPrecharQteZoneGeo(getContext(), inZoneGeo.getId(), false);
			   int nbPhotosPrincDejaLa = Outils.getDejaLaQteZoneGeo(getContext(), inZoneGeo.getId(), true);
			   int nbPhotosDejaLa = Outils.getDejaLaQteZoneGeo(getContext(), inZoneGeo.getId(), false);
			   
			   affichageBarrePhotoPrinc = true;
			   affichageBarrePhoto = true;
			   
			   if ( nbPhotosPrincATelecharger== 0){
				   summaryTexte = getContext().getString(R.string.avancement_progressbar_jamais_summary);
				   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
			   } else {
				   
				   if ( precharModeZoneGeo == Outils.PrecharMode.P1 ) {
				   
					   summaryTexte = getContext().getString(R.string.avancement_progressbar_P1_summary);
					   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
					   summaryTexte = summaryTexte.replace("@totalPh", ""+nbPhotosPrincATelecharger ) ;
					   summaryTexte = summaryTexte.replace("@nbPh", ""+nbPhotosPrincDejaLa );
					   
					   avancementPhoto = 0;
					   avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;

					   affichageBarrePhoto = false;
					   
				   } else {
					   summaryTexte = getContext().getString(R.string.avancement_progressbar_PX_summary1);
					   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
					   summaryTexte = summaryTexte.replace("@totalPh", ""+nbPhotosPrincATelecharger ) ;
					   summaryTexte = summaryTexte.replace("@nbPh", ""+nbPhotosPrincDejaLa );
					   
					   if (nbPhotosATelecharger == 0) {
						   summaryTexte += getContext().getString(R.string.avancement_progressbar_PX_jamais_summary2);
						   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
						   avancementPhoto = 0;
						   avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
					   } else {
						   summaryTexte += getContext().getString(R.string.avancement_progressbar_PX_summary2);
						   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
						   summaryTexte = summaryTexte.replace("@totalPh", ""+nbPhotosATelecharger ) ;
						   summaryTexte = summaryTexte.replace("@nbPh", ""+nbPhotosDejaLa );
						   
						   avancementPhoto = 100 * nbPhotosDejaLa / nbPhotosATelecharger;
						   avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
					   }
				   }
			   }

		   }
		   // TODO calculate download in progress
		   boolean downloadInProgress = false;
		   if(inZoneGeo.getId() == -1 && DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null){
			   downloadInProgress = true;
		   }
		   
		   progressBarZone.update(inZoneGeo.getNom(), summaryTexte, imageZone, affichageBarrePhotoPrinc, avancementPhotoPrinc, affichageBarrePhoto, avancementPhoto, downloadInProgress);
	}
    
    protected void createProgressZone(){

    	LinearLayout llContainerLayout =  (LinearLayout) findViewById(R.id.etatmodehorsligne_customview_avancements_layout);
    	
    	// Avancement et Affichage toutes Zones
    	ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.accueil_customview_zonegeo_touteszones));
    	MultiProgressBar progressBarZoneGenerale = new MultiProgressBar(this);
    	updateProgressBarZone(zoneToutesZones, progressBarZoneGenerale);
    	progressBarZones.put(zoneToutesZones.getId(), progressBarZoneGenerale); 
    	final Context context = this;
    	progressBarZoneGenerale.pbProgressBar_running.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Arrêt des téléchargements demandé", Toast.LENGTH_LONG).show();
				DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
				
				ProgressBar pbRunningBarLayout =  (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
				pbRunningBarLayout.setVisibility(View.GONE);
			}
		});
    	progressBarZoneGenerale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
				ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
		        ed.commit();
				startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
			}
		});
    	llContainerLayout.addView(progressBarZoneGenerale);

    	
    	// Avancement par Zone
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - avant ");
    	List<ZoneGeographique> listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - après");
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
			
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			MultiProgressBar progressBarZone = new MultiProgressBar(this);
 		    updateProgressBarZone(zoneGeo, progressBarZone);
 		    final int zoneGeoId = zoneGeo.getId();
 		    progressBarZone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
					ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zoneGeoId);
			        ed.commit();
					startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
				}
			});
 		    progressBarZones.put(zoneGeo.getId(), progressBarZone); 
	 		llContainerLayout.addView(progressBarZone);
		} 
    }
    
	//End of user code

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen EtatModeHorsLigne_CustomViewActivity
    	// mise à jour de la date de la base
    	TextView etatBase = (TextView) findViewById(R.id.etatmodehorsligne_customview_etat_base_description_textView);
    	CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		etatBase.setText(getString(R.string.etatmodehorsligne_customview_etat_base_description_text)+it.next().getDateBase());
    		//sb.append("Date base locale : " + it.next().getDateBase()+"\n");
		}
    	// mise à jour des progress bar
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - update progress bar : ");
		ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.accueil_customview_zonegeo_touteszones));
		updateProgressBarZone(zoneToutesZones, progressBarZones.get(zoneToutesZones.getId()));
		List<ZoneGeographique> listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
    	
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			updateProgressBarZone(zoneGeo, progressBarZones.get(zoneGeo.getId()));
		}
    		
		//End of user code
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu EtatModeHorsLigne_CustomViewActivity

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
		
		//Start of user code additional menu action EtatModeHorsLigne_CustomViewActivity

		//End of user code
        }
        return false;
    }

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
