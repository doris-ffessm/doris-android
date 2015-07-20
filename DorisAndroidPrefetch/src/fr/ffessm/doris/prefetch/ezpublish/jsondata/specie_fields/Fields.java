
package fr.ffessm.doris.prefetch.ezpublish.jsondata.specie_fields;

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
    "publication_date",
    "chantier_date",
    "verificateurs",
    "contributors",
    "discoverer",
    "display_home",
    "state",
    "nom_commun_fr",
    "nom_commun_inter",
    "others_nom_commun_fr",
    "others_name_scientific",
    "french_name_origin",
    "scientific_name_origin",
    "biotop",
    "description",
    "look_likes",
    "reglementation",
    "alimentation",
    "reproduction",
    "associated_life",
    "bio_divers",
    "biblio_ref",
    "others_biblio_ref",
    "links",
    "preformat_links",
    "zone_geo",
    "danger",
    "complementary_infos",
    "export_lines",
    "images",
    "group",
    "embranchement_taxon",
    "sous_embranchement_taxon",
    "super_classe_taxon",
    "classe_taxon",
    "sous_classe_taxon",
    "super_ordre_taxon",
    "ordre_taxon",
    "sous_ordre_taxon",
    "famille_taxon",
    "sous_famille_taxon",
    "genre",
    "espece",
    "principal_writer",
    "doridiens",
    "reference",
    "distribution_resume",
    "cle_identification",
    "correcteurs",
    "display_home_date",
    "distribution"
})
public class Fields {

    @JsonProperty("publication_date")
    private PublicationDate publicationDate;
    @JsonProperty("chantier_date")
    private ChantierDate chantierDate;
    @JsonProperty("verificateurs")
    private Verificateurs verificateurs;
    @JsonProperty("contributors")
    private Contributors contributors;
    @JsonProperty("discoverer")
    private Discoverer discoverer;
    @JsonProperty("display_home")
    private DisplayHome displayHome;
    @JsonProperty("state")
    private State state;
    @JsonProperty("nom_commun_fr")
    private NomCommunFr nomCommunFr;
    @JsonProperty("nom_commun_inter")
    private NomCommunInter nomCommunInter;
    @JsonProperty("others_nom_commun_fr")
    private OthersNomCommunFr othersNomCommunFr;
    @JsonProperty("others_name_scientific")
    private OthersNameScientific othersNameScientific;
    @JsonProperty("french_name_origin")
    private FrenchNameOrigin frenchNameOrigin;
    @JsonProperty("scientific_name_origin")
    private ScientificNameOrigin scientificNameOrigin;
    @JsonProperty("biotop")
    private Biotop biotop;
    @JsonProperty("description")
    private Description description;
    @JsonProperty("look_likes")
    private LookLikes lookLikes;
    @JsonProperty("reglementation")
    private Reglementation reglementation;
    @JsonProperty("alimentation")
    private Alimentation alimentation;
    @JsonProperty("reproduction")
    private Reproduction reproduction;
    @JsonProperty("associated_life")
    private AssociatedLife associatedLife;
    @JsonProperty("bio_divers")
    private BioDivers bioDivers;
    @JsonProperty("biblio_ref")
    private BiblioRef biblioRef;
    @JsonProperty("others_biblio_ref")
    private OthersBiblioRef othersBiblioRef;
    @JsonProperty("links")
    private Links links;
    @JsonProperty("preformat_links")
    private PreformatLinks preformatLinks;
    @JsonProperty("zone_geo")
    private ZoneGeo zoneGeo;
    @JsonProperty("danger")
    private Danger danger;
    @JsonProperty("complementary_infos")
    private ComplementaryInfos complementaryInfos;
    @JsonProperty("export_lines")
    private ExportLines exportLines;
    @JsonProperty("images")
    private Images images;
    @JsonProperty("group")
    private Group group;
    @JsonProperty("embranchement_taxon")
    private EmbranchementTaxon embranchementTaxon;
    @JsonProperty("sous_embranchement_taxon")
    private SousEmbranchementTaxon sousEmbranchementTaxon;
    @JsonProperty("super_classe_taxon")
    private SuperClasseTaxon superClasseTaxon;
    @JsonProperty("classe_taxon")
    private ClasseTaxon classeTaxon;
    @JsonProperty("sous_classe_taxon")
    private SousClasseTaxon sousClasseTaxon;
    @JsonProperty("super_ordre_taxon")
    private SuperOrdreTaxon superOrdreTaxon;
    @JsonProperty("ordre_taxon")
    private OrdreTaxon ordreTaxon;
    @JsonProperty("sous_ordre_taxon")
    private SousOrdreTaxon sousOrdreTaxon;
    @JsonProperty("famille_taxon")
    private FamilleTaxon familleTaxon;
    @JsonProperty("sous_famille_taxon")
    private SousFamilleTaxon sousFamilleTaxon;
    @JsonProperty("genre")
    private Genre genre;
    @JsonProperty("espece")
    private Espece espece;
    @JsonProperty("principal_writer")
    private PrincipalWriter principalWriter;
    @JsonProperty("doridiens")
    private Doridiens doridiens;
    @JsonProperty("reference")
    private Reference reference;
    @JsonProperty("distribution_resume")
    private DistributionResume distributionResume;
    @JsonProperty("cle_identification")
    private CleIdentification cleIdentification;
    @JsonProperty("correcteurs")
    private Correcteurs correcteurs;
    @JsonProperty("display_home_date")
    private DisplayHomeDate displayHomeDate;
    @JsonProperty("distribution")
    private Distribution distribution;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The publicationDate
     */
    @JsonProperty("publication_date")
    public PublicationDate getPublicationDate() {
        return publicationDate;
    }

