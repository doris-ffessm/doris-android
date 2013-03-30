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
package fr.ffessm.doris.android.datamodel;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;

import fr.ffessm.doris.android.datamodel.associations.*;
import fr.ffessm.doris.android.sitedoris.Outils;

// Start of user code additional import for Fiche
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
// End of user code

/** 
  * Fiche Doris, donne accés aux données de la fiche 
  */ 
@DatabaseTable(tableName = "fiche")
public class Fiche {

	public static final String XML_FICHE = "FICHE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NOMSCIENTIFIQUE = "nomScientifique";
	public static final String XML_ATT_NOMCOMMUN = "nomCommun";
	public static final String XML_ATT_NUMEROFICHE = "numeroFiche";
	public static final String XML_ATT_ETATFICHE = "etatFiche";
	public static final String XML_ATT_DATECREATION = "dateCreation";
	public static final String XML_ATT_DATEMODIFICATION = "dateModification";
	public static final String XML_REF_REDACTEURS = "redacteurs";
	public static final String XML_REF_PHOTOSFICHE = "photosFiche";
	public static final String XML_REF_ZONESGEOGRAPHIQUES = "zonesGeographiques";
	public static final String XML_REF_ZONESOBSERVATION = "zonesObservation";
	public static final String XML_REF_VERIFICATEURS = "verificateurs";
	public static final String XML_REF_RESPONSABLEREGIONAL = "responsableRegional";
	public static final String XML_REF_CONTENU = "contenu";
	public static final String XML_REF_PHOTOPRINCIPALE = "photoPrincipale";
	public static final String XML_REF_AUTRESDENOMINATIONS = "autresDenominations";
	public static final String XML_REF_GROUPE = "groupe";
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected int _id;
	

	@DatabaseField
	protected java.lang.String nomScientifique;

	@DatabaseField
	protected java.lang.String nomCommun;

	/** Numéro de la fiche tel que connu par le site lui même */ 
	@DatabaseField
	protected int numeroFiche;

	/** Etat Avancement de la fiche 
4 : Fiche Publiée - 1, 2, 3 : En cours de Rédaction - 5 : Fiche Proposée */ 
	@DatabaseField
	protected int etatFiche;

	@DatabaseField
	protected java.lang.String dateCreation;

	@DatabaseField
	protected java.lang.String dateModification;
	

	@DatabaseField(foreign = true)
	protected Participant redacteurs;

	/** Liste des photos de la fiche */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<PhotoFiche> photosFiche;

