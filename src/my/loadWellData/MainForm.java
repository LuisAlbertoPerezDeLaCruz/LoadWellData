/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import miLibreria.bd.*;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeCellRenderer;
import miLibreria.BDConn;


/**
 *
 * @author Luis
 */
public class MainForm extends javax.swing.JFrame implements miLibreria.GlobalConstants{
    public DefaultMutableTreeNode tnRaiz;
    public DefaultMutableTreeNode tnLoc;
    public DefaultMutableTreeNode tnDiv;
    public DefaultMutableTreeNode tnCli;
    public DefaultMutableTreeNode tnCampoCliente;
    public DefaultMutableTreeNode tnMacolla;
    public DefaultMutableTreeNode tnWell;
    public DefaultMutableTreeNode tnSections; 
    public DefaultMutableTreeNode tnRun;  

    public ManejoBDI oBD;
    public int profundidadArbol;
    public int nivelArbol;
    public String seleccionArbol, prevSeleccionArbol;
    public TreeNode currentTreeNode;
    static DefaultTableModel modelo;
    public Object[][] objetosEnTree;
    public long currentPozoId;
    public long currentCampoId;
    private Campo oCampo=new Campo();
    public static String sDir="";
    public static String msgVersion="";
    public String ldap="";
    public final int NINGUNO=0,ADMINISTRADOR=1,ACTUALIZADOR=2,REPORTEADOR=3;
    public long nivel=NINGUNO;
    Usuarios oUsuarios=new Usuarios(); 
    
    private BDConn jBDConn;
    private String tipoConeccion;
    private String ubicacion;
    private String servidor;
    private String instancia;
    private String userId;
    private String pass;
    private boolean esWindows;
    private String bd;

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        msgVersion="Project Leader: Luis Fernando Perez Armas\nLArmas@slb.com\n\nProgramming: LAP Consultores C.A. \nTlf: 58-414-9337812\n\nVersion: 1.0\nOctober 2015\n\n";
        this.jRadioButtonDummy.setVisible(false);
        
