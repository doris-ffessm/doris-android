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
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageLocation;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.sql.SQLException;
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
import fr.ffessm.doris.android.activities.view.MultiProgressBar;
import fr.ffessm.doris.android.async.DeplacePhotos_BgActivity;
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
	
	protected SparseArray< MultiProgressBar> progressBarZones = new SparseArray< MultiProgressBar>(); 
	protected HashMap<String, View.OnClickListener> reusableClickListener = new HashMap<String, View.OnClickListener>();
	
	// cache pour éviter de refaire des accés BDD inutiles
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

        createProgressZone();
        
        initOnClickListener();
        
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
    
    public Context getContext(){
		return this;
	}
    
    protected void updateProgressBarZone(ZoneGeographique inZoneGeo, MultiProgressBar progressBarZone){
    	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - Début");
    	Fiches_Outils fichesOutils = new Fiches_Outils(getContext());
    	String uri = fichesOutils.getZoneIcone(inZoneGeo.getZoneGeoKind());
    	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - uri icone : "+uri);  
    	int imageZone = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
	   
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
	   
	   progressBarZone.update(inZoneGeo.getNom(), summaryTexte, imageZone, affichageBarrePhotoPrinc, avancementPhotoPrinc, affichageBarrePhoto, avancementPhoto, downloadInProgress);
	}
    
    protected void createProgressZone(){

    	LinearLayout llContainerLayout =  (LinearLayout) findViewById(R.id.etatmodehorsligne_customview_avancements_layout);
    	
    	// Avancement et Affichage toutes Zones
    	ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
    	
    	MultiProgressBar progressBarZoneGenerale = new MultiProgressBar(this);
    	updateProgressBarZone(zoneToutesZones, progressBarZoneGenerale);
    	progressBarZones.put(zoneToutesZones.getId(), progressBarZoneGenerale); 
    	
    	final Context context = this;
    	progressBarZoneGenerale.pbProgressBar_running.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
				DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
				
				ProgressBar pbRunningBarLayout =  (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
				pbRunningBarLayout.setVisibility(View.GONE);
			}
		});
    	progressBarZoneGenerale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(EtatModeHorsLigne_CustomViewActivity.this, Preference_PreferenceViewActivity.class);
				intent.putExtra("type_parametre", "mode_precharg_category");
				intent.putExtra("parametre", "button_qualite_images_zones_key");
				
				startActivity(intent);
			}
		});
    	llContainerLayout.addView(progressBarZoneGenerale);

    	
    	// Avancement par Zone
    	if(listeZoneGeo == null) listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
 			
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			MultiProgressBar progressBarZone = new MultiProgressBar(this);
 		    updateProgressBarZone(zoneGeo, progressBarZone);
 		    final int zoneGeoId = zoneGeo.getId();

 		   /* progressBarZone.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
					ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zoneGeoId);
			        ed.commit();
					startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
				}
			}); */
 		   progressBarZone.setOnClickListener(new View.OnClickListener() {
 				@Override
 				public void onClick(View v) {
 					//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setOnClickListener() - zoneGeoId : "+zoneGeoId);
 					Intent intent = new Intent(EtatModeHorsLigne_CustomViewActivity.this, Preference_PreferenceViewActivity.class);
 					
 					String param = null;
 					
 					switch (zoneGeoId){
 					case 1 :
 						param = "mode_precharg_photo_region_france"; break;
 					case 2 :
 						param = "mode_precharg_photo_region_eaudouce"; break;
 					case 3 :
 						param = "mode_precharg_photo_region_atlantno"; break;
 					case 4 :
 						param = "mode_precharg_photo_region_indopac"; break;
 					case 5 :
 						param = "mode_precharg_photo_region_caraibes"; break;
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
 		    progressBarZones.put(zoneGeoId, progressBarZone); 
	 		llContainerLayout.addView(progressBarZone);
		} 
    }
    protected void initOnClickListener(){
    	addReusableClickListener(DeplacePhotos_BgActivity.INTERNAL, DeplacePhotos_BgActivity.PRIMARY);
    	addReusableClickListener(DeplacePhotos_BgActivity.INTERNAL, DeplacePhotos_BgActivity.SECONDARY);
    	addReusableClickListener(DeplacePhotos_BgActivity.INTERNAL, null);
    	
    	addReusableClickListener(DeplacePhotos_BgActivity.PRIMARY, DeplacePhotos_BgActivity.INTERNAL);
    	addReusableClickListener(DeplacePhotos_BgActivity.PRIMARY, DeplacePhotos_BgActivity.SECONDARY);
    	addReusableClickListener(DeplacePhotos_BgActivity.PRIMARY, null);
    	
    	addReusableClickListener(DeplacePhotos_BgActivity.SECONDARY, DeplacePhotos_BgActivity.INTERNAL);
    	addReusableClickListener(DeplacePhotos_BgActivity.SECONDARY, DeplacePhotos_BgActivity.PRIMARY);
    	addReusableClickListener(DeplacePhotos_BgActivity.SECONDARY, null);
    }
    private void addReusableClickListener(final String source, final String target){
    	if(target != null){
	    	reusableClickListener.put(source+"2"+target, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// déplace vers internal
					/* DorisApplicationContext.getInstance().deplacePhotos_BgActivity = 
							(DeplacePhotos_BgActivity) new DeplacePhotos_BgActivity(getApplicationContext(),getHelper()).
							execute(source, target); */
					// utilise le déplcament sous forme de service
					// use this to start and trigger a service
					Intent i= new Intent(getApplicationContext(), MovePhotoDiskService.class);
					// add data to the intent
					i.putExtra("fr.ffessm.doris.android.SOURCE_DISK", source);
					i.putExtra("fr.ffessm.doris.android.TARGET_DISK", target);
					getApplicationContext().startService(i);
					
					DorisApplicationContext.getInstance().notifyDataHasChanged(null);
					/*
					showToast(source+"2"+target);
					ImageLocation destImageLocation;
			    	if(target.equals(DeplacePhotos_BgActivity.INTERNAL)){
			    		destImageLocation = ImageLocation.APP_INTERNAL;
			    	}else if(target.equals(DeplacePhotos_BgActivity.PRIMARY)){
			    		destImageLocation = ImageLocation.PRIMARY;
			    	}else {destImageLocation = ImageLocation.SECONDARY;
			    	}
					new Photos_Outils(EtatModeHorsLigne_CustomViewActivity.this).setPreferedLocation(destImageLocation);
			        DorisApplicationContext.getInstance().notifyDataHasChanged(null);
			        */
				}
			});
    	}
    	else{
    		reusableClickListener.put(source+"2NULL", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// utilise le déplcament sous forme de service
					// use this to start and trigger a service
					Intent i= new Intent(getApplicationContext(), MovePhotoDiskService.class);
					// add data to the intent
					i.putExtra(MovePhotoDiskService.SOURCE_DISK, source);
					i.putExtra(MovePhotoDiskService.TARGET_DISK, MovePhotoDiskService.DELETE);
					getApplicationContext().startService(i);
					
					DorisApplicationContext.getInstance().notifyDataHasChanged(null);
				}
			});
    	}
    }
    
    private AsyncComputeLongRefreshScreenData lastAsyncComputeLongRefreshScreenData = null;
    
    /**
     * Classe utilisée pour rafraichir l'ihm, mais qui prend un peu de temps et ne peu pas être mis dans refreshScreenData sinon on pénalise le reste
     *
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
			// baisse la priorité pour s'assurer une meilleure réactivité
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			// calcule en tache de fond
			Photos_Outils photo_Outils = new Photos_Outils(EtatModeHorsLigne_CustomViewActivity.this);
			internalUsedSize =  photo_Outils.getPhotosDiskUsage(ImageLocation.APP_INTERNAL);
			primaryUsedSize =  photo_Outils.getPhotosDiskUsage(ImageLocation.PRIMARY);
			if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
				secondaryUsedSize = photo_Outils.getPhotosDiskUsage(ImageLocation.SECONDARY);
			}
			if(needRestart){
				// si besoin de recommencer, alors fait une mini pause avant redéclencher
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			refreshUsedDisk(internalUsedSize, primaryUsedSize, secondaryUsedSize);
			
			if(needRestart)	{
				// une notification a été recue pendant qu'on travaillait, faut la relancer pour recommencer
				lastAsyncComputeLongRefreshScreenData = (AsyncComputeLongRefreshScreenData) new AsyncComputeLongRefreshScreenData().execute();
			}
		}
	}
    
    /**
     * affiche les info d'utilisation du disque
     * Note, cela a été séparé car normalement je voulais le mettre dans un asynctask, mais on ne peut en exécuté qu'une à la fois, ce qui est bloqué si un téléchargment est en cours 
     * -> prévoir de migrer ces asynctask dans des Service
     * @param internalUsedSize
     * @param primaryUsedSize
     */
    protected void refreshUsedDisk(long internalUsedSize, long primaryUsedSize, long secondaryUsedSize){
		
    	TextView etatDiskTextView = (TextView) findViewById(R.id.etatmodehorsligne_customview_diskusage_description_textView);
		StringBuilder etatDiskStringBuilder = new StringBuilder();
		etatDiskStringBuilder.append("(Espace utilisé / disponible / total)\n");
		etatDiskStringBuilder.append("Mémoire interne : "+Disque_Outils.getHumanDiskUsage(internalUsedSize)+" / ");
		etatDiskStringBuilder.append(Disque_Outils.getHumanDiskUsage(DiskEnvironment.getInternalStorage().getSize().first)+" / ");
		etatDiskStringBuilder.append(Disque_Outils.getHumanDiskUsage(DiskEnvironment.getInternalStorage().getSize().second)+"\n");
		//etatDiskStringBuilder.append(DiskEnvironment.getInternalStorage().getFile().getAbsolutePath()+"\n");
		//etatDiskStringBuilder.append("Donnée application="+this.getDir(Photos_Outils.MED_RES_FICHE_FOLDER, Context.MODE_PRIVATE)+"\n");
		etatDiskStringBuilder.append("Disque primaire : "+Disque_Outils.getHumanDiskUsage(primaryUsedSize)+" / ");
		etatDiskStringBuilder.append(Disque_Outils.getHumanDiskUsage(DiskEnvironment.getPrimaryExternalStorage().getSize().first)+" / ");
		etatDiskStringBuilder.append(Disque_Outils.getHumanDiskUsage(DiskEnvironment.getPrimaryExternalStorage().getSize().second)+"\n");
		//etatDiskStringBuilder.append(DiskEnvironment.getPrimaryExternalStorage().getFile().getAbsolutePath()+"\n");
		if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
			etatDiskStringBuilder.append("Disque secondaire : "+Disque_Outils.getHumanDiskUsage(secondaryUsedSize)+" / ");
			try {
				etatDiskStringBuilder.append(Disque_Outils.getHumanDiskUsage(DiskEnvironment.getSecondaryExternalStorage().getSize().first)+" / ");
				etatDiskStringBuilder.append(Disque_Outils.getHumanDiskUsage(DiskEnvironment.getSecondaryExternalStorage().getSize().second)+"\n");
				// etatDiskStringBuilder.append(DiskEnvironment.getSecondaryExternalStorage().getFile().getAbsolutePath()+"\n");
			} catch (NoSecondaryStorageException e) {
				etatDiskStringBuilder.append(" not Available");
			}
			
		}
		//etatDiskStringBuilder.append("Photo actuellement sur : "+new Photos_Outils(EtatModeHorsLigne_CustomViewActivity.this).getPreferedLocation()+"\n");
		etatDiskTextView.setText(etatDiskStringBuilder.toString());
    }
    
	//End of user code

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen EtatModeHorsLigne_CustomViewActivity
    	// mise à jour de la date de la base
    	TextView etatBase = (TextView) findViewById(R.id.etatmodehorsligne_customview_etat_base_description_textView);
    	CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		etatBase.setText(getString(R.string.etatmodehorsligne_customview_etat_base_description_text)+it.next().getDateBase());
    		//sb.append("Date base locale : " + it.next().getDateBase()+"\n");
		}
    	// mise à jour des progress bar
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - update progress bar : ");
		ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
		updateProgressBarZone(zoneToutesZones, progressBarZones.get(zoneToutesZones.getId()));
		if(listeZoneGeo == null) listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			updateProgressBarZone(zoneGeo, progressBarZones.get(zoneGeo.getId()));
		}
		
		
		// workaround temporaire , on ne peut pas lancer simultanément 2 asynctask (elles attendent la fin de la précédentes)
		// en attendant que les autres taches soient transformées en en service, on fait le calcul dans le thread de l'UI quand même
		if( DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null ||
				DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity != null ||
				DorisApplicationContext.getInstance().verifieMAJFiche_BgActivity != null){
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
		Photos_Outils photo_Outils = new Photos_Outils(this);
		// disque courant
		ImageLocation currentImageLocation = photo_Outils.getPreferedLocation();		
		// vérifie qu'il n'y a pas un déplacement en cours
		boolean deplacementEnCours = DorisApplicationContext.getInstance().isMovingPhotos;
		
		ProgressBar deplacementEnCoursProgressBar = (ProgressBar) findViewById(R.id.etatmodehorsligne_customview_diskusage_buttons_progressBar);
		if(deplacementEnCours){
			deplacementEnCoursProgressBar.setVisibility(View.VISIBLE);
		}
		else deplacementEnCoursProgressBar.setVisibility(View.GONE);
		
		Button internalDiskBtn = (Button) findViewById(R.id.etatmodehorsligne_customview_diskselection_internal_btn);
		internalDiskBtn.setEnabled(!deplacementEnCours);
		//if(!deplacementEnCours){
		switch (currentImageLocation){
		case APP_INTERNAL:
			internalDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.INTERNAL+"2NULL"));
			internalDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_internal_btn_text_selected);
			break;
		case PRIMARY:
			internalDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.PRIMARY+"2"+ DeplacePhotos_BgActivity.INTERNAL));
			internalDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_internal_btn_text_not_selected);
			break;
		case SECONDARY:
			internalDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.SECONDARY+"2"+ DeplacePhotos_BgActivity.INTERNAL));
			internalDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_internal_btn_text_not_selected);
			break;
		}
			
		//}
		Button primaryDiskBtn = (Button) findViewById(R.id.etatmodehorsligne_customview_diskselection_primary_btn);
		primaryDiskBtn.setEnabled(!deplacementEnCours);
		//if(!deplacementEnCours){
		switch (currentImageLocation){
		case PRIMARY:
			primaryDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.PRIMARY+"2NULL"));
			primaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_primary_btn_text_selected);
			break;
		case APP_INTERNAL:
			primaryDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.INTERNAL+"2"+ DeplacePhotos_BgActivity.PRIMARY));
			primaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_primary_btn_text_not_selected);
			break;
		case SECONDARY:
			primaryDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.SECONDARY+"2"+ DeplacePhotos_BgActivity.PRIMARY));
			primaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_primary_btn_text_not_selected);
			break;
		}
			
		//}
		Button secondaryDiskBtn = (Button) findViewById(R.id.etatmodehorsligne_customview_diskselection_secondary_btn);
		secondaryDiskBtn.setEnabled(!deplacementEnCours);
		if(DiskEnvironment.isSecondaryExternalStorageAvailable()){
			//if(!deplacementEnCours){
			switch (currentImageLocation){
			case SECONDARY:
				secondaryDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.SECONDARY+"2NULL"));
				secondaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_secondary_btn_text_selected);
				break;
			case APP_INTERNAL:
				secondaryDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.INTERNAL+"2"+ DeplacePhotos_BgActivity.SECONDARY));
				secondaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_secondary_btn_text_not_selected);
				break;
			case PRIMARY:
				secondaryDiskBtn.setOnClickListener(reusableClickListener.get(DeplacePhotos_BgActivity.PRIMARY+"2"+ DeplacePhotos_BgActivity.SECONDARY));
				secondaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_secondary_btn_text_not_selected);
				break;
			}
				
			//}
		}
		else{
			secondaryDiskBtn.setEnabled(false);
			secondaryDiskBtn.setText(R.string.etatmodehorsligne_customview_diskselection_secondary_btn_text_not_available);
		}
		
		Log.d(LOG_TAG, "refreshScreenData thread = "+Thread.currentThread());
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
