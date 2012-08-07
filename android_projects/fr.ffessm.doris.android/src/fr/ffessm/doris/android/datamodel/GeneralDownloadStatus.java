
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

@DatabaseTable(tableName = "generalDownloadStatus")
public class GeneralDownloadStatus {

	public static final String XML_GENERALDOWNLOADSTATUS = "GENERALDOWNLOADSTATUS";
	public static final String XML_ATT_ID = "id";
	public static final String XML_ATT_NBSUMMARY = "nbSummary";
	public static final String XML_ATT_LASTSUMMARYDOWLOADED = "lastSummaryDowloaded";
	public static final String XML_ATT_LASTTRY = "lastTry";
	public static final String XML_ATT_LASTTIMEFULLSUMMARYCOMPLETED = "lastTimeFullSummaryCompleted";
	
	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected long _id;
	
	@DatabaseField
	protected int nbSummary;
	@DatabaseField
	protected int lastSummaryDowloaded;
	@DatabaseField
	protected java.lang.String lastTry;
	@DatabaseField
	protected java.lang.String lastTimeFullSummaryCompleted;
	
	@ForeignCollectionField(eager = false, foreignFieldName = "generalDownloadStatus")
	protected ForeignCollection<Card> incompleteCards;
	// Start of user code GeneralDownloadStatus additional properties
	// End of user code
	
	public GeneralDownloadStatus() {}// needed by ormlite
	public GeneralDownloadStatus(int nbSummary, int lastSummaryDowloaded, java.lang.String lastTry, java.lang.String lastTimeFullSummaryCompleted) {
		super();
		this.nbSummary = nbSummary;
		this.lastSummaryDowloaded = lastSummaryDowloaded;
		this.lastTry = lastTry;
		this.lastTimeFullSummaryCompleted = lastTimeFullSummaryCompleted;
	} 

	public long getId() {
		return _id;
	}
	public void setId(long id) {
		this._id = id;
	}

	public int getNbSummary() {
		return this.nbSummary;
	}
	public void setNbSummary(int nbSummary) {
		this.nbSummary = nbSummary;
	}
	public int getLastSummaryDowloaded() {
		return this.lastSummaryDowloaded;
	}
	public void setLastSummaryDowloaded(int lastSummaryDowloaded) {
		this.lastSummaryDowloaded = lastSummaryDowloaded;
	}
	public java.lang.String getLastTry() {
		return this.lastTry;
	}
	public void setLastTry(java.lang.String lastTry) {
		this.lastTry = lastTry;
	}
	public java.lang.String getLastTimeFullSummaryCompleted() {
		return this.lastTimeFullSummaryCompleted;
	}
	public void setLastTimeFullSummaryCompleted(java.lang.String lastTimeFullSummaryCompleted) {
		this.lastTimeFullSummaryCompleted = lastTimeFullSummaryCompleted;
	}

	public String toXML(){
		StringBuilder sb = new StringBuilder();
		sb.append("<");
    	sb.append(XML_GENERALDOWNLOADSTATUS);
		sb.append(" ");
    	sb.append(XML_ATT_ID);
    	sb.append("=\"");
		sb.append(this._id);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_NBSUMMARY);
    	sb.append("=\"");
		sb.append(this.nbSummary);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_LASTSUMMARYDOWLOADED);
    	sb.append("=\"");
		sb.append(this.lastSummaryDowloaded);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_LASTTRY);
    	sb.append("=\"");
		sb.append(this.lastTry);
    	sb.append("\" ");
		sb.append(" ");
    	sb.append(XML_ATT_LASTTIMEFULLSUMMARYCOMPLETED);
    	sb.append("=\"");
		sb.append(this.lastTimeFullSummaryCompleted);
    	sb.append("\" ");
    	sb.append(">");

		// TODO deal with other case

		sb.append("</");
    	sb.append(XML_GENERALDOWNLOADSTATUS);
    	sb.append(">");
		return sb.toString();
	}
}