/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2014 - FFESSM
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
// Start of user code additional imports InitialisationApplication_BgActivity
import android.widget.Toast;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.xml.XMLHelper;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageLocation;
// End of user code
import fr.ffessm.doris.android.tools.disk.DiskEnvironment;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;

public class DeplacePhotos_BgActivity  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = DeplacePhotos_BgActivity.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    private OrmLiteDBHelper dbHelper;
    private Context context;
    
   
    
    
    // Start of user code additional attribute declarations InitialisationApplication_BgActivity
    
    // les differentes actions possibles pour cette activité
    // premier argument = source location
    // second argument = destination ou delete action
    public static String INTERNAL = "INTERNAL";
    public static String PRIMARY = "PRIMARY";
    public static String SECONDARY = "SECONDARY";
    public static String DELETE = "DELETE";
    
    // compteurs pour affichage dans les notifications et autre progress bar
    int nbFileToCopy=0;
    int nbcopiedFiles=0;
    
    public DeplacePhotos_BgActivity(Context context, OrmLiteDBHelper dbHelper, DataChangedListener accueil_CustomViewActivity){
    	String initialTickerText = context.getString(R.string.deplacephotos_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.deplacephotos_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent());
        this.dbHelper = dbHelper;
		this.context = context;
	//	this.accueil_CustomViewActivity = accueil_CustomViewActivity;
		
    }
    // End of user code
    
	/** constructor */
    public DeplacePhotos_BgActivity(Context context, OrmLiteDBHelper dbHelper){
		String initialTickerText = context.getString(R.string.deplacephotos_bg_initialTickerText);
		String notificationTitle = context.getString(R.string.deplacephotos_bg_notificationTitle);
        mNotificationHelper = new NotificationHelper(context, initialTickerText, notificationTitle, new Intent());
        this.dbHelper = dbHelper;
		this.context = context;
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    /** premier argument = source location
     * second argument = destination ou delete action
     */
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
		
    	 
    	if(arg0.length != 2){
	    	Log.e(LOG_TAG, "déplacement impossible, nombre de  parametre incorrect : "+arg0);
	    	return 0;
    	}
    	String source = arg0[0];
    	String dest = arg0[1];
    	// vérification des paramètres et calcul du nombre de fichier à copier
    	if(source.equals(INTERNAL)){
    		nbFileToCopy = context.getDir(Photos_Outils.VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += context.getDir(Photos_Outils.MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += context.getDir(Photos_Outils.HI_RES_FICHE_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += context.getDir(Photos_Outils.PORTRAITS_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += context.getDir(Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += context.getDir(Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER, Context.MODE_PRIVATE).list().length;
    	}else if(source.equals(PRIMARY)){
    		nbFileToCopy = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, Photos_Outils.VIGNETTES_FICHE_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, Photos_Outils.MED_RES_FICHE_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, Photos_Outils.HI_RES_FICHE_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, Photos_Outils.PORTRAITS_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER).list().length;
    	}else if(source.equals(SECONDARY)){
    		try {
				nbFileToCopy = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, Photos_Outils.VIGNETTES_FICHE_FOLDER).list().length;			
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, Photos_Outils.MED_RES_FICHE_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, Photos_Outils.HI_RES_FICHE_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, Photos_Outils.PORTRAITS_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER).list().length;
    		} catch (NoSecondaryStorageException e) {
    			Log.e(LOG_TAG, "déplacement impossible, pas de stockage secondaire");
        		return 0;
			}
    	}else {
    		Log.e(LOG_TAG, "déplacement impossible, 1ier parametre incorrect : "+source);
    		return 0;
    	}
    	mNotificationHelper.setMaxItemToProcess(""+nbFileToCopy);
    	ImageLocation destImageLocation;
    	if(dest.equals(INTERNAL)){
    		destImageLocation = ImageLocation.APP_INTERNAL;
    	}else if(dest.equals(PRIMARY)){
    		destImageLocation = ImageLocation.PRIMARY;
    	}else if(dest.equals(SECONDARY)){
    		destImageLocation = ImageLocation.SECONDARY;
    	}
    	else {
    		Log.e(LOG_TAG, "déplacement impossible, second parametre incorrect : "+dest);
    		return 0;
    	}
    	
    	// déplacement vignettes
    	moveFolderContent(source, dest, Photos_Outils.VIGNETTES_FICHE_FOLDER);
    	// déplacement med_res
    	moveFolderContent(source, dest, Photos_Outils.MED_RES_FICHE_FOLDER);
    	// déplacement hi_res
    	moveFolderContent(source, dest, Photos_Outils.HI_RES_FICHE_FOLDER);
    	// déplacement photos participants
    	moveFolderContent(source, dest, Photos_Outils.PORTRAITS_FOLDER);
    	// déplacement photos glossaire
    	moveFolderContent(source, dest, Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER);
    	// déplacement biblio
    	moveFolderContent(source, dest, Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER);
		// End of user code
        
		// Start of user code end of task InitialisationApplication_BgActivity
        new Photos_Outils(context).setPreferedLocation(destImageLocation);
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		// return the number of item processed
        return nbFileToCopy;
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
		DorisApplicationContext.getInstance().deplacePhotos_BgActivity = null;
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		mNotificationHelper.completed();
	}
    protected void onPostExecute(Integer result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
		// Start of user code InitialisationApplication onPostExecute
		DorisApplicationContext.getInstance().deplacePhotos_BgActivity = null;
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
      //  if(accueil_CustomViewActivity != null){
      //  	accueil_CustomViewActivity.dataHasChanged("Base initialisée avec les données prédéfinies");
        	//Toast.makeText(accueil_CustomViewActivity.getContext(), "Base initialisée avec les données prédéfinies", Toast.LENGTH_LONG).show();
      //  }
		// End of user code
    }

    // Start of user code additional operations InitialisationApplication_BgActivity

    protected void moveFolderContent(String source, String destination, String subFolderToMove){
    	File sourceFolder;
    	if(source.equals(PRIMARY)){
    		sourceFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, subFolderToMove);
    	}else if(source.equals(SECONDARY)){
    		try {
    			sourceFolder = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, subFolderToMove);
			} catch (NoSecondaryStorageException e) {
				return;
			}
    	}
    	else {
    		// considère internal par défaut
    		sourceFolder = context.getDir(subFolderToMove, Context.MODE_PRIVATE);
    	}
    	File destFolder;
    	if(destination.equals(PRIMARY)){
    		destFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, subFolderToMove);
    	}else if(destination.equals(SECONDARY)){
    		try {
    			destFolder = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, subFolderToMove);
			} catch (NoSecondaryStorageException e) {
				return;
			}
    	}
    	else {
    		destFolder = context.getDir(subFolderToMove, Context.MODE_PRIVATE);
    	}
    	
    	
    	try {
			moveDirectory(sourceFolder, destFolder);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Problem copying", e);
		}
    }
    
    public void moveDirectory(File sourceLocation , File targetLocation) throws IOException {

    	// incrémente le compteur et notifie tous les 10
    	nbcopiedFiles++;
    	if(nbcopiedFiles % 10 == 0)	publishProgress(nbcopiedFiles);
    	
	    if (sourceLocation.isDirectory()) {
	        if (!targetLocation.exists() && !targetLocation.mkdirs()) {
	            throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
	        }

	        String[] children = sourceLocation.list();
	        for (int i=0; i<children.length; i++) {
	            moveDirectory(new File(sourceLocation, children[i]),
	                    new File(targetLocation, children[i]));
	        }
	    } else {

	        // make sure the directory we plan to store the recording in exists
	        File directory = targetLocation.getParentFile();
	        if (directory != null && !directory.exists() && !directory.mkdirs()) {
	            throw new IOException("Cannot create dir " + directory.getAbsolutePath());
	        }

	        InputStream in = new FileInputStream(sourceLocation);
	        OutputStream out = new FileOutputStream(targetLocation);

	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	    }
	    sourceLocation.delete();
	}
    
 //   private DataChangedListener accueil_CustomViewActivity = null;
	// End of user code
	
}
