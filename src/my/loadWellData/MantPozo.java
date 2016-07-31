/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;
import java.awt.Cursor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;
import miLibreria.*;


/**
 *
 * @author Luis
 */
public class MantPozo extends MantPpal {

    /**
     * Creates new form MantPozo
     */
    public boolean EsPozo;
    public String sDescNodo;
    public ManejoBDI oBD;
    static DefaultListModel<String> modeloLista;
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    public Object[] ob_array = null;
    public Well oWell;
    public Macolla oMacolla;
    public CampoCliente oCampoCliente;
    public Taladro oT;
    public java.awt.Frame parent;
    public boolean huboEliminacion=false;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    private final long NOSINC=0,SINC=1,SINC_MOD=2;
    
    public MantPozo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        oBD=new ManejoBDAccess();
        super.jButtonProcesar.setVisible(false);
        super.jButtonCancelar.setVisible(false);
        this.jButtonMantReservoirOK.setVisible(false);
        this.jButtonMantReservoirCancel.setVisible(false);
        this.jTextFieldReservoirNombre.setVisible(false);
        
    }
      
    public void setParam(ManejoBDI o) {
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
        modeloLista = new DefaultListModel<>();
        this.jListExistentes.setModel(modeloLista);
        try {
            String s;
            oBD=o;
            
            ob_array=oBD.select(Macolla.class, "nombre='"+sDescNodo+"'");
            
            if (ob_array.length>0){
                try {
                    if (ob_array.length>0) {
                        oMacolla=(Macolla) ob_array[0];
                        this.jLabelDescContenido.setText("Macolla:" + oMacolla.getNombre());
                        ob_array=oBD.select(Well.class, "macollaId="+oMacolla.getId());
                        if (ob_array.length>0) {
                            for (int i=0;i<=ob_array.length-1;i++){
                                oWell=(Well) ob_array[i];
                                modeloLista.addElement(oWell.getNombre());
                            }
                        }
                        ob_array=oBD.select(CampoCliente.class, "Id="+oMacolla.getCampoClienteId());
                        oCampoCliente=(CampoCliente) ob_array[0];
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(MantPozo.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantPozo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,Taladro.class,this.jComboBoxTaladro);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,Reservoir.class,this.jComboBoxTargetReservoir);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,WellType.class,this.jComboBoxWellType);
    }    
  
    public void procesar() {
        String s="";
        Double lat=0.0;
        Double lon=0.0;
        long taladroId=0;
        long reservoirId=0;
        long wellTypeId=0;
        String nombre="";
        String leccionesAprendidas="";
        String jobNumber="";
        long status=0;
        
        
        ingresarNuevosItemsEnTablas();
        
        if (this.jTextFieldJobNumber.getText().trim().isEmpty()) {
            s+="** Falta el JobNumber **\n\r";
        }
        else
        {
            jobNumber=this.jTextFieldJobNumber.getText() ;            
        }
        
        if (this.jTextFieldNombre.getText().trim().isEmpty() ) {
            s+="** Falta el nombre **\n\r";
        }
        else
        {
            nombre=this.jTextFieldNombre.getText() ;            
        }
        
        try {
            if (this.jTextFieldLatitud.getText().trim().isEmpty() ){
                s+="** Falta Latitud **\n\r";            
            }
            else {

                lat=oNumeros.valorDouble(this.jTextFieldLatitud.getText()) ;
            }
        }
        catch (NumberFormatException ex){}
        
        try {
            if (this.jTextFieldLongitud.getText().trim().isEmpty() ){
                s+="** Falta Longitud **\n\r";            
            }
            else {
                lon=oNumeros.valorDouble(this.jTextFieldLongitud.getText()) ;
            }
        }
        catch (NumberFormatException ex){}


        if (this.jComboBoxTaladro.getSelectedIndex()==0 ) {
            s+="** Falta Taladro **\n\r";
        }
        else {
            taladroId=oManejoDeCombos.getComboID(this.jComboBoxTaladro);
        }

        if (this.jComboBoxTargetReservoir.getSelectedIndex()==0 ) {
            s+="** Falta Target Reservoir **\n\r";
        }
        else {
            reservoirId=oManejoDeCombos.getComboID(this.jComboBoxTargetReservoir);
        }

        if (this.jComboBoxWellType.getSelectedIndex()==0 ) {
            s+="** Falta WellType **\n\r";
        }
        else {
            wellTypeId=oManejoDeCombos.getComboID(this.jComboBoxWellType);
        }

        if (this.jTextPaneLeccionesAprendidas.getText().trim().isEmpty() ) {
            s+="** Faltan las Lecciones Aprendidas **\n\r";
        }
        else
        {
            leccionesAprendidas=this.jTextPaneLeccionesAprendidas.getText() ; 
        }

         
        if (s.length()>0) {
            msgbox(s);
            return;
        }
        
        if (accion==AGREGAR) {
            status=SINC;
            oWell=new Well();
            oWell.setNombre(nombre);
            oWell.setJobNumber(jobNumber);
            oWell.setLocationLat(lat);
            oWell.setLocationLong(lon);
            oWell.setMacollaId(oMacolla.getId());
            oWell.setTaladroId(taladroId);
            oWell.setReservoirId(reservoirId);
            oWell.setLeccionesAprendidas(leccionesAprendidas);
            oWell.setWellTypeId(wellTypeId);
            oWell.setStatus(status);
            oWell.setClienteId(oCampoCliente.getClienteId());
            oBD.insert(oWell);  
         
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                msgbox("Pozo Incluido exitosamente");
            } else {
                msgbox("Pozo No Incluido. Error");
                habilitarControles(false);            
            }
        }
        if (accion==MODIFICAR) {
            oWell.setNombre(nombre);
            oWell.setJobNumber(jobNumber);
            oWell.setLocationLat(lat);
            oWell.setLocationLong(lon);
            ///oWell.setMacollaId(oMacolla.getId());
            oWell.setTaladroId(taladroId);
            oWell.setReservoirId(reservoirId);
            oWell.setWellTypeId(wellTypeId);
            oWell.setLeccionesAprendidas(leccionesAprendidas);
            if (msgboxYesNo("Confirma los cambios ?")) {
                oWell.setRequiereUpdate(false);
                oBD.update(oWell, "Id='"+oWell.getId()+"'");
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   msgbox("Pozo modificado exitosamente"); 
                }
                else
                {
                    msgbox("Pozo No Modoficado. Error");
                    habilitarControles(false);       
                }
            }             
        }
        if (accion==ELIMINAR) {
            if (msgboxYesNo("Confirma la eliminacion del pozo y de todos sus componentes derivados ?")) {
                cursorEspera();
                oBD.deletePozo(oWell.getId());
                cursorNormal();
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                if (oBD.getRegistrsoAfectados()>0){
                   msgbox("Pozo eliminado exitosamente");
                   oBD.setHuboCambios(true);
                   huboEliminacion=true;
                }
                else
                {
                    msgbox("Pozo No Eliminado. Error");
                }
            }
        }
        oBD.setHuboCambios(true);

    }
    
    private void ingresarNuevosItemsEnTablas() {
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxTargetReservoir, this.jComboBoxTargetReservoir.getSelectedIndex())) {
            Reservoir o=new Reservoir();
            ComboItem oCI=(ComboItem) this.jComboBoxTargetReservoir.getSelectedItem();
            o.setNombre(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxTargetReservoir);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxTargetReservoir);
        }        
    }
    
    public void agregar() {
        this.jTextFieldNombre.setText("");
        this.jTextFieldJobNumber.setText("");
        this.jTextFieldLatitud.setText("");
        this.jTextFieldLongitud.setText("");
        this.jComboBoxTaladro.setSelectedIndex(0);
        this.jComboBoxTargetReservoir.setSelectedIndex(0);
        this.jComboBoxWellType.setSelectedIndex(0);        
        habilitarControles(true);

    }
    
    public void modificar() {
        habilitarControles(true);
    }
    
    public void cancelar() {
        accion=NADA;
        setearControles();
        habilitarControles(false);
    }
    
    private void habilitarControles(boolean accion) {
        this.jTextFieldNombre.setEditable(accion);
        this.jTextFieldJobNumber.setEditable(accion);
        this.jTextFieldLatitud.setEditable(accion);
        this.jTextFieldLongitud.setEditable(accion);
        
        this.jTextFieldNombre.setEnabled(accion);
        this.jTextFieldJobNumber.setEnabled(accion);
        this.jTextFieldLatitud.setEnabled(accion);
        this.jTextFieldLongitud.setEnabled(accion);
        this.jComboBoxTaladro.setEnabled(accion);
        this.jComboBoxTargetReservoir.setEnabled(accion);
        this.jComboBoxWellType.setEnabled(accion);
        this.jButtonMantTaladro.setEnabled(accion);
        this.jButtonMantTargetReservoir.setEnabled(accion);
        
        this.jTextPaneLeccionesAprendidas.setEnabled(accion);
              
        this.jTextFieldNombre.repaint();
        this.jTextFieldJobNumber.repaint();
        this.jTextFieldLatitud.repaint();
        this.jTextFieldLongitud.repaint();
        this.jComboBoxTaladro.repaint();
        this.jComboBoxTargetReservoir.repaint();
    }
    
    public void cursorNormal(){
        this.setCursor(Cursor.getDefaultCursor());
    }
    public void cursorEspera(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
        jLabelNombre = new javax.swing.JLabel();
        jTextFieldNombre = new javax.swing.JTextField();
        jLabelLatitud = new javax.swing.JLabel();
        jLabelLongitud = new javax.swing.JLabel();
        jLabelLongitud1 = new javax.swing.JLabel();
        jComboBoxTargetReservoir = new javax.swing.JComboBox();
        jButtonMantTargetReservoir = new javax.swing.JButton();
        jLabelLongitud2 = new javax.swing.JLabel();
        jComboBoxTaladro = new javax.swing.JComboBox();
        jButtonMantTaladro = new javax.swing.JButton();
        jButtonMantReservoirOK = new javax.swing.JButton();
        jButtonMantReservoirCancel = new javax.swing.JButton();
        jTextFieldReservoirNombre = new javax.swing.JTextField();
        jLabelLongitud3 = new javax.swing.JLabel();
        jTextFieldLongitud = new javax.swing.JTextField();
        jTextFieldLatitud = new javax.swing.JTextField();
        jLabelLongitud4 = new javax.swing.JLabel();
        jComboBoxWellType = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPaneLeccionesAprendidas = new javax.swing.JTextPane();
        jTextFieldJobNumber = new javax.swing.JTextField();
        jLabelLongitud5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelDescContenido.setText("Contenido:");
        getContentPane().add(jLabelDescContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 40));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 130, 310));

        jLabelNombre.setText("Nombre del Pozo:");
        getContentPane().add(jLabelNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, -1, -1));

        jTextFieldNombre.setEditable(false);
        jTextFieldNombre.setEnabled(false);
        getContentPane().add(jTextFieldNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, 180, -1));

        jLabelLatitud.setText("Latitud:");
        getContentPane().add(jLabelLatitud, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, 84, -1));

        jLabelLongitud.setText("Longitud:");
        getContentPane().add(jLabelLongitud, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 84, -1));

        jLabelLongitud1.setText("Lecciones Aprendidas:");
        getContentPane().add(jLabelLongitud1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 190, -1, -1));

        jComboBoxTargetReservoir.setEnabled(false);
        jComboBoxTargetReservoir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTargetReservoirActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxTargetReservoir, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 150, 174, -1));

        jButtonMantTargetReservoir.setText("...");
        jButtonMantTargetReservoir.setEnabled(false);
        jButtonMantTargetReservoir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantTargetReservoirActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantTargetReservoir, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 150, 23, -1));

        jLabelLongitud2.setText("Job number:");
        getContentPane().add(jLabelLongitud2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 70, -1, -1));

        jComboBoxTaladro.setEnabled(false);
        jComboBoxTaladro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTaladroActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxTaladro, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, 174, -1));

        jButtonMantTaladro.setText("...");
        jButtonMantTaladro.setEnabled(false);
        jButtonMantTaladro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantTaladroActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantTaladro, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 120, 23, -1));

        jButtonMantReservoirOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantReservoirOK.setMaximumSize(new java.awt.Dimension(45, 23));
        jButtonMantReservoirOK.setMinimumSize(new java.awt.Dimension(45, 23));
        jButtonMantReservoirOK.setPreferredSize(new java.awt.Dimension(45, 23));
        jButtonMantReservoirOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantReservoirOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantReservoirOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 150, 32, -1));

        jButtonMantReservoirCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantReservoirCancel.setMaximumSize(new java.awt.Dimension(45, 23));
        jButtonMantReservoirCancel.setMinimumSize(new java.awt.Dimension(45, 23));
        jButtonMantReservoirCancel.setPreferredSize(new java.awt.Dimension(45, 23));
        jButtonMantReservoirCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantReservoirCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantReservoirCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 150, 32, -1));
        getContentPane().add(jTextFieldReservoirNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 150, 180, -1));

        jLabelLongitud3.setText("Target Reservoir:");
        getContentPane().add(jLabelLongitud3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, -1, -1));

        jTextFieldLongitud.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldLongitud.setEnabled(false);
        jTextFieldLongitud.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldLongitudKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldLongitudKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldLongitud, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, 80, -1));

        jTextFieldLatitud.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldLatitud.setEnabled(false);
        jTextFieldLatitud.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldLatitudKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldLatitudKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldLatitud, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, 80, -1));

        jLabelLongitud4.setText("Taladro:");
        getContentPane().add(jLabelLongitud4, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, -1, -1));

        jComboBoxWellType.setEnabled(false);
        jComboBoxWellType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxWellTypeActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxWellType, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 174, -1));

        jTextPaneLeccionesAprendidas.setEnabled(false);
        jTextPaneLeccionesAprendidas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextPaneLeccionesAprendidasKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(jTextPaneLeccionesAprendidas);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 222, 400, 150));

        jTextFieldJobNumber.setEditable(false);
        jTextFieldJobNumber.setEnabled(false);
        getContentPane().add(jTextFieldJobNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 70, 100, -1));

        jLabelLongitud5.setText("Well Type:");
        getContentPane().add(jLabelLongitud5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged

        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void accionClickLista() {
        this.jTextFieldNombre.setText("");
        this.jTextFieldLatitud.setText("");        
        this.jTextFieldLongitud.setText("");
        this.jTextPaneLeccionesAprendidas.setText("");
        this.jTextFieldJobNumber.setText("");
        oManejoDeCombos.setCombo(0,this.jComboBoxTaladro);
        oManejoDeCombos.setCombo(0,this.jComboBoxTargetReservoir);
        oManejoDeCombos.setCombo(0,this.jComboBoxWellType);
        if (modeloLista.isEmpty()) return;
        ComboItem oCI;
        try {
            ob_array=oBD.select(Well.class, "nombre='"+modeloLista.getElementAt(this.jListExistentes.getSelectedIndex())+"'");
            oWell= (Well) ob_array[0];
            this.jTextFieldNombre.setText(oWell.getNombre());
            this.jTextFieldJobNumber.setText(oWell.getJobNumber());
            this.jTextFieldLatitud.setText(Double.toString(oWell.getLocationLat()));        
            this.jTextFieldLongitud.setText(Double.toString(oWell.getLocationLong()));
            this.jTextPaneLeccionesAprendidas.setText(oWell.getLeccionesAprendidas());
            oManejoDeCombos.setCombo(oWell.getTaladroId(),this.jComboBoxTaladro);
            oManejoDeCombos.setCombo(oWell.getReservoirId(),this.jComboBoxTargetReservoir);
            oManejoDeCombos.setCombo(oWell.getWellTypeId(),this.jComboBoxWellType);
            ob_array=oBD.select(Taladro.class, "Id='"+oWell.getTaladroId()+"'");
            if (ob_array.length>0)
                oT= (Taladro) ob_array[0]; 
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantPozo.class.getName()).log(Level.SEVERE, null, ex);
        }          
          catch (ArrayIndexOutOfBoundsException ex) {}
        if (accion!=NADA) {
            cancelar();
        }
    }
    
    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        // TODO add your handling code here:
        if (this.jListExistentes.isEnabled())
             accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jComboBoxTargetReservoirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTargetReservoirActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_jComboBoxTargetReservoirActionPerformed

    private void jButtonMantTargetReservoirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantTargetReservoirActionPerformed
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        super.jButtonAgregar.setEnabled(false);
        super.jButtonModificar.setEnabled(false);
        super.jButtonEliminar.setEnabled(false);
        this.jComboBoxTargetReservoir.setEnabled(false);
        this.jListExistentes.setEnabled(false);
        this.jButtonMantTaladro.setEnabled(false);
        this.jTextFieldReservoirNombre.setVisible(true);
        this.jButtonMantReservoirOK.setVisible(true);
        this.jButtonMantReservoirCancel.setVisible(true);
    }//GEN-LAST:event_jButtonMantTargetReservoirActionPerformed

    private void jComboBoxTaladroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTaladroActionPerformed
        Object item = this.jComboBoxTaladro.getSelectedItem();
        String value = ((ComboItem)item).getValue();
        String nombre =((ComboItem)item).toString();   
    }//GEN-LAST:event_jComboBoxTaladroActionPerformed

    private void jButtonMantTaladroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantTaladroActionPerformed
        MantTaladro j = new MantTaladro(parent,true);
        j.setLocationRelativeTo(this);
        j.setParam(oBD);
        j.setVisible(true);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,Taladro.class,this.jComboBoxTaladro,"");
        oManejoDeCombos.setCombo(oWell.getTaladroId(), jComboBoxTaladro);
    }//GEN-LAST:event_jButtonMantTaladroActionPerformed

    private void jButtonMantReservoirOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantReservoirOKActionPerformed
        oManejoDeCombos.ingresarAlComboBox(this.jTextFieldReservoirNombre,this.jComboBoxTargetReservoir);
        this.jTextFieldReservoirNombre.setVisible(false);
        this.jButtonMantReservoirOK.setVisible(false);
        this.jButtonMantReservoirCancel.setVisible(false);        
        habilitarControles(true);
        this.jListExistentes.setEnabled(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        setearControles();
    }//GEN-LAST:event_jButtonMantReservoirOKActionPerformed

    private void jButtonMantReservoirCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantReservoirCancelActionPerformed
        this.jTextFieldReservoirNombre.setVisible(false);
        this.jButtonMantReservoirOK.setVisible(false);
        this.jButtonMantReservoirCancel.setVisible(false);        
        habilitarControles(true);
        this.jListExistentes.setEnabled(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        setearControles();        
    }//GEN-LAST:event_jButtonMantReservoirCancelActionPerformed

    private void jTextFieldLongitudKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldLongitudKeyPressed
 
    }//GEN-LAST:event_jTextFieldLongitudKeyPressed

    private void jTextFieldLongitudKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldLongitudKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldLongitudKeyTyped

    private void jTextFieldLatitudKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldLatitudKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLatitudKeyPressed

    private void jTextFieldLatitudKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldLatitudKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldLatitudKeyTyped

    private void jComboBoxWellTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxWellTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxWellTypeActionPerformed

    private void jTextPaneLeccionesAprendidasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextPaneLeccionesAprendidasKeyTyped
        char inputChar=evt.getKeyChar();
        int n=inputChar;
        if (n==39 || n==34) {
            evt.setKeyChar('\b'); 
        }
    }//GEN-LAST:event_jTextPaneLeccionesAprendidasKeyTyped

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
            java.util.logging.Logger.getLogger(MantPozo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantPozo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantPozo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantPozo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantPozo dialog = new MantPozo(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButtonMantReservoirCancel;
    private javax.swing.JButton jButtonMantReservoirOK;
    private javax.swing.JButton jButtonMantTaladro;
    private javax.swing.JButton jButtonMantTargetReservoir;
    private javax.swing.JComboBox jComboBoxTaladro;
    private javax.swing.JComboBox jComboBoxTargetReservoir;
    private javax.swing.JComboBox jComboBoxWellType;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JLabel jLabelLatitud;
    private javax.swing.JLabel jLabelLongitud;
    private javax.swing.JLabel jLabelLongitud1;
    private javax.swing.JLabel jLabelLongitud2;
    private javax.swing.JLabel jLabelLongitud3;
    private javax.swing.JLabel jLabelLongitud4;
    private javax.swing.JLabel jLabelLongitud5;
    private javax.swing.JLabel jLabelNombre;
    private javax.swing.JList jListExistentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldJobNumber;
    private javax.swing.JTextField jTextFieldLatitud;
    private javax.swing.JTextField jTextFieldLongitud;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldReservoirNombre;
    private javax.swing.JTextPane jTextPaneLeccionesAprendidas;
    // End of variables declaration//GEN-END:variables
}
