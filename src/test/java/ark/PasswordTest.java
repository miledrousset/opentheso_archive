/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark;

import mom.trd.opentheso.bdd.tools.MD5Password;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author miled.rousset
 */
public class PasswordTest {
    
    public PasswordTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void getPassword() {
        String pass = "21232f297a57a5a743894a0e4a801fc3";
        
        String test1 = "admin";
        
        
        
        String test2 = MD5Password.getEncodedPassword(test1);
        System.out.println(test2);
        
        String test3 = "adminezl,m23135rrez4(!retgpm&";
        
        String test4 = MD5Password.getEncodedPassword(test3);
        System.out.println(test4);
    }
    
}
