package com.example.intel.smscathcer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ScanService extends Service {

    private static final String TAG = "Scan Service: ";
    private final int NOTIFICATION_ID=10643668;
    private NotificationCompat.Builder mNbuilder=null;
    public static Boolean runScan = true;
    WifiManager wifiManager;

    @Override
    public void onCreate () {
        super.onCreate();
        mNbuilder = new NotificationCompat.Builder(this);
        mNbuilder.setSmallIcon(R.drawable.ic_launcher);
        mNbuilder.setPriority(Notification.PRIORITY_MAX);
        mNbuilder.setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("SMS Catcher");
        inboxStyle.setSummaryText("Scanning flow ..");
        mNbuilder.setStyle(inboxStyle);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNbuilder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mNbuilder.build());

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(receiver, mIntentFilter);

        wifiManager.startScan();
        Log.i(TAG, "Start Scanning ..");
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static void setScan(boolean flag) {
        runScan = flag;
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                if (runScan) {
                    Log.i(TAG, "Start Scanning again..");
                    wifiManager.startScan();
                }
                else {
                    Log.i(TAG, "Stop Scanning.");
                    unregisterReceiver(receiver);
                }
            }
        }
    };
}

