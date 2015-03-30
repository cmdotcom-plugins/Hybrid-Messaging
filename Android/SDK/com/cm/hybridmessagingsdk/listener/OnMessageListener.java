package com.cm.hybridmessagingsdk.listener;

import com.cm.hybridmessagingsdk.util.Message;

/**
 * Created by Dion on 07/01/15
 */
public interface OnMessageListener {

    void onReceivedMessages(Message[] messages);

    void onError(Throwable throwable);
}
