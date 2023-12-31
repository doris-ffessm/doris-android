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

// Start of user code additional import for Classification
// End of user code

/** 
  * Liste des Embranchements, Ordres, Classes, etc et de leur description 
  */ 
@DatabaseTable(tableName = "classification")
public class Classification {

	public static Log log = LogFactory.getLog(Classification.class);

	public static final String XML_CLASSIFICATION = "CLASSIFICATION";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NIVEAU = "niveau";
	public static final String XML_ATT_TERMESCIENTIFIQUE = "termeScientifique";
	public static final String XML_ATT_TERMEFRANCAIS = "termeFrancais";
	public static final String XML_ATT_DESCRIPTIF = "descriptif";
	public static final String XML_REF_CLASSIFICATIONFICHE = "classificationFiche";
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
	

	/** Niveau de la Classification = Embranchement, Groupe, etc */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String niveau;

	/** Terme Scientifique (international)
C'est lui qui permet d'identifier la Classification */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String termeScientifique;

	/** Terme Français
(il n'est pas toujours renseigné) */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String termeFrancais;

	/** Texte descriptif de la Classification */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String descriptif;
	

	@ForeignCollectionField(eager = false, foreignFieldName = "classification")
	protected ForeignCollection<ClassificationFiche> classificationFiche;

	// Start of user code Classification additional user properties

    /** Référence DORIS de la Classification  */
    /** TEST GM : SQLite = SQLite **/
    @DatabaseField
    protected int numeroDoris;

    public Classification(int numeroDoris, java.lang.String niveau, java.lang.String termeScientifique, java.lang.String termeFrancais, java.lang.String descriptif) {
        super();
        this.numeroDoris = numeroDoris;
        this.niveau = niveau;
        this.termeScientifique = termeScientifique;
        this.termeFrancais = termeFrancais;
        this.descriptif = descriptif;
    }

    public int getNumeroDoris() {
        return this.numeroDoris;
    }
    public void setNumeroDoris(int numeroDoris) {
        this.numeroDoris = numeroDoris;
    }



    // End of user code
	
	public Classification() {} // needed by ormlite
	public Classification(java.lang.String niveau, java.lang.String termeScientifique, java.lang.String termeFrancais, java.lang.String descriptif) {
		super();
		this.niveau = niveau;
		this.termeScientifique = termeScientifique;
		this.termeFrancais = termeFrancais;
		this.descriptif = descriptif;
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

	public java.lang.String getNiveau() {
		return this.niveau;
	}
	public void setNiveau(java.lang.String niveau) {
		this.niveau = niveau;
	}
	public java.lang.String getTermeScientifique() {
		return this.termeScientifique;
	}
	public void setTermeScientifique(java.lang.String termeScientifique) {
		this.termeScientifique = termeScientifique;
	}
	public java.lang.String getTermeFrancais() {
		return this.termeFrancais;
	}
	public void setTermeFrancais(java.lang.String termeFrancais) {
		this.termeFrancais = termeFrancais;
	}
	public java.lang.String getDescriptif() {
		return this.descriptif;
	}
	public void setDescriptif(java.lang.String descriptif) {
		this.descriptif = descriptif;
	}

	public Collection<ClassificationFiche> getClassificationFiche() {
		return this.classificationFiche;
	}					



	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_CLASSIFICATION);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NIVEAU+">");
		sb.append(StringEscapeUtils.escapeXml(this.niveau));
    	sb.append("</"+XML_ATT_NIVEAU+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TERMESCIENTIFIQUE+">");
		sb.append(StringEscapeUtils.escapeXml(this.termeScientifique));
    	sb.append("</"+XML_ATT_TERMESCIENTIFIQUE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TERMEFRANCAIS+">");
		sb.append(StringEscapeUtils.escapeXml(this.termeFrancais));
    	sb.append("</"+XML_ATT_TERMEFRANCAIS+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DESCRIPTIF+">");
		sb.append(StringEscapeUtils.escapeXml(this.descriptif));
    	sb.append("</"+XML_ATT_DESCRIPTIF+">");

		if(this.classificationFiche != null){
			for(ClassificationFiche ref : this.classificationFiche){
					
	    		sb.append("\n"+indent+"\t<"+XML_REF_CLASSIFICATIONFICHE+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
	    	}		
		}
		// TODO deal with other case

		sb.append("</"+XML_CLASSIFICATION+">");
		return sb.toString();
	}
}
