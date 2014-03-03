package fr.ffessm.doris.android.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;

public class Photos_Outils {
	private final String LOG_TAG = Photos_Outils.class.getCanonicalName();
	public final String VIGNETTES_FICHE_FOLDER = "vignettes_fiches";
	public final String MED_RES_FICHE_FOLDER = "medium_res_images_fiches";
	public final String HI_RES_FICHE_FOLDER = "hi_res_images_fiches";
	public final String PORTRAITS_FOLDER = "portraits";
	public final String ILLUSTRATION_DEFINITION_FOLDER = VIGNETTES_FICHE_FOLDER;
	public final String ILLUSTRATION_BIBLIO_FOLDER = VIGNETTES_FICHE_FOLDER;
	
	private Context context;
	
	public Photos_Outils(Context context){
		this.context = context;
	}
	
	public enum ImageType {
	    VIGNETTE, MED_RES, HI_RES, PORTRAITS, ILLUSTRATION_DEFINITION, ILLUSTRATION_BIBLIO
	} 

	public enum PrecharMode {
	    P0, P1, P2, P3, P4, P5, P6 
	}
	
	
	public File getImageFolder(ImageType inImageType) { 
		switch (inImageType) {
		case VIGNETTE :
			return getImageFolderVignette();
		case MED_RES :
			return getImageFolderMedRes();
		case HI_RES :
			return getImageFolderHiRes();
		case PORTRAITS :
			return getImageFolderPortraits();
		case ILLUSTRATION_DEFINITION :
			return getImageFolderGlossaire();
		case ILLUSTRATION_BIBLIO :
			return getImageFolderBiblio();
			default:
		return null;
		}
	}
	public File getImageFolderVignette() {
		return context.getDir( VIGNETTES_FICHE_FOLDER , Context.MODE_PRIVATE);
	}
	public File getImageFolderMedRes() { 
		return context.getDir( MED_RES_FICHE_FOLDER , Context.MODE_PRIVATE);
	}
	public File getImageFolderHiRes() { 
		return context.getDir( HI_RES_FICHE_FOLDER , Context.MODE_PRIVATE);
	}
	public File getImageFolderPortraits() { 
		return context.getDir( PORTRAITS_FOLDER , Context.MODE_PRIVATE);
	}
	public File getImageFolderGlossaire() { 
		return context.getDir( ILLUSTRATION_DEFINITION_FOLDER , Context.MODE_PRIVATE);
	}
	public File getImageFolderBiblio() { 
		return context.getDir( ILLUSTRATION_BIBLIO_FOLDER , Context.MODE_PRIVATE);
	}
	
	public String getbaseUrl(ImageType inImageType) { 
		switch (inImageType) {
		case VIGNETTE:
			return Constants.VIGNETTE_BASE_URL;
		case MED_RES:
			return Constants.MOYENNE_BASE_URL;
		case HI_RES:
			return Constants.GRANDE_BASE_URL;
		case PORTRAITS:
			return Constants.PORTRAIT_BASE_URL;
		case ILLUSTRATION_DEFINITION :
			return Constants.ILLUSTRATION_DEFINITION_BASE_URL;
		case ILLUSTRATION_BIBLIO :
			return Constants.ILLUSTRATION_BIBLIO_BASE_URL;
		default:
			return "";
		}
	}
	
