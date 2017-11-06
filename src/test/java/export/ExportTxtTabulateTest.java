/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import mom.trd.opentheso.core.exports.helper.ExportTxtHelper;
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
public class ExportTxtTabulateTest {
    
    public ExportTxtTabulateTest() {
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
    public void testAddTab() {
        addTabulate(0);
        addTabulate(1);
        addTabulate(2);
        addTabulate(3);        
        
    }
    
    private void addTabulate(int count) {
        StringBuilder txtBuff = new StringBuilder();
        for (int i = 0; i < count; i++) {
            txtBuff.append("toto; ");
        }
        System.out.println(txtBuff.toString());        
    }
    
}
