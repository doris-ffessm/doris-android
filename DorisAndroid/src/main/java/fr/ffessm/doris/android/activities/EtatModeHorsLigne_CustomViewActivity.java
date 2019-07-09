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
package fr.ffessm.doris.android.activities;


import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


//Start of user code additional imports EtatModeHorsLigne_CustomViewActivity
import java.util.HashMap;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.CloseableIterator;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.activities.view.MultiProgressBar;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.tools.disk.DiskEnvironmentHelper;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;
import fr.ffessm.doris.android.tools.disk.StorageHelper;
import fr.ffessm.doris.android.tools.disk.StorageHelper.StorageVolume;
import fr.ffessm.doris.android.services.GestionPhotoDiskService;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Disque_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

//End of user code
public class EtatModeHorsLigne_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements EtatModeHorsLigne_CustomViewActivity
        implements DataChangedListener
//End of user code
{

    //Start of user code constants EtatModeHorsLigne_CustomViewActivity

    private static final String LOG_TAG = EtatModeHorsLigne_CustomViewActivity.class.getCanonicalName();
    Handler mHandler;

    Photos_Outils photosOutils;
    Param_Outils paramOutils;
    Disque_Outils disqueOutils;
    Fiches_Outils fichesOutils;

    protected SparseArray<MultiProgressBar> progressBarZones = new SparseArray<MultiProgressBar>();
    protected MultiProgressBar progressBarZoneGenerale;
    protected List<MultiProgressBar> allFoldableProgressBarZones = new ArrayList<MultiProgressBar>();
    protected HashMap<String, View.OnClickListener> reusableClickListener = new HashMap<String, View.OnClickListener>();

    /**
     * Si déplacement Photos en cours, des mises à jour ne sont pas nécessaires
     * des boutons doivent être désactivés
     */
    Boolean isMovingPhotos = false;
    Boolean isTelechPhotos = false;

    /**
     * Disques Dispo
     */
    boolean carteInterneDispo = false;
    boolean carteExterneDispo = false;

    /**
     * Boutons Photos
     */
    private LinearLayout llGestionPhotos;
    private int image_maximize;
    private int image_minimize;
    private ImageButton btnFoldUnflodGestionPhotos;
    private TableLayout tlFoldUnflodGestionPhotos;
    private int imageCouranteGestionPhotos;
    private Button btnGestionPhotosResetVig;
    private Button btnGestionPhotosResetMedRes;
    private Button btnGestionPhotosResetHiRes;
    private Button btnGestionPhotosResetAutres;
    private Button btnGestionPhotosResetCache;

    /**
     * Boutons Utilisation des Disques
     */
    private LinearLayout llGestionDisk;
    private ImageButton btnFoldUnflodGestionDisk;
    private TableLayout tlFoldUnflodGestionDisk;
    private int imageCouranteGestionDisk;
    private Button btnInternalDiskDepl;
    private Button btnInternalDiskSupp;
    private Button btnPrimaryDiskDepl;
    private Button btnPrimaryDiskSupp;
    private Button btnSecondaryDiskDepl;
    private Button btnSecondaryDiskSupp;

    // cache pour éviter de refaire des accès BDD inutiles
    protected List<ZoneGeographique> listeZoneGeo = null;
    protected SparseIntArray lastFicheCount = null;
    //End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.etatmodehorsligne_customview);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Start of user code onCreate EtatModeHorsLigne_CustomViewActivity

        getSupportActionBar().setTitle(getContext().getString(R.string.etatmodehorsligne_titre_text));

        // Avancement Téléchargement Photos
        if (listeZoneGeo == null)
            listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
        this.getHelper().getZoneGeographiqueDao().clearObjectCache();
        createProgressBarZone();

        // Images des boutons
        image_maximize = R.drawable.app_expander_ic_maximized;
        image_minimize = R.drawable.app_expander_ic_minimized;

        // Création de la liste des appels au service de suppression et déplacement des images
        initOnClickListener();

        // Gestion Photos
        createGestionPhotos();

        // Gestion Disques
        createGestionDisk();

        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
                if (EtatModeHorsLigne_CustomViewActivity.this.isFinishing() || EtatModeHorsLigne_CustomViewActivity.this.isActivityDestroyed())
                    return;
                if (inputMessage.obj != null) {
                    showToast((String) inputMessage.obj);
                }

                // Mise à jour de l'affichage
                refreshScreenData();

            }

        };

        DorisApplicationContext.getInstance().addDataChangeListeners(this);
        //End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
        //Start of user code onResume EtatModeHorsLigne_CustomViewActivity
        //End of user code
    }
    //Start of user code additional code EtatModeHorsLigne_CustomViewActivity

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        // s'assure de désenregistrer le handler
        DorisApplicationContext.getInstance().removeDataChangeListeners(this);

        super.onDestroy();
    }

    public void dataHasChanged(String textmessage) {
        Message completeMessage = mHandler.obtainMessage(1, textmessage);
        completeMessage.sendToTarget();
    }

    public void dataHasChanged(Object objmessage) {
        Message completeMessage = mHandler.obtainMessage(1, objmessage);
        completeMessage.sendToTarget();
    }

    public Context getContext() {
        return this;
    }


    protected void createProgressBarZone() {

        LinearLayout llContainerLayout = (LinearLayout) findViewById(R.id.etatmodehorsligne_avancements_layout);

        // Avancement et Affichage toutes Zones
        ZoneGeographique zoneToutesZones = new ZoneGeographique();
        zoneToutesZones.setId(-1);
        zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));

        int imageZone = getFichesOutils().getZoneIconeId(zoneToutesZones.getZoneGeoKind());

        progressBarZoneGenerale = new MultiProgressBar(this, zoneToutesZones.getNom(), imageZone, true);
        updateProgressBarZone(getContext(), zoneToutesZones, progressBarZoneGenerale, "");
        progressBarZones.put(zoneToutesZones.getId(), progressBarZoneGenerale);

        final Context context = this;
        // Arrêt téléchargement si Click sur Icône de l'avancement (le petit rond qui tourne)
        progressBarZoneGenerale.pbProgressBar_running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(R.string.bg_notifToast_arretTelecharg);
                DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);

                ProgressBar pbRunningBarLayout = (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
                pbRunningBarLayout.setVisibility(View.GONE);
            }
        });
        // Affichage Préférence de la Zone Géographique
        progressBarZoneGenerale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EtatModeHorsLigne_CustomViewActivity.this, Preference_PreferenceViewActivity.class);
                intent.putExtra("type_parametre", "mode_precharg_category");
                intent.putExtra("parametre", "button_qualite_images_zones_key");

                startActivity(intent);
            }
        });
        // Masquage des Avancements par Zone si Clique sur Bouton de repli
        progressBarZoneGenerale.btnFoldUnflodSection.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarZoneGenerale.btn_fold_unfold();
                for (MultiProgressBar foldableProgressBarZones : allFoldableProgressBarZones) {
                    foldableProgressBarZones.fold_unfold();
                }
            }
        });


        llContainerLayout.addView(progressBarZoneGenerale);


        // Avancement par Zone

        for (ZoneGeographique zoneGeo : listeZoneGeo) {
            imageZone = getFichesOutils().getZoneIconeId(zoneGeo.getZoneGeoKind());

            MultiProgressBar progressBarZone = new MultiProgressBar(this, zoneGeo.getNom(), imageZone, false);
            updateProgressBarZone(context, zoneGeo, progressBarZone, "");

            final ZoneGeographique fZoneGeo = zoneGeo;
            progressBarZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BuildConfig.DEBUG)
                        Log.d(LOG_TAG, "setOnClickListener() - zoneGeoId : " + fZoneGeo.getId());
                    Intent intent = new Intent(EtatModeHorsLigne_CustomViewActivity.this, Preference_PreferenceViewActivity.class);

                    String param;

                    switch (fZoneGeo.getZoneGeoKind()) {
                        case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                            param = getParamOutils().getStringNameParam(R.string.pref_key_mode_precharg_photo_region_france);
                            break;
                        case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                            param = getParamOutils().getStringNameParam(R.string.pref_key_mode_precharg_photo_region_eaudouce);
                            break;
                        case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                            param = getParamOutils().getStringNameParam(R.string.pref_key_mode_precharg_photo_region_indopac);
                            break;
                        case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                            param = getParamOutils().getStringNameParam(R.string.pref_key_mode_precharg_photo_region_antarctique);
                            break;
                        case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                            param = getParamOutils().getStringNameParam(R.string.pref_key_mode_precharg_photo_region_caraibes);
                            break;
                        case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                            param = getParamOutils().getStringNameParam(R.string.pref_key_mode_precharg_photo_region_atlantno);
                            break;
                        default:
                            param = null;
                    }

                    if (param != null) {
                        intent.putExtra("type_parametre", "button_qualite_images_zones_key");
                        intent.putExtra("parametre", param);
                    } else {
                        intent.putExtra("type_parametre", "mode_precharg_category");
                        intent.putExtra("parametre", "button_qualite_images_zones_key");
                    }

                    startActivity(intent);
                }
            });

            progressBarZones.put(fZoneGeo.getId(), progressBarZone);
            llContainerLayout.addView(progressBarZone);
            allFoldableProgressBarZones.add(progressBarZone);

        }
    }

    protected void initOnClickListener() {

        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_FOLDER, GestionPhotoDiskService.SRC_DOS_VIGNETTES, null);
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_FOLDER, GestionPhotoDiskService.SRC_DOS_MEDRES, null);
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_FOLDER, GestionPhotoDiskService.SRC_DOS_HIRES, null);
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_FOLDER, GestionPhotoDiskService.SRC_DOS_AUTRES, null);
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_FOLDER, GestionPhotoDiskService.SRC_DOS_CACHE, null);

        addReusableClickListener(GestionPhotoDiskService.ACT_MOVE, ImageLocation.APP_INTERNAL.name(), ImageLocation.PRIMARY.name());
        addReusableClickListener(GestionPhotoDiskService.ACT_MOVE, ImageLocation.APP_INTERNAL.name(), ImageLocation.SECONDARY.name());
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_DISK, ImageLocation.APP_INTERNAL.name(), null);

        addReusableClickListener(GestionPhotoDiskService.ACT_MOVE, ImageLocation.PRIMARY.name(), ImageLocation.APP_INTERNAL.name());
        addReusableClickListener(GestionPhotoDiskService.ACT_MOVE, ImageLocation.PRIMARY.name(), ImageLocation.SECONDARY.name());
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_DISK, ImageLocation.PRIMARY.name(), null);

        addReusableClickListener(GestionPhotoDiskService.ACT_MOVE, ImageLocation.SECONDARY.name(), ImageLocation.APP_INTERNAL.name());
        addReusableClickListener(GestionPhotoDiskService.ACT_MOVE, ImageLocation.SECONDARY.name(), ImageLocation.PRIMARY.name());
        addReusableClickListener(GestionPhotoDiskService.ACT_DELETE_DISK, ImageLocation.SECONDARY.name(), null);
    }

    protected void createGestionPhotos() {

        llGestionPhotos = (LinearLayout) findViewById(R.id.etatmodehorsligne_gestion_photos_linearlayout);
        btnFoldUnflodGestionPhotos = (ImageButton) findViewById(R.id.etatmodehorsligne_gestion_photos_fold_unflod_section_imageButton);
        tlFoldUnflodGestionPhotos = (TableLayout) findViewById(R.id.etatmodehorsligne_gestion_reset_linearlayout);

        imageCouranteGestionPhotos = image_maximize;
        btnFoldUnflodGestionPhotos.setImageResource(imageCouranteGestionPhotos);

        btnGestionPhotosResetVig = (Button) findViewById(R.id.etatmodehorsligne_gestion_reset_vig_btn);
        btnGestionPhotosResetMedRes = (Button) findViewById(R.id.etatmodehorsligne_gestion_reset_med_btn);
        btnGestionPhotosResetHiRes = (Button) findViewById(R.id.etatmodehorsligne_gestion_reset_hi_btn);
        btnGestionPhotosResetAutres = (Button) findViewById(R.id.etatmodehorsligne_gestion_reset_autres_btn);
        btnGestionPhotosResetCache = (Button) findViewById(R.id.etatmodehorsligne_gestion_reset_cache_btn);

        // Masquage des Boutons de suppression des photos
        llGestionPhotos.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                foldUnflodGestionPhotos();
            }
        });
        btnFoldUnflodGestionPhotos.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                foldUnflodGestionPhotos();
            }
        });

        // Boutons de Suppression des Images par Type
        btnGestionPhotosResetVig.setOnClickListener(reusableClickListener.get(
                GestionPhotoDiskService.ACT_DELETE_FOLDER + "-" + GestionPhotoDiskService.SRC_DOS_VIGNETTES));

        btnGestionPhotosResetMedRes.setOnClickListener(reusableClickListener.get(
                GestionPhotoDiskService.ACT_DELETE_FOLDER + "-" + GestionPhotoDiskService.SRC_DOS_MEDRES));

        btnGestionPhotosResetHiRes.setOnClickListener(reusableClickListener.get(
                GestionPhotoDiskService.ACT_DELETE_FOLDER + "-" + GestionPhotoDiskService.SRC_DOS_HIRES));

        btnGestionPhotosResetAutres.setOnClickListener(reusableClickListener.get(
                GestionPhotoDiskService.ACT_DELETE_FOLDER + "-" + GestionPhotoDiskService.SRC_DOS_AUTRES));

        btnGestionPhotosResetCache.setOnClickListener(reusableClickListener.get(
                GestionPhotoDiskService.ACT_DELETE_FOLDER + "-" + GestionPhotoDiskService.SRC_DOS_CACHE));

    }

    protected void foldUnflodGestionPhotos() {
        if (tlFoldUnflodGestionPhotos.getVisibility() == View.GONE) {
            tlFoldUnflodGestionPhotos.setVisibility(View.VISIBLE);
            imageCouranteGestionPhotos = image_maximize;
        } else {
            tlFoldUnflodGestionPhotos.setVisibility(View.GONE);
            imageCouranteGestionPhotos = image_minimize;
        }
        btnFoldUnflodGestionPhotos.setImageResource(imageCouranteGestionPhotos);
    }

    protected void createGestionDisk() {

        llGestionDisk = (LinearLayout) findViewById(R.id.etatmodehorsligne_gestion_disk_layout);
        tlFoldUnflodGestionDisk = (TableLayout) findViewById(R.id.etatmodehorsligne_gestion_disk_buttons_tablelayout);
        btnFoldUnflodGestionDisk = (ImageButton) findViewById(R.id.etatmodehorsligne_gestion_disk_fold_unflod_section_imageButton);

        imageCouranteGestionDisk = image_maximize;
        btnFoldUnflodGestionDisk.setImageResource(imageCouranteGestionDisk);

        btnInternalDiskDepl = (Button) findViewById(R.id.etatmodehorsligne_diskselection_internal_depl_btn);
        btnInternalDiskSupp = (Button) findViewById(R.id.etatmodehorsligne_diskselection_internal_supp_btn);
        btnPrimaryDiskDepl = (Button) findViewById(R.id.etatmodehorsligne_diskselection_primary_depl_btn);
        btnPrimaryDiskSupp = (Button) findViewById(R.id.etatmodehorsligne_diskselection_primary_supp_btn);
        btnSecondaryDiskDepl = (Button) findViewById(R.id.etatmodehorsligne_diskselection_secondary_depl_btn);
        btnSecondaryDiskSupp = (Button) findViewById(R.id.etatmodehorsligne_diskselection_secondary_supp_btn);

        // Masquage de l'ensemble des Boutons de suppression des photos
        llGestionDisk.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                foldUnflodGestionDisk();
            }
        });
        btnFoldUnflodGestionDisk.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                foldUnflodGestionDisk();
            }
        });

        // les boutons Supprimer ont toujours le même setOnClickListener, on l'associe donc ici.
        // Pour les Déplacements, c'est dans le refreshusedDisk()
        btnInternalDiskSupp.setOnClickListener(
                reusableClickListener.get(
                        GestionPhotoDiskService.ACT_DELETE_DISK + "-" + ImageLocation.APP_INTERNAL.name()));
        btnPrimaryDiskSupp.setOnClickListener(
                reusableClickListener.get(
                        GestionPhotoDiskService.ACT_DELETE_DISK + "-" + ImageLocation.PRIMARY.name()));
        btnSecondaryDiskSupp.setOnClickListener(
                reusableClickListener.get(
                        GestionPhotoDiskService.ACT_DELETE_DISK + "-" + ImageLocation.SECONDARY.name()));
    }

    protected void foldUnflodGestionDisk() {
        if (tlFoldUnflodGestionDisk.getVisibility() == View.GONE) {
            tlFoldUnflodGestionDisk.setVisibility(View.VISIBLE);
            imageCouranteGestionDisk = image_maximize;
        } else {
            tlFoldUnflodGestionDisk.setVisibility(View.GONE);
            imageCouranteGestionDisk = image_minimize;
        }
        btnFoldUnflodGestionDisk.setImageResource(imageCouranteGestionDisk);
    }


    public void refreshProgressBarZone() {

        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - update progress bar : ");
        ZoneGeographique zoneToutesZones = new ZoneGeographique();
        zoneToutesZones.setId(-1);
        zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
        updateProgressBarZone(this, zoneToutesZones, progressBarZones.get(zoneToutesZones.getId()), "");

        // Si on sait sur quelle zone on travaille alors on ne met à jour que la progress barre de cette zone

        //Log.d(LOG_TAG, "refreshScreenData() - zoneTraitee : "+DorisApplicationContext.getInstance().zoneTraitee);
        if (DorisApplicationContext.getInstance().zoneTraitee == null
                || DorisApplicationContext.getInstance().zoneTraitee == ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES) {
            for (ZoneGeographique zoneGeo : listeZoneGeo) {
                updateProgressBarZone(this, zoneGeo, progressBarZones.get(zoneGeo.getId()), "");
            }
        } else {
            ZoneGeographique zoneGeo = new ZoneGeographique(DorisApplicationContext.getInstance().zoneTraitee);
            // Récupération Nom et Désignation dans la base
            zoneGeo = this.getHelper().getZoneGeographiqueDao().queryForId(zoneGeo.getId());
            //Log.d(LOG_TAG, "refreshScreenData() - zoneGeo : "+zoneGeo.getId()+" - "+zoneGeo.getNom());

            updateProgressBarZone(this, zoneGeo, progressBarZones.get(zoneGeo.getId()), "");
        }

    }

    public static String updateProgressBarZone(Context context, ZoneGeographique inZoneGeo, MultiProgressBar progressBarZone, String summaryPrefix) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "updateProgressBarZone() - inZoneGeo : "+inZoneGeo.getId()+" - "+inZoneGeo.getZoneGeoKind());

        Fiches_Outils fichesOutils = new Fiches_Outils(context);
        Photos_Outils photosOutils = new Photos_Outils(context);

        boolean affichageBarrePhotoPrinc;
        boolean affichageBarrePhotoAutres;
        String summaryTexte = "";
        int nbFichesZoneGeo = fichesOutils.getNbFichesZoneGeo(inZoneGeo.getZoneGeoKind());
        int avancementPhotoPrinc = 0;
        int avancementPhotoAutres = 0;

        int nbPhotosPrincATelecharger = photosOutils.getAPrecharQteZoneGeo(inZoneGeo.getZoneGeoKind(), true);
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "updateProgressBarZone() - nbPhotosPrincATelecharger : "+nbPhotosPrincATelecharger);
        int nbPhotosAutresATelecharger = photosOutils.getAPrecharQteZoneGeo(inZoneGeo.getZoneGeoKind(), false);
        int nbPhotosPrincDejaLa = photosOutils.getDejaLaQteZoneGeo(inZoneGeo.getZoneGeoKind(), true);
        int nbPhotosAutresDejaLa = photosOutils.getDejaLaQteZoneGeo(inZoneGeo.getZoneGeoKind(), false);


        // Affichage Barres avancements
        if (nbPhotosPrincATelecharger == 0) {
            affichageBarrePhotoPrinc = false;
        } else {
            affichageBarrePhotoPrinc = true;
        }
        if (nbPhotosAutresATelecharger == 0) {
            affichageBarrePhotoAutres = false;
        } else {
            affichageBarrePhotoAutres = true;
        }

        // P0 : Aucune photo à précharger

        if (nbPhotosPrincATelecharger == 0 && inZoneGeo.getZoneGeoKind() != ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES) {
            summaryTexte = context.getString(R.string.avancement_zone_progressbar_P0_summary);
            summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
        }

        // Texte de l'Avancement Global : Toutes Zones
        if (inZoneGeo.getZoneGeoKind() == ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES) {

            // P0 : Aucune photo à précharger
            if (nbPhotosPrincATelecharger == 0) {
                summaryTexte = context.getString(R.string.avancement_touteszones_progressbar_P0_summary);
                summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
            }

            // P1 : La photo principale seule
            if (nbPhotosPrincATelecharger != 0 && nbPhotosAutresATelecharger == 0) {

                avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;

                summaryTexte = context.getString(R.string.avancement_touteszones_progressbar_P1_summary);
                summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
                summaryTexte = summaryTexte.replace("@PropPh", "" + avancementPhotoPrinc);
            }

            // PX : La photo principale seule + autres photos
            if (nbPhotosPrincATelecharger != 0 && nbPhotosAutresATelecharger != 0) {

                avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
                avancementPhotoAutres = 100 * nbPhotosAutresDejaLa / nbPhotosAutresATelecharger;

                summaryTexte = context.getString(R.string.avancement_touteszones_progressbar_PX_summary1);
                summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
                summaryTexte = summaryTexte.replace("@PropPh", "" + avancementPhotoPrinc);

                summaryTexte += context.getString(R.string.avancement_touteszones_progressbar_PX_summary2);
                summaryTexte = summaryTexte.replace("@PropPh", "" + avancementPhotoAutres);
            }

        } else {
            // Texte des Avancements par Zones DORIS

            // P0 : Aucune photo à précharger
            if (nbPhotosPrincATelecharger == 0) {
                summaryTexte = context.getString(R.string.avancement_zone_progressbar_P0_summary);
                summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
            }

            // P1 : La photo principale seule
            if (nbPhotosPrincATelecharger != 0 && nbPhotosAutresATelecharger == 0) {

                avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;

                summaryTexte = context.getString(R.string.avancement_zone_progressbar_P1_summary);
                summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
                summaryTexte = summaryTexte.replace("@nbPh", "" + nbPhotosPrincDejaLa);
                summaryTexte = summaryTexte.replace("@totalPh", "" + nbPhotosPrincATelecharger);
            }

            // PX : La photo principale seule + autres photos
            if (nbPhotosPrincATelecharger != 0 && nbPhotosAutresATelecharger != 0) {

                avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
                avancementPhotoAutres = 100 * nbPhotosAutresDejaLa / nbPhotosAutresATelecharger;

                summaryTexte = context.getString(R.string.avancement_zone_progressbar_PX_summary1);
                summaryTexte = summaryTexte.replace("@nbF", "" + nbFichesZoneGeo);
                summaryTexte = summaryTexte.replace("@nbPh", "" + nbPhotosPrincDejaLa);
                summaryTexte = summaryTexte.replace("@totalPh", "" + nbPhotosPrincATelecharger);

                summaryTexte += context.getString(R.string.avancement_zone_progressbar_PX_summary2);
                summaryTexte = summaryTexte.replace("@nbPh", "" + nbPhotosAutresDejaLa);
                summaryTexte = summaryTexte.replace("@totalPh", "" + nbPhotosAutresATelecharger);
            }

        }



        // TODO calculate download in progress
        boolean downloadInProgress = false;
        if (inZoneGeo.getId() == -1 && DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null) {
            downloadInProgress = true;
        }

        if (progressBarZone != null) {
            progressBarZone.update(summaryPrefix + summaryTexte, affichageBarrePhotoPrinc, avancementPhotoPrinc, affichageBarrePhotoAutres, avancementPhotoAutres, downloadInProgress);
        }
        return summaryPrefix + summaryTexte;
    }

    private void refreshDiskDisponible() {
        // Quels emplacements sont disponibles
        carteInterneDispo = !DiskEnvironmentHelper.getPrimaryExternalStorage().isEmulated();
        //Log.d(LOG_TAG, "createGestionDisk() - carteInterneDispo : "+carteInterneDispo);

        carteExterneDispo = DiskEnvironmentHelper.isSecondaryExternalStorageAvailable(this);
        //Log.d(LOG_TAG, "createGestionDisk() - carteExterneDispo : "+carteExterneDispo);
    }

    /**
     * Affiche la taille des dossiers de l'Espace de Stockage Sélectionné
     * & Gestion des boutons de vidages des dossiers de l'Espace de Stockage Sélectionné
     */
    protected void refreshFolderSize() {
        // if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshFolderSize() - Début");

        StringBuilder etatDiskStringBuilder = new StringBuilder();
        Boolean auMoins1DossierNonVide = false;

        TextView gestionPhotosTextView = (TextView) findViewById(R.id.etatmodehorsligne_gestion_photos_description_textView);

        etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_titre));

        int sizeFolder = getPhotosOutils().getImageCountInFolderInPreferedLocation(ImageType.VIGNETTE);
        if (sizeFolder != 0) {
            auMoins1DossierNonVide = true;

            etatDiskStringBuilder.append("\n\t");
            etatDiskStringBuilder.append(sizeFolder);
            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_vignettes));
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.VIGNETTE)));

            btnGestionPhotosResetVig.setEnabled(true);
        } else {
            btnGestionPhotosResetVig.setEnabled(false);
        }
        // Si Travail en cours => Bouton Disabled
        if (isMovingPhotos || isTelechPhotos) btnGestionPhotosResetVig.setEnabled(false);


        sizeFolder = getPhotosOutils().getImageCountInFolderInPreferedLocation(ImageType.MED_RES);
        if (sizeFolder != 0) {
            auMoins1DossierNonVide = true;

            etatDiskStringBuilder.append("\n\t");
            etatDiskStringBuilder.append(sizeFolder);
            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_med_res));
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.MED_RES)));

            btnGestionPhotosResetMedRes.setEnabled(true);
        } else {
            btnGestionPhotosResetMedRes.setEnabled(false);
        }
        if (isMovingPhotos || isTelechPhotos) btnGestionPhotosResetMedRes.setEnabled(false);

        sizeFolder = getPhotosOutils().getImageCountInFolderInPreferedLocation(ImageType.HI_RES);
        if (sizeFolder != 0) {
            auMoins1DossierNonVide = true;

            etatDiskStringBuilder.append("\n\t");
            etatDiskStringBuilder.append(sizeFolder);
            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_hi_res));
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.HI_RES)));

            long test = getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.HI_RES);
            if (BuildConfig.DEBUG)
                Log.d(LOG_TAG, "refreshFolderSize() - getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.HI_RES) : "
                        + test);
            if (BuildConfig.DEBUG)
                Log.d(LOG_TAG, "refreshFolderSize() - getDisqueOutils().getHumanDiskUsage(ImageType.HI_RES) : "
                        + getDisqueOutils().getHumanDiskUsage(test));

            btnGestionPhotosResetHiRes.setEnabled(true);
        } else {
            btnGestionPhotosResetHiRes.setEnabled(false);
        }
        if (isMovingPhotos || isTelechPhotos) btnGestionPhotosResetHiRes.setEnabled(false);

        sizeFolder = getPhotosOutils().getImageCountInFolderInPreferedLocation(ImageType.PORTRAITS)
                + getPhotosOutils().getImageCountInFolderInPreferedLocation(ImageType.ILLUSTRATION_BIBLIO)
                + getPhotosOutils().getImageCountInFolderInPreferedLocation(ImageType.ILLUSTRATION_DEFINITION);
        if (sizeFolder != 0) {
            auMoins1DossierNonVide = true;

            etatDiskStringBuilder.append("\n\t");
            etatDiskStringBuilder.append(sizeFolder);
            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_autres));
            etatDiskStringBuilder.append(
                    getDisqueOutils().getHumanDiskUsage(
                            getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.PORTRAITS)
                                    + getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.ILLUSTRATION_BIBLIO)
                                    + getPhotosOutils().getPhotoDiskUsageInPreferedLocation(ImageType.ILLUSTRATION_DEFINITION)
                    ));

            btnGestionPhotosResetAutres.setEnabled(true);
        } else {
            btnGestionPhotosResetAutres.setEnabled(false);
        }
        if (isMovingPhotos || isTelechPhotos) btnGestionPhotosResetAutres.setEnabled(false);


        sizeFolder = getPhotosOutils().getImageCountInCache();
        if (sizeFolder != 0) {
            auMoins1DossierNonVide = true;

            etatDiskStringBuilder.append("\n\t");
            etatDiskStringBuilder.append(sizeFolder);
            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_cache));

            btnGestionPhotosResetCache.setEnabled(true);
        } else {
            btnGestionPhotosResetCache.setEnabled(false);
        }
        if (isMovingPhotos || isTelechPhotos) btnGestionPhotosResetCache.setEnabled(false);


        if (!auMoins1DossierNonVide) {
            etatDiskStringBuilder.append("\n\t");
            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_foldersize_vide));
        }

        gestionPhotosTextView.setText(etatDiskStringBuilder.toString());

        // Si encours de traitement on affiche la ProgressBar sinon on la cache
        ProgressBar deplacementEnCoursProgressBar = (ProgressBar) findViewById(R.id.etatmodehorsligne_gestion_photos_buttons_progressBar);
        if (isMovingPhotos) {
            deplacementEnCoursProgressBar.setVisibility(View.VISIBLE);
        } else deplacementEnCoursProgressBar.setVisibility(View.GONE);


        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshFolderSize() - Fin");
    }

    /**
     * affiche les info d'utilisation du disque
     * Note, cela a été séparé car normalement je voulais le mettre dans un asynctask, mais on ne peut en exécuter
     * qu'une à la fois, ce qui est bloqué si un téléchargement est en cours
     * -> prévoir de migrer ces asynctask dans des Services
     */
    private void refreshUsedDisk() {
        // Mise à jour de la place utilisée sur chaque disque
        if (carteExterneDispo) {
            refreshUsedDisk(getPhotosOutils().getPhotosDiskUsage(ImageLocation.APP_INTERNAL), getPhotosOutils().getPhotosDiskUsage(ImageLocation.PRIMARY), getPhotosOutils().getPhotosDiskUsage(ImageLocation.SECONDARY));
        } else {
            refreshUsedDisk(getPhotosOutils().getPhotosDiskUsage(ImageLocation.APP_INTERNAL), getPhotosOutils().getPhotosDiskUsage(ImageLocation.PRIMARY), 0);
        }
    }

    protected void refreshUsedDisk(long internalUsedSize, long primaryUsedSize, long secondaryUsedSize) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Début");

        StringBuilder etatDiskStringBuilder = new StringBuilder();

        /*
         * Espace de Stockage Sélectionné
         */

        etatDiskStringBuilder.append("Espace de Stockage utilisé :\n\t");
        switch (getPhotosOutils().getPreferedLocation()) {
            case APP_INTERNAL:
                etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_libelle));
                break;
            case PRIMARY:
                etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_libelle));
                break;
            case SECONDARY:
                etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_libelle));
                break;
        }
        etatDiskStringBuilder.append("\n\n");

        /*
         * Utilisation des Disques
         */
        etatDiskStringBuilder.append("Utilisation des Disques\n");
        etatDiskStringBuilder.append("\t(Espace utilisé / disponible / total)\n");

        // Mémoire interne
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Mémoire interne");

        etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_libelle) + " :\n\t");
        etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(internalUsedSize) + " / ");
        etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(DiskEnvironmentHelper.getInternalStorage().getSize().first) + " / ");
        etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(DiskEnvironmentHelper.getInternalStorage().getSize().second) + "\n");
        //etatDiskStringBuilder.append(DiskEnvironment.getInternalStorage().getFile().getAbsolutePath()+"\n");
        //etatDiskStringBuilder.append("Donnée application="+this.getDir(Photos_Outils.MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE)+"\n");
        //etatDiskStringBuilder.append("'Hash' pour vérifier si Carte SD Interne != Stockage interne : "
        //		+DiskEnvironment.getInternalStorage().getSize().first+"-"+DiskEnvironment.getInternalStorage().getSize().second+"\n");

        // Disque primaire (Carte SD Interne dans les paramètres)
        if (!DiskEnvironmentHelper.getPrimaryExternalStorage().isEmulated()) {
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Disque primaire (Carte SD Interne)");

            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_libelle) + " :\n\t");
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(primaryUsedSize) + " / ");
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(DiskEnvironmentHelper.getPrimaryExternalStorage().getSize().first) + " / ");
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(DiskEnvironmentHelper.getPrimaryExternalStorage().getSize().second) + "\n");
            //etatDiskStringBuilder.append(DiskEnvironment.getPrimaryExternalStorage().getFile().getAbsolutePath()+"\n");
            //etatDiskStringBuilder.append("'Hash' pour vérifier si Carte SD Interne != Stockage interne : "
            //		+DiskEnvironment.getPrimaryExternalStorage().getSize().first+"-"+DiskEnvironment.getPrimaryExternalStorage().getSize().second+"\n");
        }

        for (StorageVolume store : StorageHelper.getStorages(true)) {
            Log.d(LOG_TAG, "refreshUsedDisk() - StorageVolume " + store.toString());
        }

        // Carte SD externe (nommée amovible)
        if (DiskEnvironmentHelper.isSecondaryExternalStorageAvailable(this)) {
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Carte SD externe (nommée amovible)");

            etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_libelle) + " :\n\t");
            etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(secondaryUsedSize) + " / ");
            try {
                etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(DiskEnvironmentHelper.getSecondaryExternalStorage(this).getSize().first) + " / ");
                etatDiskStringBuilder.append(getDisqueOutils().getHumanDiskUsage(DiskEnvironmentHelper.getSecondaryExternalStorage(this).getSize().second) + "\n");
                //etatDiskStringBuilder.append(DiskEnvironment.getSecondaryExternalStorage().getFile().getAbsolutePath()+"\n");

            } catch (NoSecondaryStorageException e) {
                etatDiskStringBuilder.append(" not Available");
            }
        }
        //etatDiskStringBuilder.append("Photo actuellement sur : "+new Photos_Outils(EtatModeHorsLigne_CustomViewActivity.this).getPreferedLocation()+"\n");
        TextView gestionDiskTextView = (TextView) findViewById(R.id.etatmodehorsligne_gestion_disk_description_textView);

        gestionDiskTextView.setText(etatDiskStringBuilder.toString());

        // Si encours de traitement on affiche la ProgressBar sinon on la cache
        ProgressBar deplacementEnCoursProgressBar = (ProgressBar) findViewById(R.id.etatmodehorsligne_gestion_disk_buttons_progressBar);
        if (isMovingPhotos) {
            deplacementEnCoursProgressBar.setVisibility(View.VISIBLE);
        } else deplacementEnCoursProgressBar.setVisibility(View.GONE);

        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Fin");
    }

    public void refreshGestionDisk() {

        // Déplacement arrêté en cours
        boolean deplaceEnCours = getParamOutils().getParamBoolean(R.string.pref_key_deplace_photo_encours, false);

        // Processus de Déplacement en cours

        ImageLocation imageLocationCourante = getPhotosOutils().getPreferedLocation();
        ImageLocation imageLocationPrecedente = getPhotosOutils().getLocationPrecedente();


        // -- Mémoire Interne -- //
        // Si ni la carte Interne, ni la Carte Externe ne sont présentes aucun mouvement n'est possible
        if ((carteInterneDispo && getPhotosOutils().getPhotosDiskUsage(ImageLocation.PRIMARY) != 0)
                || (carteExterneDispo && getPhotosOutils().getPhotosDiskUsage(ImageLocation.SECONDARY) != 0)) {

            btnInternalDiskDepl.setEnabled(true);
            btnInternalDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_internal_depl_btn_text_selected));

            // Si un déplacement vers la Mémoire Interne a été arrêté avant sa fin et qu'aucun mouvement n'a repris
            // On propose la reprise
            if ((!isMovingPhotos)
                    && (deplaceEnCours)
                    && (imageLocationCourante == ImageLocation.APP_INTERNAL)) {
                btnInternalDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_internal_repr_btn_text_selected));
            }

        } else {

            btnInternalDiskDepl.setEnabled(false);
            btnInternalDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_internal_btn_text_not_available));

        }
        if (isMovingPhotos || isTelechPhotos) btnInternalDiskDepl.setEnabled(false);

        if (getPhotosOutils().getPhotosDiskUsage(ImageLocation.APP_INTERNAL) != 0) {
            btnInternalDiskSupp.setEnabled(true);
            btnInternalDiskSupp.setText(getString(R.string.etatmodehorsligne_diskselection_internal_supp_btn_text_selected));
        } else {
            btnInternalDiskSupp.setEnabled(false);
            btnInternalDiskSupp.setText(getString(R.string.etatmodehorsligne_gestion_disk_supp_btn_vide_text));
        }
        if (isMovingPhotos || isTelechPhotos) btnInternalDiskSupp.setEnabled(false);


        // -- Carte Mémoire Interne (Non Amovible, en fait une partition de la Mémoire Interne destinée à stocker les données des Applications) -- //
        // Affichage ou non de la Carte Interne
        if (carteInterneDispo) {
            TableRow trGestionDiskPrimary = (TableRow) findViewById(R.id.etatmodehorsligne_gestion_disk_primary_row);
            trGestionDiskPrimary.setVisibility(View.VISIBLE);

            if ((getPhotosOutils().getPhotosDiskUsage(ImageLocation.APP_INTERNAL) != 0)
                    || (carteExterneDispo && getPhotosOutils().getPhotosDiskUsage(ImageLocation.SECONDARY) != 0)) {
                btnPrimaryDiskDepl.setEnabled(true);
                btnPrimaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_primary_depl_btn_text_selected));

                // Si un déplacement vers le Carte Mémoire Interne a été arrêté avant sa fin et qu'aucun mouvement n'a repris
                // On propose la reprise
                if ((!isMovingPhotos)
                        && (deplaceEnCours)
                        && (imageLocationCourante == ImageLocation.PRIMARY)) {
                    btnPrimaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_primary_repr_btn_text_selected));
                }

            } else {
                btnPrimaryDiskDepl.setEnabled(false);
                btnPrimaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_primary_btn_text_not_available));
            }
            if (isMovingPhotos || isTelechPhotos) btnPrimaryDiskDepl.setEnabled(false);

            if (getPhotosOutils().getPhotosDiskUsage(ImageLocation.PRIMARY) != 0) {
                btnPrimaryDiskSupp.setEnabled(true);
                btnPrimaryDiskSupp.setText(getString(R.string.etatmodehorsligne_diskselection_primary_supp_btn_text_selected));
            } else {
                btnPrimaryDiskSupp.setEnabled(false);
                btnPrimaryDiskSupp.setText(getString(R.string.etatmodehorsligne_gestion_disk_supp_btn_vide_text));
            }
            if (isMovingPhotos || isTelechPhotos) btnPrimaryDiskSupp.setEnabled(false);

        } else {
            TableRow trGestionDiskPrimary = (TableRow) findViewById(R.id.etatmodehorsligne_gestion_disk_primary_row);
            trGestionDiskPrimary.setVisibility(View.GONE);
        }


        // -- Carte Mémoire Externe (Amovible) -- //
        // Désactivation des boutons de la carte externe qd elle n'est pas disponible
        if (DiskEnvironmentHelper.isSecondaryExternalStorageAvailable(this)) {

            btnSecondaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_depl_btn_text_selected));

            if ((getPhotosOutils().getPhotosDiskUsage(ImageLocation.APP_INTERNAL) != 0)
                    || (carteInterneDispo && getPhotosOutils().getPhotosDiskUsage(ImageLocation.PRIMARY) != 0)) {
                btnSecondaryDiskDepl.setEnabled(true);

                // Si un déplacement vers le Carte Mémoire Externe a été arrêté avant sa fin et qu'aucun mouvement n'a repris
                // On propose la reprise
                if ((!isMovingPhotos)
                        && (deplaceEnCours)
                        && (imageLocationCourante == ImageLocation.SECONDARY)) {
                    btnSecondaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_repr_btn_text_selected));
                }

            } else {
                btnSecondaryDiskDepl.setEnabled(false);
            }
            if (isMovingPhotos || isTelechPhotos) btnSecondaryDiskDepl.setEnabled(false);

            btnSecondaryDiskSupp.setVisibility(View.VISIBLE);
            if (getPhotosOutils().getPhotosDiskUsage(ImageLocation.SECONDARY) != 0) {
                btnSecondaryDiskSupp.setEnabled(true);
                btnSecondaryDiskSupp.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_supp_btn_text_selected));
            } else {
                btnSecondaryDiskSupp.setEnabled(false);
                btnSecondaryDiskSupp.setText(getString(R.string.etatmodehorsligne_gestion_disk_supp_btn_vide_text));
            }
            if (isMovingPhotos || isTelechPhotos) btnSecondaryDiskSupp.setEnabled(false);


        } else {
            // Si pas de carte amovible : message disant qu'il n'y en a pas et on masque bouton supprimant toutes les images
            btnSecondaryDiskDepl.setEnabled(false);
            btnSecondaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_btn_text_not_available));

            btnSecondaryDiskSupp.setVisibility(View.INVISIBLE);
        }


        switch (imageLocationCourante) {
            case APP_INTERNAL:
                if (deplaceEnCours) {
                    btnInternalDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + imageLocationPrecedente.name() + "2" + ImageLocation.APP_INTERNAL.name()));
                }
                btnPrimaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + ImageLocation.APP_INTERNAL.name() + "2" + ImageLocation.PRIMARY.name()));
                btnSecondaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + ImageLocation.APP_INTERNAL.name() + "2" + ImageLocation.SECONDARY.name()));
                break;
            case PRIMARY:
                btnInternalDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + ImageLocation.PRIMARY.name() + "2" + ImageLocation.APP_INTERNAL.name()));
                if (deplaceEnCours) {
                    btnPrimaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + imageLocationPrecedente.name() + "2" + ImageLocation.PRIMARY.name()));
                }
                btnSecondaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + ImageLocation.PRIMARY.name() + "2" + ImageLocation.SECONDARY.name()));
                break;
            case SECONDARY:
                btnInternalDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + ImageLocation.SECONDARY.name() + "2" + ImageLocation.APP_INTERNAL.name()));
                btnPrimaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + ImageLocation.SECONDARY.name() + "2" + ImageLocation.PRIMARY.name()));
                if (deplaceEnCours) {
                    btnSecondaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE + "-" + imageLocationPrecedente.name() + "2" + ImageLocation.SECONDARY.name()));
                }
                break;
        }

		
		/*
    	getParamOutils().getParamBoolean(R.string.pref_key_deplace_photo_encours, false)
		 */

    }

    private void addReusableClickListener(final String action, final String source, final String target) {

        if (action.equals(GestionPhotoDiskService.ACT_MOVE)) {
            reusableClickListener.put(action + "-" + source + "2" + target, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Déplace les fichiers de la source vers la cible

                    AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(getContext());


                    if (target.equals(ImageLocation.APP_INTERNAL.name())) {
                        alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_confirmation));
                    } else if (target.equals(ImageLocation.PRIMARY.name())) {
                        alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_confirmation));
                    } else if (target.equals(ImageLocation.SECONDARY.name())) {
                        alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_confirmation));
                    }
                    alertDialogbD.setCancelable(true);

                    // On déplace le disque si validé
                    alertDialogbD.setPositiveButton(getContext().getString(R.string.btn_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // utilise le déplacement sous forme de service
                                    // use this to start and trigger a service
                                    Intent i = new Intent(getApplicationContext(), GestionPhotoDiskService.class);
                                    // add data to the intent
                                    i.putExtra(GestionPhotoDiskService.INTENT_ACTION, GestionPhotoDiskService.ACT_MOVE);
                                    i.putExtra(GestionPhotoDiskService.INTENT_SOURCE, source);
                                    i.putExtra(GestionPhotoDiskService.INTENT_TARGET, target);

                                    getApplicationContext().startService(i);

                                    DorisApplicationContext.getInstance().notifyDataHasChanged(null);

                                }
                            });

                    // Abandon donc Rien à Faire
                    alertDialogbD.setNegativeButton(getContext().getString(R.string.btn_annul),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogbD.create();
                    alertDialog.show();
                }
            });
        } else if (action.equals(GestionPhotoDiskService.ACT_DELETE_DISK)) {

            //Log.d(LOG_TAG, "Création reusableClickListener - "+action+"-"+source);

            reusableClickListener.put(action + "-" + source, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(getContext());
                    alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_gestion_reset_confirmation));
                    alertDialogbD.setCancelable(true);

                    // On vide le disque si validé
                    alertDialogbD.setPositiveButton(getContext().getString(R.string.btn_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // utilise la suppression sous forme de service

                                    Intent i = new Intent(getApplicationContext(), GestionPhotoDiskService.class);

                                    i.putExtra(GestionPhotoDiskService.INTENT_ACTION, GestionPhotoDiskService.ACT_DELETE_DISK);
                                    i.putExtra(GestionPhotoDiskService.INTENT_SOURCE, source);
                                    i.putExtra(GestionPhotoDiskService.INTENT_TARGET, "");
                                    getApplicationContext().startService(i);

                                    DorisApplicationContext.getInstance().notifyDataHasChanged(null);
                                }
                            });

                    // Abandon donc Rien à Faire
                    alertDialogbD.setNegativeButton(getContext().getString(R.string.btn_annul),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogbD.create();
                    alertDialog.show();

                }
            });

        } else if (action.equals(GestionPhotoDiskService.ACT_DELETE_FOLDER)) {

            //Log.d(LOG_TAG, "Création reusableClickListener - "+action+"-"+source);

            reusableClickListener.put(action + "-" + source, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(getContext());
                    alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_gestion_reset_confirmation));
                    alertDialogbD.setCancelable(true);

                    // On vide le dossier si validé
                    alertDialogbD.setPositiveButton(getContext().getString(R.string.btn_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    // utilise la suppression sous forme de service

                                    Intent i = new Intent(getApplicationContext(), GestionPhotoDiskService.class);

                                    i.putExtra(GestionPhotoDiskService.INTENT_ACTION, GestionPhotoDiskService.ACT_DELETE_FOLDER);
                                    i.putExtra(GestionPhotoDiskService.INTENT_SOURCE, source);
                                    i.putExtra(GestionPhotoDiskService.INTENT_TARGET, "");
                                    getApplicationContext().startService(i);

                                    DorisApplicationContext.getInstance().notifyDataHasChanged(null);
                                }
                            });
                    // Abandon donc Rien à Faire
                    alertDialogbD.setNegativeButton(getContext().getString(R.string.btn_annul),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogbD.create();
                    alertDialog.show();

                }
            });

        }
    }

    private Photos_Outils getPhotosOutils() {
        if (photosOutils == null) photosOutils = new Photos_Outils(getContext());
        return photosOutils;
    }

    private Param_Outils getParamOutils() {
        if (paramOutils == null) paramOutils = new Param_Outils(getContext());
        return paramOutils;
    }

    private Disque_Outils getDisqueOutils() {
        if (disqueOutils == null) disqueOutils = new Disque_Outils(getContext());
        return disqueOutils;
    }

    private Fiches_Outils getFichesOutils() {
        if (fichesOutils == null) fichesOutils = new Fiches_Outils(getContext());
        return fichesOutils;
    }

    //End of user code

    /**
     * refresh screen from data
     */
    public void refreshScreenData() {
        //Start of user code action when refreshing the screen EtatModeHorsLigne_CustomViewActivity

        isMovingPhotos = DorisApplicationContext.getInstance().isMovingPhotos;
        isTelechPhotos = DorisApplicationContext.getInstance().isTelechPhotos;

        // mise à jour de la date de la base
        TextView etatBase = (TextView) findViewById(R.id.etatmodehorsligne_etat_base_description_textView);
        try {
            CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
            while (it.hasNext()) {
                etatBase.setText(getString(R.string.etatmodehorsligne_etat_base_description_text) + it.next().getDateBase());
                //sb.append("Date base locale : " + it.next().getDateBase()+"\n");
            }
            it.close();
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (java.lang.IllegalStateException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            // en cas d'erreur d'état, on annule tout, on est probablement en train de clore l'appli
            return;
        }

        // Mise à jour des Barres d'avancement
        if (!isMovingPhotos) refreshProgressBarZone();

        // Mise à jour du nombres de fichiers par dossier
        getPhotosOutils().refreshImagesNbInFolder();

        // Mise à jour des disques disponibles
        refreshDiskDisponible();

        // Mise à jour des tailles des dossiers
        // & Mise à jour des Boutons de Gestion des Dossiers
        refreshFolderSize();

        // Mise à jour de l'utilisation des Disques
        if (!isTelechPhotos) refreshUsedDisk();

        // mise à jour des Boutons de Gestion des Disques
        refreshGestionDisk();

        //End of user code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.etatmodehorsligne_customview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu EtatModeHorsLigne_CustomViewActivity
        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        switch (item.getItemId()) {
            case R.id.etatmodehorsligne_customview_action_preference:
                startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
                return true;
            //Start of user code additional menu action EtatModeHorsLigne_CustomViewActivity
            case R.id.etatmodehorsligne_customview_action_telecharge_photofiches:
                TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
                if (telechargePhotosFiches_BgActivity == null || telechargePhotosFiches_BgActivity.getStatus() != Status.RUNNING) {
                    DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity =
                            (TelechargePhotosAsync_BgActivity) new TelechargePhotosAsync_BgActivity(getApplicationContext()/*, this.getHelper()*/).execute("");

                } else {
                    showToast(R.string.bg_notifToast_arretTelecharg);
                    DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);

                    ProgressBar pbRunningBarLayout = (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
                    pbRunningBarLayout.setVisibility(View.GONE);
                }

                return true;
            case R.id.etatmodehorsligne_customview_action_a_propos:
                AffichageMessageHTML aPropos = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
                aPropos.affichageMessageHTML(getContext().getString(R.string.a_propos_label) + getContext().getString(R.string.app_name), aPropos.aProposAff(), "file:///android_res/raw/apropos.html");
                return true;
            case R.id.etatmodehorsligne_customview_action_aide:
                AffichageMessageHTML aide = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
                aide.affichageMessageHTML(getContext().getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html#ParamHorsLigne");
                return true;
            //End of user code
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                /* finish(); */
				/*
	        	TaskStackBuilder.create(this)
	                // Add all of this activity's parents to the back stack
	                .addNextIntentWithParentStack(getSupportParentActivityIntent())
	                // Navigate up to the closest parent
	                .startActivities();
	            */
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  ------------ dealing with Up button
    @Override
    public Intent getSupportParentActivityIntent() {
        //Start of user code getSupportParentActivityIntent EtatModeHorsLigne_CustomViewActivity
        // navigates to the parent activity
        return new Intent(this, Accueil_CustomViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack EtatModeHorsLigne_CustomViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }
}
