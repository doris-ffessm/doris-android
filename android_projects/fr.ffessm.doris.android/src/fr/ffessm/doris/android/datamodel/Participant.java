
/*******************************************************************************
 * Copyright (c) 2012 Vojtisek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Didier Vojtisek - initial API and implementation
 *******************************************************************************/
package fr.ffessm.doris.android.datamodel;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "participant")
public class Participant {

	public static final String XML_PARTICIPANT = "PARTICIPANT";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NAME = "name";
	public static final String XML_ATT_WEBSITE = "webSite";
	public static final String XML_ATT_IDDORIS = "idDoris";
	
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected long _id;
	
	@DatabaseField
	protected java.lang.String name;
	@DatabaseField
	protected java.lang.String webSite;
	@DatabaseField
	protected int idDoris;
	
	@ForeignCollectionField(eager = false, foreignFieldName = "writer")
	protected ForeignCollection<Card> writerOf;
	@ForeignCollectionField(eager = false, foreignFieldName = "verifier")
	protected ForeignCollection<Card> verifierOf;
	@ForeignCollectionField(eager = false, foreignFieldName = "author")
	protected ForeignCollection<CardPicture> authorOfPicture;
	// TODO single reference 
	// protected ParticipantPhoto photo;
	// Start of user code Participant additional properties
	// End of user code
	
	public Participant() {}// needed by ormlite
	public Participant(java.lang.String name, java.lang.String webSite, int idDoris) {
		super();
		this.name = name;
		this.webSite = webSite;
		this.idDoris = idDoris;
	} 

	public long getId() {
		return _id;
	}
	public void setId(long id) {
		this._id = id;
	}

	public java.lang.String getName() {
		return this.name;
	}
	public void setName(java.lang.String name) {
		this.name = name;
	}
	public java.lang.String getWebSite() {
		return this.webSite;
	}
	public void setWebSite(java.lang.String webSite) {
		this.webSite = webSite;
	}
	public int getIdDoris() {
		return this.idDoris;
	}
	public void setIdDoris(int idDoris) {
		this.idDoris = idDoris;
	}

	public String toXML(){
		StringBuilder sb = new StringBuilder();
		sb.append("<");
    	sb.append(XML_PARTICIPANT);
		sb.append(" ");
    	sb.append(XML_ATT_ID);
    	sb.append("=\"");
		sb.append(this._id);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_NAME);
    	sb.append("=\"");
		sb.append(this.name);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_WEBSITE);
    	sb.append("=\"");
		sb.append(this.webSite);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_IDDORIS);
    	sb.append("=\"");
		sb.append(this.idDoris);
    	sb.append("\" ");
    	sb.append(">");

		// TODO deal with other case

		sb.append("</");
    	sb.append(XML_PARTICIPANT);
    	sb.append(">");
		return sb.toString();
	}
}
