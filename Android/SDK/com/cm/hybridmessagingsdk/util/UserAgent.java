package com.cm.hybridmessagingsdk.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Locale;

/**
 * Custom UserAgent information
 */
public class UserAgent {

    String app_name;
    String app_version;
    String platform = "Android";
    String language;

    private Context mContext;

    public UserAgent(Context context) { this.mContext = context; }

    /**
     * Build UserAgent according to the devices information. It takes information from the project/device that implements this sdk.
     * @param context
     * @return String with the user agent
     */
    public String buildUserAgent(Context context) {
        if (context == null)
            throw new NullPointerException("Context is null");
        mContext = context;

        StringBuilder sb = new StringBuilder();

        /* format: (appName/appVersion (platform platformVersion; Manufacturer; BuildVersion; language); */
        sb.append(String.format("%s/%s (%s %s; %s; %s)", getAppName(), getAppVersion(), getPlatform(), getCurrentBuildVersion(), getManufacturer(), getLanguage()));

        return sb.toString();
    }

    /**
     * Retrieve the app name of the application implementing this SDK
     * @return App name
     */
    public String getAppName() {
        if(app_name != null) {
            return app_name;
        }

        // Retrieve app name from the manifest of the application integrating the SDK
        String name = mContext.getString(mContext.getApplicationInfo().labelRes); // Get label of application from manifest

        // No name found, must be specified by user itself
        if(name == null) throw new NullPointerException("No application name found in manifest, please specify in a custom UserAgent to the SDK");

        return name;
    }

    /**
     * Retrieve the app icon of the application implementing this SDK
     * Specifically used for default notifications of the SDK
     * @return App name
     */
    public static String getAppIcon(Context ctx) {
        // Retrieve app icon resource name from the manifest of the application integrating the SDK
        String icon = ctx.getString(ctx.getApplicationInfo().icon); // Get label of application from manifest

        // No name found, must be specified by user itself
        if(icon == null) throw new NullPointerException("No icon found in the Manifest. No notifcation could be created.");

        return icon;
    }

    /**
     * Retrieve version name of the application
     * @return Version name of the application specified in manifest (for example: 1.0.2)
     */
    public String getAppVersion() {
        if(app_version != null) { return app_version; }

        PackageInfo appPackage = null;
        try {
            appPackage = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); }

        String versionName = appPackage.versionName;

        // No name found, must be specified by user itself
        if(versionName == null) throw new NullPointerException("No version name found in manifest, please specify VersionName in your manifest or in the SDK's custom UserAgent");

        return appPackage.versionName;
    }

    /**
     * Return build versions number of Android as String. (For example: 2.3 or 4.0.3)
     * @return build number of Android
     */
    public String getCurrentBuildVersion() {
        return ""+Build.VERSION.RELEASE;
    }

    /**
     * Get manufacturer and device model from the current phone
     * @return
     */
    public String getManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + "; " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Get device language
     * @return
     */
    public String getLanguage() {
        return Locale.getDefault().toString();
    }

    public UserAgent setAppName(String app_name) {
        this.app_name = app_name;
        return this;
    }

    public UserAgent setAppVersion(String app_version) {
        this.app_version = app_version;
        return this;
    }

    public String getPlatform() {
        return platform;
    }

    public UserAgent setLanguage(String language) {
        this.language = language;
        return this;
    }
}
