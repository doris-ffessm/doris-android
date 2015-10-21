package fr.ffessm.doris.prefetch.ezpublish;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import fr.ffessm.doris.prefetch.PrefetchGroupes;

public class DorisAPI_JSONTreeHelper {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(DorisAPI_JSONTreeHelper.class);
		
	public boolean debug = true;
	public boolean debug_SaveJSON = true;
	public static String DEBUG_SAVE_JSON_BASE_PATH = "target/json";
	public static String JSON_EXT = ".json";
	

	public Credential credent;
	
	public DorisAPI_JSONTreeHelper( Credential credent){
		this.credent = credent;
	}

	/** Global instance of the JSON factory. */
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Renvoie la liste des nodeId de toutes les fiches espèces
	 * @param credent
	 * @param speciesPerHttpRequest limite le nombre d'espèces requises à chaque requète http
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<Integer> getSpeciesNodeIds(int speciesPerHttpRequest) throws ClientProtocolException, IOException {
		List<Integer> result = new ArrayList<Integer>();
		
		DefaultHttpClient client = new DefaultHttpClient();
		String uri = DorisOAuth2ClientCredentials.SPECIES_NODE_URL + "/list/limit/"+speciesPerHttpRequest;
		if(!DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		}
		HttpGet getHttpPage = new HttpGet(uri);
		if(DorisAPIConnexionHelper.use_http_header_for_token){
			getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		if (debug) {
			System.out.println(uri);
			System.out.println(getHttpPage.getFirstHeader("Authorization"));
		}
		HttpResponse response = client.execute(getHttpPage);
		System.out.println(response.getStatusLine());
		

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new InputStreamReader(response.getEntity().getContent()));
		// pour debug
		System.out.println(objectMapper.writeValueAsString(rootNode));
		
		/*** read ***/
		JsonNode metadata = rootNode.path("metadata");
		int childrenCount = metadata.get("childrenCount").asInt();
		System.out.println("childrenCount="+childrenCount);
		
		JsonNode childrenNodes = rootNode.path("childrenNodes");
		for (Iterator<JsonNode> iterator = childrenNodes.elements(); iterator.hasNext();) {
			JsonNode specieNodeInList = (JsonNode) iterator.next();
			System.out.println(specieNodeInList.path("objectName").textValue() + " "+specieNodeInList.path("nodeId"));
			result.add(specieNodeInList.path("nodeId").asInt());
		}
		
		int offset=speciesPerHttpRequest;
		while(offset < childrenCount){
			getSpeciesNodeIds( offset, result, speciesPerHttpRequest);
			offset = offset+speciesPerHttpRequest;
		}

		System.out.println("retrieved NodeIds count="+result.size());
		
		return result;
	}
	
	/**
	 * Rempli la liste currentSpeciesIds avec les espèces à l'offset
	 * @param offset
	 * @param currentSpeciesIds
	 * @param speciesPerHttpRequest limite le nombre d'espèces requises à chaque requète http
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void getSpeciesNodeIds(int offset,  List<Integer> currentSpeciesIds, int speciesPerHttpRequest) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		String uri =DorisOAuth2ClientCredentials.SPECIES_NODE_URL + "/list/offset/"+offset+"/limit/"+speciesPerHttpRequest;
		if(!DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		}
		HttpGet getFirstPage = new HttpGet(uri);
		if(DorisAPIConnexionHelper.use_http_header_for_token){
			getFirstPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		HttpResponse response = client.execute(getFirstPage);
		System.out.println(response.getStatusLine());
		

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new InputStreamReader(response.getEntity().getContent()));
		
		JsonNode childrenNodes = rootNode.path("childrenNodes");
		for (Iterator<JsonNode> iterator = childrenNodes.elements(); iterator.hasNext();) {
			JsonNode specieNodeInList = (JsonNode) iterator.next();
			int nodeId = specieNodeInList.path("nodeId").asInt();
			System.out.println(specieNodeInList.path("objectName").textValue() + " "+nodeId);
			if(currentSpeciesIds.contains(nodeId)){
				System.err.println("already there !!! "+nodeId);
			}
			currentSpeciesIds.add(nodeId);
		}
	}
	
	
	// ne fonctionne pas : renvoie un "Acc\u00e8s refus\u00e9. Vous n'avez pas le droit d'acc\u00e9der \u00e0 cette zone."
	public int getSpecieDorisReferenceIdFromNodeId(int specieNodeId) throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.SERVER_NODE_FIELD_BASE_URL + specieNodeId+"/field/reference");
			if(debug_SaveJSON){
		//		DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId+"/fields", DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"specieFields_" + specieNodeId+JSON_EXT);
			}
		}
		
//		HttpGet getCode = new HttpGet(DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId+"/fields");
//		getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
//		
//		HttpResponse response = client.execute(getCode);
//		System.out.println(response.getStatusLine());
//		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
//		SpecieFields_JSONData specieFieldsResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), SpecieFields_JSONData.class);
//		System.out.println("\t Specie fields: " );
//		for (Entry<String, EZObject_JSONData> entry : specieFieldsResponse.getFields().entrySet()) {
//			System.out.println("\t\t\t" + entry.getKey() + "\t" + entry.getValue().getValue());
//		//		printJSON(credent, entry.getValue().toString());
//		}
		return 0;
	}
	
	
	/* 1er TEST Guillaume : La liste des GROUPES et des SURGROUPES est toujours vide ... :-/ */
	/**
	 * Renvoie la liste des nodeId de l'ensemble des groupes
	 * @param
	 * @return la liste des nodeId de l'ensemble des groupes
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<Integer> getGroupesNodeIds() throws ClientProtocolException, IOException {
		log.debug("getGroupesNodeIds()");
		
		List<Integer> result = new ArrayList<Integer>();
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		String uri = DorisOAuth2ClientCredentials.GROUPES_NODE_URL + "/list/limit/50";
		
		if(!DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		}
		
		HttpGet getHttpPage = new HttpGet(uri);
		if(DorisAPIConnexionHelper.use_http_header_for_token){
			getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		
		if (debug) {
			System.out.println(uri);
			System.out.println(getHttpPage.getFirstHeader("Authorization"));
		}
		
		HttpResponse response = client.execute(getHttpPage);
		log.debug("response.getStatusLine() : "+response.getStatusLine());

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new InputStreamReader(response.getEntity().getContent()));
		log.debug("rootNode : "+objectMapper.writeValueAsString(rootNode));
		
		/*** read ***/
		JsonNode metadata = rootNode.path("metadata");
		
