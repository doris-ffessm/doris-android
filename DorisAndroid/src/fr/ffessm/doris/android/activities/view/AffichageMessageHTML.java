/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
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
	
	private final Param_Outils paramOutils;
	private final Disque_Outils disqueOutils;
	private final Photos_Outils photosOutils;
	
	public AffichageMessageHTML(Context context, Activity activity, OrmLiteDBHelper dbHelper) {
		this.context = context;
		this.activity = activity;
		this.dbHelper = dbHelper;
		
		paramOutils = new Param_Outils(context);
		disqueOutils = new Disque_Outils(context);
		photosOutils = new Photos_Outils(context);
	}
	
	public AffichageMessageHTML(Context context) {
		this.context = context;
		
		paramOutils = new Param_Outils(context);
		disqueOutils = new Disque_Outils(context);
		photosOutils = new Photos_Outils(context);
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
		
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
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
	    	        ormLiteDBHelper.getParticipantDao().clearObjectCache();
	    	        
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
		App_Outils outils = new App_Outils(context);
		
		texte.append(context.getString(R.string.a_propos_txt));
		texte.append(outils.getAppVersion());
		
		CloseableIterator<DorisDB_metadata> it = dbHelper.getDorisDB_metadataDao().iterator();
    	while (it.hasNext()) {
    		texte.append(System.getProperty("line.separator"));
    		texte.append(context.getString(R.string.a_propos_base_date) + it.next().getDateBase());
		}
			
		return texte.toString();
	}
}
