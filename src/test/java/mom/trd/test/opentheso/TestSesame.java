/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.test.opentheso;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axiom.c14n.Canonicalizer;
import org.apache.axiom.c14n.exceptions.InvalidCanonicalizerException;
import org.apache.axiom.c14n.exceptions.CanonicalizationException;


import org.xml.sax.SAXException;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.model.util.Models;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.ntriples.NTriplesParser;
import org.openrdf.sail.memory.MemoryStore;




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
public abstract class TestSesame {
    
    
    private static String W3C_TESTS_DIR = "http://www.w3.org/2000/10/rdf-tests/rdfcore/";

    private static String LOCAL_TESTS_DIR = "/testcases/rdfxml/";

    private static String W3C_MANIFEST_FILE = W3C_TESTS_DIR + "Manifest.rdf";

    private static String OPENRDF_MANIFEST_FILE = LOCAL_TESTS_DIR + "openrdf/Manifest.rdf";
    
    public TestSesame() {
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

/* 
 * Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. 
 *
 * Aduna licenses this file to you under the terms of the Aduna BSD 
 * License (the "License"); you may not use this file except in compliance 
 * with the License. See the LICENSE.txt file distributed with this work 
 * for the full License.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */




/**
 * JUnit test for the RDF/XML parser that uses the test manifest that is
 * available <a
 * href="http://www.w3.org/2000/10/rdf-tests/rdfcore/Manifest.rdf">online</a>.
 */

	/*-----------*
	 * Constants *
	 *-----------*/


	/*--------------------*
	 * Static initializer *
	 *--------------------*/

	public TestSuite createTestSuite()
		throws Exception
	{
		// Create an RDF repository for the manifest data
		Repository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		RepositoryConnection con = repository.getConnection();

		// Add W3C's manifest
		URL w3cManifest = resolveURL(W3C_MANIFEST_FILE);
		con.add(w3cManifest, base(W3C_MANIFEST_FILE), RDFFormat.RDFXML);

		// Add our own manifest
		URL localManifest = resolveURL(OPENRDF_MANIFEST_FILE);
		con.add(localManifest, base(localManifest.toString()), RDFFormat.RDFXML);

		// Create test suite
		TestSuite suite = new TestSuite(TestSesame.class.getName());

		// Add all positive parser tests
		String query = "select TESTCASE, INPUT, OUTPUT "
				+ "from {TESTCASE} rdf:type {test:PositiveParserTest}; "
				+ "                test:inputDocument {INPUT}; "
				+ "                test:outputDocument {OUTPUT}; "
				+ "                test:status {\"APPROVED\"} "
				+ "using namespace test = <http://www.w3.org/2000/10/rdf-tests/rdfcore/testSchema#>";
		TupleQueryResult queryResult = con.prepareTupleQuery(QueryLanguage.SERQL, query).evaluate();
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			String caseURI = bindingSet.getValue("TESTCASE").toString();
			String inputURL = bindingSet.getValue("INPUT").toString();
			String outputURL = bindingSet.getValue("OUTPUT").toString();
			suite.addTest(new PositiveParserTest(caseURI, inputURL, outputURL));
		}

		queryResult.close();

		// Add all negative parser tests
		query = "select TESTCASE, INPUT " + "from {TESTCASE} rdf:type {test:NegativeParserTest}; "
				+ "                test:inputDocument {INPUT}; " + "                test:status {\"APPROVED\"} "
				+ "using namespace test = <http://www.w3.org/2000/10/rdf-tests/rdfcore/testSchema#>";
		queryResult = con.prepareTupleQuery(QueryLanguage.SERQL, query).evaluate();
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			String caseURI = bindingSet.getValue("TESTCASE").toString();
			String inputURL = bindingSet.getValue("INPUT").toString();
			suite.addTest(new NegativeParserTest(caseURI, inputURL));
		}

		queryResult.close();
		con.close();
		repository.shutDown();

