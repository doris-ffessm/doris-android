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
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.datamodel.associations.*;

// Start of user code additional import for DefinitionGlossaire
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import fr.ffessm.doris.android.sitedoris.Outils;
// End of user code

/** 
  * Entrée pour les définitions du Glossaire 
  */ 
@DatabaseTable(tableName = "definitionGlossaire")
public class DefinitionGlossaire {

	public static Log log = LogFactory.getLog(DefinitionGlossaire.class);

	public static final String XML_DEFINITIONGLOSSAIRE = "DEFINITIONGLOSSAIRE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NUMERODORIS = "numeroDoris";
	public static final String XML_ATT_TERME = "terme";
	public static final String XML_ATT_DEFINITION = "definition";
	public static final String XML_ATT_CLEURLILLUSTRATION = "cleURLIllustration";
	public static final String XML_REF_FICHESCONCERNEES = "fichesConcernees";
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
	

	/** numéro de la définition sur le site Doris */ 
	@DatabaseField
	protected int numeroDoris;

	/** terme de l'entrée */ 
	@DatabaseField
	protected java.lang.String terme;

	/** définition de cette entrée */ 
	@DatabaseField(dataType = com.j256.ormlite.field.DataType.LONG_STRING)
	protected java.lang.String definition;

	/** URL de l'illustration éventuelle de la définition */ 
	@DatabaseField
	protected java.lang.String cleURLIllustration;
	

	/** Fiches concernées par cette définition (Ie. qui utilise dans leur texte cette définition) */ 
	// work in progress, find association 
	// Association many to many Fiches_DefinitionsGlossaire
	@ForeignCollectionField(eager = false, foreignFieldName = "definitionGlossaire")	
	protected ForeignCollection<Fiches_DefinitionsGlossaire> fiches_DefinitionsGlossaire;

	/** Fiches concernées par cette définition (Ie. qui utilise dans leur texte cette définition) 
	  * Attention, returned list is readonly
      */
	public List<Fiche> getFichesConcernees(){
		List<Fiche> result = new ArrayList<Fiche>();
		
		for (Fiches_DefinitionsGlossaire aFiches_DefinitionsGlossaire : fiches_DefinitionsGlossaire) {
			if(_contextDB != null) aFiches_DefinitionsGlossaire.setContextDB(_contextDB);
			result.add(aFiches_DefinitionsGlossaire.getFiche());
		}
		return result;
	}
	public void addFiche(Fiche fiche){
		try {
			_contextDB.fiches_DefinitionsGlossaireDao.create(new Fiches_DefinitionsGlossaire( fiche, this));		
		} catch (SQLException e) {
			log.error("Pb while adding association fiches_DefinitionsGlossaire",e);
		}
	}
	// end work in progress 	


	// Start of user code DefinitionGlossaire additional user properties

    public void getDefinitionsFromHtml(String inCodePageHtml){
    	log.debug("getDefinitionsFromHtml() - Début");

    	inCodePageHtml = Outils.nettoyageBalises(inCodePageHtml);

    	inCodePageHtml = Outils.remplacementBalises(inCodePageHtml, true);
    	
    	Source source=new Source(inCodePageHtml);
    	source.fullSequentialParse();
    	log.debug("getDefinitionsFromHtml()- source.length() : " + source.length());
    	
    	Element elementsTDTitre2 = source.getFirstElementByClass("titre2");
    	terme = Outils.nettoyageTextes( elementsTDTitre2.getRenderer().toString().replace(":", "").trim() );
    	log.debug("getDefinitionsFromHtml()- motDefini : " + terme);
    	
    	definition = elementsTDTitre2.getParentElement().getParentElement().getFirstElementByClass("normal").getRenderer().toString();
    	definition = Outils.nettoyageTextes(definition);
    	log.debug("getDefinitionsFromHtml()- Définition : " + definition);
    	
    	log.debug("getDefinitionsFromHtml() - Fin");
    }
	
	
	// End of user code
	
	public DefinitionGlossaire() {} // needed by ormlite
	public DefinitionGlossaire(int numeroDoris, java.lang.String terme, java.lang.String definition, java.lang.String cleURLIllustration) {
		super();
		this.numeroDoris = numeroDoris;
		this.terme = terme;
		this.definition = definition;
		this.cleURLIllustration = cleURLIllustration;
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

	public int getNumeroDoris() {
		return this.numeroDoris;
	}
	public void setNumeroDoris(int numeroDoris) {
		this.numeroDoris = numeroDoris;
	}
	public java.lang.String getTerme() {
		return this.terme;
	}
	public void setTerme(java.lang.String terme) {
		this.terme = terme;
	}
	public java.lang.String getDefinition() {
		return this.definition;
	}
	public void setDefinition(java.lang.String definition) {
		this.definition = definition;
	}
	public java.lang.String getCleURLIllustration() {
		return this.cleURLIllustration;
	}
	public void setCleURLIllustration(java.lang.String cleURLIllustration) {
		this.cleURLIllustration = cleURLIllustration;
	}




	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_DEFINITIONGLOSSAIRE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NUMERODORIS+">");
		sb.append(this.numeroDoris);
    	sb.append("</"+XML_ATT_NUMERODORIS+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TERME+">");
		sb.append(StringEscapeUtils.escapeXml(this.terme));
    	sb.append("</"+XML_ATT_TERME+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DEFINITION+">");
		sb.append(StringEscapeUtils.escapeXml(this.definition));
    	sb.append("</"+XML_ATT_DEFINITION+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_CLEURLILLUSTRATION+">");
		sb.append(StringEscapeUtils.escapeXml(this.cleURLIllustration));
    	sb.append("</"+XML_ATT_CLEURLILLUSTRATION+">");

		
		for(Fiche ref : this.getFichesConcernees()){
    		sb.append("\n"+indent+"\t<"+XML_REF_FICHESCONCERNEES+" id=\"");
    		sb.append(ref._id);
        	sb.append("\"/>");
			
    	}
			
		// TODO deal with other case

		sb.append("</"+XML_DEFINITIONGLOSSAIRE+">");
		return sb.toString();
	}
}
