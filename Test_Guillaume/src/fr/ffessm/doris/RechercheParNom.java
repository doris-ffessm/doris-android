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

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Log;

/* *********************************************************************
 * Classe permettant de récupérer les résultats à partir d'un mot 
 ********************************************************************** */
public class RechercheParNom {
    private final static String TAG = "RechercheParNom";
    private final static Boolean LOG = true;
    
    private Context appContext;
	public String pageRecup;
	public String pageNettoyee;
	public Integer nbResultats;
	
	public static List<String> listeFichesTrouvees = new ArrayList<String>(20);
	
    public void getPage(Context inContext, String inUrl, String inMot, boolean inSuivant, int inNumPageSuite) throws ClientProtocolException, IOException{
    	if (LOG) Log.d(TAG, "getPage()- Début");
    	if (LOG) Log.d(TAG, "getPage()- inUrl : " + inUrl);
    	if (LOG) Log.d(TAG, "getPage()- inMot : " + inMot);
    	if (LOG) Log.d(TAG, "getPage()- inSuivant : " + inSuivant);
    	if (LOG) Log.d(TAG, "getPage()- inNumPageSuite : " + inNumPageSuite);
    	
    	appContext = inContext;

    	if (inSuivant){
    		inUrl = inUrl + "&page=Suivant&PageCourante=" + inNumPageSuite;
    	}
    	
    	String cle_fichier_cache = "010-RechercheParNom£" + inMot + "£";
    	cle_fichier_cache += inUrl.trim().replace(" ", "_").replace("http://", "").replace("/", "_");

    	if (LOG) Log.v(TAG, "getPage()- cle_fichier_cache : " + cle_fichier_cache);
    	if (LOG) Log.v(TAG, "getPage()- inUrl : " + inUrl);
    	
    	pageRecup = Outils.getHtml(appContext, inUrl, cle_fichier_cache);
    	    	
    	if (LOG) Log.d(TAG, "getPage() - Fin");
    }
		    
    public Integer getNbResultats(String inCodeHtml) throws IOException{
    	if (LOG) Log.d(TAG, "resultGetNbResultats() - Début");
    	
    	Source source=new Source(inCodeHtml);
    	nbResultats = 0;
    	
    	// rechercher du nombre de fiches renvoyées, on prend la valeur affichée sur la page
    	// en version 3 du site Doris, on prend le 1er TD de la largeur = 293 (pour les recherches sur nom commun et 
    	// 330 après navigation dans l'arborescence
    	// de "XX fiches publiées sur YY" on récupère YY
		List<? extends Element> listeElements=source.getAllElements(HTMLElementName.TD);

		for (Element element : listeElements) {
			//if (LOG) Log.v(TAG, "Recherche.resultGetNbResultats() - "+element.getContent().toString());
			
			if (nbResultats == 0) {
			
				List<? extends Attribute> listeAttributs=element.getAttributes();
				for (Attribute attribut : listeAttributs) {
					if (LOG) Log.v(TAG, "resultGetNbResultats() - "+attribut.getName().toLowerCase()+" - "+attribut.getValue());
					
					if (attribut.getName().toLowerCase().equals("width") & (attribut.getValue().equals("293") || attribut.getValue().equals("330") )) {
						// suppression de tous les caractères "Blanc"
						String contenu = element.getContent().toString().replaceAll("\\s", "");
						if (LOG) Log.v(TAG, "resultGetNbResultats() - contenu (1) : "+contenu);
						
						//On garde la fin de la chaine "...<b>YY</b>" : le texte après la dernière balise <b>
						contenu = contenu.toLowerCase().replaceAll("<.*>(.[^>])","$1");
						if (LOG) Log.v(TAG, "resultGetNbResultats() - contenu (2) : "+contenu);
	
						//Supression de la balise de fin
						contenu = contenu.toLowerCase().replaceAll("<.*>", "");
						if (LOG) Log.v(TAG, "resultGetNbResultats() - contenu (3) : "+contenu);
						
						contenu = contenu.trim();
						if (LOG) Log.d(TAG, "resultGetNbResultats() - contenu : "+contenu);
						
						try {
							nbResultats = Integer.parseInt(contenu);
							break;
						} catch(NumberFormatException nFE) {
							if (LOG) Log.e(TAG, "resultGetNbResultats() - le contenu : "+contenu+" n'est pas un nombre, la page de Doris a du changer");
						}
						
					}
				}
			}
		}
		
		if (LOG) Log.d(TAG, "resultGetNbResultats() - nbResultats : "+nbResultats);
		if (LOG) Log.d(TAG, "resultGetNbResultats() - Fin");
    	return nbResultats;
    }
    
