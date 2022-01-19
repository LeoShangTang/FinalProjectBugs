/*-------------NOTES----------------

Only works on |Pixel 2 XL API 30| for now...

Something breaking my code...

*/
package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //private static final long startTimeinMilliseconds = 15000; // 600000
    //private static final long startBreakTimerMilliseconds = 10000; // 300000

    private long startTimeinMilliseconds;
    private long startBreakTimerMilliseconds;
    private long timeLeftMilliseconds;
    private long endTime;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;
    private String breakOrStudyTimer = "study";

    private TextView countDownView;

    private Button startPauseButton;
    private Button resetButton;
    private Button breakStudyTimeButton;
    private ImageButton settingsButton;
    private Button studySetButton;
    private Button breakSetButton;

    private SoundPool soundPool;
    private int necoarc;

    private EditText studyEditTextTimer;
    private EditText breakEditTextTimer;

    //-----------------------------------ACTIONS_MADE------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countDownView = findViewById(R.id.countdown_text);

        startPauseButton = findViewById(R.id.start_pause_button);
        resetButton = findViewById(R.id.reset_button);
        breakStudyTimeButton = findViewById(R.id.break_time_button);
        settingsButton = findViewById(R.id.setting_Button);
        studySetButton = findViewById(R.id.study_timer_set);
        breakSetButton = findViewById(R.id.break_timer_set);

        studyEditTextTimer = findViewById(R.id.study_timer_input);
        breakEditTextTimer = findViewById(R.id.break_timer_input);

        //-----------------------------------ACTIONS_OF_BUTTONS------------------------------------------

        //-----------------------------------Settings SET button for study timer------------------------------------------
        studySetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String input = studyEditTextTimer.getText().toString();

//            if (input.length() == 0) {
//
//                Toast.makeText(MainActivity.this, "Can't Be Empty", Toast.LENGTH_SHORT).show();
//               return;
//
//            }
//            long studyMillisecondsInput = Long.parseLong(input) * 60000;
//            if(studyMillisecondsInput == 0){
//
//                Toast.makeText(MainActivity.this, "Enter a number", Toast.LENGTH_SHORT).show();
//                return;
//
//            }
                long studyMillisecondsInput = Long.valueOf(input) * 60000;
                setStudyTime(studyMillisecondsInput);
                studyEditTextTimer.setText("");

            }
        });

//-----------------------------------Settings SET button for break timer------------------------------------------
        breakSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = breakEditTextTimer.getText().toString();


//                if (input.length() == 0) {
//
//                    Toast.makeText(MainActivity.this, "Can't Be Empty", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//                long breakMillisecondsInput = Long.parseLong(input) * 60000;
//                if(breakMillisecondsInput == 0){
//
//                    Toast.makeText(MainActivity.this, "Enter a number", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
                long breakMillisecondsInput = Long.valueOf(input) * 60000;
                setStudyTime(breakMillisecondsInput);
                breakEditTextTimer.setText("");

            }
        });

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

        //---------------ALARM SOUND
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            // or stream.music...
        }
        necoarc = soundPool.load(this, R.raw.necoarc, 1);

       updateCountDownText(); //WATCH OUTTTTTTTTTTTTTTTTTTT
    }

    //-----------------------------------METHODS------------------------------------------

    //--------------SET CUSTOM STUDY TIME
    private void setStudyTime(long milliseconds1) {
        startTimeinMilliseconds = milliseconds1;
        resetTimer();
       closeKeyboard();
    }

    //--------------SET CUSTOM BREAK TIME
    private void setBreakTime(long milliseconds2) {
        startBreakTimerMilliseconds = milliseconds2;
        resetTimer();
        closeKeyboard();
    }
    // ---------------UPDATES TEXT AND SETS FORMAT
    private void updateCountDownText(){

        int hours = (int) (timeLeftMilliseconds/1000)/3600;
        int minutes = (int) ((timeLeftMilliseconds/1000)%3600)/60;
        int seconds = (int) (timeLeftMilliseconds/1000)%60;

        String timeLeftFormatted;

        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        countDownView.setText(timeLeftFormatted);

    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                soundPool.play(necoarc, 1, 1, 0, 0, 1);

                if(breakOrStudyTimer == "break"){ // if break timer ends, automatically start study timer

                    timerRunning = false;
                    studyTimerStart();
                    startTimer();
                    breakOrStudyTimer = "study";

                    updateButtons();
                    updateCountDownText();

                }else{// if start timer ends, automatically start break timer

                    timerRunning = false;
                   breakOrStudyTimer = "break";
                    breakTimerStart();
                    startTimer();

                    updateButtons();
                    updateCountDownText();

                }
            }
        }
        .start();
       timerRunning = true;
       updateButtons();
    }

    // --------------- PAUSES TIMER
    private void pauseTimer(){

        cancelTimerBugFix();
        timerRunning = false;
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

        updateButtons();

    }

    // --------------- BREAK TIMER SWITCHES
    private void breakTimerStart(){

        endTime = System.currentTimeMillis() + timeLeftMilliseconds;

        timeLeftMilliseconds = startBreakTimerMilliseconds; // setting timer to the break time

        updateCountDownText();
        cancelTimerBugFix();

        breakOrStudyTimer = "break";
        timerRunning = false;

        updateButtons();

    }

    // --------------- STUDY TIMER SWITCHES BACK
    private void studyTimerStart(){

        endTime = System.currentTimeMillis() + timeLeftMilliseconds;

        timeLeftMilliseconds = startTimeinMilliseconds; // setting timer back to study time

        updateCountDownText();
        cancelTimerBugFix();
        breakOrStudyTimer = "study";
        timerRunning = false;
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

   // @Override
//    protected void onStop() {
//        super.onStop();
//
//        SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preference.edit();
//
//        editor.putLong("endTime", endTime);
//        editor.putLong("millisecondsLeft", timeLeftMilliseconds);
//        editor.putLong("breakTimeMillisecondsLeft", startBreakTimerMilliseconds);
//        editor.putBoolean("timerRunning", timerRunning);
//        editor.putString("breakOrStudyTimer", breakOrStudyTimer);
//
//        editor.apply();
//
//    }
//
//    @Override
//    protected void onStart() {
//
//        super.onStart();
//
//        SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);
//
//        if(breakOrStudyTimer == "break"){
//
//            timeLeftMilliseconds = preference.getLong("millisecondsLeft", startBreakTimerMilliseconds);
//            breakOrStudyTimer = preference.getString("breakOrStudyTimer", "break");
//
//        }else if(breakOrStudyTimer == "study"){
//
//            timeLeftMilliseconds = preference.getLong("millisecondsLeft", startTimeinMilliseconds);
//            breakOrStudyTimer = preference.getString("breakOrStudyTimer", "study");
//
//        }
//
//        timerRunning = preference.getBoolean("timerRunning", false);
//
//
//        updateCountDownText();
//        updateButtons();
//
//        if(timerRunning){
//
//            endTime = preference.getLong("endTime", 0);
//            timeLeftMilliseconds = endTime - System.currentTimeMillis();
//
//            if(timeLeftMilliseconds < 0){
//
//                timeLeftMilliseconds = 0;
//                timerRunning = false;
//                updateCountDownText();
//                updateButtons();
//
//            }else{
//
//                startTimer();
//
//            }
//
//        }
//
//    }
}