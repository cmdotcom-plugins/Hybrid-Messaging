package com.cm.hybridmessagingsdk.util;

public enum VerificationStatus {
    Unverified(1), Verified(2), LastPinVerificationFailed(3), WaitingForPin(4);
    private int value;

    public int getValue() {
        return value;
    }

    private VerificationStatus(int value) {
        this.value = value;
    }
}