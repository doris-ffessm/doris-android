package fr.ffessm.doris.android.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.async.NotificationHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.LimitTimer;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.disk.DiskEnvironment;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.util.Log;

/* 
 * TODO : Peut-être faudra-t-il le renommer car il fait maintenant :
 *     - les déplacements entre emplacements
 *     - la suppression de tous les fichiers d'un emplacement
 *     - la suppression des fichiers par Type d'Image
 */


public class GestionPhotoDiskService extends IntentService {


	private static final String LOG_TAG = GestionPhotoDiskService.class.getSimpleName();
	
	// premier argument = Action
	// deuxième argument = Source location ou Dossier à traiter
    // troisième = Destination
	
	// les différentes actions possibles pour cette activité
	public static String ACT_MOVE = "MOVE";
	public static String ACT_DELETE_DISK = "DELETE_DISK";
	public static String ACT_DELETE_FOLDER = "DELETE_FOLDER";
	
    public static String SRC_INTERNAL = "INTERNAL";
    public static String SRC_PRIMARY = "PRIMARY";
    public static String SRC_SECONDARY = "SECONDARY";
    
    public static String SRC_DOS_VIGNETTES = "Vignettes";
    public static String SRC_DOS_MEDRES = "MedRes";
    public static String SRC_DOS_HIRES = "HiRes";
    public static String SRC_DOS_AUTRES = "Autres";
	public static String SRC_DOS_CACHE = "Cache";
	
    public static String INTENT_ACTION = "fr.ffessm.doris.android.ACTION";
    public static String INTENT_SOURCE = "fr.ffessm.doris.android.SOURCE";
    public static String INTENT_TARGET = "fr.ffessm.doris.android.TARGET";
    
    
    private NotificationHelper mNotificationHelper;
    
    Param_Outils paramOutils;
	Disque_Outils disqueOutils;
	Photos_Outils photosOutils;
	
    // compteurs pour affichage dans les notifications et autre progress bar
    int nbFileToCopy=0;
    int nbcopiedFiles=0;
    
 // timer utilisé pour déclencher un refresh que toutes les x mili
    LimitTimer limitTimer = new LimitTimer(2000); //2000 Milliseconds
	
