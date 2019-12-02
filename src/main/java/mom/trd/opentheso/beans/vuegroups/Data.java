/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom.trd.opentheso.beans.vuegroups;

import java.io.Serializable;

/**
 *
 * @author miledrousset
 */
public class Data implements Serializable {
 
    private String name;
     
    private String size;
     
    private String miled;
    private String type;
    
    public Data(String name, String size, String type, String miled) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.miled = miled;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getSize() {
        return size;
    }
 
    public void setSize(String size) {
        this.size = size;
    }
 
    public String getType() {
        return type;
    }
 
    public void setType(String type) {
        this.type = type;
    }

    public String getMiled() {
        return miled;
    }

    public void setMiled(String miled) {
        this.miled = miled;
    }
 
    @Override
    public String toString() {
        return name;
    }

}
