
package fr.ffessm.doris.prefetch.ezpublish.jsondata.image;

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
    "file_name",
    "title",
    "legend",
    "image",
    "author",
    "condition",
    "shooting_date",
    "reference"
})
public class DataMap {

    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("title")
    private String title;
    @JsonProperty("legend")
    private String legend;
    @JsonProperty("image")
    private String image;
    @JsonProperty("author")
    private String author;
    @JsonProperty("condition")
    private String condition;
    @JsonProperty("shooting_date")
    private String shootingDate;
    @JsonProperty("reference")
    private String reference;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The fileName
     */
    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    /**
     * 
     * @param fileName
     *     The file_name
     */
    @JsonProperty("file_name")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The legend
     */
    @JsonProperty("legend")
    public String getLegend() {
        return legend;
    }

    /**
     * 
     * @param legend
     *     The legend
     */
    @JsonProperty("legend")
    public void setLegend(String legend) {
        this.legend = legend;
    }

    /**
     * 
     * @return
     *     The image
     */
    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    /**
     * 
     * @param image
     *     The image
     */
    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 
     * @return
     *     The author
     */
    @JsonProperty("author")
    public String getAuthor() {
        return author;
    }

    /**
     * 
     * @param author
     *     The author
     */
    @JsonProperty("author")
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * 
     * @return
     *     The condition
     */
    @JsonProperty("condition")
    public String getCondition() {
        return condition;
    }

    /**
     * 
     * @param condition
     *     The condition
     */
    @JsonProperty("condition")
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * 
     * @return
     *     The shootingDate
     */
    @JsonProperty("shooting_date")
    public String getShootingDate() {
        return shootingDate;
    }

    /**
     * 
     * @param shootingDate
     *     The shooting_date
     */
    @JsonProperty("shooting_date")
    public void setShootingDate(String shootingDate) {
        this.shootingDate = shootingDate;
    }

    /**
     * 
     * @return
     *     The reference
     */
    @JsonProperty("reference")
    public String getReference() {
        return reference;
    }

    /**
     * 
     * @param reference
     *     The reference
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
