package fr.ffessm.doris.android.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.async.DeplacePhotos_BgActivity;
import fr.ffessm.doris.android.async.NotificationHelper;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.disk.DiskEnvironment;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.util.Log;

public class MovePhotoDiskService extends IntentService {


	private static final String LOG_TAG = MovePhotoDiskService.class.getSimpleName();
	// les differentes actions possibles pour cette activité
    // premier argument = source location
    // second argument = destination ou delete action
    public static String INTERNAL = "INTERNAL";
    public static String PRIMARY = "PRIMARY";
    public static String SECONDARY = "SECONDARY";
    public static String DELETE = "DELETE";
    
    public static String SOURCE_DISK = "fr.ffessm.doris.android.SOURCE_DISK";
    public static String TARGET_DISK = "fr.ffessm.doris.android.TARGET_DISK";
    
    
    private NotificationHelper mNotificationHelper;
    
    // compteurs pour affichage dans les notifications et autre progress bar
    int nbFileToCopy=0;
    int nbcopiedFiles=0;
    
    // timer utilisé pour déclencher un refresh que toutes les x nano
    long notifyUITimer = System.nanoTime();
    static long NOTIFY_UI_TIMER_LENGTH = 1000000*1000; //1000 miliseconds 
	
	public MovePhotoDiskService() {
		super(MovePhotoDiskService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		//Log.d(LOG_TAG, "MovePhotoDiskService.onHandleIntent thread = "+Thread.currentThread());
		DorisApplicationContext.getInstance().isMovingPhotos = true;
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
		
		String initialTickerText = this.getString(R.string.deplacephotos_bg_initialTickerText);
		String notificationTitle = this.getString(R.string.deplacephotos_bg_notificationTitle);
		mNotificationHelper = new NotificationHelper(this, initialTickerText, notificationTitle, new Intent());
		mNotificationHelper.setNotificationID(2);
        mNotificationHelper.createNotification();
		// arrét des autres téléchargements si besoin
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
    	mNotificationHelper.setContentTitle(this.getString(R.string.deplacephotos_bg_initialTickerText));
		
		
		// Récupère les paramètres depuis l'intent
    	String source = intent.getStringExtra(SOURCE_DISK);
    	String dest = intent.getStringExtra(TARGET_DISK);
		// lance le job
    	// vérification des paramètres et calcul du nombre de fichier à copier
    	if(source.equals(INTERNAL)){
    		nbFileToCopy = this.getDir(Photos_Outils.VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += this.getDir(Photos_Outils.MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += this.getDir(Photos_Outils.HI_RES_FICHE_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += this.getDir(Photos_Outils.PORTRAITS_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += this.getDir(Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER, Context.MODE_PRIVATE).list().length;
    		nbFileToCopy += this.getDir(Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER, Context.MODE_PRIVATE).list().length;
    	}else if(source.equals(PRIMARY)){
    		nbFileToCopy = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, Photos_Outils.VIGNETTES_FICHE_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, Photos_Outils.MED_RES_FICHE_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, Photos_Outils.HI_RES_FICHE_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, Photos_Outils.PORTRAITS_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER).list().length;
    		nbFileToCopy += DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER).list().length;
    	}else if(source.equals(SECONDARY)){
    		try {
				nbFileToCopy = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, Photos_Outils.VIGNETTES_FICHE_FOLDER).list().length;			
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, Photos_Outils.MED_RES_FICHE_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, Photos_Outils.HI_RES_FICHE_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, Photos_Outils.PORTRAITS_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, Photos_Outils.ILLUSTRATION_DEFINITION_FOLDER).list().length;
	    		nbFileToCopy += DiskEnvironment.getSecondaryExternalStorage().getFilesDir(this, Photos_Outils.ILLUSTRATION_BIBLIO_FOLDER).list().length;
    		} catch (NoSecondaryStorageException e) {
    			Log.e(LOG_TAG, "déplacement impossible, pas de stockage secondaire");
        		return;
			}
    	}else {
    		Log.e(LOG_TAG, "déplacement impossible, 1ier parametre incorrect : "+source);
    		return;
    	}
    	mNotificationHelper.setMaxItemToProcess(""+nbFileToCopy);
    	
    	// si beaucoup de travail, alors baisse la priorité pour minimiser l'impact sur l'ihm et les risques de plantage
    	if(nbFileToCopy>2000)
    		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

    	
    	
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
    		return;
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
        new Photos_Outils(this).setPreferedLocation(destImageLocation);

        DorisApplicationContext.getInstance().isMovingPhotos = false;
        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
        mNotificationHelper.completed();
    	
	}

	
	protected void moveFolderContent(String source, String destination, String subFolderToMove){
    	File sourceFolder;
    	if(source.equals(PRIMARY)){
    		sourceFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, subFolderToMove);
    	}else if(source.equals(SECONDARY)){
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
    	if(destination.equals(PRIMARY)){
    		destFolder = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(this, subFolderToMove);
    	}else if(destination.equals(SECONDARY)){
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
    
	byte[] buf = new byte[1024*10];
	
    public void moveDirectory(File sourceLocation , File targetLocation) throws IOException {

    	// incrémente le compteur et notifie tous les 10
    	nbcopiedFiles++;
    	//if(nbcopiedFiles % 10 == 0)	publishProgress(nbcopiedFiles);
    	if(hasNotifyUITimerElapsed())	{
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
    
    public boolean hasNotifyUITimerElapsed(){
    	long currentTime = System.nanoTime();
    	if(currentTime - notifyUITimer > NOTIFY_UI_TIMER_LENGTH) {
    		notifyUITimer = currentTime;
    		return true;
    	}
    	return false;
    }
    
}
