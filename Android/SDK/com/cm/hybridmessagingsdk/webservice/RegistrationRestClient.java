package com.cm.hybridmessagingsdk.webservice;

import android.content.Context;

import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.util.UserAgent;
import com.cm.hybridmessagingsdk.listener.ResponseFormatHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Dion on 10/11/14.
 */
public class RegistrationRestClient {

    private static final String sRegistrations = "deviceregistrations";

    public static void getRegistration(Context context, String deviceId, ResponseFormatHandler responseHandler) {

        RestClientUsage.get(context, sRegistrations, deviceId, responseHandler);
    }

    public static void newRegistration(Context context, String pushToken, String msisdn, ResponseFormatHandler responseHandler) {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("PushToken", pushToken);
        params.put("Msisdn", msisdn);


        RestClientUsage.post(context, sRegistrations, params, responseHandler);
    }

    public static void updateRegistration(Context context, String deviceId, String pushToken, final ResponseFormatHandler responseHandler) {

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("PushToken", pushToken);

        RestClientUsage.put(context, sRegistrations, deviceId, params, responseHandler);
    }

    public static void registerByCode(Context context, String deviceId, String verificationCode, final ResponseFormatHandler responseHandler) {

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("VerificationCode", verificationCode);

        RestClientUsage.put(context, sRegistrations, deviceId, params, responseHandler);

    }
}
