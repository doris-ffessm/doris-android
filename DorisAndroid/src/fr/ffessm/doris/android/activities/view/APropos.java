package fr.ffessm.doris.android.activities.view;

import com.j256.ormlite.dao.CloseableIterator;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.tools.Outils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class APropos{
	
	private Context context;
	private Activity activity;
	private OrmLiteDBHelper dbHelper = null;
	
	
	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();
	
	public APropos(Context context, Activity activity, OrmLiteDBHelper dbHelper) {
		this.context = context;
		this.activity = activity;
		this.dbHelper = dbHelper;
	}
	
	public APropos(Context context) {
		this.context = context;
	}
 
	
	public void aProposAff() {
		StringBuilder texte = new StringBuilder();
		texte.append(context.getString(R.string.a_propos_txt_1));
		texte.append(context.getString(R.string.app_name));
		texte.append(System.getProperty("line.separator")); 
				
		texte.append(context.getString(R.string.a_propos_txt_2));
		texte.append(Outils.getAppVersion(context));
		texte.append(System.getProperty("line.separator")); 
		
		texte.append(System.getProperty("line.separator"));
		CloseableIterator<DorisDB_metadata> it = dbHelper.getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		texte.append(context.getString(R.string.a_propos_base_date) + it.next().getDateBase());
    		texte.append(System.getProperty("line.separator"));
		}
    	
		StringBuffer sizeFolderTexte =  new StringBuffer();
		if (Outils.getVignetteCount(context.getApplicationContext())!=0 ) {
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getVignetteCount(context.getApplicationContext()));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_vignettes));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getVignettesDiskUsage(context.getApplicationContext()) ) );
			sizeFolderTexte.append(System.getProperty("line.separator")); 
		}
		if (Outils.getMedResCount(context.getApplicationContext())!=0 ) {
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getMedResCount(context.getApplicationContext()));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_med_res));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getMedResDiskUsage(context.getApplicationContext()) ) );
			sizeFolderTexte.append(System.getProperty("line.separator")); 
		}
		if (Outils.getHiResCount(context.getApplicationContext())!=0 ) {
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getHiResCount(context.getApplicationContext()));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_hi_res));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getHiResDiskUsage(context.getApplicationContext()) ) );
			sizeFolderTexte.append(System.getProperty("line.separator")); 
		}
		if (sizeFolderTexte.length()!=0) {
			texte.append(context.getString(R.string.a_propos_foldersize_titre));
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

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);		
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.message_html,
    	                               (ViewGroup) activity.findViewById(R.id.layout_root));
    	
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
	    	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
	
	
}
