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
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
// Start of user code additional imports TelechargePhotosFiches_BgActivity
import java.util.ArrayList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.xml.XMLHelper;
import fr.ffessm.doris.android.tools.Outils;
// End of user code
import fr.ffessm.doris.android.tools.Outils.ImageType;

public class TelechargePhotosFiches_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = TelechargePhotosFiches_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations TelechargePhotosFiches_BgActivity
    
    protected DataChangedListener listener;
    
	class PhotoATraiter
	{
		PhotoFiche photoATraiter;
		Outils.ImageType imageType;
		boolean imagePrincipale;
		
		public PhotoATraiter(){}

		public PhotoATraiter(PhotoFiche inPhotoFiche, ImageType inImageType, boolean inImagePrincipale) {
			photoATraiter = inPhotoFiche;
			imageType = inImageType;
			imagePrincipale = inImagePrincipale;
		}
	}
		
    public TelechargePhotosFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper, DataChangedListener listener){
		String initialTickerText = context.getString(R.string.analysefiches_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.analysefiches_bg_notificationTitle);
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
		String initialTickerText = context.getString(R.string.analysefiches_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.analysefiches_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle);
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
		// do the initialization of the task here
    	// Téléchargement en tache de fond de toutes les photos de toutes les fiches correspondants aux critères de l'utilisateur
    	if(!isOnline()){
        	Log.d(LOG_TAG, "pas connexion internet : annulation du téléchargement");
        	return 0;
        }
    	
    	
    	int nbPhotoRetreived = 0;
    	//Test Temporaire permettant de coserver l'algo initial
    	// et de dévelooper un plus avancé
    	if (! PreferenceManager.getDefaultSharedPreferences(context).getBoolean("debug_new_algo_sync", true) ) {
    	// --- Algo initial ---
    			
	    	List<Fiche> listeFiches = dbHelper.getFicheDao().queryForAll();
	    	List<PhotoFiche> listePhotosATraiter = new ArrayList<PhotoFiche>();
	    	// en priorité toutes les photos principales (pour les vignettes)
	        if(!listeFiches.isEmpty()){
	        	for (Fiche fiche : listeFiches) {
	        		if( this.isCancelled()) return 0;
	        		fiche.setContextDB(dbHelper.getDorisDBHelper());
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
	    		if(nbPhotoRetreived > 10 && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_id_limit_download), false)) {
	    			Log.d(LOG_TAG, "DEBUG mode : nombre max de photo téléchargé : Arret du téléchargement");
	    			break;
	    		}
			}
    	// --- Fin Algo initial ---	
    	} else {
		// --- Algo avec Zones et qualités ---
    		List<Fiche> listeFiches = dbHelper.getFicheDao().queryForAll();
    		
    		Log.d(LOG_TAG, "Debug - 010 - pour voir durée");
    		
    		mNotificationHelper.setMaxItemToProcess(""+listeFiches.size());
    		
    		Collection<PhotoATraiter> listePhotosPrincATraiter = new ArrayList<PhotoATraiter>();
	    	Collection<PhotoATraiter> listePhotosATraiter = new ArrayList<PhotoATraiter>();
	    	List<ZoneGeographique> listeZoneGeo = dbHelper.getZoneGeographiqueDao().queryForAll();
	    	
	    	// zoneGeo : 1 - Faune et flore marines de France métropolitaine
	    	// zoneGeo : 2 - Faune et flore dulcicoles de France métropolitaine
	    	// zoneGeo : 3 - Faune et flore subaquatiques de l'Indo-Pacifique
	    	// zoneGeo : 4 - Faune et flore subaquatiques des Caraïbes
	    	// zoneGeo : 5 - Faune et flore subaquatiques de l'Atlantique Nord-Ouest

	    	Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
	    	for (ZoneGeographique zoneGeo : listeZoneGeo) {
	    		Log.d(LOG_TAG, "zoneGeo : "+zoneGeo.getId() + " - " + zoneGeo.getNom());
	    		if ( Outils.getPrecharMode(context, zoneGeo) == Outils.PrecharMode.P0 ) listeZoneGeo.remove(zoneGeo);
	    	}
	    	Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
	    	
	    	int nbFichesAnalysees = 0;
	    	int nbFichesdebug2 = 0;
	    	
	    	Log.d(LOG_TAG, "Debug - 110 - pour voir durée");
	    	
	    	// Si que des P0 pas la peine de travailler
	    	if (! Outils.isPrecharModeOnlyP0(context)) {
		    	// en priorité toutes les photos principales (pour les vignettes)
		        if(!listeFiches.isEmpty()){
		        	for (Fiche fiche : listeFiches) {
		        		if( this.isCancelled()) return 0;
		        		
		        		nbFichesAnalysees ++;
		        		if((nbFichesAnalysees % 10) == 0) {
		        			publishProgress(nbFichesAnalysees);
	          			}
		        		
		        		// Debug
		        		if( PreferenceManager.getDefaultSharedPreferences(context).getBoolean("limit_download", false)
		        				&& nbFichesAnalysees >  Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_max_card_number", "10") ) ) return 0;
		        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 210 - nbFichesAnalysees : "+nbFichesAnalysees);		
		        		fiche.setContextDB(dbHelper.getDorisDBHelper());
		        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 211");
		        		PhotoFiche photoFichePrinc = fiche.getPhotoPrincipale();
		        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 212 - photoFichePrinc : "+photoFichePrinc.getCleURL());
		        		listeZoneGeo = fiche.getZonesGeographiques();
		        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 213 - listeZoneGeo : "+listeZoneGeo.size());
		        		Collection<PhotoFiche> listePhotosFiche = fiche.getPhotosFiche();
		        		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 214 - listePhotosFiche : "+listePhotosFiche.size());
		        		// fin debug
		        		
	        			if(photoFichePrinc != null){
	        				listeZoneGeo = fiche.getZonesGeographiques();
	        				for (ZoneGeographique zoneGeo : listeZoneGeo) {
	        					Outils.ImageType imageType = Outils.getImageQualityToDownload(context, true, zoneGeo);
	        					if(imageType != null) {
	        						nbFichesdebug2 ++;
	        						if((nbFichesdebug2 % 100) == 0) Log.d(LOG_TAG, "Debug - 220 - nbFichesdebug2 : "+nbFichesdebug2);
	        		        		
	        						if (! Outils.isAvailableImagePhotoFiche(context, photoFichePrinc, imageType)) {
	        							photoFichePrinc.setContextDB(dbHelper.getDorisDBHelper());
	        							listePhotosPrincATraiter.add(new PhotoATraiter(photoFichePrinc, imageType, true));
	        						}
	        					}
	        				}	
		        		}
	              		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 230 - listePhotosPrincATraiter : "+listePhotosPrincATraiter.size());
	             	   
	        	    	// Si que des P0 et P1 pas de téléchargement de photos non principales, on passe donc
	        	    	if (! Outils.isPrecharModeOnlyP0orP1(context)) {
		        			for (PhotoFiche photoFiche : listePhotosFiche) {
		        				if (photoFiche != photoFichePrinc) {
			        				for (ZoneGeographique zoneGeo : listeZoneGeo) {
			        					Outils.ImageType imageType = Outils.getImageQualityToDownload(context, false, zoneGeo);
			        					if(imageType != null) {
			        						if (! Outils.isAvailableImagePhotoFiche(context, photoFiche, imageType)) {
					        					photoFiche.setContextDB(dbHelper.getDorisDBHelper());
						        				listePhotosATraiter.add(new PhotoATraiter(photoFiche, imageType, false));
			        						}
			        					}
			        				}
		        				}
		        			}
	        	    	}
	        	    	
	              		if((nbFichesAnalysees % 100) == 0) Log.d(LOG_TAG, "Debug - 240 - listePhotosATraiter : "+listePhotosATraiter.size());
	              		 
					}
		        }
	    	} // Fin isPrecharModeOnlyP0
	    	
			// once done, you should indicates to the notificationHelper how many item will be processed
			String initialTickerText = context.getString(R.string.telechargephotosfiches_bg_initialTickerText);
			String notificationTitle = context.getString(R.string.telechargephotosfiches_bg_notificationTitle);
	        mNotificationHelper.setContentTitle(notificationTitle);
	    	int nbPhotosATraiter = listePhotosPrincATraiter.size()+listePhotosATraiter.size();
	        mNotificationHelper.setMaxItemToProcess(""+nbPhotosATraiter);
			// End of user code
	    	
	    	// Start of user code main loop of task TelechargePhotosFiches_BgActivity
			// This is where we would do the actual job
			// you should indicates the progression using publishProgress()
			Log.d(LOG_TAG, "nombre max de photo à télécharger : "+nbPhotosATraiter);

			// On commence par les principales
			nbPhotoRetreived = recupPhotoSurInternet(listePhotosPrincATraiter);
			Log.d(LOG_TAG, "Debug - 800 - nbPhotoRetreived : "+nbPhotoRetreived);
			
			// Puis toutes les autres
			nbPhotoRetreived += recupPhotoSurInternet(listePhotosATraiter);

			Log.d(LOG_TAG, "Debug - 900 - nbPhotoRetreived : "+nbPhotoRetreived);
			Log.d(LOG_TAG, "Debug - 910 - pour voir durée");
			
    		if( this.isCancelled()) return nbPhotoRetreived;
	    	
		// --- Fin Algo avec Zones et qualités ---
    	}
		// End of user code
        
		// Start of user code end of task TelechargePhotosFiches_BgActivity
    	if(listener != null && nbPhotoRetreived != 0){
    		try{
    			listener.dataHasChanged(null);
    		}
    		catch(Exception e){
    			Log.d(LOG_TAG, "Listener n'est plus à l'écoute, arrét du téléchargement");
    			return nbPhotoRetreived;
    		}
    	}
		// return the number of item processed
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
    private int recupPhotoSurInternet(Collection<PhotoATraiter> inListePhotosATraiter) {
    	int nbPhotoRetreived = 0;
		
    	for (PhotoATraiter photoATraiter : inListePhotosATraiter) {
	    	try{
				Outils.getOrDownloadFile(context, photoATraiter.photoATraiter, photoATraiter.imageType);
				Log.i(LOG_TAG, "image "+photoATraiter.imageType+" "+photoATraiter.photoATraiter.getCleURL()+" téléchargée");
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
    	}
    	return nbPhotoRetreived;
    }
    
	// End of user code
	
}
