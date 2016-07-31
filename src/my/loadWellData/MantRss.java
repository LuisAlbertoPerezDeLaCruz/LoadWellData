/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.Timer;
import miLibreria.*;
import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;

/**
 *
 * @author USUARIO
 */
public class MantRss extends MantPpal {
    public String sDescNodo;
    public ManejoBDI oBD;
    static DefaultListModel<String> modeloLista;   
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    private boolean firstTime=true;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    public Object[] ob_array = null;
    public RSS oRSS=new RSS();
            
    
    /**
     * Creates new form MantCorridas
     */
    public MantRss(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        oBD=new ManejoBDAccess();
        super.jButtonProcesar.setVisible(false);
        super.jButtonCancelar.setVisible(false);
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
    }
    
    public void setParam(ManejoBDI o) {
        oBD=o;   
        ResultSet rs;
        modeloLista = new DefaultListModel<>();
        this.jListExistentes.setModel(modeloLista);
        String s;
        s="SELECT nombre FROM RSS";
        rs=oBD.select(s);
        try {
            while (rs.next()) {
                modeloLista.addElement(rs.getNString("nombre"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MantRss.class.getName()).log(Level.SEVERE, null, ex);
        }
        oManejoDeCombos.llenaCombo(oBD, modeloCombo, TipoRss.class, jComboBoxRssType);
        this.jRadioButtonDummy.setVisible(false);
        setearTimer();
    }
    
     public final void setearTimer() {
        int tiempoEnMilisegundos = 100;
        Timer timer = new Timer (tiempoEnMilisegundos, new ActionListener () { 
        public void actionPerformed(ActionEvent e) {
            if(accion==MODIFICAR || accion==AGREGAR ) {
                if ("Xceed".equals(jComboBoxRssType.getSelectedItem().toString())) {
                    jRadioButtonOffset08.setEnabled(true);
                    jRadioButtonOffset06.setEnabled(true);
                    jRadioButtonOffset05.setEnabled(true);
                } else {
                    jRadioButtonOffset08.setEnabled(false);
                    jRadioButtonOffset06.setEnabled(false);
                    jRadioButtonOffset05.setEnabled(false);                
                }
                if ("Archer".equals(jComboBoxRssType.getSelectedItem().toString())) 
                   jTextFieldStrikeRingAngle.setEnabled(true);
                else
                   jTextFieldStrikeRingAngle.setEnabled(false);
                if ("PD Orbit".equals(jComboBoxRssType.getSelectedItem().toString())) 
                   jTextFieldClearance.setEnabled(true);
                else
                   jTextFieldClearance.setEnabled(false);            
                pack();
            }
        } 
        });
        timer.start();
    }

    private void setearSegunTipoRss() {
         if (jComboBoxRssType.getSelectedItem().toString().indexOf("Xceed")>-1) {
             this.jTextFieldStrikeRingAngle.setText("");
             this.jTextFieldClearance.setText("");
         }
         if (jComboBoxRssType.getSelectedItem().toString().indexOf("Orbit")>-1) {
             this.jTextFieldStrikeRingAngle.setText("");
             this.jRadioButtonDummy.setSelected(true);
         }
         if (jComboBoxRssType.getSelectedItem().toString().indexOf("Archer")>-1)  {
             this.jTextFieldClearance.setText("");
             this.jRadioButtonDummy.setSelected(true);
         }
    }
    
    public void procesar() {
        String s="";
        String nombre="", rssType="";
        long rssTypeId=0;
        double offSet=0, strikeRingAngle=0,clearance=0;
        
        nombre=this.jTextFieldNombre.getText();
        
        if (this.jRadioButtonOffset05.isSelected()) offSet=0.5;
        if (this.jRadioButtonOffset06.isSelected()) offSet=0.6;
        if (this.jRadioButtonOffset08.isSelected()) offSet=0.8;
        try {
            strikeRingAngle=Double.parseDouble(this.jTextFieldStrikeRingAngle.getText());
            clearance=Double.parseDouble(this.jTextFieldClearance.getText());
        } catch (NumberFormatException ex){}
        if (this.jComboBoxRssType.getSelectedIndex()==0) {
            s+="** Falta Rss Type **\n\r";
        }
        else {
            rssType=this.jComboBoxRssType.getSelectedItem().toString().trim();
            rssTypeId=oManejoDeCombos.getComboID(jComboBoxRssType);
        }
        if ("".equals(this.jTextFieldNombre.getText().trim())) {
           s+="** Falta nombre **\n\r"; 
        }
        if ("Xceed".equals(jComboBoxRssType.getSelectedItem().toString())) {
            if (offSet==0) {
               s+="** Falta offSet **\n\r";  
            }
        }
        if ("Archer".equals(jComboBoxRssType.getSelectedItem().toString())) {
            if (strikeRingAngle==0) {
               s+="** Falta Strike Ring Angle **\n\r";  
            }
        }
        if ("PD Orbit".equals(jComboBoxRssType.getSelectedItem().toString())) {
            if (strikeRingAngle==0) {
               s+="** Falta Clearance **\n\r";  
            }
        }
        if (s.length()>0) {
            msgbox(s);
            return;
        }

        oRSS.setNombre(nombre);
        oRSS.setOffSet(offSet);
        oRSS.setStrikeRingAngle(strikeRingAngle);
        oRSS.setClearance(clearance);
        oRSS.setTipoRssId(rssTypeId);
        
        
        if (accion==AGREGAR) { 
            oBD.insert(oRSS);
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                msgbox("RSS Incluido exitosamente");
            } else {
                msgbox("RSS No Incluido. Error");
                habilitarControles(false);            
            }
        }
        if (accion==MODIFICAR) {
            if (msgboxYesNo("Confirma los cambios ?")) {
                oBD.update(oRSS,"Id="+oRSS.getId());
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   msgbox("RSS modificado exitosamente"); 
                }
                else
                {
                    msgbox("RSS No Modoficado. Error");
                    habilitarControles(false);       
                }
            }
        }
        if (accion==ELIMINAR) {
            ResultSet rs;
            rs=oBD.select("SELECT * FROM DirectionalTool WHERE rssId="+oRSS.getId());
            try {
                while (rs.next()) {
                    msgbox("No se puede eliminar este rss porque esta siendo utilizado");
                    super.jButtonAgregar.setEnabled(true);
                    super.jButtonModificar.setEnabled(false);
                    super.jButtonEliminar.setEnabled(false);
                    super.jButtonProcesar.setVisible(false);
                    super.jButtonCancelar.setVisible(false);
                    super.accion=NADA;
                    habilitarControles(false);  
                    return;                    
                }
                if (msgboxYesNo("Confirma la eliminacion ?")) {
                    oBD.delete(oRSS, "Id="+oRSS.getId());
                    if (oBD.getRegistrsoAfectados()>0){
                       habilitarControles(false); 
                       super.jButtonAgregar.setEnabled(true);
                       super.jButtonModificar.setEnabled(false);
                       super.jButtonEliminar.setEnabled(false);
                       super.jButtonProcesar.setVisible(false);
                       super.jButtonCancelar.setVisible(false);
                       super.accion=NADA;
                       this.setParam(oBD);
                       msgbox("RSS eliminado exitosamente");            
                    }
                    else
                    {
                        msgbox("RSS No Eliminado. Error");
                        habilitarControles(false);       
                    }
                }
                return;
            } catch (NullPointerException | SQLException ex) {
                Logger.getLogger(MantRss.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void agregar() {
        oRSS=new RSS();
        this.jComboBoxRssType.getModel().setSelectedItem("Seleccione");
        this.jTextFieldNombre.setText("");
        this.jTextFieldStrikeRingAngle.setText("");
        this.jTextFieldClearance.setText("");
        this.jRadioButtonDummy.setSelected(true);
        habilitarControles(true);
    }
    
    public void modificar() {
        habilitarControles(true);
    }
    
    public void cancelar() {
        accion=NADA;
        setearControles();
        accionClickLista();
        habilitarControles(false);
    }
    
    private void habilitarControles(boolean accion) {
        this.jComboBoxRssType.setEnabled(accion);
        this.jTextFieldNombre.setEnabled(accion);
        this.jTextFieldStrikeRingAngle.setEnabled(accion);
        this.jTextFieldClearance.setEnabled(accion);
        this.jRadioButtonOffset08.setEnabled(accion);
        this.jRadioButtonOffset06.setEnabled(accion);
        this.jRadioButtonOffset05.setEnabled(accion);
    }
    
   
    private void accionClickLista() {
        String s="";
        if (modeloLista.isEmpty()) return;
        try {
            s=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            ob_array=oBD.select(RSS.class, "nombre='"+s+"'");
            oRSS= (RSS) ob_array[0];
            oManejoDeCombos.setCombo(oRSS.getTipoRssId(), jComboBoxRssType);
            if (oRSS.getOffSet()==0.8) {
                this.jRadioButtonOffset08.setSelected(true);
            } else if (oRSS.getOffSet()==0.6) {
                this.jRadioButtonOffset06.setSelected(true);
            } else if (oRSS.getOffSet()==0.5) {
                this.jRadioButtonOffset05.setSelected(true);
            } else this.jRadioButtonDummy.setSelected(true);
            this.jTextFieldStrikeRingAngle.setText(Double.toString(oRSS.getStrikeRingAngle()));
            this.jTextFieldClearance.setText(Double.toString(oRSS.getClearance()));
            this.jTextFieldNombre.setText(oRSS.getNombre());
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantRss.class.getName()).log(Level.SEVERE, null, ex);
        }
          catch (ArrayIndexOutOfBoundsException ex) {} 
        if (accion!=NADA) {
            cancelar();
        }       
    }    
    
    public long getComboID (JComboBox j){
        ComboItem oCI;
        oCI= (ComboItem) j.getSelectedItem();
        return new Long(oCI.getValue());        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelDescContenido = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxRssType = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldStrikeRingAngle = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListExistentes = new javax.swing.JList();
        jTextFieldNombre = new javax.swing.JTextField();
        jTextFieldClearance = new javax.swing.JTextField();
        jRadioButtonOffset08 = new javax.swing.JRadioButton();
        jRadioButtonOffset06 = new javax.swing.JRadioButton();
        jRadioButtonOffset05 = new javax.swing.JRadioButton();
        jRadioButtonDummy = new javax.swing.JRadioButton();

        jLabelDescContenido.setText("Contenido:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 360));
        setModal(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Rss Type:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel6.setText("Nombre:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, -1, -1));

        jComboBoxRssType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "A", "F" }));
        jComboBoxRssType.setEnabled(false);
        jComboBoxRssType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRssTypeActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxRssType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 230, -1));

        jLabel7.setText("OffSet:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 124, -1, 20));

        jTextFieldStrikeRingAngle.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldStrikeRingAngle.setEnabled(false);
        jTextFieldStrikeRingAngle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldStrikeRingAngleKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldStrikeRingAngle, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 170, 80, -1));

        jLabel8.setText("Strike Ring Angle:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, -1, -1));

        jLabel9.setText("Clearance:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, -1, -1));

        jListExistentes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListExistentes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListExistentesMouseClicked(evt);
            }
        });
        jListExistentes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListExistentesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListExistentes);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 31, 140, 240));

        jTextFieldNombre.setEnabled(false);
        jTextFieldNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNombreActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 40, 230, -1));

        jTextFieldClearance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldClearance.setEnabled(false);
        jTextFieldClearance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldClearanceKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldClearance, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 210, 80, -1));

        buttonGroup1.add(jRadioButtonOffset08);
        jRadioButtonOffset08.setText("0.8");
        jRadioButtonOffset08.setEnabled(false);
        jRadioButtonOffset08.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonOffset08ActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonOffset08, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 120, -1, -1));

        buttonGroup1.add(jRadioButtonOffset06);
        jRadioButtonOffset06.setText("0.6");
        jRadioButtonOffset06.setEnabled(false);
        getContentPane().add(jRadioButtonOffset06, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, -1, -1));

        buttonGroup1.add(jRadioButtonOffset05);
        jRadioButtonOffset05.setText("0.5");
        jRadioButtonOffset05.setEnabled(false);
        getContentPane().add(jRadioButtonOffset05, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 120, -1, -1));

        buttonGroup1.add(jRadioButtonDummy);
        jRadioButtonDummy.setText("dummy");
        getContentPane().add(jRadioButtonDummy, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 120, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void jComboBoxRssTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxRssTypeActionPerformed
        setearSegunTipoRss();
    }//GEN-LAST:event_jComboBoxRssTypeActionPerformed

    private void jTextFieldNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombreActionPerformed

    private void jTextFieldStrikeRingAngleKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldStrikeRingAngleKeyTyped
        soloDobles(evt);
    }//GEN-LAST:event_jTextFieldStrikeRingAngleKeyTyped

    private void jTextFieldClearanceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldClearanceKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldClearanceKeyTyped

    private void jRadioButtonOffset08ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonOffset08ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonOffset08ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MantRss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantRss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantRss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantRss.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantRss dialog = new MantRss(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBoxRssType;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JList jListExistentes;
    private javax.swing.JRadioButton jRadioButtonDummy;
    private javax.swing.JRadioButton jRadioButtonOffset05;
    private javax.swing.JRadioButton jRadioButtonOffset06;
    private javax.swing.JRadioButton jRadioButtonOffset08;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldClearance;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldStrikeRingAngle;
    // End of variables declaration//GEN-END:variables
}
