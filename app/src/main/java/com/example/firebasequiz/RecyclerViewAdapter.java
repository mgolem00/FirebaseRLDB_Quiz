package com.example.firebasequiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mDataset;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView scoreText;

        public ViewHolder(View itemView) {
            super(itemView);
            scoreText = itemView.findViewById(R.id.ScoreTextView);
        }
    }

    public RecyclerViewAdapter(ArrayList<String> myDataset) {
        this.mDataset = myDataset;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewCell = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(viewCell);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.scoreText.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
