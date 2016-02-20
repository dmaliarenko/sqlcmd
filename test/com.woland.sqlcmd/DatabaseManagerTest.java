package com.woland.sqlcmd;

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
}
