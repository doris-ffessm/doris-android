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
import android.os.AsyncTask;
import android.util.Log;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
// Start of user code additional imports TelechargePhotosFiches_BgActivity
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.datamodel.xml.XMLHelper;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.Outils.ImageType;
// End of user code

public class TelechargePhotosFiches_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = TelechargePhotosFiches_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations TelechargePhotosFiches_BgActivity
    
    int tempo = 50;
    
    /**
     * version qui ne conserve que l'id de la photo, permet de libérer de la mémoire plus de mémoire, mais oblige à refaire une réquète
     * pour retrouver l'url de la photo
     */
	class PhotoATraiter
	{
		int id;
		Outils.ImageType imageType;
		boolean imagePrincipale;
		
		int idZoneConcernee = -1;

		public PhotoATraiter(){}

		public PhotoATraiter(PhotoFiche inPhotoFiche, ImageType inImageType, boolean inImagePrincipale, int idZoneConcernee) {
			id = inPhotoFiche.getId();
			imageType = inImageType;
			imagePrincipale = inImagePrincipale;
			this.idZoneConcernee = idZoneConcernee;
		}
		
		public int getId() {
			return this.id;
		}
		public String getIdStr() {
			return ""+this.id;
		}
		public int getIdZoneConcernee() {
			return idZoneConcernee;
		}

		public void setIdZoneConcernee(int idZoneConcernee) {
			this.idZoneConcernee = idZoneConcernee;
		}
	}
	
	/**
     * version qui ne conserve que la PhotoFiche, (pour algo optimisé)
     * pour retrouver l'url de la photo
     */
	class PhotoATraiterOptim
	{
		PhotoFiche photoFiche;
		boolean imagePrincipale;
		
		int idZoneConcernee;  // Pour l'instant, avec cet algo, la photo n'est comptabilisée que dans une seule zone

		public PhotoATraiterOptim(){}

		public PhotoATraiterOptim(PhotoFiche inPhotoFiche,  boolean inImagePrincipale, int idZoneConcernee) {
			photoFiche = inPhotoFiche;
			imagePrincipale = inImagePrincipale;
			this.idZoneConcernee = idZoneConcernee;
		}
		
		public PhotoFiche getPhotoFiche() {
			return this.photoFiche;
		}
		public int getIdZoneConcernee() {
			return idZoneConcernee;
		}

		/** assure que le hasset va bien trouver les doublons
		 * 
		 */
		@Override
		public boolean equals(Object o) {
			if(o instanceof PhotoATraiterOptim){
				return photoFiche.getId() == ((PhotoATraiterOptim)o).getPhotoFiche().getId() && 
					   imagePrincipale == ((PhotoATraiterOptim)o).imagePrincipale;
			}
			else
				return super.equals(o);
		}

		@Override
		public int hashCode() {
			// utilise l'id de la photo pour trier
			return photoFiche.getId();
		}
		
	}

	
    
	// End of user code
    
	/** constructor */
    public TelechargePhotosFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		// Start of user code additional attribute declarations TelechargePhotosFiches_BgActivity constructor
		String initialTickerText = context.getString(R.string.bg_notifText_initial);
		String notificationTitle = context.getString(R.string.bg_notifTitle_initial);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor prefEdit = preferences.edit(); 
    	
		// TODO : Tempo pour ralentir traitement : temporaire
		tempo = 50;
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
    	

		// Start of user code initialization of the task TelechargePhotosFiches_BgActivity

	    try{
			// do the initialization of the task here
	    	// Téléchargement en tache de fond de toutes les photos de toutes les fiches correspondants aux critères de l'utilisateur
	    	if(!isOnline()){
	        	Log.d(LOG_TAG, "pas connexion internet : annulation du téléchargement");
	        	return 0;
	        }
	    	
			// Si que des P0 pas la peine de travailler
	    	if (Outils.isPrecharModeOnlyP0(context)) {
	    		Log.d(LOG_TAG, "Seulement des P0 : pas la peine de travailler");
	        	return 0;
	    	}
	    	
	    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    	DorisDBHelper dorisDBHelper = dbHelper.getDorisDBHelper();
	    	
	    	GenericRawResults<String[]> rawResults = null;
	
			Outils.ImageType imageTypeImage;
			
	        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_initial));
	        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_initial) );
			mNotificationHelper.setMaxItemToProcess(""+0);
	
			
			List<ZoneGeographique> listeZoneGeo = dbHelper.getZoneGeographiqueDao().queryForAll();
	    	// zoneGeo : 1 - Faune et flore marines de France métropolitaine
	    	// zoneGeo : 2 - Faune et flore dulcicoles de France métropolitaine
	    	// zoneGeo : 3 - Faune et flore subaquatiques de l'Indo-Pacifique
	    	// zoneGeo : 4 - Faune et flore subaquatiques des Caraïbes
	    	// zoneGeo : 5 - Faune et flore subaquatiques de l'Atlantique Nord-Ouest
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
			
			// On commence par compter les photos à télécharger pour que les indicateurs d'avancement soient
			// juste et ergonomique
			Integer nbPhotosPrincATelechargerPourZone[] = new Integer[10];
			Integer nbPhotosATelechargerPourZone[] = new Integer[10];
	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
			
	    		List<String[]> countPhoto = new ArrayList<String[]>(2);
	    		
	    			    		
	    		int zoneId = zoneGeo.getId();
	    		nbPhotosPrincATelechargerPourZone[zoneId] = 0;
	    		nbPhotosATelechargerPourZone[zoneId] = 0;
	    		
	    		if ( Outils.getPrecharModeZoneGeo(context, zoneId) != Outils.PrecharMode.P0 ) {
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
	    		Outils.setParamInt(context, Outils.getKeyDataAPrecharZoneGeo(context, zoneId, true), nbPhotosPrincATelechargerPourZone[zoneId]);

	    		if ( Outils.getPrecharModeZoneGeo(context, zoneId) != Outils.PrecharMode.P0 
	    				&& Outils.getPrecharModeZoneGeo(context, zoneId) != Outils.PrecharMode.P1 ) {
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
	    		Outils.setParamInt(context, Outils.getKeyDataAPrecharZoneGeo(context, zoneId, false), nbPhotosATelechargerPourZone[zoneId]);
	    	
	    	}
	    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    	
	    	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// On commence par les photos principales
			
			// Photos déjà sur l'appareil
			HashSet<String> hsImagesVigAllreadyAvailable = Outils.getAllVignettesPhotoFicheAvailable(context);
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - VigAllreadyAvailable : "+hsImagesVigAllreadyAvailable.size() );
			HashSet<String> hsImagesMedResAllreadyAvailable = Outils.getAllMedResPhotoFicheAvailable(context);
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - MedResAllreadyAvailable : "+hsImagesMedResAllreadyAvailable.size() );
			HashSet<String> hsImagesHiResAllreadyAvailable = Outils.getAllHiResPhotoFicheAvailable(context);
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - HiResAllreadyAvailable : "+hsImagesHiResAllreadyAvailable.size() );
			
	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
	    		
				int nbPhotosPrinRecuesPourZone = 0;
	    		int zoneId = zoneGeo.getId();
	    		
	    		if ( Outils.getPrecharModeZoneGeo(context, zoneId) != Outils.PrecharMode.P0 ) {

	        		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - countNbPhotoPrincZone : "+nbPhotosPrincATelechargerPourZone[zoneId] );
			        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_imagesprinc)
			        		+ Constants.getTitreCourtZoneGeographique(Constants.getZoneGeographiqueFromId(zoneId)));
			        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_imagesprinc) );
		    		mNotificationHelper.setMaxItemToProcess(""+nbPhotosPrincATelechargerPourZone[zoneId]);
	    			publishProgress( 0 );
		
	    			imageTypeImage = Outils.getImageQualityToDownload(context, true, zoneId);
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
    					
	    					// Les vignettes des Photos Principales sont toujours téléchargées (si pas P0)
	    					if ( !hsImagesVigAllreadyAvailable.contains(photoPrincURL) ){
	    						Outils.getOrDownloadPhotoFile(context, photoPrincURL, Outils.ImageType.VIGNETTE);
	    					}
							// Comme on télécharge toujours la vignette, on ne fait avancer le compteur 
							// que si c'est la qualité demandée
							if ( imageTypeImage == Outils.ImageType.VIGNETTE) {
								nbPhotosPrinRecuesPourZone++;
							}
							
	        				if ( imageTypeImage == Outils.ImageType.MED_RES) {
	    						if ( !hsImagesMedResAllreadyAvailable.contains(photoPrincURL) ){
	        						Outils.getOrDownloadPhotoFile(context, photoPrincURL, Outils.ImageType.MED_RES);
		        				}
	    						nbPhotosPrinRecuesPourZone++;
	        				}
	        				
	        				if ( imageTypeImage == Outils.ImageType.HI_RES) {
	    						if ( !hsImagesHiResAllreadyAvailable.contains(photoPrincURL) ){
	        						Outils.getOrDownloadPhotoFile(context, photoPrincURL, Outils.ImageType.HI_RES);
		        				}
	    						nbPhotosPrinRecuesPourZone++;
	        				}
		    					
	    					if (nbPhotosPrinRecuesPourZone % 10 == 0) publishProgress( nbPhotosPrinRecuesPourZone );	
	        			
			    			//Enregistrement du nombre total de photos téléchargée pour afficher avancement au fur et à mesure
	    					if (nbPhotosPrinRecuesPourZone % 50 == 0){
	    						Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneId, true), nbPhotosPrinRecuesPourZone);
	    						DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    					}
	
	    					if( this.isCancelled()) return nbPhotosPrinRecuesPourZone;
						}
	        		} catch (IOException e) {
	        			Log.e(LOG_TAG, e.getMessage(), e);
	        		}
					listePhotoPrinc.clear();
						/*
			    			if((hsImagesPrincHiRes.size()+hsImagesPrincMedRes.size()+hsImagesPrincVig.size()) % 200 ==0){
			    				// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
			    				Thread.sleep(2 * tempo); // wait for 100 milliseconds before running another loop
			    			}
		    			*/
			        			
	    			//Enregistrement du nombre total de photos téléchargée pour afficher avancement
	        		
					Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneId, true), nbPhotosPrinRecuesPourZone);
					if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nbPhotosPrincDejaLaPourZone : "+nbPhotosPrinRecuesPourZone );
		    		
	        		// tempo pour économiser le CPU
	        		Thread.sleep(tempo); // wait for 50 milliseconds before running another loop
	    		}
		
	    	} // fin ZoneGeo Images Principales
			    	
    		// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// -- Puis toutes les autres (pas principales) --
	    	
	        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle_initial));
	        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_notifText_initial) );
    		mNotificationHelper.setMaxItemToProcess(""+0);
    			    		
    		hsImagesVigAllreadyAvailable = Outils.getAllVignettesPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - VigAllreadyAvailable : "+hsImagesVigAllreadyAvailable.size() );
    		hsImagesMedResAllreadyAvailable = Outils.getAllMedResPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - MedResAllreadyAvailable : "+hsImagesMedResAllreadyAvailable.size() );
    		hsImagesHiResAllreadyAvailable = Outils.getAllHiResPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - HiResAllreadyAvailable : "+hsImagesHiResAllreadyAvailable.size() );
    		
    		
	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
    			
	    		int nbPhotosRecuesPourZone = 0;
	    		
	    		int zoneId = zoneGeo.getId();
				        
	    		if ( Outils.getPrecharModeZoneGeo(context, zoneId) != Outils.PrecharMode.P0 
	    				&& Outils.getPrecharModeZoneGeo(context, zoneId) != Outils.PrecharMode.P1 ) {
	        		
	        		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - countNbPhotoPrincZone : "+nbPhotosATelechargerPourZone );
			        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitre_images)
			        		+ Constants.getTitreCourtZoneGeographique(Constants.getZoneGeographiqueFromId(zoneId)));
			        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTicker_images) );
		    		mNotificationHelper.setMaxItemToProcess(""+nbPhotosATelechargerPourZone[zoneId]);
		    		publishProgress( 0 );
		    				        		
	    			imageTypeImage = Outils.getImageQualityToDownload(context, false, zoneId);
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
						for (String[] resultColumns : listePhotos) {
						    
							String photoURL = resultColumns[0];
			        		
        					if ( imageTypeImage == Outils.ImageType.VIGNETTE ){
        						if ( !hsImagesVigAllreadyAvailable.contains(photoURL) ){
	        						Outils.getOrDownloadPhotoFile(context, photoURL, Outils.ImageType.VIGNETTE);
		        				}
        						nbPhotosRecuesPourZone++;
							}
        					if ( imageTypeImage == Outils.ImageType.MED_RES) {
        						if ( !hsImagesMedResAllreadyAvailable.contains(photoURL) ){
	        						Outils.getOrDownloadPhotoFile(context, photoURL, Outils.ImageType.MED_RES);
		        				}
        						nbPhotosRecuesPourZone++;
	        				}
	        				
	        				if ( imageTypeImage == Outils.ImageType.HI_RES) {
        						if ( !hsImagesHiResAllreadyAvailable.contains(photoURL) ){
	        						Outils.getOrDownloadPhotoFile(context, photoURL, Outils.ImageType.HI_RES);
		        				}
        						nbPhotosRecuesPourZone++;
	        				}
        					
	        				if (nbPhotosRecuesPourZone % 10 == 0) publishProgress( nbPhotosRecuesPourZone );
	        				
    						if (nbPhotosRecuesPourZone % 50 == 0){
    							//Enregistrement du nombre total de photos téléchargée pour afficher avancement
    			        		Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneId, false), nbPhotosRecuesPourZone);
    			        		DorisApplicationContext.getInstance().notifyDataHasChanged(null);
    							// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
		        				Thread.sleep(2 * tempo); // wait for 100 milliseconds before running another loop
		        			}

    						if( this.isCancelled()) return nbPhotosRecuesPourZone;
						}
	        		} catch (IOException e) {
	        			Log.e(LOG_TAG, e.getMessage(), e);
	        		}
				    listePhotos.clear();
    			}
	    	
	    		//Enregistrement du nombre total de photos téléchargée pour afficher avancement
        		Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneId, false), nbPhotosRecuesPourZone);

    	} // Fin Pour Chaque ZoneGeo Toutes Photos
		
		if( this.isCancelled()) return 0;
		// End of user code
        
		// Start of user code end of task TelechargePhotosFiches_BgActivity
	    } catch (InterruptedException e) {
	    	Log.d(LOG_TAG, "Interrupted"+e.getMessage());
		}finally{
	    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	    	
	    	mNotificationHelper.completed();
	    }
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
		// Start of user code TelechargePhotosFiches onCancelled
		DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
        // termine de notifier les vues qui pouvaient être interressées
		DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		// End of user code
	}
	
	@Override
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code TelechargePhotosFiches onPostExecute
        // retire l'activité qui est maintenant finie
        DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
        // termine de notifier les vues qui pouvaient être interressées
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		// End of user code
    }

    // Start of user code additional operations TelechargePhotosFiches_BgActivity
	
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
  
    // recupération de la photo sur internet
    private int recupPhotoSurInternet(HashSet<PhotoATraiterOptim> inListePhotos, Outils.ImageType inImageType, int nbPhotoRetreived) {
    	
    	Iterator<PhotoATraiterOptim> itPhoto = inListePhotos.iterator();
    	while (itPhoto.hasNext()) {
    		PhotoATraiterOptim photoATraiter = itPhoto.next();
    		PhotoFiche photo = photoATraiter.getPhotoFiche();
	    	try{
	    		if( this.isCancelled()) return nbPhotoRetreived;
				Outils.getOrDownloadPhotoFile(context, photo, inImageType);
				Log.i(LOG_TAG, "image "+inImageType+" "+photo.getCleURL()+" téléchargée");
				// fait avancer la barre de la zone concernée // TODO pas trés optimal, mais fonctionne
				Outils.setDejaLaQteZoneGeo(context, photoATraiter.getIdZoneConcernee(), photoATraiter.imagePrincipale, 
						Outils.getDejaLaQteZoneGeo(context, photoATraiter.getIdZoneConcernee(), photoATraiter.imagePrincipale)+1);        		
        		
				nbPhotoRetreived = nbPhotoRetreived+1;;
		        // notify les listener toutes les 10 photos
		        if(((nbPhotoRetreived % 10) == 0) ){
		        	publishProgress(nbPhotoRetreived);
		        	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		        	// laisse un peu de temps pour l'UI 
			        Thread.sleep(tempo);
		    		// vérifie de temps en temps la connexion
		    		if(!isOnline()){
		            	Log.d(LOG_TAG, "pas connexion internet : Arret du téléchargement");
		            	break;
		            }
		    	}
		        
			} catch (InterruptedException e) {
				Log.i(LOG_TAG, e.getMessage(), e);
				Log.d(LOG_TAG, "InterruptedException recue : Arret du téléchargement");
				// c'est probablement l'application qui se ferme, supprimer la notification
				mNotificationHelper.completed();
				break;
		    } catch (IOException e) {
				Log.i(LOG_TAG, "Erreur de téléchargement de "+e.getMessage(), e);
				continue;
			}
    	}
    	return nbPhotoRetreived;
    }
	// End of user code
	
}