	public GestionPhotoDiskService() {
		super(GestionPhotoDiskService.class.getSimpleName());
		
		paramOutils = new Param_Outils(this);
		disqueOutils = new Disque_Outils(this);
		photosOutils = new Photos_Outils(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		// Récupère les paramètres depuis l'intent
		String action = intent.getStringExtra(INTENT_ACTION);
    	String source = intent.getStringExtra(INTENT_SOURCE);
    	String dest = intent.getStringExtra(INTENT_TARGET);
    	Log.d(LOG_TAG, "onHandleIntent() - action : "+action);
    	Log.d(LOG_TAG, "onHandleIntent() - source : "+source);
    	Log.d(LOG_TAG, "onHandleIntent() - dest : "+dest);
    	
    	
		// lance le job
    	// vérification des paramètres et calcul du nombre de fichier à copier
    	
   		//Log.d(LOG_TAG, "MovePhotoDiskService.onHandleIntent thread = "+Thread.currentThread());
		DorisApplicationContext.getInstance().isMovingPhotos = true;
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		
        String initialTickerText = "";
		String notificationTitle = "";
        if(action.equals(ACT_MOVE)){
			initialTickerText = this.getString(R.string.deplacephotos_bg_initialTickerText);
			notificationTitle = this.getString(R.string.deplacephotos_bg_notificationTitle);
        }
        if(action.equals(ACT_DELETE_FOLDER) || action.equals(ACT_DELETE_DISK)){
			initialTickerText = this.getString(R.string.suppressionphotos_bg_initialTickerText);
			notificationTitle = this.getString(R.string.suppressionphotos_bg_notificationTitle);
        }
		
		mNotificationHelper = new NotificationHelper(this, initialTickerText, notificationTitle, new Intent());
		mNotificationHelper.setNotificationID(2);
        mNotificationHelper.createNotification();
        
        
		// Arrêt des autres téléchargements si besoin
		// stop les téléchargements si besoin
    	if(DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null){
    		DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
    		mNotificationHelper.setContentTitle("Attente de fermeture d'autre taches");
    		mNotificationHelper.progressUpdate(0);
    		while(DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null 
    				&& DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.getStatus() == Status.RUNNING){
    			try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
    		}
    	}
    	
    	// baisse la priorité pour minimiser l'impact sur l'ihm et les risques de plantage
    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

    	mNotificationHelper.setContentTitle(initialTickerText);
    	

    	if(action.equals(ACT_MOVE) || action.equals(ACT_DELETE_DISK)){
	    	
	    	if(source.equals(SRC_INTERNAL)){
	    		nbFileToCopy = this.getDir(this.getString(R.string.folder_vignettes_fiches), Context.MODE_PRIVATE).list().length;
	    		nbFileToCopy += this.getDir(this.getString(R.string.folder_med_res_fiches), Context.MODE_PRIVATE).list().length;
	    		nbFileToCopy += this.getDir(this.getString(R.string.folder_hi_res_fiches), Context.MODE_PRIVATE).list().length;
	    		nbFileToCopy += this.getDir(this.getString(R.string.folder_portraits), Context.MODE_PRIVATE).list().length;
	    		nbFileToCopy += this.getDir(this.getString(R.string.folder_illustration_definitions), Context.MODE_PRIVATE).list().length;
	    		nbFileToCopy += this.getDir(this.getString(R.string.folder_illustration_biblio), Context.MODE_PRIVATE).list().length;
	    	}else if(source.equals(SRC_PRIMARY)){
	   			nbFileToCopy = disqueOutils.getPrimaryExternalStorageNbFiles( this.getString(R.string.folder_vignettes_fiches) );
	    		nbFileToCopy += disqueOutils.getPrimaryExternalStorageNbFiles( this.getString(R.string.folder_med_res_fiches) );
	    		nbFileToCopy += disqueOutils.getPrimaryExternalStorageNbFiles( this.getString(R.string.folder_hi_res_fiches) );
	    		nbFileToCopy += disqueOutils.getPrimaryExternalStorageNbFiles( this.getString(R.string.folder_portraits) );
	    		nbFileToCopy += disqueOutils.getPrimaryExternalStorageNbFiles( this.getString(R.string.folder_illustration_definitions) );
	    		nbFileToCopy += disqueOutils.getPrimaryExternalStorageNbFiles( this.getString(R.string.folder_illustration_biblio) );
	    	}else if(source.equals(SRC_SECONDARY)){
				nbFileToCopy = disqueOutils.getSecondaryExternalStorageNbFiles( this.getString(R.string.folder_vignettes_fiches) );
	    		nbFileToCopy += disqueOutils.getSecondaryExternalStorageNbFiles( this.getString(R.string.folder_med_res_fiches) );
	    		nbFileToCopy += disqueOutils.getSecondaryExternalStorageNbFiles( this.getString(R.string.folder_hi_res_fiches) );
	    		nbFileToCopy += disqueOutils.getSecondaryExternalStorageNbFiles( this.getString(R.string.folder_portraits) );
	    		nbFileToCopy += disqueOutils.getSecondaryExternalStorageNbFiles( this.getString(R.string.folder_illustration_definitions) );
	    		nbFileToCopy += disqueOutils.getSecondaryExternalStorageNbFiles( this.getString(R.string.folder_illustration_biblio) );
	    	}else {
	    		Log.e(LOG_TAG, "déplacement impossible, 1ier parametre incorrect : "+source);
	    		return;
	    	}
	    	
	    	Log.d(LOG_TAG, "onHandleIntent() - nbFileToCopy : "+nbFileToCopy);
	    	mNotificationHelper.setMaxItemToProcess(""+nbFileToCopy);
    	}

	
    	if(action.equals(ACT_DELETE_DISK)){
    		clearFolder(source, this.getString(R.string.folder_vignettes_fiches));
    		clearFolder(source, this.getString(R.string.folder_med_res_fiches));
    		clearFolder(source, this.getString(R.string.folder_hi_res_fiches));
    		clearFolder(source, this.getString(R.string.folder_portraits));
    		clearFolder(source, this.getString(R.string.folder_illustration_definitions));
    		clearFolder(source, this.getString(R.string.folder_illustration_biblio));
    		
    		// On remet à jour les compteurs de téléchargement, ils seront recalculés lors des
           	// prochain téléchargement
    		reset_nbphotos_recues();
    		
    		DorisApplicationContext.getInstance().isMovingPhotos = false;
            DorisApplicationContext.getInstance().notifyDataHasChanged(null);
            mNotificationHelper.completed();
    		return;
    	}
    	
    	if(action.equals(ACT_MOVE)){
	    	ImageLocation destImageLocation;
	    	if(dest.equals(SRC_INTERNAL)){
	    		destImageLocation = ImageLocation.APP_INTERNAL;
	    	}else if(dest.equals(SRC_PRIMARY)){
	    		destImageLocation = ImageLocation.PRIMARY;
	    	}else if(dest.equals(SRC_SECONDARY)){
	    		destImageLocation = ImageLocation.SECONDARY;
	    	}
	    	else {
	    		Log.e(LOG_TAG, "déplacement impossible, second parametre incorrect : "+dest);
	    		return;
	    	}
	    	
	    	// Enregistrement du mouvement qui va être réalisé, afin de pouvoir le reprendre
	    	// s'il était interrompu
	        new Photos_Outils(this).setPreferedLocation(destImageLocation);
	        new Param_Outils(this).setParamBoolean(R.string.pref_key_deplace_photo_encours, true);
	        
	    	// déplacement vignettes
	    	moveFolderContent(source, dest, this.getString(R.string.folder_vignettes_fiches));
	    	// déplacement med_res
	    	moveFolderContent(source, dest, this.getString(R.string.folder_med_res_fiches));
	    	// déplacement hi_res
	    	moveFolderContent(source, dest, this.getString(R.string.folder_hi_res_fiches));
	    	// déplacement photos participants
	    	moveFolderContent(source, dest, this.getString(R.string.folder_portraits));
	    	// déplacement photos glossaire
	    	moveFolderContent(source, dest, this.getString(R.string.folder_illustration_definitions));
	    	// déplacement biblio
	    	moveFolderContent(source, dest, this.getString(R.string.folder_illustration_biblio));
			// End of user code
	        
			// Start of user code end of task InitialisationApplication_BgActivity
	    	
	    	// Le traitement s'est terminé correctement
	    	new Param_Outils(this).setParamBoolean(R.string.pref_key_deplace_photo_encours, false);
	
	        DorisApplicationContext.getInstance().isMovingPhotos = false;
	        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	        mNotificationHelper.completed();
    	}
    	
    	
    	if(action.equals(ACT_DELETE_FOLDER)){

           	if (source.equals(SRC_DOS_VIGNETTES)){
           		disqueOutils.clearFolder(photosOutils.getImageFolderVignette(), 0);
           		
               	// On remet à jour les compteurs de téléchargement, ils seront recalculés lors des
               	// prochain téléchargement
               	reset_nbphotos_recues();
           		
           	} else if (source.equals(SRC_DOS_MEDRES)){
           		disqueOutils.clearFolder(photosOutils.getImageFolderMedRes(), 0);
           		
           		reset_nbphotos_recues();
           		
           	} else if (source.equals(SRC_DOS_HIRES)){
           		disqueOutils.clearFolder(photosOutils.getImageFolderHiRes(), 0);
           		
           		reset_nbphotos_recues();
           		
           	} else if (source.equals(SRC_DOS_AUTRES)){
           		disqueOutils.clearFolder(photosOutils.getImageFolderPortraits(), 0);
           		disqueOutils.clearFolder(photosOutils.getImageFolderGlossaire(), 0);
           		disqueOutils.clearFolder(photosOutils.getImageFolderBiblio(), 0);
           		
           	} else if (source.equals(SRC_DOS_CACHE)){
           		disqueOutils.clearFolder(this.getCacheDir(), 0);
           		
           	}
           	
           	DorisApplicationContext.getInstance().isMovingPhotos = false;
	        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
	        mNotificationHelper.completed();

    	}
	}

	
	protected void moveFolderContent(String source, String destination, String subFolderToMove){
    	File sourceFolder;
    	if(source.equals(SRC_PRIMARY)){
    		sourceFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, subFolderToMove);
    	}else if(source.equals(SRC_SECONDARY)){
    		try {
    			sourceFolder = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, subFolderToMove);
			} catch (NoSecondaryStorageException e) {
				return;
			}
    	}
    	else {
    		// considère internal par défaut
    		sourceFolder = this.getDir(subFolderToMove, Context.MODE_PRIVATE);
    	}
    	File destFolder;
    	if(destination.equals(SRC_PRIMARY)){
    		destFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, subFolderToMove);
    	}else if(destination.equals(SRC_SECONDARY)){
    		try {
    			destFolder = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, subFolderToMove);
			} catch (NoSecondaryStorageException e) {
				return;
			}
    	}
    	else {
    		destFolder = this.getDir(subFolderToMove, Context.MODE_PRIVATE);
    	}
    	
    	
    	try {
			moveDirectory(sourceFolder, destFolder);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Problem copying", e);
		}
    }
    
	byte[] buf = new byte[1024];
	
    public void moveDirectory(File sourceLocation , File targetLocation) throws IOException {

    	// incrémente le compteur et notifie tous les 10
    	nbcopiedFiles++;
    	//if(nbcopiedFiles % 10 == 0)	publishProgress(nbcopiedFiles);
    	if(limitTimer.hasTimerElapsed())	{
    		mNotificationHelper.progressUpdate(nbcopiedFiles);
    		DorisApplicationContext.getInstance().notifyDataHasChanged(null);	
    	} 
    	Thread.yield();
    	
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
	        //byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	    }
	    sourceLocation.delete();
	}
    
    protected void clearFolder(String source, String subFolderToMove){
    	File sourceFolder;
    	if(source.equals(SRC_PRIMARY)){
    		sourceFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, subFolderToMove);
    	}else if(source.equals(SRC_SECONDARY)){
    		try {
    			sourceFolder = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, subFolderToMove);
			} catch (NoSecondaryStorageException e) {
				return;
			}
    	}
    	else {
    		// considère internal par défaut
    		sourceFolder = this.getDir(subFolderToMove, Context.MODE_PRIVATE);
    	}
    	
    	clearFolder(sourceFolder);
    }
    
    public int clearFolder(File inFolder){
		int deletedFiles = 0;
	    if (inFolder!= null && inFolder.isDirectory()) {
	        try {
	            for (File child:inFolder.listFiles()) {

	                //first delete subdirectories recursively
	                if (child.isDirectory()) {
	                    deletedFiles += clearFolder(child);
	                }

	                //then delete the files and subdirectories in this dir
	                //only empty directories can be deleted, so subdirs have been done first
	                if (child.delete()) {
	                        deletedFiles++;
	                    }
	            }
	        }
	        catch(Exception e) {
	        	Log.e(LOG_TAG, String.format("Failed to clean the folder, error %s", e.getMessage()));
	        }
	    }
	    Log.d(LOG_TAG, "clearFolder() - Fichiers effacés : "+deletedFiles);
	    return deletedFiles;
	}
    
    
    public void reset_nbphotos_recues(){
    	
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE, true), 0);
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE, false), 0);
    	
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE, true), 0);
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE, false), 0);
    	
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE, true), 0);
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE, false), 0);
    	
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES, true), 0);
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES, false), 0);
    	
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST, true), 0);       	
    	paramOutils.setParamInt(photosOutils.getKeyDataRecuesZoneGeo(
    			ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST, false), 0); 

    }
    
}
