package com.virtualglasses.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnextion {
   
    private static final String URL      =
        "jdbc:mysql://localhost:3306/glasses_db?useSSL=false&serverTimezone=UTC";
    private static final String USER     = "root";     
    private static final String PASSWORD = "";    

    public static Connection getConnection() throws SQLException {
      
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC MySQL introuvable", e);
        }
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
           
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
