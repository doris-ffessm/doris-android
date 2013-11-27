/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
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


import java.io.File;
import java.util.List;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.BufferType;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import android.preference.PreferenceManager;
//Start of user code additional imports Accueil_CustomViewActivity

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.j256.ormlite.dao.CloseableIterator;
import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.DorisApplication;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.async.InitialisationApplication_BgActivity;
import fr.ffessm.doris.android.async.TelechargeFiches_BgActivity;
import fr.ffessm.doris.android.async.TelechargePhotosFiches_BgActivity;
import fr.ffessm.doris.android.async.VerifieNouvellesFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.Outils.PrecharMode;
//End of user code
public class Accueil_CustomViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>
//Start of user code additional implements Accueil_CustomViewActivity
	implements DataChangedListener
//End of user code
{
	
	//Start of user code constants Accueil_CustomViewActivity
//	static final int TELECHARGE_FICHE_MENU_ID = 1;	
	static final int TELECHARGE_PHOTO_FICHES_MENU_ID = 2;
//	static final int VERIFIE_MAJ_FICHES_MENU_ID = 3;
//	static final int VERIFIE_NOUVELLES_FICHES_MENU_ID = 4;
//	static final int RESET_DB_FROM_XML_MENU_ID = 5;
	static final int APROPOS = 6;
	
	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();
	Handler mHandler;
	
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        setContentView(R.layout.accueil_customview);
        //Start of user code onCreate Accueil_CustomViewActivity
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
            	if(Accueil_CustomViewActivity.this.isFinishing()) return;
            	if(inputMessage.obj != null ){
            		showToast((String) inputMessage.obj);
            	}
            	refreshScreenData();
            }

        };

        
        //Lors du 1er démarrage de l'application dans la version actuelle,
        //on affiche la boite d'A Propos
        String VersionAffichageAPropos = Outils.getParamString(this.getApplicationContext(), R.string.pref_key_a_propos_version, "jamais");
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - VersionAffichageAPropos : "+VersionAffichageAPropos);
    	
        //Récupération du numéro de Version de DORIS
        String appVersionName = Outils.getAppVersion(this);
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - appVersionName : "+appVersionName);

        if (!VersionAffichageAPropos.equals(appVersionName)) {
        	if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Affichage A Propos");
        	aPropos();
        	
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor prefEdit = preferences.edit();  
        	prefEdit.putString(this.getApplicationContext().getString(R.string.pref_key_a_propos_version), appVersionName);
        	prefEdit.commit();
        }
        
        if(DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity != null){
        	// une tache précédente est en cours, on se réabonne aux évènements 
        	// (on est probablement sur une rotation d'écran)
        	Log.d(LOG_TAG, "onCreate() - une tache précédente est en cours, on se réabonne aux évènements");
        	DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.addListener(this);
        }
        else{
	        // pas de tache précédente en cours
        	// démarre ou pas un téléchargement de photos au démarrage	
        	Outils.ConnectionType connectionType = Outils.getConnectionType(this.getApplicationContext());
        	Log.d(LOG_TAG, "onCreate() - connectionType : "+connectionType);
        	boolean wifiOnly = Outils.getParamBoolean(this.getApplicationContext(), R.string.pref_mode_precharg_wifi_only, true);
        	Log.d(LOG_TAG, "onCreate() - wifiOnly : "+wifiOnly);
        	if ( connectionType == Outils.ConnectionType.WIFI 
	        		|| (! wifiOnly && connectionType == Outils.ConnectionType.GSM)){
		
        		Log.d(LOG_TAG, "onCreate() - Lancement préchargement");
        		DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = (TelechargePhotosFiches_BgActivity) new TelechargePhotosFiches_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");

	        }
        }        
        
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
    protected void onDestroy(){
    	Log.d(LOG_TAG, "onDestroy()");
    	TelechargePhotosFiches_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
    	if(telechargePhotosFiches_BgActivity != null && telechargePhotosFiches_BgActivity.getStatus() == Status.RUNNING){
    		((TelechargePhotosFiches_BgActivity)telechargePhotosFiches_BgActivity).removeListener(this);    		
    		// TODO déterminer si c'est une rotation ou une vrai fin de l'appli pour tuer les taches background ou pas
    		Log.d(LOG_TAG, "onDestroy() - isFinishing() : "+isFinishing());
    		if(isFinishing())
    			Log.d(LOG_TAG, "onDestroy() - telechargePhotosFiches_BgActivity.cancel(true) : "+telechargePhotosFiches_BgActivity.cancel(true) );
    	}
    	super.onDestroy();
    	
    	
    }
    
	public void onClickBtnListeFiches(View view){
		startActivity(new Intent(this, ListeFicheAvecFiltre_ClassListViewActivity.class));
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
	
	
	private void aPropos() {
		StringBuffer texte = new StringBuffer();
		texte.append(getContext().getString(R.string.a_propos_txt_1));
		texte.append(DorisApplication.class.getSimpleName());
		texte.append(System.getProperty("line.separator")); 
				
		texte.append(getContext().getString(R.string.a_propos_txt_2));
		texte.append(Outils.getAppVersion(this));
		texte.append(System.getProperty("line.separator")); 
		
		texte.append(System.getProperty("line.separator"));
		CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		texte.append(getContext().getString(R.string.a_propos_base_date) + it.next().getDateBase());
    		texte.append(System.getProperty("line.separator"));
		}
    	
		StringBuffer sizeFolderTexte =  new StringBuffer();
		if (Outils.getVignetteCount(this.getApplicationContext())!=0 ) {
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getVignetteCount(this.getApplicationContext()));
			sizeFolderTexte.append(getContext().getString(R.string.a_propos_foldersize_vignettes));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getVignettesDiskUsage(this.getApplicationContext()) ) );
			sizeFolderTexte.append(System.getProperty("line.separator")); 
		}
		if (Outils.getMedResCount(this.getApplicationContext())!=0 ) {
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getMedResCount(this.getApplicationContext()));
			sizeFolderTexte.append(getContext().getString(R.string.a_propos_foldersize_med_res));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getMedResDiskUsage(this.getApplicationContext()) ) );
			sizeFolderTexte.append(System.getProperty("line.separator")); 
		}
		if (Outils.getHiResCount(this.getApplicationContext())!=0 ) {
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getHiResCount(this.getApplicationContext()));
			sizeFolderTexte.append(getContext().getString(R.string.a_propos_foldersize_hi_res));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getHiResDiskUsage(this.getApplicationContext()) ) );
			sizeFolderTexte.append(System.getProperty("line.separator")); 
		}
		if (sizeFolderTexte.length()!=0) {
			texte.append(getContext().getString(R.string.a_propos_foldersize_titre));
			texte.append(System.getProperty("line.separator"));
			texte.append(sizeFolderTexte);
		}
			
		affichageMessageHTML( texte.toString(),	"file:///android_res/raw/apropos.html");		
	}
	/* *********************************************************************
     * fonction permettant d'afficher des pages web locales comme l'Apropos par exemple
     ********************************************************************** */
	private void affichageMessageHTML(String inTitre, String inURL) {
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Début");
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - inTitre : " + inTitre);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - inURL : " + inURL);
    	
		final Context  context = getBaseContext();
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);		
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.message_html,
    	                               (ViewGroup) findViewById(R.id.layout_root));
    	
    	TextView text = (TextView) layout.findViewById(R.id.text);
    	text.setText(inTitre);
    	
    	WebView pageWeb = (WebView) layout.findViewById(R.id.webView);
    	pageWeb.setWebViewClient(new WebViewClient() {  
    	    @Override  
    	    public boolean shouldOverrideUrlLoading(WebView inView, String inUrl)  
    	    {  
    	    	
    	    	if (inUrl.startsWith("http")){
	    	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Lancement navigateur Android Défaut");
	        		
	    	    	Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(inUrl));
					context.startActivity(intent);
	
					return true;
    	    	} else {
    	    		return true;
    	    	}
    	    }  
    	});  
    	
    	pageWeb.loadUrl(inURL);
    	alertDialog.setView(layout);
    	alertDialog.show();
    	
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Fin");
	}
	
	//End of user code

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen Accueil_CustomViewActivity
    	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - Début");
    	
    	LinearLayout llContainerLayout =  (LinearLayout) findViewById(R.id.avancements_layout);
    	String summaryTexte = getContext().getString(R.string.avancement_progressbar_summary);
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - summaryTexte"+summaryTexte);
    	summaryTexte = summaryTexte.replace("@total", ""+100 ) ;
    	
    	String summaryTexte1 = summaryTexte.replace("@nb", ""+25 );
    	addProgressBarView(llContainerLayout, "Titre 1", summaryTexte1, R.drawable.icone_france, 25);

    	DorisDBHelper dorisDBHelper = this.getHelper().getDorisDBHelper();
    	List<ZoneGeographique> listeZoneGeo = this.getHelper().getZoneGeographiqueDao().queryForAll();
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());
		for (ZoneGeographique zoneGeo : listeZoneGeo) {
			
			String uri = "drawable/"+ Outils.getZoneIcone(zoneGeo.getId()); 
	    	int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
			
	    	
	    	
	    	addProgressBarView(llContainerLayout, zoneGeo.getNom(), "summaryTexte1", imageResource, 25);
		}
		
		
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	// Debbug
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	StringBuffer sb = new StringBuffer();
    	sb.append("- - Debbug - -\n");
    	
    	CloseableIterator<DorisDB_metadata> it = getHelper().getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		sb.append("Date base locale : " + it.next().getDateBase()+"\n");
		}
    	
    	RuntimeExceptionDao<Fiche, Integer> ficheDao = getHelper().getFicheDao();
    	sb.append("Nombres de fiches dans la base locale : "+ficheDao.countOf());
     	RuntimeExceptionDao<PhotoFiche, Integer> photoFicheDao = getHelper().getPhotoFicheDao();
    	sb.append("\nNombres de photos référencées : "+photoFicheDao.countOf());
    	sb.append("\n\tNombres de photos téléchargées : "+Outils.getVignetteCount(this.getApplicationContext()));
    	double sizeInMiB = Outils.getPhotosDiskUsage(getApplicationContext())/(double)(1024.0*1024.0);
    	sb.append("\t("+String.format("%.2f", sizeInMiB)+" MiB)");
    	
    	
    	// Test pour voir où est le cache Picasso
    	sb.append("\n- - - - - -\n");
    	sb.append(getApplicationContext().getCacheDir().getAbsolutePath()+"\n");
     	for (File child:getApplicationContext().getCacheDir().listFiles()) {
     		sb.append(child.getAbsolutePath()+"\n");
     		if (child.getName().equals("picasso-cache") ) {
     			int i = 0;
     			for (File subchild:child.listFiles()) {
     	     		sb.append("\t\t"+subchild.getName()+"\n");
     	     		i++;
     	     		if ( i >5) break;
     			}
     		}
     	}
     	
     	sb.append("- - - - - -\n");
     	sb.append(getApplicationContext().getFilesDir().getAbsolutePath()+"\n");
     	for (File child:getApplicationContext().getFilesDir().listFiles()) {
     		sb.append(child.getAbsolutePath()+"\n");
     	}
     	// TODO : Piste pour sauvegarder les images après téléchargement
     	// Cf. http://stackoverflow.com/questions/19345576/cannot-draw-recycled-bitmaps-exception-with-picasso
     	// et surtout : http://www.basic4ppc.com/android/forum/threads/picasso-image-downloading-and-caching-library.31495/
    	// Fin test
    	
    	((TextView) findViewById(R.id.accueil_debug_text)).setText(sb.toString());
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	// Fin Debbug
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	
    	//End of user code
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu Accueil_CustomViewActivity
	//	menu.add(Menu.NONE, TELECHARGE_FICHE_MENU_ID, 1, R.string.telecharge_fiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, TELECHARGE_PHOTO_FICHES_MENU_ID, 2, R.string.telecharge_photofiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
    //    menu.add(Menu.NONE, VERIFIE_NOUVELLES_FICHES_MENU_ID, 4, R.string.verifie_nouvelles_fiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
    //    menu.add(Menu.NONE, RESET_DB_FROM_XML_MENU_ID, 5, R.string.reinitialise_a_partir_du_xml_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, APROPOS, 2, R.string.a_propos_label).setIcon(android.R.drawable.ic_menu_info_details);
		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
			case 777:
		            startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
		            return true;
		
		//Start of user code additional menu action Accueil_CustomViewActivity
		/*	case TELECHARGE_FICHE_MENU_ID:
				new TelechargeFiches_BgActivity(getApplicationContext(), this.getHelper()).execute("");
				break; */
			case TELECHARGE_PHOTO_FICHES_MENU_ID:
				TelechargePhotosFiches_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;		    	
				if(telechargePhotosFiches_BgActivity == null || telechargePhotosFiches_BgActivity.getStatus() != Status.RUNNING)
					DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity = 
						(TelechargePhotosFiches_BgActivity) new TelechargePhotosFiches_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
				break;
		/*	case VERIFIE_NOUVELLES_FICHES_MENU_ID:
				new VerifieNouvellesFiches_BgActivity(getApplicationContext(), this.getHelper()).execute("");
				break;
			case RESET_DB_FROM_XML_MENU_ID:
				reinitializeDBFromPrefetched();
				break; */
			case APROPOS:
				aPropos();				
				break;
		//End of user code
        }
        return false;
    }

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
	
	protected void addProgressBarZone(){
		   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - Début");  
		   
		   
		   
	}
	
	protected void addProgressBarView(LinearLayout inContainerLayout, String inTitre, String inSummary, int inIcone, int inAvancement){
	   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - Début");  	
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	   View convertView = inflater.inflate(R.layout.avancement, null);
	   
	   TextView tvTitleText = (TextView) convertView.findViewById(R.id.title);
	   tvTitleText.setText(inTitre);
	   
	   ImageView ivIcone = (ImageView) convertView.findViewById(R.id.icon);
	   ivIcone.setImageResource(inIcone);
		
	   TextView tvSummaryText = (TextView) convertView.findViewById(R.id.summary);
	   tvSummaryText.setText(inSummary);
		
	   ProgressBar pbProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
	   pbProgressBar.setProgress(inAvancement);
	   
	   // Changement couleur de la barre en fonction de l'avancement
	   int limite1 = Integer.parseInt(getContext().getString(R.string.avancement_progressbar_limite1) );
	   int limite2 = Integer.parseInt(getContext().getString(R.string.avancement_progressbar_limite2) );
	   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - limites : "+limite1+" - "+limite2);
	   
	   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - Couleur1 :"+getContext().getString(R.string.avancement_progressbar_couleur1));
	   
	   int couleur;
	   if (inAvancement <= limite1) {
		   	couleur = Color.parseColor( getContext().getString(R.string.avancement_progressbar_couleur1) );   
	   } else {
		   if (inAvancement <= limite2) {
			   couleur = Color.parseColor( getContext().getString(R.string.avancement_progressbar_couleur2) );
		   } else {
			   couleur = Color.parseColor( getContext().getString(R.string.avancement_progressbar_couleur3) );
		   }
	   }
	   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - couleur : "+couleur);
	   //pbProgressBar.getProgressDrawable().setColorFilter(couleur, Mode.MULTIPLY);
	   pbProgressBar.getProgressDrawable().setColorFilter(couleur, Mode.SRC_IN);
	   
	   inContainerLayout.addView(convertView);
	}
}
