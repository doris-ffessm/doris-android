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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;

public class SiteDoris {

	// Initialisation de la Gestion des Log
	public static Log log = LogFactory.getLog(SiteDoris.class);
	

    
    public static HashSet<Fiche> getListeFichesFromHtml(String inCodePageHtml) {
    	log.trace("getListeFichesFromHtml()- Début");
    	
    	HashSet<Fiche> listeFiches = new HashSet<Fiche>(0);
    	
    	Source source=new Source(Outils.remplacementBalises(Outils.nettoyageBalises(inCodePageHtml),false ) );
    	source.fullSequentialParse();
    	log.debug("getListeFichesFromHtml()- source.length() : " + source.length());
    	//log.debug("getListeFiches()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));

    	Element elementTableracine=source.getFirstElementByClass("titre_page").getParentElement().getParentElement();
    	//log.debug("getListeFiches()- elementTableracine.length() : " + elementTableracine.length());
    	//log.debug("getListeFiches()- elementTableracine : " + elementTableracine.toString().substring(0, Math.min(100, elementTableracine.toString().length())));

    	List<? extends Element> listeElementsTD = elementTableracine.getAllElements(HTMLElementName.TD);
    	log.debug("getListeFichesFromHtml() - listeElementsTD.size() : " + listeElementsTD.size());
		
    	for (Element elementTD : listeElementsTD) {
    		//log.debug("getListeFiches() - elementTD.length() : " + elementTD.length());
    		//log.debug("getListeFiches()- elementTD : " + elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
    		
    		String elementTDwidth = elementTD.getAttributeValue("width");
			if (elementTDwidth != null){
    			if (elementTDwidth.toString().equals("75%")) {
    				//log.debug("getListeFiches() - elementTD : "+elementTD.getRenderer());
    				Element elementTDA = elementTD.getFirstElement(HTMLElementName.A);
    				
    				String contenu = elementTDA.getRenderer().toString();
    				//log.debug("getListeFiches() - contenu : "+contenu);
    				
    				String ficheNomScientifique = contenu.replaceAll("([^-]*)-(.*)", "$1").trim();
    				String ficheNomCommun = contenu.replaceAll("([^-]*)-(.*)", "$2").trim();
    				int ficheId = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "").replaceAll("&.*", ""));
    				int ficheEtat = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_etat=", "").replaceAll("&.*", ""));
    				
    				String dateCreation = ""; //TODO : Ça aurait été bien que la date de modif. apparaisse dans la page des noms scientifiques
    				String dateModification = "";
    				log.info("getListeFichesFromHtml() - fiche : "+ficheId+" - "+ficheNomScientifique+" - "+ficheNomCommun + " - Etat : " + ficheEtat);
    				
    				String textePourRechercheRapide = ficheNomCommun+" "+ficheNomScientifique;
    				Fiche fiche = new Fiche(ficheNomScientifique, ficheNomCommun, ficheId, ficheEtat, dateCreation, dateModification, "", Outils.formatStringNormalizer(textePourRechercheRapide).toLowerCase(), "" );
      				
    				listeFiches.add(fiche);
    			}
			}
			
		}
		log.trace("getListeFichesFromHtml()- Fin");
		return listeFiches;
    }
	
	
    
    public static List<Groupe> getListeGroupesFromHtml(String inCodePageHtml){
    	log.trace("getGroupes() - Début");
    	
    	List<Groupe> listeGroupes = new ArrayList<Groupe>(0);
    	
    	int nivPrecedent = 0;
    	
    	// Création du groupe racine qui contiendra récursivement tout l'arbre
    	// TODO : Supprimer sur les Créations de Groupe ci-dessous le commentaire tempo aidant à debugger
    	Groupe groupe = new Groupe(0, 0, "racine","Les grands groupes", "", "");
    	Groupe groupeRacine = groupe;
    	Groupe groupeNiveau1Courant = null;
    	Groupe groupeNiveau2Courant = null;
    	Groupe groupeNiveau3Courant = null;
    	
    	listeGroupes.add(groupe);
    	
    	Source source=new Source(Outils.remplacementBalises(Outils.nettoyageBalises(inCodePageHtml), false ) );
    	source.fullSequentialParse();
    	//log.debug("getGroupes()- source.length() : " + source.length());
    	
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
				//log.info("getGroupes() - elementTR : "+elementTR.toString().substring(0, Math.min(100, elementTR.toString().length())));
				//Groupes Niveau 1 et Niveau 2
				elementTD = elementTR.getFirstElementByClass("titre2");
				if (elementTD != null) {
					//log.info("getGroupes() - elementTD : "+elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
					//Groupes Niveau 1
					Element elementIMG = elementTD.getFirstElement(HTMLElementName.IMG);
					if (elementIMG != null) {
						if (elementIMG.getAttributeValue("src").contains("pucecarre.gif")) {
							String nom = elementTD.getRenderer().toString().replaceAll("\\(.*", "").trim();
							String description = elementTD.getRenderer().toString().replaceAll(".*\\((.*)\\).*", "$1").trim();
							if (nom.equals(description)) description = "";
							log.info("getGroupes() - groupe 1 : "+nom+" - "+description);
							
							groupe = new Groupe(0, 0, nom, description, "images/pucecarre.gif", "");
							listeGroupes.add(groupe);
							groupe.setGroupePere(groupeRacine);

							groupeNiveau1Courant = groupe;
							nivPrecedent = 1;
						}
					} else {
						//Groupes Niveau 2
						if (nivPrecedent != 0) {
							
							String nom = elementTD.getRenderer().toString().replaceAll("\\(.*", "").trim();
							String description = elementTD.getRenderer().toString().replaceAll(".*\\((.*)\\).*", "$1").trim();
							if (nom.equals(description)) description = "";
							
							groupe = new Groupe(0, 0, nom, description, "", "");
							listeGroupes.add(groupe);
							
							if (nivPrecedent >= 1) {
								groupe.setGroupePere(groupeNiveau1Courant);
								log.info("getGroupes() - groupe 2 : "+nom+" - "+description+" <- "+groupeNiveau1Courant.getNomGroupe());
							} else {
								groupe.setGroupePere(groupeRacine);
								log.info("getGroupes() - groupe 2 : "+nom+" - "+description+" <- "+groupeRacine.getNomGroupe());
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
							
								//log.info("getGroupes() - groupe 3 - 4 ? elementIMG : "+elementIMG.getAttributeValue("src"));
								
								if (elementIMG.getAttributeValue("src").contains("/images_groupe/")) {
									listeElementsA = elementIMG.getParentElement().getParentElement().getParentElement().getAllElements(HTMLElementName.A);
									
									for (Element elementA : listeElementsA) {
										//log.info("getGroupes() - elementA : "+elementA.toString());
										
										String elementAClass = elementA.getAttributeValue("class");
										if (elementAClass != null){
											//Groupes Niveau 3
											if (elementAClass.toString().equals("normal")){
																						
												if (nivPrecedent == 1){
													
													Integer numGroupe = Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", ""));
													String nom = elementA.getRenderer().toString().replaceAll("\\(.*", "").trim();
													String description = elementA.getRenderer().toString().replaceAll(".*\\((.*)\\).*", "$1").trim();
													if (nom.equals(description)) description = "";
													log.info("getGroupes() - groupe 2 : "+nom+" - "+description+" <- "+groupeNiveau1Courant.getNomGroupe());
													
													String urlPhotoGroupe = elementIMG.getAttributeValue("src").toString();
													
													groupe = new Groupe( numGroupe, 0, nom, description, urlPhotoGroupe, "");
													listeGroupes.add(groupe);
	
													groupe.setGroupePere(groupeNiveau1Courant);
													
													groupeNiveau2Courant = groupe;
											    	nivPrecedent = 2;
												} else if (nivPrecedent >= 2){
													
													Integer numGroupe = Integer.parseInt(elementA.getAttributeValue("href").toString().replaceAll(".*=", ""));
													String nom = elementA.getRenderer().toString().replaceAll("\\(.*", "").trim();
													String description = elementA.getRenderer().toString().replaceAll(".*\\((.*)\\).*", "$1").trim();
													if (nom.equals(description)) description = "";
													log.info("getGroupes() - groupe 3 : "+numGroupe+" - "+nom+" - "+description+" <- "+groupeNiveau2Courant.getNomGroupe());
													
													// Récupération de la vignette du Groupe
													// TODO : Prévoir dans la base de donnée son URL et son nom (son nom = en fait le numéro du Groupe)
													// (attention il faudra l'afficher sur fond blanc : sinon pas propre visuellement
													String urlPhotoGroupe = elementIMG.getAttributeValue("src").toString();
													
													groupe = new Groupe(numGroupe, 0, nom, description, urlPhotoGroupe, "");
													listeGroupes.add(groupe);

													groupe.setGroupePere(groupeNiveau2Courant);

													if (groupeNiveau2Courant.getCleURLImage().isEmpty()) groupeNiveau2Courant.setCleURLImage(urlPhotoGroupe);

													groupeNiveau3Courant = groupe;
											    	nivPrecedent = 3;
												}
											}
										}	
									}
								}
								//log.info("getGroupes() - test  0");
								if (elementIMG.getAttributeValue("src").contains("/images_sousgroupe/")) {
									//log.info("getGroupes() - test  1 - "+elementIMG.toString());
									
									listeElementsA = elementIMG.getParentElement().getParentElement().getParentElement().getAllElements(HTMLElementName.A);
									
									for (Element elementAG4 : listeElementsA) {
										//log.info("getGroupes() - elementA : "+elementAG4.toString());
										
										String elementAClassG4 = elementAG4.getAttributeValue("class");
										if (elementAClassG4 != null){
											//Groupes Niveau 4
											if (elementAClassG4.toString().equals("normalgris2")){
												Integer numGroupe = Integer.parseInt(elementAG4.getAttributeValue("href").toString().replaceAll(".*sousgroupe_numero=(\\d+)&groupe_numero.*", "$1"));
												String nom = elementAG4.getRenderer().toString().replaceAll("\\x85","...").replaceAll("\\(.*", "").trim();
												String description = elementAG4.getRenderer().toString().replaceAll("\\x85","...").replaceAll(".*\\((.*)\\).*", "$1").trim();
												if (nom.equals(description)) description = "";
												log.info("getGroupes() - groupe 4 : "+numGroupe+" - "+nom+" - "+description+" <- "+groupeNiveau3Courant.getNomGroupe());

												// Récupération de la vignette du Groupe
												String urlPhotoGroupe = elementIMG.getAttributeValue("src").toString();
												
												groupe = new Groupe(groupeNiveau3Courant.getNumeroGroupe(), numGroupe, nom, description, urlPhotoGroupe, "");
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
		log.trace("getGroupes() - Fin");
		return listeGroupes;
    }

    public static Groupe getGroupeFromListeGroupes(List<Groupe> listeGroupes, int numGroupe, int numSousGroupe){
    	log.trace("getGroupeFromListeGroupes() - Début");
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
    			log.trace("getGroupeFromListeGroupes() - Fin");
    			return groupe;
    		}
    		
    	}
    	
    	log.debug("getGroupeFromListeGroupes() - Fin (sans avoir trouvé de groupe correspondant)");
		return null;
    }



	public static List<PhotoFiche> getListePhotosFicheFromHtml(Fiche fiche,
			String inCodePageHtml) {
		log.trace("getListePhotosFiche()- Début");
    	
		List<PhotoFiche> listePhotosFiche = new ArrayList<PhotoFiche>(0);
    	
    	Source source=new Source( Outils.remplacementBalises( Outils.nettoyageBalises(inCodePageHtml), false ) );
    	source.fullSequentialParse();
    	//log.debug("getListePhotosFiche()- source.length() : " + source.length());
    	//log.debug("getListePhotosFiche()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));
    	
    	Element elementTableRacine=source.getFirstElementByClass("titre2").getParentElement().getParentElement();
    	List<? extends Element> listeElementsTD = elementTableRacine.getAllElements(HTMLElementName.TD);
    	//log.debug("getListePhotosFiche() - listeElementsTD.size() : " + listeElementsTD.size());
    	
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
    			titrePhotoCourante = titrePhotoCouranteElem.getRenderer().toString().trim();
    			titrePhotoCourante = titrePhotoCourante.replaceAll("[0-9].-", "").trim();
    			titrePhotoCourante = titrePhotoCourante.replaceAll("[0-9].", "").trim();
    		}
    		if(elementTD.getAttributeValue("class").equals("normal")){
    			// c'est la description
    	    	Element descritionPhotoCouranteElem = null;
    			descritionPhotoCouranteElem = elementTD;
    			descritionPhotoCourante = descritionPhotoCouranteElem.getRenderer().toString();
    			
    			descritionPhotoCourante = Outils.nettoyageTextes(descritionPhotoCourante);
    			
    			//Suppression Balises (A en particulier)
    			//TODO : Mettre un lien un jour si ergonomie le permet
    			descritionPhotoCourante = descritionPhotoCourante.replaceAll("<[^>]*>", "").trim();
    		}
    		if(elementTD.getAttributeValue("class").equals("liste1")){
    			// c'est l'image
    			Element elementIMG = elementTD.getFirstElement(HTMLElementName.IMG);
    			if(elementIMG != null){
	    			String cleURL = elementIMG.getAttributeValue("src");
	    			cleURL = cleURL.substring(cleURL.lastIndexOf("/"), cleURL.length()); // garde seulement le nom du fichier 
	    			cleURL = cleURL.replace(" ", "%20");	// on s'assure d'avoir une url valide
	    			if (listePhotosFiche.isEmpty()) {
	    				//Image Principale
	    				PhotoFiche photoFiche = new PhotoFiche(cleURL,titrePhotoCourante, descritionPhotoCourante, true);
	    				listePhotosFiche.add(photoFiche);
	    			} else {
	    				PhotoFiche photoFiche = new PhotoFiche(cleURL,titrePhotoCourante, descritionPhotoCourante);
	    				listePhotosFiche.add(photoFiche);
	    			}
	    			
	    			
	    			titrePhotoCourante = null;
	    			descritionPhotoCourante = null;
    			}
    			else{
    				log.warn("getListePhotosFiche() - ignore photo incorrecte pour la fiche "+fiche.getNumeroFiche());
    				// ignore l'image si image manquante
    				continue;
    			}
    		}
    	}
		return listePhotosFiche;
	}
    
	

    public static List<Participant> getListeParticipantsParInitialeFromHtml(String inCodePageHtml){
    	log.debug("getListeParticipantsParInitiale() - Début");
    	
    	List<Participant> listeParticipants = new ArrayList<Participant>(0);
    	
    	Source source=new Source(Outils.remplacementBalises(Outils.nettoyageBalises(inCodePageHtml), true ) );
    	source.fullSequentialParse();
    	log.debug("getListeParticipantsParInitiale()- source.length() : " + source.length());
    	
    	/* Pour trouver les Participants
    	 *	Rechercher 1er TD Class = titre2
    	 *		Remonter de 6 niveaux => TABLE
    	 *
    	 *	Pour chaque TR fils direct de TABLE
    	 *		Si Num TR modulo 4 = 1
    	 *			Rechercher TD Class = titre2 => Contenu texte = Nom du Participant
         *    
         *		Si Num TR modulo 4 = 3
         *			Recherche A Class = lien_email et href <> "" => Derrière le = on a la 
         *référence du participant
         *    	 Pour toutes IMG :
         *    Si alt=Photographe alors Participant est Photographe
         *    Si alt=Rédacteur alors Participant est Rédacteur
         *    Si alt=Vérificateur alors Participant est Vérificateur
    	 */
    	
    	Element element1erTRTitre2 = source.getFirstElementByClass("titre2");
    	// La page U n'a par exemple aucun Participant
    	if (element1erTRTitre2 == null) {
    		return listeParticipants;
    	}
    	
    	Element elementTABLERacine = element1erTRTitre2.getParentElement().getParentElement().getParentElement().getParentElement().getParentElement();
    	List<? extends Element> listeElementsTR = elementTABLERacine.getAllElements(HTMLElementName.TR);
		int profondeurTRlignes = elementTABLERacine.getFirstElement(HTMLElementName.TR).getDepth();
    	
		String participantId = "";
		String participantNom = "";
		String participantKind = "";
		String participantUrlPhoto = "";
		String participantDescription = "";
		
		int numeroTR = 0;
    	for (Element elementTR : listeElementsTR) {
			if ( elementTR.getDepth() == profondeurTRlignes ) {
				numeroTR++;
				
				//log.info("getListeParticipantsParInitiale() - elementTR : "+numeroTR+" - "+elementTR.toString().substring(0, Math.min(100, elementTR.toString().length())));
				
				if (numeroTR % 4 == 1){
					Element elementTDTitre2ParticipantNom = elementTR.getFirstElementByClass("titre2");
					log.info("getListeParticipantsParInitiale() - Nom Participant : "+elementTDTitre2ParticipantNom.getRenderer() );
					participantNom = elementTDTitre2ParticipantNom.getRenderer().toString().trim();
					
					List<? extends Element> listeElementsTDTitre2ParticipantKind = elementTR.getAllElementsByClass("normal_gris_gras");
					for (Element elementTDTitre2 : listeElementsTDTitre2ParticipantKind) {

						if ( elementTDTitre2.getFirstElement(HTMLElementName.IMG) != null ){
							String kind = elementTDTitre2.getFirstElement(HTMLElementName.IMG).getAttributeValue("alt");
							log.info("getListeParticipantsParInitiale() - Participant Kind : "+ kind + " - " +Constants.getTypeParticipant(kind));
							//TODO : Il faudra mettre autre chose
							participantKind += Constants.getTypeParticipant(kind).ordinal()+";";
						} else {
							log.info("getListeParticipantsParInitiale() - Participant Provenance : "+elementTDTitre2.getRenderer().toString().trim() );
							participantDescription = elementTDTitre2.getRenderer().toString().trim()+"<br/>";
						}
					}
				}
				
				if (numeroTR % 4 == 3){
					Element elementAlien_emailParticipantId = elementTR.getFirstElementByClass("lien_email");
					//log.info("getListeParticipantsParInitiale() - id Participant : "+elementAlien_emailParticipantId.getAttributeValue("href") );
					String href = ""+elementAlien_emailParticipantId.getAttributeValue("href");
					participantId = href.substring(Math.min(href.length(), href.indexOf("=")+1 ) );
					//log.info("getListeParticipantsParInitiale() - debug 1 : "+participantId+" - href : "+href+" - "+ href.indexOf("=") );
					if (href.indexOf("=")==-1){
						Element elementAlien_fichecontactParticipantId = elementTR.getFirstElementByClass("lien_fichecontact");
						href = ""+elementAlien_fichecontactParticipantId.getAttributeValue("href");
						//log.info("getListeParticipantsParInitiale() - href : "+href+" - "+ href.indexOf("=") );
						
						participantId = href.substring(Math.min(href.length(), href.indexOf("=")+1) );
						//log.info("getListeParticipantsParInitiale() - debug 2 : "+participantId );

					}
					if (participantId.indexOf("&")!=-1){
						participantId=participantId.substring(0, participantId.indexOf("&") );
					}
					log.info("getListeParticipantsParInitiale() - id Participant : "+participantId);
					
					List<? extends Element> listeElementsIMGParticipantPhoto = elementTR.getAllElements(HTMLElementName.IMG);
					for (Element elementIMG : listeElementsIMGParticipantPhoto) {
						String elementTDwidth = elementIMG.getAttributeValue("width");
						if (elementTDwidth != null && elementTDwidth.equals("150") ){
							//log.info("getListeParticipantsParInitiale() - photo : "+elementIMG.getAttributeValue("src"));
							participantUrlPhoto = elementIMG.getAttributeValue("src");
							participantUrlPhoto = participantUrlPhoto.replace(" ", "%20");	// on s'assure d'avoir une url valide
						}
					}

					List<? extends Element> listeElementsTD = elementTR.getAllElements(HTMLElementName.TD);
					int numeroTD = 0;
					for (Element elementTD : listeElementsTD) {
						String elementTDheight = elementTD.getAttributeValue("height");
						String elementTDclass = elementTD.getAttributeValue("class");
						if (elementTDheight != null && elementTDheight.equals("20") ) {
							if (elementTDclass != null && elementTDclass.equals("normal") ) {
								numeroTD ++;
								//log.info("getListeParticipantsParInitiale() - numeroTD : "+numeroTD+" - "+elementTD.getContent().toString());
								if (numeroTD==1){
									if (elementTD.getContent().toString().contains("images/18_probe.gif")) {
										String siteWeb = elementTD.getFirstElement("src", Pattern.compile("images/18_probe\\.gif")).getParentElement().getRenderer().toString().trim();
										//log.info("getListeParticipantsParInitiale() - Site Web : "+siteWeb);
										participantDescription += "Site Web : <a href=\""+siteWeb+"\">"+siteWeb+"</a><br/>";
									}
								} else if (numeroTD==2){
									String description = elementTD.getRenderer().toString();
									//description = Outils.remplacementBalises(description, true);
									//description = Outils.nettoyageTextes(description);
									
									log.info("getListeParticipantsParInitiale() - Description : "+description);
									if (!description.isEmpty()) participantDescription += description+"<br/>";
								}
							}
						}
					}
				}
				if (numeroTR % 4 == 3){
					participantDescription = Outils.remplacementBalises(participantDescription, true);
					participantDescription = Outils.nettoyageTextes(participantDescription);
					log.info("getListeParticipantsParInitiale() - participantKind : "+participantKind);
					log.info("getListeParticipantsParInitiale() - participantDescription : "+participantDescription);
					
					listeParticipants.add(new Participant( participantNom, Integer.valueOf(participantId), participantUrlPhoto, participantKind, participantDescription) );
					participantNom = "";
					participantId = "";
					participantKind = "";
					participantUrlPhoto = "";
					participantDescription = "";
				}
			}
    	} // Fin Pour Chaque TR
    	
    	log.debug("getListeParticipantsParInitiale() - listeParticipants : "+listeParticipants.size());
		log.debug("getListeParticipantsParInitiale() - Fin");
		return listeParticipants;
    }
    
    public static HashSet<Fiche> getListeFichesUpdated(HashSet<Fiche> inListeFichesRef, HashSet<Fiche> inListeFichesSite) {
    	log.debug("getListeFichesUpdated()- Début");
    	log.debug("getListeFichesUpdated()- Liste Base : "+inListeFichesRef.size());
    	log.debug("getListeFichesUpdated()- Liste Site : "+inListeFichesSite.size());
    	
    	HashSet<Fiche> listeFichesUpdated = new HashSet<Fiche>(0);
     	
    	HashSet<String> listeFichesEtatsRef = new HashSet<String>(0);
    	// On charge une liste de tous les couples : Ref. Fiche - État Fiche
    	Iterator<Fiche> iFicheRef = inListeFichesRef.iterator();
    	while (iFicheRef.hasNext()) {
    		Fiche ficheRef = iFicheRef.next();
    		listeFichesEtatsRef.add( ficheRef.getRefEtatFiche() );
    	}
    	
    	Iterator<Fiche> iFicheSite = inListeFichesSite.iterator();
    	while (iFicheSite.hasNext()) {
    		// Si Nouvelle Fiche ou État a changé alors le couple ne peut être trouvé dans la liste de référence
    		// Tentative d'optime pour avoir une empreinte mémoire la plus faible
    		Fiche ficheSite = iFicheSite.next();
    		if ( ! listeFichesEtatsRef.contains( ficheSite.getRefEtatFiche() ) ){
    			listeFichesUpdated.add(ficheSite);
    		}
    	}
    	log.debug("getListeFichesUpdated()- Liste Site Updated : "+listeFichesUpdated.size());
    	
		log.debug("getListeFichesUpdated()- Fin");
		return listeFichesUpdated;
    }
    
    public static Participant getParticipantFromListeParticipants(List<Participant> listeParticipants, int numParticipant){
    	//log.trace("getParticipantFromListeParticipants() - Début");
    	//log.debug("getParticipantFromListeParticipants() - numParticipant : "+numParticipant);
    	
    	// Contrôle basique des entrées
    	if (numParticipant == 0) {
    		log.error("getParticipantFromListeParticipants() - numParticipant ne peut être égal à 0");
    		return null;
    	}
    	
    	for (Participant participant : listeParticipants) {
    	
    		if ( participant.getId() == numParticipant) {
    			log.debug("getParticipantFromListeParticipants() - Participant Trouvé : "+participant.getId());
    			//log.trace("getParticipantFromListeParticipants() - Fin");
    			return participant;
    		}
    		
    	}
    	
    	//log.debug("getParticipantFromListeParticipants() - Fin (sans avoir trouvé de Participant correspondant)");
		return null;
    }
    

    public static List<DefinitionGlossaire> getListeDefinitionsParInitialeFromHtml(String inCodePageHtml){
    	//log.debug("getListeDefinitionsParInitialeFromHtml() - Début");
    	
    	List<DefinitionGlossaire> listeDefinitions = new ArrayList<DefinitionGlossaire>(0);
    	
    	Source source=new Source(Outils.remplacementBalises(Outils.nettoyageBalises(inCodePageHtml), false ) );
    	source.fullSequentialParse();
    	//log.debug("getListeDefinitionsParInitialeFromHtml()- source.length() : " + source.length());
    	
    	List<? extends Element> listeElementsTD = source.getAllElementsByClass("liste0");
    			
    	for (Element elementTD : listeElementsTD) {
    		log.debug("getListeDefinitionsParInitialeFromHtml()- elementTD : " +elementTD.getRenderer().toString());
			if (elementTD.getRenderer().toString().trim().replaceAll("<[^>]*>", "").isEmpty() ) {
				String numeroDefinition =  elementTD.getRenderer().toString().trim().replaceAll(".*glossaire_numero=([^&]*)&.*", "$1");
				log.debug("getListeDefinitionsParInitialeFromHtml()- numeroDefinition : " +numeroDefinition);
			
				listeDefinitions.add(new DefinitionGlossaire(Integer.valueOf(numeroDefinition),"","",""));
			
			}
    	} // Fin Pour Chaque TR
    	
    	//log.debug("getListeDefinitionsParInitialeFromHtml() - listeDefinitions : "+listeDefinitions.size());
		//log.debug("getListeDefinitionsParInitialeFromHtml() - Fin");
		return listeDefinitions;
    }

    public static boolean getContinuerListeDefinitionsParInitialeFromHtml(String inCodePageHtml){
    	//log.debug("getContinuerListeDefinitionsParInitialeFromHtml() - Début");
    	boolean continuer = true;
    	
    	String indexPage = inCodePageHtml.replaceAll("\n", "").replaceAll(".*>Page ([^<>]*)<.*", "$1").trim();
    	//log.debug("getContinuerListeDefinitionsParInitialeFromHtml() - indexPage :"+indexPage);
    	String numeroPageCourante = indexPage.replaceAll("/.*", "").trim();
    	//log.debug("getContinuerListeDefinitionsParInitialeFromHtml() - numeroPageCourante :"+numeroPageCourante);
    	String nbPages = indexPage.replaceAll(".*/", "").trim();
    	//log.debug("getContinuerListeDefinitionsParInitialeFromHtml() - nbPages :"+nbPages);
    	if (numeroPageCourante.equals(nbPages)) continuer = false;
    	
    	//log.debug("getContinuerListeDefinitionsParInitialeFromHtml() - continuer :"+continuer);
    	return continuer;
    }
    
    public static boolean getContinuerContenuGroupeFromHtml(String inCodePageHtml){
    	//log.debug("getContinuerContenuGroupeFromHtml() - Début");
    	boolean continuer = true;
    	
    	String indexPage = Pattern.compile(".*>Page ([^<>]*)<.*", Pattern.DOTALL).matcher(inCodePageHtml).replaceAll("$1");
     	//log.debug("getContinuerContenuGroupeFromHtml() - indexPage :"+indexPage);
    	String numeroPageCourante = indexPage.replaceAll("/.*", "").trim();
    	//log.debug("getContinuerContenuGroupeFromHtml() - numeroPageCourante :"+numeroPageCourante);
    	String nbPages = indexPage.replaceAll(".*/", "").trim();
    	//log.debug("getContinuerContenuGroupeFromHtml() - nbPages :"+nbPages);
    	if (numeroPageCourante.equals(nbPages)) continuer = false;
    	
    	//log.debug("getContinuerContenuGroupeFromHtml() - continuer :"+continuer);
    	return continuer;
    }
    
    public static List<EntreeBibliographie> getListeBiblioFromHtml(String inCodePageHtml) {
    	log.trace("getListeBiblioFromHtml()- Début");
    	
    	List<EntreeBibliographie> listeBiblio = new ArrayList<EntreeBibliographie>(0);
    	
		// Une entrée bibliographique ne fonctionne pas, parce qu'elle est très longue
    	// il est probable qu'une astuce ait été utilisée, mais le site est tellement
    	// mal foutu qu'il arrive à générer une balise non terminée : </,
		// idBiblio : 618
		// auteurs : Jourdan A.-J.-L.
    	inCodePageHtml = Pattern.compile("</,[^<>]*<strong>", Pattern.DOTALL).matcher(inCodePageHtml).replaceAll(",");
	    	
    	Source source=new Source(Outils.nettoyageBalises(inCodePageHtml) );
    	source.fullSequentialParse();
    	//log.debug("getListeBiblioFromHtml()- source.length() : " + source.length());
    	//log.debug("getListeFiches()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));

    	List<? extends Element> listeElementsA = source.getAllElements(HTMLElementName.A);
    	//log.debug("getListeBiblioFromHtml() - listeElementsA.size() : " + listeElementsA.size());

    	for (Element elementA : listeElementsA) {
    		//log.debug("getListeFiches() - elementTD.length() : " + elementTD.length());
    		//log.debug("getListeFiches()- elementTD : " + elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
    		
    		String elementAClass = elementA.getAttributeValue("class");
			if (elementAClass != null){
    			if (elementAClass.toString().equals("normal")) {
    	    		log.debug("getListeBiblioFromHtml() - - - - - - - -");
    				log.debug("getListeFiches() - elementA : " + elementA.getContent().toString().replaceAll("\n|\r", ""));
    				
    				String bibliographie = elementA.getRenderer().toString().replaceAll("\n|\r", "").trim();
    				//log.debug("getListeBiblioFromHtml() - bibliographie : " + bibliographie);
    				
    				String idBiblio = elementA.getAttributeValue("href").replaceAll("[^=]*=([0-9]*)", "$1");
    				log.debug("getListeBiblioFromHtml() - idBiblio : " + idBiblio);
    				
    				String annee = bibliographie.replaceAll(".*, ([0-9]{4}),.*", "$1").trim();
    				//log.debug("getListeBiblioFromHtml() - annee : " + annee);
    				
    				String auteurs = bibliographie.replaceAll("^(.*), "+annee+",.*$", "$1").trim();
    				log.debug("getListeBiblioFromHtml() - auteurs : " + auteurs);
    				
    				String titre = "";
    				log.debug("getListeFiches() - STRONG : " + elementA.getFirstElement(HTMLElementName.STRONG));
    				if (elementA.getFirstElement(HTMLElementName.STRONG) != null) {
    					titre = elementA.getFirstElement(HTMLElementName.STRONG).getRenderer().toString().trim().replaceAll("\n|\r", "").trim();
    				}
    				log.debug("getListeBiblioFromHtml() - titre : " + titre);
    				
    				
    				//String edition = bibliographie.replaceAll(".*"+titre+",(.*)$", "$1").trim();
    				String regExp = ".*"+titre+",(.*)$";
    				String edition = Pattern.compile(regExp, Pattern.DOTALL).matcher(bibliographie).replaceAll("$1");
    				//log.debug("getListeBiblioFromHtml() - edition : " + edition);
    				
    				// TODO : l'illustration éventuelle dans cleURLIllustration (mais il faudrait alors télécharger la page de l'entrée bibliographique
    				listeBiblio.add(new EntreeBibliographie( Integer.valueOf(idBiblio),
    						Outils.nettoyageTextes(titre).trim(),
    						Outils.nettoyageTextes(auteurs).trim(),
    						annee,
    						Outils.nettoyageTextes(edition).trim(),
    						"",
    						(Outils.formatStringNormalizer(titre+" "+auteurs)).toLowerCase() ));
    			}
			}
			
		}
		log.trace("getListeBiblioFromHtml()- Fin");
		return listeBiblio;
    }
	
	
    
    
}
