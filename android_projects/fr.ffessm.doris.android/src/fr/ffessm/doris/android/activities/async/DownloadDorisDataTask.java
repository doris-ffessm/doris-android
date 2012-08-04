package fr.ffessm.doris.android.activities.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadDorisDataTask  extends AsyncTask<String,Integer, Integer>{
	private static final String LOG_TAG = DownloadDorisDataTask.class.getCanonicalName();
	
	
    private NotificationHelper mNotificationHelper;
    public DownloadDorisDataTask(Context context){
        mNotificationHelper = new NotificationHelper(context);
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected Integer doInBackground(String... arg0) {
    	
    	// get first page
    	try {
			processFirstPage("http://doris.ffessm.fr/fiches_liste_recherche.asp?nomcommun=e");
		} catch (IOException e1) {
			Log.e(LOG_TAG, e1.toString(), e1);
		}
    	
    	
    	
    	
        //This is where we would do the actual download stuff
        //for now I'm just going to loop for 10 seconds
        // publishing progress every second
        for (int i=10;i<=100;i += 10)
            {
                try {
                    Thread.sleep(1000);
                    publishProgress(i);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        return 100;
    }
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }
    protected void onPostExecute(Void result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
    }

    
    protected String getPageContent(String urlString) throws IOException{
    	StringBuilder sb = new StringBuilder();
    	URL url = new URL(urlString);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
        
        try{
	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	            sb.append(inputLine);
	        }
        finally{
        	in.close();
        }
        // convert to UTF8 if necessary
        // TODO
        //charset=iso-8859-1
        //sb.toString().
        return sb.toString();
    }
    
    protected void processFirstPage(String urlString) throws IOException{
    	String content = getPageContent(urlString);
    	Log.d(LOG_TAG, "retrieved page "+urlString);
    	Log.d(LOG_TAG, content);
    	Integer maxNbPages = getNbPages(content);
    	mNotificationHelper.setMaxNbPages(maxNbPages.toString());
    	
    	processCardSummariesInPage(content);
    	
    }
    
    protected void processCardSummariesInPage(String pageContent){
 /*   	String patternString =
"<table width=\"196\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#F3F3F3\">" +
"\\W*<tr>"+
"\\W*<td width=\"5\" >&nbsp;</td>"+
"\\W*<td height=\"110\" align=\"center\" valign=\"bottom\" >"+
"\\W*<a href=\"photo_gde_taille3.asp?temp=0&nomcommunte=&nomcommunpr=&nomcommunen=&allcheck=&checkvar=&touslesmotste=&touslesmotsen=&touslesmotspr=&nomcommun=e&touslesmots=&PageCourante=1&varposition=1&fiche_numero=2676&origine=recherche&fiche_etat=5\">"+
"\\W*<img src=\"(\\S+)\" height=\"105\" border=\"0\" >\\W</a>"+ // url photo principale
"\\W*<td width=\"5\" >&nbsp;</td>"+
"\\W*</tr>"+
"\\W*<tr>"+
"\\W*<td >&nbsp;</td>"+
"\\W*<td height=\"29\"  align=\"center\"  class=\"normal_noir_gras\"><em>(\\w*)</em></td>"+ // nom scientifique
"\\W*<td >&nbsp;</td>"+
"\\W*</tr>"+
"\\W*<tr>"+
"\\W*<td >&nbsp;</td>"+
"\\W*<td height=\"29\"  align=\"center\"  class=\"gris_gras\">(\\w*)</td>"+ //  nom commun
"\\W*<td >&nbsp;</td>"+
"\\W*</tr>"+
"\\W*<tr bgcolor=\"#FAEDC0\">"+
"\\W*<td >&nbsp;</td>"+
"\\W*<td  align=\"center\"  class=\"normalgris\">"+
"\\W*<a href=\"fiche3.asp?\\S+&fiche_numero=(\\d*)\\S+&fiche_etat=5\"class=\"normalgris\"><img src=\"images/fiche.gif\" width=\"18\" height=\"18\" border=\"0\" align=\"absmiddle\">.*</a>"+
"\\W*</td>"+
"\\W*<td >&nbsp;</td>"+
"\\W*</tr>"+
"\\W*</table>";    	*/
/*
<table width="196" height="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="#F3F3F3">
                      <tr>
                        <td width="5" >&nbsp;</td>
                        <td height="110" align="center" valign="bottom" >
						
                         

                        
                        <a href="photo_gde_taille3.asp?temp=0&nomcommunte=&nomcommunpr=&nomcommunen=&allcheck=&checkvar=&touslesmotste=&touslesmotsen=&touslesmotspr=&nomcommun=e&touslesmots=&PageCourante=1&varposition=1&fiche_numero=2676&origine=recherche&fiche_etat=5">
						 <img src="http://doris.ffessm.fr/gestionenligne/photos_fiche_vig/Abietinaria-abietina-sas1.JPG" height="105" border="0" >						 </a>
                        
                         
                         
                                      						</td>
                        <td width="5" >&nbsp;</td>
                      </tr>
                      <tr>
                        <td >&nbsp;</td>
                        <td height="29"  align="center"  class="normal_noir_gras"><em>Abietinaria abietina</em></td>
                        <td >&nbsp;</td>
                      </tr>
                      <tr>
                        <td >&nbsp;</td>
                        <td height="29"  align="center"  class="gris_gras">Hydraire sapin</td>
                        <td >&nbsp;</td>
                      </tr>
					  <tr bgcolor="#FAEDC0">
                        <td >&nbsp;</td>
                        <td  align="center"  class="normalgris">
                           <a href="fiche3.asp?temp=0&nomcommunte=&nomcommunpr=&nomcommunen=&allcheck=&checkvar=&touslesmotste=&touslesmotsen=&touslesmotspr=&nomcommun=e&touslesmots=&PageCourante=1&varpositionf=1&fiche_numero=2676&origine=recherche&fiche_etat=5"class="normalgris"><img src="images/fiche.gif" width="18" height="18" border="0" align="absmiddle">Fiche propos�e...</a>
						   
                         </td>
                        <td >&nbsp;</td>
                      </tr>
</table> 
 	
 */
    	
    	String patternString =
    			//"fiche_numero=(\\d*)";
    			"<em>([-a-zA-Zàéèêïù'’0-9&; \\t]*)</em></td>"+
    			"\\s*<td >&nbsp;</td>"+
    			"\\s*</tr>"+
    			"\\s*<tr>"+
    			"\\s*<td >&nbsp;</td>"+
    			"\\s*<td[-a-zA-Z0-9=\"\\s]*class=\"gris_gras\">([-a-zA-Zàéèêïù'’0-9&; \\t]*)</td>"+
    			"\\s*<td >&nbsp;</td>"+
    			"\\s*</tr>"+
    			"\\s*<tr [-a-zA-Z0-9=#\"\\s]*>"+
    			"\\s*<td >&nbsp;</td>"+
    			"\\s*<td [-a-zA-Z0-9=#\"\\s]*>"+
    			"\\s*<a href=\"fiche3.asp\\?[-a-zA-Z0-9&=]*&fiche_numero=(\\d*)&";	  
					  
    			
    			
    			; //</span>";//"<span\\s*class=\"gris_gras\">([-a-zA-Z��0-9&;]*)</span></td>";//+
                //"<td[-a-zA-Z0-9=\"\\s]*>"+
                //"\\s<a href=\"fiche3.asp[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*fiche_numero=(\\d*)";
    			//"\\bhttp://doris\\.ffessm\\.fr/gestionenligne/photos/(.*)\"";//+
               // ".*<td class=\"trait_hautbas\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
               // ".*<td width=\"331\" height=\"25\" valign=\"middle\" bgcolor=\"#FFFFFF\"><span class=\"normal_noir_gras\"><em>(\\w*)</em></span><span class=\"gris_gras\">(\\w*)</span></td>"+
               // ".*<a href=\"fiche3.asp\\?temp=0&nomcommunte=&nomcommunpr=&nomcommunen=&allcheck=&checkvar=&touslesmotste=&touslesmotsen=&touslesmotspr=&nomcommun=e&touslesmots=&PageCourante=1&varpositionf=2&fiche_numero=(\\d*)&origine=recherche&fiche_etat=(\\d*)\" class=\"titre2\" >";
    	
    	Pattern pattern = Pattern.compile(patternString);
    	Matcher matcher = pattern.matcher(pageContent);
		// Check all occurance
    	Log.d(LOG_TAG, "Looking for pattern in page");
		while (matcher.find()) {
			//Log.d(LOG_TAG, matcher.group(1) + " " + matcher.group(2)+ " " + matcher.group(3)+ " " + matcher.group(4));
			Log.d(LOG_TAG,"SciName="+ matcher.group(1)+" CommonName="+ matcher.group(2) + " Fiche numero="+ matcher.group(3) );
			//result = Integer.parseInt(matcher.group(1));
		}
    	
    }
    
    protected int getNbPages(String firstPageContent){
    	int result = 1;
    	Pattern pattern = Pattern.compile("<td width=\"120\" align=\"center\" valign=\"middle\" bgcolor=\"#F3F3F3\" class=\"gris_gras\">Page 1/(\\d*) </td>");
    	//String template = "<td width=\"120\" align=\"center\" valign=\"middle\" bgcolor=\"#F3F3F3\" class=\"gris_gras\">Page 1/"; //184 </td>";
    	
    	Matcher matcher = pattern.matcher(firstPageContent);
		// Check all occurance
		while (matcher.find()) {
			Log.d(LOG_TAG, matcher.group(1));
			result = Integer.parseInt(matcher.group(1));
		}
    	return result;
    }
    
	
}
