package fr.ffessm.doris.android.activities.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        try{
	        String inputLine;
	        while ((inputLine = in.readLine()) != null)
	            sb.append(inputLine);
	        }
        finally{
        	in.close();
        }
        return sb.toString();
    }
    
    protected void processFirstPage(String urlString) throws IOException{
    	String content = getPageContent(urlString);
    	Log.d(LOG_TAG, "retrieved page "+urlString);
    	Log.d(LOG_TAG, content);
    	Integer maxNbPages = getNbPages(content);
    	mNotificationHelper.setMaxNbPages(maxNbPages.toString());
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
