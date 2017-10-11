/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.KeyManagementException;
import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class NewPutHandleTest {

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

    @Test

    public void putHandle() {

        String output = "";
        String xmlRecord = "";

        try {

            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream("key.p12"), "motdepasse".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, "motdepasse".toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("cacerts2"), "motdepasse".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            URL url = new URL("https://cchum-isi-handle01.in2p3.fr:8001/api/handles/20.500.11942/opentheso4.3.2");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Handle clientCert=\"true\"");
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            String data = "{\"index\":1,\"type\":\"URL\",\"data\":{\"format\":\"string\",\"value\":\"http://opentheso.mom.fr\"},\"ttl\":86400,\"permissions\":\"1110\"}";

            OutputStream os = conn.getOutputStream();

            OutputStreamWriter out = new OutputStreamWriter(os);
            out.write(data);
            out.flush();

            int status = conn.getResponseCode();
            InputStream in = status >= 400 ? conn.getErrorStream() : conn.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((output = br.readLine()) != null) {
                xmlRecord += output;
            }
            byte[] bytes = xmlRecord.getBytes();
            xmlRecord = new String(bytes, Charset.forName("UTF-8"));

            System.out.println(xmlRecord);
            os.close();

        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (KeyStoreException ex) {
            System.out.println(ex);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        } catch (CertificateException ex) {
            System.out.println(ex);
        } catch (UnrecoverableKeyException ex) {
            System.out.println(ex);
        } catch (KeyManagementException ex) {
            System.out.println(ex);
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
