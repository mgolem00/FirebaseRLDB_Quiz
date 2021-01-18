package com.example.firebasequiz;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HighscoreActivity extends AppCompatActivity {
    private FirebaseDatabase database;

    private ArrayList<String> dataset = new ArrayList<String>();
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference().child("db").child("globalRecords");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataset.size() != 0) {
                    dataset.clear();
                }
                for (DataSnapshot recordSnapshot: dataSnapshot.getChildren()) {
                    String record = recordSnapshot.getKey() + ". - " +String.valueOf(recordSnapshot.child("user").getValue()) + ": " + String.valueOf(recordSnapshot.child("score").getValue());
                    dataset.add(record);
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HighscoreActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(dataset);
        recyclerView.setAdapter(adapter);
    }
}
