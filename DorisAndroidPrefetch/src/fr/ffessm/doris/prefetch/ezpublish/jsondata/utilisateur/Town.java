
package fr.ffessm.doris.prefetch.ezpublish.jsondata.utilisateur;

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
    "type",
    "identifier",
    "value",
    "id",
    "classattribute_id"
})
public class Town {

    @JsonProperty("type")
    private String type;
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("value")
    private String value;
    @JsonProperty("id")
    private int id;
    @JsonProperty("classattribute_id")
    private int classattributeId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Town() {
    }

    /**
     * 
     * @param id
     * @param value
     * @param type
     * @param identifier
     * @param classattributeId
     */
    public Town(String type, String identifier, String value, int id, int classattributeId) {
        this.type = type;
        this.identifier = identifier;
        this.value = value;
        this.id = id;
        this.classattributeId = classattributeId;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public Town withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The identifier
     */
    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 
     * @param identifier
     *     The identifier
     */
    @JsonProperty("identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Town withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * 
     * @return
     *     The value
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    public Town withValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    public Town withId(int id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * @return
     *     The classattributeId
     */
    @JsonProperty("classattribute_id")
    public int getClassattributeId() {
        return classattributeId;
    }

    /**
     * 
     * @param classattributeId
     *     The classattribute_id
     */
    @JsonProperty("classattribute_id")
    public void setClassattributeId(int classattributeId) {
        this.classattributeId = classattributeId;
    }

    public Town withClassattributeId(int classattributeId) {
        this.classattributeId = classattributeId;
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

    public Town withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
