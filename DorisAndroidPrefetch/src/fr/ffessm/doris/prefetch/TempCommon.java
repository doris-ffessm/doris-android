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


package fr.ffessm.doris.prefetch;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.datamodel.Fiche;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;


/* *********************************************************************
 * Outils
 ********************************************************************** */
public class TempCommon {
    
	// Inititalisation de la Gestion des Log
	private final static String LOGTAG = "TempCommon";
    private static Log trace = PrefetchDorisWebSite.trace;
    
    
    private final static String SITE_RACINE_URL = "http://doris.ffessm.fr/";
    private final static String LISTE_TOUTES_FICHES_URL = "nom_scientifique.asp?numero_fichier=10";
    
    public static String getSiteUrl() {
		String listeFichesUrl = SITE_RACINE_URL;
		return listeFichesUrl;
    }
    
    public static String getListeFichesUrl() {
		String listeFichesUrl = SITE_RACINE_URL + LISTE_TOUTES_FICHES_URL;
    	return listeFichesUrl;
    }
    
    public static List<Fiche> getListeFiches(String inCodePageHtml, int inNbFichesMax) {
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- Début");
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- NbFichesMax : "+inNbFichesMax);
    	
    	// TODO : j'ai mis 1, je ne connais pas les conséquences de ce choix
    	// 3000 n'est-il pas trop grand
    	List<Fiche> listeFiches = new ArrayList<Fiche>(1);
    	
    	Source source=new Source(inCodePageHtml);
    	source.fullSequentialParse();
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- source.length() : " + source.length());
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));

    	Element elementTableracine=source.getFirstElementByClass("titre_page").getParentElement().getParentElement();
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- elementTableracine.length() : " + elementTableracine.length());
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- elementTableracine : " + elementTableracine.toString().substring(0, Math.min(100, elementTableracine.toString().length())));

    	List<? extends Element> listeElementsTD = elementTableracine.getAllElements(HTMLElementName.TD);
    	trace.log(trace.LOG_DEBUG, LOGTAG, "getGroupes() - listeElementsTD.size() : " + listeElementsTD.size());
		
    	for (Element elementTD : listeElementsTD) {
    		//trace.log(trace.LOG_DEBUG, LOGTAG, "getGroupes() - elementTD.length() : " + elementTD.length());
    		//trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- elementTD : " + elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
    		
    		String elementTDwidth = elementTD.getAttributeValue("width");
			if (elementTDwidth != null){
    			if (elementTDwidth.toString().equals("75%")) {
    				trace.log(trace.LOG_DEBUG, LOGTAG, "getGroupes() - elementTD : "+elementTD.getRenderer());
    				Element elementTDA = elementTD.getFirstElement(HTMLElementName.A);
    				
    				String contenu = elementTDA.getRenderer().toString();
    				trace.log(trace.LOG_DEBUG, LOGTAG, "getGroupes() - contenu : "+contenu);
    				
    				String ficheNomScientifique = contenu.replaceAll("([^-]*)-(.*)", "$1").trim();
    				String ficheNomCommun = contenu.replaceAll("([^-]*)-(.*)", "$2").trim();
    				int ficheId = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "").replaceAll("&.*", ""));
    				int ficheEtat = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_etat=", "").replaceAll("&.*", ""));
    				
    				trace.log(trace.LOG_VERBOSE, LOGTAG, "getGroupes() - fiche : "+ficheId+" - "+ficheNomScientifique+" - "+ficheNomCommun + " - Etat : " + ficheEtat);
    				Fiche fiche = new Fiche(ficheNomScientifique, ficheNomCommun, ficheId, ficheEtat);
      				
    				listeFiches.add(fiche);
    			}
			}
			
			// Permet de limiter le nombre de fiches traitées pendant les dev.
			if (listeFiches.size() >= inNbFichesMax){
				trace.log(trace.LOG_VERBOSE, LOGTAG, "getGroupes() - le nombre de fiches max. est atteint : "+listeFiches.size());
				break;
			}

		}
		trace.log(trace.LOG_DEBUG, LOGTAG, "getListeFiches()- Fin");
		return listeFiches;
    }
    
    
}