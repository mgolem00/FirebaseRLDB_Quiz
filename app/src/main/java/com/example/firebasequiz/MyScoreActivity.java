package com.example.firebasequiz;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyScoreActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private ArrayList<String> dataset = new ArrayList<String>();
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String userID = mAuth.getCurrentUser().getUid();

        DatabaseReference myRef = database.getReference().child("db").child("personalScores").child(userID);
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataset.size() != 0) {
                    dataset.clear();
                }
                for (DataSnapshot scoreSnapshot: dataSnapshot.getChildren()) {
                    String score = String.valueOf(scoreSnapshot.getValue());
                    dataset.add(score);
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FIREBASE", "Failed to read value.", error.toException());
            }
        });
    }

    private void initRecyclerView() {
        Log.d("FIREBASE_LOAD2", String.valueOf(dataset.size()));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyScoreActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(dataset);
        recyclerView.setAdapter(adapter);
    }
}
