package com.cm.hybridmessagingsdk.webservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.ResponseFormatHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dion on 10/11/14.
 */
public class RestClientUsage {

    // Base URL of Notifire API
    public static final String BASE_URL = "https://hybridmessagingapi.cm.nl/";


    private static Header[] getHeaders(Context context, String method, String uri, String deviceId) {
        // Get timestamp in seconds
        Date date = new Date();
        String timestamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(date.getTime()));

        // Create mac according
        String key_api = getMetaData(context, api_key);
        StringBuilder sb = new StringBuilder();
        String secret_key = getMetaData(context, api_secret);
        String mac = Security.buildRequestMac(secret_key, timestamp, method, uri);


        sb.append("MAC kid=\""+key_api+"\" ts=\""+timestamp+"\" mac=\""+mac+"\"");

        Header[] headers = null;

        // Create/Get user agent from the HybridMessagingSDK
        String userAgent = HybridMessaging.getUserAgent().buildUserAgent(context);
        if(userAgent == null) {
            userAgent = "(Android)";
        }

        if(deviceId != null) {

            headers = new Header[]{
                    new BasicHeader("Authorization", sb.toString()),
                    new BasicHeader("User-Agent", userAgent),
                    new BasicHeader("DeviceID", deviceId)
            };
        } else {

            headers = new Header[]{
                    new BasicHeader("Authorization", sb.toString()),
                    new BasicHeader("User-Agent", userAgent)
            };
        }

        return headers;
    }

    // Static method for building a GET request to a specific destination
    public static void get(Context context, String uri, String deviceId, final ResponseFormatHandler responseHandler) {

        Header[] headers = getHeaders(context, "GET", uri, deviceId);

        String temp = uri.replaceAll(" ", "%20");
        String absoluteUrl = getAbsoluteUrl(temp);

        HybridRestClient.executeGet(absoluteUrl, headers, responseHandler);
    }

    // Static method for building a POST request to a specific destination
    public static void post(Context context, String uri, Map<String, String> params, final ResponseFormatHandler responseHandler) {

        Header[] headers    = getHeaders(context, "POST", uri, HybridMessaging.getDeviceId());
        StringEntity entity = buildJsonParams(params);

        // Important content-type. Without application/json server won't accept the StringEntity
        String contentType  = "application/json";
        entity.setContentType(contentType);

        HybridRestClient client1 = new HybridRestClient();
        client1.executePost(getAbsoluteUrl(uri), headers, entity, contentType, responseHandler);
    }


    // Static method for building a POST request to a specific destination
    public static void put(Context context, String uri, String deviceId, Map<String, String> params, final ResponseFormatHandler responseHandler) {

        Header[] headers = getHeaders(context, "PUT", uri, deviceId);


        StringEntity entity = buildJsonParams(params);

        // Important content-type. Without application/json server won't accept the StringEntity
        String contentType = "application/json";
        entity.setContentType(contentType);

        HybridRestClient.executePut(getAbsoluteUrl(uri),  headers, entity, contentType, responseHandler);
    }

    /**
     * Create out of a Key Value pair valid json to send to the server as Post parameters
     * @param params
     * @return StringEntity accepted by servers accepting application/json
     */
    private static StringEntity buildJsonParams(Map<String, String> params) {

        // Turn params into json
        Set<String> set = params.keySet();
        JSONObject jsonObject = new JSONObject();

        // Put keys and values in a JSONObject
        for(String key : set) {
            try {
                jsonObject.put(key, params.get(key));
            } catch (JSONException e) { e.printStackTrace(); }
        }

        // Turn JSONObject into an json String
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return entity;
    }

    // Simple method to use the BASE_URL and build the destination url
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    // Meta tags to retrieve keys from the manifest of the application implementing the SDK
    private static final String api_key     = "com.cm.hybridmessaging.api_key";
    private static final String api_secret  = "com.cm.hybridmessaging.secret";
    public static final String PROJECT_ID   = "com.cm.hybridmessaging.projectid";

    /**
     * Retrieve keys from the manifest defined by the application implementing this SDK
     * @param context
     * @param tag
     * @return
     */
    public static String getMetaData(Context context, String tag) {
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = ai.metaData;
        String result = bundle.getString(tag);
        if(result == null) {
            throw new NullPointerException("Define "+ tag +" in you manifest as meta-tag.");
        }
        return bundle.getString(tag);
    }
}
