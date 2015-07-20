package fr.ffessm.doris.prefetch.ezpublish;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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


	
	
//	public static void getSpeciesList_full_data_binding_version(Credential credent) throws ClientProtocolException, IOException {
//		DefaultHttpClient client = new DefaultHttpClient();
//		
//		if (debug) {
//			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.SPECIES_NODE_URL + "/list");
//			if(debug_SaveJSON){
//				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.SPECIES_NODE_URL + "/list", DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"speciesList" +JSON_EXT);
//			}
//		}
//		
//		HttpGet getCode = new HttpGet(DorisOAuth2ClientCredentials.SPECIES_NODE_URL + "/list");
//		getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
//
//		HttpResponse response = client.execute(getCode);
//		System.out.println(response.getStatusLine());
//
//		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
//		SpeciesList_JSONData speciesListResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()),
//				SpeciesList_JSONData.class);
//
//		for (Specie_Metadata specie : speciesListResponse.getChildrenNodes()) {
//			System.out.println("\t" + specie.getObjectName() + " nodeId=" + specie.getNodeId());
//		}
//
//	}

	

	public static Specie getSpecieFromNodeId(Credential credent, int specieNodeId) throws ClientProtocolException,
			IOException {

		DefaultHttpClient client = new DefaultHttpClient();

		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId, DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"specie_" + specieNodeId+JSON_EXT);
			}
		}

		HttpGet getCode = new HttpGet(DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId);
		getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());

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
	
	public static SpecieFields getSpecieFieldsFromNodeId(Credential credent, int specieNodeId) throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId+"/fields");
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId+"/fields", DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"specieFields_" + specieNodeId+JSON_EXT);
			}
		}
		
		HttpGet getCode = new HttpGet(DorisOAuth2ClientCredentials.SERVER_NODE_URL + specieNodeId+"/fields");
		getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		
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
	
	
	public static void getImageList(Credential credent) throws ClientProtocolException,
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
	
	public static Image getImageFromImageId(Credential credent, int imageId) throws ClientProtocolException,
	IOException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (debug) {
			DorisAPIConnexionHelper.printJSON(credent, DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId);
			if(debug_SaveJSON){
				DorisAPIConnexionHelper.saveJSONFile(credent, DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId, DEBUG_SAVE_JSON_BASE_PATH+ File.separatorChar+"image_" + imageId+JSON_EXT);
			}
		}
		

		HttpGet getCode = new HttpGet(DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId);
		getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		
		HttpResponse response = client.execute(getCode);
		System.out.println(response.getStatusLine());
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Image imageResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Image.class);
		System.out.println("\t image path: " + imageResponse.getDataMap().getImage() );
		
		return imageResponse;
		
		
	}	
	

}
