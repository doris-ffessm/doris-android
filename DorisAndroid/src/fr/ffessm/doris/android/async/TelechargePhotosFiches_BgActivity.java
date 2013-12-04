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
    
    protected DataChangedListener listener;
    
    
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

		
    public TelechargePhotosFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper, DataChangedListener listener){
    	String notificationTitle = context.getString(R.string.bg_notifTitle_initial);
    	String initialTickerText = context.getString(R.string.bg_notifText_initial);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle);
        this.dbHelper = dbHelper;
		this.context = context;
		this.listener = listener;
    }
    
    public void removeListener(DataChangedListener listener){
    	listener= null;
    }
    public void removeAllListeners(){
    	listener= null;
    }
    public void addListener(DataChangedListener listener){
    	this.listener= listener;
    }
	// End of user code
    
	/** constructor */
    public TelechargePhotosFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		// Start of user code additional attribute declarations TelechargePhotosFiches_BgActivity constructor
		String initialTickerText = context.getString(R.string.bg_notifText_initial);
		String notificationTitle = context.getString(R.string.bg_notifTitle_initial);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle);
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
    	if(listener != null ){
    		try{
    			listener.dataHasChanged(null);
    		}
    		catch(Exception e){
    			Log.d(LOG_TAG, "Listener n'est plus à l'écoute, arrét du téléchargement");
    			return nbPhotoRetreived;
    		}
    	}
    	DorisDBHelper dorisDBHelper = dbHelper.getDorisDBHelper();
    	
    	String notificationTitle = "";
    	String initialTickerText = "";
    	
    	//Test Temporaire permettant de conserver l'algo initial
    	// et d'en développer un plus avancé
    	if (PreferenceManager.getDefaultSharedPreferences(context).getString("debug_algo_sync", "A2").equals("A0") ) {
    	// --- Algo initial ---
			notificationTitle = context.getString(R.string.analysefiches00_bg_initialTickerText);
	        mNotificationHelper.setContentTitle(notificationTitle);
	        
	    	List<Fiche> listeFiches = dbHelper.getFicheDao().queryForAll();
	    	List<PhotoFiche> listePhotosATraiter = new ArrayList<PhotoFiche>();
	    	// en priorité toutes les photos principales (pour les vignettes)
	        if(!listeFiches.isEmpty()){
	        	for (Fiche fiche : listeFiches) {
	        		if( this.isCancelled()) return 0;
	        		fiche.setContextDB(dorisDBHelper);
	        		if( !Outils.isAvailableImagePrincipaleFiche(context, fiche)){
	        			PhotoFiche photoFiche = fiche.getPhotoPrincipale();
	        			if(photoFiche != null){
	            			photoFiche.setContextDB(dbHelper.getDorisDBHelper());
	        				listePhotosATraiter.add(photoFiche);
	        			}
	        		}
				}
	        }
	        // TODO puis les autres photos applicable aux filtres utilisateurs
	        
			// once done, you should indicates to the notificationHelper how many item will be processed
			mNotificationHelper.setMaxItemToProcess(""+listePhotosATraiter.size());
			// End of user code
    	
    	// Start of user code main loop of task TelechargePhotosFiches_BgActivity
			// This is where we would do the actual job
			// you should indicates the progression using publishProgress()
			Log.d(LOG_TAG, "nombre max de photo à télécharger : "+listePhotosATraiter.size());
	    	
	    	for (PhotoFiche photoFiche : listePhotosATraiter) {
	    		
	    		if( this.isCancelled()) return nbPhotoRetreived;
	    		// recupération de la photo sur internet
	    		try{
	    			Outils.getOrDownloadVignetteFile(context, photoFiche);
	    			Log.i(LOG_TAG, "vignette" +photoFiche.getCleURL()+" téléchargée");
	    			nbPhotoRetreived = nbPhotoRetreived+1;
	    			publishProgress(nbPhotoRetreived);
	    			// laisse un peu de temps entre chaque téléchargement 
	                Thread.sleep(10);
	                // notify les listener toutes les 10 photos
	                if(((nbPhotoRetreived % 10) == 0) && listener != null){
	                	try{
	            			listener.dataHasChanged(null);
	            		}
	            		catch(Exception e){
	            			Log.d(LOG_TAG, "Listener n'est plus à l'écoute, Arrét du téléchargement");
	            			return nbPhotoRetreived;
	            		}
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
	    		// DEBUG arret avant la fin
	    		if(nbPhotoRetreived > 10 && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_key_limit_download), false)) {
	    			Log.d(LOG_TAG, "DEBUG mode : nombre max de photo téléchargé : Arret du téléchargement");
	    			break;
	    		}
			}
    	}
    	// --- Fin Algo initial ---	
	    
		// --- Algo avec Zones et qualités ---
    	if (PreferenceManager.getDefaultSharedPreferences(context).getString("debug_algo_sync", "A2").equals("A1") ) {
    		//List<Fiche> listeFiches = dbHelper.getFicheDao().queryForAll();
    		
    		CloseableIterator<Fiche> itFiches = dbHelper.getFicheDao().closeableIterator();
    		
    		Log.d(LOG_TAG, "Debug - 010 - pour voir durée");
    		
    		mNotificationHelper.setMaxItemToProcess(""+dbHelper.getFicheDao().countOf());
    		
    		Collection<PhotoATraiter> collectPhotosPrincATraiter = new ArrayList<PhotoATraiter>();
	    	Collection<PhotoATraiter> collectPhotosATraiter = new ArrayList<PhotoATraiter>();
	    	List<ZoneGeographique> listeZoneGeo = dbHelper.getZoneGeographiqueDao().queryForAll();
	    	
	    	// zoneGeo : 1 - Faune et flore marines de France métropolitaine
	    	// zoneGeo : 2 - Faune et flore dulcicoles de France métropolitaine
	    	// zoneGeo : 3 - Faune et flore subaquatiques de l'Indo-Pacifique
	    	// zoneGeo : 4 - Faune et flore subaquatiques des Caraïbes
	    	// zoneGeo : 5 - Faune et flore subaquatiques de l'Atlantique Nord-Ouest

	    	Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
	    		Log.d(LOG_TAG, "zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
	    		if ( Outils.getPrecharModeZoneGeo(context, zoneGeo.getId()) == Outils.PrecharMode.P0 ) listeZoneGeo.remove(zoneGeo);
	    	}
	    	Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
	    	
	    	int nbFichesAnalysees = 0;
	    	int nbFichesdebug2 = 0;
	    	
	    	Log.d(LOG_TAG, "Debug - 110 - pour voir durée");
	    	
	    	
	    	// Si que des P0 pas la peine de travailler
	    	if (! Outils.isPrecharModeOnlyP0(context)) {
		    	// en priorité toutes les photos principales (pour les vignettes)
	    		while(itFiches.hasNext()){
	    			Fiche fiche = itFiches.next();
	        		if( this.isCancelled()) return 0;
	        		
	        		nbFichesAnalysees ++;
	        		if((nbFichesAnalysees % 10) == 0) {
	        			publishProgress(nbFichesAnalysees);
          			}
	        		
	        		// Debug
	        		if( PreferenceManager.getDefaultSharedPreferences(context).getBoolean("limit_download", false)
	        				&& nbFichesAnalysees >  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_max_card_number", "10") ) ) return 0;
	        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 210 - nbFichesAnalysees : "+nbFichesAnalysees);		
	        		fiche.setContextDB(dorisDBHelper);
	        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 211");
	        		PhotoFiche photoFichePrinc = fiche.getPhotoPrincipale();
	        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 212 - photoFichePrinc : "+photoFichePrinc.getCleURL());
	        		listeZoneGeo = fiche.getZonesGeographiques();
	        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 213 - listeZoneGeo : "+listeZoneGeo.size());
	        		// fin debug
	        		
        			if(photoFichePrinc != null){
        				listeZoneGeo = fiche.getZonesGeographiques();
        				Collection<Outils.ImageType> typesImagesARecuperer = new HashSet<Outils.ImageType>(3); // hashset pour n'ajouter les type qu'une seule fois;
        				// Temporaire : on télécharge toujours le format vignette afin d'accélérer l'affichage des listes
        				typesImagesARecuperer.add(Outils.ImageType.VIGNETTE);
        				// pas idéal mais considère la fiche pour une seule zone pour l'instant
        				int idZoneConcernee=-1;
        				for (ZoneGeographique zoneGeo : listeZoneGeo) {
        					Outils.ImageType imageType = Outils.getImageQualityToDownload(context, true, zoneGeo.getId());
        					if(imageType != null) {
        						typesImagesARecuperer.add(imageType);
        					}
        					idZoneConcernee = zoneGeo.getId();
        				}	
        				for(Outils.ImageType imageType :typesImagesARecuperer){
    						nbFichesdebug2 ++;
    						if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 220 - nbFichesdebug2 : "+nbFichesdebug2);
        					if (! Outils.isAvailableImagePhotoFiche(context, photoFichePrinc, imageType)) {
    							photoFichePrinc.setContextDB(dorisDBHelper);
    							collectPhotosPrincATraiter.add(new PhotoATraiter(photoFichePrinc, imageType, true, idZoneConcernee));
    						}
        				}
	        		}
              		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 230 - listePhotosPrincATraiter : "+collectPhotosPrincATraiter.size());
             	   
        	    	// Si que des P0 et P1 pas de téléchargement de photos non principales, on passe donc
        	    	if (! Outils.isPrecharModeOnlyP0orP1(context)) {
    	        		Collection<PhotoFiche> listePhotosFiche = fiche.getPhotosFiche();
    	        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 240 - listePhotosFiche : "+listePhotosFiche.size());

        	    		for (PhotoFiche photoFiche : listePhotosFiche) {
	        				if (photoFiche != photoFichePrinc) {
		        				for (ZoneGeographique zoneGeo : listeZoneGeo) {
		        					Outils.ImageType imageType = Outils.getImageQualityToDownload(context, false, zoneGeo.getId());
		        					if(imageType != null) {
		        						if (! Outils.isAvailableImagePhotoFiche(context, photoFiche, imageType)) {
				        					photoFiche.setContextDB(dorisDBHelper);
				        					collectPhotosATraiter.add(new PhotoATraiter(photoFiche, imageType, false, zoneGeo.getId()));
		        						}
		        					}
		        				}
	        				}
	        			}
        	    	}
        	    	
              		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 250 - listePhotosATraiter : "+collectPhotosATraiter.size());
              		 
				}
	    	} // Fin !isPrecharModeOnlyP0
	    	
			// once done, you should indicates to the notificationHelper how many item will be processed
			initialTickerText = context.getString(R.string.telechargephotosfiches01_bg_initialTickerText);
			notificationTitle = context.getString(R.string.telechargephotosfiches01_bg_notificationTitle);
	        mNotificationHelper.setContentTitle(notificationTitle);
	    	int nbPhotosATraiter = collectPhotosPrincATraiter.size()+collectPhotosATraiter.size();
	        mNotificationHelper.setMaxItemToProcess(""+nbPhotosATraiter);
			Log.d(LOG_TAG, "nombre max de photo à télécharger : "+nbPhotosATraiter);

			// On commence par les principales
			nbPhotoRetreived = recupPhotoSurInternet(collectPhotosPrincATraiter, 0);
			Log.d(LOG_TAG, "Debug - 800 - nbPhotoRetreived : "+nbPhotoRetreived);
			
			// Puis toutes les autres
			nbPhotoRetreived += recupPhotoSurInternet(collectPhotosATraiter, 0);

			Log.d(LOG_TAG, "Debug - 900 - nbPhotoRetreived : "+nbPhotoRetreived);
			Log.d(LOG_TAG, "Debug - 910 - pour voir durée");
			
    		if( this.isCancelled()) return nbPhotoRetreived;
    	}
		// --- Fin Algo avec Zones et qualités ---
    	
    	
    	// --- Algo avec Zones et qualités Optimisé ---
    	if (PreferenceManager.getDefaultSharedPreferences(context).getString("debug_algo_sync", "A2").equals("A2") ) {
    		
    		// Question pour Didier
    		List<Fiches_ZonesGeographiques> listeAssoc1= dbHelper.getFiches_ZonesGeographiquesDao().queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, "1");
    		Log.d(LOG_TAG, "Debug - 301 - listeAssoc1.size() : "+listeAssoc1.size());
    		List<Fiches_ZonesGeographiques> listeAssoc2= dbHelper.getFiches_ZonesGeographiquesDao().queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, "1");
    		Log.d(LOG_TAG, "Debug - 302 - listeAssoc2.size() : "+listeAssoc2.size());
    				
    		HashSet<Fiches_ZonesGeographiques> testHashSet = new HashSet<Fiches_ZonesGeographiques>(listeAssoc1);
    		Log.d(LOG_TAG, "Debug - 341 - testHashSet.size() : "+testHashSet.size());
    		testHashSet.addAll(listeAssoc2);
    		Log.d(LOG_TAG, "Debug - 342 - testHashSet.size() : "+testHashSet.size());
    		// Comment on fait pour ne pas avoir de doublon ?
    		// J'ai essayé de modifier : Fiches_ZonesGeographiques
    		// mais il ne repère pas les doublons tout seul (j'aurai voulu que ce soit magique :-)
    		// Fin Question pour Didier
    		
	
    		// Si que des P0 pas la peine de travailler
	    	if (! Outils.isPrecharModeOnlyP0(context)) {
	    		
	    		Log.d(LOG_TAG, "Debug - 010 - pour voir durée");
	    		
	    		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor prefEdit = preferences.edit(); 
                
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
		        		
		        		if(listeFichesZone !=  null)	for (Fiches_ZonesGeographiques fiche_ZonesGeographiques : listeFichesZone) {
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
	        				} // fin Fiches par Zone
		        		}
		    			//Enregistrement du nombre total de photos à télécharger pour afficher avancement
		        		Outils.setParamInt(context, Outils.getKeyDataAPrecharZoneGeo(context, zoneGeo.getId(), true), nbPhotosPrincATelechargerPourZone);
		        		Outils.setParamInt(context, Outils.getKeyDataDejaLaZoneGeo(context, zoneGeo.getId(), true), nbPhotosPrincDejaLaPourZone);
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
		        		
		        		if(listeFichesZone !=  null)	for (Fiches_ZonesGeographiques fiche_ZonesGeographiques : listeFichesZone) {
		        			
		        			compteurAvancement ++;
		        			if( (compteurAvancement % 10) == 0) publishProgress( compteurAvancement );
		        			
		        			idFiche = Integer.valueOf(fiche_ZonesGeographiques.getFiche().getId());
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


     	}
		// --- Fin Algo avec Zones et qualités Optimisé ---
    	
    		
		// End of user code
        
		// Start of user code end of task TelechargePhotosFiches_BgActivity
    }finally{
    	if(listener != null ){
    		try{
    			listener.dataHasChanged(null);
    		}
    		catch(Exception e){
    			Log.d(LOG_TAG, "Listener n'est plus à l'écoute, arrét du téléchargement");
    		}
    	}
    	
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
        DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code TelechargePhotosFiches onPostExecute
        // retire l'activité qui est maintenant finie
        DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = null;
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
    private int recupPhotoSurInternet(Collection<PhotoATraiter> inListePhotosATraiter, int nbPhotoRetreived) {

    	for (PhotoATraiter photoATraiter : inListePhotosATraiter) {
	    	try{
	    		PhotoFiche photo = dbHelper.getPhotoFicheDao().queryForId(photoATraiter.id);
				Outils.getOrDownloadPhotoFile(context, photo, photoATraiter.imageType);
				Log.i(LOG_TAG, "image "+photoATraiter.imageType+" "+photo.getCleURL()+" téléchargée");
				
				nbPhotoRetreived = nbPhotoRetreived+1;
				if( (nbPhotoRetreived % 10) == 0) publishProgress(nbPhotoRetreived);
				// laisse un peu de temps entre chaque téléchargement 
		        Thread.sleep(10);
		        // notify les listener toutes les 10 photos
		        if(((nbPhotoRetreived % 10) == 0) && listener != null){
		        	try{
		    			listener.dataHasChanged(null);
		    		}
		    		catch(Exception e){
		    			Log.d(LOG_TAG, "Listener n'est plus à l'écoute, Arrét du téléchargement");
		    			return nbPhotoRetreived;
		    		}
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
        		
				nbPhotoRetreived = nbPhotoRetreived+1;
				if( (nbPhotoRetreived % 10) == 0) publishProgress(nbPhotoRetreived);
				// laisse un peu de temps entre chaque téléchargement 
		        Thread.sleep(10);
		        // notify les listener toutes les 10 photos
		        if(((nbPhotoRetreived % 10) == 0) && listener != null){
		        	try{
		    			listener.dataHasChanged(null);
		    		}
		    		catch(Exception e){
		    			Log.d(LOG_TAG, "Listener n'est plus à l'écoute, Arrét du téléchargement");
		    			return nbPhotoRetreived;
		    		}
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
