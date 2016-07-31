/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import miLibreria.*;
import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;


/**
 *
 * @author Luis
 */
public class MantSecciones extends MantPpal {

    /**
     * Creates new form MantPozo
     */
    public boolean EsPozo;
    public String sDescNodo;
    public ManejoBDI oBD;
    static DefaultListModel<ClaveDesc> modeloLista;   
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    public Object[] ob_array = null;
    public Well oWell;
    public Sections oS;
    public java.awt.Frame parent;
    public long ultimoNumeroIdentificador;
    public JFormattedTextField jTextFieldEdicionCombo;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    
    public MantSecciones(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        oBD=new ManejoBDAccess();
        super.jButtonProcesar.setVisible(false);
        super.jButtonCancelar.setVisible(false);
        
	this.jTextFieldDiameters.setVisible(false);
        this.jButtonMantDiametersOK.setVisible(false);
        this.jButtonMantDiametersCancel.setVisible(false);
        
        this.jTextFieldSectionType.setVisible(false);
        this.jButtonMantSectionTypeOK.setVisible(false);
        this.jButtonMantSectionTypeCancel.setVisible(false);
               
        this.jTextFieldCasingOD.setVisible(false);
        this.jButtonMantCasingODOK.setVisible(false);
        this.jButtonMantCasingODCancel.setVisible(false);
        
        this.jTextFieldCasingGrade.setVisible(false);
        this.jButtonMantCasingGradeOK.setVisible(false);
        this.jButtonMantCasingGradeCancel.setVisible(false);
        
    }
      
