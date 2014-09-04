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
import java.util.HashSet;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.activities.DetailEntreeGlossaire_ElementViewActivity;
import fr.ffessm.doris.android.activities.DetailsFiche_ElementViewActivity;
import fr.ffessm.doris.android.activities.DetailsParticipant_ElementViewActivity;
import fr.ffessm.doris.android.activities.Glossaire_ClassListViewActivity;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.disk.DiskEnvironment;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;

import android.content.Context;
import android.util.Log;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;

public class Photos_Outils {
	private static final String LOG_TAG = Photos_Outils.class.getCanonicalName();
	
	public static final String VIGNETTES_FICHE_FOLDER = "vignettes_fiches";
	public static final String MED_RES_FICHE_FOLDER = "medium_res_images_fiches";
	public static final String HI_RES_FICHE_FOLDER = "hi_res_images_fiches";
	public static final String PORTRAITS_FOLDER = "portraits";
	public static final String ILLUSTRATION_DEFINITION_FOLDER = "illustrations";
	public static final String ILLUSTRATION_BIBLIO_FOLDER = "biblio";
	
	private Context context;
	private Fiches_Outils fichesOutils;
	
	public Photos_Outils(Context context){
		this.context = context;
		this.fichesOutils = new Fiches_Outils(context);
	}
	
	public enum ImageType {
	    VIGNETTE, MED_RES, HI_RES, PORTRAITS, ILLUSTRATION_DEFINITION, ILLUSTRATION_BIBLIO
	} 

	/***
	 * P0 : Aucune photo préchargée
	 * P1 : La photo principale en qualité vignette
	 * P2 : Toutes les photos en qualité vignette
	 * P3 : La photo principale en qualité intermédiaire, les autres en vignette
	 * P4 : Toutes les photos en qualité intermédiaire
	 * P5 : La photo principale en haute résolution, les autres en intermédiaire
	 * P6 : Toutes les photos en haute résolution
	 */
	public enum PrecharMode {
	    P0, P1, P2, P3, P4, P5, P6 
	}
	
	// type pour le choix de l'emplacement des photos
	public enum ImageLocation {
		APP_INTERNAL, PRIMARY, SECONDARY
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
	public File getImageFolder(ImageLocation baseImageLocation, ImageType inImageType) {
		switch (inImageType) {
		case VIGNETTE :
			return getFolderFromBaseLocation(baseImageLocation, VIGNETTES_FICHE_FOLDER);
		case MED_RES :
			return getFolderFromBaseLocation(baseImageLocation, MED_RES_FICHE_FOLDER);
		case HI_RES :
			return getFolderFromBaseLocation(baseImageLocation, HI_RES_FICHE_FOLDER);
		case PORTRAITS :
			return getFolderFromBaseLocation(baseImageLocation, PORTRAITS_FOLDER);
		case ILLUSTRATION_DEFINITION :
			return getFolderFromBaseLocation(baseImageLocation, ILLUSTRATION_DEFINITION_FOLDER);
		case ILLUSTRATION_BIBLIO :
			return getFolderFromBaseLocation(baseImageLocation, ILLUSTRATION_BIBLIO_FOLDER);
		default:
			return null;
		}
	}
	/**
	 * Récupère le folder requis en utilisant les préférences utilisateur comme base
	 * Attention renvoie le disque interne si le disque secondaire n'est pas disponible
	 * Utiliser la fonction isPreferedLocationAvailable
	 * @param requestedSubFolder
	 * @return
	 */
	public File getFolderFromPreferedLocation(String requestedSubFolder) {
		//Log.d(LOG_TAG, "getFolderFromPreferedLocation("+ requestedSubFolder+") "+getPreferedLocation());
		return getFolderFromBaseLocation(getPreferedLocation(), requestedSubFolder);
	}
	
