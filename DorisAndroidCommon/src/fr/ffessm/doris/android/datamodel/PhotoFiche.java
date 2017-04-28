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

// Start of user code additional import for PhotoFiche
// End of user code

/** 
  *  
  */ 
@DatabaseTable(tableName = "photoFiche")
public class PhotoFiche {

	public static Log log = LogFactory.getLog(PhotoFiche.class);

	public static final String XML_PHOTOFICHE = "PHOTOFICHE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_CLEURL = "cleURL";
	public static final String XML_ATT_IMAGEVIGNETTE = "imageVignette";
	public static final String XML_ATT_IMAGEMOYENNE = "imageMoyenne";
	public static final String XML_ATT_IMAGEGRANDE = "imageGrande";
	public static final String XML_ATT_TITRE = "titre";
	public static final String XML_ATT_DESCRIPTION = "description";
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
	public boolean fiche_mayNeedDBRefresh = true;
	

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String cleURL;

	/** TEST GM : File = SQLite **/
	protected java.io.File imageVignette;

	/** TEST GM : File = SQLite **/
	protected java.io.File imageMoyenne;

	/** TEST GM : File = SQLite **/
	protected java.io.File imageGrande;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String titre;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField(dataType = com.j256.ormlite.field.DataType.LONG_STRING)
	protected java.lang.String description;
	

	@DatabaseField(foreign = true) //, columnName = USER_ID_FIELD_NAME)
	protected Fiche fiche;

	// Start of user code PhotoFiche additional user properties	
	
	// Pas enregistré sur PhotoFiche mais permet de faire passer l'info "proprement" à Fiche
	public boolean estPhotoPrincipale = false;
	public PhotoFiche(java.lang.String cleURL, java.lang.String titre, java.lang.String description,
			boolean estPhotoPrincipale) {
		super();
		this.cleURL = cleURL;
		this.titre = titre;
		this.description = description;
		this.estPhotoPrincipale = estPhotoPrincipale;
	}

	// Retourne seulement le nom du fichier, c'est à dire le dernier mot
	public java.lang.String getCleURLNomFichier() {
		return this.cleURL.substring(this.cleURL.lastIndexOf('/') + 1);
	}

	// End of user code
	
	public PhotoFiche() {} // needed by ormlite
	public PhotoFiche(java.lang.String cleURL, java.lang.String titre, java.lang.String description) {
		super();
		this.cleURL = cleURL;
		this.titre = titre;
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

	public java.lang.String getCleURL() {
		return this.cleURL;
	}
	public void setCleURL(java.lang.String cleURL) {
		this.cleURL = cleURL;
	}
	public java.io.File getImageVignette() {
		// Start of user code placeholder for derived attribute imageVignette
		// End of user code			
		return this.imageVignette;
	}
	public void setImageVignette(java.io.File imageVignette) {
		this.imageVignette = imageVignette;
	}
	public java.io.File getImageMoyenne() {
		// Start of user code placeholder for derived attribute imageMoyenne
		// End of user code			
		return this.imageMoyenne;
	}
	public void setImageMoyenne(java.io.File imageMoyenne) {
		this.imageMoyenne = imageMoyenne;
	}
	public java.io.File getImageGrande() {
		// Start of user code placeholder for derived attribute imageGrande
		// End of user code			
		return this.imageGrande;
	}
	public void setImageGrande(java.io.File imageGrande) {
		this.imageGrande = imageGrande;
	}
	public java.lang.String getTitre() {
		return this.titre;
	}
	public void setTitre(java.lang.String titre) {
		this.titre = titre;
	}
	public java.lang.String getDescription() {
		return this.description;
	}
	public void setDescription(java.lang.String description) {
		this.description = description;
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
			log.warn("PhotoFiche may not be properly refreshed from DB (_id="+_id+")");
		}
		return this.fiche;
	}
	public void setFiche(Fiche fiche) {
		this.fiche = fiche;
	}			



	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_PHOTOFICHE);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_CLEURL+">");
		sb.append(StringEscapeUtils.escapeXml(this.cleURL));
    	sb.append("</"+XML_ATT_CLEURL+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_TITRE+">");
		sb.append(StringEscapeUtils.escapeXml(this.titre));
    	sb.append("</"+XML_ATT_TITRE+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DESCRIPTION+">");
		sb.append(StringEscapeUtils.escapeXml(this.description));
    	sb.append("</"+XML_ATT_DESCRIPTION+">");

		// TODO deal with other case

		sb.append("</"+XML_PHOTOFICHE+">");
		return sb.toString();
	}
}
