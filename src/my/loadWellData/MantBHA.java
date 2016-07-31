/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;
import miLibreria.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


/**
 *
 * @author Luis
 */

public class MantBHA extends MantPpal {
    public String sDescNodo;
    private javax.swing.JComboBox jComboBoxPipes = new javax.swing.JComboBox();
    private javax.swing.JComboBox jComboBoxEstabilizers = new javax.swing.JComboBox();
    public ManejoBDI oBD;
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    public BHA oBHA;
    public Run oRunFromMainForm=new Run();
    ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    public boolean allowEdit=true;
    public java.awt.Frame parent;
    public int alturaVentana;
    public File selectedFile;
    public File currDirectory;

    /**
     * Creates new form MantBHA
     */
    public MantBHA(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();        
     }
    
    public void setParam(ManejoBDI oBDIn) {
        oBD=oBDIn;
        
        this.jLabelBendHousingAngle.setVisible(false);
        this.jPanelAmpliacion.setVisible(false);
        
        super.jPanel1.setVisible(allowEdit);
        this.jLabelCargarXML.setEnabled(allowEdit);
        this.jRadioButtonDummy.setVisible(false);
        this.jLabelDescContenido.setText("Run: #" + sDescNodo.substring(sDescNodo.indexOf('#')+2));
              
        super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);
        
        alturaVentana=this.getHeight();
        
