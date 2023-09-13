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
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.SortModesTools;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.ffessm.doris.android.tools.Zones_Outils;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import java.io.File;
//Start of user code additional imports Accueil_CustomViewActivity
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.CloseableIterator;

import android.widget.ImageButton;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.activities.view.MultiProgressBar;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Disque_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.disk.DiskEnvironmentHelper;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;
import fr.ffessm.doris.android.tools.disk.StorageHelper;
import fr.ffessm.doris.android.tools.disk.StorageHelper.StorageVolume;

//End of user code
public class Accueil_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements Accueil_CustomViewActivity
        implements DataChangedListener
//End of user code
{

    //Start of user code constants Accueil_CustomViewActivity

    private static final String LOG_TAG = Accueil_CustomViewActivity.class.getSimpleName();
    Handler mHandler;
    LinearLayout llContainerLayout;

    boolean isOnCreate = true;

    Fiches_Outils fichesOutils;
    App_Outils outils;
    Param_Outils paramOutils;
    Reseau_Outils reseauOutils;
    Photos_Outils photosOutils;


    // deal with zone folding/unfolding
    public ImageButton btnFoldUnfoldZoneSection;
    private LinearLayout llFoldUnfoldZoneSection;
    private boolean isZoneFold = false;
    protected List<View> allFoldableZoneView  = new ArrayList<>();


    public ImageButton btnFoldUnfoldSpecieGroupSection;

    // deal with mode folding/unfolding
    public ImageButton btnFoldUnfoldModeSection;
    private boolean isModeFold = false;
    protected List<View> allFoldableModeView  = new ArrayList<>();

    protected SparseArray<MultiProgressBar> progressBarZones = new SparseArray<>();

    // si false alors c'est que l'utilisateur a cliqué sur la croix pour le fermer,
    // tant que l'appli est ouverte elle ne se rouvrira pas, même en cas de rotation
    public static boolean mustShowLogoFede = true;

    //End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.accueil_customview);
        //Start of user code onCreate Accueil_CustomViewActivity
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Début");

	/*	// si pas de fiche alors il faut initialiser la base à partir du prefetched_DB
		RuntimeExceptionDao<Fiche, Integer> ficheDao = getHelper().getFicheDao();
    	if(ficheDao.countOf() == 0){
    		new InitialisationApplication_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
    		
    		showToast("Veuillez patienter que la base de donnée s'initialise.");
		}*/

        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
                if (Accueil_CustomViewActivity.this.isFinishing() || Accueil_CustomViewActivity.this.isActivityDestroyed())
                    return;
                if (inputMessage.obj != null) {
                    showToast((String) inputMessage.obj);
                }
                refreshScreenData();
            }
        };

        // Affichage Icônes Fédé.
        if (!mustShowLogoFede || !getParamOutils().getParamBoolean(R.string.pref_key_accueil_aff_iconesfede, true)) {
            ((RelativeLayout) findViewById(R.id.accueil_logos)).setVisibility(View.GONE);
        }

        // Affichage Debug
        if (getParamOutils().getParamBoolean(R.string.pref_key_affichage_debug, false)) {
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Affichage Debug");
            ((RelativeLayout) findViewById(R.id.accueil_logos)).setVisibility(View.GONE);
            ((ScrollView) findViewById(R.id.accueil_debug)).setVisibility(View.VISIBLE);
        }

        // Affichage zone géo
        createNavigationZonesGeoViews();

        // Affichage groupes d'espèces
        createSpecieGroupViews();

        // Affichage modeAffichages
        createModeAffichageViews();

        //Lors du 1er démarrage de l'application dans la version actuelle,
        //on affiche la boite d'A Propos
        String VersionAffichageAPropos = getParamOutils().getParamString(R.string.pref_key_a_propos_version, "");
        if (BuildConfig.DEBUG)
            Log.v(LOG_TAG, "onCreate() - VersionAffichageAPropos : " + VersionAffichageAPropos);

        //Récupération du numéro de Version de DORIS
        String appVersionName = getOutils().getAppVersion();
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - appVersionName : " + appVersionName);

        if (!VersionAffichageAPropos.equals(appVersionName)) {
            if (BuildConfig.DEBUG)
                Log.v(LOG_TAG, "onCreate() - Affichage A Propos : " + appVersionName);
            AffichageMessageHTML aPropos = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
            //affiche les changements récents
            aPropos.affichageMessageHTML(getContext().getString(R.string.a_propos_label) + getContext().getString(R.string.app_name), aPropos.aProposAff(), "file:///android_res/raw/apropos.html#changements");
            getParamOutils().setParamString(R.string.pref_key_a_propos_version, appVersionName);
        }

        if (DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null) {
            // une tache précédente est en cours, on se réabonne aux évènements
            // (on est probablement sur une rotation d'écran)
            Log.d(LOG_TAG, "onCreate() - une tache précédente est en cours, on se réabonne aux évènements");
        } else {
            // pas de tache précédente en cours
            // démarre ou pas un téléchargement de photos au démarrage
            Reseau_Outils.ConnectionType connectionType = getReseauOutils().getConnectionType();
            Log.d(LOG_TAG, "onCreate() - connectionType : " + connectionType);
            boolean wifiOnly = getParamOutils().getParamBoolean(R.string.pref_key_mode_precharg_wifi_only, true);
            Log.d(LOG_TAG, "onCreate() - wifiOnly : " + wifiOnly);
            if (connectionType == Reseau_Outils.ConnectionType.WIFI
                    || (!wifiOnly && connectionType == Reseau_Outils.ConnectionType.GSM)) {

                // On démarrage d'abord la MaJ des photos,
                Log.d(LOG_TAG, "onCreate() - Lancement Telechargement Photos");

                DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity =
                        (TelechargePhotosAsync_BgActivity) new TelechargePhotosAsync_BgActivity(
                                getApplicationContext()).execute("");
            }
        }

        DorisApplicationContext.getInstance().addDataChangeListeners(this);

        // Initialisation par défaut : retour à l'accueil
        DorisApplicationContext.getInstance().resetIntentPrecedent(getIntent());

        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Fin");
        //End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
        //Start of user code onResume Accueil_CustomViewActivity
        Log.d(LOG_TAG, "onResume()");

        DorisApplicationContext.getInstance().resetIntentPrecedent(getIntent());

        //End of user code
    }

    //Start of user code additional code Accueil_CustomViewActivity
    @Override
    protected void onPause() {
        super.onPause();
        //PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        Log.d(LOG_TAG, "onDestroy() - isFinishing() : " + isFinishing());

        DorisApplicationContext.getInstance().removeDataChangeListeners(this);

        TelechargePhotosAsync_BgActivity telechargePhotosAsync_BgAct = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
        if (telechargePhotosAsync_BgAct != null && telechargePhotosAsync_BgAct.getStatus() == Status.RUNNING) {
            if (isFinishing())
                Log.d(LOG_TAG, "onDestroy() - TelechargePhotosAsync.cancel(true) : " + telechargePhotosAsync_BgAct.cancel(true));
        }

        super.onDestroy();
    }

    /**
     * Création de la liste pliable des Zones
     */
    protected void createNavigationZonesGeoViews() {

        LinearLayout llContainerLayout = (LinearLayout) findViewById(R.id.accueil_navigation_zones_layout);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ZoneGeographique currentZoneFilter=null;
        int currentZoneFilterId = prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1);
        if (currentZoneFilterId == -1 || currentZoneFilterId == 0) { // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
            // pas de zone précdente
        } else {
            currentZoneFilter = getHelper().getZoneGeographiqueDao().queryForId(currentZoneFilterId);
        }

        // deal with fold/unfold
        btnFoldUnfoldZoneSection = (ImageButton) findViewById(R.id.accueil_zone_fold_unfold_section_imageButton);
        llFoldUnfoldZoneSection = (LinearLayout) findViewById(R.id.accueil_navigation_zones_layout);
        isZoneFold = true;
        btnFoldUnfoldZoneSection.setVisibility(View.VISIBLE);
        if(isZoneFold) {
            btnFoldUnfoldZoneSection.setImageBitmap(drawIconWithGear(getResources().getDrawable(R.drawable.doris_icone_toutes_zones)));
        } else {
            btnFoldUnfoldZoneSection.setImageBitmap(drawIconWithGear(getResources().getDrawable(R.drawable.doris_icone_toutes_zones)));
        }

        // btnFoldUnfoldZoneSection // toute la section sert de lien pour plier/déplier
        llFoldUnfoldZoneSection.setOnClickListener(v -> {
            foldUnfoldZoneSection();
        });
        btnFoldUnfoldZoneSection.setOnClickListener(v -> {
            foldUnfoldZoneSection();
        });


        // Affichage lien vers "toutes Zones"
        ZoneGeographique zoneToutesZones = new ZoneGeographique();
        zoneToutesZones.setToutesZones();
        View allZonesView = createNavigationZoneView(zoneToutesZones, 0);
        allFoldableZoneView.add(allZonesView);
        llContainerLayout.addView(allZonesView);
        allZonesView.setVisibility(View.GONE);

        // affichage lien vers les zones sauf la zone courante déjà présentée dans createCurrentZoneGeoViews
        List<ZoneGeographique> listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : " + listeZoneGeo.size());
        int i = 1;
        for (ZoneGeographique zoneGeo : listeZoneGeo) {
            // show all but previous zone (already displayed)
            // make sure to set the DBHelper on the zone
            if(zoneGeo.getContextDB() == null) {
                zoneGeo.setContextDB(this.getHelper().getDorisDBHelper());
            }

            View zoneView = createNavigationZoneView(zoneGeo, i);
            allFoldableZoneView.add(zoneView);
            llContainerLayout.addView(zoneView);
            zoneView.setVisibility(View.GONE);
            i++;
        }

    }

    /* Création de la Zone (Textes, Icônes et Boutons */
    protected View createNavigationZoneView(final ZoneGeographique zone, int index) {
        final Context context = this;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewZone = inflater.inflate(R.layout.zonegeoselection_listviewrow, null);

        // Nom et Description de la Zone
        TextView tvLabel = (TextView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_label);
        tvLabel.setText(zone.getNom());

        if (ScreenTools.getScreenWidth(context) > 500) { // TODO devra probablement être adapté lorsque l'on aura des fragments
            TextView tvLDetails = (TextView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_details);
            tvLDetails.setVisibility(View.VISIBLE);
            tvLDetails.setText(zone.getDescription());
        } else {
            viewZone.findViewById(R.id.zonegeoselection_listviewrow_details).setVisibility(View.GONE);
        }

        // Icône illustrant la Zone
        int imageZone = getFichesOutils().getZoneIconeId(zone.getZoneGeoKind());

        //int indentation = Zones_Outils.getZoneLevel(zone) * 32 ; // adjust indentation size
        //viewZone.setPadding(indentation, 0, 0, 0);
        LinearLayout treeNodeZone = (LinearLayout)viewZone.findViewById(R.id.zonegeoselection_tree_nodes);
        int zoneDepth = Zones_Outils.getZoneLevel(zone);
        for (int i = 0; i < zoneDepth; i++) {
            ImageView image = new ImageView(this);
            //image.setAdjustViewBounds(true);
            image.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            //image.setMaxHeight(treeNodeZone.getHeight());
            //image.setMaxWidth(32);
            //image.setMinimumWidth(32);
            //image.setImageResource(R.drawable.ic_app_filter_geo_zone);
            try {
                if(Zones_Outils.isLastChild(zone)){
                    image.setImageResource(R.drawable.ic_app_treenode_last_child);
                } else {
                    image.setImageResource(R.drawable.ic_app_treenode_middle_child);
                }
            } catch (SQLException throwables) {
                Log.e(LOG_TAG, "Error determining zonegeo sibling", throwables);
                throwables.printStackTrace();
            }
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            // Adds the view to the layout
            treeNodeZone.addView(image, 0);
        }

        ImageView ivIcone = (ImageView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_icon);
        ivIcone.setImageResource(imageZone);
        int iconeTaille = ScreenTools.dp2px(context, getParamOutils().getParamInt(R.string.pref_key_accueil_icone_taille,
                Integer.parseInt(context.getString(R.string.accueil_icone_taille_defaut))
        ));
        ivIcone.setMaxHeight(iconeTaille);
        ivIcone.setMaxWidth(iconeTaille);


        // Quelle est l'action principale : par défaut ouverture de la liste des fiches de la Zone
        // sinon ouverture de l'arbre phylogénétique
        final String accueil_liste_ou_arbre_pardefaut = getParamOutils().getParamString(R.string.pref_key_accueil_liste_ou_arbre_pardefaut, "liste");
        //Log.d(LOG_TAG, "accueil_liste_ou_arbre_pardefaut : "+accueil_liste_ou_arbre_pardefaut);


        // Gestion Clic Principal sur la Zone (partout sauf 2 boutons "secondaires" (càd de droite))
        viewZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = zone.getDescription();
                if(desc == null) {
                    desc= Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind());
                }
                showToast(desc);
            }
        });

        // BoutonRadio
        RadioButton imgBtnH = (RadioButton) viewZone.findViewById(R.id.zonegeoselection_selectBtn_radio);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int currentZoneFilterId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
        imgBtnH.setChecked(zone.getId() == currentZoneFilterId);
        imgBtnH.setOnClickListener( v -> {
            int size = allFoldableZoneView.size();
            for (int j = 0; j < size; j++) {
                View foldableZone = allFoldableZoneView.get(j);
                RadioButton radio = (RadioButton) foldableZone.findViewById(R.id.zonegeoselection_selectBtn_radio);
                if(j == index ) {
                    radio.setChecked(true);
                } else {
                    radio.setChecked(false);
                }
            }

            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
            // positionne la recherche pour cette zone
            ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zone.getId());
            ed.apply();
            // update main screen icons
            Accueil_CustomViewActivity.this.refreshScreenData();

            // close foldable layout
            foldUnfoldZoneSection();
        } );

        return viewZone;
    }

    private void foldUnfoldZoneSection() {
        isZoneFold = !isZoneFold;
        if(isZoneFold) {
            ((TextView)findViewById(R.id.accueil_zone_title)).setText(R.string.accueil_customview_show_other_zones);
        } else {
            ((TextView)findViewById(R.id.accueil_zone_title)).setText(R.string.accueil_customview_hide_other_zones);
        }
        for (View foldableZone : allFoldableZoneView) {
            if (isZoneFold) {
                foldableZone.setVisibility(View.GONE);
            } else {
                foldableZone.setVisibility(View.VISIBLE);
            }
        }
    }
    protected void createSpecieGroupViews() {
        LinearLayout llContainer = (LinearLayout) findViewById(R.id.accueil_specie_group_layout);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        btnFoldUnfoldSpecieGroupSection = (ImageButton) llContainer.findViewById(R.id.accueil_specie_group_fold_unfold_section_imageButton);
        btnFoldUnfoldSpecieGroupSection.setImageBitmap(drawIconWithGear(getResources().getDrawable(R.drawable.ic_action_arbre_phylogenetique)));
        final Context context = this;
        // toute la section et le bouton sert de lien pour plier/déplier
        View.OnClickListener cl =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
                Intent toGroupeSelectionView = new Intent(context, GroupeSelection_ClassListViewActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("GroupeSelection_depuisAccueil", true);
                toGroupeSelectionView.putExtras(b);

                showToast(getString(R.string.accueil_recherche_guidee_arbre_toast_text));
                startActivity(toGroupeSelectionView);
            }
        };
        llContainer.setOnClickListener(cl);
        btnFoldUnfoldSpecieGroupSection.setOnClickListener(cl);
    }
    protected void createModeAffichageViews() {
        LinearLayout llContainerLayout = (LinearLayout) findViewById(R.id.accueil_mode_affichage_layout);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        final String[] modesValues = this.getResources().getStringArray(R.array.accueil_listes_ou_arbre_values);
        String[] modesLibelles = this.getResources().getStringArray(R.array.accueil_listes_ou_arbre_lib);
        int[] icons = { R.drawable.ic_action_liste_fiches, R.drawable.ic_action_arbre_phylogenetique, R.drawable.ic_action_liste_images};
        for (int i = 0; i < modesValues.length ; i++) {
            String mode = modesLibelles[i];
            LayoutInflater inflater = (LayoutInflater) this
               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View viewMode = inflater.inflate(R.layout.mode_affichage_listviewrow, null);

            TextView tvLabel = (TextView) viewMode.findViewById(R.id.mode_affichage_listviewrow_text);
            tvLabel.setText(mode);

            ImageView iv = (ImageView) viewMode.findViewById(R.id.mode_affichage_listviewrow_icon);
            iv.setImageResource(icons[i]);

            RadioButton radio = (RadioButton) viewMode.findViewById(R.id.mode_affichage_listviewrow_radio);

            // set current check button value
            String current = prefs.getString(getResources().getString(
                    R.string.pref_key_accueil_liste_ou_arbre_pardefaut),
                    getResources().getString(
                            R.string.accueil_liste_ou_arbre_default));
            radio.setChecked(current.equals(modesValues[i]));

            final int radioIndex = i;
            radio.setOnClickListener(new View.OnClickListener() {
                final int index = radioIndex;

                public void onClick(View v) {
                    int size = allFoldableModeView.size();
                    for (int j = 0; j < size; j++) {
                        View foldableMode = allFoldableModeView.get(j);
                        RadioButton radio = (RadioButton) foldableMode.findViewById(R.id.mode_affichage_listviewrow_radio);
                        if(j == index ) {
                            radio.setChecked(true);
                        } else {
                            radio.setChecked(false);
                        }
                    }
                    // update preferences
                    prefs.edit().putString(
                            Accueil_CustomViewActivity.this.getResources().getString(
                                    R.string.pref_key_accueil_liste_ou_arbre_pardefaut),
                                    modesValues[index]).apply();
                    // update main screen icons
                    Accueil_CustomViewActivity.this.refreshScreenData();
                    // close foldable layout
                    foldUnfoldModeSection();
                }
            });

            allFoldableModeView.add(viewMode);
            llContainerLayout.addView(viewMode);
            viewMode.setVisibility(View.GONE);
       }

        // deal with fold/unfold
        btnFoldUnfoldModeSection = (ImageButton) findViewById(R.id.accueil_mode_affichage_fold_unfold_section_imageButton);
        btnFoldUnfoldModeSection.setImageBitmap(drawIconWithGear(getResources().getDrawable(R.drawable.ic_action_liste_fiches)));

        // toute la section sert de lien pour plier/déplier
        LinearLayout llFold = (LinearLayout) llContainerLayout.findViewById(R.id.accueil_mode_affichage_fold_layout);
        llFold.setOnClickListener(v -> {
            foldUnfoldModeSection();
        });
        btnFoldUnfoldModeSection.setOnClickListener(v -> {
            foldUnfoldModeSection();
        });
    }
    private void foldUnfoldModeSection() {
        isModeFold = !isModeFold;
        if(isModeFold) {
            ((TextView)findViewById(R.id.accueil_mode_affichage_title)).setText(R.string.accueil_customview_show_mode_affichage);
        } else {
            ((TextView)findViewById(R.id.accueil_mode_affichage_title)).setText(R.string.accueil_customview_hide_mode_affichage);
        }
        for (View foldableMode : allFoldableModeView) {
            if (isModeFold) {
                foldableMode.setVisibility(View.GONE);
            } else {
                foldableMode.setVisibility(View.VISIBLE);
            }
        }
    }

    /*public void onClickAfficherListe(View view){
    	showToast("L'idée est d'afficher directement la liste filtrée depuis ici, mais il faudrait que la ProgressionBar soit un objet plus propre.");
    }*/
    public void onClickBtnListeFiches(View view) {
        startActivity(new Intent(this, ListeFicheAvecFiltre_ClassListViewActivity.class));
    }

    public void onClickBtnRechercheGuidee(View view) {
        //Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
        Intent toGroupeSelectionView = new Intent(this, GroupeSelection_ClassListViewActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("GroupeSelection_depuisAccueil", true);
        toGroupeSelectionView.putExtras(b);
        startActivity(toGroupeSelectionView);
    }

    /*public void onClickBtnListeParticipants(View view){
        startActivity(new Intent(this, ListeParticipantAvecFiltre_ClassListViewActivity.class));
    }
    public void onClickBtnGlossaire(View view){
        startActivity(new Intent(this, Glossaire_ClassListViewActivity.class));
    }*/
    public void onClickBtnIconeSiteWeb_doris(View view) {
        String url = getString(R.string.accueil_customview_logo_doris_url);
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    public void onClickBtnIconeSiteBio(View view) {
        String url = getString(R.string.accueil_customview_logo_bio_url);
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    public void onClickBtnIconeSiteWebFFESSM(View view) {
        String url = getString(R.string.accueil_customview_logo_doris_url);
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    public void onClickBtnFermer(View view) {
        mustShowLogoFede = false;
        ((RelativeLayout) findViewById(R.id.accueil_logos)).setVisibility(View.GONE);

        showToast(getContext().getString(R.string.accueil_customview_logos_preference));
    }
	/*public void reinitializeDBFromPrefetched(){
		//XMLHelper.loadDBFromXMLFile(getHelper().getDorisDBHelper(), this.getResources().openRawResource(R.raw.prefetched_db));

		new InitialisationApplication_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
		showToast("Veuillez patienter que la base de donnée s'initialise.");
		
    }*/

    public void dataHasChanged(String textmessage) {
        Message completeMessage = mHandler.obtainMessage(1, textmessage);
        completeMessage.sendToTarget();
    }

    public Context getContext() {
        return this;
    }

    protected void updateProgressBarZone(ZoneGeographique inZoneGeo, MultiProgressBar progressBarZone) {
        // ajout au résumé de la date de la base
        StringBuilder sbTexte = new StringBuilder();
        sbTexte.append(getContext().getString(R.string.accueil_customview_texte_text));

        CloseableIterator<DorisDB_metadata> itDorisDB = getHelper().getDorisDB_metadataDao().iterator();
        while (itDorisDB.hasNext()) {
            sbTexte.append(itDorisDB.next().getDateBase());
        }
        sbTexte.append("\n");
        sbTexte.append(getPhotosOutils().getCurrentPhotosDiskUsageShortSummary(this));
        sbTexte.append(" ; ");

        EtatModeHorsLigne_CustomViewActivity.updateProgressBarZone(this, inZoneGeo, progressBarZone, sbTexte.toString());
    }

    /**
     * draw a bitmap with a gear on bottom right
     * @return
     */
    protected Bitmap drawIconWithGear(Drawable baseDrawable){

        int width = (int) (ScreenTools.dp2px(this,
                        getParamOutils().getParamInt(R.string.pref_key_accueil_icone_taille, Integer.parseInt(this.getString(R.string.accueil_icone_taille_defaut))))/1.5);
        int height = width;

        // Create a blank bitmap with the desired width and height
        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw on the bitmap
        Canvas canvas = new Canvas(combinedBitmap);
        Drawable gearDrawable = getResources().getDrawable(R.drawable.gear_grey);
        baseDrawable.setBounds(0,0,width, height);
        gearDrawable.setBounds(width/2, height/2, width, height);

        baseDrawable.draw(canvas);
        gearDrawable.draw(canvas);
        return combinedBitmap;
    }

    protected Bitmap drawIconForCurrentSearch(){

        int width = ScreenTools.dp2px(this,
                getParamOutils().getParamInt(R.string.pref_key_accueil_icone_taille, Integer.parseInt(this.getString(R.string.accueil_icone_taille_defaut))));
        int height = width;

        // Create a blank bitmap with the desired width and height
        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw on the bitmap
        Canvas canvas = new Canvas(combinedBitmap);


        // Load your drawables
        //Drawable backgroundDrawable = getResources().getDrawable(R.drawable.icon_background);
        Drawable zoneDrawable = getResources().getDrawable(getFichesOutils().getZoneIconeId(getCurrentZoneGeographique().getZoneGeoKind()));
        Drawable modeDrawable = SortModesTools.getDrawable(this, getCurrentMode());
        Groupe specieGroup = getCurrentSpecieFilter();
        Drawable specieDrawable;
        if (specieGroup != null && specieGroup.getCleURLImage() != null && !specieGroup.getCleURLImage().isEmpty()) {
            int identifierIconeGroupe = getResources().getIdentifier(specieGroup.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw", getPackageName());

            Bitmap bitmap = BitmapFactory.decodeStream(getResources().openRawResource(identifierIconeGroupe));
            specieDrawable = new BitmapDrawable(getResources(), bitmap);
        } else {
            // default image
            specieDrawable =  getResources().getDrawable(R.drawable.app_ic_launcher);
        }

        // Set bounds for the drawables (adjust these as needed)
        //backgroundDrawable.setBounds(0, 0, width, height);
        zoneDrawable.setBounds(0, 0, width/2, height/2);
        modeDrawable.setBounds(width/2, height/4, width, height - height/4);
        specieDrawable.setBounds(width/5, height/2, width/2 + width/5, height);

        // Draw the drawables onto the canvas
        //backgroundDrawable.draw(canvas);
        zoneDrawable.draw(canvas);
        modeDrawable.draw(canvas);
        specieDrawable.draw(canvas);
        return combinedBitmap;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "Preference change detected for key =" + key);
        if (key.equals(R.string.pref_key_theme)) {
            // change theme to the selected one
            showToast("Preference change detected for Theme=" + sharedPreferences.getString(key, "Default"));
            //	sharedPreferences.getString(key, )
            //	ThemeUtil.changeToTheme(this, theme)
        }
    }

    /**
     * get the ZoneGeographique as set in the preferences or "Touteszones" if no preferences
     * @return
     */
    private ZoneGeographique getCurrentZoneGeographique() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ZoneGeographique currentZoneFilter=null;
        int currentZoneFilterId = prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1);
        if (currentZoneFilterId == -1 || currentZoneFilterId == 0) { // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
            // no previous zone, use a "AllZone"
            currentZoneFilter = new ZoneGeographique();
            currentZoneFilter.setToutesZones();
        } else {
            currentZoneFilter = getHelper().getZoneGeographiqueDao().queryForId(currentZoneFilterId);
        }
        return currentZoneFilter;
    }

    /**
     * return the Groupe of the current specie filter or null if no filter
     */
    private Groupe getCurrentSpecieFilter() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int groupRootId = Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId();
        int filtreCourantId = prefs.getInt(getString(R.string.pref_key_filtre_groupe), groupRootId);
        if (filtreCourantId == groupRootId) {
            return null;
        } else {
            Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);
            return groupeFiltreCourant;
        }
    }

    private String getCurrentMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getResources().getString(
                R.string.pref_key_accueil_liste_ou_arbre_pardefaut),
                getResources().getString(
                        R.string.accueil_liste_ou_arbre_default));
    }

    private Param_Outils getParamOutils() {
        if (paramOutils == null) paramOutils = new Param_Outils(getContext());
        return paramOutils;
    }

    private App_Outils getOutils() {
        if (outils == null) outils = new App_Outils(getContext());
        return outils;
    }

    private Fiches_Outils getFichesOutils() {
        if (fichesOutils == null) fichesOutils = new Fiches_Outils(getContext());
        return fichesOutils;
    }

    private Reseau_Outils getReseauOutils() {
        if (reseauOutils == null) reseauOutils = new Reseau_Outils(getContext());
        return reseauOutils;
    }

    private Photos_Outils getPhotosOutils() {
        if (photosOutils == null) photosOutils = new Photos_Outils(getContext());
        return photosOutils;
    }

    @SuppressLint("NewApi")
    private void debugTest(StringBuilder sb) {

        for (DorisDB_metadata dorisDB_metadata : getHelper().getDorisDB_metadataDao()) {
            sb.append("Date base locale : " + dorisDB_metadata.getDateBase() + "\n");
        }

        sb.append("- - - - - -\n");
        sb.append(getApplicationContext().getFilesDir().getAbsolutePath() + "\n");
        sb.append(getApplicationContext().getFilesDir().listFiles().length + "\n");
        sb.append("- - - - - -\n");


        sb.append("prefered_disque : " +
                ImageLocation.values()[getParamOutils().getParamInt(R.string.pref_key_prefered_disque_stockage_photo,
                        ImageLocation.APP_INTERNAL.ordinal())] + "\n");

        Disque_Outils disqueOutils = new Disque_Outils(getContext());
        sb.append("Espace Interne - Espace Total : " + disqueOutils.getHumanDiskUsage(DiskEnvironmentHelper.getInternalStorage().getSize().second) + "\n");
        sb.append("Espace Interne - Place Dispo. : " + disqueOutils.getHumanDiskUsage(DiskEnvironmentHelper.getInternalStorage().getSize().first) + "\n");
        sb.append("Espace Interne - Path : " + DiskEnvironmentHelper.getInternalStorage().getMountPointFile().getAbsolutePath() + "\n");

        sb.append("Carte SD Interne - Dispo. ( *.isEmulated() ) : " + DiskEnvironmentHelper.getPrimaryExternalStorage().isEmulated() + "\n");
        if (!DiskEnvironmentHelper.getPrimaryExternalStorage().isEmulated()) {
            try {
                sb.append("Carte SD Interne - Espace Total : " + disqueOutils.getHumanDiskUsage(DiskEnvironmentHelper.getPrimaryExternalStorage().getSize().second) + "\n");
                sb.append("Carte SD Interne - Place Dispo. : " + disqueOutils.getHumanDiskUsage(DiskEnvironmentHelper.getPrimaryExternalStorage().getSize().first) + "\n");
                sb.append("Carte SD Interne - Path : " + DiskEnvironmentHelper.getPrimaryExternalStorage().getMountPointFile().getAbsolutePath() + "\n");
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

        }

        sb.append("Carte Externe - Dispo. ( *Available() ) : " + DiskEnvironmentHelper.isSecondaryExternalStorageAvailable(this) + "\n");
        if (DiskEnvironmentHelper.isSecondaryExternalStorageAvailable(this)) {
            try {
                sb.append("Carte Externe - Espace Total : " + disqueOutils.getHumanDiskUsage(DiskEnvironmentHelper.getSecondaryExternalStorage(this).getSize().second) + "\n");
                sb.append("Carte Externe - Place Dispo. : " + disqueOutils.getHumanDiskUsage(DiskEnvironmentHelper.getSecondaryExternalStorage(this).getSize().first) + "\n");
                sb.append("Carte Externe - Path : " + DiskEnvironmentHelper.getSecondaryExternalStorage(this).getMountPointFile().getAbsolutePath() + "\n");
            } catch (NoSecondaryStorageException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        sb.append("déplacement en cours : " +
                getParamOutils().getParamBoolean(R.string.pref_key_deplace_photo_encours, false) + "\n");

        sb.append("List StorageVolume:\n");
        for (StorageVolume st : StorageHelper.getStorages(true)) {
            sb.append("  " + st.toString() + "\n");
        }

//    	sb.append("test:\n");
//    	sb.append("  Environment.getExternalStoragePublicDirectory(\"DORISAndroid\")="+ Environment.getExternalStoragePublicDirectory("DORISAndroid").getAbsolutePath()+"\n");
//    	sb.append("  Environment.getExternalStoragePublicDirectory(\"\")="+ Environment.getExternalStoragePublicDirectory("").getAbsolutePath()+"\n");
//    	sb.append("test Context.getExternalFilesDirs(\"\"):\n");
//    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//	    	for( File st :this.getExternalFilesDirs("")){
//	    		sb.append("  "+ st.getAbsolutePath().toString()+"\n");
//	    	}
//    	}
//    	sb.append("test Context.getExternalFilesDirs(\"DORISAndroid\"):\n");
//    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//	    	for( File st :this.getExternalFilesDirs("DORISAndroid")){
//	    		sb.append("  "+ st.getAbsolutePath().toString()+"\n");
//	    	}
//    	}

        ContextCompat.getExternalFilesDirs(this, "");
        sb.append("test ContextCompat.getExternalFilesDirs(\"/\"):\n");
        for (File st : ContextCompat.getExternalFilesDirs(this, "")) {

            if (st != null) {

                sb.append("  " + st.getAbsolutePath().toString() + "\n");

            }
        }
    }

    //End of user code

    /**
     * refresh screen from data
     */

    public void refreshScreenData() {
        //Start of user code action when refreshing the screen Accueil_CustomViewActivity
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - Début");
  	
    	/* 
    	DVK 
    	StringBuilder sbTexte = new StringBuilder();
    	sbTexte.append(getContext().getString(R.string.accueil_customview_texte_text));
    	
    	CloseableIterator<DorisDB_metadata> itDorisDB = getHelper().getDorisDB_metadataDao().iterator();
    	while (itDorisDB.hasNext()) {
    		sbTexte.append(itDorisDB.next().getDateBase());
		}
    	((TextView) findViewById(R.id.accueil_texte)).setText(sbTexte.toString());
    	*/
        // recherche précédente
        //ImageView ivIcone = (ImageView) findViewById(R.id.accueil_recherche_precedente_icone);
        int iconeSize = ScreenTools.dp2px(this,
                getParamOutils().getParamInt(R.string.pref_key_accueil_icone_taille, Integer.parseInt(this.getString(R.string.accueil_icone_taille_defaut))));
        ImageView ivIcon = ((ImageView) findViewById(R.id.accueil_recherche_precedente_icone));
        ivIcon.setMaxHeight(iconeSize);
        ivIcon.setImageBitmap(drawIconForCurrentSearch());


        StringBuilder sbRecherchePrecedente = new StringBuilder();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        Groupe currentSpecieGroupFilter = getCurrentSpecieFilter();
        if(currentSpecieGroupFilter == null) {
            sbRecherchePrecedente.append(getString(R.string.accueil_recherche_precedente_filtreEspece_sans));
        } else {
            sbRecherchePrecedente.append(getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec) + " " + currentSpecieGroupFilter.getNomGroupe().trim());
        }

        sbRecherchePrecedente.append("\n");
        int currentFilterId = prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1);
        if (currentFilterId == -1 || currentFilterId == 0) { // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
            sbRecherchePrecedente.append(getString(R.string.accueil_recherche_precedente_filtreGeographique_sans));
        } else {
            ZoneGeographique currentZoneFilter = getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
            if (currentZoneFilter != null) {
                sbRecherchePrecedente.append(getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec) + " " + currentZoneFilter.getNom().trim());
            } else {
                sbRecherchePrecedente.append(getString(R.string.accueil_recherche_precedente_filtreGeographique_sans));
            }
        }
        sbRecherchePrecedente.append("\n");
        sbRecherchePrecedente.append(SortModesTools.getLabelMap(this).get(getCurrentMode()));

        // TODO rappeler le dernier text recherché
        TextView tvRecherchePrecedente = (TextView) findViewById(R.id.accueil_recherche_precedente_details);
        tvRecherchePrecedente.setText(sbRecherchePrecedente.toString());

        //((ImageView) findViewById(R.id.accueil_recherche_guidee_icone)).setMaxHeight(iconeZine);

        // Affichage Gestion du Mode Hors Ligne (Avancement Global (Toutes Zones) sur Accueil
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - isOnCreate : "+isOnCreate);
        ZoneGeographique zoneToutesZones = new ZoneGeographique();
        zoneToutesZones.setId(-1);
        zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
        if (isOnCreate) {
            llContainerLayout = (LinearLayout) findViewById(R.id.accueil_progress_layout);

            int imageZoneToutesZones = getFichesOutils().getZoneIconeId(zoneToutesZones.getZoneGeoKind());

            // Maj Nb Photos à télécharger
            List<ZoneGeographique> listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
            for (ZoneGeographique zoneGeo : listeZoneGeo) {
                //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - zoneGeo : "+zoneGeo.getNom());
                getPhotosOutils().setAPrecharQteParZoneGeo(zoneGeo, true);
                getPhotosOutils().setAPrecharQteParZoneGeo(zoneGeo, false);
            }

            MultiProgressBar progressBarZoneGenerale = new MultiProgressBar(this, zoneToutesZones.getNom(),  R.drawable.ic_storage, false);
            progressBarZoneGenerale.ivIcon.setMaxWidth(96);
            updateProgressBarZone(zoneToutesZones, progressBarZoneGenerale);
            progressBarZones.put(zoneToutesZones.getId(), progressBarZoneGenerale);

            final Context context = this;
            progressBarZoneGenerale.pbProgressBar_running.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Accueil_CustomViewActivity.this.isFinishing() || Accueil_CustomViewActivity.this.isActivityDestroyed())
                        return;
                    showToast(R.string.bg_notifToast_arretTelecharg);
                    DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);

                    ProgressBar pbRunningBarLayout = (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
                    pbRunningBarLayout.setVisibility(View.GONE);
                }
            });
            progressBarZoneGenerale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
                }
            });
            llContainerLayout.addView(progressBarZoneGenerale);

        } else {
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - updateProgressBarZone - Avant");
            updateProgressBarZone(zoneToutesZones, progressBarZones.get(zoneToutesZones.getId()));
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - updateProgressBarZone - Après");
        }

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Debbug
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - Debbug : "+getParamOutils().getParamBoolean(R.string.pref_key_affichage_debug, false));
        if (getParamOutils().getParamBoolean(R.string.pref_key_affichage_debug, false)) {
            StringBuilder sb = new StringBuilder();
            sb.append("- - Debbug - -\n");

            debugTest(sb);

            ((TextView) findViewById(R.id.accueil_debug_text)).setText(sb.toString());

        }
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Fin Debbug
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        isOnCreate = false;
        //End of user code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.accueil_customview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu Accueil_CustomViewActivity

        // TODO : Enlever qd développements Jeux terminés
        if (!getParamOutils().getParamBoolean(R.string.pref_key_affichage_debug, false) && !getParamOutils().getParamBoolean(R.string.pref_key_jeux_actifs, false)) {
            MenuItem menuJeux = menu.findItem(R.id.accueil_customview_action_jeux);
            menuJeux.setVisible(false);
        }

        //End of user code
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        switch (item.getItemId()) {
            case R.id.accueil_customview_action_preference:
                startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
                return true;
            //Start of user code additional menu action Accueil_CustomViewActivity
            case R.id.accueil_customview_action_telecharge_photofiches:
                TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
                if (telechargePhotosFiches_BgActivity == null || telechargePhotosFiches_BgActivity.getStatus() != Status.RUNNING) {
                    DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity =
                            (TelechargePhotosAsync_BgActivity) new TelechargePhotosAsync_BgActivity(getApplicationContext()/*, this.getHelper()*/).execute("");

                } else {
                    if (Accueil_CustomViewActivity.this.isFinishing() || Accueil_CustomViewActivity.this.isActivityDestroyed())
                        return true;
                    showToast(R.string.bg_notifToast_arretTelecharg);
                    DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);

                    ProgressBar pbRunningBarLayout = (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
                    pbRunningBarLayout.setVisibility(View.GONE);
                }

                return true;
            case R.id.accueil_customview_action_a_propos:
                AffichageMessageHTML aPropos = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
                aPropos.affichageMessageHTML(getContext().getString(R.string.a_propos_label) + getContext().getString(R.string.app_name), aPropos.aProposAff(), "file:///android_res/raw/apropos.html");
                return true;
            case R.id.accueil_customview_action_participant:
                startActivity(new Intent(this, ListeParticipantAvecFiltre_ClassListViewActivity.class));
                return true;
            case R.id.accueil_customview_action_glossaire:
                startActivity(new Intent(this, Glossaire_ClassListViewActivity.class));
                return true;
            case R.id.accueil_customview_action_bibliographie:
                startActivity(new Intent(this, ListeBibliographieAvecFiltre_ClassListViewActivity.class));
                return true;
            case R.id.accueil_customview_action_jeux:
                startActivity(new Intent(this, Jeux_CustomViewActivity.class));
                return true;
            case R.id.accueil_customview_action_aide:
                AffichageMessageHTML aide = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
                aide.affichageMessageHTML(getContext().getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html");
                return true;

            //End of user code
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
