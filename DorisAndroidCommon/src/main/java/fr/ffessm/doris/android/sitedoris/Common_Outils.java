/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
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

		// On convertit <b> en <strong> les 2 sont confondus sur le site
		texte = StringUtils.replace(texte, "<b>", "<strong>");
		texte = StringUtils.replace(texte, "</b>", "</strong>");

        // remplace les &****; par les caractères correspondant
        texte = codeHtml2Text(texte);

		//log.debug("nettoyageBalises() - 010");
        texte = StringUtils.replace(texte, "<html/>", "");
        texte = StringUtils.replace(texte, "<html />", "");

		// Il faut nettoyer cette balise originale ...
		// <strong style="font-size: 11px;">
		texte = texte.replaceAll("<strong [^>]*>", "<strong>");

        // Des commentaires de toutes sortes, surtout liés à des copier / coller depuis word .... ppffff ....
        texte = texte.replaceAll("<!--[^>]*-->", "");

		//log.debug("nettoyageBalises() - 020");
		
		//Permet que la recherche des définitions fonctionne mieux ensuite
		texte = StringUtils.replace(texte, "**", "##");
		texte = StringUtils.replace(texte, "</strong>*", "*</strong>");
		texte = StringUtils.replace(texte, "</em>*", "*</em>");
		texte = StringUtils.replace(texte, "</i>*", "*</i>");
		
		//log.debug("nettoyageBalises() - 030");
		
		//L'adresse du site n'apporte rien et pose des problèmes qd on va recherche les liens
		// vers les sites extérieurs
		texte = StringUtils.replace(texte, "(http|https)://doris.ffessm.fr/", "");
		
		//De même pour les site : www.ffessm.fr et www.security.fr qui sont sur toutes les pages
		texte = StringUtils.replace(texte, "href=\"(http|https)://www.ffessm.fr\"", "");
		texte = StringUtils.replace(texte, "href=\"(http|https)://biologie.ffessm.fr/\"", "");
		texte = StringUtils.replace(texte, "href=\"(http|https)://www.security.fr/\"", "");
		
		//log.debug("nettoyageBalises() - 040");
		
		// Certaines fiches comme : http://doris.ffessm.fr/fiche2.asp?fiche_numero=3527 (au 30 mars 13)
		// contiennent des " dans le nom de l'animal, or les " ne sont pas échappés donc ça met le
		// Bazar dans le code html
		// Je retire donc ici les paires de " qui sont à l'intérieure d'une autre paire de "
		// et qui ne contiennent pas de = ? < >
		texte = texte.replaceAll("(href=\"[^\"]*)\"([^\"=?<>]*)\"([^\"]*\")", "$1$2$3");

		//log.debug("nettoyageBalises() - 050");

        // On enlève lien non cliquable
		texte = texte.replaceAll("<a href=\"(http|https)://[^>]*></a>", "");

        // On vire target=... des liens
        texte = StringUtils.replace(texte, "<a target=\"_blank\" href=", "<a href=");
        texte = StringUtils.replace(texte, " target=\"_blank\">", ">");


		//log.debug("nettoyageBalises() - 060");

		// Suppression des textes masqués en étant écrit en blanc sur fond blanc (si, si il y a eu ...)
		// <span style="color: #ffffff;">Vidéoris</span>
		texte = texte.replaceAll("<span style=\"color: #ffffff;\">[^<>]*</span>", "");

        // Suppression Balises de coloration "non significatives" en 2 temps car la balise span peut contenir un span
        // et la récurrence n'est pas gérée par les expressions régulières
        // Ici on remplace les balises span d'ouverture, après le remplacement des balises par {{*}} on enlève tous les span restant
        texte = texte.replaceAll("<span style=\"color:[^>]*>", "<span>");
        texte = texte.replaceAll("<span class=[^>]*>", "<span>");
        texte = texte.replaceAll("<i[^>]*>", "<i>");
        texte = texte.replaceAll("<em[^>]*>", "<em>");

		texte = StringUtils.replace(texte, "bgcolor=\"#ffffff\" onMouseOver=\"this.bgColor='#F3F3F3';\" onMouseOut=\"this.bgColor='#ffffff';\"", "" );
		texte = StringUtils.replace(texte, "color=\"#999999\"", "");
		//log.debug("nettoyageBalises() - 090");


        // Il arrive très souvent qu'une balise ouverte soit aussitôt refermée
        // ce doit sans doute être dû à l'interface de saisie ou des outils utilisés en amont
        // Toujours est-il que ça peut gêner ensuite, que ça fait perdre du temps et de la place
        texte = texte.replaceAll("<strong></strong>|</strong><strong>|<em></em>|</em><em>|<i></i>|</i><i>|<span></span>", "");
        texte = texte.replaceAll("<p></p>", "");
        texte = texte.replaceAll("<sub></sub>", "");

		// Les tableaux ne sont pas représentés.
		// Il faut commencer par supprimer tout ce qui est vide, donc on commence par les TD, puis les TR, TBODY et TABLE.
		// Un saut de ligne est fait à la fin d'un TR.
		texte = StringUtils.replace(texte, "<td></td>", "");
		texte = StringUtils.replace(texte, "<tbody></tbody>", "");
		texte = StringUtils.replace(texte, "<tr></tr>", "");
		texte = StringUtils.replace(texte, "</tr>", "<br/>");
		texte = texte.replaceAll("<table>|<tbody>|<tr>|<td>|</td>|</tbody>|</table>", "");

        // Cas particulier ...
        texte = StringUtils.replace(texte, "<!m>", "");
        texte = StringUtils.replace(texte, "<!>", "");

		// Tous les sauts de ligne de la même façon + gain de place en hauteur pour l'interface Android
		texte = texte.replaceAll("<br>|<br />", "<br/>");

		texte = StringUtils.replace(texte, "<br/><br/>", "<br/>");

		//log.debug("nettoyageBalises() - texte : " + texte);
    	//log.debug("nettoyageBalises() - Fin");
		return texte.trim();
	}

    public String remplacementBalises(String texte, boolean avecMiseEnForme) {
    	//log.debug("remplacementBalises() - Début");
    	//log.debug("remplacementBalises() - texteANettoye : " + texte);
    	
		if (avecMiseEnForme) {
			//Gras
			texte = StringUtils.replace(texte, "<strong>", "{{g}}");
			texte = StringUtils.replace(texte, "</strong>", "{{/g}}");
			//Italique
			texte = texte.replaceAll("<em[^<]*>|<i[^<]*>", "{{i}}");
			texte = texte.replaceAll("</em>|</i>", "{{/i}}");
			//Souligné
			texte = texte.replaceAll("<span style=\"text-decoration: underline;\">([^<>]*)</span>","{{s}}$1{{/s}}");
            //Exposant
            //TODO : On pourrait garder les exposants mais ne sachant pas encore faire, ils sont supprimés
            texte = texte.replaceAll("<sup>([^<>]*)</sup>","$1");
            //Souligné
            //TODO : On pourrait garder les souligés mais ne sachant pas encore faire, ils sont supprimés
            texte = texte.replaceAll("<u>([^<>]*)</u>","$1");

            //Sauts de ligne
			texte = StringUtils.replace(texte, "<br/>", "{{n/}}");

            //Remplacement de l'enchainement entre 2 paragraphe par un Saut de ligne
            texte = StringUtils.replace(texte, "</p><p>", "{{n/}}");
            texte = StringUtils.replace(texte, "<p>", "");
			//Suppression de <p abp="9999">
            texte = texte.replaceAll("<p[^>]*>", "");
            texte = StringUtils.replace(texte, "</p>", "");

			//Lien vers autres fiches
			texte = texte.replaceAll("<[^<]*fiche_numero=([0-9]*)\"[^>]*>([^<]*)</a>", "{{F:$1}}$2{{/F}}");
			texte = texte.replaceAll("<[^<]*specie/([0-9]*)[^>]*>([^<]*)</a>", "{{F:$1}}$2{{/F}}");

			//Lien vers termes du glossaire
			//(*) remplacé par (#)
			texte = StringUtils.replace(texte, "(*)", "(#)");
			//Mot suffixé par *
            texte = texte.replaceAll("([ >\\}'\\(])([^ >\\}'\\(]*)\\*", "$1{{D:$2}}$2{{/D}}");
            // en V4,
            texte = texte.replaceAll("<a href=\"Glossaire/([^\"]*)\"[^>]*>([^<]*)</a>", "{{D:$1}}$2{{/D}}");

			//Image du Glossaire (elles sont dans le texte) - <img src="gestionenligne/diaporamaglo/16.jpg     ">
			texte = texte.replaceAll("<img src=\"gestionenligne/diaporamaglo/([^\" >]*)[ ]*\">", "{{E:$1/}}");

            //TODO : Permettre de faire des liens vers des Participants à DORIS
            // Pour l'instant, on vire le lien
            texte = texte.replaceAll("<a href=\"../contact_fiche.asp[^>]*>([^<]*)</a>", "$1");

			//Lien vers site extérieur : oiseaux.net, fishbase.org, etc ...
			texte = texte.replaceAll("<a[^>]*href=\"http://([^\"]*)\"[^>]*>([^<]*)</a>", "{{A:$1}}$2{{/A}}");

            // Pour les derniers liens qui trainent, on fait un lien vers le site de DORIS
            texte = texte.replaceAll("<a href=\"([^\"]*)\"[^>]*>([^<]*)</a>", "{{A:http://doris.ffessm.fr/$1}}$2{{/A}}");

			// Après cela on nettoie un peu et met en ordre
			// Mieux vaut le faire dans le prefetch qd on a le temps qu'à la présentation
			texte = texte.replaceAll("\\{\\{/i\\}\\}\\{\\{i\\}\\}|\\{\\{/g\\}\\}\\{\\{g\\}\\}","");
			texte = texte.replaceAll("\\{\\{/i\\}\\} \\{\\{i\\}\\}|\\{\\{/g\\}\\} \\{\\{g\\}\\}"," ");
			
			// Le Gras ne doit pas être à l'intérieure d'un lien mais l'entourer
			// ça ne semble arriver que pour ce cas, i.e. pas pour les termes du glossaire, l'italique etc..
			texte = StringUtils.replace(texte, "{{/g}}{{/F}}", "{{/F}}{{/g}}");
			texte = texte.replaceAll("\\{\\{F:([0-9]*)\\}\\}\\{\\{g\\}\\}", "{{g}}{{F:$1}}");

            // Les listes à Puces et Ordonnées sont représentées par des -
            //   Les nouvelles lignes => Saut de Ligne puis la puce
            texte = StringUtils.replace(texte, "<li>", "{{n/}}  -");
            //   Un saut de ligne après la dernière ligne (au cas où)
            texte = texte.replaceAll("</ul>|</ol>", "{{n/}}");
            //   Suppression de toutes autres balises
            texte = texte.replaceAll("<ul[^<]*>|<ol[^<]*>|</li>", "");

            // Suppression Balises de coloration "non significatives" en 2 temps car la balise span peut contenir un span
            // et la récurrence n'est pas gérée par les expressions régulières
            // Avant on a remplacé les balises span d'ouverture, ici (après le remplacement des balises par {{*}}) on enlève tous les span restant
            texte = texte.replaceAll("<span[^>]*>", "");
            texte = StringUtils.replace(texte, "</span>", "");

		} else {
			texte = texte.replaceAll("<strong>|</strong>|<em>|</em>|<i>|</i>|<br/>|<span>|</span>", "");
		}
		
		//log.debug("remplacementBalises() - texteNettoye : " + texteNettoye);
    	//log.info("remplacementBalises() - Fin");
		return texte.trim();
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
		texteNettoye = texteNettoye.replaceAll("\u0092|\u2019", "'");
		
		// suppression des blancs multiples
		texteNettoye = texteNettoye.replaceAll("[ \t]{2,}"," ");
		
		// remplacement de l'espace insécable devant : ; ! ?
		texteNettoye = texteNettoye.replaceAll(" :", "\u00A0:")
				.replaceAll(" ;", "\u00A0;")
				.replaceAll(" !", "\u00A0!")
				.replaceAll(" \\?", "\u00A0?");

        // Remplacement &...; par les caractères alpha.
        texteNettoye = codeHtml2Text(texteNettoye);

		texteNettoye = texteNettoye.trim();
		//log.debug("nettoyageTextes() - texteNettoye : " + texteNettoye);
		
		//log.debug("nettoyageTextes() - Fin");
		return texteNettoye;
	}
    

    /* *********************************************************************
     * Permet d'enlever les accents, cédilles et autres
     *  ********************************************************************* */
	public String formatStringNormalizer(String string) {
	    char[] charsData = new char[string.length()];
	    string.getChars(0, charsData.length, charsData, 0);

	    String charsDataNormalized = "";
	    
	    char c;
	    for (int i = 0; i < charsData.length; i++) {
	    	// On prend tout d'abord le caractère d'origine puis si besoin il sera remplacé
	    	c = charsData[i];
	    	
	        // Met en minuscule les lettres de A à Z
	    	if (c >= 'A' && c <= 'Z') {
	        	c = (char) (c - 'A' + 'a');
	        } else {
	        	if ( c >= '\u00c0' && c <= '\u00df') {
		        	c = (char) (c - '\u00c0' + '\u00e0');
	        	}

	            switch (c) {
	            case '\u00e0':
	            case '\u00e2':
	            case '\u00e4':
	            	c = 'a';
	                break;
	            case '\u00e7':
	            	c = 'c';
	                break;
	            case '\u00e8':
	            case '\u00e9':
	            case '\u00ea':
	            case '\u00eb':
	            	c = 'e';
	                break;
	            case '\u00ee':
	            case '\u00ef':
	            	c = 'i';
	                break;
	            case '\u00f4':
	            case '\u00f6':
	            	c = 'o';
	                break;
	            case '\u00f9':
	            case '\u00fb':
	            case '\u00fc':
	            	c = 'u';
	                break;
	            case '\u0152':
	            case '\u0153':
	            	c = 'o';
	            	charsDataNormalized += c;
	            	c = 'e';
	                break;
	            case '\u00e6':
	            	c = 'a';
	            	charsDataNormalized += c;
	            	c = 'e';
	                break;
	            }
	        }
        	//log.debug("formatStringNormalizer() - charsData["+i+"] : " + charsData[i]
        	//		+ " - c : " + c);
        	charsDataNormalized += c;
	    }
	 
	    return charsDataNormalized;
	}

    public String codeHtml2Text(String texte) {

        texte = StringUtils.replace(texte, "&agrave;", "à");
        texte = StringUtils.replace(texte, "&Agrave;", "À");
        texte = StringUtils.replace(texte, "&acirc;", "â");
        texte = StringUtils.replace(texte, "&Acirc;", "Â");
        texte = StringUtils.replace(texte, "&ccedil;", "ç");
        texte = StringUtils.replace(texte, "&Ccedil;", "Ç");
        texte = StringUtils.replace(texte, "&eacute;", "é");
        texte = StringUtils.replace(texte, "&Eacute;", "É");
        texte = StringUtils.replace(texte, "&egrave;", "è");
        texte = StringUtils.replace(texte, "&Egrave;", "È");
        texte = StringUtils.replace(texte, "&ecirc;", "ê");
        texte = StringUtils.replace(texte, "&euml;", "ë");
        texte = StringUtils.replace(texte, "&Euml;", "Ë");
        texte = StringUtils.replace(texte, "&icirc;", "î");
        texte = StringUtils.replace(texte, "&Icirc;", "Î");
        texte = StringUtils.replace(texte, "&iuml;", "ï");
        texte = StringUtils.replace(texte, "&Iuml;", "Ï");
        texte = StringUtils.replace(texte, "&ocirc;", "ô");
        texte = StringUtils.replace(texte, "&Ocirc;", "Ô");
        texte = StringUtils.replace(texte, "&ouml;", "ö");
        texte = StringUtils.replace(texte, "&Ouml;", "Ö");
        texte = StringUtils.replace(texte, "&oelig;", "œ");
        texte = StringUtils.replace(texte, "&OElig;", "Œ");
        texte = StringUtils.replace(texte, "&ugrave;", "ù");
        texte = StringUtils.replace(texte, "&Ugrave;", "Ù");
        texte = StringUtils.replace(texte, "&ucirc;", "û");
        texte = StringUtils.replace(texte, "&Ucirc;", "Û");
        texte = StringUtils.replace(texte, "&uuml;", "ü");
        texte = StringUtils.replace(texte, "&Uuml;", "Ü");
        texte = StringUtils.replace(texte, "&nbsp;", " ");
        texte = StringUtils.replace(texte, "&deg;", "°");
        texte = StringUtils.replace(texte, "&acute;", "'");
        texte = StringUtils.replace(texte, "&quot;", "'");
        texte = StringUtils.replace(texte, "&rsquo;", "'");
        texte = StringUtils.replace(texte, "&laquo;", "«");
        texte = StringUtils.replace(texte, "&raquo;", "»");
        texte = StringUtils.replace(texte, "&amp;", "&");
        texte = StringUtils.replace(texte, "&hellip;", "…");

        return texte;
    }


}
