package com.cm.hybridmessagingsdk.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dion on 10/11/14.
 */
public class Registration {

    public enum Platform {
        iOS(1), Android(2), WindowsPhone(3);
        private int value;

        public int getValue() {
            return value;
        }

        private Platform(int value) {
            this.value = value;
        }
    }

    private String deviceId;
    private Platform platform;
    private VerificationStatus status;
    private String Msisdn;
    private Date created;
    private boolean registeredForPushNotification;

    private static String jsonDeviceId = "DeviceID";
    private static String jsonPlatform = "Platform";
    private static String jsonRegisStatus = "RegistrationStatus";
    private static String jsonMsisdn = "Msisdn";
    private static String jsonCreated = "Created";
    private static String jsonWantsNotifications = "RegisteredForPushNotification";

    /**
     * Create Regidtration object from JSONOjbect. Checking for every element if its exist to prevent interrupting the object creation
     * @param json
     * @return
     */
    public static Registration getRegistrationFromJson(JSONObject json) {
        String deviceId = null;
        String platform = null;
        String status = null;
        String msisdn = null;
        String created = null;
        boolean isRegisteredForNotifications = true;

        try {
            if(json.has(jsonDeviceId)) {
                deviceId = json.getString(jsonDeviceId);
            }

            if(json.has(jsonPlatform)) {
                platform = json.getString(jsonPlatform);
            }

            if(json.has(jsonRegisStatus)) {
                status = json.getString(jsonRegisStatus);
            }

            if(json.has(jsonMsisdn)) {
                msisdn = json.getString(jsonMsisdn);
            }

            if(json.has(jsonCreated)) {
                created = json.getString(jsonCreated);
            }

            if(json.has(jsonWantsNotifications)) {
                isRegisteredForNotifications = json.getBoolean(jsonWantsNotifications);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Registration(deviceId, platform, status, msisdn, created, isRegisteredForNotifications);
    }

    public Registration(String deviceId, String platform, String status, String msisdn, String created, boolean registeredForPushNotification) {
        this.deviceId = deviceId;
        this.platform = getPlatformFromString(platform);
        this.status = getStatusFromString(status);
        setCreated(created);
        this.Msisdn = msisdn;
        this.registeredForPushNotification = registeredForPushNotification;
    }

    public Platform getPlatformFromString(String platform) {
        if(status == null) return null;

        if(platform.equals("iOS")) return Platform.iOS;
        else if(platform.equals("Android")) return Platform.Android;
        else if(platform.equals("WindowsPhone")) return Platform.WindowsPhone;

        return null;
    }

    public VerificationStatus getStatusFromString(String status) {
        if(status == null) return null;

        if(status.equals("Unverified")) {
            return VerificationStatus.Unverified;
        } else if (status.equals("PinVerified")) {
            return VerificationStatus.Verified;
        } else if (status.equals("LastPinVerificationFailed")) {
            return VerificationStatus.LastPinVerificationFailed;
        } else if (status.equals("WaitingForPin")) {
            return VerificationStatus.WaitingForPin;
        }

        return null;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public String getMsisdn() {
        return Msisdn;
    }

    public void setMsisdn(String msisdn) {
        Msisdn = msisdn;
    }

    public void setStatus(String status) {
        this.status = getStatusFromString(status);
    }

    public String getCreated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = dateFormat.format(created);
        return date;
    }

    public void setCreated(String textCreated) {
        if(textCreated == null) return;
        try {
            created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS").parse(textCreated);
        }
        catch (ParseException e) { e.printStackTrace(); }
    }

    public boolean isRegisteredForPushNotification() {
        return registeredForPushNotification;
    }

    public void setRegisteredForPushNotification(boolean registeredForPushNotification) {
        this.registeredForPushNotification = registeredForPushNotification;
    }
}
