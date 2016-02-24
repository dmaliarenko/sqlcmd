package com.woland;

import java.sql.*;
import java.util.Arrays;
import java.util.Random;

public class DatabaseManager {

    private Connection connection;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String database = "sqlcmd";
        String user = "woland";
        String password = "159267483";

        DatabaseManager manager = new DatabaseManager();
        manager.connect( database, user, password );
        Connection connection = manager.getConnection();

        //delete
        manager.clear("user");

        //Insert
        DataSet data = new DataSet ();
        data.put("name", "Stiven");
        data.put("password", "123456789");
        data.put("id", 13);

        manager.create (data);

        //select
        String[] tables = manager.getTableNames ();
        System.out.println (Arrays.toString(tables));

        String tableName = "user";

        //another select
        DataSet[] result = manager.getTableData (tableName );

        //update
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE public.user SET password = ? WHERE id > 3");
        String pass = "password_" + new Random().nextInt();
        ps.setString(1, "password_" + pass);

        // call executeUpdate to execute our sql update statement
        ps.executeUpdate();
        ps.close();
        //stmt.close();


        connection.close();

    }

    public DataSet[] getTableData(String tableName) {
        try {
            int size = getSize ( tableName );

            Statement stmt = connection.createStatement ();
            ResultSet rs = stmt.executeQuery ( "SELECT * FROM public." + tableName );
            ResultSetMetaData rsmd = rs.getMetaData ();

            DataSet[] result = new DataSet[size];
            int index = 0;

            while (rs.next ()) {
                DataSet dataSet = new DataSet ();
                result[index++] = dataSet;

                //iterate from 1 (because postgre)
                for (int i = 1; i <= rsmd.getColumnCount (); i++) {
                    dataSet.put ( rsmd.getColumnName ( i ), rs.getObject ( i ) );
                }
            }

            rs.close ();
            stmt.close ();
            System.out.println ( Arrays.toString ( result ) );
            return result;
        } catch ( SQLException e ) {
            e.printStackTrace ();
            return new DataSet[0]; //return empty array
        }
    }

    private int getSize(String tableName) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rsCount = stmt.executeQuery("SELECT COUNT(*) FROM public." + tableName);
        rsCount.next();
        int size = rsCount.getInt(1);
        rsCount.close ();
        return size;
    }

    public String[] getTableNames() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables  WHERE table_schema='public' AND table_type='BASE TABLE'");
            String[] tables = new String[100];
            int index = 0;

            while (rs.next()) {
                tables[index++] = rs.getString("table_name");
                //System.out.println("table_name: " + rs.getString("table_name"));
            }
            tables = Arrays.copyOf (tables, index, String[].class); // cut array (100 to n)
            rs.close();
            stmt.close();
            return tables;
        } catch (SQLException e) {
            e.printStackTrace ();
            return new String[0];
        }
    }

    public void connect(String database, String user, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println ("Please add jdbc jar to project.");
            e.printStackTrace ();
        }

        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/" + database, user, password);

        } catch (SQLException e) {
            System.out.println (String.format ("Cant get connection for database:%s, user:%s", database, user));
            e.printStackTrace ();
            connection =  null;
        }
    }

    private Connection getConnection() {
        return connection;
    }

    public void clear(String tableName) {

        try {

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM public." + tableName);
            stmt.close(); //TODO best close in final block

        } catch ( SQLException e ) {
            e.printStackTrace ();
        }

    }

    public void create(DataSet input) {

        try {
            Statement stmt = connection.createStatement();

            String tableNames = getNameFormated ( input, "%s," );
            String values = getValuesFormated ( input, "'%s'," );

            stmt.executeUpdate("INSERT INTO public.user (" + tableNames + ") VALUES (" + values + ")");
            stmt.close();

        } catch ( SQLException e ) {
            e.printStackTrace ();
        }
    }

    private String getValuesFormated(DataSet input, String format) {
        String values = "";
        for (Object value : input.getValues ()) {
            values += String.format(format, value);
        }
        values = values.substring ( 0,  values.length () - 1);
        return values;
    }

    public void update(String tableName, int id, DataSet newValue) {
        try {

            String tableNames = getNameFormated ( newValue, "%s = ?," );

            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE public." + tableName + " SET " + tableNames + " WHERE id = ?");

            int index = 1;
            for ( Object value : newValue.getValues () ) {
                ps.setObject( index++, value );
            }

            ps.setInt ( index,  id );

            // call executeUpdate to execute our sql update statement
            ps.executeUpdate();
            ps.close();

        } catch ( SQLException e ) {
            e.printStackTrace ();

        }
    }

    private String getNameFormated(DataSet newValue, String format) {
        String string = "";
        for (String name : newValue.getNames ()) {
            string += String.format(format, name);
        }
        string = string.substring ( 0,  string.length () - 1);
        return string;
    }

}