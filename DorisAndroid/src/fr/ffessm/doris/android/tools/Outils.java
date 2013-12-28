package fr.ffessm.doris.android.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
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
	
	public static File getImageFolder(Context inContext, ImageType inImageType) { 
		switch (inImageType) {
		case VIGNETTE :
			return getImageFolderVignette(inContext);
		case MED_RES :
			return getImageFolderVignette(inContext);
		case HI_RES :
			return getImageFolderVignette(inContext);
			default:
		return null;
		}
	}
	public static File getImageFolderVignette(Context inContext) { 
		return inContext.getDir( VIGNETTES_FICHE_FOLDER , Context.MODE_PRIVATE);
	}
	public static File getImageFolderMedRes(Context inContext) { 
		return inContext.getDir( MED_RES_FICHE_FOLDER , Context.MODE_PRIVATE);
	}
	public static File getImageFolderHiRes(Context inContext) { 
		return inContext.getDir( HI_RES_FICHE_FOLDER , Context.MODE_PRIVATE);
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
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - photofiche : "+ photofiche );
    	
		switch(PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_ttzones,"P1"))){
		case P1 :
		case P2 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Vignettes" );
			return isAvailableVignettePhotoFiche(inContext, photofiche);
		case P3 :
		case P4 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Medium" );
			return isAvailableMedResPhotoFiche(inContext, photofiche);
		case P5 :
		case P6 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Hight" );
	    	return isAvailableHiResPhotoFiche(inContext, photofiche);
		default:
			return false;
		}
	}

	public static boolean isAvailableImagePhotoFiche(Context inContext, PhotoFiche inPhotofiche, ImageType inImageType){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - photofiche : "+ photofiche + " - ImageType : " + inImageType);
    	
		switch(inImageType){
		case VIGNETTE :
			return isAvailableVignettePhotoFiche(inContext, inPhotofiche);
		case MED_RES :
			return isAvailableMedResPhotoFiche(inContext, inPhotofiche);
		case HI_RES :
	    	return isAvailableHiResPhotoFiche(inContext, inPhotofiche);
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
		File imageFolder = getImageFolderVignette(inContext);		
		return new File(imageFolder, photo.getCleURL());
	}
	
	public static File getMedResFile(Context inContext, PhotoFiche photo) throws IOException{		
		File imageFolder = getImageFolderMedRes(inContext);		
		return new File(imageFolder, photo.getCleURL());
	}
	
	public static File getHiResFile(Context inContext, PhotoFiche photo) throws IOException{		
		File imageFolder = getImageFolderHiRes(inContext);	;		
		return new File(imageFolder, photo.getCleURL());
	}

	public static HashSet getAllVignettesPhotoFicheAvailable(Context inContext){
		HashSet hsPhotoFicheAvailable = new HashSet<File>();
		File imageFolder = getImageFolderVignette(inContext);		
		for (File file : imageFolder.listFiles()) {
			hsPhotoFicheAvailable.add(file);
		}
		return hsPhotoFicheAvailable;
	}
	public static HashSet getAllMedResPhotoFicheAvailable(Context inContext){
		HashSet hsPhotoFicheAvailable = new HashSet<File>();
		File imageFolder = getImageFolderMedRes(inContext);		
		for (File file : imageFolder.listFiles()) {
			hsPhotoFicheAvailable.add(file);
		}
		return hsPhotoFicheAvailable;
	}
	public static HashSet getAllHiResPhotoFicheAvailable(Context inContext){
		HashSet hsPhotoFicheAvailable = new HashSet<File>();
		File imageFolder = getImageFolderHiRes(inContext);		
		for (File file : imageFolder.listFiles()) {
			hsPhotoFicheAvailable.add(file);
		}
		return hsPhotoFicheAvailable;
	}
	
	public static File getOrDownloadVignetteFile(Context inContext, PhotoFiche photo) throws IOException{
		return getOrDownloadPhotoFile(inContext, photo, ImageType.VIGNETTE);
	}
	
	public static File getOrDownloadPhotoFile(Context inContext, PhotoFiche photo, ImageType imageType) throws IOException{
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
		return getFileCount(inContext, getImageFolderVignette(inContext));
	}
	public static int getMedResCount(Context inContext){
		return getFileCount(inContext, getImageFolderMedRes(inContext));
	}
	public static int getHiResCount(Context inContext){
		return getFileCount(inContext, getImageFolderHiRes(inContext));
	}
	
	public static int getFileCount(Context inContext, File inFolder){
		return inFolder.list().length;
	}
	
	public static long getPhotosDiskUsage(Context inContext){
    	return getVignettesDiskUsage(inContext) + getMedResDiskUsage(inContext) + getHiResDiskUsage(inContext);
	}
	public static long getVignettesDiskUsage(Context inContext){
    	return getDiskUsage(inContext, getImageFolderVignette(inContext) );
	}
	public static long getMedResDiskUsage(Context inContext){
    	return getDiskUsage(inContext, getImageFolderMedRes(inContext) );
	}
	public static long getHiResDiskUsage(Context inContext){
    	return getDiskUsage(inContext, getImageFolderHiRes(inContext) );
	}
	public static long getDiskUsage(Context inContext, File inImageFolder){
		DiskUsage du = new DiskUsage();
    	du.accept(inImageFolder);
    	return du.getSize();
	}
	public static String getHumanDiskUsage(long inSize){
		String sizeTexte = "";
		// octet => ko
		inSize = inSize/1024;
        if ( inSize < 1024 ) {
        	sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Ko";
        } else {
        	inSize = inSize / 1024;
        	if ( inSize < 1024 ) {
        		sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Mo";
        	} else {
        		inSize = inSize / 1024;
        		sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Go";
        	}
        }
    	return sizeTexte;
	}

	
	/* *********************************************************************
     * Type de connection : aucune, wifi, gsm 
     ********************************************************************** */		
	public static ConnectionType getConnectionType(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "connectionType() - isOnline : true");
	    	
	    	NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "connectionType() - mWifi.isConnected() : "+ mWifi.isConnected() );
	    	
	    	if (mWifi.isConnected() ) {
		    	return ConnectionType.WIFI;
	    	} else {
		        return ConnectionType.GSM;
	    	}
	    } else {
	    	return ConnectionType.AUCUNE;
	    }
	}
	
	public static long getNbVignettesAPrecharger(Context inContext,  OrmLiteDBHelper helper){
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getNbVignettesAPrecharger() - début" );
    	
		long nbPhotosAPrecharger;
 	
		switch(PrecharMode.valueOf(Outils.getParamString(inContext, R.string.pref_key_mode_precharg_region_ttzones,"P1"))) {
		case P1 :
			nbPhotosAPrecharger = helper.getFicheDao().countOf();
			break;
		case P2 :
		case P3 :
			nbPhotosAPrecharger = helper.getPhotoFicheDao().countOf();
			break;
		default:
			nbPhotosAPrecharger = 0;
		}
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getNbVignettesAPrecharger() - nbPhotosAPrecharger : " + nbPhotosAPrecharger );
		return nbPhotosAPrecharger;
	}
	
	public static long getNbMedResAPrecharger(Context inContext,  OrmLiteDBHelper helper){
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getNbMedResAPrecharger() - début" );
    	
		long nbPhotosAPrecharger;
 
		switch(PrecharMode.valueOf(Outils.getParamString(inContext, R.string.pref_key_mode_precharg_region_ttzones,"P1"))) {
		case P3 :
			nbPhotosAPrecharger = helper.getFicheDao().countOf();
			break;
		case P4 :
		case P5 :
			nbPhotosAPrecharger = helper.getPhotoFicheDao().countOf();
			break;
		default:
			nbPhotosAPrecharger = 0;
		}
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getNbMedResAPrecharger() - nbPhotosAPrecharger : " + nbPhotosAPrecharger );
		return nbPhotosAPrecharger;
	}
	public static long getNbHiResAPrecharger(Context inContext,  OrmLiteDBHelper helper){
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getNbHiResAPrecharger() - début" );
    	
		long nbPhotosAPrecharger;
 
		switch(PrecharMode.valueOf(Outils.getParamString(inContext, R.string.pref_key_mode_precharg_region_ttzones,"P1"))) {
		case P5 :
			nbPhotosAPrecharger = helper.getFicheDao().countOf();
			break;
		case P6 :
			nbPhotosAPrecharger = helper.getPhotoFicheDao().countOf();
			break;
		default:
			nbPhotosAPrecharger = 0;
		}
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getNbHiResAPrecharger() - nbPhotosAPrecharger : " + nbPhotosAPrecharger );
		return nbPhotosAPrecharger;
	}
	
	/* Lecture Paramètres */
	public static String getStringKeyParam(Context inContext, int inParam) {
		return inContext.getResources().getResourceEntryName(inParam);
	}
	public static String getStringNameParam(Context inContext, int inParam) {
		return inContext.getString(inParam);
	}
	public static boolean getParamBoolean(Context inContext, int inParam, boolean inValDef) {
		return PreferenceManager.getDefaultSharedPreferences(inContext).getBoolean(inContext.getString(inParam), inValDef);
	}
	public static String getParamString(Context inContext, int inParam, String inValDef) {
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamString() - param : " + inParam + "-" + inContext.getString(inParam) );
		return PreferenceManager.getDefaultSharedPreferences(inContext).getString(inContext.getString(inParam), inValDef);
	}
	public static long getParamLong(Context inContext, int inParam, Long inValDef) {
		return PreferenceManager.getDefaultSharedPreferences(inContext).getLong(inContext.getString(inParam), inValDef);
	}
	public static int getParamInt(Context inContext, int inParam, int inValDef) {
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamInt() - param : " + inParam );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamInt() - param : " + inContext.getString(inParam) );
		return PreferenceManager.getDefaultSharedPreferences(inContext).getInt(inContext.getString(inParam), inValDef);
	}
	/* Enregistrement paramètres */
	public static void setParamString(Context inContext, int inParam, String inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();
	    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - param : " + inContext.getString(inParam) );
	    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - getStringKeyParam : " + Outils.getStringKeyParam(inContext,inParam) );
	    prefEdit.putString(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}
	public static void setParamInt(Context inContext, int inParam, int inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putInt(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}
	public static void setParamBoolean(Context inContext, int inParam, Boolean inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putBoolean(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}
	
	
	public static ImageType getImageQualityToDownload(Context inContext, boolean inPhotoPrincipale, int inIdZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageQualityToDownload() - Début" );
    	
		ImageType imageType;
		PrecharMode prechargementMode = getPrecharModeZoneGeo(inContext, inIdZoneGeo);
		
		if (inPhotoPrincipale) {
			switch(prechargementMode){
			case P1 :
			case P2 :
				return ImageType.VIGNETTE;
			case P3 :
			case P4 :
				return ImageType.MED_RES;
			case P5 :
			case P6 :
				return ImageType.HI_RES;
			default:
				return null;
			}
		} else {
			switch(prechargementMode){
			case P2 :
			case P3 :
				return ImageType.VIGNETTE;
			case P4 :
			case P5 :
				return ImageType.MED_RES;
			case P6 :
				return ImageType.HI_RES;
			default:
				return null;
			}
		}
		
	}
	
	public static PrecharMode getPrecharModeZoneGeo(Context inContext, int inIdZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharModeZoneGeo() - Début" );
		
		switch(inIdZoneGeo){
		case 1 :
			return PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_france,"P1"));
		case 2 :
			return PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_eaudouce,"P1"));
		case 3 :
			return PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_indopac,"P1"));
		case 4 :
			return PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_caraibes,"P1"));
		case 5 :
			return PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_atlantno,"P1"));
		default :
			return null;
		}
	}
	
	/**
	 * récupère le nombre déclaré de photo à précharger
	 * La déclaration est stockée dans les préférences
	 * zone par zone, principale ou pas
	 * @param inContext context permettant de récupérer l'info depuis les préférences
	 * @param inIdZoneGeo 
	 * @param inPrincipale concerne la photo prinicipale ou n'importe quelle photo
	 * @return
	 */
	public static int getAPrecharQteZoneGeo(Context inContext, int inIdZoneGeo, Boolean inPrincipale){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - data_nbphotos_atelecharger_france : "+getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_france, 0) );
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_france, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_france, 0 );
			case 2 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0 );
			case 3 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0 );
			case 4 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0 );
			case 5 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0 );
			default :
				return 0;
			}
		} else {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_france, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_eaudouce, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_atlantno, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_indopac, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_france, 0 );
			case 2 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_eaudouce, 0 );
			case 3 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_indopac, 0 );
			case 4 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_caraibes, 0 );
			case 5 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_atelecharger_atlantno, 0 );
			default :
				return 0;
			}
		}
	}
	
	/**
	 * récupère le nombre de photos déclarées présentes (stockées dansles préférences)
	 * zone par zone, principale ou pas
	 * @param inContext
	 * @param inIdZoneGeo
	 * @param inPrincipale concerne la photo prinicipale ou n'importe quelle photo
	 * @return
	 */
	public static int getDejaLaQteZoneGeo(Context inContext, int inIdZoneGeo, Boolean inPrincipale){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - data_nbphotos_recues_france : "+getParamInt(inContext, R.string.pref_key_nbphotos_recues_france, 0) );
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_france, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_eaudouce, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_atlantno, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_indopac, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_france, 0 );
			case 2 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_eaudouce, 0 );
			case 3 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_indopac, 0 );
			case 4 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_caraibes, 0 );
			case 5 :
				return getParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_atlantno, 0 );
			default :
				return 0;
			}
		} else {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = getParamInt(inContext, R.string.pref_key_nbphotos_recues_france, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_recues_eaudouce, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_recues_atlantno, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_recues_indopac, 0 );
				nbAPrechar += getParamInt(inContext, R.string.pref_key_nbphotos_recues_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_recues_france, 0 );
			case 2 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_recues_eaudouce, 0 );
			case 3 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_recues_indopac, 0 );
			case 4 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_recues_caraibes, 0 );
			case 5 :
				return getParamInt(inContext, R.string.pref_key_nbphotos_recues_atlantno, 0 );
			default :
				return 0;
			}
		}
	}
	/**
	 * enregistre le nombre de photos déclarées présentes (stockées dansles préférences)
	 * @param inContext
	 * @param inIdZoneGeo
	 * @param inPrincipale
	 * @param nouvelleValeur
	 */
	public static void setDejaLaQteZoneGeo(Context inContext, int inIdZoneGeo, Boolean inPrincipale, int nouvelleValeur){
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case -1 :
				// ERREUR, pas possible pour la zone générale, calculé à partir des autres zones
				// TODO probablement pas terrible comme précision à cause des photos des fiches présentes dans plusieurs zone
				break;
			case 1 :
				setParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_france, nouvelleValeur);
				break;
			case 2 :
				setParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_eaudouce, nouvelleValeur);
				break;
			case 3 :
				setParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_indopac, nouvelleValeur);
				break;
			case 4 :
				setParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_caraibes, nouvelleValeur);
				break;
			case 5 :
				setParamInt(inContext, R.string.pref_key_nbphotosprinc_recues_atlantno, nouvelleValeur);
				break;
			default :
				break;
			}
		} else {
			switch(inIdZoneGeo){
			case -1 :
				// ERREUR, pas possible pour la zone générale, calculé à partir des autres zones
				// TODO probablement pas terrible comme précision à cause des photos des fiches présentes dans plusieurs zone
			case 1 :
				setParamInt(inContext, R.string.pref_key_nbphotos_recues_france, nouvelleValeur);
				break;
			case 2 :
				setParamInt(inContext, R.string.pref_key_nbphotos_recues_eaudouce, nouvelleValeur);
				break;
			case 3 :
				setParamInt(inContext, R.string.pref_key_nbphotos_recues_indopac, nouvelleValeur);
				break;
			case 4 :
				setParamInt(inContext, R.string.pref_key_nbphotos_recues_caraibes, nouvelleValeur);
				break;
			case 5 :
				setParamInt(inContext, R.string.pref_key_nbphotos_recues_atlantno, nouvelleValeur);
				break;
			default :
				break;
			}
		}
	}
	
	
	/**
	 * récupère l'id de la resource textuelle pour les clé des préférences pour récupérer ou stocker des info de photo à télécharger ou déjà téléchargée
	 * zone par zone, photo principale ou pas
	 * @param inContext
	 * @param inIdZoneGeo
	 * @param inPrincipale
	 * @return
	 */
	// TODO : C'est crado mais c'est rassemblé ici
	public static int getKeyDataAPrecharZoneGeo(Context inContext, int inIdZoneGeo, Boolean inPrincipale){
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case 1 :
				return R.string.pref_key_nbphotosprinc_atelecharger_france;
			case 2 :
				return R.string.pref_key_nbphotosprinc_atelecharger_eaudouce;
			case 3 :
				return R.string.pref_key_nbphotosprinc_atelecharger_indopac;
			case 4 :
				return R.string.pref_key_nbphotosprinc_atelecharger_caraibes;
			case 5 :
				return R.string.pref_key_nbphotosprinc_atelecharger_atlantno;
			default :
				return 0;
			}
		} else {
			switch(inIdZoneGeo){
			case 1 :
				return R.string.pref_key_nbphotos_atelecharger_france;
			case 2 :
				return R.string.pref_key_nbphotos_atelecharger_eaudouce;
			case 3 :
				return R.string.pref_key_nbphotos_atelecharger_indopac;
			case 4 :
				return R.string.pref_key_nbphotos_atelecharger_caraibes;
			case 5 :
				return R.string.pref_key_nbphotos_atelecharger_atlantno;
			default :
				return 0;
			}
		}
	}
	// TODO : Crado aussi
	public static int getKeyDataDejaLaZoneGeo(Context inContext, int inIdZoneGeo, Boolean inPrincipale){
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case 1 :
				return R.string.pref_key_nbphotosprinc_recues_france;
			case 2 :
				return R.string.pref_key_nbphotosprinc_recues_eaudouce;
			case 3 :
				return R.string.pref_key_nbphotosprinc_recues_indopac;
			case 4 :
				return R.string.pref_key_nbphotosprinc_recues_caraibes;
			case 5 :
				return R.string.pref_key_nbphotosprinc_recues_atlantno;
			default :
				return 0;
			}
		} else {
			switch(inIdZoneGeo){
			case 1 :
				return R.string.pref_key_nbphotos_recues_france;
			case 2 :
				return R.string.pref_key_nbphotos_recues_eaudouce;
			case 3 :
				return R.string.pref_key_nbphotos_recues_indopac;
			case 4 :
				return R.string.pref_key_nbphotos_recues_caraibes;
			case 5 :
				return R.string.pref_key_nbphotos_recues_atlantno;
			default :
				return 0;
			}
		}
	}
	
	
	
	public static boolean isPrecharModeOnlyP0(Context inContext){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		
		if ( PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_france,"P1")) == PrecharMode.P0 
			&& PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_eaudouce,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_atlantno,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_indopac,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_caraibes,"P1")) == PrecharMode.P0
			) return true;
		return false;	
	}
	
	public static boolean isPrecharModeOnlyP0orP1(Context inContext){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		
		if ( ( PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_france,"P1")) == PrecharMode.P0 
				|| PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_france,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_eaudouce,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_eaudouce,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_atlantno,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_atlantno,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_indopac,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_indopac,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_caraibes,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_caraibes,"P1")) == PrecharMode.P1 )
			) return true;
		return false;	
	}
	
	public static String getAppVersion(Context inContext) {
		try	{
        	PackageManager pm = inContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo( inContext.getPackageName(), 0);
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getAppVersion() - appVersionName : "+pi.versionName);
            return pi.versionName;
     	} catch(Exception e) {
    		if (BuildConfig.DEBUG) Log.e(LOG_TAG, "getAppVersion() - erreur : ");
    		e.printStackTrace();
    		return "";
    	}
	}
	

	
    public static int clearFolder(File inFolder, int inNbJours){
		int deletedFiles = 0;
	    if (inFolder!= null && inFolder.isDirectory()) {
	        try {
	            for (File child:inFolder.listFiles()) {

	                //first delete subdirectories recursively
	                if (child.isDirectory()) {
	                    deletedFiles += clearFolder(child, inNbJours);
	                }

	                //then delete the files and subdirectories in this dir
	                //only empty directories can be deleted, so subdirs have been done first
	                if (child.lastModified() < new Date().getTime() - inNbJours * DateUtils.DAY_IN_MILLIS) {
	                    if (child.delete()) {
	                        deletedFiles++;
	                    }
	                }
	            }
	        }
	        catch(Exception e) {
	        	Log.e(LOG_TAG, String.format("Failed to clean the folder, error %s", e.getMessage()));
	        }
	    }
	    return deletedFiles;
	}
	
    // TODO : En attendant d'obtenir la nouvelle version de Common
	public static String getZoneIcone(Context inContext, int inId) {
	   	switch (inId) {
	   	case -1:
    		return inContext.getString(R.string.icone_touteszones);
    	case 1:
    		return inContext.getString(R.string.icone_france);
		case 2:
			return inContext.getString(R.string.icone_eaudouce);
		case 3:
			return inContext.getString(R.string.icone_indopac);
		case 4:
			return inContext.getString(R.string.icone_caraibes);
		case 5:
			return inContext.getString(R.string.icone_atlantno);
		default:
			return "";
		}
	}
}
