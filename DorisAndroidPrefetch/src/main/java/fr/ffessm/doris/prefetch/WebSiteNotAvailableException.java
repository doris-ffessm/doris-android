package fr.ffessm.doris.prefetch;

/**
 * Exception to track errors coming from the website
 * Typically 503 Service Temporarily Unavailable
 */
public class WebSiteNotAvailableException extends Exception {
    public String requestedURI;
    public int errorCode;
    public WebSiteNotAvailableException(String message, String uri, int errorCode){
        super(message);
        this.requestedURI = uri;
        this.errorCode = errorCode;
    }

}
