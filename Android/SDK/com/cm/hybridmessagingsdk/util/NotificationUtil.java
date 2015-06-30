package com.cm.hybridmessagingsdk.util;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;

import java.util.Random;

/**
 * NotificationUtil will take care of all notifications of the HybridMessagingSDK
 * The Notification can be in a couple of states:
 *
 * - By default the NotificationUtil will fire a notification
 * - If the project implementing the SDK sets its own listener to create their own notification the Notification util will not make a notification
 * - The notifications can also be turned off
 */
public class NotificationUtil {

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
    private static String KEY_NOTIF_FIRE = "KEY_NOTIF_FIRE";
    public NotificationUtil fireNotificationByDefault(boolean fire) {
        PreferenceHandler.saveValueBoolean(mContext, KEY_NOTIF_FIRE, fire);
        return this;
    }

    public boolean getFireNotificationByDefault() {
        return PreferenceHandler.getValueBoolean(mContext, KEY_NOTIF_FIRE);
    }

    /**
     * Set the title of the notification. By default its the name of the application.
     * If the name of the application is not available the title is "Notification"
     * @param name
     * @return
     */
    private static String KEY_NOTIF_NAME = "NOTIF_NAME";
    public NotificationUtil setNotificationTitle(String name) {
        PreferenceHandler.saveValueString(mContext, KEY_NOTIF_NAME, name);
        return this;
    }

    public String getNotificationTitle() {
        return PreferenceHandler.getValueString(mContext, KEY_NOTIF_NAME);
    }

    private static String KEY_NOTIF_ICON = "NOTIF_ICON";
    public void setNotificationIcon(int resourceId) {
        PreferenceHandler.saveValueInt(mContext, KEY_NOTIF_ICON, resourceId);
    }

    public int getNotificatonIcon() {
        return PreferenceHandler.getValueInt(mContext, KEY_NOTIF_ICON);
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
        if(!getFireNotificationByDefault()) {
             return; // do nothing
        }

        // If the notification does not match the default name, use the new name.
        // If it does match, retrieve the name of the app.
        if(mContext != null) {
            UserAgent userAgent = new UserAgent(mContext);
            if (getNotificationTitle() == null) {
                setNotificationTitle(userAgent.getAppName()); // Retrieve name of the app from the project implementing this SDK
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
        if(getNotificatonIcon() == Integer.MIN_VALUE) {
            String name = UserAgent.getAppIcon(mContext);
            Resources resources = mContext.getResources();
            setNotificationIcon(resources.getIdentifier(name, "drawable", mContext.getPackageName()));
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setContentTitle(getNotificationTitle())
                        .setSmallIcon(getNotificatonIcon())
                        .setContentText(notification.getMessage());

        notificationManager.notify(getNotificationId(notification), mBuilder.build());
    }

    /*
     * Get the notificationId from the notification object received from the HybridMessaging server.
     * If the notificationId does not meet the criteria a new one will be created.
     */
    private int getNotificationId(Notification notification) {
        // NotificationId Length
        int count = notification.getNotificationId().length();
        int seed = (int)Math.pow(10, 5); // 100k
        int notificationId = 0;

        String notifId = null;
        try {
            notifId = notification.getNotificationId().substring(count - 6, count);
        } catch(Exception ex) { /* nothing */ }

        try {
            notificationId = Integer.valueOf(notifId);
        } catch(Exception ex) {
            notificationId = new Random().nextInt(seed);
        }
        return notificationId;
    }
}
