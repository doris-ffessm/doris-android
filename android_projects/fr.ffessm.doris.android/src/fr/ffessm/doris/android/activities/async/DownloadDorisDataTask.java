package fr.ffessm.doris.android.activities.async;

import android.content.Context;
import android.os.AsyncTask;

public class DownloadDorisDataTask  extends AsyncTask<String,Integer, Integer>{
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

	
}
