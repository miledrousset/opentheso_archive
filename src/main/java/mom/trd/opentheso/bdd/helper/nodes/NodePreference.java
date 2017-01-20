package mom.trd.opentheso.bdd.helper.nodes;


public class NodePreference {
    private String sourceLang;
    private int nbAlertCdt;
    private boolean alertCdt;

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

}