		return suite;
	}

	private static URL resolveURL(String urlString)
		throws MalformedURLException
	{
		if (urlString.startsWith(W3C_TESTS_DIR)) {
			// resolve to local copy
			urlString = LOCAL_TESTS_DIR + "w3c-approved/" + urlString.substring(W3C_TESTS_DIR.length());
		}

		if (urlString.startsWith("/")) {
			return TestSesame.class.getResource(urlString);
		}
		else {
			return url(urlString);
		}
	}

	protected abstract RDFParser createRDFParser();

	/*--------------------------------*
	 * Inner class PositiveParserTest *
	 *--------------------------------*/

	private class PositiveParserTest extends TestCase {

		/*-----------*
		 * Variables *
		 *-----------*/

		private String inputURL;

		private String outputURL;

		/*--------------*
		 * Constructors *
		 *--------------*/

		public PositiveParserTest(String caseURI, String inputURL, String outputURL) {
			super(caseURI);
			this.inputURL = inputURL;
			this.outputURL = outputURL;
		}

		/*---------*
		 * Methods *
		 *---------*/

		@Override
		protected void runTest()
			throws Exception
		{
			// Parse input data
			RDFParser rdfxmlParser = createRDFParser();
		//	rdfxmlParser.setValueFactory(new CanonXMLValueFactory());
			rdfxmlParser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
			rdfxmlParser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);

			Set<Statement> inputCollection = new LinkedHashSet<Statement>();
			StatementCollector inputCollector = new StatementCollector(inputCollection);
			rdfxmlParser.setRDFHandler(inputCollector);

			InputStream in = resolveURL(inputURL).openStream();
			rdfxmlParser.parse(in, base(inputURL));
			in.close();

			// Parse expected output data
			NTriplesParser ntriplesParser = new NTriplesParser();
			//ntriplesParser.setValueFactory(new CanonXMLValueFactory());
			ntriplesParser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
			ntriplesParser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);

			Set<Statement> outputCollection = new LinkedHashSet<Statement>();
			StatementCollector outputCollector = new StatementCollector(outputCollection);
			ntriplesParser.setRDFHandler(outputCollector);

			in = resolveURL(outputURL).openStream();
			ntriplesParser.parse(in, base(inputURL));
			in.close();

			// Check equality of the two models
			if (!Models.isomorphic(inputCollection, outputCollection)) {
				StringBuilder sb = new StringBuilder(1024);
				sb.append("models not equal\n");
				sb.append("Expected:\n");
				for (Statement st : outputCollection) {
					sb.append(st).append("\n");
				}
				sb.append("Actual:\n");
				for (Statement st : inputCollection) {
					sb.append(st).append("\n");
				}

				fail(sb.toString());
			}
		}

	} // end inner class PositiveParserTest

	/*--------------------------------*
	 * Inner class NegativeParserTest *
	 *--------------------------------*/

	private class NegativeParserTest extends TestCase {

		/*-----------*
		 * Variables *
		 *-----------*/

		private String inputURL;

		/*--------------*
		 * Constructors *
		 *--------------*/

		public NegativeParserTest(String caseURI, String inputURL) {
			super(caseURI);
			this.inputURL = inputURL;
		}

		/*---------*
		 * Methods *
		 *---------*/

		@Override
		protected void runTest() {
			try {
				// Try parsing the input; this should result in an error being
				// reported.
				RDFParser rdfxmlParser = createRDFParser();
				rdfxmlParser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
				rdfxmlParser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, true);
				rdfxmlParser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, true);

				rdfxmlParser.setRDFHandler(new StatementCollector());

				InputStream in = resolveURL(inputURL).openStream();
				rdfxmlParser.parse(in, base(inputURL));
				in.close();

				fail("Parser parses erroneous data without reporting errors");
			}
			catch (RDFParseException e) {
				// This is expected as the input file is incorrect RDF
			}
			catch (Exception e) {
				fail("Error: " + e.getMessage());
			}
		}

	} // end inner class NegativeParserTest


	private static URL url(String uri)
		throws MalformedURLException
	{
		if (!uri.startsWith("injar:"))
			return new URL(uri);
		int start = uri.indexOf(':') + 3;
		int end = uri.indexOf('/', start);
		String encoded = uri.substring(start, end);
		try {
			String jar = URLDecoder.decode(encoded, "UTF-8");
			return new URL("jar:" + jar + '!' + uri.substring(end));
		}
		catch (UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
	}

	private static String base(String uri) {
		if (!uri.startsWith("jar:"))
			return uri;
		int start = uri.indexOf(':') + 1;
		int end = uri.lastIndexOf('!');
		String jar = uri.substring(start, end);
		try {
			String encoded = URLEncoder.encode(jar, "UTF-8");
			return "injar://" + encoded + uri.substring(end + 1);
		}
		catch (UnsupportedEncodingException e) {
			throw new AssertionError(e);
		}
	}
  
}

