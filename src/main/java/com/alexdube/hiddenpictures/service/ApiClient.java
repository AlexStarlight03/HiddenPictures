package com.alexdube.hiddenpictures.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ApiClient {
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
}