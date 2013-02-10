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

import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/* *********************************************************************
 * Classe permettant de récupérer les résultats à partir de la recherche guidée
 ********************************************************************** */
public class RechercheGuidee {
    private final static String TAG = "RechercheGuidee";
    private final static Boolean LOG = false;
    
    private Context appContext;
    
    public String racineSite;
    
    public int filtre_zone_geo = -1;
	public String url;
	public String pageRecup;
	public String pageNettoyee;
	
	public Groupe groupe;
	
	private int[] groupeNivPrecedents = {-1,-1,-1,-1,-1};
	private int nivPrecedent;
	

	
    public void getPageGrandsGroupes(Context inContext, SharedPreferences inPreferences) throws ClientProtocolException, IOException{
    	if (LOG) Log.d(TAG, "getPageGrandsGroupes()- Début");
    	
    	appContext = inContext;
    	
    	racineSite = appContext.getString(R.string.cst_racineSite);
    	    	    
		//url = racineSite + "/groupes.asp?numero_fichier=10";
    	
		//Quelle zone géographique est paramétrée ?
		filtre_zone_geo = get_filtre_zone_geo(inPreferences);
		if (LOG) Log.v(TAG, "getPageGrandsGroupes() - filtre_zone_geo : "+filtre_zone_geo);

		String url = "";
		switch(filtre_zone_geo){
		case 1 :
			//France
        	url = racineSite + "/groupes.asp?numero_fichier=1";
        	break;
		case 2:
			//Eau douce
	        url = racineSite + "/groupes.asp?numero_fichier=2";
        	break;
		case 3:
			//Atlantique Nord-Ouest
	        url = racineSite + "/groupes.asp?numero_fichier=5";
        	break;
		case 4:
			//Indo-Pacifique
	        url = racineSite + "/groupes.asp?numero_fichier=3";
        	break;
		case 5:
			//Caraïbes
	        url = racineSite + "/groupes.asp?numero_fichier=40";
        	break;
		default :
			url = racineSite + "/groupes.asp?numero_fichier=10";
		}
		if (LOG) Log.v(TAG, "getPageGrandsGroupes() - url : "+url);
    	
    	
		String cle_fichier_cache = "090-RechercheParNavigation£" + filtre_zone_geo;
    	
		pageRecup = Outils.getHtml(appContext, url, cle_fichier_cache);
    	    	
    	if (LOG) Log.d(TAG, "getPageGrandsGroupes() - Fin");
    }   
    
