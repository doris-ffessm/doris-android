package fr.ffessm.doris.prefetch.ezpublish.jsondata.classification;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "classIdentifier",
        "data_map"
})
public class Classification {

    @JsonProperty("classIdentifier")
    private String classIdentifier;
    @JsonProperty("data_map")
    private DataMap dataMap;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Classification() {
    }

    /**
     *
     * @param dataMap
     * @param classIdentifier
     */
    public Classification(String classIdentifier, DataMap dataMap) {
        super();
        this.classIdentifier = classIdentifier;
        this.dataMap = dataMap;
    }

    @JsonProperty("classIdentifier")
    public String getClassIdentifier() {
        return classIdentifier;
    }

    @JsonProperty("classIdentifier")
    public void setClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
    }

    public Classification withClassIdentifier(String classIdentifier) {
        this.classIdentifier = classIdentifier;
        return this;
    }

    @JsonProperty("data_map")
    public DataMap getDataMap() {
        return dataMap;
    }

    @JsonProperty("data_map")
    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public Classification withDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
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

    public Classification withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}