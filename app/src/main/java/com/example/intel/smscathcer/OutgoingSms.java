package com.example.intel.smscathcer;

import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;

/*
    Made by Made by avrahamcohen.ac@gmail.com .

    Overview :

    OutgoingSms is responsible for sending an SMS

    Input:
    phone: the phone number to send the data.
    data:
            if the data is null, it will send a CMD to trigger the scan.
            if the data is no null it will send the data as is.
*/


public class OutgoingSms extends Activity{

    final SmsManager smsManager = SmsManager.getDefault();
    private static final String TAG = "SMS Catcher: ";
    private static final String CMD = "find me";
    String phoneToFollow = new String();

    protected void sendSMSMessage(String Phone, String Data) {
        Phone.replace("-", "");

        if (Data.isEmpty()) {
            try {
                smsManager.sendTextMessage(Phone, null, CMD, null, null);
                Log.i(TAG, "SMS Sent");
            } catch (Exception e) {
                Log.i(TAG, "Exception sendSMSMessage " + e);
            }
        } else {
            try {
                smsManager.sendTextMessage(Phone, null, Data, null, null);
                Log.i(TAG, "SMS Sent");
            } catch (Exception e) {
                Log.i(TAG, "Exception sendSMSMessage " + e);
            }
            phoneToFollow = Phone;
        }
    }

    public String getPhone() { return this.phoneToFollow;}
    public void setPhone() { this.phoneToFollow = "";}
}
