package fr.ffessm.doris.android.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;

public class Outils {
	private static final String LOG_TAG = Outils.class.getCanonicalName();
	public static final String VIGNETTES_FICHE_FOLDER = "vignettes_fiches";
	public static final String MED_RES_FICHE_FOLDER = "medium_res_images_fiches";
	public static final String HI_RES_FICHE_FOLDER = "hi_res_images_fiches";
	
	public enum ImageType {
	    VIGNETTE, MED_RES, HI_RES 
	} 
	
	public enum ConnectionType {
	    AUCUNE, WIFI, GSM 
	}
	
	public enum PrecharMode {
	    P0, P1, P2, P3, P4, P5, P6 
	}
	
	/**
	 * renvoie l'image principale actuellement disponible pour une fiche donnée, 
	 * renvoie null si aucune disponible
	 * @param inContext
	 * @param fiche
	 * @return
	 */
	public static Bitmap getAvailableImagePrincipaleFiche(Context inContext, Fiche fiche){
		
		Bitmap result = null;		
		File imageFolder = inContext.getDir(VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE);
		
		PhotoFiche imagePrincipale;
		imagePrincipale = fiche.getPhotoPrincipale();
		if(imagePrincipale != null){
			File vignetteImage = new File(imageFolder, imagePrincipale.getCleURL());
			if(vignetteImage.exists()){
				result = BitmapFactory.decodeFile(vignetteImage.getPath());
			}
		}
		// utilise l'icone de base en tant que substitut
		// TODO prendre l'icone du groupe auquel appartient l'espèce ?
		// result = BitmapFactory.decodeResource(inContext.getResources(), R.drawable.ic_launcher);
		return result;
	}
	
	public static boolean isAvailableImagePrincipaleFiche(Context inContext, Fiche fiche){
		
		PhotoFiche imagePrincipale;
		imagePrincipale = fiche.getPhotoPrincipale();
		return isAvailableImagePhotoFiche(inContext, imagePrincipale);		
	}
	
	
	public static Bitmap getAvailableImagePhotoFiche(Context inContext, PhotoFiche photofiche){
		
		Bitmap result = null;		
		File imageFolder = inContext.getDir(VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE);
		
		if(photofiche != null){
			File vignetteImage = new File(imageFolder, photofiche.getCleURL());
			if(vignetteImage.exists()){
				result = BitmapFactory.decodeFile(vignetteImage.getPath());
			}
		}
		// utilise l'icone de base en tant que substitut
		// TODO prendre l'icone du groupe auquel appartient l'espèce ?
		// result = BitmapFactory.decodeResource(inContext.getResources(), R.drawable.ic_launcher);
		return result;
	}
	
