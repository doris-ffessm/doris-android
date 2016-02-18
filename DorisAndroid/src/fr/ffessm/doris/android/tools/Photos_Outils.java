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

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.Disque_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.disk.DiskEnvironmentHelper;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;

public class Photos_Outils {
	private static final String LOG_TAG = Photos_Outils.class.getCanonicalName();
	
	public enum ImageType {
	    VIGNETTE,
	    MED_RES,
		HI_RES, 
		PORTRAITS, 
		ILLUSTRATION_DEFINITION, 
		ILLUSTRATION_BIBLIO
	} 

	public enum PrecharMode {
		// P0 : Aucune photo préchargée
	    P0,
	    // P1 : La photo principale en qualité vignette
	    P1, 
	    // P2 : Toutes les photos en qualité vignette
	    P2, 
	    // P3 : La photo principale en qualité intermédiaire, les autres en vignette
	    P3, 
	    // P4 : Toutes les photos en qualité intermédiaire
	    P4, 
	    // P5 : La photo principale en haute résolution, les autres en intermédiaire
	    P5, 
	    // P6 : Toutes les photos en haute résolution
	    P6 
	}
	
	
	private Context context;
	private Fiches_Outils fichesOutils;
	private Param_Outils paramOutils;
	private Disque_Outils disqueOutils;
	
	// [Disque] [ImageType]
	public int imagesNbInFolder[][] = new int [3] [10];
	
