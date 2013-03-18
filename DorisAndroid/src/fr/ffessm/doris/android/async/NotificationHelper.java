/* invalid */
package fr.ffessm.doris.android.async;

import fr.ffessm.doris.android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;
    
    private String maxItemToProcess = "???";
    
	/** Initial text that appears in the status bar */
    private String initialTickerText;
 
	/** Full title of the notification in the pull down */
	private String notificationContentTitle;

	public NotificationHelper(Context context, String initialTickerText, String contentTitle)
    {
        mContext = context;
		this.initialTickerText = initialTickerText;
		this.notificationContentTitle = contentTitle;
    }

    /**
     * Put the notification into the status bar
     */
    public void createNotification() {
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //create the notification
        int icon = android.R.drawable.stat_sys_download;
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, initialTickerText, when);

        //create the content which is shown in the notification pulldown
        CharSequence contentText = "0 / "+maxItemToProcess; //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        //add the additional content and intent to the notification
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);

        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int nbItemsComplete) {
        //build up the new status message
        CharSequence contentText = nbItemsComplete + " / " +maxItemToProcess;
        //publish it to the status bar
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new "task complete" notification
     */
    public void completed()    {
        //remove the notification from the status bar
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
    
    public String getMaxItemToProcess() {
		return maxItemToProcess;
	}

	public void setMaxItemToProcess(String maxItemToProcess) {
		this.maxItemToProcess = maxItemToProcess;
	}
}
