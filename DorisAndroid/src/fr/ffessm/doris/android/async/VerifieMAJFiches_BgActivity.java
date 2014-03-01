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
// Start of user code additional imports VerifieMAJFiches_BgActivity

import com.j256.ormlite.dao.GenericRawResults;

import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.tools.Outils;

import java.io.File;
import java.util.HashSet;

// End of user code

public class VerifieMAJFiches_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = VerifieMAJFiches_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations VerifieMAJFiches_BgActivity
    
    
    
    
    
	// End of user code
    
	/** constructor */
    public VerifieMAJFiches_BgActivity(Context context, OrmLiteDBHelper dbHelper){
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
		//mNotificationHelper.setMaxNbPages(maxNbPages.toString());
		// End of user code
    	
    	// Start of user code main loop of task VerifieMAJFiches_BgActivity
		// This is where we would do the actual job
    	HashSet<FicheLight> listeFichesBase = new HashSet<FicheLight>(100);
    	try{
    		
	    	// Récupération de la liste des Fiches de la Base
	    	//OutilsBase outilsBase = new OutilsBase(dbHelper.getDorisDBHelper());
	    	listeFichesBase = new HashSet<FicheLight>((int) dbHelper.getDorisDBHelper().ficheDao.countOf());
	    	//ficheList = dbHelper.getDorisDBHelper().ficheDao.queryForAll();
	    	
	    	GenericRawResults<String[]> rawResults =
	    			dbHelper.getDorisDBHelper().ficheDao.queryRaw("SELECT _id, numeroFiche, etatFiche FROM fiche");
			for (String[] resultColumns : rawResults) {
			    String iDString = resultColumns[0];
			    String numeroFicheString = resultColumns[1];
			    String etatFicheString = resultColumns[2];
			    listeFichesBase.add(new FicheLight(
			    		Integer.parseInt(iDString),
			    		Integer.parseInt(numeroFicheString),
			    		Integer.parseInt(etatFicheString)) );
			}
			Log.d(LOG_TAG, "doInBackground() - Fiches de la Base : "+listeFichesBase.size() );
			
			
			// Récupération de la liste Fiches depuis le Site
	    	String urlListeFiches =  Constants.getListeFichesUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES));
	    	Log.d(LOG_TAG, "doInBackground() - urlFiche : "+urlListeFiches);
	    	
	    	String fichierDansCache = "listeFiches-10.html";
	    	Log.d(LOG_TAG, "doInBackground() - fichierDansCache : "+fichierDansCache);
	    	
	    	try {
	    		Outils.getHtml(context, urlListeFiches, fichierDansCache);
			} catch (IOException e) {
				Log.w(LOG_TAG, e.getMessage(), e);
			}   
	    	
	    	String contenuFichierHtml = fr.ffessm.doris.android.sitedoris.Outils
				.getFichierTxtFromDisk(new File(context.getCacheDir()+"/"+fichierDansCache));
	 
	    	HashSet<Fiche> listeFichesSite = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
	    	Log.d(LOG_TAG, "doInBackground() - Fiches de la Base : "+listeFichesSite.size() );
	    	
	    	for (Fiche ficheSite : listeFichesSite){
	    		FicheLight ficheLightSite = new FicheLight(
    				ficheSite.getId(),
    				ficheSite.getNumeroFiche(),
    				ficheSite.getEtatFiche());
	    		if (listeFichesBase.contains(ficheLightSite)){
	    			Log.d(LOG_TAG, "doInBackground() - Fiche inchangée : "+ficheSite.getNumeroFiche() );
	    		} else {
	    			Log.d(LOG_TAG, "doInBackground() - Fiche modifiée : "+ficheSite.getNumeroFiche() );
	    			
	    			
	    			
	    		}
	    		
	    		
	    	}
	    	
	    	
    	} catch(java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
    	}
		
    	
    	
    	
    	
    	
    	
    	
		// you should indicates the progression using publishProgress()
		for (int i=10;i<=100;i += 10)
            {
                try {
					// simply sleep for one second
                    Thread.sleep(1000);
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
		// End of user code
    }

    // Start of user code additional operations VerifieMAJFiches_BgActivity
	
    // Entête Fiche permettant d'avoir une emprunte mémoire minimum
    private class FicheLight {
    	int _id;
    	int numeroFiche;
    	int etatFiche;
    	FicheLight(int _id, int numeroFiche, int etatFiche){
    		this._id = _id;
    		this.numeroFiche = numeroFiche;
    		this.etatFiche = etatFiche;
    	}
    }
    
    
	// End of user code
	
}
