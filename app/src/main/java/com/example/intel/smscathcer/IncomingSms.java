package com.example.intel.smscathcer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/*
    Made by Made by avrahamcohen.ac@gmail.com .

    Overview :

    IncomingSms is responsible for receiving an SMS

    IncomingSms: register in Android system to catch all the SMS's..
    debugSMS: Printing and Toasting whatever ..
    handleSMS: Is a state machine depends what the device is supposed to do.
        You can look inside the function to see the flow ..
    startScanFlow: Triggers the scan method.
    responseFromUser: If the response from user is not a CMD for triggering ..
    startProcess: Starting the flow to collect all the WiFi data and then start the scan.

*/

public class IncomingSms extends BroadcastReceiver {

    private static final String TAG = "SMS Catcher: ";
    private static final String CMD = "find me";

    public void onReceive(Context context, Intent intent) {
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            Log.i(TAG, "Start Scanning again from Incomming SMS ..");
            return;
        }

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    debugSMS(context, senderNum, message);
                    handleSMS(context, senderNum, message);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Exception smsReceiver" + e);
        }
    }

    public void debugSMS(Context context, String Phone, String Data) {
        Log.i(TAG, "Sender Name: " + Phone + ", Message: " + Data);

        Toast toast = Toast.makeText(context,
                "You got an SMS from :\n" +
                        "Phone Number: " + Phone + "\n" +
                        "Message: " + Data, Toast.LENGTH_LONG);
        toast.show();
    }

    public void handleSMS(Context context, String Phone, String Data) {

        OutgoingSms sendSms = new OutgoingSms();

        String MAC = null;
        Boolean WiFiActive = false;
        String SSID = null;

        /*
            Return with sms with the following:
            1. MAC ID.
            2. WiFi ON / OFF.
            3. SSID if connected ?
         */

        /*
            Start scan for two minutes.
         */

        if (startProcess(Data)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            MAC = wInfo.getMacAddress();
            WiFiActive = wifiManager.isWifiEnabled();
            SSID = wInfo.getSSID();

            /* Return SMS with the following .. */
            String info = "MAC:" + MAC + ",ACTIVE:" + WiFiActive + ",SSID:" + SSID;
            sendSms.sendSMSMessage(Phone, info);

            /* Start scan service */
            ScanService.setScan(true);
            Intent scan = new Intent(context, ScanService.class);
            context.startService(scan);
        }

        if (responseFromUser(Phone)) {
            final String response = "User starts scan process !";
            debugSMS(context, Phone, response);
        }

        sendSms.setPhone();
    }

    /*
    public void startScanFlow(WifiManager wifiManager) {

        Calendar c = Calendar.getInstance();
        int current = c.get(Calendar.SECOND);

        while(true) {
            if (wifiManager.startScan())
                Log.i(TAG, "Scan Completed");
            else Log.i(TAG, "Scan Error");
        }
    }
    */

    public Boolean responseFromUser(String Phone) {
        OutgoingSms sendSms = new OutgoingSms();
        if ((sendSms.getPhone()).equals(Phone)) return true;
        return false;
    }

    public Boolean startProcess(String Data) {
        if (Data.equals(CMD)) return true;
        return false;
    }
}

