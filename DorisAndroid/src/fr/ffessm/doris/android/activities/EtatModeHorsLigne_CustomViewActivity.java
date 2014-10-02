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
package fr.ffessm.doris.android.activities;


import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.services.GestionPhotoDiskService;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Photos_Outils.PrecharMode;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
//Start of user code additional imports EtatModeHorsLigne_CustomViewActivity
import java.util.HashMap;
import java.util.List;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.AsyncTask.Status;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.j256.ormlite.dao.CloseableIterator;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.activities.view.MultiProgressBar;

import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.SQLiteDataBaseHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.disk.DiskEnvironment;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;
//End of user code
public class EtatModeHorsLigne_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements EtatModeHorsLigne_CustomViewActivity
	implements DataChangedListener
//End of user code
{
	
	//Start of user code constants EtatModeHorsLigne_CustomViewActivity
	
	private static final String LOG_TAG = EtatModeHorsLigne_CustomViewActivity.class.getCanonicalName();
	Handler mHandler;
	
	Photos_Outils photosOutils = new Photos_Outils(getContext());
	Param_Outils paramOutils = new Param_Outils(getContext());
	Disque_Outils disqueOutils = new Disque_Outils( getContext() );
	Fiches_Outils fichesOutils = new Fiches_Outils(getContext());
	
	protected SparseArray<MultiProgressBar> progressBarZones = new SparseArray<MultiProgressBar>(); 
	protected MultiProgressBar progressBarZoneGenerale;
	protected List<MultiProgressBar> allFoldableProgressBarZones = new ArrayList<MultiProgressBar>();
	protected HashMap<String, View.OnClickListener> reusableClickListener = new HashMap<String, View.OnClickListener>();
	
	/** Disques Dispo */
	boolean carteInterneDispo =  false;
	boolean carteExterneDispo = false;
	
	/** Boutons Photos */
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
	
	/** Boutons Utilisation des Disques */
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

	/** Called when the activity is first created. */
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
            	if(EtatModeHorsLigne_CustomViewActivity.this.isFinishing()) return;
            	if(inputMessage.obj != null ){
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
    protected void onDestroy(){
    	Log.d(LOG_TAG, "onDestroy()");
    	// s'assure de désenregistrer le handler
    	DorisApplicationContext.getInstance().removeDataChangeListeners(this);

    	super.onDestroy();
    }
    
    public void dataHasChanged(String textmessage){
    	Message completeMessage = mHandler.obtainMessage(1, textmessage);
    	completeMessage.sendToTarget();
	}
    public void dataHasChanged(Object objmessage){
    	Message completeMessage = mHandler.obtainMessage(1, objmessage);
    	completeMessage.sendToTarget();
	}
    
    public Context getContext(){
		return this;
	}


    protected void createProgressBarZone(){

    	LinearLayout llContainerLayout =  (LinearLayout) findViewById(R.id.etatmodehorsligne_avancements_layout);
    	
    	// Avancement et Affichage toutes Zones
    	ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
    	
    	int imageZone = fichesOutils.getZoneIconeId(zoneToutesZones.getZoneGeoKind());
    	
    	progressBarZoneGenerale = new MultiProgressBar(this,zoneToutesZones.getNom(),imageZone,true);
    	updateProgressBarZone(zoneToutesZones, progressBarZoneGenerale);
    	progressBarZones.put(zoneToutesZones.getId(), progressBarZoneGenerale); 
    	
    	final Context context = this;
    	// Arrêt téléchargement si Click sur Icône de l'avancement (le petit rond qui tourne)
    	progressBarZoneGenerale.pbProgressBar_running.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
				DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
				
				ProgressBar pbRunningBarLayout =  (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
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
    	if(listeZoneGeo == null) listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
 			
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			imageZone = fichesOutils.getZoneIconeId(zoneGeo.getZoneGeoKind());
			
			MultiProgressBar progressBarZone = new MultiProgressBar(this, zoneGeo.getNom(), imageZone, false);
 		    updateProgressBarZone(zoneGeo, progressBarZone);

 		   /* progressBarZone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
					ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zoneGeoId);
			        ed.commit();
					startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
				}
			}); */
 		    
 		   final ZoneGeographique fZoneGeo = zoneGeo;
 		   progressBarZone.setOnClickListener(new View.OnClickListener() {
 				@Override
 				public void onClick(View v) {
 					if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setOnClickListener() - zoneGeoId : "+fZoneGeo.getId());
 					Intent intent = new Intent(EtatModeHorsLigne_CustomViewActivity.this, Preference_PreferenceViewActivity.class);
 					
 					String param;
 					
 					switch(fZoneGeo.getZoneGeoKind()){
 					case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE :
 						param = paramOutils.getStringNameParam(R.string.pref_key_mode_precharg_photo_region_france);
 					case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE :
 						param = paramOutils.getStringNameParam(R.string.pref_key_mode_precharg_photo_region_eaudouce);
 					case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE :
 						param = paramOutils.getStringNameParam(R.string.pref_key_mode_precharg_photo_region_indopac);
 					case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES :
 						param = paramOutils.getStringNameParam(R.string.pref_key_mode_precharg_photo_region_caraibes);
 					case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST :
 						param = paramOutils.getStringNameParam(R.string.pref_key_mode_precharg_photo_region_atlantno);
 					default :
 						param = null;
 					}
 					
 					if (param != null){
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
    
    protected void initOnClickListener(){
    	
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
    
    protected void createGestionPhotos(){

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
			GestionPhotoDiskService.ACT_DELETE_FOLDER+"-"+GestionPhotoDiskService.SRC_DOS_VIGNETTES) );

		btnGestionPhotosResetMedRes.setOnClickListener(reusableClickListener.get(
				GestionPhotoDiskService.ACT_DELETE_FOLDER+"-"+GestionPhotoDiskService.SRC_DOS_MEDRES) );
		
		btnGestionPhotosResetHiRes.setOnClickListener(reusableClickListener.get(
				GestionPhotoDiskService.ACT_DELETE_FOLDER+"-"+GestionPhotoDiskService.SRC_DOS_HIRES) );
		
		btnGestionPhotosResetAutres.setOnClickListener(reusableClickListener.get(
				GestionPhotoDiskService.ACT_DELETE_FOLDER+"-"+GestionPhotoDiskService.SRC_DOS_AUTRES) );
	
		btnGestionPhotosResetCache.setOnClickListener(reusableClickListener.get(
				GestionPhotoDiskService.ACT_DELETE_FOLDER+"-"+GestionPhotoDiskService.SRC_DOS_CACHE) );
		
    }
    
    protected void foldUnflodGestionPhotos() {
		if(tlFoldUnflodGestionPhotos.getVisibility() == View.GONE){
			tlFoldUnflodGestionPhotos.setVisibility(View.VISIBLE);
			imageCouranteGestionPhotos = image_maximize;
		}
		else{
			tlFoldUnflodGestionPhotos.setVisibility(View.GONE);
			imageCouranteGestionPhotos = image_minimize;
		}
		btnFoldUnflodGestionPhotos.setImageResource(imageCouranteGestionPhotos);
    }
 
    protected void createGestionDisk(){

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
				GestionPhotoDiskService.ACT_DELETE_DISK+"-"+ImageLocation.APP_INTERNAL.name()));
		btnPrimaryDiskSupp.setOnClickListener(
				reusableClickListener.get(
					GestionPhotoDiskService.ACT_DELETE_DISK+"-"+ImageLocation.PRIMARY.name()));
		btnSecondaryDiskSupp.setOnClickListener(
				reusableClickListener.get(
					GestionPhotoDiskService.ACT_DELETE_DISK+"-"+ImageLocation.SECONDARY.name()));
    }
    
    protected void foldUnflodGestionDisk(){
		if(tlFoldUnflodGestionDisk.getVisibility() == View.GONE){
			tlFoldUnflodGestionDisk.setVisibility(View.VISIBLE);
			imageCouranteGestionDisk = image_maximize;
		}
		else{
			tlFoldUnflodGestionDisk.setVisibility(View.GONE);
			imageCouranteGestionDisk = image_minimize;
		}
		btnFoldUnflodGestionDisk.setImageResource(imageCouranteGestionDisk);
    }

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen EtatModeHorsLigne_CustomViewActivity
    	
    	// mise à jour de la date de la base
    	TextView etatBase = (TextView) findViewById(R.id.etatmodehorsligne_etat_base_description_textView);
    	CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		etatBase.setText(getString(R.string.etatmodehorsligne_etat_base_description_text)+it.next().getDateBase());
    		//sb.append("Date base locale : " + it.next().getDateBase()+"\n");
		}
 
    	// Mise à jour des Barres d'avancement
    	refreshProgressBarZone();
    	
    	// Mise à jour des disques disponibles
    	refreshDiskDisponible();
    	
    	// Mise à jour des tailles des dossiers 
    	// & Mise à jour des Boutons de Gestion des Dossiers
		refreshFolderSize();
		
		// Mise à jour de l'utilisation des Disques 
		refreshUsedDisk();
		
    	// mise à jour des Boutons de Gestion des Disques
       	refreshGestionDisk();
       	
		//End of user code
	}

    public void refreshProgressBarZone() {
    	
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - update progress bar : ");
		ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
		updateProgressBarZone(zoneToutesZones, progressBarZones.get(zoneToutesZones.getId()));
		
		// Si on sait sur quelle zone on travaille alors on ne met à jour que la progress barre de cette zone
		
		//Log.d(LOG_TAG, "refreshScreenData() - zoneTraitee : "+DorisApplicationContext.getInstance().zoneTraitee);
		if (DorisApplicationContext.getInstance().zoneTraitee == null
			|| DorisApplicationContext.getInstance().zoneTraitee == ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES ) {
			if(listeZoneGeo == null) listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
			for (ZoneGeographique zoneGeo : listeZoneGeo) {
				updateProgressBarZone(zoneGeo, progressBarZones.get(zoneGeo.getId()));
			}
		} else {
			ZoneGeographique zoneGeo = new ZoneGeographique(DorisApplicationContext.getInstance().zoneTraitee);
			// Récupération Nom et Désignation dans la base
			zoneGeo = this.getHelper().getZoneGeographiqueDao().queryForId(zoneGeo.getId());
			//Log.d(LOG_TAG, "refreshScreenData() - zoneGeo : "+zoneGeo.getId()+" - "+zoneGeo.getNom());
			
			updateProgressBarZone(zoneGeo, progressBarZones.get(zoneGeo.getId()));
		}
		
	}    
      
    protected void updateProgressBarZone(ZoneGeographique inZoneGeo, MultiProgressBar progressBarZone){
    	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "updateProgressBarZone() - inZoneGeo : "+inZoneGeo.getId()+" - "+inZoneGeo.getZoneGeoKind());
	   
    	boolean affichageBarrePhotoPrinc;
    	boolean affichageBarrePhoto;
    	String summaryTexte = "";
    	int nbFichesZoneGeo = fichesOutils.getNbFichesZoneGeo(inZoneGeo.getZoneGeoKind());
    	int avancementPhotoPrinc =0;
    	int avancementPhoto =0;
	   	   
    	Photos_Outils.PrecharMode precharModeZoneGeo = photosOutils.getPrecharModeZoneGeo(inZoneGeo.getZoneGeoKind());
	   
    	if ( precharModeZoneGeo == Photos_Outils.PrecharMode.P0 ) {
	
		   affichageBarrePhotoPrinc = false;
		   affichageBarrePhoto = false;
		   
		   summaryTexte = getContext().getString(R.string.avancement_progressbar_aucune_summary);
		   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
		   
    	} else {
		   int nbPhotosPrincATelecharger = photosOutils.getAPrecharQteZoneGeo(inZoneGeo.getZoneGeoKind(), true);
		   int nbPhotosATelecharger = photosOutils.getAPrecharQteZoneGeo(inZoneGeo.getZoneGeoKind(), false);
		   int nbPhotosPrincDejaLa = photosOutils.getDejaLaQteZoneGeo(inZoneGeo.getZoneGeoKind(), true);
		   int nbPhotosDejaLa = photosOutils.getDejaLaQteZoneGeo(inZoneGeo.getZoneGeoKind(), false);
		   
		   affichageBarrePhotoPrinc = true;
		   affichageBarrePhoto = true;
		   
		   if ( nbPhotosPrincATelecharger== 0){
			   summaryTexte = getContext().getString(R.string.avancement_progressbar_jamais_summary);
			   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
	   } else {
		   
		   if ( precharModeZoneGeo == Photos_Outils.PrecharMode.P1 ) {
		   
			   summaryTexte = getContext().getString(R.string.avancement_progressbar_P1_summary);
			   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
			   summaryTexte = summaryTexte.replace("@totalPh", ""+nbPhotosPrincATelecharger ) ;
			   summaryTexte = summaryTexte.replace("@nbPh", ""+nbPhotosPrincDejaLa );
			   
			   avancementPhoto = 0;
			   avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
	
			   affichageBarrePhoto = false;
			   
		   } else {
			   summaryTexte = getContext().getString(R.string.avancement_progressbar_PX_summary1);
			   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
			   summaryTexte = summaryTexte.replace("@totalPh", ""+nbPhotosPrincATelecharger ) ;
			   summaryTexte = summaryTexte.replace("@nbPh", ""+nbPhotosPrincDejaLa );
			   
			   if (nbPhotosATelecharger == 0) {
				   summaryTexte += getContext().getString(R.string.avancement_progressbar_PX_jamais_summary2);
				   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
				   avancementPhoto = 0;
				   avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
			   } else {
				   summaryTexte += getContext().getString(R.string.avancement_progressbar_PX_summary2);
				   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
				   summaryTexte = summaryTexte.replace("@totalPh", ""+nbPhotosATelecharger ) ;
				   summaryTexte = summaryTexte.replace("@nbPh", ""+nbPhotosDejaLa );
					   
					   avancementPhoto = 100 * nbPhotosDejaLa / nbPhotosATelecharger;
					   avancementPhotoPrinc = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
				   }
			   }
		   }
	
	   }
	   // TODO calculate download in progress
	   boolean downloadInProgress = false;
	   if(inZoneGeo.getId() == -1 && DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null){
		   downloadInProgress = true;
	   }
	   
	   progressBarZone.update(summaryTexte, affichageBarrePhotoPrinc, avancementPhotoPrinc, affichageBarrePhoto, avancementPhoto, downloadInProgress);
	}
    
    private void refreshDiskDisponible() {
		// Quels emplacements sont disponibles
		carteInterneDispo = false;
		if (! disqueOutils.identifiantPartition(DiskEnvironment.getInternalStorage()).equals(
				disqueOutils.identifiantPartition(DiskEnvironment.getPrimaryExternalStorage()) ) )
			carteInterneDispo = true;
		Log.d(LOG_TAG, "createGestionDisk() - carteInterneDispo : "+carteInterneDispo);
		
		carteExterneDispo = DiskEnvironment.isSecondaryExternalStorageAvailable();
		Log.d(LOG_TAG, "createGestionDisk() - carteExterneDispo : "+carteExterneDispo);
    }

    /**
     * Affiche la taille des dossiers de l'Espace de Stockage Sélectionné
     * & Gestion des boutons de vidages des dossiers de l'Espace de Stockage Sélectionné
     */
    protected void refreshFolderSize(){
    	// if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshFolderSize() - Début");
    	
		StringBuilder etatDiskStringBuilder = new StringBuilder();
		Boolean auMoins1DossierNonVide = false;
		
    	TextView gestionPhotosTextView = (TextView) findViewById(R.id.etatmodehorsligne_gestion_photos_description_textView);

		etatDiskStringBuilder.append( "Nb Images et Taille Dossiers\u00A0:" ); 
		
		int sizeFolder = photosOutils.getImageCountInFolder(ImageType.VIGNETTE);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_vignettes) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPhotoDiskUsage(ImageType.VIGNETTE) ) );
			
			btnGestionPhotosResetVig.setEnabled(true);
		} else {
			btnGestionPhotosResetVig.setEnabled(false);
		}
		// Si Travail en cours => Bouton Disabled
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnGestionPhotosResetVig.setEnabled(false);
		
		
		
		sizeFolder = photosOutils.getImageCountInFolder(ImageType.MED_RES);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_med_res) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPhotoDiskUsage(ImageType.MED_RES) ) );
			
			btnGestionPhotosResetMedRes.setEnabled(true);
		} else {
			btnGestionPhotosResetMedRes.setEnabled(false);
		}
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnGestionPhotosResetMedRes.setEnabled(false);
		
		sizeFolder = photosOutils.getImageCountInFolder(ImageType.HI_RES);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_hi_res) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPhotoDiskUsage(ImageType.HI_RES) ) );
			
			btnGestionPhotosResetHiRes.setEnabled(true);
		} else {
			btnGestionPhotosResetHiRes.setEnabled(false);
		}
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnGestionPhotosResetHiRes.setEnabled(false);
		
		sizeFolder = photosOutils.getImageCountInFolder(ImageType.PORTRAITS)
				+ photosOutils.getImageCountInFolder(ImageType.ILLUSTRATION_BIBLIO)
				+ photosOutils.getImageCountInFolder(ImageType.ILLUSTRATION_DEFINITION);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_autres) );
			etatDiskStringBuilder.append(
					disqueOutils.getHumanDiskUsage(
						photosOutils.getPhotoDiskUsage(ImageType.PORTRAITS)
						+ photosOutils.getPhotoDiskUsage(ImageType.ILLUSTRATION_BIBLIO)
						+ photosOutils.getPhotoDiskUsage(ImageType.ILLUSTRATION_DEFINITION)
					) );
			
			btnGestionPhotosResetAutres.setEnabled(true);
		} else {
			btnGestionPhotosResetAutres.setEnabled(false);
		}
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnGestionPhotosResetAutres.setEnabled(false);
		
		
		sizeFolder = photosOutils.getImageCountInCache();
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_cache) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPicasoCacheUsage() ) );
			
			btnGestionPhotosResetCache.setEnabled(true);
		} else {
			btnGestionPhotosResetCache.setEnabled(false);
		}
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnGestionPhotosResetCache.setEnabled(false);
		
		
		if (!auMoins1DossierNonVide){
			etatDiskStringBuilder.append( "\n\t" );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_vide) );
		}
		
		gestionPhotosTextView.setText(etatDiskStringBuilder.toString());
		
		// Si encours de traitement on affiche la ProgressBar sinon on la cache
		ProgressBar deplacementEnCoursProgressBar = (ProgressBar) findViewById(R.id.etatmodehorsligne_gestion_photos_buttons_progressBar);
		if (DorisApplicationContext.getInstance().isMovingPhotos) {
			deplacementEnCoursProgressBar.setVisibility(View.VISIBLE);
		}
		else deplacementEnCoursProgressBar.setVisibility(View.GONE);
		
		
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshFolderSize() - Fin");
    }
        
    /**
     * affiche les info d'utilisation du disque
     * Note, cela a été séparé car normalement je voulais le mettre dans un asynctask, mais on ne peut en exécuter
     * qu'une à la fois, ce qui est bloqué si un téléchargement est en cours 
     * -> prévoir de migrer ces asynctask dans des Services
     * @param internalUsedSize
     * @param primaryUsedSize
     */
    private void refreshUsedDisk() {
		// Mise à jour de la place utilisée sur chaque disque
		if(carteExterneDispo){
			refreshUsedDisk(photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL), photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY), photosOutils.getPhotosDiskUsage(ImageLocation.SECONDARY));
		}
		else{
			refreshUsedDisk(photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL), photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY), 0);
		}   	
    }
    protected void refreshUsedDisk(long internalUsedSize, long primaryUsedSize, long secondaryUsedSize){
    	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Début");
    	
		StringBuilder etatDiskStringBuilder = new StringBuilder();
		
    	/*
    	 * Espace de Stockage Sélectionné
    	 */
		
		etatDiskStringBuilder.append( "Espace de Stockage utilisé :\n\t" ); 
		switch (photosOutils.getPreferedLocation()){
		case APP_INTERNAL:
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_libelle) );
			break;
		case PRIMARY:
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_libelle) );
			break;
		case SECONDARY:
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_libelle) );
			break;
		}
		etatDiskStringBuilder.append( "\n\n" ); 
		
    	/*
    	 * Utilisation des Disques
    	 */
		etatDiskStringBuilder.append( "Utilisation des Disques\n" ); 
		etatDiskStringBuilder.append("\t(Espace utilisé / disponible / total)\n");
		
		// Mémoire interne
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Mémoire interne");
		
		etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_libelle)+" :\n\t");
		etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(internalUsedSize)+" / ");
		etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(DiskEnvironment.getInternalStorage().getSize().first)+" / ");
		etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(DiskEnvironment.getInternalStorage().getSize().second)+"\n");
		//etatDiskStringBuilder.append(DiskEnvironment.getInternalStorage().getFile().getAbsolutePath()+"\n");
		//etatDiskStringBuilder.append("Donnée application="+this.getDir(Photos_Outils.MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE)+"\n");
		//etatDiskStringBuilder.append("'Hash' pour vérifier si Carte SD Interne != Stockage interne : "
		//		+DiskEnvironment.getInternalStorage().getSize().first+"-"+DiskEnvironment.getInternalStorage().getSize().second+"\n");
		
		// Disque primaire (Carte SD Interne dans les paramètres)
		if ( !disqueOutils.identifiantPartition(DiskEnvironment.getInternalStorage()).equals(
				disqueOutils.identifiantPartition(DiskEnvironment.getPrimaryExternalStorage()) )
				) {
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Disque primaire (Carte SD Interne)");
			
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_libelle)+" :\n\t");
			etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(primaryUsedSize)+" / ");
			etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(DiskEnvironment.getPrimaryExternalStorage().getSize().first)+" / ");
			etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(DiskEnvironment.getPrimaryExternalStorage().getSize().second)+"\n");
			//etatDiskStringBuilder.append(DiskEnvironment.getPrimaryExternalStorage().getFile().getAbsolutePath()+"\n");
			//etatDiskStringBuilder.append("'Hash' pour vérifier si Carte SD Interne != Stockage interne : "
			//		+DiskEnvironment.getPrimaryExternalStorage().getSize().first+"-"+DiskEnvironment.getPrimaryExternalStorage().getSize().second+"\n");
		}
		
		// Carte SD externe (nommée amovible)
		if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Carte SD externe (nommée amovible)");
			
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_libelle)+" :\n\t");
			etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(secondaryUsedSize)+" / ");
			try {
				etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(DiskEnvironment.getSecondaryExternalStorage().getSize().first)+" / ");
				etatDiskStringBuilder.append(disqueOutils.getHumanDiskUsage(DiskEnvironment.getSecondaryExternalStorage().getSize().second)+"\n");
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
		if (DorisApplicationContext.getInstance().isMovingPhotos) {
			deplacementEnCoursProgressBar.setVisibility(View.VISIBLE);
		}
		else deplacementEnCoursProgressBar.setVisibility(View.GONE);
		
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Fin");
    }
	//End of user code
   
    public void refreshGestionDisk() {
    	
    	boolean deplaceEnCours = paramOutils.getParamBoolean(R.string.pref_key_deplace_photo_encours, false);
    	
		ImageLocation imageLocationCourante = photosOutils.getPreferedLocation();		
		ImageLocation imageLocationPrecedente = photosOutils.getLocationPrecedente();	

    	
    	// -- Mémoire Interne -- //
		// Si ni la carte Interne, ni la Carte Externe ne sont présentes aucun mouvement n'est possible
		if ( ( carteInterneDispo && photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY) != 0 )
			|| ( carteExterneDispo && photosOutils.getPhotosDiskUsage(ImageLocation.SECONDARY) != 0 ) ) {
			
			btnInternalDiskDepl.setEnabled(true);
			btnInternalDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_internal_depl_btn_text_selected));
		
			// Si un déplacement vers la Mémoire Interne a été arrêté avant sa fin et qu'aucun mouvement n'a repris
			// On propose la reprise
			if ( ( ! DorisApplicationContext.getInstance().isMovingPhotos )
				&& ( deplaceEnCours )
				&& ( imageLocationCourante == ImageLocation.APP_INTERNAL )) {
				btnInternalDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_internal_repr_btn_text_selected));
			}
				
		} else {
			
			btnInternalDiskDepl.setEnabled(false);
			btnInternalDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_internal_btn_text_not_available));

		}
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnInternalDiskDepl.setEnabled(false);
		
		if( photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL) != 0 ) {
			btnInternalDiskSupp.setEnabled(true);
			btnInternalDiskSupp.setText(getString(R.string.etatmodehorsligne_diskselection_internal_supp_btn_text_selected));
		} else {
			btnInternalDiskSupp.setEnabled(false);
			btnInternalDiskSupp.setText(getString(R.string.etatmodehorsligne_gestion_disk_supp_btn_vide_text));
		}
		if (DorisApplicationContext.getInstance().isMovingPhotos) btnInternalDiskSupp.setEnabled(false);	

		
		// -- Carte Mémoire Interne (Non Amovible, en fait une partition de la Mémoire Interne destinée à stocker les données des Applications) -- //
		// Affichage ou non de la Carte Interne
		if ( carteInterneDispo ) {
			TableRow trGestionDiskPrimary = (TableRow) findViewById(R.id.etatmodehorsligne_gestion_disk_primary_row);
			trGestionDiskPrimary.setVisibility(View.VISIBLE);
			
			if( ( photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL) != 0 )
					|| ( carteExterneDispo && photosOutils.getPhotosDiskUsage(ImageLocation.SECONDARY) != 0 ) ){
				btnPrimaryDiskDepl.setEnabled(true);
				btnPrimaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_primary_depl_btn_text_selected));

				// Si un déplacement vers le Carte Mémoire Interne a été arrêté avant sa fin et qu'aucun mouvement n'a repris
				// On propose la reprise
				if ( ( ! DorisApplicationContext.getInstance().isMovingPhotos )
					&& ( deplaceEnCours )
					&& ( imageLocationCourante == ImageLocation.PRIMARY )) {
					btnPrimaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_primary_repr_btn_text_selected));
				}
				
			} else {
				btnPrimaryDiskDepl.setEnabled(false);
				btnPrimaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_primary_btn_text_not_available));
			}
			if (DorisApplicationContext.getInstance().isMovingPhotos) btnPrimaryDiskDepl.setEnabled(false);
			
			if( photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY) != 0 ) {
				btnPrimaryDiskSupp.setEnabled(true);
				btnPrimaryDiskSupp.setText(getString(R.string.etatmodehorsligne_diskselection_primary_supp_btn_text_selected));
			} else {
				btnPrimaryDiskSupp.setEnabled(false);
				btnPrimaryDiskSupp.setText(getString(R.string.etatmodehorsligne_gestion_disk_supp_btn_vide_text));
			}
			if (DorisApplicationContext.getInstance().isMovingPhotos) btnPrimaryDiskSupp.setEnabled(false);
			
		} else {
			TableRow trGestionDiskPrimary = (TableRow) findViewById(R.id.etatmodehorsligne_gestion_disk_primary_row);
			trGestionDiskPrimary.setVisibility(View.GONE);
		}
		
		
		// -- Carte Mémoire Externe (Amovible) -- //
		// Désactivation des boutons de la carte externe qd elle n'est pas disponible
		if ( DiskEnvironment.isSecondaryExternalStorageAvailable() ){
			
			btnSecondaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_depl_btn_text_selected));
			
			if ( ( photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL) != 0 )
					|| ( carteInterneDispo && photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY) != 0 ) ){
					btnSecondaryDiskDepl.setEnabled(true);
					
					// Si un déplacement vers le Carte Mémoire Externe a été arrêté avant sa fin et qu'aucun mouvement n'a repris
					// On propose la reprise
					if ( ( ! DorisApplicationContext.getInstance().isMovingPhotos )
						&& ( deplaceEnCours )
						&& ( imageLocationCourante == ImageLocation.SECONDARY )) {
						btnSecondaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_repr_btn_text_selected));
					}
					
			} else {
				btnSecondaryDiskDepl.setEnabled(false);
			}
			if (DorisApplicationContext.getInstance().isMovingPhotos) btnSecondaryDiskDepl.setEnabled(false);
			
			btnSecondaryDiskSupp.setVisibility(View.VISIBLE);
			if ( photosOutils.getPhotosDiskUsage(ImageLocation.SECONDARY) != 0 ) {
				btnSecondaryDiskSupp.setEnabled(true);
				btnSecondaryDiskSupp.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_supp_btn_text_selected));
			} else {
				btnSecondaryDiskSupp.setEnabled(false);
				btnSecondaryDiskSupp.setText(getString(R.string.etatmodehorsligne_gestion_disk_supp_btn_vide_text));
			}
			if (DorisApplicationContext.getInstance().isMovingPhotos) btnSecondaryDiskSupp.setEnabled(false);
			
			
		} else {
			// Si pas de carte amovible : message disant qu'il n'y en a pas et on masque bouton supprimant toutes les images
			btnSecondaryDiskDepl.setEnabled(false);
			btnSecondaryDiskDepl.setText(getString(R.string.etatmodehorsligne_diskselection_secondary_btn_text_not_available));
			
			btnSecondaryDiskSupp.setVisibility(View.INVISIBLE);
		}
		

		
		switch (imageLocationCourante){
		case APP_INTERNAL:
			if (deplaceEnCours) {
				btnInternalDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+imageLocationPrecedente.name()+"2"+ImageLocation.APP_INTERNAL.name()));
			}
			btnPrimaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+ImageLocation.APP_INTERNAL.name()+"2"+ ImageLocation.PRIMARY.name()));
			btnSecondaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+ImageLocation.APP_INTERNAL.name()+"2"+ ImageLocation.SECONDARY.name()));
			break;
		case PRIMARY:
			btnInternalDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+ImageLocation.PRIMARY.name()+"2"+ ImageLocation.APP_INTERNAL.name()));
			if (deplaceEnCours) {
				btnPrimaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+imageLocationPrecedente.name()+"2"+ImageLocation.PRIMARY.name()));
			}
			btnSecondaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+ImageLocation.PRIMARY.name()+"2"+ ImageLocation.SECONDARY.name()));
			break;
		case SECONDARY:
			btnInternalDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+ImageLocation.SECONDARY.name()+"2"+ ImageLocation.APP_INTERNAL.name()));
			btnPrimaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+ImageLocation.SECONDARY.name()+"2"+ ImageLocation.PRIMARY.name()));
			if (deplaceEnCours) {
				btnSecondaryDiskDepl.setOnClickListener(reusableClickListener.get(GestionPhotoDiskService.ACT_MOVE+"-"+imageLocationPrecedente.name()+"2"+ImageLocation.SECONDARY.name()));
			}
			break;
		}

		
		/*
    	paramOutils.getParamBoolean(R.string.pref_key_deplace_photo_encours, false)
		 */

	}
    
    private void addReusableClickListener(final String action, final String source, final String target){
    	
    	if ( action.equals(GestionPhotoDiskService.ACT_MOVE) ) {
	    	reusableClickListener.put(action+"-"+source+"2"+target, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Déplace les fichiers de la source vers la cible

					AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(getContext());
					
					
					if ( target.equals(ImageLocation.APP_INTERNAL.name()) ) {
						alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_confirmation));
					} else if ( target.equals(ImageLocation.PRIMARY.name()) ) {
						alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_confirmation));
					} else if ( target.equals(ImageLocation.SECONDARY.name()) ) {
						alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_confirmation));
					}
			       	alertDialogbD.setCancelable(true);
			       	
			       	// On déplace le disque si validé
			       	alertDialogbD.setPositiveButton(getContext().getString(R.string.btn_yes),
		       			new DialogInterface.OnClickListener() {
		                	public void onClick(DialogInterface dialog, int id) {

								// utilise le déplacement sous forme de service
								// use this to start and trigger a service
								Intent i= new Intent(getApplicationContext(), GestionPhotoDiskService.class);
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
    	}
    	else if ( action.equals(GestionPhotoDiskService.ACT_DELETE_DISK) ) {
    		
    		Log.d(LOG_TAG, "Création reusableClickListener - "+action+"-"+source);
    		
    		reusableClickListener.put(action+"-"+source, new View.OnClickListener() {
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

		    					Intent i= new Intent(getApplicationContext(), GestionPhotoDiskService.class);

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

    	}
    	else if ( action.equals(GestionPhotoDiskService.ACT_DELETE_FOLDER) ) {

    		Log.d(LOG_TAG, "Création reusableClickListener - "+action+"-"+source);
    		
    		reusableClickListener.put(action+"-"+source, new View.OnClickListener() {
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

			    					Intent i= new Intent(getApplicationContext(), GestionPhotoDiskService.class);

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
				if(telechargePhotosFiches_BgActivity == null || telechargePhotosFiches_BgActivity.getStatus() != Status.RUNNING) {
					DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = 
						(TelechargePhotosAsync_BgActivity) new TelechargePhotosAsync_BgActivity(getApplicationContext()/*, this.getHelper()*/).execute("");
	
				} else {
					Toast.makeText(this, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
					DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
					
					ProgressBar pbRunningBarLayout =  (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
					pbRunningBarLayout.setVisibility(View.GONE);
				}
				
	            return true;
			case R.id.etatmodehorsligne_customview_action_a_propos:
				AffichageMessageHTML aPropos = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
				aPropos.affichageMessageHTML(getContext().getString(R.string.a_propos_label)+getContext().getString(R.string.app_name), aPropos.aProposAff(),	"file:///android_res/raw/apropos.html");		
				return true;
	        case R.id.etatmodehorsligne_customview_action_aide:
	        	AffichageMessageHTML aide = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
				aide.affichageMessageHTML(getContext().getString(R.string.aide_label), "", "file:///android_res/raw/aide.html#ParamHorsLigne");
				return true;
		//End of user code
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
	        	TaskStackBuilder.create(this)
	                // Add all of this activity's parents to the back stack
	                .addNextIntentWithParentStack(getSupportParentActivityIntent())
	                // Navigate up to the closest parent
	                .startActivities();
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
	
	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
