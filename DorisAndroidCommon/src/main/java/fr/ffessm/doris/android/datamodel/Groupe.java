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
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Start of user code additional import for Groupe


// End of user code

/** 
  * Groupe correspond aux entités permettant de naviguer par image sur le site web : 
http://doris.ffessm.fr/groupes.asp?numero_fichier=10. Le niveau 0 est la racine du site. 
  */ 
@DatabaseTable(tableName = "groupe")
public class Groupe extends AbstractWebNodeObject {

	public static Log log = LogFactory.getLog(Groupe.class);

	public static final String XML_GROUPE = "GROUPE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NUMEROGROUPE = "numeroGroupe";
	public static final String XML_ATT_NUMEROSOUSGROUPE = "numeroSousGroupe";
	public static final String XML_ATT_NOMGROUPE = "nomGroupe";
	public static final String XML_ATT_DESCRIPTIONGROUPE = "descriptionGroupe";
	public static final String XML_ATT_CLEURLIMAGE = "cleURLImage";
	public static final String XML_ATT_NOMIMAGE = "nomImage";
	public static final String XML_ATT_DESCRIPTIONDETAILLEEGROUPE = "descriptionDetailleeGroupe";
	public static final String XML_REF_GROUPESFILS = "groupesFils";
	public static final String XML_REF_GROUPEPERE = "groupePere";


	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	/**
	 * object created from DB may need to be updated from the DB for being fully navigable
	 */
	public boolean groupePere_mayNeedDBRefresh = true;
	

	/** XXX de groupe_numero=XXX dans l'url permettant d'afficher le groupe.
Certains groupes (de haut niveau) n'ont pas de numéro */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int numeroGroupe;

	/** XXX de sousgroupe_numero=XXX dans l'url permettant avec le numero de groupe d'afficher le groupe.
Seuls certains groupes de plus bas niveau ont le numéro de sous-groupe */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int numeroSousGroupe;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String nomGroupe;

	/** Souvent une petite liste d'exemples illustratifs */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String descriptionGroupe;

	/** url de base pour les images de ce groupe */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String cleURLImage;

	/** nom de l'image de ce groupe */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String nomImage;

	/** couleur associée au groupe Doris */
	@DatabaseField
	protected int couleurGroupe;

	/** En haut des pages de groupe, il y a un petit texte expliquant les grandes caractéristques du groupe.
C'est ce texte. */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String descriptionDetailleeGroupe;
	

	/** Permet de créer ou lire l'arborescence des groupes par récurrence */ 
	@ForeignCollectionField(eager = false, foreignFieldName = "groupePere")
	protected ForeignCollection<Groupe> groupesFils;

	@DatabaseField(foreign = true) //, columnName = USER_ID_FIELD_NAME)
	protected Groupe groupePere;

	// Start of user code Groupe additional user properties
	
