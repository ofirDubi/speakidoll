package com.example.ofir.speekidoll;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class ManageStickersActivity extends AppCompatActivity {
    private TextToSpeech t1;
    private NfcAdapter nfcAdapter;
    private EditText sentence_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stickers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        sentence_text = (EditText)findViewById(R.id.sentence_editText);
        t1=new TextToSpeech(ManageStickersActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=t1.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                        t1.setLanguage(Locale.US);
                    }
                    else{
                        Log.d("*******allgood","*************************** all good ***************************");
                        // String text = "Activated";

                        // t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("*******detected nfc***", "detected");
        String textToConvert = sentence_text.getText().toString();

        if(textToConvert ==null){
            textToConvert = "";
        }
        Log.d("****going to talk ***", "String is: "+textToConvert);
        Toast.makeText(this, "NFc intent received! - "+textToConvert, Toast.LENGTH_LONG).show();

        String uid = StickerData.generateUid();

        HashMap<String, String> myHashRender = new HashMap();
        String destinationFileName = "/sdcard/"+uid+".wav";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, textToConvert);
        t1.synthesizeToFile(textToConvert, myHashRender, destinationFileName);

        //now i need to write the uid to the nfc tag
        //t1.speak(textToConvert, TextToSpeech.QUEUE_FLUSH, null);
        Toast.makeText(this, "talked", Toast.LENGTH_LONG).show();

        if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
                Log.d("*******nfc message:***", messages.toString());
            }
        }else{
            Log.d("*****message failed****", "intent is: " + intent.getAction());
        }
    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter,
                null);

        super.onResume();
    }

    @Override
    protected void onPause() {

        nfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.nfc_fragment, container,
                    false);
            return rootView;
        }
    }

}
