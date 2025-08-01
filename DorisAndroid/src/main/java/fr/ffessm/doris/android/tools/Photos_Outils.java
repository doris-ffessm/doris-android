/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.dao.DaoManager;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.Disque_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.disk.DiskEnvironmentHelper;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;

/**
 * Attention certaines fonctions se basent sur imagesNbInFolder qui n'est calculé qu'une fois par défaut
 * pour faire des estimations de l'espace disque utilisé,
 * si des téléchargements sont en cours, il faut éventuellement relancer refreshImagesNbInFolder();
 */
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

    private OrmLiteDBHelper dbHelper;
    private DorisDBHelper dorisDBHelper;

    // [Disque] [ImageType]
    public int imagesNbInFolder[][] = new int[3][10];

    // assure que les utilisations de  imagesNbInFolder soit initialisé au moins une fois via l'appel de refreshImagesNbInFolder()
    private boolean imagesNbInFolderIsInitialized = false;

    // Constructeur
    public Photos_Outils(Context context) {
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
            case VIGNETTE:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_vignettes_fiches));
            case MED_RES:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_med_res_fiches));
            case HI_RES:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_hi_res_fiches));
            case PORTRAITS:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_portraits));
            case ILLUSTRATION_DEFINITION:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_illustration_definitions));
            case ILLUSTRATION_BIBLIO:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_illustration_biblio));
            default:
                return null;
        }
    }


    // Temporaire : on commence par regarder si le fichier ne serait pas de le dossier de la version précédente
    // Si c'était le cas, on déplace l'image plutôt que de la télécharger
    public File getImageFolderInPreferedLocationAnc(ImageType inImageType) {
        return getImageFolderAnc(getPreferedLocation(), inImageType);
    }

    public File getImageFolderAnc(ImageLocation baseImageLocation, ImageType inImageType) {
        switch (inImageType) {
            case VIGNETTE:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_anc_vignettes_fiches));
            case MED_RES:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_anc_med_res_fiches));
            case HI_RES:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_anc_hi_res_fiches));
            case PORTRAITS:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_anc_portraits));
            case ILLUSTRATION_DEFINITION:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_anc_illustration_definitions));
            case ILLUSTRATION_BIBLIO:
                return getFolderFromBaseLocation(baseImageLocation, context.getString(R.string.folder_anc_illustration_biblio));
            default:
                return null;
        }
    }


    /**
     * Récupère le folder requis en utilisant les préférences utilisateur comme base
     * Attention renvoie le disque interne si le disque secondaire n'est pas disponible
     * Utiliser la fonction isPreferedLocationAvailable
     *
     * @param requestedSubFolder
     * @return
     */
    public File getFolderFromPreferedLocation(String requestedSubFolder) {
        //Log.d(LOG_TAG, "getFolderFromPreferedLocation("+ requestedSubFolder+") "+getPreferedLocation());
        return getFolderFromBaseLocation(getPreferedLocation(), requestedSubFolder);
    }

    public File getFolderFromBaseLocation(ImageLocation baseImageLocation, String requestedSubFolder) {
        //Log.d(LOG_TAG, "getFolderFromPreferedLocation("+ requestedSubFolder+") sur : "+baseImageLocation);

        String[] dossiers = requestedSubFolder.split("/");
        String dossierRacine = dossiers[0];
        //Log.d(LOG_TAG, "getFolderFromPreferedLocation() - dossierRacine : "+ dossierRacine);

        switch (baseImageLocation) {
            case PRIMARY:
                return DiskEnvironmentHelper.getPrimaryExternalStorage().getFilesDir(context, dossierRacine);
            case SECONDARY:
                try {
                    return DiskEnvironmentHelper.getSecondaryExternalStorage(context).getFilesDir(context, dossierRacine);
                    //return DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, requestedSubFolder);
                } catch (NoSecondaryStorageException e) {
                    return context.getDir(dossierRacine, Context.MODE_PRIVATE);
                }
            case APP_INTERNAL:
            default:
                //Log.d(LOG_TAG, "getFolderFromPreferedLocation() - 1 : "+ context.getDir( "" , Context.MODE_PRIVATE));
                //Log.d(LOG_TAG, "getFolderFromPreferedLocation() - 2 : "+ context.getDir( dossierRacine , Context.MODE_PRIVATE));
                return context.getDir(dossierRacine, Context.MODE_PRIVATE);
        }
    }


    public File getImageFolderVignette() {
        return getFolderFromPreferedLocation(context.getString(R.string.folder_vignettes_fiches));
    }

    public File getImageFolderMedRes() {
        return getFolderFromPreferedLocation(context.getString(R.string.folder_med_res_fiches));
    }

    public File getImageFolderHiRes() {
        return getFolderFromPreferedLocation(context.getString(R.string.folder_hi_res_fiches));
    }

    public File getImageFolderPortraits() {
        return getFolderFromPreferedLocation(context.getString(R.string.folder_portraits));
    }

    public File getImageFolderGlossaire() {
        return getFolderFromPreferedLocation(context.getString(R.string.folder_illustration_definitions));
    }

    public File getImageFolderBiblio() {
        return getFolderFromPreferedLocation(context.getString(R.string.folder_illustration_biblio));
    }

    /**
     * renvoie l'emplacement préféré si disponible, sinon emplacement par défaut : APP_INTERNAL
     */
    public ImageLocation getPreferedLocation() {
        return ImageLocation.values()[
                paramOutils.getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
                        ImageLocation.APP_INTERNAL.ordinal())
                ];
    }

    /**
     * renvoie l'emplacement précédent si disponible, sinon emplacement par défaut : APP_INTERNAL
     */
    public ImageLocation getLocationPrecedente() {
        return ImageLocation.values()[
                paramOutils.getParamInt(R.string.pref_key_prefered_disque_stockage_photo_precedent,
                        ImageLocation.APP_INTERNAL.ordinal())
                ];
    }

    public void setPreferedLocation(ImageLocation preferedImageLocation) {

        // On enregistre l'emplacement préféré précédent afin de pouvoir faire une reprise si
        // le traitement était interrompu avant sa fin
        paramOutils.setParamInt(R.string.pref_key_prefered_disque_stockage_photo_precedent,
                paramOutils.getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
                        ImageLocation.APP_INTERNAL.ordinal()));

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
                return Constants.IMAGE_BASE_URL + inPhotoUrl.replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.VIGNETTE_BASE_URL_SUFFIXE);
            case MED_RES:
                return Constants.IMAGE_BASE_URL + inPhotoUrl.replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.MOYENNE_BASE_URL_SUFFIXE);
            case HI_RES:
                return Constants.IMAGE_BASE_URL + inPhotoUrl;
            case PORTRAITS:
                return Constants.PORTRAIT_BASE_URL;
            case ILLUSTRATION_DEFINITION:
                return Constants.ILLUSTRATION_DEFINITION_BASE_URL;
            case ILLUSTRATION_BIBLIO:
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
    // se nommant comme la 1ère lettre du fichier (en FAT32 on peut avoir jusqu'à 65536
    // sauf que ce doit être de noms courts
    // Réellement ça pète vers 18000
    public File getSousDossierPhoto(File imageFolder, String inPhotoDisque) {
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getSousDossierPhoto() - imageFolder : "+ imageFolder );
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getSousDossierPhoto() - inPhotoURL : "+ inPhotoURL );

        // Qd on télécharge, il y a un / devant les images des fiches mais pas pour les autres types d'images.
        // De plus, on appelle parfois cette fonction après avoir déjà supprimer le /, donc ici on l'enlève
        // qd il existe et on a toujours la même lettre

        return new File(imageFolder.getPath() + "/" + inPhotoDisque.charAt(0));
    }

    public boolean isAvailableInFolderPhoto(String inPhotoURL, ImageType inImageType) {
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - inPhotoURL : "+ inPhotoURL );

        String photoNom = inPhotoURL.substring(inPhotoURL.lastIndexOf('/') + 1);
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - photoNom : "+ photoNom );


        File imageFolder = getImageFolderInPreferedLocation(inImageType);

        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - imageFolder : "+ imageFolder.toString() );
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - imageFolder : "+ imageFolder.exists() );

        if (!inPhotoURL.isEmpty()) {

            //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - getSousDossierPhoto : "
            //										+ getSousDossierPhoto(imageFolder, photoNom) );
            //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - getSousDossierPhoto : "
            //												+ getSousDossierPhoto(imageFolder, photoNom).exists() );
            File test = new File(getSousDossierPhoto(imageFolder, photoNom), photoNom);
            //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - test : "+ test.getAbsolutePath() );
            //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - test.exists() : "+ test.exists() );

            if (test.exists()
            ) return true;
        }
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - return false;");
        return false;
    }

    public File getPhotoFile(String photoNom, ImageType inImageType) throws IOException {
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - photoURL : "+ photoNom );

        File imageFolder = getImageFolderInPreferedLocation(inImageType);

        File fichierImage = new File(imageFolder, photoNom);
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - fichierImage.exists() : "+fichierImage.exists());
        if (fichierImage.exists()) return fichierImage;

        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - getSousDossierPhoto(...) : "+getSousDossierPhoto(imageFolder, photoNom));
        fichierImage = new File(getSousDossierPhoto(imageFolder, photoNom), photoNom);
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - fichierImage.exists() : "+fichierImage.exists());
        if (fichierImage.exists()) return fichierImage;

        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getPhotoFile() - return null");
        return null;
    }


    public HashSet<String> getAllPhotosAvailable(ImageType inImageType) {
        return getAllFilesAvailable(getImageFolderInPreferedLocation(inImageType));
    }

    public HashSet<String> getAllFilesAvailable(File inDossier) {
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "getAllFilesAvailable() - inDossier : "+ inDossier.getPath() );
        HashSet<String> hsPhotosAvailable = new HashSet<>();

        for (File file : inDossier.listFiles()) {
            if (file.isDirectory()) {
                hsPhotosAvailable.addAll(getAllFilesAvailable(file));
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

    /**
     * Télécharge la photo en utuilisant les informations de  post fix si disponible et nécessaire
     *
     * @param photoPath
     * @param imageType
     * @param imgPostfixCodesString
     * @throws IOException
     */
    public void downloadPostFixedPhotoFile(String photoPath, ImageType imageType, String imgPostfixCodesString) throws IOException {
        //if (BuildConfig.DEBUG) Log.i(LOG_TAG, "downloadPhotoFile() - imageType : "+imageType.name());
        // !! split -1 car https://stackoverflow.com/questions/14602062/java-string-split-removed-empty-values
        String[] imgPostfixCodes = imgPostfixCodesString.split("&", -1);
        switch (imageType) {
            case VIGNETTE:
                if (imgPostfixCodes.length > 0 && imgPostfixCodes[0].length() > 0) {
                    // on a un postfix code
                    String postfix = Constants.ImagePostFixCode.getEnumFromCode(imgPostfixCodes[0]).getPostFix();
                    downloadPhotoFile(Constants.IMAGE_BASE_URL + "/"
                                    + photoPath.replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE + "$", postfix),
                            photoPath.substring(photoPath.lastIndexOf('/') + 1),
                            imageType
                    );
                } else {
                    // pas de code, on considère l'image principale comme vignette
                    // !! on ne la télécharge pas dans vignette mais dans HI_RES, sinon quand elle deviendra officielle on ne saura pas l'effacer
                    // on préfère que ce soit géré lors de l'usage
                    downloadPostFixedPhotoFile(photoPath, ImageType.HI_RES, "");
                }
                break;
            case MED_RES:
                // Dans DORIS V4, les images des fiches sont dans des sous-dossiers se nommant presque comme l'image,
                // il est enregistré dans la base (dans le champs cleUrl), on ne garde donc que le dernier mot ici
                if (imgPostfixCodes.length > 1 && imgPostfixCodes[1].length() > 0) {
                    // on a un postfix code
                    String postfix = Constants.ImagePostFixCode.getEnumFromCode(imgPostfixCodes[1]).getPostFix();
                    downloadPhotoFile(Constants.IMAGE_BASE_URL + "/"
                                    + photoPath.replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE + "$", postfix),
                            photoPath.substring(photoPath.lastIndexOf('/') + 1),
                            imageType
                    );
                } else {
                    // pas de code, on considère l'image principale comme vignette
                    // !! on ne la télécharge pas dans vignette mais dans HI_RES, sinon quand elle deviendra officielle on ne saura pas l'effacer
                    // on préfère que ce soit géré lors de l'usage
                    downloadPostFixedPhotoFile(photoPath, ImageType.HI_RES, "");
                }
                break;
            case HI_RES:
                downloadPhotoFile(Constants.IMAGE_BASE_URL + "/"
                                + photoPath.replaceAll(
                        Constants.IMAGE_BASE_URL_SUFFIXE + "$", Constants.GRANDE_BASE_URL_SUFFIXE),
                        photoPath.substring(photoPath.lastIndexOf('/') + 1),
                        imageType
                );
                break;
            case PORTRAITS:
            case ILLUSTRATION_DEFINITION:
            case ILLUSTRATION_BIBLIO:
                downloadPhotoFile(photoPath,
                        photoPath,
                        imageType);
                break;
            default:
        }
    }

    /**
     * Télécharge la photo à l'url indiquée (sans modification) et la copie dans l'emplacement
     *
     * @param inPhotoUrl
     * @param inPhotoDisque
     * @param inImageType
     * @return
     * @throws IOException
     */
    public boolean downloadPhotoFile(String inPhotoUrl, String inPhotoDisque, ImageType inImageType) throws IOException {
        //Log.d(LOG_TAG, "downloadPhotoFile() : "+inImageType+" - "+inPhotoUrl+" - "+inPhotoDisque );
        if (!inPhotoUrl.isEmpty()) {

            //File imageFolder = getImageFolderInPreferedLocation(inImageType);
            // Chaque image est stockée dans un sous dossier s'appelant comme la 1ère lettre du fichier
            File imageFolder = getSousDossierPhoto(
                    getImageFolderInPreferedLocation(inImageType),
                    inPhotoDisque
            );

            /* On crée les dossiers s'ils étaient inexistants */
            //Log.i(LOG_TAG, "downloadPhotoFile() - dossierDestination : "+ imageFolder );
            if (!imageFolder.exists() && !imageFolder.mkdirs()) {
                throw new IOException("Cannot create dir " + imageFolder.getAbsolutePath());
            }

            File fichierImage = new File(imageFolder, inPhotoDisque);
            if (!fichierImage.exists()) {

                // Temporaire : on commence par regarder si le fichier ne serait pas de le dossier de la versoin précédente
                // Si c'était le cas, on déplace l'image plutôt que de la télécharger
                File imageFolderAnc = getSousDossierPhoto(
                        getImageFolderInPreferedLocationAnc(inImageType),
                        inPhotoDisque
                );
                File fichierImageAnc = new File(imageFolderAnc, inPhotoDisque);
                //Log.d(LOG_TAG, "downloadPhotoFile() - fichierImageAnc.getPath() : "+fichierImageAnc.getPath());
                //Log.d(LOG_TAG, "downloadPhotoFile() - fichierImageAnc.exists() : "+fichierImageAnc.exists());
                if (fichierImageAnc.exists()) {

                    //Log.i(LOG_TAG, "downloadPhotoFile() - fichierImageAnc.AbsolutePath : "+ fichierImageAnc.getAbsolutePath() );
                    //Log.i(LOG_TAG, "downloadPhotoFile() - fichierImageAnc.Name : "+ fichierImageAnc.getName() );

                    input = new FileInputStream(fichierImageAnc);
                    output = new FileOutputStream(fichierImage);

                    while ((count = input.read(buffer)) > 0) {
                        output.write(buffer, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();

                    return true;
                } else {
                    URL urlHtml = null;
                    try {
                        urlHtml = new URL(
                                //getbaseUrl(inImageType)+inPhotoUrl.replace(" ", "%20")
                                //getImageUrl(inPhotoUrl, inImageType).replace(" ", "%20")
                                inPhotoUrl.replace(" ", "%20")
                        );
                    } catch (MalformedURLException e) {
                        Log.w(LOG_TAG, e.getMessage(), e);
                        return false;
                    }

                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) urlHtml.openConnection();
                        urlConnection.setConnectTimeout(3000);
                        urlConnection.setReadTimeout(10000);

                        urlConnection.connect();

                        // download the file
                        input = urlConnection.getInputStream();
                        //Log.d(LOG_TAG, "downloadPhotoFile() - fichierImage.getCanonicalPath() : "+fichierImage.getCanonicalPath() );
                        output = new FileOutputStream(fichierImage);

                        while ((count = input.read(buffer)) != -1) {
                            output.write(buffer, 0, count);
                        }

                        urlConnection.disconnect();
                        output.flush();
                        output.close();
                        input.close();

                    } catch (IOException e) {
                        Log.w(LOG_TAG, e.getMessage(), e);
                        return false;
                    }
                }
            }


        } else {
            return false;
        }


        Log.d(LOG_TAG, "downloadPhotoFile() - Fin " + inPhotoUrl);
        return true;
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * Taille des Dossiers (Nb Fichiers)
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public void refreshImagesNbInFolder() {
        //Log.d(LOG_TAG, "refreshImagesNbInFolder() - Début");

        for (ImageLocation imageLocation : ImageLocation.values()) {
            // Pour chaque Type d'Images
            for (ImageType imageType : ImageType.values()) {
                // Primary et Secondary n existe pas forcément
                if (disqueOutils.isStorageExist(imageLocation)) {
                    // Si le Dossier n existe pas 0, sinon on compte
                    if (getImageFolder(imageLocation, imageType) == null) {
                        Log.w(LOG_TAG, "refreshImagesNbInFolder() : warning : folder doesn't exist for " + imageLocation + " - " + imageType + " but disqueOutils.isStorageExist doesn't detect it");
                        imagesNbInFolder[imageLocation.ordinal()][imageType.ordinal()] = 0;
                    } else {
                        if (!getImageFolder(imageLocation, imageType).exists()) {
                            imagesNbInFolder[imageLocation.ordinal()][imageType.ordinal()] = 0;
                        } else {
                            imagesNbInFolder[imageLocation.ordinal()][imageType.ordinal()] =
                                    disqueOutils.nbFileInFolder(getImageFolder(imageLocation, imageType));
                        }
                    }
                } else {
                    imagesNbInFolder[imageLocation.ordinal()][imageType.ordinal()] = 0;
                }
                Log.d(LOG_TAG, "refreshImagesNbInFolder() : " + imageLocation + " - " + imageType + " - "
                        + imagesNbInFolder[imageLocation.ordinal()][imageType.ordinal()]);
            }
        }

        imagesNbInFolderIsInitialized = true;
    }


    public int getImageCountInFolderInPreferedLocation(ImageType inImageType) {
        return getImageCountInFolder(getPreferedLocation(), inImageType);
    }

    public int getImageCountInFolder(ImageLocation baseImageLocation, ImageType inImageType) {
        //Log.d(LOG_TAG, "getImageCountInFolder() - Début");
        if (!imagesNbInFolderIsInitialized) refreshImagesNbInFolder();
        return imagesNbInFolder[baseImageLocation.ordinal()][inImageType.ordinal()];
    }

    public int getImageCountInCache() {
        //	Log.d(LOG_TAG, "Photos_Outils() - getImageCountInCache() cahcelocation="+context.getCacheDir().getPath());
        File picasoFolder = new File(context.getCacheDir().getPath() + "/picasso-cache");
        if (picasoFolder.exists()) return picasoFolder.listFiles().length;
        else return 0;
    }


    public int getImageCountInAllFoldersInPreferedLocation() {
        return getImageCountInAllFolders(getPreferedLocation());
    }

    public int getImageCountInAllFolders(ImageLocation baseImageLocation) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getImageCountInAllFolders()");
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

    /**
     * !!! se base ImagesNbInFolder sur pour une mesure plus précise en cas de téléchargement en cours il faut
     * rappeler refreshImagesNbInFolder() ai été appelé au moins une fois !
     *
     * @return
     */
    public long getPhotosDiskUsageInPreferedLocation() {
        return getPhotosDiskUsage(getPreferedLocation());
    }

    public long getPhotosDiskUsage(ImageLocation baseImageLocation) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotosDiskUsage()");
        return getPhotoDiskUsage(baseImageLocation, ImageType.VIGNETTE)
                + getPhotoDiskUsage(baseImageLocation, ImageType.MED_RES)
                + getPhotoDiskUsage(baseImageLocation, ImageType.HI_RES)
                + getPhotoDiskUsage(baseImageLocation, ImageType.PORTRAITS)
                + getPhotoDiskUsage(baseImageLocation, ImageType.ILLUSTRATION_BIBLIO)
                + getPhotoDiskUsage(baseImageLocation, ImageType.ILLUSTRATION_DEFINITION);
    }

    public String getCurrentPhotosDiskUsageShortSummary(Context context) {
        StringBuilder sb = new StringBuilder();
        switch (getPreferedLocation()) {
            case APP_INTERNAL:
                sb.append(context.getString(R.string.etatmodehorsligne_diskselection_internal_libelle));
                break;
            case PRIMARY:
                sb.append(context.getString(R.string.etatmodehorsligne_diskselection_primary_libelle));
                break;
            case SECONDARY:
                sb.append(context.getString(R.string.etatmodehorsligne_diskselection_secondary_libelle));
                break;
        }
        sb.append("[~");
        sb.append(new Disque_Outils(context).getHumanDiskUsage(getPhotosDiskUsageInPreferedLocation()));
        sb.append("]");
        return sb.toString();
    }

    public long getPhotoDiskUsageInPreferedLocation(ImageType inImageType) {
        return getPhotoDiskUsage(getPreferedLocation(), inImageType);
    }

    public long getPhotoDiskUsage(ImageLocation baseImageLocation, ImageType inImageType) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotoDiskUsage() - baseImageLocation : "+baseImageLocation.name());
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Photos_Outils() - getPhotoDiskUsage() - inImageType : "+inImageType.name());
        return (long)getImageCountInFolder(baseImageLocation, inImageType) * (long)getTailleMoyImageUnitaire(inImageType);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * Gestion téléchargements
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ImageType getImageQualityToDownload(boolean inPhotoPrincipale, ZoneGeographiqueKind inZoneGeo) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getImageQualityToDownload() - Début" );

        PrecharMode prechargementMode = getPrecharModeZoneGeo(inZoneGeo);

        if (inPhotoPrincipale) {
            switch (prechargementMode) {
                case P1:
                case P2:
                    return ImageType.VIGNETTE;
                case P3:
                case P4:
                    return ImageType.MED_RES;
                case P5:
                case P6:
                    return ImageType.HI_RES;
                default:
                    return null;
            }
        } else {
            switch (prechargementMode) {
                case P2:
                case P3:
                    return ImageType.VIGNETTE;
                case P4:
                case P5:
                    return ImageType.MED_RES;
                case P6:
                    return ImageType.HI_RES;
                default:
                    return null;
            }
        }

    }

    public PrecharMode getPrecharModeZoneGeo(ZoneGeographiqueKind inZoneGeo) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharModeZoneGeo() - Début" );

        switch (inZoneGeo) {
            case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france, "P1"));
            case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce, "P1"));
            case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac, "P1"));
            case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_antarctique, "P1"));
            case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes, "P1"));
            case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno, "P1"));
            case FAUNE_FLORE_MER_ROUGE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_merrouge, "P1"));
            case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_mediter, "P1"));
            case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantne, "P1"));
            case FAUNE_FLORE_GUYANNE:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_guyanne, "P1"));
            case FAUNE_FLORE_HABITAT:
                return PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_habitat, "P1"));
            default:
                return null;
        }
    }

    /**
     * récupère le nombre déclaré de photos à précharger
     * La déclaration est stockée dans les préférences
     * zone par zone, principale ou pas
     *
     * @param inZoneGeo zone geographique considérée
     * @param inPrincipale concerne la photo principale ou n'importe quelle photo
     * @return
     */
    public int getAPrecharQteZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - Début" );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inIdZoneGeo : "+inZoneGeo.name() );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getAPrecharQteZoneGeo() - inPrincipale : "+inPrincipale );

        if (inPrincipale) {
            switch (inZoneGeo) {
                case FAUNE_FLORE_TOUTES_ZONES:
                    int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_france, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_antarctique, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_guyanne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_mediter, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_merrouge, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_habitat, 0);
                    return nbAPrechar;
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_france, 0);
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_eaudouce, 0);
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_indopac, 0);
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_antarctique, 0);
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_caraibes, 0);
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantno, 0);
                case FAUNE_FLORE_MER_ROUGE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_merrouge, 0);
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_mediter, 0);
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantne, 0);
                case FAUNE_FLORE_GUYANNE:
                return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_guyanne, 0);
                case FAUNE_FLORE_HABITAT:
                return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_habitat, 0);
                default:
                    return 0;
            }
        } else {
            switch (inZoneGeo) {
                case FAUNE_FLORE_TOUTES_ZONES:
                    int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_france, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_eaudouce, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_atlantno, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_indopac, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_antarctique, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_caraibes, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_guyanne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_mediter, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_merrouge, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_habitat, 0);
                    return nbAPrechar;
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_france, 0);
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_eaudouce, 0);
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_indopac, 0);
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_antarctique, 0);
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_caraibes, 0);
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_atlantno, 0);
                case FAUNE_FLORE_MER_ROUGE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_merrouge, 0);
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_atelecharger_mediter, 0);
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_atlantne, 0);
                case FAUNE_FLORE_GUYANNE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_guyanne, 0);
                case FAUNE_FLORE_HABITAT:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_atelecharger_habitat, 0);
                default:
                    return 0;
            }
        }
    }

    /**
     * Enregistre le nombre de photos à précharger
     * La déclaration est stockée dans les préférences
     * zone par zone, principale ou pas
     *
     * @param inZoneGeo zone geographique considérée
     * @param inPrincipale concerne la photo principale ou n'importe quelle photo
     * @return
     */
    public int setAPrecharQteParZoneGeo(ZoneGeographique inZoneGeo, Boolean inPrincipale) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setAPrecharQteZoneGeo() - Début" );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setAPrecharQteZoneGeo() - inIdZoneGeo : "+inZoneGeo.getNom() );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setAPrecharQteZoneGeo() - inPrincipale : "+inPrincipale.toString() );

        dbHelper = OpenHelperManager.getHelper(context.getApplicationContext(), OrmLiteDBHelper.class);
        dorisDBHelper = dbHelper.getDorisDBHelper();

        GenericRawResults<String[]> rawResults = null;
        List<String[]> countPhoto = new ArrayList<String[]>(2);
        int nbrePhotos = 0;

        if ( inPrincipale && getPrecharModeZoneGeo(inZoneGeo.getZoneGeoKind()) != Photos_Outils.PrecharMode.P0 ) {
            try{
                rawResults =
                        dorisDBHelper.photoFicheDao.queryRaw("SELECT count(*) FROM fiches_ZonesGeographiques, fiche, photoFiche "
                                + "WHERE ZoneGeographique_id = " + inZoneGeo.getId() + " "
                                + "AND  fiches_ZonesGeographiques.Fiche_id = fiche._id "
                                + "AND photoFiche._id =  fiche.photoPrincipale_id" );
                countPhoto = rawResults.getResults();
                rawResults.close();
                nbrePhotos = Integer.valueOf(countPhoto.get(0)[0]);

            } catch (java.sql.SQLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        if ( !inPrincipale && getPrecharModeZoneGeo(inZoneGeo.getZoneGeoKind()) != Photos_Outils.PrecharMode.P0
                && getPrecharModeZoneGeo(inZoneGeo.getZoneGeoKind()) != Photos_Outils.PrecharMode.P1 ) {
            try{
                rawResults =
                        dorisDBHelper.photoFicheDao.queryRaw("SELECT count(*) FROM fiches_ZonesGeographiques, photoFiche "
                                + "WHERE ZoneGeographique_id = " + inZoneGeo.getId() + " "
                                + "AND  fiches_ZonesGeographiques.Fiche_id = photoFiche.fiche_id ");
                countPhoto = rawResults.getResults();
                rawResults.close();
                nbrePhotos = Integer.valueOf(countPhoto.get(0)[0]);

            } catch (java.sql.SQLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setAPrecharQteZoneGeo() - nb Photos : "+nbrePhotos );
        paramOutils.setParamInt(getKeyDataAPrecharZoneGeo(inZoneGeo.getZoneGeoKind(), inPrincipale), nbrePhotos);

        DaoManager.unregisterDao(dbHelper.getConnectionSource(), dorisDBHelper.photoFicheDao);

        return nbrePhotos;
    }

    /**
     * récupère le nombre de photos déclarées présentes (stockées dans les préférences)
     * zone par zone, principale ou pas
     *
     * @param inZoneGeo
     * @param inPrincipale concerne la photo principale ou n'importe quelle photo
     * @return
     */
    public int getDejaLaQteZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getDejaLaQteZoneGeo() - Début" );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getDejaLaQteZoneGeo() - inIdZoneGeo : "+inIdZoneGeo );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getDejaLaQteZoneGeo() - data_nbphotos_recues_france : "+getParamInt(context, R.string.pref_key_nbphotos_recues_france, 0) );

        if (inPrincipale) {
            switch (inZoneGeo) {
                case FAUNE_FLORE_TOUTES_ZONES:
                    int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_france, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_eaudouce, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_atlantno, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_indopac, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_antarctique, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_caraibes, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_guyanne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_atlantne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_mediter, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_merrouge, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_habitat, 0);
                    //  pas besoin de noter mer rouge, mediterranée qui sont des sous zones
                    return nbAPrechar;
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_france, 0);
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_eaudouce, 0);
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_indopac, 0);
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_caraibes, 0);
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_atlantno, 0);
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_antarctique, 0);
                case FAUNE_FLORE_MER_ROUGE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_merrouge, 0);
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_mediter, 0);
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_atlantne, 0);
                case FAUNE_FLORE_GUYANNE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_guyanne, 0);
                case FAUNE_FLORE_HABITAT:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotosprinc_recues_habitat, 0);
                default:
                    return 0;
            }
        } else {
            switch (inZoneGeo) {
                case FAUNE_FLORE_TOUTES_ZONES:
                    int nbAPrechar = paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_france, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_eaudouce, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_atlantno, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_indopac, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_antarctique, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_caraibes, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_atlantne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_mediter, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_guyanne, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_merrouge, 0);
                    nbAPrechar += paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_habitat, 0);
                    //  pas besoin de noter mer rouge, mediterranée qui sont des sous zones
                    return nbAPrechar;
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_france, 0);
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_eaudouce, 0);
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_indopac, 0);
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_caraibes, 0);
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_atlantno, 0);
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_antarctique, 0);
                case FAUNE_FLORE_MER_ROUGE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_merrouge, 0);
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_mediter, 0);
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_atlantne, 0);
                case FAUNE_FLORE_GUYANNE:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_guyanne, 0);
                case FAUNE_FLORE_HABITAT:
                    return paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_habitat, 0);
                default:
                    return 0;
            }
        }
    }


    /**
     * récupère l'id de la resource textuelle pour les clé des préférences pour récupérer ou stocker
     * des info de photo à télécharger ou déjà téléchargée zone par zone, photo principale ou pas
     *
     * @param inZoneGeo
     * @param inPrincipale
     * @return
     */
    // TODO : C'est crado mais c'est rassemblé ici
    public int getKeyDataAPrecharZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale) {
        if (inPrincipale) {
            switch (inZoneGeo) {
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_france;
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_eaudouce;
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_indopac;
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return R.string.pref_key_nbphotosprinc_atelecharger_caraibes;
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return R.string.pref_key_nbphotosprinc_atelecharger_atlantno;
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return R.string.pref_key_nbphotosprinc_atelecharger_antarctique;
                case FAUNE_FLORE_MER_ROUGE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_merrouge;
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_mediter;
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_atlantne;
                case FAUNE_FLORE_GUYANNE:
                    return R.string.pref_key_nbphotosprinc_atelecharger_guyanne;
                case FAUNE_FLORE_HABITAT:
                    return R.string.pref_key_nbphotosprinc_atelecharger_habitat;
                default:
                    return 0;
            }
        } else {
            switch (inZoneGeo) {
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotos_atelecharger_france;
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotos_atelecharger_eaudouce;
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return R.string.pref_key_nbphotos_atelecharger_indopac;
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return R.string.pref_key_nbphotos_atelecharger_caraibes;
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return R.string.pref_key_nbphotos_atelecharger_atlantno;
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return R.string.pref_key_nbphotos_atelecharger_antarctique;
                case FAUNE_FLORE_MER_ROUGE:
                    return R.string.pref_key_nbphotos_atelecharger_merrouge;
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return R.string.pref_key_nbphotos_atelecharger_mediter;
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return R.string.pref_key_nbphotos_atelecharger_atlantne;
                case FAUNE_FLORE_GUYANNE:
                    return R.string.pref_key_nbphotos_atelecharger_guyanne;
                case FAUNE_FLORE_HABITAT:
                    return R.string.pref_key_nbphotos_atelecharger_habitat;
                default:
                    return 0;
            }
        }
    }

    // TODO : Crado aussi
    public int getKeyDataRecuesZoneGeo(ZoneGeographiqueKind inZoneGeo, Boolean inPrincipale) {
        if (inPrincipale) {
            switch (inZoneGeo) {
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotosprinc_recues_france;
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotosprinc_recues_eaudouce;
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return R.string.pref_key_nbphotosprinc_recues_indopac;
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return R.string.pref_key_nbphotosprinc_recues_antarctique;
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return R.string.pref_key_nbphotosprinc_recues_caraibes;
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return R.string.pref_key_nbphotosprinc_recues_atlantno;
                case FAUNE_FLORE_MER_ROUGE:
                    return R.string.pref_key_nbphotosprinc_recues_merrouge;
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return R.string.pref_key_nbphotosprinc_recues_mediter;
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return R.string.pref_key_nbphotosprinc_recues_atlantne;
                case FAUNE_FLORE_GUYANNE:
                    return R.string.pref_key_nbphotosprinc_recues_guyanne;
                case FAUNE_FLORE_HABITAT:
                    return R.string.pref_key_nbphotosprinc_recues_habitat;
                default:
                    return 0;
            }
        } else {
            switch (inZoneGeo) {
                case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotos_recues_france;
                case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                    return R.string.pref_key_nbphotos_recues_eaudouce;
                case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                    return R.string.pref_key_nbphotos_recues_indopac;
                case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                    return R.string.pref_key_nbphotos_recues_antarctique;
                case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                    return R.string.pref_key_nbphotos_recues_caraibes;
                case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                    return R.string.pref_key_nbphotos_recues_atlantno;
                case FAUNE_FLORE_MER_ROUGE:
                    return R.string.pref_key_nbphotos_recues_merrouge;
                case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                    return R.string.pref_key_nbphotos_recues_mediter;
                case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                    return R.string.pref_key_nbphotos_recues_atlantne;
                case FAUNE_FLORE_GUYANNE:
                    return R.string.pref_key_nbphotos_recues_guyanne;
                case FAUNE_FLORE_HABITAT:
                    return R.string.pref_key_nbphotos_recues_habitat;
                default:
                    return 0;
            }
        }
    }

    public boolean isPrecharModeOnlyP0() {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );

        if (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_antarctique, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_merrouge, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_mediter, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantne, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_guyanne, "P1")) == PrecharMode.P0
                && PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_habitat, "P1")) == PrecharMode.P0
        ) return true;
        return false;
    }

    public boolean isPrecharModeOnlyP0orP1() {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );

        if ((PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_france, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_eaudouce, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantno, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_indopac, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_antarctique, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_antarctique, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_caraibes, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_merrouge, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_merrouge, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantne, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_atlantne, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_guyanne, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_guyanne, "P1")) == PrecharMode.P1)
                && (PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_habitat, "P1")) == PrecharMode.P0
                || PrecharMode.valueOf(paramOutils.getParamString(R.string.pref_key_mode_precharg_photo_region_habitat, "P1")) == PrecharMode.P1)
        ) return true;
        return false;
    }


    public int getTailleMoyImageUnitaire(ImageType imageType) {
        switch (imageType) {
            case VIGNETTE:
                return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_vignette));
            case MED_RES:
                return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_med_res));
            case PORTRAITS:
                return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_portraits));
            case ILLUSTRATION_DEFINITION:
                return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_illus_definiions));
            case ILLUSTRATION_BIBLIO:
                return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_illus_biblio));
            case HI_RES:
                return Integer.parseInt(context.getString(R.string.etatmodehorsligne_taillemoy_hi_res));
            default:
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
    public boolean isPhotosParFicheInitialized = false;

    public void initNbPhotosParFiche() {
        nbFichesTotal = fichesOutils.getNbFichesZoneGeo(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES);

        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
        RuntimeExceptionDao<PhotoFiche, Integer> entriesFicheDao = ormLiteDBHelper.getPhotoFicheDao();
        nbPhotosTotal = (int) entriesFicheDao.countOf();
        entriesFicheDao = null;
        ormLiteDBHelper.close();

        // On calcule le nombre de photos (hors principales) moyen par fiche
        if (nbFichesTotal != 0) {
            nbPhotosParFiche = (nbPhotosTotal - nbFichesTotal) / nbFichesTotal;
        } else {
            nbPhotosParFiche = 0;
        }

        for (ZoneGeographiqueKind zone : ZoneGeographiqueKind.values()) {
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "initNbPhotosParFiche() - zone : "+zone);
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "initNbPhotosParFiche() - zone.ordinal() : "+zone.ordinal());
            nbFichesZoneGeo[zone.ordinal()] = fichesOutils.getNbFichesZoneGeo(zone);
        }
        isPhotosParFicheInitialized = true;
    }



    public long getEstimVolPhotosParZone(PrecharMode precharMode, ZoneGeographiqueKind inZoneGeo) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getEstimVolPhotosParZone() - precharMode : "+precharMode);
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getEstimVolPhotosParZone() - inZoneGeo : "+inZoneGeo);
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getEstimVolPhotosParZone() - nbFichesZoneGeo : "+nbFichesZoneGeo[inZoneGeo.ordinal()]);

        int volPhotosPrincipales = 0;
        int volPhotos = 0;

        switch (precharMode) {
            case P0:
                return 0;
            case P1:
                volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE);
                return volPhotosPrincipales;
            case P2:
                volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE);
                volPhotos = (int) (nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.VIGNETTE));
                return volPhotosPrincipales + volPhotos;
            case P3:
                volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
                        + nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.MED_RES);
                volPhotos = (int) (nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.VIGNETTE));
                return volPhotosPrincipales + volPhotos;
            case P4:
                volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
                        + nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.MED_RES);
                volPhotos = (int) (nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.MED_RES));
                return volPhotosPrincipales + volPhotos;
            case P5:
                volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
                        + nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.HI_RES);
                volPhotos = (int) (nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.MED_RES));
                return volPhotosPrincipales + volPhotos;
            case P6:
                volPhotosPrincipales = nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.VIGNETTE)
                        + nbFichesZoneGeo[inZoneGeo.ordinal()] * getTailleMoyImageUnitaire(ImageType.HI_RES);
                volPhotos = (int) (nbFichesZoneGeo[inZoneGeo.ordinal()] * nbPhotosParFiche * getTailleMoyImageUnitaire(ImageType.HI_RES));
                return volPhotosPrincipales + volPhotos;
            default:
                return 0;
        }

    }

    public long getEstimVolPhotosAutres() {
        long nbPhotos = 0;
        int volPhotos = 0;
        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);

        try {
            nbPhotos = ormLiteDBHelper.getParticipantDao().countOf(
                    ormLiteDBHelper.getParticipantDao().queryBuilder().setCountOf(true).where().not().eq("cleURLPhotoParticipant", "").prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }
        volPhotos += (int) nbPhotos * getTailleMoyImageUnitaire(ImageType.PORTRAITS);

        try {
            nbPhotos = ormLiteDBHelper.getEntreeBibliographieDao().countOf(
                    ormLiteDBHelper.getEntreeBibliographieDao().queryBuilder().setCountOf(true).where().not().eq("cleURLIllustration", "").prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }
        volPhotos += (int) nbPhotos * getTailleMoyImageUnitaire(ImageType.ILLUSTRATION_BIBLIO);

        try {
            nbPhotos = ormLiteDBHelper.getDefinitionGlossaireDao().countOf(
                    ormLiteDBHelper.getDefinitionGlossaireDao().queryBuilder().setCountOf(true).where().not().eq("cleURLIllustration", "").prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }
        volPhotos += (int) nbPhotos * getTailleMoyImageUnitaire(ImageType.ILLUSTRATION_DEFINITION);

        return volPhotos;
    }


}
