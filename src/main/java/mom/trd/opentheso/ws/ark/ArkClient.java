package mom.trd.opentheso.ws.ark;

import fr.mom.arkeo.soap.Account;
import fr.mom.arkeo.soap.Ark;
import fr.mom.arkeo.soap.ArkManager;
import fr.mom.arkeo.soap.ArkManagerService;
import fr.mom.arkeo.soap.DcElement;
import fr.mom.arkeo.soap.Login;
import fr.mom.arkeo.soap.LoginService;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.namespace.QName;

public final class ArkClient {

    private static final QName LOGIN_SERVICE_NAME = new QName("http://soap.arkeo.mom.fr/", "LoginService");
    private static final QName ARKMANAGER_SERVICE_NAME = new QName("http://soap.arkeo.mom.fr/", "ArkManagerService");

    private Properties propertiesArk;  
    
    
    public ArkClient() {
    }

    public void setPropertiesArk(Properties propertiesArk) {
        this.propertiesArk = propertiesArk;
    }

    private static Account login(String baseId, String user, String password) {

        URL wsdlURL = LoginService.WSDL_LOCATION;
        LoginService ss = new LoginService(wsdlURL, LOGIN_SERVICE_NAME);
        Login login = ss.getLoginPort();
        Account account = login.authentification(baseId, user, password);
        return account;
    }

    private static Ark createArk(Account account, Ark ark) {
        URL wsdlURL = ArkManagerService.WSDL_LOCATION;
        ArkManagerService ss = new ArkManagerService(wsdlURL, ARKMANAGER_SERVICE_NAME);
        ArkManager arkManager = ss.getArkManagerPort();
        Ark returnedArk = arkManager.createArk(account, ark);
        return returnedArk;
    }

    public String getInfosArkId(String idArk){
        // compte de Frantiq
        Account account = login(
                propertiesArk.getProperty("idNaan"),
                propertiesArk.getProperty("user"),
                propertiesArk.getProperty("password"));
        
        Ark ark = getArk(account, idArk);
        if(ark != null) 
            return ark.getArk();
        return null;
        
    }
    
