package fr.ffessm.doris.prefetch.ezpublish.jsondata.specie;

public class Specie_Metadata {
	
	/* 
	 "objectName":"Nessitheras rhombopteryx",
	 "classIdentifier":"specie",
	 "datePublished":1433056014,
	 "dateModified":1433536023,
	 "objectRemoteId":"73a8cc90a052a041f70eff30d32d6f33",
	 "objectId":135955,
	 "nodeId":131722,
	 "nodeRemoteId":"5931fd6dfab28b1a7fffe2dc5ce95f9d",
	 "fullUrl":"http://doris.donatello.io/Especes/Nessitheras-rhombopteryx-Grrrand-serpent-de-mer-et-d-eau-douce-11232",
	 "link":"http://doris.donatello.io/api/ezp/v1/content/node/131722"}
	 */
	private String objectName;
	private String classIdentifier;
	private long datePublished;
	private long dateModified;
	private String objectRemoteId;
	private long objectId;
	private long nodeId;
	private String nodeRemoteId;
	private String fullUrl;
	private String link;
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getClassIdentifier() {
		return classIdentifier;
	}
	public void setClassIdentifier(String classIdentifier) {
		this.classIdentifier = classIdentifier;
	}
	public long getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(long datePublished) {
		this.datePublished = datePublished;
	}
	public long getDateModified() {
		return dateModified;
	}
	public void setDateModified(long dateModified) {
		this.dateModified = dateModified;
	}
	public String getObjectRemoteId() {
		return objectRemoteId;
	}
	public void setObjectRemoteId(String objectRemoteId) {
		this.objectRemoteId = objectRemoteId;
	}
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeRemoteId() {
		return nodeRemoteId;
	}
	public void setNodeRemoteId(String nodeRemoteId) {
		this.nodeRemoteId = nodeRemoteId;
	}
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
