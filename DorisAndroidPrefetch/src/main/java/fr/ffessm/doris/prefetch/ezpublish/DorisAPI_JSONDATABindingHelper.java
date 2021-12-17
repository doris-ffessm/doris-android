package fr.ffessm.doris.prefetch.ezpublish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.ffessm.doris.prefetch.WebSiteNotAvailableException;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.bibliographie.Bibliographie;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.classification.Classification;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.glossaire.Glossaire;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.groupe.Groupe;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.utilisateur.Utilisateur;

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

    public Utilisateur getUtilisateurFieldsFromNodeId(int participantNodeId) throws IOException, WebSiteNotAvailableException {
        log.debug(String.format("getUtilisateurFieldsFromNodeId(participantNodeId=%s)",participantNodeId));

        HttpResponse response = getFieldsFromNodeId(participantNodeId);

        if ( response == null )  {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Utilisateur utilisateur = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Utilisateur.class
            );

        log.debug(String.format("%d - %s - %s %s - %s",
                participantNodeId,
                utilisateur.getFields().getReference().getValue(),
                utilisateur.getFields().getFirstName().getValue(),
                utilisateur.getFields().getLastName().getValue(),
                utilisateur.getFields().getDescription().getValue()
        ));

        return utilisateur;
    }

    public Glossaire getTermeFieldsFromNodeId(int termeNodeId) throws IOException, WebSiteNotAvailableException {
        log.debug("getTermeFieldsFromNodeId - termeNodeId : " + termeNodeId);

        HttpResponse response = getFieldsFromNodeId(termeNodeId);

        if ( response == null )  {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Glossaire glossaire = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Glossaire.class
            );


        System.out.println("\t Référence : " + glossaire.getFields().getReference().getValue() );
        System.out.println("\t Titre : " + glossaire.getFields().getTitle().getValue() );
        System.out.println("\t Définition : " + glossaire.getFields().getDefinition().getValue() );

        return glossaire;
    }

    public Bibliographie getOeuvreFieldsFromNodeId(int termeNodeId) throws IOException, WebSiteNotAvailableException {
        log.debug("getTermeFieldsFromNodeId - termeNodeId : " + termeNodeId);

        HttpResponse response = getFieldsFromNodeId(termeNodeId);

        if ( response == null )  {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Bibliographie oeuvre = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Bibliographie.class
            );

        System.out.println("\t Référence : " + oeuvre.getFields().getReference().getValue() );
        System.out.println("\t Titre : " + oeuvre.getFields().getTitle().getValue() );

        return oeuvre;
    }

    public Espece getEspeceFieldsFromNodeId(int especeNodeId) throws IOException, WebSiteNotAvailableException {
        log.debug("getSpecieFieldsFromNodeId - specieNodeId : " + especeNodeId);

        DefaultHttpClient client = new DefaultHttpClient();
        String uri = DorisOAuth2ClientCredentials.getServerNodeUrlTousLesChamps( String.valueOf(especeNodeId) );
        //log.debug("getSpecieFieldsFromNodeId - uri : " + uri);

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

        if (credent != null && !DorisAPIConnexionHelper.use_http_header_for_token){
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
        //log.debug("getSpecieFieldsFromNodeId - response.getStatusLine() : "+response.getStatusLine());

        if ( response.getStatusLine().getStatusCode() != 200 )  {
            throw new WebSiteNotAvailableException(response.getStatusLine().toString(), uri, response.getStatusLine().getStatusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Espece espece = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Espece.class
            );

        System.out.println("\t Référence : " + espece.getFields().getReference().getValue() );
        System.out.println("\t Espece : " + espece.getFields().getEspece().getValue() );
        //System.out.println("\t Genre : " + espece.getFields().getGenre().getValue() );
        //System.out.println("\t Etat : " + espece.getFields().getState().getValue() );
        //System.out.println("\t Images : " + espece.getFields().getImages().getValue() );

        return espece;
    }

    public Classification getClassificationFieldsFromObjectId(int classificationObjectId) throws IOException,
            WebSiteNotAvailableException {
        log.debug("getClassificationFieldsFromObjectId - classificationObjectId : " + classificationObjectId);

        HttpResponse response = getFieldsFromObjectId(classificationObjectId);

        if ( response == null )  {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Classification classificationJSON = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Classification.class
            );

        //System.out.println("\t Nom Latin : " + classificationJSON.getDataMap().getNameLatin() );
        //System.out.println("\t Nom Francais : " + classificationJSON.getDataMap().getNameFrench());
        //System.out.println("\t Description : " + classificationJSON.getDataMap().getDescription() );

        return classificationJSON;
    }

    public Groupe getGroupeFieldsFromObjectId(int groupeObjectId) throws IOException, WebSiteNotAvailableException {
        log.debug("getGroupeFieldsFromObjectId - groupeObjectId : " + groupeObjectId);

        HttpResponse response = getFieldsFromObjectId(groupeObjectId);

        if ( response == null )  {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Groupe groupeJSON = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Groupe.class
            );

        //System.out.println("\t Nom Latin : " + classificationJSON.getDataMap().getNameLatin() );
        //System.out.println("\t Nom Francais : " + classificationJSON.getDataMap().getNameFrench());
        //System.out.println("\t Description : " + classificationJSON.getDataMap().getDescription() );

        return groupeJSON;
    }

    public HttpResponse getFieldsFromNodeId(int nodeId) throws IOException, WebSiteNotAvailableException {
        log.debug("getFieldsFromNodeId - nodeId : " + nodeId);

        DefaultHttpClient client = new DefaultHttpClient();
        String uri = DorisOAuth2ClientCredentials.getServerNodeUrlTousLesChamps( String.valueOf(nodeId) );
        log.debug("getFieldsFromNodeId - uri : " + uri);

        if (credent != null && debug) {
            DorisAPIConnexionHelper.printJSON(credent, uri);
            if(debug_SaveJSON){
                DorisAPIConnexionHelper.saveJSONFile(
                        credent,
                        uri,
                        DEBUG_SAVE_JSON_BASE_PATH + File.separatorChar+"specieFields_" + nodeId+JSON_EXT
                );
            }
        }

        if(credent != null && !DorisAPIConnexionHelper.use_http_header_for_token){
            uri = uri+"?oauth_token="+credent.getAccessToken();
        } else {
            uri = uri+"?oauth_token="+DorisOAuth2ClientCredentials.API_SUFFIXE;
        }

        //log.debug("getFieldsFromNodeId - uri & oauth_token : " + uri);

        HttpGet getHttpPage = new HttpGet(uri);
        if(credent != null && DorisAPIConnexionHelper.use_http_header_for_token){
            getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
        }

        HttpResponse response = client.execute(getHttpPage);
        //log.debug("getFieldsFromNodeId - response.getStatusLine() : "+response.getStatusLine());

        if ( response.getStatusLine().getStatusCode() != 200 )  {
            log.warn("getFieldsFromNodeId - response.getStatusLine() : "+response.getStatusLine());
            throw new WebSiteNotAvailableException(response.getStatusLine().toString(), uri, response.getStatusLine().getStatusCode());
        }

        return response;
    }

    public HttpResponse getFieldsFromObjectId(int objectId) throws IOException,
            WebSiteNotAvailableException {
        //log.debug("getFieldsFromObjectId - Début");
        //log.debug("getFieldsFromObjectId - objectId : " + objectId);

        DefaultHttpClient client = new DefaultHttpClient();
        String uri = DorisOAuth2ClientCredentials.getServerObjectUrlTousLesChamps( String.valueOf(objectId) );
        //log.debug("getFieldsFromObjectId - uri : " + uri);

        if (credent != null && debug) {
            DorisAPIConnexionHelper.printJSON(credent, uri);
            if(debug_SaveJSON){
                DorisAPIConnexionHelper.saveJSONFile(
                        credent,
                        uri,
                        DEBUG_SAVE_JSON_BASE_PATH + File.separatorChar+"specieFields_" + objectId+JSON_EXT
                );
            }
        }

        if(credent != null && !DorisAPIConnexionHelper.use_http_header_for_token){
            uri = uri+"?oauth_token="+credent.getAccessToken();
        } else {
            uri = uri+"?oauth_token="+DorisOAuth2ClientCredentials.API_SUFFIXE;
        }

        //log.debug("getFieldsFromObjectId - uri & oauth_token : " + uri);

        HttpGet getHttpPage = new HttpGet(uri);
        if(credent != null && DorisAPIConnexionHelper.use_http_header_for_token){
            getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
        }

        HttpResponse response = client.execute(getHttpPage);
        //log.debug("getFieldsFromObjectId - response.getStatusLine() : "+response.getStatusLine());

        if ( response.getStatusLine().getStatusCode() != 200 )  {
            log.warn(String.format("%s returned %s", uri, response.getStatusLine()));
            throw new WebSiteNotAvailableException(response.getStatusLine().toString(), uri, response.getStatusLine().getStatusCode());
        }

        return response;
    }

    public Image getImageFromImageId(int imageId, int retry) throws IOException, WebSiteNotAvailableException {
	    int nbTries = 0;
        WebSiteNotAvailableException lastException = null;
	    while(nbTries <= retry) {
            try {
                return getImageFromImageId(imageId);
            } catch (WebSiteNotAvailableException e) {
                lastException = e;
                nbTries++;
                if(nbTries <= retry) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                log.warn(String.format("nbTries=%d ", nbTries));
            }
        }
	    if(lastException != null) throw lastException;
	    else return null;
    }

    public Image getImageFromImageId(int imageId) throws IOException, WebSiteNotAvailableException {

		DefaultHttpClient client = new DefaultHttpClient();
		
		String uri =DorisOAuth2ClientCredentials.SERVER_OBJECT_URL + imageId;

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

		if ( response.getStatusLine().getStatusCode() != 200 )  {
            log.warn(String.format("%s returned %s", uri, response.getStatusLine()));
            throw new WebSiteNotAvailableException(response.getStatusLine().toString(), uri, response.getStatusLine().getStatusCode());
        }

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Image imageResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Image.class);
        log.debug(String.format("%s\n\timage path:%s", uri, imageResponse.getDataMap().getImage()));
		
		return imageResponse;
	}

}
