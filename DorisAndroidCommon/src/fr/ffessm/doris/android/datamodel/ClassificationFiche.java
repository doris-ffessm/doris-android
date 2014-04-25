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

// Start of user code additional import for ClassificationFiche
// End of user code

/** 
  * Classification sur une fiche 
  */ 
@DatabaseTable(tableName = "classificationFiche")
public class ClassificationFiche {

	public static Log log = LogFactory.getLog(ClassificationFiche.class);

	public static final String XML_CLASSIFICATIONFICHE = "CLASSIFICATIONFICHE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NUMORDRE = "numOrdre";
	public static final String XML_REF_CLASSIFICATION = "classification";
	public static final String XML_REF_FICHE = "fiche";
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
	public boolean classification_mayNeedDBRefresh = true;
	public boolean fiche_mayNeedDBRefresh = true;
	

	/** Permet d'afficher le tableau dans l'ordre du site directement */ 
	/** TEST GM :  = SQLite **/
	/** Contournement Guillaume car att.storage est toujours vide chez moi **/
	@DatabaseField
	protected int numOrdre;
	
	//GMo : Ajouté à la main 
	@DatabaseField(foreign = true) //, columnName = USER_ID_FIELD_NAME)
	protected Classification classification;

	//GMo : Ajouté à la main 
	@DatabaseField(foreign = true) //, columnName = USER_ID_FIELD_NAME)
	protected Fiche fiche;

	// Start of user code ClassificationFiche additional user properties
	
	public ClassificationFiche(Classification classification, int numOrdre) {
		super();
		this.classification = classification;
		this.numOrdre = numOrdre;
	} 
	
	// End of user code
	
	public ClassificationFiche() {} // needed by ormlite
	public ClassificationFiche(int numOrdre) {
		super();
		this.numOrdre = numOrdre;
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

	public int getNumOrdre() {
		return this.numOrdre;
	}
	public void setNumOrdre(int numOrdre) {
		this.numOrdre = numOrdre;
	}

	public Classification getClassification() {
		try {
			if(classification_mayNeedDBRefresh && _contextDB != null){
				_contextDB.classificationDao.refresh(this.classification);
				classification_mayNeedDBRefresh = false;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
		}
		if(_contextDB==null && this.classification == null){
			log.warn("ClassificationFiche may not be properly refreshed from DB (_id="+_id+")");
		}
		return this.classification;
	}
	public void setClassification(Classification classification) {
		this.classification = classification;
	}			
	public Fiche getFiche() {
		try {
			if(fiche_mayNeedDBRefresh && _contextDB != null){
				_contextDB.ficheDao.refresh(this.fiche);
				fiche_mayNeedDBRefresh = false;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
		}
		if(_contextDB==null && this.fiche == null){
			log.warn("ClassificationFiche may not be properly refreshed from DB (_id="+_id+")");
		}
		return this.fiche;
	}
	public void setFiche(Fiche fiche) {
		this.fiche = fiche;
	}			



	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_CLASSIFICATIONFICHE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NUMORDRE+">");
		sb.append(this.numOrdre);
    	sb.append("</"+XML_ATT_NUMORDRE+">");

		if(this.classification!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_CLASSIFICATION+">");
			sb.append(this.classification.getId());
	    	sb.append("</"+XML_REF_CLASSIFICATION+">");
		}
		if(this.fiche!= null){
			sb.append("\n"+indent+"\t<"+XML_REF_FICHE+">");
			sb.append(this.fiche.getId());
	    	sb.append("</"+XML_REF_FICHE+">");
		}
		// TODO deal with other case

		sb.append("</"+XML_CLASSIFICATIONFICHE+">");
		return sb.toString();
	}
}
