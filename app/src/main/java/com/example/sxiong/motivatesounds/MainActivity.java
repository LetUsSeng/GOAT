package com.example.sxiong.motivatesounds;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import android.hardware.Sensor;
import android.hardware.SensorManager;



/**
 *
 *  Purpose: GOAT
 *
 * Created By: Seng Ye Xiong
 *
 */
public class MainActivity extends AppCompatActivity {


    private TextView txtSpeechInput;
    //play button
    ImageView playButton;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });




        //playButton
        playButton = (ImageView) findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                goatScream();
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                goatScream();
            }
        });

    }


    /**
     *
     * Make the goat scream
     *
     */
    public void goatScream()
    {
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.screaming_goat);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();

            };
        });
        v.vibrate(1000);
    }

    /**
     *
     * code obtain from http://stackandroid.com/tutorial/android-speech-to-text-tutorial/
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
     *
     *
     * code obtain from http://stackandroid.com/tutorial/android-speech-to-text-tutorial/
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


                    goatRecognize(result.get(0));
                }
                break;
            }

        }
    }

    /**
     * function
     * Find the word goat
     *
     * @param words
     */
    public void goatRecognize(String words){

        boolean isGoat = false;

        //split string into array
        String[] goatWords = words.split("[/ ]", -1);

        for(int i = 0; i < goatWords.length && !isGoat; i++)
        {
            try{
                //check if the word goat is present in a sentence
                if(goatWords[i].toLowerCase().equals("goat")){
                    isGoat = true;
                    //System.out.println("There is a goat");

                }
                //check if first letter match that of goat
                if(goatWords[i].substring(0,1).toLowerCase().equals("g")
                        && goatWords[i + 1].substring(0,1).toLowerCase().equals("o")
                        && goatWords[i + 2].substring(0,1).toLowerCase().equals("a")
                        && goatWords[i + 3].substring(0,1).toLowerCase().equals("t")){
                    isGoat = true;
                    //System.out.println("There is a goat");
                }
                //System.out.println(goatWords[i].substring(0,1));
            }
            catch (Exception e)
            {

            }
            //screams if condition of goat matches
            if(isGoat){
                goatScream();
            }



        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
