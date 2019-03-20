
package fr.ffessm.doris.prefetch.ezpublish.jsondata.glossaire;

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
    "title",
    "definition",
    "statut",
    "illustrations",
    "reference"
})
public class Fields {

    @JsonProperty("title")
    private Title title;
    @JsonProperty("definition")
    private Definition definition;
    @JsonProperty("statut")
    private Statut statut;
    @JsonProperty("illustrations")
    private Illustrations illustrations;
    @JsonProperty("reference")
    private Reference reference;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public Title getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(Title title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The definition
     */
    @JsonProperty("definition")
    public Definition getDefinition() {
        return definition;
    }

    /**
     * 
     * @param definition
     *     The definition
     */
    @JsonProperty("definition")
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    /**
     * 
     * @return
     *     The statut
     */
    @JsonProperty("statut")
    public Statut getStatut() {
        return statut;
    }

    /**
     * 
     * @param statut
     *     The statut
     */
    @JsonProperty("statut")
    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    /**
     * 
     * @return
     *     The illustrations
     */
    @JsonProperty("illustrations")
    public Illustrations getIllustrations() {
        return illustrations;
    }

    /**
     * 
     * @param illustrations
     *     The illustrations
     */
    @JsonProperty("illustrations")
    public void setIllustrations(Illustrations illustrations) {
        this.illustrations = illustrations;
    }

    /**
     * 
     * @return
     *     The reference
     */
    @JsonProperty("reference")
    public Reference getReference() {
        return reference;
    }

    /**
     * 
     * @param reference
     *     The reference
     */
    @JsonProperty("reference")
    public void setReference(Reference reference) {
        this.reference = reference;
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
