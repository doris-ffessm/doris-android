package fr.ffessm.doris.prefetch.ezpublish;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

public class DorisAPI_JSONDATABindingHelper {

	public static boolean debug = true;
	public static boolean debug_SaveJSON = true;
	public static String DEBUG_SAVE_JSON_BASE_PATH = "target/json";
	public static String JSON_EXT = ".json";

	public static Log log = LogFactory.getLog(DorisAPI_JSONDATABindingHelper.class);
	
	/** Global instance of the JSON factory. */
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	public Credential credent;
	
	public DorisAPI_JSONDATABindingHelper(){
		this.credent = null;
	}

	public DorisAPI_JSONDATABindingHelper(Credential credent){
		this.credent = credent;
	}

	/**
	 * récupère un Specie à partir de son NodeId
	 * @param
	 * @param especeNodeId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Espece getEspeceFromNodeId(int especeNodeId) throws ClientProtocolException,
			IOException {

		/*
		DefaultHttpClient client = new DefaultHttpClient();
		String uri =DorisOAuth2ClientCredentials.SERVER_NODE_URL + especeNodeId;
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, uri);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, uri, DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"specie_" + especeNodeId+JSON_EXT);
			}
		}

		if(!DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		}
		HttpGet getCode = new HttpGet(uri);
		if(DorisAPIConnexionHelper.use_http_header_for_token){
			getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		HttpResponse response = client.execute(getCode);
		System.out.println(response.getStatusLine());
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Espece especeReponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Espece.class);
		System.out.println("\t Espece : " + especeReponse.getMetadata().getObjectName());
		for (Entry<String, Object> entry : especeReponse.getLinks().entrySet()) {
			System.out.println("\t\t" + entry.getKey() + "\t" + entry.getValue());
			//		printJSON(credent, entry.getValue().toString());
		}
		*/
		return null;
	}
	

	public Espece getEspeceFieldsFromNodeId(int especeNodeId) throws ClientProtocolException,
	IOException {
		log.debug("getSpecieFieldsFromNodeId - Début");
		log.debug("getSpecieFieldsFromNodeId - specieNodeId : " + especeNodeId);
		
		DefaultHttpClient client = new DefaultHttpClient();
		String uri = DorisOAuth2ClientCredentials.getServerNodeUrlTousLesChamps( String.valueOf(especeNodeId) );
		log.debug("getSpecieFieldsFromNodeId - uri : " + uri);
		
		if (credent != null && debug) {
			DorisAPIConnexionHelper.printJSON(credent, uri);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(
							credent,
							uri,
							DEBUG_SAVE_JSON_BASE_PATH + File.separatorChar+"specieFields_" + especeNodeId+JSON_EXT
						);
			}
		}

		if(credent != null && !DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		} else {
			uri = uri+"?oauth_token="+DorisOAuth2ClientCredentials.API_SUFFIXE;
		}

		log.debug("getSpecieFieldsFromNodeId - uri & oauth_token : " + uri);

		HttpGet getHttpPage = new HttpGet(uri);
		if(credent != null && DorisAPIConnexionHelper.use_http_header_for_token){
			getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		
		HttpResponse response = client.execute(getHttpPage);
		log.debug("getSpecieFieldsFromNodeId - response.getStatusLine() : "+response.getStatusLine());

		ObjectMapper objectMapper = new ObjectMapper();
		Espece espece = new Espece();

		try {
			espece = objectMapper.readValue(
					new InputStreamReader(response.getEntity().getContent()),
					Espece.class
				);
		}
		catch (JsonGenerationException e) {
		    e.printStackTrace();
		}
		catch (  JsonMappingException e) {
		    e.printStackTrace();
		}
		catch (  IOException e) {
		    e.printStackTrace();
		}

		System.out.println("\t Référence : " + espece.getFields().getReference().getValue() );
		System.out.println("\t Espece : " + espece.getFields().getEspece().getValue() );
		System.out.println("\t Genre : " + espece.getFields().getGenre().getValue() );
		System.out.println("\t Etat : " + espece.getFields().getState().getValue() );
		System.out.println("\t Images : " + espece.getFields().getImages().getValue() );

		return espece;
	}
	
	public void getImageList() throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.IMAGES_NODE_URL+"/list");
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.IMAGES_NODE_URL+"/list", DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"imageList"+JSON_EXT);
			}
		}
		
		// TODO finish
		
	}	
	
	public List<Image> getImageListForSpecieNodeId(int specieNodeId) throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.IMAGES_NODE_URL+"/list");
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.IMAGES_NODE_URL+"/list", DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"imageList"+JSON_EXT);
			}
		}
		
		// TODO finish
		return null;
	}
	
	public Image getImageFromImageId(int imageId) throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		String uri =DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId;
		
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId, DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"image_" + imageId+JSON_EXT);
			}
		}
		
		if(credent != null && !DorisAPIConnexionHelper.use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		} else {
			uri = uri+"?oauth_token="+DorisOAuth2ClientCredentials.API_SUFFIXE;
		}
		HttpGet getCode = new HttpGet(uri);
		if(credent != null && DorisAPIConnexionHelper.use_http_header_for_token){
			getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		
		HttpResponse response = client.execute(getCode);
		System.out.println("\t response : " + response.getStatusLine());

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Image imageResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Image.class);
		System.out.println("\t image path: " + imageResponse.getDataMap().getImage() );
		
		return imageResponse;
	}
	

}
