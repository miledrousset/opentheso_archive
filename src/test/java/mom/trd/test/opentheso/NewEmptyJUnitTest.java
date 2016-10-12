/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.faces.bean.ManagedProperty;
import mom.trd.opentheso.SelectedBeans.LanguageBean;
import mom.trd.opentheso.SelectedBeans.SelectedThesaurus;
import mom.trd.opentheso.SelectedBeans.StatBean;
import mom.trd.opentheso.bdd.helper.Connexion;
import mom.trd.opentheso.bdd.helper.GroupHelper;
import mom.trd.opentheso.bdd.helper.StatisticHelper;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatConcept;
import mom.trd.opentheso.bdd.helper.nodes.statistic.NodeStatTheso;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author antonio.perez
 */
public class NewEmptyJUnitTest {

    public NewEmptyJUnitTest() {
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
    public void apple() throws SQLException {
        HikariDataSource conn = openConnexionPool();
        recuperatefils(conn);
    }

    @ManagedProperty(value = "#{poolConnexion}")
    private Connexion connect;

    public void recuperatefils(HikariDataSource ds) throws SQLException {

        LanguageBean laguageBean = new LanguageBean();
        String id_theso = "11";
        int j = 0;
        HashMap<String, String> map = new HashMap<>();
        Connection conn, conn2,conn3 = null;
        Statement stmt, stmt1, stmt2 = null;
        ArrayList<Integer> niveaux = new ArrayList<>();
        ResultSet resultSet, resultSet1, rS;
        boolean first = true;
        //id_theso = theso;
        String i = "";
        String lange = "fr";
        ArrayList<String> candidats = new ArrayList<>();
        ArrayList<Integer> combienterm = new ArrayList<>();
        int domines=0;

        try {
            conn = conn2=conn3 = ds.getConnection();
            try {
                stmt = conn.createStatement();
                stmt1 = conn2.createStatement();
                stmt2 = conn2.createStatement();
                try {
                    String query = "SELECT idgroup, lexicalvalue, lang FROM concept_group_label where idthesaurus ='" + id_theso + "'";
                    resultSet = stmt.executeQuery(query);
                    while (resultSet.next()) {
                        String lexical = "";
                        String lang = "(";
                        String idgroup = resultSet.getString(1);
                        if (i == null ? idgroup != null : !i.equals(idgroup)) {
                            lexical += resultSet.getString(2);
                            lang += resultSet.getString(3) + ")";
                            lexical += lang;
                            candidats.add(lexical);
                            niveaux.add(j);
                            domines++;
                        } else {
                            String change = map.get(i);
                            lang = "(";
                            lexical += ", " + resultSet.getString(2);
                            lang += resultSet.getString(3) + ")";
                            lexical += lang;
                            change += lexical;
                            lexical = change;
                            int ou = candidats.size();
                            candidats.remove(ou - 1);
                            candidats.add(lexical);
                        }
                        i = resultSet.getString(1);
                        map.put(idgroup, lexical);

                    }
                    j++;
                    for (Map.Entry e : map.entrySet()) {
                        int combien = 0;
                        int cantitad=0;
                        ArrayList<String> id = new ArrayList<>();
                        String query2 = "Select *  from concept_historique where id_thesaurus = '" + id_theso + "' and id_group ='" + e.getKey() + "' and top_concept ='true'";
                        String query4 ="Select id_concept from concept where id_group ='"+ e.getKey()+"' ";
                        rS= stmt2.executeQuery(query4);
                        while(rS.next())
                        {
                            cantitad++;
                        }
                        combienterm.add(cantitad);
                        resultSet = stmt.executeQuery(query2);
                        while (resultSet.next()) {
                            combien++;
                            String query3 = "Select id_term from term where id_term ='" + resultSet.getString(1) + "' and lang ='" + lange + "'";
                            resultSet1 = stmt1.executeQuery(query3);
                            if (resultSet1.next()) {
                                candidats.add(resultSet1.getString(1));
                                id.add(resultSet1.getString(1));
                                niveaux.add(j);

                            }
                        }
                        for (int z = 0; z < combien; z++) {
                            int tamanio = id.size();
                            j++;
                            genererfils(ds, id_theso, lange, candidats, id.get(z), niveaux, j);
                            j--;
                        }

                    }
                    changenames(ds, candidats);
                    creedocumentatlch(candidats,id_theso,lange,niveaux, combienterm, map, domines);    
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void genererfils(HikariDataSource ds, String idTheso, String langue, ArrayList<String> term, String nom, ArrayList<Integer> niveaux, int j) {
        Connection conn, conn2 = null;
        Statement stmt, stmt1 = null;
        ResultSet resultset, resulset1;
        try {
            conn = conn2 = ds.getConnection();
            try {
                stmt = conn.createStatement();
                stmt1 = conn2.createStatement();
                try {
                    String query = "select * from hierarchical_relationship where id_thesaurus ='" + idTheso + "' and id_concept1='" + nom + "' and role ='NT'";
                    resultset = stmt.executeQuery(query);
                    while (resultset.next()) {

                        niveaux.add(j);
                        term.add(resultset.getString(4));
                        j++;
                        genererfils(ds, idTheso, langue, term, resultset.getString(4), niveaux, j);
                        j--;
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changenames(HikariDataSource ds, ArrayList<String> candidats) {
        Connection conn, conn2 = null;
        Statement stmt, stmt1 = null;
        ResultSet resultset, resulset1;
        try {
            conn = conn2 = ds.getConnection();
            try {
                stmt = conn.createStatement();
                stmt1 = conn2.createStatement();
                try {
                    for (int i = 2; i < candidats.size(); i++) {
                        int pos;
                        String query = "select lexical_value from term where id_term ='" + candidats.get(i) + "' and lang ='fr'";
                        resultset = stmt.executeQuery(query);
                        if(resultset.next())
                        {
                            int ou = candidats.size();
                            candidats.remove(i);
                            candidats.add(i,resultset.getString(1));
                        }
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void creedocumentatlch(ArrayList<String> candidat,String id_theso, String lg,ArrayList<Integer>niveaux, ArrayList<Integer>cantite, HashMap<String, String> map, int domaines)
    {
        int z=0;
        int az=0;
        String document="Thésaurus: "+id_theso+"\n\n";
        for (Map.Entry e : map.entrySet()) {
            document+= e.getValue()+"\n";
            for(int j=domaines; j< (cantite.get(az)+domaines);j++)
            {
                donneespace(document, niveaux.get(j), j, candidat.get(j));
                z++;
            }
           az++;
        }
        System.out.println(document);
    }
    private void donneespace(String document, int niveaux, int pos, String term)
    {
        for (int i=0; i< niveaux; i++)
        {
            document+="  ";
        }
        document+= term+"\n";
    }

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        config.setMinimumIdle(1);
        config.setAutoCommit(true);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("user", "opentheso");
        config.addDataSourceProperty("password", "opentheso");
        config.addDataSourceProperty("databaseName", "OTW2");

        config.addDataSourceProperty("serverName", "localhost");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }
}
