/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import java.io.File;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
//import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author miled.rousset
 */
public class testApiSkosOfficial {
    
    public testApiSkosOfficial() {
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
    // @Test
    // public void hello() {}




/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: Mar 17, 2008<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
 //   @Test

    public void testingOWL() throws OWLOntologyCreationException, OWLOntologyStorageException
        {

        // Get hold of an ontology manager 
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); 

        // Load an ontology from the Web.  We load the ontology from a document IRI 
        IRI docIRI = IRI.create("http://www.w3.org/2009/08/skos-reference/skos.rdf"); 
        OWLOntology skos = manager.loadOntologyFromOntologyDocument(docIRI); 

        System.out.println("Loaded ontology: " + skos); 
        System.out.println();

        // Save a local copy of the ontology.  (Specify a path appropriate to your setup) 
        File file = new File("/Users/Miled/Downloads/downloadAndSaveOWLFile.owl"); 
        manager.saveOntology(skos, IRI.create(file.toURI())); 

        // Ontologies are saved in the format from which they were loaded.   
        // We can get information about the format of an ontology from its manager 
        OWLOntologyFormat format = (OWLOntologyFormat) manager.getOntologyFormat(skos); 
        System.out.println("    format: " + format); 
        System.out.println();

        // Save the ontology in owl/xml format 
        OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat(); 

        // Some ontology formats support prefix names and prefix IRIs.   
        // In our case we loaded the pizza ontology from an rdf/xml format, which supports prefixes.  
        // When we save the ontology in the new format we will copy the prefixes over  
        // so that we have nicely abbreviated IRIs in the new ontology document 
        if(format.isPrefixOWLOntologyFormat()) 
        { 
            owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat()); 
        } 

   //     manager.saveOntology(skos, (OWLOntologyFormat) (OWLDocumentFormat) owlxmlFormat, IRI.create(file.toURI())); 

        // Dump an ontology to System.out by specifying a different OWLOntologyOutputTarget 
        // Note that we can write an ontology to a stream in a similar way  
        // using the StreamOutputTarget class 
        OWLOntologyDocumentTarget documentTarget = new SystemOutDocumentTarget(); 

        // Try another format - The Manchester OWL Syntax 
        ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat(); 

        if(format.isPrefixOWLOntologyFormat()) 
        { 
            manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat()); 
        } 
   //     manager.saveOntology(skos, (OWLOntologyFormat) (OWLDocumentFormat) manSyntaxFormat, documentTarget); 
    }
    
    
    
    
}
    

