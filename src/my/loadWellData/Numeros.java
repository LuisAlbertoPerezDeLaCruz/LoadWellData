/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.text.DecimalFormat;
import java.text.ParseException;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Luis
 */
public class Numeros {
    NumberFormatter number=new NumberFormatter(new DecimalFormat("###0.000"));
    
    public Double valorDouble(String s) {
        int primerPunto=0,segundoPunto=0;
        Double a;
        for (int i=1;i<=s.length()-1;i++) {
            if (s.charAt(i) == '.') {
               if (primerPunto==0) {
                   primerPunto=i;
               } else {
                   segundoPunto=i;
                   break;
               }                       
            }
        }
        if (segundoPunto>0) {
            s=s.substring(0, segundoPunto);
        }
        try {
            a=new Double(s);
        } catch (java.lang.ClassCastException ex) {
            a=0.0;
        }
        return a;
    }
    
    public long valorLong(String s) {
        long l;
        l=new Long(s);
        return l;
    }
    
    public String valorString(Long a) {
        String s;
        s=a.toString().trim();
        return s;
    }
    
    public String valorString(Double a) {
        String s;
        try {
            s=number.valueToString(a);
            s=s.replace(',', '.');
        } catch (ParseException ex) {
            s="0.000";
        }
        return s;
    }
    public void soloDobles(java.awt.event.KeyEvent evt) {
        char inputChar=evt.getKeyChar();
        if ((inputChar>'9' | inputChar <'0') & inputChar!='.') {
            evt.setKeyChar('\b');
        }         
    }    
    public void soloEnteros(java.awt.event.KeyEvent evt) {
        char inputChar=evt.getKeyChar();
        if ((inputChar>'9' | inputChar <'0')) {
            evt.setKeyChar('\b');
        }         
    }
}