    /**
     * 
     * @param publicationDate
     *     The publication_date
     */
    @JsonProperty("publication_date")
    public void setPublicationDate(PublicationDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * 
     * @return
     *     The chantierDate
     */
    @JsonProperty("chantier_date")
    public ChantierDate getChantierDate() {
        return chantierDate;
    }

    /**
     * 
     * @param chantierDate
     *     The chantier_date
     */
    @JsonProperty("chantier_date")
    public void setChantierDate(ChantierDate chantierDate) {
        this.chantierDate = chantierDate;
    }

    /**
     * 
     * @return
     *     The verificateurs
     */
    @JsonProperty("verificateurs")
    public Verificateurs getVerificateurs() {
        return verificateurs;
    }

    /**
     * 
     * @param verificateurs
     *     The verificateurs
     */
    @JsonProperty("verificateurs")
    public void setVerificateurs(Verificateurs verificateurs) {
        this.verificateurs = verificateurs;
    }

    /**
     * 
     * @return
     *     The contributors
     */
    @JsonProperty("contributors")
    public Contributors getContributors() {
        return contributors;
    }

    /**
     * 
     * @param contributors
     *     The contributors
     */
    @JsonProperty("contributors")
    public void setContributors(Contributors contributors) {
        this.contributors = contributors;
    }

    /**
     * 
     * @return
     *     The discoverer
     */
    @JsonProperty("discoverer")
    public Discoverer getDiscoverer() {
        return discoverer;
    }

    /**
     * 
     * @param discoverer
     *     The discoverer
     */
    @JsonProperty("discoverer")
    public void setDiscoverer(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    /**
     * 
     * @return
     *     The displayHome
     */
    @JsonProperty("display_home")
    public DisplayHome getDisplayHome() {
        return displayHome;
    }

    /**
     * 
     * @param displayHome
     *     The display_home
     */
    @JsonProperty("display_home")
    public void setDisplayHome(DisplayHome displayHome) {
        this.displayHome = displayHome;
    }

    /**
     * 
     * @return
     *     The state
     */
    @JsonProperty("state")
    public State getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    @JsonProperty("state")
    public void setState(State state) {
        this.state = state;
    }

    /**
     * 
     * @return
     *     The nomCommunFr
     */
    @JsonProperty("nom_commun_fr")
    public NomCommunFr getNomCommunFr() {
        return nomCommunFr;
    }

    /**
     * 
     * @param nomCommunFr
     *     The nom_commun_fr
     */
    @JsonProperty("nom_commun_fr")
    public void setNomCommunFr(NomCommunFr nomCommunFr) {
        this.nomCommunFr = nomCommunFr;
    }

    /**
     * 
     * @return
     *     The nomCommunInter
     */
    @JsonProperty("nom_commun_inter")
    public NomCommunInter getNomCommunInter() {
        return nomCommunInter;
    }

    /**
     * 
     * @param nomCommunInter
     *     The nom_commun_inter
     */
    @JsonProperty("nom_commun_inter")
    public void setNomCommunInter(NomCommunInter nomCommunInter) {
        this.nomCommunInter = nomCommunInter;
    }

    /**
     * 
     * @return
     *     The othersNomCommunFr
     */
    @JsonProperty("others_nom_commun_fr")
    public OthersNomCommunFr getOthersNomCommunFr() {
        return othersNomCommunFr;
    }

    /**
     * 
     * @param othersNomCommunFr
     *     The others_nom_commun_fr
     */
    @JsonProperty("others_nom_commun_fr")
    public void setOthersNomCommunFr(OthersNomCommunFr othersNomCommunFr) {
        this.othersNomCommunFr = othersNomCommunFr;
    }

    /**
     * 
     * @return
     *     The othersNameScientific
     */
    @JsonProperty("others_name_scientific")
    public OthersNameScientific getOthersNameScientific() {
        return othersNameScientific;
    }

    /**
     * 
     * @param othersNameScientific
     *     The others_name_scientific
     */
    @JsonProperty("others_name_scientific")
    public void setOthersNameScientific(OthersNameScientific othersNameScientific) {
        this.othersNameScientific = othersNameScientific;
    }

    /**
     * 
     * @return
     *     The frenchNameOrigin
     */
    @JsonProperty("french_name_origin")
    public FrenchNameOrigin getFrenchNameOrigin() {
        return frenchNameOrigin;
    }

    /**
     * 
     * @param frenchNameOrigin
     *     The french_name_origin
     */
    @JsonProperty("french_name_origin")
    public void setFrenchNameOrigin(FrenchNameOrigin frenchNameOrigin) {
        this.frenchNameOrigin = frenchNameOrigin;
    }

    /**
     * 
     * @return
     *     The scientificNameOrigin
     */
    @JsonProperty("scientific_name_origin")
    public ScientificNameOrigin getScientificNameOrigin() {
        return scientificNameOrigin;
    }

    /**
     * 
     * @param scientificNameOrigin
     *     The scientific_name_origin
     */
    @JsonProperty("scientific_name_origin")
    public void setScientificNameOrigin(ScientificNameOrigin scientificNameOrigin) {
        this.scientificNameOrigin = scientificNameOrigin;
    }

    /**
     * 
     * @return
     *     The biotop
     */
    @JsonProperty("biotop")
    public Biotop getBiotop() {
        return biotop;
    }

    /**
     * 
     * @param biotop
     *     The biotop
     */
    @JsonProperty("biotop")
    public void setBiotop(Biotop biotop) {
        this.biotop = biotop;
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

    /**
     * 
     * @return
     *     The lookLikes
     */
    @JsonProperty("look_likes")
    public LookLikes getLookLikes() {
        return lookLikes;
    }

    /**
     * 
     * @param lookLikes
     *     The look_likes
     */
    @JsonProperty("look_likes")
    public void setLookLikes(LookLikes lookLikes) {
        this.lookLikes = lookLikes;
    }

    /**
     * 
     * @return
     *     The reglementation
     */
    @JsonProperty("reglementation")
    public Reglementation getReglementation() {
        return reglementation;
    }

    /**
     * 
     * @param reglementation
     *     The reglementation
     */
    @JsonProperty("reglementation")
    public void setReglementation(Reglementation reglementation) {
        this.reglementation = reglementation;
    }

    /**
     * 
     * @return
     *     The alimentation
     */
    @JsonProperty("alimentation")
    public Alimentation getAlimentation() {
        return alimentation;
    }

    /**
     * 
     * @param alimentation
     *     The alimentation
     */
    @JsonProperty("alimentation")
    public void setAlimentation(Alimentation alimentation) {
        this.alimentation = alimentation;
    }

    /**
     * 
     * @return
     *     The reproduction
     */
    @JsonProperty("reproduction")
    public Reproduction getReproduction() {
        return reproduction;
    }

    /**
     * 
     * @param reproduction
     *     The reproduction
     */
    @JsonProperty("reproduction")
    public void setReproduction(Reproduction reproduction) {
        this.reproduction = reproduction;
    }

    /**
     * 
     * @return
     *     The associatedLife
     */
    @JsonProperty("associated_life")
    public AssociatedLife getAssociatedLife() {
        return associatedLife;
    }

    /**
     * 
     * @param associatedLife
     *     The associated_life
     */
    @JsonProperty("associated_life")
    public void setAssociatedLife(AssociatedLife associatedLife) {
        this.associatedLife = associatedLife;
    }

    /**
     * 
     * @return
     *     The bioDivers
     */
    @JsonProperty("bio_divers")
    public BioDivers getBioDivers() {
        return bioDivers;
    }

    /**
     * 
     * @param bioDivers
     *     The bio_divers
     */
    @JsonProperty("bio_divers")
    public void setBioDivers(BioDivers bioDivers) {
        this.bioDivers = bioDivers;
    }

    /**
     * 
     * @return
     *     The biblioRef
     */
    @JsonProperty("biblio_ref")
    public BiblioRef getBiblioRef() {
        return biblioRef;
    }

    /**
     * 
     * @param biblioRef
     *     The biblio_ref
     */
    @JsonProperty("biblio_ref")
    public void setBiblioRef(BiblioRef biblioRef) {
        this.biblioRef = biblioRef;
    }

    /**
     * 
     * @return
     *     The othersBiblioRef
     */
    @JsonProperty("others_biblio_ref")
    public OthersBiblioRef getOthersBiblioRef() {
        return othersBiblioRef;
    }

    /**
     * 
     * @param othersBiblioRef
     *     The others_biblio_ref
     */
    @JsonProperty("others_biblio_ref")
    public void setOthersBiblioRef(OthersBiblioRef othersBiblioRef) {
        this.othersBiblioRef = othersBiblioRef;
    }

    /**
     * 
     * @return
     *     The links
     */
    @JsonProperty("links")
    public Links getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    @JsonProperty("links")
    public void setLinks(Links links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The preformatLinks
     */
    @JsonProperty("preformat_links")
    public PreformatLinks getPreformatLinks() {
        return preformatLinks;
    }

    /**
     * 
     * @param preformatLinks
     *     The preformat_links
     */
    @JsonProperty("preformat_links")
    public void setPreformatLinks(PreformatLinks preformatLinks) {
        this.preformatLinks = preformatLinks;
    }

    /**
     * 
     * @return
     *     The zoneGeo
     */
    @JsonProperty("zone_geo")
    public ZoneGeo getZoneGeo() {
        return zoneGeo;
    }

    /**
     * 
     * @param zoneGeo
     *     The zone_geo
     */
    @JsonProperty("zone_geo")
    public void setZoneGeo(ZoneGeo zoneGeo) {
        this.zoneGeo = zoneGeo;
    }

    /**
     * 
     * @return
     *     The danger
     */
    @JsonProperty("danger")
    public Danger getDanger() {
        return danger;
    }

    /**
     * 
     * @param danger
     *     The danger
     */
    @JsonProperty("danger")
    public void setDanger(Danger danger) {
        this.danger = danger;
    }

    /**
     * 
     * @return
     *     The complementaryInfos
     */
    @JsonProperty("complementary_infos")
    public ComplementaryInfos getComplementaryInfos() {
        return complementaryInfos;
    }

    /**
     * 
     * @param complementaryInfos
     *     The complementary_infos
     */
    @JsonProperty("complementary_infos")
    public void setComplementaryInfos(ComplementaryInfos complementaryInfos) {
        this.complementaryInfos = complementaryInfos;
    }

    /**
     * 
     * @return
     *     The exportLines
     */
    @JsonProperty("export_lines")
    public ExportLines getExportLines() {
        return exportLines;
    }

    /**
     * 
     * @param exportLines
     *     The export_lines
     */
    @JsonProperty("export_lines")
    public void setExportLines(ExportLines exportLines) {
        this.exportLines = exportLines;
    }

    /**
     * 
     * @return
     *     The images
     */
    @JsonProperty("images")
    public Images getImages() {
        return images;
    }

    /**
     * 
     * @param images
     *     The images
     */
    @JsonProperty("images")
    public void setImages(Images images) {
        this.images = images;
    }

    /**
     * 
     * @return
     *     The group
     */
    @JsonProperty("group")
    public Group getGroup() {
        return group;
    }

    /**
     * 
     * @param group
     *     The group
     */
    @JsonProperty("group")
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * 
     * @return
     *     The embranchementTaxon
     */
    @JsonProperty("embranchement_taxon")
    public EmbranchementTaxon getEmbranchementTaxon() {
        return embranchementTaxon;
    }

    /**
     * 
     * @param embranchementTaxon
     *     The embranchement_taxon
     */
    @JsonProperty("embranchement_taxon")
    public void setEmbranchementTaxon(EmbranchementTaxon embranchementTaxon) {
        this.embranchementTaxon = embranchementTaxon;
    }

    /**
     * 
     * @return
     *     The sousEmbranchementTaxon
     */
    @JsonProperty("sous_embranchement_taxon")
    public SousEmbranchementTaxon getSousEmbranchementTaxon() {
        return sousEmbranchementTaxon;
    }

    /**
     * 
     * @param sousEmbranchementTaxon
     *     The sous_embranchement_taxon
     */
    @JsonProperty("sous_embranchement_taxon")
    public void setSousEmbranchementTaxon(SousEmbranchementTaxon sousEmbranchementTaxon) {
        this.sousEmbranchementTaxon = sousEmbranchementTaxon;
    }

    /**
     * 
     * @return
     *     The superClasseTaxon
     */
    @JsonProperty("super_classe_taxon")
    public SuperClasseTaxon getSuperClasseTaxon() {
        return superClasseTaxon;
    }

    /**
     * 
     * @param superClasseTaxon
     *     The super_classe_taxon
     */
    @JsonProperty("super_classe_taxon")
    public void setSuperClasseTaxon(SuperClasseTaxon superClasseTaxon) {
        this.superClasseTaxon = superClasseTaxon;
    }

    /**
     * 
     * @return
     *     The classeTaxon
     */
    @JsonProperty("classe_taxon")
    public ClasseTaxon getClasseTaxon() {
        return classeTaxon;
    }

    /**
     * 
     * @param classeTaxon
     *     The classe_taxon
     */
    @JsonProperty("classe_taxon")
    public void setClasseTaxon(ClasseTaxon classeTaxon) {
        this.classeTaxon = classeTaxon;
    }

    /**
     * 
     * @return
     *     The sousClasseTaxon
     */
    @JsonProperty("sous_classe_taxon")
    public SousClasseTaxon getSousClasseTaxon() {
        return sousClasseTaxon;
    }

    /**
     * 
     * @param sousClasseTaxon
     *     The sous_classe_taxon
     */
    @JsonProperty("sous_classe_taxon")
    public void setSousClasseTaxon(SousClasseTaxon sousClasseTaxon) {
        this.sousClasseTaxon = sousClasseTaxon;
    }

    /**
     * 
     * @return
     *     The superOrdreTaxon
     */
    @JsonProperty("super_ordre_taxon")
    public SuperOrdreTaxon getSuperOrdreTaxon() {
        return superOrdreTaxon;
    }

    /**
     * 
     * @param superOrdreTaxon
     *     The super_ordre_taxon
     */
    @JsonProperty("super_ordre_taxon")
    public void setSuperOrdreTaxon(SuperOrdreTaxon superOrdreTaxon) {
        this.superOrdreTaxon = superOrdreTaxon;
    }

    /**
     * 
     * @return
     *     The ordreTaxon
     */
    @JsonProperty("ordre_taxon")
    public OrdreTaxon getOrdreTaxon() {
        return ordreTaxon;
    }

    /**
     * 
     * @param ordreTaxon
     *     The ordre_taxon
     */
    @JsonProperty("ordre_taxon")
    public void setOrdreTaxon(OrdreTaxon ordreTaxon) {
        this.ordreTaxon = ordreTaxon;
    }

    /**
     * 
     * @return
     *     The sousOrdreTaxon
     */
    @JsonProperty("sous_ordre_taxon")
    public SousOrdreTaxon getSousOrdreTaxon() {
        return sousOrdreTaxon;
    }

    /**
     * 
     * @param sousOrdreTaxon
     *     The sous_ordre_taxon
     */
    @JsonProperty("sous_ordre_taxon")
    public void setSousOrdreTaxon(SousOrdreTaxon sousOrdreTaxon) {
        this.sousOrdreTaxon = sousOrdreTaxon;
    }

    /**
     * 
     * @return
     *     The familleTaxon
     */
    @JsonProperty("famille_taxon")
    public FamilleTaxon getFamilleTaxon() {
        return familleTaxon;
    }

    /**
     * 
     * @param familleTaxon
     *     The famille_taxon
     */
    @JsonProperty("famille_taxon")
    public void setFamilleTaxon(FamilleTaxon familleTaxon) {
        this.familleTaxon = familleTaxon;
    }

    /**
     * 
     * @return
     *     The sousFamilleTaxon
     */
    @JsonProperty("sous_famille_taxon")
    public SousFamilleTaxon getSousFamilleTaxon() {
        return sousFamilleTaxon;
    }

    /**
     * 
     * @param sousFamilleTaxon
     *     The sous_famille_taxon
     */
    @JsonProperty("sous_famille_taxon")
    public void setSousFamilleTaxon(SousFamilleTaxon sousFamilleTaxon) {
        this.sousFamilleTaxon = sousFamilleTaxon;
    }

    /**
     * 
     * @return
     *     The genre
     */
    @JsonProperty("genre")
    public Genre getGenre() {
        return genre;
    }

    /**
     * 
     * @param genre
     *     The genre
     */
    @JsonProperty("genre")
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * 
     * @return
     *     The espece
     */
    @JsonProperty("espece")
    public Espece getEspece() {
        return espece;
    }

    /**
     * 
     * @param espece
     *     The espece
     */
    @JsonProperty("espece")
    public void setEspece(Espece espece) {
        this.espece = espece;
    }

    /**
     * 
     * @return
     *     The principalWriter
     */
    @JsonProperty("principal_writer")
    public PrincipalWriter getPrincipalWriter() {
        return principalWriter;
    }

    /**
     * 
     * @param principalWriter
     *     The principal_writer
     */
    @JsonProperty("principal_writer")
    public void setPrincipalWriter(PrincipalWriter principalWriter) {
        this.principalWriter = principalWriter;
    }

    /**
     * 
     * @return
     *     The doridiens
     */
    @JsonProperty("doridiens")
    public Doridiens getDoridiens() {
        return doridiens;
    }

    /**
     * 
     * @param doridiens
     *     The doridiens
     */
    @JsonProperty("doridiens")
    public void setDoridiens(Doridiens doridiens) {
        this.doridiens = doridiens;
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

    /**
     * 
     * @return
     *     The distributionResume
     */
    @JsonProperty("distribution_resume")
    public DistributionResume getDistributionResume() {
        return distributionResume;
    }

    /**
     * 
     * @param distributionResume
     *     The distribution_resume
     */
    @JsonProperty("distribution_resume")
    public void setDistributionResume(DistributionResume distributionResume) {
        this.distributionResume = distributionResume;
    }

    /**
     * 
     * @return
     *     The cleIdentification
     */
    @JsonProperty("cle_identification")
    public CleIdentification getCleIdentification() {
        return cleIdentification;
    }

    /**
     * 
     * @param cleIdentification
     *     The cle_identification
     */
    @JsonProperty("cle_identification")
    public void setCleIdentification(CleIdentification cleIdentification) {
        this.cleIdentification = cleIdentification;
    }

    /**
     * 
     * @return
     *     The correcteurs
     */
    @JsonProperty("correcteurs")
    public Correcteurs getCorrecteurs() {
        return correcteurs;
    }

    /**
     * 
     * @param correcteurs
     *     The correcteurs
     */
    @JsonProperty("correcteurs")
    public void setCorrecteurs(Correcteurs correcteurs) {
        this.correcteurs = correcteurs;
    }

    /**
     * 
     * @return
     *     The displayHomeDate
     */
    @JsonProperty("display_home_date")
    public DisplayHomeDate getDisplayHomeDate() {
        return displayHomeDate;
    }

    /**
     * 
     * @param displayHomeDate
     *     The display_home_date
     */
    @JsonProperty("display_home_date")
    public void setDisplayHomeDate(DisplayHomeDate displayHomeDate) {
        this.displayHomeDate = displayHomeDate;
    }

    /**
     * 
     * @return
     *     The distribution
     */
    @JsonProperty("distribution")
    public Distribution getDistribution() {
        return distribution;
    }

    /**
     * 
     * @param distribution
     *     The distribution
     */
    @JsonProperty("distribution")
    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
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
