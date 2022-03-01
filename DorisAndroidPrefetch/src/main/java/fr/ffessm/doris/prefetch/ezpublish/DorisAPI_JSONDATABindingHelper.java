package fr.ffessm.doris.prefetch.ezpublish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
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
	protected DorisAPIHTTPHelper httpHelper;
	
	public DorisAPI_JSONDATABindingHelper(){
		this.credent = null;
        httpHelper = new DorisAPIHTTPHelper(null);
	}

	public DorisAPI_JSONDATABindingHelper(Credential credent){
		this.credent = credent;
        httpHelper = new DorisAPIHTTPHelper(credent);
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

        log.debug(String.format("%d - %s - %s %s - %.256s", // limit description (it may contain image binaries !)
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

        log.debug(String.format("\n\tRéférence : %s\n\tTitre : %s\n\tDéfinition : %s",
                glossaire.getFields().getReference().getValue(),
                glossaire.getFields().getTitle().getValue(),
                glossaire.getFields().getDefinition().getValue()));

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

        log.debug(String.format("\n\tRéférence : %s\n\tTitre : %s",
                oeuvre.getFields().getReference().getValue(),
                oeuvre.getFields().getTitle().getValue()));

        return oeuvre;
    }

    public Espece getEspeceFieldsFromNodeId(int especeNodeId) throws IOException, WebSiteNotAvailableException {
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

        HttpResponse response = httpHelper.getHttpResponse( uri);

        ObjectMapper objectMapper = new ObjectMapper();
        Espece espece = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Espece.class
            );

        log.debug(String.format("\n\tRéférence : %s\n\tEspèce : %s",
                espece.getFields().getReference().getValue(),
                espece.getFields().getEspece().getValue()));

        return espece;
    }

    public Classification getClassificationFieldsFromObjectId(int classificationObjectId) throws IOException,
            WebSiteNotAvailableException {
        //log.debug("getClassificationFieldsFromObjectId - classificationObjectId : " + classificationObjectId);

        try {
            HttpResponse response = getFieldsFromObjectId(classificationObjectId);

            if (response == null) {
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Classification classificationJSON = objectMapper.readValue(
                    new InputStreamReader(response.getEntity().getContent()),
                    Classification.class
            );

            return classificationJSON;
        } catch (WebSiteNotAvailableException e) {
            if(e.errorCode == 500) {
                String uri = DorisOAuth2ClientCredentials.getServerObjectUrlTousLesChamps( String.valueOf(classificationObjectId) );
                log.warn(String.format("Ignoring invalid classification %d on the server %s", classificationObjectId, uri), e);
            } else throw e;
        }
        return null;
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
        return httpHelper.getHttpResponse( uri);
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

        return httpHelper.getHttpResponse( uri);
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

        HttpResponse response = httpHelper.getHttpResponse( uri);

		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		Image imageResponse = mapper.readValue(new InputStreamReader(response.getEntity().getContent()), Image.class);
        log.debug(String.format("%s\n\timage path:%s", uri, imageResponse.getDataMap().getImage()));
		
		return imageResponse;
	}

}
