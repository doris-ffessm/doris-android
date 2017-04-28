
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
public class Fields {

    @JsonProperty("title")
    private Title title;
    @JsonProperty("cover")
    private Cover cover;
    @JsonProperty("publication_year")
    private PublicationYear publicationYear;
    @JsonProperty("extra_info")
    private ExtraInfo extraInfo;
    @JsonProperty("isbn")
    private Isbn isbn;
    @JsonProperty("main_author")
    private MainAuthor mainAuthor;
    @JsonProperty("extra_authors")
    private ExtraAuthors extraAuthors;
    @JsonProperty("reference")
    private Reference reference;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Fields() {
    }

    /**
     * 
     * @param cover
     * @param title
     * @param extraAuthors
     * @param isbn
     * @param mainAuthor
     * @param extraInfo
     * @param publicationYear
     * @param reference
     */
    public Fields(Title title, Cover cover, PublicationYear publicationYear, ExtraInfo extraInfo, Isbn isbn, MainAuthor mainAuthor, ExtraAuthors extraAuthors, Reference reference) {
        this.title = title;
        this.cover = cover;
        this.publicationYear = publicationYear;
        this.extraInfo = extraInfo;
        this.isbn = isbn;
        this.mainAuthor = mainAuthor;
        this.extraAuthors = extraAuthors;
        this.reference = reference;
    }

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
     *     The cover
     */
    @JsonProperty("cover")
    public Cover getCover() {
        return cover;
    }

    /**
     * 
     * @param cover
     *     The cover
     */
    @JsonProperty("cover")
    public void setCover(Cover cover) {
        this.cover = cover;
    }

    /**
     * 
     * @return
     *     The publicationYear
     */
    @JsonProperty("publication_year")
    public PublicationYear getPublicationYear() {
        return publicationYear;
    }

    /**
     * 
     * @param publicationYear
     *     The publication_year
     */
    @JsonProperty("publication_year")
    public void setPublicationYear(PublicationYear publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * 
     * @return
     *     The extraInfo
     */
    @JsonProperty("extra_info")
    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

    /**
     * 
     * @param extraInfo
     *     The extra_info
     */
    @JsonProperty("extra_info")
    public void setExtraInfo(ExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * 
     * @return
     *     The isbn
     */
    @JsonProperty("isbn")
    public Isbn getIsbn() {
        return isbn;
    }

    /**
     * 
     * @param isbn
     *     The isbn
     */
    @JsonProperty("isbn")
    public void setIsbn(Isbn isbn) {
        this.isbn = isbn;
    }

    /**
     * 
     * @return
     *     The mainAuthor
     */
    @JsonProperty("main_author")
    public MainAuthor getMainAuthor() {
        return mainAuthor;
    }

    /**
     * 
     * @param mainAuthor
     *     The main_author
     */
    @JsonProperty("main_author")
    public void setMainAuthor(MainAuthor mainAuthor) {
        this.mainAuthor = mainAuthor;
    }

    /**
     * 
     * @return
     *     The extraAuthors
     */
    @JsonProperty("extra_authors")
    public ExtraAuthors getExtraAuthors() {
        return extraAuthors;
    }

    /**
     * 
     * @param extraAuthors
     *     The extra_authors
     */
    @JsonProperty("extra_authors")
    public void setExtraAuthors(ExtraAuthors extraAuthors) {
        this.extraAuthors = extraAuthors;
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
