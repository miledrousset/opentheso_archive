/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.util.Date;

/**
 *
 * @author jm.prudham
 */
public class AlertStruct {
    private String thesaurusEnAcces;
    private boolean alertB;
    private Date date_debut_envoi_cdt_propos;
    private Date date_debut_envoi_cdt_valid;
    private int period_envoi_cdt_propos;
    private int period_envoi_cdt_valid;

    public AlertStruct() {
    }
    
    
    public AlertStruct(String thesaurusEnAcces, boolean alertB, Date date_debut_envoi_cdt_propos, Date date_debut_envoi_cdt_valid, int period_envoi_cdt_propos, int period_envoi_cdt_valid) {
        this.thesaurusEnAcces = thesaurusEnAcces;
        this.alertB = alertB;
        this.date_debut_envoi_cdt_propos = date_debut_envoi_cdt_propos;
        this.date_debut_envoi_cdt_valid = date_debut_envoi_cdt_valid;
        this.period_envoi_cdt_propos = period_envoi_cdt_propos;
        this.period_envoi_cdt_valid = period_envoi_cdt_valid;
    }

    public String getThesaurusEnAcces() {
        return thesaurusEnAcces;
    }

    public boolean isAlertB() {
        return alertB;
    }

    public Date getDate_debut_envoi_cdt_propos() {
        return date_debut_envoi_cdt_propos;
    }

    public Date getDate_debut_envoi_cdt_valid() {
        return date_debut_envoi_cdt_valid;
    }

    public int getPeriod_envoi_cdt_propos() {
        return period_envoi_cdt_propos;
    }

    public int getPeriod_envoi_cdt_valid() {
        return period_envoi_cdt_valid;
    }

    public void setThesaurusEnAcces(String thesaurusEnAcces) {
        this.thesaurusEnAcces = thesaurusEnAcces;
    }

    public void setAlertB(boolean alertB) {
        this.alertB = alertB;
    }

    public void setDate_debut_envoi_cdt_propos(Date date_debut_envoi_cdt_propos) {
        this.date_debut_envoi_cdt_propos = date_debut_envoi_cdt_propos;
    }

    public void setDate_debut_envoi_cdt_valid(Date date_debut_envoi_cdt_valid) {
        this.date_debut_envoi_cdt_valid = date_debut_envoi_cdt_valid;
    }

    public void setPeriod_envoi_cdt_propos(int period_envoi_cdt_propos) {
        this.period_envoi_cdt_propos = period_envoi_cdt_propos;
    }

    public void setPeriod_envoi_cdt_valid(int period_envoi_cdt_valid) {
        this.period_envoi_cdt_valid = period_envoi_cdt_valid;
    }
    
    
}