	public static boolean isAvailableImagePhotoFiche(Context inContext, PhotoFiche photofiche){
		if (BuildConfig.DEBUG) Log.d("Outils", "isAvailableImagePhotoFiche() - photofiche : "+ photofiche );
    	
		switch(PrecharMode.valueOf(getParamString(inContext, R.string.pref_mode_precharg_region_ttzones,"P1"))){
		case P1 :
		case P2 :
			if (BuildConfig.DEBUG) Log.d("Outils", "isAvailableImagePhotoFiche() - Vignettes" );
			return isAvailableVignettePhotoFiche(inContext, photofiche);
		case P3 :
		case P4 :
			if (BuildConfig.DEBUG) Log.d("Outils", "isAvailableImagePhotoFiche() - Medium" );
			return isAvailableMedResPhotoFiche(inContext, photofiche);
		case P5 :
		case P6 :
			if (BuildConfig.DEBUG) Log.d("Outils", "isAvailableImagePhotoFiche() - Hight" );
	    	return isAvailableHiResPhotoFiche(inContext, photofiche);
		default:
			return false;
		}
	}


	
	public static boolean isAvailableVignettePhotoFiche(Context inContext, PhotoFiche photofiche){
		File imageFolder = inContext.getDir(VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE);		
		if(photofiche != null && photofiche.getCleURL() != null && !photofiche.getCleURL().isEmpty()){
			File vignetteImage = new File(imageFolder, photofiche.getCleURL());
			if(vignetteImage.exists()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAvailableMedResPhotoFiche(Context inContext, PhotoFiche photofiche){
		File imageFolder = inContext.getDir( MED_RES_FICHE_FOLDER , Context.MODE_PRIVATE);		
		if(photofiche != null){
			File vignetteImage = new File(imageFolder, photofiche.getCleURL());
			if(vignetteImage.exists()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAvailableHiResPhotoFiche(Context inContext, PhotoFiche photofiche){
		File imageFolder = inContext.getDir( HI_RES_FICHE_FOLDER , Context.MODE_PRIVATE);		
		if(photofiche != null){
			File vignetteImage = new File(imageFolder, photofiche.getCleURL());
			if(vignetteImage.exists()){
				return true;
			}
		}
		return false;
	}
	
	public static File getVignetteFile(Context inContext, PhotoFiche photo) throws IOException{		
		File imageFolder = inContext.getDir(VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE);		
		return new File(imageFolder, photo.getCleURL());
	}
	
	public static File getMedResFile(Context inContext, PhotoFiche photo) throws IOException{		
		File imageFolder = inContext.getDir(MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE);		
		return new File(imageFolder, photo.getCleURL());
	}
	
	public static File getHiResFile(Context inContext, PhotoFiche photo) throws IOException{		
		File imageFolder = inContext.getDir(HI_RES_FICHE_FOLDER, Context.MODE_PRIVATE);		
		return new File(imageFolder, photo.getCleURL());
	}
	
	public static File getOrDownloadVignetteFile(Context inContext, PhotoFiche photo) throws IOException{
		return getOrDownloadFile(inContext, photo, ImageType.VIGNETTE);
	}
	
	public static File getOrDownloadFile(Context inContext, PhotoFiche photo, ImageType imageType) throws IOException{
		File result = null;	
		String imageFolderName="";
		String baseUrl="";
		switch (imageType) {
		case VIGNETTE:
			imageFolderName = VIGNETTES_FICHE_FOLDER;
			baseUrl=PhotoFiche.VIGNETTE_BASE_URL;
			break;
		case MED_RES:
			imageFolderName = MED_RES_FICHE_FOLDER;
			baseUrl=PhotoFiche.MOYENNE_BASE_URL;
			break;
		case HI_RES:
			imageFolderName = HI_RES_FICHE_FOLDER;
			baseUrl=PhotoFiche.GRANDE_BASE_URL;
			break;
		default:
			break;
		}
		File imageFolder = inContext.getDir(imageFolderName, Context.MODE_PRIVATE);
		
		
		if(photo != null){
			File vignetteImage = new File(imageFolder, photo.getCleURL());
			if(vignetteImage.exists()){
				result = vignetteImage;
			}
			else{
				StringBuffer stringBuffer = new StringBuffer("");
		    	BufferedReader bufferedReader = null;
		    
				URL urlHtml;
				try {
					String urlNettoyee = baseUrl+photo.getCleURL();
					urlNettoyee = urlNettoyee.replaceAll(" ", "%20");
					urlHtml = new URL(urlNettoyee);
					HttpURLConnection urlConnection = (HttpURLConnection) urlHtml.openConnection();
			        urlConnection.setConnectTimeout(3000);
			        urlConnection.setReadTimeout(10000);
			        
			        urlConnection.connect();
		            // this will be useful so that you can show a typical 0-100% progress bar
		            int fileLength = urlConnection.getContentLength();

		            
		            // download the file
		            InputStream input = urlConnection.getInputStream();
		            OutputStream output = new FileOutputStream(vignetteImage);

		            byte data[] = new byte[1024];
		            long total = 0;
		            int count;
		            while ((count = input.read(data)) != -1) {
		                total += count;
		                output.write(data, 0, count);
		            }

		            output.flush();
		            output.close();
		            input.close();
			        
				} catch (MalformedURLException e) {
					Log.w(LOG_TAG, e.getMessage(), e);
				}
				
			}
		}
		
		return result;
	}
	
	public static int getVignetteCount(Context inContext){
		File imageFolder = inContext.getDir(VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE);
		return imageFolder.list().length;
	}
	public static int getMedResCount(Context inContext){
		File imageFolder = inContext.getDir(MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE);
		return imageFolder.list().length;
	}
	public static int getHiResCount(Context inContext){
		File imageFolder = inContext.getDir(HI_RES_FICHE_FOLDER, Context.MODE_PRIVATE);
		return imageFolder.list().length;
	}
	
	public static long getPhotosDiskUsage(Context inContext){
    	return getVignettesDiskUsage(inContext) + getMedResDiskUsage(inContext) + getHiResDiskUsage(inContext);
	}
	public static long getVignettesDiskUsage(Context inContext){
		File imageFolder = inContext.getDir(VIGNETTES_FICHE_FOLDER, Context.MODE_PRIVATE);
		DiskUsage du = new DiskUsage();
    	du.accept(imageFolder);
    	return du.getSize();
	}
	public static long getMedResDiskUsage(Context inContext){
		File imageFolder = inContext.getDir(MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE);
		DiskUsage du = new DiskUsage();
    	du.accept(imageFolder);
    	return du.getSize();
	}
	public static long getHiResDiskUsage(Context inContext){
		File imageFolder = inContext.getDir(HI_RES_FICHE_FOLDER, Context.MODE_PRIVATE);
		DiskUsage du = new DiskUsage();
    	du.accept(imageFolder);
    	return du.getSize();
	}
	
	/* *********************************************************************
     * isOnline permet de vérifier que l'appli a bien accès à Internet
     * si Que Wifi en paramètre envoie faux si pas sur Wifi
     * TODO : type de connection
     ********************************************************************** */	
	/* Guillaume : Ne doit plus servir, cf. : connectionType() */
	public static boolean isOnline(Context context) {
		if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - Début");
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - isOnline : true");
	    	
	    	NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    	if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - mWifi.isConnected() : "+ mWifi.isConnected() );
	    	
	    	if (! PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_sync_auto_wifi_only), true) || mWifi.isConnected() ) {
	    		if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - Wifi = True or OnlyWifi = false");
		    	if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - Fin");
		    	return true;
	    	} else {
	    		if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - mais pas en Wifi et OnlyWifi = True");
		    	if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - Fin");
		        return false;
	    	}
	    } else {
	    	//String text = "Aucune Connection Internet disponible";
	    	//if (LOG) Log.e(TAG, "isOnline() - " + text);
	    	//Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
			//toast.show();
			
	    	if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - isOnline : false");
	    	if (BuildConfig.DEBUG) Log.d("Outils", "isOnline() - Fin");
	    	return false;
	    }
	}
	
	/* *********************************************************************
     * Type de connection : aucune, wifi, gsm 
     ********************************************************************** */		
	public static ConnectionType getConnectionType(Context context) {
		if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - Début");
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - isOnline : true");
	    	
	    	NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    	if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - mWifi.isConnected() : "+ mWifi.isConnected() );
	    	
	    	if (mWifi.isConnected() ) {
	    		if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - Wifi = True");
		    	if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - Fin");
		    	return ConnectionType.WIFI;
	    	} else {
	    		if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - Wifi = False");
		    	if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - Fin");
		        return ConnectionType.GSM;
	    	}
	    } else {
			
	    	if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - isOnline : false");
	    	if (BuildConfig.DEBUG) Log.d("Outils", "connectionType() - Fin");
	    	return ConnectionType.AUCUNE;
	    }
	}
	
	/* Lecture Paramètres */
	public static boolean getParamBoolean(Context context, int param, boolean valDef) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getApplicationContext().getString(param), valDef);
	}
	public static String getParamString(Context context, int param, String valDef) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getApplicationContext().getString(param), valDef);
	}
	public static long getParamLong(Context context, int param, Long valDef) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(context.getApplicationContext().getString(param), valDef);
	}
}
