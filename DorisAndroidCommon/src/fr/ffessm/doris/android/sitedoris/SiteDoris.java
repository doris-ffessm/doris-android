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
import fr.ffessm.doris.android.datamodel.PhotoFiche;

public class SiteDoris {

	// Inititalisation de la Gestion des Log
	public static Log log = LogFactory.getLog(SiteDoris.class);
	

    
    public static List<Fiche> getListeFiches(String inCodePageHtml) {
    	log.debug("getListeFiches()- Début");
    	
    	List<Fiche> listeFiches = new ArrayList<Fiche>(0);
    	
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
    				
    				String dateCreation = ""; //TODO : Ça aurait été bien que la date de modif. apparaisse dans la page des noms scientifiques
    				String dateModification = "";
    				log.info("getGroupes() - fiche : "+ficheId+" - "+ficheNomScientifique+" - "+ficheNomCommun + " - Etat : " + ficheEtat);
    				
    				Fiche fiche = new Fiche(ficheNomScientifique, ficheNomCommun, ficheId, ficheEtat, dateCreation, dateModification);
      				
    				listeFiches.add(fiche);
    			}
			}
			
		}
		log.debug("getListeFiches()- Fin");
		return listeFiches;
    }
	
	
    
    public static List<Groupe> getListeGroupes(String inCodePageHtml){
    	log.debug("getGroupes() - Début");
    	
    	List<Groupe> listeGroupes = new ArrayList<Groupe>(0);
    	
    	int nivPrecedent = 0;
    	
    	// Création du groupe racine qui contiendra récurcivement tout l'arbre
    	// TODO : Supprimer sur les Créations de Groupe ci-dessous le commentaire tempo aidaant à debbeuger
    	Groupe groupe = new Groupe(0, 0, "racine","Tempo pour debug : 0-0");
    	Groupe groupeRacine = groupe;
    	Groupe groupeNiveau1Courant = null;
    	Groupe groupeNiveau2Courant = null;
    	Groupe groupeNiveau3Courant = null;
    	
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
							
							groupe = new Groupe(0, 0, elementTD.getRenderer().toString(),"Tempo pour debug : 1-0");
							listeGroupes.add(groupe);
							groupe.setGroupePere(groupeRacine);
							
							groupeNiveau1Courant = groupe;
							nivPrecedent = 1;
						}
					} else {
						//Groupes Niveau 2
						if (nivPrecedent != 0) {
							log.info("getGroupes() - groupe 2 : "+elementTD.getRenderer().toString());
							
							groupe = new Groupe(0, 0, elementTD.getRenderer().toString(),"Tempo pour debug : 2-0");
							listeGroupes.add(groupe);
							
							if (nivPrecedent >= 1) {
								groupe.setGroupePere(groupeNiveau1Courant);
							} else {
								groupe.setGroupePere(groupeRacine);
							}
							
							groupeNiveau2Courant = groupe;
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
													
													groupe = new Groupe(Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", "")), 0, elementA.getRenderer().toString(),"Tempo pour debug : 3-1");
													listeGroupes.add(groupe);
	
													groupe.setGroupePere(groupeNiveau1Courant);
													
													groupeNiveau2Courant = groupe;
											    	nivPrecedent = 2;
												} else if (nivPrecedent >= 2){
													log.info("getGroupes() - groupe 3 : "+elementA.getRenderer().toString());
													
													groupe = new Groupe(Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", "")), 0, elementA.getRenderer().toString(),"Tempo pour debug : 3-2");
													listeGroupes.add(groupe);

													groupe.setGroupePere(groupeNiveau2Courant);

													groupeNiveau3Courant = groupe;
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
												
												groupe = new Groupe(groupeNiveau3Courant.getNumeroGroupe(), Integer.parseInt(elementAG4.getAttributeValue("href").toString().replaceAll(".*sousgroupe_numero=(\\d+)&groupe_numero.*", "$1")), elementAG4.getRenderer().toString(),"Tempo pour debug : 4-0");
												listeGroupes.add(groupe);
												
												groupe.setGroupePere(groupeNiveau3Courant);
												
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

    public static Groupe getGroupeFromListeGroupes(List<Groupe> listeGroupes, int numGroupe, int numSousGroupe){
    	log.debug("getGroupeFromListeGroupes() - Début");
    	log.debug("getGroupeFromListeGroupes() - numGroupe : "+numGroupe);
    	log.debug("getGroupeFromListeGroupes() - numSousGroupe : "+numSousGroupe);
    	
    	// Contrôle basique des entrées
    	if (numGroupe == 0) {
    		log.error("getGroupeFromListeGroupes() - refGroupe ne peut être égal à 0");
    		return null;
    	}
    	
    	for (Groupe groupe : listeGroupes) {
    	
    		if ( groupe.getNumeroGroupe() == numGroupe && ( numSousGroupe == 0 || groupe.getNumeroSousGroupe() == numSousGroupe) ) {
    			log.debug("getGroupeFromListeGroupes() - Groupe Trouvé : "+groupe.getId()+" - "+groupe.getNomGroupe());
    			log.debug("getGroupeFromListeGroupes() - Fin");
    			return groupe;
    		}
    		
    	}
    	
    	log.debug("getGroupeFromListeGroupes() - Fin (sans avoir trouvé de groupe correspondant)");
		return null;
    }



	public static List<PhotoFiche> getListePhotosFiche(Fiche fiche,
			String inCodePageHtml) {
		log.debug("getListePhotosFiche()- Début");
    	
    	List<PhotoFiche> listePhotosFiche = new ArrayList<PhotoFiche>(0);
    	
    	Source source=new Source(Outils.nettoyageBalises(inCodePageHtml));
    	source.fullSequentialParse();
    	log.debug("getListePhotosFiche()- source.length() : " + source.length());
    	log.debug("getListePhotosFiche()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));
    	
    	Element elementTableracine=source.getFirstElementByClass("titre2").getParentElement().getParentElement();
    	List<? extends Element> listeElementsTD = elementTableracine.getAllElements(HTMLElementName.TD);
    	log.debug("getListePhotosFiche() - listeElementsTD.size() : " + listeElementsTD.size());
    	
    	// titre de la photo courante
    	String titrePhotoCourante = null;
    	// descritioon de la photo courante
    	String descritionPhotoCourante = null;
    	
    	for (Element elementTD : listeElementsTD) {
    		if(elementTD.getAttributeValue("class").equals("normal_noir_gras")){
    			// c'est le titre
    	    	Element titrePhotoCouranteElem = null;
    			titrePhotoCouranteElem = elementTD;
    			titrePhotoCourante = titrePhotoCouranteElem.getRenderer().toString();
    		}
    		if(elementTD.getAttributeValue("class").equals("normal")){
    			// c'est la description
    	    	Element descritionPhotoCouranteElem = null;
    			descritionPhotoCouranteElem = elementTD;
    			descritionPhotoCourante = descritionPhotoCouranteElem.getRenderer().toString();
    		}
    		if(elementTD.getAttributeValue("class").equals("liste1")){
    			// c'est l'image
    			Element elementIMG = elementTD.getFirstElement(HTMLElementName.IMG);
    			String cleURL = elementIMG.getAttributeValue("src");
    			cleURL = cleURL.substring(cleURL.lastIndexOf("/"), cleURL.length()); // garde seulement le nom du fichier
    			PhotoFiche photoFiche = new PhotoFiche(cleURL,titrePhotoCourante, descritionPhotoCourante);
  				
    			listePhotosFiche.add(photoFiche);
    			
    			titrePhotoCourante = null;
    			descritionPhotoCourante = null;
    		}
    	}
		return listePhotosFiche;
	}
    
}