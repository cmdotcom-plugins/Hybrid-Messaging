package com.cm.hybridmessagingsdk.receivers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.cm.hybridmessagingsdk.GCMNotificationIntentService;
import com.cm.hybridmessagingsdk.HybridMessaging;

/**
 * Created by Dion on 05/11/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName comp = new ComponentName(context.getPackageName(), GCMNotificationIntentService.class.getName());
        HybridMessaging.context = context;
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
