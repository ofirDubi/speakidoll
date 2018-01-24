package com.example.ofir.speekidoll;


import android.nfc.NdefMessage;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.os.Build;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextToSpeech t1;
    NfcAdapter nfcAdapter;
    RadioGroup radioSentenceGroup;
    RadioGroup radioLanguageGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioSentenceGroup=(RadioGroup)findViewById(R.id.radioSentenceGroup);
        radioLanguageGroup=(RadioGroup)findViewById(R.id.radioLanguageGroup);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        t1=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

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

        Button clickButton = (Button) findViewById(R.id.changeLnaguageButton);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int selectedId=radioLanguageGroup.getCheckedRadioButtonId();
                RadioButton selectedSentence =(RadioButton)findViewById(selectedId);

                String newLang =  selectedSentence.getText().toString().toUpperCase();
                Log.d("changing language", "***********changing language to: " + newLang);

                int result=t1.setLanguage(newLang.equals("Japanese") ? Locale.JAPANESE : Locale.ENGLISH );
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
        });


        if (nfcAdapter != null && nfcAdapter.isEnabled()) {

        } else {
            finish();
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("*******detected nfc***", "detected");
        int selectedId=radioSentenceGroup.getCheckedRadioButtonId();
        RadioButton selectedSentence =(RadioButton)findViewById(selectedId);
        String text = selectedSentence.getText().toString();
        Toast.makeText(this, "NFc intent received!", Toast.LENGTH_LONG).show();
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }
    }

}
