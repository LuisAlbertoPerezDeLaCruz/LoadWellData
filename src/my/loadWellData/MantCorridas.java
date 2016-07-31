/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import miLibreria.*;
import miLibreria.bd.*;
import miLibreria.ManejoDeCombos;


/**
 *
 * @author USUARIO
 */
public class MantCorridas extends MantPpal {
    public String sDescNodo;
    public ManejoBDI oBD;
    public Sections oSections;
    private Run oRun;
    static DefaultListModel<ClaveDesc> modeloLista;   
    static DefaultComboBoxModel<ComboItem> modeloCombo;
    private long ultimoNumeroIdentificador;
    private final javax.swing.JComboBox jComboBoxServicios = new javax.swing.JComboBox();
    private final javax.swing.JComboBox jComboBoxPegas = new javax.swing.JComboBox();
    private final javax.swing.JComboBox jComboBoxSubSections = new javax.swing.JComboBox();
    private boolean firstTime=true;
    public long currentPozoId;
    private ManejoDeCombos oManejoDeCombos=new ManejoDeCombos();
    
    /**
     * Creates new form MantCorridas
     */
    public MantCorridas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void setParam(ManejoBDI o) {
        try {
            ResultSet rs;
            String s,s1;
            ClaveDesc ocd;
            modeloLista = new DefaultListModel<>();
            this.jListExistentes.setModel(modeloLista);
            this.jListExistentes.repaint();
            oBD=o;
            s=sDescNodo.substring(sDescNodo.indexOf('#')+2);
            oSections=(Sections) oBD.select(Sections.class, "wellId=" + currentPozoId + " AND numeroIdentificador='"+s+"'")[0];
            super.jPanel1.setLocation(this.getWidth()-MantPpal.ancho, this.getHeight()-MantPpal.alto);

            this.jButtonMantReasonForPOOHOK.setVisible(false);
            this.jButtonMantReasonForPOOHCancel.setVisible(false);
            this.jTextFieldReasonForPOOHNombre.setVisible(false);
            
            this.jLabelDescContenido.setText("Seccion: #" + oSections.getNumeroIdentificador());
            s1="SELECT * from ConsultaRun1 WHERE sectionId="+oSections.getId()+" ORDER BY numeroIdentificador Asc;";            
            rs=oBD.select(s1);
            
            while (rs.next()){
                ultimoNumeroIdentificador=new Long(rs.getString("numeroIdentificador"));
                s1="#"+rs.getString("numeroIdentificador");
                s1+=" ( " + rs.getDate("fechaHoraComienzo");
                s1+="  " + rs.getTime("fechaHoraComienzo");
                s1+=").";
                s1+=" Responsable: " + rs.getString("empresaResponsableNombre");
                ocd=new ClaveDesc();
                ocd.setClave(rs.getLong("Id"));
                ocd.setDesc(s1);
                modeloLista.addElement(ocd);
            } 
            
            oManejoDeCombos.llenaCombo(oBD,modeloCombo,EmpresaResponsable.class,this.jComboBoxEmpresaResponsable); 
            oManejoDeCombos.llenaCombo(oBD,modeloCombo,ReasonForPOOH.class,this.jComboBoxReasonForPOOH); 
              
            this.jTableServicios.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
            this.jTableServicios.getColumnModel().getColumn(1).setPreferredWidth(300);
            this.jTableServicios.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));

            for (int i=0;i<=this.jTableServicios.getRowCount()-1;i++) {
                for (int j=0;j<=this.jTableServicios.getColumnCount()-1;j++) {
                    MantCorridas.this.jTableServicios.setValueAt("", i, j);                
                }
            } 
            
            oManejoDeCombos.llenaCombo(oBD,modeloCombo,Servicio.class,this.jComboBoxServicios,"");
            
