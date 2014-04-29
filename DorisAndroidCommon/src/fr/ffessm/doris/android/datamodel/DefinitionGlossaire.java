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

// Start of user code additional import for DefinitionGlossaire
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
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
	/** TEST GM :  = SQLite **/
	/** Contournement Guillaume car att.storage est toujours vide chez moi **/
	@DatabaseField
	protected int numeroDoris;

	/** terme de l'entrée */ 
	/** TEST GM :  = SQLite **/
	/** Contournement Guillaume car att.storage est toujours vide chez moi **/
	@DatabaseField
	protected java.lang.String terme;

	/** définition de cette entrée */ 
	/** TEST GM :  = SQLite **/
	/** Contournement Guillaume car att.storage est toujours vide chez moi **/
	@DatabaseField(dataType = com.j256.ormlite.field.DataType.LONG_STRING)
	protected java.lang.String definition;

	/** Liste des URL des illustrations éventuelles de la définition (séparateur : ;) */ 
	/** TEST GM :  = SQLite **/
	/** Contournement Guillaume car att.storage est toujours vide chez moi **/
	@DatabaseField
	protected java.lang.String cleURLIllustration;
	

	// Start of user code DefinitionGlossaire additional user properties

    public void getDefinitionsFromHtml(String inCodePageHtml){
    	//log.debug("getDefinitionsFromHtml() - Début");
    	Common_Outils commonOutils = new Common_Outils();
    	
    	inCodePageHtml = commonOutils.nettoyageBalises(inCodePageHtml);

    	inCodePageHtml = commonOutils.remplacementBalises(inCodePageHtml, true);
    	
    	
    	Source source=new Source(inCodePageHtml);
    	source.fullSequentialParse();
    	//log.debug("getDefinitionsFromHtml()- source.length() : " + source.length());
    	
    	Element elementTDTitre2 = source.getFirstElementByClass("titre2");
    	terme = commonOutils.nettoyageTextes( elementTDTitre2.getRenderer().toString().replace(":", "").trim() );
    	log.debug("getDefinitionsFromHtml()- motDefini : " + terme);
    	
    	definition = elementTDTitre2.getParentElement().getParentElement().getFirstElementByClass("normal").getRenderer().toString();
    	
    	//log.debug("getDefinitionsFromHtml()- Définition : " + definition);
    	
    	// Traitement des définitions complexes telles que : Byssus
    	List<Element> listeElementsTR = elementTDTitre2.getParentElement().getParentElement().getParentElement()
    			.getParentElement().getParentElement().getChildElements();
    	//log.debug("getDefinitionsFromHtml()- listeElementsTR : " + listeElementsTR.toString());
    	int rangTR = 0;
    	for (Element elementTR : listeElementsTR) {
    		//log.debug("getDefinitionsFromHtml()- (element.getName() : " + element.getName());
    		if (elementTR.getName() == HTMLElementName.TR){
    			rangTR++;
    			if (rangTR == 6){
    				//log.debug("getDefinitionsFromHtml()- TR 6 : " + elementTR.getRenderer().toString());
    				List<Element> listeTablesligne = elementTR.getAllElements(HTMLElementName.TABLE);
					for (Element elementTable : listeTablesligne) {
						//log.debug("getDefinitionsFromHtml()- getDepth : " + elementTable.getDepth());
    					if ( elementTable.getDepth() == 14 ){
    						List<Element> listeTRlignes = elementTable.getAllElements(HTMLElementName.TR);
    						for (Element elementTRLigne : listeTRlignes) {
    							//log.debug("getDefinitionsFromHtml()- elementLigne getDepth : " + elementTRLigne.getDepth());
    							if ( elementTRLigne.getDepth() == 15 && !elementTRLigne.getRenderer().toString().trim().isEmpty()) {
    								definition = definition+"{{n/}}"+elementTRLigne.getRenderer().toString().trim();
    							}
    						}
    						
    					}
    				}
    				
    			}
    		}
    	}
    	definition = commonOutils.nettoyageTextes(definition);
    	log.debug("getDefinitionsFromHtml() - definition : " + definition);

    	// permet de faire gagner du temps sur le téléphone
		for (String imageDefinition : getListeImagesDefinition() ){
			cleURLIllustration = cleURLIllustration + imageDefinition + ";";
		}

		source = null;
    	//log.debug("getDefinitionsFromHtml() - Fin");
    }
	
	
    public List<String> getListeImagesDefinition(){
    	//log.debug("getListeImagesDefinition() - Début");
    	//log.debug("getListeImagesDefinition() - definition : " + definition);
    	List<String> listeImagesDefinition = new ArrayList<String>();
    	
	    String chaineCaract = definition;
		int positionCourante = 0;
		int positionFin = 0;
		int iteration = 0;
		boolean fin = false;
		
		do {
			iteration++;
			positionCourante = chaineCaract.indexOf("{{E:", 0);
			//log.debug("getListeImagesDefinition() - 1 : "+positionCourante+" - "+positionFin);
			
			if (positionCourante != -1) {

				positionFin = chaineCaract.indexOf("/}}", positionCourante);
				//log.debug("getListeImagesDefinition() - 2 : "+positionCourante+" - "+positionFin);
				
				//String image = chaineCaract.substring(positionCourante+4, positionFin);
				//log.debug("getListeImagesDefinition() - image : "+image);
				
				listeImagesDefinition.add(chaineCaract.substring(positionCourante+4, positionFin));
				
	    		positionCourante = positionFin + 3;
	    		chaineCaract = chaineCaract.substring(positionCourante);
			} else {
				fin = true;
			}
	
	 	} while( fin == false && iteration < 10);
		
		//log.debug("getListeImagesDefinition() - Fin");
		return listeImagesDefinition;
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

		// TODO deal with other case

		sb.append("</"+XML_DEFINITIONGLOSSAIRE+">");
		return sb.toString();
	}
}
