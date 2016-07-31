/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

/**
 *
 * @author USUARIO
 */
public class ClaveDesc {
    long clave;
    String desc;
    
    @Override
    public String toString(){
        return desc;
    }
    public void setClave(long l){
        clave=l;
    }
    public void setDesc(String d) {
        desc=d;
    }
    public String getDesc(){
        return desc;
    }
    public long getClave(){
        return clave;
    }
}
