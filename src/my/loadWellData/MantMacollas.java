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
public class MantMacollas extends MantPpal {
    public String sDescNodo;
    public ManejoBDI oBD;
    private Macolla oMacolla;
    private Campo oCampo;
    static DefaultListModel<ClaveDesc> modeloLista;   
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    private boolean firstTime=true;
    public long currentCampoId;
    private long macollaId;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
            
    
    /**
     * Creates new form MantCorridas
     */
    public MantMacollas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void setParam(ManejoBDI o) {
        String s,s1;
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
        try {
            ResultSet rs;
            ClaveDesc ocd;
            modeloLista = new DefaultListModel<>();
            this.jListExistentes.setModel(modeloLista);
            this.jListExistentes.repaint();
            oBD=o;
            oCampo=(Campo) oBD.select(Campo.class, "nombre='" + sDescNodo + "'" )[0];
            this.jTextFieldCampo.setText(oCampo.getNombre());
            
            s="SELECT * from ConsultaMacolla1 WHERE campoId="+oCampo.getId()+";";            
            rs=oBD.select(s);
            while (rs.next()){
                s1="Cliente:"+rs.getString("clientesNombre");
                s1+=", Macolla:" + rs.getString("macollaNombre");
                s1+=", Nro de Pozos:" + rs.getLong("totalNumberOfWells");
                s1+=".";
                ocd=new ClaveDesc();
                ocd.setClave(rs.getLong("macollaId"));
                ocd.setDesc(s1);
                modeloLista.addElement(ocd);
            }                
            oManejoDeCombos.llenaCombo(oBD,modeloCombo,Clientes.class,this.jComboBoxCliente); 
                      
            
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(MantMacollas.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
    public void procesar() {
        String s="";
        long clienteId=0;
        long totalNumberOfWells=0;
        long campoClienteId=0;
        String macollaNombre="";
        ComboItem oCI;
        
        if (this.jComboBoxCliente.getSelectedIndex()==0) {
            s+="** Falta Cliente **\n\r";
        }
        else {
            clienteId=getComboID(this.jComboBoxCliente);
        }
        
        if (this.jTextFieldMacollaNombre.getText().trim().isEmpty()) {
            s+="** Falta Nombre de la Macolla **\n\r";
        }
        else {
            macollaNombre=this.jTextFieldMacollaNombre.getText().trim();
        }        
               
        if (this.jTextFieldTotalNumberOfWells.getText().trim().isEmpty()){
            s+="** Falta Cantidad de pozos **\n\r";            
        }
        else {
            totalNumberOfWells=oNumeros.valorLong(this.jTextFieldTotalNumberOfWells.getText());
        }
        
        Object o=null;
      
        if (s.length()>0) {
            msgbox(s);
            return;
        }
        Object []a=null;
        CampoCliente oCampoCliente=new CampoCliente();
        try {
            a=oBD.select(CampoCliente.class, "clienteId="+clienteId + " AND campoId="+oCampo.getId());
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantMacollas.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (a.length>0) {
           oCampoCliente=(CampoCliente) a[0];
           campoClienteId=oCampoCliente.getId();
        } else {
          oCampoCliente.setCampoId(oCampo.getId());
          oCampoCliente.setClienteId(clienteId);
          oBD.insert(oCampoCliente);
          campoClienteId=oBD.ultimaClave(oCampoCliente);
        }
            
        if (accion==AGREGAR) {
            oMacolla=new Macolla();
            oMacolla.setCampoClienteId(campoClienteId);         
            oMacolla.setTotalNumberOfWells(totalNumberOfWells);
            oMacolla.setNombre(macollaNombre);
            
            oBD.insert(oMacolla);
            macollaId=oBD.ultimaClave(oMacolla);
            if (oBD.getRegistrsoAfectados()>0){                
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                this.setParam(oBD);
                super.accion=NADA;
                msgbox("Macolla Incluida exitosamente");
            } else {
                msgbox("Macolla No Incluida. Error");
                habilitarControles(false);            
            }
        }
        
        if (accion==MODIFICAR) {
            oMacolla=new Macolla();
            oMacolla.setNombre(macollaNombre);
            oMacolla.setTotalNumberOfWells(totalNumberOfWells);
            oMacolla.setCampoClienteId(campoClienteId);
            oBD.update(oMacolla,"Id="+macollaId);
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                this.setParam(oBD);
                super.accion=NADA;
                msgbox("Macolla Actualizada exitosamente");
            } else {
                msgbox("Macolla No Actualizada. Error");
                habilitarControles(false);            
            }
        }
        
        if (accion==ELIMINAR) {
            if (msgboxYesNo("Confirma la eliminacion de la Macolla y de toda la informacion relacionada ?")) {
                oBD.deleteMacolla(oMacolla.getId());
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   this.setParam(oBD);
                   super.accion=NADA;
                   oBD.setHuboCambios(true);
                   msgbox("Macolla eliminada exitosamente");            
                }
                else
                {
                    msgbox("Corrida No Eliminada. Error");
                    habilitarControles(false);       
                }
            }
        }
        oBD.setHuboCambios(true);
    }
    
    public void agregar() {
        oManejoDeCombos.setCombo(0,this.jComboBoxCliente);
        this.jTextFieldMacollaNombre.setText("");
        this.jTextFieldTotalNumberOfWells.setText("");
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
        this.jComboBoxCliente.setEnabled(accion);
        this.jTextFieldTotalNumberOfWells.setEnabled(accion);
        this.jTextFieldMacollaNombre.setEnabled(accion);  
    }
    
    private void accionClickLista() {
        ComboItem oCI=new ComboItem();
        try {
            if (accion!=NADA) return;
            if (modeloLista.isEmpty()) return;
            ClaveDesc ocd;
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
            ocd=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            oMacolla= (Macolla) oBD.select(Macolla.class, "id="+ocd.getClave())[0];
            macollaId=oMacolla.getId();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantMacollas.class.getName()).log(Level.SEVERE, null, ex);
        }
        Object []a=null;
        CampoCliente oCampoCliente=new CampoCliente();
        try {
            a=oBD.select(CampoCliente.class, "Id="+oMacolla.getCampoClienteId());
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantMacollas.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (a.length>0) {
           oCampoCliente=(CampoCliente) a[0];
        }
        this.jTextFieldMacollaNombre.setText(oMacolla.getNombre());
        this.jTextFieldTotalNumberOfWells.setText(oNumeros.valorString(oMacolla.getTotalNumberOfWells()));
        oManejoDeCombos.setCombo(oCampoCliente.getClienteId(),this.jComboBoxCliente);
        this.jTextFieldCampo.setText(oCampo.getNombre());
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jListExistentes = new javax.swing.JList();
        jComboBoxCliente = new javax.swing.JComboBox();
        jLabelLongitud7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldTotalNumberOfWells = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldMacollaNombre = new javax.swing.JTextField();
        jTextFieldCampo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        jLabelDescContenido.setText("Contenido:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(595, 332));
        setMinimumSize(new java.awt.Dimension(595, 332));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(595, 332));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 11, 500, 80));

        jComboBoxCliente.setEnabled(false);
        getContentPane().add(jComboBoxCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 230, -1));

        jLabelLongitud7.setText("Cantidad de Pozos:");
        getContentPane().add(jLabelLongitud7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 120, 20));

        jLabel4.setText("Nombre:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 160, -1, -1));

        jTextFieldTotalNumberOfWells.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldTotalNumberOfWells.setEnabled(false);
        jTextFieldTotalNumberOfWells.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldTotalNumberOfWellsKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldTotalNumberOfWells, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 180, 80, -1));

        jLabel5.setText("Cliente:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jTextFieldMacollaNombre.setEnabled(false);
        jTextFieldMacollaNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMacollaNombreActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldMacollaNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, 210, -1));

        jTextFieldCampo.setEnabled(false);
        getContentPane().add(jTextFieldCampo, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, 210, -1));

        jLabel6.setText("Campo:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged
    
    private void jTextFieldTotalNumberOfWellsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTotalNumberOfWellsKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldTotalNumberOfWellsKeyTyped

    private void jTextFieldMacollaNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMacollaNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldMacollaNombreActionPerformed

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
            java.util.logging.Logger.getLogger(MantMacollas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantMacollas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantMacollas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantMacollas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantMacollas dialog = new MantMacollas(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox jComboBoxCliente;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JLabel jLabelLongitud7;
    private javax.swing.JList jListExistentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldCampo;
    private javax.swing.JTextField jTextFieldMacollaNombre;
    private javax.swing.JTextField jTextFieldTotalNumberOfWells;
    // End of variables declaration//GEN-END:variables
}
