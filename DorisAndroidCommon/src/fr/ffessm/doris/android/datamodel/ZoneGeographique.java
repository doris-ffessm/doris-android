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

// Start of user code additional import for ZoneGeographique
// End of user code

/** 
  *  
  */ 
@DatabaseTable(tableName = "zoneGeographique")
public class ZoneGeographique {

	public static Log log = LogFactory.getLog(ZoneGeographique.class);

	public static final String XML_ZONEGEOGRAPHIQUE = "ZONEGEOGRAPHIQUE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NOM = "nom";
	public static final String XML_ATT_DESCRIPTION = "description";
	public static final String XML_REF_FICHES = "fiches";
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
	public boolean _mayNeedDBRefresh = true;
	

	@DatabaseField
	protected java.lang.String nom;

	@DatabaseField
	protected java.lang.String description;
	

	// work in progress, find association 
	// Association many to many Fiches_ZonesGeographiques
	@ForeignCollectionField(eager = false, foreignFieldName = "zoneGeographique")	
	protected ForeignCollection<Fiches_ZonesGeographiques> fiches_ZonesGeographiques;

	/**  
	  * Attention, returned list is readonly
      */
	public List<Fiche> getFiches(){
		List<Fiche> result = new ArrayList<Fiche>();
		
		for (Fiches_ZonesGeographiques aFiches_ZonesGeographiques : fiches_ZonesGeographiques) {
			if(_contextDB != null) aFiches_ZonesGeographiques.setContextDB(_contextDB);
			result.add(aFiches_ZonesGeographiques.getFiche());
		}
		return result;
	}
	public void addFiche(Fiche fiche){
		try {
			_contextDB.fiches_ZonesGeographiquesDao.create(new Fiches_ZonesGeographiques( this, fiche));
		} catch (SQLException e) {
			log.error("Pb while adding association fiches_ZonesGeographiques",e);
		}
	}
	// end work in progress 	

				

	// Start of user code ZoneGeographique additional user properties
	// End of user code
	
	public ZoneGeographique() {} // needed by ormlite
	public ZoneGeographique(java.lang.String nom, java.lang.String description) {
		super();
		this.nom = nom;
		this.description = description;
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

	public java.lang.String getNom() {
		return this.nom;
	}
	public void setNom(java.lang.String nom) {
		this.nom = nom;
	}
	public java.lang.String getDescription() {
		return this.description;
	}
	public void setDescription(java.lang.String description) {
		this.description = description;
	}




	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_ZONEGEOGRAPHIQUE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NOM+">");
		sb.append(StringEscapeUtils.escapeXml(this.nom));
    	sb.append("</"+XML_ATT_NOM+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DESCRIPTION+">");
		sb.append(StringEscapeUtils.escapeXml(this.description));
    	sb.append("</"+XML_ATT_DESCRIPTION+">");

		
		for(Fiche ref : this.getFiches()){
    		sb.append("\n"+indent+"\t<"+XML_REF_FICHES+" id=\"");
    		sb.append(ref._id);
        	sb.append("\"/>");
			
    	}
			
		// TODO deal with other case

		sb.append("</"+XML_ZONEGEOGRAPHIQUE+">");
		return sb.toString();
	}
}
