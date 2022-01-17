/*-------------NOTES----------------

Only works on |Pixel 2 XL API 30| for now...

Fix updateButton method ----> how??!?!?!?!

BIGGEST PROBLEM TO FIX:

        Press Study time ---> Press Start ---> Press Reset

This causes the time to be reset to the study timer (10 minuites) and not to break timer.
The study and breaktime button is also stuck at the text study because of this bug.

The cause for these errors are definitely related to my boolean system...

*/
package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Times
    private static final long startTimerMilliseconds = 600000;
    private static final long startBreakTimerMilliseconds = 300000;

    private long timeLeftMilliseconds = startTimerMilliseconds;
    private long timeLeftBreakTimer = startBreakTimerMilliseconds;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;
    private boolean breakTimerRunning; // do ittttt


    // Making sure that countdown text updates and is viewable
    private TextView countDownView;

    // Buttons
    private Button startPauseButton;
    private Button resetButton;
    private Button breakStudyTimeButton;

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


        //-----------------------------------ACTIONS_OF_BUTTONS------------------------------------------
        //---------------START AND PAUSE BUTTON
        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerRunning){
                    pauseTimer();
                }else{
                    startTimer();
                }
            }
        });

        //---------------BREAK AND STUDY BUTTON
        breakStudyTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(breakTimerRunning == true||timeLeftMilliseconds == startBreakTimerMilliseconds){
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
            public void onTick(long timeLeftUntilFinished) { // COUNTDOWN

                timeLeftMilliseconds = timeLeftUntilFinished;
                updateCountDownText();

            }
            @Override
            public void onFinish() { // What Timer does after it's done

                timerRunning = false;
                breakTimerRunning = true;

                timeLeftMilliseconds = startBreakTimerMilliseconds; // Sets back to breakTimer (SEPERATE METHOD TO BE ADDED ON LATER)

                startPauseButton.setText("Start");
               //updateButtons();
            }
        }
        .start();
        // Add if else statement for breakTimer????????
        timerRunning = true;
        breakTimerRunning = false; // ????

        startPauseButton.setText("Pause");
        //updateButtons();
    }

    // --------------- PAUSES TIMER
    private void pauseTimer(){

        cancelTimerBugFix();

        timerRunning = false;
        breakTimerRunning = false; // KEEP IN EYE ON

        startPauseButton.setText("Start");

        //updateButtons();
    }

    // --------------- RESETS TIMER
    //LOTS OF BUGS IN RESET METHOD
    private void resetTimer(){

        // probably has bugs but I don't know
        if(breakTimerRunning == true ||timeLeftMilliseconds == startBreakTimerMilliseconds){

            timeLeftMilliseconds = startBreakTimerMilliseconds;

        }else if(breakTimerRunning == false||timeLeftMilliseconds == startTimerMilliseconds){ //problems

            timeLeftMilliseconds = startTimerMilliseconds;

        }

        updateCountDownText();

        cancelTimerBugFix();

        timerRunning = false;
        breakTimerRunning = false; // KEEP IN EYE ON
        startPauseButton.setText("Start");

        //updateButtons();

    }

    // --------------- BREAK TIMER STARTS
    private void breakTimerStart(){

        timeLeftMilliseconds = startBreakTimerMilliseconds; // setting timer to the break time

        updateCountDownText();
        cancelTimerBugFix();

        //Setting booleans
        breakTimerRunning = true;
        timerRunning = false;

        breakStudyTimeButton.setText("Study Time");
        startPauseButton.setText("Start");

        //updateButtons()

    }

    // --------------- STUDY TIMER STARTS AGAIN
    private void studyTimerStart(){

        timeLeftMilliseconds = startTimerMilliseconds; // setting timer back to study time

        updateCountDownText();
        cancelTimerBugFix();

        //Setting booleans
        breakTimerRunning = false;
        timerRunning = false; // Be careful --- Change to false maybe..

        breakStudyTimeButton.setText("Break Time");
       startPauseButton.setText("Start");

      //updateButtons()

    }

    // --------------- UPDATES BUTTONS
    //BUGS BUGS BUGS BUGS
    private void updateButtons(){

        if(timerRunning){
            startPauseButton.setText("Pause");
        }else{
            startPauseButton.setText("Start");
        } if(breakTimerRunning == true||timeLeftMilliseconds == startBreakTimerMilliseconds){
            breakStudyTimeButton.setText("Break Time");
            startPauseButton.setText("Start");
        }else{
            breakStudyTimeButton.setText("Study Time");
            startPauseButton.setText("Start");
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
        timeLeftBreakTimer = savedInstanceState.getLong("breakTimeLeft");
        timerRunning = savedInstanceState.getBoolean("timerRunning");
        breakTimerRunning = savedInstanceState.getBoolean("breakTimerRunning");

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
        editor.putLong("breakTimeLeft", timeLeftBreakTimer);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putBoolean("breakTimerRunning", breakTimerRunning);

        editor.apply();

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);

        timeLeftMilliseconds = preference.getLong("millisecondsLeft", startTimerMilliseconds);
        timeLeftBreakTimer = preference.getLong("breakTimeLeft", startBreakTimerMilliseconds);

        timerRunning = preference.getBoolean("timerRunning", false);
        breakTimerRunning = preference.getBoolean("breakTimerRunning", false);


        updateCountDownText();
//        updateButtons();

    }
}