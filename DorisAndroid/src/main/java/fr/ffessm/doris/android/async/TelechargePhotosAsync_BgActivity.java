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
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;

import com.j256.ormlite.android.apptools.OpenHelperManager;

// Start of user code additional imports TelechargePhotosAsync_BgActivity

import java.util.ArrayList;
import java.util.HashSet;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import fr.ffessm.doris.android.activities.EtatModeHorsLigne_CustomViewActivity;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.LimitTimer;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;

import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

// End of user code

public class TelechargePhotosAsync_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = TelechargePhotosAsync_BgActivity.class.getCanonicalName();


    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;

    // Start of user code additional attribute declarations TelechargePhotosAsync_BgActivity

    private DorisDBHelper dorisDBHelper;

    // Permet de ralentir le traitement pour laisser du temps processeur aux autres applications
    // en milliseconde, on multiplie selon les contextes par 1, 2, 4
    int tempo = 50;

    // timer utilisé pour déclencher un refresh que toutes les x mili
    LimitTimer limitTimer = new LimitTimer(5000); //5 secondes 

    private Param_Outils paramOutils;
    private Photos_Outils photosOutils;
    private Reseau_Outils reseauOutils;

    Integer nbPhotosPrincATelechargerPourZone[] = new Integer[12];
	Integer nbPhotosATelechargerPourZone[] = new Integer[12];
	HashSet<String> hsImagesVigAllreadyAvailable;
	HashSet<String> hsImagesMedResAllreadyAvailable;
	HashSet<String> hsImagesHiResAllreadyAvailable;
	Integer nbPhotosATelechargerPourParticipant = 0;
	Integer nbPhotosATelechargerPourBiblio = 0;
	Integer nbPhotosATelechargerPourGlossaire = 0;


	// End of user code

	/** constructor */
    public TelechargePhotosAsync_BgActivity(Context context){
		this.dbHelper = OpenHelperManager.getHelper(context.getApplicationContext(), OrmLiteDBHelper.class);
		// use application wide helper
        this.context = context.getApplicationContext();
		// Start of user code additional attribute declarations TelechargePhotosAsync_BgActivity constructor
    	Log.d(LOG_TAG, "TelechargePhotosAsync_BgActivity() - Début");

    	DorisApplicationContext.getInstance().isTelechPhotos = true;

		String notificationTitle = context.getString(R.string.bg_notifTitle_imagesinitial);

    	String initialTickerText = context.getString(R.string.bg_notifText_imagesinitial);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle,
        		new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		paramOutils = new Param_Outils(context);
		photosOutils = new Photos_Outils(context);
		reseauOutils = new Reseau_Outils(context);

		// TODO : Tempo pour ralentir traitement : lecture paramètre temporaire
        try{
        	tempo = Integer.valueOf(preferences.getString(context.getString(R.string.pref_key_asynch_tempo), "50") );
        }catch(Exception e){}

        this.dorisDBHelper = dbHelper.getDorisDBHelper();

        Log.d(LOG_TAG, "TelechargePhotosAsync_BgActivity() - Fin");
		// End of user code

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

    	Log.d(LOG_TAG, "doInBackground() - Début");

    	//mNotificationHelper.setMaxNbPages(maxNbPages.toString());
		// End of user code

    	// Start of user code main loop of task TelechargePhotosAsync_BgActivity
		// This is where we would do the actual job
		// you should indicates the progression using publishProgress()
    	try{
    		// baisse la priorité pour s'assurer une meilleure réactivité
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

			// do the initialization of the task here
	    	// Téléchargement en tache de fond de toutes les photos de toutes les fiches correspondants aux critères de l'utilisateur


			Reseau_Outils reseauOutils = new Reseau_Outils(context);
    		if(reseauOutils.getConnectionType() == Reseau_Outils.ConnectionType.AUCUNE){
	        	Log.d(LOG_TAG, "pas connexion internet : annulation du téléchargement");
	        	return 0;
	        }

			// Si que des P0 pas la peine de travailler
	    	if (photosOutils.isPrecharModeOnlyP0()) {
	    		Log.d(LOG_TAG, "Seulement des P0 : pas la peine de travailler");
	        	return 0;
	    	}


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
	    	// zoneGeo : 6 - Faune et flore des Terres Antarctiques Françaises
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
			nbPhotosFichesATelecharger(listeZoneGeo);
			DorisApplicationContext.getInstance().notifyDataHasChanged(null);


	    	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// On commence par traiter les photos principales des Fiches

			// Photos déjà sur l'appareil
			mNotificationHelper.setContentTitle(context.getString(R.string.bg_notifTitle_imagesinitial));
		    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_rechercheImagesDispo) );
			mNotificationHelper.setMaxItemToProcess(0);

	   		photosDejaTelechargees();

			telechargementPhotosPrincipalesFiches(listeZoneGeo);
			if( this.isCancelled()) return 0;

    		// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// -- Puis toutes les autres des Fiches (pas principales) --

			// Photos déjà sur l'appareil
			mNotificationHelper.setContentTitle(context.getString(R.string.bg_notifTitle_imagesinitial));
			mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_rechercheImagesDispo) );
			mNotificationHelper.setMaxItemToProcess(0);

	   		photosDejaTelechargees();

			telechargementPhotosFiches(listeZoneGeo);
			if( this.isCancelled()) return 0;

    		// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// -- Les photos des Intervenants --

			if (paramOutils.getParamBoolean(R.string.pref_key_mode_precharg_photo_autres, false)) {
				telechargementPhotosIntervenants();
				if( this.isCancelled()) return 0;

				telechargementPhotosBibliographie();
				if( this.isCancelled()) return 0;

				telechargementPhotosGlossaire();
				if( this.isCancelled()) return 0;
			}


		}finally{
	    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);

	    	mNotificationHelper.completed();
	    }
		// End of user code

		// Start of user code end of task TelechargePhotosAsync_BgActivity

    	Log.d(LOG_TAG, "doInBackground() - Fin");
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

		DorisApplicationContext.getInstance().isTelechPhotos = false;

		DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
        // termine de notifier les vues qui pouvaient être intéressées
		DorisApplicationContext.getInstance().notifyDataHasChanged(null);

		// End of user code
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code TelechargePhotosAsync onPostExecute
        // retire l'activité qui est maintenant finie
        DorisApplicationContext.getInstance().isTelechPhotos = false;
        DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
        // termine de notifier les vues qui pouvaient être intéressées
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);

		// End of user code
    }

    // Start of user code additional operations TelechargePhotosAsync_BgActivity
    public OrmLiteDBHelper getHelper() {
    	return this.dbHelper;
    }



    public void photosDejaTelechargees() {
		hsImagesVigAllreadyAvailable = photosOutils.getAllPhotosAvailable(ImageType.VIGNETTE);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "photosDejaTelechargees - VigAllreadyAvailable : "+hsImagesVigAllreadyAvailable.size() );
		hsImagesMedResAllreadyAvailable = photosOutils.getAllPhotosAvailable(ImageType.MED_RES);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "photosDejaTelechargees - MedResAllreadyAvailable : "+hsImagesMedResAllreadyAvailable.size() );
		hsImagesHiResAllreadyAvailable = photosOutils.getAllPhotosAvailable(ImageType.HI_RES);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "photosDejaTelechargees - HiResAllreadyAvailable : "+hsImagesHiResAllreadyAvailable.size() );
    }

	/* On commence par compter les photos à télécharger pour que les indicateurs d'avancement soient
	 * juste et ergonomique
	 */
    public void nbPhotosFichesATelecharger(List<ZoneGeographique> listeZoneGeo) {
        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(0);

    	for (ZoneGeographique zoneGeo : listeZoneGeo) {

			nbPhotosPrincATelechargerPourZone[zoneGeo.getId()] = photosOutils.setAPrecharQteParZoneGeo(zoneGeo, true);
			nbPhotosATelechargerPourZone[zoneGeo.getId()] = photosOutils.setAPrecharQteParZoneGeo(zoneGeo, false);

    	}
    }

    public int telechargementPhotosPrincipalesFiches(List<ZoneGeographique> listeZoneGeo){

    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosPrincipalesFiches - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());

    		GenericRawResults<String[]> rawResults = null;
    		Photos_Outils.ImageType imageTypeImage;

			int nbPhotosPrinRecuesPourZone = 0;
    		int zoneId = zoneGeo.getId();

    		if ( photosOutils.getPrecharModeZoneGeo(zoneGeo.getZoneGeoKind()) != Photos_Outils.PrecharMode.P0 ) {

        		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosPrincipalesFiches - countNbPhotoPrincZone : "+nbPhotosPrincATelechargerPourZone[zoneId] );
		        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesprinc)
		        		+ Constants.getTitreCourtZoneGeographique(zoneGeo.getZoneGeoKind()));
		        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesprinc) );
	    		mNotificationHelper.setMaxItemToProcess(nbPhotosPrincATelechargerPourZone[zoneId]);
    			publishProgress( 0 );

    			imageTypeImage = photosOutils.getImageQualityToDownload(true, zoneGeo.getZoneGeoKind());
    			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosPrincipalesFiches - zoneId : "+zoneId+" - Type Image : "+imageTypeImage);

        		// SELECT cleURL FROM fiches_ZonesGeographiques, fiche, photoFiche WHERE ZoneGeographique_id = 1
	    		// AND  fiches_ZonesGeographiques.Fiche_id = fiche._id AND photoFiche._id =  fiche.photoPrincipale_id

				// récupère les cleURL et les imgPostfixCodes des fiches

        		List<String[]> listePhotoPrinc = new ArrayList<String[]>(100);
        		try{
					rawResults = dorisDBHelper.photoFicheDao.queryRaw(
						"SELECT cleURL, imgPostfixCodes FROM fiches_ZonesGeographiques, fiche, photoFiche "
						+ "WHERE ZoneGeographique_id = " + zoneId + " "
						+ "AND  fiches_ZonesGeographiques.Fiche_id = fiche._id "
						+ "AND photoFiche._id =  fiche.photoPrincipale_id "
						+ "AND cleURL <> ''");
						listePhotoPrinc = rawResults.getResults();
		        		rawResults.close();
        		} catch (java.sql.SQLException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
        		}

            	//DaoManager.clearCache();
            	DaoManager.unregisterDao(dbHelper.getConnectionSource(), dorisDBHelper.photoFicheDao);

				try {
					//int nbTelechargements = 0;

					for (String[] resultColumns : listePhotoPrinc) {

    					// Les vignettes des Photos Principales sont toujours téléchargées (si pas P0)
    					if ( !hsImagesVigAllreadyAvailable.contains(resultColumns[0]) ){
    						photosOutils.downloadPostFixedPhotoFile(resultColumns[0], Photos_Outils.ImageType.VIGNETTE, resultColumns[1]);
    						//nbTelechargements++;
    					}

						// Comme on télécharge toujours la vignette, on ne fait avancer le compteur
						// que si c'est la qualité demandée
						if ( imageTypeImage == Photos_Outils.ImageType.VIGNETTE) {
							nbPhotosPrinRecuesPourZone++;
						}

        				if ( imageTypeImage == Photos_Outils.ImageType.MED_RES) {
    						if ( !hsImagesMedResAllreadyAvailable.contains(resultColumns[0]) ){
    							photosOutils.downloadPostFixedPhotoFile("/"+resultColumns[0], Photos_Outils.ImageType.MED_RES, resultColumns[1]);
    							//nbTelechargements++;
    						}
    						nbPhotosPrinRecuesPourZone++;
        				}

        				if ( imageTypeImage == Photos_Outils.ImageType.HI_RES) {
    						if ( !hsImagesHiResAllreadyAvailable.contains(resultColumns[0]) ){
    							photosOutils.downloadPostFixedPhotoFile("/"+resultColumns[0], Photos_Outils.ImageType.HI_RES, resultColumns[1]);
    							//nbTelechargements++;
    						}
    						nbPhotosPrinRecuesPourZone++;
        				}
	    				/*	
    					if ( nbPhotosPrinRecuesPourZone % 100 == 0
							|| (nbTelechargements != 0 && nbTelechargements % 10 == 0) )
    							publishProgress( nbPhotosPrinRecuesPourZone );	

		    			//Enregistrement du nombre total de photos téléchargée pour afficher avancement au fur et à mesure
    					if (nbPhotosPrinRecuesPourZone % 200 == 0
							|| (nbTelechargements != 0 && nbTelechargements % 10 == 0) ) {
    						paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(zoneGeo.getZoneGeoKind(), true), nbPhotosPrinRecuesPourZone);
    						DorisApplicationContext.getInstance().notifyDataHasChanged(null);
    					}*/
    					if(limitTimer.hasTimerElapsed()){
    						DorisApplicationContext.getInstance().zoneTraitee = zoneGeo.getZoneGeoKind();

    						publishProgress( nbPhotosPrinRecuesPourZone );
    						paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(zoneGeo.getZoneGeoKind(), true), nbPhotosPrinRecuesPourZone);
    						DorisApplicationContext.getInstance().notifyDataHasChanged(null);
						    Log.d(LOG_TAG, "telechargementPhotosPrincipalesFiches() nbPhotosPrinRecuesPourZone="+nbPhotosPrinRecuesPourZone);
    					}

    					if (nbPhotosPrinRecuesPourZone % 150 == 0){
	    					if( this.isCancelled()) return nbPhotosPrinRecuesPourZone;
	    					if( reseauOutils.getConnectionType() == Reseau_Outils.ConnectionType.AUCUNE ) return nbPhotosPrinRecuesPourZone;

	    					// tempo pour économiser le CPU
	    	        		Thread.sleep(tempo); // wait for 50 milliseconds before running another loop
    					}
					}
        		} catch (IOException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
        		} catch (InterruptedException e) {
        			Log.e(LOG_TAG, e.getMessage(), e);
				}

    			//Enregistrement du nombre total de photos téléchargée pour afficher avancement
				// systématiquement donc, i.e. on n'attend pas les n secondes habituelles
				paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(zoneGeo.getZoneGeoKind(), true), nbPhotosPrinRecuesPourZone);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosPrincipalesFiches - nbPhotosPrincDejaLaPourZone : "+nbPhotosPrinRecuesPourZone );
				publishProgress( nbPhotosPrinRecuesPourZone );
				DorisApplicationContext.getInstance().notifyDataHasChanged(null);

    		}
			if(this.isCancelled()){
				// annulation demandée, fini la tache dés que possible
				return 0;
			}

    	} // fin ZoneGeo Images Principales
    	return 0;
    }

    public int telechargementPhotosFiches(List<ZoneGeographique> listeZoneGeo){

    	GenericRawResults<String[]> rawResults = null;
		Photos_Outils.ImageType imageTypeImage;

	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(0);

		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosFiches - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());

			int nbPhotosRecuesPourZone = 0;

			int zoneId = zoneGeo.getId();

			if ( photosOutils.getPrecharModeZoneGeo(zoneGeo.getZoneGeoKind()) != Photos_Outils.PrecharMode.P0
					&& photosOutils.getPrecharModeZoneGeo(zoneGeo.getZoneGeoKind()) != Photos_Outils.PrecharMode.P1 ) {

	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosFiches - countNbPhotoPrincZone : "+nbPhotosATelechargerPourZone[zoneId] );
		        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_images)
		        		+ Constants.getTitreCourtZoneGeographique(zoneGeo.getZoneGeoKind()) );
		        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_images) );
	    		mNotificationHelper.setMaxItemToProcess(nbPhotosATelechargerPourZone[zoneId]);
	    		publishProgress( 0 );

				imageTypeImage = photosOutils.getImageQualityToDownload(false, zoneGeo.getZoneGeoKind());
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosFiches - zoneId : "+zoneId+" - Type Image : "+imageTypeImage);


				// SELECT cleURL FROM fiches_ZonesGeographiques, photoFiche WHERE ZoneGeographique_id = 1
				// AND  fiches_ZonesGeographiques.Fiche_id = photoFiche.fiche_id

				// Création Index :
				// CREATE INDEX fiches_ZoneGeographiques_Id ON fiches_ZonesGeographiques(ZoneGeographique_id ASC);
				// CREATE INDEX photoFiche_I_ficheId ON photoFiche(fiche_id ASC);

	    		// récupère les cleURL et le imgPostfixCodes des fiches
	    		List<String[]> listePhotos = new ArrayList<String[]>(100);
	    		try{
					rawResults =
						dorisDBHelper.fiches_ZonesGeographiquesDao.queryRaw(
							"SELECT cleURL, imgPostfixCodes FROM fiches_ZonesGeographiques, photoFiche "
							+ "WHERE ZoneGeographique_id = " + zoneId + " "
							+ "AND  fiches_ZonesGeographiques.Fiche_id = photoFiche.fiche_id "
							+ "AND cleURL <> ''"
							);
					listePhotos = rawResults.getResults();
	        		rawResults.close();
	    		} catch (java.sql.SQLException e) {
	    			Log.e(LOG_TAG, e.getMessage(), e);
	    		}

	        	//DaoManager.clearCache();
	        	DaoManager.unregisterDao(dbHelper.getConnectionSource(), dorisDBHelper.fiches_ZonesGeographiquesDao);


			    try{
			    	//int nbTelechargements = 0;
					for (String[] resultColumns : listePhotos) {

						// On télécharge toujours la vignette si en mode <> P0 ou P1
						if ( !hsImagesVigAllreadyAvailable.contains(resultColumns[0]) ){
							photosOutils.downloadPostFixedPhotoFile("/"+resultColumns[0], Photos_Outils.ImageType.VIGNETTE, resultColumns[1]);
							//nbTelechargements++;
        				}

						if ( imageTypeImage == Photos_Outils.ImageType.VIGNETTE ){
							nbPhotosRecuesPourZone++;
						}

						if ( imageTypeImage == Photos_Outils.ImageType.MED_RES) {
							if ( !hsImagesMedResAllreadyAvailable.contains(resultColumns[0]) ){
								photosOutils.downloadPostFixedPhotoFile("/"+resultColumns[0], Photos_Outils.ImageType.MED_RES, resultColumns[1]);
								//nbTelechargements++;
							}
							nbPhotosRecuesPourZone++;
	    				}

	    				if ( imageTypeImage == Photos_Outils.ImageType.HI_RES) {
							if ( !hsImagesHiResAllreadyAvailable.contains(resultColumns[0]) ){
								photosOutils.downloadPostFixedPhotoFile("/"+resultColumns[0], Photos_Outils.ImageType.HI_RES, resultColumns[1]);
								//nbTelechargements++;
							}
							nbPhotosRecuesPourZone++;
	    				}
						
	    				/*if ( nbPhotosRecuesPourZone % 100 == 0
    						|| (nbTelechargements != 0 && nbTelechargements % 10 == 0) )
	    						publishProgress( nbPhotosRecuesPourZone );

						if ( nbPhotosRecuesPourZone % 200 == 0
							|| (nbTelechargements != 0 && nbTelechargements % 10 == 0) ) {
							
							//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosFiches - nbPhotosRecuesPourZone : "+nbPhotosRecuesPourZone + " - nbTelechargements : "+nbTelechargements);
							
							//Enregistrement du nombre total de photos téléchargée pour afficher avancement
							paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(zoneGeo.getZoneGeoKind(), false), nbPhotosRecuesPourZone);
			        		DorisApplicationContext.getInstance().notifyDataHasChanged(null);
						}*/
						if(limitTimer.hasTimerElapsed()){
							DorisApplicationContext.getInstance().zoneTraitee = zoneGeo.getZoneGeoKind();

							publishProgress( nbPhotosRecuesPourZone );
    						//Enregistrement du nombre total de photos téléchargée pour afficher avancement
							paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(zoneGeo.getZoneGeoKind(), false), nbPhotosRecuesPourZone);
			        		DorisApplicationContext.getInstance().notifyDataHasChanged(null);
							Log.d(LOG_TAG, "telechargementPhotosFiches() nbPhotosRecuesPourZone="+nbPhotosRecuesPourZone +" zone="+zoneGeo.getNom());
    					}
						if (nbPhotosRecuesPourZone % 150 == 0){
							if( this.isCancelled()) return nbPhotosRecuesPourZone;
							if( reseauOutils.getConnectionType() == Reseau_Outils.ConnectionType.AUCUNE ) return nbPhotosRecuesPourZone;

							// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
	        				Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
						}
						if(this.isCancelled()){
							// annulation demandée, fini la tache dés que possible
							break;
						}
					}
	    		} catch (IOException e) {
	    			Log.e(LOG_TAG, e.getMessage(), e);
	    		} catch (InterruptedException e) {
	    			Log.e(LOG_TAG, e.getMessage(), e);
				}
			}

			//Enregistrement du nombre total de photos téléchargée pour afficher avancement
			paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(zoneGeo.getZoneGeoKind(), false), nbPhotosRecuesPourZone);
			publishProgress( nbPhotosRecuesPourZone );
			DorisApplicationContext.getInstance().notifyDataHasChanged(null);


			if(this.isCancelled()){
				// annulation demandée, fini la tache dés que possible
				return 0;
			}
		} // Fin Pour Chaque ZoneGeo Toutes Photos
		return 0;
	}


    public int telechargementPhotosIntervenants(){

    	GenericRawResults<String[]> rawResults = null;

	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(0);

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
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosIntervenants() - nbPhotosATelechargerPourParticipant : "+nbPhotosATelechargerPourParticipant );

        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesparticipants));
        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesparticipants) );
   		mNotificationHelper.setMaxItemToProcess(nbPhotosATelechargerPourParticipant);
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

				String photoSurDisque = resultColumns[0].replace("gestionenligne/photos_vig", "");
				//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosIntervenants() - photoSurDisque : "+photoSurDisque );

				if ( !hsImagesVigAllreadyAvailable.contains(photoSurDisque) ){
					//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosIntervenants() - hsImagesVigAllreadyAvailable = false" );
					photosOutils.downloadPostFixedPhotoFile(photoSurDisque, Photos_Outils.ImageType.PORTRAITS, "");
				}

				nbIntervenantsAnalyses++;

				if (nbIntervenantsAnalyses % 20 == 0) publishProgress( nbIntervenantsAnalyses );

				if (nbIntervenantsAnalyses % 50 == 0){
					if( this.isCancelled()) return nbIntervenantsAnalyses;
					if( reseauOutils.getConnectionType() == Reseau_Outils.ConnectionType.AUCUNE ) return nbIntervenantsAnalyses;

					// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
    				Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
				}
				if(this.isCancelled()){
					// annulation demandée, fini la tache dés que possible
					break;
				}
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		return 0;
	}

    public int telechargementPhotosBibliographie(){

    	GenericRawResults<String[]> rawResults = null;

	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(0);

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
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosBibliographie() - nbPhotosATelechargerPourBiblio : "+nbPhotosATelechargerPourBiblio );

        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesbibliographie));
        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesbibliographie) );
   		mNotificationHelper.setMaxItemToProcess(nbPhotosATelechargerPourBiblio);
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
				//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosBibliographie() - photoURL : "+photoURL );

				if ( !hsImagesVigAllreadyAvailable.contains(Constants.PREFIX_IMGDSK_BIBLIO + photoURL) ){
					//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosBibliographie() - hsImagesVigAllreadyAvailable = false" );
					photosOutils.downloadPhotoFile("/"+photoURL, Constants.PREFIX_IMGDSK_BIBLIO + photoURL, Photos_Outils.ImageType.ILLUSTRATION_BIBLIO);
				}

				nbBiblioAnalyses++;

				if (nbBiblioAnalyses % 20 == 0) publishProgress( nbBiblioAnalyses );

				if (nbBiblioAnalyses % 50 == 0){
					if( this.isCancelled()) return nbBiblioAnalyses;
					if( reseauOutils.getConnectionType() == Reseau_Outils.ConnectionType.AUCUNE ) return nbBiblioAnalyses;

					// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
    				Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
				}
				if(this.isCancelled()){
					// annulation demandée, fini la tache dés que possible
					break;
				}
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		return 0;
	}

    public int telechargementPhotosGlossaire(){

		GenericRawResults<String[]> rawResults = null;

	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_imagesinitial));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_imagesinitial) );
		mNotificationHelper.setMaxItemToProcess(0);

		// Nombre de Photos dans le Glossaire
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
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosGlossaire() - nbPhotosATelechargerPourGlossaire : "+nbPhotosATelechargerPourGlossaire );

	    mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesglossaire));
	    mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesglossaire) );
			mNotificationHelper.setMaxItemToProcess(nbPhotosATelechargerPourGlossaire);
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
						//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosGlossaire() - photoURL : "+photoURL );

						if ( !hsImagesVigAllreadyAvailable.contains(Constants.PREFIX_IMGDSK_DEFINITION + photoURL) ){
							//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "telechargementPhotosGlossaire() - hsImagesVigAllreadyAvailable = false" );
							photosOutils.downloadPhotoFile("/"+photoURL, Constants.PREFIX_IMGDSK_DEFINITION + photoURL, Photos_Outils.ImageType.ILLUSTRATION_DEFINITION);
						}

						nbTermesAnalyses++;

						if (nbTermesAnalyses % 10 == 0) publishProgress( nbTermesAnalyses );

						if (nbTermesAnalyses % 50 == 0){
							if( this.isCancelled()) return nbTermesAnalyses;
							if( reseauOutils.getConnectionType() == Reseau_Outils.ConnectionType.AUCUNE ) return nbTermesAnalyses;

							// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
							Thread.sleep(4 * tempo); // wait for 200 milliseconds before running another loop
						}
						if(this.isCancelled()){
							// annulation demandée, fini la tache dés que possible
							break;
						}

					}
				}
				if(this.isCancelled()){
					// annulation demandée, fini la tache dés que possible
					break;
				}
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}

		return 0;
	}

	// End of user code

}
