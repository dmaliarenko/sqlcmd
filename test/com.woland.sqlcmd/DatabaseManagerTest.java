package com.woland.sqlcmd;

import com.woland.DataSet;
import com.woland.DatabaseManager;
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
        manager.connect ("sqlcmd", "woland", "159267483");
    }

    @Test
    public void testGetAllTableNames() {
        String[] tableNames = manager.getTableNames ();
        assertEquals("[user]", Arrays.toString(tableNames));
    }

    @Test
    public void testGetTableData() {
        //given
        manager.clear("user"); //clear our base for right test

        // when
        DataSet input = new DataSet();
        input.put("name", "Stiven");
        input.put("password", "123456789");
        input.put("id", 13);
        manager.create(input); //create new record in base

        //then
        DataSet[] users = manager.getTableData ( "user" );
        assertEquals ( 1, users.length );

        DataSet user = users[0];
        assertEquals ( "[name, password, id]", Arrays.toString(user.getNames ()) );
        assertEquals ( "[Stiven, 123456789, 13]", Arrays.toString(user.getValues ()) );
    }

    @Test
    public void testUpdateData() {
        //given
        manager.clear("user"); //clear our base for right test

        DataSet input = new DataSet();
        input.put("name", "Stiven");
        input.put("password", "123456789");
        input.put("id", 13);
        manager.create(input); //create new record in base

        //when
        DataSet newValue = new DataSet();
        newValue.put("password", "pass2");
        manager.update("user", 13, newValue);

        //then
        DataSet[] users = manager.getTableData ( "user" );
        assertEquals ( 1, users.length );

        DataSet user = users[0];
        assertEquals ( "[name, password, id]", Arrays.toString(user.getNames ()) );
        assertEquals ( "[Stiven, pass2, 13]", Arrays.toString(user.getValues ()) );
    }

}