	public File getFolderFromBaseLocation(ImageLocation baseImageLocation, String requestedSubFolder) {
		//Log.d(LOG_TAG, "getFolderFromPreferedLocation("+ requestedSubFolder+") "+getPreferedLocation());
		switch(baseImageLocation){
		case PRIMARY:
			return DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, requestedSubFolder);
		case SECONDARY:
			try {
				return DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, requestedSubFolder);
			} catch (NoSecondaryStorageException e) {
				return context.getDir( requestedSubFolder , Context.MODE_PRIVATE);
			}
		case APP_INTERNAL: 
		default:
			return context.getDir( requestedSubFolder , Context.MODE_PRIVATE);
		}
	}
	
	public boolean isPreferedLocationAvailable(){
		switch(getPreferedLocation()){
		case SECONDARY:
			try {
				return DiskEnvironment.getSecondaryExternalStorage().isAvailable();
			} catch (NoSecondaryStorageException e) {
				return false;
			}
		default:
			return true;
		}
	}
	
	public File getImageFolderVignette() {
		return getFolderFromPreferedLocation( VIGNETTES_FICHE_FOLDER );
	}
	public File getImageFolderMedRes() { 
		return getFolderFromPreferedLocation( MED_RES_FICHE_FOLDER);
	}
	public File getImageFolderHiRes() { 
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageFolderHiRes() - context : " + context.toString() );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageFolderHiRes() - HI_RES_FICHE_FOLDER : " +HI_RES_FICHE_FOLDER);
		return getFolderFromPreferedLocation( HI_RES_FICHE_FOLDER);
	}
	public File getImageFolderPortraits() { 
		return getFolderFromPreferedLocation( PORTRAITS_FOLDER);
	}
	public File getImageFolderGlossaire() { 
		return getFolderFromPreferedLocation( ILLUSTRATION_DEFINITION_FOLDER );
	}
	public File getImageFolderBiblio() { 
		return getFolderFromPreferedLocation( ILLUSTRATION_BIBLIO_FOLDER );
	}
	
	/**
	 * renvoie l'emplacement préféré si disponible, sinon emplacement par défaut : APP_INTERNAL
	 */
	public ImageLocation getPreferedLocation(){
		Param_Outils paramOutil = new Param_Outils(context);
		
		return ImageLocation.values()[
              paramOutil.getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
            		  ImageLocation.APP_INTERNAL.ordinal())
        		  ];
		
	}
	
	public void setPreferedLocation(ImageLocation preferedImageLocation){
		Param_Outils paramOutil = new Param_Outils(context);
		
		// On enregistre l'emplacement préféré précédent afin de pouvoir faire une reprise si
		// le traitement était interrompu avant sa fin
		paramOutil.setParamInt(R.string.pref_key_prefered_disque_stockage_photo_precedent,
				paramOutil.getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
						ImageLocation.APP_INTERNAL.ordinal() ) );
		
		paramOutil.setParamInt(R.string.pref_key_prefered_disque_stockage_photo,
				preferedImageLocation.ordinal());
				
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
		Param_Outils paramOutils = new Param_Outils(context);
		
		switch(PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_region_ttzones,"P1"))){
		case P1 :
		case P2 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Vignettes" );
			return isAvailableInFolderPhoto(photofiche.getCleURL(), ImageType.VIGNETTE);
		case P3 :
		case P4 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Medium" );
			return isAvailableInFolderPhoto(photofiche.getCleURL(), ImageType.MED_RES);
		case P5 :
		case P6 :
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "isAvailableImagePhotoFiche() - Hight" );
	    	return isAvailableInFolderPhoto(photofiche.getCleURL(), ImageType.HI_RES);
		default:
			return false;
		}
	}

	public boolean isAvailableInFolderPhoto(String inPhotoURL, ImageType inImageType){
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailablePhoto() - inPhotoURL : "+ inPhotoURL );
		
		File imageFolder = getImageFolder(inImageType);	
		if(!inPhotoURL.isEmpty()){
			File fichierImage = new File(imageFolder, inPhotoURL);
			//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailablePhoto() - fichierImage : "+ fichierImage.toString() );
			if(fichierImage.exists()){
				return true;
			}
		}
		return false;
	}
	
	
	public File getPhotoFile(String photoURL, ImageType inImageType) throws IOException{
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - photoURL : "+ photoURL );
		
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
		
		if(!photoUrl.isEmpty()){
			File fichierImage = new File(getImageFolder(imageType), photoDisque);
			if(fichierImage.exists()){
				result = fichierImage;
			}
			else{
		    
				URL urlHtml = null;
				try {
					String urlNettoyee = getbaseUrl(imageType)+photoUrl;
					urlHtml = new URL(urlNettoyee.replace(" ", "%20"));
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


	public int getImageCountInFolder(ImageType inImageType){
		return getImageFolder(inImageType).list().length;
	}
	
	public long getPhotosDiskUsage(ImageLocation baseImageLocation){
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotosDiskUsage()");
    	return getPhotoDiskUsage(baseImageLocation, ImageType.VIGNETTE)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.MED_RES)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.HI_RES)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.PORTRAITS)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.ILLUSTRATION_BIBLIO)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.ILLUSTRATION_DEFINITION);
	}
	public long getPhotosDiskUsage(){
    	return getPhotoDiskUsage(ImageType.VIGNETTE)
    			+ getPhotoDiskUsage(ImageType.MED_RES)
    			+ getPhotoDiskUsage(ImageType.HI_RES)
    			+ getPhotoDiskUsage(ImageType.PORTRAITS)
    			+ getPhotoDiskUsage(ImageType.ILLUSTRATION_BIBLIO)
    			+ getPhotoDiskUsage(ImageType.ILLUSTRATION_DEFINITION);
	}
	
	public long getPhotoDiskUsage(ImageType inImageType,int pipot){
		Disque_Outils disqueOutils = new Disque_Outils(context);
    	return disqueOutils.getDiskUsage(getImageFolder(inImageType) );
	}
	public long getPhotoDiskUsage(ImageType inImageType){
    	return getImageFolder(inImageType).list().length * 8500 ;
	}
	
	
	public long getPhotoDiskUsage(ImageLocation baseImageLocation, ImageType inImageType){
		Disque_Outils disqueOutils = new Disque_Outils(context);
		Log.d(LOG_TAG, "getPhotoDiskUsage "+inImageType+" "+getImageFolder(inImageType));
    	return disqueOutils.getDiskUsage(getImageFolder(baseImageLocation, inImageType) );
	}
	
	public ImageType getImageQualityToDownload(boolean inPhotoPrincipale, ZoneGeographiqueKind inZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageQualityToDownload() - Début" );

		PrecharMode prechargementMode = getPrecharModeZoneGeo(inZoneGeo);
		
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
	
	public PrecharMode getPrecharModeZoneGeo(ZoneGeographiqueKind inZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharModeZoneGeo() - Début" );
		Param_Outils paramOutils = new Param_Outils(context);
		
		switch(inZoneGeo){
		case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
			return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france,"P1"));
		case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
			return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1"));
		case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
			return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac,"P1"));
		case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
			return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes,"P1"));
		case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
			return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno,"P1"));
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
	public int getAPrecharQteZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - data_nbphotos_atelecharger_france : "+getParamInt(context, R.string.pref_key_nbphotos_atelecharger_france, 0) );
		Param_Outils paramOutils = new Param_Outils(context);
		
		if (inPrincipale) {
			switch(inZoneGeo){
			case FAUNE_FLORE_TOUTES_ZONES :
				int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_france, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0 );
				return nbAPrechar;
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_france, 0 );
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0 );
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0 );
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0 );
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0 );
			default :
				return 0;
			}
		} else {
			switch(inZoneGeo){
			case FAUNE_FLORE_TOUTES_ZONES :
				int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_france, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_eaudouce, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_atlantno, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_indopac, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_caraibes, 0 );
				return nbAPrechar;
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_france, 0 );
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_eaudouce, 0 );
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_indopac, 0 );
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_caraibes, 0 );
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_atlantno, 0 );
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
	public int getDejaLaQteZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - data_nbphotos_recues_france : "+getParamInt(context, R.string.pref_key_nbphotos_recues_france, 0) );
		Param_Outils paramOutils = new Param_Outils(context);
		
		if (inPrincipale) {
			switch(inZoneGeo){
			case FAUNE_FLORE_TOUTES_ZONES :
				int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_france, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_eaudouce, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_atlantno, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_indopac, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_caraibes, 0 );
				return nbAPrechar;
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_france, 0 );
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_eaudouce, 0 );
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_indopac, 0 );
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_caraibes, 0 );
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_atlantno, 0 );
			default :
				return 0;
			}
		} else {
			switch(inZoneGeo){
			case FAUNE_FLORE_TOUTES_ZONES :
				int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_france, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_eaudouce, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_atlantno, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_indopac, 0 );
				nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_caraibes, 0 );
				return nbAPrechar;
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_france, 0 );
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_eaudouce, 0 );
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_indopac, 0 );
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_caraibes, 0 );
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_atlantno, 0 );
			default :
				return 0;
			}
		}
	}
	
	
	/**
	 * récupère l'id de la resource textuelle pour les clé des préférences pour récupérer ou stocker
	 * des info de photo à télécharger ou déjà téléchargée zone par zone, photo principale ou pas
	 * @param context
	 * @param inIdZoneGeo
	 * @param inPrincipale
	 * @return
	 */
	// TODO : C'est crado mais c'est rassemblé ici
	public int getKeyDataAPrecharZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale){
		if (inPrincipale) {
			switch(inZoneGeo){
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotosprinc_atelecharger_france;
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotosprinc_atelecharger_eaudouce;
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return R.string.pref_key_nbphotosprinc_atelecharger_indopac;
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return R.string.pref_key_nbphotosprinc_atelecharger_caraibes;
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return R.string.pref_key_nbphotosprinc_atelecharger_atlantno;
			default :
				return 0;
			}
		} else {
			switch(inZoneGeo){
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotos_atelecharger_france;
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotos_atelecharger_eaudouce;
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return R.string.pref_key_nbphotos_atelecharger_indopac;
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return R.string.pref_key_nbphotos_atelecharger_caraibes;
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return R.string.pref_key_nbphotos_atelecharger_atlantno;
			default :
				return 0;
			}
		}
	}
	// TODO : Crado aussi
	public int getKeyDataRecuesZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale){
		if (inPrincipale) {
			switch(inZoneGeo){
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotosprinc_recues_france;
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotosprinc_recues_eaudouce;
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return R.string.pref_key_nbphotosprinc_recues_indopac;
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return R.string.pref_key_nbphotosprinc_recues_caraibes;
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return R.string.pref_key_nbphotosprinc_recues_atlantno;
			default :
				return 0;
			}
		} else {
			switch(inZoneGeo){
			case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotos_recues_france;
			case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
				return R.string.pref_key_nbphotos_recues_eaudouce;
			case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
				return R.string.pref_key_nbphotos_recues_indopac;
			case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
				return R.string.pref_key_nbphotos_recues_caraibes;
			case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
				return R.string.pref_key_nbphotos_recues_atlantno;
			default :
				return 0;
			}
		}
	}
	
	
	
	public boolean isPrecharModeOnlyP0(){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		Param_Outils paramOutils = new Param_Outils(context);
		
		if ( PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france,"P1")) == PrecharMode.P0 
			&& PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac,"P1")) == PrecharMode.P0
			&& PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes,"P1")) == PrecharMode.P0
			) return true;
		return false;	
	}
	
	public boolean isPrecharModeOnlyP0orP1(){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		Param_Outils paramOutils = new Param_Outils(context);
		
		if ( ( PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france,"P1")) == PrecharMode.P0 
				|| PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac,"P1")) == PrecharMode.P1 )
			&& ( PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes,"P1")) == PrecharMode.P0
				|| PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes,"P1")) == PrecharMode.P1 )
			) return true;
		return false;	
	}
	

	public int getTailleMoyImageUnitaire(ImageType imageType) {
	   	switch (imageType) {
	   	case VIGNETTE :
    		return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_vignette));
    	case MED_RES :
    	case PORTRAITS :
    	case ILLUSTRATION_DEFINITION :
    	case ILLUSTRATION_BIBLIO :
    		return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_med_res));
		case HI_RES :
			return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_hi_res));
		default :
			return 0;
		}
	}
	
	/***
	 * Pour estimer le volume de photos à télécharger / stocker :
	 *   - on estime le nombre de photos
	 *   - on multiplie par une constante qui sur-estime un peu
	 */
	private int nbFichesTotal;
	private int nbPhotosTotal;
	private float nbPhotosParFiche;
	private int nbFichesZoneGeo[] = new int[ZoneGeographiqueKind.values().length];
	
	public void initNbPhotosParFiche() {
		nbFichesTotal = fichesOutils.getNbFichesZoneGeo(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES);
	
		OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
		RuntimeExceptionDao<PhotoFiche, Integer> entriesFicheDao = ormLiteDBHelper.getPhotoFicheDao();
		nbPhotosTotal = (int)entriesFicheDao.countOf();
		entriesFicheDao = null;
		
		// On calcule le nombre de photos (hors principales) moyen par fiche
		nbPhotosParFiche = ( nbPhotosTotal - nbFichesTotal ) / nbFichesTotal;
		
		for (ZoneGeographiqueKind zone : ZoneGeographiqueKind.values()){
			//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "initNbPhotosParFiche() - zone : "+zone);
			//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "initNbPhotosParFiche() - zone.ordinal() : "+zone.ordinal());
			nbFichesZoneGeo[zone.ordinal()] = fichesOutils.getNbFichesZoneGeo(zone);
		}
		
	}
	
	public long getEstimVolPhotosParZone(PrecharMode precharMode, ZoneGeographiqueKind inZoneGeo){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getEstimVolPhotosParZone() - precharMode : "+precharMode);
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getEstimVolPhotosParZone() - inZoneGeo : "+inZoneGeo);
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getEstimVolPhotosParZone() - nbFichesZoneGeo : "+nbFichesZoneGeo[inZoneGeo.ordinal()]);
		
		int volPhotosPrincipales = 0;
		int volPhotos = 0;
		
		switch(precharMode){
		case P0 :
			return 0;
		case P1 :
			volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE);
			return volPhotosPrincipales;
		case P2 :
			volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE);
			volPhotos = (int)(nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.VIGNETTE));
			return volPhotosPrincipales + volPhotos;
		case P3 :
			volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
								+ nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.MED_RES);
			volPhotos = (int)(nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.VIGNETTE));
			return volPhotosPrincipales + volPhotos;
		case P4 :
			volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
								+ nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.MED_RES);
			volPhotos = (int)(nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.MED_RES));
			return volPhotosPrincipales + volPhotos;
		case P5 :
			volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
								+ nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.HI_RES);
			volPhotos = (int)(nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.MED_RES));
			return volPhotosPrincipales + volPhotos;
		case P6 :
			volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
								+ nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.HI_RES);
			volPhotos = (int)(nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.HI_RES));
			return volPhotosPrincipales + volPhotos;
		default:
			return 0;
		}
		
	}

	
	public long getEstimVolPhotosAutres(){
		long nbPhotos = 0;
		int volPhotos = 0;
		OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
		
		try {
			nbPhotos = ormLiteDBHelper.getParticipantDao().countOf(
				ormLiteDBHelper.getParticipantDao().queryBuilder().setCountOf(true).where().not().eq("cleURLPhotoParticipant", "").prepare());
		} catch (SQLException error) {
			error.printStackTrace();
		}
		volPhotos += (int)nbPhotos * getTailleMoyImageUnitaire(ImageType.PORTRAITS);
		
		try {
			nbPhotos = ormLiteDBHelper.getEntreeBibliographieDao().countOf(
				ormLiteDBHelper.getEntreeBibliographieDao().queryBuilder().setCountOf(true).where().not().eq("cleURLIllustration", "").prepare());
		} catch (SQLException error) {
			error.printStackTrace();
		}
		volPhotos += (int)nbPhotos * getTailleMoyImageUnitaire(ImageType.ILLUSTRATION_BIBLIO);

		try {
			nbPhotos = ormLiteDBHelper.getDefinitionGlossaireDao().countOf(
				ormLiteDBHelper.getDefinitionGlossaireDao().queryBuilder().setCountOf(true).where().not().eq("cleURLIllustration", "").prepare());
		} catch (SQLException error) {
			error.printStackTrace();
		}
		volPhotos += (int)nbPhotos * getTailleMoyImageUnitaire(ImageType.ILLUSTRATION_DEFINITION);

		return volPhotos;
	}

			


	
}
