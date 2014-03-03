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
package fr.ffessm.doris.android.async;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import fr.ffessm.doris.android.activities.EtatModeHorsLigne_CustomViewActivity;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
// Start of user code additional imports TelechargePhotosAsync_BgActivity

import java.util.ArrayList;
import java.util.HashSet;

import com.j256.ormlite.dao.GenericRawResults;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;

import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

// End of user code

public class TelechargePhotosAsync_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = TelechargePhotosAsync_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations TelechargePhotosAsync_BgActivity
    // Permet de ralentir le traitement pour laisser du temps processeur aux autres applications
    // en milliseconde, on multiplie selon les contextes par 1, 2, 4
    int tempo = 50;
    
    final Photos_Outils photosOutils = new Photos_Outils(context);
    
    Integer nbPhotosPrincATelechargerPourZone[] = new Integer[10];
	Integer nbPhotosATelechargerPourZone[] = new Integer[10];
	HashSet<String> hsImagesVigAllreadyAvailable;
	HashSet<String> hsImagesMedResAllreadyAvailable;
	HashSet<String> hsImagesHiResAllreadyAvailable;
	Integer nbPhotosATelechargerPourParticipant = 0;
	Integer nbPhotosATelechargerPourBiblio = 0;
	Integer nbPhotosATelechargerPourGlossaire = 0;
	// End of user code
    
	/** constructor */
    public TelechargePhotosAsync_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		// Start of user code additional attribute declarations TelechargePhotosAsync_BgActivity constructor
		String initialTickerText = context.getString(R.string.bg_notifText_imagesinitial);
		String notificationTitle = context.getString(R.string.bg_notifTitle_imagesinitial);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	
		// TODO : Tempo pour ralentir traitement : lecture paramètre temporaire
        try{
        	tempo = Integer.valueOf(preferences.getString(context.getString(R.string.pref_key_asynch_tempo), "50") );
        }catch(Exception e){}

		// End of user code
        this.dbHelper = dbHelper;
		this.context = context;
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected Integer doInBackground(String... arg0) {
    	

		// Start of user code initialization of the task TelechargePhotosAsync_BgActivity
		// do the initializatio of the task here
		// once done, you should indicates to the notificationHelper how many item will be processed
		//mNotificationHelper.setMaxNbPages(maxNbPages.toString());
		// End of user code
    	
    	// Start of user code main loop of task TelechargePhotosAsync_BgActivity
		// This is where we would do the actual job
		// you should indicates the progression using publishProgress()
    	try{
			// do the initialization of the task here
	    	// Téléchargement en tache de fond de toutes les photos de toutes les fiches correspondants aux critères de l'utilisateur
	    	if(Outils.getConnectionType(context) == Outils.ConnectionType.AUCUNE){
	        	Log.d(LOG_TAG, "pas connexion internet : annulation du téléchargement");
	        	return 0;
	        }
	    	
			// Si que des P0 pas la peine de travailler
	    	if (photosOutils.isPrecharModeOnlyP0()) {
	    		Log.d(LOG_TAG, "Seulement des P0 : pas la peine de travailler");
	        	return 0;
	    	}
	    	
	    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    	DorisDBHelper dorisDBHelper = dbHelper.getDorisDBHelper();
	    	
	    	
	    	/* On commence par compter les photos à télécharger pour que les indicateurs d'avancement soient
	    	 * juste et ergonomique
	    	 */
	    	List<ZoneGeographique> listeZoneGeo;
			listeZoneGeo = dbHelper.getZoneGeographiqueDao().queryForAll();
	    	// zoneGeo : 1 - Faune et flore marines de France métropolitaine
	    	// zoneGeo : 2 - Faune et flore dulcicoles de France métropolitaine
	    	// zoneGeo : 3 - Faune et flore subaquatiques de l'Indo-Pacifique
	    	// zoneGeo : 4 - Faune et flore subaquatiques des Caraïbes
	    	// zoneGeo : 5 - Faune et flore subaquatiques de l'Atlantique Nord-Ouest
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());

			DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    	
			nbPhotosFichesATelecharger(dorisDBHelper, listeZoneGeo);

	    	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// On commence par traiter les photos principales des Fiches
			
			// Photos déjà sur l'appareil
	   		photosDejaTelechargees(dorisDBHelper);
	   		
			telechargementPhotosPrincipalesFiches(dorisDBHelper, listeZoneGeo);
			if( this.isCancelled()) return 0;
			
    		// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// -- Puis toutes les autres des Fiches (pas principales) --
			
			// Photos déjà sur l'appareil
	   		photosDejaTelechargees(dorisDBHelper);
	   		
			telechargementPhotosFiches(dorisDBHelper, listeZoneGeo);
			if( this.isCancelled()) return 0;

    		// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// -- Les photos des Intervenants --
			
			// Photos déjà sur l'appareil
	   		photosDejaTelechargees(dorisDBHelper);
	   		
			if (Outils.getParamBoolean(context, R.string.pref_key_mode_precharg_photo_autres, false)) {
				telechargementPhotosIntervenants(dorisDBHelper);
				if( this.isCancelled()) return 0;
				
				telechargementPhotosBibliographie(dorisDBHelper);
				if( this.isCancelled()) return 0;
				
				telechargementPhotosGlossaire(dorisDBHelper);
				if( this.isCancelled()) return 0;
			}
			

		}finally{
	    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    	
	    	mNotificationHelper.completed();
	    }
		// End of user code
        
		// Start of user code end of task TelechargePhotosAsync_BgActivity
		// return the number of item processed
        return 0;
		// End of user code
    }
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }
	@Override
	protected void onCancelled() {
		super.onCancelled();
		mNotificationHelper.completed();
		// Start of user code TelechargePhotosAsync onCancelled

		DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
        // termine de notifier les vues qui pouvaient être intéressées
		DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		
		majParamNbandSize();
		// End of user code
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code TelechargePhotosAsync onPostExecute
        // retire l'activité qui est maintenant finie
        DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
        // termine de notifier les vues qui pouvaient être intéressées
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
        
        majParamNbandSize();
		// End of user code
    }

    // Start of user code additional operations TelechargePhotosAsync_BgActivity
    public OrmLiteDBHelper getHelper() {
    	return this.dbHelper;
    }
    
    

    public void photosDejaTelechargees(DorisDBHelper dorisDBHelper) {
		hsImagesVigAllreadyAvailable = photosOutils.getAllPhotosAvailable(ImageType.VIGNETTE);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - VigAllreadyAvailable : "+hsImagesVigAllreadyAvailable.size() );
		hsImagesMedResAllreadyAvailable = photosOutils.getAllPhotosAvailable(ImageType.MED_RES);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - MedResAllreadyAvailable : "+hsImagesMedResAllreadyAvailable.size() );
		hsImagesHiResAllreadyAvailable = photosOutils.getAllPhotosAvailable(ImageType.HI_RES);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - HiResAllreadyAvailable : "+hsImagesHiResAllreadyAvailable.size() );
    }
	
	/* On commence par compter les photos à télécharger pour que les indicateurs d'avancement soient
	 * juste et ergonomique
	 */
    public void nbPhotosFichesATelecharger(DorisDBHelper dorisDBHelper, List<ZoneGeographique> listeZoneGeo) {	
        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(""+0);

    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
		
    		GenericRawResults<String[]> rawResults = null;
    		
    		List<String[]> countPhoto = new ArrayList<String[]>(2);
    			    		
    		int zoneId = zoneGeo.getId();
    		nbPhotosPrincATelechargerPourZone[zoneId] = 0;
    		nbPhotosATelechargerPourZone[zoneId] = 0;
    		
    		if ( photosOutils.getPrecharModeZoneGeo(zoneId) != Photos_Outils.PrecharMode.P0 ) {
        		try{
					rawResults =
						dorisDBHelper.photoFicheDao.queryRaw("SELECT count(*) FROM fiches_ZonesGeographiques, fiche, photoFiche "
							+ "WHERE ZoneGeographique_id = " + zoneId + " "
							+ "AND  fiches_ZonesGeographiques.Fiche_id = fiche._id "
							+ "AND photoFiche._id =  fiche.photoPrincipale_id" );
					countPhoto = rawResults.getResults();
	        		rawResults.close();
        		} catch (java.sql.SQLException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
        		}
        		nbPhotosPrincATelechargerPourZone[zoneId] = Integer.valueOf(countPhoto.get(0)[0]);
    		}
    		Outils.setParamInt(context, photosOutils.getKeyDataAPrecharZoneGeo(zoneId, true), nbPhotosPrincATelechargerPourZone[zoneId]);

    		if ( photosOutils.getPrecharModeZoneGeo(zoneId) != Photos_Outils.PrecharMode.P0 
    				&& photosOutils.getPrecharModeZoneGeo(zoneId) != Photos_Outils.PrecharMode.P1 ) {
        		try{
					rawResults =
						dorisDBHelper.photoFicheDao.queryRaw("SELECT count(*) FROM fiches_ZonesGeographiques, photoFiche "
								+ "WHERE ZoneGeographique_id = " + zoneId + " "
								+ "AND  fiches_ZonesGeographiques.Fiche_id = photoFiche.fiche_id ");
					countPhoto = rawResults.getResults();
	        		rawResults.close();
        		} catch (java.sql.SQLException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
        		}
        		nbPhotosATelechargerPourZone[zoneId] = Integer.valueOf(countPhoto.get(0)[0]);
    		}
    		Outils.setParamInt(context, photosOutils.getKeyDataAPrecharZoneGeo(zoneId, false), nbPhotosATelechargerPourZone[zoneId]);
    	
    	}
    }
 	    	
    public int telechargementPhotosPrincipalesFiches(DorisDBHelper dorisDBHelper, List<ZoneGeographique> listeZoneGeo){

    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
    		
    		GenericRawResults<String[]> rawResults = null;
    		Photos_Outils.ImageType imageTypeImage;
    		
			int nbPhotosPrinRecuesPourZone = 0;
    		int zoneId = zoneGeo.getId();
    		
    		if ( photosOutils.getPrecharModeZoneGeo(zoneId) != Photos_Outils.PrecharMode.P0 ) {

        		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - countNbPhotoPrincZone : "+nbPhotosPrincATelechargerPourZone[zoneId] );
		        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesprinc)
		        		+ Constants.getTitreCourtZoneGeographique(Constants.getZoneGeographiqueFromId(zoneId)));
		        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesprinc) );
	    		mNotificationHelper.setMaxItemToProcess(""+nbPhotosPrincATelechargerPourZone[zoneId]);
    			publishProgress( 0 );
	
    			imageTypeImage = photosOutils.getImageQualityToDownload(true, zoneId);
    			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneId : "+zoneId+" - Type Image : "+imageTypeImage);

        		// SELECT cleURL FROM fiches_ZonesGeographiques, fiche, photoFiche WHERE ZoneGeographique_id = 1
	    		// AND  fiches_ZonesGeographiques.Fiche_id = fiche._id AND photoFiche._id =  fiche.photoPrincipale_id

				// récupère les id seulement des fiches

        		List<String[]> listePhotoPrinc = new ArrayList<String[]>(100);
        		try{
					rawResults = dorisDBHelper.photoFicheDao.queryRaw(
						"SELECT cleURL FROM fiches_ZonesGeographiques, fiche, photoFiche "
						+ "WHERE ZoneGeographique_id = " + zoneId + " "
						+ "AND  fiches_ZonesGeographiques.Fiche_id = fiche._id "
						+ "AND photoFiche._id =  fiche.photoPrincipale_id" );
						listePhotoPrinc = rawResults.getResults();
		        		rawResults.close();
        		} catch (java.sql.SQLException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
        		}

				try {

					for (String[] resultColumns : listePhotoPrinc) {
						
						String photoPrincURL = resultColumns[0];
						int nbTelechargements = 0;
						
    					// Les vignettes des Photos Principales sont toujours téléchargées (si pas P0)
    					if ( !hsImagesVigAllreadyAvailable.contains(photoPrincURL) ){
    						photosOutils.getOrDownloadPhotoFile(photoPrincURL, Photos_Outils.ImageType.VIGNETTE);
    						nbTelechargements++;
    					}
						// Comme on télécharge toujours la vignette, on ne fait avancer le compteur 
						// que si c'est la qualité demandée
						if ( imageTypeImage == Photos_Outils.ImageType.VIGNETTE) {
							nbPhotosPrinRecuesPourZone++;
						}
						
        				if ( imageTypeImage == Photos_Outils.ImageType.MED_RES) {
    						if ( !hsImagesMedResAllreadyAvailable.contains(photoPrincURL) ){
    							photosOutils.getOrDownloadPhotoFile(photoPrincURL, Photos_Outils.ImageType.MED_RES);
        						nbTelechargements++;
    						}
    						nbPhotosPrinRecuesPourZone++;
        				}
        				
        				if ( imageTypeImage == Photos_Outils.ImageType.HI_RES) {
    						if ( !hsImagesHiResAllreadyAvailable.contains(photoPrincURL) ){
    							photosOutils.getOrDownloadPhotoFile(photoPrincURL, Photos_Outils.ImageType.HI_RES);
        						nbTelechargements++;
    						}
    						nbPhotosPrinRecuesPourZone++;
        				}
	    					
    					if (nbPhotosPrinRecuesPourZone % 100 == 0 || nbTelechargements % 10 == 0) publishProgress( nbPhotosPrinRecuesPourZone );	
        			
		    			//Enregistrement du nombre total de photos téléchargée pour afficher avancement au fur et à mesure
    					if (nbPhotosPrinRecuesPourZone % 200 == 0 || nbTelechargements % 10 == 0){
    						Outils.setParamInt(context, photosOutils.getKeyDataRecuesZoneGeo(zoneId, true), nbPhotosPrinRecuesPourZone);
    						DorisApplicationContext.getInstance().notifyDataHasChanged(null);
    					}

    					if (nbPhotosPrinRecuesPourZone % 200 == 0){
	    	        		// tempo pour économiser le CPU
	    	        		Thread.sleep(tempo); // wait for 50 milliseconds before running another loop
	    					if( this.isCancelled()) return nbPhotosPrinRecuesPourZone;
    					}
					}
        		} catch (IOException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
        		} catch (InterruptedException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
				}

    			//Enregistrement du nombre total de photos téléchargée pour afficher avancement
				Outils.setParamInt(context, photosOutils.getKeyDataRecuesZoneGeo(zoneId, true), nbPhotosPrinRecuesPourZone);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nbPhotosPrincDejaLaPourZone : "+nbPhotosPrinRecuesPourZone );
				publishProgress( nbPhotosPrinRecuesPourZone );
    		}
	
    	} // fin ZoneGeo Images Principales
    	return 0;
    }

    public int telechargementPhotosFiches(DorisDBHelper dorisDBHelper, List<ZoneGeographique> listeZoneGeo){

    	GenericRawResults<String[]> rawResults = null;
		Photos_Outils.ImageType imageTypeImage;
		
	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(""+0);
			    				
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
			
			int nbPhotosRecuesPourZone = 0;
			
			int zoneId = zoneGeo.getId();
			        
			if ( photosOutils.getPrecharModeZoneGeo(zoneId) != Photos_Outils.PrecharMode.P0 
					&& photosOutils.getPrecharModeZoneGeo(zoneId) != Photos_Outils.PrecharMode.P1 ) {
	    		
	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - countNbPhotoPrincZone : "+nbPhotosATelechargerPourZone );
		        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_images)
		        		+ Constants.getTitreCourtZoneGeographique(Constants.getZoneGeographiqueFromId(zoneId)));
		        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_images) );
	    		mNotificationHelper.setMaxItemToProcess(""+nbPhotosATelechargerPourZone[zoneId]);
	    		publishProgress( 0 );
	    				        		
				imageTypeImage = photosOutils.getImageQualityToDownload(false, zoneId);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneId : "+zoneId+" - Type Image : "+imageTypeImage);
	    		
		
				// SELECT cleURL FROM fiches_ZonesGeographiques, photoFiche WHERE ZoneGeographique_id = 1
				// AND  fiches_ZonesGeographiques.Fiche_id = photoFiche.fiche_id
					
				// Création Index : 
				// CREATE INDEX fiches_ZoneGeographiques_Id ON fiches_ZonesGeographiques(ZoneGeographique_id ASC);
				// CREATE INDEX photoFiche_I_ficheId ON photoFiche(fiche_id ASC);
	    		
	    		// récupère les id seulement des fiches
	    		List<String[]> listePhotos = new ArrayList<String[]>(100);
	    		try{
					rawResults =
						dorisDBHelper.fiches_ZonesGeographiquesDao.queryRaw(
							"SELECT cleURL FROM fiches_ZonesGeographiques, photoFiche "
							+ "WHERE ZoneGeographique_id = " + zoneId + " "
							+ "AND  fiches_ZonesGeographiques.Fiche_id = photoFiche.fiche_id "
							);
					listePhotos = rawResults.getResults();
	        		rawResults.close();
	    		} catch (java.sql.SQLException e) {
	    			Log.e(LOG_TAG, e.getMessage(), e);
	    		}
		        		
			    try{
			    	int nbTelechargements = 0;
					for (String[] resultColumns : listePhotos) {
					    
						String photoURL = resultColumns[0];
		        		
						if ( imageTypeImage == Photos_Outils.ImageType.VIGNETTE ){
							if ( !hsImagesVigAllreadyAvailable.contains(photoURL) ){
								photosOutils.getOrDownloadPhotoFile(photoURL, Photos_Outils.ImageType.VIGNETTE);
	    						nbTelechargements++;
	        				}
							nbPhotosRecuesPourZone++;
						}
						if ( imageTypeImage == Photos_Outils.ImageType.MED_RES) {
							if ( !hsImagesMedResAllreadyAvailable.contains(photoURL) ){
								photosOutils.getOrDownloadPhotoFile(photoURL, Photos_Outils.ImageType.MED_RES);
	    						nbTelechargements++;
							}
							nbPhotosRecuesPourZone++;
	    				}
	    				
	    				if ( imageTypeImage == Photos_Outils.ImageType.HI_RES) {
							if ( !hsImagesHiResAllreadyAvailable.contains(photoURL) ){
								photosOutils.getOrDownloadPhotoFile(photoURL, Photos_Outils.ImageType.HI_RES);
	    						nbTelechargements++;
							}
							nbPhotosRecuesPourZone++;
	    				}
						
	    				if (nbPhotosRecuesPourZone % 100 == 0 || nbTelechargements % 10 == 0) publishProgress( nbPhotosRecuesPourZone );
	    				
						if (nbPhotosRecuesPourZone % 500 == 0 || nbTelechargements % 10 == 0){
							//Enregistrement du nombre total de photos téléchargée pour afficher avancement
			        		Outils.setParamInt(context, photosOutils.getKeyDataRecuesZoneGeo(zoneId, false), nbPhotosRecuesPourZone);
			        		DorisApplicationContext.getInstance().notifyDataHasChanged(null);
						}
						if (nbPhotosRecuesPourZone % 200 == 0){	
							if( this.isCancelled()) return nbPhotosRecuesPourZone;
								// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
	        				Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
						}
						
						
					}
	    		} catch (IOException e) {
	    			Log.e(LOG_TAG, e.getMessage(), e);
	    		} catch (InterruptedException e) {
	    			Log.e(LOG_TAG, e.getMessage(), e);
				}
			}
		
			//Enregistrement du nombre total de photos téléchargée pour afficher avancement
			Outils.setParamInt(context, photosOutils.getKeyDataRecuesZoneGeo(zoneId, false), nbPhotosRecuesPourZone);
			publishProgress( nbPhotosRecuesPourZone );
			
		} // Fin Pour Chaque ZoneGeo Toutes Photos
		return 0;
	}
    

    public int telechargementPhotosIntervenants(DorisDBHelper dorisDBHelper){
    	
    	GenericRawResults<String[]> rawResults = null;
		
	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(""+0);

		// Nombre de Photos d'intervenants
		List<String[]> countPhoto = new ArrayList<String[]>(2);
		try{
			rawResults =
				dorisDBHelper.participantDao.queryRaw("SELECT count(*) FROM Participant "
					+ "WHERE cleURLPhotoParticipant <> \"\"");
			countPhoto = rawResults.getResults();
    		rawResults.close();
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		nbPhotosATelechargerPourParticipant = Integer.valueOf(countPhoto.get(0)[0]);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosIntervenants() - nbPhotosATelechargerPourParticipant : "+nbPhotosATelechargerPourParticipant );
    	
        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesparticipants));
        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesparticipants) );
   		mNotificationHelper.setMaxItemToProcess(""+nbPhotosATelechargerPourParticipant);
   		publishProgress( 0 );

 		// récupère les url des photos
		// SELECT cleURLPhotoParticipant FROM Participant WHERE cleURLPhotoParticipant <> ""
		List<String[]> listePhotos = new ArrayList<String[]>(100);
		try{
			rawResults =
				dorisDBHelper.participantDao.queryRaw(
					"SELECT cleURLPhotoParticipant FROM Participant "
					+ "WHERE cleURLPhotoParticipant <> \"\""
					);
			listePhotos = rawResults.getResults();
    		rawResults.close();
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
        		
	    try{
	    	int nbIntervenantsAnalyses = 0;
			for (String[] resultColumns : listePhotos) {
			    
				String photoURL = resultColumns[0];
				String photoSurDisque = photoURL.replace("gestionenligne/photos_vig", "");
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosIntervenants() - photoSurDisque : "+photoSurDisque );
		    	
				if ( !hsImagesVigAllreadyAvailable.contains(photoSurDisque) ){
					if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosIntervenants() - hsImagesVigAllreadyAvailable = false" );
					photosOutils.getOrDownloadPhotoFile(photoSurDisque, Photos_Outils.ImageType.PORTRAITS);
				}
				
				nbIntervenantsAnalyses++;
				
				if (nbIntervenantsAnalyses % 10 == 0) publishProgress( nbIntervenantsAnalyses );
				
				if (nbIntervenantsAnalyses % 10 == 0){
					//Enregistrement du nombre total de photos téléchargée pour afficher avancement
	        		//Outils.setParamInt(context, Outils.getKeyDataRecuesZoneGeo(context, zoneId, false), nbPhotosRecuesPourZone);
	        		//DorisApplicationContext.getInstance().notifyDataHasChanged(null);
				}
				if (nbIntervenantsAnalyses % 50 == 0){	
					if( this.isCancelled()) return nbIntervenantsAnalyses;
					// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
    				Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
				}
				
						
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		return 0;
	}
    
public int telechargementPhotosBibliographie(DorisDBHelper dorisDBHelper){
    	
    	GenericRawResults<String[]> rawResults = null;
		
	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(""+0);

		// Nombre de Photos de la Bibliographie
		List<String[]> countPhoto = new ArrayList<String[]>(2);
		try{
			rawResults =
				dorisDBHelper.entreeBibliographieDao.queryRaw("SELECT count(*) FROM entreeBibliographie "
					+ "WHERE cleURLIllustration <> \"\"");
			countPhoto = rawResults.getResults();
    		rawResults.close();
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		nbPhotosATelechargerPourBiblio = Integer.valueOf(countPhoto.get(0)[0]);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosBibliographie() - nbPhotosATelechargerPourBiblio : "+nbPhotosATelechargerPourBiblio );
    	
        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesbibliographie));
        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesbibliographie) );
   		mNotificationHelper.setMaxItemToProcess(""+nbPhotosATelechargerPourBiblio);
   		publishProgress( 0 );

 		// récupère les url des photos
		List<String[]> listePhotos = new ArrayList<String[]>(100);
		try{
			rawResults =
				dorisDBHelper.entreeBibliographieDao.queryRaw(
					"SELECT cleURLIllustration FROM entreeBibliographie "
					+ "WHERE cleURLIllustration <> \"\""
					);
			listePhotos = rawResults.getResults();
    		rawResults.close();
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
        		
	    try{
	    	int nbBiblioAnalyses = 0;
			for (String[] resultColumns : listePhotos) {
			    
				String photoURL = resultColumns[0];
				photoURL = photoURL.replace("gestionenligne/photos_biblio_moy/", "");
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosBibliographie() - photoURL : "+photoURL );
		    	
				if ( !hsImagesVigAllreadyAvailable.contains(Constants.PREFIX_IMGDSK_BIBLIO + photoURL) ){
					if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosBibliographie() - hsImagesVigAllreadyAvailable = false" );
					photosOutils.getOrDownloadPhotoFile("/"+photoURL, Constants.PREFIX_IMGDSK_BIBLIO + photoURL, Photos_Outils.ImageType.ILLUSTRATION_BIBLIO);
				}
				
				nbBiblioAnalyses++;
				
				if (nbBiblioAnalyses % 10 == 0) publishProgress( nbBiblioAnalyses );
				
				if (nbBiblioAnalyses % 10 == 0){
					//Enregistrement du nombre total de photos téléchargée pour afficher avancement
	        		//Outils.setParamInt(context, Outils.getKeyDataRecuesZoneGeo(context, zoneId, false), nbPhotosRecuesPourZone);
	        		//DorisApplicationContext.getInstance().notifyDataHasChanged(null);
				}
				if (nbBiblioAnalyses % 50 == 0){	
					if( this.isCancelled()) return nbBiblioAnalyses;
					// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
    				Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
				}
				
						
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		return 0;
	}

public int telechargementPhotosGlossaire(DorisDBHelper dorisDBHelper){
	
	GenericRawResults<String[]> rawResults = null;
	
    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
	mNotificationHelper.setMaxItemToProcess(""+0);

	// Nombre de Photos de la Glossaire
	List<String[]> countPhoto = new ArrayList<String[]>(2);
	try{
		rawResults =
			dorisDBHelper.definitionGlossaireDao.queryRaw("SELECT count(*) FROM definitionGlossaire "
				+ "WHERE cleURLIllustration <> \"\"");
		countPhoto = rawResults.getResults();
		rawResults.close();
	} catch (java.sql.SQLException e) {
		Log.e(LOG_TAG, e.getMessage(), e);
	}
	nbPhotosATelechargerPourGlossaire = Integer.valueOf(countPhoto.get(0)[0]);
	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosGlossaire() - nbPhotosATelechargerPourGlossaire : "+nbPhotosATelechargerPourGlossaire );
	
    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesglossaire));
    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesglossaire) );
		mNotificationHelper.setMaxItemToProcess(""+nbPhotosATelechargerPourGlossaire);
		publishProgress( 0 );

		// récupère les url des photos
	List<String[]> listeListePhotos = new ArrayList<String[]>(100);
	try{
		rawResults =
			dorisDBHelper.definitionGlossaireDao.queryRaw(
				"SELECT cleURLIllustration FROM definitionGlossaire "
				+ "WHERE cleURLIllustration <> \"\""
				);
		listeListePhotos = rawResults.getResults();
		rawResults.close();
	} catch (java.sql.SQLException e) {
		Log.e(LOG_TAG, e.getMessage(), e);
	}
    		
    try{
    	int nbTermesAnalyses = 0;
		for (String[] resultColumns : listeListePhotos) {
		    
			String listePhotos = resultColumns[0];
			String[] photosURL = listePhotos.split(";");
			
			for (String photoURL : photosURL) {
				if (!photoURL.isEmpty()) {
		
					photoURL = photoURL.replace("gestionenligne/diaporamaglo/", "");
					if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosGlossaire() - photoURL : "+photoURL );
			    	
					if ( !hsImagesVigAllreadyAvailable.contains(Constants.PREFIX_IMGDSK_DEFINITION + photoURL) ){
						if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosGlossaire() - hsImagesVigAllreadyAvailable = false" );
						photosOutils.getOrDownloadPhotoFile("/"+photoURL, Constants.PREFIX_IMGDSK_DEFINITION + photoURL, Photos_Outils.ImageType.ILLUSTRATION_DEFINITION);
					}
					
					nbTermesAnalyses++;
					
					if (nbTermesAnalyses % 10 == 0) publishProgress( nbTermesAnalyses );
					
					if (nbTermesAnalyses % 10 == 0){
						//Enregistrement du nombre total de photos téléchargée pour afficher avancement
		        		//Outils.setParamInt(context, Outils.getKeyDataRecuesZoneGeo(context, zoneId, false), nbPhotosRecuesPourZone);
		        		//DorisApplicationContext.getInstance().notifyDataHasChanged(null);
					}
					if (nbTermesAnalyses % 50 == 0){	
						if( this.isCancelled()) return nbTermesAnalyses;
						// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
						Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
					}
					
				}
			}
		}
	} catch (IOException e) {
		Log.e(LOG_TAG, e.getMessage(), e);
	} catch (InterruptedException e) {
		Log.e(LOG_TAG, e.getMessage(), e);
	}

	return 0;
}


    public void majParamNbandSize() {
    	Outils.setParamInt(context,R.string.pref_key_nbphotos_recues_vignettes, photosOutils.getImageCount(ImageType.VIGNETTE));
		Outils.setParamLong(context,R.string.pref_key_size_folder_vignettes, photosOutils.getPhotoDiskUsage(ImageType.VIGNETTE));
		Outils.setParamInt(context,R.string.pref_key_nbphotos_recues_med_res, photosOutils.getImageCount(ImageType.MED_RES));
		Outils.setParamLong(context,R.string.pref_key_size_folder_med_res, photosOutils.getPhotoDiskUsage(ImageType.MED_RES));
		Outils.setParamInt(context,R.string.pref_key_nbphotos_recues_hi_res, photosOutils.getImageCount(ImageType.HI_RES));
		Outils.setParamLong(context,R.string.pref_key_size_folder_hi_res, photosOutils.getPhotoDiskUsage(ImageType.HI_RES));

    }	
	// End of user code
	
}
