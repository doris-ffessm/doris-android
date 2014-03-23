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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* *********************************************************************
 * Outils
 ********************************************************************** */
public class Common_Outils {
    
	// Initialisation de la Gestion des Log
	public static Log log = LogFactory.getLog(Common_Outils.class);
    
	// Constructeur
	public Common_Outils(){
		
	}
	
    public String nettoyageBalises(String texte) {
    	//log.debug("nettoyageBalises() - Début");
    	//log.debug("nettoyageBalises() - texteANettoye : " + texteANettoye);
		
    	//log.debug("nettoyageBalises() - 005");
    	
		//TODO : je me demande bien pourquoi j'ai fait cela : GMo 04/01/2014
		texte = StringUtils.replace(texte, "&nbsp;", " ");
		
		// On convertit <b> en <strong> les 2 sont confondus sur le site
		texte = StringUtils.replace(texte, "<b>", "<strong>");
		texte = StringUtils.replace(texte, "</b>", "</strong>");

		//log.debug("nettoyageBalises() - 010");
		
		// Il faut nettoyer cette balise originale ...
		// <strong style="font-size: 11px;">
		texte = texte.replaceAll("<strong [^>]*>", "");
		
		//log.debug("nettoyageBalises() - 020");
		
		// Tous les sauts de ligne de la même façon + gain de place en hauteur pour l'interface Android
		texte = StringUtils.replace(texte, "<br>", "<br/>");
		texte = StringUtils.replace(texte, "<br />", "<br/>");
		texte = StringUtils.replace(texte, "<br/><br/>", "<br/>");
		
		//log.debug("nettoyageBalises() - 025");
		
		//Permet que la recherche des définitions fonctionne mieux ensuite
		texte = StringUtils.replace(texte, "**", "##");
		texte = StringUtils.replace(texte, "</strong>*", "*</strong>");
		texte = StringUtils.replace(texte, "</em>*", "*</em>");
		texte = StringUtils.replace(texte, "</i>*", "*</i>");
		
		//log.debug("nettoyageBalises() - 030");
		
		//L'adresse du site n'apporte rien et pose des problèmes qd on va recherche les liens
		// vers les sites extérieurs
		String SITE_RACINE_URL = "http://doris.ffessm.fr/";
		//texte = StringUtils.replace(texte, Constants.getSiteUrl(), "");
		texte = StringUtils.replace(texte, SITE_RACINE_URL, "");
		
		//De même pour les site : www.ffessm.fr et www.security.fr qui sont sur toutes les pages
		texte = StringUtils.replace(texte, "href=\"http://www.ffessm.fr\"", "");
		texte = StringUtils.replace(texte, "href=\"http://biologie.ffessm.fr/\"", "");
		texte = StringUtils.replace(texte, "href=\"http://www.security.fr/\"", "");
		
		//log.debug("nettoyageBalises() - 040");
		
		// Certaines fiches comme : http://doris.ffessm.fr/fiche2.asp?fiche_numero=3527 (au 30 mars 13)
		// contiennent des " dans le nom de l'animal, or les " ne sont pas échappés donc ça met le
		// Bazar dans le code html
		// Je retire donc ici les paires de " qui sont à l'intérieure d'une autre paire de "
		// et qui ne contiennent pas de = ? < >
		texte = texte.replaceAll("(href=\"[^\"]*)\"([^\"=?<>]*)\"([^\"]*\")", "$1$2$3");
		
		//log.debug("nettoyageBalises() - 050");
		
		//Il arrive très souvent qu'une balise ouverte soit aussitôt refermée
		// ce doit sans doute être dû à l'interface de saisie ou des outils utilisés en amont
		// Toujours est-il que ça peut gêner ensuite, que ça fait perdre du temps et de la place
		texte = StringUtils.replace(texte, "<strong></strong>", "");
		texte = StringUtils.replace(texte, "</strong><strong>", "");
		texte = StringUtils.replace(texte, "<em></em>", "");
		texte = StringUtils.replace(texte, "</em><em>", "");
		texte = StringUtils.replace(texte, "<i></i>", "");
		texte = StringUtils.replace(texte, "</i><i>", "");
		texte = texte.replaceAll("<a href=\"http://[^>]*></a>", "");
			
		//log.debug("nettoyageBalises() - 060");
		
		// Suppression des textes masqués en étant écrit en blanc sur fond blanc
		// <span style="color: #ffffff;">Vidéoris</span>
		texte = texte.replaceAll("<span style=\"color: #ffffff;\">[^<>]*</span>", "");
		
		//log.debug("nettoyageBalises() - 090");
		
		//log.debug("nettoyageBalises() - texte : " + texte);
		//log.debug("nettoyageBalises() - Fin");
		return texte;
	}

