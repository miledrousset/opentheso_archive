/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.timeJob;

import java.sql.Date;

/**
 *
 * @author jm.prudham
 */
public class SparqlStruct {
    private String adresseServeur;
    private String  mot_de_passe;//en clair?
    private String nom_d_utilisateur;
    private String graph;
    private boolean synchro;
    private String thesaurus;
    private Date heure;

    public SparqlStruct(String adresseServeur, String mot_de_passe, String nom_d_utilisateur, String graph, boolean synchro, String thesaurus) {
        this.adresseServeur = adresseServeur;
        this.mot_de_passe = mot_de_passe;
        this.nom_d_utilisateur = nom_d_utilisateur;
        this.graph = graph;
        this.synchro = synchro;
        this.thesaurus = thesaurus;
    }

    public SparqlStruct() {
    }
    
    public String getAdresseServeur() {
        return adresseServeur;
    }

    public void setAdresseServeur(String adresseServeur) {
        this.adresseServeur = adresseServeur;
    }

    public String getMot_de_passe() {
        return mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        this.mot_de_passe = mot_de_passe;
    }

    public String getNom_d_utilisateur() {
        return nom_d_utilisateur;
    }

    public void setNom_d_utilisateur(String nom_d_utilisateur) {
        this.nom_d_utilisateur = nom_d_utilisateur;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public boolean isSynchro() {
        return synchro;
    }

    public void setSynchro(boolean synchro) {
        this.synchro = synchro;
    }

    public String getThesaurus() {
        return thesaurus;
    }

    public void setThesaurus(String thesaurus) {
        this.thesaurus = thesaurus;
    }

    public Date getHeure() {
        return heure;
    }

    public void setHeure(Date heure) {
        this.heure = heure;
    }
    
    
    
    
    
}
