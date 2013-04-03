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

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;

public class SiteDoris {

	// Inititalisation de la Gestion des Log
	public static Log log = LogFactory.getLog(SiteDoris.class);
	

    
    public static List<Fiche> getListeFiches(String inCodePageHtml, int inNbFichesMax) {
    	log.debug("getListeFiches()- Début");
    	log.debug("getListeFiches()- NbFichesMax : "+inNbFichesMax);
    	
    	// TODO : j'ai mis 1, je ne connais pas les conséquences de ce choix
    	// 3000 n'est-il pas trop grand ?
    	List<Fiche> listeFiches = new ArrayList<Fiche>(1);
    	
    	Source source=new Source(Outils.nettoyageBalises(inCodePageHtml));
    	source.fullSequentialParse();
    	log.debug("getListeFiches()- source.length() : " + source.length());
    	log.debug("getListeFiches()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));

    	Element elementTableracine=source.getFirstElementByClass("titre_page").getParentElement().getParentElement();
    	log.debug("getListeFiches()- elementTableracine.length() : " + elementTableracine.length());
    	log.debug("getListeFiches()- elementTableracine : " + elementTableracine.toString().substring(0, Math.min(100, elementTableracine.toString().length())));

    	List<? extends Element> listeElementsTD = elementTableracine.getAllElements(HTMLElementName.TD);
    	log.debug("getGroupes() - listeElementsTD.size() : " + listeElementsTD.size());
		
    	for (Element elementTD : listeElementsTD) {
    		log.debug("getGroupes() - elementTD.length() : " + elementTD.length());
    		log.debug("getListeFiches()- elementTD : " + elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
    		
    		String elementTDwidth = elementTD.getAttributeValue("width");
			if (elementTDwidth != null){
    			if (elementTDwidth.toString().equals("75%")) {
    				log.debug("getGroupes() - elementTD : "+elementTD.getRenderer());
    				Element elementTDA = elementTD.getFirstElement(HTMLElementName.A);
    				
    				String contenu = elementTDA.getRenderer().toString();
    				log.debug("getGroupes() - contenu : "+contenu);
    				
    				String ficheNomScientifique = contenu.replaceAll("([^-]*)-(.*)", "$1").trim();
    				String ficheNomCommun = contenu.replaceAll("([^-]*)-(.*)", "$2").trim();
    				int ficheId = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "").replaceAll("&.*", ""));
    				int ficheEtat = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_etat=", "").replaceAll("&.*", ""));
    				
    				String dateCreation = ""; //TODO
    				String dateModification = ""; //TODO
    				log.info("getGroupes() - fiche : "+ficheId+" - "+ficheNomScientifique+" - "+ficheNomCommun + " - Etat : " + ficheEtat);
    				
    				Fiche fiche = new Fiche(ficheNomScientifique, ficheNomCommun, ficheId, ficheEtat, dateCreation, dateModification);
      				
    				listeFiches.add(fiche);
    			}
			}
			
			// Permet de limiter le nombre de fiches traitées pendant les dev.
			if (listeFiches.size() >= inNbFichesMax){
				log.info("getGroupes() - le nombre de fiches max. est atteint : "+listeFiches.size());
				break;
			}

		}
		log.debug("getListeFiches()- Fin");
		return listeFiches;
    }
	
	
    
