package com.cm.hybridmessagingsdk.webservice;

import android.content.Context;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.ResponseFormatHandler;
import com.cm.hybridmessagingsdk.util.Filter;

/**
 * Created by Dion on 10/11/14.
 */
public class MessageRestClient {

    private static final String sMessagesTake = "messages";

    public static void getMessages(Context context, Filter filter, final ResponseFormatHandler responseHandler) {

        StringBuilder sb = new StringBuilder();
        sb.append(sMessagesTake);

        // Get OData filter if filter not is null
        if(filter != null)
            sb.append(Filter.buildFilter(filter));

        RestClientUsage.get(context, sb.toString(), HybridMessaging.getDeviceId(), responseHandler);
    }

    public static void getMessagesAndTake(Context context, int take, final ResponseFormatHandler responseHandler) {
        RestClientUsage.get(context, String.format(sMessagesTake, take), HybridMessaging.getDeviceId(), responseHandler);
    }

    public static void getMessagesSkipAndTake(Context context, int skip, int take, final ResponseFormatHandler responseHandler) {

        RestClientUsage.get(context, String.format(sMessagesTake, skip, take), HybridMessaging.getDeviceId(), responseHandler);
    }
}
