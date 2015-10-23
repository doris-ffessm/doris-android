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
"classIdentifier",
"data_map"
})
public class Definition {

@JsonProperty("classIdentifier")
private String classIdentifier;
@JsonProperty("data_map")
private DataMap dataMap;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
*
* @return
* The classIdentifier
*/
@JsonProperty("classIdentifier")
public String getClassIdentifier() {
return classIdentifier;
}

/**
*
* @param classIdentifier
* The classIdentifier
*/
@JsonProperty("classIdentifier")
public void setClassIdentifier(String classIdentifier) {
this.classIdentifier = classIdentifier;
}

/**
*
* @return
* The dataMap
*/
@JsonProperty("data_map")
public DataMap getDataMap() {
return dataMap;
}

/**
*
* @param dataMap
* The data_map
*/
@JsonProperty("data_map")
public void setDataMap(DataMap dataMap) {
this.dataMap = dataMap;
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