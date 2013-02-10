/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
Copyright du Code : Guillaume Moynard  ([29/05/2011]) 

Guillaume Moynard : gmo7942@gmail.com

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
* ********************************************************************
* ********************************************************************* */

package fr.ffessm.doris;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

/* *********************************************************************
 * Outils
 ********************************************************************** */
public class Outils {
    private final static String TAG = "Outils";
    private final static Boolean LOG = true;

    private final static String PREFS_NAME = "DorisParam";
    static SharedPreferences preferences;
    
	/* *********************************************************************
     * getHtml permet de récupérer le fichier html à partir de l'URL
     * et de stocker le résultat dans un cache qui devrait permettre d'accélérer
     * la récup et consommer moins de bande passante
     ********************************************************************** */
	public static String getHtml (Context inContext, String inUrl, String inCleFichier) throws IOException{
    	if (LOG) Log.d(TAG, "getHtml()- Début");
    	if (LOG) Log.d(TAG, "getHtml()- inUrl : " + inUrl);
    	if (LOG) Log.d(TAG, "getHtml()- inCleFichier : " + inCleFichier);

    	if (inUrl.length()==0 || inCleFichier.length()==0)
    	{	
    		if (LOG) Log.d(TAG, "getHtml()- problèmes sur les paramètres");
    		return "";
    	}

        preferences = inContext.getSharedPreferences(PREFS_NAME, 0);
        int type_cache = preferences.getInt("type_cache", 2);
        if (LOG) Log.d(TAG, "getHtml()- type_cache : "+type_cache);
        
    	if(new File(inContext.getCacheDir(), inCleFichier).exists()){
    		if (LOG) Log.d(TAG, "getHtml()- fichier : "+inCleFichier+" trouvé dans le cache");
    		
            // Durée cache en millisecondes
            long dernierModif = new File(inContext.getCacheDir(), inCleFichier).lastModified() - System.currentTimeMillis();
            // Durée en jour
            dernierModif = dernierModif / ( DateUtils.DAY_IN_MILLIS );
            if (LOG) Log.d(TAG, "getHtml()- dernierModif : "+dernierModif);
            
            if( (type_cache == 1 && dernierModif < 1)
            	|| (type_cache == 2 && dernierModif < 7)	
            	|| (type_cache == 3 && dernierModif < 30)
            	|| (type_cache == 4 ))
            {
	    		if (LOG) Log.d(TAG, "getHtml()- recup dans le cache");
	    		
	    		FileInputStream objFile = new FileInputStream(new File(inContext.getCacheDir(), inCleFichier).getAbsoluteFile());
	    		InputStreamReader objReader = new InputStreamReader(objFile);
	    		BufferedReader objBufferReader = new BufferedReader(objReader);
	    		StringBuffer objBuffer = new StringBuffer();
	    		String strLine;
	    		while ((strLine = objBufferReader.readLine()) != null) {
	    			objBuffer.append(strLine);
	    			objBuffer.append("\n");
	    		}
	    		objFile.close();
	    		if (LOG) Log.d(TAG, "getHtml() - codeHtml : " +objBuffer.toString().substring(0, Math.min(objBuffer.toString().length(), 20)));
	    		if (LOG) Log.d(TAG, "getHtml() - Fin");
	    		return objBuffer.toString();
            }
    	}
    	
    	if (LOG) Log.d(TAG, "getHtml()- sur Internet");
    	StringBuffer stringBuffer = new StringBuffer("");
    	BufferedReader bufferedReader = null;
    
		URL urlHtml = new URL(inUrl);
		HttpURLConnection urlConnection = (HttpURLConnection) urlHtml.openConnection();
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(10000);
        
		try {
			InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
			
			//On vérifie que l'on est bien sur Doris (dans le cas ou l'on est redirigé vers Free, SFR, etc.
			if (!urlHtml.getHost().equals(urlConnection.getURL().getHost())) {
		    	String text = "Problème vraisemblable de redirection";
		    	Log.e(TAG, "getHtml() - " + text);
		    	Toast toast = Toast.makeText(inContext, text, Toast.LENGTH_LONG);
				toast.show();
				return "";
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));

			}