    public void setParam(ManejoBDI o) {
        ResultSet rs;
        String s,s1;
        ClaveDesc ocd;
        
        modeloLista = new DefaultListModel<>();
        this.jListExistentes.setModel(modeloLista);
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
        try {
            oBD=o;
            
            ob_array=oBD.select(Well.class, "nombre='"+sDescNodo+"'");            

            try {
                if (ob_array.length>0) {
                    oWell=(Well) ob_array[0];
                    this.jLabelDescContenido.setText("Pozo:" + oWell.getNombre());
                    ob_array=oBD.select(Sections.class, "wellId="+oWell.getId());
                    s1="SELECT * from ConsultaSecciones1 WHERE wellId="+oWell.getId()+" ORDER BY numeroIdentificador;";
                    rs=oBD.select(s1);
                    while (rs.next()){
                        ultimoNumeroIdentificador=new Long(rs.getString("numeroIdentificador"));
                        s1=""+rs.getString("numeroIdentificador");
                        s1+=" - " + rs.getString("sectionTypeDescription");
                        s1+=" (" + rs.getLong("initialDepth");
                        s1+=" - " + rs.getLong("finalDepth");
                        s1+=").";
                        s1+=" Responsable: " + rs.getString("empresaResponsableNombre");                           
                        ocd=new ClaveDesc();
                        ocd.setClave(rs.getLong("Id"));
                        ocd.setDesc(s1);
                        modeloLista.addElement(ocd);                            
                    }                       
                }
            } catch (InstantiationException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(MantSecciones.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantSecciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,EmpresaResponsable.class,this.jComboBoxEmpresaResponsable);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,Diameters.class,this.jComboBoxDiameters);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,SectionType.class,this.jComboBoxSectionType);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,CasingsOD.class,this.jComboBoxCasingsOD);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,CasingGrade.class,this.jComboBoxCasingGrade);
        
    }
    
    public long getComboID (JComboBox j){
        ComboItem oCI;
        oCI= (ComboItem) j.getSelectedItem();
        return new Long(oCI.getValue());        
    }
          
    public void procesar() {
        String s="";
        Double lat=0.0;
        Double lon=0.0;
        long taladroId=0;
        String nombre="";
        
        long empresaResponsableId=0;
        long diameterId=0;
        long sectionTypeId=0;
        long casingODId=0;
        long casingGradeId=0;
        long nroCorridasCasingLiner=0;
        
        Double initialDepth=0.0;
        Double finalDepth=0.0; 
        Double casingInitialDepth=0.0;
        Double casingFinalDepth=0.0;
        Double casingNominalWeight=0.0;

        
        ingresarNuevosItemsEnTablas();
        
        if (this.jComboBoxEmpresaResponsable.getSelectedIndex()==0) {
            s+="** Falta Empresa Responsable **\n\r";
        }
        else {
            empresaResponsableId=getComboID(this.jComboBoxEmpresaResponsable);
        }
        if (this.jComboBoxDiameters.getSelectedIndex()==0) {
            s+="** Falta Diameter **\n\r";
        }
        else {
            diameterId=getComboID(this.jComboBoxDiameters);
        }
        if (this.jComboBoxSectionType.getSelectedIndex()==0) {
            s+="** Falta Section Type **\n\r";
        }
        else {
            sectionTypeId=getComboID(this.jComboBoxSectionType);
        }
        if (this.jComboBoxCasingsOD.getSelectedIndex()==0) {
            s+="** Falta Casing OD **\n\r";
        }
        else {
            casingODId=getComboID(this.jComboBoxCasingsOD);
        }
        if (this.jComboBoxCasingGrade.getSelectedIndex()==0) {
            s+="** Falta Casing Grade **\n\r";
        }
        else {
            casingGradeId=getComboID(this.jComboBoxCasingGrade);
        }
        
        if (this.jTextFieldInitialDepth.getText().trim().isEmpty()){
            s+="** Falta Initial Depth **\n\r";            
        }
        else {
            initialDepth=new Double(this.jTextFieldInitialDepth.getText().replace(",", ".")) ;
        }
        
        if (this.jTextFieldFinalDepth.getText().trim().isEmpty()){
            s+="** Falta Final Depth **\n\r";            
        }
        else {
            finalDepth=new Double(this.jTextFieldFinalDepth.getText().replace(",", ".")) ;
        }
        
        if (this.jTextFieldCasingInitialDepth.getText().trim().isEmpty()){
            s+="** Falta Casing Initial Depth **\n\r";            
        }
        else {
            casingInitialDepth=new Double(this.jTextFieldCasingInitialDepth.getText().replace(",", ".")) ;
        }
        
        if (this.jTextFieldCasingFinalDepth.getText().trim().isEmpty()){
            s+="** Falta Casing Final Depth **\n\r";            
        }
        else {
            casingFinalDepth=new Double(this.jTextFieldCasingFinalDepth.getText().replace(",", ".")) ;
        }
        
        if (this.jTextFieldNominalWeigth.getText().trim().isEmpty()){
            s+="** Falta Casing Nominal Weigth **\n\r";            
        }
        else {
            casingNominalWeight=new Double(this.jTextFieldNominalWeigth.getText().replace(",", ".")) ;
        }
        
        if (this.jTextFieldNroCorridasCasingLiner.getText().trim().isEmpty()){
            s+="** Falta nro de corridas Casing/Liner **\n\r";            
        }
        else {
            nroCorridasCasingLiner=new Long(this.jTextFieldNroCorridasCasingLiner.getText().replace(",", ".")) ;
        }
        
        if (this.dateTimePickerInicioBajadaRevestidor.getDate()==null) {
            s+="** Fecha/hora de inicio de bajada del revestidor invalida **";
        }
        
        if (this.dateTimePickerFinalBajadaRevestidor.getDate()==null) {
            s+="** Fecha/hora final de bajada del revestidor invalida **";
        }
        
        this.jTextFieldNroCorridasCasingLiner.setEnabled(false);
        
        if (s.length()>0) {
            msgbox(s);
            return;
        }

        if (accion==AGREGAR) {
            oS=new Sections();
            oS.setWellId(oWell.getId());
            oS.setNumeroIdentificador(ultimoNumeroIdentificador+1);
            oS.setCasingFinalDepth(casingFinalDepth);
            oS.setCasingInitialDepth(casingInitialDepth);
            oS.setCasingNominalWeight(casingNominalWeight);
            oS.setEmpresaResponsableId(empresaResponsableId);
            oS.setFinalDepth(finalDepth);
            oS.setInitialDepth(initialDepth);
            oS.setConfirmacionNivel1(0);
            oS.setConfirmacionNivel2(0);
            oS.setCasingODId(casingODId);
            oS.setCasingGradeId(casingGradeId);
            oS.setDiameterId(diameterId);
            oS.setSectionTypeId(sectionTypeId);
            oS.setFechaHoraInicioBajadaRevestidor(this.dateTimePickerInicioBajadaRevestidor.getDate());
            oS.setFechaHoraFinalBajadaRevestidor(this.dateTimePickerFinalBajadaRevestidor.getDate());
            oS.setNroCorridasCasingLiner(nroCorridasCasingLiner);
            oBD.insert(oS);
            if (oBD.getRegistrsoAfectados()>0){
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                super.accion=NADA;
                this.setParam(oBD);
                msgbox("Section Incluida exitosamente");
            } else {
                msgbox("Section No Incluida. Error");
                habilitarControles(false);            
            }
        }
        if (accion==MODIFICAR) {
            oS.setCasingFinalDepth(casingFinalDepth);
            oS.setCasingGradeId(casingGradeId);
            oS.setCasingInitialDepth(casingInitialDepth);
            oS.setCasingNominalWeight(casingNominalWeight);
            oS.setCasingODId(casingODId);
            oS.setDiameterId(diameterId);
            oS.setSectionTypeId(sectionTypeId);
            oS.setEmpresaResponsableId(empresaResponsableId);
            oS.setFinalDepth(finalDepth);
            oS.setInitialDepth(initialDepth);
            oS.setConfirmacionNivel1(0);
            oS.setConfirmacionNivel2(0);
            oS.setFechaHoraInicioBajadaRevestidor(this.dateTimePickerInicioBajadaRevestidor.getDate());
            oS.setFechaHoraFinalBajadaRevestidor(this.dateTimePickerFinalBajadaRevestidor.getDate());
            oS.setNroCorridasCasingLiner(nroCorridasCasingLiner);
            if (msgboxYesNo("Confirma los cambios ?")) {
                oS.setRequiereUpdate(false);
                oBD.update(oS, "Id='"+oS.getId()+"'");
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   msgbox("Section modificada exitosamente"); 
                }
                else
                {
                    msgbox("Section No Modificada. Error");
                    habilitarControles(false);       
                }
            }             
        }
        if (accion==ELIMINAR) {
            if (msgboxYesNo("Confirma la eliminacion de la seccion con toda la informacion relacionada ?")) {
                oBD.deleteSection(oS.getId());
                if (oBD.getRegistrsoAfectados()>0){
                   habilitarControles(false); 
                   super.jButtonAgregar.setEnabled(true);
                   super.jButtonModificar.setEnabled(false);
                   super.jButtonEliminar.setEnabled(false);
                   super.jButtonProcesar.setVisible(false);
                   super.jButtonCancelar.setVisible(false);
                   super.accion=NADA;
                   this.setParam(oBD);
                   oBD.setHuboCambios(true);
                   msgbox("Section eliminada exitosamente");            
                }
                else
                {
                    msgbox("Section No Eliminada. Error");
                    habilitarControles(false);       
                }
            }
        }
        oBD.setHuboCambios(true);
    }
    
    private void ingresarNuevosItemsEnTablas() {
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxDiameters, this.jComboBoxDiameters.getSelectedIndex())) {
            Diameters o=new Diameters();
            Double d;
            ComboItem oCI=(ComboItem) this.jComboBoxDiameters.getSelectedItem();
            d=new Double(oCI.getKey().replace(',','.'));
            o.setSize(d);
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxDiameters);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxDiameters);
        } 
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxSectionType, this.jComboBoxSectionType.getSelectedIndex())) {
            SectionType o=new SectionType();
            ComboItem oCI=(ComboItem) this.jComboBoxSectionType.getSelectedItem();
            o.setDescription(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxSectionType);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxSectionType);
        }
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxCasingsOD, this.jComboBoxCasingsOD.getSelectedIndex())) {
            CasingsOD o=new CasingsOD();
            Double d;
            ComboItem oCI=(ComboItem) this.jComboBoxCasingsOD.getSelectedItem();
            d=new Double(oCI.getKey().replace(',','.'));
            o.setOd(d);
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxCasingsOD);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxCasingsOD);
        } 
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxCasingGrade, this.jComboBoxCasingGrade.getSelectedIndex())) {
            CasingGrade o=new CasingGrade();
            ComboItem oCI=(ComboItem) this.jComboBoxCasingGrade.getSelectedItem();
            o.setDescription(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxCasingGrade);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxCasingGrade);
        }
    }
    
    public void agregar() {
        oManejoDeCombos.setCombo(0,this.jComboBoxEmpresaResponsable);
        oManejoDeCombos.setCombo(0,this.jComboBoxSectionType);
        oManejoDeCombos.setCombo(0,this.jComboBoxDiameters);
        oManejoDeCombos.setCombo(0,this.jComboBoxCasingsOD);
        oManejoDeCombos.setCombo(0,this.jComboBoxCasingGrade);
        this.jTextFieldInitialDepth.setText(null);
        this.jTextFieldFinalDepth.setText(null);
        this.jTextFieldCasingInitialDepth.setText(null);
        this.jTextFieldCasingFinalDepth.setText(null);
        this.jTextFieldNominalWeigth.setText(null);
        this.dateTimePickerInicioBajadaRevestidor.setDate(null);
        this.dateTimePickerFinalBajadaRevestidor.setDate(null);
        this.jTextFieldNroCorridasCasingLiner.setText(null);
        this.jButtonMantDiameters.setEnabled(true);
        this.jButtonMantSectionType.setEnabled(true);
        this.jButtonMantCasingOD.setEnabled(true); 
        this.jButtonMantCasingGrade.setEnabled(true);
        this.dateTimePickerInicioBajadaRevestidor.setEnabled(true);
        this.dateTimePickerFinalBajadaRevestidor.setEnabled(true);
        this.jTextFieldNroCorridasCasingLiner.setEnabled(true);
        this.jComboBoxEmpresaResponsable.requestFocus();
        habilitarControles(true);
    }
    
    public void modificar() {
        this.jButtonMantDiameters.setEnabled(true);
        this.jButtonMantSectionType.setEnabled(true);
        this.jButtonMantCasingOD.setEnabled(true); 
        this.jButtonMantCasingGrade.setEnabled(true);
        this.dateTimePickerInicioBajadaRevestidor.setEnabled(true);
        this.dateTimePickerFinalBajadaRevestidor.setEnabled(true);
        this.jTextFieldNroCorridasCasingLiner.setEnabled(true);
        this.jComboBoxEmpresaResponsable.requestFocus();
        habilitarControles(true);       
    }
    
    public void cancelar() {
        accion=NADA;
        setearControles();
        habilitarControles(false);
        this.jButtonMantDiameters.setEnabled(false);
        this.jButtonMantCasingGrade.setEnabled(false);
        this.jButtonMantCasingOD.setEnabled(false);
        this.jButtonMantSectionType.setEnabled(false);
        this.dateTimePickerInicioBajadaRevestidor.setEnabled(false);
        this.dateTimePickerFinalBajadaRevestidor.setEnabled(false);
        this.jTextFieldNroCorridasCasingLiner.setEnabled(false);
    }
    
    private void habilitarControles(boolean accion) {

        this.jComboBoxEmpresaResponsable.setEnabled(accion);
        this.jComboBoxDiameters.setEnabled(accion);
        this.jComboBoxSectionType.setEnabled(accion);
        this.jComboBoxCasingsOD.setEnabled(accion);
        this.jComboBoxCasingGrade.setEnabled(accion);
        this.jTextFieldCasingInitialDepth.setEnabled(accion);
        this.jTextFieldCasingFinalDepth.setEnabled(accion);
        this.jTextFieldInitialDepth.setEnabled(accion);
        this.jTextFieldFinalDepth.setEnabled(accion);
        this.jTextFieldNominalWeigth.setEnabled(accion);
        this.dateTimePickerInicioBajadaRevestidor.setEnabled(accion);
        this.dateTimePickerFinalBajadaRevestidor.setEnabled(accion);
        this.jTextFieldNroCorridasCasingLiner.setEnabled(accion);
              
        this.jComboBoxEmpresaResponsable.repaint();
        this.jComboBoxSectionType.repaint();
        this.jComboBoxCasingsOD.repaint();
        this.jComboBoxCasingGrade.repaint();
        this.jTextFieldCasingInitialDepth.repaint();
        this.jTextFieldCasingFinalDepth.repaint();
        this.jTextFieldInitialDepth.repaint();
        this.jTextFieldFinalDepth.repaint();
        this.jTextFieldNominalWeigth.repaint();
        this.dateTimePickerInicioBajadaRevestidor.repaint();
        this.dateTimePickerFinalBajadaRevestidor.repaint();
        this.jTextFieldNroCorridasCasingLiner.repaint();
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
        jComboBoxEmpresaResponsable = new javax.swing.JComboBox();
        jLabelLongitud9 = new javax.swing.JLabel();
        jLabelLongitud4 = new javax.swing.JLabel();
        jLabelLongitud5 = new javax.swing.JLabel();
        jLabelLongitud6 = new javax.swing.JLabel();
        jLabelLongitud7 = new javax.swing.JLabel();
        jLabelLongitud8 = new javax.swing.JLabel();
        jLabelLongitud2 = new javax.swing.JLabel();
        jComboBoxCasingGrade = new javax.swing.JComboBox();
        jButtonMantCasingGrade = new javax.swing.JButton();
        jButtonMantCasingGradeOK = new javax.swing.JButton();
        jButtonMantCasingGradeCancel = new javax.swing.JButton();
        jLabelLongitud1 = new javax.swing.JLabel();
        jComboBoxCasingsOD = new javax.swing.JComboBox();
        jButtonMantCasingOD = new javax.swing.JButton();
        jButtonMantCasingODOK = new javax.swing.JButton();
        jButtonMantCasingODCancel = new javax.swing.JButton();
        jLabelLongitud3 = new javax.swing.JLabel();
        jComboBoxSectionType = new javax.swing.JComboBox();
        jButtonMantSectionType = new javax.swing.JButton();
        jButtonMantSectionTypeOK = new javax.swing.JButton();
        jButtonMantSectionTypeCancel = new javax.swing.JButton();
        jLabelLongitud = new javax.swing.JLabel();
        jComboBoxDiameters = new javax.swing.JComboBox();
        jButtonMantDiameters = new javax.swing.JButton();
        jButtonMantDiametersOK = new javax.swing.JButton();
        jButtonMantDiametersCancel = new javax.swing.JButton();
        jLabelLongitud10 = new javax.swing.JLabel();
        jLabelLongitud11 = new javax.swing.JLabel();
        jLabelLongitud12 = new javax.swing.JLabel();
        dateTimePickerFinalBajadaRevestidor = new com.toedter.calendar.JDateChooser();
        dateTimePickerInicioBajadaRevestidor = new com.toedter.calendar.JDateChooser();
        jTextFieldDiameters = new javax.swing.JTextField();
        jTextFieldSectionType = new javax.swing.JTextField();
        jTextFieldCasingOD = new javax.swing.JTextField();
        jTextFieldCasingGrade = new javax.swing.JTextField();
        jTextFieldInitialDepth = new javax.swing.JTextField();
        jTextFieldFinalDepth = new javax.swing.JTextField();
        jTextFieldCasingInitialDepth = new javax.swing.JTextField();
        jTextFieldNroCorridasCasingLiner = new javax.swing.JTextField();
        jTextFieldNominalWeigth = new javax.swing.JTextField();
        jTextFieldCasingFinalDepth = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(683, 483));
        setModal(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelDescContenido.setText("Contenido:");
        getContentPane().add(jLabelDescContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 100, 70));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 11, 450, 70));

        jComboBoxEmpresaResponsable.setEnabled(false);
        jComboBoxEmpresaResponsable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEmpresaResponsableActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxEmpresaResponsable, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 230, -1));

        jLabelLongitud9.setText("Empresa Responsable:");
        getContentPane().add(jLabelLongitud9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabelLongitud4.setText("#Corridas casing/liner:");
        getContentPane().add(jLabelLongitud4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, -1, -1));

        jLabelLongitud5.setText("Casing Initial Depth:");
        getContentPane().add(jLabelLongitud5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, -1, -1));

        jLabelLongitud6.setText("Casing Final Depth:");
        getContentPane().add(jLabelLongitud6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 280, -1, -1));

        jLabelLongitud7.setText("Initial Depth:");
        getContentPane().add(jLabelLongitud7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, -1, -1));

        jLabelLongitud8.setText("Final Depth:");
        getContentPane().add(jLabelLongitud8, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 250, -1, -1));

        jLabelLongitud2.setText("Casing Grade:");
        getContentPane().add(jLabelLongitud2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        jComboBoxCasingGrade.setEnabled(false);
        jComboBoxCasingGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCasingGradeActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxCasingGrade, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, 116, -1));

        jButtonMantCasingGrade.setText("...");
        jButtonMantCasingGrade.setEnabled(false);
        jButtonMantCasingGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantCasingGradeActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantCasingGrade, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 210, 32, -1));

        jButtonMantCasingGradeOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantCasingGradeOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantCasingGradeOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantCasingGradeOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 210, 32, -1));

        jButtonMantCasingGradeCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantCasingGradeCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantCasingGradeCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantCasingGradeCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 210, 32, -1));

        jLabelLongitud1.setText("Casings OD:");
        getContentPane().add(jLabelLongitud1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        jComboBoxCasingsOD.setEnabled(false);
        jComboBoxCasingsOD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCasingsODActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxCasingsOD, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, 115, -1));

        jButtonMantCasingOD.setText("...");
        jButtonMantCasingOD.setEnabled(false);
        jButtonMantCasingOD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantCasingODActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantCasingOD, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, 32, -1));

        jButtonMantCasingODOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantCasingODOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantCasingODOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantCasingODOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 32, -1));

        jButtonMantCasingODCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantCasingODCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantCasingODCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantCasingODCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 180, 32, -1));

        jLabelLongitud3.setText("Section Type:");
        getContentPane().add(jLabelLongitud3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, 18));

        jComboBoxSectionType.setEnabled(false);
        jComboBoxSectionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSectionTypeActionPerformed(evt);
            }
        });
        getContentPane().add(jComboBoxSectionType, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 150, 116, -1));

        jButtonMantSectionType.setText("...");
        jButtonMantSectionType.setEnabled(false);
        jButtonMantSectionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantSectionTypeActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantSectionType, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 150, 32, -1));

        jButtonMantSectionTypeOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantSectionTypeOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantSectionTypeOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantSectionTypeOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 150, 32, -1));

        jButtonMantSectionTypeCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantSectionTypeCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantSectionTypeCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantSectionTypeCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 150, 32, -1));

        jLabelLongitud.setText("Diametro:");
        getContentPane().add(jLabelLongitud, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));

        jComboBoxDiameters.setEnabled(false);
        jComboBoxDiameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDiametersActionPerformed(evt);
            }
        });
        jComboBoxDiameters.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBoxDiametersKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jComboBoxDiametersKeyTyped(evt);
            }
        });
        getContentPane().add(jComboBoxDiameters, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, 116, -1));

        jButtonMantDiameters.setText("...");
        jButtonMantDiameters.setEnabled(false);
        jButtonMantDiameters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantDiametersActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantDiameters, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 32, -1));

        jButtonMantDiametersOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantDiametersOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantDiametersOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantDiametersOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 120, 32, -1));

        jButtonMantDiametersCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantDiametersCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantDiametersCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantDiametersCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 120, 32, -1));

        jLabelLongitud10.setText("Casing Nominal Weigth:");
        getContentPane().add(jLabelLongitud10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 410, -1, -1));

        jLabelLongitud11.setText("Inicio bajada revestidor:");
        getContentPane().add(jLabelLongitud11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, -1, -1));

        jLabelLongitud12.setText("Final bajada revestidor:");
        getContentPane().add(jLabelLongitud12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, -1, -1));

        dateTimePickerFinalBajadaRevestidor.setDateFormatString("dd/MM/yyyy HH:mm");
        dateTimePickerFinalBajadaRevestidor.setEnabled(false);
        getContentPane().add(dateTimePickerFinalBajadaRevestidor, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 350, 160, -1));

        dateTimePickerInicioBajadaRevestidor.setDateFormatString("dd/MM/yyyy HH:mm");
        dateTimePickerInicioBajadaRevestidor.setEnabled(false);
        getContentPane().add(dateTimePickerInicioBajadaRevestidor, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 320, 160, -1));

        jTextFieldDiameters.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldDiameters.setEnabled(false);
        jTextFieldDiameters.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldDiametersKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldDiameters, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 120, 110, -1));

        jTextFieldSectionType.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldSectionType.setEnabled(false);
        jTextFieldSectionType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldSectionTypeKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldSectionType, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 150, 110, -1));

        jTextFieldCasingOD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCasingOD.setEnabled(false);
        jTextFieldCasingOD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCasingODKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldCasingOD, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 180, 110, -1));

        jTextFieldCasingGrade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCasingGrade.setEnabled(false);
        jTextFieldCasingGrade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCasingGradeKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldCasingGrade, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 210, 110, -1));

        jTextFieldInitialDepth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldInitialDepth.setEnabled(false);
        jTextFieldInitialDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldInitialDepthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldInitialDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 110, -1));

        jTextFieldFinalDepth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldFinalDepth.setEnabled(false);
        jTextFieldFinalDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldFinalDepthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldFinalDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 250, 110, -1));

        jTextFieldCasingInitialDepth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCasingInitialDepth.setEnabled(false);
        jTextFieldCasingInitialDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCasingInitialDepthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldCasingInitialDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 280, 110, -1));

        jTextFieldNroCorridasCasingLiner.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldNroCorridasCasingLiner.setEnabled(false);
        jTextFieldNroCorridasCasingLiner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNroCorridasCasingLinerKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldNroCorridasCasingLiner, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 380, 110, -1));

        jTextFieldNominalWeigth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldNominalWeigth.setEnabled(false);
        jTextFieldNominalWeigth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNominalWeigthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldNominalWeigth, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 410, 110, -1));

        jTextFieldCasingFinalDepth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldCasingFinalDepth.setEnabled(false);
        jTextFieldCasingFinalDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCasingFinalDepthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldCasingFinalDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 280, 110, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void accionClickLista() {
        ClaveDesc ocd;
        if (modeloLista.isEmpty()) return;
        try {
            ocd=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            ob_array=oBD.select(Sections.class, "id="+ocd.getClave());
            oS= (Sections) ob_array[0];
            oManejoDeCombos.setCombo(oS.getEmpresaResponsableId(),this.jComboBoxEmpresaResponsable);
            oManejoDeCombos.setCombo(oS.getSectionTypeId(),this.jComboBoxSectionType);
            oManejoDeCombos.setCombo(oS.getDiameterId(),this.jComboBoxDiameters);
            oManejoDeCombos.setCombo(oS.getCasingODId(),this.jComboBoxCasingsOD);
            oManejoDeCombos.setCombo(oS.getCasingGradeId(),this.jComboBoxCasingGrade);
            this.dateTimePickerInicioBajadaRevestidor.setDate(oS.getFechaHoraInicioBajadaRevestidor());
            this.dateTimePickerFinalBajadaRevestidor.setDate(oS.getFechaHoraFinalBajadaRevestidor());
            this.jTextFieldInitialDepth.setText(oNumeros.valorString(oS.getInitialDepth()));
            this.jTextFieldFinalDepth.setText(oNumeros.valorString(oS.getFinalDepth()));
            this.jTextFieldCasingInitialDepth.setText(oNumeros.valorString(oS.getCasingInitialDepth()));
            this.jTextFieldCasingFinalDepth.setText(oNumeros.valorString(oS.getCasingFinalDepth()));
            this.jTextFieldNominalWeigth.setText(oNumeros.valorString(oS.getCasingNominalWeight()));
            this.jTextFieldNroCorridasCasingLiner.setText(oNumeros.valorString(oS.getNroCorridasCasingLiner()));
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantSecciones.class.getName()).log(Level.SEVERE, null, ex);
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
    }//GEN-LAST:event_jComboBoxEmpresaResponsableActionPerformed

    private void jComboBoxDiametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDiametersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxDiametersActionPerformed

    private void jComboBoxSectionTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSectionTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxSectionTypeActionPerformed

    private void jComboBoxCasingsODActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCasingsODActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxCasingsODActionPerformed

    private void jComboBoxCasingGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCasingGradeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxCasingGradeActionPerformed

    private void jTextFieldEdicionComboKeyTyped(java.awt.event.KeyEvent evt) {                                                          
        // TODO add your handling code here:
    }
    
    private void jButtonMantDiametersOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantDiametersOKActionPerformed
      oManejoDeCombos.ingresarAlComboBox(this.jTextFieldDiameters,this.jComboBoxDiameters);  
      habilitarControles(true);
      super.jButtonCancelar.setEnabled(true);
      super.jButtonProcesar.setEnabled(true);
      this.jButtonMantDiameters.setEnabled(true);
      this.jTextFieldDiameters.setEnabled(false);
      this.jTextFieldDiameters.setVisible(false);
      this.jTextFieldDiameters.setText("");
      this.jButtonMantDiametersOK.setVisible(false);
      this.jButtonMantDiametersCancel.setVisible(false);
      this.jTextFieldDiameters.repaint();
      this.pack();
    }//GEN-LAST:event_jButtonMantDiametersOKActionPerformed
       
    private void jComboBoxDiametersKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBoxDiametersKeyTyped
        // TODO add your handling code here:         
    }//GEN-LAST:event_jComboBoxDiametersKeyTyped

    private void jComboBoxDiametersKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBoxDiametersKeyPressed
    }//GEN-LAST:event_jComboBoxDiametersKeyPressed

    
    private void jButtonMantDiametersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantDiametersActionPerformed
        // TODO add your handling code here: 
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantDiameters.setEnabled(false);
        this.jTextFieldDiameters.setEnabled(true);
        this.jTextFieldDiameters.setVisible(true);
        this.jButtonMantDiametersOK.setVisible(true);
        this.jButtonMantDiametersCancel.setVisible(true);
        this.jTextFieldDiameters.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantDiametersActionPerformed

    private void jButtonMantDiametersCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantDiametersCancelActionPerformed
        // TODO add your handling code here:
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantDiameters.setEnabled(true);
        this.jTextFieldDiameters.setEnabled(false);
        this.jTextFieldDiameters.setVisible(false);
        this.jTextFieldDiameters.setText("");
        this.jButtonMantDiametersOK.setVisible(false);
        this.jButtonMantDiametersCancel.setVisible(false);
        this.jTextFieldDiameters.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantDiametersCancelActionPerformed

    private void jButtonMantSectionTypeOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantSectionTypeOKActionPerformed
        // TODO add your handling code here:
        oManejoDeCombos.ingresarAlComboBox(this.jTextFieldSectionType,this.jComboBoxSectionType);  
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantSectionType.setEnabled(true);
        this.jTextFieldSectionType.setEnabled(false);
        this.jTextFieldSectionType.setVisible(false);
        this.jTextFieldSectionType.setText("");
        this.jButtonMantSectionTypeOK.setVisible(false);
        this.jButtonMantSectionTypeCancel.setVisible(false);
        this.jTextFieldSectionType.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantSectionTypeOKActionPerformed

    private void jButtonMantSectionTypeCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantSectionTypeCancelActionPerformed
        // TODO add your handling code here:
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantSectionType.setEnabled(true);
        this.jTextFieldSectionType.setEnabled(false);
        this.jTextFieldSectionType.setVisible(false);
        this.jTextFieldSectionType.setText("");
        this.jButtonMantSectionTypeOK.setVisible(false);
        this.jButtonMantSectionTypeCancel.setVisible(false);
        this.jTextFieldSectionType.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantSectionTypeCancelActionPerformed

    private void jButtonMantSectionTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantSectionTypeActionPerformed
        // TODO add your handling code here:
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantSectionType.setEnabled(false);
        this.jTextFieldSectionType.setEnabled(true);
        this.jTextFieldSectionType.setVisible(true);
        this.jButtonMantSectionTypeOK.setVisible(true);
        this.jButtonMantSectionTypeCancel.setVisible(true);
        this.jTextFieldSectionType.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantSectionTypeActionPerformed

    private void jButtonMantCasingODOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantCasingODOKActionPerformed
        // TODO add your handling code here:
        oManejoDeCombos.ingresarAlComboBox(this.jTextFieldCasingOD,this.jComboBoxCasingsOD);  
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantCasingOD.setEnabled(true);
        this.jTextFieldCasingOD.setEnabled(false);
        this.jTextFieldCasingOD.setVisible(false);
        this.jTextFieldCasingOD.setText("");
        this.jButtonMantCasingODOK.setVisible(false);
        this.jButtonMantCasingODCancel.setVisible(false);
        this.jTextFieldCasingOD.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantCasingODOKActionPerformed

    private void jButtonMantCasingODCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantCasingODCancelActionPerformed
        // TODO add your handling code here:
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantCasingOD.setEnabled(true);
        this.jTextFieldCasingOD.setEnabled(false);
        this.jTextFieldCasingOD.setVisible(false);
        this.jTextFieldCasingOD.setText("");
        this.jButtonMantCasingODOK.setVisible(false);
        this.jButtonMantCasingODCancel.setVisible(false);
        this.jTextFieldCasingOD.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantCasingODCancelActionPerformed

    private void jButtonMantCasingODActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantCasingODActionPerformed
        // TODO add your handling code here:
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantCasingOD.setEnabled(false);
        this.jTextFieldCasingOD.setEnabled(true);
        this.jTextFieldCasingOD.setVisible(true);
        this.jButtonMantCasingODOK.setVisible(true);
        this.jButtonMantCasingODCancel.setVisible(true);
        this.jTextFieldCasingOD.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantCasingODActionPerformed

    private void jButtonMantCasingGradeOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantCasingGradeOKActionPerformed
        // TODO add your handling code here:
        oManejoDeCombos.ingresarAlComboBox(this.jTextFieldCasingGrade,this.jComboBoxCasingGrade);  
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantCasingGrade.setEnabled(true);
        this.jTextFieldCasingGrade.setEnabled(false);
        this.jTextFieldCasingGrade.setVisible(false);
        this.jTextFieldCasingGrade.setText("");
        this.jButtonMantCasingGradeOK.setVisible(false);
        this.jButtonMantCasingGradeCancel.setVisible(false);
        this.jTextFieldCasingGrade.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantCasingGradeOKActionPerformed

    private void jButtonMantCasingGradeCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantCasingGradeCancelActionPerformed
        // TODO add your handling code here:
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantCasingGrade.setEnabled(true);
        this.jTextFieldCasingGrade.setEnabled(false);
        this.jTextFieldCasingGrade.setVisible(false);
        this.jTextFieldCasingGrade.setText("");
        this.jButtonMantCasingGradeOK.setVisible(false);
        this.jButtonMantCasingGradeCancel.setVisible(false);
        this.jTextFieldCasingGrade.repaint();
        this.pack();
    }//GEN-LAST:event_jButtonMantCasingGradeCancelActionPerformed

    private void jButtonMantCasingGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantCasingGradeActionPerformed
        // TODO add your handling code here:
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantCasingGrade.setEnabled(false);
        this.jTextFieldCasingGrade.setEnabled(true);
        this.jTextFieldCasingGrade.setVisible(true);
        this.jButtonMantCasingGradeOK.setVisible(true);
        this.jButtonMantCasingGradeCancel.setVisible(true);
        this.jTextFieldCasingGrade.repaint();
        this.pack();

    }//GEN-LAST:event_jButtonMantCasingGradeActionPerformed

    private void jTextFieldDiametersKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDiametersKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldDiametersKeyTyped

    private void jTextFieldSectionTypeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSectionTypeKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSectionTypeKeyTyped

    private void jTextFieldCasingODKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCasingODKeyTyped
        oNumeros.soloDobles(evt); 
    }//GEN-LAST:event_jTextFieldCasingODKeyTyped

    private void jTextFieldCasingGradeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCasingGradeKeyTyped
 
    }//GEN-LAST:event_jTextFieldCasingGradeKeyTyped

    private void jTextFieldInitialDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInitialDepthKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldInitialDepthKeyTyped

    private void jTextFieldFinalDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFinalDepthKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldFinalDepthKeyTyped

    private void jTextFieldCasingInitialDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCasingInitialDepthKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldCasingInitialDepthKeyTyped

    private void jTextFieldNroCorridasCasingLinerKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNroCorridasCasingLinerKeyTyped
        oNumeros.soloEnteros(evt);
    }//GEN-LAST:event_jTextFieldNroCorridasCasingLinerKeyTyped

    private void jTextFieldCasingFinalDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCasingFinalDepthKeyTyped
        oNumeros.soloDobles(evt);        
    }//GEN-LAST:event_jTextFieldCasingFinalDepthKeyTyped

    private void jTextFieldNominalWeigthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNominalWeigthKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldNominalWeigthKeyTyped

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
            java.util.logging.Logger.getLogger(MantSecciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantSecciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantSecciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantSecciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantSecciones dialog = new MantSecciones(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser dateTimePickerFinalBajadaRevestidor;
    private com.toedter.calendar.JDateChooser dateTimePickerInicioBajadaRevestidor;
    private javax.swing.JButton jButtonMantCasingGrade;
    private javax.swing.JButton jButtonMantCasingGradeCancel;
    private javax.swing.JButton jButtonMantCasingGradeOK;
    private javax.swing.JButton jButtonMantCasingOD;
    private javax.swing.JButton jButtonMantCasingODCancel;
    private javax.swing.JButton jButtonMantCasingODOK;
    private javax.swing.JButton jButtonMantDiameters;
    private javax.swing.JButton jButtonMantDiametersCancel;
    private javax.swing.JButton jButtonMantDiametersOK;
    private javax.swing.JButton jButtonMantSectionType;
    private javax.swing.JButton jButtonMantSectionTypeCancel;
    private javax.swing.JButton jButtonMantSectionTypeOK;
    private javax.swing.JComboBox jComboBoxCasingGrade;
    private javax.swing.JComboBox jComboBoxCasingsOD;
    private javax.swing.JComboBox jComboBoxDiameters;
    private javax.swing.JComboBox jComboBoxEmpresaResponsable;
    private javax.swing.JComboBox jComboBoxSectionType;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JLabel jLabelLongitud;
    private javax.swing.JLabel jLabelLongitud1;
    private javax.swing.JLabel jLabelLongitud10;
    private javax.swing.JLabel jLabelLongitud11;
    private javax.swing.JLabel jLabelLongitud12;
    private javax.swing.JLabel jLabelLongitud2;
    private javax.swing.JLabel jLabelLongitud3;
    private javax.swing.JLabel jLabelLongitud4;
    private javax.swing.JLabel jLabelLongitud5;
    private javax.swing.JLabel jLabelLongitud6;
    private javax.swing.JLabel jLabelLongitud7;
    private javax.swing.JLabel jLabelLongitud8;
    private javax.swing.JLabel jLabelLongitud9;
    private javax.swing.JList jListExistentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldCasingFinalDepth;
    private javax.swing.JTextField jTextFieldCasingGrade;
    private javax.swing.JTextField jTextFieldCasingInitialDepth;
    private javax.swing.JTextField jTextFieldCasingOD;
    private javax.swing.JTextField jTextFieldDiameters;
    private javax.swing.JTextField jTextFieldFinalDepth;
    private javax.swing.JTextField jTextFieldInitialDepth;
    private javax.swing.JTextField jTextFieldNominalWeigth;
    private javax.swing.JTextField jTextFieldNroCorridasCasingLiner;
    private javax.swing.JTextField jTextFieldSectionType;
    // End of variables declaration//GEN-END:variables
}



