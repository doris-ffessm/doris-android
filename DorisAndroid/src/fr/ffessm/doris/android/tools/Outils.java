package fr.ffessm.doris.android.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.activities.DetailEntreeGlossaire_ElementViewActivity;
import fr.ffessm.doris.android.activities.DetailsFiche_ElementViewActivity;
import fr.ffessm.doris.android.activities.DetailsParticipant_ElementViewActivity;
import fr.ffessm.doris.android.activities.Glossaire_ClassListViewActivity;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
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
	
	
	public static File getImageFolder(Context inContext, ImageType inImageType) { 
		switch (inImageType) {
		case VIGNETTE :
			return getImageFolderVignette(inContext);
		case MED_RES :
			return getImageFolderMedRes(inContext);
		case HI_RES :
			return getImageFolderHiRes(inContext);
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

	public static String getbaseUrl(Context inContext, ImageType inImageType) { 
		switch (inImageType) {
		case VIGNETTE:
			return Constants.VIGNETTE_BASE_URL;
		case MED_RES:
			return Constants.MOYENNE_BASE_URL;
		case HI_RES:
			return Constants.GRANDE_BASE_URL;
		default:
			return "";
		}
	}
	
	public static boolean isAvailableImagePhotoFiche(Context inContext, PhotoFiche photofiche){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - photofiche : "+ photofiche );
    	
		switch(PrecharMode.valueOf(getParamString(inContext, R.string.pref_key_mode_precharg_region_ttzones,"P1"))){
		case P1 :
		case P2 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Vignettes" );
			return isAvailablePhoto(inContext, photofiche.getCleURL(), ImageType.VIGNETTE);
		case P3 :
		case P4 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Medium" );
			return isAvailablePhoto(inContext, photofiche.getCleURL(), ImageType.MED_RES);
		case P5 :
		case P6 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Hight" );
	    	return isAvailablePhoto(inContext, photofiche.getCleURL(), ImageType.HI_RES);
		default:
			return false;
		}
	}

	public static boolean isAvailablePhoto(Context inContext, String inPhotoURL, ImageType inImageType){
		File imageFolder = getImageFolder(inContext, inImageType);	
		if(!inPhotoURL.isEmpty()){
			File fichierImage = new File(imageFolder, inPhotoURL);
			if(fichierImage.exists()){
				return true;
			}
		}
		return false;
	}
	
	
	public static File getPhotoFile(Context inContext, String photoURL, ImageType inImageType) throws IOException{		
		File imageFolder = getImageFolder(inContext, inImageType);		
		return new File(imageFolder, photoURL);
	}
	

	public static HashSet<String> getAllPhotosAvailable(Context inContext, ImageType inImageType){
		HashSet<String> hsPhotosAvailable = new HashSet<String>();
		File imageFolder = getImageFolder(inContext, inImageType);		
		for (File file : imageFolder.listFiles()) {
			hsPhotosAvailable.add(file.getName());
		}
		return hsPhotosAvailable;
	}
	
	public static File getOrDownloadPhotoFile(Context inContext, String photoUrl, ImageType imageType) throws IOException{
		File result = null;	
		String baseUrl = getbaseUrl(inContext, imageType);
		File imageFolder = getImageFolder(inContext, imageType);
		
		if(!photoUrl.isEmpty()){
			File fichierImage = new File(imageFolder, photoUrl);
			if(fichierImage.exists()){
				result = fichierImage;
			}
			else{
		    
				URL urlHtml = null;
				try {
					String urlNettoyee = baseUrl+photoUrl;
					urlNettoyee = urlNettoyee.replaceAll(" ", "%20");
					urlHtml = new URL(urlNettoyee);
				} catch (MalformedURLException e ) {
					Log.w(LOG_TAG, e.getMessage(), e);
				}
				try {
					HttpURLConnection urlConnection = (HttpURLConnection) urlHtml.openConnection();
			        urlConnection.setConnectTimeout(3000);
			        urlConnection.setReadTimeout(10000);
			        
			        urlConnection.connect();
		            
		            // download the file
		            InputStream input = urlConnection.getInputStream();
		            OutputStream output = new FileOutputStream(fichierImage);

		            byte data[] = new byte[1024];
		            int count;
		            while ((count = input.read(data)) != -1) {
		                output.write(data, 0, count);
		            }

		            output.flush();
		            output.close();
		            input.close();
			        
				} catch (IOException e) {
					Log.w(LOG_TAG, e.getMessage(), e);
				}
				
			}
		}
		
		return result;
	}


	public static int getImageCount(Context inContext, ImageType inImageType){
		return getImageFolder(inContext, inImageType).list().length;
	}
	
	public static long getPhotosDiskUsage(Context inContext){
    	return getPhotoDiskUsage(inContext, ImageType.VIGNETTE) + getPhotoDiskUsage(inContext, ImageType.MED_RES) + getPhotoDiskUsage(inContext, ImageType.HI_RES);
	}
	public static long getPhotoDiskUsage(Context inContext, ImageType inImageType){
    	return getDiskUsage(inContext, getImageFolder(inContext, inImageType) );
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
		if (PreferenceManager.getDefaultSharedPreferences(inContext).contains(inContext.getString(inParam)) ) {
			return PreferenceManager.getDefaultSharedPreferences(inContext).getLong(inContext.getString(inParam), inValDef);
		} else {
			return inValDef;
		}
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
	public static void setParamLong(Context inContext, int inParam, long inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putLong(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}	
	
	public static ImageType getImageQualityToDownload(Context inContext, boolean inPhotoPrincipale, int inIdZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageQualityToDownload() - Début" );

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
	public static int getKeyDataRecuesZoneGeo(Context inContext, int inIdZoneGeo, Boolean inPrincipale){
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
	
	

    public final static SpannableString textToSpannableStringDoris(final Context context, CharSequence texte) {
	    //Log.d(LOG_TAG, "textToSpannableStringDoris() - texte : "+texte);
	    
	    SpannableString richtext = new SpannableString("");
	    
	    if ( !texte.toString().replaceAll("\\s", "").matches(".*\\{\\{[^\\}]*\\}\\}.*")) {
	    	//Log.d(LOG_TAG, "textToSpannableStringDoris() - Aucun bloc {{*}}");
	    	return new SpannableString(texte);
	    	
	    } else {
	    	//Log.d(LOG_TAG, "textToSpannableStringDoris() - Traitement récurrent des blocs {{*}}");
	    	
	    	// TODO : doit être améliorable mais je n'arrive pas à manipuler directement SpannableString
	    	// donc pas de concat, pas de regexp.
	        List<TextSpan> listeFicheNumero = new ArrayList<TextSpan>();
	        List<TextSpan> pileDerniereBalise = new ArrayList<TextSpan>();
	        
	        String texteInter = texte.toString();
	        StringBuilder texteFinal = new StringBuilder();
	        int iTmp = 0;
	        while (texteInter.contains("{{") && iTmp < 100 ) {
	        	iTmp ++;
	        	
	        	// Recherche 1ère Balise à traiter
	        	int posDepTexteInter = texteInter.indexOf("{{");
	        	int posFinTexteInter = texteInter.indexOf("}}");
	        	
	        	String balise = texteInter.substring(posDepTexteInter+2, posFinTexteInter);
	        	
	        	//Log.d(LOG_TAG, "textToSpannableStringDoris() - texteInter : "+texteInter
	        	//		+ " - " + posDepTexteInter + "-" + posFinTexteInter + " -> " + balise);
	        	
	        	if (balise.equals("i")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.ITALIQUE,posDepTexteFinal,0));
	        	}
	        	else if (balise.equals("/i")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.ITALIQUE,ts.positionDebut,posFinTexteFinal));
	        	}
	        	else if (balise.equals("g")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.GRAS,posDepTexteFinal,0));
	        	}
	        	else if (balise.equals("/g")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.GRAS,ts.positionDebut,posFinTexteFinal));
	        	}
	        	else if (balise.equals("s")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.SOULIGNE,posDepTexteFinal,0));
	        	}
	        	else if (balise.equals("/s")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.SOULIGNE,ts.positionDebut,posFinTexteFinal));
	        	}
	        	else if (balise.equals("n/")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) + "\n");
	        			        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	}
	        	else if (balise.startsWith("F:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.FICHE,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/F")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.FICHE,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	else if (balise.startsWith("D:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.DEFINITION,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/D")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.DEFINITION,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	else if (balise.startsWith("A:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.LIENWEB,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/A")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.LIENWEB,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	else if (balise.startsWith("P:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.PARTICIPANT,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/P")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.PARTICIPANT,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	
	        } // fin du While
	        
	        texteFinal.append(texteInter);
	        //Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal après while : "+texteFinal);
	        //Log.d(LOG_TAG, "textToSpannableStringDoris() - longueur : "+texteFinal.length());
	        
	        richtext = new SpannableString(texteFinal);
	        
	        for (final TextSpan ts : listeFicheNumero) {
	        	//Log.d(LOG_TAG, "textToSpannableStringDoris() - ts : "+ts.spanType.name()+" - "+ts.info);
	        	
	        	if ( ts.spanType == TextSpan.SpanType.ITALIQUE ) {
	        		richtext.setSpan(new StyleSpan(Typeface.ITALIC), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.GRAS ) {
	        		richtext.setSpan(new StyleSpan(Typeface.BOLD), ts.positionDebut, ts.positionFin, 0);
	        		if ( !context.getString(R.string.detailsfiche_elementview_couleur_gras).isEmpty() ){
	        			richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_gras))), ts.positionDebut, ts.positionFin, 0);
	        		}
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.SOULIGNE ) {
	        		richtext.setSpan(new UnderlineSpan(), ts.positionDebut, ts.positionFin, 0);
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.FICHE ) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  
			            	Intent toDetailView = new Intent(context, DetailsFiche_ElementViewActivity.class);
			                Bundle bundle = new Bundle();
			                bundle.putInt("ficheNumero", Integer.valueOf(ts.info) );
			                
			                OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
			                RuntimeExceptionDao<Fiche, Integer> entriesDao = ormLiteDBHelper.getFicheDao();
			                bundle.putInt("ficheId", entriesDao.queryForEq("numeroFiche", Integer.valueOf(ts.info)).get(0).getId() );
			                
			        		toDetailView.putExtras(bundle);
			        		context.startActivity(toDetailView);
			            }  
			        };
			     	//Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_lienfiche))), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.DEFINITION) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  

			                Bundle bundle = new Bundle();
			                //bundle.putInt("ficheNumero", Integer.valueOf(ts.info) );
			                
			                //Parfois les mots sont au pluriel, on enlève donc ici un éventuel s final
			                String terme = ts.info.replaceAll("s$", "");
			                
			                OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
			                RuntimeExceptionDao<DefinitionGlossaire, Integer> entriesDao = ormLiteDBHelper.getDefinitionGlossaireDao();
			                List<DefinitionGlossaire> listeDefinitions = new ArrayList<DefinitionGlossaire>();
			                int idDefinition = 0;
			                try {
			                	//Commence par le terme au singulier
			                	listeDefinitions = entriesDao.query(
										entriesDao.queryBuilder().where().like("terme", terme+"%").prepare() );
								if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
								else {
									//#Commence par# le terme au masculin singulier
									String termeTmp1 = terme;
									termeTmp1 = termeTmp1.replaceAll("elle$", "el");
									termeTmp1 = termeTmp1.replaceAll("ienne$", "ien");
									termeTmp1 = termeTmp1.replaceAll("euse$", "eur");
									termeTmp1 = termeTmp1.replaceAll("e$", "");
									listeDefinitions = entriesDao.query(
											entriesDao.queryBuilder().where().like("terme", termeTmp1+"%").prepare() );
									if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
									else {
										//#Commence par# le terme au masculin singulier ...al => ...aux
										String termeTmp2 = terme;
										termeTmp2 = termeTmp2.replaceAll("aux$", "al");
										termeTmp2 = termeTmp2.replaceAll("eaux$", "eau");
										termeTmp2 = termeTmp2.replaceAll("ale$", "al");
										termeTmp2 = termeTmp2.replaceAll("ive$", "if");
										listeDefinitions = entriesDao.query(
												entriesDao.queryBuilder().where().like("terme", termeTmp2+"%").prepare() );
										if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
										else {
											//#Contient# le terme au singulier
											listeDefinitions = entriesDao.query(
													entriesDao.queryBuilder().where().like("terme", "%"+terme+"%").prepare() );
											if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
											else {
												//#Contient# le terme au masculin singulier
												listeDefinitions = entriesDao.query(
														entriesDao.queryBuilder().where().like("terme", "%"+termeTmp1+"%").prepare() );
												if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
												else {
													//#Contient# le terme au masculin singulier
													listeDefinitions = entriesDao.query(
															entriesDao.queryBuilder().where().like("terme", "%"+termeTmp2+"%").prepare() );
													if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
													else {
														//le É par exemple ne fonctionne pas avec LIKE dans SQLite
														// Bug connu : http://www.sqlite.org/lang_expr.html#like
														listeDefinitions = entriesDao.queryForAll();
														String texteRecherche = terme.replaceAll("e$", "").replaceAll("ux$", "").toLowerCase();
														for (DefinitionGlossaire definition : listeDefinitions){
															if (definition.getTerme().toString().toLowerCase().contains(texteRecherche)) {
																idDefinition = definition.getId();
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								
							} catch (SQLException e) {
								e.printStackTrace();
							}

			                
			                if(idDefinition != 0){
		                    	
		                    	bundle.putInt("definitionGlossaireId", idDefinition );
		                    	
				            	Intent toDefinitionlView = new Intent(context, DetailEntreeGlossaire_ElementViewActivity.class);
		                    	toDefinitionlView.putExtras(bundle);
		                    	context.startActivity(toDefinitionlView);
		                    } else {
		                    	Intent toDefinitionlView = new Intent(context, Glossaire_ClassListViewActivity.class);
		                    	context.startActivity(toDefinitionlView);
		                    }

			            }  
			        };
			     	//Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_liendefinition))), ts.positionDebut, ts.positionFin, 0);
	        	} // Fin else DEFINITION
	        	else if ( ts.spanType == TextSpan.SpanType.LIENWEB) {
	        		String url = ts.info;
	        		if (!url.contains("http")) url = "http://"+url;
	        		richtext.setSpan(new URLSpan(url), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.PARTICIPANT ) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  
			    	        Intent toDetailView = new Intent(context, DetailsParticipant_ElementViewActivity.class);
			    	        
			    	        Bundle b = new Bundle();
			    	        b.putInt("participantId", Integer.valueOf(ts.info) );
			    			
			    	        toDetailView.putExtras(b);
			    			context.startActivity(toDetailView);
			            }  
			        };
			     	//Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_lienparticipant))), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        }
	        
	        /* Pour jour mettre des images directement dans le texte : la picto dangerosité par exemple.
	        Drawable d = getResources().getDrawable(R.drawable.icon32); 
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()); 
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            */
	        
	        return richtext;
	    }

    }
	

    public static class TextSpan {
    	
    	public enum SpanType {
    	    FICHE, ITALIQUE, GRAS, SOULIGNE, SAUTDELIGNE, DEFINITION, LIENWEB, PARTICIPANT
    	} 
    	
    	SpanType spanType = null;
    	int positionDebut;
    	int positionFin;
    	String info = ""; 
    	
    	public TextSpan(SpanType spanType, int positionDebut, int positionFin) {
    		this.spanType = spanType;
    		this.positionDebut = positionDebut;
    		this.positionFin = positionFin;
    	}
    	
    	public TextSpan(SpanType spanType, int positionDebut, int positionFin, String info) {
    		this.spanType = spanType;
    		this.positionDebut = positionDebut;
    		this.positionFin = positionFin;
    		this.info = info;
    	}
    }
    
}
