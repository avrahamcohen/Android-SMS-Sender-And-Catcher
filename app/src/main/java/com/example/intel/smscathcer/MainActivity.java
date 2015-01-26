package com.example.intel.smscathcer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/*
    Made by avrahamcohen.ac@gmail.com .

    Overview :

    MainActivity is responsible for the main screen .. DA ..
    In the menu you can choose one contact from your contact list ..
    And then the app automatically send an SMS.
    The SMS contain CMD which you can find in the
    OutgoingSms.java.

*/

public class MainActivity extends Activity {

    private static final int PICK_CONTACT = 1001;
    private static final int RESULT_OK = -1;
    private static final String TAG = "SMS Catcher: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScanService.setScan(false);
        stopService(new Intent(getApplicationContext(), ScanService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_contact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    String id = null;
                    String name = null;
                    String phoneNumber = null;
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    }
                    Cursor contactCursor = getContentResolver().query(contactData,
                            new String[]{ContactsContract.Contacts._ID}, null, null,
                            null);
                    if (contactCursor.moveToFirst()) {
                        id = contactCursor.getString(contactCursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                    }
                    contactCursor.close();
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                            new String[]{id}, null);
                    if (phoneCursor.moveToFirst()) {
                        phoneNumber = phoneCursor
                                .getString(phoneCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phoneCursor.close();
                    Log.v(TAG, "Name: " + name + ", Phone: " + phoneNumber);

                    OutgoingSms sendSms = new OutgoingSms();
                    sendSms.sendSMSMessage(phoneNumber, "");
                }
                break;
        }
    }
}
