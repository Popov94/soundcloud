package com.example.soundcloud.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBManager {

        private static DBManager instance;
        private static final String IP      = "localhost";
        private static final String PORT    = "3306";
        private static final String SCHEMA  = "spring_test";
        private static final String USER    = "root";
        private static final String PASS    = "root";
        private Connection connection;

        private DBManager(){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://"+IP+":"+PORT+"/"+SCHEMA, USER, PASS);
            } catch (ClassNotFoundException e) {
                System.out.println("Driver not found");
            }
            catch (SQLException e){
                System.out.println("Connection creation failed - " + e.getMessage());
            }
        }

        public static DBManager getInstance(){
            if(instance == null){
                instance = new DBManager();
            }
            return instance;
        }

        public Connection getConnection() {
            return connection;
        }

        public void closeConnection(){
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("connection could not be closed");
            }
        }
}
