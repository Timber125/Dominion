/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Service;

import Server.Server;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Emiel
 */
public class JDBCDatabaseServiceTest{
    private Server server;
    private JDBCMemoryDatabaseService instance;
    
    public JDBCDatabaseServiceTest() {
        this.server = new Server("localhost", 13337, 13338);
        this.instance = new JDBCMemoryDatabaseService(server);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void testGetNames() throws Exception {
        System.out.println("getNames");
        ArrayList<String> result = instance.getNames();
        assertNotNull(result.get(0));
    }
    
    @Test
    public void testLogin() throws Exception {
        System.out.println("login");
        assertTrue(instance.login("Emiel", "Random"));
    } 
    
    @Test
    public void testRegister() throws Exception {
        System.out.println("register");
        instance.register("Timber", "Random");
        assertFalse(instance.register("Timber", "Random"));
    }
    
    
    @Test
    public void testGetPlayersOfGame() throws Exception {
        System.out.println("getPlayersOfGame");
        //assertTrue(instance.getPlayersOfGame(2).contains(1) && instance.getPlayersOfGame(2).contains(2));
    }
    
    @Test
    public void testUpdateJson() throws Exception {
        System.out.println("updateJson");
        instance.addGameLogTable(1);
        assertTrue(instance.updateJSON(1, "iets"));
    }
    
    @Test
    public void testGetJsonArray() throws Exception{
        System.out.println("updateJson");
        assertNotNull(instance.getJsonArrayFromTable(1));
    }
}
