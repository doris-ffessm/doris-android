package fr.ffessm.doris.android.activities.view;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.activities.DetailsParticipant_ElementViewActivity;
import fr.ffessm.doris.android.activities.Preference_PreferenceViewActivity;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AffichageMessageHTML {
	
	private Context context;
	private Activity activity;
	private OrmLiteDBHelper dbHelper = null;
	
	
	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();
	
	Param_Outils paramOutils = new Param_Outils(context.getApplicationContext());
	
	public AffichageMessageHTML(Context context, Activity activity, OrmLiteDBHelper dbHelper) {
		this.context = context;
		this.activity = activity;
		this.dbHelper = dbHelper;
	}
	
	public AffichageMessageHTML(Context context) {
		this.context = context;
	}

	/* *********************************************************************
     * fonction permettant d'afficher des pages web locales comme l'Apropos par exemple
     ********************************************************************** */
	public void affichageMessageHTML(String inTitre, String inTexte, String inURL) {
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Début");
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - inTitre : " + inTitre);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - inTexte : " + inTexte);
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - inURL : " + inURL);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		AlertDialog alertDialog = alertDialogBuilder.create();
		
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		
    	View layout = inflater.inflate(R.layout.apropos_aide,
    	                               (ViewGroup) activity.findViewById(R.id.layout_root));
    	
    	alertDialog.setTitle(inTitre);
    	
    	TextView text = (TextView) layout.findViewById(R.id.text);
    	if (! inTexte.isEmpty()) {
	       	text.setText(inTexte);
    	} else {
    		text.setVisibility(View.GONE);
    	}
    	
    	WebView pageWeb = (WebView) layout.findViewById(R.id.webView);
    	pageWeb.setWebViewClient(new WebViewClient() {  
    	    @Override  
    	    public boolean shouldOverrideUrlLoading(WebView inView, String inUrl)  
    	    {  
    	    	
    	    	if (inUrl.startsWith("http")){
	    	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Lancement navigateur Android Défaut");
	    	    	
	    	    	Intent intent = new Intent(Intent.ACTION_VIEW);
	    	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setData(Uri.parse(inUrl));
					context.startActivity(intent);
	
					return true;
    	    	} else if (inUrl.startsWith("participant")){
    	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Affichage Participant : "+inUrl.replace("participant://", ""));
	
    	    		Intent toParticipantView = new Intent(context, DetailsParticipant_ElementViewActivity.class);
    	    		
	    	        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
	                RuntimeExceptionDao<Participant, Integer> entriesDao = ormLiteDBHelper.getParticipantDao();
	    	        
    	    		Bundle b = new Bundle();
	    	        b.putInt("participantId", entriesDao.queryForEq("numeroParticipant", Integer.valueOf( inUrl.replace("participant://", "") ) ).get(0).getId() );
	    	        
	    	        toParticipantView.putExtras(b);
	    			context.startActivity(toParticipantView);
	    			
    	    		return true;
    	    	} else if (inUrl.startsWith("preference")){
    	    		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Affichage preference : "+inUrl.replace("preference://", ""));
	
    	    		Intent toPrefView = new Intent(context, Preference_PreferenceViewActivity.class);
 					
    	    		String[] pref = inUrl.replace("preference://", "").split("/");
    	    		
    	    		toPrefView.putExtra("type_parametre", pref[0]);
    	    		toPrefView.putExtra("parametre", pref[1]);
    	    		
    	    		Bundle b = new Bundle();
    	    		toPrefView.putExtras(b);
	    			context.startActivity(toPrefView);
	    			
    	    		return true;
    	    	} else {
    	    		return true;
    	    	}
    	    }  
    	});  
    	
    	pageWeb.loadUrl(inURL);
    	alertDialog.setView(layout);

    	alertDialog.show();

    	alertDialog.getWindow().setLayout(ScreenTools.getScreenWidth(activity) - 20, ScreenTools.getScreenHeight(activity) - 20);
    	
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "affichageMessageHTML() - Fin");
	}
	
	
	public String aProposAff() {
		StringBuilder texte = new StringBuilder();
		Outils outils = new Outils(context);
		
		texte.append(context.getString(R.string.a_propos_txt));
		texte.append(outils.getAppVersion());
		
		CloseableIterator<DorisDB_metadata> it = dbHelper.getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		texte.append(System.getProperty("line.separator"));
    		texte.append(context.getString(R.string.a_propos_base_date) + it.next().getDateBase());
		}
    	
		StringBuffer sizeFolderTexte =  new StringBuffer();
		Disque_Outils disqueOutils = new Disque_Outils(context.getApplicationContext());
		
		if ( paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_vignettes, 0) !=0 ) {
			sizeFolderTexte.append(System.getProperty("line.separator")); 
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_vignettes, 0));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_vignettes));
			sizeFolderTexte.append(disqueOutils.getHumanDiskUsage(paramOutils.getParamLong(R.string.pref_key_size_folder_vignettes, 0L ) ) );
		}
		if ( paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_med_res, 0) !=0 ) {
			sizeFolderTexte.append(System.getProperty("line.separator")); 
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_med_res, 0));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_med_res));
			sizeFolderTexte.append(disqueOutils.getHumanDiskUsage(paramOutils.getParamLong(R.string.pref_key_size_folder_med_res, 0L ) ) );
		}
		if ( paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_hi_res, 0) !=0 ) {
			sizeFolderTexte.append(System.getProperty("line.separator")); 
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_hi_res, 0));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_hi_res));
			sizeFolderTexte.append(disqueOutils.getHumanDiskUsage(paramOutils.getParamLong(R.string.pref_key_size_folder_hi_res, 0L ) ) );
		}
		
		if (sizeFolderTexte.length()!=0) {
			texte.append(System.getProperty("line.separator"));
			texte.append(context.getString(R.string.a_propos_foldersize_titre));
			texte.append(sizeFolderTexte);
		}
			
		return texte.toString();
	}
}
