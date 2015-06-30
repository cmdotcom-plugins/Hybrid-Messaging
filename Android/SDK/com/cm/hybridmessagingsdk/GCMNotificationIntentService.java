package com.cm.hybridmessagingsdk;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;
import com.cm.hybridmessagingsdk.receivers.GcmBroadcastReceiver;
import com.cm.hybridmessagingsdk.util.Notification;
import com.cm.hybridmessagingsdk.util.NotificationUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by Dion on 05/11/14.
 */
public class GCMNotificationIntentService extends IntentService {

    public static final String TAG = "GCMNotificationIntentService";

    public static String PROJECT_ID;

    public GCMNotificationIntentService() {
        super("com.cm.hybridmessagingsdk.GCMNotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        NotificationUtil notificationUtil = new NotificationUtil(this);

        if (extras != null) {
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                    for (int i = 0; i < 5; i++) {
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e) {}
                    }

                    String messageId = null;
                    long ttl = 30L;

                     Bundle bundle = intent.getExtras();
                     messageId = bundle.getString("message_id");
                     String notificationId = bundle.getString("from");
                     String message = bundle.getString("message");
                     String tempttl = bundle.getString("time_to_live");
                     Notification notification = new Notification(messageId, notificationId, message);

                     if(tempttl != null) {
                         ttl = Long.valueOf(bundle.getString("time_to_live"));
                     }

                     notificationUtil.handleNotification(notification);

                    // ## Send ACK back to server ## //
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("message_type", "DLR");

                    try {
                        if(tempttl != null) {
                            gcm.send(PROJECT_ID + "@gcm.googleapis.com", messageId, ttl, dataBundle);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}