    public String remplacementBalises(String texte, boolean avecMiseEnForme) {
    	//log.debug("remplacementBalises() - Début");
    	//log.debug("remplacementBalises() - texteANettoye : " + texte);

		if (avecMiseEnForme) {
			//Gras
			texte = StringUtils.replace(texte, "<strong>", "{{g}}");
			texte = StringUtils.replace(texte, "</strong>", "{{/g}}");
			//Italique
			texte = StringUtils.replace(texte, "<em>", "{{i}}");
			texte = StringUtils.replace(texte, "</em>", "{{/i}}");
			texte = StringUtils.replace(texte, "<i>", "{{i}}");
			texte = StringUtils.replace(texte, "</i>", "{{/i}}");
			//Souligné
			texte = texte.replaceAll("<span style=\"text-decoration: underline;\">([^<>]*)</span>","{{s}}$1{{/s}}");
			//Sauts de ligne
			texte = StringUtils.replace(texte, "<br/>", "{{n/}}");
			
			//Lien vers autres fiches
			texte = texte.replaceAll("<[^<]*fiche_numero=([0-9]*)\"[^>]*>([^<]*)</a>", "{{F:$1}}$2{{/F}}");
			
			//Lien vers termes du glossaire
			texte = texte.replaceAll("([ >\\}'\\(])([^ >\\}'\\(]*)\\*", "$1{{D:$2}}$2{{/D}}");
			//Image du Glossaire (elles sont dans le texte) - <img src="gestionenligne/diaporamaglo/16.jpg     ">
			texte = texte.replaceAll("<img src=\"gestionenligne/diaporamaglo/([^\" >]*)[ ]*\">", "{{E:$1/}}");
			
			
			//Lien vers site extérieur : oiseaux.net, fishbase.org, etc ...
			texte = texte.replaceAll("<a href=\"http://([^\"]*)\"[^>]*>([^<]*)</a>", "{{A:$1}}$2{{/A}}");
			
			// Après cela on nettoie un peu et met en ordre
			// Mieux vaut le faire dans le prefetch qd on a le temps qu'à la présentation
			texte = StringUtils.replace(texte, "{{/i}}{{i}}","");
			texte = StringUtils.replace(texte, "{{/g}}{{g}}","");
			
			texte = StringUtils.replace(texte, "{{/i}} {{i}}"," ");
			texte = StringUtils.replace(texte, "{{/g}} {{g}}"," ");
			
			// Le Gras ne doit pas être à l'intérieure d'un lien mais l'entourer
			// ça ne semble arriver que pour ce cas, i.e. pas pour les termes du glossaire, l'italique etc..
			texte = StringUtils.replace(texte, "{{/g}}{{/F}}", "{{/F}}{{/g}}");
			texte = texte.replaceAll("\\{\\{F:([0-9]*)\\}\\}\\{\\{g\\}\\}", "{{g}}{{F:$1}}");
			
		} else {
			texte = StringUtils.replace(texte, "<strong>", "");
			texte = StringUtils.replace(texte, "</strong>", "");
			texte = StringUtils.replace(texte, "<em>", "");
			texte = StringUtils.replace(texte, "</em>", "");
			texte = StringUtils.replace(texte, "<i>", "");
			texte = StringUtils.replace(texte, "</i>", "");
			texte = StringUtils.replace(texte, "<br/>", " ");
		}
		
		//log.debug("remplacementBalises() - texteNettoye : " + texteNettoye);
		//log.info("remplacementBalises() - Fin");
		return texte;
	}
	
    public String nettoyageTextes(String texteANettoye) {
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
