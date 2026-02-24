package com.alexdube.hiddenpictures.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String HOST = "ep-orange-feather-ai1awcup-pooler.c-4.us-east-1.aws.neon.tech";
    private static final String DATABASE = "neondb";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_m5v8ZwQdjcHa";

    private static final String URL = "jdbc:postgresql://"+HOST+"/"+DATABASE+"?sslmode=require";

    private static Connection connection;

    public static Connection getConnection(){
        try{
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion etablie avec succes" + connection);
            }
        } catch(SQLException e) {
            System.out.println("Erreur de connexion a la base de donnee" + e + connection);
            e.printStackTrace();
        }
        return connection;
    }

    public static void initDatabase() {
        // On parle ici de LDD - Language Definition des Donnees _ On utilise les statements
        String createTableUser = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                date_inscription TIMESTAMP DEFAULT NOW()
            )
            """;
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createTableUser);
            System.out.println("Table Users creee avec success");
        } catch (SQLException e) {
            System.out.println("Erreur lors de creation de table Users" + e);
        }
        String createTableGames = """
            CREATE TABLE IF NOT EXISTS games (
                id SERIAL PRIMARY KEY,
                user_id INT REFERENCES users(id),
                score INT NOT NULL,
                played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                temps_partie INT
            )
            """;
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createTableGames);
            System.out.println("Table Games creee avec success");
        } catch (SQLException e) {
            System.out.println("Erreur lors de creation de table Games" + e);
        }
    }
}
