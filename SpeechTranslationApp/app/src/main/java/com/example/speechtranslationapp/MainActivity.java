package com.example.speechtranslationapp;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private final String requestURL = "https://www.googleapis.com/language/translate/v2?key=";
    //TODO Add your own key
    private final String requestKey = "\n";
    private final String requestSrc = "&source=";
    private final String requestDst = "&target=";
    private final String requestTxt = "&q=";
    private final String formatTxt = "&format=text";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeechInput = (TextView) findViewById(R.id.voiceInput);
        tts = new TextToSpeech(this, this);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }


    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    getTranslation(result.get(0),"en", "fr");
                    }
            }
            break;
        }

    }



    private void speak(String text){
        Locale aLocale = new Locale("fr","FR");
        tts.setLanguage(aLocale);
        tts.speak(text,TextToSpeech.QUEUE_FLUSH, null, null);
    }

    //TODO
    public void getTranslation( String txt, String src, String dst ){
        String request = requestURL + requestKey + requestSrc + src + requestDst + dst+ formatTxt + requestTxt + txt;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, request, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response

                        JSONObject test = null;
                        try {
                            test = response.getJSONObject("data");
                            txtSpeechInput.setText(test.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray translations = null;
                        try {
                            translations = test.getJSONArray("translations");
                            txtSpeechInput.setText(translations.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject translated_text = translations.getJSONObject(0);
                            String final_translation = translated_text.getString("translatedText");
                            txtSpeechInput.setText(final_translation);
                            String translated = txtSpeechInput.getText().toString().trim();
                            speak(translated);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", "nope");
                    }
                }
        );
        queue.add(getRequest);
    }



    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This language is not supported", Toast.LENGTH_LONG).show();
            }
            else{
                promptSpeechInput();
            }
        }else{
            Toast.makeText(this, "Initialization failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }}