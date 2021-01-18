package com.example.firebasequiz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EndGameActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userID;
    private String userMail;

    private TextView congratulationsTextView;
    private TextView finalScoreTextView;
    private TextView finalTimeTextView;
    private Button yesSave;
    private Button noSave;

    private int score;
    private int timeLeft;

    private ArrayList<GlobalRecord> globalRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endgame);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        userID = mAuth.getCurrentUser().getUid();
        userMail = mAuth.getCurrentUser().getEmail();

        loadUI();

        score = getIntent().getIntExtra("score", 0);
        timeLeft = getIntent().getIntExtra("time_left", 0);
        finalTimeTextView.setText("Time left: " + String.valueOf(timeLeft));
        finalScoreTextView.setText("Score: " + String.valueOf(score));
    }

    private void loadUI() {
        congratulationsTextView = findViewById(R.id.CongratsTextView);
        runAnimation();

        finalScoreTextView = findViewById(R.id.FinalScoreTextView);
        finalTimeTextView = findViewById(R.id.FinalTimeTextView);

        yesSave = findViewById(R.id.YesButton);
        yesSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newKey = database.getReference().child("db").child("personalScores").child(userID).push().getKey();
                database.getReference().child("db").child("personalScores").child(userID).child(newKey).setValue(score);

                DatabaseReference myRef = database.getReference().child("db").child("globalRecords");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot scoreSnapshot: dataSnapshot.getChildren()) {
                            GlobalRecord rec = scoreSnapshot.getValue(GlobalRecord.class);
                            globalRecords.add(rec);
                        }

                        GlobalRecord newRecord = new GlobalRecord(score, userMail);
                        globalRecords.add(newRecord);
                        globalRecords.sort(Comparator.comparingInt(GlobalRecord::getScore).reversed());
                        globalRecords.remove(10);

                        Map<String, Object> childUpdates = new HashMap<>();
                        for(int i = 0; i < 10; i++) {
                            childUpdates.put(String.valueOf(i+1), globalRecords.get(i));
                        }
                        database.getReference().child("db").child("globalRecords").updateChildren(childUpdates);
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("FIREBASE", "Failed to read value.", error.toException());
                    }
                });
            }
        });

        noSave = findViewById(R.id.NoButton);
        noSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void runAnimation()
    {
        Animation scaleAnim = AnimationUtils.loadAnimation(EndGameActivity.this, R.anim.scale);
        congratulationsTextView.startAnimation(scaleAnim);
    }

}
