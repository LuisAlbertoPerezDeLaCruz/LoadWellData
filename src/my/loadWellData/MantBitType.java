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
public class MantBitType extends MantPpal {
    public String sDescNodo;
    public ManejoBDI oBD;
    static DefaultListModel<String> modeloLista;   
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    private boolean firstTime=true;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    public Object[] ob_array = null;
    public BITType oBT=new BITType();
            
    
    /**
     * Creates new form MantCorridas
     */
    public MantBitType(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        oBD=new ManejoBDAccess();
        super.jButtonProcesar.setVisible(false);
        super.jButtonCancelar.setVisible(false);
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
        this.jRadioButtonDummyConeType.setVisible(false);
        this.jRadioButtonDummyConeType.setSelected(true);
        this.jRadioButtonDummyBitType.setVisible(false);
        this.jRadioButtonDummyBitType.setSelected(true);
    }
    
    public void setParam(ManejoBDI o) {
        pantallaVacia();
        oBD=o;   
        ResultSet rs;
        modeloLista = new DefaultListModel<>();
        this.jListExistentes.setModel(modeloLista);
        String s;
        s="SELECT nombre FROM BITType";
        rs=oBD.select(s);
        try {
            while (rs.next()) {
                modeloLista.addElement(rs.getNString("nombre"));
            }
        } catch (SQLException | NullPointerException ex) {
            Logger.getLogger(MantTaladro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void procesar() {
        String s="";
	String nombre=null;
        String iadc=null;
        String model=null;
        boolean esPDC=false;
        long cantBlades=-1;
        long cuttersSize=-1;
        double gauge=-1;
        boolean esRollerCone=false;
        boolean esMilledTooth=false;
        boolean esTCI=false;
        
        if ( this.jTextFieldNombre.getText().isEmpty() ) {
            s+="** Falta Nombre **\n\r";
        }
        else {
            nombre=this.jTextFieldNombre.getText();
        }
        if ( this.jTextFieldModelo.getText().isEmpty() ) {
            s+="** Falta Model **\n\r";
        }
        else {
            model=this.jTextFieldModelo.getText();
        }
        if ( this.jTextFieldIADC.getText().isEmpty() ) {
            s+="** Falta IADC **\n\r";
        }
        else {
            iadc=this.jTextFieldIADC.getText();
        }
        if (this.jRadioButtonPDC.isSelected()==false && this.jRadioButtonRollerCone.isSelected()==false) {
            s+="** Falta Bit Type **\n\r";
        }
        else {
            esPDC=this.jRadioButtonPDC.isSelected();
            esRollerCone=this.jRadioButtonRollerCone.isSelected();
        }
        if (esPDC) {
            if (this.jTextFieldNumberOfBlades.getText().isEmpty()) {
               s+="** Falta Number of Blades **\n\r"; 
            }
            else {
               cantBlades=new Long(this.jTextFieldNumberOfBlades.getText());   
            } 
            if (this.jTextFieldCuttersSize.getText().isEmpty()) {
               s+="** Falta Cutters Size **\n\r"; 
            }
            else {
               cuttersSize=new Long(this.jTextFieldCuttersSize.getText());   
            }
            if (this.jTextFieldGauge.getText().trim().isEmpty()){
                s+="** Falta Gauge **\n\r";            
            }
            else {
                gauge=new Double(this.jTextFieldGauge.getText().replace(",", ".")) ;
            }
        }
        if (esRollerCone){
           if (this.jRadioButtonDummyConeType.isSelected()) {
               s+="** Falta Cone Type **\n\r";
           } else {
               esMilledTooth=this.jRadioButtonMilledTooth.isSelected();
               esTCI=this.jRadioButtonTCI.isSelected();
           }
        }
                
        if (s.length()>0) {
            msgbox(s);
            return;
        }

        oBT.setNombre(nombre);
        oBT.setModel(model);
        oBT.setIadc(iadc);
        oBT.setEsPDC(esPDC);
        oBT.setEsRollerCone(esRollerCone);
        oBT.setCantBlades(cantBlades);
        oBT.setCuttersSize(cuttersSize);
        oBT.setGauge(gauge);
        oBT.setEsTCI(esTCI);
        oBT.setEsMilledTooth(esMilledTooth);
        
        if (accion==AGREGAR) { 
            oBD.insert(oBT);
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                msgbox("BitType Incluido exitosamente");
            } else {
                msgbox("BitType No Incluido. Error");
                habilitarControles(false);            
            }
        }
        if (accion==MODIFICAR) {
            if (msgboxYesNo("Confirma los cambios ?")) {
                oBD.update(oBT,"Id="+oBT.getId());
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   msgbox("BitType modificado exitosamente"); 
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
            rs=oBD.select("SELECT * FROM BHA WHERE BitTypeId="+oBT.getId());
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
                    oBD.delete(oBT, "Id="+oBT.getId());
                    if (oBD.getRegistrsoAfectados()>0){
                       habilitarControles(false); 
                       super.jButtonAgregar.setEnabled(true);
                       super.jButtonModificar.setEnabled(false);
                       super.jButtonEliminar.setEnabled(false);
                       super.jButtonProcesar.setVisible(false);
                       super.jButtonCancelar.setVisible(false);
                       super.accion=NADA;
                       this.setParam(oBD);
                       msgbox("BitType eliminado exitosamente");            
                    }
                    else
                    {
                        msgbox("BitType No Eliminado. Error");
                        habilitarControles(false);       
                    }
                }
                return;
            } catch (NullPointerException | SQLException ex) {
                Logger.getLogger(MantBitType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void agregar() {
        this.pantallaVacia();
        habilitarControles(true);
    }
    
    public void modificar() {
        habilitarControles(true);
        if (this.jRadioButtonPDC.isSelected()) pdcAction();
        if (this.jRadioButtonRollerCone.isSelected()) rollerConeAction();
    }
    
    public void cancelar() {
        accion=NADA;
        setearControles();
        accionClickLista();
        habilitarControles(false);
    }
    
    private void habilitarControles(boolean accion) {
        this.jTextFieldNombre.setEnabled(accion);
        this.jTextFieldModelo.setEnabled(accion);
        this.jTextFieldIADC.setEnabled(accion);
        this.jRadioButtonPDC.setEnabled(accion);
        this.jRadioButtonRollerCone.setEnabled(accion);
        this.jTextFieldNumberOfBlades.setEnabled(accion);
        this.jTextFieldGauge.setEnabled(accion);
        this.jTextFieldCuttersSize.setEnabled(accion);
        this.jRadioButtonMilledTooth.setEnabled(accion);
        this.jRadioButtonTCI.setEnabled(accion);
        this.jTextFieldNombre.requestFocusInWindow();
    }
    
    private void accionClickLista() {
        String s="";
        this.jRadioButtonDummyConeType.setSelected(true);
        this.jRadioButtonDummyBitType.setSelected(true);
        if (modeloLista.isEmpty()) return;
        try {
            s=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            ob_array=oBD.select(BITType.class, "nombre='"+s+"'");
            oBT= (BITType) ob_array[0];
            this.jTextFieldNombre.setText(oBT.getNombre());
            this.jTextFieldModelo.setText(oBT.getModel());
            this.jTextFieldIADC.setText(oBT.getIadc());
            this.jRadioButtonPDC.setSelected(oBT.getEsPDC());
            this.jRadioButtonRollerCone.setSelected(oBT.getEsRollerCone());
            this.jRadioButtonMilledTooth.setSelected(oBT.getEsMilledTooth());
            this.jRadioButtonTCI.setSelected(oBT.getEsTCI());
            this.jTextFieldCuttersSize.setText((oBT.getCuttersSize()==-1)?"":""+oBT.getCuttersSize());
            this.jTextFieldGauge.setText((oBT.getGauge()==-1)?"":""+oBT.getGauge());
            this.jTextFieldNumberOfBlades.setText((oBT.getCantBlades()==-1)?"":""+oBT.getCantBlades());
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantBitType.class.getName()).log(Level.SEVERE, null, ex);
        }
          catch (ArrayIndexOutOfBoundsException ex) {} 
        if (accion!=NADA) {
            cancelar();
        }       
    } 
    
    private void pantallaVacia() {
         oBT=new BITType();
         this.jTextFieldNombre.setText(oBT.getNombre());
         this.jTextFieldModelo.setText(oBT.getModel());
         this.jTextFieldIADC.setText(oBT.getIadc());
         this.jRadioButtonDummyBitType.setSelected(true);
         this.jRadioButtonDummyConeType.setSelected(true);
         this.jTextFieldCuttersSize.setText((oBT.getCuttersSize()==-1)?"":""+oBT.getCuttersSize());
         this.jTextFieldGauge.setText((oBT.getGauge()==-1)?"":""+oBT.getGauge());
         this.jTextFieldNumberOfBlades.setText((oBT.getCantBlades()==-1)?"":""+oBT.getCantBlades());
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
        buttonGroupBitType = new javax.swing.ButtonGroup();
        buttonGroupConeType = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListExistentes = new javax.swing.JList();
        jTextFieldNombre = new javax.swing.JTextField();
        jTextFieldIADC = new javax.swing.JTextField();
        jTextFieldModelo = new javax.swing.JTextField();
        jRadioButtonPDC = new javax.swing.JRadioButton();
        jRadioButtonRollerCone = new javax.swing.JRadioButton();
        jRadioButtonMilledTooth = new javax.swing.JRadioButton();
        jRadioButtonTCI = new javax.swing.JRadioButton();
        jTextFieldGauge = new javax.swing.JTextField();
        jTextFieldNumberOfBlades = new javax.swing.JTextField();
        jTextFieldCuttersSize = new javax.swing.JTextField();
        jRadioButtonDummyConeType = new javax.swing.JRadioButton();
        jRadioButtonDummyBitType = new javax.swing.JRadioButton();

        jLabelDescContenido.setText("Contenido:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 320));
        setModal(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setText("Modelo:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, -1, -1));

        jLabel6.setText("Nombre:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, -1, -1));

        jLabel7.setText("IADC:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, -1, -1));

        jLabel8.setText("Bit Type:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, -1, -1));

        jLabel9.setText("Cone Type:");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, -1, -1));

        jLabel10.setText("Number of Blades:");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, -1, -1));

        jLabel11.setText("Cutters Size:");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, -1, -1));

        jLabel12.setText("Gauge:");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 240, -1, -1));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 31, 140, 230));

        jTextFieldNombre.setEnabled(false);
        jTextFieldNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNombreActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 160, -1));

        jTextFieldIADC.setEnabled(false);
        jTextFieldIADC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIADCActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldIADC, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, 160, -1));

        jTextFieldModelo.setEnabled(false);
        jTextFieldModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldModeloActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldModelo, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 160, -1));

        buttonGroupBitType.add(jRadioButtonPDC);
        jRadioButtonPDC.setText("PDC");
        jRadioButtonPDC.setEnabled(false);
        jRadioButtonPDC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPDCActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonPDC, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, -1, -1));

        buttonGroupBitType.add(jRadioButtonRollerCone);
        jRadioButtonRollerCone.setText("Roller Cone");
        jRadioButtonRollerCone.setEnabled(false);
        jRadioButtonRollerCone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonRollerConeActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonRollerCone, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, -1, -1));

        buttonGroupConeType.add(jRadioButtonMilledTooth);
        jRadioButtonMilledTooth.setText("Milled Tooth");
        jRadioButtonMilledTooth.setEnabled(false);
        getContentPane().add(jRadioButtonMilledTooth, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 150, -1, -1));

        buttonGroupConeType.add(jRadioButtonTCI);
        jRadioButtonTCI.setText("TCI");
        jRadioButtonTCI.setEnabled(false);
        getContentPane().add(jRadioButtonTCI, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 150, -1, -1));

        jTextFieldGauge.setEnabled(false);
        jTextFieldGauge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGaugeActionPerformed(evt);
            }
        });
        jTextFieldGauge.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldGaugeKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldGauge, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, 100, -1));

        jTextFieldNumberOfBlades.setEnabled(false);
        jTextFieldNumberOfBlades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNumberOfBladesActionPerformed(evt);
            }
        });
        jTextFieldNumberOfBlades.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNumberOfBladesKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldNumberOfBlades, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 180, 100, -1));

        jTextFieldCuttersSize.setEnabled(false);
        jTextFieldCuttersSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCuttersSizeActionPerformed(evt);
            }
        });
        jTextFieldCuttersSize.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCuttersSizeKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldCuttersSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 210, 100, -1));

        buttonGroupConeType.add(jRadioButtonDummyConeType);
        jRadioButtonDummyConeType.setText("DummyConeType");
        jRadioButtonDummyConeType.setEnabled(false);
        getContentPane().add(jRadioButtonDummyConeType, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 150, -1, -1));

        buttonGroupBitType.add(jRadioButtonDummyBitType);
        jRadioButtonDummyBitType.setText("DummyBitType");
        jRadioButtonDummyBitType.setEnabled(false);
        jRadioButtonDummyBitType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDummyBitTypeActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButtonDummyBitType, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 120, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void jTextFieldNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNombreActionPerformed

    private void jTextFieldIADCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIADCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldIADCActionPerformed

    private void jTextFieldModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldModeloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldModeloActionPerformed

    private void jTextFieldGaugeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldGaugeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldGaugeActionPerformed

    private void jTextFieldNumberOfBladesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNumberOfBladesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNumberOfBladesActionPerformed

    private void jTextFieldCuttersSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCuttersSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCuttersSizeActionPerformed

    private void jRadioButtonRollerConeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonRollerConeActionPerformed
          rollerConeAction(); 
    }//GEN-LAST:event_jRadioButtonRollerConeActionPerformed

    private void rollerConeAction() {
        if (this.jRadioButtonRollerCone.isSelected()) {
            this.jRadioButtonMilledTooth.setEnabled(true);
            this.jRadioButtonTCI.setEnabled(true); 
            this.jTextFieldNumberOfBlades.setEnabled(false);
            this.jTextFieldCuttersSize.setEnabled(false);
            this.jTextFieldGauge.setEnabled(false);
            this.jTextFieldNumberOfBlades.setText("");
            this.jTextFieldCuttersSize.setText("");
            this.jTextFieldGauge.setText("");
        } else {
            this.jRadioButtonMilledTooth.setEnabled(false);
            this.jRadioButtonTCI.setEnabled(false);
            this.jRadioButtonDummyConeType.setSelected(true);
            this.jTextFieldNumberOfBlades.setEnabled(true);
            this.jTextFieldCuttersSize.setEnabled(true);
            this.jTextFieldGauge.setEnabled(true);
        } 
    }
    
    private void jRadioButtonPDCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPDCActionPerformed
       pdcAction();     
    }//GEN-LAST:event_jRadioButtonPDCActionPerformed

    private void pdcAction() {
        if (this.jRadioButtonPDC.isSelected()) {
            this.jRadioButtonMilledTooth.setEnabled(false);
            this.jRadioButtonTCI.setEnabled(false); 
            this.jRadioButtonDummyConeType.setSelected(true);
            this.jTextFieldNumberOfBlades.setEnabled(true);
            this.jTextFieldCuttersSize.setEnabled(true);
            this.jTextFieldGauge.setEnabled(true);
        } else {
            this.jTextFieldNumberOfBlades.setEnabled(false);
            this.jTextFieldCuttersSize.setEnabled(false);
            this.jTextFieldGauge.setEnabled(false);
            this.jTextFieldNumberOfBlades.setText("");
            this.jTextFieldCuttersSize.setText("");
            this.jTextFieldGauge.setText("");
            this.jRadioButtonMilledTooth.setEnabled(true);
            this.jRadioButtonTCI.setEnabled(true); 
            this.jRadioButtonDummyConeType.setSelected(true);
        }
    }
    
    private void jTextFieldGaugeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldGaugeKeyTyped
        soloDobles(evt);
    }//GEN-LAST:event_jTextFieldGaugeKeyTyped

    private void jTextFieldNumberOfBladesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNumberOfBladesKeyTyped
        soloEnteros(evt);
    }//GEN-LAST:event_jTextFieldNumberOfBladesKeyTyped

    private void jTextFieldCuttersSizeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCuttersSizeKeyTyped
        soloEnteros(evt);
    }//GEN-LAST:event_jTextFieldCuttersSizeKeyTyped

    private void jRadioButtonDummyBitTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDummyBitTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonDummyBitTypeActionPerformed
    
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
            java.util.logging.Logger.getLogger(MantBitType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantBitType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantBitType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantBitType.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                MantBitType dialog = new MantBitType(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroupBitType;
    private javax.swing.ButtonGroup buttonGroupConeType;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JList jListExistentes;
    private javax.swing.JRadioButton jRadioButtonDummyBitType;
    private javax.swing.JRadioButton jRadioButtonDummyConeType;
    private javax.swing.JRadioButton jRadioButtonMilledTooth;
    private javax.swing.JRadioButton jRadioButtonPDC;
    private javax.swing.JRadioButton jRadioButtonRollerCone;
    private javax.swing.JRadioButton jRadioButtonTCI;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldCuttersSize;
    private javax.swing.JTextField jTextFieldGauge;
    private javax.swing.JTextField jTextFieldIADC;
    private javax.swing.JTextField jTextFieldModelo;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldNumberOfBlades;
    // End of variables declaration//GEN-END:variables
}
