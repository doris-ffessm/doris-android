package fr.ffessm.doris.android.activities.view;

import com.j256.ormlite.dao.CloseableIterator;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Outils.ImageType;
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
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AffichageMessageHTML {
	
	private Context context;
	private Activity activity;
	private OrmLiteDBHelper dbHelper = null;
	
	
	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();
	
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
		texte.append(context.getString(R.string.a_propos_txt));
		texte.append(Outils.getAppVersion(context));
		
		CloseableIterator<DorisDB_metadata> it = dbHelper.getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		texte.append(System.getProperty("line.separator"));
    		texte.append(context.getString(R.string.a_propos_base_date) + it.next().getDateBase());
		}
    	
		StringBuffer sizeFolderTexte =  new StringBuffer();
		if (Outils.getImageCount(context.getApplicationContext(), ImageType.VIGNETTE)!=0 ) {
			sizeFolderTexte.append(System.getProperty("line.separator")); 
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getImageCount(context.getApplicationContext(), ImageType.VIGNETTE));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_vignettes));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getPhotoDiskUsage(context, ImageType.VIGNETTE) ) );
		}
		if (Outils.getImageCount(context.getApplicationContext(), ImageType.MED_RES)!=0 ) {
			sizeFolderTexte.append(System.getProperty("line.separator"));
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getImageCount(context.getApplicationContext(), ImageType.MED_RES));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_med_res));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getPhotoDiskUsage(context, ImageType.MED_RES) ) );
		}
		if (Outils.getImageCount(context.getApplicationContext(), ImageType.HI_RES)!=0 ) {
			sizeFolderTexte.append(System.getProperty("line.separator"));
			sizeFolderTexte.append("\t");
			sizeFolderTexte.append(Outils.getImageCount(context.getApplicationContext(), ImageType.HI_RES));
			sizeFolderTexte.append(context.getString(R.string.a_propos_foldersize_hi_res));
			sizeFolderTexte.append(Outils.getHumanDiskUsage(Outils.getPhotoDiskUsage(context, ImageType.HI_RES) ) );
		}
		if (sizeFolderTexte.length()!=0) {
			texte.append(System.getProperty("line.separator"));
			texte.append(context.getString(R.string.a_propos_foldersize_titre));
			texte.append(sizeFolderTexte);
		}
			
		return texte.toString();
	}
}
