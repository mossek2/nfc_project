package com.example.kevin.nfcappdemo;

        import android.app.Activity;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.nfc.NdefMessage;
        import android.nfc.NdefRecord;
        import android.nfc.NfcAdapter;
        import android.nfc.NfcAdapter.CreateNdefMessageCallback;
        import android.nfc.NfcEvent;
        import android.os.Bundle;
        import android.os.Parcelable;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;
        import java.nio.charset.Charset;

        import static android.nfc.NdefRecord.createMime;


public class MainActivity extends Activity implements CreateNdefMessageCallback {
    NfcAdapter mNfcAdapter;
    TextView textView;
    EditText editText;
    Button savey;
    String msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.textView);
        final EditText editText = (EditText) findViewById(R.id.message_box);
        final Button savey = (Button) findViewById(R.id.save);

        ///////LOOK HERE ED////
        savey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                msg = editText.getText().toString();
            }
        });

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else {
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = msg;
        NdefMessage msgOut = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.com.example.kevin.nfcappdemo", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        ,NdefRecord.createApplicationRecord("com.example.kevin.nfcappdemo")
                });
        return msgOut;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        editText.setVisibility(View.INVISIBLE);
        savey.setVisibility(View.INVISIBLE);
        textView.setText(new String(msg.getRecords()[0].getPayload()));
    }
}