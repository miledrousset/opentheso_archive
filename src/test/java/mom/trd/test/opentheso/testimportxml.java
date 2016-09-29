/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Table;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author antonio.perez
 */
public class testimportxml {

    public testimportxml() {
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
    // public void hello() {}

    public void ouvreFichier() {
        HikariDataSource conn = openConnexionPool();

        SAXBuilder builder = new SAXBuilder();
        ArrayList<Table> toutTables = new ArrayList<>();
        ArrayList<LineOfData> lineOfDatas = new ArrayList<>();

        File xmlFile = new File("C:/Users/antonio.perez/Desktop/testbon.xml");
        try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build(xmlFile);

            //Se obtiene la raiz 'tables'
            Element rootNode = document.getRootElement();

            //Se obtiene la lista de hijos de la raiz 'tables'
            // ici on a toutes les tables (les enfants de la racine)
            List list = rootNode.getChildren("table");

            //Se recorre la lista de hijos de 'tables'
            for (int i = 0; i < list.size(); i++) {
                //Se obtiene el elemento 'tabla'
                // ici on a la première table
                Element tabla = (Element) list.get(i);

                //Se obtiene el atributo 'nombre' que esta en el tag 'tabla'
                //ici on a le nom de la table
                String nombreTabla = tabla.getAttributeValue("nom");

                System.out.println("Nom de la table : " + nombreTabla);

                //Se obtiene la lista de hijos del tag 'tabla'
                // ici c'est la liste des lignes de la table
                List lista_campos = tabla.getChildren();

                //      System.out.println("\tid_user\t\tusername\t\tpassword\t\tactive\t\tmail");
                //ici on découpe la liste des lignes
                for (int j = 0; j < lista_campos.size(); j++) {
                    //Se obtiene el elemento 'campo'

                    //ici on a une ligne de la table
                    Element campo = (Element) lista_campos.get(j);
                    //System.out.println("nouvelle ligne table "+ nombreTabla);
                    for (Element colonne : campo.getChildren()) {
                        LineOfData lineOfData = new LineOfData();
                        //System.out.println("Nom de la colonne = " + colonne.getName());
                        //System.out.println("valeur de la colonne = " + colonne.getText());

                        lineOfData.setColomne(colonne.getName());
                        lineOfData.setValue(colonne.getText());
                        lineOfDatas.add(lineOfData);
                    }
                    insertLine(conn, lineOfDatas, nombreTabla);
                    lineOfDatas.clear();

                }
                /// mettre à jour la table dans la BDD
            }

        } catch (IOException io) {
            System.out.println(io.getMessage());
        } catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
        }

    }

    private void insertLine(HikariDataSource ds, ArrayList<LineOfData> table, String nomtable) {
        Statement stmt;
        String nomcolun ="";
        String values = "";
        boolean first = true;
        for (LineOfData lineOfData : table) {
            if (!first) {
                nomcolun +=",";
                values += ",";
            }
            nomcolun += lineOfData.getColomne();
            if (lineOfData.getValue()== null) values+="";
            else 
                values += "'" +lineOfData.getValue() + "'";
            first=false;
            
        }
        values += ");";
        System.out.println(nomcolun);
        System.out.println(values);
        try 
        {
            Connection connection = ds.getConnection();
            stmt = connection.createStatement();
            try 
            {
                System.out.println(nomtable);
                // récupération des noms des colonnes de la table
                String query = "INSERT INTO " + nomtable + " ( "
                        +nomcolun + ") VALUES (" + values;
                System.out.println(query);
                stmt.executeUpdate(query);
            }
            finally {
                    stmt.close();
                    connection.close();
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        config.addDataSourceProperty("databaseName", "otw_test");

        config.addDataSourceProperty("serverName", "localhost");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }
}
