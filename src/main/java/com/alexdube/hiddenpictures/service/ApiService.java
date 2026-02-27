package com.alexdube.hiddenpictures.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alexdube.hiddenpictures.model.Game;
import com.alexdube.hiddenpictures.model.LeaderboardEntry;
import com.alexdube.hiddenpictures.model.User;

public class ApiService {

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBoolean("isAdmin")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBoolean("isAdmin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(int id, String newUsername, String newPassword) {
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
            stmt.setInt(idx, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAdminStatus(int userId, boolean isAdmin) {
        String sql = "UPDATE users SET isAdmin = ? WHERE id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        if (!deleteGamesByUser(id)) {
            return false;
        }
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGamesByUser(int userId) {
        String sql = "DELETE FROM games WHERE user_id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBoolean("isAdmin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addGame(int userId, int score) {
        String sql = "INSERT INTO games (user_id, score, played_at) VALUES (?, ?, ?)";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Game> getAllGames() {
        List<Game> list = new ArrayList<>();
        String sql = "SELECT * FROM games ORDER BY played_at";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Game(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("score"),
                        rs.getTimestamp("played_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateGame(int gameId, int newScore) {
        String sql = "UPDATE games SET score = ? WHERE id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newScore);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGame(int gameId) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur de hashage : " + e.getMessage());
            return password;
        }
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
                String date = rs.getTimestamp("played_at").toLocalDateTime().toString();
                return new BestScore(score, date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getAvgScore(int userId) {
        String sql = "SELECT AVG(score) AS avg_score FROM games WHERE user_id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getGamesCount(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM games WHERE user_id = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<LeaderboardEntry> getLeaderboard(String orderBy) {
        List<LeaderboardEntry> list = new ArrayList<>();
        String sql = "SELECT u.username, g.score, g.played_at " +
                "FROM games g JOIN users u ON g.user_id = u.id " +
                "ORDER BY " + orderBy;
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

}