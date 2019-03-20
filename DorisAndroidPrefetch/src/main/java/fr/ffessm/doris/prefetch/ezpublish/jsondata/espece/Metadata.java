
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
    "childrenCount",
    "parentNodeId"
})
public class Metadata {

    @JsonProperty("childrenCount")
    private String childrenCount;
    @JsonProperty("parentNodeId")
    private String parentNodeId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The childrenCount
     */
    @JsonProperty("childrenCount")
    public String getChildrenCount() {
        return childrenCount;
    }

    /**
     * 
     * @param childrenCount
     *     The childrenCount
     */
    @JsonProperty("childrenCount")
    public void setChildrenCount(String childrenCount) {
        this.childrenCount = childrenCount;
    }

    /**
     * 
     * @return
     *     The parentNodeId
     */
    @JsonProperty("parentNodeId")
    public String getParentNodeId() {
        return parentNodeId;
    }

    /**
     * 
     * @param parentNodeId
     *     The parentNodeId
     */
    @JsonProperty("parentNodeId")
    public void setParentNodeId(String parentNodeId) {
        this.parentNodeId = parentNodeId;
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