	public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe, java.lang.String descriptionGroupe, java.lang.String cleURLImage, java.lang.String nomImage) {
		super();
		this.numeroGroupe = numeroGroupe;
		this.numeroSousGroupe = numeroSousGroupe;
		this.nomGroupe = nomGroupe;
		this.descriptionGroupe = descriptionGroupe;
		this.cleURLImage = cleURLImage;
		this.nomImage = nomImage;
	}

    public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe, java.lang.String descriptionGroupe, java.lang.String cleURLImage, java.lang.String nomImage, java.lang.String descriptionDetailleeGroupe, Groupe groupePere) {
        super();
        this.numeroGroupe = numeroGroupe;
        this.numeroSousGroupe = numeroSousGroupe;
        this.nomGroupe = nomGroupe;
        this.descriptionGroupe = descriptionGroupe;
        this.cleURLImage = cleURLImage;
        this.nomImage = nomImage;
        this.descriptionDetailleeGroupe = descriptionDetailleeGroupe;
        this.groupePere = groupePere;
    }

    public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe,
				  java.lang.String descriptionGroupe,
				  java.lang.String cleURLImage,
				  java.lang.String nomImage,
				  java.lang.String descriptionDetailleeGroupe,
				  int couleurGroupe,
				  Groupe groupePere) {
		super();
		this.numeroGroupe = numeroGroupe;
		this.numeroSousGroupe = numeroSousGroupe;
		this.nomGroupe = nomGroupe;
		this.descriptionGroupe = descriptionGroupe;
		this.cleURLImage = cleURLImage;
		this.nomImage = nomImage;
		this.descriptionDetailleeGroupe = descriptionDetailleeGroupe;
		this.couleurGroupe = couleurGroupe;
		this.groupePere = groupePere;
	}

	public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe, Groupe groupePere) {
		super();
		this.numeroGroupe = numeroGroupe;
		this.numeroSousGroupe = numeroSousGroupe;
		this.nomGroupe = nomGroupe;
		this.groupePere = groupePere;
	}

	public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe,
				  int couleurGroupe, Groupe groupePere) {
		super();
		this.numeroGroupe = numeroGroupe;
		this.numeroSousGroupe = numeroSousGroupe;
		this.nomGroupe = nomGroupe;
		this.couleurGroupe = couleurGroupe;
		this.groupePere = groupePere;
	}

    public String getImageNameOnDisk(){
		//log.warn("getImageNameOnDisk() - groupe : "+_id+" - "+numeroGroupe+" - "+numeroSousGroupe);
		//log.warn("getImageNameOnDisk() - cleURLImage : "+cleURLImage);
		return cleURLImage.replace("gestionenligne/", "")
			.replace("images/", "images_groupe/").replace("/","_");
	}

	// End of user code
	
	public Groupe() {} // needed by ormlite
	public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe, java.lang.String descriptionGroupe, java.lang.String cleURLImage, java.lang.String nomImage, java.lang.String descriptionDetailleeGroupe) {
		super();
		this.numeroGroupe = numeroGroupe;
		this.numeroSousGroupe = numeroSousGroupe;
		this.nomGroupe = nomGroupe;
		this.descriptionGroupe = descriptionGroupe;
		this.cleURLImage = cleURLImage;
		this.nomImage = nomImage;
		this.descriptionDetailleeGroupe = descriptionDetailleeGroupe;
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

	public int getNumeroGroupe() {
		return this.numeroGroupe;
	}
	public void setNumeroGroupe(int numeroGroupe) {
		this.numeroGroupe = numeroGroupe;
	}
	public int getNumeroSousGroupe() {
		return this.numeroSousGroupe;
	}
	public void setNumeroSousGroupe(int numeroSousGroupe) {
		this.numeroSousGroupe = numeroSousGroupe;
	}
	public java.lang.String getNomGroupe() {
		return this.nomGroupe;
	}
	public void setNomGroupe(java.lang.String nomGroupe) {
		this.nomGroupe = nomGroupe;
	}
	public java.lang.String getDescriptionGroupe() {
		return this.descriptionGroupe;
	}
	public void setDescriptionGroupe(java.lang.String descriptionGroupe) {
		this.descriptionGroupe = descriptionGroupe;
	}

	public int getCouleurGroupe() {
		return couleurGroupe;
	}

	public void setCouleurGroupe(int couleurGroupe) {
		this.couleurGroupe = couleurGroupe;
	}

	public java.lang.String getCleURLImage() {
		return this.cleURLImage;
	}
	public void setCleURLImage(java.lang.String cleURLImage) {
		this.cleURLImage = cleURLImage;
	}
	public java.lang.String getNomImage() {
		return this.nomImage;
	}
	public void setNomImage(java.lang.String nomImage) {
		this.nomImage = nomImage;
	}
	public java.lang.String getDescriptionDetailleeGroupe() {
		return this.descriptionDetailleeGroupe;
	}
	public void setDescriptionDetailleeGroupe(java.lang.String descriptionDetailleeGroupe) {
		this.descriptionDetailleeGroupe = descriptionDetailleeGroupe;
	}

	/** Permet de créer ou lire l'arborescence des groupes par récurrence */
	public Collection<Groupe> getGroupesFils() {
		return this.groupesFils;
	}					
	public Groupe getGroupePere() {
		try {
			if(groupePere_mayNeedDBRefresh && _contextDB != null){
				_contextDB.groupeDao.refresh(this.groupePere);
				groupePere_mayNeedDBRefresh = false;
			}
		} catch (SQLException e) {
			log.error("erreur dans getGroupePere()");
			log.error(e.getMessage(),e);
		}
		if(_contextDB==null && this.groupePere == null){
			log.warn("Groupe may not be properly refreshed from DB (_id="+_id+")");
		}
		
		log.debug("getGroupePere() - groupePere.id="+_id+")");
		log.debug("getGroupePere() - groupePere.numeroGroupe="+numeroGroupe+")");
		log.debug("getGroupePere() - groupePere.nomGroupe="+nomGroupe+")");
		
		return this.groupePere;
	}
	public void setGroupePere(Groupe groupePere) {
		this.groupePere = groupePere;
	}			



	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_GROUPE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_NOMGROUPE);
    	sb.append("=\"");
		sb.append(StringEscapeUtils.escapeXml(this.nomGroupe));
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_DESCRIPTIONGROUPE);
    	sb.append("=\"");
		sb.append(StringEscapeUtils.escapeXml(this.descriptionGroupe));
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_DESCRIPTIONDETAILLEEGROUPE);
    	sb.append("=\"");
		sb.append(StringEscapeUtils.escapeXml(this.descriptionDetailleeGroupe));
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NUMEROGROUPE+">");
		sb.append(this.numeroGroupe);
    	sb.append("</"+XML_ATT_NUMEROGROUPE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_NUMEROSOUSGROUPE+">");
		sb.append(this.numeroSousGroupe);
    	sb.append("</"+XML_ATT_NUMEROSOUSGROUPE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_CLEURLIMAGE+">");
		sb.append(StringEscapeUtils.escapeXml(this.cleURLImage));
    	sb.append("</"+XML_ATT_CLEURLIMAGE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_NOMIMAGE+">");
		sb.append(StringEscapeUtils.escapeXml(this.nomImage));
    	sb.append("</"+XML_ATT_NOMIMAGE+">");

		sb.append("\n"+indent+"\t<"+XML_REF_GROUPESFILS+">");
		if(this.groupesFils != null){
			for(Groupe ref : this.groupesFils){
				sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
	    	}
		}
		sb.append("</"+XML_REF_GROUPESFILS+">");		
		// TODO deal with other case

		sb.append("</"+XML_GROUPE+">");
		return sb.toString();
	}
}
