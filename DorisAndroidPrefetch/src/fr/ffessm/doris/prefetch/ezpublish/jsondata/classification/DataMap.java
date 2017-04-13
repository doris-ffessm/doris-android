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
        "name_latin",
        "name_french",
        "description"
})
public class DataMap {

    @JsonProperty("name_latin")
    private String nameLatin;
    @JsonProperty("name_french")
    private String nameFrench;
    @JsonProperty("description")
    private String description;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public DataMap() {
    }

    /**
     *
     * @param description
     * @param nameFrench
     * @param nameLatin
     */
    public DataMap(String nameLatin, String nameFrench, String description) {
        super();
        this.nameLatin = nameLatin;
        this.nameFrench = nameFrench;
        this.description = description;
    }

    @JsonProperty("name_latin")
    public String getNameLatin() {
        return nameLatin;
    }

    @JsonProperty("name_latin")
    public void setNameLatin(String nameLatin) {
        this.nameLatin = nameLatin;
    }

    public DataMap withNameLatin(String nameLatin) {
        this.nameLatin = nameLatin;
        return this;
    }

    @JsonProperty("name_french")
    public String getNameFrench() {
        return nameFrench;
    }

    @JsonProperty("name_french")
    public void setNameFrench(String nameFrench) {
        this.nameFrench = nameFrench;
    }

    public DataMap withNameFrench(String nameFrench) {
        this.nameFrench = nameFrench;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public DataMap withDescription(String description) {
        this.description = description;
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

    public DataMap withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}