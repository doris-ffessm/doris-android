package fr.ffessm.doris.prefetch.ezpublish;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class DorisAPIConnexionHelper {

	public static boolean debug = true;
	public static boolean debug_SaveJSON = true;
	/** permet de basculer entre le mode http_header et paramètre pour passer le access_token dans la requète */
	public static boolean use_http_header_for_token = false; 
	public static String DEBUG_SAVE_JSON_BASE_PATH = "target";
	public static String JSON_EXT = ".json";

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/** Global instance of the JSON factory. */
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Get the Doris credential for the given user
	 * uses a local server and opens a web page so the user can enter his login/password
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static Credential authorizeViaWebPage(String userId) throws Exception {

		// set up authorization code flow
		AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(), HTTP_TRANSPORT,
				JSON_FACTORY, new GenericUrl(DorisOAuth2ClientCredentials.TOKEN_SERVER_URL), new ClientParametersAuthentication(
						DorisOAuth2ClientCredentials.API_KEY, DorisOAuth2ClientCredentials.API_SECRET),
				DorisOAuth2ClientCredentials.API_KEY, DorisOAuth2ClientCredentials.AUTHORIZATION_SERVER_URL)/* setScopes(Arrays.asList(SCOPE)).setDataStoreFactory(DATA_STORE_FACTORY)
																											 */.build();
		// authorize
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(DorisOAuth2ClientCredentials.DOMAIN)
				.setPort(DorisOAuth2ClientCredentials.PORT).build();

		return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId);
	}

		
	public static void printJSON(Credential credent, String url) throws ClientProtocolException, IOException{
		System.out.println(url);
		DefaultHttpClient client = new DefaultHttpClient();
		String uri = url;
		if(credent != null && !use_http_header_for_token){
			uri = uri+"?oauth_token="+credent.getAccessToken();
		}
		HttpGet getCode = new HttpGet(uri);
		getCode.addHeader("User-Agent","Doris-Android-prefecth");
		if(use_http_header_for_token){
			getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}
		HttpResponse response = client.execute(getCode);
		//System.out.println(response.getStatusLine());
		BufferedReader rd2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line2 = "";
		while ((line2 = rd2.readLine()) != null) {
			System.out.println("\t " + line2);
		}
	}
	
	
	public static void saveJSONFile(Credential credent, String url, String filePath) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet getCode = new HttpGet(url);
		getCode.addHeader("User-Agent","Doris-Android-prefecth");
		if (credent != null) {
			getCode.addHeader("Authorization", "OAuth " + credent.getAccessToken());
		}

		HttpResponse response = client.execute(getCode);
		
		
		File f = new File(filePath);
		if (!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		
		FileWriter fstream = new FileWriter(filePath);
        BufferedWriter fbw = new BufferedWriter(fstream);
		BufferedReader rd2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				 String line2 = "";
		while ((line2 = rd2.readLine()) != null) {
			fbw.write(line2);
		}
		fbw.close();
	}
}