        this.jButtonMantBitManufacturerOK.setVisible(false);
        this.jButtonMantBitManufacturerCancel.setVisible(false);
        this.jTextFieldBitManufacturerNombre.setVisible(false);
        
        
        this.jTablePipes.getColumnModel().getColumn(0).setPreferredWidth(300);
        this.jTablePipes.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
        this.jTablePipes.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));
        this.jTablePipes.getColumnModel().getColumn(2).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));
        this.jTablePipes.getColumnModel().getColumn(3).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));

        for (int i=0;i<=this.jTablePipes.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTablePipes.getColumnCount()-1;j++) {
                MantBHA.this.jTablePipes.setValueAt("", i, j);                
            }
        }
      
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoPipe.class,this.jComboBoxPipes,"");
        
        jComboBoxPipes.setEnabled(true);
        jComboBoxPipes.setVisible(true);
        this.jTablePipes.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(jComboBoxPipes));
        TableModelListener tml;
        this.jTablePipes.getModel().addTableModelListener(
        new TableModelListener() 
        {
            int l=0;
            int row=0;
            int col=0;
            Object o;
            int rowCount=MantBHA.this.jTablePipes.getRowCount();
            int colCount=MantBHA.this.jTablePipes.getColumnCount();
            Object[][] a=new Object[rowCount][colCount];
            boolean cambiando=false;
            int j=0;
            public void tableChanged(TableModelEvent evt) 
            {   
                if (cambiando) return;
                
                for (int i=0;i<=rowCount-1;i++) {
                    for (int j=0;j<=colCount-1;j++) {
                        a[i][j]="";
                    }
                }
                
                l=0;
                
                row=evt.getFirstRow();
                col=evt.getColumn();
                ComboItem oCI=null;
                if (col==0) {
                    for (int i=0;i<=rowCount-1;i++) {
                        o=MantBHA.this.jTablePipes.getValueAt(i, 0);
                        if (o.getClass()==ComboItem.class){
                            oCI=(ComboItem) o;                        
                            if (oCI.getKey() !="" && oCI.getKey() !=null) {
                                try {
                                    for (j=0;j<=colCount-1;j++) {
                                        a[l][j]=MantBHA.this.jTablePipes.getValueAt(i, j);
                                    }
                                    a[l][1]=""+(l+1);
                                    l++;
                                }
                                catch (NullPointerException ex){a[l][j]="";} 
                            } 
                        }
                    }                   
                    for (int i=0;i<rowCount;i++) {
                        for (int j=0;j<=colCount-1;j++) {
                            cambiando=true;
                            MantBHA.this.jTablePipes.setValueAt(a[i][j], i, j);
                        }
                    }
                    cambiando=false;
                }                
            }
        });
        
        this.jTableEstabilizers.getColumnModel().getColumn(0).setPreferredWidth(300);
        this.jTableEstabilizers.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
        this.jTableEstabilizers.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));
        this.jTableEstabilizers.getColumnModel().getColumn(2).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));
        this.jTableEstabilizers.getColumnModel().getColumn(3).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));        
        
        for (int i=0;i<=this.jTableEstabilizers.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTableEstabilizers.getColumnCount()-1;j++) {
                MantBHA.this.jTableEstabilizers.setValueAt("", i, j);                
            }
        }        
        
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoEstabilizer.class,this.jComboBoxEstabilizers,"");
        
        jComboBoxEstabilizers.setEnabled(true);
        jComboBoxEstabilizers.setVisible(true);
        this.jTableEstabilizers.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(jComboBoxEstabilizers));
        
        this.jTableEstabilizers.getModel().addTableModelListener(
        new TableModelListener() 
        {
            int l=0;
            int row=0;
            int col=0;
            Object o;
            int rowCount=MantBHA.this.jTableEstabilizers.getRowCount();
            int colCount=MantBHA.this.jTableEstabilizers.getColumnCount();
            Object[][] a=new Object[rowCount][colCount];
            boolean cambiando=false;
            int j=0;
            public void tableChanged(TableModelEvent evt) 
            {   
                if (cambiando) return;
                
                for (int i=0;i<=rowCount-1;i++) {
                    for (int j=0;j<=colCount-1;j++) {
                        a[i][j]="";
                    }
                }
                
                l=0;
                
                row=evt.getFirstRow();
                col=evt.getColumn();
                ComboItem oCI=null;
                if (col==0) {
                    for (int i=0;i<=rowCount-1;i++) {
                        o=MantBHA.this.jTableEstabilizers.getValueAt(i, 0);
                        if (o.getClass()==ComboItem.class){
                            oCI=(ComboItem) o;                        
                            if (oCI.getKey() !="" && oCI.getKey() !=null) {
                                try {
                                    for (j=0;j<=colCount-1;j++) {
                                        a[l][j]=MantBHA.this.jTableEstabilizers.getValueAt(i, j);
                                    }
                                    a[l][1]=""+(l+1);
                                    l++;
                                }
                                catch (NullPointerException ex){a[l][j]="";} 
                            } 
                        }
                    }                   
                    for (int i=0;i<rowCount;i++) {
                        for (int j=0;j<=colCount-1;j++) {
                            cambiando=true;
                            MantBHA.this.jTableEstabilizers.setValueAt(a[i][j], i, j);
                        }
                    }
                    cambiando=false;
                }                
            }
        });
        
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoMotor.class,this.jComboBoxMotor);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,RSS.class,this.jComboBoxRSS);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITType.class,this.jComboBoxBitType);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITManufacturer.class,this.jComboBoxBitManufacturer);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITManufacturer.class,this.jComboBoxReamerManufacturer);        
        
        if (mostrarBHA(this.oRunFromMainForm)) {
            super.jButtonAgregar.setEnabled(false);
            super.jButtonAgregar.setVisible(false);
            super.jButtonModificar.setEnabled(true);
            super.jButtonEliminar.setEnabled(true);
        } else {
            super.jButtonAgregar.setEnabled(true);
            super.jButtonModificar.setEnabled(false);
            super.jButtonEliminar.setEnabled(false);            
        }
        
        habilitarControles(false);
             
    }
    
    private boolean mostrarBHA(Run oRun) {
        oBHA=new BHA();
        DirectionalTool oDT=new DirectionalTool();
        BHAPipe oBHAPipe=new BHAPipe();
        BHAEstabilizer oBHAEstabilizer=new BHAEstabilizer();
        TipoPipe oTP=new TipoPipe();
        TipoEstabilizer oTE=new TipoEstabilizer();
        Object[] oArray=null;
        ComboItem oCI=new ComboItem();
        try {
            if (oBD.select(BHA.class, "runId="+oRun.getId()).length>0) {
                oBHA=(BHA) oBD.select(BHA.class, "runId="+oRun.getId())[0];
                oArray=oBD.select(BHAPipe.class, "bhaId="+oBHA.getId());
                oDT=(DirectionalTool) oBD.select(DirectionalTool.class, "bhaId="+oBHA.getId())[0];
            }
        } catch (InstantiationException | IllegalAccessException | ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (oBHA.getId()==-1) {
            accion=NADA;
            setearControles();
            return false;
        }
        oManejoDeCombos.setCombo(oBHA.getBitTypeId(),this.jComboBoxBitType);
        oManejoDeCombos.setCombo(oBHA.getBitManufacturerId(),this.jComboBoxBitManufacturer);
        
        for (int i=0;i<=oArray.length-1;i++) {
            oBHAPipe=(BHAPipe) oArray[i];
            try {
                oTP=(TipoPipe) oBD.select(TipoPipe.class,"id="+oBHAPipe.getTipoPipeId())[0];
                oCI=new ComboItem();
                oCI.setValue(Long.toString(oTP.getId()));
                oCI.setKey(oTP.getNombre());
                MantBHA.this.jTablePipes.setValueAt(oCI, i, 0);
                MantBHA.this.jTablePipes.setValueAt(oBHAPipe.getSecuencia(), i, 1);
                MantBHA.this.jTablePipes.setValueAt(oBHAPipe.getLongitud(), i, 2);
                MantBHA.this.jTablePipes.setValueAt(oBHAPipe.getOd(), i, 3);
                
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            oArray=oBD.select(BHAEstabilizer.class, "bhaId="+oBHA.getId());
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i=0;i<=oArray.length-1;i++) {
            oBHAEstabilizer=(BHAEstabilizer) oArray[i];
            try {
                oTE=(TipoEstabilizer) oBD.select(TipoEstabilizer.class,"id="+oBHAEstabilizer.getTipoEstabilizerId())[0];
                oCI=new ComboItem();
                oCI.setValue(Long.toString(oTE.getId()));
                oCI.setKey(oTE.getNombre());
                MantBHA.this.jTableEstabilizers.setValueAt(oCI, i, 0);
                MantBHA.this.jTableEstabilizers.setValueAt(oBHAEstabilizer.getPosicion(), i, 1);
                MantBHA.this.jTableEstabilizers.setValueAt(oBHAEstabilizer.getDistancia(), i, 2);
                MantBHA.this.jTableEstabilizers.setValueAt(oBHAEstabilizer.getOd(), i, 3);
                
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        
        if (oDT.getMotor()) {
           oManejoDeCombos.setCombo(oDT.getTipoMotorId(),this.jComboBoxMotor);
           this.jRadioButtonMotor.setSelected(true);
           TipoMotor oTM;
           try {
               oTM=(TipoMotor) oBD.select(TipoMotor.class, "Id="+oDT.getTipoMotorId())[0];
               this.jLabelBendHousingAngle.setText("Bend Housing Angle: "+oTM.getBendHousingAngle());
               this.jLabelBendHousingAngle.setVisible(true);
           } catch (InstantiationException | IllegalAccessException ex) {
               Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
        if (oDT.getRss()) {
           oManejoDeCombos.setCombo(oDT.getRssId(),this.jComboBoxRSS); 
           this.jRadioButtonRSS.setSelected(true);
           this.jLabelBendHousingAngle.setText("Bend Housing Angle: ");
           this.jLabelBendHousingAngle.setVisible(false);
        }
        
        this.jTextFieldTFA.setText(Double.toString(oBHA.getTfa()));
        this.jCheckBoxInFlexLine.setSelected(oBHA.getInFlexLine());
        this.jTextFieldBitSN.setText(oBHA.getSerialNumber());
        
        this.jCheckBoxAmpliacion.setSelected(oBHA.getAmpliacion());
        
        if (oBHA.getAmpliacion()==false) oBHA.setReamerSize(0);
        this.jPanelAmpliacion.setVisible(oBHA.getAmpliacion());
        this.jTextFieldReamerModel.setText(oBHA.getReamerModel());
        this.jTextFieldReamerSize.setText(Double.toString(oBHA.getReamerSize()));
        oManejoDeCombos.setCombo(oBHA.getReamerManufacturerId(),this.jComboBoxReamerManufacturer);
        reSize();
        
        return true;
    }
    
    public void agregar() {
        limpiarPantalla();
        habilitarControles(true);
    }
    
    public void limpiarPantalla() {
        this.jComboBoxBitType.setSelectedIndex(0);  
        this.jComboBoxBitManufacturer.setSelectedIndex(0);
        for (int i=this.jTablePipes.getRowCount()-1;i>=0;i--) {
                MantBHA.this.jTablePipes.setValueAt("", i, 0);                
        }
        for (int i=this.jTableEstabilizers.getRowCount()-1;i>=0;i--) {
                MantBHA.this.jTableEstabilizers.setValueAt("", i, 0);                
        }
        this.jTextFieldTFA.setText("");
        this.jTextFieldBitSN.setText("");        
        this.jCheckBoxInFlexLine.setSelected(false);
        this.jComboBoxMotor.setSelectedIndex(0);
        this.jComboBoxRSS.setSelectedIndex(0);
        this.jRadioButtonDummy.setSelected(true);
    }
    
    public void modificar() {
        habilitarControles(true);
    }    
    
    public void cancelar() {
        accion=NADA;
        setearControles();
        habilitarControles(false);
        if (mostrarBHA(this.oRunFromMainForm)) {
            super.jButtonModificar.setEnabled(true);
            super.jButtonEliminar.setEnabled(true);
        } else {
            super.jButtonModificar.setEnabled(false);
            super.jButtonEliminar.setEnabled(false);            
        }
    }
    
    public void procesar() {
        String s="";
        long bitTypeId=0;
        long bitManufacturerId=0;
        long tipoMotorId=0;
        long RSSId=0;
        long directionalToolId=0;
        long bhaId=0;
        double tfa=0;
        boolean huboError=false;
        TableModel tmPipes,tmEstabilizers;
        BHAPipe oBHAPipe=new BHAPipe();
        BHAEstabilizer oBHAEstabilizer=new BHAEstabilizer();
        ComboItem oCI;
        final int MOTOR=1;
        final int RSS=2;
        final int NADA=0;
        int tipoDT=NADA;
        String serialNumber="";
        
        ingresarNuevosItemsEnTablas();
        
        if (this.jComboBoxBitType.getSelectedIndex()==0) {
            s+="** Falta Bit Type **\n\r";
        }
        else {
            bitTypeId=oManejoDeCombos.getComboID(this.jComboBoxBitType);
        }
        
        if (this.jComboBoxBitManufacturer.getSelectedIndex()==0) {
            s+="** Falta Bit Manufacturer **\n\r";
        }
        else {
            bitManufacturerId=oManejoDeCombos.getComboID(this.jComboBoxBitManufacturer);
        }
        
        if (this.jRadioButtonMotor.isSelected()) 
            tipoDT=MOTOR;
        else if (this.jRadioButtonRSS.isSelected())
            tipoDT=RSS; 
        
        if (tipoDT==NADA) {
           s+="** Falta TFA **\n\r";  
        } else;
        {
          if (tipoDT==MOTOR && this.jComboBoxMotor.getSelectedIndex()==0) 
             s+="** Falta Tipo Motor **\n\r";
          else if (tipoDT==MOTOR) 
              tipoMotorId=oManejoDeCombos.getComboID(this.jComboBoxMotor);
          
          if (tipoDT==RSS && this.jComboBoxRSS.getSelectedIndex()==0) 
             s+="** Falta RSS **\n\r";
          else if (tipoDT==RSS) 
              RSSId=oManejoDeCombos.getComboID(this.jComboBoxRSS);
        }        
               
        if (this.jTextFieldTFA.getText().trim().isEmpty()){
            s+="** Falta TFA **\n\r";            
        }
        else {
            tfa=new Double(this.jTextFieldTFA.getText().replace(",", ".")) ;
        }
        
        if (this.jTextFieldBitSN.getText().trim().isEmpty()){
            s+="** Falta Bit Serial Number **\n\r";            
        }
        else {
            serialNumber=this.jTextFieldBitSN.getText();
        }
        
        Object o=null;
        
        tmPipes=this.jTablePipes.getModel();
        boolean hayPipes = false;
        for (int i=0;i<=tmPipes.getRowCount()-1;i++) {
            o=tmPipes.getValueAt(i, 0);
            if (o=="" || o==null) {break;}
            hayPipes=true;
            for (int j=1;j<=tmPipes.getColumnCount()-1;j++) {
                o=tmPipes.getValueAt(i, j);
                if (o=="" || o==null) {
                    s+="** Información incompleta en Pipes, posicion:"+ (i+1) + "**\n\r";
                    break;
                }
            }
        }
        
        if (hayPipes==false) {
            s+="** Falta la información de Pipes **\n\r";
        }
        
        tmEstabilizers=this.jTableEstabilizers.getModel();
        boolean hayEstabilizers = false;
        for (int i=0;i<=tmEstabilizers.getRowCount()-1;i++) {
            o=tmEstabilizers.getValueAt(i, 0);
            if (o=="" || o==null) {break;}
            hayEstabilizers=true;
            for (int j=1;j<=tmEstabilizers.getColumnCount()-1;j++) {
                o=tmEstabilizers.getValueAt(i, j);
                if (o=="" || o==null) {
                    s+="** Información incompleta en Estabilizers, posicion:"+ (i+1) + "**\n\r";
                    break;
                }
            }
        }
        
        if (hayEstabilizers==false) {
            s+="** Falta la información de Estabilizers **\n\r";
        }
        
        if (this.jCheckBoxAmpliacion.isSelected()) {
            if ("".equals(this.jTextFieldReamerModel.getText())) s+="** Falta Reamer Model **\n\r";
            if ("".equals(this.jTextFieldReamerSize.getText())) {
                s+="** Falta Reamer Size **\n\r";
            } else {
                if (Double.parseDouble(this.jTextFieldReamerSize.getText())==0.0) s+="** Falta Reamer Size **\n\r";
            }
            if (this.jComboBoxReamerManufacturer.getSelectedIndex()==0) s+="** Falta Reamer Manufacturer **\n\r";
        }
        
        if (s.length()>0) {
            msgbox(s);
            return;
        }
        
        if (accion==AGREGAR) {
                
            if (!huboError){
                oBHA = new BHA();
                oBHA.setTipoDT((tipoDT==MOTOR)?"MOTOR":"RSS");
                oBHA.setBitTypeId(bitTypeId);
                oBHA.setBitManufacturerId(bitManufacturerId);
                oBHA.setSerialNumber(serialNumber);
                oBHA.setRunId(this.oRunFromMainForm.getId());
                oBHA.setTfa(tfa);
                oBHA.setInFlexLine(this.jCheckBoxInFlexLine.isSelected());
                oBHA.setAmpliacion(this.jCheckBoxAmpliacion.isSelected());
                if (this.jCheckBoxAmpliacion.isSelected()) {
                    oBHA.setReamerModel(this.jTextFieldReamerModel.getText());
                    oBHA.setReamerSize(Double.parseDouble(this.jTextFieldReamerSize.getText()));
                    oBHA.setReamerManufacturerId(oManejoDeCombos.getComboID(this.jComboBoxReamerManufacturer));
                }
                if (oBD.insert(oBHA)){
                    bhaId=oBD.ultimaClave(oBHA);
                    oBHA.setId(bhaId);
                }else
                    huboError=true;                
            } 
            
            DirectionalTool oDT=new DirectionalTool();
            oDT.setBhaId(bhaId);
            if (tipoMotorId>0){
               oDT.setMotor(true);
               oDT.setTipoMotorId(tipoMotorId);                
            }else {
               oDT.setRss(true);
               oDT.setRssId(RSSId);                 
            }
            if (oBD.insert(oDT)){
               directionalToolId=oBD.ultimaClave(oDT);
            }else 
                huboError=true;
            
            if (!huboError){
                for (int i=0;i<=tmPipes.getRowCount()-1;i++) {
                    o=tmPipes.getValueAt(i, 0);
                    if (o.getClass()!=ComboItem.class){break;}
                    oCI=(ComboItem) o;
                    oBHAPipe=new BHAPipe();
                    oBHAPipe.setTipoPipeId(new Long(oCI.getValue()));
                    for (int j=1;j<=tmPipes.getColumnCount()-1;j++) {
                        o=tmPipes.getValueAt(i, j);
                        oBHAPipe.setBhaId(bhaId);
                        if (j==1) {
                            oBHAPipe.setSecuencia(new Long(o.toString()));
                        }
                        if (j==2) {
                            oBHAPipe.setLongitud(new Long(o.toString()));
                        }
                        if (j==3) {
                            oBHAPipe.setOd(new Double(o.toString()));
                        }                        
                    }
                    if (! oBD.insert(oBHAPipe))huboError=true; 
                }
            }
            if (!huboError){
                for (int i=0;i<=tmEstabilizers.getRowCount()-1;i++) {
                    o=tmEstabilizers.getValueAt(i, 0);
                    if (o.getClass()!=ComboItem.class){break;}
                    oCI=(ComboItem) o;
                    oBHAEstabilizer=new BHAEstabilizer();
                    oBHAEstabilizer.setTipoEstabilizerId(new Long(oCI.getValue()));
                    for (int j=1;j<=tmEstabilizers.getColumnCount()-1;j++) {
                        o=tmEstabilizers.getValueAt(i, j);
                        oBHAEstabilizer.setBhaId(bhaId);
                        if (j==1) {
                            oBHAEstabilizer.setPosicion(new Long(o.toString()));
                        }
                        if (j==2) {
                            oBHAEstabilizer.setDistancia(new Double(o.toString()));
                        }
                        if (j==3) {
                            oBHAEstabilizer.setOd(new Double(o.toString()));
                        }                        
                    }
                    if (! oBD.insert(oBHAEstabilizer))huboError=true; 
                }
            }
            
            if (huboError) {
                    msgbox("BHA No Agredada. Error");
                    habilitarControles(false);       
            }else {
                 msgbox("BHA Agredada Satisfactoriamente");
                 accion=AFTER_UPDATE;
                 super.jButtonAgregar.setVisible(false);
                 setearControles();
                 habilitarControles(false); 
            }
        }
        
        if (accion==MODIFICAR) {
            bhaId=oBHA.getId();
            if (!huboError){
                oBHA.setTipoDT((tipoDT==MOTOR)?"MOTOR":"RSS");
                oBHA.setBitTypeId(bitTypeId);
                oBHA.setBitManufacturerId(bitManufacturerId);
                oBHA.setSerialNumber(serialNumber);
                oBHA.setRunId(this.oRunFromMainForm.getId());
                oBHA.setTfa(tfa);
                oBHA.setInFlexLine(this.jCheckBoxInFlexLine.isSelected());
                oBHA.setAmpliacion(this.jCheckBoxAmpliacion.isSelected());
                oBHA.setReamerModel(this.jTextFieldReamerModel.getText());
                oBHA.setReamerSize(Double.parseDouble(this.jTextFieldReamerSize.getText()));
                oBHA.setReamerManufacturerId(oManejoDeCombos.getComboID(this.jComboBoxReamerManufacturer));
                if (!oBD.update(oBHA, "iD="+oBHA.getId())) {
                    huboError=true;
                }
            } 
            
            DirectionalTool oDT=new DirectionalTool();
            oBD.delete(oDT, "bhaId="+bhaId);
            oDT.setBhaId(bhaId);
            if (tipoMotorId>0){
               oDT.setMotor(true);
               oDT.setTipoMotorId(tipoMotorId);                
            }else {
               oDT.setRss(true);
               oDT.setRssId(RSSId);                 
            }
            if (oBD.insert(oDT)){
               directionalToolId=oBD.ultimaClave(oDT);
            }else 
                huboError=true;
            
            if (!huboError){
                oBD.delete(oBHAPipe, "bhaId="+bhaId);
                for (int i=0;i<=tmPipes.getRowCount()-1;i++) {
                    o=tmPipes.getValueAt(i, 0);
                    if (o.getClass()!=ComboItem.class){break;}
                    oCI=(ComboItem) o;
                    oBHAPipe=new BHAPipe();
                    oBHAPipe.setTipoPipeId(new Long(oCI.getValue()));
                    for (int j=1;j<=tmPipes.getColumnCount()-1;j++) {
                        o=tmPipes.getValueAt(i, j);
                        oBHAPipe.setBhaId(bhaId);
                        if (j==1) {
                            oBHAPipe.setSecuencia(new Long(o.toString()));
                        }
                        if (j==2) {
                            oBHAPipe.setLongitud(new Long(o.toString()));
                        }
                        if (j==3) {
                            oBHAPipe.setOd(new Double(o.toString()));
                        }                        
                    }
                    if (! oBD.insert(oBHAPipe))huboError=true; 
                }
            }
            if (!huboError){
                oBD.delete(oBHAEstabilizer, "bhaId="+bhaId);
                for (int i=0;i<=tmEstabilizers.getRowCount()-1;i++) {
                    o=tmEstabilizers.getValueAt(i, 0);
                    if (o.getClass()!=ComboItem.class){break;}
                    oCI=(ComboItem) o;
                    oBHAEstabilizer=new BHAEstabilizer();
                    oBHAEstabilizer.setTipoEstabilizerId(new Long(oCI.getValue()));
                    for (int j=1;j<=tmEstabilizers.getColumnCount()-1;j++) {
                        o=tmEstabilizers.getValueAt(i, j);
                        oBHAEstabilizer.setBhaId(bhaId);
                        if (j==1) {
                            oBHAEstabilizer.setPosicion(new Long(o.toString()));
                        }
                        if (j==2) {
                            oBHAEstabilizer.setDistancia(new Double(o.toString()));
                        }
                        if (j==3) {
                            oBHAEstabilizer.setOd(new Double(o.toString()));
                        }                        
                    }
                    if (! oBD.insert(oBHAEstabilizer))huboError=true; 
                }
            }
            
            if (huboError) {
                    msgbox("BHA No Modificada. Error");
                    habilitarControles(false);       
            }else {
                limpiarPantalla();
                mostrarBHA(this.oRunFromMainForm);
                habilitarControles(false); 
                accion=AFTER_UPDATE;
                setearControles();
                msgbox("BHA Modificada Satisfactoriamente");
            }
        }
        
        if (accion==ELIMINAR) {
            bhaId=oBHA.getId();
            oBD.delete(new BHAPipe(), "bhaId="+bhaId);            
            oBD.delete(new BHAEstabilizer(), "bhaId="+bhaId);                        
            if (!huboError) oBD.delete(new DirectionalTool(), "bhaId="+bhaId);            
            oBD.delete(new BHA(), "Id="+bhaId);
                        
            if (oBD.getRegistrsoAfectados()==0) {
                    msgbox("BHA No Eliminada Satisfactoriamente. Hubo Error");
                    habilitarControles(false);       
            }else {
                limpiarPantalla();
                mostrarBHA(this.oRunFromMainForm);
                habilitarControles(false);
                accion=AFTER_DELETE;
                setearControles();
                msgbox("BHA Eliminada Satisfactoriamente");
                super.jButtonAgregar.setVisible(true);
            }
        }
        //oBD.huboCambios=true;
        oBD.setHuboCambios(true);
    }
    
    private void ingresarNuevosItemsEnTablas() {

        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxRSS, this.jComboBoxRSS.getSelectedIndex())) {
            RSS o=new RSS();
            ComboItem oCI=(ComboItem) this.jComboBoxRSS.getSelectedItem();
            o.setNombre(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxRSS);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxRSS);
        }
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxBitType, this.jComboBoxBitType.getSelectedIndex())) {
            BITType o=new BITType();
            ComboItem oCI=(ComboItem) this.jComboBoxBitType.getSelectedItem();
            o.setNombre(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxBitType);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxBitType);
        }
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxBitManufacturer, this.jComboBoxBitManufacturer.getSelectedIndex())) {
            BITManufacturer o=new BITManufacturer();
            ComboItem oCI=(ComboItem) this.jComboBoxBitManufacturer.getSelectedItem();
            o.setNombre(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxBitManufacturer);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxBitManufacturer);
        }
    }
    
    
    private void habilitarControles(boolean accion) {
        this.jButtonMantBitType.setEnabled(accion);
        this.jTablePipes.setEnabled(accion);
        this.jTableEstabilizers.setEnabled(accion);
        this.jTextFieldTFA.setEnabled(accion);
        this.jCheckBoxInFlexLine.setEnabled(accion);
        this.jRadioButtonMotor.setEnabled(accion);
        this.jRadioButtonRSS.setEnabled(accion);
        this.jComboBoxBitType.setEnabled(accion);
        this.jComboBoxBitManufacturer.setEnabled(accion);
        this.jButtonMantBitType.setEnabled(accion);
        this.jButtonMantBitManufacturer.setEnabled(accion);
        this.jTextFieldBitSN.setEnabled(accion);
        this.jComboBoxMotor.setEnabled(false);
        this.jComboBoxRSS.setEnabled(false);
        if (this.jRadioButtonMotor.isSelected()) {
            this.jComboBoxMotor.setEnabled(accion);
            this.jButtonMantMotor.setEnabled(accion);
        }
        if (this.jRadioButtonRSS.isSelected()){
            this.jComboBoxRSS.setEnabled(accion);
            this.jButtonMantRSS.setEnabled(accion);
        }
        this.jCheckBoxAmpliacion.setEnabled(accion);
        this.jComboBoxReamerManufacturer.setEnabled(accion);
        this.jTextFieldReamerModel.setEnabled(accion);
        this.jTextFieldReamerSize.setEnabled(accion);
    }
    
    private void reSize() {
        Dimension d=new Dimension();
        int w=this.getWidth();
        int h=this.jPanelAmpliacion.getHeight();
        if (this.jCheckBoxAmpliacion.isSelected()) {
            d.height=alturaVentana;
            d.width=w;
        } 
        else {
            d.height=alturaVentana-h;
            d.width=w;
        }
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        this.setPreferredSize(d);
        super.jPanel1.setLocation(d.width-MantPpal.ancho, d.height-MantPpal.alto);
        pack();
    }
    
    private void cargarXML(){
        if (confirmBox("Este proceso va a reemplazar la informacion del BHA \n"
                + "de esta corrida con la informacion contenida en el archivo xml.\n"
                + "La informacion BHA previamente cargada se perdera.\n"                
                + "Quiere continuar ?","Confirmar Carga BHA (XML)")==false) {
            return;
        }
        seleccionaProcesarArchivo();
        cursorNormal();
        limpiarPantalla();
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoPipe.class,this.jComboBoxPipes,"");
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoEstabilizer.class,this.jComboBoxEstabilizers,"");        
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoMotor.class,this.jComboBoxMotor);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,RSS.class,this.jComboBoxRSS);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITType.class,this.jComboBoxBitType);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITManufacturer.class,this.jComboBoxBitManufacturer);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITManufacturer.class,this.jComboBoxReamerManufacturer);  
        mostrarBHA(this.oRunFromMainForm);
    }
   
    public void seleccionaProcesarArchivo(){
        DirectionalTool oDT;
        String sWork="";
        BHA oBHA=new BHA();
        TipoPipe oTP=new TipoPipe();
        BHAPipe[] aBHAPipe=new BHAPipe[50] ;
        BHAPipe oBHAPipe=new BHAPipe();
        BHAEstabilizer[] aBHAEstabilizer=new BHAEstabilizer[50] ;
        BHAEstabilizer oBHAEstabilizer=new BHAEstabilizer();
        BITManufacturer oBM=new BITManufacturer();
        BITType oBITType=new BITType();
        TipoMotor oTipoMotor=new TipoMotor();
        TipoRss oTipoRss=new TipoRss();
        RSS oRSS=new RSS();
        DecimalFormat df = new DecimalFormat("#0.000");
        ComboItem o;
        int secuencia=0;
        String vendor="", model="",numSerial="",typeTubularComp="",
               len="",od="",uom="", typeBit="",description="",angleuom="",
               distBladeBot="",odBladeMx="", shapeBlade="",typeBlade="";
        double dblLen=0,dblOd=0, dblAngleuom=0,dblDistBladeBot=0,dblOdBladeMx=0;

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos Xml", new String[] {"xml"});
        fileChooser.setCurrentDirectory(currDirectory);
        fileChooser.setDialogTitle("Escoja el Archivo BHA correspondiente a esta corrida");
        fileChooser.setFileFilter(null);      
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter); 
        int result = fileChooser.showOpenDialog(this);
        currDirectory=fileChooser.getCurrentDirectory();
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
         } else return;
        
        cursorEspera();
               
        //Se limpia informacion previa
        o=new ComboItem();
        o.setKey("");
        limpiarPantalla();

        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = selectedFile;
        try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement(); //tubulars
            List listTubulars=rootNode.getChildren();
            Element tubular=(Element) listTubulars.get(0);
            List listTubular=tubular.getChildren();
            
            //Primero veo si es MOTOR o RSS en la secuencia (2) que esta en el nodo 5
            Element tubularComponentPrev=(Element) listTubular.get(5);
            List listTubularComponentPrevRows=tubularComponentPrev.getChildren();
            Element tubularComponentPrevRow=(Element) listTubularComponentPrevRows.get(0);
            if ("motor".equals(tubularComponentPrevRow.getValue())) {
                oBHA.setTipoDT("MOTOR");
            } 
            if ("rotary steering tool".equals(tubularComponentPrevRow.getValue())) {
                oBHA.setTipoDT("RSS");
            }

            for (int i=0;i<=listTubular.size()-1;i++) {
                Element tubularComponent=(Element) listTubular.get(i);
                if ("tubularComponent".equals(tubularComponent.getName())) {
                   List listTubularComponentRows=tubularComponent.getChildren();
                   for (int j=0;j<=listTubularComponentRows.size()-1;j++) {
                       Element tubularComponentRow=(Element) listTubularComponentRows.get(j);
                       String s=tubularComponentRow.getName();
                       if ("typeTubularComp".equals(tubularComponentRow.getName())) {
                           typeTubularComp=tubularComponentRow.getContent().get(0).getValue();
                       }
                       if ("description".equals(tubularComponentRow.getName())) {
                           description=tubularComponentRow.getContent().get(0).getValue();
                       }
                       if ("bend".equals(tubularComponentRow.getName())) {
                          for (int k=0;k<=tubularComponentRow.getContent().size()-1;k++) {
                              Element e=(Element) tubularComponentRow.getContent().get(k);
                              if ("angle".equals(e.getName())) {
                                  angleuom=e.getValue();
                                  dblAngleuom=new Double(angleuom)*57.2958;
                              }
                          }
                       }                       
                       if ("sequence".equals(tubularComponentRow.getName())) {
                           secuencia=Integer.parseInt(tubularComponentRow.getContent().get(0).getValue());
                       }
                       if ("vendor".equals(tubularComponentRow.getName())) {
                           vendor=tubularComponentRow.getContent().get(0).getValue();
                       }
                       if ("model".equals(tubularComponentRow.getName())) {
                           model=tubularComponentRow.getContent().get(0).getValue();
                       }
                       if ("len".equals(tubularComponentRow.getName())) {
                           len=tubularComponentRow.getContent().get(0).getValue();
                           uom=tubularComponentRow.getAttributes().get(0).getValue();
                           dblLen=Double.parseDouble(len);
                           if ("m".equals(uom)) {
                              dblLen*=3.281; 
                           }
                           len=df.format(round(dblLen,3));
                           dblLen=Double.parseDouble(len);
                       }
                       if ("od".equals(tubularComponentRow.getName())) {
                           od=tubularComponentRow.getContent().get(0).getValue();
                           uom=tubularComponentRow.getAttributes().get(0).getValue();
                           dblOd=Double.parseDouble(od);
                           if ("m".equals(uom)) {
                              dblOd*=3.281; 
                           }
                           od=df.format(round(dblOd,3));
                           dblOd=Double.parseDouble(od);
                       }
                       if ("customData".equals(tubularComponentRow.getName())) {
                          for (int k=0;k<=tubularComponentRow.getContent().size()-1;k++) {
                              Element e=(Element) tubularComponentRow.getContent().get(k);
                              if ("numSerial".equals(e.getName())) {
                                  numSerial=e.getValue();
                              }
                          }
                       }
                       if ("bitRecord".equals(tubularComponentRow.getName())) {
                          for (int k=0;k<=tubularComponentRow.getContent().size()-1;k++) {
                              Element e=(Element) tubularComponentRow.getContent().get(k);
                              if ("typeBit".equals(e.getName())) {
                                  typeBit=e.getValue();
                              }
                          }
                       }
                       if ("stabilizer".equals(tubularComponentRow.getName())) {
                          for (int k=0;k<=tubularComponentRow.getContent().size()-1;k++) {
                              Element e=(Element) tubularComponentRow.getContent().get(k);
                              if ("distBladeBot".equals(e.getName())) {
                                  distBladeBot =e.getValue();
                                  uom=e.getAttributes().get(0).getValue();
                                  dblDistBladeBot=new Double(distBladeBot);
                                  if ("m".equals(uom))
                                    dblDistBladeBot*=3.281;
                                  distBladeBot=df.format(round(dblDistBladeBot,3));
                                  dblDistBladeBot=new Double(distBladeBot);
                              }
                              if ("odBladeMx".equals(e.getName())) {
                                  odBladeMx =e.getValue();
                                  uom=e.getAttributes().get(0).getValue();
                                  dblOdBladeMx=new Double(odBladeMx);
                                  if ("m".equals(uom))
                                    dblOdBladeMx*=3.281;    
                                  odBladeMx=df.format(round(dblOdBladeMx,3));
                                  dblOdBladeMx=new Double(odBladeMx);
                              }
                              if ("shapeBlade".equals(e.getName())) {
                                  shapeBlade=e.getValue();
                              }
                              if ("typeBlade".equals(e.getName())) {
                                  typeBlade=e.getValue();
                              }
                          }
                          oBHAEstabilizer=new BHAEstabilizer();
                          oBHAEstabilizer.setDistancia(dblDistBladeBot);
                          oBHAEstabilizer.setOd(dblOdBladeMx);
                          oBHAEstabilizer.setPosicion(secuencia);
                          String sTipoEstabilizerNombre=oBHA.getTipoDT()+" "+shapeBlade+" "+typeBlade;
                          TipoEstabilizer oTipoEstabilizer=new TipoEstabilizer();
                          if (oBD.getRowCount("SELECT Id FROM TipoEstabilizer WHERE nombre='"+sTipoEstabilizerNombre+"'")==0){
                             oTipoEstabilizer.setNombre(sTipoEstabilizerNombre);
                             oBD.insert(oTipoEstabilizer);
                          }
                          try {
                              oTipoEstabilizer=(TipoEstabilizer) oBD.select(TipoEstabilizer.class, "nombre='"+sTipoEstabilizerNombre+"'")[0];
                          } catch (InstantiationException | IllegalAccessException ex) {
                              Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                          }
                          oBHAEstabilizer.setTipoEstabilizerId(oTipoEstabilizer.getId());
                          for (int k=0;k<=aBHAEstabilizer.length-1;k++) {
                              if (aBHAEstabilizer[k]==null) {
                                  aBHAEstabilizer[k]=oBHAEstabilizer;
                                  break;
                              }
                          }
                       }
                   }

                   if (secuencia==1){
                       
                       oBHA.setSerialNumber(numSerial);
                       
                       if (oBD.getRowCount("SELECT id FROM BITManufacturer WHERE nombre='"+vendor+"'")==0){
                           oBM=new BITManufacturer();
                           oBM.setNombre(vendor);
                           oBD.insert(oBM);
                       }
                       try {
                           oBM=(BITManufacturer) oBD.select(BITManufacturer.class, "nombre='"+vendor+"'")[0];
                       } catch (InstantiationException | IllegalAccessException ex) {
                           Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                       } 
                       sWork=typeTubularComp+" ("+model+")";
                       if (oBD.getRowCount("SELECT id FROM BITType WHERE nombre='"+sWork+"'")==0){
                           oBITType=new BITType();
                           oBITType.setNombre(sWork);
                           oBITType.setModel(model);
                           oBITType.setEsRollerCone("roller cone".equals(typeBit));
                           oBITType.setEsPDC("PDC".equals(typeBit));
                           oBITType.setCantBlades(0);
                           oBITType.setCuttersSize(0);                           
                           oBITType.setGauge(3);
                           oBITType.setEsMilledTooth(oBITType.getNombre().indexOf("mill tooth")>-1);
                           oBITType.setEsTCI(oBITType.getNombre().indexOf("carbide insert")>-1);
                           oBD.insert(oBITType);
                       }
                       try {
                           oBITType=(BITType) oBD.select(BITType.class, "nombre='"+sWork+"'")[0];
                       } catch (InstantiationException | IllegalAccessException ex) {
                           Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                       }
                       oBHA.setBitManufacturerId(oBM.getId());
                       oBHA.setBitTypeId(oBITType.getId());
                       oBHA.setRunId(this.oRunFromMainForm.getId()); 
                       
                       if(oBD.getRowCount("SELECT * FROM BHA WHERE runId="+this.oRunFromMainForm.getId())==0){
                          oBD.insert(oBHA);
                       }else {
                          oBD.update(oBHA, "runId="+this.oRunFromMainForm.getId());
                       }
                       try {
                           if (oBD.select(BHA.class, "runId="+this.oRunFromMainForm.getId()).length>0) {
                               oBHA=(BHA) oBD.select(BHA.class, "runId="+this.oRunFromMainForm.getId())[0];
                           }
                       } catch (InstantiationException | IllegalAccessException | ArrayIndexOutOfBoundsException ex) {
                           Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                       } 
                    }
                   
                    if (secuencia==2){
                        if ("MOTOR".equals(oBHA.getTipoDT())) {
                            if (oBD.getRowCount("SELECT * FROM TipoMotor WHERE nombre='"+description+"'")==0){
                              oTipoMotor=new TipoMotor();
                              oTipoMotor.setNombre(description);
                              oTipoMotor.setMotorType(description.substring(0, 1));
                              oTipoMotor.setMotorSize(new Long(description.substring(1, 4)));
                              oTipoMotor.setBearingSection(description.substring(4, 5));
                              oTipoMotor.setPWSRelation(new Long(description.substring(5, 7)));
                              oTipoMotor.setPWSStages(new Long(description.substring(7, 9)));
                              oTipoMotor.setPWSType(description.substring(9, 11));
                              oTipoMotor.setBendHousingAngle(dblAngleuom);
                              oTipoMotor.setElastomer("RM100");
                              oBD.insert(oTipoMotor);                              
                            }
                            try {
                                if (oBD.select(TipoMotor.class, "nombre='"+description+"'").length>0) {
                                    oTipoMotor=(TipoMotor) oBD.select(TipoMotor.class, "nombre='"+description+"'")[0];
                                }
                            } catch (InstantiationException | IllegalAccessException | ArrayIndexOutOfBoundsException ex) {
                                Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                            }                            
                        }
                        if ("RSS".equals(oBHA.getTipoDT())) {
                            if (oBD.getRowCount("SELECT * FROM RSS WHERE nombre='"+model+"'")==0){
                                oRSS=new RSS();
                                oRSS.setNombre(model);
                                if (oBD.getRowCount("SELECT Id FROM TipoRSS WHERE nombre='"+model+"'")==0){
                                   oTipoRss=new TipoRss();
                                   oTipoRss.setNombre(model);
                                   oBD.insert(oTipoRss);
                                }
                                try {
                                    if (oBD.select(TipoRss.class, "nombre='"+model+"'").length>0) {
                                        oTipoRss=(TipoRss) oBD.select(TipoRss.class, "nombre='"+model+"'")[0];
                                    }
                                } catch (InstantiationException | IllegalAccessException | ArrayIndexOutOfBoundsException ex) {
                                    Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                                } 
                                oRSS.setTipoRssId(oTipoRss.getId());
                                oBD.insert(oRSS);
                            }
                        }
                        try {
                            if (oBD.select(RSS.class, "nombre='"+model+"'").length>0) {
                                oRSS=(RSS) oBD.select(RSS.class, "nombre='"+model+"'")[0];
                            }
                        } catch (InstantiationException | IllegalAccessException | ArrayIndexOutOfBoundsException ex) {
                            Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                        }                        
                    }
                                          
                   if (oBD.getRowCount("SELECT id FROM TipoPipe WHERE nombre='"+typeTubularComp+"'")==0){
                       oTP=new TipoPipe();
                       oTP.setNombre(typeTubularComp);
                       oBD.insert(oTP);
                   }
                   try {
                       oTP=(TipoPipe) oBD.select(TipoPipe.class, "nombre='"+typeTubularComp+"'")[0];
                   } catch (InstantiationException | IllegalAccessException ex) {
                       Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
                   }
                   oBHAPipe=new BHAPipe();
                   oBHAPipe.setLongitud(dblLen);
                   oBHAPipe.setOd(dblOd);
                   oBHAPipe.setSecuencia(secuencia);
                   oBHAPipe.setTipoPipeId(oTP.getId());
                   aBHAPipe[secuencia]=oBHAPipe;
                }
            }
            
            int dummy=0;
            
            oBD.delete(new BHAPipe(), "BHAId="+oBHA.getId());
            for (int k=1;k<=secuencia;k++) {
                oBHAPipe=aBHAPipe[k];
                oBHAPipe.setBhaId(oBHA.getId());
                oBD.insert(oBHAPipe);
            } 
            oBD.delete(new BHAEstabilizer(), "BHAId="+oBHA.getId());
            for (int k=0;k<=aBHAEstabilizer.length-1;k++) {
                oBHAEstabilizer=aBHAEstabilizer[k];
                if (oBHAEstabilizer==null) break;        
                oBHAEstabilizer.setBhaId(oBHA.getId());
                oBD.insert(oBHAEstabilizer);
            }
            
            oDT=new DirectionalTool();
            oDT.setBhaId(oBHA.getId());
            oDT.setMotor("MOTOR".equals(oBHA.getTipoDT()));
            oDT.setRss("RSS".equals(oBHA.getTipoDT()));
            oDT.setTipoMotorId(oTipoMotor.getId());
            oDT.setRssId(oRSS.getId());
            oBD.delete(oDT, "BHAId="+oBHA.getId());
            oBD.insert(oDT);
                   
        }catch ( JDOMException jdomex ) {
            System.out.println( jdomex.getMessage() );
        } catch (IOException ex) {
            Logger.getLogger(MantBHA.class.getName()).log(Level.SEVERE, null, ex);
        }
        cursorNormal();
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    public boolean confirmBox(String s, String t) {
        boolean r=false;
        int i=-1;
        i=JOptionPane.showConfirmDialog(null,s,t,JOptionPane.YES_NO_OPTION);
        if (i==JOptionPane.YES_OPTION) r=true;
        return r;
    }
    
    private void cursorNormal(){
        this.setCursor(Cursor.getDefaultCursor());
    }
    
    private void cursorEspera(){
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableEstabilizers = new javax.swing.JTable();
        jLabelDescContenido = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTablePipes = new javax.swing.JTable();
        jPanelDirectionalTools = new javax.swing.JPanel();
        jComboBoxRSS = new javax.swing.JComboBox();
        jButtonMantRSS = new javax.swing.JButton();
        jComboBoxMotor = new javax.swing.JComboBox();
        jButtonMantMotor = new javax.swing.JButton();
        jRadioButtonMotor = new javax.swing.JRadioButton();
        jRadioButtonRSS = new javax.swing.JRadioButton();
        jRadioButtonDummy = new javax.swing.JRadioButton();
        jLabelBendHousingAngle = new javax.swing.JLabel();
        jCheckBoxInFlexLine = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jComboBoxBitManufacturer = new javax.swing.JComboBox();
        jComboBoxBitType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldBitManufacturerNombre = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButtonMantBitType = new javax.swing.JButton();
        jButtonMantBitManufacturer = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButtonMantBitManufacturerOK = new javax.swing.JButton();
        jButtonMantBitManufacturerCancel = new javax.swing.JButton();
        jTextFieldBitSN = new javax.swing.JTextField();
        jLabelCargarXML = new javax.swing.JLabel();
        jCheckBoxAmpliacion = new javax.swing.JCheckBox();
        jPanelAmpliacion = new javax.swing.JPanel();
        jTextFieldReamerSize = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldReamerModel = new javax.swing.JTextField();
        jComboBoxReamerManufacturer = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldTFA = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(800, 650));
        setModal(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTableEstabilizers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Stabilizer", "Posicion", "Distancia", "OD"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEstabilizers.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTableEstabilizers);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 540, 90));

        jLabelDescContenido.setText("Contenido:");
        getContentPane().add(jLabelDescContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 470, 20));

        jTablePipes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Descripcion", "Secuencia", "Longitud", "OD"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablePipes.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTablePipes);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 540, 120));

        jPanelDirectionalTools.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelDirectionalTools.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBoxRSS.setEnabled(false);
        jPanelDirectionalTools.add(jComboBoxRSS, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, 220, -1));

        jButtonMantRSS.setText("jButton1");
        jButtonMantRSS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonMantRSS.setEnabled(false);
        jButtonMantRSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantRSSActionPerformed(evt);
            }
        });
        jPanelDirectionalTools.add(jButtonMantRSS, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 30, -1));

        jComboBoxMotor.setEnabled(false);
        jPanelDirectionalTools.add(jComboBoxMotor, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 220, -1));

        jButtonMantMotor.setText("jButton1");
        jButtonMantMotor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonMantMotor.setEnabled(false);
        jButtonMantMotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantMotorActionPerformed(evt);
            }
        });
        jPanelDirectionalTools.add(jButtonMantMotor, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 30, -1));

        buttonGroup1.add(jRadioButtonMotor);
        jRadioButtonMotor.setText("Motor");
        jRadioButtonMotor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMotorActionPerformed(evt);
            }
        });
        jPanelDirectionalTools.add(jRadioButtonMotor, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        buttonGroup1.add(jRadioButtonRSS);
        jRadioButtonRSS.setText("RSS");
        jRadioButtonRSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonRSSActionPerformed(evt);
            }
        });
        jPanelDirectionalTools.add(jRadioButtonRSS, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        buttonGroup1.add(jRadioButtonDummy);
        jRadioButtonDummy.setText("Dummy");
        jPanelDirectionalTools.add(jRadioButtonDummy, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 70, -1, -1));

        jLabelBendHousingAngle.setText("Bend Housing Angle:");
        jLabelBendHousingAngle.setEnabled(false);
        jPanelDirectionalTools.add(jLabelBendHousingAngle, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 170, 20));

        getContentPane().add(jPanelDirectionalTools, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 410, 580, 100));

        jCheckBoxInFlexLine.setText("In Flex Line");
        jCheckBoxInFlexLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxInFlexLineActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBoxInFlexLine, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 380, -1, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBoxBitManufacturer.setEnabled(false);
        jPanel1.add(jComboBoxBitManufacturer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 250, -1));

        jComboBoxBitType.setEnabled(false);
        jPanel1.add(jComboBoxBitType, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 250, -1));

        jLabel4.setText("BIT Type");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 50, -1));

        jTextFieldBitManufacturerNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBitManufacturerNombreActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldBitManufacturerNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 70, 220, -1));

        jLabel5.setText("Manufacturer");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 70, -1));

        jButtonMantBitType.setText("...");
        jButtonMantBitType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonMantBitType.setEnabled(false);
        jButtonMantBitType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantBitTypeActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonMantBitType, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, 30, -1));

        jButtonMantBitManufacturer.setText("...");
        jButtonMantBitManufacturer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonMantBitManufacturer.setEnabled(false);
        jButtonMantBitManufacturer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantBitManufacturerActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonMantBitManufacturer, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, 30, -1));

        jLabel6.setText("Serial Number");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, -1));

        jButtonMantBitManufacturerOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantBitManufacturerOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantBitManufacturerOKActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonMantBitManufacturerOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 70, 32, -1));

        jButtonMantBitManufacturerCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantBitManufacturerCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantBitManufacturerCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonMantBitManufacturerCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 70, 32, -1));

        jTextFieldBitSN.setEnabled(false);
        jTextFieldBitSN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldBitSNActionPerformed(evt);
            }
        });
        jPanel1.add(jTextFieldBitSN, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, -1));

        jLabelCargarXML.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelCargarXML.setText("Cargar BHA desde archivo XML");
        jLabelCargarXML.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelCargarXML.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelCargarXMLMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelCargarXMLMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelCargarXMLMouseExited(evt);
            }
        });
        jPanel1.add(jLabelCargarXML, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, 220, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 720, 100));

        jCheckBoxAmpliacion.setText("Ampliacion");
        jCheckBoxAmpliacion.setEnabled(false);
        jCheckBoxAmpliacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAmpliacionActionPerformed(evt);
            }
        });
        getContentPane().add(jCheckBoxAmpliacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 380, -1, -1));

        jPanelAmpliacion.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelAmpliacion.setMaximumSize(new java.awt.Dimension(420, 90));
        jPanelAmpliacion.setMinimumSize(new java.awt.Dimension(420, 90));
        jPanelAmpliacion.setPreferredSize(new java.awt.Dimension(420, 90));
        jPanelAmpliacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldReamerSize.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldReamerSize.setEnabled(false);
        jTextFieldReamerSize.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldReamerSizeKeyTyped(evt);
            }
        });
        jPanelAmpliacion.add(jTextFieldReamerSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 60, -1));

        jLabel1.setText("Model:");
        jPanelAmpliacion.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jLabel3.setText(" Manufacturer:");
        jPanelAmpliacion.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, -1, -1));

        jTextFieldReamerModel.setEnabled(false);
        jTextFieldReamerModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldReamerModelActionPerformed(evt);
            }
        });
        jPanelAmpliacion.add(jTextFieldReamerModel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 100, -1));

        jComboBoxReamerManufacturer.setEnabled(false);
        jPanelAmpliacion.add(jComboBoxReamerManufacturer, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 260, -1));

        jLabel7.setText("Reamer:");
        jPanelAmpliacion.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jLabel8.setText(" Size:");
        jPanelAmpliacion.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, -1));

        getContentPane().add(jPanelAmpliacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 530, 490, 90));

        jTextFieldTFA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldTFA.setEnabled(false);
        jTextFieldTFA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldTFAKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldTFA, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 380, 80, -1));

        jLabel2.setText("TFA:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonMantBitTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantBitTypeActionPerformed
        MantBitType j=new MantBitType(parent,true);
        j.setLocationRelativeTo(this);
        j.setParam(oBD);
        j.setVisible(true);
        long l=oManejoDeCombos.getComboID(jComboBoxBitType);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,BITType.class,this.jComboBoxBitType);
        oManejoDeCombos.setCombo(l, jComboBoxBitType);
    }//GEN-LAST:event_jButtonMantBitTypeActionPerformed

    private void jRadioButtonRSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonRSSActionPerformed
        if (MantBHA.this.jRadioButtonRSS.isSelected()) {
            MantBHA.this.jComboBoxRSS.setEnabled(true);
            MantBHA.this.jButtonMantRSS.setEnabled(true);
            MantBHA.this.jComboBoxMotor.setSelectedIndex(0);
        }
        else
        {
            MantBHA.this.jComboBoxRSS.setEnabled(false);
            MantBHA.this.jButtonMantRSS.setEnabled(false);
        }
        if (MantBHA.this.jRadioButtonMotor.isSelected()) {
            MantBHA.this.jComboBoxMotor.setEnabled(true);
            MantBHA.this.jButtonMantMotor.setEnabled(true);
            MantBHA.this.jComboBoxRSS.setSelectedIndex(0);
        }
        else
        {
            MantBHA.this.jComboBoxMotor.setEnabled(false);
            MantBHA.this.jButtonMantMotor.setEnabled(false);
        }
    }//GEN-LAST:event_jRadioButtonRSSActionPerformed

    private void jRadioButtonMotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMotorActionPerformed
        if (MantBHA.this.jRadioButtonMotor.isSelected()) {
            MantBHA.this.jComboBoxMotor.setEnabled(true);
            MantBHA.this.jButtonMantMotor.setEnabled(true);
            MantBHA.this.jComboBoxRSS.setSelectedIndex(0);
        }
        else
        {
            MantBHA.this.jComboBoxMotor.setEnabled(false);
            MantBHA.this.jButtonMantMotor.setEnabled(false);
        }
        if (MantBHA.this.jRadioButtonRSS.isSelected()) {
            MantBHA.this.jComboBoxRSS.setEnabled(true);
            MantBHA.this.jButtonMantRSS.setEnabled(true);
            MantBHA.this.jComboBoxMotor.setSelectedIndex(0);
        }
        else
        {
            MantBHA.this.jComboBoxRSS.setEnabled(false);
            MantBHA.this.jButtonMantRSS.setEnabled(false);
        }
    }//GEN-LAST:event_jRadioButtonMotorActionPerformed
  
    private void jButtonMantMotorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantMotorActionPerformed
        MantMotores j=new MantMotores(parent,true);
        j.setLocationRelativeTo(this);
        j.setParam(oBD);
        j.setVisible(true);
        long l=oManejoDeCombos.getComboID(jComboBoxMotor);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoMotor.class,this.jComboBoxMotor);
        oManejoDeCombos.setCombo(l, jComboBoxMotor);
    }//GEN-LAST:event_jButtonMantMotorActionPerformed

    private void jButtonMantRSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantRSSActionPerformed
        MantRss j=new MantRss(parent,true);
        j.setLocationRelativeTo(this);
        j.setParam(oBD);
        j.setVisible(true);
        long l=oManejoDeCombos.getComboID(jComboBoxRSS);
        oManejoDeCombos.llenaCombo(oBD,modeloCombo,RSS.class,this.jComboBoxRSS);
        oManejoDeCombos.setCombo(l, jComboBoxRSS);
    }//GEN-LAST:event_jButtonMantRSSActionPerformed

    private void jTextFieldBitManufacturerNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBitManufacturerNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBitManufacturerNombreActionPerformed

    private void jButtonMantBitManufacturerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantBitManufacturerActionPerformed
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantBitManufacturer.setEnabled(false);
        this.jTextFieldBitManufacturerNombre.setEnabled(true);
        this.jTextFieldBitManufacturerNombre.setVisible(true);
        this.jButtonMantBitManufacturerOK.setVisible(true);
        this.jButtonMantBitManufacturerCancel.setVisible(true);
    }//GEN-LAST:event_jButtonMantBitManufacturerActionPerformed

    private void jButtonMantBitManufacturerOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantBitManufacturerOKActionPerformed
      oManejoDeCombos.ingresarAlComboBox(this.jTextFieldBitManufacturerNombre,this.jComboBoxBitManufacturer);  
      habilitarControles(true);
      super.jButtonCancelar.setEnabled(true);
      super.jButtonProcesar.setEnabled(true);
      this.jButtonMantBitManufacturer.setEnabled(true);
      this.jComboBoxBitManufacturer.setEnabled(true); 
      this.jTextFieldBitManufacturerNombre.setEnabled(false);
      this.jTextFieldBitManufacturerNombre.setVisible(false);
      this.jTextFieldBitManufacturerNombre.setText("");
      this.jButtonMantBitManufacturerOK.setVisible(false);
      this.jButtonMantBitManufacturerCancel.setVisible(false);
    }//GEN-LAST:event_jButtonMantBitManufacturerOKActionPerformed

    private void jButtonMantBitManufacturerCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantBitManufacturerCancelActionPerformed
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantBitManufacturer.setEnabled(true);
        this.jComboBoxBitManufacturer.setEnabled(true);
        this.jTextFieldBitManufacturerNombre.setEnabled(false);
        this.jTextFieldBitManufacturerNombre.setVisible(false);
        this.jTextFieldBitManufacturerNombre.setText("");
        this.jButtonMantBitManufacturerOK.setVisible(false);
        this.jButtonMantBitManufacturerCancel.setVisible(false);
    }//GEN-LAST:event_jButtonMantBitManufacturerCancelActionPerformed

    private void jTextFieldBitSNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBitSNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBitSNActionPerformed

    private void jTextFieldReamerSizeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldReamerSizeKeyTyped
        soloDobles(evt);
    }//GEN-LAST:event_jTextFieldReamerSizeKeyTyped

    private void jTextFieldTFAKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldTFAKeyTyped
        soloDobles(evt);
    }//GEN-LAST:event_jTextFieldTFAKeyTyped

    private void jCheckBoxInFlexLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxInFlexLineActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxInFlexLineActionPerformed

    private void jTextFieldReamerModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldReamerModelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldReamerModelActionPerformed

    private void jCheckBoxAmpliacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAmpliacionActionPerformed
        if (jCheckBoxAmpliacion.isSelected()) {
            this.jPanelAmpliacion.setVisible(true);
        } else
        {
          this.jPanelAmpliacion.setVisible(false);
          this.jTextFieldReamerSize.setText("0.0");
          this.jTextFieldReamerModel.setText("");
          this.jComboBoxReamerManufacturer.setSelectedIndex(0);
        }
        reSize();
    }//GEN-LAST:event_jCheckBoxAmpliacionActionPerformed

    private void jLabelCargarXMLMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCargarXMLMouseEntered
        jLabelCargarXML.setForeground(Color.BLUE);
    }//GEN-LAST:event_jLabelCargarXMLMouseEntered

    private void jLabelCargarXMLMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCargarXMLMouseExited
        jLabelCargarXML.setForeground(Color.BLACK);
    }//GEN-LAST:event_jLabelCargarXMLMouseExited

    private void jLabelCargarXMLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCargarXMLMouseClicked
        cargarXML();
    }//GEN-LAST:event_jLabelCargarXMLMouseClicked

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
            java.util.logging.Logger.getLogger(MantBHA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantBHA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantBHA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantBHA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantBHA dialog = new MantBHA(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButtonMantBitManufacturer;
    private javax.swing.JButton jButtonMantBitManufacturerCancel;
    private javax.swing.JButton jButtonMantBitManufacturerOK;
    private javax.swing.JButton jButtonMantBitType;
    private javax.swing.JButton jButtonMantMotor;
    private javax.swing.JButton jButtonMantRSS;
    private javax.swing.JCheckBox jCheckBoxAmpliacion;
    private javax.swing.JCheckBox jCheckBoxInFlexLine;
    private javax.swing.JComboBox jComboBoxBitManufacturer;
    private javax.swing.JComboBox jComboBoxBitType;
    private javax.swing.JComboBox jComboBoxMotor;
    private javax.swing.JComboBox jComboBoxRSS;
    private javax.swing.JComboBox jComboBoxReamerManufacturer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelBendHousingAngle;
    private javax.swing.JLabel jLabelCargarXML;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelAmpliacion;
    private javax.swing.JPanel jPanelDirectionalTools;
    private javax.swing.JRadioButton jRadioButtonDummy;
    private javax.swing.JRadioButton jRadioButtonMotor;
    private javax.swing.JRadioButton jRadioButtonRSS;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableEstabilizers;
    private javax.swing.JTable jTablePipes;
    private javax.swing.JTextField jTextFieldBitManufacturerNombre;
    private javax.swing.JTextField jTextFieldBitSN;
    private javax.swing.JTextField jTextFieldReamerModel;
    private javax.swing.JTextField jTextFieldReamerSize;
    private javax.swing.JTextField jTextFieldTFA;
    // End of variables declaration//GEN-END:variables
}

class ColorColumnRenderer extends DefaultTableCellRenderer
{
   Color bkgndColor, fgndColor;
     
   public ColorColumnRenderer(Color bkgnd, Color foregnd) {
      super();
      bkgndColor = bkgnd;
      fgndColor = foregnd;
   }
     
   public Component getTableCellRendererComponent
        (JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column)
   {
      Component cell = super.getTableCellRendererComponent
         (table, value, isSelected, hasFocus, row, column);
  
      cell.setBackground( bkgndColor );
      cell.setForeground( fgndColor );
      
      return cell;
   }
}