    public String getArkId(String date, String url, String title, 
            String creator, ArrayList<DcElement> dcElementsList, String prefix) {
        Account account;
        try {
            account = login(
                propertiesArk.getProperty("idNaan"),
                propertiesArk.getProperty("user"),
                propertiesArk.getProperty("password"));
                if(account == null) return null;
            //    System.out.println("authentification.result=" + account.getUser().getFirstname() + " " + account.getUser().getLastname());
                Ark inputArk = new Ark();
                inputArk.setDate(date);
                inputArk.setUrlTarget(url);
                inputArk.setTitle(title);
                //prefixes à définir type DCMI
                inputArk.setType(prefix);//"pcrt");

                inputArk.setCreator(creator);

                for (DcElement dcElementsList1 : dcElementsList) {
                    inputArk.getDcElements().add(dcElementsList1);
                }
                Ark returnedArk = createArk(account, inputArk);    
                if(returnedArk != null)
                    return returnedArk.getArk();
                return null;
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;
        

        
        /*
        for(DcElement dcElement : returnedArk.getDcElements()){
        	System.out.println(dcElement.getName()+" = " +dcElement.getValue());
        }
        */

        
        // Liste des DcElements
    /*     
        EL_ABSTRACT = "abstract",
        EL_ACCRIGHTS = "accessRights",
        EL_ACCMETHOD = "accrualMethod",
        EL_ACCPERIOD = "accrualPeriodicity",
        EL_ACCPOLICY = "accrualPolicy",
        EL_ALT = "alternative",
        EL_AUDIENCE = "audience",
        EL_AVAILABLE = "available",
        EL_BIBCITE = "bibliographicCitation",
        EL_CONFORMS = "conformsTo",
        EL_CONTRIB = "contributor",
        EL_COVERAGE = "coverage",
        EL_CREATED = "created",
        EL_CREATOR = "creator",
        EL_DATE = "date",
        EL_DATEACC = "dateAccepted",
        EL_DATECPR = "dateCopyrighted",
        EL_DATESUB = "dateSubmitted",
        EL_DESC = "description",
        EL_EDUCLEVEL = "educationLevel",
        EL_EXTENT = "extent",
        EL_FORMAT = "format",
        EL_HASFORMAT = "hasFormat",
        EL_HASPART = "hasPart",
        EL_HASVERS = "hasVersion",
        EL_ID = "identifier",
        EL_INSTMETHOD = "instructionalMethod",
        EL_ISFMTOF = "isFormatOf",
        EL_ISPARTOF = "isPartOf",
        EL_ISREFBY = "isReferencedBy",
        EL_ISREPBY = "isReplacedBy",
        EL_ISREQBY = "isRequiredBy",
        EL_ISSUED = "issued",
        EL_ISVSNOF = "isVersionOf",
        EL_LANG = "language",
        EL_LICENSE = "license",
        EL_MEDIATOR = "mediator",
        EL_MEDIUM = "medium",
        EL_MOD = "modified",
        EL_PROV = "provenance",
        EL_PUB = "publisher",
        EL_REFS = "references",
        EL_REL = "relation",
        EL_REP = "replaces",
        EL_REQ = "requires",
        EL_RIGHTS = "rights",
        EL_RIGHTSHOLDER = "rightsHolder",
        EL_SOURCE = "source",
        EL_SPATIAL = "spatial",
        EL_SUBJECT = "subject",
        EL_TOC = "tableOfContents",
        EL_TEMPORAL = "temporal",
        EL_TITLE = "title",
        EL_TYPE = "type",
        EL_VALID = "valid";
        */

    //    System.out.println("ark.result=" + returnedArk.getArk());
    }
    
    public String updateArkId(String date, String url, String title, 
            String creator, ArrayList<DcElement> dcElementsList, String type) {

     // compte de Frantiq
        Account account = login(
                propertiesArk.getProperty("idNaan"),
                propertiesArk.getProperty("user"),
                propertiesArk.getProperty("password"));
        
        
    //    System.out.println("authentification.result=" + account.getUser().getFirstname() + " " + account.getUser().getLastname());
        Ark inputArk = new Ark();
        inputArk.setDate(date);
        inputArk.setUrlTarget(url);
        inputArk.setTitle(title);
        //prefixes à définir type DCMI
        inputArk.setType(type);//"pcrt");
        
        inputArk.setCreator(creator);
      
        for (DcElement dcElementsList1 : dcElementsList) {
            inputArk.getDcElements().add(dcElementsList1);
        }
        Ark returnedArk = updateArk(account, inputArk);
        
        /*
        for(DcElement dcElement : returnedArk.getDcElements()){
        	System.out.println(dcElement.getName()+" = " +dcElement.getValue());
        }
        */
        if(returnedArk != null)
            return returnedArk.getArk();
        return null;
        
        // Liste des DcElements
    /*     
        EL_ABSTRACT = "abstract",
        EL_ACCRIGHTS = "accessRights",
        EL_ACCMETHOD = "accrualMethod",
        EL_ACCPERIOD = "accrualPeriodicity",
        EL_ACCPOLICY = "accrualPolicy",
        EL_ALT = "alternative",
        EL_AUDIENCE = "audience",
        EL_AVAILABLE = "available",
        EL_BIBCITE = "bibliographicCitation",
        EL_CONFORMS = "conformsTo",
        EL_CONTRIB = "contributor",
        EL_COVERAGE = "coverage",
        EL_CREATED = "created",
        EL_CREATOR = "creator",
        EL_DATE = "date",
        EL_DATEACC = "dateAccepted",
        EL_DATECPR = "dateCopyrighted",
        EL_DATESUB = "dateSubmitted",
        EL_DESC = "description",
        EL_EDUCLEVEL = "educationLevel",
        EL_EXTENT = "extent",
        EL_FORMAT = "format",
        EL_HASFORMAT = "hasFormat",
        EL_HASPART = "hasPart",
        EL_HASVERS = "hasVersion",
        EL_ID = "identifier",
        EL_INSTMETHOD = "instructionalMethod",
        EL_ISFMTOF = "isFormatOf",
        EL_ISPARTOF = "isPartOf",
        EL_ISREFBY = "isReferencedBy",
        EL_ISREPBY = "isReplacedBy",
        EL_ISREQBY = "isRequiredBy",
        EL_ISSUED = "issued",
        EL_ISVSNOF = "isVersionOf",
        EL_LANG = "language",
        EL_LICENSE = "license",
        EL_MEDIATOR = "mediator",
        EL_MEDIUM = "medium",
        EL_MOD = "modified",
        EL_PROV = "provenance",
        EL_PUB = "publisher",
        EL_REFS = "references",
        EL_REL = "relation",
        EL_REP = "replaces",
        EL_REQ = "requires",
        EL_RIGHTS = "rights",
        EL_RIGHTSHOLDER = "rightsHolder",
        EL_SOURCE = "source",
        EL_SPATIAL = "spatial",
        EL_SUBJECT = "subject",
        EL_TOC = "tableOfContents",
        EL_TEMPORAL = "temporal",
        EL_TITLE = "title",
        EL_TYPE = "type",
        EL_VALID = "valid";
        */

    //    System.out.println("ark.result=" + returnedArk.getArk());
    }    
    
    private static Ark updateArk(Account account, Ark ark){
      	 URL wsdlURL = ArkManagerService.WSDL_LOCATION;
      	 ArkManagerService ss = new ArkManagerService(wsdlURL, ARKMANAGER_SERVICE_NAME);
      	 ArkManager arkManager = ss.getArkManagerPort();      	    
      	 Ark returnedArk = arkManager.updateArk(account,ark);
      	 return returnedArk;
     }
    
    private static Ark getArk(Account account, String arkId){
     	 URL wsdlURL = ArkManagerService.WSDL_LOCATION;
     	 ArkManagerService ss = new ArkManagerService(wsdlURL, ARKMANAGER_SERVICE_NAME);
     	 ArkManager arkManager = ss.getArkManagerPort();      	    
     	 Ark returnedArk = arkManager.getArk(account,arkId);
     	 return returnedArk;
    }
    
}
