package com.cm.hybridmessagingsdk.listener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Dion on 28/11/14.
 */
public interface ResponseFormatHandler {

    void onReceiveJSONObject(JSONObject jsonObject);

    void onReceiveJSONArray(JSONArray jsonArray);

    void onError(Throwable throwable);
}
