package com.alexdube.hiddenpictures.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeaderboardEntry {
    private int rank;
    private String username;
    private int score;
    private LocalDateTime date;

    public LeaderboardEntry(int rank, String username, int score, LocalDateTime date) {
        this.rank = rank;
        this.username = username;
        this.score = score;
        this.date = date;
    }

    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public LocalDateTime getDate() { return date; }

    public String getDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
