package com.cm.hybridmessagingsdk.listener;

import com.cm.hybridmessagingsdk.util.Registration;

/**
 * Created by Dion on 07/01/15
 */
public interface OnRegistrationListener {

    void onReceivedRegistration(Registration registration);

    void onError(Throwable throwable);
}