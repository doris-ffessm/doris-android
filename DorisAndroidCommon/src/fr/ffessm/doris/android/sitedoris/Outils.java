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


package fr.ffessm.doris.android.sitedoris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;


/* *********************************************************************
 * Outils
 ********************************************************************** */
public class Outils {
    
	// Inititalisation de la Gestion des Log
	public static Log log = LogFactory.getLog(Outils.class);
    

    public static boolean getFichierUrl(String inUrl, String inFichierRetour) {
    	log.debug("getFichierUrl()- Début");
    	log.info("getFichierUrl()- url : " + inUrl);
    	log.info("getFichierUrl()- Fichier à Retourner : " + inFichierRetour);
    	
    	InputStream flux = null;
        FileOutputStream fichierUrl = null;

        try
        {
            URL url = new URL(inUrl);
            URLConnection connection = url.openConnection();
            int fileLength = connection.getContentLength();

            if (fileLength == -1)
            {
                log.error("URL Invalide : " + inUrl);
                return false;
            }

            flux = connection.getInputStream();
            fichierUrl = new FileOutputStream(inFichierRetour);
            byte[] buffer = new byte[1024];
            int read;

            while ((read = flux.read(buffer)) > 0)
            	fichierUrl.write(buffer, 0, read);
            fichierUrl.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.error("Erreur lors du téléchargement du fichier : " + inUrl);
            return false;
        }
        finally
        {
            try
            {  	
            	if(fichierUrl!=null) fichierUrl.close();            	
            	if(flux!=null)  flux.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                log.error("Erreur lors de l'écriture du fichier : " + inFichierRetour);
                return false;
            }
        }
    	
    	
    	log.debug("getFichierUrl()- Fin");
    	return true;
    }


