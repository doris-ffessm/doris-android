/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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
// Start of user code additional imports InitialisationApplication_BgActivity
import fr.ffessm.doris.android.datamodel.DataChangedListener;
// End of user code

public class InitialisationApplication_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = InitialisationApplication_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
    // Start of user code additional attribute declarations InitialisationApplication_BgActivity
    public InitialisationApplication_BgActivity(Context context, OrmLiteDBHelper dbHelper, DataChangedListener accueil_CustomViewActivity){
    	String initialTickerText = context.getString(R.string.initialisationapplication_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.initialisationapplication_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent());
        this.dbHelper = dbHelper;
		this.context = context;
		this.accueil_CustomViewActivity = accueil_CustomViewActivity;
		
    }
    // End of user code
    
	/** constructor */
    public InitialisationApplication_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		String initialTickerText = context.getString(R.string.initialisationapplication_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.initialisationapplication_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent());
        this.dbHelper = dbHelper;
		this.context = context;
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected Integer doInBackground(String... arg0) {
    	

		// Start of user code initialization of the task InitialisationApplication_BgActivity
		// do the initializatio of the task here
		// once done, you should indicates to the notificationHelper how many item will be processed
		//mNotificationHelper.setMaxNbPages(maxNbPages.toString());
		// End of user code
    	
    	// Start of user code main loop of task InitialisationApplication_BgActivity
		// This is where we would do the actual job
		// you should indicates the progression using publishProgress()
    //	XMLHelper.loadDBFromXMLFile(dbHelper.getDorisDBHelper(), context.getResources().openRawResource(R.raw.prefetched_db));
		
        publishProgress(1);
		// End of user code
        
		// Start of user code end of task InitialisationApplication_BgActivity
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
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code InitialisationApplication onPostExecute
        if(accueil_CustomViewActivity != null){
        	accueil_CustomViewActivity.dataHasChanged("Base initialisée avec les données prédéfinies");
        	//Toast.makeText(accueil_CustomViewActivity.getContext(), "Base initialisée avec les données prédéfinies", Toast.LENGTH_LONG).show();
        }
		// End of user code
    }

    // Start of user code additional operations InitialisationApplication_BgActivity

    private DataChangedListener accueil_CustomViewActivity = null;
	// End of user code
	
}
