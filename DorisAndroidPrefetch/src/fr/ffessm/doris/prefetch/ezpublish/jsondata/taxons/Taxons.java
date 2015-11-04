
package fr.ffessm.doris.prefetch.ezpublish.jsondata.taxons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "childrenNodes",
    "metadata",
    "requestedResponseGroups"
})
public class Taxons {

    @JsonProperty("childrenNodes")
    private List<ChildNodes> childrenNodes = new ArrayList<ChildNodes>();
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("requestedResponseGroups")
    private List<String> requestedResponseGroups = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The childrenNodes
     */
    @JsonProperty("childrenNodes")
    public List<ChildNodes> getChildrenNodes() {
        return childrenNodes;
    }

    /**
     * 
     * @param childrenNodes
     *     The childrenNodes
     */
    @JsonProperty("childrenNodes")
    public void setChildrenNodes(List<ChildNodes> childrenNodes) {
        this.childrenNodes = childrenNodes;
    }

    /**
     * 
     * @return
     *     The metadata
     */
    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The requestedResponseGroups
     */
    @JsonProperty("requestedResponseGroups")
    public List<String> getRequestedResponseGroups() {
        return requestedResponseGroups;
    }

    /**
     * 
     * @param requestedResponseGroups
     *     The requestedResponseGroups
     */
    @JsonProperty("requestedResponseGroups")
    public void setRequestedResponseGroups(List<String> requestedResponseGroups) {
        this.requestedResponseGroups = requestedResponseGroups;
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
