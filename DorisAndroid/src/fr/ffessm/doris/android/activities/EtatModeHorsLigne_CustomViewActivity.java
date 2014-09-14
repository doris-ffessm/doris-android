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
import fr.ffessm.doris.android.services.MovePhotoDiskService;
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
	
	/** bouton fold unflod Photos disponibles */
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
	
	/** bouton fold unflod Utilisation des Disques */
	private LinearLayout llGestionDisk;
	private ImageButton btnFoldUnflodGestionDisk;
	private TableLayout tlFoldUnflodGestionDisk;
	private int imageCouranteGestionDisk;
	
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
		
    	// Suppression des Images
		
		//btnGestionPhotosResetVig.setOnClickListener(reusableClickListener.get(
		//	MovePhotoDiskService.ACT_DELETE_FOLDER+"-"+MovePhotoDiskService.SRC_DOS_VIGNETTES) );
		btnGestionPhotosResetVig.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				showToast("Test Bouton Delete Vignettes");
			}
		});
				
		btnGestionPhotosResetCache.setOnClickListener(reusableClickListener.get(
				MovePhotoDiskService.ACT_DELETE_FOLDER+"-"+MovePhotoDiskService.SRC_DOS_CACHE) );
		
		
		/*
		btnGestionPhotosResetVig.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				gestionPhotosResetDossier("Vignettes");
			}
		});
		btnGestionPhotosResetMedRes.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				gestionPhotosResetDossier("MedRes");
			}
		});
		btnGestionPhotosResetHiRes.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				gestionPhotosResetDossier("HiRes");
			}
		});
		btnGestionPhotosResetAutres.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				gestionPhotosResetDossier("Autres");
			}
		});
		btnGestionPhotosResetCache.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				//gestionPhotosResetDossier("Cache");
			}
		});
		*/
		
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

        //TODO : Temporaire tant que la fonction n'est pas tout à fait au point
        // Permet de n'activer le choix de l'emplacement des images que pour les personnes qui auront compris
        // que c'est en dev.
        if (paramOutils.getParamBoolean(R.string.pref_key_deplacer_images_debug, false)){
        	llGestionDisk.setVisibility(View.VISIBLE);
        	tlFoldUnflodGestionDisk.setVisibility(View.VISIBLE);
        	
        	btnFoldUnflodGestionDisk = (ImageButton) findViewById(R.id.etatmodehorsligne_gestion_disk_fold_unflod_section_imageButton);
         	
        	initOnClickListener();
        
    		imageCouranteGestionDisk = image_maximize;
    		btnFoldUnflodGestionDisk.setImageResource(imageCouranteGestionDisk);

        	// Masquage des Boutons de suppression des photos
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
        
    		if ( disqueOutils.identifiantPartition(DiskEnvironment.getInternalStorage()).equals(
    				disqueOutils.identifiantPartition(DiskEnvironment.getPrimaryExternalStorage()) )
    				) {
    			TableRow trGestionDiskPrimary = (TableRow) findViewById(R.id.etatmodehorsligne_gestion_disk_primary_row);
    			trGestionDiskPrimary.setVisibility(View.GONE);
    		}
    		
    		if( !DiskEnvironment.isSecondaryExternalStorageAvailable() ){
    			TableRow trGestionDiskSecondary = (TableRow) findViewById(R.id.etatmodehorsligne_gestion_disk_secondary_row);
    			trGestionDiskSecondary.setVisibility(View.GONE);
    		}
    		
        } else {
        	llGestionDisk.setVisibility(View.GONE);
        	tlFoldUnflodGestionDisk.setVisibility(View.GONE);
        }
    	
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
    
    protected void initOnClickListener(){
    	
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_FOLDER, MovePhotoDiskService.SRC_DOS_VIGNETTES, null);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_FOLDER, MovePhotoDiskService.SRC_DOS_MEDRES, null);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_FOLDER, MovePhotoDiskService.SRC_DOS_HIRES, null);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_FOLDER, MovePhotoDiskService.SRC_DOS_AUTRES, null);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_FOLDER, MovePhotoDiskService.SRC_DOS_CACHE, null);
    	
    	addReusableClickListener(MovePhotoDiskService.ACT_MOVE, MovePhotoDiskService.SRC_INTERNAL, MovePhotoDiskService.SRC_PRIMARY);
    	addReusableClickListener(MovePhotoDiskService.ACT_MOVE, MovePhotoDiskService.SRC_INTERNAL, MovePhotoDiskService.SRC_SECONDARY);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_DISK, MovePhotoDiskService.SRC_INTERNAL, null);
    	
    	addReusableClickListener(MovePhotoDiskService.ACT_MOVE, MovePhotoDiskService.SRC_PRIMARY, MovePhotoDiskService.SRC_INTERNAL);
    	addReusableClickListener(MovePhotoDiskService.ACT_MOVE, MovePhotoDiskService.SRC_PRIMARY, MovePhotoDiskService.SRC_SECONDARY);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_DISK, MovePhotoDiskService.SRC_PRIMARY, null);
    	
    	addReusableClickListener(MovePhotoDiskService.ACT_MOVE, MovePhotoDiskService.SRC_SECONDARY, MovePhotoDiskService.SRC_INTERNAL);
    	addReusableClickListener(MovePhotoDiskService.ACT_MOVE, MovePhotoDiskService.SRC_SECONDARY, MovePhotoDiskService.SRC_PRIMARY);
    	addReusableClickListener(MovePhotoDiskService.ACT_DELETE_DISK, MovePhotoDiskService.SRC_SECONDARY, null);
    }
    
    private void addReusableClickListener(final String action, final String source, final String target){
    	
    	if ( action.equals(MovePhotoDiskService.ACT_MOVE) ) {
	    	reusableClickListener.put(action+"-"+source+"2"+target, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Déplace les fichiers de la source vers la cible

					// utilise le déplacement sous forme de service
					// use this to start and trigger a service
					Intent i= new Intent(getApplicationContext(), MovePhotoDiskService.class);
					// add data to the intent
					i.putExtra(MovePhotoDiskService.INTENT_ACTION, MovePhotoDiskService.ACT_MOVE);
					i.putExtra(MovePhotoDiskService.INTENT_SOURCE, source);
					i.putExtra(MovePhotoDiskService.INTENT_TARGET, target);
					
					getApplicationContext().startService(i);
					
					DorisApplicationContext.getInstance().notifyDataHasChanged(null);
				}
			});
    	}
    	else if ( action.equals(MovePhotoDiskService.ACT_DELETE_DISK) ) {
    		reusableClickListener.put(action+"-"+source, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// utilise le déplacement sous forme de service
					// use this to start and trigger a service
					Intent i= new Intent(getApplicationContext(), MovePhotoDiskService.class);
					// add data to the intent
					i.putExtra(MovePhotoDiskService.INTENT_ACTION, MovePhotoDiskService.ACT_DELETE_DISK);
					i.putExtra(MovePhotoDiskService.INTENT_SOURCE, source);
					i.putExtra(MovePhotoDiskService.INTENT_TARGET, "");
					getApplicationContext().startService(i);
					
					DorisApplicationContext.getInstance().notifyDataHasChanged(null);
				}
			});
    	}
    	else if ( action.equals(MovePhotoDiskService.ACT_DELETE_FOLDER) ) {
    		showToast("Création reusableClickListener - "+action+"-"+source);
    		
    		reusableClickListener.put(action+"-"+source, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					showToast("Utilisation reusableClickListener - "+action+"-"+source);
					
					/*
					AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(getContext());
			       	alertDialogbD.setMessage(getContext().getString(R.string.etatmodehorsligne_gestion_reset_confirmation));
			       	alertDialogbD.setCancelable(true);
			       	

			       	
			       	// On vide le dossier si validé
			       	alertDialogbD.setPositiveButton(getContext().getString(R.string.btn_yes),
			       			new DialogInterface.OnClickListener() {
			                	public void onClick(DialogInterface dialog, int id) {

			    					// utilise la suppression sous forme de service

			    					Intent i= new Intent(getApplicationContext(), MovePhotoDiskService.class);

			    					i.putExtra(MovePhotoDiskService.INTENT_ACTION, MovePhotoDiskService.ACT_DELETE_FOLDER);
			    					i.putExtra(MovePhotoDiskService.INTENT_SOURCE, source);
			    					i.putExtra(MovePhotoDiskService.INTENT_TARGET, "");
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
					*/
	
				}
			});
    		
    	
    	}
    }
    
    private AsyncComputeLongRefreshScreenData lastAsyncComputeLongRefreshScreenData = null;
    
    /**
     * Classe utilisée pour rafraîchir l'ihm, mais qui prend un peu de temps
     * et ne peut pas être mise dans refreshScreenData sinon on pénalise le reste
     */
    private class AsyncComputeLongRefreshScreenData extends AsyncTask<Void, Void, Void> {

    	long internalUsedSize =  0;
    	long primaryUsedSize =  0;
    	long secondaryUsedSize = 0;
		public boolean needRestart = false;
		
		public AsyncComputeLongRefreshScreenData() {
			super();
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "AsyncComputeLongRefreshScreenData() - doInBackground()");
			
			// baisse la priorité pour s'assurer une meilleure réactivité
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
						
			// Mémoire interne
			if (BuildConfig.DEBUG) Log.d(LOG_TAG, "AsyncComputeLongRefreshScreenData() - doInBackground() - Mémoire interne");
			internalUsedSize =  photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL);
			
			// Disque primaire (Carte SD Interne dans les paramètres)
			if ( !disqueOutils.identifiantPartition(DiskEnvironment.getInternalStorage()).equals(
					disqueOutils.identifiantPartition(DiskEnvironment.getPrimaryExternalStorage()) )
				) {
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "AsyncComputeLongRefreshScreenData() - doInBackground() - Disque primaire (Carte SD Interne)");
				primaryUsedSize =  photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY);
			}
			
			// Carte SD externe (nommée amovible)
			if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
				if (BuildConfig.DEBUG) Log.d(LOG_TAG, "AsyncComputeLongRefreshScreenData() - doInBackground() - Carte SD externe (nommée amovible)");
				secondaryUsedSize = photosOutils.getPhotosDiskUsage(ImageLocation.SECONDARY);
			}
			
			if(needRestart){
				// si besoin de recommencer, alors fait une mini pause avant redéclencher
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			refreshFolderSize();
			
			refreshUsedDisk(internalUsedSize, primaryUsedSize, secondaryUsedSize);
			
			if(needRestart)	{
				// une notification a été reçue pendant qu'on travaillait, faut la relancer pour recommencer
				lastAsyncComputeLongRefreshScreenData = (AsyncComputeLongRefreshScreenData) new AsyncComputeLongRefreshScreenData().execute();
			}
		}
	}
    
    /**
     * affiche la taille des dossiers de l'Espace de Stockage Sélectionné
     */
    protected void refreshFolderSize(){
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshFolderSize() - Début");
    	
		StringBuilder etatDiskStringBuilder = new StringBuilder();
		Boolean auMoins1DossierNonVide = false;
		
    	TextView gestionPhotosTextView = (TextView) findViewById(R.id.etatmodehorsligne_gestion_photos_description_textView);

		etatDiskStringBuilder.append( "Nb Images et Taille Dossiers\u00A0:" ); 
		
		int sizeFolder = photosOutils.getImageCountInFolder(ImageType.VIGNETTE);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_vignettes) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPhotoDiskUsage(ImageType.VIGNETTE) ) );
			
			btnGestionPhotosResetVig.setEnabled(true);
		} else {
			btnGestionPhotosResetVig.setEnabled(false);
		}
		
		sizeFolder = photosOutils.getImageCountInFolder(ImageType.MED_RES);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_med_res) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPhotoDiskUsage(ImageType.MED_RES) ) );
			
			btnGestionPhotosResetMedRes.setEnabled(true);
		} else {
			btnGestionPhotosResetMedRes.setEnabled(false);
		}
		
		sizeFolder = photosOutils.getImageCountInFolder(ImageType.HI_RES);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_hi_res) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getPhotoDiskUsage(ImageType.HI_RES) ) );
			
			btnGestionPhotosResetHiRes.setEnabled(true);
		} else {
			btnGestionPhotosResetHiRes.setEnabled(false);
		}
		
		sizeFolder = photosOutils.getImageCountInFolder(ImageType.PORTRAITS)
				+ photosOutils.getImageCountInFolder(ImageType.ILLUSTRATION_BIBLIO)
				+ photosOutils.getImageCountInFolder(ImageType.ILLUSTRATION_DEFINITION);
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t\t" );
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
		
		sizeFolder = photosOutils.getImageCountInCache();
		if ( sizeFolder !=0 ) {
			auMoins1DossierNonVide = true;
			
			etatDiskStringBuilder.append( "\n\t\t" );
			etatDiskStringBuilder.append( sizeFolder );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_cache) );
			etatDiskStringBuilder.append( disqueOutils.getHumanDiskUsage( photosOutils.getCacheUsage() ) );
			
			btnGestionPhotosResetCache.setEnabled(true);
		} else {
			btnGestionPhotosResetCache.setEnabled(false);
		}
		
		if (!auMoins1DossierNonVide){
			etatDiskStringBuilder.append( "\n\t\t" );
			etatDiskStringBuilder.append( getContext().getString(R.string.etatmodehorsligne_foldersize_vide) );
		}
		
		gestionPhotosTextView.setText(etatDiskStringBuilder.toString());
		
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshFolderSize() - Fin");
    }
    /**
     * affiche les info d'utilisation du disque
     * Note, cela a été séparé car normalement je voulais le mettre dans un asynctask, mais on ne peut en exécuter
     * qu'une à la fois, ce qui est bloqué si un téléchargement est en cours 
     * -> prévoir de migrer ces asynctask dans des Services
     * @param internalUsedSize
     * @param primaryUsedSize
     */
    protected void refreshUsedDisk(long internalUsedSize, long primaryUsedSize, long secondaryUsedSize){
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Début");
    	
		StringBuilder etatDiskStringBuilder = new StringBuilder();
		
    	/*
    	 * Espace de Stockage Sélectionné
    	 */
		
		etatDiskStringBuilder.append( "Espace de Stockage utilisé :\n\t\t" ); 
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
		etatDiskStringBuilder.append("\t\t\t\t\t\t\t\t(Espace utilisé / disponible / total)\n");
		
		// Mémoire interne
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Mémoire interne");
		
		etatDiskStringBuilder.append( "\t\t" );
		etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_internal_libelle)+" : ");
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
			
			etatDiskStringBuilder.append( "\t\t" );
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_primary_libelle)+" : ");
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
			
			etatDiskStringBuilder.append( "\t\t" );
			etatDiskStringBuilder.append(getContext().getString(R.string.etatmodehorsligne_diskselection_secondary_libelle)+" : ");
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
		
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshUsedDisk() - Fin");
    }
	//End of user code

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
    	
    	
    	// mise à jour des progress bar
    	refreshProgressBarZone();

    	
    	// mise à jour de la Gestion des Disques
        // TODO : Temporaire tant que la fonction n'est pas tout à fait au point
        // Permet de n'activer le choix de l'emplacement des images que pour les personnes qui auront compris
        // que c'est en dev.
        if (paramOutils.getParamBoolean(R.string.pref_key_deplacer_images_debug, false)){
        	refreshGestionDisk();
        }
        
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
    
    public void refreshGestionDisk() {
		
		// workaround temporaire , on ne peut pas lancer simultanément 2 asynctask (elles attendent la fin de la précédentes)
		// en attendant que les autres taches soient transformées en un service, on fait le calcul dans le thread de l'UI quand même
		if( DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null ||
				DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity != null ||
				DorisApplicationContext.getInstance().verifieMAJFiche_BgActivity != null){
			
				Log.d(LOG_TAG, "refreshScreenData() - isSecondaryExternalStorageAvailable() : "+DiskEnvironment.isSecondaryExternalStorageAvailable());
			
			refreshFolderSize();
			
			if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
				refreshUsedDisk(photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL), photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY), photosOutils.getPhotosDiskUsage(ImageLocation.SECONDARY));
			}
			else{
				refreshUsedDisk(photosOutils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL), photosOutils.getPhotosDiskUsage(ImageLocation.PRIMARY), 0);
			}
		}
		
		// déclenche le calcul de l'espace disque utilisé
		if(lastAsyncComputeLongRefreshScreenData != null){
			if( lastAsyncComputeLongRefreshScreenData.getStatus() == Status.FINISHED){
				// ne relance pas si déjà en cours
				lastAsyncComputeLongRefreshScreenData = (AsyncComputeLongRefreshScreenData) new AsyncComputeLongRefreshScreenData().execute();
			}
			else{
				lastAsyncComputeLongRefreshScreenData.needRestart = true;
			}
		} else lastAsyncComputeLongRefreshScreenData = (AsyncComputeLongRefreshScreenData) new AsyncComputeLongRefreshScreenData().execute();
		
		// Affiche les boutons suivant les configurations possibles
		
		// disque courant
		ImageLocation currentImageLocation = photosOutils.getPreferedLocation();		
		// vérifie qu'il n'y a pas un déplacement en cours
		boolean deplacementEnCours = DorisApplicationContext.getInstance().isMovingPhotos;
		
		ProgressBar deplacementEnCoursProgressBar = (ProgressBar) findViewById(R.id.etatmodehorsligne_gestion_disk_buttons_progressBar);
		if(deplacementEnCours){
			deplacementEnCoursProgressBar.setVisibility(View.VISIBLE);
		}
		else deplacementEnCoursProgressBar.setVisibility(View.GONE);
		
		Button internalDiskBtn = (Button) findViewById(R.id.etatmodehorsligne_diskselection_internal_depl_btn);
		internalDiskBtn.setEnabled(!deplacementEnCours);
		//if(!deplacementEnCours){
		switch (currentImageLocation){
		case APP_INTERNAL:
			internalDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_DELETE_DISK+"-"+MovePhotoDiskService.SRC_INTERNAL));
			internalDiskBtn.setText(R.string.etatmodehorsligne_diskselection_internal_depl_btn_text_selected);
			break;
		case PRIMARY:
			internalDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_MOVE+"-"+MovePhotoDiskService.SRC_PRIMARY+"2"+ MovePhotoDiskService.SRC_INTERNAL));
			internalDiskBtn.setText(R.string.etatmodehorsligne_diskselection_internal_depl_btn_text_selected);
			break;
		case SECONDARY:
			internalDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_MOVE+"-"+MovePhotoDiskService.SRC_SECONDARY+"2"+ MovePhotoDiskService.SRC_INTERNAL));
			internalDiskBtn.setText(R.string.etatmodehorsligne_diskselection_internal_depl_btn_text_selected);
			break;
		}
			
		//}
		Button primaryDiskBtn = (Button) findViewById(R.id.etatmodehorsligne_diskselection_primary_depl_btn);
		primaryDiskBtn.setEnabled(!deplacementEnCours);
		if(!disqueOutils.identifiantPartition(DiskEnvironment.getInternalStorage()).equals(
				disqueOutils.identifiantPartition(DiskEnvironment.getPrimaryExternalStorage()) )
			){
			//if(!deplacementEnCours){
			switch (currentImageLocation){
			case PRIMARY:
				primaryDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_DELETE_DISK+"-"+MovePhotoDiskService.SRC_PRIMARY));
				primaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_internal_stop_btn_text_selected);
				break;
			case APP_INTERNAL:
				primaryDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_MOVE+"-"+MovePhotoDiskService.SRC_INTERNAL+"2"+ MovePhotoDiskService.SRC_PRIMARY));
				primaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_internal_stop_btn_text_selected);
				break;
			case SECONDARY:
				primaryDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_MOVE+"-"+MovePhotoDiskService.SRC_SECONDARY+"2"+ MovePhotoDiskService.SRC_PRIMARY));
				primaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_internal_stop_btn_text_selected);
				break;
			}
		}else{
			primaryDiskBtn.setEnabled(false);
			primaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_primary_btn_text_not_available);
		}
			
		//}
		Button secondaryDiskBtn = (Button) findViewById(R.id.etatmodehorsligne_diskselection_secondary_depl_btn);
		secondaryDiskBtn.setEnabled(!deplacementEnCours);
		if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
			//if(!deplacementEnCours){
			switch (currentImageLocation){
			case SECONDARY:
				secondaryDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_DELETE_DISK+"-"+MovePhotoDiskService.SRC_SECONDARY));
				secondaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_secondary_depl_btn_text_selected);
				break;
			case APP_INTERNAL:
				secondaryDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_MOVE+"-"+MovePhotoDiskService.SRC_INTERNAL+"2"+ MovePhotoDiskService.SRC_SECONDARY));
				secondaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_secondary_depl_btn_text_selected);
				break;
			case PRIMARY:
				secondaryDiskBtn.setOnClickListener(reusableClickListener.get(MovePhotoDiskService.ACT_MOVE+"-"+MovePhotoDiskService.SRC_PRIMARY+"2"+ MovePhotoDiskService.SRC_SECONDARY));
				secondaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_secondary_depl_btn_text_selected);
				break;
			}
				
			//}
		}
		else{
			secondaryDiskBtn.setEnabled(false);
			secondaryDiskBtn.setText(R.string.etatmodehorsligne_diskselection_secondary_btn_text_not_available);
		}
		
		Log.d(LOG_TAG, "refreshScreenData thread = "+Thread.currentThread());

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
