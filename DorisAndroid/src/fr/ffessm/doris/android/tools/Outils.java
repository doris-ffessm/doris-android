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

public class Outils {
	private static final String LOG_TAG = Outils.class.getCanonicalName();
	
	public enum ConnectionType {
	    AUCUNE, WIFI, GSM 
	}


	public static long getDiskUsage(Context inContext, File inImageFolder){
		DiskUsage du = new DiskUsage();
    	du.accept(inImageFolder);
    	return du.getSize();
	}
	public static String getHumanDiskUsage(long inSize){
		String sizeTexte = "";
		// octet => ko
		inSize = inSize/1024;
        if ( inSize < 1024 ) {
        	sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Ko";
        } else {
        	inSize = inSize / 1024;
        	if ( inSize < 1024 ) {
        		sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Mo";
        	} else {
        		inSize = inSize / 1024;
        		sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Go";
        	}
        }
    	return sizeTexte;
	}

	
	/* *********************************************************************
     * Type de connection : aucune, wifi, gsm 
     ********************************************************************** */		
	public static ConnectionType getConnectionType(Context context) {
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
	
	/* Lecture Paramètres */
	public static String getStringKeyParam(Context inContext, int inParam) {
		return inContext.getResources().getResourceEntryName(inParam);
	}
	public static String getStringNameParam(Context inContext, int inParam) {
		return inContext.getString(inParam);
	}
	public static boolean getParamBoolean(Context inContext, int inParam, boolean inValDef) {
		return PreferenceManager.getDefaultSharedPreferences(inContext).getBoolean(inContext.getString(inParam), inValDef);
	}
	public static String getParamString(Context inContext, int inParam, String inValDef) {
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamString() - param : " + inParam + "-" + inContext.getString(inParam) );
		return PreferenceManager.getDefaultSharedPreferences(inContext).getString(inContext.getString(inParam), inValDef);
	}
	public static long getParamLong(Context inContext, int inParam, Long inValDef) {
		if (PreferenceManager.getDefaultSharedPreferences(inContext).contains(inContext.getString(inParam)) ) {
			return PreferenceManager.getDefaultSharedPreferences(inContext).getLong(inContext.getString(inParam), inValDef);
		} else {
			return inValDef;
		}
	}
	public static int getParamInt(Context inContext, int inParam, int inValDef) {
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamInt() - param : " + inParam );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamInt() - param : " + inContext.getString(inParam) );
		return PreferenceManager.getDefaultSharedPreferences(inContext).getInt(inContext.getString(inParam), inValDef);
	}
	/* Enregistrement paramètres */
	public static void setParamString(Context inContext, int inParam, String inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();
	    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - param : " + inContext.getString(inParam) );
	    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - getStringKeyParam : " + Outils.getStringKeyParam(inContext,inParam) );
	    prefEdit.putString(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}
	public static void setParamInt(Context inContext, int inParam, int inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putInt(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}
	public static void setParamBoolean(Context inContext, int inParam, Boolean inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putBoolean(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}
	
	public static void setParamLong(Context inContext, int inParam, long inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(inContext);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putLong(inContext.getString(inParam), inVal);
		prefEdit.commit();
	}	

	
	public static String getAppVersion(Context inContext) {
		try	{
        	PackageManager pm = inContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo( inContext.getPackageName(), 0);
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getAppVersion() - appVersionName : "+pi.versionName);
            return pi.versionName;
     	} catch(Exception e) {
    		if (BuildConfig.DEBUG) Log.e(LOG_TAG, "getAppVersion() - erreur : ");
    		e.printStackTrace();
    		return "";
    	}
	}
	

	
    public static int clearFolder(File inFolder, int inNbJours){
		int deletedFiles = 0;
	    if (inFolder!= null && inFolder.isDirectory()) {
	    	Log.d(LOG_TAG, "clearFolder() - inFolder : "+inFolder);
	        try {
	            for (File child:inFolder.listFiles()) {

	                //first delete subdirectories recursively
	                if (child.isDirectory()) {
	                    deletedFiles += clearFolder(child, inNbJours);
	                }

	                //then delete the files and subdirectories in this dir
	                //only empty directories can be deleted, so subdirs have been done first
	                if (child.lastModified() < new Date().getTime() - inNbJours * DateUtils.DAY_IN_MILLIS) {
	                    if (child.delete()) {
	                        deletedFiles++;
	                    }
	                }
	            }
	        }
	        catch(Exception e) {
	        	Log.e(LOG_TAG, String.format("Failed to clean the folder, error %s", e.getMessage()));
	        }
	    }
	    Log.d(LOG_TAG, "clearFolder() - Fichiers effacés : "+deletedFiles);
	    return deletedFiles;
	}
	
    // TODO : En attendant d'obtenir la nouvelle version de Common
	public static String getZoneIcone(Context inContext, int inId) {
	   	switch (inId) {
	   	case -1:
    		return inContext.getString(R.string.icone_touteszones);
    	case 1:
    		return inContext.getString(R.string.icone_france);
		case 2:
			return inContext.getString(R.string.icone_eaudouce);
		case 3:
			return inContext.getString(R.string.icone_indopac);
		case 4:
			return inContext.getString(R.string.icone_caraibes);
		case 5:
			return inContext.getString(R.string.icone_atlantno);
		default:
			return "";
		}
	}
	
	

    public final static SpannableString textToSpannableStringDoris(final Context context, CharSequence texte) {
	    //Log.d(LOG_TAG, "textToSpannableStringDoris() - texte : "+texte);
	    
	    SpannableString richtext = new SpannableString("");
	    
	    if ( !texte.toString().replaceAll("\\s", "").matches(".*\\{\\{[^\\}]*\\}\\}.*")) {
	    	//Log.d(LOG_TAG, "textToSpannableStringDoris() - Aucun bloc {{*}}");
	    	return new SpannableString(texte);
	    	
	    } else {
	    	//Log.d(LOG_TAG, "textToSpannableStringDoris() - Traitement récurrent des blocs {{*}}");
	    	
	    	// TODO : doit être améliorable mais je n'arrive pas à manipuler directement SpannableString
	    	// donc pas de concat, pas de regexp.
	        List<TextSpan> listeFicheNumero = new ArrayList<TextSpan>();
	        List<TextSpan> pileDerniereBalise = new ArrayList<TextSpan>();
	        
	        String texteInter = texte.toString();
	        StringBuilder texteFinal = new StringBuilder();
	        int iTmp = 0;
	        while (texteInter.contains("{{") && iTmp < 100 ) {
	        	iTmp ++;
	        	
	        	// Recherche 1ère Balise à traiter
	        	int posDepTexteInter = texteInter.indexOf("{{");
	        	int posFinTexteInter = texteInter.indexOf("}}");
	        	
	        	String balise = texteInter.substring(posDepTexteInter+2, posFinTexteInter);
	        	
	        	Log.d(LOG_TAG, "textToSpannableStringDoris() - texteInter : "+texteInter
	        			+ " - " + posDepTexteInter + "-" + posFinTexteInter + " -> " + balise);
	        	
	        	if (balise.equals("i")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.ITALIQUE,posDepTexteFinal,0));
	        	}
	        	else if (balise.equals("/i")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.ITALIQUE,ts.positionDebut,posFinTexteFinal));
	        	}
	        	else if (balise.equals("g")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.GRAS,posDepTexteFinal,0));
	        	}
	        	else if (balise.equals("/g")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.GRAS,ts.positionDebut,posFinTexteFinal));
	        	}
	        	else if (balise.equals("s")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.SOULIGNE,posDepTexteFinal,0));
	        	}
	        	else if (balise.equals("/s")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.SOULIGNE,ts.positionDebut,posFinTexteFinal));
	        	}
	        	else if (balise.equals("n/")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) + "\n");
	        			        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	}
	        	else if (balise.startsWith("F:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.FICHE,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/F")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.FICHE,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	else if (balise.startsWith("D:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.DEFINITION,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/D")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.DEFINITION,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	else if (balise.startsWith("E:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) + balise);
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.ILLUSTRATION_DEFINITION,posDepTexteFinal,posFinTexteFinal,
	        				balise.substring(2, balise.length()-1)));
	        	}
	        	else if (balise.startsWith("A:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.LIENWEB,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/A")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.LIENWEB,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	else if (balise.startsWith("P:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	
	        		pileDerniereBalise.add(new TextSpan(TextSpan.SpanType.PARTICIPANT,posDepTexteFinal,0,
	        				balise.substring(2, balise.length())));
	        	}
	        	else if (balise.equals("/P")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		TextSpan ts = pileDerniereBalise.get(pileDerniereBalise.size()-1);
	        		pileDerniereBalise.remove(pileDerniereBalise.size()-1);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.PARTICIPANT,ts.positionDebut,posFinTexteFinal,
	        				ts.info));
	        	}
	        	
	        } // fin du While
	        
	        texteFinal.append(texteInter);
	        //Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal après while : "+texteFinal);
	        //Log.d(LOG_TAG, "textToSpannableStringDoris() - longueur : "+texteFinal.length());
	        
	        richtext = new SpannableString(texteFinal);
	        
	        for (final TextSpan ts : listeFicheNumero) {
	        	//Log.d(LOG_TAG, "textToSpannableStringDoris() - ts : "+ts.spanType.name()+" - "+ts.info);
	        	
	        	if ( ts.spanType == TextSpan.SpanType.ITALIQUE ) {
	        		richtext.setSpan(new StyleSpan(Typeface.ITALIC), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.GRAS ) {
	        		richtext.setSpan(new StyleSpan(Typeface.BOLD), ts.positionDebut, ts.positionFin, 0);
	        		if ( !context.getString(R.string.detailsfiche_elementview_couleur_gras).isEmpty() ){
	        			richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_gras))), ts.positionDebut, ts.positionFin, 0);
	        		}
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.SOULIGNE ) {
	        		richtext.setSpan(new UnderlineSpan(), ts.positionDebut, ts.positionFin, 0);
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.FICHE ) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  
			            	Intent toDetailView = new Intent(context, DetailsFiche_ElementViewActivity.class);
			                Bundle bundle = new Bundle();
			                bundle.putInt("ficheNumero", Integer.valueOf(ts.info) );
			                
			                OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
			                RuntimeExceptionDao<Fiche, Integer> entriesDao = ormLiteDBHelper.getFicheDao();
			                bundle.putInt("ficheId", entriesDao.queryForEq("numeroFiche", Integer.valueOf(ts.info)).get(0).getId() );
			                
			        		toDetailView.putExtras(bundle);
			        		context.startActivity(toDetailView);
			            }  
			        };
			     	//Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_lienfiche))), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.DEFINITION) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  

			                Bundle bundle = new Bundle();
			                //bundle.putInt("ficheNumero", Integer.valueOf(ts.info) );
			                
			                //Parfois les mots sont au pluriel, on enlève donc ici un éventuel s final
			                String terme = ts.info.replaceAll("s$", "");
			                
			                OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(context);
			                RuntimeExceptionDao<DefinitionGlossaire, Integer> entriesDao = ormLiteDBHelper.getDefinitionGlossaireDao();
			                List<DefinitionGlossaire> listeDefinitions = new ArrayList<DefinitionGlossaire>();
			                int idDefinition = 0;
			                try {
			                	//Commence par le terme au singulier
			                	listeDefinitions = entriesDao.query(
										entriesDao.queryBuilder().where().like("terme", terme+"%").prepare() );
								if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
								else {
									//#Commence par# le terme au masculin singulier
									String termeTmp1 = terme;
									termeTmp1 = termeTmp1.replaceAll("elle$", "el");
									termeTmp1 = termeTmp1.replaceAll("ienne$", "ien");
									termeTmp1 = termeTmp1.replaceAll("euse$", "eur");
									termeTmp1 = termeTmp1.replaceAll("e$", "");
									listeDefinitions = entriesDao.query(
											entriesDao.queryBuilder().where().like("terme", termeTmp1+"%").prepare() );
									if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
									else {
										//#Commence par# le terme au masculin singulier ...al => ...aux
										String termeTmp2 = terme;
										termeTmp2 = termeTmp2.replaceAll("aux$", "al");
										termeTmp2 = termeTmp2.replaceAll("eaux$", "eau");
										termeTmp2 = termeTmp2.replaceAll("ale$", "al");
										termeTmp2 = termeTmp2.replaceAll("ive$", "if");
										listeDefinitions = entriesDao.query(
												entriesDao.queryBuilder().where().like("terme", termeTmp2+"%").prepare() );
										if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
										else {
											//#Contient# le terme au singulier
											listeDefinitions = entriesDao.query(
													entriesDao.queryBuilder().where().like("terme", "%"+terme+"%").prepare() );
											if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
											else {
												//#Contient# le terme au masculin singulier
												listeDefinitions = entriesDao.query(
														entriesDao.queryBuilder().where().like("terme", "%"+termeTmp1+"%").prepare() );
												if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
												else {
													//#Contient# le terme au masculin singulier
													listeDefinitions = entriesDao.query(
															entriesDao.queryBuilder().where().like("terme", "%"+termeTmp2+"%").prepare() );
													if(!listeDefinitions.isEmpty()) idDefinition = listeDefinitions.get(0).getId();
													else {
														//le É par exemple ne fonctionne pas avec LIKE dans SQLite
														// Bug connu : http://www.sqlite.org/lang_expr.html#like
														listeDefinitions = entriesDao.queryForAll();
														String texteRecherche = terme.replaceAll("e$", "").replaceAll("ux$", "").toLowerCase();
														for (DefinitionGlossaire definition : listeDefinitions){
															if (definition.getTerme().toString().toLowerCase().contains(texteRecherche)) {
																idDefinition = definition.getId();
																break;
															}
														}
													}
												}
											}
										}
									}
								}
								
							} catch (SQLException e) {
								e.printStackTrace();
							}

			                
			                if(idDefinition != 0){
		                    	
		                    	bundle.putInt("definitionGlossaireId", idDefinition );
		                    	
				            	Intent toDefinitionlView = new Intent(context, DetailEntreeGlossaire_ElementViewActivity.class);
		                    	toDefinitionlView.putExtras(bundle);
		                    	context.startActivity(toDefinitionlView);
		                    } else {
		                    	Intent toDefinitionlView = new Intent(context, Glossaire_ClassListViewActivity.class);
		                    	context.startActivity(toDefinitionlView);
		                    }

			            }  
			        };
			     	//Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_liendefinition))), ts.positionDebut, ts.positionFin, 0);
	        	} // Fin else DEFINITION
	        	
	        	else if ( ts.spanType == TextSpan.SpanType.ILLUSTRATION_DEFINITION) {
	    	        //Pour jour mettre des images directement dans le texte : la picto dangerosité par exemple.
	    	        String nomPhoto = Constants.PREFIX_IMGDSK_DEFINITION+ts.info;
	    	        Log.d(LOG_TAG, "textToSpannableStringDoris()- nomPhoto : "+nomPhoto);

	    	        Drawable drawable = new BitmapDrawable();
	    	        ImageSpan imageSpan = null;
	    	        Photos_Outils photosOutils = new Photos_Outils(context);
	    	        
	    	        if(photosOutils.isAvailablePhoto(nomPhoto, Photos_Outils.ImageType.ILLUSTRATION_DEFINITION)){
	    	        	Log.d(LOG_TAG, "textToSpannableStringDoris()- isAvailablePhoto");
	    	        	try {
	    	        		String path = photosOutils.getPhotoFile(nomPhoto, Photos_Outils.ImageType.ILLUSTRATION_DEFINITION).getAbsolutePath();
	    	        		Log.d(LOG_TAG, "textToSpannableStringDoris()- path : "+path);

	    	        		drawable = Drawable.createFromPath(path);
							drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()); 
							imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
	    	        	} catch (IOException e) {
							e.printStackTrace();
						}

	    	        }  else {
	    	        	Log.d(LOG_TAG, "textToSpannableStringDoris()- ! isAvailablePhoto");
	    	        	imageSpan = new ImageSpan(context, R.drawable.app_glossaire_indisponible );
	    	        	
	    				ClickableSpan clickableSpan = new ClickableSpan() {  
				            @Override  
				            public void onClick(View view) {
				            	
			    	        	AffichageMessageHTML aide = new AffichageMessageHTML(context, (Activity) context, new OrmLiteDBHelper(context));
			    				aide.affichageMessageHTML(context.getString(R.string.aide_label), "", "file:///android_res/raw/aide.html#ImagesIndisponibles");

				            }  
				        };
				        richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
	    	        }

	    	        Log.d(LOG_TAG, "textToSpannableStringDoris()- richtext : "+richtext.length());
	    	        Log.d(LOG_TAG, "textToSpannableStringDoris()- ts.positionDebut : "+ts.positionDebut);
	    	        richtext.setSpan(imageSpan, ts.positionDebut, ts.positionFin, 0);

	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.LIENWEB) {
	        		String url = ts.info;
	        		if (!url.contains("http")) url = "http://"+url;
	        		richtext.setSpan(new URLSpan(url), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.PARTICIPANT ) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  
			    	        Intent toDetailView = new Intent(context, DetailsParticipant_ElementViewActivity.class);
			    	        
			    	        Bundle b = new Bundle();
			    	        b.putInt("participantId", Integer.valueOf(ts.info) );
			    			
			    	        toDetailView.putExtras(b);
			    			context.startActivity(toDetailView);
			            }  
			        };
			     	//Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_lienparticipant))), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        }

	        
	        return richtext;
	    }

    }
	

    public static class TextSpan {
    	
    	public enum SpanType {
    	    FICHE, ITALIQUE, GRAS, SOULIGNE, SAUTDELIGNE,
    	    DEFINITION, ILLUSTRATION_DEFINITION,
    	    LIENWEB, PARTICIPANT 
    	} 
    	
    	SpanType spanType = null;
    	int positionDebut;
    	int positionFin;
    	String info = ""; 
    	
    	public TextSpan(SpanType spanType, int positionDebut, int positionFin) {
    		this.spanType = spanType;
    		this.positionDebut = positionDebut;
    		this.positionFin = positionFin;
    	}
    	
    	public TextSpan(SpanType spanType, int positionDebut, int positionFin, String info) {
    		this.spanType = spanType;
    		this.positionDebut = positionDebut;
    		this.positionFin = positionFin;
    		this.info = info;
    	}
    }
    
    
	/* *********************************************************************
	 * POUR L'INSTANT ICI, VOIR PLUS TARD POUR EN AVOIR UN COMMUN AVEC PREFECTCH SI POSSIBLE
	 * ISSU DE DORIS for ANDROID 1
     * getHtml permet de récupérer le fichier html à partir de l'URL
     * et de stocker le résultat dans un cache qui devrait permettre d'accélérer
     * la récup et consommer moins de bande passante
     ********************************************************************** */
	public static String getHtml (Context inContext, String inUrl, String inCleFichier) throws IOException{
    	Log.d(LOG_TAG, "getHtml()- Début");
    	Log.d(LOG_TAG, "getHtml()- inUrl : " + inUrl);
    	Log.d(LOG_TAG, "getHtml()- inCleFichier : " + inCleFichier);
    	
    	//Pour le travail de debbug
    	//if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }
    	
    	
    	if (inUrl.length()==0 || inCleFichier.length()==0)
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
		    	Toast toast = Toast.makeText(inContext, text, Toast.LENGTH_LONG);
				toast.show();
				return "";
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
			}
    		//On lit ligne à ligne le bufferedReader pour le stocker dans le stringBuffer
    		String ligneCodeHTML = bufferedReader.readLine();
    		FileOutputStream fos = null;
    		fos = new FileOutputStream(new File(inContext.getCacheDir(), inCleFichier));
    		
    		int i = 0;
    		
    		while (ligneCodeHTML != null){
    			fos.write(ligneCodeHTML.trim().getBytes());
    			
    			i++;
    			//if (i % 100 == 0) Log.d(LOG_TAG, "getHtml() - "+i+" - ligneCodeHTML : "+ligneCodeHTML.trim().length());
    			
    			ligneCodeHTML = bufferedReader.readLine();
    		}
    		Log.d(LOG_TAG, "getHtml() - "+i);
			
    		fos.flush();
            fos.close();
            
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
