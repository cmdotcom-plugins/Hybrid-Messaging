package com.cmtelecom.example.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cm.example.R;
import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.OnRegistrationListener;
import com.cm.hybridmessagingsdk.listener.OnVerificationStatus;
import com.cm.hybridmessagingsdk.util.Registration;
import com.cm.hybridmessagingsdk.util.VerificationStatus;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;
import java.util.Set;


public class RegistrationActivity extends ActionBarActivity implements OnRegistrationListener {

    private static final String TAG = "RegistrationActivity";

    EditText tbPhoneNumber;
    EditText tbRegionCode;
    TextView tbCountryName;
    RelativeLayout countrySelector;
    Button btnRegister;

    String countryISO = "NL";
    static String phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        /* Set up Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_toolbar);


        /* Initialize the Hybrid Messaging SDK
         * Always initialize at the start of the app */
        HybridMessaging.initialize(this);

        HybridMessaging.getVerificationStatus(new OnVerificationStatus() {
            @Override
            public void onVerificationStatus(VerificationStatus verificationStatus) {

                switch(verificationStatus) {
                    case Verified:
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case Unverified:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(Throwable throwable) { }
        });

        // Region code view for converting the number later. [e.g. (+1), (+31)]
        tbRegionCode = (EditText) findViewById(R.id.tbRegionCode);
        // The country selected provides the ability to choose a different country
        // and change the region code through libphonenumber
        countrySelector = (RelativeLayout) findViewById(R.id.countrySelector);
        // Displaying the country name within the country selector
        tbCountryName = (TextView) findViewById(R.id.tbSelectedCountry);
        // EditText for the user to type his phonenumber or msisdn.
        tbPhoneNumber = (EditText) findViewById(R.id.lblMsisdn);

        // Set example phonenumber for with the country code
        tbPhoneNumber.setHint(getExampleNumberFormatted(countryISO)); // countryIso(default) = NL
        tbPhoneNumber.requestFocus();

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                String regionCode = tbRegionCode.getText().toString();
                if(regionCode.isEmpty()) {
                    isValid = false;
                    tbRegionCode.setError("Select your country or enter your region code");
                }

                String phonenumber = tbPhoneNumber.getText().toString();
                if(phonenumber.isEmpty()) {
                    isValid = false;
                    tbPhoneNumber.setError("Phone number required");
                }

                if(isValid) {
                    RegistrationActivity.phonenumber = tbPhoneNumber.getText().toString();
                    String msisdn = convertToCorrectMsisdn(tbPhoneNumber.getText().toString(), countryISO);
                    HybridMessaging.registerNewUser(msisdn, RegistrationActivity.this);
                }
            }
        });

        tbRegionCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TAG", "onTextChanged");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("TAG", "beforeTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = getCountryNameByRegionCode(s.toString());
                tbCountryName.setText(result);

            }
        });

        countrySelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, CountrySelectorActivity.class);
                intent.putExtra("countryISO", countryISO);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String countryCode = bundle.getString("countryCode");
            if(countryCode != null || !"".equals(countryCode) ) tbRegionCode.setText(countryCode);
            String countryName = bundle.getString("countryName");
            if(countryName != null || !"".equals(countryName)) tbCountryName.setText(countryName);
            String countryISO = bundle.getString("countryISO");

            tbPhoneNumber.setHint(getExampleNumberFormatted(countryISO));
        }

    }

    /**
     * Retrieves an example (mobile)phonenumber based on the country from PhoneNumberLib.
     * After retrieving the number gets formatted as an Mobile number
     *
     * @param countryISO
     * @return formatted number as String
     */
    public String getExampleNumberFormatted(String countryISO) {
        Phonenumber.PhoneNumber numberUtil = PhoneNumberUtil.getInstance().getExampleNumberForType(countryISO, PhoneNumberUtil.PhoneNumberType.MOBILE);

        if(numberUtil == null) return "Unknown";

        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(numberUtil.getCountryCode());
        phoneNumber.setNationalNumber(numberUtil.getNationalNumber());

        return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
    }

    /**
     * Give country code as input and get the ISO back.
     * Locale used in this example is: English
     * @param countryCode
     * @return
     */
    public String getCountryISOForCountryCode(int countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Set<String> set = PhoneNumberUtil.getInstance().getSupportedRegions();

        String[] arr = set.toArray(new String[set.size()]);

        for (int i = 0; i < set.size(); i++) {
            int code = phoneNumberUtil.getCountryCodeForRegion(arr[i]);
            if(code == countryCode) {
                Locale locale = new Locale("en", arr[i]);
                Log.e("Country", locale.getDisplayCountry());
                return arr[i];
            }
        }
        return null;
    }

    /**
     * Get the country code (Locale: English) by the region code
     * Directly set the hint of tbPhoneNumber by formatting the new locale to an example number
     * @param regionCode
     * @return Country name
     */
    private String getCountryNameByRegionCode(String regionCode) {
        if(regionCode == null) return "Invalid region code";
        if("".equals(regionCode)) return "Invalid region code";

        int code = Integer.valueOf(regionCode);

        String newCountryISO = getCountryISOForCountryCode(code);

        tbPhoneNumber.setHint(getExampleNumberFormatted(newCountryISO));
        if(newCountryISO == null) {
            return "Invalid region code";
        }
        return new Locale("en", newCountryISO).getDisplayCountry();
    }

    /**
     * Convert a given MSISDN to a valid MSISDN with leading country code number prefix
     *
     * @param msisdn Given MSISDN
     * @param countryCode Given country code
     * @return The converted valid MSISDN with leading country code number prefix
     */
    private String convertToCorrectMsisdn(String msisdn, String countryCode) {

        if (msisdn == null || msisdn.isEmpty() || countryCode == null || countryCode.isEmpty()) {
            return null;
        }

        Phonenumber.PhoneNumber phoneNumber = null;

        try {
            phoneNumber = PhoneNumberUtil.getInstance().parse(msisdn, countryCode);
        } catch (NumberParseException e) {
            return null;
        }

        String convertedMsisdn = PhoneNumberUtil.getInstance().isValidNumber(phoneNumber) ?  "00" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber() : null;
        return convertedMsisdn;
    }

    @Override
    public void onReceivedRegistration(Registration registration) {
        checkVerificationStatus(RegistrationActivity.this, registration.getStatus());
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Oops something went wrong", Toast.LENGTH_LONG);
    }

    /**
     * Check verification status given from the HybridMessagingSDK
     * Depending on the status the app will open the correct intent or show a Toast
     * @param context
     * @param verificationStatus provided by the HybridMessagingSDK container the status of the user
     */
    public static void checkVerificationStatus(Context context, VerificationStatus verificationStatus) {
        if(verificationStatus == null) {
            Toast.makeText(context, "No connection or SDK not properly configured", Toast.LENGTH_LONG).show();
            return;
        }

        switch (verificationStatus) {
            case Verified:
                Intent intentLogin = new Intent(context, MainActivity.class);
                context.startActivity(intentLogin);
                break;
            case WaitingForPin:
                Intent intentVerifyPin = new Intent(context, RegistrationPinActivity.class);
                intentVerifyPin.putExtra("phonenumber", phonenumber);
                context.startActivity(intentVerifyPin);
                break;
            case Unverified:
                // do nothing
                break;
            case LastPinVerificationFailed:
                Toast.makeText(context, "PIN verification failed", Toast.LENGTH_LONG);
                break;
        }
    }
}
