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
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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
public class Fiche extends AbstractWebNodeObject {

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

	/** Numéro de la fiche tel que connu par le site lui même
	 TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int numeroFiche;

	/** Etat Avancement de la fiche 
4 : Fiche Publiée - 1, 2, 3 : En cours de Rédaction - 5 : Fiche Proposée
	 TEST GM : SQLite = SQLite **/
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
