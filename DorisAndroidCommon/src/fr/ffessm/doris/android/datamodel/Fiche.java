/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2014 - FFESSM
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
package fr.ffessm.doris.android.datamodel;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.datamodel.associations.*;

// Start of user code additional import for Fiche
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang3.StringUtils;

import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.ParticipantKind;
import fr.ffessm.doris.android.sitedoris.ErrorCollector;
import fr.ffessm.doris.android.sitedoris.FicheLight;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
// End of user code

/** 
  * Fiche Doris, donne accès aux données de la fiche 
  */ 
@DatabaseTable(tableName = "fiche")
public class Fiche {

	public static Log log = LogFactory.getLog(Fiche.class);

	public static final String XML_FICHE = "FICHE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NOMSCIENTIFIQUE = "nomScientifique";
	public static final String XML_ATT_NOMCOMMUN = "nomCommun";
	public static final String XML_ATT_NUMEROFICHE = "numeroFiche";
	public static final String XML_ATT_ETATFICHE = "etatFiche";
	public static final String XML_ATT_DATECREATION = "dateCreation";
	public static final String XML_ATT_DATEMODIFICATION = "dateModification";
	public static final String XML_ATT_NUMEROFICHESLIEES = "numerofichesLiees";
	public static final String XML_ATT_TEXTEPOURRECHERCHERAPIDE = "textePourRechercheRapide";
	public static final String XML_ATT_PICTOGRAMMES = "pictogrammes";
	public static final String XML_REF_PHOTOSFICHE = "photosFiche";
	public static final String XML_REF_ZONESGEOGRAPHIQUES = "zonesGeographiques";
	public static final String XML_REF_ZONESOBSERVATION = "zonesObservation";
	public static final String XML_REF_CONTENU = "contenu";
	public static final String XML_REF_PHOTOPRINCIPALE = "photoPrincipale";
	public static final String XML_REF_AUTRESDENOMINATIONS = "autresDenominations";
	public static final String XML_REF_GROUPE = "groupe";
	public static final String XML_REF_INTERVENANTS = "intervenants";
	public static final String XML_REF_CLASSIFICATION = "classification";
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected int _id;

	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	/**
	 * object created from DB may need to be updated from the DB for being fully navigable
	 */
	public boolean photoPrincipale_mayNeedDBRefresh = true;
	public boolean groupe_mayNeedDBRefresh = true;
	

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String nomScientifique;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String nomCommun;

	/** Numéro de la fiche tel que connu par le site lui même */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int numeroFiche;

	/** Etat Avancement de la fiche 
4 : Fiche Publiée - 1, 2, 3 : En cours de Rédaction - 5 : Fiche Proposée */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int etatFiche;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String dateCreation;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String dateModification;

	/** numéros des fiches liées séparé par des point virgules */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String numerofichesLiees;

	/** Texte précalculé pour optimiser les recherches (sans accents, sans majuscules) avec autres dénominations */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField(dataType = com.j256.ormlite.field.DataType.LONG_STRING)
	protected java.lang.String textePourRechercheRapide;

	/** id des pictogrammes applicables à cette fiche séparés par des points virgules */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String pictogrammes;
	

	/** Liste des photos de la fiche */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<PhotoFiche> photosFiche;

	/** zones géographiques où l'on peut trouver l'élément décrit par la fiche */ 
	// work in progress, find association 
	// Association many to many Fiches_ZonesGeographiques
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")	
	protected ForeignCollection<Fiches_ZonesGeographiques> fiches_ZonesGeographiques;

	/** zones géographiques où l'on peut trouver l'élément décrit par la fiche 
	  * Attention, returned list is readonly
      */
	public List<ZoneGeographique> getZonesGeographiques(){
		List<ZoneGeographique> result = new ArrayList<ZoneGeographique>();
		
		for (Fiches_ZonesGeographiques aFiches_ZonesGeographiques : fiches_ZonesGeographiques) {
			if(_contextDB != null) aFiches_ZonesGeographiques.setContextDB(_contextDB);
			result.add(aFiches_ZonesGeographiques.getZoneGeographique());
		}
		return result;
	}
	public void addZoneGeographique(ZoneGeographique zoneGeographique){
		try {
			_contextDB.fiches_ZonesGeographiquesDao.create(new Fiches_ZonesGeographiques( zoneGeographique, this));		
		} catch (SQLException e) {
			log.error("Pb while adding association fiches_ZonesGeographiques",e);
		}
	}
	// end work in progress 	

				

	/** zones  où l'on peut observer l'élément décrit par la fiche */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiches")
	protected ForeignCollection<ZoneObservation> zonesObservation;

	/** contenu textuel de la fiche */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<SectionFiche> contenu;

	/** Photo par défaut de l'espèce présentée par cette fiche. Elle est aussi présente dans la liste "photosFiche". */ 
	@DatabaseField(foreign = true)
	protected PhotoFiche photoPrincipale;

	/** Liste des autres dénominations de l'espèce présentée sur la fiche. */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<AutreDenomination> autresDenominations;

	/** Permet d'identifier avec le sous-groupe (optionnel) le groupe auquel est rattaché la fiche */ 
	@DatabaseField(foreign = true)
	protected Groupe groupe;

	/** intervenants sur une fiche */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<IntervenantFiche> intervenants;