            this.jTablePegas.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
            this.jTablePegas.getColumnModel().getColumn(1).setPreferredWidth(300);
            this.jTablePegas.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
            this.jTablePegas.getColumnModel().getColumn(2).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));

            for (int i=0;i<=this.jTablePegas.getRowCount()-1;i++) {
                for (int j=0;j<=this.jTablePegas.getColumnCount()-1;j++) {
                    MantCorridas.this.jTablePegas.setValueAt("", i, j);                
                }
            } 
            
            oManejoDeCombos.llenaCombo(oBD,modeloCombo,TipoMecanismoPega.class,this.jComboBoxPegas,"");
            
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(MantCorridas.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        jComboBoxServicios.setEnabled(true);
        jComboBoxServicios.setVisible(true);
        this.jTableServicios.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jComboBoxServicios));
        
        jComboBoxPegas.setEnabled(true);
        jComboBoxPegas.setVisible(true);
        this.jTablePegas.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jComboBoxPegas));
        
        if (firstTime) {
            
            this.jTableServicios.getModel().addTableModelListener(
            new TableModelListener() 
            {
                int l=0;
                int row=0;
                int col=0;
                Object o;
                int rowCount=MantCorridas.this.jTableServicios.getRowCount();
                int colCount=MantCorridas.this.jTableServicios.getColumnCount();
                Object[][] a=new Object[rowCount][colCount];
                boolean cambiando=false;
                int j=0;
                int colCombo=1;
                int colPos=0;

                @Override
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

                    if (col==colCombo) {
                        for (int i=0;i<=rowCount-1;i++) {
                            o=MantCorridas.this.jTableServicios.getValueAt(i, colCombo);
                            if (o.getClass()==ComboItem.class){
                                oCI=(ComboItem) o;                        
                                if (oCI.getKey() !="" && oCI.getKey() !=null) {
                                    try {
                                        for (j=0;j<=colCount-1;j++) {
                                            a[l][j]=MantCorridas.this.jTableServicios.getValueAt(i, j);
                                        }
                                        a[l][colPos]=""+(l+1);
                                        l++;
                                    }
                                    catch (NullPointerException ex){a[l][j]="";} 
                                } 
                            }
                        }                   
                        for (int i=0;i<rowCount;i++) {
                            for (int j=0;j<=colCount-1;j++) {
                                cambiando=true;
                                MantCorridas.this.jTableServicios.setValueAt(a[i][j], i, j);
                            }
                        }
                        cambiando=false;
                    }                
                }
            });            
            
            this.jTablePegas.getModel().addTableModelListener(
            new TableModelListener() 
            {
                int l=0;
                int row=0;
                int col=0;
                Object o;
                int rowCount=MantCorridas.this.jTablePegas.getRowCount();
                int colCount=MantCorridas.this.jTablePegas.getColumnCount();
                Object[][] a=new Object[rowCount][colCount];
                boolean cambiando=false;
                int j=0;
                int colCombo=1;
                int colPos=0;

                @Override
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

                    if (col==colCombo) {
                        for (int i=0;i<=rowCount-1;i++) {
                            o=MantCorridas.this.jTablePegas.getValueAt(i, colCombo);
                            if (o.getClass()==ComboItem.class){
                                oCI=(ComboItem) o;                        
                                if (oCI.getKey() !="" && oCI.getKey() !=null) {
                                    try {
                                        for (j=0;j<=colCount-1;j++) {
                                            a[l][j]=MantCorridas.this.jTablePegas.getValueAt(i, j);
                                        }
                                        a[l][colPos]=""+(l+1);
                                        l++;
                                    }
                                    catch (NullPointerException ex){a[l][j]="";} 
                                } 
                            }
                        }                   
                        for (int i=0;i<rowCount;i++) {
                            for (int j=0;j<=colCount-1;j++) {
                                cambiando=true;
                                MantCorridas.this.jTablePegas.setValueAt(a[i][j], i, j);
                            }
                        }
                        cambiando=false;
                    }                
                }
            });

            this.jTableSubSections.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
            this.jTableSubSections.getColumnModel().getColumn(1).setPreferredWidth(200);
            this.jTableSubSections.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(Color.LIGHT_GRAY, Color.blue));
            this.jTableSubSections.getColumnModel().getColumn(2).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));
            this.jTableSubSections.getColumnModel().getColumn(3).setCellRenderer(new ColorColumnRenderer(Color.WHITE, Color.blue));

            for (int i=0;i<=this.jTableSubSections.getRowCount()-1;i++) {
                for (int j=0;j<=this.jTableSubSections.getColumnCount()-1;j++) {
                    MantCorridas.this.jTableSubSections.setValueAt("", i, j);                
                }
            } 

            oManejoDeCombos.llenaCombo(oBD,modeloCombo,DrillingSubSectionType.class,this.jComboBoxSubSections,"");
            jComboBoxSubSections.setEnabled(true);
            jComboBoxSubSections.setVisible(true);
            this.jTableSubSections.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jComboBoxSubSections));

            this.jTableSubSections.getModel().addTableModelListener(
            new TableModelListener() 
            {
                int l=0;
                int row=0;
                int col=0;
                Object o;
                int rowCount=MantCorridas.this.jTableSubSections.getRowCount();
                int colCount=MantCorridas.this.jTableSubSections.getColumnCount();
                Object[][] a=new Object[rowCount][colCount];
                boolean cambiando=false;
                int j=0;
                int colCombo=1;
                int colPos=0;

                @Override
                public void tableChanged(TableModelEvent evt) 
                {                

                    calculaProfundidades();

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

                    if (col==colCombo) {
                        for (int i=0;i<=rowCount-1;i++) {
                            o=MantCorridas.this.jTableSubSections.getValueAt(i, colCombo);
                            if (o.getClass()==ComboItem.class){
                                oCI=(ComboItem) o;                        
                                if (oCI.getKey() !="" && oCI.getKey() !=null) {
                                    try {
                                        for (j=0;j<=colCount-1;j++) {
                                            a[l][j]=MantCorridas.this.jTableSubSections.getValueAt(i, j);
                                        }
                                        a[l][colPos]=""+(l+1);
                                        l++;
                                    }
                                    catch (NullPointerException ex){a[l][j]="";} 
                                } 
                            }
                        }

                        for (int i=0;i<rowCount;i++) {
                            for (int j=0;j<=colCount-1;j++) {
                                cambiando=true;
                                MantCorridas.this.jTableSubSections.setValueAt(a[i][j], i, j);
                            }
                        }
                        cambiando=false;
                    }                
                }
            });
            firstTime=false;
        }        
    }
    
    public void procesar() {
        String s="";
        String sw="";
        long empresaResponsableId=0;
        long reasonForPOOHId=0;
        Double initialDepth=0.0;
        Double finalDepth=0.0; 
        Double brt=0.0;
        TableModel tmTipoMecanismoPega;
        TableModel tmServicios;
        TableModel tmDrillingSubSectionType;
        ComboItem oCI;
        RunTipoMecanismoPega oRunTipoMecanismoPega=new RunTipoMecanismoPega();
        RunServicio oRunServicio=new RunServicio();
        RunSubSection oRunSubSection=new RunSubSection();
        RunEstatus oRE=new RunEstatus();
        
        boolean esGYRO=this.jCheckBoxGYRO.isSelected();
        
        calculaProfundidades();
        
        ingresarNuevosItemsEnTablas();
        
        tmTipoMecanismoPega=this.jTablePegas.getModel();
        tmServicios=this.jTableServicios.getModel();
        tmDrillingSubSectionType=this.jTableSubSections.getModel();
        
        if (this.jComboBoxEmpresaResponsable.getSelectedIndex()==0) {
            s+="** Falta Empresa Responsable **\n\r";
        }
        else {
            empresaResponsableId=getComboID(this.jComboBoxEmpresaResponsable);
        }
        
        reasonForPOOHId=getComboID(this.jComboBoxReasonForPOOH);
        
        if (this.jTextFieldInitialDepth.getText().trim().isEmpty()){
            s+="** Falta Initial Depth **\n\r";            
        }
        else {
            initialDepth=oNumeros.valorDouble(this.jTextFieldInitialDepth.getText());
        }
        
        if (this.jTextFieldFinalDepth.getText().trim().isEmpty()){
            s+="** Falta Final Depth **\n\r";            
        }
        else {
            finalDepth=oNumeros.valorDouble(this.jTextFieldFinalDepth.getText());
        }
        
        if (this.jTextFieldBRT.getText().trim().isEmpty()){
            s+="** Falta BRT **\n\r";            
        }
        else {
            brt=oNumeros.valorDouble(this.jTextFieldBRT.getText());
        }
        
        Object o=null;
      
        for (int i=0;i<=tmTipoMecanismoPega.getRowCount()-1;i++) {
            o=tmTipoMecanismoPega.getValueAt(i, 0);
            if (o=="" || o==null) {break;}
          
            for (int j=1;j<=tmTipoMecanismoPega.getColumnCount()-1;j++) {
                o=tmTipoMecanismoPega.getValueAt(i, j);
                if (o=="" || o==null) {
                    s+="** Información incompleta en TipoMecanismoPega, posicion:"+ (i+1) + "**\n\r";
                    break;
                }
            }
        }
        
        for (int i=0;i<=tmDrillingSubSectionType.getRowCount()-1;i++) {
            o=tmDrillingSubSectionType.getValueAt(i, 0);
            if (o=="" || o==null) {break;}
          
            for (int j=1;j<=tmDrillingSubSectionType.getColumnCount()-1;j++) {
                o=tmDrillingSubSectionType.getValueAt(i, j);
                if (o=="" || o==null) {
                    s+="** Información incompleta en Drilling Sub Sections, posicion:"+ (i+1) + "**\n\r";
                    break;
                }
            }
        }
        
        double anterior=0.0, actual=0.0;
        double desde=0.0, hasta=0.0;
        for (int i=0;i<=this.jTableSubSections.getRowCount()-1;i++) {
            if (this.jTableSubSections.getValueAt(i, 0)=="") break;
            desde=(Double) this.jTableSubSections.getValueAt(i, 2);
            hasta=(Double) this.jTableSubSections.getValueAt(i, 3);
            if (i>0) {
                anterior=(Double) this.jTableSubSections.getValueAt(i-1, 3);
                actual=(Double) this.jTableSubSections.getValueAt(i, 2);
            }
            if ((anterior >= actual && actual>0.0) || desde >= hasta) {
                s+="** Error de secuencia en SubSecciones, por favor revise. **";
            }
        }
        
        if (s.length()>0) {
            msgbox(s);
            return;
        }
        
       
        if (sw.length()>0) {
            msgbox(sw);
        }
        
     
        if (accion==AGREGAR) {
            oRun=new Run();
            oRun.setSectionId(oSections.getId());         
            oRun.setEmpresaResponsableId(empresaResponsableId);
            oRun.setReasonForPOOHId(reasonForPOOHId);
            oRun.setNumero(ultimoNumeroIdentificador+1);
            oRun.setFechaHoraComienzo(this.dateTimePickerComienzo.getDate());
            oRun.setFechaHoraFinalizacion(this.dateTimePickerFinalizacion.getDate());
            oRun.setFinalDepth(finalDepth);
            oRun.setInitialDepth(initialDepth);
            oRun.setEsGyro(esGYRO);
            oRun.setBrt(brt);
            oRun.setWellId(currentPozoId);
            oBD.insert(oRun);
            Long runId=oBD.ultimaClave(oRun);
            if (oBD.getRegistrsoAfectados()>0){
                
		for (int i=0;i<=tmTipoMecanismoPega.getRowCount()-1;i++) {
		    o=tmTipoMecanismoPega.getValueAt(i, 1);
		    if (o.getClass()!=ComboItem.class){break;}
		    oCI=(ComboItem) o;
		    oRunTipoMecanismoPega=new RunTipoMecanismoPega();
		    oRunTipoMecanismoPega.setTipoMecanismoPegaId(new Long(oCI.getValue()));
		    for (int j=0;j<=tmTipoMecanismoPega.getColumnCount()-1;j++) {
			o=tmTipoMecanismoPega.getValueAt(i, j);
			oRunTipoMecanismoPega.setRunId(runId);
			if (j==0) {
			    oRunTipoMecanismoPega.setPosicion(new Long(o.toString()));
			}
			if (j==2) {
			    oRunTipoMecanismoPega.setTvd(new Double(o.toString()));
			}                       
		    }
		    oBD.insert(oRunTipoMecanismoPega);
		}
                
		for (int i=0;i<=tmServicios.getRowCount()-1;i++) {
		    o=tmServicios.getValueAt(i, 1);
		    if (o.getClass()!=ComboItem.class){break;}
		    oCI=(ComboItem) o;
		    oRunServicio=new RunServicio();
		    oRunServicio.setServicioId(new Long(oCI.getValue()));
		    oRunServicio.setRunId(runId);                     
		    oBD.insert(oRunServicio);
		}
                
		for (int i=0;i<=tmDrillingSubSectionType.getRowCount()-1;i++) {
		    o=tmDrillingSubSectionType.getValueAt(i, 1);
		    if (o.getClass()!=ComboItem.class){break;}
		    oCI=(ComboItem) o;
		    oRunSubSection=new RunSubSection();
		    oRunSubSection.setDrillingSubSectionId(new Long(oCI.getValue()));
		    for (int j=0;j<=tmDrillingSubSectionType.getColumnCount()-1;j++) {
			o=tmDrillingSubSectionType.getValueAt(i, j);
			oRunSubSection.setRunId(runId);
			if (j==0) {
			    oRunSubSection.setPosicion(new Long(o.toString()));
			}
			if (j==2) {
			    oRunSubSection.setProfundidadInicial(new Double(o.toString()));
			} 
			if (j==3) {
			    oRunSubSection.setProfundidadFinal(new Double(o.toString()));
			}
		    }
		    oBD.insert(oRunSubSection);
		}
                
                oRE.setRunId(runId);
                oBD.insert(oRE);
                
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                this.setParam(oBD);
                super.accion=NADA;
                msgbox("Corrida Incluida exitosamente");
            } else {
                msgbox("Corrida No Incluida. Error");
                habilitarControles(false);            
            }
        }
        
        if (accion==MODIFICAR) {
            long runId=oRun.getId();
            oRun.setEmpresaResponsableId(empresaResponsableId);
            oRun.setReasonForPOOHId(reasonForPOOHId);
            oRun.setFechaHoraComienzo(this.dateTimePickerComienzo.getDate());
            oRun.setFechaHoraFinalizacion(this.dateTimePickerFinalizacion.getDate());
            oRun.setFinalDepth(finalDepth);
            oRun.setInitialDepth(initialDepth);
            oRun.setBrt(brt);
            oRun.setEsGyro(esGYRO);
            oRun.setWellId(currentPozoId);
            oRun.setRequiereUpdate(false);
            oBD.update(oRun,"Id="+runId);
            if (oBD.getRegistrsoAfectados()>0){
                oBD.delete(new RunTipoMecanismoPega() ,"runId="+oRun.getId() );
                oBD.delete(new RunSubSection() ,"runId="+oRun.getId() );
                oBD.delete(new RunServicio(),"runId="+oRun.getId());
		for (int i=0;i<=tmTipoMecanismoPega.getRowCount()-1;i++) {
		    o=tmTipoMecanismoPega.getValueAt(i, 1);
		    if (o.getClass()!=ComboItem.class){break;}
		    oCI=(ComboItem) o;
		    oRunTipoMecanismoPega=new RunTipoMecanismoPega();
		    oRunTipoMecanismoPega.setTipoMecanismoPegaId(new Long(oCI.getValue()));
		    for (int j=0;j<=tmTipoMecanismoPega.getColumnCount()-1;j++) {
			o=tmTipoMecanismoPega.getValueAt(i, j);
			oRunTipoMecanismoPega.setRunId(runId);
			if (j==0) {
			    oRunTipoMecanismoPega.setPosicion(new Long(o.toString()));
			}
			if (j==2) {
			    oRunTipoMecanismoPega.setTvd(new Double(o.toString()));
			}                       
		    }
		    oBD.insert(oRunTipoMecanismoPega); 
		}
                for (int i=0;i<=tmServicios.getRowCount()-1;i++) {
		    o=tmServicios.getValueAt(i, 1);
		    if (o.getClass()!=ComboItem.class){break;}
		    oCI=(ComboItem) o;
		    oRunServicio=new RunServicio();
		    oRunServicio.setServicioId(new Long(oCI.getValue()));
		    oRunServicio.setRunId(runId);                     
		    oBD.insert(oRunServicio);
		}
		for (int i=0;i<=tmDrillingSubSectionType.getRowCount()-1;i++) {
		    o=tmDrillingSubSectionType.getValueAt(i, 1);
		    if (o.getClass()!=ComboItem.class){break;}
		    oCI=(ComboItem) o;
		    oRunSubSection=new RunSubSection();
		    oRunSubSection.setDrillingSubSectionId(new Long(oCI.getValue()));
		    for (int j=0;j<=tmDrillingSubSectionType.getColumnCount()-1;j++) {
			o=tmDrillingSubSectionType.getValueAt(i, j);
			oRunSubSection.setRunId(runId);
			if (j==0) {
			    oRunSubSection.setPosicion(new Long(o.toString()));
			}
			if (j==2) {
			    oRunSubSection.setProfundidadInicial(new Double(o.toString()));
			} 
			if (j==3) {
			    oRunSubSection.setProfundidadFinal(new Double(o.toString()));
			}
		    }
		    oBD.insert(oRunSubSection);
		}
                habilitarControles(false); 
                super.jButtonAgregar.setEnabled(true);
                super.jButtonModificar.setEnabled(false);
                super.jButtonEliminar.setEnabled(false);
                super.jButtonProcesar.setVisible(false);
                super.jButtonCancelar.setVisible(false);
                this.setParam(oBD);
                super.accion=NADA;
                msgbox("Corrida Actualizada exitosamente");
            } else {
                msgbox("Corrida No Actualizada. Error");
                habilitarControles(false);            
            }
        }
        
        if (accion==ELIMINAR) {
            if (msgboxYesNo("Confirma la eliminacion de la corrida y de toda la informacion relacionada ?")) {
                oBD.deleteRun(oRun.getId());
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
                   msgbox("Corrida eliminada exitosamente");            
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
        oManejoDeCombos.setCombo(0,this.jComboBoxEmpresaResponsable);
        oManejoDeCombos.setCombo(0,this.jComboBoxReasonForPOOH);
        this.dateTimePickerComienzo.setDate(new Date());
        this.dateTimePickerFinalizacion.setDate(new Date());
        this.jTextFieldInitialDepth.setText("");
        this.jTextFieldFinalDepth.setText("");
        this.jTextFieldBRT.setText("");        
        for (int i=0;i<=this.jTablePegas.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTablePegas.getColumnCount()-1;j++) {
                    MantCorridas.this.jTablePegas.setValueAt("", i, j);                
            }
        } 
        for (int i=0;i<=this.jTableSubSections.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTableSubSections.getColumnCount()-1;j++) {
                    MantCorridas.this.jTableSubSections.setValueAt("", i, j);                
            }
        }        
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
        this.jComboBoxEmpresaResponsable.setEnabled(accion);
        this.jComboBoxReasonForPOOH.setEnabled(accion);
        this.dateTimePickerComienzo.setEnabled(accion);
        this.dateTimePickerFinalizacion.setEnabled(accion);
        this.jButtonMantReasonForPOOH.setEnabled(accion);
        this.jTextFieldBRT.setEnabled(accion);
        this.jTablePegas.setEnabled(accion);
        this.jTableSubSections.setEnabled(accion);
        this.jTableServicios.setEnabled(accion);
        this.jCheckBoxGYRO.setEnabled(accion);
        this.jListExistentes.setEnabled(!accion);
    }
    
    private void ingresarNuevosItemsEnTablas() {
        if (oManejoDeCombos.esNuevoElComboItem(this.jComboBoxReasonForPOOH, this.jComboBoxReasonForPOOH.getSelectedIndex())) {
            ReasonForPOOH o=new ReasonForPOOH();
            ComboItem oCI=(ComboItem) this.jComboBoxReasonForPOOH.getSelectedItem();
            o.setNombre(oCI.getKey());
            o.setId(new Long(oCI.getValue()));
            oBD.insert(o);
            oManejoDeCombos.llenaCombo(oBD, modeloCombo, o.getClass(), this.jComboBoxReasonForPOOH);
            oManejoDeCombos.setCombo(oBD.ultimaClave(o) , this.jComboBoxReasonForPOOH);
        }        
    }
    
    private void accionClickLista() {
        if (accion!=NADA) return;
        ResultSet rs;
        String s;
        Object[] oArray=null;
        ComboItem oCI=new ComboItem();
        TipoMecanismoPega oTP=new TipoMecanismoPega();
        Servicio oSrv=new Servicio();
        DrillingSubSectionType oDSST=new DrillingSubSectionType();        
        RunTipoMecanismoPega oRunTipoMecanismoPega=new RunTipoMecanismoPega();
        RunSubSection oRunSubSection=new RunSubSection();
        for (int i=0;i<=this.jTablePegas.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTablePegas.getColumnCount()-1;j++) {
                    MantCorridas.this.jTablePegas.setValueAt("", i, j);                
            }
        } 
        
        RunServicio oRunServicio=new RunServicio();
        for (int i=0;i<=this.jTableServicios.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTableServicios.getColumnCount()-1;j++) {
                    MantCorridas.this.jTableServicios.setValueAt("", i, j);                
            }
        }
        
        for (int i=0;i<=this.jTableSubSections.getRowCount()-1;i++) {
            for (int j=0;j<=this.jTableSubSections.getColumnCount()-1;j++) {
                    MantCorridas.this.jTableSubSections.setValueAt("", i, j);                
            }
        }
        try {
            if (accion!=NADA) return;
            ClaveDesc ocd;
            if (modeloLista.isEmpty()) return;
            ocd=modeloLista.getElementAt(this.jListExistentes.getSelectedIndex());
            oRun= (Run) oBD.select(Run.class, "id="+ocd.getClave())[0];
            this.dateTimePickerComienzo.setDate(oRun.getFechaHoraComienzo());
            this.dateTimePickerFinalizacion.setDate(oRun.getFechaHoraFinalizacion());
            super.jButtonModificar.setEnabled(true);  
            super.jButtonEliminar.setEnabled(true);
            oManejoDeCombos.setCombo(oRun.getEmpresaResponsableId(),this.jComboBoxEmpresaResponsable);
            oManejoDeCombos.setCombo(oRun.getReasonForPOOHId(),this.jComboBoxReasonForPOOH);
            this.jTextFieldInitialDepth.setText(""+oRun.getInitialDepth());
            this.jTextFieldFinalDepth.setText(""+oRun.getFinalDepth());
            this.jTextFieldBRT.setText(""+oRun.getBrt());
            oArray=oBD.select(RunTipoMecanismoPega.class, "runId="+oRun.getId());
            for (int i=0;i<=oArray.length-1;i++) {
                    oRunTipoMecanismoPega=(RunTipoMecanismoPega) oArray[i];
                    oTP=(TipoMecanismoPega) oBD.select(TipoMecanismoPega.class,"id="+oRunTipoMecanismoPega.getTipoMecanismoPegaId())[0];
                    oCI=new ComboItem();
                    oCI.setValue(Long.toString(oTP.getId()));
                    oCI.setKey(oTP.getDescripcion());
                    MantCorridas.this.jTablePegas.setValueAt(oRunTipoMecanismoPega.getPosicion(), i, 0);
                    MantCorridas.this.jTablePegas.setValueAt(oCI, i, 1);		
                    MantCorridas.this.jTablePegas.setValueAt(oRunTipoMecanismoPega.getTvd(), i, 2);
            }
            
            oArray=oBD.select(RunServicio.class, "runId="+oRun.getId());
            for (int i=0;i<=oArray.length-1;i++) {
                    oRunServicio=(RunServicio) oArray[i];
                    oSrv=(Servicio) oBD.select(Servicio.class,"id="+oRunServicio.getServicioId())[0];
                    oCI=new ComboItem();
                    oCI.setValue(Long.toString(oSrv.getId()));
                    oCI.setKey(oSrv.getNombre());
                    MantCorridas.this.jTableServicios.setValueAt(oCI, i, 1);		
            }
                       
            oArray=oBD.select(RunSubSection.class, "runId="+oRun.getId());
            for (int i=0;i<=oArray.length-1;i++) {
                    oRunSubSection=(RunSubSection) oArray[i];
                    oDSST=(DrillingSubSectionType) oBD.select(DrillingSubSectionType.class,"id="+oRunSubSection.getDrillingSubSectionId())[0];
                    oCI=new ComboItem();
                    oCI.setValue(Long.toString(oDSST.getId()));
                    oCI.setKey(oDSST.getDescription());
                    MantCorridas.this.jTableSubSections.setValueAt(oRunSubSection.getPosicion(), i, 0);
                    MantCorridas.this.jTableSubSections.setValueAt(oCI, i, 1);		
                    MantCorridas.this.jTableSubSections.setValueAt(oRunSubSection.getProfundidadInicial(), i, 2);
                    MantCorridas.this.jTableSubSections.setValueAt(oRunSubSection.getProfundidadFinal(), i, 3);
            }
            
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MantCorridas.class.getName()).log(Level.SEVERE, null, ex);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListExistentes = new javax.swing.JList();
        jComboBoxEmpresaResponsable = new javax.swing.JComboBox();
        jLabelLongitud7 = new javax.swing.JLabel();
        jLabelLongitud8 = new javax.swing.JLabel();
        jLabelLongitud9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTablePegas = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        dateTimePickerFinalizacion = new com.toedter.calendar.JDateChooser();
        dateTimePickerComienzo = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableSubSections = new javax.swing.JTable();
        jTextFieldInitialDepth = new javax.swing.JTextField();
        jTextFieldBRT = new javax.swing.JTextField();
        jTextFieldFinalDepth = new javax.swing.JTextField();
        jComboBoxReasonForPOOH = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jButtonMantReasonForPOOH = new javax.swing.JButton();
        jButtonMantReasonForPOOHOK = new javax.swing.JButton();
        jButtonMantReasonForPOOHCancel = new javax.swing.JButton();
        jTextFieldReasonForPOOHNombre = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableServicios = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jCheckBoxGYRO = new javax.swing.JCheckBox();

        jLabelDescContenido.setText("Contenido:");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(800, 500));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(800, 500));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Comienzo:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        jLabel2.setText("Reason for POOH:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 180, -1));

        jLabel3.setText("Sub Sections:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

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

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 380, 80));

        jComboBoxEmpresaResponsable.setEnabled(false);
        getContentPane().add(jComboBoxEmpresaResponsable, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 230, -1));

        jLabelLongitud7.setText("BRT en horas:");
        getContentPane().add(jLabelLongitud7, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 60, 90, 20));

        jLabelLongitud8.setText("Final Depth:");
        getContentPane().add(jLabelLongitud8, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, -1, -1));

        jLabelLongitud9.setText("Initial Depth:");
        getContentPane().add(jLabelLongitud9, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 110, -1, -1));

        jTablePegas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "nro", "Mecanismo", "TDV"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablePegas.setEnabled(false);
        jTablePegas.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTablePegas);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 360, 360, 90));

        jLabel4.setText("Empresa Responsable:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));

        dateTimePickerFinalizacion.setDateFormatString("dd/MM/yyyy HH:mm");
        dateTimePickerFinalizacion.setEnabled(false);
        dateTimePickerFinalizacion.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateTimePickerFinalizacionPropertyChange(evt);
            }
        });
        getContentPane().add(dateTimePickerFinalizacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 160, -1));

        dateTimePickerComienzo.setDateFormatString("dd/MM/yyyy HH:mm");
        dateTimePickerComienzo.setEnabled(false);
        dateTimePickerComienzo.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                dateTimePickerComienzoCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                dateTimePickerComienzoInputMethodTextChanged(evt);
            }
        });
        dateTimePickerComienzo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateTimePickerComienzoPropertyChange(evt);
            }
        });
        dateTimePickerComienzo.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                dateTimePickerComienzoVetoableChange(evt);
            }
        });
        getContentPane().add(dateTimePickerComienzo, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, 160, -1));

        jLabel5.setText("Servicios:");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 340, -1, -1));

        jTableSubSections.setModel(new javax.swing.table.DefaultTableModel(
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
                "nro", "Tipo", "Profundidad Inicial", "Profundidad Final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableSubSections.setEnabled(false);
        jTableSubSections.getTableHeader().setReorderingAllowed(false);
        jTableSubSections.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTableSubSectionsFocusLost(evt);
            }
        });
        jTableSubSections.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSubSectionsMouseClicked(evt);
            }
        });
        jTableSubSections.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTableSubSectionsInputMethodTextChanged(evt);
            }
        });
        jTableSubSections.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableSubSectionsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTableSubSectionsKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jTableSubSections);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 740, 90));

        jTextFieldInitialDepth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldInitialDepth.setEnabled(false);
        jTextFieldInitialDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldInitialDepthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldInitialDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 130, 90, -1));

        jTextFieldBRT.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldBRT.setEnabled(false);
        jTextFieldBRT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldBRTKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldBRT, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 60, 80, -1));

        jTextFieldFinalDepth.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldFinalDepth.setEnabled(false);
        jTextFieldFinalDepth.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldFinalDepthKeyTyped(evt);
            }
        });
        getContentPane().add(jTextFieldFinalDepth, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 130, 90, -1));

        jComboBoxReasonForPOOH.setEnabled(false);
        getContentPane().add(jComboBoxReasonForPOOH, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 230, -1));

        jLabel6.setText("Finalización:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, -1, -1));

        jButtonMantReasonForPOOH.setText("...");
        jButtonMantReasonForPOOH.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonMantReasonForPOOH.setEnabled(false);
        jButtonMantReasonForPOOH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantReasonForPOOHActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantReasonForPOOH, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 180, 30, -1));

        jButtonMantReasonForPOOHOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Checkmark 24x16.png"))); // NOI18N
        jButtonMantReasonForPOOHOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantReasonForPOOHOKActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantReasonForPOOHOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 180, 32, -1));

        jButtonMantReasonForPOOHCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/Cancelar 24x16.png"))); // NOI18N
        jButtonMantReasonForPOOHCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMantReasonForPOOHCancelActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMantReasonForPOOHCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 180, 32, -1));

        jTextFieldReasonForPOOHNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldReasonForPOOHNombreActionPerformed(evt);
            }
        });
        getContentPane().add(jTextFieldReasonForPOOHNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 180, 200, -1));

        jTableServicios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "nro", "Servicio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableServicios.setEnabled(false);
        jTableServicios.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTableServicios);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 360, 260, 90));

        jLabel7.setText("Situaciones de Pegas:");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, -1, -1));

        jCheckBoxGYRO.setText("GYRO");
        jCheckBoxGYRO.setEnabled(false);
        getContentPane().add(jCheckBoxGYRO, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 130, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListExistentesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListExistentesMouseClicked
        accionClickLista();
    }//GEN-LAST:event_jListExistentesMouseClicked

    private void jListExistentesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListExistentesValueChanged
        accionClickLista();
    }//GEN-LAST:event_jListExistentesValueChanged

    private void jTableSubSectionsInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTableSubSectionsInputMethodTextChanged

    }//GEN-LAST:event_jTableSubSectionsInputMethodTextChanged

    private void jTableSubSectionsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableSubSectionsKeyReleased
        calculaProfundidades();
    }//GEN-LAST:event_jTableSubSectionsKeyReleased

    private void calculaProfundidades(){
        int ultimo=-1;
        for (int i=0;i<=this.jTableSubSections.getRowCount()-1;i++) {
            if (this.jTableSubSections.getValueAt(i, 0)=="") break;
            ultimo=i;
        }
        if (ultimo>-1) {
            MantCorridas.this.jTextFieldInitialDepth.setText(this.jTableSubSections.getValueAt(0, 2).toString());
            MantCorridas.this.jTextFieldFinalDepth.setText(this.jTableSubSections.getValueAt(ultimo, 3).toString());
        } 
        else {
           MantCorridas.this.jTextFieldInitialDepth.setText("");
           MantCorridas.this.jTextFieldFinalDepth.setText("");            
        }            
    }
    
    private void calculaBRT() {
        DecimalFormat myFormatter = new DecimalFormat("########.##");
        Calendar calComienzo = Calendar.getInstance();
        Calendar calFinalizacion = Calendar.getInstance();
        double diferencia=0.0;
        double brtCalculado=0.0;
        String output="";
        try {
            calComienzo.setTime(this.dateTimePickerComienzo.getDate());
            calFinalizacion.setTime(this.dateTimePickerFinalizacion.getDate());
            diferencia=calFinalizacion.getTimeInMillis()-calComienzo.getTimeInMillis();
            brtCalculado=diferencia/3600000;
            output=myFormatter.format(brtCalculado).replace(",", ".");
            this.jTextFieldBRT.setText(output);            
        } catch (NullPointerException exc) {;}
    }
    
    private void jTableSubSectionsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableSubSectionsKeyPressed
        calculaProfundidades();
    }//GEN-LAST:event_jTableSubSectionsKeyPressed

    private void jTableSubSectionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSubSectionsMouseClicked
        calculaProfundidades();
    }//GEN-LAST:event_jTableSubSectionsMouseClicked

    private void jTableSubSectionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTableSubSectionsFocusLost
        calculaProfundidades();
    }//GEN-LAST:event_jTableSubSectionsFocusLost

    private void jTextFieldBRTKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBRTKeyTyped
        oNumeros.soloDobles(evt);
    }//GEN-LAST:event_jTextFieldBRTKeyTyped

    private void jTextFieldInitialDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldInitialDepthKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldInitialDepthKeyTyped

    private void jTextFieldFinalDepthKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFinalDepthKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFinalDepthKeyTyped

    private void dateTimePickerComienzoCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_dateTimePickerComienzoCaretPositionChanged

    }//GEN-LAST:event_dateTimePickerComienzoCaretPositionChanged

    private void dateTimePickerComienzoInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_dateTimePickerComienzoInputMethodTextChanged
 
    }//GEN-LAST:event_dateTimePickerComienzoInputMethodTextChanged

    private void dateTimePickerComienzoVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_dateTimePickerComienzoVetoableChange

    }//GEN-LAST:event_dateTimePickerComienzoVetoableChange

    private void dateTimePickerComienzoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateTimePickerComienzoPropertyChange
        calculaBRT();
    }//GEN-LAST:event_dateTimePickerComienzoPropertyChange

    private void dateTimePickerFinalizacionPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateTimePickerFinalizacionPropertyChange
        calculaBRT();
    }//GEN-LAST:event_dateTimePickerFinalizacionPropertyChange

    private void jButtonMantReasonForPOOHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantReasonForPOOHActionPerformed
        habilitarControles(false);
        super.jButtonCancelar.setEnabled(false);
        super.jButtonProcesar.setEnabled(false);
        this.jButtonMantReasonForPOOH.setEnabled(false);
        this.jTextFieldReasonForPOOHNombre.setEnabled(true);
        this.jTextFieldReasonForPOOHNombre.setVisible(true);
        this.jButtonMantReasonForPOOHOK.setVisible(true);
        this.jButtonMantReasonForPOOHCancel.setVisible(true);
    }//GEN-LAST:event_jButtonMantReasonForPOOHActionPerformed

    private void jButtonMantReasonForPOOHOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantReasonForPOOHOKActionPerformed
        oManejoDeCombos.ingresarAlComboBox(this.jTextFieldReasonForPOOHNombre,this.jComboBoxReasonForPOOH);
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantReasonForPOOH.setEnabled(true);
        this.jComboBoxReasonForPOOH.setEnabled(true);
        this.jTextFieldReasonForPOOHNombre.setEnabled(false);
        this.jTextFieldReasonForPOOHNombre.setVisible(false);
        this.jTextFieldReasonForPOOHNombre.setText("");
        this.jButtonMantReasonForPOOHOK.setVisible(false);
        this.jButtonMantReasonForPOOHCancel.setVisible(false);
    }//GEN-LAST:event_jButtonMantReasonForPOOHOKActionPerformed

    private void jButtonMantReasonForPOOHCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMantReasonForPOOHCancelActionPerformed
        habilitarControles(true);
        super.jButtonCancelar.setEnabled(true);
        super.jButtonProcesar.setEnabled(true);
        this.jButtonMantReasonForPOOH.setEnabled(true);
        this.jButtonMantReasonForPOOH.setEnabled(true);
        this.jTextFieldReasonForPOOHNombre.setEnabled(false);
        this.jTextFieldReasonForPOOHNombre.setVisible(false);
        this.jTextFieldReasonForPOOHNombre.setText("");
        this.jButtonMantReasonForPOOHOK.setVisible(false);
        this.jButtonMantReasonForPOOHCancel.setVisible(false);
    }//GEN-LAST:event_jButtonMantReasonForPOOHCancelActionPerformed

    private void jTextFieldReasonForPOOHNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldReasonForPOOHNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldReasonForPOOHNombreActionPerformed

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
            java.util.logging.Logger.getLogger(MantCorridas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MantCorridas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MantCorridas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MantCorridas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MantCorridas dialog = new MantCorridas(new javax.swing.JFrame(), true);
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
    private com.toedter.calendar.JDateChooser dateTimePickerComienzo;
    private com.toedter.calendar.JDateChooser dateTimePickerFinalizacion;
    private javax.swing.JButton jButtonMantReasonForPOOH;
    private javax.swing.JButton jButtonMantReasonForPOOHCancel;
    private javax.swing.JButton jButtonMantReasonForPOOHOK;
    private javax.swing.JCheckBox jCheckBoxGYRO;
    private javax.swing.JComboBox jComboBoxEmpresaResponsable;
    private javax.swing.JComboBox jComboBoxReasonForPOOH;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelDescContenido;
    private javax.swing.JLabel jLabelLongitud7;
    private javax.swing.JLabel jLabelLongitud8;
    private javax.swing.JLabel jLabelLongitud9;
    private javax.swing.JList jListExistentes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTablePegas;
    private javax.swing.JTable jTableServicios;
    private javax.swing.JTable jTableSubSections;
    private javax.swing.JTextField jTextFieldBRT;
    private javax.swing.JTextField jTextFieldFinalDepth;
    private javax.swing.JTextField jTextFieldInitialDepth;
    private javax.swing.JTextField jTextFieldReasonForPOOHNombre;
    // End of variables declaration//GEN-END:variables
}
