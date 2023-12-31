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

// Start of user code additional import for EntreeBibliographie
// End of user code

/** 
  * Entrée de la Bibliographie 
  */ 
@DatabaseTable(tableName = "entreeBibliographie")
public class EntreeBibliographie extends AbstractWebNodeObject {

	public static Log log = LogFactory.getLog(EntreeBibliographie.class);

	public static final String XML_ENTREEBIBLIOGRAPHIE = "ENTREEBIBLIOGRAPHIE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NUMERODORIS = "numeroDoris";
	public static final String XML_ATT_TITRE = "titre";
	public static final String XML_ATT_AUTEURS = "auteurs";
	public static final String XML_ATT_ANNEE = "annee";
	public static final String XML_ATT_DETAILS = "details";
	public static final String XML_ATT_CLEURLILLUSTRATION = "cleURLIllustration";
	public static final String XML_ATT_TEXTEPOURRECHERCHE = "textePourRecherche";


	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	/**
	 * object created from DB may need to be updated from the DB for being fully navigable
	 */
	

	/** numéro de l'entrée sur le site doris.ffessm.fr */
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int numeroDoris;

	/** Titre du Livre, Revue, etc. */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String titre;

	/** Auteur(s) */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField(dataType = com.j256.ormlite.field.DataType.LONG_STRING)
	protected java.lang.String auteurs;

	/** Année de Parution */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String annee;

	/** Détails comme l'édition, etc. */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String details;

	/** URL de l'illustration éventuelle de l'entrée bibliographique */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String cleURLIllustration;

	/** Permet de Rechercher par Auteurs et Titre */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String textePourRecherche;
	

	// Start of user code EntreeBibliographie additional user properties
	// End of user code
	
	public EntreeBibliographie() {} // needed by ormlite
	public EntreeBibliographie(int numeroDoris, java.lang.String titre, java.lang.String auteurs, java.lang.String annee, java.lang.String details, java.lang.String cleURLIllustration, java.lang.String textePourRecherche) {
		super();
		this.numeroDoris = numeroDoris;
		this.titre = titre;
		this.auteurs = auteurs;
		this.annee = annee;
		this.details = details;
		this.cleURLIllustration = cleURLIllustration;
		this.textePourRecherche = textePourRecherche;
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
	public java.lang.String getTitre() {
		return this.titre;
	}
	public void setTitre(java.lang.String titre) {
		this.titre = titre;
	}
	public java.lang.String getAuteurs() {
		return this.auteurs;
	}
	public void setAuteurs(java.lang.String auteurs) {
		this.auteurs = auteurs;
	}
	public java.lang.String getAnnee() {
		return this.annee;
	}
	public void setAnnee(java.lang.String annee) {
		this.annee = annee;
	}
	public java.lang.String getDetails() {
		return this.details;
	}
	public void setDetails(java.lang.String details) {
		this.details = details;
	}
	public java.lang.String getCleURLIllustration() {
		return this.cleURLIllustration;
	}
	public void setCleURLIllustration(java.lang.String cleURLIllustration) {
		this.cleURLIllustration = cleURLIllustration;
	}
	public java.lang.String getTextePourRecherche() {
		return this.textePourRecherche;
	}
	public void setTextePourRecherche(java.lang.String textePourRecherche) {
		this.textePourRecherche = textePourRecherche;
	}




	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_ENTREEBIBLIOGRAPHIE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NUMERODORIS+">");
		sb.append(this.numeroDoris);
    	sb.append("</"+XML_ATT_NUMERODORIS+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TITRE+">");
		sb.append(StringEscapeUtils.escapeXml(this.titre));
    	sb.append("</"+XML_ATT_TITRE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_AUTEURS+">");
		sb.append(StringEscapeUtils.escapeXml(this.auteurs));
    	sb.append("</"+XML_ATT_AUTEURS+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_ANNEE+">");
		sb.append(StringEscapeUtils.escapeXml(this.annee));
    	sb.append("</"+XML_ATT_ANNEE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DETAILS+">");
		sb.append(StringEscapeUtils.escapeXml(this.details));
    	sb.append("</"+XML_ATT_DETAILS+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_CLEURLILLUSTRATION+">");
		sb.append(StringEscapeUtils.escapeXml(this.cleURLIllustration));
    	sb.append("</"+XML_ATT_CLEURLILLUSTRATION+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TEXTEPOURRECHERCHE+">");
		sb.append(StringEscapeUtils.escapeXml(this.textePourRecherche));
    	sb.append("</"+XML_ATT_TEXTEPOURRECHERCHE+">");

		// TODO deal with other case

		sb.append("</"+XML_ENTREEBIBLIOGRAPHIE+">");
		return sb.toString();
	}
}
