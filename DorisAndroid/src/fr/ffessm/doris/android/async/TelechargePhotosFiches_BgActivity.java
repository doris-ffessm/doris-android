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
// Start of user code additional imports
import java.util.ArrayList;

import android.preference.PreferenceManager;

import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.xml.XMLHelper;
import fr.ffessm.doris.android.tools.Outils;
// End of user code

public class TelechargePhotosFiches_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = TelechargePhotosFiches_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations
    
	// End of user code
    
	/** constructor */
    public TelechargePhotosFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		String initialTickerText = context.getString(R.string.telechargephotosfiches_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.telechargephotosfiches_bg_notificationTitle);
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
    	

		// Start of user code initialization of the task
		// do the initialization of the task here
    	
    	int nbPhotoToRetreive = 0;
    	List<Fiche> listeFiches = dbHelper.getFicheDao().queryForAll();
    	List<PhotoFiche> listePhotosATraiter = new ArrayList<PhotoFiche>();
    	// en priorité toutes les photos principales (pour les vignettes)
        if(!listeFiches.isEmpty()){
        	for (Fiche fiche : listeFiches) {
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
    	
    	// Start of user code main loop of task
		// This is where we would do the actual job
		// you should indicates the progression using publishProgress()
    	int nbPhotoRetreived = 0;
    	for (PhotoFiche photoFiche : listePhotosATraiter) {
    		
    		// recupération de la photo sur internet
    		try{
    			Outils.getVignetteFile(context, photoFiche);
    			Log.i(LOG_TAG, "vignette" +photoFiche.getCleURL()+" téléchargée");
    			nbPhotoRetreived = nbPhotoRetreived+1;
    			publishProgress(nbPhotoRetreived);
    			// laisse un peu de temps entre chaque téléchargement 
                Thread.sleep(10);
    		} catch (InterruptedException e) {
    			Log.i(LOG_TAG, e.getMessage(), e);
            } catch (IOException e) {
    			Log.i(LOG_TAG, "Error while downloading, stopping for this time. "+e.getMessage(), e);
    			break;
			}
    		// DEBUG arret avant la fin
    		if(nbPhotoRetreived > 10 && PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_id_limit_download), true)) {
    			Log.d(LOG_TAG, "DEBUG mode : nombre max de photo téléchargé : Arret du téléchargement");
    			break;
    		}
		}
		// End of user code
        
		// Start of user code end of task
		// return the number of item processed
        return nbPhotoRetreived;
		// End of user code
    }
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
    }

    
    // Start of user code additional operations
	
	// End of user code
    
	
}