	public boolean isAvailableImagePhotoFiche(PhotoFiche photofiche){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - photofiche : "+ photofiche );
    	
		switch(PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_region_ttzones,"P1"))){
		case P1 :
		case P2 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Vignettes" );
			return isAvailablePhoto(photofiche.getCleURL(), ImageType.VIGNETTE);
		case P3 :
		case P4 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Medium" );
			return isAvailablePhoto(photofiche.getCleURL(), ImageType.MED_RES);
		case P5 :
		case P6 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Hight" );
	    	return isAvailablePhoto(photofiche.getCleURL(), ImageType.HI_RES);
		default:
			return false;
		}
	}

	public boolean isAvailablePhoto(String inPhotoURL, ImageType inImageType){
		File imageFolder = getImageFolder(inImageType);	
		if(!inPhotoURL.isEmpty()){
			File fichierImage = new File(imageFolder, inPhotoURL);
			if(fichierImage.exists()){
				return true;
			}
		}
		return false;
	}
	
	
	public File getPhotoFile(String photoURL, ImageType inImageType) throws IOException{		
		File imageFolder = getImageFolder(inImageType);		
		return new File(imageFolder, photoURL);
	}
	

	public HashSet<String> getAllPhotosAvailable(ImageType inImageType){
		HashSet<String> hsPhotosAvailable = new HashSet<String>();
		File imageFolder = getImageFolder(inImageType);		
		for (File file : imageFolder.listFiles()) {
			hsPhotosAvailable.add(file.getName());
		}
		return hsPhotosAvailable;
	}
	
	public File getOrDownloadPhotoFile(String photoUrl, ImageType imageType) throws IOException{
		return getOrDownloadPhotoFile(photoUrl, photoUrl, imageType);
	}
	
	public File getOrDownloadPhotoFile(String photoUrl, String photoDisque, ImageType imageType) throws IOException{
		File result = null;	
		String baseUrl = getbaseUrl(imageType);
		File imageFolder = getImageFolder(imageType);
		
		if(!photoUrl.isEmpty()){
			File fichierImage = new File(imageFolder, photoDisque);
			if(fichierImage.exists()){
				result = fichierImage;
			}
			else{
		    
				URL urlHtml = null;
				try {
					String urlNettoyee = baseUrl+photoUrl;
					urlNettoyee = urlNettoyee.replace(" ", "%20");
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


	public int getImageCount(ImageType inImageType){
		return getImageFolder(inImageType).list().length;
	}
	
	public long getPhotosDiskUsage(){
    	return getPhotoDiskUsage(ImageType.VIGNETTE)
    			+ getPhotoDiskUsage(ImageType.MED_RES)
    			+ getPhotoDiskUsage(ImageType.HI_RES)
    			+ getPhotoDiskUsage(ImageType.PORTRAITS);
	}
	public long getPhotoDiskUsage(ImageType inImageType){
    	return Outils.getDiskUsage(context, getImageFolder(inImageType) );
	}


	
	public ImageType getImageQualityToDownload(boolean inPhotoPrincipale, int inIdZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageQualityToDownload() - Début" );

		PrecharMode prechargementMode = getPrecharModeZoneGeo(inIdZoneGeo);
		
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
	
	public PrecharMode getPrecharModeZoneGeo(int inIdZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharModeZoneGeo() - Début" );
		
		switch(inIdZoneGeo){
		case 1 :
			return PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_france,"P1"));
		case 2 :
			return PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1"));
		case 3 :
			return PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_indopac,"P1"));
		case 4 :
			return PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_caraibes,"P1"));
		case 5 :
			return PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_atlantno,"P1"));
		default :
			return null;
		}
	}
	
	/**
	 * récupère le nombre déclaré de photo à précharger
	 * La déclaration est stockée dans les préférences
	 * zone par zone, principale ou pas
	 * @param context context permettant de récupérer l'info depuis les préférences
	 * @param inIdZoneGeo 
	 * @param inPrincipale concerne la photo principale ou n'importe quelle photo
	 * @return
	 */
	public int getAPrecharQteZoneGeo(int inIdZoneGeo, Boolean inPrincipale){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - data_nbphotos_atelecharger_france : "+getParamInt(context, R.string.pref_key_nbphotos_atelecharger_france, 0) );
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_france, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_france, 0 );
			case 2 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0 );
			case 3 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0 );
			case 4 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0 );
			case 5 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0 );
			default :
				return 0;
			}
		} else {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_france, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_eaudouce, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_atlantno, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_indopac, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_france, 0 );
			case 2 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_eaudouce, 0 );
			case 3 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_indopac, 0 );
			case 4 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_caraibes, 0 );
			case 5 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_atelecharger_atlantno, 0 );
			default :
				return 0;
			}
		}
	}
	
	/**
	 * récupère le nombre de photos déclarées présentes (stockées dans les préférences)
	 * zone par zone, principale ou pas
	 * @param context
	 * @param inIdZoneGeo
	 * @param inPrincipale concerne la photo principale ou n'importe quelle photo
	 * @return
	 */
	public int getDejaLaQteZoneGeo(int inIdZoneGeo, Boolean inPrincipale){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - data_nbphotos_recues_france : "+getParamInt(context, R.string.pref_key_nbphotos_recues_france, 0) );
		if (inPrincipale) {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_france, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_eaudouce, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_atlantno, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_indopac, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_france, 0 );
			case 2 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_eaudouce, 0 );
			case 3 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_indopac, 0 );
			case 4 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_caraibes, 0 );
			case 5 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotosprinc_recues_atlantno, 0 );
			default :
				return 0;
			}
		} else {
			switch(inIdZoneGeo){
			case -1 :
				int nbAPrechar = Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_france, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_eaudouce, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_atlantno, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_indopac, 0 );
				nbAPrechar += Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_caraibes, 0 );
				return nbAPrechar;
			case 1 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_france, 0 );
			case 2 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_eaudouce, 0 );
			case 3 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_indopac, 0 );
			case 4 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_caraibes, 0 );
			case 5 :
				return Outils.getParamInt(context, R.string.pref_key_nbphotos_recues_atlantno, 0 );
			default :
				return 0;
			}
		}
	}
	
	
	/**
	 * récupère l'id de la resource textuelle pour les clé des préférences pour récupérer ou stocker des info de photo à télécharger ou déjà téléchargée
	 * zone par zone, photo principale ou pas
	 * @param context
	 * @param inIdZoneGeo
	 * @param inPrincipale
	 * @return
	 */
	// TODO : C'est crado mais c'est rassemblé ici
	public int getKeyDataAPrecharZoneGeo(int inIdZoneGeo, Boolean inPrincipale){
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
	public int getKeyDataRecuesZoneGeo(int inIdZoneGeo, Boolean inPrincipale){
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
	
	
	
	public boolean isPrecharModeOnlyP0(){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		
		if ( PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_france,"P1")) == PrecharMode.P0 
			&& PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_atlantno,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_indopac,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_caraibes,"P1")) == PrecharMode.P0
			) return true;
		return false;	
	}
	
	public boolean isPrecharModeOnlyP0orP1(){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		
		if ( ( PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_france,"P1")) == PrecharMode.P0 
				|| PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_france,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_atlantno,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_atlantno,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_indopac,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_indopac,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_caraibes,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(Outils.getParamString(context, R.string.pref_key_mode_precharg_photo_region_caraibes,"P1")) == PrecharMode.P1 )
			) return true;
		return false;	
	}
	
	
	
    // TODO : En attendant d'obtenir la nouvelle version de Common
	public String getZoneIcone(int inId) {
	   	switch (inId) {
	   	case -1:
    		return context.getString(R.string.icone_touteszones);
    	case 1:
    		return context.getString(R.string.icone_france);
		case 2:
			return context.getString(R.string.icone_eaudouce);
		case 3:
			return context.getString(R.string.icone_indopac);
		case 4:
			return context.getString(R.string.icone_caraibes);
		case 5:
			return context.getString(R.string.icone_atlantno);
		default:
			return "";
		}
	}
   
    
}