	// Constructeur
	public Photos_Outils(Context context){
		this.context = context;
		this.fichesOutils = new Fiches_Outils(context);
		this.paramOutils = new Param_Outils(context);
		this.disqueOutils = new Disque_Outils(context);
	}


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * Dossiers de Stockage des Images
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public File getImageFolderInPreferedLocation(ImageType inImageType) { 
		return getImageFolder(getPreferedLocation(), inImageType);
	}
	public File getImageFolder(ImageLocation baseImageLocation, ImageType inImageType) {
		switch (inImageType) {
		case VIGNETTE :
			return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_vignettes_fiches) );
		case MED_RES :
			return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_med_res_fiches) );
		case HI_RES :
			return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_hi_res_fiches) );
		case PORTRAITS :
			return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_portraits) );
		case ILLUSTRATION_DEFINITION :
			return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_illustration_definitions) );
		case ILLUSTRATION_BIBLIO :
			return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_illustration_biblio) );
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
		Log.d(LOG_TAG, "getFolderFromPreferedLocation("+ requestedSubFolder+") sur : "+baseImageLocation);
		
		String[] dossiers = requestedSubFolder.split("/");
		String dossierRacine = dossiers[0];
		
		switch(baseImageLocation){
		case PRIMARY:
			return DiskEnvironmentHelper.getPrimaryExternalStorage().getFilesDir(context, dossierRacine);
		case SECONDARY:
			try {
				return DiskEnvironmentHelper.getSecondaryExternalStorage(context).getFilesDir(context, dossierRacine);
				//return DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, requestedSubFolder);
			} catch (NoSecondaryStorageException e) {
				return context.getDir( dossierRacine , Context.MODE_PRIVATE);
			}
		case APP_INTERNAL: 
		default:
			Log.d(LOG_TAG, "getFolderFromPreferedLocation() : "+ context.getDir( "" , Context.MODE_PRIVATE));
			Log.d(LOG_TAG, "getFolderFromPreferedLocation() : "+ context.getDir( dossierRacine , Context.MODE_PRIVATE));
			return context.getDir( dossierRacine , Context.MODE_PRIVATE);
		}
	}

	
	public File getImageFolderVignette() {
		return getFolderFromPreferedLocation( context.getString(R.string.folder_vignettes_fiches) );
	}
	public File getImageFolderMedRes() { 
		return getFolderFromPreferedLocation( context.getString(R.string.folder_med_res_fiches) );
	}
	public File getImageFolderHiRes() { 
		return getFolderFromPreferedLocation( context.getString(R.string.folder_hi_res_fiches) );
	}
	public File getImageFolderPortraits() { 
		return getFolderFromPreferedLocation( context.getString(R.string.folder_portraits) );
	}
	public File getImageFolderGlossaire() { 
		return getFolderFromPreferedLocation( context.getString(R.string.folder_illustration_definitions) );
	}
	public File getImageFolderBiblio() { 
		return getFolderFromPreferedLocation( context.getString(R.string.folder_illustration_biblio) );
	}
	
	/**
	 * renvoie l'emplacement préféré si disponible, sinon emplacement par défaut : APP_INTERNAL
	 */
	public ImageLocation getPreferedLocation(){
		return ImageLocation.values()[
              paramOutils.getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
            		  ImageLocation.APP_INTERNAL.ordinal())
        		  ];
	}
	/**
	 * renvoie l'emplacement précédent si disponible, sinon emplacement par défaut : APP_INTERNAL
	 */
	public ImageLocation getLocationPrecedente(){
		return ImageLocation.values()[
              paramOutils.getParamInt(R.string.pref_key_prefered_disque_stockage_photo_precedent,
            		  ImageLocation.APP_INTERNAL.ordinal())
        		  ];
	}
	
	public void setPreferedLocation(ImageLocation preferedImageLocation){

		// On enregistre l'emplacement préféré précédent afin de pouvoir faire une reprise si
		// le traitement était interrompu avant sa fin
		paramOutils.setParamInt(R.string.pref_key_prefered_disque_stockage_photo_precedent,
				paramOutils.getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
						ImageLocation.APP_INTERNAL.ordinal() ) );
		
		paramOutils.setParamInt(R.string.pref_key_prefered_disque_stockage_photo,
				preferedImageLocation.ordinal());
				
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * URL de téléchargement des Images
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/*public String getbaseUrl(ImageType inImageType) { 
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
	}*/
	
	public String getImageUrl(String inPhotoUrl, ImageType inImageType) { 
		switch (inImageType) {
		case VIGNETTE:
			return Constants.IMAGE_BASE_URL+inPhotoUrl.replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.VIGNETTE_BASE_URL_SUFFIXE);
		case MED_RES:
			return Constants.IMAGE_BASE_URL+inPhotoUrl.replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.MOYENNE_BASE_URL_SUFFIXE);
		case HI_RES:
			return Constants.IMAGE_BASE_URL+inPhotoUrl;
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
	
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * Photos sur Disque
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	// Si on ne trouve pas le fichier dans le dossier, on regarde dans le sous-dossier
	// se nommant comme la 1ère lettre du fichier (en FAT32 on peut en avoir jusqu'à 65536
	// sauf que ce doit être de noms courts
	// Réellement ça pète vers 18000
	public File getSousDossierPhoto(File imageFolder, String inPhotoDisque){
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getSousDossierPhoto() - imageFolder : "+ imageFolder );
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getSousDossierPhoto() - inPhotoURL : "+ inPhotoURL );
		
		// Qd on télécharge, il y a un / devant les images des fiches mais pas pour les autres types d'images.
		// De plus, on appelle parfois cette fonction après avoir déjà supprimer le /, donc ici on l'enlève
		// qd il existe et on a toujours la même lettre
		
		return new File (imageFolder.getPath()+"/"+inPhotoDisque.charAt(0));
	}
	
	public boolean isAvailableInFolderPhoto(String inPhotoURL, ImageType inImageType){
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - inPhotoURL : "+ inPhotoURL );
		
		File imageFolder = getImageFolderInPreferedLocation(inImageType);

		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - imageFolder : "+ imageFolder.toString() );
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - imageFolder : "+ imageFolder.exists() );
		
		if(!inPhotoURL.isEmpty()){
			
			if (new File(imageFolder, inPhotoURL).exists()) return true;
			
			//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - getSousDossierPhoto : "+ getSousDossierPhoto(imageFolder, inPhotoURL) );
			//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - getSousDossierPhoto : "+ getSousDossierPhoto(imageFolder, inPhotoURL).exists() );
			File test = new File(
					getSousDossierPhoto(imageFolder, inPhotoURL),
					inPhotoURL);
			//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - test : "+ test.getAbsolutePath() );
			//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - test.exists() : "+ test.exists() );
			
			if (test.exists()
				) return true;
		}
		//if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - return false;");
		return false;
	}
	
	public File getPhotoFile(String inPhotoURL, ImageType inImageType) throws IOException{
		if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - photoURL : "+ inPhotoURL );
		
		File imageFolder = getImageFolderInPreferedLocation(inImageType);
		
		File fichierImage = new File(imageFolder, inPhotoURL);
		if (fichierImage.exists()) return fichierImage;
		
		fichierImage = new File( getSousDossierPhoto(imageFolder, inPhotoURL) ,inPhotoURL);
		if (fichierImage.exists()) return fichierImage;
		
		return null;
	}


	public HashSet<String> getAllPhotosAvailable(ImageType inImageType){
		return getAllFilesAvailable(getImageFolderInPreferedLocation(inImageType));
	}
	public HashSet<String> getAllFilesAvailable(File inDossier){
		if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getAllFilesAvailable() - inDossier : "+ inDossier.getPath() );
		HashSet<String> hsPhotosAvailable = new HashSet<String>();
		
		for (File file : inDossier.listFiles()) {
			if (file.isDirectory()){
				hsPhotosAvailable.addAll( getAllFilesAvailable(file) );
			} else {
				hsPhotosAvailable.add(file.getName());
			}
		}
		return hsPhotosAvailable;
	}
	
	
	
    private byte buffer[] = new byte[1024];
	InputStream input;
    OutputStream output;
    private int count;
    
	public void downloadPhotoFile(String photoUrl, ImageType imageType) throws IOException{

		switch (imageType) {
		case VIGNETTE:
		case MED_RES:
		case HI_RES:
			// Dans DORIS V4, les images des fiches sont dans des sous-dossiers se nomant presque comme l'image,
			// il est enregistré dans la base (dans le champs cleUrl), on ne garde donc que le dernier mot ici
			downloadPhotoFile(photoUrl,
								photoUrl.substring(photoUrl.lastIndexOf('/') + 1),
								imageType);
		case PORTRAITS:
		case ILLUSTRATION_DEFINITION :
		case ILLUSTRATION_BIBLIO :
			downloadPhotoFile(photoUrl,
								photoUrl,
								imageType);
		default:
		}
		
		
	}
	
	public void downloadPhotoFile(String inPhotoUrl, String inPhotoDisque, ImageType inImageType) throws IOException{
		//Log.d(LOG_TAG, "downloadPhotoFile() : "+imageType+" - "+photoUrl+" - "+photoDisque );
		if(!inPhotoUrl.isEmpty()){
			
			//File imageFolder = getImageFolderInPreferedLocation(inImageType);
			// Chaque image est stockée dans un sous dossier s'appelant comme la 1ère lettre du fichier
			File imageFolder = getSousDossierPhoto(
					getImageFolderInPreferedLocation(inImageType),
					inPhotoDisque
				);
			
	    	/* On crée les dossiers s'ils étaient inexistants */
			if (!imageFolder.exists() && !imageFolder.mkdirs()) {
	            throw new IOException("Cannot create dir " + imageFolder.getAbsolutePath());
	        }
			
	    	File fichierImage = new File(imageFolder, inPhotoDisque);
			if(!fichierImage.exists()){
		    
				URL urlHtml = null;
				try {
					urlHtml = new URL(
							//getbaseUrl(inImageType)+inPhotoUrl.replace(" ", "%20")
							getImageUrl(inPhotoUrl, inImageType).replace(" ", "%20")
						);
				} catch (MalformedURLException e ) {
					Log.w(LOG_TAG, e.getMessage(), e);
				}
				try {
					HttpURLConnection urlConnection = (HttpURLConnection) urlHtml.openConnection();
			        urlConnection.setConnectTimeout(3000);
			        urlConnection.setReadTimeout(10000);
			        
			        urlConnection.connect();
		            
		            // download the file
		            input = urlConnection.getInputStream();
		            Log.d(LOG_TAG, "downloadPhotoFile() : "+fichierImage.getCanonicalPath() );
		            output = new FileOutputStream(fichierImage);

		            while ( ( count = input.read(buffer) ) != -1) {
		                output.write(buffer, 0, count);
		            }

		            urlConnection.disconnect();
		            output.flush();
		            output.close();
		            input.close();
			        
				} catch (IOException e) {
					Log.w(LOG_TAG, e.getMessage(), e);
				}
				
			}
		}
		
	}

	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * Taille des Dossiers (Nb Fichiers)
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public void refreshImagesNbInFolder(){

		for (ImageLocation imageLocation : ImageLocation.values()) {
			// Pour chaque Type d'Images
			for (ImageType imageType : ImageType.values()) {
				// Primary et Secondary n existe pas forcément
				if (disqueOutils.isStorageExist(imageLocation)) {
					// Si le Dossier n existe pas 0, sinon on compte
					if(getImageFolder(imageLocation, imageType) == null){
						Log.w(LOG_TAG, "refreshImagesNbInFolder() : warning : folder doesn't exist for "+imageLocation+" - "+imageType + " but disqueOutils.isStorageExist doesn't detect it");
						imagesNbInFolder [imageLocation.ordinal()] [imageType.ordinal()] = 0;
					}else{
						if (! getImageFolder(imageLocation, imageType).exists() ) {
							imagesNbInFolder [imageLocation.ordinal()] [imageType.ordinal()] = 0;
						} else {
							imagesNbInFolder [imageLocation.ordinal()] [imageType.ordinal()] = 
								disqueOutils.nbFileInFolder(getImageFolder(imageLocation, imageType) );
						}
					}
				} else {
					imagesNbInFolder [imageLocation.ordinal()] [imageType.ordinal()] = 0;
				}
				Log.d(LOG_TAG, "refreshImagesNbInFolder() : "+imageLocation+" - "+imageType+" - "
						+imagesNbInFolder [imageLocation.ordinal()] [imageType.ordinal()] );
			}
		}
	}

	
	
	public int getImageCountInFolderInPreferedLocation(ImageType inImageType){
		return getImageCountInFolder(getPreferedLocation(), inImageType);
	}
	public int getImageCountInFolder(ImageLocation baseImageLocation, ImageType inImageType){
		return imagesNbInFolder[baseImageLocation.ordinal()] [inImageType.ordinal()];
	}	

	public int getImageCountInCache(){
		//	Log.d(LOG_TAG, "Photos_Outils() - getImageCountInCache() cahcelocation="+context.getCacheDir().getPath());
		File picasoFolder = new File(context.getCacheDir().getPath()+"/picasso-cache");
		if(picasoFolder.exists()) return picasoFolder.listFiles().length;
		else return 0;
	}
	
	
	public int getImageCountInAllFoldersInPreferedLocation(){
    	return getImageCountInAllFolders(getPreferedLocation());
	}
	public int getImageCountInAllFolders(ImageLocation baseImageLocation){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotosDiskUsage()");
    	return getImageCountInFolder(baseImageLocation, ImageType.VIGNETTE)
    			+ getImageCountInFolder(baseImageLocation, ImageType.MED_RES)
    			+ getImageCountInFolder(baseImageLocation, ImageType.HI_RES)
    			+ getImageCountInFolder(baseImageLocation, ImageType.PORTRAITS)
    			+ getImageCountInFolder(baseImageLocation, ImageType.ILLUSTRATION_BIBLIO)
    			+ getImageCountInFolder(baseImageLocation, ImageType.ILLUSTRATION_DEFINITION);
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * Taille des Dossiers (en Ko, Mo, etc.)
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */	
	public long getPhotosDiskUsageInPreferedLocation(){
    	return getPhotosDiskUsage(getPreferedLocation());
	}
	public long getPhotosDiskUsage(ImageLocation baseImageLocation){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotosDiskUsage()");
    	return getPhotoDiskUsage(baseImageLocation, ImageType.VIGNETTE)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.MED_RES)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.HI_RES)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.PORTRAITS)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.ILLUSTRATION_BIBLIO)
    			+ getPhotoDiskUsage(baseImageLocation, ImageType.ILLUSTRATION_DEFINITION);
	}
	
	
	public long getPhotoDiskUsageInPreferedLocation(ImageType inImageType) {
		return getPhotoDiskUsage(getPreferedLocation(), inImageType);
	}
	public long getPhotoDiskUsage(ImageLocation baseImageLocation, ImageType inImageType){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotoDiskUsage() - baseImageLocation : "+baseImageLocation.name());
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotoDiskUsage() - inImageType : "+inImageType.name());
    	return getImageCountInFolder(baseImageLocation, inImageType) * getTailleMoyImageUnitaire(inImageType) ;
	}


	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	 * 
	 * Gestion téléchargements
	 * 
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */	
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
    		return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_med_res));
    	case PORTRAITS :
    		return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_portraits));
    	case ILLUSTRATION_DEFINITION :
    		return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_illus_definiions));
    	case ILLUSTRATION_BIBLIO :
    		return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_illus_biblio));
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
		ormLiteDBHelper.close();
		
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
