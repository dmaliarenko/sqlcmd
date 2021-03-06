package com.woland.sqlcmd;

import java.sql.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/sqlcmd", "postgres",
                    "postgres");

        //Insert
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT INTO public.users (name, password) VALUES ('Stiven', '123456789')");
        stmt.close();

        //select
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM public.users WHERE id > 1");
        while (rs.next()) {
            System.out.println("id: " + rs.getString("id"));
            System.out.println("name: " + rs.getString("name"));
            System.out.println("password: " + rs.getString("password"));
            System.out.println("-----------");
        }
        rs.close();
        stmt.close();

        //table names
        stmt = connection.createStatement();
        rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables  WHERE table_schema='public' AND table_type='BASE TABLE'");
        while (rs.next()) {
            System.out.println("table_name: " + rs.getString("table_name"));
        }
        rs.close();
        stmt.close();


        //delete
        stmt = connection.createStatement();
        stmt.executeUpdate("DELETE FROM public.users WHERE id > 6");
        stmt.close();

        //update
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE public.users SET password = ? WHERE id > 3");
        String pass = "password_" + new Random().nextInt();
        ps.setString(1, "password_" + pass);

        // call executeUpdate to execute our sql update statement
        ps.executeUpdate();
        ps.close();
        stmt.close();

        connection.close();

    }

}