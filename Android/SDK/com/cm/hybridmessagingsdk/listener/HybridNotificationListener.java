package com.cm.hybridmessagingsdk.listener;

import android.content.Context;

import com.cm.hybridmessagingsdk.util.Notification;

/**
 * Created by Dion on 04/12/14
 */
public interface HybridNotificationListener {

    void onReceiveHybridNotification(Context context, Notification notification);
}
