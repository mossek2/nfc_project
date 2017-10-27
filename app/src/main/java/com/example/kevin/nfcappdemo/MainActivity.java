package com.example.kevin.nfcappdemo;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static android.nfc.NdefRecord.createMime;


public class MainActivity extends Activity implements
        NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback{

    TextView textInfo;
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInfo = (TextView) findViewById(R.id.info);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter == null) {
            Toast.makeText(MainActivity.this,
                    "NFC is not available",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "Set Callback(s)",
                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this,this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //NdefRecord rtdUriRecord = NdefRecord.createUri("http://google.com");
        String text = ("Alright Ed. It worked\n\n" +
                "Arrival Time: " + System.currentTimeMillis());
        NdefMessage ndefMessageOut = new NdefMessage(
                new NdefRecord[] { createMime(
                        "com.example.kevin.nfcappdemo", text.getBytes())
                });
        return ndefMessageOut;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] parcelables =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage inNdeMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdeRecords = inNdeMessage.getRecords();
            NdefRecord ndefRecord_0 = inNdeRecords[0];
            String inMsg = new String(ndefRecord_0.getPayload());
            textInfo.setText(inMsg);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }


    @Override
    public void onNdefPushComplete(NfcEvent event) {
        final String eventString = "onNodePushComplete\n" + event.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        eventString,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
