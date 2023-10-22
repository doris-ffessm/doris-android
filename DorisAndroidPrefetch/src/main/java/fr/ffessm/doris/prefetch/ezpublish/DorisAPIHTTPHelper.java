package fr.ffessm.doris.prefetch.ezpublish;

import com.google.api.client.auth.oauth2.Credential;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import fr.ffessm.doris.prefetch.WebSiteNotAvailableException;

public class DorisAPIHTTPHelper {


    public static Log log = LogFactory.getLog(DorisAPIHTTPHelper.class);
    public Credential credent;


    /** used to count number of call in the prefetch application */
    public static long nbHttpCall = 0;

    public DorisAPIHTTPHelper(Credential credent) {
        this.credent = credent;
    }

    public HttpResponse getHttpResponse(String uri) throws IOException, WebSiteNotAvailableException {


        if (credent != null && !DorisAPIConnexionHelper.use_http_header_for_token) {
            uri = uri + "?oauth_token=" + credent.getAccessToken();
        } else {
            uri = uri + "?oauth_token=" + DorisOAuth2ClientCredentials.API_ACCESSTOKEN;
        }

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getHttpPage = new HttpGet(uri);
        getHttpPage.addHeader("User-Agent","Doris-Android-prefecth");
        if (credent != null && DorisAPIConnexionHelper.use_http_header_for_token) {
            getHttpPage.addHeader("Authorization", "OAuth " + credent.getAccessToken());
        } else {
            getHttpPage.addHeader("Authorization", "OAuth " + DorisOAuth2ClientCredentials.API_ACCESSTOKEN);
        }

        HttpResponse response;
        int nbTries = 0;
        boolean shouldRetry = true;
        do {
            response = client.execute(getHttpPage);
            nbHttpCall++;
            if (response.getStatusLine().getStatusCode() != 200 && nbTries < 5) {

                nbTries++;
                log.info("full uri : " + uri);
                response.getEntity().consumeContent();
                log.warn(String.format("%s : Retrying after 20s", response.getStatusLine()));
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            } else {
                shouldRetry = false;
            }

        } while (shouldRetry);

        if (response.getStatusLine().getStatusCode() != 200) {
            log.debug("full uri : " + uri);
            log.warn(String.format("%s : nbTries=%d", response.getStatusLine(), nbTries));
            WebSiteNotAvailableException e = new WebSiteNotAvailableException(response.getStatusLine().toString(), uri, response.getStatusLine().getStatusCode());
            response.getEntity().consumeContent();
            throw e;
        }


        return response;
    }

    public  static long getNbHttpCall() {
        return nbHttpCall;
    }
}
