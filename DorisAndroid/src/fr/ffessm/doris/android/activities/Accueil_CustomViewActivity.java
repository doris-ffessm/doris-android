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
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code additional imports Accueil_CustomViewActivity
import java.util.HashMap;
import java.util.List;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.j256.ormlite.dao.CloseableIterator;
import android.widget.ImageButton;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.activities.view.MultiProgressBar;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.async.VerifieMAJFiche_BgActivity;
import fr.ffessm.doris.android.async.VerifieMAJFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

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
	
	Fiches_Outils fichesOutils = new Fiches_Outils(getContext());
	App_Outils outils = new App_Outils(getContext());
	Param_Outils paramOutils = new Param_Outils(getContext());
	Reseau_Outils reseauOutils = new Reseau_Outils(getContext());
	
	protected HashMap<Integer, MultiProgressBar> progressBarZones = new HashMap<Integer, MultiProgressBar>(); 
	
	// si false alors c'est que l'utilisateur a cliqué sur la croix pour le fermer, 
	// tant que l'appli est ouverte elle ne se rouvrira pas, même en cas de rotation
	public static boolean mustShowLogoFede = true;
	
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.accueil_customview);
        //Start of user code onCreate Accueil_CustomViewActivity
	/*	// si pas de fiche alors il faut initialiser la base à partir du prefetched_DB
		RuntimeExceptionDao<Fiche, Integer> ficheDao = getHelper().getFicheDao();
    	if(ficheDao.countOf() == 0){
    		new InitialisationApplication_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
    		
    		showToast("Veuillez patienter que la base de donnée s'initialise.");
		}*/
        
        
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - isOnCreate : "+isOnCreate);
        
        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
        	/*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
            	if(Accueil_CustomViewActivity.this.isFinishing()) return;
            	if(inputMessage.obj != null ){
            		showToast((String) inputMessage.obj);
            	}
            	refreshScreenData();
            }
        };

        // Affichage Icones Fédé.
        if (!mustShowLogoFede || !paramOutils.getParamBoolean(R.string.pref_key_accueil_aff_iconesfede, true)){
        	((RelativeLayout) findViewById(R.id.accueil_logos)).setVisibility(View.GONE);
        }
        
        // Affichage Debug
        if (paramOutils.getParamBoolean(R.string.pref_key_affichage_debug, false)){
        	if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Affichage Debug");
        	((RelativeLayout) findViewById(R.id.accueil_logos)).setVisibility(View.GONE);
        	((ScrollView) findViewById(R.id.accueil_debug)).setVisibility(View.VISIBLE);
        }

        // affichage zone géo
        createNavigationZonesGeoViews();

        //Lors du 1er démarrage de l'application dans la version actuelle,
        //on affiche la boite d'A Propos
        String VersionAffichageAPropos = paramOutils.getParamString(R.string.pref_key_a_propos_version, "");
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - VersionAffichageAPropos : "+VersionAffichageAPropos);
    	
        //Récupération du numéro de Version de DORIS
        String appVersionName = outils.getAppVersion();
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - appVersionName : "+appVersionName);

        if (!VersionAffichageAPropos.equals(appVersionName)) {
        	if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Affichage A Propos");
			AffichageMessageHTML aPropos = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
			aPropos.aProposAff();
        	
			paramOutils.setParamString(R.string.pref_key_a_propos_version, appVersionName);
        }
        
        if(DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null){
        	// une tache précédente est en cours, on se réabonne aux évènements 
        	// (on est probablement sur une rotation d'écran)
        	Log.d(LOG_TAG, "onCreate() - une tache précédente est en cours, on se réabonne aux évènements");
        }
        else{
	        // pas de tache précédente en cours
        	// démarre ou pas un téléchargement de photos au démarrage	
        	Reseau_Outils.ConnectionType connectionType = reseauOutils.getConnectionType();
        	Log.d(LOG_TAG, "onCreate() - connectionType : "+connectionType);
        	boolean wifiOnly = paramOutils.getParamBoolean(R.string.pref_key_mode_precharg_wifi_only, true);
        	Log.d(LOG_TAG, "onCreate() - wifiOnly : "+wifiOnly);
        	if ( connectionType == Reseau_Outils.ConnectionType.WIFI 
	        		|| (! wifiOnly && connectionType == Reseau_Outils.ConnectionType.GSM)){
		
        		Log.d(LOG_TAG, "onCreate() - Lancement préchargement");
        		// On démarrage d'abord la MaJ des fiches, puis elle enchaînera avec telechargePhotosFiches
        		/* DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity =
        		 * 		(TelechargePhotosAsync_BgActivity) new TelechargePhotosAsync_BgActivity(
        		 * 			getApplicationContext(), this.getHelper()).execute("");
        		 * */
        		//VerifieMAJFiches_BgActivity verifieMAJFiches_BgActivity = DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity;		    	
				
	        	DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity =
	        		(VerifieMAJFiches_BgActivity) new VerifieMAJFiches_BgActivity(getApplicationContext()/*,
						this.getHelper()*/).execute(""+Fiches_Outils.TypeLancement_kind.START);

	        }
        } 
        DorisApplicationContext.getInstance().addDataChangeListeners(this);

		//End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
		//Start of user code onResume Accueil_CustomViewActivity
		Log.d(LOG_TAG, "onResume()");
		//End of user code
	}
    //Start of user code additional code Accueil_CustomViewActivity
    @Override
    protected void onPause() {
        super.onPause();
        //PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onDestroy(){
    	Log.d(LOG_TAG, "onDestroy()");
    	Log.d(LOG_TAG, "onDestroy() - isFinishing() : "+isFinishing());
    	
    	DorisApplicationContext.getInstance().removeDataChangeListeners(this);
    	
    	TelechargePhotosAsync_BgActivity telechargePhotosAsync_BgAct = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
    	if(telechargePhotosAsync_BgAct != null && telechargePhotosAsync_BgAct.getStatus() == Status.RUNNING){ 		
    		if(isFinishing()) Log.d(LOG_TAG, "onDestroy() - TelechargePhotosAsync.cancel(true) : "+telechargePhotosAsync_BgAct.cancel(true) );
    	}
    	
    	VerifieMAJFiches_BgActivity verifieMAJFiches_BgAct = DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity;
    	if(verifieMAJFiches_BgAct != null && verifieMAJFiches_BgAct.getStatus() == Status.RUNNING){ 		
    		if(isFinishing()) Log.d(LOG_TAG, "onDestroy() - VerifieMAJFiches.cancel(true) : "+verifieMAJFiches_BgAct.cancel(true) );
    	}
    	
    	VerifieMAJFiche_BgActivity verifieMAJFiche_BgAct = DorisApplicationContext.getInstance().verifieMAJFiche_BgActivity;
    	if(verifieMAJFiche_BgAct != null && verifieMAJFiche_BgAct.getStatus() == Status.RUNNING){ 		
    		if(isFinishing()) Log.d(LOG_TAG, "onDestroy() - VerifieMAJFiche.cancel(true) : "+verifieMAJFiche_BgAct.cancel(true) );
    	}
    	
    	super.onDestroy();
    }
    
    protected View createNavigationZoneView(final ZoneGeographique zone){
    	final Context context = this;
        final int zoneId = zone.getId();
        
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewZone = inflater.inflate(R.layout.zonegeoselection_listviewrow, null);
        
        TextView tvLabel = (TextView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_label);
        tvLabel.setText(zone.getNom());
        
        if(ScreenTools.getScreenWidth(context) > 500){ // TODO devra probablement être adapté lorsque l'on aura des fragments
	        TextView tvLDetails = (TextView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_details);
	        tvLDetails.setVisibility(View.VISIBLE);
	        tvLDetails.setText(zone.getDescription());
        } else {
        	viewZone.findViewById(R.id.zonegeoselection_listviewrow_details).setVisibility(View.GONE);
        }
        
        // Utilise le bouton select pour naviguer vers 
        ImageButton imgSelect = (ImageButton) viewZone.findViewById(R.id.zonegeoselection__selectBtn);
        imgSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
				// positionne la recherche pour cette zone
				ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zoneId);
				// réinitialise le filtre espèce
				ed.putInt(context.getString(R.string.pref_key_filtre_groupe), 1);
		        ed.commit();				
				//Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
				Intent toGroupeSelectionView = new Intent(context, GroupeSelection_ClassListViewActivity.class);
		        Bundle b = new Bundle();
		        b.putBoolean("GroupeSelection_depuisAccueil", true);
		        toGroupeSelectionView.putExtras(b);
		        showToast(getString(R.string.accueil_recherche_guidee_label_text)+"; "
		        	+Constants.getTitreCourtZoneGeographique(Constants.getZoneGeographiqueFromId(zone.getId() )));
		        
		        startActivity(toGroupeSelectionView);
			}
		});
        
        String uri = fichesOutils.getZoneIcone(zone.getId()); 
        int imageZone = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
        
        ImageView ivIcone = (ImageView)viewZone.findViewById(R.id.zonegeoselection_listviewrow_icon);
        ivIcone.setImageResource(imageZone);   
        int iconeZine = Integer.valueOf(paramOutils.getParamString(R.string.pref_key_accueil_icon_size, "64"));
	    ivIcone.setMaxHeight(iconeZine);
	    ivIcone.setMaxWidth(iconeZine);
        
        
        viewZone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
				// positionne la recherche pour cette zone
				ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zoneId);
				// réinitialise le filtre espèce
				ed.putInt(context.getString(R.string.pref_key_filtre_groupe), 1);
		        ed.commit();
		        showToast(Constants.getTitreCourtZoneGeographique(Constants.getZoneGeographiqueFromId(zone.getId() )));
				startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
			}
		});
    	return viewZone;
    }
    
    protected void createNavigationZonesGeoViews(){

    	LinearLayout llContainerLayout =  (LinearLayout) findViewById(R.id.accueil_navigation_zones_layout);
    	// Affichage lien vers "toutes Zones"
    	ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.accueil_customview_zonegeo_touteszones));
        
        llContainerLayout.addView(createNavigationZoneView(zoneToutesZones));
    	
    	// affichage lien vers les zones 
    	
        List<ZoneGeographique> listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
			
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			llContainerLayout.addView(createNavigationZoneView(zoneGeo));
		}
        
        
    }
    
    /*public void onClickAfficherListe(View view){
    	showToast("L'idée est d'afficher directement la liste filtrée depuis ici, mais il faudrait que la ProgressionBar soit un objet plus propre.");
    }*/
	public void onClickBtnListeFiches(View view){
		startActivity(new Intent(this, ListeFicheAvecFiltre_ClassListViewActivity.class));
    }
	/*public void onClickBtnRechercheGuidee(View view){
		//Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
		Intent toGroupeSelectionView = new Intent(this, GroupeSelection_ClassListViewActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("GroupeSelection_depuisAccueil", true);
        toGroupeSelectionView.putExtras(b);
        startActivity(toGroupeSelectionView);
	}*/
	/*public void onClickBtnListeParticipants(View view){
		startActivity(new Intent(this, ListeParticipantAvecFiltre_ClassListViewActivity.class));
	}
	public void onClickBtnGlossaire(View view){
		startActivity(new Intent(this, Glossaire_ClassListViewActivity.class));
	}*/
	public void onClickBtnIconeSiteWeb_doris(View view){
		String url = getString(R.string.accueil_customview_logo_doris_url);
		if (!url.isEmpty()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
    }
	public void onClickBtnIconeSiteBio(View view){
		String url = getString(R.string.accueil_customview_logo_bio_url);
		if (!url.isEmpty()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
    }
	public void onClickBtnIconeSiteWebFFESSM(View view){
		String url = getString(R.string.ffessm_url);
		if (!url.isEmpty()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
    }
	public void onClickBtnFermer(View view){
		mustShowLogoFede = false;
    	((RelativeLayout) findViewById(R.id.accueil_logos)).setVisibility(View.GONE);
    	
    	showToast(getContext().getString(R.string.accueil_customview_logos_preference));
    }
	/*public void reinitializeDBFromPrefetched(){
		//XMLHelper.loadDBFromXMLFile(getHelper().getDorisDBHelper(), this.getResources().openRawResource(R.raw.prefetched_db));

		new InitialisationApplication_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
		showToast("Veuillez patienter que la base de donnée s'initialise.");
		
    }*/
	
	public void dataHasChanged(String textmessage){
		 Message completeMessage = mHandler.obtainMessage(1, textmessage);
         completeMessage.sendToTarget();
	}
	public Context getContext(){
		return this;
	}
	
	
	protected void updateProgressBarZone(ZoneGeographique inZoneGeo, MultiProgressBar progressBarZone){
		   //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - Début");
		   
		   String uri = fichesOutils.getZoneIcone(inZoneGeo.getId());
		   //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - uri icone : "+uri);  
		   int imageZone = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
		   
		   boolean affichageBarrePhotoPrinc;
		   boolean affichageBarrePhoto;
		   String summaryTexte = "";
		   int nbFichesZoneGeo = 0;
		   int avancementPhotoPrinc =0;
		   int avancementPhoto =0;
		   
		   if (inZoneGeo.getId() == -1){
			   nbFichesZoneGeo = (int)getHelper().getFicheDao().countOf();
		   } else {
			   nbFichesZoneGeo = getHelper().getFiches_ZonesGeographiquesDao().queryForEq(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME, inZoneGeo.getId()).size();
		   }
		   
		   Photos_Outils photosOutils = new Photos_Outils(getContext());
		   Photos_Outils.PrecharMode precharModeZoneGeo = photosOutils.getPrecharModeZoneGeo(inZoneGeo.getId());
		   
		   if ( precharModeZoneGeo == Photos_Outils.PrecharMode.P0 ) {

			   affichageBarrePhotoPrinc = false;
			   affichageBarrePhoto = false;
			   
			   summaryTexte = getContext().getString(R.string.avancement_progressbar_aucune_summary);
			   summaryTexte = summaryTexte.replace("@nbF", ""+nbFichesZoneGeo );
			   
		   } else {
			   int nbPhotosPrincATelecharger = photosOutils.getAPrecharQteZoneGeo(inZoneGeo.getId(), true);
			   int nbPhotosATelecharger = photosOutils.getAPrecharQteZoneGeo(inZoneGeo.getId(), false);
			   int nbPhotosPrincDejaLa = photosOutils.getDejaLaQteZoneGeo(inZoneGeo.getId(), true);
			   int nbPhotosDejaLa = photosOutils.getDejaLaQteZoneGeo(inZoneGeo.getId(), false);
			   
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
		   
		   // ajout au résumé de la date de la base
		   StringBuilder sbTexte = new StringBuilder();
		   sbTexte.append(getContext().getString(R.string.accueil_customview_texte_text));
	    	
		   CloseableIterator<DorisDB_metadata> itDorisDB = getHelper().getDorisDB_metadataDao().iterator();
		   while (itDorisDB.hasNext()) {
			   sbTexte.append(itDorisDB.next().getDateBase());
		   }
		   sbTexte.append("\n");
		   sbTexte.append(summaryTexte);
		   
		   progressBarZone.update(inZoneGeo.getNom(), sbTexte.toString(), imageZone, affichageBarrePhotoPrinc, avancementPhotoPrinc, affichageBarrePhoto, avancementPhoto, downloadInProgress);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(LOG_TAG, "Preference change detected for key ="+key);
        if (key.equals(R.string.pref_key_theme)) {
            // change theme to the selected one
        	showToast("Preference change detected for Theme="+sharedPreferences.getString(key, "Default" ));
        //	sharedPreferences.getString(key, )
        //	ThemeUtil.changeToTheme(this, theme)
        }
    }

	
	//End of user code

    /** refresh screen from data 
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
        int iconeZine = Integer.valueOf(paramOutils.getParamString(R.string.pref_key_accueil_icon_size, "64"));
        ((ImageView) findViewById(R.id.accueil_recherche_precedente_icone)).setMaxHeight(iconeZine);
        
        
    	StringBuilder sbRecherchePrecedente = new StringBuilder(); 
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    	
        int filtreCourantId = prefs.getInt(getString(R.string.pref_key_filtre_groupe), 1);	        
		if(filtreCourantId==1){
			sbRecherchePrecedente.append(getString(R.string.accueil_recherche_precedente_filtreEspece_sans));
        }
		else {
			Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);
			sbRecherchePrecedente.append(getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec)+" "+groupeFiltreCourant.getNomGroupe().trim());
		}
		sbRecherchePrecedente.append(" ; ");
		int currentFilterId = prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1);
        if(currentFilterId == -1 || currentFilterId == 0){ // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
        	sbRecherchePrecedente.append(getString(R.string.accueil_recherche_precedente_filtreGeographique_sans));
        }
        else{
        	ZoneGeographique currentZoneFilter= getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
        	sbRecherchePrecedente.append(getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec)+" "+currentZoneFilter.getNom().trim());
        }
        
        // TODO rappeler le dernier text recherché
    	TextView tvRecherchePrecedente = (TextView)findViewById(R.id.accueil_recherche_precedente_details);
    	tvRecherchePrecedente.setText(sbRecherchePrecedente.toString());
    	
    	//((ImageView) findViewById(R.id.accueil_recherche_guidee_icone)).setMaxHeight(iconeZine);
    	
    	
    	// Affichage de chaque Zones - Toutes Zones en 1er
    	// if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - isOnCreate : "+isOnCreate); 
    	ZoneGeographique zoneToutesZones = new ZoneGeographique();
    	zoneToutesZones.setId(-1);
    	zoneToutesZones.setNom(getContext().getString(R.string.avancement_touteszones_titre));
    	if (isOnCreate) {
	    	llContainerLayout =  (LinearLayout) findViewById(R.id.accueil_progress_layout);
	    	
	    	// Avancement et Affichage toutes Zones
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
					startActivity(new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
				}
			});
	    	llContainerLayout.addView(progressBarZoneGenerale);

	    	
	    	
    	} else {
    		// if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - update progress bar : ");
    		updateProgressBarZone(zoneToutesZones, progressBarZones.get(zoneToutesZones.getId()));
    	
    	}
		
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	// Debbug
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	if (paramOutils.getParamBoolean(R.string.pref_key_affichage_debug, false)){
	    	StringBuilder sb = new StringBuilder();
	    	sb.append("- - Debbug - -\n");
	    	
	    	CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
	    	while (it.hasNext()) {
	    		sb.append("Date base locale : " + it.next().getDateBase()+"\n");
			}
	    	/*
	    	RuntimeExceptionDao<Fiche, Integer> ficheDao = getHelper().getFicheDao();
	    	sb.append("Nombres de fiches dans la base locale : "+ficheDao.countOf());
	     	RuntimeExceptionDao<PhotoFiche, Integer> photoFicheDao = getHelper().getPhotoFicheDao();
	    	sb.append("\nNombres de photos référencées : "+photoFicheDao.countOf());
	    	sb.append("\n\tNombres de photos téléchargées : "+Outils.getVignetteCount(this.getApplicationContext()));
	    	double sizeInMiB = Outils.getPhotosDiskUsage(getApplicationContext())/(double)(1024.0*1024.0);
	    	sb.append("\t("+String.format("%.2f", sizeInMiB)+" MiB)");
	    	*/
	    	
	    	// Test pour voir où est le cache Picasso
	    	/*
	    	sb.append("\n- - - - - -\n");
	    	sb.append(getApplicationContext().getCacheDir().getAbsolutePath()+"\n");
	     	for (File child:getApplicationContext().getCacheDir().listFiles()) {
	     		sb.append(child.getAbsolutePath()+"\n");
	     		if (child.getName().equals("picasso-cache") ) {
	     			sb.append(""+String.format("%.2f", Outils.getDiskUsage(getApplicationContext(), child)/(double)(1024.0*1024.0) )+" MiB)\n");
	     			
	     			int i = 0;
	     			for (File subchild:child.listFiles()) {
	     	     		sb.append("\t\t"+subchild.getName()+"\n");
	     	     		i++;
	     	     		if ( i >5) break;
	     			}
	     		}
	     	}
	     	*/
	    	/*
	     	sb.append("- - - - - -\n");
	     	sb.append(getApplicationContext().getFilesDir().getAbsolutePath()+"\n");
	     	sb.append(getApplicationContext().getFilesDir().listFiles().length+"\n");
	     	sb.append("- - - - - -\n");
	     	URI uri = null;
	     	try {
	     		uri = new URI("file:///android_res/raw/images_groupe_1.gif");
	     	} catch (URISyntaxException e) {
	     		
	     	}
	     	File file = new File(uri);
	     	sb.append(file.getAbsolutePath()+"\n");
	     	sb.append("dir ? : "+file.isDirectory()+" - file ? : "+file.isFile()+"\n");
	     	if (file.isDirectory()) {
		     	for (File child:file.listFiles()) {
		     		sb.append(child.getAbsolutePath()+"\n");
		     		if (child.isDirectory()) {
			     		for (File subChild:child.listFiles()) {
			     			sb.append(" - "+subChild.getAbsolutePath()+"\n");
			     		}
		     		}
		     	}
	     	}
	     	// TODO : Piste pour sauvegarder les images après téléchargement
	     	// Cf. http://stackoverflow.com/questions/19345576/cannot-draw-recycled-bitmaps-exception-with-picasso
	     	// et surtout : http://www.basic4ppc.com/android/forum/threads/picasso-image-downloading-and-caching-library.31495/
	    	// Fin test
	    	*/
	    	
	    	/*
	    	Fiches_Outils fichesOutils = new Fiches_Outils(getApplicationContext());
	    	sb.append(fichesOutils.getDerniereMajListeFichesTypeZoneGeo(1));
	    	*/
	    	
	    	
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
        	case R.id.accueil_customview_action_maj_listesfiches:
        		VerifieMAJFiches_BgActivity verifieMAJFiches_BgActivity = DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity;		    	
				if(verifieMAJFiches_BgActivity == null || verifieMAJFiches_BgActivity.getStatus() != Status.RUNNING) {
	        		DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity =
	        			(VerifieMAJFiches_BgActivity) new VerifieMAJFiches_BgActivity(getApplicationContext()/*,
						this.getHelper()*/).execute(""+Fiches_Outils.TypeLancement_kind.MANUEL);
				}
        		// TODO : refreshScreenData();
            	return true;
	        case R.id.accueil_customview_action_a_propos:
	        	AffichageMessageHTML aPropos = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
				aPropos.affichageMessageHTML(getContext().getString(R.string.a_propos_label)+getContext().getString(R.string.app_name), aPropos.aProposAff(),	"file:///android_res/raw/apropos.html");
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
	        case R.id.accueil_customview_action_aide:
	        	AffichageMessageHTML aide = new AffichageMessageHTML(getContext(), (Activity) getContext(), getHelper());
				aide.affichageMessageHTML(getContext().getString(R.string.aide_label), "", "file:///android_res/raw/aide.html");
				return true;

		//End of user code
			default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
