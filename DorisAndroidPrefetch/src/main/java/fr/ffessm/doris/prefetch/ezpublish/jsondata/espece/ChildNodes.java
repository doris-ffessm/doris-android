
package fr.ffessm.doris.prefetch.ezpublish.jsondata.espece;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "objectName",
    "classIdentifier",
    "datePublished",
    "dateModified",
    "objectRemoteId",
    "objectId",
    "nodeId",
    "nodeRemoteId",
    "fullUrl",
    "link"
})
public class ChildNodes {

    @JsonProperty("objectName")
    private String objectName;
    @JsonProperty("classIdentifier")
    private String classIdentifier;
    @JsonProperty("datePublished")
    private Integer datePublished;
    @JsonProperty("dateModified")
    private Integer dateModified;
    @JsonProperty("objectRemoteId")
    private String objectRemoteId;
    @JsonProperty("objectId")
    private Integer objectId;
    @JsonProperty("nodeId")
    private Integer nodeId;
    @JsonProperty("nodeRemoteId")
    private String nodeRemoteId;
    @JsonProperty("fullUrl")
    private String fullUrl;
    @JsonProperty("link")
    private String link;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The objectName
     */
    @JsonProperty("objectName")
    public String getObjectName() {
        return objectName;
    }

    /**
     * 
     * @param objectName
     *     The objectName
     */
    @JsonProperty("objectName")
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * 
     * @return
     *     The classIdentifier
     */
    @JsonProperty("classIdentifier")
    public String getClassIdentifier() {
        return classIdentifier;
    }

    /**
     * 
     * @param classIdentifier
     *     The classIdentifier
     */
    @JsonProperty("classIdentifier")
    public void setClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    /**
     * 
     * @return
     *     The datePublished
     */
    @JsonProperty("datePublished")
    public Integer getDatePublished() {
        return datePublished;
    }

    /**
     * 
     * @param datePublished
     *     The datePublished
     */
    @JsonProperty("datePublished")
    public void setDatePublished(Integer datePublished) {
        this.datePublished = datePublished;
    }

    /**
     * 
     * @return
     *     The dateModified
     */
    @JsonProperty("dateModified")
    public Integer getDateModified() {
        return dateModified;
    }

    /**
     * 
     * @param dateModified
     *     The dateModified
     */
    @JsonProperty("dateModified")
    public void setDateModified(Integer dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * 
     * @return
     *     The objectRemoteId
     */
    @JsonProperty("objectRemoteId")
    public String getObjectRemoteId() {
        return objectRemoteId;
    }

    /**
     * 
     * @param objectRemoteId
     *     The objectRemoteId
     */
    @JsonProperty("objectRemoteId")
    public void setObjectRemoteId(String objectRemoteId) {
        this.objectRemoteId = objectRemoteId;
    }

    /**
     * 
     * @return
     *     The objectId
     */
    @JsonProperty("objectId")
    public Integer getObjectId() {
        return objectId;
    }

    /**
     * 
     * @param objectId
     *     The objectId
     */
    @JsonProperty("objectId")
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    /**
     * 
     * @return
     *     The nodeId
     */
    @JsonProperty("nodeId")
    public Integer getNodeId() {
        return nodeId;
    }

    /**
     * 
     * @param nodeId
     *     The nodeId
     */
    @JsonProperty("nodeId")
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * 
     * @return
     *     The nodeRemoteId
     */
    @JsonProperty("nodeRemoteId")
    public String getNodeRemoteId() {
        return nodeRemoteId;
    }

    /**
     * 
     * @param nodeRemoteId
     *     The nodeRemoteId
     */
    @JsonProperty("nodeRemoteId")
    public void setNodeRemoteId(String nodeRemoteId) {
        this.nodeRemoteId = nodeRemoteId;
    }

    /**
     * 
     * @return
     *     The fullUrl
     */
    @JsonProperty("fullUrl")
    public String getFullUrl() {
        return fullUrl;
    }

    /**
     * 
     * @param fullUrl
     *     The fullUrl
     */
    @JsonProperty("fullUrl")
    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    /**
     * 
     * @return
     *     The link
     */
    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    /**
     * 
     * @param link
     *     The link
     */
    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