    /* *********************************************************************
     * Recherche des Fiches dans une pages de résultats
     ********************************************************************** */
    public List<String> getFiches(String inCodeHtml) {
    	if (LOG) Log.d(TAG, "getFiches() - Début");
    	    	
    	Source source=new Source(inCodeHtml);
		
		List<? extends Element> listeElements=source.getAllElements(HTMLElementName.TABLE);
		
		for (Element element : listeElements) {
		
			if ( element.getAttributeValue("border") != null
					&& element.getAttributeValue("cellpadding")!= null
					&& element.getAttributeValue("cellspacing") != null
					&& element.getAttributeValue("height") != null
					&& element.getAttributeValue("width") != null ) {
			
				if ( element.getAttributeValue("border").equals("0")
						&& element.getAttributeValue("cellpadding").equals("0")
						&& element.getAttributeValue("cellspacing").equals("0")
						&& element.getAttributeValue("height").equals("100%")
						&& element.getAttributeValue("width").equals("196")) {
					
						if (LOG) Log.v(TAG, "getFiches() - element.toString() : " + element.toString());
					
						Fiche fiche = new Fiche(appContext);
						
						//Dans 1 1er temps on ne crée que l'entête de la fiche, dans le cas d'une recherche
						// Ca permet de gagner du temps
						
						fiche.setEnteteFiche(element.toString());
						if (LOG) Log.d(TAG, "getFiches() - fiche.ref : " + fiche.ref);
						
						//Si fiche.ref == null c'est qu'il y a eu un souci
						if ( fiche.ref != null ){
						
							//si la fiche n'a jamais été créée on l'ajoute à notre Liste de Fiches 
							if (Doris.listeFiches.get(fiche.ref) == null){
								
								if (LOG) Log.v(TAG, "getFiches() - Doris.listeFiches.put()");
								Doris.listeFiches.put(fiche.ref, fiche);
							
							//Si la fiche existe mais pas son entête (si la fiche a été créée par un lien par exemple)
							//alors on complète la fiche existente
							} else if (Doris.listeFiches.get(fiche.ref).getEnteteExistence() == false){
								
								if (LOG) Log.v(TAG, "getFiches() - Doris.listeFiches.get().setEnteteFiche()");
								Doris.listeFiches.get(fiche.ref).setEnteteFicheFromFiche(fiche);
							
							}
							
							listeFichesTrouvees.add(fiche.ref);
							
						} else {
							
							if (LOG) Log.e(TAG, "getFiches() - fiche.ref == null => souci sur la page html");
					
						}
						
						fiche = null;
				}
			}
		}
		if (LOG) Log.d(TAG, "getFiches() - Fin");
		return listeFichesTrouvees;
    }
    
	
    
    public boolean getPageSuite(String inCodeHtml) throws IOException{
    	if (LOG) Log.d(TAG, "getPageSuite() - Début");
    	
		boolean pageSuivanteTrouvee;
		
		pageSuivanteTrouvee = inCodeHtml.contains("page=Suivant&PageCourante=");
		
		if (LOG) Log.d(TAG, "getPageSuite() - pageSuivanteTrouvee : "+pageSuivanteTrouvee);
		if (LOG) Log.d(TAG, "getPageSuite() - Fin");
    	return pageSuivanteTrouvee;
    }
     
    
    
}