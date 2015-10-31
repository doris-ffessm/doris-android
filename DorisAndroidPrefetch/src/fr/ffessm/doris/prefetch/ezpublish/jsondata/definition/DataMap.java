package fr.ffessm.doris.prefetch.ezpublish.jsondata.definition;

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

public class DataMap {

	@JsonProperty("title")
	private String title;
	@JsonProperty("definition")
	private String definition;
	@JsonProperty("statut")
	private String statut;
	@JsonProperty("illustrations")
	private String illustrations;
	@JsonProperty("reference")
	private String reference;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	/**
	*
	* @return
	* The title
	*/
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}
	
	/**
	*
	* @param title
	* The title
	*/
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	*
	* @return
	* The definition
	*/
	@JsonProperty("definition")
	public String getDefinition() {
		return definition;
	}
	
	/**
	*
	* @param definition
	* The definition
	*/
	@JsonProperty("definition")
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	/**
	*
	* @return
	* The statut
	*/
	@JsonProperty("statut")
	public String getStatut() {
		return statut;
	}
	
	/**
	*
	* @param statut
	* The statut
	*/
	@JsonProperty("statut")
	public void setStatut(String statut) {
		this.statut = statut;
	}
	
	/**
	*
	* @return
	* The illustrations
	*/
	@JsonProperty("illustrations")
	public String getIllustrations() {
		return illustrations;
	}
	
	/**
	*
	* @param illustrations
	* The illustrations
	*/
	@JsonProperty("illustrations")
	public void setIllustrations(String illustrations) {
		this.illustrations = illustrations;
	}
	
	/**
	*
	* @return
	* The reference
	*/
	@JsonProperty("reference")
	public String getReference() {
		return reference;
	}
	
	/**
	*
	* @param reference
	* The reference
	*/
	@JsonProperty("reference")
	public void setReference(String reference) {
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