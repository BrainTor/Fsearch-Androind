package com.example.fsearch.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.example.fsearch.Preference;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

public class MMS {
    Context context;
    Preference preference;

    public MMS(Context context) {
        this.context = context;
    }

    public void sendMMS(Bitmap bitmap, String text) {
        preference = new Preference(context);
        preference.loadPreference();
        Settings sendSettings = new Settings();
        sendSettings.setMmsc(preference.MMSC);
        sendSettings.setProxy(preference.MMSproxy);
        sendSettings.setPort(preference.MMSport);
        sendSettings.setGroup(true);
        sendSettings.setDeliveryReports(false);
        sendSettings.setSplit(false);
        sendSettings.setSplitCounter(false);
        sendSettings.setStripUnicode(false);
        sendSettings.setSignature("");
        sendSettings.setSendLongAsMms(true);
        sendSettings.setSendLongAsMmsAfter(3);
        sendSettings.setAccount("jklinker1@gmail.com");
        sendSettings.setRnrSe(null);
        Transaction sendTransaction = new Transaction(context, sendSettings);
        Message mMessage = new Message(text, preference.MMSnumber);
        mMessage.setImage(bitmap);   // not necessary for voice or sms messages
        mMessage.setType(Message.TYPE_SMSMMS);  // could also be Message.TYPE_VOICE
        sendTransaction.sendNewMessage(mMessage, Thread.currentThread().getId());
        Log.d("fsearch", "Send MMS to:" + preference.MMSnumber);
    }

}
