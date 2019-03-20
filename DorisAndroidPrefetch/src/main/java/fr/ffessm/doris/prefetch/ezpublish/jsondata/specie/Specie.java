package fr.ffessm.doris.prefetch.ezpublish.jsondata.specie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Specie {

	// {"metadata":{"objectName":"Cerianthus membranaceus","classIdentifier":"specie","datePublished":1158395412,"dateModified":1398933012,"objectRemoteId":"0d3e50ff25c40e62fa510cb6f50da3e2","objectId":75135,"nodeId":72613,"nodeRemoteId":"2143ff311f73a73d233fa9035c171f33","fullUrl":"http:\/\/doris.donatello.io\/Especes\/Grand-cerianthe4"},
	//  "links":{"publication_date":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/publication_date","chantier_date":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/chantier_date","verificateurs":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/verificateurs","contributors":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/contributors","discoverer":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/discoverer","display_home":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/display_home","state":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/state","nom_commun_fr":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/nom_commun_fr","nom_commun_inter":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/nom_commun_inter","others_nom_commun_fr":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/others_nom_commun_fr","others_name_scientific":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/others_name_scientific","french_name_origin":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/french_name_origin","scientific_name_origin":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/scientific_name_origin","biotop":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/biotop","description":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/description","look_likes":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/look_likes","reglementation":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/reglementation","alimentation":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/alimentation","reproduction":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/reproduction","associated_life":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/associated_life","bio_divers":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/bio_divers","biblio_ref":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/biblio_ref","others_biblio_ref":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/others_biblio_ref","links":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/links","preformat_links":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/preformat_links","zone_geo":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/zone_geo","danger":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/danger","complementary_infos":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/complementary_infos","export_lines":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/export_lines","images":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/images","group":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/group","embranchement_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/embranchement_taxon","sous_embranchement_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/sous_embranchement_taxon","super_classe_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/super_classe_taxon","classe_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/classe_taxon","sous_classe_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/sous_classe_taxon","super_ordre_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/super_ordre_taxon","ordre_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/ordre_taxon","sous_ordre_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/sous_ordre_taxon","famille_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/famille_taxon","sous_famille_taxon":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/sous_famille_taxon","genre":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/genre","espece":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/espece","principal_writer":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/principal_writer","doridiens":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/doridiens","reference":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/reference","distribution_resume":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/distribution_resume","cle_identification":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/cle_identification","correcteurs":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/correcteurs","display_home_date":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/display_home_date","distribution":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/field\/distribution","*":"http:\/\/doris.donatello.io\/api\/ezp\/content\/node\/72613\/fields"},
	//  "requestedResponseGroups":["Metadata"]}

	private Specie_Metadata metadata;
	private Map<String, Object> links = new HashMap<String, Object>();
	private List<String> requestedResponseGroups = new ArrayList<String>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public Specie_Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Specie_Metadata metadata) {
		this.metadata = metadata;
	}
	public Map<String, Object> getLinks() {
		return links;
	}
	public void setLinks(Map<String, Object> links) {
		this.links = links;
	}
	public List<String> getRequestedResponseGroups() {
		return requestedResponseGroups;
	}
	public void setRequestedResponseGroups(List<String> requestedResponseGroups) {
		this.requestedResponseGroups = requestedResponseGroups;
	}
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}
