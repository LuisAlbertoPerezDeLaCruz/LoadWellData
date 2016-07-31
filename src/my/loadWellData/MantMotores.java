/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import miLibreria.*;
import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;

/**
 *
 * @author USUARIO
 */
public class MantMotores extends MantPpal {
    public String sDescNodo;
    public ManejoBDI oBD;
    static DefaultListModel<String> modeloLista;   
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    private boolean firstTime=true;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    public Object[] ob_array = null;
    public TipoMotor oTP=new TipoMotor();
            
    
    /**
     * Creates new form MantCorridas
     */
    public MantMotores(java.awt.Frame parent, boolean modal) {
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
        s="SELECT nombre FROM TipoMotor";
        rs=oBD.select(s);
        try {
            while (rs.next()) {
                modeloLista.addElement(rs.getNString("nombre"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MantTaladro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void procesar() {
        String s="";
        String nombre="",motorType="",motorSize="",bearingSection="",
               pwsRelation="",pwsStages="",pwsType="",bendHousingAngle="",elastomer="";
        if (this.jComboBoxMotorType.getSelectedIndex()==0) {
            s+="** Falta Motor Type **\n\r";
        }
        else {
            motorType=this.jComboBoxMotorType.getSelectedItem().toString().trim();
            nombre+=motorType;
        }
        if (this.jComboBoxMotorSize.getSelectedIndex()==0) {
            s+="** Falta Motor Size **\n\r";
        }
        else {
            motorSize=this.jComboBoxMotorSize.getSelectedItem().toString().trim();
            nombre+=motorSize;
        }
        if (this.jComboBoxBearingSection.getSelectedIndex()==0) {
            s+="** Falta Bearing Section **\n\r";
        }
        else {
            bearingSection=this.jComboBoxBearingSection.getSelectedItem().toString().trim();
            nombre+=bearingSection;
        }
        if (this.jComboBoxPWSRelation.getSelectedIndex()==0) {
            s+="** Falta PWS Relation **\n\r";
        }
        else {
            pwsRelation=this.jComboBoxPWSRelation.getSelectedItem().toString().trim();
            nombre+=pwsRelation;
        }
        if (this.jComboBoxPWSStages.getSelectedIndex()==0) {
            s+="** Falta PWS Stages **\n\r";
        }
        else {
            pwsStages=this.jComboBoxPWSStages.getSelectedItem().toString().trim();
            nombre+=pwsStages;
        }
        if (this.jComboBoxPWSType.getSelectedIndex()==0) {
            s+="** Falta PWS Type **\n\r";
        }
        else {
            pwsType=this.jComboBoxPWSType.getSelectedItem().toString().trim();
            nombre+=pwsType;
        }
        if (this.jComboBoxBendHousingAngle.getSelectedIndex()==0) {
            s+="** Falta Bend Housing Angle **\n\r";
        }
        else {
            bendHousingAngle=this.jComboBoxBendHousingAngle.getSelectedItem().toString().trim();
        }
        if ( this.jTextFieldElastomer.getText().isEmpty() ) {
            s+="** Falta Elastomer **\n\r";
        }
        else {
            elastomer=this.jTextFieldElastomer.getText();
        }
        if (s.length()>0) {
            msgbox(s);
            return;
        }
        this.jTextFieldNombre.setText(nombre);
        oTP.setNombre(nombre);
        oTP.setMotorType(motorType);
        oTP.setMotorSize(Long.parseLong(motorSize));
        oTP.setBearingSection(bearingSection);
        oTP.setPWSRelation(Long.parseLong(pwsRelation));
        oTP.setPWSStages(Long.parseLong(pwsStages));
        oTP.setPWSType(pwsType);
        oTP.setBendHousingAngle(Double.parseDouble(bendHousingAngle));
        oTP.setElastomer(elastomer);
        
        if (accion==AGREGAR) { 
            oBD.insert(oTP);
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                msgbox("Motor Incluido exitosamente");
            } else {
                msgbox("Motor No Incluido. Error");
                habilitarControles(false);            
            }
        }
        if (accion==MODIFICAR) {
            if (msgboxYesNo("Confirma los cambios ?")) {
                oBD.update(oTP,"Id="+oTP.getId());
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   msgbox("Motor modificado exitosamente"); 
                }
                else
                {
                    msgbox("Motor No Modoficado. Error");
                    habilitarControles(false);       
                }
            }
        }
        if (accion==ELIMINAR) {
            ResultSet rs;
            rs=oBD.select("SELECT * FROM DirectionalTool WHERE TipoMotorId="+oTP.getId());
            try {
                while (rs.next()) {
                    msgbox("No se puede eliminar este motor porque esta siendo utilizado");
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
                    oBD.delete(oTP, "Id="+oTP.getId());
                    if (oBD.getRegistrsoAfectados()>0){
                       habilitarControles(false); 
                       super.jButtonAgregar.setEnabled(true);
                       super.jButtonModificar.setEnabled(false);
                       super.jButtonEliminar.setEnabled(false);
                       super.jButtonProcesar.setVisible(false);
                       super.jButtonCancelar.setVisible(false);
                       super.accion=NADA;
                       this.setParam(oBD);
                       msgbox("Motor eliminado exitosamente");            
                    }
                    else
                    {
                        msgbox("Motor No Eliminado. Error");
                        habilitarControles(false);       
                    }
                }
                return;
            } catch (NullPointerException | SQLException ex) {
                Logger.getLogger(MantMotores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void agregar() {
        oTP=new TipoMotor();
        this.jComboBoxMotorType.getModel().setSelectedItem("Seleccione");
        this.jComboBoxMotorSize.getModel().setSelectedItem("Seleccione");            
        this.jComboBoxBearingSection.getModel().setSelectedItem("Seleccione");
        this.jComboBoxPWSRelation.getModel().setSelectedItem("Seleccione");
        this.jComboBoxPWSStages.getModel().setSelectedItem("Seleccione");
        this.jComboBoxPWSType.getModel().setSelectedItem("Seleccione");
        this.jComboBoxBendHousingAngle.getModel().setSelectedItem("Seleccione");
        this.jTextFieldElastomer.setText("RM100"); 
        this.jTextFieldNombre.setText("");
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
        this.jComboBoxBearingSection.setEnabled(accion);
        this.jComboBoxBendHousingAngle.setEnabled(accion);
        this.jComboBoxMotorSize.setEnabled(accion);
        this.jComboBoxMotorType.setEnabled(accion);
        this.jComboBoxPWSRelation.setEnabled(accion);
        this.jComboBoxPWSStages.setEnabled(accion);
        this.jComboBoxPWSType.setEnabled(accion);
        this.jTextFieldElastomer.setEnabled(accion);
    }
    
    private void accionClickLista1() {
        String s1;
        try {
            s1=this.jListExistentes.getSelectedValue().toString();
            ob_array=oBD.select(TipoMotor.class, "nombre='"+s1+"'");
            oTP=(TipoMotor) ob_array[0];
            this.jComboBoxMotorType.getModel().setSelectedItem(oTP.getMotorType());
            this.jComboBoxMotorSize.getModel().setSelectedItem(oTP.getMotorSize());            
            this.jComboBoxBearingSection.getModel().setSelectedItem(oTP.getBearingSection());
            this.jComboBoxPWSRelation.getModel().setSelectedItem(oTP.getPWSRelation());
            this.jComboBoxPWSStages.getModel().setSelectedItem(oTP.getPWSStages());
            this.jComboBoxPWSType.getModel().setSelectedItem(oTP.getPWSType());
            this.jComboBoxBendHousingAngle.getModel().setSelectedItem(oTP.getBendHousingAngle());
            this.jTextFieldElastomer.setText(oTP.getElastomer()); 
            this.jTextFieldNombre.setText(oTP.getNombre());
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
        } catch (InstantiationException | IllegalAccessException | NullPointerException ex) {
            Logger.getLogger(MantMotores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void accionClickLista() {
        String s="";
        if (modeloLista.isEmpty()) return;
        try {
            s=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            ob_array=oBD.select(TipoMotor.class, "nombre='"+s+"'");
            oTP= (TipoMotor) ob_array[0];
            this.jComboBoxMotorType.getModel().setSelectedItem(oTP.getMotorType());
            this.jComboBoxMotorSize.getModel().setSelectedItem(oTP.getMotorSize());            
            this.jComboBoxBearingSection.getModel().setSelectedItem(oTP.getBearingSection());
            this.jComboBoxPWSRelation.getModel().setSelectedItem(oTP.getPWSRelation());
            this.jComboBoxPWSStages.getModel().setSelectedItem(oTP.getPWSStages());
            this.jComboBoxPWSType.getModel().setSelectedItem(oTP.getPWSType());
            this.jComboBoxBendHousingAngle.getModel().setSelectedItem(oTP.getBendHousingAngle());
            this.jTextFieldElastomer.setText(oTP.getElastomer()); 
            this.jTextFieldNombre.setText(oTP.getNombre());
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantMotores.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel5 = new javax.swing.JLabel();
        jComboBoxMotorSize = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxMotorType = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxBearingSection = new javax.swing.JComboBox();
        jComboBoxPWSRelation = new javax.swing.JComboBox();
        jComboBoxPWSStages = new javax.swing.JComboBox();
        jComboBoxPWSType = new javax.swing.JComboBox();
        jComboBoxBendHousingAngle = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldElastomer = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListExistentes = new javax.swing.JList();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldNombre = new javax.swing.JTextField();

        jLabelDescContenido.setText("Contenido:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 360));
        setModal(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Motor Type:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, -1, -1));

        jComboBoxMotorSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "475", "675", "700", "800", "825", "962" }));
        jComboBoxMotorSize.setEnabled(false);
        getContentPane().add(jComboBoxMotorSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, 100, -1));

        jLabel6.setText("Nombre:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, -1, -1));

        jComboBoxMotorType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "A", "F" }));
        jComboBoxMotorType.setEnabled(false);
        jComboBoxMotorType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMotorTypeActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxMotorType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 100, -1));

        jLabel7.setText("Motor Size:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, -1, -1));

        jComboBoxBearingSection.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "M", "S", "XC", "XF", " " }));
        jComboBoxBearingSection.setEnabled(false);
        getContentPane().add(jComboBoxBearingSection, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 120, 100, -1));

        jComboBoxPWSRelation.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "12", "23", "34", "45", "78" }));
        jComboBoxPWSRelation.setEnabled(false);
        getContentPane().add(jComboBoxPWSRelation, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 150, 100, -1));

        jComboBoxPWSStages.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "4", "5", "40", "66" }));
        jComboBoxPWSStages.setEnabled(false);
        jComboBoxPWSStages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPWSStagesActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxPWSStages, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 180, 100, -1));

        jComboBoxPWSType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "SP", "XP", "GT", "ERT", "HF", "AF" }));
        jComboBoxPWSType.setEnabled(false);
        getContentPane().add(jComboBoxPWSType, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 210, 100, -1));

        jComboBoxBendHousingAngle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione", "0", "0.261", "0.518", "0.765", "1", "1.218", "1.414", "1.587", "1.732", "1.848", "1.932", "1.983", "2", "0.392", "0.776", "1.148", "1.5", "1.826", "2.121", "2.38", "2.598", "2.772", "2.898", "2.974", "3" }));
        jComboBoxBendHousingAngle.setEnabled(false);
        getContentPane().add(jComboBoxBendHousingAngle, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, 100, -1));

        jLabel8.setText("Bearing Section:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, -1, -1));

        jLabel9.setText("PWS Relation:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, -1, -1));

        jLabel10.setText("PWS Stages:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, -1, -1));

        jLabel11.setText("PWS Type:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, -1, -1));

        jLabel12.setText("Bend Housing Angle:");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 240, -1, -1));

        jTextFieldElastomer.setText("RM100");
        jTextFieldElastomer.setEnabled(false);
        getContentPane().add(jTextFieldElastomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 270, 100, -1));

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

        jLabel13.setText("Elastomer:");
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, -1, -1));

        jTextFieldNombre.setEnabled(false);
        jTextFieldNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNombreActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 100, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void jComboBoxMotorTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMotorTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxMotorTypeActionPerformed

    private void jComboBoxPWSStagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPWSStagesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxPWSStagesActionPerformed

    private void jTextFieldNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombreActionPerformed
    
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
            java.util.logging.Logger.getLogger(MantMotores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantMotores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantMotores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantMotores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantMotores dialog = new MantMotores(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox jComboBoxBearingSection;
    private javax.swing.JComboBox jComboBoxBendHousingAngle;
    private javax.swing.JComboBox jComboBoxMotorSize;
    private javax.swing.JComboBox jComboBoxMotorType;
    private javax.swing.JComboBox jComboBoxPWSRelation;
    private javax.swing.JComboBox jComboBoxPWSStages;
    private javax.swing.JComboBox jComboBoxPWSType;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JList jListExistentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldElastomer;
    private javax.swing.JTextField jTextFieldNombre;
    // End of variables declaration//GEN-END:variables
}
