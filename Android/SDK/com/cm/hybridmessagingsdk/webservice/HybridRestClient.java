package com.cm.hybridmessagingsdk.webservice;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cm.hybridmessagingsdk.listener.ResponseFormatHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class HybridRestClient {

    /**
     * Execute a Post to a REST api. The api requires headers to send with the request as verification.
     * Also the Content-type must be application-json whereby the post values will be sended as StringEntity
     * @param url destination url
     * @param headers All headers required for verification. Content-type does not belong here
     * @param entity Entity containing all the post values
     */
    protected static void executePost(final String url, final Header[] headers, final StringEntity entity, final String contentType, final ResponseFormatHandler responseFormatHandler) {  // If you want to use post method to hit server

        new AsyncTask<String, Void, HttpResponse>() {

            @Override
            protected HttpResponse doInBackground(String... string) {
                String result = null;
                HttpResponse response = null;

                // Create HTTPPost client
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                // Add header
                httpPost.setHeaders(headers);

                // Add content type
                httpPost.setHeader("Content-Type", contentType);

                httpPost.setEntity(entity);
                boolean isAborted = false;
                try {

                    response = httpClient.execute(httpPost);
                    return response;
                } catch (UnsupportedEncodingException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                } catch (IOException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(HttpResponse response) {
                if(response == null) return;
                try {
                    HttpEntity entity = response.getEntity();
                    examineJson(EntityUtils.toString(entity), response.getStatusLine().getStatusCode(), null ,responseFormatHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Execute a Get to the HybridMessaging RestAPI. The api requires headers to send with the request as verification.
     * @param url
     * @param headers
     * @param responseFormatHandler
     */
    protected static void executeGet(final String url, final Header[] headers, final ResponseFormatHandler responseFormatHandler) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... string) {
                String result = null;

                HttpClient httpClient = new DefaultHttpClient();

                HttpGet httpget = new HttpGet(url);

                // Add header
                httpget.setHeaders(headers);


                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    result = httpClient.execute(httpget, responseHandler);
                    return result;
                } catch (UnsupportedEncodingException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                } catch (IOException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                }
                return null;
            }


            protected void onPostExecute(String result) {
                examineJson(result, -1, null,responseFormatHandler);
            }

        }.execute();

    }

    /**
     * Execute a PUT to a REST api. The api requires headers to send with the request as verification.
     * Also the Content-type must be application-json whereby the put values will be sended as StringEntity
     * @param url destination url
     * @param headers All headers required for verification. Content-type does not belong here
     * @param entity Entity containing all the put values
     */
    protected static void executePut(final String url, final Header[] headers, final StringEntity entity, final String contentType, final ResponseFormatHandler responseFormatHandler) {

        new AsyncTask<String, Void, HttpResponse>() {

            @Override
            protected HttpResponse doInBackground(String... string) {
                String result = null;
                HttpResponse response = null;

                // Create HTTPut client
                HttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(url);

                // Add header
                httpPut.setHeaders(headers);

                // Add content type
                httpPut.setHeader("Content-Type", contentType);

                httpPut.setEntity(entity);

                try {

                    response = httpClient.execute(httpPut);
                    return response;
                } catch (UnsupportedEncodingException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                } catch (IOException e) {
                    throwErrorOnMainThread(responseFormatHandler, -1, e);
                    e.printStackTrace();
                }
                return response;
            }

            protected void onPostExecute(HttpResponse response) {
                try {
                    HttpEntity entity = response.getEntity();
                    int statusCode = response.getStatusLine().getStatusCode();
                    examineJson(EntityUtils.toString(entity), statusCode, null, responseFormatHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Main method for handling errors on the mainthread since this whole class is basically performed on a seperate thread
     * @param responseFormatHandler The handler for sending a response back to the project implementing this SDK
     * @param statusCode Statuscode
     * @param throwable Exception error
     */
    private static void throwErrorOnMainThread(final ResponseFormatHandler responseFormatHandler, final int statusCode, final Throwable throwable) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {

            @Override
            public void run() {
                if(responseFormatHandler != null) {
                    responseFormatHandler.onError(throwable);
                }
            }
        });
    }

    /**
     * Examine the type of the json and fire the ResponseFormatHandler with the result
     * Check if the json is a JSONArray, JSONObject or string
     *
     * Json will be examined by JSONTokener. When examining fails ResponseFormatHandler.error will be called.
     *
     */
    private static void examineJson(String json, int statusCode, Throwable throwable, ResponseFormatHandler responseHandler) {
        if(json == null) {
            responseHandler.onError(new Throwable("Json response is empty")); // If response is null, return error code -1
            return;
        }

        Object response = null;
        try {
            response = new JSONTokener(json).nextValue();
        } catch (JSONException e) {
            responseHandler.onError(new Throwable("Invalid json: Json could not be parsed"));
        };

        if(response == null) {
            responseHandler.onError(new Throwable("Json error: Error while parsing json")); // -2 Error parsing the json
            return;
        }

        // Check what for type the response of the server is
        if(response instanceof JSONObject) {
            // Send back json as JSONObject
            responseHandler.onReceiveJSONObject((JSONObject)response);
        } else if(response instanceof JSONArray) {
            // Send back json as JSONArray
            responseHandler.onReceiveJSONArray((JSONArray) response);
        }
    }
}