    public static List<Groupe> getListeGroupes(String inCodePageHtml){
    	log.debug("getGroupes() - Début");
    	
    	// TODO : j'ai mis 1, je ne connais pas les conséquences de ce choix
    	// 200 n'est-il pas trop grand ?
    	List<Groupe> listeGroupes = new ArrayList<Groupe>(0);
    	
    	int numGroupe = 0;
    	int nivPrecedent = 0;
    	int[] groupeNivPrecedents = {-1,-1,-1,-1,-1};
    	
    	// Création du groupe racine qui contiendra récurcivement tout l'arbre
    	Groupe groupe = new Groupe("racine", 0, 0, 0, "","");
    	listeGroupes.add(groupe);
    	
    	Source source=new Source(inCodePageHtml);
    	source.fullSequentialParse();
    	log.debug("getGroupes()- source.length() : " + source.length());
    	
    	List<? extends Element> listeElementsTable;
    	List<? extends Element> listeElementsA;
    	
    	Element elementTD;
    	Element ElementNormal;
    	
    	Element elementTitreGrandsGroupes = source.getFirstElementByClass("titre3");
		
    	
    	Element elementTable = elementTitreGrandsGroupes.getParentElement().getParentElement().getParentElement();

    	
		listeElementsTable = elementTable.getAllElements(HTMLElementName.TR);
		int profondeurTRlignes = elementTable.getFirstElement(HTMLElementName.TR).getDepth();
		
		for (Element elementTR : listeElementsTable) {
			if ( elementTR.getDepth() == profondeurTRlignes ) {
				log.info("getGroupes() - elementTR : "+elementTR.toString().substring(0, Math.min(100, elementTR.toString().length())));
				//Groupes Niveau 1 et Niveau 2
				elementTD = elementTR.getFirstElementByClass("titre2");
				if (elementTD != null) {
					log.info("getGroupes() - elementTD : "+elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
					//Groupes Niveau 1
					Element elementIMG = elementTD.getFirstElement(HTMLElementName.IMG);
					if (elementIMG != null) {
						if (elementIMG.getAttributeValue("src").contains("pucecarre.gif")) {
							log.info("getGroupes() - groupe 1 : "+elementTD.getRenderer().toString());
							
							numGroupe += 1;  
							groupe = new Groupe("G1-"+numGroupe, 1, 0, 0, elementTD.getRenderer().toString(),"");
							listeGroupes.add(groupe);
							//groupe.setGoupePere(listeGroupes.get(0));
							//addEnfant(numGroupe, 1, elementTD.getRenderer().toString());
							
							groupeNivPrecedents[1] = numGroupe;
							groupeNivPrecedents[2] = -1;
							groupeNivPrecedents[3] = -1;
							groupeNivPrecedents[4] = -1;
							
							nivPrecedent = 1;
						}
					} else {
						//Groupes Niveau 2
						if (nivPrecedent != 0) {
							log.info("getGroupes() - groupe 2 : "+elementTD.getRenderer().toString());
							
							numGroupe += 1;  
							groupe = new Groupe("G2-"+numGroupe, 2, 0, 0, elementTD.getRenderer().toString(),"");
							listeGroupes.add(groupe);
							//groupe.setGoupePere(groupeNivPrecedents[1]);
							
							//int numGroupe = groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.size();
							//groupeListeEnfants.get(groupeNivPrecedents[1]).addEnfant(numGroupe,	2, elementTD.getRenderer().toString());
							
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
							
								log.info("getGroupes() - groupe 3 - 4 ? elementIMG : "+elementIMG.getAttributeValue("src"));
								
								if (elementIMG.getAttributeValue("src").contains("/images_groupe/")) {
									listeElementsA = elementIMG.getParentElement().getParentElement().getParentElement().getAllElements(HTMLElementName.A);
									
									for (Element elementA : listeElementsA) {
										log.info("getGroupes() - elementA : "+elementA.toString());
										
										String elementAClass = elementA.getAttributeValue("class");
										if (elementAClass != null){
											//Groupes Niveau 3
											if (elementAClass.toString().equals("normal")){
																						
												if (nivPrecedent == 1){
													log.info("getGroupes() - groupe 3 : "+elementA.getRenderer().toString());
													
													numGroupe += 1;  
													groupe = new Groupe("G2-"+numGroupe, 2, 0, 0, elementA.getRenderer().toString(),"");
													listeGroupes.add(groupe);
													//int numGroupe = groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.size();;
													//groupeListeEnfants.get(groupeNivPrecedents[1]).addEnfant(numGroupe,	3, elementA.getRenderer().toString());
													//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(numGroupe).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());
													//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(numGroupe).setNumUrlGroupe(Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", "")));
													
													//if (groupeListeEnfants.get(groupeNivPrecedents[1]).urlVignette == ""){groupeListeEnfants.get(groupeNivPrecedents[1]).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());}
	
													groupeNivPrecedents[2] = numGroupe;
													groupeNivPrecedents[3] = -1;
											    	groupeNivPrecedents[4] = -1;
											    	
											    	nivPrecedent = 2;
												} else if (nivPrecedent >= 2){
													log.info("getGroupes() - groupe 3 : "+elementA.getRenderer().toString());
													
													groupe = new Groupe("G3-"+numGroupe, 3, 0, 0, elementA.getRenderer().toString(),"");
													listeGroupes.add(groupe);
													
													//int numGroupe = groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.size();
													//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).addEnfant(numGroupe,	3, elementA.getRenderer().toString());
													//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(numGroupe).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());
													//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(numGroupe).setNumUrlGroupe(Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", "")));
													
													//if (groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).urlVignette == ""){groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());}
													//if (groupeListeEnfants.get(groupeNivPrecedents[1]).urlVignette == ""){groupeListeEnfants.get(groupeNivPrecedents[1]).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());}
	
													groupeNivPrecedents[3] = numGroupe;
													groupeNivPrecedents[4] = -1;
											    	
											    	nivPrecedent = 3;
												}
											}
										}	
									}
								}
								log.info("getGroupes() - test  0");
								if (elementIMG.getAttributeValue("src").contains("/images_sousgroupe/")) {
									log.info("getGroupes() - test  1 - "+elementIMG.toString());
									
									listeElementsA = elementIMG.getParentElement().getParentElement().getParentElement().getAllElements(HTMLElementName.A);
									
									for (Element elementAG4 : listeElementsA) {
										log.info("getGroupes() - elementA : "+elementAG4.toString());
										
										String elementAClassG4 = elementAG4.getAttributeValue("class");
										if (elementAClassG4 != null){
											//Groupes Niveau 4
											if (elementAClassG4.toString().equals("normalgris2")){

												log.info("getGroupes() - groupe 4 : "+elementAG4.getRenderer().toString());
												
												groupe = new Groupe("G4-"+numGroupe, 4, 0, 0, elementAG4.getRenderer().toString(),"");
												listeGroupes.add(groupe);
												
												//int numGroupe = groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.size();
												//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).addEnfant(numGroupe,	4, elementAG4.getRenderer().toString());
												//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.get(numGroupe).setUrlVignette(racineSite+"/"+elementIMG.getAttributeValue("src").toString());
												//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.get(numGroupe).setNumUrlGroupe(groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).numUrlGroupe);
												//groupeListeEnfants.get(groupeNivPrecedents[1]).groupeListeEnfants.get(groupeNivPrecedents[2]).groupeListeEnfants.get(groupeNivPrecedents[3]).groupeListeEnfants.get(numGroupe).setNumUrlSsGroupe(Integer.parseInt(elementAG4.getAttributeValue("href").toString().replaceAll(".*sousgroupe_numero=(\\d+)&groupe_numero.*", "$1")));
													
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
		log.debug("getGroupes() - Fin");
		return listeGroupes;
    }

    
    
    
    
}