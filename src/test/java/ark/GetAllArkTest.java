/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.bdd.helper.ConceptHelper;
import mom.trd.opentheso.bdd.helper.NoteHelper;
import mom.trd.opentheso.bdd.helper.TermHelper;
import mom.trd.opentheso.bdd.helper.nodes.NodeConceptArkId;
import mom.trd.opentheso.bdd.helper.nodes.notes.NodeNote;
import mom.trd.opentheso.bdd.helper.nodes.term.NodeTermTraduction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author miled.rousset
 */
public class GetAllArkTest {

    public GetAllArkTest() {
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

    /**
     * Test of Get datas for SiteMap.
     */
    @org.junit.Test

    public void testExportAllDatas() {
        HikariDataSource conn = openConnexionPool();

        String idTheso = "TH_1";
        ConceptHelper conceptHelper = new ConceptHelper();
        ArrayList<NodeConceptArkId> allIds = conceptHelper.getAllConceptArkIdOfThesaurus(conn, idTheso);
        StringBuilder file = new StringBuilder();
        TermHelper termHelper = new TermHelper();
        NoteHelper noteHelper = new NoteHelper();

        ArrayList<NodeTermTraduction> nodeTermTraductions;
        String idTerme;
        ArrayList<NodeNote> nodeNote;
        boolean passed = false;
        boolean notePassed = false;

        String note = "";

        for (NodeConceptArkId ids : allIds) {
            file.append(ids.getIdConcept());
            file.append("\t");

            if (ids.getIdArk() == null || ids.getIdArk().isEmpty()) {
                file.append("");
            } else {
                file.append(ids.getIdArk().substring(ids.getIdArk().indexOf("/") + 1));
            }
            nodeTermTraductions = termHelper.getAllTraductionsOfConcept(conn, ids.getIdConcept(), idTheso);
            if (!nodeTermTraductions.isEmpty()) {
                for (NodeTermTraduction nodeTermTraduction : nodeTermTraductions) {
                    if (nodeTermTraduction.getLang().equalsIgnoreCase("fr")) {
                        file.append("\t");
                        file.append(nodeTermTraduction.getLexicalValue());
                        //    file.append("(");
                        //    file.append(nodeTermTraduction.getLang());
                        //    file.append(")");
                    }
                }
            }
            idTerme = termHelper.getIdTermOfConcept(conn, ids.getIdConcept(), idTheso);
            nodeNote = noteHelper.getListNotesTerm(conn, idTerme, idTheso, "fr");

            for (NodeNote nodeNote1 : nodeNote) {
                if (nodeNote1.getLang().equalsIgnoreCase("fr")) {
                    if (nodeNote1.getNotetypecode().equalsIgnoreCase("definition")) {
                        note = nodeNote1.getLexicalvalue().replace('\r', ' ');
                        note = note.replace('\n', ' ');
                        if (!notePassed) {
                            file.append("\t");
                        } else {
                            file.append(" ## ");
                        }
                        file.append(note);
                        passed = true;
                        notePassed = true;
                    }
                }
            }
            if (!passed) {
                file.append("\t");
                file.append(" ");
            }
            passed = false;
            notePassed = false;
            file.append("\n");
        }

        System.out.println(file.toString());

        conn.close();
    }

    private HikariDataSource openConnexionPool() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        /*    config.addDataSourceProperty("user", "opentheso");
         config.addDataSourceProperty("password", "opentheso");
         config.addDataSourceProperty("databaseName", "OTW");
         */
        config.addDataSourceProperty("user", "pactols");
        config.addDataSourceProperty("password", "pactols");
        config.addDataSourceProperty("databaseName", "pactols");

        //  config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("portNumber", "5433");
        config.addDataSourceProperty("serverName", "localhost");
        //    config.addDataSourceProperty("serverName", "193.48.137.88");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }

}
