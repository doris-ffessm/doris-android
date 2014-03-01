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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import fr.ffessm.doris.android.activities.EtatModeHorsLigne_CustomViewActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.OutilsBase;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
// Start of user code additional imports VerifieMAJFiches_BgActivity
// End of user code

public class VerifieMAJToutesFiches_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = VerifieMAJToutesFiches_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations VerifieMAJFiches_BgActivity
    
    private String typeMAJ;
    private int zoneGeo;
    private int goupe;
    private int sousGroupe;
    
    public VerifieMAJToutesFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper, DataChangedListener verifieMAJFiches_BgActivity){
		String initialTickerText = context.getString(R.string.initialisationapplication_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.initialisationapplication_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
        this.dbHelper = dbHelper;
		this.context = context;
		
		this.verifieMAJFiches_BgActivity = verifieMAJFiches_BgActivity;
    }
    
    
    
	// End of user code
    
	/** constructor */
    public VerifieMAJToutesFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		// Start of user code additional attribute declarations VerifieMAJFiches_BgActivity constructor
		String initialTickerText = context.getString(R.string.verifiemajfiches_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.verifiemajfiches_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
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
    	

		// Start of user code initialization of the task VerifieMAJFiches_BgActivity
		// do the initializatio of the task here
		// once done, you should indicates to the notificationHelper how many item will be processed

    	typeMAJ = arg0[0];
    	Log.d(LOG_TAG, "doInBackground() - typeMAJ : "+typeMAJ);
    	
    	if (typeMAJ.equals("Toutes")) {
    	} else if (typeMAJ.equals("New")) {
    	} else if (typeMAJ.equals("ZoneGeo")) {
    		zoneGeo = Integer.valueOf(arg0[1]);
    	} else if (typeMAJ.equals("Groupe")) {
    		goupe = Integer.valueOf(arg0[1]);
    		sousGroupe = Integer.valueOf(arg0[2]);
    	}

    	
    	mNotificationHelper.setMaxItemToProcess(""+100);
		// End of user code
    	
    	// Start of user code main loop of task VerifieMAJFiches_BgActivity
		// This is where we would do the actual job
    	
    	/* Si Type MAJ = Toutes : On met tout à jour, pour le wifi ...
    	 * ZoneGeo = on liste l'ensemble des fiches 
    	 */
    	
    	int numeroFiche = 9999;
    	// Récupération Fiche de la Base
    	OutilsBase outilsBase = new OutilsBase(dbHelper.getDorisDBHelper());
    	Fiche ficheDeLaBase = outilsBase.queryFicheByNumeroFiche(numeroFiche);
		ficheDeLaBase.setContextDB(dbHelper.getDorisDBHelper());
		Log.d(LOG_TAG, "doInBackground() - Fiche de la Base : "+ficheDeLaBase.getEtatFiche()+" - "
				+ ficheDeLaBase.getNomCommun() + " - " + ficheDeLaBase.getDateCreation()
				+ " - " + ficheDeLaBase.getDateModification());
		
		// Récupération Fiche du Site
    	String urlFiche =  Constants.getFicheFromIdUrl( numeroFiche );
    	Log.d(LOG_TAG, "doInBackground() - urlFiche : "+urlFiche);
    	
    	String fichierDansCache = "fiche-"+numeroFiche+".html";
    	Log.d(LOG_TAG, "doInBackground() - fichierDansCache : "+fichierDansCache);
    	
    	try {
    		Outils.getHtml(context, urlFiche, fichierDansCache);
		} catch (IOException e) {
			Log.w(LOG_TAG, e.getMessage(), e);
		}   
    	
    	String contenuFichierHtml = fr.ffessm.doris.android.sitedoris.Outils
			.getFichierTxtFromDisk(new File(context.getCacheDir()+"/"+fichierDansCache));
 
    	Fiche ficheSite = new Fiche();
		ficheSite.getFicheEtatDateModifFromHtml(contenuFichierHtml);
		Log.d(LOG_TAG, "doInBackground() - Fiche du Site : "+ficheSite.getEtatFiche()+" - "
				+ ficheSite.getDateModification());
		
		//Si le statut a changé ou que la date de mise à jour a évolué, on continue
		if ( ficheSite.getEtatFiche() != ficheDeLaBase.getEtatFiche()
			|| !ficheSite.getDateModification().equals(ficheDeLaBase.getDateModification()) ) {

	    	List<Groupe> listeGroupes = new ArrayList<Groupe>(0);
	    	listeGroupes.addAll(dbHelper.getGroupeDao().queryForAll());
			Log.d(LOG_TAG, "doInBackground() - listeGroupes.size : "+listeGroupes.size());
			
	    	List<Participant> listeParticipants = new ArrayList<Participant>(0);
			listeParticipants.addAll(dbHelper.getParticipantDao().queryForAll());
			Log.d(LOG_TAG, "doInBackground() - listeParticipants.size : "+listeParticipants.size());
	    	
			ficheSite.setContextDB(dbHelper.getDorisDBHelper());
			
			try {
				ficheSite.getFicheFromHtml(contenuFichierHtml, listeGroupes, listeParticipants);
				Log.d(LOG_TAG, "doInBackground() - Fiche : "+ficheSite.getNomCommun());

				ficheDeLaBase.updateFromFiche(ficheSite);
				
				dbHelper.getDorisDBHelper().ficheDao.update(
						ficheDeLaBase
					);

				
			} catch (SQLException e) {
				Log.w(LOG_TAG, e.getMessage(), e);
			}
		
		}
    	
    	
    	// TODO : à faire ensuite
		// you should indicates the progression using publishProgress()
		for (int i=10;i<=100;i += 10) {
            try {
				// simply sleep for one second
                Thread.sleep(100);
                publishProgress(i);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		
		
		
		
		// End of user code
        
		// Start of user code end of task VerifieMAJFiches_BgActivity

		// return the number of item processed
        return 100;
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
		// Start of user code VerifieMAJFiches onCancelled
		// End of user code
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code VerifieMAJFiches onPostExecute
        
        if(verifieMAJFiches_BgActivity != null){
        	verifieMAJFiches_BgActivity.dataHasChanged("Fiche mise à jour depuis le site Doris");
        	//Toast.makeText(accueil_CustomViewActivity.getContext(), "Base initialisée avec les données prédéfinies", Toast.LENGTH_LONG).show();
        }
        
		DorisApplicationContext.getInstance().notifyDataHasChanged(null);

		// End of user code
    }

    // Start of user code additional operations VerifieMAJFiches_BgActivity
    private DataChangedListener verifieMAJFiches_BgActivity = null;
	// End of user code
	
}
