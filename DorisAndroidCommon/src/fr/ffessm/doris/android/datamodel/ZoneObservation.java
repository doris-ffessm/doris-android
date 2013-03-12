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

import fr.ffessm.doris.android.datamodel.associations.*;

/** 
  *  
  */ 
@DatabaseTable(tableName = "zoneObservation")
public class ZoneObservation {

	public static final String XML_ZONEOBSERVATION = "ZONEOBSERVATION";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NOM = "nom";
	public static final String XML_REF_FICHES = "fiches";
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected int _id;
	

	@DatabaseField
	protected java.lang.String nom;
	

	public List<Fiche> lookupFiches(DorisDBHelper contextDB) throws SQLException {
		if (fichesQuery == null) {
			fichesQuery = makeFichesQuery(contextDB);
		}
		fichesQuery.setArgumentHolderValue(0, this);
		return contextDB.ficheDao.query(fichesQuery);
	}
	private PreparedQuery<Fiche> fichesQuery = null;
	/**
	 * Build a query for Fiche objects that match a ZoneObservation
	 */
	private PreparedQuery<Fiche> makeFichesQuery(DorisDBHelper contextDB) throws SQLException {
		// build our inner query for UserPost objects
		QueryBuilder<Fiches_ZonesObservations, Integer> fiches_ZonesObservationsQb = contextDB.fiches_ZonesObservationsDao.queryBuilder();
		// just select the post-id field
		fiches_ZonesObservationsQb.selectColumns(Fiches_ZonesObservations.FICHE_ID_FIELD_NAME);
		SelectArg userSelectArg = new SelectArg();
		// you could also just pass in user1 here
		fiches_ZonesObservationsQb.where().eq(Fiches_ZonesObservations.ZONEOBSERVATION_ID_FIELD_NAME, userSelectArg);

		// build our outer query for Post objects
		QueryBuilder<Fiche, Integer> ficheQb = contextDB.ficheDao.queryBuilder();
		// where the id matches in the fiche-id from the inner query
		ficheQb.where().in("_id", fiches_ZonesObservationsQb);
		return ficheQb.prepare();
	}


				

	// Start of user code ZoneObservation additional user properties
	// End of user code
	
	public ZoneObservation() {} // needed by ormlite
	public ZoneObservation(java.lang.String nom) {
		super();
		this.nom = nom;
	} 

	public int getId() {
		return _id;
	}
	public void setId(int id) {
		this._id = id;
	}

	public java.lang.String getNom() {
		return this.nom;
	}
	public void setNom(java.lang.String nom) {
		this.nom = nom;
	}




	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_ZONEOBSERVATION);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NOM+">");
		sb.append(this.nom);
    	sb.append("</"+XML_ATT_NOM+">");

		try{
			for(Fiche ref : this.lookupFiches(contextDB)){
	    		sb.append("\n"+indent+"\t<"+XML_REF_FICHES+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
				
	    	}
		}
		catch(SQLException e){};	
		// TODO deal with other case

		sb.append("</"+XML_ZONEOBSERVATION+">");
		return sb.toString();
	}
}
