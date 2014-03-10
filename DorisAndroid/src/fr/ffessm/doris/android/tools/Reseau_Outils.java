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
package fr.ffessm.doris.android.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.activities.DetailEntreeGlossaire_ElementViewActivity;
import fr.ffessm.doris.android.activities.DetailsFiche_ElementViewActivity;
import fr.ffessm.doris.android.activities.DetailsParticipant_ElementViewActivity;
import fr.ffessm.doris.android.activities.Glossaire_ClassListViewActivity;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;

public class Reseau_Outils {
	private static final String LOG_TAG = Reseau_Outils.class.getCanonicalName();
	
	private Context context;
	
	public Reseau_Outils(Context context){
		this.context = context;
	}
	
	public enum ConnectionType {
	    AUCUNE, WIFI, GSM 
	}

	
	/* *********************************************************************
     * Type de connection : aucune, wifi, gsm 
     ********************************************************************** */		
	public ConnectionType getConnectionType() {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "connectionType() - isOnline : true");
	    	
	    	NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "connectionType() - mWifi.isConnected() : "+ mWifi.isConnected() );
	    	
	    	if (mWifi.isConnected() ) {
		    	return ConnectionType.WIFI;
	    	} else {
		        return ConnectionType.GSM;
	    	}
	    } else {
	    	return ConnectionType.AUCUNE;
	    }
	}
	
	
    
	/* *********************************************************************
	 * POUR L'INSTANT ICI, VOIR PLUS TARD POUR EN AVOIR UN COMMUN AVEC PREFECTCH SI POSSIBLE
	 * ISSU DE DORIS for ANDROID 1
     * getHtml permet de récupérer le fichier html à partir de l'URL
     * et de stocker le résultat dans un cache qui devrait permettre d'accélérer
     * la récup et consommer moins de bande passante
     ********************************************************************** */
	public String getHtml (String inUrl) throws IOException{
    	Log.d(LOG_TAG, "getHtml()- Début");
    	Log.d(LOG_TAG, "getHtml()- inUrl : " + inUrl);
    	
    	
    	if (inUrl.length()==0)
    	{	
    		Log.d(LOG_TAG, "getHtml()- problèmes sur les paramètres");
    		return "";
    	}
        
    	StringBuffer stringBuffer = new StringBuffer("");
    	BufferedReader bufferedReader = null;
    	
    	URL urlHtml = null;
    	try {
    		urlHtml = new URL(inUrl);
    	} catch (MalformedURLException e ) {
			Log.w(LOG_TAG, e.getMessage(), e);
		}
    	
    	HttpURLConnection urlConnection = null;
    	try {
			urlConnection = (HttpURLConnection) urlHtml.openConnection();
			//Log.d(LOG_TAG, "getHtml()- 010 : "+urlConnection.toString());
	        urlConnection.setConnectTimeout(3000);
	        urlConnection.setReadTimeout(10000);
	        urlConnection.connect();
    	} catch (IOException e ) {
			Log.w(LOG_TAG, e.getMessage(), e);
		}
    	
		try {
			InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
			//On vérifie que l'on est bien sur Doris (dans le cas ou l'on est re-dirigé vers Free, SFR, etc.
			if (!urlHtml.getHost().equals(urlConnection.getURL().getHost())) {
		    	String text = "Problème vraisemblable de redirection";
		    	Log.e(LOG_TAG, "getHtml() - " + text);
		    	Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
				toast.show();
				return "";
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
			}
    		//On lit ligne à ligne le bufferedReader pour le stocker dans le stringBuffer
    		String ligneCodeHTML = bufferedReader.readLine();
    		
    		int i = 0;
    		
    		while (ligneCodeHTML != null){
    			stringBuffer.append(ligneCodeHTML.trim());
    			i++;
    			//if (i % 100 == 0) Log.d(LOG_TAG, "getHtml() - "+i+" - ligneCodeHTML : "+ligneCodeHTML.trim().length());
    			
    			ligneCodeHTML = bufferedReader.readLine();
    		}
    		Log.d(LOG_TAG, "getHtml() - "+i);
            
		} catch(SocketTimeoutException erreur) {
			String text = "La Connexion semble trop lente";
        	Log.e(LOG_TAG, "getHtml() - " + text + " - " + erreur.toString());
        	return "";
        	
        } catch (Exception erreur) {
	    	String text = "Problème inconnu : "+erreur.toString();
	    	Log.e(LOG_TAG, "getHtml() - " + text);
			return "";
			
    	} finally {
    		urlConnection.disconnect();

    		//Dans tous les cas on ferme le bufferedReader s'il n'est pas null
    		if (bufferedReader != null){
    			try{
    				bufferedReader.close();
    			}catch(IOException e){
    	    		Log.e(LOG_TAG, "getHtml()" + e.getMessage());
    			}
    		}
    	}
    	
		Log.d(LOG_TAG, "getHtml() - length : "+stringBuffer.toString().length());
    	Log.d(LOG_TAG, "getHtml() - codeHtml : " +stringBuffer.toString().substring(0, Math.min(stringBuffer.toString().length(), 20)));
		Log.d(LOG_TAG, "getHtml() - Fin");
    	return stringBuffer.toString();
	}
    
    
}
