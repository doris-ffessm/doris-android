
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

@DatabaseTable(tableName = "picture")
public class Picture {

	public static final String XML_PICTURE = "PICTURE";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_PICTUREFILENAME = "pictureFileName";
	public static final String XML_ATT_TITLE = "title";
	public static final String XML_ATT_DESCRIPTION = "description";
	
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected long _id;
	
	@DatabaseField
	protected java.lang.String pictureFileName;
	@DatabaseField
	protected java.lang.String title;
	@DatabaseField
	protected java.lang.String description;
	
	@DatabaseField(foreign = true) //, columnName = USER_ID_FIELD_NAME)
	protected Participant author;
	@DatabaseField(foreign = true) //, columnName = USER_ID_FIELD_NAME)
	protected Card ofCard;
	// Start of user code Picture additional properties
	// End of user code
	
	public Picture() {}// needed by ormlite
	public Picture(java.lang.String pictureFileName, java.lang.String title, java.lang.String description) {
		super();
		this.pictureFileName = pictureFileName;
		this.title = title;
		this.description = description;
	} 

	public long getId() {
		return _id;
	}
	public void setId(long id) {
		this._id = id;
	}

	public java.lang.String getPictureFileName() {
		return this.pictureFileName;
	}
	public void setPictureFileName(java.lang.String pictureFileName) {
		this.pictureFileName = pictureFileName;
	}
	public java.lang.String getTitle() {
		return this.title;
	}
	public void setTitle(java.lang.String title) {
		this.title = title;
	}
	public java.lang.String getDescription() {
		return this.description;
	}
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public String toXML(){
		StringBuilder sb = new StringBuilder();
		sb.append("<");
    	sb.append(XML_PICTURE);
		sb.append(" ");
    	sb.append(XML_ATT_ID);
    	sb.append("=\"");
		sb.append(this._id);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_PICTUREFILENAME);
    	sb.append("=\"");
		sb.append(this.pictureFileName);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_TITLE);
    	sb.append("=\"");
		sb.append(this.title);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_DESCRIPTION);
    	sb.append("=\"");
		sb.append(this.description);
    	sb.append("\" ");
    	sb.append(">");

		// TODO deal with other case

		sb.append("</");
    	sb.append(XML_PICTURE);
    	sb.append(">");
		return sb.toString();
	}
}
