package com.cm.hybridmessagingsdk.listener;

import com.cm.hybridmessagingsdk.util.VerificationStatus;

public interface OnVerificationStatus {

    void onVerificationStatus(VerificationStatus status);

    void onError(Throwable throwable);
}
