package com.woland.sqlcmd;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by woland on 2/20/16.
 */
public class DatabaseManagerTest {

    private DatabaseManager manager;

    @Before
    public void setup() {
        manager = new DatabaseManager ();
        manager.connect ("sqlcmd", "postgres", "postgres");
    }

    @Test
    public void testGetAllTableNames() {
        String[] tableNames = manager.getTableNames ();
        assertEquals("[users]", Arrays.toString(tableNames));
    }

    @Test
    public void testGetTableData() {
        //given
        manager.clear("users"); //clear our base for right test

        // when
        DataSet input = new DataSet();
        input.put("id", 13);
        input.put("name", "Stiven");
        input.put("password", "123456789");
        manager.create(input); //create new record in base

        //then
        DataSet[] users = manager.getTableData ( "users" );
        assertEquals ( 1, users.length );

        DataSet user = users[0];
        assertEquals ( "[id, name, password]", Arrays.toString(user.getNames ()) );
        assertEquals ( "[13, Stiven, 123456789]", Arrays.toString(user.getValues ()) );
    }

    @Test
    public void testUpdateData() {
        //given
        manager.clear("users"); //clear our base for right test

        DataSet input = new DataSet();
        input.put("id", 13);
        input.put("name", "Stiven");
        input.put("password", "123456789");
        manager.create(input); //create new record in base

        //when
        DataSet newValue = new DataSet();
        newValue.put("password", "pass2");
        manager.update(  "users", 13, newValue);

        //then
        DataSet[] users = manager.getTableData ( "users" );
        assertEquals ( 1, users.length );

        DataSet user = users[0];
        assertEquals ( "[id, name, password]", Arrays.toString(user.getNames ()) );
        assertEquals ( "[13, Stiven, pass2]", Arrays.toString(user.getValues ()) );
    }

}