	public static String getFichier(File inFichier) {
    	log.debug("getFichier()- Début");
    	log.info("getFichier()- Fichier : " + inFichier);
    	
    	FileInputStream objFile = null;
		try {
			objFile = new FileInputStream(inFichier);
			
			InputStreamReader objReader = new InputStreamReader(objFile, "iso-8859-1");
			BufferedReader objBufferReader = new BufferedReader(objReader);
			StringBuffer objBuffer = new StringBuffer();
			String strLine;
			try {
				while ((strLine = objBufferReader.readLine()) != null) {
					objBuffer.append(strLine);
					objBuffer.append("\n");
				}
				try {
					objFile.close();
					
					log.debug("getFichier()- Fin");
			    	return (objBuffer.toString());
			    	
				} catch (IOException e) {

					e.printStackTrace();
					
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		log.error("Erreur lors de la lecture du fichier : " + inFichier);
     	log.debug("getFichier()- Fin");
		return null;
	}

	/* *********************************************************************
     * ciblePage permet de supprimer tout le superflu de la page HTML
     ********************************************************************* */
    public static String ciblePage(String inCodeHtml, String inTypePage) throws IOException{
    	log.debug("ciblePage() - Début");
    	//log.debug("ciblePage() - codeHtml : " + inCodeHtml.substring(0, Math.min(50, inCodeHtml.length())));
    	log.debug("ciblePage() - inTypePage : " + inTypePage);
    			
    	String pageANettoyer = inCodeHtml;
    	String typePage = inTypePage;
    	
    	// Suppression des sauts de lignes
    	pageANettoyer.replaceAll("/r/n","");
    	//log.debug("ciblePage() - pageANettoyer 10 : " + pageANettoyer.substring(0, Math.min(100, pageANettoyer.length())));
    	
    	// Suppression des espaces inutiles (entre les ><)
    	pageANettoyer.replaceAll(">\\s*<","><");
    	//log.debug("ciblePage() - pageANettoyer 20 : " + pageANettoyer.substring(0, Math.min(100, pageANettoyer.length())));
    	
		Source source=new Source(pageANettoyer);
		source.fullSequentialParse();
		Element tableResultats = null;
		
		if (typePage == "RESULTATS" || typePage == "FICHE"){
			// Récupération de la Table des Résultats
			List<? extends Element> listeElementsTable=source.getAllElements(HTMLElementName.TABLE);
			for (Element elementTable : listeElementsTable) {
				//log.debug("ciblePage() - elementTable : " + elementTable.toString().substring(0, Math.min(100, elementTable.toString().length())));
				
				List<? extends Attribute> listeAttributs=elementTable.getAttributes();
				for (Attribute attribut : listeAttributs) {
					if (attribut.getName().toLowerCase().equals("width") &  attribut.getValue().equals("820")) {
						//log.debug("ciblePage() - Table Trouvée : " + attribut.getName() + " = " +  attribut.getValue());
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
			//log.debug("ciblePage() - titre3 : "+ elementTitreGrandsGroupes.toString().substring(0, Math.min(20,elementTitreGrandsGroupes.toString().length())));
			
			Element elementTable = elementTitreGrandsGroupes.getParentElement().getParentElement().getParentElement();
			//log.debug("ciblePage() - table : "+elementTable.toString().substring(0, Math.min(20,elementTable.toString().length())));

			tableResultats = elementTable;

		}
 
		if ( tableResultats != null ){
			log.debug("ciblePage() - longueur tableResultats : "+tableResultats.toString().length());
			//log.debug("ciblePage() - tableResultats : "+tableResultats.toString().substring(0, Math.min(50, tableResultats.toString().length())));
			//log.debug("ciblePage() - tableResultats : ...");
			//log.debug("ciblePage() - tableResultats : "+tableResultats.toString().substring(Math.max(0, tableResultats.toString().length()-50), tableResultats.toString().length() ));
			log.debug("ciblePage() - Fin");
		
			return tableResultats.toString();
		
		} else {
			log.warn("ciblePage() - tableResultats == null");
			log.debug("ciblePage() - Fin");
			
			return null;
		}
    }


    public static String nettoyageBalises(String texteANettoye) {
    	log.debug("nettoyageBalises() - Début");
    	log.debug("nettoyageBalises() - texteANettoye : " + texteANettoye);
		String texteNettoye = texteANettoye;
		
		texteNettoye = texteNettoye.replace("&nbsp;</td>", "</td>");
    	
    	/*
		texteNettoye = texteNettoye.replace("<strong>", "");
		texteNettoye = texteNettoye.replace("</strong>", "");
		texteNettoye = texteNettoye.replace("<em>", "");
		texteNettoye = texteNettoye.replace("</em>", "");
		texteNettoye = texteNettoye.replace("<br>", "");
		texteNettoye = texteNettoye.replace("<br/>", "");
		*/
		texteNettoye = texteNettoye.replace("<strong>", "")
				.replace("</strong>", "")
				.replace("<em>", "")
				.replace("</em>", "")
				.replace("<br>", "")
				.replace("<br/>", "");
		// Certaines fiches comme : http://doris.ffessm.fr/fiche2.asp?fiche_numero=3527 (au 30 mars 13)
		// contiennent des " dans le nom de l'animal, or les " ne sont pas échappés donc ça met le
		// bazard dans le code html
		// Je retire donc ici les paires de " qui sont à l'intérieure d'une autre paire de "
		// et qui ne contiennent pas de = ? < >
		log.debug("nettoyageBalises() - vitesse 1");
		texteNettoye = texteNettoye.replaceAll("(href=\"[^\"]*)\"([^\"=?<>]*)\"([^\"]*\")", "$1$2$3");
		log.debug("nettoyageBalises() - vitesse 2");
		
		log.debug("nettoyageBalises() - texteNettoye : " + texteNettoye);
		log.debug("nettoyageBalises() - Fin");
		return texteNettoye;
	}

	
    public static String nettoyageTextes(String texteANettoye) {
    	log.debug("nettoyageTextes() - Début");
    	log.debug("nettoyageTextes() - texteANettoye : " + texteANettoye);
		String texteNettoye = texteANettoye;
		
		//if (LOG) Log.v(TAG, "nettoyageCaracteres() - texteNettoye : " + texteNettoye.charAt(7) + " - " + texteNettoye.codePointAt(7));
		
		// A priori seuls les caratères de table Unicode "Latin étendu A" nécessite une transcodif.
		// en français seul oe
		
		//œ oe 
		texteNettoye = texteNettoye.replaceAll("\u009C", "\u0153");
		//Œ OE
		texteNettoye = texteNettoye.replaceAll("\u008C", "\u0152");

		// ⊃2; => ² \u00B2
		texteNettoye = texteNettoye.replaceAll("\u22832;", "\u00B2");
		
		// " *" => "*"
		texteNettoye = texteNettoye.replace(" *", "*");
				
		// suppression des sauts de ligne si pas avant une majuscule ou un - (puce)
		texteNettoye = texteNettoye.replaceAll("\r\n([^A-Z\\-])", " $1");
		//log.debug("nettoyageTextes() - 010 : " + texteNettoye);
		texteNettoye = texteNettoye.replaceAll("\n([^A-Z\\-])", " $1");
		//log.debug("nettoyageTextes() - 020 : " + texteNettoye);
		
		// suppression des blancs multiples
		texteNettoye = texteNettoye.replaceAll("[ \t]{2,}"," ");
		
		texteNettoye = texteNettoye.trim();
		log.debug("nettoyageTextes() - texteNettoye : " + texteNettoye);
		
		log.debug("nettoyageTextes() - Fin");
		return texteNettoye;
	}
    
    /* *********************************************************************
     * Permet d'enlever les accents, cédilles et autres
     *  ********************************************************************* */
	static public String formatStringNormalizer(String string) {
	    char[] charsData = new char[string.length()];
	    string.getChars(0, charsData.length, charsData, 0);
	 
	    char c;
	    for (int i = 0; i < charsData.length; i++) {
	        if ((c = charsData[i]) >= 'A' && c <= 'Z') {
	            charsData[i] = (char) (c - 'A' + 'a');
	        } else {
	            switch (c) {
	            case '\u00e0':
	            case '\u00e2':
	            case '\u00e4':
	                charsData[i] = 'a';
	                break;
	            case '\u00e7':
	                charsData[i] = 'c';
	                break;
	            case '\u00e8':
	            case '\u00e9':
	            case '\u00ea':
	            case '\u00eb':
	                charsData[i] = 'e';
	                break;
	            case '\u00ee':
	            case '\u00ef':
	                charsData[i] = 'i';
	                break;
	            case '\u00f4':
	            case '\u00f6':
	                charsData[i] = 'o';
	                break;
	            case '\u00f9':
	            case '\u00fb':
	            case '\u00fc':
	                charsData[i] = 'u';
	                break;
	            }
	        }
	    }
	 
	    return new String(charsData);
	}
   

}