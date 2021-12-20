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

import fr.ffessm.doris.prefetch.WebSiteNotAvailableException;

public class DorisAPI_JSONTreeHelper {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(DorisAPI_JSONTreeHelper.class);
		
	public boolean debug = true;
	public boolean debug_SaveJSON = true;
	public static String DEBUG_SAVE_JSON_BASE_PATH = "target/json";
	public static String JSON_EXT = ".json";

	public Credential credent;
	protected DorisAPIHTTPHelper httpHelper;
	
	public DorisAPI_JSONTreeHelper(){
		httpHelper = new DorisAPIHTTPHelper(null);
	}

	public DorisAPI_JSONTreeHelper( Credential credent){
		this.credent = credent;
		this.httpHelper = new DorisAPIHTTPHelper(credent);
	}
	
	/** Global instance of the JSON factory. */
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Renvoie la liste des Fiches
	 * @param
	 * @returnLa liste des Fiches
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<ObjNameNodeId> getFichesNodeIds(int nbLimitRequest, int offset) throws ClientProtocolException, IOException, WebSiteNotAvailableException {
        log.debug(String.format("getIntervenantsNodeIds(nbLimitRequest=%d, offset=%d)",  nbLimitRequest, offset ));
			
		return getNodeIdsFromNodeUrl(DorisOAuth2ClientCredentials.SPECIES_NODE_URL, nbLimitRequest, offset);
	}

    /**
     * Renvoie la liste des Intervenants
     * @param
     * @returnLa liste des Intervenants
     * @throws ClientProtocolException
     * @throws IOException
     */
    public List<ObjNameNodeId> getIntervenantsNodeIds(int nbLimitRequest, int offset) throws ClientProtocolException, IOException, WebSiteNotAvailableException {
        log.debug(String.format("getIntervenantsNodeIds(nbLimitRequest=%d, offset=%d)",  nbLimitRequest, offset ));

        return getNodeIdsFromNodeUrl(DorisOAuth2ClientCredentials.PARTICIPANTS_NODE_URL, nbLimitRequest, offset);
    }

    /**
     * Renvoie la liste des Termes du Glossaire
     * @param
     * @returnLa liste des Termes du Glossaire
     * @throws ClientProtocolException
     * @throws IOException
     */
    public List<ObjNameNodeId> getTermesNodeIds(int nbLimitRequest, int offset) throws ClientProtocolException, IOException, WebSiteNotAvailableException {
        log.debug(String.format("getBibliographieNodeIds(nbLimitRequest=%d, offset=%d)",  nbLimitRequest, offset ));

        return getNodeIdsFromNodeUrl(DorisOAuth2ClientCredentials.GLOSSAIRE_NODE_URL, nbLimitRequest, offset);
    }

    /**
     * Renvoie la liste des Oeuvres de la Bibliographie
     * @param
     * @returnLa liste des Oeuvres de la Bibliographie
     * @throws ClientProtocolException
     * @throws IOException
     */
    public List<ObjNameNodeId> getBibliographieNodeIds(int nbLimitRequest, int offset) throws ClientProtocolException, IOException, WebSiteNotAvailableException {
        log.debug(String.format("getBibliographieNodeIds(nbLimitRequest=%d, offset=%d)",  nbLimitRequest, offset ));

        return getNodeIdsFromNodeUrl(DorisOAuth2ClientCredentials.BIBLIO_NODE_URL, nbLimitRequest, offset);
    }

    /**
     * Renvoie la Classification
     * @param
     * @returnLa Classification
     * @throws ClientProtocolException
     * @throws IOException
     */
    public List<ObjNameNodeId> getClassificationNodeIds(int nbLimitRequest, int offset) throws ClientProtocolException, IOException, WebSiteNotAvailableException {
        log.debug(String.format("getClassificationNodeIds(nbLimitRequest=%d, offset=%d)",  nbLimitRequest, offset ));

        return getNodeIdsFromNodeUrl(DorisOAuth2ClientCredentials.TAXONS_NODE_URL, nbLimitRequest, offset);
    }

	/**
	 * Renvoie les id du NODE_URL passé en paramètre
	 * @param
	 * @return Les id du NODE_URL passé en paramètre
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List<ObjNameNodeId> getNodeIdsFromNodeUrl(String nodeUrl, int nbLimitRequest, int offset) throws ClientProtocolException, IOException, WebSiteNotAvailableException {
		log.debug(String.format("getNodeIdsFromNodeUrl(nodeUrl=\"%s\", nbLimitRequest=%d, offset=%d)", nodeUrl, nbLimitRequest, offset ));

		DefaultHttpClient client = new DefaultHttpClient();
		
		String uri = nodeUrl + "/list";
		if (offset != 0) uri += "/offset/"+offset;
		uri += "/limit/"+nbLimitRequest;
		log.debug("uri : "+uri.toString());

		HttpResponse response = httpHelper.getHttpResponse(uri);


		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(new InputStreamReader(response.getEntity().getContent()));
		log.debug("node : "+objectMapper.writeValueAsString(rootNode));

		/*** read ***/
		JsonNode metadata = rootNode.path("metadata");
		
		int childrenCount = metadata.get("childrenCount").asInt();
		log.debug("nb total nodes :"+childrenCount);

        List<ObjNameNodeId> result = new ArrayList<ObjNameNodeId>();

		JsonNode childrenNodes = rootNode.path("childrenNodes");
		for (Iterator<JsonNode> iterator = childrenNodes.elements(); iterator.hasNext();) {
			JsonNode nodeInList = (JsonNode) iterator.next();
			
			log.debug("node value : "+objectMapper.writeValueAsString(nodeInList));

			result.add(new ObjNameNodeId(nodeInList.path("nodeId").asInt(),nodeInList.path("objectId").asInt(),nodeInList.path("objectName").asText(), nodeInList.path("dateModified").asInt()));
		}
		log.debug("nb retreived nodes :"+result.size());
	
		return result;
	}

}
