package com.example.firebasequiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userID;

    private ArrayList<Question> dataset = new ArrayList<Question>();

    private TextView currentScoreTextView;
    private TextView timerTextView;
    private TextView questionTextView;
    private TextView questionNumTextView;
    private RadioGroup questionsRadioGroup;
    private RadioButton radioAnswer0Button;
    private RadioButton radioAnswer1Button;
    private RadioButton radioAnswer2Button;
    private RadioButton radioAnswer3Button;
    private Button answerButton;

    private int questionCounter=0;
    private int score=0;
    private CountDownTimer cTimer = null;
    private int timeLeft = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        userID = mAuth.getCurrentUser().getUid();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        // register PhoneStateListener
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {
                //  React to incoming call.
                // If phone ringing
                if(state==TelephonyManager.CALL_STATE_RINGING)
                {
                    Log.d("RINGING", "RINGING");
                    Log.d("TIMER", "Time left: " + String.valueOf(timeLeft));
                    cancelTimer();
                }
                // If incoming call received
                if(state==TelephonyManager.CALL_STATE_OFFHOOK)
                {
                    Log.d("INCALL", "INCALL");
                    Log.d("TIMER", "Time left: " + String.valueOf(timeLeft));
                    cancelTimer();
                }
                //If no call or ringing
                if(state==TelephonyManager.CALL_STATE_IDLE)
                {
                    if(timeLeft != 60)
                    {
                        Log.d("IDLE", "IDLE");
                        Log.d("TIMER", "Time left: " + String.valueOf(timeLeft));
                        startTimer();
                    }
                }
            }
        };
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);

        loadUI();
        loadDataset();
    }

    private void loadUI() {
        currentScoreTextView = findViewById(R.id.CurrentScoreTextView);
        timerTextView = findViewById(R.id.TimerTextView);
        questionTextView = findViewById(R.id.QuestionTextView);
        questionNumTextView = findViewById(R.id.QuestionNumTextView);
        questionsRadioGroup = findViewById(R.id.QuestionRadioGroup);
        radioAnswer0Button = findViewById(R.id.Radio0);
        radioAnswer1Button = findViewById(R.id.Radio1);
        radioAnswer2Button = findViewById(R.id.Radio2);
        radioAnswer3Button = findViewById(R.id.Radio3);
        answerButton = findViewById(R.id.AnswerButton);
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId=questionsRadioGroup.getCheckedRadioButtonId();
                RadioButton radioFinalAnswerButton=(RadioButton)findViewById(selectedId);

                if(radioFinalAnswerButton.getText().equals(dataset.get(questionCounter).correct_answer)) {
                    score += 10;
                    radioFinalAnswerButton.setBackgroundColor(Color.GREEN);
                }
                else {
                    if(score >= 10) {
                        score -= 10;
                    }
                    radioFinalAnswerButton.setBackgroundColor(Color.RED);
                }

                CountDownTimer wTimer = null;
                wTimer = new CountDownTimer(500, 500) {
                    public void onTick(long millisUntilFinished) { }
                    public void onFinish() {
                        questionCounter++;
                        radioFinalAnswerButton.setBackgroundColor(Color.WHITE);
                        showQuestion();
                    }
                };
                wTimer.start();
            }
        });
    }

    private void loadDataset() {
        DatabaseReference myRef = database.getReference().child("db").child("questions");
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataset.size() != 0) {
                    dataset.clear();
                }
                for (DataSnapshot questionSnapshot: dataSnapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    dataset.add(question);
                }
                randomizeQuestions();
                showQuestion();
                startTimer();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FIREBASE", "Failed to read value.", error.toException());
            }
        });
    }

    private void randomizeQuestions() {
        Collections.shuffle(dataset);
        for(int i = 19; i > 9; i--) {
            dataset.remove(i);
        }
    }

    private void showQuestion() {
        if(dataset.size() > (questionCounter+1)) {
            currentScoreTextView.setText("Score: " + String.valueOf(score));
            questionTextView.setText(dataset.get(questionCounter).question);
            questionNumTextView.setText(String.valueOf(questionCounter + 1) + " / 10");

            ArrayList<String> qa = new ArrayList<>();
            qa.add(dataset.get(questionCounter).correct_answer);
            qa.add(dataset.get(questionCounter).incorrect_answer1);
            qa.add(dataset.get(questionCounter).incorrect_answer2);
            qa.add(dataset.get(questionCounter).incorrect_answer3);
            Collections.shuffle(qa);
            radioAnswer0Button.setText(qa.get(0));
            radioAnswer1Button.setText(qa.get(1));
            radioAnswer2Button.setText(qa.get(2));
            radioAnswer3Button.setText(qa.get(3));
        }
        else {
            endRound();
        }
    }

    private void startTimer() {
        cTimer = new CountDownTimer(timeLeft*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeft = (int)(millisUntilFinished / 1000);
                timerTextView.setText("Time Left: " + String.valueOf(timeLeft));
            }
            public void onFinish() {
                endRound();
            }
        };
        cTimer.start();
    }

    private void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    private void endRound() {
        cancelTimer();

        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra("time_left", timeLeft);
        int finalScore = score + (timeLeft * 10);
        intent.putExtra("score", finalScore);
        startActivity(intent);

        finish();
    }
}
