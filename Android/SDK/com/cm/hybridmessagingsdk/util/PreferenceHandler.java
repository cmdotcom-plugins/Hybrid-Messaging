package com.cm.hybridmessagingsdk.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dion on 02/12/14.
 */
public class PreferenceHandler {

    private static String sPreference_name = "com.cm.hybridsdk.data";

    private static String sPreference_value_msisdn = "MSISDN";
    private static String sPreference_value_regisid = "REGISID";
    private static String sPreference_value_deviceid = "DEVICEID";

    public  static String PREFERENCE_VALUE_VERSION = "VERSION";

    /**
     * Get ObscuredSharedPreferences made for encryption.
     * This encryption doesn't fully cover everything but makes it for simple rooted users harder to retrieve values from the preferences.
     * @param context context
     * @return SharedPreferences
     */
    private static SharedPreferences getPreferences(Context context) {
        return  new ObscuredSharedPreferences(
                context, context.getSharedPreferences(sPreference_name, Context.MODE_PRIVATE));
    }

    public static void saveValueInt(Context context, String key, int value) {
        getPreferences(context).edit().putInt(key,value).commit();
    }

    protected static int getValueInt(Context context, String key) {
        return getPreferences(context).getInt(key, Integer.MIN_VALUE);
    }

    public static void saveValueBoolean(Context context, String key, boolean value) {
        getPreferences(context).edit().putBoolean(key, value).commit();
    }

    protected static boolean getValueBoolean(Context context, String key) {
        return getPreferences(context).getBoolean(key, false);
    }

    public static void saveValueString(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).commit();
    }

    protected static String getValueString(Context context, String key) {
        return getPreferences(context).getString(key, null);
    }

    public static void saveMsisdn(Context context, String msisdn) {
        getPreferences(context).edit().putString(sPreference_value_msisdn,msisdn).commit();
    }

    public static String getMsisdn(Context context) {
        return getPreferences(context).getString(sPreference_value_msisdn, "");
    }

    public static void saveRegistrationId(Context context, String regId) {
        getPreferences(context).edit().putString(sPreference_value_regisid, regId).commit();
    }

    public static String getRegistrationId(Context context) {
        return getPreferences(context).getString(sPreference_value_regisid, "");

    }

    public static void saveDeviceId(Context context, String deviceId) {
        getPreferences(context).edit().putString(sPreference_value_deviceid, deviceId).commit();
    }

    public static String getDeviceId(Context context) {
        return getPreferences(context).getString(sPreference_value_deviceid, "");

    }


}
