/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.core.imports.helper;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.skos.SKOSChange;
import org.semanticweb.skos.SKOSChangeException;
import org.semanticweb.skos.SKOSContentManager;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataFactory;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSFormat;
import org.semanticweb.skos.SKOSInputSource;
import org.semanticweb.skos.SKOSStorageException;
import uk.ac.manchester.cs.skos.SKOSDatasetImpl;

/**
 *
 * @author miled.rousset
 */
public class SKOSManager implements SKOSContentManager {
    private final OWLOntologyManager man;

    private Map<URI, SKOSDatasetImpl> skosVocabularies;

    private BidirectionalShortFormProviderAdapter biAdapt;

    public SKOSManager(OWLOntologyManager man) {
        this.man = OWLManager.createOWLOntologyManager();
        skosVocabularies = new HashMap<URI, SKOSDatasetImpl>();
    }
    
    
    
    @Override
    public List<SKOSChange> applyChanges(List<SKOSChange> list) throws SKOSChangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SKOSChange> applyChange(SKOSChange skosc) throws SKOSChangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SKOSDataset createSKOSDataset(URI uri) throws SKOSCreationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SKOSDataset loadDataset(URI uri) throws SKOSCreationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SKOSDataset loadDataset(SKOSInputSource skosis) throws SKOSCreationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SKOSDataset loadDatasetFromPhysicalURI(URI uri) throws SKOSCreationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public SKOSDataset loadDatasetFromInputStream(InputStream uri) throws SKOSCreationException {
        
        OWLOntology onto = null;
        try {
            onto = man.loadOntologyFromOntologyDocument(uri);
        } catch (OWLOntologyCreationException e) {
            throw new SKOSCreationException(e);
        }

        SKOSDatasetImpl voc;
  /*      voc = new SKOSDatasetImpl((SKOSManager)this, onto);
        if (voc.getURI() != null) {
            System.out.println("new ontology loaded: " + voc.getAsOWLOntology().getOntologyID());
            skosVocabularies.put(voc.getURI(), voc);
        }*/
        return null; //voc;
        

    }

    @Override
    public SKOSDataFactory getSKOSDataFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(SKOSDataset skosd) throws SKOSStorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(SKOSDataset skosd, URI uri) throws SKOSStorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(SKOSDataset skosd, SKOSFormat skosf, URI uri) throws SKOSStorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
