package com.cmtelecom.example.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cm.example.R;
import com.cm.hybridmessagingsdk.HybridMessaging;
import com.cm.hybridmessagingsdk.listener.OnVerificationStatus;
import com.cm.hybridmessagingsdk.util.VerificationStatus;

public class RegistrationPinActivity extends ActionBarActivity {

    TextView lblInfoSentTo;
    TextView lblWrongNumber;
    EditText tbPincode;
    private static final String TAG = "RegistrationPinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_pin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_toolbar);

        Bundle bundle = getIntent().getExtras();
        String phonenumber = bundle.getString("phonenumber");

        lblInfoSentTo = (TextView) findViewById(R.id.lblSentTo);
        StringBuilder sb = new StringBuilder();
        sb.append("Activation code has been sent to <b>").append(phonenumber).append("</b>. Enter the code below to activate your account");
        lblInfoSentTo.setText(Html.fromHtml(sb.toString()));

        lblWrongNumber = (TextView) findViewById(R.id.lblWrongNumber);
        lblWrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationPinActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        tbPincode = (EditText) findViewById(R.id.tbPincode);
        tbPincode.addTextChangedListener(getTextWatcher());

    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("asd", "s.length" + s.length() + " start" + start + " before" + before + " after" + count);
                if(s.length() == 4 && start == 3) {
                    onActivatePinButtonClicked(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    /**
     * Event handler method for when a Click-event has been triggered by the user clicking on the ActivatePin button
     *
     * @param view Given view object
     */
    public void onActivatePinButtonClicked(View view) {

        Log.d(TAG, "Click event occurred on ActivatePin button");

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Loading...", "Activating account...", true);
        progressDialog.setCancelable(true);

        Editable editablePinText = tbPincode.getText();
        if (editablePinText == null) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Oops something went wrong", Toast.LENGTH_LONG);
            return;
        }

        String givenPin = editablePinText.toString();
        if (givenPin == null || givenPin.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Pincode is empty", Toast.LENGTH_LONG);
            return;
        }

        Log.d(TAG, "Click event occurred on ActivatePin button - GivenPin: " + givenPin + "...");

        HybridMessaging.registerUserByPincode(givenPin, new OnVerificationStatus() {
            @Override
            public void onVerificationStatus(VerificationStatus status) {
                RegistrationActivity.checkVerificationStatus(RegistrationPinActivity.this, status);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable) {
                progressDialog.dismiss();
            }
        });
    }
}