	/** zones géographiques où l'on peut trouver l'élément décrit par la fiche */ 
	public List<ZoneGeographique> lookupZonesGeographiques(DorisDBHelper contextDB) throws SQLException {
		if (zonesGeographiquesQuery == null) {
			zonesGeographiquesQuery = makeZonesGeographiquesQuery(contextDB);
		}
		zonesGeographiquesQuery.setArgumentHolderValue(0, this);
		return contextDB.zoneGeographiqueDao.query(zonesGeographiquesQuery);
	}
	private PreparedQuery<ZoneGeographique> zonesGeographiquesQuery = null;
	/**
	 * Build a query for ZoneGeographique objects that match a Fiche
	 */
	private PreparedQuery<ZoneGeographique> makeZonesGeographiquesQuery(DorisDBHelper contextDB) throws SQLException {
		// build our inner query for UserPost objects
		QueryBuilder<Fiches_ZonesGeographiques, Integer> fiches_ZonesGeographiquesQb = contextDB.fiches_ZonesGeographiquesDao.queryBuilder();
		// just select the post-id field
		fiches_ZonesGeographiquesQb.selectColumns(Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		// you could also just pass in user1 here
		fiches_ZonesGeographiquesQb.where().eq(Fiches_ZonesGeographiques.FICHE_ID_FIELD_NAME, userSelectArg);

		// build our outer query for Post objects
		QueryBuilder<ZoneGeographique, Integer> zoneGeographiqueQb = contextDB.zoneGeographiqueDao.queryBuilder();
		// where the id matches in the zoneGeographique-id from the inner query
		zoneGeographiqueQb.where().in("_id", fiches_ZonesGeographiquesQb);
		return zoneGeographiqueQb.prepare();
	}


				

	/** zones  où l'on peut observer l'élément décrit par la fiche */ 
	public List<ZoneObservation> lookupZonesObservation(DorisDBHelper contextDB) throws SQLException {
		if (zonesObservationQuery == null) {
			zonesObservationQuery = makeZonesObservationQuery(contextDB);
		}
		zonesObservationQuery.setArgumentHolderValue(0, this);
		return contextDB.zoneObservationDao.query(zonesObservationQuery);
	}
	private PreparedQuery<ZoneObservation> zonesObservationQuery = null;
	/**
	 * Build a query for ZoneObservation objects that match a Fiche
	 */
	private PreparedQuery<ZoneObservation> makeZonesObservationQuery(DorisDBHelper contextDB) throws SQLException {
		// build our inner query for UserPost objects
		QueryBuilder<Fiches_ZonesObservations, Integer> fiches_ZonesObservationsQb = contextDB.fiches_ZonesObservationsDao.queryBuilder();
		// just select the post-id field
		fiches_ZonesObservationsQb.selectColumns(Fiches_ZonesObservations.ZONEOBSERVATION_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		// you could also just pass in user1 here
		fiches_ZonesObservationsQb.where().eq(Fiches_ZonesObservations.FICHE_ID_FIELD_NAME, userSelectArg);

		// build our outer query for Post objects
		QueryBuilder<ZoneObservation, Integer> zoneObservationQb = contextDB.zoneObservationDao.queryBuilder();
		// where the id matches in the zoneObservation-id from the inner query
		zoneObservationQb.where().in("_id", fiches_ZonesObservationsQb);
		return zoneObservationQb.prepare();
	}


				

	/** listes des personnes ayant vérifié la fiche */ 
	public List<Participant> lookupVerificateurs(DorisDBHelper contextDB) throws SQLException {
		if (verificateursQuery == null) {
			verificateursQuery = makeVerificateursQuery(contextDB);
		}
		verificateursQuery.setArgumentHolderValue(0, this);
		return contextDB.participantDao.query(verificateursQuery);
	}
	private PreparedQuery<Participant> verificateursQuery = null;
	/**
	 * Build a query for Participant objects that match a Fiche
	 */
	private PreparedQuery<Participant> makeVerificateursQuery(DorisDBHelper contextDB) throws SQLException {
		// build our inner query for UserPost objects
		QueryBuilder<Fiches_verificateurs_Participants, Integer> fiches_verificateurs_ParticipantsQb = contextDB.fiches_verificateurs_ParticipantsDao.queryBuilder();
		// just select the post-id field
		fiches_verificateurs_ParticipantsQb.selectColumns(Fiches_verificateurs_Participants.PARTICIPANT_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		// you could also just pass in user1 here
		fiches_verificateurs_ParticipantsQb.where().eq(Fiches_verificateurs_Participants.FICHE_ID_FIELD_NAME, userSelectArg);

		// build our outer query for Post objects
		QueryBuilder<Participant, Integer> participantQb = contextDB.participantDao.queryBuilder();
		// where the id matches in the participant-id from the inner query
		participantQb.where().in("_id", fiches_verificateurs_ParticipantsQb);
		return participantQb.prepare();
	}


				

	/** responsable régional de la fiche */ 
	@DatabaseField(foreign = true)
	protected Participant responsableRegional;

	/** contenu textuel de la fiche */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<SectionFiche> contenu;

	/** Photo par défaut de l'espèce présentée par cette fiche. Elle est aussi présente dans la liste "photosFiche". */ 
	@DatabaseField(foreign = true)
	protected PhotoFiche photoPrincipale;

	/** Liste des autres dénominations de l'espèce présentée sur la fiche. */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<AutreDenomination> autresDenominations;

	/** Permet d'identifier avec le sous-groupe (optionel) le groupe auquel est rattaché la fiche */ 
	@DatabaseField(foreign = true)
	protected Groupe groupe;

	// Start of user code Fiche additional user properties
	
	// Inititalisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(Fiche.class);
	
	public void getFiche(String htmlFiche){
		log.debug("getFiche() - Début");
		
    	int i;
    	String listeLienRencontre = "";
    	
    	htmlFiche = Outils.nettoyageBalises(htmlFiche);
    	
    	
    	try {
			htmlFiche = Outils.ciblePage(htmlFiche, "FICHE");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	log.debug("getFiche() - htmlFiche : " + htmlFiche.substring(0, 200));
    	
		// Utilisation du parser Jericho
		Source source=new Source(htmlFiche);
		
		// Necessaire pour trouver ensuite les pères
		source.fullSequentialParse();

		// Recherche TD dont la class est code_fiche
		// Il contient le code fiche, le nom français, le nom latin et la zone géographique
		Element ElementTDcode_fiche;
		ElementTDcode_fiche = source.getFirstElementByClass("code_fiche");
		
		log.debug("getFiche() - ElementTDcode_fiche.toString() : " + ElementTDcode_fiche.toString());

		String ficheRef = ElementTDcode_fiche.getFirstElementByClass("normalgris").getRenderer().toString().trim();
		ficheRef = ficheRef.replace("(N°", "").replace(")", "");
		setNumeroFiche(Integer.parseInt(ficheRef));
		log.info("getFiche() - ref : " + ficheRef);		
				
		//Centrage sur la TABLE qui contient tout le texte et les images
		Element ElementTable;
		List<? extends Element> listeElementsTable_TABLE;
		List<? extends Attribute> listeAttributs;
		int num_table = 0;
		
		// Lecture des informations pour une fiche complète
		if ( getEtatFiche() == 4) {
		
			
			//Recup de l'ensemble des lignes (TR) de la TABLE
			// La 1ère contient l'entête
			// La 2ème contient la description
			// La 4ème contient la classification et la suite
			
			ElementTable=source.getFirstElementByClass("trait_cadregris").getFirstElement();
			log.debug("getFiche() - ElementTable : " + ElementTable.toString().substring(0, Math.min(ElementTable.toString().length(),200)));
			
			listeElementsTable_TABLE = ElementTable.getFirstElement(HTMLElementName.TABLE).getChildElements();
			log.debug("getFiche() - listeElementsTable_TABLE.size : " + listeElementsTable_TABLE.size());

			for (Element elementTable_TABLE : listeElementsTable_TABLE) {
				num_table++;
				log.debug("getFiche() - ligneTable_TR :" + num_table);
				log.debug("getFiche() - elementTable_TR : " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),100)));
				switch(num_table) {
				//Entête de la Fiche
				case 1 :
					//Recup du TD qui contient les infos Haut Gauche
					List<? extends Element> listeElementsHG_TD = elementTable_TABLE.getAllElementsByClass("code_fiche");
					
					for (Element element : listeElementsHG_TD) {
						List<? extends Element> listeElementsHG_TR = element.getAllElements(HTMLElementName.TR);
						i = 0;
						for (Element elementTR : listeElementsHG_TR) {
							i++;
							if (i == 2) {
								log.info("getFiche() - ficheNomLatin : " + elementTR.getRenderer().toString());
								setNomScientifique( Outils.nettoyageCaracteres(elementTR.getRenderer().toString().trim()) );
							}
							if (i == 3) {
								log.info("getFiche() - ficheRegion : " + elementTR.getRenderer().toString());
								// TODO :
								//ficheRegion = elementTR.getRenderer().toString().trim();
							}
							if (i == 5) {
								log.info("getFiche() - ficheNomFrancais : " + elementTR.getRenderer().toString());
								setNomCommun( Outils.nettoyageCaracteres(elementTR.getRenderer().toString().trim()) );
							}
						}
					}
					
					//Recup TRs Haut Droit contenant le Groupe auquel appartient l'espèce
					List<? extends Element> listeElementsIMG = elementTable_TABLE.getAllElements(HTMLElementName.IMG);
					for (Element element : listeElementsIMG) {
						listeAttributs=element.getAttributes();
						for (Attribute attribut : listeAttributs) {
							
							if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_groupe/") ) {

								Element listeElementsHD_TR = element.getParentElement().getParentElement();
								
								// TODO :
								//groupeRef = Integer.parseInt(attribut.getValue().toString().replaceAll(".*images_groupe/([0-9]*).gif","$1"));
								//log.info("getFiche() - groupeRef : " + groupeRef);

								// TODO :
								//groupeRefTexte = listeElementsHD_TR.getRenderer().toString().trim();
								//log.info("getFiche() - groupeRefTexte : " + groupeRefTexte);
							}
						}
					}

					
					
					break;
				//Description
				case 2 :
					
					//Le grand pere du 1er TD de class Normal est le TBODY des Détails
					Element ElementsMG_normal=elementTable_TABLE.getFirstElementByClass("normal");
					log.debug("getFiche() - ElementsMG_rubrique : " + ElementsMG_normal.toString().substring(0, Math.min(ElementsMG_normal.toString().length(),20)));
					Element ElementsMG=ElementsMG_normal.getParentElement().getParentElement();
					List<? extends Element> listeElementsMG_TD = ElementsMG.getAllElements(HTMLElementName.TD);
					String autresDenominations = "";
					boolean autresDenominationsFlag = true;
					String rubrique = "";
					String contenu = "";
	
					for (Element elementTD : listeElementsMG_TD) {
						log.debug("getFiche() - listeElementsMG_TD : " + elementTD.toString().substring(0, Math.min(elementTD.toString().length(),50)));
						listeAttributs=elementTD.getAttributes();
						for (Attribute attribut : listeAttributs) {
							
							if (attribut.getName().equals("class") && attribut.getValue().equals("rubrique")) {
								//Si c'est la 1ère fois que l'on passe alors on enregistre les autres dénomination et 
								// l'international dans un nouveau détail
								if (rubrique.equals("") && !autresDenominations.equals("") && autresDenominationsFlag) {
									// TODO:
									//ficheListeDetails.add(new Detail("Autres Denominations", autresDenominations, true));
									
									log.info("getFiche() - autresDenominations(A) : " + autresDenominations);
									autresDenominations = "";
									//autresDenominationsFlag = false;
								}
								rubrique = elementTD.getRenderer().toString().trim();
							}
							
							if (attribut.getName().equals("class") && attribut.getValue().equals("normal") ) {
								log.debug("getFiche() - rubrique : " + rubrique);
								if (rubrique.equals("")) {
									autresDenominations  = elementTD.getRenderer().toString().trim();
									
									// suppression des sauts de ligne
									autresDenominations = autresDenominations.replaceAll("\r\n", " ").replaceAll("\n", " ");
									log.debug("getFiche() - autresDenominations(B1) : " + autresDenominations);
	
									// suppression des blancs multiples
									autresDenominations = autresDenominations.replaceAll("\\s{2,}"," ");
									log.debug("getFiche() - autresDenominations(B2) : " + autresDenominations);
									
									// permet d'enlever les Liens et de les remplacer par (*)
									autresDenominations = autresDenominations.replaceAll("<[^>]*>", "(*)").trim();
										
								} else {
									contenu = elementTD.getRenderer().toString();
									log.debug("getFiche() - contenu(initial) : " + contenu);
									
									// suppression des sauts de ligne
									contenu = contenu.replaceAll("\r\n", " ").replaceAll("\n", " ");
									log.debug("getFiche() - contenu(1) : " + contenu);
	
									// suppression des blancs multiples
									contenu = contenu.replaceAll("\\s{2,}"," ");
									log.debug("getFiche() - contenu(2) : " + contenu);
									
									// permet d'enlever les Liens et de les remplacer par (*)
									contenu = contenu.replaceAll("<[^>]*>", "(*)").trim();
	
									log.info("getFiche() - rubrique : " + rubrique);
									log.info("getFiche() - contenu(après nettoyage) : " + contenu);
									//TODO :
									//ficheListeDetails.add(new Detail(rubrique,contenu,false));
								}
							}
						}
						
						// Création de la liste des Liens (url vers d'autres fiches)
						
						for (Element elementTDA : elementTD.getAllElements(HTMLElementName.A)) {
							
							log.debug("getFiche() - A : " + elementTDA.getRenderer().toString().trim() + " - lien : " + elementTDA.getAttributeValue("href"));
							
							if (elementTDA.getAttributeValue("href").startsWith("../") || elementTDA.getAttributeValue("href").startsWith("http://doris.ffessm.fr") ) {
							
								if (elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "") != "" && elementTDA.getRenderer().toString().trim() != "") {
								
									String tempLien = elementTDA.getRenderer().toString().trim();
									log.info("getFiche() - listeLienRencontre : " + listeLienRencontre );
									
									if (!listeLienRencontre.contains(tempLien + "£")) {
										listeLienRencontre += tempLien + "£";
										
										//TODO :
										//ficheListeLiensUrl.add(elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", ""));
										//ficheListeLiensTexte.add(tempLien);
									}
								}
							}
						}
					}
					
					//Recup du TD qui contient les infos DROITE (images et qui a fait la fiche)
					//Recup du TR dont le 3ème TD fils contient les infos DROITE (images et qui a fait la fiche)
					
					List<? extends Element> listeElements1 = null;
					listeElements1 = elementTable_TABLE.getAllElementsByClass("trait_cadregris");
					log.debug("getFiche() -  element : " + " - " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),30)));
					
