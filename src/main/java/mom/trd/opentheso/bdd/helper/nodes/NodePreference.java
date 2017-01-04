package mom.trd.opentheso.bdd.helper.nodes;


public class NodePreference {
    private String sourceLang;
    private int nbAlertCdt;
    private boolean alertCdt;
    private boolean gps_integrertraduction;
    private boolean gps_reemplacertraduction;
    private boolean gps_alignementautomatique;
    private int gps_id_source;

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

    public boolean isGps_integrertraduction() {
        return gps_integrertraduction;
    }

    public void setGps_integrertraduction(boolean gps_integrertraduction) {
        this.gps_integrertraduction = gps_integrertraduction;
    }

    public boolean isGps_reemplacertraduction() {
        return gps_reemplacertraduction;
    }

    public void setGps_reemplacertraduction(boolean gps_reemplacertraduction) {
        this.gps_reemplacertraduction = gps_reemplacertraduction;
    }

    public boolean isGps_alignementautomatique() {
        return gps_alignementautomatique;
    }

    public void setGps_alignementautomatique(boolean gps_alignementautomatique) {
        this.gps_alignementautomatique = gps_alignementautomatique;
    }

    public int getGps_id_source() {
        return gps_id_source;
    }

    public void setGps_id_source(int gps_id_source) {
        this.gps_id_source = gps_id_source;
    }
    
    
}
