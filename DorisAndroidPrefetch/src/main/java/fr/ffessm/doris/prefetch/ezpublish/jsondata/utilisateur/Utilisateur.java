
package fr.ffessm.doris.prefetch.ezpublish.jsondata.utilisateur;

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
    "fields",
    "requestedResponseGroups"
})
public class Utilisateur {

    @JsonProperty("fields")
    private Fields fields;
    @JsonProperty("requestedResponseGroups")
    private List<String> requestedResponseGroups = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Utilisateur() {
    }

    /**
     * 
     * @param requestedResponseGroups
     * @param fields
     */
    public Utilisateur(Fields fields, List<String> requestedResponseGroups) {
        this.fields = fields;
        this.requestedResponseGroups = requestedResponseGroups;
    }

    /**
     * 
     * @return
     *     The fields
     */
    @JsonProperty("fields")
    public Fields getFields() {
        return fields;
    }

    /**
     * 
     * @param fields
     *     The fields
     */
    @JsonProperty("fields")
    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public Utilisateur withFields(Fields fields) {
        this.fields = fields;
        return this;
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

    public Utilisateur withRequestedResponseGroups(List<String> requestedResponseGroups) {
        this.requestedResponseGroups = requestedResponseGroups;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Utilisateur withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