					for (Element element : listeElements1) {
						log.debug("getFiche() - vignette 1: " + element.toString().substring(0, Math.min(100,element.toString().toString().length())));
						
						Element element2 = element.getFirstElement(HTMLElementName.A);
						String urlImageDansFiche = "";
						
						if (element2 != null)
						{
							element2 = element2.getFirstElement(HTMLElementName.IMG);
							log.debug("getFiche() - vignette 2: " + element2.toString().substring(0, Math.min(100,element2.toString().toString().length())));
							
							listeAttributs=element2.getAttributes();
							for (Attribute attribut : listeAttributs) {
								if (attribut.getName().equalsIgnoreCase("src") && attribut.getValue().contains("gestionenligne")){
									//TODO :
									//urlImageDansFiche = attribut.getValue().replaceAll(Extraction.racineSite, "");
									log.info("getFiche() - urlImageDansFiche : " + urlImageDansFiche);
																										
									break;
								}
								
							}
						}
						
						//Recup Texte
						element2 = element.getFirstElementByClass("normal2");
						if (element2 != null)
						{
							log.info("getFiche() - Texte : " + element2.getRenderer().toString());
							// TODO :
							// ficheListeImages.add(new Image(element2.getRenderer().toString(),urlImageDansFiche));
						}
					}					
					break;
					
