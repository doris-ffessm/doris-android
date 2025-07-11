/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android.async;

import fr.ffessm.doris.android.R;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private Context mContext;
    private Intent resultIntent;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotifyBuilder;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;

    private int maxItemToProcess = 0;

    public static String CHANNEL_ID = "BackgroundActivityChannel";

    // Start of user code notification helper additional attributes
    /**
     * Racine du texte qui apparaît dans la status bar
     */
    private String racineTickerText = "";

    // End of user code

    /**
     * Initial text that appears in the status bar
     */
    private String initialTickerText;

    /**
     * Full title of the notification in the pull down
     */
    private String notificationContentTitle;

    public NotificationHelper(Context context, String initialTickerText, String contentTitle, Intent resultIntent) {
        mContext = context;
        this.resultIntent = resultIntent;
        this.initialTickerText = initialTickerText;
        this.notificationContentTitle = contentTitle;
        // TODO evaluate if we can create the channel in a better place
        this.createNotificationChannel();
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
        //mNotification = new Notification(icon, initialTickerText, when);


        //create the content which is shown in the notification pulldown
        // Start of user code notification helper additional status message (createNotification)
        CharSequence contentText = "";
        if (maxItemToProcess != 0) {
            contentText = "0 / " + maxItemToProcess; //Text of the notification in the pull down
        } else {
            contentText = "";
        }
        // End of user code

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        mContentIntent = PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        //add the additional content and intent to the notification
        // mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);

        mNotifyBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setTicker(initialTickerText)
                .setSmallIcon(R.drawable.app_ic_notification)
                .setContentTitle(notificationContentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(mContentIntent);
        mNotification = mNotifyBuilder.build();

        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.background_notification_channel_name);
            String description = mContext.getString(R.string.background_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     *
     * @param nbItemsComplete
     */
    public void progressUpdate(int nbItemsComplete) {
        //build up the new status message

        // Start of user code notification helper additional status message (progressUpdate)
        CharSequence contentText = "";
        if (maxItemToProcess != 0) {
            contentText = racineTickerText + nbItemsComplete + " / " + maxItemToProcess;
            mNotifyBuilder.setProgress(maxItemToProcess, nbItemsComplete, false);
        } else {
            contentText = racineTickerText;
        }

        // End of user code
        //publish it to the status bar
        //mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        //mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        mNotifyBuilder.setContentText(contentText);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                NOTIFICATION_ID,
                mNotifyBuilder.build());

    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new "task complete" notification
     */
    public void completed() {
        //remove the notification from the status bar
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public int getMaxItemToProcess() {
        return maxItemToProcess;
    }

    public void setMaxItemToProcess(int maxItemToProcess) {
        this.maxItemToProcess = maxItemToProcess;
        // Issue the initial notification with zero progress
        mNotifyBuilder.setProgress(maxItemToProcess, 0, false);
        progressUpdate(0);
    }

    public void setContentTitle(String contentTitle) {

        this.notificationContentTitle = contentTitle;
        mNotifyBuilder.setContentTitle(contentTitle);

        mNotificationManager.notify(
                NOTIFICATION_ID,
                mNotifyBuilder.build());
    }

    // Start of user code notification helper additional operations
    public void setRacineTickerText(String racineTickerText) {
        this.racineTickerText = racineTickerText;
    }

    public void setNotificationID(int newNotificationID) {
        NOTIFICATION_ID = newNotificationID;
    }
    // End of user code
}