		int childrenCount = metadata.get("childrenCount").asInt();
		log.debug("nb Groupes :"+childrenCount);
		
		
		JsonNode childrenNodes = rootNode.path("childrenNodes");
		for (Iterator<JsonNode> iterator = childrenNodes.elements(); iterator.hasNext();) {
			JsonNode specieNodeInList = (JsonNode) iterator.next();
			System.out.println(specieNodeInList.path("objectName").textValue() + " "+specieNodeInList.path("nodeId"));
			result.add(specieNodeInList.path("nodeId").asInt());
		}
		

		System.out.println("retrieved NodeIds count="+result.size());
		
		return result;
	}
	
	
	
	// Il faudra peut-être / probablement ensuite mutualiser dans une seule procédure avec paramètres toutes les récupération
	// Mais je vais au bout d'une pour comprendre l'ensemble
	/**
	 * Renvoie la liste des Termes du Glossaire
	 * @param
	 * @return Les Termes du Glossaire
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<Integer> getGlossaireNodeIds(int termesPerHttpRequest) throws ClientProtocolException, IOException {
		log.debug("getGlossaireNodeIds()");
		
		List<Integer> result = new ArrayList<Integer>();
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		String uri = DorisOAuth2ClientCredentials.GLOSSAIRE_NODE_URL + "/list/limit/"+termesPerHttpRequest;
		
		if(!DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		}
		
		HttpGet getHttpPage = new HttpGet(uri);
		if(DorisAPIConnexionHelper.use_http_header_for_token){
			getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		
		if (debug) {
			System.out.println(uri);
			System.out.println(getHttpPage.getFirstHeader("Authorization"));
		}
		
		HttpResponse response = client.execute(getHttpPage);
		log.debug("response.getStatusLine() : "+response.getStatusLine());

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new InputStreamReader(response.getEntity().getContent()));
		log.debug("noeud Glossaire : "+objectMapper.writeValueAsString(rootNode));
		
		/*** read ***/
		JsonNode metadata = rootNode.path("metadata");
		
		int childrenCount = metadata.get("childrenCount").asInt();
		log.debug("nb Termes :"+childrenCount);
		
		
		JsonNode childrenNodes = rootNode.path("childrenNodes");
		for (Iterator<JsonNode> iterator = childrenNodes.elements(); iterator.hasNext();) {
			JsonNode glossaireNodeInList = (JsonNode) iterator.next();
			
			log.debug("noeud terme : "+objectMapper.writeValueAsString(glossaireNodeInList));
			log.debug(glossaireNodeInList.path("objectName").textValue());
			
			result.add(glossaireNodeInList.path("nodeId").asInt());
		}
		log.debug("nb Termes :"+result.size());


		
		return result;
	}
}
