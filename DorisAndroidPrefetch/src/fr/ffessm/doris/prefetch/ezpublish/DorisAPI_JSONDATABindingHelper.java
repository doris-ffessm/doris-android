package fr.ffessm.doris.prefetch.ezpublish;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.specie.Specie;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.specie_fields.SpecieFields;

public class DorisAPI_JSONDATABindingHelper {

	public static boolean debug = true;
	public static boolean debug_SaveJSON = true;
	public static String DEBUG_SAVE_JSON_BASE_PATH = "target/json";
	public static String JSON_EXT = ".json";


	/** Global instance of the JSON factory. */
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	public Credential credent;
	
	public DorisAPI_JSONDATABindingHelper(Credential credent){
		this.credent = credent;
	}

	/**
	 * récupère un Specie à partir de son NodeId
	 * @param credent
	 * @param specieNodeId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Specie getSpecieFromNodeId(int specieNodeId) throws ClientProtocolException,
			IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		String uri =DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId;
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, uri);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, uri, DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"specie_" + specieNodeId+JSON_EXT);
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
		Specie specieResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Specie.class);
		System.out.println("\t Specie: " + specieResponse.getMetadata().getObjectName());
		for (Entry<String, Object> entry : specieResponse.getLinks().entrySet()) {
			System.out.println("\t\t" + entry.getKey() + "\t" + entry.getValue());
	//		printJSON(credent, entry.getValue().toString());
		}
		return specieResponse;
	}
	
	public SpecieFields getSpecieFieldsFromNodeId(int specieNodeId) throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		String uri =DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId+"/fields";
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, uri);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, uri, DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"specieFields_" + specieNodeId+JSON_EXT);
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
		SpecieFields specieFieldsResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), SpecieFields.class);
		//System.out.println("\t Specie fields: " );
		//for (Entry<String, EZObject_JSONData> entry : specieFieldsResponse.getFields().entrySet()) {
		//	System.out.println("\t\t\t" + entry.getKey() + "\t" + entry.getValue().getValue());
		//		printJSON(credent, entry.getValue().toString());
		//}
		return specieFieldsResponse;
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
		Image imageResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Image.class);
		System.out.println("\t image path: " + imageResponse.getDataMap().getImage() );
		
		return imageResponse;
		
		
	}	
	

}
