/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mom.trd.opentheso.SelectedBeans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "vue", eager = true)
@SessionScoped

public class Vue implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    PropertiesNames propertiesNames = new PropertiesNames();
    /*
    Pour le choix de l'édition des BT
    */
    
    /*Attributs edition*/
    private boolean language = false;
    private boolean edit = false;
    private boolean creat = false;
    private boolean trad = false;
    private boolean addSkos = false;
    private boolean addCsv = false;
    private boolean thesoToSkosCsv = false;
    private boolean thesoToSkosCsvFile = false;
    
    /*Attributs gestion*/

    
//    private boolean addDom = false;
    private int selectedActionDom = PropertiesNames.noActionDom;    
    private int addTInfo = 0;
    private boolean addTSpe = false;
    private int addTGen = 0;
    private int delTGen = 0;
    private int moveBranch = 0;
    private int moveBranchToMT = 0;
    private int moveBranchFromMT = 0;
    private int moveBranchFromOrphin = 0;   
    private int moveBranchFromOrphinToMT = 0;
    
    private int addTAsso = 0;
    private int addTSyno = 0;
    private int addNote = 0;
    private int addAlign = 0;
    private boolean addImage = false;
    private boolean addFacette = false;
    private boolean facette = false;
    private int addTrad = 0;
    private boolean cptToSkos = false;
    private boolean branchToSkos = false;
    private boolean branchToSkosFile = false;
    
    private int onglet = 0;
    
    /*Attributs candidats*/
    private boolean addCandidat = false;
    private boolean addPropCandidat = false;
    private boolean addInsertCdt = false;
    private boolean addValidCdt = false;
    private boolean editCandidat = false;
    private boolean editPropCandidat = false;
    
    /*Attributs OpenIndex*/
    private boolean download = false;
    private boolean upload = false;

    /*Attributs configurations*/
    private boolean addUser = false;
    private boolean editUser = false;
    
    /*Attributs statistiques*/
    private boolean statTheso = false;
    private boolean statCpt = false;
    
    public Vue() {
    }

    public boolean isLanguage() {
        return language;
    }

    
    public void setLanguage(boolean language) {
        this.language = language;
        creat = false;
        edit = false;
        trad = false;
        addSkos = false;
        addCsv = false;
    }

    public int getAddTGen() {
        return addTGen;
    }

    public void setAddTGen(int addTGen) {
        this.addTGen = addTGen;
    }

    public int getDelTGen() {
        return delTGen;
    }

    public void setDelTGen(int delTGen) {
        this.delTGen = delTGen;
    }

    public int getMoveBranch() {
        return moveBranch;
    }

    public void setMoveBranch(int moveBranch) {
        this.moveBranch = moveBranch;
    }
    
    public int getMoveBranchToMT() {
        return moveBranchToMT;
    }

    public void setMoveBranchToMT(int moveBranchToMT) {
        this.moveBranchToMT = moveBranchToMT;
    }

    public int getMoveBranchFromMT() {
        return moveBranchFromMT;
    }

    public void setMoveBranchFromMT(int moveBranchFromMT) {
        this.moveBranchFromMT = moveBranchFromMT;
    }

    public int getMoveBranchFromOrphinToMT() {
        return moveBranchFromOrphinToMT;
    }

    public void setMoveBranchFromOrphinToMT(int moveBranchFromOrphinToMT) {
        this.moveBranchFromOrphinToMT = moveBranchFromOrphinToMT;
    }



    public int getMoveBranchFromOrphin() {
        return moveBranchFromOrphin;
    }

    public void setMoveBranchFromOrphin(int moveBranchFromOrphin) {
        this.moveBranchFromOrphin = moveBranchFromOrphin;
    }
    
    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
        trad = false;
        creat = false;
        addSkos = false;
        addCsv = false;
    }

    public boolean isCreat() {
        return creat;
    }

    public void setCreat(boolean creat) {
        this.creat = creat;
        edit = false;
        trad = false;
        language = false;
        addSkos = false;
        addCsv = false;
    }

    public boolean isTrad() {
        return trad;
    }

    public void setTrad(boolean trad) {
        this.trad = trad;
        edit = false;
        creat = false;
        addSkos = false;
        addCsv = false;
    }

    public boolean isAddSkos() {
        return addSkos;
    }

    public void setAddSkos(boolean addSkos) {
        this.addSkos = addSkos;
        edit = false;
        trad = false;
        language = false;
        creat = false;
        addCsv = false;
    }
    
    public int getSelectedActionDom() {
        return selectedActionDom;
    }    
    
    public void setSelectedActionDom(int propertie) {
        this.selectedActionDom = propertie;
    }    

    public boolean isAddTSpe() {
        return addTSpe;
    }

    public void setAddTSpe(boolean addTSpe) {
        this.addTSpe = addTSpe;
    }

    public int getAddTAsso() {
        return addTAsso;
    }

    public void setAddTAsso(int addTAsso) {
        this.addTAsso = addTAsso;
    }

    public int getAddTSyno() {
        return addTSyno;
    }

    public void setAddTSyno(int addTSyno) {
        this.addTSyno = addTSyno;
    }

    public int getAddNote() {
        return addNote;
    }

    public void setAddNote(int addNote) {
        this.addNote = addNote;
    }

    public boolean isAddImage() {
        return addImage;
    }

    public void setAddImage(boolean addImage) {
        this.addImage = addImage;
    }

    public boolean isAddFacette() {
        return addFacette;
    }

    public void setAddFacette(boolean addFacette) {
        this.addFacette = addFacette;
    }

    public int getAddTrad() {
        return addTrad;
    }

    public void setAddTrad(int addTrad) {
        this.addTrad = addTrad;
    }

    public int getAddTInfo() {
        return addTInfo;
    }

    public void setAddTInfo(int addTInfo) {
        this.addTInfo = addTInfo;
    }

    public boolean isAddCandidat() {
        return addCandidat;
    }

    public void setAddCandidat(boolean addCandidat) {
        this.addCandidat = addCandidat;
    }

    public boolean isAddPropCandidat() {
        return addPropCandidat;
    }

    public void setAddPropCandidat(boolean addPropCandidat) {
        this.addPropCandidat = addPropCandidat;
    }

    public boolean isAddValidCdt() {
        return addValidCdt;
    }

    public void setAddValidCdt(boolean addValidCdt) {
        this.addValidCdt = addValidCdt;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public boolean isAddInsertCdt() {
        return addInsertCdt;
    }

    public void setAddInsertCdt(boolean addInsertCdt) {
        this.addInsertCdt = addInsertCdt;
    }

    public int getOnglet() {
        return onglet;
    }

    public void setOnglet(int onglet) {
        this.onglet = onglet;
    }

    public boolean isAddUser() {
        return addUser;
    }

    public void setAddUser(boolean addUser) {
        this.addUser = addUser;
        editUser = false;
    }

    public boolean isEditUser() {
        return editUser;
    }

    public void setEditUser(boolean editUser) {
        this.editUser = editUser;
        addUser = false;
    }

    public boolean isCptToSkos() {
        return cptToSkos;
    }

    public void setCptToSkos(boolean cptToSkos) {
        this.cptToSkos = cptToSkos;
    }

    public boolean isBranchToSkos() {
        return branchToSkos;
    }

    public void setBranchToSkos(boolean branchToSkos) {
        this.branchToSkos = branchToSkos;
    }

    public boolean isThesoToSkosCsv() {
        return thesoToSkosCsv;
    }

    public void setThesoToSkosCsv(boolean thesoToSkosCsv) {
        this.thesoToSkosCsv = thesoToSkosCsv;
    }

    public boolean isStatTheso() {
        return statTheso;
    }

    public void setStatTheso(boolean statTheso) {
        this.statTheso = statTheso;
    }

    public boolean isStatCpt() {
        return statCpt;
    }

    public void setStatCpt(boolean statCpt) {
        this.statCpt = statCpt;
    }

    public boolean isBranchToSkosFile() {
        return branchToSkosFile;
    }

    public void setBranchToSkosFile(boolean branchToSkosFile) {
        this.branchToSkosFile = branchToSkosFile;
    }

    public boolean isThesoToSkosCsvFile() {
        return thesoToSkosCsvFile;
    }

    public void setThesoToSkosCsvFile(boolean thesoToSkosCsvFile) {
        this.thesoToSkosCsvFile = thesoToSkosCsvFile;
    }

    public boolean isFacette() {
        return facette;
    }

    public void setFacette(boolean facette) {
        this.facette = facette;
    }

    public boolean isEditCandidat() {
        return editCandidat;
    }

    public void setEditCandidat(boolean editCandidat) {
        this.editCandidat = editCandidat;
    }

    public boolean isEditPropCandidat() {
        return editPropCandidat;
    }

    public void setEditPropCandidat(boolean editPropCandidat) {
        this.editPropCandidat = editPropCandidat;
    }

    public int getAddAlign() {
        return addAlign;
    }

    public void setAddAlign(int addAlign) {
        this.addAlign = addAlign;
    }

    public boolean isAddCsv() {
        return addCsv;
    }

    public void setAddCsv(boolean addCsv) {
        this.addCsv = addCsv;
        edit = false;
        trad = false;
        creat = false;
        language = false;
        addSkos = false;
    }
}
