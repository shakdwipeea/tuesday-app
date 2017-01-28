package com.shakdwipeea.tuesday.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.google.common.base.Strings;

public class SmsReceiver extends BroadcastReceiver {

    public interface SmsListener {
        void onMessageReceived(String message);
    }

    private static SmsListener smsListener;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        if (pdus != null) {
            for (Object pdu : pdus) {
                SmsMessage smsMessage;

                if (Build.VERSION.SDK_INT >= 19) { //KITKAT
                    SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                    smsMessage = msgs[0];
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
                }

                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.

                if (sender.endsWith("TUEOTP")) {
                    String messageBody = smsMessage.getMessageBody();

                    //Pass on the text to our listener.
                    smsListener.onMessageReceived(messageBody);
                }
            }
        }

    }

    public static void bindListener(SmsListener listener) {
        smsListener = listener;
    }
}
