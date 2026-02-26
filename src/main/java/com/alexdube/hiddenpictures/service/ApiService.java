package com.alexdube.hiddenpictures.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alexdube.hiddenpictures.model.LeaderboardEntry;

public class ApiService {

    public int getGamesCount(int userId) {
        String sql = "SELECT COUNT(*) FROM games WHERE user_id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class BestScore {
        public final int score;
        public final String date;
        public BestScore(int score, String date) {
            this.score = score;
            this.date = date;
        }
    }

    public BestScore getBestScore(int userId) {
        String sql = "SELECT score, played_at FROM games WHERE user_id = ? ORDER BY score DESC, played_at ASC LIMIT 1";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int score = rs.getInt("score");
                String date = rs.getTimestamp("played_at").toLocalDateTime().toLocalDate().toString();
                return new BestScore(score, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getAvgScore(int userId) {
        String sql = "SELECT AVG(score) FROM games WHERE user_id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean updateProfile(int userId, String newUsername, String newPassword) {
        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        boolean updateUsername = newUsername != null && !newUsername.isEmpty();
        boolean updatePassword = newPassword != null && !newPassword.isEmpty();
        if (!updateUsername && !updatePassword) return false;
        if (updateUsername) sql.append("username = ?");
        if (updatePassword) {
            if (updateUsername) sql.append(", ");
            sql.append("password = ?");
        }
        sql.append(" WHERE id = ?");
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (updateUsername) stmt.setString(idx++, newUsername);
            if (updatePassword) stmt.setString(idx++, hashPassword(newPassword));
            stmt.setInt(idx, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    public boolean saveGameScore(int userId, int score) {
        String sql = "INSERT INTO games (user_id, score, played_at) VALUES (?, ?, ?)";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LeaderboardEntry> getLeaderboard(String orderBy) {
        List<LeaderboardEntry> list = new ArrayList<>();
        String sql = String.format("""
            SELECT u.username, g.score, g.played_at
            FROM games g
            JOIN users u ON g.user_id = u.id
            ORDER BY %s
            LIMIT 10
        """, orderBy);
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int rank = 1;
            while (rs.next()) {
                list.add(new LeaderboardEntry(
                        rank++,
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getTimestamp("played_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LeaderboardEntry> getLeaderboardByUser(String username) {
        List<LeaderboardEntry> list = new ArrayList<>();
        String sql = """
            SELECT u.username, g.score, g.played_at
            FROM games g
            JOIN users u ON g.user_id = u.id
            WHERE u.username = ?
            ORDER BY g.score DESC, g.played_at ASC
        """;
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            int rank = 1;
            while (rs.next()) {
                list.add(new LeaderboardEntry(
                        rank++,
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getTimestamp("played_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}