    public void getGroupes(Context inContext){
    	if (LOG) Log.d(TAG, "getGroupes() - Début");
    	
    	groupe = new Groupe(inContext, 0, 0, "");
    	nivPrecedent = 0;
    	
    	Source source=new Source(pageNettoyee);
    	source.fullSequentialParse();
    	
    	List<? extends Element> listeElementsTable;
    	List<? extends Element> listeElementsA;
    	
    	Element elementTD;
    	Element ElementNormal;
    	
		listeElementsTable=source.getAllElements(HTMLElementName.TR);
		int profondeurTRlignes = source.getFirstElement(HTMLElementName.TR).getDepth();
		
		for (Element elementTR : listeElementsTable) {
			if ( elementTR.getDepth() == profondeurTRlignes ) {
				if (LOG) Log.v(TAG, "getGroupes() - elementTR : "+elementTR.toString().substring(0, Math.min(100, elementTR.toString().length())));
				//Groupes Niveau 1 et Niveau 2
				elementTD = elementTR.getFirstElementByClass("titre2");
				if (elementTD != null) {
					if (LOG) Log.v(TAG, "getGroupes() - elementTD : "+elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
					//Groupes Niveau 1
					Element elementIMG = elementTD.getFirstElement(HTMLElementName.IMG);
					if (elementIMG != null) {
						if (elementIMG.getAttributeValue("src").contains("pucecarre.gif")) {
							if (LOG) Log.v(TAG, "getGroupes() - groupe 1 : "+elementTD.getRenderer().toString());
							
							int numGroupe = groupe.groupeListeEnfants.size();  
							groupe.addEnfant(numGroupe, 1, elementTD.getRenderer().toString());
							
							groupeNivPrecedents[1] = numGroupe;
							groupeNivPrecedents[2] = -1;
							groupeNivPrecedents[3] = -1;
							groupeNivPrecedents[4] = -1;
							
							nivPrecedent = 1;
						}
					} else {
						//Groupes Niveau 2
						if (nivPrecedent != 0) {
							if (LOG) Log.v(TAG, "getGroupes() - groupe 2 : "+elementTD.getRenderer().toString());
							
							int numGroupe = groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.size();
							groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).addEnfant(numGroupe,	2, elementTD.getRenderer().toString());
							
							groupeNivPrecedents[2] = numGroupe;
							groupeNivPrecedents[3] = -1;
							groupeNivPrecedents[4] = -1;
							
							nivPrecedent = 2;
						}
					}
					elementIMG = null;
				} else {
					//Groupes Niveau 3 et Niveau 4
					if (nivPrecedent != 0) {
						ElementNormal = elementTR.getFirstElementByClass("normal");
						if (ElementNormal != null){

							for (Element elementIMG : elementTR.getAllElements(HTMLElementName.IMG)) {
							
								if (LOG) Log.v(TAG, "getGroupes() - groupe 3 - 4 ? elementIMG : "+elementIMG.getAttributeValue("src"));
								
								if (elementIMG.getAttributeValue("src").contains("/images_groupe/")) {
									listeElementsA = elementIMG.getParentElement().getParentElement().getParentElement().getAllElements(HTMLElementName.A);
									
									for (Element elementA : listeElementsA) {
										if (LOG) Log.v(TAG, "getGroupes() - elementA : "+elementA.toString());
										
										String elementAClass = elementA.getAttributeValue("class");
										if (elementAClass != null){
											//Groupes Niveau 3
											if (elementAClass.toString().equals("normal")){
																						
												if (nivPrecedent == 1){
													if (LOG) Log.v(TAG, "getGroupes() - groupe 3 : "+elementA.getRenderer().toString());
													
													int numGroupe = groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.size();;
													groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).addEnfant(numGroupe,	3, elementA.getRenderer().toString());
													groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(numGroupe).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());
													groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(numGroupe).setNumUrlGroupe(Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", "")));
													
													if (groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).urlVignette == ""){groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());}
	
													groupeNivPrecedents[2] = numGroupe;
													groupeNivPrecedents[3] = -1;
											    	groupeNivPrecedents[4] = -1;
											    	
											    	nivPrecedent = 2;
												} else if (nivPrecedent >= 2){
													if (LOG) Log.v(TAG, "getGroupes() - groupe 3 : "+elementA.getRenderer().toString());
													
													int numGroupe = groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.size();
													groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).addEnfant(numGroupe,	3, elementA.getRenderer().toString());
													groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(numGroupe).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());
													groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(numGroupe).setNumUrlGroupe(Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", "")));
													
													if (groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).urlVignette == ""){groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());}
													if (groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).urlVignette == ""){groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());}
	
													groupeNivPrecedents[3] = numGroupe;
													groupeNivPrecedents[4] = -1;
											    	
											    	nivPrecedent = 3;
												}
											}
										}	
									}
								}
								if (LOG) Log.v(TAG, "getGroupes() - test  0");
								if (elementIMG.getAttributeValue("src").contains("/images_sousgroupe/")) {
									if (LOG) Log.v(TAG, "getGroupes() - test  1");
									
									listeElementsA = elementIMG.getParentElement().getParentElement().getAllElements(HTMLElementName.A);
									
									for (Element elementAG4 : listeElementsA) {
										if (LOG) Log.v(TAG, "getGroupes() - elementA : "+elementAG4.toString());
										
										String elementAClassG4 = elementAG4.getAttributeValue("class");
										if (elementAClassG4 != null){
											//Groupes Niveau 4
											if (elementAClassG4.toString().equals("normalgris2")){

												if (LOG) Log.v(TAG, "getGroupes() - groupe 4 : "+elementAG4.getRenderer().toString());
												
												int numGroupe = groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.size();
												groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).addEnfant(numGroupe,	4, elementAG4.getRenderer().toString());
												groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.get(numGroupe).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());
												groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.get(numGroupe).setNumUrlGroupe(groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).numUrlGroupe);
												groupe.groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.get(numGroupe).setNumUrlSsGroupe(Integer.parseInt(elementAG4.getAttributeValue("href").toString().replaceAll(".*sousgroupe_numero=(\\d+)&groupe_numero.*", "$1")));
													
										    	groupeNivPrecedents[4] = numGroupe;
										    	
										    	nivPrecedent = 4;

											}

										}										
								
								
									}
								}
							}

						}

					}
				
				}
			}
		}
		if (LOG) Log.d(TAG, "getGroupes() - Fin");
    }
    
	public int get_filtre_zone_geo(SharedPreferences inPreferences) {
		int filtre_zone_geo;
		filtre_zone_geo = inPreferences.getInt("filtre_zone_geo", 0);
		return filtre_zone_geo;
	}
}