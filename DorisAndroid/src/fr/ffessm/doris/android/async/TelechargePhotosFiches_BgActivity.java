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
    int nbPhotoRetreived = 0;
    try{
		// do the initialization of the task here
    	// Téléchargement en tache de fond de toutes les photos de toutes les fiches correspondants aux critères de l'utilisateur
    	if(!isOnline()){
        	Log.d(LOG_TAG, "pas connexion internet : annulation du téléchargement");
        	return 0;
        }
    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
    	DorisDBHelper dorisDBHelper = dbHelper.getDorisDBHelper();
    	
    	String notificationTitle = "";
    	String initialTickerText = "";
 	
		// Si que des P0 pas la peine de travailler
    	if (! Outils.isPrecharModeOnlyP0(context)) {
    		
    		Log.d(LOG_TAG, "Debug - 010 - pour voir durée");
    		 
    		File fichierPhoto;
    		HashSet<Integer> ficheTraitee = new HashSet<Integer>(100);
    		Integer idFiche;
    		Outils.ImageType imageTypeImage;
    		int compteurAvancement = 0;
    		int nbPhotosATraiter = 0;
    		
    		List<ZoneGeographique> listeZoneGeo = dbHelper.getZoneGeographiqueDao().queryForAll();
	    	// zoneGeo : 1 - Faune et flore marines de France métropolitaine
	    	// zoneGeo : 2 - Faune et flore dulcicoles de France métropolitaine
	    	// zoneGeo : 3 - Faune et flore subaquatiques de l'Indo-Pacifique
	    	// zoneGeo : 4 - Faune et flore subaquatiques des Caraïbes
	    	// zoneGeo : 5 - Faune et flore subaquatiques de l'Atlantique Nord-Ouest
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
    		
	    	// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// On commence par les photos principales
    		
	    	PhotoFiche photoFichePrinc;
    		HashSet<PhotoATraiterOptim> hsImagesPrincVig = new HashSet<PhotoATraiterOptim>(100);
    		HashSet<PhotoATraiterOptim> hsImagesPrincMedRes = new HashSet<PhotoATraiterOptim>(100);
    		HashSet<PhotoATraiterOptim> hsImagesPrincHiRes = new HashSet<PhotoATraiterOptim>(100);

    		int nbPhotosPrincATelechargerPourZone;
    		int nbPhotosPrincDejaLaPourZone;
    		
    		// Affiche : Analyse Fiche et Nb de Zones à analyser
	        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle03_analysefiche_imagesprinc) );
	        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTitre03_analysefiche_imagesprinc) );
    		mNotificationHelper.setMaxItemToProcess(""+listeZoneGeo.size());
    	        		
    		HashSet<File> hsImagesVigAllreadyAvailable = Outils.getAllVignettesPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - VigAllreadyAvailable : "+hsImagesVigAllreadyAvailable.size() );
    		HashSet<File> hsImagesMedResAllreadyAvailable = Outils.getAllMedResPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - MedResAllreadyAvailable : "+hsImagesMedResAllreadyAvailable.size() );
    		HashSet<File> hsImagesHiResAllreadyAvailable = Outils.getAllHiResPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - HiResAllreadyAvailable : "+hsImagesHiResAllreadyAvailable.size() );

	        
	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
	    		
    			compteurAvancement ++;
    			publishProgress( compteurAvancement );
    			nbPhotosPrincATelechargerPourZone = 0;
	    		nbPhotosPrincDejaLaPourZone = 0;
	    		
	    		if ( Outils.getPrecharModeZoneGeo(context, zoneGeo.getId()) != Outils.PrecharMode.P0 ) {
	    			
	    			imageTypeImage = Outils.getImageQualityToDownload(context, true, zoneGeo.getId());
	    			
	    			List<Fiches_ZonesGeographiques> listeFichesZone 
	    				= dbHelper.getFiches_ZonesGeographiquesDao().queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, zoneGeo.getId());
	        		Log.d(LOG_TAG, "Debug - 301 - listeAssoc.size() : "+listeFichesZone.size());
	        		
	        		if(listeFichesZone !=  null)	{
	        			for (Fiches_ZonesGeographiques fiche_ZonesGeographiques : listeFichesZone) {
	        		
		        			idFiche = Integer.valueOf( fiche_ZonesGeographiques.getFiche().getId() );
		        			
		        			if ( !ficheTraitee.contains(idFiche) ) {
		        				
		        				ficheTraitee.add( idFiche );
		        				
			        			fiche_ZonesGeographiques.setContextDB(dbHelper.getDorisDBHelper());
			    				Fiche fiche = fiche_ZonesGeographiques.getFiche();
			    				
			    				photoFichePrinc = fiche.getPhotoPrincipale();
			        			if(photoFichePrinc != null){
			        				// Temporaire : on télécharge toujours le format vignette afin d'accélérer l'affichage des listes
			        				nbPhotosPrincATelechargerPourZone++;
			        				
			        				if ( !hsImagesPrincVig.contains(photoFichePrinc) ){
			        					// Vérification que pas déjà téléchargée
			        					fichierPhoto = new File(Outils.getImageFolderVignette(context), photoFichePrinc.getCleURL());
			        					if ( !hsImagesVigAllreadyAvailable.contains(fichierPhoto) ){
			        						hsImagesPrincVig.add(new PhotoATraiterOptim(photoFichePrinc, true, zoneGeo.getId()));
			        					}  else nbPhotosPrincDejaLaPourZone++;
			        				} else nbPhotosPrincDejaLaPourZone++;
			        				
			        				if ( imageTypeImage == Outils.ImageType.MED_RES) {
			        					nbPhotosPrincATelechargerPourZone++;
			        					if ( !hsImagesPrincMedRes.contains(photoFichePrinc) ){
			        						fichierPhoto = new File(Outils.getImageFolderMedRes(context), photoFichePrinc.getCleURL());
				        					if ( !hsImagesMedResAllreadyAvailable.contains(fichierPhoto) ){
				        						hsImagesPrincMedRes.add(new PhotoATraiterOptim(photoFichePrinc, true, zoneGeo.getId()));
				        					} else nbPhotosPrincDejaLaPourZone++;
				        				} else nbPhotosPrincDejaLaPourZone++;
			        				}
			        				if ( imageTypeImage == Outils.ImageType.HI_RES) {
			        					nbPhotosPrincATelechargerPourZone++;
			        					if ( !hsImagesPrincHiRes.contains(photoFichePrinc) ){
			        						fichierPhoto = new File(Outils.getImageFolderHiRes(context), photoFichePrinc.getCleURL());
				        					if ( !hsImagesHiResAllreadyAvailable.contains(fichierPhoto) ){
				        						hsImagesPrincHiRes.add(new PhotoATraiterOptim(photoFichePrinc, true, zoneGeo.getId()));
				        					} else nbPhotosPrincDejaLaPourZone++;
				        				} else nbPhotosPrincDejaLaPourZone++;
			        				}
			        			}
			        			if((hsImagesPrincHiRes.size()+hsImagesPrincMedRes.size()+hsImagesPrincVig.size()) % 200 ==0){
			        				// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
			        				Thread.sleep(2 * tempo); // wait for 100 milliseconds before running another loop
			        			}
	        				} // fin Fiches par Zone
	        			}
	        		}
	    			//Enregistrement du nombre total de photos à télécharger pour afficher avancement
	        		Outils.setParamInt(context, Outils.getKeyDataAPrecharZoneGeo(context, zoneGeo.getId(), true), nbPhotosPrincATelechargerPourZone);
	        		Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneGeo.getId(), true), nbPhotosPrincDejaLaPourZone);
	        		
	        		// tempo pour économiser le CPU
	        		Thread.sleep(tempo); // wait for 50 milliseconds before running another loop
	    		}
	    	} // fin ZoneGeo
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - ImagesPrincVig : "+hsImagesPrincVig.size() );
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - ImagesPrincMedRes : "+hsImagesPrincMedRes.size() );
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - ImagesPrincHiRes : "+hsImagesPrincHiRes.size() );
	    
	    	
			// Indication de l'avancement pour utilisateur
	        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle03_telecharge_imagesprinc) );
	        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTitre03_telecharge_imagesprinc) );
	    	nbPhotosATraiter = hsImagesPrincVig.size()+hsImagesPrincMedRes.size()+hsImagesPrincHiRes.size();
	        mNotificationHelper.setMaxItemToProcess(""+nbPhotosATraiter);
	        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "nombre max de photo à télécharger : "+nbPhotosATraiter);

			// On commence par les vignettes de photos principales
			if (hsImagesPrincVig.size() != 0){
				nbPhotoRetreived = recupPhotoSurInternet(hsImagesPrincVig, Outils.ImageType.VIGNETTE, 0);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nb vignettes récupérées : "+nbPhotoRetreived);
			}
			if (hsImagesPrincMedRes.size() != 0){
				nbPhotoRetreived = recupPhotoSurInternet(hsImagesPrincMedRes, Outils.ImageType.MED_RES, nbPhotoRetreived);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nb med res récupérées : "+nbPhotoRetreived);
			}
			if (hsImagesPrincHiRes.size() != 0){
				nbPhotoRetreived = recupPhotoSurInternet(hsImagesPrincHiRes, Outils.ImageType.HI_RES, nbPhotoRetreived);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nb hi res récupérées : "+nbPhotoRetreived);
			}
    		hsImagesPrincVig = null;
    		hsImagesPrincMedRes = null;
    		hsImagesPrincHiRes = null;
    		ficheTraitee.clear();

    		// -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
			// -- Puis toutes les autres (pas principales) --
    		HashSet<PhotoATraiterOptim> hsImagesVig = new HashSet<PhotoATraiterOptim>(100);
    		HashSet<PhotoATraiterOptim> hsImagesMedRes = new HashSet<PhotoATraiterOptim>(100);
    		HashSet<PhotoATraiterOptim> hsImagesHiRes = new HashSet<PhotoATraiterOptim>(100);
    		
    		int nbPhotosATelechargerPourZone;
    		int nbPhotosDejaLaPourZone;
    		
    		hsImagesVigAllreadyAvailable = Outils.getAllVignettesPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - VigAllreadyAvailable : "+hsImagesVigAllreadyAvailable.size() );
    		hsImagesMedResAllreadyAvailable = Outils.getAllMedResPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - MedResAllreadyAvailable : "+hsImagesMedResAllreadyAvailable.size() );
    		hsImagesHiResAllreadyAvailable = Outils.getAllHiResPhotoFicheAvailable(context);
    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - HiResAllreadyAvailable : "+hsImagesHiResAllreadyAvailable.size() );

	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
        		
	    		nbPhotosATelechargerPourZone = 0;
	    		nbPhotosDejaLaPourZone = 0;
	    		
	    		notificationTitle = context.getString(R.string.bg_notifTitle03_analysefiche_images)
	    				+" "+zoneGeo.getNom();
		        mNotificationHelper.setContentTitle(notificationTitle);
		        mNotificationHelper.setRacineTickerText(context.getString(R.string.bg_racineTitre03_analysefiche_images));
		        
	    		if ( Outils.getPrecharModeZoneGeo(context, zoneGeo.getId()) != Outils.PrecharMode.P0 
	    				&& Outils.getPrecharModeZoneGeo(context, zoneGeo.getId()) != Outils.PrecharMode.P1 ) {

	    			imageTypeImage = Outils.getImageQualityToDownload(context, false, zoneGeo.getId());
	    			
	    			List<Fiches_ZonesGeographiques> listeFichesZone 
	    				= dbHelper.getFiches_ZonesGeographiquesDao().queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, zoneGeo.getId());
	        		Log.d(LOG_TAG, "Debug - 301 - listeAssoc.size() : "+listeFichesZone.size());
			        
	        		mNotificationHelper.setMaxItemToProcess(""+listeFichesZone.size());
	        		compteurAvancement = 0;
	        		
	        		if(listeFichesZone !=  null)	{
	        			for (Fiches_ZonesGeographiques fiche_ZonesGeographiques : listeFichesZone) {
	        				compteurAvancement ++;
	        				if( (compteurAvancement % 10) == 0) publishProgress( compteurAvancement );

	        				idFiche = Integer.valueOf(fiche_ZonesGeographiques.getFiche().getId());
	        				// Les compteurs des photos à télécharger sont moins ergonomiques
	        				// mais ainsi on ne traite qu'une fois chaque fiche ce qui fait gagner bcq de temps de traitement
	        				if (!ficheTraitee.contains( idFiche ) ) {

	        					ficheTraitee.add( idFiche );

	        					fiche_ZonesGeographiques.setContextDB(dbHelper.getDorisDBHelper());
	        					Fiche fiche = fiche_ZonesGeographiques.getFiche();

	        					for (PhotoFiche photoFiche : fiche.getPhotosFiche()) {

	        						if(photoFiche != null){

	        							if ( imageTypeImage == Outils.ImageType.VIGNETTE ){
	        								nbPhotosATelechargerPourZone++;
	        								// Vérification que pas déjà téléchargée
	        								fichierPhoto = new File(Outils.getImageFolderVignette(context), photoFiche.getCleURL());
	        								if ( !hsImagesVigAllreadyAvailable.contains(fichierPhoto) ){
	        									hsImagesVig.add(new PhotoATraiterOptim(photoFiche, false, zoneGeo.getId()));
	        								} else nbPhotosDejaLaPourZone++;
	        							}
	        							if ( imageTypeImage == Outils.ImageType.MED_RES) {
	        								nbPhotosATelechargerPourZone++;
	        								if ( !hsImagesMedRes.contains(photoFiche) ){
	        									fichierPhoto = new File(Outils.getImageFolderMedRes(context), photoFiche.getCleURL());
	        									if ( !hsImagesMedResAllreadyAvailable.contains(fichierPhoto) ){
	        										hsImagesMedRes.add(new PhotoATraiterOptim(photoFiche, false, zoneGeo.getId()));
	        									} else nbPhotosDejaLaPourZone++;
	        								}
	        							}
	        							if ( imageTypeImage == Outils.ImageType.HI_RES) {
	        								nbPhotosATelechargerPourZone++;
	        								if ( !hsImagesHiRes.contains(photoFiche) ){
	        									fichierPhoto = new File(Outils.getImageFolderHiRes(context), photoFiche.getCleURL());
	        									if ( !hsImagesHiResAllreadyAvailable.contains(fichierPhoto) ){
	        										hsImagesHiRes.add(new PhotoATraiterOptim(photoFiche, false, zoneGeo.getId()));
	        									} else nbPhotosDejaLaPourZone++;
	        								}
	        							}
	        						}
	        						if((hsImagesHiRes.size() +hsImagesMedRes.size() +hsImagesVig.size()) % 200 ==0){
				        				// toutes les 200 images ajoutées fait une micro pause pour économiser le CPU pour l'UI
				        				Thread.sleep(2 * tempo); // wait for 100 milliseconds before running another loop
				        			}
	        					}
	        				}
	        			}
	        		}
	    				
	    		}
	    		
	    		//Enregistrement du nombre total de photos à télécharger pour afficher avancement
        		Outils.setParamInt(context, Outils.getKeyDataAPrecharZoneGeo(context, zoneGeo.getId(), false), nbPhotosATelechargerPourZone);
        		Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneGeo.getId(), false), nbPhotosDejaLaPourZone);
        		
	    	} // Fin Pour Chaque ZoneGeo
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - ImagesVig : "+hsImagesVig.size() );
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - ImagesMedRes : "+hsImagesMedRes.size() );
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - ImagesHiRes : "+hsImagesHiRes.size() );
	    
	    	
			// Indication de l'avancement pour utilisateur
	        mNotificationHelper.setContentTitle( context.getString(R.string.bg_notifTitle03_telecharge_images) );
	        mNotificationHelper.setRacineTickerText( context.getString(R.string.bg_racineTitre03_telecharge_images) );
	    	nbPhotosATraiter = hsImagesVig.size()+hsImagesMedRes.size()+hsImagesHiRes.size();
	        mNotificationHelper.setMaxItemToProcess(""+nbPhotosATraiter);
	        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "nombre max de photo à télécharger : "+nbPhotosATraiter);

			// On commence par les vignettes de photos principales
			if (hsImagesVig.size() != 0){
				nbPhotoRetreived = recupPhotoSurInternet(hsImagesVig, Outils.ImageType.VIGNETTE, 0);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nb vignettes récupérées : "+nbPhotoRetreived);
			}
			if (hsImagesMedRes.size() != 0){
				nbPhotoRetreived = recupPhotoSurInternet(hsImagesMedRes, Outils.ImageType.MED_RES, nbPhotoRetreived);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nb med res récupérées : "+nbPhotoRetreived);
			}
			if (hsImagesHiRes.size() != 0){
				nbPhotoRetreived = recupPhotoSurInternet(hsImagesHiRes, Outils.ImageType.HI_RES, nbPhotoRetreived);
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nb hi res récupérées : "+nbPhotoRetreived);
			}
			
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "doInBackground - nbPhotoRetreived : "+nbPhotoRetreived);
			Log.d(LOG_TAG, "Debug - 910 - pour voir durée");
			
    		if( this.isCancelled()) return nbPhotoRetreived;
    	} // Fin !isPrecharModeOnlyP0
    	
    		
		// End of user code
    	
    	// Start of user code main loop of task TelechargePhotosFiches_BgActivity
		// This is where we would do the actual job
		// you should indicates the progression using publishProgress()
		/*for (int i=10;i<=100;i += 10)
            {
                try {
					// simply sleep for one second
                    Thread.sleep(1000);
                    publishProgress(i);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
		// End of user code
        
		// Start of user code end of task TelechargePhotosFiches_BgActivity
    } catch (InterruptedException e) {
    	Log.d(LOG_TAG, "Interrupted"+e.getMessage());
	}finally{
    	DorisApplicationContext.getInstance().notifyDataHasChanged(null);
    	
    	mNotificationHelper.completed();
    }
    return nbPhotoRetreived;
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
