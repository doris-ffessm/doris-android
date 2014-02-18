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
import java.util.regex.Pattern;

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
    
	// Initialisation de la Gestion des Log
	public static Log log = LogFactory.getLog(Outils.class);
    

    public static boolean getFichierFromUrl(String inUrl, String inFichierRetour) {
    	//log.debug("getFichierUrl()- Début");
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
    	
    	
    	//log.debug("getFichierUrl()- Fin");
    	return true;
    }


	public static String getFichierTxtFromDisk(File inFichier) {
    	//log.debug("getFichier()- Début");
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
     	//log.debug("getFichier()- Fin");
		return null;
	}

    public static String nettoyageBalises(String texteANettoye) {
    	//log.debug("nettoyageBalises() - Début");
    	//log.debug("nettoyageBalises() - texteANettoye : " + texteANettoye);

		String texteNettoye = texteANettoye;
		
		//TODO : je me demande bien pourquoi j'ai fait cela : GMo 04/01/2014
		texteNettoye = texteNettoye.replace("&nbsp;", " ");
		
		// On convertit <b> en <strong> les 2 sont confondus sur le site
		texteNettoye = texteNettoye.replace("<b>", "<strong>")
						.replace("</b>", "</strong>");

		// Tous les sauts de ligne de la même façon + gain de place en hauteur pour l'interface Android
		texteNettoye = texteNettoye.replace("<br>", "<br/>")
				.replace("<br />", "<br/>")
				.replace("<br/><br/>", "<br/>");
		
		//Permet que la recherche des définitions fonctionne mieux ensuite
		texteNettoye = texteNettoye.replace("**", "##")
				.replace("</strong>*", "*</strong>")
				.replace("</em>*", "*</em>")
				.replace("</i>*", "*</i>");
		
		//L'adresse du site n'apporte rien et pose des problèmes qd on va recherche les liens
		// vers les sites extérieurs
		texteNettoye = texteNettoye.replace(Constants.getSiteUrl(), "");
		//De même pour les site : www.ffessm.fr et www.security.fr qui sont sur toutes les pages
		texteNettoye = texteNettoye.replace("href=\"http://www.ffessm.fr\"", "")
				.replace("href=\"http://biologie.ffessm.fr/\"", "")
				.replace("href=\"http://www.security.fr/\"", "");
		
		
		// Certaines fiches comme : http://doris.ffessm.fr/fiche2.asp?fiche_numero=3527 (au 30 mars 13)
		// contiennent des " dans le nom de l'animal, or les " ne sont pas échappés donc ça met le
		// Bazar dans le code html
		// Je retire donc ici les paires de " qui sont à l'intérieure d'une autre paire de "
		// et qui ne contiennent pas de = ? < >
		texteNettoye = texteNettoye.replaceAll("(href=\"[^\"]*)\"([^\"=?<>]*)\"([^\"]*\")", "$1$2$3");

		//Il arrive très souvent qu'une balise ouverte soit aussitôt refermée
		// ce doit sans doute être dû à l'interface de saisie ou des outils utilisés en amont
		// Toujours est-il que ça peut gêner ensuite, que ça fait perdre du temps et de la place
		texteNettoye = texteNettoye.replace("<strong></strong>", "")
				.replace("</strong><strong>", "")
				.replace("<em></em>", "")
				.replace("</em><em>", "")
				.replace("<i></i>", "")
				.replace("</i><i>", "");
		texteNettoye = texteNettoye.replaceAll("<a href=\"http://[^>]*></a>", "");
			
		// Suppression des textes masqués en étant écrit en blanc sur fond blanc
		// <span style="color: #ffffff;">Vidéoris</span>
		texteNettoye = texteNettoye.replaceAll("<span style=\"color: #ffffff;\">[^<>]*</span>", "");
		
		//log.debug("nettoyageBalises() - texteNettoye : " + texteNettoye);
		//log.debug("nettoyageBalises() - Fin");
		return texteNettoye;
	}

    public static String remplacementBalises(String texteANettoye, boolean avecMiseEnForme) {
    	//log.debug("remplacementBalises() - Début");
    	//log.debug("remplacementBalises() - texteANettoye : " + texteANettoye);
		String texteNettoye = texteANettoye;

		if (avecMiseEnForme) {
			//Gras
			texteNettoye = texteNettoye.replace("<strong>", "{{g}}");
			texteNettoye = texteNettoye.replace("</strong>", "{{/g}}");
			//Italique
			texteNettoye = texteNettoye.replace("<em>", "{{i}}");
			texteNettoye = texteNettoye.replace("</em>", "{{/i}}");
			texteNettoye = texteNettoye.replace("<i>", "{{i}}");
			texteNettoye = texteNettoye.replace("</i>", "{{/i}}");
			//Souligné
			texteNettoye = texteNettoye.replaceAll("<span style=\"text-decoration: underline;\">([^<>]*)</span>","{{s}}$1{{/s}}");
			//Sauts de ligne
			texteNettoye = texteNettoye.replace("<br/>", "{{n/}}");
			
			//Lien vers autres fiches
			texteNettoye = texteNettoye.replaceAll("<[^<]*fiche_numero=([0-9]*)\"[^>]*>([^<]*)</a>", "{{F:$1}}$2{{/F}}");
			
			//Lien vers termes du glossaire
			texteNettoye = texteNettoye.replaceAll("([ >\\}'\\(])([^ >\\}'\\(]*)\\*", "$1{{D:$2}}$2{{/D}}");
			//Image du Glossaire (elles sont dans le texte) - <img src="gestionenligne/diaporamaglo/16.jpg     ">
			texteNettoye = texteNettoye.replaceAll("<img src=\"gestionenligne/diaporamaglo/([^\" >]*)[ ]*\">", "{{E:$1/}}");
			
			
			//Lien vers site extérieur : oiseaux.net, fishbase.org, etc ...
			texteNettoye = texteNettoye.replaceAll("<a href=\"http://([^\"]*)\"[^>]*>([^<]*)</a>", "{{A:$1}}$2{{/A}}");
			
			// Après cela on nettoie un peu et met en ordre
			// Mieux vaut le faire dans le prefetch qd on a le temps qu'à la présentation
			texteNettoye = texteNettoye.replace("{{/i}}{{i}}","");
			texteNettoye = texteNettoye.replace("{{/g}}{{g}}","");
			
			texteNettoye = texteNettoye.replace("{{/i}} {{i}}"," ");
			texteNettoye = texteNettoye.replace("{{/g}} {{g}}"," ");
			
			// Le Gras ne doit pas être à l'intérieure d'un lien mais l'entourer
			// ça ne semble arriver que pour ce cas, i.e. pas pour les termes du glossaire, l'italique etc..
			texteNettoye = texteNettoye.replace("{{/g}}{{/F}}", "{{/F}}{{/g}}");
			texteNettoye = texteNettoye.replaceAll("\\{\\{F:([0-9]*)\\}\\}\\{\\{g\\}\\}", "{{g}}{{F:$1}}");
			
		} else {
			texteNettoye = texteNettoye.replace("<strong>", "");
			texteNettoye = texteNettoye.replace("</strong>", "");
			texteNettoye = texteNettoye.replace("<em>", "");
			texteNettoye = texteNettoye.replace("</em>", "");
			texteNettoye = texteNettoye.replace("<i>", "");
			texteNettoye = texteNettoye.replace("</i>", "");
			texteNettoye = texteNettoye.replace("<br/>", " ");
		}
		
		//log.debug("remplacementBalises() - texteNettoye : " + texteNettoye);
		//log.debug("remplacementBalises() - Fin");
		return texteNettoye;
	}
	
    public static String nettoyageTextes(String texteANettoye) {
    	//log.debug("nettoyageTextes() - Début");
    	//log.debug("nettoyageTextes() - texteANettoye : " + texteANettoye);

		String texteNettoye = texteANettoye;
		
		//if (LOG) Log.v(TAG, "nettoyageCaracteres() - texteNettoye : " + texteNettoye.charAt(7) + " - " + texteNettoye.codePointAt(7));
		
		// On commence par enlever toutes les dernières balises qu'il pourrait rester
		texteNettoye = texteNettoye.replaceAll("<[^>]*>", "");
		
		// A priori seuls les caractères de table Unicode "Latin étendu A" nécessite une transcodif.
		// en français seul oe
		
		//œ oe 
		texteNettoye = texteNettoye.replaceAll("\u009C", "\u0153");
		//Œ OE
		texteNettoye = texteNettoye.replaceAll("\u008C", "\u0152");

		// Le 2 est transformé en 2 caractères par le parseur
		// ⊃2; => ² \u00B2
		texteNettoye = texteNettoye.replaceAll("\u22832;", "\u00B2");

		// suppression des sauts de ligne car gérés avant grace aux {{n/}}
		texteNettoye = texteNettoye.replaceAll("\r\n", "");
		texteNettoye = texteNettoye.replaceAll("\n", "");
		
		// Certains Textes contiennent des Guillemets de type :
		// PRIVATE USE TWO (C292 UTF8, 0092 en Java)
		// de même : RIGHT SINGLE QUOTATION MARK (E28099 UTF8, \u2019 en Java)
		// au lieu de ' - sous Android les TextView les remplacent par des espaces
		texteNettoye = texteNettoye.replaceAll("\u0092", "'");
		texteNettoye = texteNettoye.replaceAll("\u2019", "'");
		
		// suppression des blancs multiples
		texteNettoye = texteNettoye.replaceAll("[ \t]{2,}"," ");
		
		// remplacement de l'espace insécable devant : ; ! ?
		texteNettoye = texteNettoye.replaceAll(" :", "\u00A0:")
				.replaceAll(" ;", "\u00A0;")
				.replaceAll(" !", "\u00A0!")
				.replaceAll(" \\?", "\u00A0?");
		
		texteNettoye = texteNettoye.trim();
		//log.debug("nettoyageTextes() - texteNettoye : " + texteNettoye);
		
		//log.debug("nettoyageTextes() - Fin");
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
