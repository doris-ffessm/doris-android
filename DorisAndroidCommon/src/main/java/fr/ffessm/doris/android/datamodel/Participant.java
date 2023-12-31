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

import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Start of user code additional import for Participant
// End of user code

/** 
  *  
  */ 
@DatabaseTable(tableName = "participant")
public class Participant extends AbstractWebNodeObject {

	public static Log log = LogFactory.getLog(Participant.class);

	public static final String XML_PARTICIPANT = "PARTICIPANT";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NOM = "nom";
	public static final String XML_ATT_NUMEROPARTICIPANT = "numeroParticipant";
	public static final String XML_ATT_CLEURLPHOTOPARTICIPANT = "cleURLPhotoParticipant";
	public static final String XML_ATT_FONCTIONS = "fonctions";
	public static final String XML_ATT_DESCRIPTION = "description";
	public static final String XML_REF_INTERVENANTFICHES = "intervenantFiches";


	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	/**
	 * object created from DB may need to be updated from the DB for being fully navigable
	 */
	

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String nom;

	/** identifiant du participant sur le site doris.ffessm.fr */
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected int numeroParticipant;

	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String cleURLPhotoParticipant;

	/** Liste des ParticipantKind : Rédacteur, Relecteur, Responsable, etc. */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String fonctions;

	/** Texte de Description du Participant */ 
	/** TEST GM : SQLite = SQLite **/
	@DatabaseField
	protected java.lang.String description;


	@ForeignCollectionField(eager = false, foreignFieldName = "participant")
	protected ForeignCollection<IntervenantFiche> intervenantFiches;

	// Start of user code Participant additional user properties
	
	public String getPhotoNom() {
		return cleURLPhotoParticipant.replace("gestionenligne/photos_vig/", "").trim();
	} 
	
	
	
	
	// End of user code
	
	public Participant() {} // needed by ormlite
	public Participant(java.lang.String nom, int numeroParticipant, java.lang.String cleURLPhotoParticipant, java.lang.String fonctions, java.lang.String description) {
		super();
		this.nom = nom;
		this.numeroParticipant = numeroParticipant;
		this.cleURLPhotoParticipant = cleURLPhotoParticipant;
		this.fonctions = fonctions;
		this.description = description;
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
	public int getNumeroParticipant() {
		return this.numeroParticipant;
	}
	public void setNumeroParticipant(int numeroParticipant) {
		this.numeroParticipant = numeroParticipant;
	}
	public java.lang.String getCleURLPhotoParticipant() {
		return this.cleURLPhotoParticipant;
	}
	public void setCleURLPhotoParticipant(java.lang.String cleURLPhotoParticipant) {
		this.cleURLPhotoParticipant = cleURLPhotoParticipant;
	}
	public java.lang.String getFonctions() {
		return this.fonctions;
	}
	public void setFonctions(java.lang.String fonctions) {
		this.fonctions = fonctions;
	}
	public java.lang.String getDescription() {
		return this.description;
	}
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public Collection<IntervenantFiche> getIntervenantFiches() {
		return this.intervenantFiches;
	}					



	public String toXML(String indent, DorisDBHelper contextDB){
		StringBuilder sb = new StringBuilder();
		sb.append(indent+"<");
    	sb.append(XML_PARTICIPANT);
		sb.append(" "+XML_ATT_ID+"=\"");
		sb.append(this._id);
    	sb.append("\" ");
    	sb.append(">");

		sb.append("\n"+indent+"\t<"+XML_ATT_NOM+">");
		sb.append(StringEscapeUtils.escapeXml(this.nom));
    	sb.append("</"+XML_ATT_NOM+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_NUMEROPARTICIPANT+">");
		sb.append(this.numeroParticipant);
    	sb.append("</"+XML_ATT_NUMEROPARTICIPANT+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_CLEURLPHOTOPARTICIPANT+">");
		sb.append(StringEscapeUtils.escapeXml(this.cleURLPhotoParticipant));
    	sb.append("</"+XML_ATT_CLEURLPHOTOPARTICIPANT+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_FONCTIONS+">");
		sb.append(StringEscapeUtils.escapeXml(this.fonctions));
    	sb.append("</"+XML_ATT_FONCTIONS+">");
		sb.append("\n"+indent+"\t<"+XML_ATT_DESCRIPTION+">");
		sb.append(StringEscapeUtils.escapeXml(this.description));
    	sb.append("</"+XML_ATT_DESCRIPTION+">");

		if(this.intervenantFiches != null){
			for(IntervenantFiche ref : this.intervenantFiches){
					
	    		sb.append("\n"+indent+"\t<"+XML_REF_INTERVENANTFICHES+" id=\"");
	    		sb.append(ref._id);
	        	sb.append("\"/>");
	    	}		
		}
		// TODO deal with other case

		sb.append("</"+XML_PARTICIPANT+">");
		return sb.toString();
	}
}
