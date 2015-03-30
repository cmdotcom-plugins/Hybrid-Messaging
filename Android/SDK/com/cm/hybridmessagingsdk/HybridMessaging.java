package com.cm.hybridmessagingsdk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.cm.hybridmessagingsdk.listener.HybridNotificationListener;
import com.cm.hybridmessagingsdk.listener.OnMessageListener;
import com.cm.hybridmessagingsdk.listener.OnRegistrationListener;
import com.cm.hybridmessagingsdk.listener.OnVerificationStatus;
import com.cm.hybridmessagingsdk.listener.ResponseFormatHandler;
import com.cm.hybridmessagingsdk.util.Filter;
import com.cm.hybridmessagingsdk.util.Message;
import com.cm.hybridmessagingsdk.util.NotificationUtil;
import com.cm.hybridmessagingsdk.util.PlayServiceProvider;
import com.cm.hybridmessagingsdk.util.PreferenceHandler;
import com.cm.hybridmessagingsdk.util.Registration;
import com.cm.hybridmessagingsdk.util.UserAgent;
import com.cm.hybridmessagingsdk.util.Validate;
import com.cm.hybridmessagingsdk.util.VerificationStatus;
import com.cm.hybridmessagingsdk.webservice.MessageRestClient;
import com.cm.hybridmessagingsdk.webservice.RegistrationRestClient;
import com.cm.hybridmessagingsdk.webservice.RestClientUsage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HybridMessaging {

    private String TAG = "HybridMessagingSDK";

    public static Context context;
    private static UserAgent mUserAgent;
    protected static NotificationUtil notificationUtil;


    // initialize //

    public HybridMessaging(Context context) {
        Validate.notNull(context, "context");
        HybridMessaging.context = context;

        getSenderId();

        PlayServiceProvider playServiceProvider = new PlayServiceProvider(context);
        notificationUtil = new NotificationUtil(context);
    }

    public static void initialize(Context context) {
        Validate.notNull(context, "context");
        HybridMessaging.context = context;

        getSenderId();

        PlayServiceProvider playServiceProvider = new PlayServiceProvider(context);
        notificationUtil = new NotificationUtil(context);
    }


    // SDK/API Getters & Setters //

    /**
     * Retrieve Sender id from the manifest of the project implementing this SDK
     */
    private static void getSenderId() {
        Validate.notNull(context, "context");

        String senderId = RestClientUsage.getMetaData(context, RestClientUsage.PROJECT_ID);
        GCMNotificationIntentService.PROJECT_ID = senderId;
    }

    /**
     * Check wether the msisdn is saved locally or could be retrieved from the simcard. Simcard has more priority than locally saved msisdn.
     *
     * WARNING: When you want to retrieve the number from the sim you need the permission set: READ_PHONE_STATE.
     * without this permission the number will only check if the phone number is stored locally.
     * @return
     */
    public static String getMsisdn() {
        Validate.notNull(context, "context");

        PackageManager pm = context.getPackageManager();
        String number = null;
        if (pm.checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            number = tMgr.getLine1Number();
        } else {
//            Log.e("HybridMessagingSDK", "No permission set READ_PHONE_STATE. Number could not be read from the telephone. Set this permission to automatically retrieve phone number from sim");
        }

        if(number == null || "".equals(number)) {
            number = PreferenceHandler.getMsisdn(context);
        }
        return number;
    }

    public static void resetLogin() {
        PreferenceHandler.saveDeviceId(context, "");
        PreferenceHandler.saveMsisdn(context, "");
        PreferenceHandler.saveRegistrationId(context, "");
    }

    /**
     * Set the msisdn used by the user. This is necessary for the API to work.
     * @param msisdn
     */
    protected static void setMsisdn(String msisdn) {
        Validate.notNull(context, "context");

        PreferenceHandler.saveMsisdn(context, msisdn);
    }

    /**
     * Get deviceId from preferences once received when registering trough the SDK
     * @return
     */
    public static String getDeviceId() {
        Validate.notNull(context, "context");

        return PreferenceHandler.getDeviceId(context);
    }

    /**
     * Set specific values for the user agent
     * @return UserAgent containing simple information about the users phone
     */
    public static UserAgent setUserAgent() {
        Validate.notNull(context, "context");

        if(mUserAgent == null) {
            mUserAgent = new UserAgent(context);
        }
        return mUserAgent;
    }

    /**
     * Get the UserAgent send with the requests
     * @return UserAgent containing simple information about the users phone
     */
    public static UserAgent getUserAgent() {
        Validate.notNull(context, "context");
        if(mUserAgent == null) {
            mUserAgent = new UserAgent(context);
        }
        return mUserAgent;
    }

    // Notifications //

    /**
     * The HybridMessagingSDK will show notifications by default in the Notification bar when receiving a message
     * Turn this on or off.
     *
     * @param showNotification value indicating if the notification will be fired
     */
    public static void fireNotificationsByDefault(boolean showNotification) {
        Validate.notNull(notificationUtil, "NotificationUtil");

        notificationUtil.fireNotificationByDefault(showNotification);
    }

     /**
     * Set the title of the notification. By default its the name of the application.
     * If the name of the application is not available the title is "Notification"
      *
     * @param title
     */
    public static void setNotificationTitle(String title) {
        notificationUtil.setNotificationTitle(title);
    }

    /**
     * Set the notification icon displayed when receiving a notification message trough the SDK
     * @param resourceId
     */
    public static void setNotificationIcon(int resourceId) {
        notificationUtil.setNotificationIcon(resourceId);
    }

    /**
     * Handle the notification yourself. Use the HybridNotificationListener to receive the notification properties.
     *
     * @param hybridNotificationListener
     */
    public static void setHybridNotificationListener(HybridNotificationListener hybridNotificationListener) {
        notificationUtil.buildOwnNotification(hybridNotificationListener);
    }


    // Registering //

    /**
     * Get the verification status a user. See com.cm.hybridmessagingsdk.Status for the possible outcomes
     * @param onVerificationStatus
     */
    public static void getVerificationStatus(final OnVerificationStatus onVerificationStatus) {
        Validate.notNull(context, "context");

        String deviceId = getDeviceId();
        // deviceId is needed for verification. The SDK does not contain a deviceId when the user is not registered
        if("".equals(deviceId)) {
            Validate.notNull(onVerificationStatus, "OnverificationStatus");
            onVerificationStatus.onVerificationStatus(VerificationStatus.Unverified);
        }


        RegistrationRestClient.getRegistration(context, getDeviceId(), new ResponseFormatHandler() {

            @Override
            public void onReceiveJSONObject(JSONObject jsonObject) {
                if (onVerificationStatus == null)
                    throw new NullPointerException("OnVerificationStatus can't be null");

                // Build registration object from received json
                Registration registration = Registration.getRegistrationFromJson(jsonObject);

                // Save device id
                PreferenceHandler.saveDeviceId(context, registration.getDeviceId());

                // Send Verification response back to the project implementing the SDK
                onVerificationStatus.onVerificationStatus(registration.getStatus());
            }

            @Override
            public void onReceiveJSONArray(JSONArray jsonArray) {
                //Nothing
            }

            @Override
            public void onError(Throwable throwable) {
                if (onVerificationStatus == null)
                    throw new NullPointerException("OnVerificationStatus can't be null");
                onVerificationStatus.onError(throwable);
            }
        });
    }

    /**
     * Register a new user trough the SDK by given registrationId and msisdn
     * registerNewUser will return a deviceId that will be used for everything in the SDK.
     *
     * @param msisdn The phonenumber of the user formatted with country code
     * @param onRegistrationListener callback
     */
    public static void registerNewUser(final String msisdn, final OnRegistrationListener onRegistrationListener) {
        Validate.notNull(context, "context");

        String regId = PreferenceHandler.getRegistrationId(context);
        if(regId == null) {
            throw new NullPointerException("No registrationId available. Do you have the Play Store installed on your device?");
        }

        RegistrationRestClient.newRegistration(context, regId, msisdn, new ResponseFormatHandler() {

            @Override
            public void onReceiveJSONObject(JSONObject jsonObject) {
                if (onRegistrationListener == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");

                // Set msisdn
                setMsisdn(msisdn);

                // Build registration object from received json
                Registration registration = Registration.getRegistrationFromJson(jsonObject);

                // Save device id
                PreferenceHandler.saveDeviceId(context, registration.getDeviceId());

                // Create registration object from JSONObject and send it to the client
                onRegistrationListener.onReceivedRegistration(registration);
            }

            @Override
            public void onReceiveJSONArray(JSONArray jsonArray) { }

            @Override
            public void onError(Throwable throwable) {
                if (onRegistrationListener == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");
                onRegistrationListener.onError(throwable);
            }

        });
    }

    /**
     * Request a new verification pin from the SDK. The verification pin will be send via sms.
     * @param msisdn
     * @param handler
     */
    public static void requestNewVerificationPin(final String msisdn, final OnRegistrationListener handler) {
        // Just calling register new user.
        // The only difference is that the deviceId is already saved by the SDK and the same pin will be send
        // Instead of a new one
        registerNewUser(msisdn, handler);
    }

    /**
     * Send the new registrationId and deviceId to the server
     * @param handler receiving response in format
     */
    private static void updateRegistration(final OnRegistrationListener handler) {
        Validate.notNull(context, "context");

        String regId = PreferenceHandler.getRegistrationId(context);
        if(regId == null) {
            throw new NullPointerException("No registrationId available. Do you have the Play Store installed on your device?");
        }

        RegistrationRestClient.updateRegistration(context, getDeviceId(), regId, new ResponseFormatHandler() {

            @Override
            public void onReceiveJSONObject(JSONObject jsonObject) {
                if (handler == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");
                // Create registration object from JSONObject and send it to the client
                handler.onReceivedRegistration(Registration.getRegistrationFromJson(jsonObject));
            }

            @Override
            public void onReceiveJSONArray(JSONArray jsonArray) {
                //Nothing
            }

            @Override
            public void onError(Throwable throwable) {
                if (handler == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");
                handler.onError(throwable);
            }
        });
    }

    /**
     * When registering the user will receive a pincode to complete the registration process. Use this method in order to complete the registration
     * and verify the users phone number.
     * @param verificationCode 4 digit pincode
     * @param onVerificationStatus receiving response in format
     */
    public static void registerUserByPincode(String verificationCode, final OnVerificationStatus onVerificationStatus) {
        Validate.notNull(context, "context");

        RegistrationRestClient.registerByCode(context, getDeviceId(), verificationCode, new ResponseFormatHandler() {
            @Override
            public void onReceiveJSONObject(JSONObject jsonObject) {
                if (onVerificationStatus == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");
                // Create registration object from JSONObject and extract the VerificationStatus
                Registration registration = Registration.getRegistrationFromJson(jsonObject);

                onVerificationStatus.onVerificationStatus(registration.getStatus());
            }

            @Override
            public void onReceiveJSONArray(JSONArray jsonArray) {
                //Nothing
            }

            @Override
            public void onError(Throwable throwable) {
                if (onVerificationStatus == null)
                    throw new NullPointerException("OnVerificationStatus can't be null");
                onVerificationStatus.onError(throwable);
            }
        });
    }


    // Messages //

    /**
     * Get all messages for a user, limit is set by the SDK. Default is unknown.
     * @param handler receiving response in format
     */
    public static void getMessages(OnMessageListener handler) {
        getMessages(null, handler);
    }

    /**
     *
     * @param amount is the limit for the amount of messages the SDK will return
     * @param handler receiving response in format
     */
    public static void getMessages(int amount, OnMessageListener handler) {
        Filter filter = new Filter();
        filter.addFilter(Filter.OPTION_SELECT, "ID,Body,DateTime,Sender");
        filter.addFilter(Filter.OPTION_TOP, String.valueOf(amount));

        getMessages(filter, handler);
    }

    /**
     *
     * @param offset offset of the messages that the SDK will return
     * @param amount is the limit for the amount of messages receiving from the SDK
     * @param handler receiving response in format
     */
    public static void getMessages(int offset,  int amount, final OnMessageListener handler) {
        Filter filter = new Filter();
        filter.addFilter(Filter.OPTION_SELECT, "ID,Body,DateTime,Sender");
        filter.addFilter(Filter.OPTION_SKIP, String.valueOf(offset));
        filter.addFilter(Filter.OPTION_TOP,String.valueOf(amount));

        getMessages(filter, handler);
    }

    /**
     *
     * @param filter The filter where you can specify which data to receive. Check OData docs for more.
     * @param handler receiving response in format
     */
    public static void getMessagesWithFilter(Filter filter, final OnMessageListener handler) {
        Validate.notNull(context, "context");

        getMessages(filter, handler);
    }

    /**
     *
     * @param dateInMillisec The date given in milliseconds
     * @param handler receiving response in format
     */
    public static void getMessagesFromDate(long dateInMillisec, final OnMessageListener handler) {

        Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS");

        String s = formatter.format(new Date(dateInMillisec));

        Filter filter = new Filter();
        filter.addFilter(Filter.OPTION_FILTER, "DateTime gt DateTime'" + s + "'");


        getMessages(filter, handler);
    }

    /**
     * This method is for most used to send the params of the other message methods and handle the HybridMessagingResponseHandler only one time
     * @param filter filter to specify the format to return the values according to the standard of OData
     * @param handler receiving response in format
     */
    private static void getMessages(Filter filter, final OnMessageListener handler) {
        Validate.notNull(context, "context");

        MessageRestClient.getMessages(context, filter, new ResponseFormatHandler() {

            @Override
            public void onReceiveJSONObject(JSONObject jsonObject) {}

            @Override
            public void onReceiveJSONArray(JSONArray jsonArray) {
                if (handler == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");

                int length = jsonArray.length();
                ArrayList<Message> messages = new ArrayList<Message>();

                for(int i = 0; i < length; i++) {
                    try {
                        messages.add(Message.getMessageFromJson(jsonArray.getJSONObject(i)));
                    } catch (JSONException e) { e.printStackTrace(); }
                }

                // Send back an empty list
                handler.onReceivedMessages(messages.toArray(new Message[messages.size()]));
            }

            @Override
            public void onError(Throwable throwable) {
                if (handler == null)
                    throw new NullPointerException("HybridMessagingResponseHandler can't be null");
                handler.onError(throwable);
            }
        });
    }
}
