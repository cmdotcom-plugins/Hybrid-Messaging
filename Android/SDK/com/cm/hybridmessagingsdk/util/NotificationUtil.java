package com.cm.hybridmessagingsdk.util;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.R;
import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;

/**
 * NotificationUtil will take care of all notifications of the HybridMessagingSDK
 * The Notification can be in a couple of states:
 *
 * - By default the NotificationUtil will fire a notification
 * - If the project implementing the SDK sets its own listener to create their own notification the Notification util will not make a notification
 * - The notifications can also be turned off
 */
public class NotificationUtil {

    private static final String sDefaultName = "Notification";

    /* Indicates whether this class may throw notifications by its own. */
    private boolean mFireNotification = true;

    /* Title of the notification. By default its sDefaultName. */
    private String  notificationTitle = sDefaultName;

    /* Icon will be set by default to the application icon specified in the manifest */
    private int icon;

    /* Notification listener will be fired when instantiated */
    public static HybridNotificationListener notificationListener;

    private Context mContext;

    public NotificationUtil(Context context) {
        this.mContext = context;
    }

    /**
     * Decide whether the notification should be fired by default.
     * If not the notification can be processed as alternative with the HybridNotificationListener
     *
     * @param fire
     * @return
     */
    public NotificationUtil fireNotificationByDefault(boolean fire) {
        this.mFireNotification = fire;
        return this;
    }

    /**
     * Set the title of the notification. By default its the name of the application.
     * If the name of the application is not available the title is "Notification"
     * @param name
     * @return
     */
    public NotificationUtil setNotificationTitle(String name) {
        this.notificationTitle = name;
        return this;
    }

    public void setNotificationIcon(int resourceId) {
        this.icon = resourceId;
    }

    /**
     * Set your own HybridNotificationListener to build your own custom Notifications
     * @param hybridNotificationListener
     * @return
     */
    public NotificationUtil buildOwnNotification(HybridNotificationListener hybridNotificationListener) {
        NotificationUtil.notificationListener = hybridNotificationListener;
        return this;
    }

    /**
     * Handle the notification according to the settings of the user
     *
     * @param notification the notification object containing the message and notificationId
     */
    public void handleNotification(Notification notification)
    {

        if(notificationListener != null) notificationListener.onReceiveHybridNotification(HybridMessaging.context, notification);

        // Check whether the project allows the NotificationUtil to fire a notification
        if(!mFireNotification) {
             return; // do nothing
        }

        // If the notification does not match the default name, use the new name.
        // If it does match, retrieve the name of the app.
        if(mContext != null) {
            UserAgent userAgent = new UserAgent(mContext);
            if (this.notificationTitle.equals(sDefaultName)) {
                this.notificationTitle = userAgent.getAppName(); // Retrieve name of the app from the project implementing this SDK
            }
        } else {
            throw new NullPointerException("No context specified for the SDK, please initialize the SDK");
        }

        showNotification(notification);
    }

    /**
     * Build a simple notification and show it on the Notification bar
     * @param notification Notification object containing the necessary information for building the content in the notification
     */
    public void showNotification(Notification notification) {

        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // get icon of the project
        if(icon == 0) {
            String name = UserAgent.getAppIcon(mContext);
            Resources resources = mContext.getResources();
            icon = resources.getIdentifier(name, "drawable", mContext.getPackageName());
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setContentTitle(notificationTitle)
                        .setSmallIcon(icon)
                        .setContentText(notification.getMessage());

        int count = notification.getNotificationId().length();
        String notifId = notification.getNotificationId().substring(count - 6, count);
        notificationManager.notify(Integer.valueOf(notifId), mBuilder.build());
    }


}
