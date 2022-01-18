/*-------------NOTES----------------

Only works on |Pixel 2 XL API 30| for now...

Fix updateButton method ----> how??!?!?!?!

BIGGEST PROBLEM TO FIX:

    Start ---> Pause ----> Start

    NOW PAUSE BUTTON NO LONGER WORKS
    This also messes up with the reset button, causing some other weird bugs but this should be fixed when pause button works again

    BUT, without pressing pause, reset button now properly resets back to conresponding timers

*/
package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Times
    private static final long startTimeinMilliseconds = 600000; // 600000
    private static final long startBreakTimerMilliseconds = 300000; // 300000

    private long timeLeftMilliseconds = startTimeinMilliseconds;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;
    private String breakOrStudyTimer = "study";

    // Making sure that countdown text updates and is viewable
    private TextView countDownView;

    // Buttons
    private Button startPauseButton;
    private Button resetButton;
    private Button breakStudyTimeButton;

    private ImageButton settingsButton;

    private SoundPool soundPool;
    private int necoarc;

    //-----------------------------------ACTIONS_MADE------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting variables to their corresponding buttons and texts
        countDownView = findViewById(R.id.countdown_text);

        startPauseButton = findViewById(R.id.start_pause_button);
        resetButton = findViewById(R.id.reset_button);
        breakStudyTimeButton = findViewById(R.id.break_time_button);
        settingsButton = findViewById(R.id.setting_Button);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
        }else{

            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
            // or stream.music...

        }

        //-----------------------------------ACTIONS_OF_BUTTONS------------------------------------------

        //-----------------------------------SETTINGS BUTTON------------------------------------------
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

            }
        });

        //---------------START AND PAUSE BUTTON
        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerRunning){

                    pauseTimer();

                }else if(timerRunning == false){

                    startTimer();

                }
            }
        });

        //---------------BREAK AND STUDY BUTTON
        breakStudyTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(breakOrStudyTimer == "break" ){

                    studyTimerStart();

                }else{

                    breakTimerStart();

                }
            }
        });

        //---------------RESET BUTTON
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
        updateCountDownText();
    }

    //-----------------------------------METHODS------------------------------------------

    // ---------------UPDATES TEXT AND SETS FORMAT
    private void updateCountDownText(){

        int minutes = (int) (timeLeftMilliseconds/1000)/60;
        int seconds = (int) (timeLeftMilliseconds/1000)%60;

        String timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        countDownView.setText(timeLeftFormat);

    }

    // ---------------STARTS TIMER
    private void startTimer(){

        countDownTimer = new CountDownTimer(timeLeftMilliseconds, 1000) {

            @Override
            public void onTick(long timeLeftUntilFinished) { // COUNTDOWN HAPPENS

                timerRunning = true;

                timeLeftMilliseconds = timeLeftUntilFinished;
                updateCountDownText();

            }
            @Override
            public void onFinish() { // What Timer does after it's done

                if(breakOrStudyTimer == "break"){ // if break timer ends, automatically start study timer

                    timerRunning = true;
                    breakOrStudyTimer = "study";
                    startTimer();

                    //startPauseButton.setText("Pause");

                    updateButtons();

                    updateCountDownText();

                }else{// if start timer ends, automatically start break timer

                    timerRunning = true;
                    breakOrStudyTimer = "break";
                    breakTimerStart();
                    startTimer();

                    //startPauseButton.setText("Pause");

                    updateButtons();

                    updateCountDownText();

                }

            }
        }
        .start();

       timerRunning = true;
       //startPauseButton.setText("Pause");

        updateButtons();
    }

    // --------------- PAUSES TIMER
    private void pauseTimer(){

        cancelTimerBugFix();

        timerRunning = false;

        //startPauseButton.setText("Start");

        updateButtons();
    }

    // --------------- RESETS TIMER
    //LOTS OF BUGS IN RESET METHOD
    private void resetTimer(){

        if(breakOrStudyTimer == "break"){

            timeLeftMilliseconds = startBreakTimerMilliseconds;

        }else if (breakOrStudyTimer == "study"){

            timeLeftMilliseconds = startTimeinMilliseconds;

        }

        updateCountDownText();
        cancelTimerBugFix();

        timerRunning = false;
        //startPauseButton.setText("Start");

        updateButtons();

    }

    // --------------- BREAK TIMER SWITCHES
    private void breakTimerStart(){

        timeLeftMilliseconds = startBreakTimerMilliseconds; // setting timer to the break time

        updateCountDownText();
        cancelTimerBugFix();

        breakOrStudyTimer = "break";
        timerRunning = false;

        //breakStudyTimeButton.setText("Study Time");
        //startPauseButton.setText("Start");

        updateButtons();

    }

    // --------------- STUDY TIMER SWITCHES BACK
    private void studyTimerStart(){

        timeLeftMilliseconds = startTimeinMilliseconds; // setting timer back to study time

        updateCountDownText();
        cancelTimerBugFix();


        breakOrStudyTimer = "study";
        timerRunning = false;

//        breakStudyTimeButton.setText("Break Time");
//       startPauseButton.setText("Start");

      updateButtons();

    }

    // --------------- UPDATES BUTTONS
    //BUGS BUGS BUGS BUGS
    private void updateButtons(){

        if(timerRunning){

            startPauseButton.setText("Pause");

        }else if(timerRunning == false){

            startPauseButton.setText("Start");

        }

        if(breakOrStudyTimer == "break"){

            breakStudyTimeButton.setText("Study TIme");

        }else if(breakOrStudyTimer == "study"){

            breakStudyTimeButton.setText("Break Time");

        }

    }

    // --------------- CANCEL BUTTON BUG FIXES
    private void cancelTimerBugFix(){
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    //-------------------------SAVING DATA------------------------------------------

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        timeLeftMilliseconds = savedInstanceState.getLong("millisecondsLeft");
        timerRunning = savedInstanceState.getBoolean("timerRunning");


        updateCountDownText();
//        updateButtons();

        if(timerRunning){

            startTimer();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();

        editor.putLong("millisecondsLeft", timeLeftMilliseconds);
        editor.putLong("breakTimeMillisecondsLeft", startBreakTimerMilliseconds);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putString("breakOrStudyTimer", breakOrStudyTimer);

        editor.apply();

    }

    @Override
    protected void onStart() {

        super.onStart();

        SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);

        timeLeftMilliseconds = preference.getLong("millisecondsLeft", startTimeinMilliseconds);

        timerRunning = preference.getBoolean("timerRunning", false);

        breakOrStudyTimer = preference.getString("breakOrStudyTimer", "study");

        updateCountDownText();
        updateButtons();

        if(timerRunning){


        }

    }
}