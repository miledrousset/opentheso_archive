package mom.trd.opentheso.bdd.helper.nodes;


public class NodePreference {
    private String sourceLang;
    private int nbAlertCdt;
    private boolean alertCdt;
    private int identifierType;
    
    private boolean useArk;
    private String serveurArk;
    private String pathImage;
    private String dossierResize;
    
    
/*    private String protcolMail;
    private String hostMail;
    private int portMail;
    private boolean authMail;
    private String mailFrom;
    private String transportMail;
*/    
    
    private boolean bddActive;
    private boolean bddUseId;
    private String urlBdd;
    private String urlCounterBdd;    
    
    private boolean z3950actif;
    private String collectionAdresse;
    private String noticeUrl;
    private String urlEncode;
    private String pathNotice1;
    private String pathNotice2;
    private String cheminSite;
    private boolean webservices;

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public int getNbAlertCdt() {
        return nbAlertCdt;
    }

    public void setNbAlertCdt(int nbAlertCdt) {
        this.nbAlertCdt = nbAlertCdt;
    }

    public boolean isAlertCdt() {
        return alertCdt;
    }

    public void setAlertCdt(boolean alertCdt) {
        this.alertCdt = alertCdt;
    }

    public int getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(int identifierType) {
        this.identifierType = identifierType;
    }

    public boolean isUseArk() {
        return useArk;
    }

    public void setUseArk(boolean useArk) {
        this.useArk = useArk;
    }

    public String getServeurArk() {
        return serveurArk;
    }

    public void setServeurArk(String serveurArk) {
        this.serveurArk = serveurArk;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getDossierResize() {
        return dossierResize;
    }

    public void setDossierResize(String dossierResize) {
        this.dossierResize = dossierResize;
    }

    public boolean isBddActive() {
        return bddActive;
    }

    public void setBddActive(boolean bddActive) {
        this.bddActive = bddActive;
    }

    public boolean isBddUseId() {
        return bddUseId;
    }

    public void setBddUseId(boolean bddUseId) {
        this.bddUseId = bddUseId;
    }

    public String getUrlBdd() {
        return urlBdd;
    }

    public void setUrlBdd(String urlBdd) {
        this.urlBdd = urlBdd;
    }

    public boolean getZ3950acif() {
        return z3950actif;
    }

    public void setZ3950acif(boolean z3950acif) {
        this.z3950actif = z3950acif;
    }

    public String getCollectionAdresse() {
        return collectionAdresse;
    }

    public void setCollectionAdresse(String collectionAdresse) {
        this.collectionAdresse = collectionAdresse;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public String getUrlEncode() {
        return urlEncode;
    }

    public void setUrlEncode(String urlEncode) {
        this.urlEncode = urlEncode;
    }

    public String getPathNotice1() {
        return pathNotice1;
    }

    public void setPathNotice1(String pathNotice1) {
        this.pathNotice1 = pathNotice1;
    }

    public String getPathNotice2() {
        return pathNotice2;
    }

    public void setPathNotice2(String pathNotice2) {
        this.pathNotice2 = pathNotice2;
    }

    public boolean isZ3950actif() {
        return z3950actif;
    }

    public void setZ3950actif(boolean z3950actif) {
        this.z3950actif = z3950actif;
    }

    public String getCheminSite() {
        return cheminSite;
    }

    public void setCheminSite(String cheminSite) {
        this.cheminSite = cheminSite;
    }

    public String getUrlCounterBdd() {
        return urlCounterBdd;
    }

    public void setUrlCounterBdd(String urlCounterBdd) {
        this.urlCounterBdd = urlCounterBdd;
    }

    public boolean isWebservices() {
        return webservices;
    }

    public void setWebservices(boolean webservices) {
        this.webservices = webservices;
    }
    

}
