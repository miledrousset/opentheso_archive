/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.List;
import mom.trd.opentheso.SelectedBeans.DownloadBean;
import mom.trd.opentheso.SelectedBeans.Connexion;
import mom.trd.opentheso.bdd.helper.nodes.NodeLang;
import mom.trd.opentheso.bdd.helper.nodes.group.NodeGroup;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.primefaces.model.StreamedContent;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

/**
 *
 * @author jm.prudham
 */
public class SynchroSparql implements Runnable {

    private SparqlStruct sparqlStruct;
    private List<NodeLang> liste_lang;
    private List<NodeGroup> liste_group;
    private Connexion conn;
    @Override
    public void  run(){
    System.out.println("dans le run saprtql");
    String url=sparqlStruct.getAdresseServeur().replaceAll("http","jdbc:virtuoso").trim()+":1111";
    VirtGraph graph=new VirtGraph(sparqlStruct.getGraph(),url,sparqlStruct.getNom_d_utilisateur(),
            sparqlStruct.getMot_de_passe());

    String str = "CLEAR GRAPH <"+sparqlStruct.getGraph()+">";
    VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(str, graph);
    vur.exec();
    
    
    Model m=ModelFactory.createDefaultModel();
      
    DownloadBean db=new DownloadBean();
    db.setConnect(conn);
    StreamedContent file=db.thesoToFile(sparqlStruct.getThesaurus(), liste_lang, liste_group, 0);
      
    try{
        m.read(file.getStream(),null);
    }
    catch(Exception e){
        //graph.close();
    }
    StmtIterator iter=m.listStatements();
    while(iter.hasNext()){
        Statement stmt=iter.nextStatement();
        Resource subject=stmt.getSubject();
        Property predicate=stmt.getPredicate();
        RDFNode object=stmt.getObject();
        Triple tri=new Triple(subject.asNode(),predicate.asNode(),object.asNode());
        graph.add(tri);

        
        }
    graph.close();
  
    }

    public SparqlStruct getSparqlStruct() {
        return sparqlStruct;
    }

    public void setSparqlStruct(SparqlStruct sparqlStruct) {
        this.sparqlStruct = sparqlStruct;
    }

    public List<NodeLang> getListe_lang() {
        return liste_lang;
    }

    public void setListe_lang(List<NodeLang> liste_lang) {
        this.liste_lang = liste_lang;
    }

    public List<NodeGroup> getListe_group() {
        return liste_group;
    }

    public void setListe_group(List<NodeGroup> liste_group) {
        this.liste_group = liste_group;
    }

    public void setConn(Connexion conn) {
        this.conn = conn;
    }

 
    
}
