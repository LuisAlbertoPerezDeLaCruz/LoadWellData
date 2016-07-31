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
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import miLibreria.*;
import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;


/**
 *
 * @author Luis
 */
public class MantTaladro extends MantPpal {

    /**
     * Creates new form MantPozo
     */
    public String sDescNodo;
    public ManejoBDI oBD;
    static DefaultListModel<String> modeloLista;
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    public Object[] ob_array = null;
    public Taladro oTaladro;
    public EmpresaResponsable oER;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    
    
    public MantTaladro(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        oBD=new ManejoBDAccess();
        super.jButtonProcesar.setVisible(false);
        super.jButtonCancelar.setVisible(false);
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
	this.jTextFieldEmpresaResponsable.setVisible(false);
        this.jButtonMantEmpresaResponsableOK.setVisible(false);
        this.jButtonMantEmpresaResponsableCancel.setVisible(false);
    }
    
    public void setParam(ManejoBDI o) {
        ResultSet rs;
        modeloLista = new DefaultListModel<>();
        modeloCombo=new DefaultComboBoxModel<>();
        this.jListExistentes.setModel(modeloLista);
        this.jComboBoxEmpresaResponsable.setModel(modeloCombo);
        String s;
        oBD=o;
        s="SELECT * FROM TaladrosDeEmpresas";
        rs=oBD.select(s);
        try {
            while (rs.next()) {
                modeloLista.addElement(rs.getNString("taladroNombre")+ ", " + rs.getNString("empresaNombre"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MantTaladro.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        try {
            ob_array=oBD.select(EmpresaResponsable.class, "1");
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantTaladro.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        modeloCombo.addElement(new ComboItem("Seleccione", Long.toString(0)));    
        if (ob_array.length>0){            
            for (int i=0;i<=ob_array.length-1;i++) {
               oER=(EmpresaResponsable) ob_array[i];
               modeloCombo.addElement(new ComboItem(oER.getNombre().trim(), Long.toString(oER.getId())));
            }
            this.jComboBoxEmpresaResponsable.setSelectedIndex(0);
        }
    }
    
    public void procesar() {
        String s="";
        long cap=0;
        long empresaResponsableId=0;
        boolean esHidraulico=this.jCheckBoxHidraulico.isSelected();
        boolean esKelly=this.jCheckBoxKelly.isSelected();
        boolean esModular=this.jCheckBoxModular.isSelected();
        boolean esTopDrive=this.jCheckBoxTopDrive.isSelected();  
        String nombre="";
        
        ingresarNuevosItemsEnTablas();
        
        if (this.jTextFieldNombre.getText().trim().isEmpty()) {
            s+="** Falta el nombre **\n\r";
        }
        else
        {
            nombre=this.jTextFieldNombre.getText() ;            
        }
        
        if (this.jTextCapacidad.getText().trim().isEmpty()){
            s+="** Falta Capacidad **\n\r";            
        }
        else {
            cap=oNumeros.valorLong(this.jTextCapacidad.getText()) ;
        }            
      
        if (this.jComboBoxEmpresaResponsable.getSelectedIndex()==0) {
            s+="** Falta Empresa Responsable **\n\r";
        }
        else {
            empresaResponsableId=oER.getId();
        }
         
        if (s.length()>0) {
            msgbox(s);
            return;
        }
        
        if (accion==AGREGAR) {
            oTaladro=new Taladro();
            oTaladro.setNombre(nombre);
            oTaladro.setCapacidad(cap);
            oTaladro.setEmpresaResponsableId(empresaResponsableId);
            oTaladro.setKelly(esKelly);
            oTaladro.setModular(esModular);
            oTaladro.setHidraulico(esHidraulico);
            oTaladro.setTopDrive(esTopDrive);
            oBD.insert(oTaladro);  
         
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                msgbox("Taladro Incluido exitosamente");
            } else {
                msgbox("Taladro No Incluido. Error");
                habilitarControles(false);            
            }
        }
        if (accion==MODIFICAR) {
            oTaladro.setNombre(nombre);
            oTaladro.setCapacidad(cap);
            oTaladro.setEmpresaResponsableId(empresaResponsableId);
            oTaladro.setKelly(esKelly);
            oTaladro.setModular(esModular);
            oTaladro.setHidraulico(esHidraulico);
            oTaladro.setTopDrive(esTopDrive);
            if (msgboxYesNo("Confirma los cambios ?")) {
                oBD.update(oTaladro, "Id='"+oTaladro.getId()+"'");
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   msgbox("Taladro modificado exitosamente"); 
                }
                else
                {
                    msgbox("Taladro No Modoficado. Error");
                    habilitarControles(false);       
                }
            }             
        }
        if (accion==ELIMINAR) {
            if (msgboxYesNo("Confirma la eliminacion ?")) {
                oBD.delete(oTaladro, "Id='"+oTaladro.getId()+"'");
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   msgbox("Taladro eliminado exitosamente");            
                }
                else
                {
                    msgbox("Taladro No Eliminado. Error");
                    habilitarControles(false);       
                }
            }
        }
    }
    
    public void agregar() {
        this.jTextFieldNombre.setText("");
        this.jTextCapacidad.setText("");
        this.jComboBoxEmpresaResponsable.setSelectedIndex(0);
        this.jCheckBoxHidraulico.setSelected(false);
        this.jCheckBoxKelly.setSelected(false);
        this.jCheckBoxModular.setSelected(false);
        this.jCheckBoxTopDrive.setSelected(false);
        this.jButtonMantEmpresaResponsable.setEnabled(true);
        habilitarControles(true);
    }
    
    public void modificar() {
        habilitarControles(true); 
        this.jButtonMantEmpresaResponsable.setEnabled(true);
    }
    
    public void cancelar() {
        accion=NADA;
        setearControles();
        habilitarControles(false);
        this.jButtonMantEmpresaResponsable.setEnabled(false);
    }    
    
    private void habilitarControles(boolean accion) {
        this.jTextFieldNombre.setEditable(accion);
        this.jTextCapacidad.setEditable(accion);
        
        this.jTextFieldNombre.setEnabled(accion);
        this.jTextCapacidad.setEnabled(accion);
        this.jComboBoxEmpresaResponsable.setEnabled(accion);
        this.jCheckBoxHidraulico.setEnabled(accion);
        this.jCheckBoxKelly.setEnabled(accion);
        this.jCheckBoxModular.setEnabled(accion);
        this.jCheckBoxTopDrive.setEnabled(accion); 
        this.jButtonMantEmpresaResponsable.setEnabled(accion);
              
        this.jTextFieldNombre.repaint();
        this.jTextCapacidad.repaint();
        this.jComboBoxEmpresaResponsable.repaint();
        this.jCheckBoxHidraulico.repaint();
        this.jCheckBoxKelly.repaint();
        this.jCheckBoxModular.repaint();
        this.jCheckBoxTopDrive.repaint();        
    }
    
    private void ingresarNuevosItemsEnTablas() {
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxEmpresaResponsable, this.jComboBoxEmpresaResponsable.getSelectedIndex())) {
            EmpresaResponsable o=new EmpresaResponsable();
            ComboItem oCI=(ComboItem) this.jComboBoxEmpresaResponsable.getSelectedItem();
            o.setNombre(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxEmpresaResponsable);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxEmpresaResponsable);
        }
    }
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jListExistentes = new javax.swing.JList();
        jTextFieldNombre = new javax.swing.JTextField();
        jLabelNombre = new javax.swing.JLabel();
        jLabelLongitud1 = new javax.swing.JLabel();
        jComboBoxEmpresaResponsable = new javax.swing.JComboBox();
        jLabelLatitud = new javax.swing.JLabel();
        jCheckBoxModular = new javax.swing.JCheckBox();
        jCheckBoxKelly = new javax.swing.JCheckBox();
        jCheckBoxTopDrive = new javax.swing.JCheckBox();
        jTextCapacidad = new javax.swing.JTextField();
        jCheckBoxHidraulico = new javax.swing.JCheckBox();
        jButtonMantEmpresaResponsable = new javax.swing.JButton();
        jButtonMantEmpresaResponsableOK = new javax.swing.JButton();
        jTextFieldEmpresaResponsable = new javax.swing.JTextField();
        jButtonMantEmpresaResponsableCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(680, 332));
        setMinimumSize(new java.awt.Dimension(680, 332));
        setPreferredSize(new java.awt.Dimension(680, 332));
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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 560, 60));

        jTextFieldNombre.setEditable(false);
        jTextFieldNombre.setEnabled(false);
        getContentPane().add(jTextFieldNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 112, -1));

        jLabelNombre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelNombre.setText("Taladro:");
        getContentPane().add(jLabelNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 90, 50, -1));

        jLabelLongitud1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelLongitud1.setText("Empresa Responsable:");
        getContentPane().add(jLabelLongitud1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jComboBoxEmpresaResponsable.setEnabled(false);
        jComboBoxEmpresaResponsable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEmpresaResponsableActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxEmpresaResponsable, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, 170, -1));

        jLabelLatitud.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelLatitud.setText("Capacidad:");
        getContentPane().add(jLabelLatitud, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 60, -1));

        jCheckBoxModular.setText("Modular");
        jCheckBoxModular.setEnabled(false);
        jCheckBoxModular.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        getContentPane().add(jCheckBoxModular, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        jCheckBoxKelly.setText("Kelly");
        jCheckBoxKelly.setEnabled(false);
        jCheckBoxKelly.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBoxKelly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxKellyActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBoxKelly, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, 64, -1));

        jCheckBoxTopDrive.setText("Top Drive");
        jCheckBoxTopDrive.setEnabled(false);
        jCheckBoxTopDrive.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBoxTopDrive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTopDriveActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBoxTopDrive, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 180, 76, -1));

        jTextCapacidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextCapacidad.setEnabled(false);
        jTextCapacidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextCapacidadKeyTyped(evt);
            }
        });
        getContentPane().add(jTextCapacidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, 60, -1));

        jCheckBoxHidraulico.setText("Hidraulico");
        jCheckBoxHidraulico.setEnabled(false);
        jCheckBoxHidraulico.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBoxHidraulico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxHidraulicoActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBoxHidraulico, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 180, 82, -1));

        jButtonMantEmpresaResponsable.setText("...");
        jButtonMantEmpresaResponsable.setEnabled(false);
        jButtonMantEmpresaResponsable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantEmpresaResponsableActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantEmpresaResponsable, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, 32, -1));

        jButtonMantEmpresaResponsableOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantEmpresaResponsableOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantEmpresaResponsableOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantEmpresaResponsableOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, 32, -1));

        jTextFieldEmpresaResponsable.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldEmpresaResponsable.setEnabled(false);
        jTextFieldEmpresaResponsable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldEmpresaResponsableKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldEmpresaResponsable, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 120, 160, -1));

        jButtonMantEmpresaResponsableCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantEmpresaResponsableCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantEmpresaResponsableCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantEmpresaResponsableCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 120, 32, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void accionClickLista() {
         String s="",s1="",s2="";
        if (modeloLista.isEmpty()) return;
        ComboItem oCI;
        try {
            s=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            s1=s.substring(0,s.indexOf(','));
            s2=s.substring(s.indexOf(',')+1).trim();
            ob_array=oBD.select(Taladro.class, "nombre='"+s1+"'");
            oTaladro= (Taladro) ob_array[0];
            ob_array=oBD.select(Taladro.class, "Id='"+oTaladro.getId()+"'");
            oTaladro= (Taladro) ob_array[0];
            this.jTextFieldNombre.setText(oTaladro.getNombre());
            this.jTextCapacidad.setText(Long.toString(oTaladro.getCapacidad())); 
            this.jCheckBoxHidraulico.setSelected(oTaladro.getHidraulico());
            this.jCheckBoxKelly.setSelected(oTaladro.getKelly());
            this.jCheckBoxModular.setSelected(oTaladro.getModular());
            this.jCheckBoxTopDrive.setSelected(oTaladro.getTopDrive());
            for (int i=0;i<=this.jComboBoxEmpresaResponsable.getItemCount()-1;i++) {
                oCI=(ComboItem) this.jComboBoxEmpresaResponsable.getItemAt(i);
                if (oCI.toString().trim().equals(s2)){
                    this.jComboBoxEmpresaResponsable.setSelectedIndex(i); 
                    this.jComboBoxEmpresaResponsable.repaint();
                }
            }
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
            //this.jComboBoxTaladro.setSelectedIndex(1);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantTaladro.class.getName()).log(Level.SEVERE, null, ex);
        }
          catch (ArrayIndexOutOfBoundsException ex) {} 
        if (accion!=NADA) {
            cancelar();
        }       
    }
    
    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        // TODO add your handling code here:
        accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jComboBoxEmpresaResponsableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxEmpresaResponsableActionPerformed
        // TODO add your handling code here:
        Object item = this.jComboBoxEmpresaResponsable.getSelectedItem();
        String value = ((ComboItem)item).getValue();
        String nombre =((ComboItem)item).toString();
        oER=new EmpresaResponsable();
        oER.setNombre(nombre);
        oER.setId(new Long(value));
    }//GEN-LAST:event_jComboBoxEmpresaResponsableActionPerformed

    private void jCheckBoxKellyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxKellyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxKellyActionPerformed

    private void jCheckBoxTopDriveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTopDriveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxTopDriveActionPerformed

    private void jCheckBoxHidraulicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxHidraulicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxHidraulicoActionPerformed

    private void jTextCapacidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextCapacidadKeyTyped
        oNumeros.soloEnteros(evt);
    }//GEN-LAST:event_jTextCapacidadKeyTyped

    private void jButtonMantEmpresaResponsableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantEmpresaResponsableActionPerformed
        // TODO add your handling code here:
        habilitarControles(false);
        this.jButtonMantEmpresaResponsableOK.setVisible(true);
        this.jButtonMantEmpresaResponsableCancel.setVisible(true);
        this.jTextFieldEmpresaResponsable.setVisible(true);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantEmpresaResponsable.setEnabled(false);
        this.jButtonMantEmpresaResponsableOK.setEnabled(true);
        this.jButtonMantEmpresaResponsableCancel.setEnabled(true);
        this.jTextFieldEmpresaResponsable.setEnabled(true);
        this.jListExistentes.setEnabled(false);
    }//GEN-LAST:event_jButtonMantEmpresaResponsableActionPerformed

    private void jButtonMantEmpresaResponsableOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantEmpresaResponsableOKActionPerformed
        oManejoDeCombos.ingresarAlComboBox(this.jTextFieldEmpresaResponsable,this.jComboBoxEmpresaResponsable);
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantEmpresaResponsable.setEnabled(true);
        this.jTextFieldEmpresaResponsable.setEnabled(false);
        this.jTextFieldEmpresaResponsable.setVisible(false);
        this.jTextFieldEmpresaResponsable.setText("");
        this.jButtonMantEmpresaResponsableOK.setVisible(false);
        this.jButtonMantEmpresaResponsableCancel.setVisible(false);
        this.jTextFieldEmpresaResponsable.repaint();
        this.jListExistentes.setEnabled(true);
        this.pack();
    }//GEN-LAST:event_jButtonMantEmpresaResponsableOKActionPerformed

    private void jTextFieldEmpresaResponsableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldEmpresaResponsableKeyTyped
        
    }//GEN-LAST:event_jTextFieldEmpresaResponsableKeyTyped

    private void jButtonMantEmpresaResponsableCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantEmpresaResponsableCancelActionPerformed
        // TODO add your handling code here:
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantEmpresaResponsable.setEnabled(true);
        this.jTextFieldEmpresaResponsable.setEnabled(false);
        this.jTextFieldEmpresaResponsable.setVisible(false);
        this.jTextFieldEmpresaResponsable.setText("");
        this.jButtonMantEmpresaResponsableOK.setVisible(false);
        this.jButtonMantEmpresaResponsableCancel.setVisible(false);
        this.jTextFieldEmpresaResponsable.repaint();
        this.jListExistentes.setEnabled(true);
        this.pack();
    }//GEN-LAST:event_jButtonMantEmpresaResponsableCancelActionPerformed

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
            java.util.logging.Logger.getLogger(MantTaladro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantTaladro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantTaladro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantTaladro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantTaladro dialog = new MantTaladro(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButtonMantEmpresaResponsable;
    private javax.swing.JButton jButtonMantEmpresaResponsableCancel;
    private javax.swing.JButton jButtonMantEmpresaResponsableOK;
    private javax.swing.JCheckBox jCheckBoxHidraulico;
    private javax.swing.JCheckBox jCheckBoxKelly;
    private javax.swing.JCheckBox jCheckBoxModular;
    private javax.swing.JCheckBox jCheckBoxTopDrive;
    private javax.swing.JComboBox jComboBoxEmpresaResponsable;
    private javax.swing.JLabel jLabelLatitud;
    private javax.swing.JLabel jLabelLongitud1;
    private javax.swing.JLabel jLabelNombre;
    private javax.swing.JList jListExistentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextCapacidad;
    private javax.swing.JTextField jTextFieldEmpresaResponsable;
    private javax.swing.JTextField jTextFieldNombre;
    // End of variables declaration//GEN-END:variables
}
