package com.example.firebasequiz;

public class GlobalRecord {

    public int score;
    public String user;

    public GlobalRecord() {}
    public GlobalRecord(int score, String user) {
        this.score = score;
        this.user = user;
    }

    public int getScore() {
        return this.score;
    }
}