				//Ligne blanche => rien à faire mais à garder pour ne pas passer dans le default
				case 3 :
					break;
					
				//la classification et la suite
				case 4 :
					// To Do
					break;
				default :
					break;
				}
			}
			
			//Recup du sous-Groupe auquel appartient l'espèce
			List<? extends Element> listeElementsTDSousGroupe = source.getAllElementsByClass("sousgroupe_fiche");
			for (Element element : listeElementsTDSousGroupe) {
				int index = listeElementsTDSousGroupe.indexOf(element);
				log.debug("getFiche() - index : " + index);
				
				if (index == 1) {
					// TODO :
					//sousgroupeRefTexte = element.getRenderer().toString().trim();
					//log.info("getFiche() - sousgroupeRefTexte : " + sousgroupeRefTexte);
				}
				if (index == 2) {
					
					listeAttributs = element.getFirstElement(HTMLElementName.IMG).getAttributes();
					for (Attribute attribut : listeAttributs) {
						
						if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_sousgroupe/") ) {
							// TODO :
							//sousgroupeRef = Integer.parseInt(attribut.getValue().toString().toLowerCase().replaceAll(".*images_sousgroupe/([0-9]*).(gif|jpg)","$1"));
							//log.info("getFiche() - sousgroupeRef : " + sousgroupeRef);
						}
					}
				}
			}

			
		}
		
		
		//Lecture des informations pour une fiche proposée et pour le début d'une fiche en cours de rédaction
		if ( getEtatFiche() == 1 || getEtatFiche() == 2 || getEtatFiche() == 3
				|| getEtatFiche() == 5 ) {
			ElementTable=source.getFirstElementByClass("trait_cadregris");
			log.debug("getFiche() - ElementTable : " + ElementTable.toString().substring(0, Math.min(ElementTable.toString().length(),200)));

			listeElementsTable_TABLE = ElementTable.getAllElements(HTMLElementName.TABLE);
			log.debug("getFiche() - listeElementsTable_TABLE.size : " + listeElementsTable_TABLE.size());

			for (Element elementTable_TABLE : listeElementsTable_TABLE) {
				num_table++;
				log.debug("getFiche() - num_table :" + num_table);
				log.debug("getFiche() - elementTable_TABLE.length() : " + elementTable_TABLE.toString().length());
				log.debug("getFiche() - elementTable_TABLE : " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),100)));

				String largeurTable = elementTable_TABLE.getAttributeValue("width");
				log.debug("getFiche() - largeurTable :" + largeurTable);
				String urlImageDansFiche = "";
				
				//Entête de la Fiche
				if (num_table== 2) {
			
					Element ElementInfosGauche = elementTable_TABLE.getFirstElementByClass("code_fiche").getFirstElement();
					
					try	{
						Element ElementNomLatin = ElementInfosGauche.getFirstElementByClass("texte_bandeau").getFirstElement();
						log.info("getFiche() - ElementNomLatin : " + ElementNomLatin.getRenderer().toString().trim());
						setNomScientifique( ElementNomLatin.getRenderer().toString().trim() );
					} catch (Exception e) {
		        		log.debug("getFiche() - le nom latin n'est pas toujours renseigné");
		        	}
					
					try	{
						Element ElementDistribution = ElementInfosGauche.getFirstElementByClass("normal").getFirstElement();
						log.info("getFiche() - ElementDistribution : " + ElementDistribution.getRenderer().toString().trim());
						// TODO :
						//ficheRegion = ElementDistribution.getRenderer().toString().trim();
					} catch (Exception e) {
		        		log.debug("getFiche() - la Distribution n'est pas toujours renseignée");
		        	}
					
					try	{
						Element ElementNomCommun = ElementInfosGauche.getFirstElementByClass("titre2").getFirstElement();
						log.info("getFiche() - ElementNomCommun : " + ElementNomCommun.getRenderer().toString().trim());
						setNomCommun( ElementNomCommun.getRenderer().toString().trim() );
					} catch (Exception e) {
		        		log.debug("getFiche() - le nom français n'est pas toujours renseigné");
		        	}
					
					//Recup TRs Haut Droit contenant le Groupe auquel appartient l'espèce
					List<? extends Element> listeElementsIMG = elementTable_TABLE.getAllElements(HTMLElementName.IMG);
					for (Element element : listeElementsIMG) {
						listeAttributs=element.getAttributes();
						for (Attribute attribut : listeAttributs) {
							
							if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_groupe/") ) {

								Element listeElementsHD_TR = element.getParentElement().getParentElement();
								// TODO :
								//groupeRef = Integer.parseInt(attribut.getValue().toString().replaceAll(".*images_groupe/([0-9]*).gif","$1"));
								//log.info("getFiche() - groupeRef : " + groupeRef);
								// TODO :
								//groupeRefTexte = listeElementsHD_TR.getRenderer().toString().trim();
								//log.info("getFiche() - groupeRefTexte : " + groupeRefTexte);
							}
						}
					}

								
				}
					
				if (largeurTable!=null && largeurTable.equals("372")) {
					log.debug("getFiche() - Recup. Images");
					
					//Recup du TR dont le 3ème TD fils contient les infos DROITE (images et qui a fait la fiche)
					List<? extends Element> ListeelementTable_IMG = elementTable_TABLE.getAllElements(HTMLElementName.IMG);
					
					for (Element elementTableImg : ListeelementTable_IMG) {
						List<? extends Attribute> listeAttributsTableImg=elementTableImg.getAttributes();
						for (Attribute attribut : listeAttributsTableImg) {
							if (attribut.getName().equalsIgnoreCase("src") && attribut.getValue().contains("gestionenligne")){
								// TODO :
								// urlImageDansFiche = attribut.getValue().replaceAll(Extraction.racineSite, "");
								log.info("getFiche() - urlImageDansFiche : " + urlImageDansFiche);
														
								break;
							}
							
						}
					}
						
					//Recup Texte
					Element element2 = elementTable_TABLE.getFirstElementByClass("normal2");
					if (element2 != null)
					{
						log.info("getFiche() - Texte : " + element2.getRenderer().toString());
						// TODO :
						// ficheListeImages.add(new Image(element2.getRenderer().toString(), urlImageDansFiche));
					}
					break;
				
				}
				

			}
			
			//Recup du sous-Groupe auquel appartient l'espèce
			log.debug("getFiche() - Recup du sous-Groupe auquel appartient l'espèce");
			List<? extends Element> listeElementsTDSousGroupe = source.getAllElementsByClass("sousgroupe_fiche");
			
			for (Element element : listeElementsTDSousGroupe) {
				int index = listeElementsTDSousGroupe.indexOf(element);
				log.debug("getFiche() - index : " + index);
				
				if (index == 1) {
					// TODO :
					//sousgroupeRefTexte = element.getRenderer().toString().trim();
					//log.info("getFiche() - sousgroupeRefTexte : " + sousgroupeRefTexte);
				}
				if (index == 2) {
					
					listeAttributs = element.getFirstElement(HTMLElementName.IMG).getAttributes();
					for (Attribute attribut : listeAttributs) {
						
						if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_sousgroupe/") ) {
							// TODO :
							//sousgroupeRef = Integer.parseInt(attribut.getValue().toString().toLowerCase().replaceAll(".*images_sousgroupe/([0-9]*).(gif|jpg)","$1"));
							//log.info("getFiche() - sousgroupeRef : " + sousgroupeRef);
						}
					}
				}
			}
			
			
		}
		
		listeElementsTable_TABLE = null;
		
    	log.debug("getFiche() - Fin");
	}
	
	
	public class Detail {

		public String titre;
		public String contenu;
		public boolean affiche;
		
		public Detail(String inTitre, String inContenu, boolean inAffiche) {
			log.debug("Detail() - Début");
			log.debug("Detail() - Titre : " + inTitre);
			log.debug("Detail() - Contenu : " + inContenu);
			log.debug("Detail() - Affiche : " + inAffiche);
			titre = inTitre;
			contenu = inContenu;
			affiche = inAffiche;
			log.debug("Detail() - Fin");
		}
	}
	
	public class Image {

		public String titre;
		public boolean principale;
		public String urlVignette;
		public String urlImage;
		
		public Image(String inTitre, String inUrl) {
			log.debug("Image() - Début");
			log.debug("Image() - Titre : " + inTitre);
			log.debug("Image() - Url : " + inUrl);

			titre = inTitre;
			
			//Les remplacements si dessous permettent de "calculer" simplement la référence de la grande image
			urlImage = inUrl.replace("/photos_fiche_moy/","/photos/").replace("/photos_fiche_vig/","/photos/");
			//ou de la vignette
			urlVignette = inUrl.replace("/photos_fiche_moy/","/photos_fiche_vig/");
			
			//Si c'est l'image principale alors c'est l'image : photos_fiche_moy
			if (inUrl.contains("/photos_fiche_moy/")) {
				principale = true;
			}else {
				principale = false;
			}
			
			log.debug("Image() - principale : " + principale);
			log.debug("Image() - urlImage : " + urlImage);
			log.debug("Image() - urlVignette : " + urlVignette);
			log.debug("Image() - Fin");
		}
		
	}
	
	
	
	// End of user code
	
	public Fiche() {} // needed by ormlite
	public Fiche(java.lang.String nomScientifique, java.lang.String nomCommun, int numeroFiche, int etatFiche, java.lang.String dateCreation, java.lang.String dateModification) {
		super();
		this.nomScientifique = nomScientifique;
		this.nomCommun = nomCommun;
		this.numeroFiche = numeroFiche;
		this.etatFiche = etatFiche;
		this.dateCreation = dateCreation;
		this.dateModification = dateModification;
	} 

	public int getId() {
		return _id;
	}
	public void setId(int id) {
		this._id = id;
	}

	public java.lang.String getNomScientifique() {
		return this.nomScientifique;
	}
	public void setNomScientifique(java.lang.String nomScientifique) {
		this.nomScientifique = nomScientifique;
	}
	public java.lang.String getNomCommun() {
		return this.nomCommun;
	}
	public void setNomCommun(java.lang.String nomCommun) {
		this.nomCommun = nomCommun;
	}
	public int getNumeroFiche() {
		return this.numeroFiche;
	}
	public void setNumeroFiche(int numeroFiche) {
		this.numeroFiche = numeroFiche;
	}
	public int getEtatFiche() {
		return this.etatFiche;
	}
	public void setEtatFiche(int etatFiche) {
		this.etatFiche = etatFiche;
	}
	public java.lang.String getDateCreation() {
		return this.dateCreation;
	}
	public void setDateCreation(java.lang.String dateCreation) {
		this.dateCreation = dateCreation;
	}
	public java.lang.String getDateModification() {
		return this.dateModification;
	}
	public void setDateModification(java.lang.String dateModification) {
		this.dateModification = dateModification;
	}

	public Participant getRedacteurs() {
		return this.redacteurs;
	}
	public void setRedacteurs(Participant redacteurs) {
		this.redacteurs = redacteurs;
	}			
	/** Liste des photos de la fiche */
	public Collection<PhotoFiche> getPhotosFiche() {
		return this.photosFiche;
	}					
	/** responsable régional de la fiche */ 
	public Participant getResponsableRegional() {
		return this.responsableRegional;
	}
	public void setResponsableRegional(Participant responsableRegional) {
		this.responsableRegional = responsableRegional;
	}			
	/** contenu textuel de la fiche */
	public Collection<SectionFiche> getContenu() {
		return this.contenu;
	}					
	/** Photo par défaut de l'espèce présentée par cette fiche. Elle est aussi présente dans la liste "photosFiche". */ 
	public PhotoFiche getPhotoPrincipale() {
		return this.photoPrincipale;
	}
	public void setPhotoPrincipale(PhotoFiche photoPrincipale) {
		this.photoPrincipale = photoPrincipale;
	}			
	/** Liste des autres dénominations de l'espèce présentée sur la fiche. */
	public Collection<AutreDenomination> getAutresDenominations() {
		return this.autresDenominations;
	}					
	/** Permet d'identifier avec le sous-groupe (optionel) le groupe auquel est rattaché la fiche */ 
	public Groupe getGroupe() {
		return this.groupe;
	}
	public void setGroupe(Groupe groupe) {
		this.groupe = groupe;
	}			



	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_FICHE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_NOMSCIENTIFIQUE);
    	sb.append("=\"");
		sb.append(StringEscapeUtils.escapeXml(this.nomScientifique));
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_NOMCOMMUN);
    	sb.append("=\"");
		sb.append(StringEscapeUtils.escapeXml(this.nomCommun));
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_NUMEROFICHE);
    	sb.append("=\"");
		sb.append(this.numeroFiche);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_ETATFICHE);
    	sb.append("=\"");
		sb.append(this.etatFiche);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_DATECREATION+">");
		sb.append(StringEscapeUtils.escapeXml(this.dateCreation));
    	sb.append("</"+XML_ATT_DATECREATION+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DATEMODIFICATION+">");
		sb.append(StringEscapeUtils.escapeXml(this.dateModification));
    	sb.append("</"+XML_ATT_DATEMODIFICATION+">");

		if(this.redacteurs!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_REDACTEURS+">");
			sb.append(this.redacteurs);
	    	sb.append("</"+XML_REF_REDACTEURS+">");
		}
		sb.append("\n"+indent+"\t<"+XML_REF_PHOTOSFICHE+">");
		for(PhotoFiche ref : this.photosFiche){
			sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
    	}
		sb.append("</"+XML_REF_PHOTOSFICHE+">");		
		try{
			for(ZoneGeographique ref : this.lookupZonesGeographiques(contextDB)){
	    		sb.append("\n"+indent+"\t<"+XML_REF_ZONESGEOGRAPHIQUES+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
				
	    	}
		}
		catch(SQLException e){};	
		try{
			for(ZoneObservation ref : this.lookupZonesObservation(contextDB)){
	    		sb.append("\n"+indent+"\t<"+XML_REF_ZONESOBSERVATION+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
				
	    	}
		}
		catch(SQLException e){};	
		try{
			for(Participant ref : this.lookupVerificateurs(contextDB)){
	    		sb.append("\n"+indent+"\t<"+XML_REF_VERIFICATEURS+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
				
	    	}
		}
		catch(SQLException e){};	
		if(this.responsableRegional!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_RESPONSABLEREGIONAL+">");
			sb.append(this.responsableRegional);
	    	sb.append("</"+XML_REF_RESPONSABLEREGIONAL+">");
		}
		sb.append("\n"+indent+"\t<"+XML_REF_CONTENU+">");
		for(SectionFiche ref : this.contenu){
			sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
    	}
		sb.append("</"+XML_REF_CONTENU+">");		
		if(this.photoPrincipale!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_PHOTOPRINCIPALE+">");
			sb.append(this.photoPrincipale);
	    	sb.append("</"+XML_REF_PHOTOPRINCIPALE+">");
		}
		sb.append("\n"+indent+"\t<"+XML_REF_AUTRESDENOMINATIONS+">");
		for(AutreDenomination ref : this.autresDenominations){
			sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
    	}
		sb.append("</"+XML_REF_AUTRESDENOMINATIONS+">");		
		if(this.groupe!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_GROUPE+">");
			sb.append(this.groupe);
	    	sb.append("</"+XML_REF_GROUPE+">");
		}
		// TODO deal with other case

		sb.append("</"+XML_FICHE+">");
		return sb.toString();
	}
}