        seleccionArbol="";
        prevSeleccionArbol="";
        objetosEnTree=new Object[200000][2];
        jTree1.setCellRenderer(new MyTreeCellRenderer());
        jTree1.addTreeSelectionListener((TreeSelectionEvent e) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    jTree1.getLastSelectedPathComponent();
            
            /* if nothing is selected */
        
            if (node == null) return;
                       
            /* retrieve the node that was selected */
            {
                currentTreeNode=node;
                buscaElPozo();
                profundidadArbol=node.getDepth(); 
                nivelArbol=node.getLevel();
                System.out.println(node+"->nivel:"+nivelArbol);
                seleccionArbol=node.toString();
                this.jRadioButtonMacolla.setEnabled(false);
                this.jRadioButtonPozo.setEnabled(false);
                this.jRadioButtonSecciones.setEnabled(false);
                this.jRadioButtonCorridas.setEnabled(false);
                this.jRadioButtonBHA.setEnabled(false);
                this.jRadioButtonSurvey.setEnabled(false);
                this.jRadioButtonPlan.setEnabled(false);                
                this.jButtonLlamarMantPpal.setEnabled(false);
                this.jRadioButtonSlideSheet.setEnabled(false);
                this.jRadioButtonLAS.setEnabled(false);
                this.jRadioButtonDummy.setSelected(true);
                
                if (currentPozoId>0)
                     muestraPozo(currentPozoId);
                
                if (nivelArbol==Niveles.nivelCampo) {
                    this.jRadioButtonMacolla.setEnabled(true);
                }
                
                if (nivelArbol==Niveles.nivelMacolla) {
                    this.jRadioButtonPozo.setEnabled(true);
                }
                if (nivelArbol==Niveles.nivelPozo) {
                   if (!prevSeleccionArbol.equals(seleccionArbol)) 
                   {
                       prevSeleccionArbol=seleccionArbol;
                   }
                   this.jRadioButtonSecciones.setEnabled(true);
                }                
                if (nivelArbol==Niveles.nivelSeccion) {
                    this.jRadioButtonCorridas.setEnabled(true);
                }
                if (nivelArbol==Niveles.nivelCorrida){
                    this.jRadioButtonBHA.setEnabled(true);
                    this.jRadioButtonSurvey.setEnabled(true);
                    this.jRadioButtonPlan.setEnabled(true); 
                    this.jRadioButtonSlideSheet.setEnabled(true);
                    this.jRadioButtonLAS.setEnabled(true);
                }
            }
        });
        
        modelo = (DefaultTableModel) this.jTable1.getModel();
        for (int i=modelo.getRowCount()-1;i>=0;i--) {
            modelo.removeRow(i);
        }
        this.setLocationRelativeTo(null);
        
        jBDConn=new BDConn(this,true);
        jBDConn.llenarPantalla(jBDConn.sParam);        
        jBDConn.setVisible(true);        
        tipoConeccion=jBDConn.getTipoConeccion();
        ubicacion=jBDConn.getUbicacion();
        servidor=jBDConn.getServidor();
        instancia=jBDConn.getInstancia();
        userId=jBDConn.getUserId();
        pass=jBDConn.getPassWord();
        esWindows=jBDConn.getEsWindows();
        bd=jBDConn.getBD();
        
        if ("MSACCESS".equals(tipoConeccion)) {
           oBD=new ManejoBDAccess();
           oBD.setUbicacion(ubicacion);
        }
        else {
           oBD=new ManejoBDSqlServer();
           oBD.setServidor(servidor);
           oBD.setInstancia(instancia);
           oBD.setBD(bd);
           oBD.setUserId(userId);
           oBD.setPassword(pass);
           oBD.setEsWindows(esWindows);
        }
        
        oBD.conectar();
        if (oBD.getconectado()==false) try {
            System.exit(0);
        } catch (Throwable ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ldap=System.getProperty("user.name");
        //ldap="MGomez";
        //ldap="FUlanito";
        //ldap="CHpulin";
        
        if ("LArmas".equals(ldap) || "Luis".equals(ldap) || "USUARIO".equals(ldap)) {
            nivel=ADMINISTRADOR;
        } else {
            try {
                Object[] o= oBD.select(Usuarios.class,"ldap='"+ldap+"'");
                if (o.length>0) {
                   oUsuarios=(Usuarios) o[0];
                   nivel=desencriptarNivel(ldap,(int) oUsuarios.getNivel());
                }
            } catch (Exception ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            if (nivel==NINGUNO) { 
                msgbox("Usted no se encuentra registrado en el sistema.");
                System.exit(0);
            }
        }
        if (nivel==ADMINISTRADOR || nivel==ACTUALIZADOR) {
             LlenaArbol();           
        } else {
            msgbox("Usted no tiene permiso para utilizar esta aplicacion.");
            System.exit(0);
        }
            

    }

    
    private void buscaElPozo() {
        Run oRun=new Run();
        Sections oSections=new Sections();
        Well oWell=new Well();
        Object o=null;
        currentPozoId=0;
        for (int i=0;i<=objetosEnTree.length-1;i++) {
            if (objetosEnTree[i][0]==currentTreeNode) {
                o=objetosEnTree[i][1];
                break;
            }
        }
        if (o==null) return;
        if (o.getClass()==Run.class) {
            oRun=(Run) o;
            try {
                oRun=(Run) oBD.select(Run.class, "Id="+oRun.getId())[0];
                oSections=(Sections) oBD.select(Sections.class, "Id="+oRun.getSectionId())[0];
                currentPozoId=oSections.getWellId();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (o.getClass()==Sections.class) {
            oSections=(Sections) o;
            try {
                oSections=(Sections) oBD.select(Sections.class, "Id="+oSections.getId())[0];
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            currentPozoId=oSections.getWellId();
        }
        if (o.getClass()==Well.class) {
            oWell=(Well) o;
            currentPozoId=oWell.getId();
        }
    }
    
    public String[] CadenaPadres(TreeNode t){
        String[] s=new String[10];
        return s;
    }
    
    public void muestraPozo(long wellId) {
        Well oPozo;
        oPozo=new Well();
        String s;
        try {
            oPozo=(Well) oBD.select(Well.class, "Id="+wellId)[0];
            s="Pozo: ";
            s+=oPozo.getNombre();
            s+=", Latitud:"+oPozo.getLocationLat();
            s+=", Longitud:"+oPozo.getLocationLong();
            this.jLabelPozo.setText(s);
            if (oBD.huboCambios)
                actualizaEstatusPozo(oPozo.getId());
            MuestraEstatusPozo(oPozo.getId());            
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void limpiaInfoPozo() {
       this.jLabelPozo.setText(""); 
        for (int i=modelo.getRowCount()-1;i>=0;i--) {
             modelo.removeRow(i);
        }
    }

    public void muestraPozo() {
        Object[] ob_array;
        String s;
        Well oPozo;
        oPozo=new Well();
        try {
            ob_array=oBD.select(Well.class, "nombre='"+seleccionArbol+"'");
            if (ob_array.length>0) {
                oPozo=(Well) ob_array[0];
                s="Pozo: ";
                s+=oPozo.getNombre();
                s+=", Latitud:"+oPozo.getLocationLat();
                s+=", Longitud:"+oPozo.getLocationLong();
                this.jLabelPozo.setText(s);
                if (oBD.huboCambios)
                    actualizaEstatusPozo(oPozo.getId());
                MuestraEstatusPozo(oPozo.getId());
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void MuestraEstatusPozo(long wellId) {
       String s="SELECT * FROM EstatusPozo WHERE wellId="+wellId+";";
       ResultSet rs=oBD.select(s);
       for (int i=modelo.getRowCount()-1;i>=0;i--) {
            modelo.removeRow(i);
       }
       if (oBD.getRegistrsoAfectados()==0) {
           return;
       }
        try {
            while (rs.next()){
               modelo.addRow(new Object[] {rs.getInt("sectionNumero"),rs.getInt("runNumero"),rs.getBoolean("estatusSurvey"),rs.getBoolean("estatusPlan"),rs.getBoolean("estatusSS"),rs.getBoolean("estatusBHA"),rs.getBoolean("estatusLAS"),rs.getBoolean("estatusCONFDE"),rs.getBoolean("estatusCONFFSM")} ); 
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    public void actualizaEstatusPozo(long wellId) {
        Object[] o=null;
        Sections oSection=new Sections();
        try {
            o= oBD.select(Sections.class, "wellId="+wellId);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i=0;i<=o.length-1;i++) {
            oSection=(Sections) o[i];
            actualizaSectionsEstatus(oSection.getId());
        }
        oBD.setHuboCambios(false);
    }
    
    public void actualizaSectionsEstatus(long sectionId) {
        Object[] o=null;
        Run oRun=new Run();
        try {
            o= oBD.select(Run.class, "sectionId="+sectionId);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i=0;i<=o.length-1;i++) {
            oRun=(Run) o[i];
            actualizaRunEstatus(oRun.getId());
        }

    }
    
    public void actualizaRunEstatus(long runId) {
        RunEstatus oRE=new RunEstatus();
        oRE.setRunId(runId);
        oRE.setEstatusSurvey(oBD.tieneRegistros("CuentaRegistrosEnSurvey", "cantidad", "runId="+runId));
        oRE.setEstatusPlan(oBD.tieneRegistros("CuentaRegistrosEnPlan", "cantidad", "runId="+runId));
        oRE.setEstatusSS(oBD.tieneRegistros("CuentaRegistrosEnSlideSheet", "cantidad", "runId="+runId));
        oRE.setEstatusBHA(oBD.tieneRegistros(new BHA(), "runId="+runId));
        oRE.setEstatusLAS(oBD.tieneRegistros("CuentaRegistrosEnLAS", "cantidad", "runId="+runId)); 
        oBD.update(oRE, "runId="+runId);
    }
    
    public void LlenaArbol() {
        int i=0;
        cursorEspera();
        try {
            tnRaiz=new javax.swing.tree.DefaultMutableTreeNode("Venezuela");
            ResultSet rsLoc;
            Location oLoc=new Location();
            objetosEnTree[i][0]=tnRaiz;
            objetosEnTree[i][1]=null;
            rsLoc=oBD.select(oLoc, "1");
            while (rsLoc.next()) {
                tnLoc=new javax.swing.tree.DefaultMutableTreeNode(rsLoc.getString("nombre"));
                oLoc=new Location();
                oLoc.setId(rsLoc.getLong("Id"));
                objetosEnTree[++i][0]=tnLoc;
                objetosEnTree[i][1]=oLoc;
                ResultSet rsDiv;
                Division oDiv=new Division();
                rsDiv=oBD.select(oDiv, "locationId="+rsLoc.getInt("Id"));
                while (rsDiv.next()) {
                   tnDiv=new javax.swing.tree.DefaultMutableTreeNode(rsDiv.getString("nombre"));
                   oDiv=new Division();
                   oDiv.setId(rsDiv.getLong("Id"));
                   objetosEnTree[++i][0]=tnDiv;
                   objetosEnTree[i][1]=oDiv;
                   ResultSet rsCli;
                   Clientes oCli=new Clientes();
                   rsCli=oBD.select(oCli, "divisionId="+rsDiv.getInt("Id"));
                   while (rsCli.next()) {
                       tnCli=new javax.swing.tree.DefaultMutableTreeNode(rsCli.getString("nombre"));
                       objetosEnTree[++i][0]=tnCli;
                       objetosEnTree[i][1]=oCli;
                       ResultSet rsCampoCliente;
                       String s;
                       s="SELECT CampoCliente.Id,Campo.Nombre, CampoCliente.ClienteId "
                               + "FROM (CampoCliente INNER JOIN Clientes ON CampoCliente.ClienteId = Clientes.Id) "
                               + "INNER JOIN Campo ON CampoCliente.CampoId = Campo.Id "
                               + "where CampoCliente.ClienteId="+rsCli.getInt("Id")+";";
                       rsCampoCliente=oBD.select(s);
                       CampoCliente oCampoCliente=new CampoCliente();
                       while (rsCampoCliente.next()) {
                          tnCampoCliente=new javax.swing.tree.DefaultMutableTreeNode(rsCampoCliente.getString("nombre"));
                          oCampoCliente=new CampoCliente();
                          oCampoCliente.setId(rsCampoCliente.getLong("Id"));
                          objetosEnTree[++i][0]=tnCampoCliente;
                          objetosEnTree[i][1]=oCampoCliente;
                          ResultSet rsMacolla;
                          Macolla oMacolla=new Macolla();
                          rsMacolla=oBD.select(oMacolla, "campoClienteId="+rsCampoCliente.getInt("Id"));
                          while (rsMacolla.next()) {
                              tnMacolla=new javax.swing.tree.DefaultMutableTreeNode(rsMacolla.getString("nombre"));
                              oMacolla=new Macolla();
                              oMacolla.setId(rsMacolla.getLong("Id"));
                              objetosEnTree[++i][0]=tnMacolla;
                              objetosEnTree[i][1]=oMacolla;                          
                              ResultSet rsWell;
                              Well oWell=new Well();
                              if (rsMacolla.getInt("Id")==1) {
                                  int dummy=-1;
                              }
                              rsWell=oBD.select(oWell,"macollaId="+rsMacolla.getInt("Id")+" AND clienteId="+rsCampoCliente.getInt("ClienteId") + " ORDER BY nombre ASC" );
                              while (rsWell.next()) {
                                  oWell=new Well();
                                  oWell.setId(rsWell.getLong("Id"));
                                  oWell.setStatus(rsWell.getLong("status"));
                                  String sWork=rsWell.getString("nombre");
                                  if (oWell.getStatus()==1) sWork+="*";
                                  tnWell=new javax.swing.tree.DefaultMutableTreeNode(sWork);
                                  objetosEnTree[++i][0]=tnWell;
                                  objetosEnTree[i][1]=oWell;
                                  ResultSet rsSections;
                                  Sections oS=new Sections();
                                  rsSections=oBD.select(oS,"wellId="+rsWell.getInt("Id") );
                                  while (rsSections.next()){
                                      String ss=rsSections.getString("numeroIdentificador");
                                      tnSections=new javax.swing.tree.DefaultMutableTreeNode("Section#:" + rsSections.getString("numeroIdentificador"));
                                      oS=new Sections();
                                      oS.setId(rsSections.getLong("Id"));
                                      objetosEnTree[++i][0]=tnSections;
                                      objetosEnTree[i][1]=oS;                  
                                      ResultSet rsRun;
                                      Run oR=new Run();
                                      rsRun=oBD.select(oR,"sectionId="+rsSections.getInt("Id") );
                                      while (rsRun.next()) {
                                          String rr=rsRun.getString("numero");
                                          tnRun=new javax.swing.tree.DefaultMutableTreeNode("Run#:" + rsRun.getString("numero"));
                                          oR=new Run();
                                          try {
                                              Object o=oBD.select(Run.class,"id="+rsRun.getLong("Id"))[0];
                                              oR=(Run)o;
                                          } catch (InstantiationException | IllegalAccessException ex) {
                                              Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                                          }
                                          oR.setId(rsRun.getLong("Id"));
                                          objetosEnTree[++i][0]=tnRun;
                                          objetosEnTree[i][1]=oR;                      
                                          tnSections.add(tnRun);
                                      }                                  
                                      tnWell.add(tnSections);
                                  }                                  
                                  tnMacolla.add(tnWell);
                              }
                              tnCampoCliente.add(tnMacolla);
                          }
                          tnCli.add(tnCampoCliente);
                       }
                       tnDiv.add(tnCli);
                   }
                   tnLoc.add(tnDiv);
                }
                tnRaiz.add(tnLoc);
            }
            jTree1.setModel(new javax.swing.tree.DefaultTreeModel(tnRaiz));
            jScrollPane1.setViewportView(jTree1);    
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        cursorNormal();
    }
    
    public String seleccionaArchivo(){
        File currDirectory=null, selectedFile=null;
        currDirectory=new File("config.001");
        
        Writer writer = null;
      
        
        if (!currDirectory.exists()) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream("config.001"), "utf-8"));
                writer.write("c:\\");
            } catch (IOException ex) {
              // report
            } finally {
               try {writer.close();} catch (Exception ex) {/*ignore*/}
            }
        }
        
        String line = null;
        BufferedReader in = null;

        try {
           in = new BufferedReader(new FileReader("config.001"));
        } catch (FileNotFoundException ex) {
           Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
           line = in.readLine();
           in.close();
        } catch (IOException ex) {
           Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        currDirectory=new File(line);
       
        String s="";
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Base de datos Access", new String[] {"accdb"});
        fileChooser.setCurrentDirectory(currDirectory);
        fileChooser.setDialogTitle("Escoja la ubicacion de la base de datos");
        fileChooser.setFileFilter(null);      
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter); 
        int result = fileChooser.showOpenDialog(this);
        currDirectory=fileChooser.getCurrentDirectory();
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            s=selectedFile.toString();
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream("config.001"), "utf-8"));
                writer.write(s);
            } catch (IOException ex) {
              // report
            } finally {
               try {writer.close();} catch (Exception ex) {/*ignore*/}
            }
        }
        return s;
    }
    
    private void quitarAsterisco(long wellId) {
       boolean ok=true;
       Well oWell=new Well();
       String s="";
       s="SELECT id FROM RUN WHERE wellId="+wellId+" AND requiereUpdate=true";
       if (oBD.getRowCount(s)>0) ok=false;
       s="SELECT id FROM Sections WHERE wellId="+wellId+" AND requiereUpdate=true";
       if (oBD.getRowCount(s)>0) ok=false;
       s="SELECT id FROM Wells WHERE id="+wellId+" AND requiereUpdate=true";
       if (oBD.getRowCount(s)>0) ok=false;
       if (ok) {
           try {
               oWell=(Well) oBD.select(Well.class, "id="+wellId)[0];
           } catch (InstantiationException | IllegalAccessException ex) {
               Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
           }
           oWell.setStatus(2);
           oBD.update(oWell, "id="+wellId);           
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabelPozo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButtonLlamarMantPpal = new javax.swing.JButton();
        jRadioButtonPozo = new javax.swing.JRadioButton();
        jRadioButtonCorridas = new javax.swing.JRadioButton();
        jRadioButtonPlan = new javax.swing.JRadioButton();
        jRadioButtonSurvey = new javax.swing.JRadioButton();
        jRadioButtonLAS = new javax.swing.JRadioButton();
        jRadioButtonSlideSheet = new javax.swing.JRadioButton();
        jRadioButtonBHA = new javax.swing.JRadioButton();
        jRadioButtonSecciones = new javax.swing.JRadioButton();
        jRadioButtonDummy = new javax.swing.JRadioButton();
        jRadioButtonMacolla = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LoadWellData");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSplitPane2.setAutoscrolls(true);
        jSplitPane2.setOneTouchExpandable(true);

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 322));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setAutoscrolls(true);
        jTree1.setMaximumSize(new java.awt.Dimension(250, 16));
        jTree1.setMinimumSize(new java.awt.Dimension(250, 16));
        jTree1.setPreferredSize(new java.awt.Dimension(500, 2500));
        jScrollPane1.setViewportView(jTree1);

        jSplitPane2.setLeftComponent(jScrollPane1);

        jPanel1.setAutoscrolls(true);
        jPanel1.setMaximumSize(new java.awt.Dimension(714, 596));
        jPanel1.setMinimumSize(new java.awt.Dimension(714, 596));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelPozo.setText("Pozo:");
        jLabelPozo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.add(jLabelPozo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 61, 400, 50));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Secciones", "Corridas", "Survey", "Plan", "SS", "BHA", "LAS", "Conf DE", "Conf FMS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(20);
        jScrollPane2.setViewportView(jTable1);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 156, 704, 440));

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonLlamarMantPpal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/btn_next.png"))); // NOI18N
        jButtonLlamarMantPpal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonLlamarMantPpal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonLlamarMantPpal.setEnabled(false);
        jButtonLlamarMantPpal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLlamarMantPpalActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonLlamarMantPpal, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 100, 40, 20));

        buttonGroup1.add(jRadioButtonPozo);
        jRadioButtonPozo.setText("Pozo");
        jRadioButtonPozo.setEnabled(false);
        jRadioButtonPozo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonPozoStateChanged(evt);
            }
        });
        jPanel2.add(jRadioButtonPozo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        buttonGroup1.add(jRadioButtonCorridas);
        jRadioButtonCorridas.setText("Corridas");
        jRadioButtonCorridas.setEnabled(false);
        jRadioButtonCorridas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCorridasActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonCorridas, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        buttonGroup1.add(jRadioButtonPlan);
        jRadioButtonPlan.setText("Plan");
        jRadioButtonPlan.setEnabled(false);
        jRadioButtonPlan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPlanActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonPlan, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, -1));

        buttonGroup1.add(jRadioButtonSurvey);
        jRadioButtonSurvey.setText("Survey");
        jRadioButtonSurvey.setEnabled(false);
        jRadioButtonSurvey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSurveyActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonSurvey, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        buttonGroup1.add(jRadioButtonLAS);
        jRadioButtonLAS.setText("LAS");
        jRadioButtonLAS.setEnabled(false);
        jRadioButtonLAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonLASActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonLAS, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, -1, -1));

        buttonGroup1.add(jRadioButtonSlideSheet);
        jRadioButtonSlideSheet.setText("Slide Sheet");
        jRadioButtonSlideSheet.setEnabled(false);
        jRadioButtonSlideSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSlideSheetActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonSlideSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, -1, -1));

        buttonGroup1.add(jRadioButtonBHA);
        jRadioButtonBHA.setText("BHA");
        jRadioButtonBHA.setEnabled(false);
        jRadioButtonBHA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonBHAActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonBHA, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, -1, -1));

        buttonGroup1.add(jRadioButtonSecciones);
        jRadioButtonSecciones.setText("Secciones");
        jRadioButtonSecciones.setEnabled(false);
        jRadioButtonSecciones.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonSeccionesStateChanged(evt);
            }
        });
        jRadioButtonSecciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSeccionesActionPerformed(evt);
            }
        });
        jPanel2.add(jRadioButtonSecciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        buttonGroup1.add(jRadioButtonDummy);
        jPanel2.add(jRadioButtonDummy, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, -1, -1));

        buttonGroup1.add(jRadioButtonMacolla);
        jRadioButtonMacolla.setText("Macolla");
        jRadioButtonMacolla.setEnabled(false);
        jRadioButtonMacolla.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButtonMacollaStateChanged(evt);
            }
        });
        jPanel2.add(jRadioButtonMacolla, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(427, 11, 280, 130));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/loadWellData/SB.png"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 140, 40));

        jSplitPane2.setRightComponent(jPanel1);

        getContentPane().add(jSplitPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLlamarMantPpalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLlamarMantPpalActionPerformed
        
        if (this.jRadioButtonMacolla.isSelected()){
            currentCampoId=0;
            try {
                Object[] o=oBD.select(Campo.class, "nombre='"+this.seleccionArbol+"'");
                oCampo=(Campo) o[0];
                currentCampoId=oCampo.getId();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            llamarMantMacolla();
        }        
        if (this.jRadioButtonPozo.isSelected()){
            llamarMantPozo();
        }
        if (this.jRadioButtonSecciones.isSelected()){
            llamarMantSecciones();
        }   
        if (this.jRadioButtonCorridas.isSelected()){
            llamarMantCorridas();        } 
        
        if (this.jRadioButtonBHA.isSelected()){
            llamarMantBHA();
        }
        if (this.jRadioButtonSurvey.isSelected()){
            llamarCargaSurvey();
        }
        if (this.jRadioButtonPlan.isSelected()){
            llamarCargaPlan();
        }
        if (this.jRadioButtonSlideSheet.isSelected()){
            llamarCargaSlideSheet();
        }
        if (this.jRadioButtonLAS.isSelected()){
            llamarCargaLAS();
        }
        if (oBD.getHuboCambios()) {
            actualizaEstatusPozo(currentPozoId);
            MuestraEstatusPozo(currentPozoId);
        }
        this.jRadioButtonDummy.setSelected(true);
    }//GEN-LAST:event_jButtonLlamarMantPpalActionPerformed
    
    private void llamarMantSecciones() {
        MantSecciones j=new MantSecciones(this,true);
        String s=this.seleccionArbol;
        if (s.endsWith("*")) {
            s=s.replace("*", " ").trim();
        }
        j.setLocationRelativeTo(this);
        j.sDescNodo=s;
        j.setParam(oBD);
        if (j.oWell.getRequiereUpdate()==false) {
            j.setVisible(true);
            quitarAsterisco(this.currentPozoId);  
        }else{
            s="El pozo "+this.seleccionArbol+ " fue sincronizado con la BD FTL. \n";
            s+="Para poder actualizar sus secciones se requiere que confirme\n";
            s+="la informacion mediante una actualizacion del pozo.\n";
            s+="Actualize el pozo y vuelva a intentarlo.";
            msgbox(s);
        }
        reUbicacionEnArbol();
    }
    
    private void llamarMantPozo() {
        MantPozo j = new MantPozo(this,true);
        j.setLocationRelativeTo(this);
        j.EsPozo=this.jRadioButtonPozo.isSelected(); 
        j.sDescNodo=this.seleccionArbol;
        j.setParam(oBD);
        j.setVisible(true);
        quitarAsterisco(j.oWell.getId());  
        reUbicacionEnArbol();
        if (j.huboEliminacion) limpiaInfoPozo();
    }
    
    private void llamarMantMacolla() {
        MantMacollas j=new MantMacollas(this,true);
        j.setLocationRelativeTo(this);
        j.sDescNodo=this.seleccionArbol;
        j.currentCampoId=this.currentCampoId;
        j.setParam(oBD);
        j.setVisible(true);
        reUbicacionEnArbol();      
    }
    
    private void llamarMantCorridas() {
        String s="";
        MantCorridas j=new MantCorridas(this,true);
        j.setLocationRelativeTo(this);
        j.sDescNodo=this.seleccionArbol;
        j.currentPozoId=this.currentPozoId;
        j.setParam(oBD);
        if (j.oSections.getRequiereUpdate()==false) {
            j.setVisible(true);
            quitarAsterisco(this.currentPozoId);    
        }else{
            s="La "+this.seleccionArbol+ " fue sincronizada con la BD FTL. \n";
            s+="Para poder actualizar sus corridas se requiere que confirme\n";
            s+="la informacion mediante una actualizacion de la seccion.\n";
            s+="Actualize la seccion y vuelva a intentarlo.";
            msgbox(s);
        }
        reUbicacionEnArbol();
    }
      
    private void llamarMantBHA() {
        String s="";
        MantBHA j=new MantBHA(this,true);
        j.setLocationRelativeTo(this);
        for (int i=0;i<=objetosEnTree.length-1;i++) {
            if (objetosEnTree[i][0]==currentTreeNode) {
                j.oRunFromMainForm=(Run) objetosEnTree[i][1];
                break;
            }
        }
        if (j.oRunFromMainForm.getRequiereUpdate()==true) {
            s="La Corrida "+currentTreeNode+" fue sincronizada desde la BD FTL. \n";
            s+="Se requiere confirmar/completar la informacionmediante una actualizacion.\n";
            s+="Actualize la corrida y vuelva a intentarlo.";
            msgbox(s);
            return;
        }
        j.sDescNodo=this.seleccionArbol;
        j.setParam(oBD);
        j.setVisible(true);
    } 
    
    public boolean llamarCargaSurvey() { 
        String s="";
        CargaSurvey j=new CargaSurvey(this,true);
        for (int i=0;i<=objetosEnTree.length-1;i++) {
            if (objetosEnTree[i][0]==currentTreeNode) {
                j.oRunFromMainForm=(Run) objetosEnTree[i][1];
                break;
            }
        }
        if (j.oRunFromMainForm.getRequiereUpdate()==true) {
            s="La Corrida "+currentTreeNode+" fue sincronizada desde la BD FTL. \n";
            s+="Se requiere confirmar/completar la informacionmediante una actualizacion.\n";
            s+="Actualize la corrida y vuelva a intentarlo.";
            msgbox(s);
            return false;
        }
        j.setVisible(true);
        j.setParam(oBD);
        j.setLocationRelativeTo(this);
        j.setVisible(true);
        muestraPozo(currentPozoId);
        return true;
    }
    
    public boolean llamarCargaPlan() {  
        String s="";
        CargaWellPlan j=new CargaWellPlan(this,true);
        for (int i=0;i<=objetosEnTree.length-1;i++) {
            if (objetosEnTree[i][0]==currentTreeNode) {
                j.oRunFromMainForm=(Run) objetosEnTree[i][1];
                break;
            }
        }
        if (j.oRunFromMainForm.getRequiereUpdate()==true) {
            s="La Corrida "+currentTreeNode+" fue sincronizada desde la BD FTL. \n";
            s+="Se requiere confirmar/completar la informacionmediante una actualizacion.\n";
            s+="Actualize la corrida y vuelva a intentarlo.";
            msgbox(s);
            return false;
        }
        j.setParam(oBD);
        j.setLocationRelativeTo(this);
        j.setVisible(true);

        muestraPozo(currentPozoId);
        return true;
    }
    
    public boolean llamarCargaSlideSheet() {
        String s="";
        long runId=0;
        boolean ok=false;

        CargaSlideSheet j=new CargaSlideSheet(this,true);
        for (int i=0;i<=objetosEnTree.length-1;i++) {
            if (objetosEnTree[i][0]==currentTreeNode) {
                j.oRunFromMainForm=(Run) objetosEnTree[i][1];
                break;
            }
        }

        runId=j.oRunFromMainForm.getId();
        if (j.oRunFromMainForm.getRequiereUpdate()==true) {
            s="La Corrida "+currentTreeNode+" fue sincronizada desde la BD FTL. \n";
            s+="Se requiere confirmar/completar la informacionmediante una actualizacion.\n";
            s+="Actualize la corrida y vuelva a intentarlo.";
            msgbox(s);
            return false;
        }
        try {
            Object[] o=oBD.select(BHA.class, "runId="+runId);
            BHA oBHA=(BHA) o[0];
            if (oBHA.getRunId()==runId) ok=true;
        } catch (InstantiationException | IllegalAccessException | ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ok) {
            j.setParam(oBD);
            j.setLocationRelativeTo(this);
            j.setVisible(true);
            muestraPozo(currentPozoId);
        }
        else {
            msgbox("Se necesita cargar el BHA antes del Slide Sheet.");
        }
        return ok;
    }
    
    public void msgbox(String s){
        //JOptionPane.showMessageDialog(null, s);
        JOptionPane.showMessageDialog(null, s,"Informacion",JOptionPane.INFORMATION_MESSAGE);
    }
    
    public boolean llamarCargaLAS() {  
        String s="";
        CargaLAS j=new CargaLAS(this,true);
        for (int i=0;i<=objetosEnTree.length-1;i++) {
            if (objetosEnTree[i][0]==currentTreeNode) {
                j.oRunFromMainForm=(Run) objetosEnTree[i][1];
                break;
            }
        }
        if (j.oRunFromMainForm.getRequiereUpdate()==true) {
            s="La Corrida "+currentTreeNode+" fue sincronizada desde la BD FTL. \n";
            s+="Se requiere confirmar/completar la informacionmediante una actualizacion.\n";
            s+="Actualize la corrida y vuelva a intentarlo.";
            msgbox(s);
            return false;
        }
        j.setParam(oBD);
        j.setLocationRelativeTo(this);
        j.setVisible(true);
        muestraPozo(currentPozoId);
        return true;
    }
    
        private void reUbicacionEnArbol() {
        DefaultMutableTreeNode previousNode 
           =(DefaultMutableTreeNode)this.jTree1.getSelectionPath().getLastPathComponent();
        LlenaArbol();
        DefaultMutableTreeNode currentNode = tnRaiz.getNextNode();
        do {
           if (currentNode.toString().equals(previousNode.toString())){
               if (currentNode.getParent().toString().equals(previousNode.getParent().toString())) {
                    this.jTree1.expandPath(new TreePath(currentNode.getPath()));
                    this.jTree1.setSelectionPath(new TreePath(currentNode.getPath()));
                    break;
               }
           }
           currentNode = currentNode.getNextNode(); 
        } 
        while (currentNode != null);        
    }
    
    public void cursorNormal(){
        this.setCursor(Cursor.getDefaultCursor());
    }
    public void cursorEspera(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    private int encriptarNivel(String s,int i) {
        int nivelEncriptado=0;
        int j=0;
        if (i==1) j=9748;
        if (i==2) j=15623;
        if (i==3) j=3276;
        nivelEncriptado=s.charAt(0)+s.charAt(3)+s.charAt(2)+j;
        return nivelEncriptado;
    }
    
    private int desencriptarNivel(String s,int i) {
        int nivelDesencriptado=0;
        int j=0;
        j=i-s.charAt(0)-s.charAt(3)-s.charAt(2);
        if (j==9748) nivelDesencriptado=1;
        if (j==15623) nivelDesencriptado=2;
        if (j==3276) nivelDesencriptado=3;
        return nivelDesencriptado;        
    }
    
    private void jRadioButtonPlanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPlanActionPerformed
        llamarManPpalonOff();
    }//GEN-LAST:event_jRadioButtonPlanActionPerformed

    private void jRadioButtonLASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonLASActionPerformed
        llamarManPpalonOff();
    }//GEN-LAST:event_jRadioButtonLASActionPerformed

    private void jRadioButtonSlideSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSlideSheetActionPerformed
        llamarManPpalonOff();
    }//GEN-LAST:event_jRadioButtonSlideSheetActionPerformed

    private void jRadioButtonBHAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonBHAActionPerformed
        llamarManPpalonOff();
    }//GEN-LAST:event_jRadioButtonBHAActionPerformed

    private void jRadioButtonPozoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButtonPozoStateChanged
        llamarManPpalonOff();            
    }//GEN-LAST:event_jRadioButtonPozoStateChanged

    private void jRadioButtonSeccionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSeccionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonSeccionesActionPerformed

    private void jRadioButtonSeccionesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButtonSeccionesStateChanged
        // TODO add your handling code here:
        llamarManPpalonOff();
    }//GEN-LAST:event_jRadioButtonSeccionesStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        oBD.desconectar();
    }//GEN-LAST:event_formWindowClosing

    private void jRadioButtonCorridasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCorridasActionPerformed
        llamarManPpalonOff();      
    }//GEN-LAST:event_jRadioButtonCorridasActionPerformed

    private void jRadioButtonSurveyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSurveyActionPerformed
        llamarManPpalonOff();
    }//GEN-LAST:event_jRadioButtonSurveyActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        msgbox(msgVersion,"About LoadWellData");
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jRadioButtonMacollaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButtonMacollaStateChanged
        llamarManPpalonOff(); 
    }//GEN-LAST:event_jRadioButtonMacollaStateChanged
    
    private void llamarManPpalonOff() {
        this.jButtonLlamarMantPpal.setEnabled(false);
        
        if (this.jRadioButtonMacolla.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonPozo.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonSecciones.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }   
        if (this.jRadioButtonCorridas.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonBHA.isSelected()) {
            
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonSurvey.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonPlan.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonSlideSheet.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
        if (this.jRadioButtonLAS.isSelected()) {
            this.jButtonLlamarMantPpal.setEnabled(true);    
        }
    }
    
    public void msgbox(String s, String t){
        JOptionPane.showMessageDialog(null, s,t,MessageType.WARNING.ordinal());        
    }
    
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
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonLlamarMantPpal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelPozo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButtonBHA;
    private javax.swing.JRadioButton jRadioButtonCorridas;
    private javax.swing.JRadioButton jRadioButtonDummy;
    private javax.swing.JRadioButton jRadioButtonLAS;
    private javax.swing.JRadioButton jRadioButtonMacolla;
    private javax.swing.JRadioButton jRadioButtonPlan;
    private javax.swing.JRadioButton jRadioButtonPozo;
    private javax.swing.JRadioButton jRadioButtonSecciones;
    private javax.swing.JRadioButton jRadioButtonSlideSheet;
    private javax.swing.JRadioButton jRadioButtonSurvey;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
class Niveles {
    public static final int nivelPais=0;
    public static final int nivelLocation=1;
    public static final int nivelDivision=2;
    public static final int nivelCliente=3;
    public static final int nivelCampo=4;
    public static final int nivelMacolla=5;
    public static final int nivelPozo=6;
    public static final int nivelSeccion=7;
    public static final int nivelCorrida=8;
}

class MyTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        String node = (String) ((DefaultMutableTreeNode) value).getUserObject();

        // If the node is a leaf and ends with "*"
        if (node.endsWith("*")) {
            // Paint the node in blue
            this.setForeground(java.awt.Color.RED);
            this.setBackgroundNonSelectionColor(java.awt.Color.GRAY);
        }
        
        return this;
    }
}