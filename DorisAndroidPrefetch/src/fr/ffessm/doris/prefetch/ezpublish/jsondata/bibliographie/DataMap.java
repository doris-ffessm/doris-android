
package fr.ffessm.doris.prefetch.ezpublish.jsondata.bibliographie;

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
    "cover",
    "publication_year",
    "extra_info",
    "isbn",
    "main_author",
    "extra_authors",
    "reference"
})
public class DataMap {

    @JsonProperty("title")
    private String title;
    @JsonProperty("cover")
    private String cover;
    @JsonProperty("publication_year")
    private String publicationYear;
    @JsonProperty("extra_info")
    private String extraInfo;
    @JsonProperty("isbn")
    private String isbn;
    @JsonProperty("main_author")
    private String mainAuthor;
    @JsonProperty("extra_authors")
    private String extraAuthors;
    @JsonProperty("reference")
    private String reference;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The cover
     */
    @JsonProperty("cover")
    public String getCover() {
        return cover;
    }

    /**
     * 
     * @param cover
     *     The cover
     */
    @JsonProperty("cover")
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 
     * @return
     *     The publicationYear
     */
    @JsonProperty("publication_year")
    public String getPublicationYear() {
        return publicationYear;
    }

    /**
     * 
     * @param publicationYear
     *     The publication_year
     */
    @JsonProperty("publication_year")
    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * 
     * @return
     *     The extraInfo
     */
    @JsonProperty("extra_info")
    public String getExtraInfo() {
        return extraInfo;
    }

    /**
     * 
     * @param extraInfo
     *     The extra_info
     */
    @JsonProperty("extra_info")
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * 
     * @return
     *     The isbn
     */
    @JsonProperty("isbn")
    public String getIsbn() {
        return isbn;
    }

    /**
     * 
     * @param isbn
     *     The isbn
     */
    @JsonProperty("isbn")
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * 
     * @return
     *     The mainAuthor
     */
    @JsonProperty("main_author")
    public String getMainAuthor() {
        return mainAuthor;
    }

    /**
     * 
     * @param mainAuthor
     *     The main_author
     */
    @JsonProperty("main_author")
    public void setMainAuthor(String mainAuthor) {
        this.mainAuthor = mainAuthor;
    }

    /**
     * 
     * @return
     *     The extraAuthors
     */
    @JsonProperty("extra_authors")
    public String getExtraAuthors() {
        return extraAuthors;
    }

    /**
     * 
     * @param extraAuthors
     *     The extra_authors
     */
    @JsonProperty("extra_authors")
    public void setExtraAuthors(String extraAuthors) {
        this.extraAuthors = extraAuthors;
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