	/** Tableau Phylogénétique */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "fiche")
	protected ForeignCollection<ClassificationFiche> classification;

	// Start of user code Fiche additional user properties

	/** renvoie le texte brut
	 * 
	 * @return
	 */
	public String getNomScientifiqueTxt(){
		return StringUtils.replaceEach(this.nomScientifique, new String[]{"{{g}}","{{/g}}","{{i}}","{{/i}}"}, new String[]{"","","",""});
	}
	
	/** renvoie le couple Ref - État Fiche
	 * 
	 * @return
	 */
	public String getRefEtatFiche(){
		return numeroFiche+"-"+etatFiche;
	}
	
	public void getFicheEtatDateModifFromHtml(String htmlFiche) {
		//log.trace("getFicheEtatDateModifFromHtml() - Début");
		
		//4 : Fiche Publiée - 1, 2, 3 : En cours de Rédaction - 5 : Fiche Proposée 
		if ( htmlFiche.contains("Fiche propos") ){
			etatFiche = 5;
		} else if ( htmlFiche.contains("Fiche en cours de r") ) {
			etatFiche = 1;
		} else {
			etatFiche = 4;
		}
		//log.trace("getFicheEtatDateModifFromHtml() - etatFiche : "+etatFiche);
		
		// Quelque soit leur état dans le bas des fiches on a toujours la date de dernière modification
		//<strong>DORIS</strong>,&nbsp;4/2/2013&nbsp;:&nbsp;
		dateModification = Pattern.compile(".*<strong>DORIS</strong>,&nbsp;([^&]*)&nbsp;.*",
											Pattern.DOTALL).matcher(htmlFiche).replaceAll("$1");
		
		//log.trace("getFicheEtatDateModifFromHtml() - Fin");
	}
	
	public void getFicheFromHtml(String htmlFiche, List<Groupe> listeGroupes, List<Participant> listeParticipants) throws SQLException{
		//log.trace("getFicheFromHtml() - Début");
		
		SiteDoris siteDoris = new SiteDoris();
		Common_Outils commonOutils = new Common_Outils();
		
		pictogrammes = "";
		
    	int i;
    	
		// TODO : si un jour on veut avoir la liste inversée des fiches
    	//StringBuilder sbListeLiensVersFiches = new StringBuilder();
    	
    	// -- Cible de la page en enlevant tout débord tout ce qui est totalement superflu --
    	// -- Issue de la Version 1 : ca fonctionne => je garde / GMo
		Source source=new Source( commonOutils.nettoyageBalises(htmlFiche) );
		source.fullSequentialParse();
		Element tableResultats = null;

		// Récupération de la Table des Résultats
		List<? extends Element> listeElementsTable=source.getAllElements(HTMLElementName.TABLE);
		for (Element elementTable : listeElementsTable) {
			//log.debug("ciblePage() - elementTable : " + elementTable.toString().substring(0, Math.min(100, elementTable.toString().length())));
			
			List<? extends Attribute> listeAttributs=elementTable.getAttributes();
			for (Attribute attribut : listeAttributs) {
				if (attribut.getName().toLowerCase().equals("width") &  attribut.getValue().equals("820")) {
					//log.debug("ciblePage() - Table Trouvée : " + attribut.getName() + " = " +  attribut.getValue());
					tableResultats = elementTable;
					break;
				}
			}
			if (tableResultats != null) break;
		}
				
    	// Permet ensuite d'utiliser la mise en forme
    	// mais le prix à payer est de supprimer à la main partout où elle n'est pas nécessaire
    	htmlFiche = commonOutils.remplacementBalises(tableResultats.toString(), true);
    	
    	//log.debug("getFiche() - htmlFiche : " + htmlFiche.substring(0, 200));
    	
		// Utilisation du parser Jericho
		source = new Source(htmlFiche);
		
		// Nécessaire pour trouver ensuite les pères
		source.fullSequentialParse();

		// Recherche TD dont la class est code_fiche
		// Il contient le code fiche, le nom français, le nom latin et la zone géographique
		Element ElementTDcode_fiche;
		ElementTDcode_fiche = source.getFirstElementByClass("code_fiche");
		

		String ficheRef = ElementTDcode_fiche.getFirstElementByClass("normalgris").getRenderer().toString().trim();
		ficheRef = ficheRef.replaceAll("\\{\\{[^\\}]*\\}\\}", "").replace("(N°", "").replace(")", "").trim();
		setNumeroFiche(Integer.parseInt(ficheRef));
		//log.info("getFicheFromHtml() - ref : " + ficheRef);
		//log.info("getFicheFromHtml() - Etat Fiche : " + getEtatFiche());		

		String errorGroup = "fiches.fiche_"+ficheRef;
		ErrorCollector.getInstance().addGroup(errorGroup);
		
		// Zones d'Observation (!!! <> Zones Géographie)
		// Elles sont affichées entre le Nom Scientifique et le Nom Commun.
		// TODO : (Une fois un test effectué modifier la phrase)
		// Ce sont des saisies manuelles mais en utilisant la "," et le "et" comme séparateur
		// on doit pouvoir obtenir une liste des zones d'observation assez propre
		// TODO : En fait, pas du tout, c'est très disparate  => un dico. ne doit pas pouvoir servir
		if (ElementTDcode_fiche.getFirstElementByClass("normal") != null) {
			String listeZonesObservation = ElementTDcode_fiche.getFirstElementByClass("normal").getRenderer().toString().trim();
			
			//log.info("##01 -" + listeZonesObservation.trim());
					
			listeZonesObservation = listeZonesObservation.replace(", sauf", " sauf");
			
			i = 0;
			while (listeZonesObservation.matches("^[^\\(]*\\([,^\\)]*\\).*") && i < 20) {
				//log.info("##11 -" + listeZonesObservation.trim());
				listeZonesObservation = listeZonesObservation.replaceAll("(\\([^,\\)]*?),", "$1£");
				i++;
			}
			//log.info("##12 -" + listeZonesObservation.trim());
			
			
			listeZonesObservation = listeZonesObservation.replaceAll("(\\([^,\\)]*?),", "$1£");
			//log.info("##21 -" + listeZonesObservation.trim());
		
			//log.info("getFicheFromHtml() - listeZonesObservation : " + listeZonesObservation);
			if (! listeZonesObservation.isEmpty()) {
				String[] zonesObservation = listeZonesObservation.split(",|et");
				for (String zoneObservationTxt : zonesObservation){
					//log.info("##31 -" + zoneObservationTxt.trim());
				}
			}

		}
		
		//Centrage sur la TABLE qui contient tout le texte et les images
		Element ElementTable;
		List<? extends Element> listeElementsTable_TABLE;
		List<? extends Attribute> listeAttributs;
		int num_table = 0;
		int groupeRef = 0;
		int sousgroupeRef = 0;
		
		int positionSectionDansFiche = 0;
		
		//Initialisation Liste des Autres Dénominations
		String autresDenominationsPourRechercheRapide = "";
		
		// Lecture des informations pour une fiche complète
		if ( getEtatFiche() == 4) {
		
			
			//Recup de l'ensemble des lignes (TR) de la TABLE
			// La 1ère contient l'entête
			// La 2ème contient la description
			// La 4ème contient la classification et la suite
			
			ElementTable=source.getFirstElementByClass("trait_cadregris").getFirstElement();
			//log.debug("getFiche() - ElementTable : " + ElementTable.toString().substring(0, Math.min(ElementTable.toString().length(),200)));
			
			listeElementsTable_TABLE = ElementTable.getFirstElement(HTMLElementName.TABLE).getChildElements();
			//log.debug("getFiche() - listeElementsTable_TABLE.size : " + listeElementsTable_TABLE.size());

			for (Element elementTable_TABLE : listeElementsTable_TABLE) {
				num_table++;
				//log.debug("getFicheFromHtml() - num_table : " + num_table);
				
				//log.debug("getFiche() - ligneTable_TR :" + num_table);
				//log.debug("getFiche() - elementTable_TR : " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),100)));
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
							if (i == 1) {
								//Pour chaque IMG contenu on a un petit logo indiquant si l'espèce est protégée, réglementée, dangereuse
								List<? extends Element> listeElementsTR_IMG = elementTR.getAllElements(HTMLElementName.IMG);
								for (Element elementImg : listeElementsTR_IMG) {
									//log.info("getFicheFromHtml() - ficheTagInfo : " + elementImg.getAttributeValue("alt")+" + "+elementImg.getAttributeValue("src"));
									pictogrammes += Constants.getTypePicto(elementImg.getAttributeValue("alt")).ordinal() + ";";
								}
								listeElementsTR_IMG = null;
							}
							if (i == 2) {
								//log.info("getFicheFromHtml() - ficheNomLatin : " + elementTR.getRenderer().toString().trim());
								setNomScientifique( commonOutils.nettoyageTextes(elementTR.getRenderer().toString().trim()) );
							}
							if (i == 3) {
								//log.info("getFicheFromHtml() - ficheRegion : " + elementTR.getRenderer().toString().trim());
								// TODO :
								//ficheRegion = elementTR.getRenderer().toString().trim();
							}
							if (i == 5) {
								//log.info("getFicheFromHtml() - ficheNomFrancais : " + elementTR.getRenderer().toString().trim());
								setNomCommunNeverEmpty( commonOutils.nettoyageTextes(elementTR.getRenderer().toString().replaceAll("\\{\\{[^\\}]*\\}\\}", "")).trim() );
							}
						}
						listeElementsHG_TR = null;
					}
					listeElementsHG_TD = null;
					
					//Recup TRs Haut Droit contenant le Groupe auquel appartient l'espèce
					List<? extends Element> listeElementsIMG = elementTable_TABLE.getAllElements(HTMLElementName.IMG);
					for (Element element : listeElementsIMG) {
						listeAttributs=element.getAttributes();
						for (Attribute attribut : listeAttributs) {
							
							if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_groupe/") ) {
								// Certaines fiches appartiennent à un groupe sans être dans un sous-groupe
								// c'est pourquoi, on initialise d'abord le groupe
								groupeRef = Integer.parseInt(attribut.getValue().toString().replaceAll(".*images_groupe/([0-9]*).gif","$1"));
								//log.info("getFicheFromHtml() - groupeRef : " + groupeRef);

								groupe = siteDoris.getGroupeFromListeGroupes(listeGroupes, groupeRef, 0);
							}
						}
					}
					listeElementsIMG = null;
					break;
					
				//Description Gauche et Droite
				case 2 :
					
					//Le grand-père du 1er TD de class Normal est le TBODY des Détails
					Element ElementsMG_normal=elementTable_TABLE.getFirstElementByClass("normal");
					//log.debug("getFiche() - ElementsMG_rubrique : " + ElementsMG_normal.toString().substring(0, Math.min(ElementsMG_normal.toString().length(),20)));
					Element ElementsMG=ElementsMG_normal.getParentElement().getParentElement();
					List<? extends Element> listeElementsMG_TD = ElementsMG.getAllElements(HTMLElementName.TD);

					boolean autresDenominationsFlag = true;
					String rubrique = "";
	
					for (Element elementTD : listeElementsMG_TD) {
						//log.debug("getFiche() - listeElementsMG_TD : " + elementTD.toString().substring(0, Math.min(elementTD.toString().length(),50)));
						listeAttributs=elementTD.getAttributes();
						for (Attribute attribut : listeAttributs) {
							// Récupération du Titre de la rubrique
							// Le titre est vide pour "Autres Dénominations"
							if (attribut.getName().equals("class") && attribut.getValue().equals("rubrique")) {
								
								rubrique = elementTD.getRenderer().toString().trim();
								
								//Si c'est la 1ère fois que l'on passe par "rubrique" alors 
								// c'est que l'on a déjà passé les autres dénomination et que donc on les connait
								if (!rubrique.equals("") && autresDenominationsFlag) {
									autresDenominationsFlag = false;
								}

							}
							
							if (attribut.getName().equals("class") && attribut.getValue().equals("normal") ) {
								//log.debug("getFicheFromHtml() - rubrique : " + rubrique);
								if (autresDenominationsFlag) {
									
									String autresDenominationsTexte = commonOutils.nettoyageTextes(elementTD.getRenderer().toString().trim());
									
									// permet d'enlever les Liens et de les remplacer par un texte, par exemple "(Fiche)"
									autresDenominationsTexte = autresDenominationsTexte.replaceAll("<[^>]*fiche_numero=([^>]*)>", "{{$1}}").trim();
									autresDenominationsPourRechercheRapide = autresDenominationsPourRechercheRapide + autresDenominationsTexte.replaceAll("\\([^\\)]*\\)", "")+" ";
									
									if ( ! autresDenominationsTexte.isEmpty() ){
										AutreDenomination autreDenomination = new AutreDenomination(autresDenominationsTexte, "");									
										autreDenomination.setFiche(this);
										_contextDB.autreDenominationDao.create(autreDenomination);
									}

									else{
										ErrorCollector.getInstance().addError(errorGroup, "rubrique autre dénomination vide", "Rubrique autre dénomination existante mais vide pour la fiche "+this.getNumeroFiche()+" -"+getNomCommun());
									}
									
								} else {
									
									String contenuTexte = commonOutils.nettoyageTextes(elementTD.getRenderer().toString().trim());
									//log.debug("getFiche() - contenu(initial) : " + contenuTexte);
	
									// permet d'enlever les Liens et de les remplacer par une balise qui sera réutilisée dans l'appli.
									//<../fiche2.asp?fiche_numero=289>
									contenuTexte = contenuTexte.replaceAll("<[^>]*fiche_numero=([^>]*)>", "{{$1}}").trim();
	
									// parfois il y a encore d'autre <A ...> qui pointent vers rien
									// On les enlève
									contenuTexte = contenuTexte.replaceAll("<[^>]*>", "").trim();
									
									//log.info("getFicheFromHtml() - rubrique : " + rubrique);
									//log.info("getFicheFromHtml() - contenu(après nettoyage) : " + contenuTexte);
									
									if ( ! contenuTexte.isEmpty() ){
										positionSectionDansFiche++;
										SectionFiche contenu = new SectionFiche(positionSectionDansFiche, rubrique, contenuTexte);
										contenu.setFiche(this);
										_contextDB.sectionFicheDao.create(contenu);
									}
									else{
										ErrorCollector.getInstance().addError(errorGroup, "rubrique "+rubrique+" vide", "Rubrique "+rubrique+" existante mais vide pour la fiche "+this.getNumeroFiche()+" -"+getNomCommun());
									}
								}
							}
						}
						
						// Création de la liste des Liens (url vers d'autres fiches)
						/*
						for (Element elementTDA : elementTD.getAllElements(HTMLElementName.A)) {
							String hrefValue = elementTDA.getAttributeValue("href");
							log.debug("getFicheFromHtml() - A : " + elementTDA.getRenderer().toString().trim() + " - lien : " + hrefValue);
							
							if (hrefValue != null && (hrefValue.startsWith("../") || hrefValue.startsWith("http://doris.ffessm.fr")) ) {
							
								if (elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "") != "" && elementTDA.getRenderer().toString().trim() != "") {
								
									String tempLien = elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "").replaceAll("&.*", "");
									
									if (!sbListeLiensVersFiches.toString().contains(tempLien + ";")) {
										sbListeLiensVersFiches.append(tempLien + ";");
									}
									log.info("getFicheFromHtml() - listeLienRencontre : " + sbListeLiensVersFiches.toString() );
								}
							}
						}*/
					}
					listeElementsMG_TD = null;
					
					
					//Recup du TD qui contient les infos DROITE (images et qui a fait la fiche)
					//Recup du TR dont le 3ème TD fils contient les infos DROITE (images et qui a fait la fiche)
					List<? extends Element> listeElementsEntoureDeGris = elementTable_TABLE.getAllElementsByClass("trait_cadregris");
					//log.debug("getFiche() -  element : " + " - " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),30)));
					
					for (Element elementEntoureDeGris : listeElementsEntoureDeGris) {
						//log.debug("getFiche() - vignette 1: " + elementEntoureDeGris.toString().substring(0, Math.min(100,elementEntoureDeGris.toString().toString().length())));
						
						// Les participants
						// On vérifie que le cadre Gris contient la rubrique Participants
						Element elementRubriqueinEntoureDeGris= elementEntoureDeGris.getFirstElementByClass("rubrique");
						if (elementRubriqueinEntoureDeGris != null) {
							//log.debug("getFicheFromHtml() - 520 - "+elementRubriqueinEntoureDeGris.getRenderer().toString());
							if (elementRubriqueinEntoureDeGris.getRenderer().toString().trim().contains("Participants")) {
								// On parcourt les TD
								// Si class=gris_gras et contenu texte != vide => qualité de la personne ci-après
								// Si class=normal et contenu texte != vide => nom de la personne
								// TODO : Si class=normal et contenu texte = vide  et Contient <A> => 
								//    ref de la personne = echo href | grep "s/.*contact_numero=(.*)/$1/"
								ParticipantKind intervenantQualite = null;
								String intervenantRef = null;
								
								String listeParticipantsTexteBrute = elementRubriqueinEntoureDeGris.getParentElement().getParentElement().getFirstElement(HTMLElementName.TABLE).getRenderer().toString();
								//log.debug("getFiche() - 525 - "+listeParticipantsTexteBrute);
				
								//String[] listeParticipantsTexte = listeParticipantsTexteBrute.split("\n");
								//log.debug("getFiche() - 530 - "+listeParticipantsTexte.length);
								
								for (String ligne : listeParticipantsTexteBrute.split("\n")) {
									//log.debug("getFiche() - 535 - "+ligne.trim());
									
									if ( Constants.getTypeParticipant(ligne.trim()) != null ){
										intervenantQualite = Constants.getTypeParticipant(ligne.trim() );
										//log.debug("getFicheFromHtml() - Type Intervenant: " + intervenantQualite);
									}
									
									if ( ligne.trim().contains("contact_numero=") ){
										intervenantRef = ligne.trim().replaceAll(".*contact_fiche.*contact_numero=(.*)>", "$1");
										//log.debug("getFicheFromHtml() - Ref Intervenant: " + intervenantRef);
										
										Participant participant = siteDoris.getParticipantFromListeParticipants(listeParticipants, Integer.valueOf(intervenantRef) );
										if (participant != null) {
											IntervenantFiche intervenantFiche = new IntervenantFiche(  participant, intervenantQualite.ordinal());
											intervenantFiche.setFiche(this);
											_contextDB.intervenantFicheDao.create(intervenantFiche);
										}
										else{
											ErrorCollector.getInstance().addError(errorGroup, "Participant "+intervenantRef+" introuvable dans la base");
										}
									}
									
								}
							}
						}
										
					} // Fin parcourt des Cadres Gris
					listeElementsEntoureDeGris = null;
					
					// Les dates de Créations et de nodifications
					// Elles sont dans le seul TD de class = normalgris
					Element ElementDates=elementTable_TABLE.getFirstElementByClass("normalgris");
					//log.info("getFiche() - Bloc Dates : " + ElementDates.getRenderer().toString());
					
					dateCreation = commonOutils.nettoyageTextes(ElementDates.getRenderer().toString()).replaceAll("Création le.: ([^ ]*).*", "$1").trim();
					dateModification = commonOutils.nettoyageTextes(ElementDates.getRenderer().toString()).replaceAll(".*modification le(.*) [^ ]*", "$1").trim();
					//log.debug("getFicheFromHtml() - dateCreation : " + dateCreation);
					//log.debug("getFicheFromHtml() - dateModification : " + dateModification);
					break; // Fin de la recherche d'infos dans le bloc principal
					
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
				//log.debug("getFicheFromHtml() - index : " + index);
				
				if (index == 1) {					
					// Texte du sous-groupe (inutile pour nous)
				}
				if (index == 2) {
					
					listeAttributs = element.getFirstElement(HTMLElementName.IMG).getAttributes();
					for (Attribute attribut : listeAttributs) {
						
						if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_sousgroupe/") ) {
							// Certaines fiches appartiennent à un groupe sans être dans un sous-groupe
							// c'est pourquoi, on a d'abord initialiser les groupes
							// et qu'on l'écrase ici. (on a l'arborescence des groupes par ailleurs)
							sousgroupeRef = Integer.parseInt(attribut.getValue().toString().toLowerCase().replaceAll(".*images_sousgroupe/([0-9]*).(gif|jpg)","$1"));
							//log.info("getFicheFromHtml() - sousgroupeRef : " + sousgroupeRef);

							groupe = siteDoris.getGroupeFromListeGroupes(listeGroupes, groupeRef, sousgroupeRef);
							
						}
					}
				}
			}
			listeElementsTDSousGroupe = null;
		}
		
		
		//Lecture des informations pour une fiche proposée et pour le début d'une fiche en cours de rédaction
		if ( getEtatFiche() == 1 || getEtatFiche() == 2 || getEtatFiche() == 3
				|| getEtatFiche() == 5 ) {
			ElementTable=source.getFirstElementByClass("trait_cadregris");
			//log.debug("getFiche() - ElementTable : " + ElementTable.toString().substring(0, Math.min(ElementTable.toString().length(),200)));

			listeElementsTable_TABLE = ElementTable.getAllElements(HTMLElementName.TABLE);
			//log.debug("getFiche() - listeElementsTable_TABLE.size : " + listeElementsTable_TABLE.size());

			for (Element elementTable_TABLE : listeElementsTable_TABLE) {
				num_table++;
				//log.debug("getFiche() - num_table :" + num_table);
				//log.debug("getFiche() - elementTable_TABLE.length() : " + elementTable_TABLE.toString().length());
				//log.debug("getFiche() - elementTable_TABLE : " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),100)));

				
				//Entête de la Fiche
				if (num_table== 2) {
			
					Element ElementInfosGauche = elementTable_TABLE.getFirstElementByClass("code_fiche").getFirstElement();
					
					try	{
						Element ElementNomLatin = ElementInfosGauche.getFirstElementByClass("texte_bandeau").getFirstElement();
						//log.info("getFicheFromHtml() - ElementNomLatin : " + ElementNomLatin.getRenderer().toString().trim());
						setNomScientifique( ElementNomLatin.getRenderer().toString().trim() );
					} catch (Exception e) {
		        		log.debug("getFicheFromHtml() - le nom latin n'est pas toujours renseigné");
		        	}
					
					try	{
						Element ElementDistribution = ElementInfosGauche.getFirstElementByClass("normal").getFirstElement();
						//log.info("getFicheFromHtml() - ElementDistribution : " + ElementDistribution.getRenderer().toString().trim());

						String ficheRegion = ElementDistribution.getRenderer().toString().trim();
						if ( ! ficheRegion.isEmpty() ){
							positionSectionDansFiche++;
							SectionFiche contenu = new SectionFiche(positionSectionDansFiche,"Distribution", ficheRegion);
							contenu.setFiche(this);
							_contextDB.sectionFicheDao.create(contenu);
						}
					} catch (Exception e) {
		        		log.debug("getFicheFromHtml() - la Distribution n'est pas toujours renseignée");
		        	}
					
					try	{
						Element ElementNomCommun = ElementInfosGauche.getFirstElementByClass("titre2").getFirstElement();
						//log.info("getFicheFromHtml() - ElementNomCommun : " + ElementNomCommun.getRenderer().toString().trim());
						setNomCommunNeverEmpty( commonOutils.nettoyageTextes(ElementNomCommun.getRenderer().toString().replaceAll("\\{\\{[^\\}]*\\}\\}", "")).trim() );
					} catch (Exception e) {
		        		log.debug("getFicheFromHtml() - le nom français n'est pas toujours renseigné");
		        	}
					
					//Recup TRs Haut Droit contenant le Groupe auquel appartient l'espèce
					List<? extends Element> listeElementsIMG = elementTable_TABLE.getAllElements(HTMLElementName.IMG);
					for (Element element : listeElementsIMG) {
						listeAttributs=element.getAttributes();
						for (Attribute attribut : listeAttributs) {
							
							if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_groupe/") ) {

								groupeRef = Integer.parseInt(attribut.getValue().toString().replaceAll(".*images_groupe/([0-9]*).gif","$1"));
								//log.info("getFicheFromHtml() - groupeRef : " + groupeRef);

								groupe = siteDoris.getGroupeFromListeGroupes(listeGroupes, groupeRef, 0);

							}
						}
					}
					listeElementsIMG = null;
				} // Fin Table avec Nom Commun et Scientifique
					


			}
			
			//Recup du sous-Groupe auquel appartient l'espèce
			//log.debug("getFiche() - Recup du sous-Groupe auquel appartient l'espèce");
			List<? extends Element> listeElementsTDSousGroupe = source.getAllElementsByClass("sousgroupe_fiche");
			
			for (Element element : listeElementsTDSousGroupe) {
				int index = listeElementsTDSousGroupe.indexOf(element);
				//log.debug("getFiche() - index : " + index);
				
				if (index == 1) {
				}
				if (index == 2) {
					
					listeAttributs = element.getFirstElement(HTMLElementName.IMG).getAttributes();
					for (Attribute attribut : listeAttributs) {
						
						if (attribut.getName().equals("src") && attribut.getValue().toString().startsWith("gestionenligne/images_sousgroupe/") ) {
							sousgroupeRef = Integer.parseInt(attribut.getValue().toString().toLowerCase().replaceAll(".*images_sousgroupe/([0-9]*).(gif|jpg)","$1"));
							//log.info("getFicheFromHtml() - sousgroupeRef : " + sousgroupeRef);

							groupe = siteDoris.getGroupeFromListeGroupes(listeGroupes, groupeRef, sousgroupeRef);
						}
					}
				}
			} // Fin recherche Sous-Groupe
			listeElementsTDSousGroupe = null;
			
			//Recup du TD qui contient les infos DROITE (images et qui a fait la fiche)
			//Recup du TR dont le 3ème TD fils contient les infos DROITE (images et qui a fait la fiche)
			List<? extends Element> listeElementsEntoureDeGris = source.getAllElementsByClass("trait_cadregris");
			
			for (Element elementEntoureDeGris : listeElementsEntoureDeGris) {
				//log.debug("getFicheFromHtml() - 540 - elementEntoureDeGris : " + elementEntoureDeGris.toString().substring(0, Math.min(100,elementEntoureDeGris.toString().toString().length())));
				
				// Les participants
				// On vérifie que le cadre Gris contient la rubrique Participants
				Element elementRubriqueinEntoureDeGris= elementEntoureDeGris.getFirstElementByClass("rubrique");
				if (elementRubriqueinEntoureDeGris != null) {
					//log.debug("getFicheFromHtml() - 550 - "+elementRubriqueinEntoureDeGris.getRenderer().toString());
					if (elementRubriqueinEntoureDeGris.getRenderer().toString().trim().contains("Participants")) {
						// On parcourt les TD
						// Si class=gris_gras et contenu texte != vide => qualité de la personne ci-après
						// Si class=normal et contenu texte != vide => nom de la personne
						// TODO : Si class=normal et contenu texte = vide  et Contient <A> => 
						//    ref de la personne = echo href | grep "s/.*contact_numero=(.*)/$1/"
						ParticipantKind intervenantQualite = null;
						String intervenantRef = null;
						
						String listeParticipantsTexteBrute = elementRubriqueinEntoureDeGris.getParentElement().getParentElement().getFirstElement(HTMLElementName.TABLE).getRenderer().toString();
						//log.debug("getFicheFromHtml() - 555 - "+listeParticipantsTexteBrute);
						
						for (String ligne : listeParticipantsTexteBrute.split("\n")) {
							//log.debug("getFicheFromHtml() - 565 - "+ligne.trim());
							
							if ( Constants.getTypeParticipant(ligne.trim()) != null ){
								intervenantQualite = Constants.getTypeParticipant(ligne.trim() );
								//log.info("getFicheFromHtml() - Type Intervenant: " + intervenantQualite);
							}
							
							if ( ligne.trim().contains("contact_numero=") ){
								intervenantRef = ligne.trim().replaceAll(".*contact_fiche.*contact_numero=(.*)>", "$1");
								//log.info("getFicheFromHtml() - Ref Intervenant: " + intervenantRef);
								
								Participant participant = siteDoris.getParticipantFromListeParticipants(listeParticipants, Integer.valueOf(intervenantRef) );
								if (participant != null) {
									IntervenantFiche intervenantFiche = new IntervenantFiche( participant, intervenantQualite.ordinal());
									intervenantFiche.setFiche(this);
									_contextDB.intervenantFicheDao.create(intervenantFiche);
								}				
							}
							
						}
					}
				}
								
			} // Fin parcourt des Cadres Gris
			listeElementsEntoureDeGris = null;
			
			// Les dates de Créations et de modifications
			// Elles sont dans le seul TD de class = normalgris
			Element ElementDates=source.getFirstElementByClass("normalgris2");
			//log.info("getFiche() - Bloc Dates : " + ElementDates.getRenderer().toString());
			
			dateCreation = ElementDates.getRenderer().toString().replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll(".*DORIS[^,]*,([^:]*):.*", "$1").trim();
			dateModification = dateCreation;
			log.debug("getFicheFromHtml() - dateCreation : " + dateCreation);
			//log.debug("getFicheFromHtml() - dateModification : " + dateModification);
		
		}
		
		// Partie basse de la Fiche
		/* Premier class="tableau_trait"
		 * Remonter de 5 niveaux : <table width="100%" border="0" cellspacing="0" cellpadding="0">
		 * Pour chaque <TR> de profondeur +1 : getChildElements()
		 * Le 5ème TR contient le 1er Titre
		 * Le 6ème le 1er contenu
		 * le 7ème est vide => enregistrement : RAZ valeur Titre & Contenu
		 * le 8ème le 2ème titre
		 * le 9ème le 2ème contenu
		 * le 11ème etc. etc.
		 */
		
		if (etatFiche == 4) {
			// Premier class="tableau_trait" ce qui permet de repérer le tableau de la classification
			Element ElementTableauTrait=source.getFirstElementByClass("tableau_trait");
			if (ElementTableauTrait!=null) {
				//log.debug("getFiche() - Bloc TableauTrait : " + ElementTableauTrait.toString().substring(0, Math.min(ElementTableauTrait.toString().length(),30)));
				
				//Remonter de 5 niveaux : <table width="100%" border="0" cellspacing="0" cellpadding="0">
				Element ElementTableBasse=ElementTableauTrait.getParentElement().getParentElement().getParentElement().getParentElement().getParentElement();
				//log.debug("getFiche() - Bloc TableBasse : " + ElementTableBasse.toString().substring(0, Math.min(ElementTableBasse.toString().length(),30)));
				
				int indice = 0;
				String dernierTitreSection="";
				for (Element element : ElementTableBasse.getChildElements()) {
					indice++;

					if (indice >= 5){
						if (element.getContent().toString().contains("images/black_round_grey")) {
							String section = element.getRenderer().toString().trim();
							
							//Certain TR ne sont pas constitué de la même manière que les autres
							//Dans 1 TR il y a une TABLE
							if (element.getContent().toString().contains("table") ){
								//log.debug("getFiche() - Test 1 : " + element.getContent().toString());
								Element sousElement = element.getFirstElement(HTMLElementName.IMG).getParentElement();
								if (sousElement != null) {
									//log.debug("getFiche() - Test 2 : " + sousElement.getContent().toString());
									section = commonOutils.nettoyageTextes(sousElement.getRenderer().toString().trim());
								}
							}
							//log.info("getFiche() - Section : " + section);
							dernierTitreSection = section;
						}
						if (element.getContent().toString().contains("class=\"normal\"")) {
							String texte = element.getRenderer().toString().trim();
							
							//log.info("getFiche() - Texte : " + texte);
							
							// Entrées Bibliographiques
							if (element.getContent().toString().contains("table") ){
								Element sousElement = element.getFirstElementByClass("table_biblio");
								if (sousElement != null) {
									texte="";
									sousElement = sousElement.getParentElement().getParentElement().getParentElement();
									
									for (Element elementClassNormal : sousElement.getAllElementsByClass("normal")) {
										texte += elementClassNormal.getRenderer().toString().trim()+"{{n/}}{{n/}}";
									}
								}
							}
							
							texte = commonOutils.nettoyageTextes(texte);
							//log.info("getFiche() - Texte après nettoyage : " + texte);
							positionSectionDansFiche++;
							SectionFiche contenu = new SectionFiche(100+positionSectionDansFiche, dernierTitreSection, texte);
							contenu.setFiche(this);
							_contextDB.sectionFicheDao.create(contenu);
						}
						
					}
				} // Fin extraction données du bas de la page sauf tableau Phylogénique
				
				//Remonter de 2 niveaux pour avoir le tableau

				Element ElementTableauPhylogenetique=ElementTableauTrait.getParentElement().getParentElement();
				//log.debug("getFiche() - Bloc Tableau Phylogenetique : " + ElementTableauPhylogenetique.toString().substring(0, Math.min(ElementTableBasse.toString().length(),30)));
				
				indice = 0;
				String type_classification = "";
				String terme_scientifique = "";
				String terme_francais = "";
				String description = "";
				for (Element elementTR : ElementTableauPhylogenetique.getAllElements(HTMLElementName.TR)) {
					indice++;
					// La 1ère Ligne contient les Titres => On oublie
					if (indice != 1){
						int numColonne = 0;
						for (Element elementTD : elementTR.getAllElements(HTMLElementName.TD)) {
							numColonne ++;
							if (numColonne == tableauPhylogenetiqueColonnes.CLASSIFICATION_TYPE.ordinal() + 1) {
								type_classification = elementTD.getRenderer().toString();
							}
							if (numColonne == tableauPhylogenetiqueColonnes.TERME_SCIENTIFIQUE.ordinal() + 1) {
								terme_scientifique = elementTD.getRenderer().toString();
							}
							if (numColonne == tableauPhylogenetiqueColonnes.TERME_FRANCAIS.ordinal() + 1) {
								terme_francais = elementTD.getRenderer().toString();
							}
							if (numColonne == tableauPhylogenetiqueColonnes.DESCRIPTION.ordinal() + 1) {
								description = elementTD.getRenderer().toString().trim();
								if (description.equals("{{n/}}")) description = "";
							}
						}

						//log.debug("getFiche() - Tableau Phylogenetique : " + type_classification + " - "
						//		+ terme_scientifique + " - " + terme_francais + " - " + description);
						
						// Si la classification n'a pas encore été créée dans le dico, on le fait
						Map<String, Object> sqlMap = new HashMap<String, Object>();
						sqlMap.put("niveau", type_classification);
						sqlMap.put("termeScientifique", terme_scientifique);

						Classification classification = new Classification();

						List<Classification> listClassification = _contextDB.classificationDao.queryForFieldValues(sqlMap); 
						if ( listClassification.isEmpty() ){
							classification = new Classification(type_classification, terme_scientifique, terme_francais, commonOutils.nettoyageTextes(description));
							_contextDB.classificationDao.create(classification);
						} else {
							classification = listClassification.get(0);
						}
						// Création de la relation entre la Classification et la Fiche
						ClassificationFiche classificationFiche = new ClassificationFiche( classification, indice);
						classificationFiche.setFiche(this);
						_contextDB.classificationFicheDao.create(classificationFiche);
							
						
					}
				}
				
				
			}
		}
		
		// TODO : Est-ce vraiment toujours utile ?
		/*
		if (sbListeLiensVersFiches.length() !=0){
			setNumerofichesLiees(sbListeLiensVersFiches.toString());									
		}
		*/
		
		
		// Texte pour recherche rapide dans les listes de fiches
		StringBuilder sbTextePourRechercheRapide = new StringBuilder();
		if (getNomCommun() != null) {
			sbTextePourRechercheRapide.append(getNomCommun());
		}
		if (getNomScientifique() != null) sbTextePourRechercheRapide.append(" "+getNomScientifique().replaceAll("\\([^\\)]*\\)", ""));
		sbTextePourRechercheRapide.append(" "+autresDenominationsPourRechercheRapide.trim());
		
		sbTextePourRechercheRapide = new StringBuilder(sbTextePourRechercheRapide.toString().replaceAll("\\{\\{[^\\}]*\\}\\}", "") );
		setTextePourRechercheRapide( commonOutils.formatStringNormalizer(sbTextePourRechercheRapide.toString()).toLowerCase() );
		
		// RAZ
		source = null;
		listeElementsTable_TABLE = null;
		listeElementsTable = null;
		listeAttributs = null;
    	//log.trace("getFicheFromHtml() - Fin");
	}
	
	enum tableauPhylogenetiqueColonnes{
		CLASSIFICATION_TYPE,
		TERME_SCIENTIFIQUE,
		TERME_FRANCAIS,
		DESCRIPTION
	}
	
	public Boolean updateFromFiche(Fiche ficheUpdated){
		/* TODO : Mettre à jour les listes
		* - des Contenues : SectionFiche
		* - des Photos
		* - Autres Dénominations
		* - Intervenants
		*/
		if (this.numeroFiche != ficheUpdated.numeroFiche) {
			return false;
		}
		
		this.etatFiche = ficheUpdated.etatFiche;
		this.nomScientifique = ficheUpdated.nomScientifique;
		this.nomCommun = ficheUpdated.nomCommun;
		
		this.dateCreation = ficheUpdated.dateCreation;
		this.dateModification = ficheUpdated.dateModification;
		
		this.numerofichesLiees = ficheUpdated.numerofichesLiees;
		this.textePourRechercheRapide = ficheUpdated.textePourRechercheRapide;
		this.pictogrammes = ficheUpdated.pictogrammes;
		
		return true;
	}
	
	/**
	 * @return liste des numeroFiche des fiches liées
	 */
	public List<Integer> getNumerosFichesLiees(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(String  s : this.numerofichesLiees.split(";")){
			if (!s.isEmpty()) result.add(Integer.parseInt(s));
		}
		return result;
	}
	
	public Fiche(FicheLight ficheLight) {
		super();
		this.nomScientifique = ficheLight.getNomScientifique();
		
		setNomCommunNeverEmpty(ficheLight.getNomCommun());
		
		this.numeroFiche = ficheLight.getNumeroFiche();
		this.etatFiche = ficheLight.getEtatFiche();
	}
	
	public void setNomCommunNeverEmpty(java.lang.String nomCommun) {
		if (nomCommun.isEmpty()) nomCommun = Constants.FICHE_NOMCOMMUN_VALUE_IF_EMPTY;
		this.nomCommun = nomCommun;
	}
	public java.lang.String getNomCommunNeverEmpty() {
		if (this.nomCommun.equals(Constants.FICHE_NOMCOMMUN_VALUE_IF_EMPTY)) return "";
		return this.nomCommun;
	}
	// End of user code
	
	public Fiche() {} // needed by ormlite
	public Fiche(java.lang.String nomScientifique, java.lang.String nomCommun, int numeroFiche, int etatFiche, java.lang.String dateCreation, java.lang.String dateModification, java.lang.String numerofichesLiees, java.lang.String textePourRechercheRapide, java.lang.String pictogrammes) {
		super();
		this.nomScientifique = nomScientifique;
		this.nomCommun = nomCommun;
		this.numeroFiche = numeroFiche;
		this.etatFiche = etatFiche;
		this.dateCreation = dateCreation;
		this.dateModification = dateModification;
		this.numerofichesLiees = numerofichesLiees;
		this.textePourRechercheRapide = textePourRechercheRapide;
		this.pictogrammes = pictogrammes;
	} 

	public int getId() {
		return _id;
	}
	public void setId(int id) {
		this._id = id;
	}

	public DorisDBHelper getContextDB(){
		return _contextDB;
	}
	public void setContextDB(DorisDBHelper contextDB){
		this._contextDB = contextDB;
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
	public java.lang.String getNumerofichesLiees() {
		return this.numerofichesLiees;
	}
	public void setNumerofichesLiees(java.lang.String numerofichesLiees) {
		this.numerofichesLiees = numerofichesLiees;
	}
	public java.lang.String getTextePourRechercheRapide() {
		return this.textePourRechercheRapide;
	}
	public void setTextePourRechercheRapide(java.lang.String textePourRechercheRapide) {
		this.textePourRechercheRapide = textePourRechercheRapide;
	}
	public java.lang.String getPictogrammes() {
		return this.pictogrammes;
	}
	public void setPictogrammes(java.lang.String pictogrammes) {
		this.pictogrammes = pictogrammes;
	}

	/** Liste des photos de la fiche */
	public Collection<PhotoFiche> getPhotosFiche() {
		return this.photosFiche;
	}					
	/** zones  où l'on peut observer l'élément décrit par la fiche */
	public Collection<ZoneObservation> getZonesObservation() {
		return this.zonesObservation;
	}					
	/** contenu textuel de la fiche */
	public Collection<SectionFiche> getContenu() {
		return this.contenu;
	}					
	/** Photo par défaut de l'espèce présentée par cette fiche. Elle est aussi présente dans la liste "photosFiche". */ 
	public PhotoFiche getPhotoPrincipale() {
		try {
			if(photoPrincipale_mayNeedDBRefresh && _contextDB != null){
				_contextDB.photoFicheDao.refresh(this.photoPrincipale);
				photoPrincipale_mayNeedDBRefresh = false;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
		}
		if(_contextDB==null && this.photoPrincipale == null){
			log.warn("Fiche may not be properly refreshed from DB (_id="+_id+")");
		}
		return this.photoPrincipale;
	}
	public void setPhotoPrincipale(PhotoFiche photoPrincipale) {
		this.photoPrincipale = photoPrincipale;
	}			
	/** Liste des autres dénominations de l'espèce présentée sur la fiche. */
	public Collection<AutreDenomination> getAutresDenominations() {
		return this.autresDenominations;
	}					
	/** Permet d'identifier avec le sous-groupe (optionnel) le groupe auquel est rattaché la fiche */ 
	public Groupe getGroupe() {
		try {
			if(groupe_mayNeedDBRefresh && _contextDB != null){
				_contextDB.groupeDao.refresh(this.groupe);
				groupe_mayNeedDBRefresh = false;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
		}
		if(_contextDB==null && this.groupe == null){
			log.warn("Fiche may not be properly refreshed from DB (_id="+_id+")");
		}
		return this.groupe;
	}
	public void setGroupe(Groupe groupe) {
		this.groupe = groupe;
	}			
	/** intervenants sur une fiche */
	public Collection<IntervenantFiche> getIntervenants() {
		return this.intervenants;
	}					
	/** Tableau Phylogénétique */
	public Collection<ClassificationFiche> getClassification() {
		return this.classification;
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
		sb.append("\n"+indent+"\t<"+XML_ATT_NUMEROFICHESLIEES+">");
		sb.append(StringEscapeUtils.escapeXml(this.numerofichesLiees));
    	sb.append("</"+XML_ATT_NUMEROFICHESLIEES+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TEXTEPOURRECHERCHERAPIDE+">");
		sb.append(StringEscapeUtils.escapeXml(this.textePourRechercheRapide));
    	sb.append("</"+XML_ATT_TEXTEPOURRECHERCHERAPIDE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_PICTOGRAMMES+">");
		sb.append(StringEscapeUtils.escapeXml(this.pictogrammes));
    	sb.append("</"+XML_ATT_PICTOGRAMMES+">");

		sb.append("\n"+indent+"\t<"+XML_REF_PHOTOSFICHE+">");
		if(this.photosFiche != null){
			for(PhotoFiche ref : this.photosFiche){
				sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
	    	}
		}
		sb.append("</"+XML_REF_PHOTOSFICHE+">");		
		
		for(ZoneGeographique ref : this.getZonesGeographiques()){
    		sb.append("\n"+indent+"\t<"+XML_REF_ZONESGEOGRAPHIQUES+" id=\"");
    		sb.append(ref._id);
        	sb.append("\"/>");
			
    	}
			
		if(this.zonesObservation != null){
			for(ZoneObservation ref : this.zonesObservation){
					
	    		sb.append("\n"+indent+"\t<"+XML_REF_ZONESOBSERVATION+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
	    	}		
		}
		sb.append("\n"+indent+"\t<"+XML_REF_CONTENU+">");
		if(this.contenu != null){
			for(SectionFiche ref : this.contenu){
				sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
	    	}
		}
		sb.append("</"+XML_REF_CONTENU+">");		
		if(this.photoPrincipale!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_PHOTOPRINCIPALE+">");
			sb.append(this.photoPrincipale.getId());
	    	sb.append("</"+XML_REF_PHOTOPRINCIPALE+">");
		}
		sb.append("\n"+indent+"\t<"+XML_REF_AUTRESDENOMINATIONS+">");
		if(this.autresDenominations != null){
			for(AutreDenomination ref : this.autresDenominations){
				sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
	    	}
		}
		sb.append("</"+XML_REF_AUTRESDENOMINATIONS+">");		
		if(this.groupe!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_GROUPE+">");
			sb.append(this.groupe.getId());
	    	sb.append("</"+XML_REF_GROUPE+">");
		}
		if(this.intervenants != null){
			for(IntervenantFiche ref : this.intervenants){
					
	    		sb.append("\n"+indent+"\t<"+XML_REF_INTERVENANTS+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
	    	}		
		}
		if(this.classification != null){
			for(ClassificationFiche ref : this.classification){
					
	    		sb.append("\n"+indent+"\t<"+XML_REF_CLASSIFICATION+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
	    	}		
		}
		// TODO deal with other case

		sb.append("</"+XML_FICHE+">");
		return sb.toString();
	}
}
