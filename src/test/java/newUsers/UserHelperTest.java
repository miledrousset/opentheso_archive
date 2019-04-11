/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newUsers;

import com.zaxxer.hikari.HikariDataSource;
import connexion.ConnexionTest;
import java.util.ArrayList;
import java.util.Map;
import mom.trd.opentheso.bdd.helper.UserHelper2;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser;
import mom.trd.opentheso.bdd.helper.nodes.NodeUser2;
import mom.trd.opentheso.bdd.helper.nodes.NodeUserRole;
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
public class UserHelperTest {

    public UserHelperTest() {
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
    public void getUser() {
        int idUser = 15;
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        UserHelper2 userHelper2 = new UserHelper2();

        NodeUser2 nodeUser = userHelper2.getUser(conn, idUser);

        conn.close();
    }

    @Test
    public void getUsersOfGroup() {
        UserHelper2 userHelper2 = new UserHelper2();
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();

        ArrayList<NodeUserRole> nodeUser2s = userHelper2.getUsersRolesByGroup(conn, 3, 2);

    }

    @Test
    public void getAuthorizedRoles() {
        UserHelper2 userHelper2 = new UserHelper2();
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();

        ArrayList<Map.Entry<String, String>> authorizedRols = userHelper2.getAuthorizedRoles(conn, 3);

    }

    @Test
    public void addUser() {
        UserHelper2 userHelper2 = new UserHelper2();
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();
        int idUser = -1;
        boolean status = userHelper2.addUser(conn, "userName2dd", "mail2dd@mom.fr", "21232f297a57a5a743894a0e4a801fc3",
                true, true);
        if (status) {
            idUser = userHelper2.getIdUser(conn, "userName2dd", "21232f297a57a5a743894a0e4a801fc3");
            status = userHelper2.addUserRoleOnGroup(conn, idUser, 1, 1);
        }
        System.out.println("" + idUser);
        
    }
    
    @Test
    public void getLabelGroup(){
        UserHelper2 userHelper2 = new UserHelper2();
        ConnexionTest connexionTest = new ConnexionTest();
        HikariDataSource conn = connexionTest.getConnexionPool();    
        String label = userHelper2.getGroupName(conn, 1);
    }

}
