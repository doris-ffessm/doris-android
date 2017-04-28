
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
    "first_name",
    "last_name",
    "user_account",
    "image",
    "description",
    "photo_gear",
    "website",
    "address",
    "town",
    "postal_code",
    "telephone",
    "mobile",
    "region",
    "country",
    "copyright_doris",
    "copyright_bioobs",
    "file_one",
    "file_two",
    "file_three",
    "reference",
    "correction_member"
})
public class Fields {

    @JsonProperty("first_name")
    private FirstName firstName;
    @JsonProperty("last_name")
    private LastName lastName;
    @JsonProperty("user_account")
    private UserAccount userAccount;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("description")
    private Description description;
    @JsonProperty("photo_gear")
    private PhotoGear photoGear;
    @JsonProperty("website")
    private Website website;
    @JsonProperty("address")
    private Address address;
    @JsonProperty("town")
    private Town town;
    @JsonProperty("postal_code")
    private PostalCode postalCode;
    @JsonProperty("telephone")
    private Telephone telephone;
    @JsonProperty("mobile")
    private Mobile mobile;
    @JsonProperty("region")
    private Region region;
    @JsonProperty("country")
    private Country country;
    @JsonProperty("copyright_doris")
    private CopyrightDoris copyrightDoris;
    @JsonProperty("copyright_bioobs")
    private CopyrightBioobs copyrightBioobs;
    @JsonProperty("file_one")
    private FileOne fileOne;
    @JsonProperty("file_two")
    private FileTwo fileTwo;
    @JsonProperty("file_three")
    private FileThree fileThree;
    @JsonProperty("reference")
    private Reference reference;
    @JsonProperty("correction_member")
    private CorrectionMember correctionMember;
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
     * @param region
     * @param lastName
     * @param photoGear
     * @param correctionMember
     * @param website
     * @param image
     * @param reference
     * @param country
     * @param fileTwo
     * @param fileThree
     * @param copyrightBioobs
     * @param postalCode
     * @param address
     * @param description
     * @param fileOne
     * @param town
     * @param firstName
     * @param userAccount
     * @param copyrightDoris
     * @param telephone
     * @param mobile
     */
    public Fields(FirstName firstName, LastName lastName, UserAccount userAccount, Image image, Description description, PhotoGear photoGear, Website website, Address address, Town town, PostalCode postalCode, Telephone telephone, Mobile mobile, Region region, Country country, CopyrightDoris copyrightDoris, CopyrightBioobs copyrightBioobs, FileOne fileOne, FileTwo fileTwo, FileThree fileThree, Reference reference, CorrectionMember correctionMember) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userAccount = userAccount;
        this.image = image;
        this.description = description;
        this.photoGear = photoGear;
        this.website = website;
        this.address = address;
        this.town = town;
        this.postalCode = postalCode;
        this.telephone = telephone;
        this.mobile = mobile;
        this.region = region;
        this.country = country;
        this.copyrightDoris = copyrightDoris;
        this.copyrightBioobs = copyrightBioobs;
        this.fileOne = fileOne;
        this.fileTwo = fileTwo;
        this.fileThree = fileThree;
        this.reference = reference;
        this.correctionMember = correctionMember;
    }

    /**
     * 
     * @return
     *     The firstName
     */
    @JsonProperty("first_name")
    public FirstName getFirstName() {
        return firstName;
    }

    /**
     * 
     * @param firstName
     *     The first_name
     */
    @JsonProperty("first_name")
    public void setFirstName(FirstName firstName) {
        this.firstName = firstName;
    }

    public Fields withFirstName(FirstName firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     * 
     * @return
     *     The lastName
     */
    @JsonProperty("last_name")
    public LastName getLastName() {
        return lastName;
    }

    /**
     * 
     * @param lastName
     *     The last_name
     */
    @JsonProperty("last_name")
    public void setLastName(LastName lastName) {
        this.lastName = lastName;
    }

    public Fields withLastName(LastName lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * 
     * @return
     *     The userAccount
     */
    @JsonProperty("user_account")
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * 
     * @param userAccount
     *     The user_account
     */
    @JsonProperty("user_account")
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Fields withUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    /**
     * 
     * @return
     *     The image
     */
    @JsonProperty("image")
    public Image getImage() {
        return image;
    }

    /**
     * 
     * @param image
     *     The image
     */
    @JsonProperty("image")
    public void setImage(Image image) {
        this.image = image;
    }

    public Fields withImage(Image image) {
        this.image = image;
        return this;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public Description getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(Description description) {
        this.description = description;
    }

    public Fields withDescription(Description description) {
        this.description = description;
        return this;
    }

    /**
     * 
     * @return
     *     The photoGear
     */
    @JsonProperty("photo_gear")
    public PhotoGear getPhotoGear() {
        return photoGear;
    }

    /**
     * 
     * @param photoGear
     *     The photo_gear
     */
    @JsonProperty("photo_gear")
    public void setPhotoGear(PhotoGear photoGear) {
        this.photoGear = photoGear;
    }

    public Fields withPhotoGear(PhotoGear photoGear) {
        this.photoGear = photoGear;
        return this;
    }

    /**
     * 
     * @return
     *     The website
     */
    @JsonProperty("website")
    public Website getWebsite() {
        return website;
    }

    /**
     * 
     * @param website
     *     The website
     */
    @JsonProperty("website")
    public void setWebsite(Website website) {
        this.website = website;
    }

    public Fields withWebsite(Website website) {
        this.website = website;
        return this;
    }

    /**
     * 
     * @return
     *     The address
     */
    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
    }

    public Fields withAddress(Address address) {
        this.address = address;
        return this;
    }

    /**
     * 
     * @return
     *     The town
     */
    @JsonProperty("town")
    public Town getTown() {
        return town;
    }

    /**
     * 
     * @param town
     *     The town
     */
    @JsonProperty("town")
    public void setTown(Town town) {
        this.town = town;
    }

    public Fields withTown(Town town) {
        this.town = town;
        return this;
    }

    /**
     * 
     * @return
     *     The postalCode
     */
    @JsonProperty("postal_code")
    public PostalCode getPostalCode() {
        return postalCode;
    }

    /**
     * 
     * @param postalCode
     *     The postal_code
     */
    @JsonProperty("postal_code")
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    public Fields withPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    /**
     * 
     * @return
     *     The telephone
     */
    @JsonProperty("telephone")
    public Telephone getTelephone() {
        return telephone;
    }

    /**
     * 
     * @param telephone
     *     The telephone
     */
    @JsonProperty("telephone")
    public void setTelephone(Telephone telephone) {
        this.telephone = telephone;
    }

    public Fields withTelephone(Telephone telephone) {
        this.telephone = telephone;
        return this;
    }

    /**
     * 
     * @return
     *     The mobile
     */
    @JsonProperty("mobile")
    public Mobile getMobile() {
        return mobile;
    }

    /**
     * 
     * @param mobile
     *     The mobile
     */
    @JsonProperty("mobile")
    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

    public Fields withMobile(Mobile mobile) {
        this.mobile = mobile;
        return this;
    }

    /**
     * 
     * @return
     *     The region
     */
    @JsonProperty("region")
    public Region getRegion() {
        return region;
    }

    /**
     * 
     * @param region
     *     The region
     */
    @JsonProperty("region")
    public void setRegion(Region region) {
        this.region = region;
    }

    public Fields withRegion(Region region) {
        this.region = region;
        return this;
    }

    /**
     * 
     * @return
     *     The country
     */
    @JsonProperty("country")
    public Country getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    @JsonProperty("country")
    public void setCountry(Country country) {
        this.country = country;
    }

    public Fields withCountry(Country country) {
        this.country = country;
        return this;
    }

    /**
     * 
     * @return
     *     The copyrightDoris
     */
    @JsonProperty("copyright_doris")
    public CopyrightDoris getCopyrightDoris() {
        return copyrightDoris;
    }

    /**
     * 
     * @param copyrightDoris
     *     The copyright_doris
     */
    @JsonProperty("copyright_doris")
    public void setCopyrightDoris(CopyrightDoris copyrightDoris) {
        this.copyrightDoris = copyrightDoris;
    }

    public Fields withCopyrightDoris(CopyrightDoris copyrightDoris) {
        this.copyrightDoris = copyrightDoris;
        return this;
    }

    /**
     * 
     * @return
     *     The copyrightBioobs
     */
    @JsonProperty("copyright_bioobs")
    public CopyrightBioobs getCopyrightBioobs() {
        return copyrightBioobs;
    }

    /**
     * 
     * @param copyrightBioobs
     *     The copyright_bioobs
     */
    @JsonProperty("copyright_bioobs")
    public void setCopyrightBioobs(CopyrightBioobs copyrightBioobs) {
        this.copyrightBioobs = copyrightBioobs;
    }

    public Fields withCopyrightBioobs(CopyrightBioobs copyrightBioobs) {
        this.copyrightBioobs = copyrightBioobs;
        return this;
    }

    /**
     * 
     * @return
     *     The fileOne
     */
    @JsonProperty("file_one")
    public FileOne getFileOne() {
        return fileOne;
    }

    /**
     * 
     * @param fileOne
     *     The file_one
     */
    @JsonProperty("file_one")
    public void setFileOne(FileOne fileOne) {
        this.fileOne = fileOne;
    }

    public Fields withFileOne(FileOne fileOne) {
        this.fileOne = fileOne;
        return this;
    }

    /**
     * 
     * @return
     *     The fileTwo
     */
    @JsonProperty("file_two")
    public FileTwo getFileTwo() {
        return fileTwo;
    }

    /**
     * 
     * @param fileTwo
     *     The file_two
     */
    @JsonProperty("file_two")
    public void setFileTwo(FileTwo fileTwo) {
        this.fileTwo = fileTwo;
    }

    public Fields withFileTwo(FileTwo fileTwo) {
        this.fileTwo = fileTwo;
        return this;
    }

    /**
     * 
     * @return
     *     The fileThree
     */
    @JsonProperty("file_three")
    public FileThree getFileThree() {
        return fileThree;
    }

    /**
     * 
     * @param fileThree
     *     The file_three
     */
    @JsonProperty("file_three")
    public void setFileThree(FileThree fileThree) {
        this.fileThree = fileThree;
    }

    public Fields withFileThree(FileThree fileThree) {
        this.fileThree = fileThree;
        return this;
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

    public Fields withReference(Reference reference) {
        this.reference = reference;
        return this;
    }

    /**
     * 
     * @return
     *     The correctionMember
     */
    @JsonProperty("correction_member")
    public CorrectionMember getCorrectionMember() {
        return correctionMember;
    }

    /**
     * 
     * @param correctionMember
     *     The correction_member
     */
    @JsonProperty("correction_member")
    public void setCorrectionMember(CorrectionMember correctionMember) {
        this.correctionMember = correctionMember;
    }

    public Fields withCorrectionMember(CorrectionMember correctionMember) {
        this.correctionMember = correctionMember;
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

    public Fields withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
