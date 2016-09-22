/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import mom.trd.opentheso.core.exports.helper.ExportPrivatesDatas;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_Candidat;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_Fusion;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_Group_Historique;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_Group_Label_Historique;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_Historique;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_Term_Candidat;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Concept_orphan;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Hierarchical_Relationship_Historique;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Images;
import mom.trd.opentheso.core.exports.privatesdatas.LineOfData;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Non_Preferred_Term;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Note_Historique;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Preferences;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Proposition;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Role;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Term_Candidat;
import mom.trd.opentheso.core.exports.privatesdatas.tables.Term_Historique;
import mom.trd.opentheso.core.exports.privatesdatas.tables.User_Role;
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
public class TestExportPrivatesDatas {
    
    private String xml = "";
    public TestExportPrivatesDatas() {
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
        config.addDataSourceProperty("databaseName", "OTW");

        config.addDataSourceProperty("serverName", "localhost");
        HikariDataSource poolConnexion1 = new HikariDataSource(config);
        return poolConnexion1;
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testGetUsers() {
        HikariDataSource conn = openConnexionPool();
        ExportPrivatesDatas exportPrivatesDatas = new ExportPrivatesDatas();
        //ArrayList <Users> list_userRol = exportPrivatesDatas.getUSers(conn);
        //ArrayList <Role> list_userRol = exportPrivatesDatas.getRoles(conn);
        //ArrayList <User_Role> list_userRol = exportPrivatesDatas.getUser_Roles(conn);
        //ArrayList <Concept_Term_Candidat> list_userRol = exportPrivatesDatas.getConceptTermCandidat(conn);
        //ArrayList <Proposition> list_userRol = exportPrivatesDatas.getProposition(conn);
        //ArrayList <Concept_Candidat> list_userRol = exportPrivatesDatas.getconceptCandidat(conn);
        //ArrayList <Term_Candidat> list_userRol = exportPrivatesDatas.getTermeCandidat(conn);
        //ArrayList <Concept_orphan> list_userRol = exportPrivatesDatas.getConceptOrphelin(conn);
        //ArrayList <Concept_Fusion> list_userRol = exportPrivatesDatas.getconceptFusion(conn);
        //ArrayList <Images> list_userRol = exportPrivatesDatas.getImages(conn);
        //ArrayList <Preferences> list_userRol = exportPrivatesDatas.getPreferences(conn);
        //ArrayList <Concept_Group_Historique> list_userRol = exportPrivatesDatas.getConceptGroupHist(conn);
        //ArrayList <Concept_Group_Label_Historique> list_userRol = exportPrivatesDatas.getconceptGroupLabelH(conn);
        //ArrayList <Concept_Historique> list_userRol = exportPrivatesDatas.getConceptHistorique(conn);
        //ArrayList <Hierarchical_Relationship_Historique> list_userRol = exportPrivatesDatas.getHierarchicalRelationshipH(conn);
        //ArrayList <Non_Preferred_Term> list_userRol = exportPrivatesDatas.getNonPreferredTerm(conn);
        //ArrayList <Note_Historique> list_userRol = exportPrivatesDatas.getNoteHistorique(conn);
        //ArrayList <Term_Historique> list_userRol = exportPrivatesDatas.getTermHistorique(conn);
        conn.close();
    
    }

    @Test
    public void testWriteUsersIntoXML1() {
        HikariDataSource conn = openConnexionPool();
        ExportPrivatesDatas exportPrivatesDatas = new ExportPrivatesDatas();
        ArrayList <Table> usersList = exportPrivatesDatas.getDatasOfTable(conn, "alignement_type");
        //ArrayList <Proposition> listPropo = exportPrivatesDatas.getProposition(conn);
        //ArrayList <Concept_Candidat> listConceptC = exportPrivatesDatas.getconceptCandidat(conn);
        //ArrayList <Term_Candidat> listTermC = exportPrivatesDatas.getTermeCandidat(conn);
        //ArrayList <Concept_orphan> list_userRol = exportPrivatesDatas.getConceptOrphelin(conn);
        //ArrayList <Concept_Fusion> list_userRol = exportPrivatesDatas.getconceptFusion(conn);
        //ArrayList <Images> list_userRol = exportPrivatesDatas.getImages(conn);
        //ArrayList <Preferences> list_userRol = exportPrivatesDatas.getPreferences(conn);
        //ArrayList <Concept_Group_Historique> list_userRol = exportPrivatesDatas.getConceptGroupHist(conn);
        //ArrayList <Concept_Group_Label_Historique> list_userRol = exportPrivatesDatas.getconceptGroupLabelH(conn);
        //ArrayList <Concept_Historique> list_userRol = exportPrivatesDatas.getConceptHistorique(conn);
        //ArrayList <Hierarchical_Relationship_Historique> list_userRol = exportPrivatesDatas.getHierarchicalRelationshipH(conn);
        //ArrayList <Non_Preferred_Term> list_userRol = exportPrivatesDatas.getNonPreferredTerm(conn);
        //ArrayList <Note_Historique> list_userRol = exportPrivatesDatas.getNoteHistorique(conn);
        //ArrayList <Term_Historique> list_userRol = exportPrivatesDatas.getTermHistorique(conn);
        
        conn.close();
    
        writeHead();
        
        startTable("users");
        for (Table user : usersList) {
            startLine();
            for (LineOfData lineOfData : user.getLineOfDatas()) {
                writeLine(lineOfData.getColomne(), lineOfData.getValue());
            }
            endLine();
        }
        endTable("users");
        System.out.println(xml);
    }
    
    private void startTable(String tableName) {
        xml += "\n";
        xml += "<" + tableName + ">";
    }
    
    private void endTable(String tableName) {
        xml += "\n";
        xml += "</" + tableName + ">";
    }    
    
    private void startLine() {
        xml += "\n";
        xml += "    ";
        xml += "<ligne>";
    }
    
    private void endLine() {
        xml += "\n";
        xml += "    ";
        xml += "</ligne>";
    }    
    
    private void writeLine(String colomne, String value) {
        xml += "\n";
        xml += "        ";
        xml += "<" + colomne + ">";
        xml += "\n";
        xml += "            ";        
        xml += "<value>";
        xml += value;
        xml += "</value>";
        xml += "\n";
        xml += "        ";
        xml += "</" + colomne + ">";
    }
    
    private void writeHead(){
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    }
}
 /*
    @Test
    public void testWriteUsersIntoXML() {
        HikariDataSource conn = openConnexionPool();
        ExportPrivatesDatas exportPrivatesDatas = new ExportPrivatesDatas();
       
        ArrayList <Users> usersList = exportPrivatesDatas.getUSers(conn);
        ArrayList <Role> listRol = exportPrivatesDatas.getRoles(conn);
        ArrayList <User_Role> list_UserRol = exportPrivatesDatas.getUser_Roles(conn);
        ArrayList <Concept_Term_Candidat> listConceptTermCandidat = exportPrivatesDatas.getConceptTermCandidat(conn);
        ArrayList <Proposition> listPropo = exportPrivatesDatas.getProposition(conn);
        ArrayList <Concept_Candidat> listConceptC = exportPrivatesDatas.getconceptCandidat(conn);
        ArrayList <Term_Candidat> listTermC = exportPrivatesDatas.getTermeCandidat(conn);
        //ArrayList <Concept_orphan> list_userRol = exportPrivatesDatas.getConceptOrphelin(conn);
        //ArrayList <Concept_Fusion> list_userRol = exportPrivatesDatas.getconceptFusion(conn);
        //ArrayList <Images> list_userRol = exportPrivatesDatas.getImages(conn);
        //ArrayList <Preferences> list_userRol = exportPrivatesDatas.getPreferences(conn);
        //ArrayList <Concept_Group_Historique> list_userRol = exportPrivatesDatas.getConceptGroupHist(conn);
        //ArrayList <Concept_Group_Label_Historique> list_userRol = exportPrivatesDatas.getconceptGroupLabelH(conn);
        //ArrayList <Concept_Historique> list_userRol = exportPrivatesDatas.getConceptHistorique(conn);
        //ArrayList <Hierarchical_Relationship_Historique> list_userRol = exportPrivatesDatas.getHierarchicalRelationshipH(conn);
        //ArrayList <Non_Preferred_Term> list_userRol = exportPrivatesDatas.getNonPreferredTerm(conn);
        //ArrayList <Note_Historique> list_userRol = exportPrivatesDatas.getNoteHistorique(conn);
        //ArrayList <Term_Historique> list_userRol = exportPrivatesDatas.getTermHistorique(conn);
        
        conn.close();
    
        String xml = "";
        
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xml += "\n";
        xml += "<users>";
        for (Users users : usersList) {           
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id_user>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users.getId_user();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_user>";
            xml += "\n";            
            xml += "        ";
            xml += "<username>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users.getUsername();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</username>";
            xml += "\n";            
            xml += "        ";
            xml += "<password>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users.getPassword();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</password>";
            xml += "\n";    
            xml += "        ";
            xml += "<active>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users.isActive();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</active>";
            xml += "\n";            
            xml += "        ";
            xml += "<mail>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users.getMail();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</mail>";
            xml += "\n";  
            xml += "    ";
            xml += "</ligne>";
        }
        xml+="\n";
        xml += "</users>"; 
        xml+="\n";
        xml += "<roles>";
        for (Role role : listRol ) {
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += role.getId();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id>";
            xml += "\n";            
            xml += "        ";
            xml += "<name>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += role.getName();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</name>";
            xml += "\n";            
            xml += "        ";
            xml += "<description>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += role.getDescription();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</description>";
            xml += "</ligne>";
        }
        xml += "\n";
        xml += "</roles>";
        xml += "\n";
        xml += "<users_roles>";
        for (User_Role users_role : list_UserRol) {           
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id_user>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users_role.getId_user();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_user>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_role>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users_role.getId_role();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_role>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_thesaurus>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users_role.getId_thesaurus();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_thesaurus>";
            xml += "\n";    
            xml += "        ";
            xml += "<id_group>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += users_role.getId_group();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_group>";
            xml += "</ligne>";
        }
        xml+="\n";
        xml += "</users_roles>"; 
        xml+="\n";
        xml += "<concept_term_candidat>";
        for (Concept_Term_Candidat conceptTermC : listConceptTermCandidat) {           
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id_concept>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptTermC.getId_concept();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_concept>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_term>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptTermC.getId_term();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_term>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_thesaurus>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptTermC.getId_thesaurus();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_thesaurus>";
            xml += "</ligne>";
        }
        xml+="\n";
        xml += "</concept_term_candidat>"; 
        xml+="\n";
        xml += "<proposition>";
        for (Proposition propo : listPropo) {           
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id_concept>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getId_concept();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_concept>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_user>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getId_user();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_user>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_thesaurus>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getId_thesaurus();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_thesaurus>";
            xml += "\n";    
            xml += "        ";
            xml += "<note>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getNote();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</note>";
            xml += "\n";
            xml += "        ";
            xml += "<created>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getCreated();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</created>";
            xml += "\n";            
            xml += "        ";
            xml += "<modified>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getModified();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</modified>";
            xml += "\n";            
            xml += "        ";
            xml += "<concept_parent>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getConcept_parent();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</concept_parent>";
            xml += "\n";    
            xml += "        ";
            xml += "<id_group>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += propo.getId_group();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_group>";
            xml += "</ligne>";
        }
        xml+="\n";
        xml += "</proposition>"; 
        xml+="\n";
        xml += "<concept_candidat>";
        for (Concept_Candidat conceptC : listConceptC) {           
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id_concept>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getId_concept();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_concept>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_thesaurus>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getId_thesaururs();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_thesaurus>";
            xml += "\n";
            xml += "        ";
            xml += "<created>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getCreated();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</created>";
            xml += "\n";            
            xml += "        ";
            xml += "<modified>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getModified();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</modified>";
            xml += "\n";            
            xml += "        ";
            xml += "<status>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getStatus();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</status>";
            xml += "\n";    
            xml += "        ";
            xml += "<id>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getId();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id>";
            xml += "\n";    
            xml += "        ";
            xml += "<admin_message>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getAdmin_message();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</admin_message>";
            xml += "\n";    
            xml += "        ";
            xml += "<admin_id>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += conceptC.getAdmin_id();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</admin_id>";
            xml += "</ligne>";
        }
        xml+="\n";
        xml += "</concept_candidat>"; 
        xml+="\n";
        xml += "<term_candidat>";
        for (Term_Candidat termC : listTermC) {           
            xml += "\n";
            xml += "    ";
            xml += "<ligne>";
            xml += "\n";
            xml += "        ";
            xml += "<id_term>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getId_term();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_term>";
            xml += "\n";            
            xml += "        ";
            xml += "<lexical_value>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getLexical_value();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</lexical_value>";
            xml += "\n";
            xml += "        ";
            xml += "<lang>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getLang();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</lang>";
            xml += "\n";            
            xml += "        ";
            xml += "<id_thesaurus>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getId_thesaurus();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id_thesaurus>";
            xml += "\n";            
            xml += "        ";
            xml += "<created>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getCreated();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</created>";
            xml += "\n";    
            xml += "        ";
            xml += "<modified>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getModified();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</modified>";
            xml += "\n";    
            xml += "        ";
            xml += "<contributor>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getContributor();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</contributor>";
            xml += "\n";    
            xml += "        ";
            xml += "<id>";
            xml += "\n";
            xml += "            ";
            xml += "<value>";
            xml += termC.getId();
            xml += "</value>";
            xml += "\n";
            xml += "        ";
            xml += "</id>";
            xml += "</ligne>";
        }
        xml+="\n";
        xml += "</term_candidat>"; 
        xml+="\n";
        
        
        
        System.out.println(xml);
    }    

}*/