    		//On lit ligne à ligne le bufferedReader pour le stocker dans le stringBuffer
    		String ligneCodeHTML = bufferedReader.readLine();
    		while (ligneCodeHTML != null){
    			stringBuffer.append(ligneCodeHTML);
    			stringBuffer.append("\n");
    			ligneCodeHTML = bufferedReader.readLine();
    		}
		}
		catch(SocketTimeoutException erreur) {
	        	Log.e(TAG, "getHtml() - " + inContext.getString(R.string.txt_connectionLente) + " - " + erreur.toString(), erreur);
	        	Toast toast = Toast.makeText(inContext, inContext.getString(R.string.txt_connectionLente), Toast.LENGTH_LONG);
				toast.show();
        }
		catch (Exception e){
    		Log.e(TAG, e.getMessage());
	    	String text = "Problème inconnu : "+e.getMessage();
	    	Log.e(TAG, "getHtml() - " + text);
	    	Toast toast = Toast.makeText(inContext, text, Toast.LENGTH_LONG);
			toast.show();
			return "";
    	}finally{
    		urlConnection.disconnect();

    		//Dans tous les cas on ferme le bufferedReader s'il n'est pas null
    		if (bufferedReader != null){
    			try{
    				bufferedReader.close();
    			}catch(IOException e){
    	    		Log.e(TAG, "getHtml()" + e.getMessage());
    			}
    		}
    	}
    	
    	if (type_cache != 0){
	        FileOutputStream fos = null;
	        try {
	            fos = new FileOutputStream(new File(inContext.getCacheDir(), inCleFichier));
	            fos.write(stringBuffer.toString().getBytes());
	            fos.flush();
	            fos.close();
	        }
	        //this should never happen
	        catch(FileNotFoundException e) {
	        	Log.e(TAG, e.toString(), e);
	        }
    	}
    	
        if (LOG) Log.d(TAG, "getHtml() - codeHtml : " +stringBuffer.toString().substring(0, Math.min(stringBuffer.toString().length(), 20)));
		if (LOG) Log.d(TAG, "getHtml() - Fin");
    	return stringBuffer.toString();
	}
	
	
	/* *********************************************************************
     * ciblePage permet de supprimer tout le superflu de la page HTML
     ********************************************************************* */
    public static String ciblePage(String inCodeHtml, String inTypePage) throws IOException{
    	if (LOG) Log.d(TAG, "ciblePage() - Début");
    	if (LOG) Log.d(TAG, "ciblePage() - codeHtml : " + inCodeHtml.substring(0, Math.min(50, inCodeHtml.length())));
    	if (LOG) Log.d(TAG, "ciblePage() - inTypePage : " + inTypePage);
    			
    	String pageANettoyer = inCodeHtml;
    	String typePage = inTypePage;
    	
    	// Suppression des sauts de lignes
    	pageANettoyer.replaceAll("/r/n","");
    	if (LOG) Log.v(TAG, "ciblePage() - pageANettoyer 10 : " + pageANettoyer.substring(0, Math.min(100, pageANettoyer.length())));
    	
    	// Suppression des espaces inutiles (entre les ><)
    	pageANettoyer.replaceAll(">\\s*<","><");
    	if (LOG) Log.v(TAG, "ciblePage() - pageANettoyer 20 : " + pageANettoyer.substring(0, Math.min(100, pageANettoyer.length())));
    	
		Source source=new Source(pageANettoyer);
		source.fullSequentialParse();
		Element tableResultats = null;
		
		if (typePage == "RESULTATS" || typePage == "FICHE"){
			// Récupération de la Table des Résultats
			List<? extends Element> listeElementsTable=source.getAllElements(HTMLElementName.TABLE);
			for (Element elementTable : listeElementsTable) {
				if (LOG) Log.v(TAG, "ciblePage() - elementTable : " + elementTable.toString().substring(0, Math.min(100, elementTable.toString().length())));
				
				List<? extends Attribute> listeAttributs=elementTable.getAttributes();
				for (Attribute attribut : listeAttributs) {
					if (attribut.getName().toLowerCase().equals("width") &  attribut.getValue().equals("820")) {
						if (LOG) Log.v(TAG, "ciblePage() - Table Trouvée : " + attribut.getName() + " = " +  attribut.getValue());
						tableResultats = elementTable;
						break;
					}
				}
				if (tableResultats != null) break;
			}
		}

		if (typePage == "RECHERCHE"){
			// Récupération de la Table des Résultats
			Element elementTitreGrandsGroupes = source.getFirstElementByClass("titre3");
			if (LOG) Log.v(TAG, "ciblePage() - titre3 : "+ elementTitreGrandsGroupes.toString().substring(0, Math.min(20,elementTitreGrandsGroupes.toString().length())));
			
			Element elementTable = elementTitreGrandsGroupes.getParentElement().getParentElement().getParentElement();
			if (LOG) Log.v(TAG, "ciblePage() - table : "+elementTable.toString().substring(0, Math.min(20,elementTable.toString().length())));

			tableResultats = elementTable;

		}
    	if (LOG) Log.v(TAG, "ciblePage() - tableResultats : " + tableResultats.toString().substring(0, Math.min(100, tableResultats.toString().length())));
		

		if ( tableResultats != null ){
			if (LOG) Log.v(TAG, "ciblePage() - longueur tableResultats : "+tableResultats.toString().length());
			if (LOG) Log.v(TAG, "ciblePage() - tableResultats : "+tableResultats.toString().substring(0, Math.min(50, tableResultats.toString().length())));
			if (LOG) Log.v(TAG, "ciblePage() - tableResultats : ...");
			if (LOG) Log.v(TAG, "ciblePage() - tableResultats : "+tableResultats.toString().substring(Math.max(0, tableResultats.toString().length()-50), tableResultats.toString().length() ));
			if (LOG) Log.d(TAG, "ciblePage() - Fin");
			return tableResultats.toString();
		} else {
			if (LOG) Log.w(TAG, "ciblePage() - tableResultats = null");
			if (LOG) Log.d(TAG, "ciblePage() - Fin");
			return null;
		}
    }

	/* *********************************************************************
     * getImage permet de récupérer une Image à partir d'une URL
     * et de stocker le résultat dans un cache qui devrait permettre d'accélérer
     * la récup et consommer moins de bande passante
     ********************************************************************** */
	public static Bitmap getImage(Context inContext, String inImageUrl, String inCle, String inLibelleImage) {
		if (LOG) Log.d(TAG, "getImage() - Début");
		if (LOG) Log.v(TAG, "getImage() - inImageUrl = "+inImageUrl);
		if (LOG) Log.v(TAG, "getImage() - inCle = "+inCle);
		
		Bitmap image = null;

        preferences = inContext.getSharedPreferences(PREFS_NAME, 0);
        int type_cache = preferences.getInt("type_cache", 2);
        if (LOG) Log.v(TAG, "getImage() - type_cache = "+type_cache);
        
        inImageUrl = inImageUrl.replace(" ","%20");
		if (LOG) Log.v(TAG, "getImage() - imageUrl = "+inImageUrl);
		
		String cleFichier = getCleImage(inImageUrl, inCle, inLibelleImage);
		if (LOG) Log.v(TAG, "getImage() - cleFichier = "+cleFichier);
		
        // Récupération de l'image dans le cache ?
        if(new File(inContext.getCacheDir(), cleFichier).exists()){

            // Durée cache en millisecondes
            long dernierModif = new File(inContext.getCacheDir(), cleFichier).lastModified() - System.currentTimeMillis();
            // Durée en jour
            dernierModif = dernierModif / ( DateUtils.DAY_IN_MILLIS );
            
            if( (type_cache == 1 && dernierModif < 1)
            	|| (type_cache == 2 && dernierModif < 7)	
            	|| (type_cache == 3 && dernierModif < 30)
            	|| (type_cache == 4 ))
            {
        	
            	if (LOG) Log.v(TAG, "getImage() - recup dans le cache");
            	if (LOG) Log.v(TAG, "getImage() - inContext.getCacheDir : " + inContext.getCacheDir());

            	image = BitmapFactory.decodeFile(new File(inContext.getCacheDir(), cleFichier).getPath());
	            
            	if (image == null) {
            		if (LOG) Log.v(TAG, "getImage() - image = null");
            	}
            }
        }
        
        if (image == null) {
        
	        // SINON
	        //recup image sur Internet
	        if (LOG) Log.v(TAG, "getImage() - recup sur Image sur Internet - avant");	
	
	        try {
		        URL url = new URL(inImageUrl);
		        
		        URLConnection urlConnection = url.openConnection();
		        
		        urlConnection.setConnectTimeout(3000);
		        urlConnection.setReadTimeout(10000);
	            
	            image = BitmapFactory.decodeStream(urlConnection.getInputStream());
	            if (LOG) Log.v(TAG, "getImage() - recup sur Image sur Internet - après");	
	            if (LOG) Log.v(TAG, "getImage() - Hauteur Largeur image : " + image.getHeight() + "x" + image.getWidth());
	        }
	        catch(SocketTimeoutException erreur) {
	        	Log.e(TAG, "getImage() - " + inContext.getString(R.string.txt_connectionLente) + " - " + erreur.toString(), erreur);
	        	Toast toast = Toast.makeText(inContext, inContext.getString(R.string.txt_connectionLente), Toast.LENGTH_LONG);
				toast.show();
				
				image = BitmapFactory.decodeResource(inContext.getResources(), R.drawable.doris);
	        }
	        catch(IOException erreur) {
	            Log.e(TAG, "getImage() - " + erreur.toString(), erreur);
	        }
	         
	        //Sauvegarde dans le cache	
	        if (type_cache != 0 && image != null){
	        	if (LOG) Log.v(TAG, "getImage() - Sauvegarde dans le cache");	
	            FileOutputStream fos = null;
	            try {
	            	if (LOG) Log.v(TAG, "getImage() - inContext.getCacheDir() : "+inContext.getCacheDir());	
	            	if (LOG) Log.v(TAG, "getImage() - imageName : "+cleFichier);	
	                fos = new FileOutputStream(new File(inContext.getCacheDir(), cleFichier));
	                
	                if(fos != null && image != null) {
	                	//Envoie de l'image dans le fichier cache
		                if(!image.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
		                    Log.e(TAG, "L'image " + cleFichier + " n'a pas pu être mise dans le cache !");
		                }
		                fos.flush();
		                fos.close();
		            }
		            else {
		            	Log.e(TAG, "L'image " + cleFichier + " n'a pas pu être mise dans le cache !");
		            }
	                
	            }
	            //Nécessaire mais ne devrait jamais arriver
	            catch(FileNotFoundException erreur) {
	            	Log.e(TAG, erreur.toString(), erreur);
	            }
	            catch(IOException erreur) {
	            	Log.e(TAG, erreur.toString(), erreur);
	            }
	            
	        } 
        }
            
        if (LOG) Log.d(TAG, "getImage() - Fin");
        return image;
    }

	public static String getCleImage(String inImageUrl, String inCle, String inLibelleImage) {
		if (LOG) Log.d(TAG, "getCleImage() - Début");
		if (LOG) Log.d(TAG, "getCleImage() - inImageUrl = "+inImageUrl);
		if (LOG) Log.d(TAG, "getCleImage() - inCle = "+inCle);
		if (LOG) Log.d(TAG, "getCleImage() - inLibelleImage = "+inLibelleImage);
		
		String cleFichier = "030-Image£";
		cleFichier += inCle + "£";
		cleFichier += inLibelleImage + "£";
		cleFichier += inImageUrl.replace("http://doris.ffessm.fr/gestionenligne/","").replace("/","£");
		
		if (LOG) Log.d(TAG, "getCleImage() - cleFichier = "+cleFichier);
		return cleFichier;
	}
    
    
	/* *********************************************************************
     * isOnline permet de vérifier que l'appli a bien accès à Internet
     ********************************************************************** */		
	public static boolean isOnline(Context inContext) {
		if (LOG) Log.d(TAG, "isOnline() - Début");
	    ConnectivityManager cm = (ConnectivityManager) inContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	if (LOG) Log.d(TAG, "isOnline() - isOnline : true");
	    	if (LOG) Log.d(TAG, "isOnline() - Fin");
	        return true;
	        
	    } else {
	    	//String text = "Aucune Connection Internet disponible";
	    	//if (LOG) Log.e(TAG, "isOnline() - " + text);
	    	//Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
			//toast.show();
			
			if (LOG) Log.d(TAG, "isOnline() - isOnline : false");
			if (LOG) Log.d(TAG, "isOnline() - Fin");
	    	return false;
	    }
	}

	/* *********************************************************************
     * Permet de nettoyer par récurrence un dossier (utile pour le nettoyage du cache)
     ********************************************************************** */
	public static int clearFolder(final File inDir, final int inNumDays) {
		if (LOG) Log.d(TAG, "clearFolder() - Début");
	    int deletedFiles = 0;
	    if (inDir!= null && inDir.isDirectory()) {
	        try {
	            for (File child:inDir.listFiles()) {

	                //first delete subdirectories recursively
	                if (child.isDirectory()) {
	                    deletedFiles += clearFolder(child, inNumDays);
	                }

	                //then delete the files and subdirectories in this dir
	                //only empty directories can be deleted, so subdirs have been done first
	                if (child.lastModified() < new Date().getTime() - inNumDays * DateUtils.DAY_IN_MILLIS) {
	                    if (child.delete()) {
	                        deletedFiles++;
	                    }
	                }
	            }
	        }
	        catch(Exception e) {
	            Log.e(TAG, String.format("Problème lors de la suppression du cache, erreur %s", e.getMessage()));
	        }
	    }
	    if (LOG) Log.d(TAG, "clearFolder() - Fin");
	    return deletedFiles;
	}
	
	/* *********************************************************************
     * Taille complète (par récurrence) d'un dossier (utile pour la taille du cache)
     ********************************************************************** */
	public static long sizeFolder(final File inDir) {
		if (LOG) Log.d(TAG, "sizeFolder() - Début");
		long size = 0;
	    if (inDir!= null && inDir.isDirectory()) {
	        try {
	            for (File child:inDir.listFiles()) {

	                //On parcourt récursivement le dossier
	                if (child.isDirectory()) {
	                	size += sizeFolder(child);
	                } else {
	                	size += child.length();
	                }
	            }
	        }
	        catch(Exception e) {
	            Log.e(TAG, String.format("Erreur lors de la lecture de la taille du dossier, erreur %s", e.getMessage()));
	        }
	    }
	    if (LOG) Log.d(TAG, "sizeFolder() - size : "+size);
	    if (LOG) Log.d(TAG, "sizeFolder() - Fin");
	    return size;
	}
	
	/* *********************************************************************
     * isAumoins1FicheDansLeCache renvoie Vrai si au moins une fiche enregistrée dans le cache
     ********************************************************************** */		
	public static boolean isAumoins1FicheDansLeCache(Context inContext) {
		if (LOG) Log.d(TAG, "isAumoins1FicheDansLeCache() - Début");
		
		boolean auMoins1Fiche = false;
		File dossierCache = inContext.getCacheDir();
		if (LOG) Log.v(TAG, "isAumoins1FicheDansLeCache() - dossierCache : "+dossierCache.toString());
		
		if (dossierCache!= null && dossierCache.isDirectory()) {
			
	        try {
	            for (File child:dossierCache.listFiles()) {
	            	if (LOG) Log.v(TAG, "isAumoins1FicheDansLeCache() - child : "+child.getName());
	            	 if (child.isFile()) {
	            	  if (child.getName().startsWith("-Fiche£",3)) {
	            		  auMoins1Fiche = true;
	            		  break;
	            	  }
	            	 }
	            }
	        } catch(Exception e) {
	            Log.e(TAG, String.format("Erreur lors de la lecture du cache, erreur %s", e.getMessage()));
	        }
		}
    	if (LOG) Log.d(TAG, "isAumoins1FicheDansLeCache() - auMoins1Fiche : "+auMoins1Fiche);        	
    	
    	if (LOG) Log.d(TAG, "isAumoins1FicheDansLeCache() - Fin");
		return auMoins1Fiche;
	}
	
}