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

// Start of user code additional imports VerifieMAJFiche_BgActivity

import java.sql.SQLException;
import java.util.ArrayList;
import fr.ffessm.doris.android.activities.EtatModeHorsLigne_CustomViewActivity;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.DataBase_Outils;
import fr.ffessm.doris.android.sitedoris.Constants.FileHtmlKind;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.DorisApplicationContext;
// End of user code

public class VerifieMAJFiche_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = VerifieMAJFiche_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;    
    private Context context;
    
    // Start of user code additional attribute declarations VerifieMAJFiche_BgActivity
    private Reseau_Outils reseauOutils;
	// End of user code
    
	/** constructor */
    public VerifieMAJFiche_BgActivity(Context context){
		this.dbHelper = OpenHelperManager.getHelper(context.getApplicationContext(), OrmLiteDBHelper.class);
		// use application wide helper
        this.context = context.getApplicationContext();
		// Start of user code additional attribute declarations VerifieMAJFiche_BgActivity constructor
    	reseauOutils = new Reseau_Outils(context);
    	
    	String initialTickerText = context.getString(R.string.bg_notifText_fichesInitial);
		String notificationTitle = context.getString(R.string.bg_notifTitle_fichesInitial);
        
		mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
		
        // End of user code
        
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected Integer doInBackground(String... arg0) {
    	

		// Start of user code initialization of the task VerifieMAJFiche_BgActivity
		// do the initializatio of the task here
		// once done, you should indicates to the notificationHelper how many item will be processed

    	int numeroFiche = Integer.valueOf(arg0[0]);
    	
    	mNotificationHelper.setMaxItemToProcess(""+1);
    	publishProgress(1);
		// End of user code
    	
    	// Start of user code main loop of task VerifieMAJFiche_BgActivity
		// This is where we would do the actual job

    	//Log.d(LOG_TAG, "doInBackground() - numeroFiche : "+numeroFiche);
    	
    	// Récupération Fiche de la Base
    	Fiche ficheDeLaBase = (new DataBase_Outils(dbHelper.getDorisDBHelper())).queryFicheByNumeroFiche(numeroFiche);
		ficheDeLaBase.setContextDB(dbHelper.getDorisDBHelper());
		Log.d(LOG_TAG, "doInBackground() - Fiche de la Base : "+ficheDeLaBase.getEtatFiche()+" - "
				+ ficheDeLaBase.getNomCommunNeverEmpty() + " - " + ficheDeLaBase.getDateCreation()
				+ " - " + ficheDeLaBase.getDateModification());
		
		// Récupération Fiche du Site
    	String urlFiche =  Constants.getFicheFromIdUrl( numeroFiche );
    	//Log.d(LOG_TAG, "doInBackground() - urlFiche : "+urlFiche);
    	
    	String contenuFichierHtml = "";
    	try {
    		contenuFichierHtml = reseauOutils.getHtml(urlFiche, FileHtmlKind.FICHE);
		} catch (IOException e) {
			Log.w(LOG_TAG, e.getMessage(), e);
		}   
    	
    	Fiche ficheSite = new Fiche();
    	ficheSite.setContextDB(dbHelper.getDorisDBHelper());
    	
		ficheSite.getFicheEtatDateModifFromHtml(contenuFichierHtml);
		//Log.d(LOG_TAG, "doInBackground() - Fiche du Site : "+ficheSite.getEtatFiche()+" - "
		//		+ ficheSite.getDateModification());
		
		//Si le statut a changé ou que la date de mise à jour a évolué, on continue
		if ( ficheSite.getEtatFiche() != ficheDeLaBase.getEtatFiche()
			|| !ficheSite.getDateModification().equals(ficheDeLaBase.getDateModification()) ) {

	    	List<Groupe> listeGroupes = new ArrayList<Groupe>(0);
	    	listeGroupes.addAll(dbHelper.getGroupeDao().queryForAll());
			//Log.d(LOG_TAG, "doInBackground() - listeGroupes.size : "+listeGroupes.size());
			
	    	List<Participant> listeParticipants = new ArrayList<Participant>(0);
			listeParticipants.addAll(dbHelper.getParticipantDao().queryForAll());
			//Log.d(LOG_TAG, "doInBackground() - listeParticipants.size : "+listeParticipants.size());
			
			try {
				// Nécessaire pour que la mise à jour suivante fonctionne
				// (système hérité du passé et surtout fonctionnant dans le prefetch => GMo : je garde)
				ficheDeLaBase.setEtatFiche(ficheSite.getEtatFiche());
				
				dbHelper.getDorisDBHelper().ficheDao.update(
						ficheDeLaBase
					);
				
				// Mise à jour réelle de la Fiche
				ficheDeLaBase.getFicheFromHtml(contenuFichierHtml, listeGroupes, listeParticipants);
				//Log.d(LOG_TAG, "doInBackground() - Fiche : "+ficheDeLaBase.getNomCommun());

				dbHelper.getDorisDBHelper().ficheDao.update(
						ficheDeLaBase
					);
	
			} catch (SQLException e) {
				Log.w(LOG_TAG, e.getMessage(), e);
			}
		
		}
    	
	

		// End of user code
        
		// Start of user code end of task VerifieMAJFiche_BgActivity
		// return the number of item processed
        return 1;
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
		// Start of user code VerifieMAJFiche onCancelled
		// End of user code
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code VerifieMAJFiche onPostExecute

        if(verifieMAJFiches_BgActivity != null){
        	verifieMAJFiches_BgActivity.dataHasChanged("Fiche mise à jour depuis le site Doris");
        	//Toast.makeText(accueil_CustomViewActivity.getContext(), "Base initialisée avec les données prédéfinies", Toast.LENGTH_LONG).show();
        }
        
		DorisApplicationContext.getInstance().notifyDataHasChanged(null);

        // End of user code
    }

    // Start of user code additional operations VerifieMAJFiche_BgActivity
    private DataChangedListener verifieMAJFiches_BgActivity = null;
	// End of user code
	
}