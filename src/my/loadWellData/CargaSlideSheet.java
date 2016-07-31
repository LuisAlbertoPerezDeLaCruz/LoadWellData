/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import miLibreria.bd.*;
import org.jdom2.Document;         // |
import org.jdom2.Element;          // |\ Librerías
import org.jdom2.JDOMException;    // |/ JDOM
import org.jdom2.input.SAXBuilder; // |

/**
 *
 * @author Luis
 */

public class CargaSlideSheet extends CargaArchivoExcel implements miLibreria.GlobalConstants {
    public ManejoBDI oBD;
    public SlideSheetPerMD oSlideSheetPerMD;
    public Run oRunFromMainForm=new Run();
    public Run oRun;
    private java.awt.Frame parent1;
    private Object[][] campos;
    private CamposEscojidosSS cp=new CamposEscojidosSS();
    private final int MOTOR=1;
    private final int RSS=2;
    private final int RSSXCEED=3;
    private final int NADA=0;
    private int tipoDT=NADA;
    private BHA oBHA;
    
    public CargaSlideSheet(java.awt.Frame parent, boolean modal) {        
        super(parent, modal);
        initComponents();
        oRun=new Run();               
        parent1=parent;
    }
    
    public long getSlideSheetId() {
        ResultSet rs;
        long slideSheetId=-1; 
        SlideSheet oSlideSheet=new SlideSheet();
        rs=oBD.select(oSlideSheet, "runId="+oRun.getId());
        try {
            while (rs.next()){
                slideSheetId=rs.getLong("Id");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return slideSheetId;
    }
    
    @Override
    public void mostrar() {
        long slideSheetId=getSlideSheetId();
        
        if (slideSheetId>-1) {
            MuestraSlideSheetPerMD j=new MuestraSlideSheetPerMD(parent1,true);
            j.slideSheetId=slideSheetId;
            j.setParam(oBD);
            j.cargaTabla();
            j.setLocationRelativeTo(this);
            j.setVisible(true);
        }                
        else
            msgbox("No se puede mostrar el SlideSheet de esta corrida porque no se ha cargado.");
    }
    
    @Override
    public void desHacer() {
        long slideSheetId=getSlideSheetId();
        
        if (slideSheetId!=valorNulo) {
            oBD.delete(new SlideSheetPerMD(), "slideSheetId="+slideSheetId);
            oBD.delete(new SlideSheet(), "Id="+slideSheetId);
            oBD.delete(new DrillingSummary(),"runId="+oRun.getId());
            msgbox("Accion DesHacer culminada con exito.");
            status=VACIO;
            this.jTextAreaArchivo.setText("");
            setearControles();
        }                
        else
            msgbox("No se puede dehacer el SlideSheet de esta corrida porque no se ha cargado.");
        oBD.setHuboCambios(true);            
    }
    
    public void setParam(ManejoBDI oBDIn) {
        oBD=oBDIn;
        oBHA=new BHA();
        
        try {
            oRun=(Run) oBD.select(Run.class, "id="+oRunFromMainForm.getId())[0];
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            oBHA=(BHA) oBD.select(BHA.class, "runId="+oRun.getId())[0];
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (getSlideSheetId()==-1) {
            status=VACIO;
        } else {
          status=CARGADO;  
        }
        setearControles();
    }
    
    @Override
    public void procesar() {
        SlideSheet oSlideSheet=new SlideSheet();
        double desde=0,hasta=0;
        String valor;
        ResultSet rs;
        int i0,i1;        
        long slideSheetId=valorNulo;
        boolean ok=false;
        SlideSheetPerMD[] aSlideSheetPerMD;
        
        i0=17;
        i1=0;
        
        if (noEsExcel) {
            cursorEspera();
            aSlideSheetPerMD=procesarXML();            
            rs=oBD.select(oSlideSheet, "runId="+oRun.getId());
            try {
                while (rs.next()){
                    slideSheetId=rs.getLong("Id");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (slideSheetId==valorNulo) {
               oSlideSheet=new SlideSheet();
               oSlideSheet.setRunId(oRun.getId());
               if (oBD.insert(oSlideSheet)) {
                   try {
                       oSlideSheet=(SlideSheet) oBD.select(SlideSheet.class, "runId="+oRun.getId())[0];
                       slideSheetId=oSlideSheet.getId();
                   } catch (InstantiationException | IllegalAccessException ex) {
                       Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            }else {
                oBD.delete(new SlideSheetPerMD(), "slideSheetId="+slideSheetId);
                oBD.delete(new DrillingSummary(),"runId="+oRun.getId());
            }
            if (slideSheetId!=valorNulo) {
                for (int i=0;i<=aSlideSheetPerMD.length-1;i++) {
                    if(aSlideSheetPerMD[i].getMdFrom()>=this.oRun.getInitialDepth() && aSlideSheetPerMD[i].getMdTo()<=this.oRun.getFinalDepth()) {
                       aSlideSheetPerMD[i].setSlideSheetId(slideSheetId);
                       oBD.insert(aSlideSheetPerMD[i]);
                    }
                }
            }
            this.procesarDiretionalDrillingSumary(aSlideSheetPerMD);
            oBD.setHuboCambios(true);
            cursorNormal();
            msgbox("SlideSheet cargado exitosamente");
            status=CARGADO;
            setearControles();
            this.jTextAreaArchivo.setText("");
            return;
        }
        
        establecerColumnas(i0);
        
        if (oXL.abrirArchivo()) {
            oXL.establecerHoja(0);
            valor=oXL.valorCelda(8,1);
            if ("Borehole".equals(valor.trim()))
                ok=true;
            if (!ok) {
                msgbox("Archivo seleccionado no es un archivo SlideSheet.");
                return;
            }
            i1=oXL.getLimites()[0];
            for (int i=i0;i<i1;i++) {
               valor=oXL.valorCelda(i,(int) campos[cp.MDFrom][1]);
               if (valor.trim()=="" || valor=="null" ) {
                   i1=i-1;
                   break;                  
               }
            }

            rs=oBD.select(oSlideSheet, "runId="+oRun.getId());
            try {
                while (rs.next()){
                    slideSheetId=rs.getLong("Id");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (slideSheetId==valorNulo) {
               oSlideSheet=new SlideSheet();
               oSlideSheet.setRunId(oRun.getId());
               if (oBD.insert(oSlideSheet)) {
                   try {
                       oSlideSheet=(SlideSheet) oBD.select(SlideSheet.class, "runId="+oRun.getId())[0];
                       slideSheetId=oSlideSheet.getId();
                   } catch (InstantiationException | IllegalAccessException ex) {
                       Logger.getLogger(CargaSlideSheet.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            }else {
                oBD.delete(new SlideSheetPerMD(), "slideSheetId="+slideSheetId);
                oBD.delete(new DrillingSummary(),"runId="+oRun.getId());
            }
            if (slideSheetId!=valorNulo) {
                procesarSlideSheetDeLaSeccion(i0, i1, slideSheetId, this.oRun.getInitialDepth(), this.oRun.getFinalDepth());
            }
            else
                msgbox("No se pudo incluir el SlideSheet. Error al insertar SlideSheet en BD.");
        }  
        oBD.setHuboCambios(true);
    }
    
    @Override
    public boolean archivoValido() {
        boolean ok=false;
        if (noEsExcel) {
            tipoDT=("MOTOR".equals(oBHA.getTipoDT()))?MOTOR:RSS;
            //Se crea un SAXBuilder para poder parsear el archivo
            SAXBuilder builder = new SAXBuilder();
            File xmlFile = selectedFile;
            SlideSheetPerMD[] aSlideSheetPerMD=null;
            try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement();
            if ("SteeringSheet".equals(rootNode.getName())) {
                return true;
            }
            } catch (Exception ex) {
                return false;
            }
            return false;
        }
        String valor;
        try {
        if (oXL.abrirArchivo()) {
            oXL.establecerHoja(0);
            valor=oXL.valorCelda(8,1);
            if ("Borehole".equals(valor.trim())) {
                
                campos=new Object [][] {
                    {"Start Time",-1},
                    {"End Time",-1},
                    {"MD From",-1},
                    {"MD To",-1},
                    {"Drilling Mode",-1},
                    {"TF Mode",-1},
                    {"TF Angle",-1},
                    {"Flow",-1},
                    {"SPP Off Bott",-1},
                    {"SPP On Bott",-1},
                    {"WOB",-1},
                    {"SRPM",-1},
                    {"Torque",-1},
                    {"Off Bot Torque",-1},
                    {"Operation Mode",-1},
                    {"Power Setting",-1},
                    {"Desired Power Setting",-1},
                    {"Desired TF Angle",-1},
                };
                
                tipoDT=("MOTOR".equals(oBHA.getTipoDT()))?MOTOR:RSS;
                
                if (tipoDT==MOTOR) {
                    for (int i=0;i<=oXL.getLimites()[1];i++) {
                        valor=oXL.valorCelda(15,i);
                        if ("Drilling Mode".equals(valor) || "Operation Mode".equals(valor) )  {
                            ok=true;
//                            if ("Operation Mode".equals(valor)) {
//                                campos[4][0]="Operation Mode";
//                            }
                        }
                    }
                } else {                    
                    for (int i=0;i<=oXL.getLimites()[1];i++) {
                        valor=oXL.valorCelda(15,i);
                        if ("Desired Power Setting".equals(valor)) {
                            tipoDT=RSSXCEED;
                            ok=true;
                        }
                        if ("Power Setting".equals(valor)) {
                            tipoDT=RSS;
                            ok=true;
                        }
                    }                    
                }
                return ok;
            }
        }
        } catch (NullPointerException ex) {}
        return false;
    }
    
    private void establecerColumnas(int i0) {
        String valor;
        if (oXL.abrirArchivo()) {
            oXL.establecerHoja(0); 
            for (int j=0;j<=campos.length-1;j++) {
                for (int j1=0;j1<=100;j1++) {
                    valor=oXL.valorCelda(i0-2,j1);
                    if (valor.equals(campos[j][0])) {
                        campos[j][1]=j1;
                        break;
                    }
                }
            }
        }            
    }
    
   
    public void msgbox(String s){
        JOptionPane.showMessageDialog(null, s);        
    }
    
    public void procesarSlideSheetDeLaSeccion(int i0, int i1, long slideSheetId, double valorDesde, double valorHasta) {
        String valor;
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        cursorEspera();
        for (int i=i0;i<i1;i++) {
            oSlideSheetPerMD=new SlideSheetPerMD();
            oSlideSheetPerMD.setDrillingMode("");
            oSlideSheetPerMD.setOperationMode("");
            for (int j=0;j<=(tipoDT==MOTOR?25:26);j++) {
                valor=oXL.valorCelda(i,j);
                if ("N/A".equals(valor)) valor="0";
                try {
                    if ((int) campos[cp.StartTime][1]==j) {
                        oSlideSheetPerMD.setFecha(f.parse(valor));
                        oSlideSheetPerMD.setStartTime(f.parse(valor));
                    }
                    if ((int) campos[cp.EndTime][1]==j) {
                        oSlideSheetPerMD.setEndTime(f.parse(valor));
                    }
                    if ((int) campos[cp.MDFrom][1]==j) {
                        oSlideSheetPerMD.setMdFrom(new Double(valor));
                    }
                    if ((int) campos[cp.MDTo][1]==j) {
                        oSlideSheetPerMD.setMdTo(new Double(valor));
                    }
                    if ((int) campos[cp.DrillingMode][1]==j) {
                        oSlideSheetPerMD.setDrillingMode(valor);
                    }
                    if ((int) campos[cp.TFMode][1]==j) {
                        oSlideSheetPerMD.setTfMode(valor);
                    }
                    if ((int) campos[cp.TFAngle][1]==j) {
                        oSlideSheetPerMD.setTfAngle(Math.round(new Double(valor)));
                    } 
                    if ((int) campos[cp.WOB][1]==j) {
                        oSlideSheetPerMD.setWob(new Double(valor));
                    }
                    if ((int) campos[cp.SRPM][1]==j) {
                        oSlideSheetPerMD.setSrpm(Math.round(new Double(valor)));
                    }
                    if ((int) campos[cp.Flow][1]==j) {
                        oSlideSheetPerMD.setFlow(Math.round(new Double(valor)));
                    }
                    if ((int) campos[cp.SPPOffBott][1]==j) {
                        oSlideSheetPerMD.setSppOffBott(new Double(valor));
                    }
                    if ((int) campos[cp.SPPOnBott][1]==j) {
                        oSlideSheetPerMD.setSppOnBott(new Double(valor));
                    }
                    if ((int) campos[cp.Torque][1]==j) {
                        oSlideSheetPerMD.setTorque(new Double(valor));
                    }
                    if ((int) campos[cp.OffBotTorque][1]==j) {
                        oSlideSheetPerMD.setOffBotTorque(new Double(valor)); 
                    } 
                    if ((int) campos[cp.OperationMode][1]==j) {
                        oSlideSheetPerMD.setOperationMode(valor); 
                    }
                    if ((int) campos[cp.PowerSetting][1]==j) {
                        oSlideSheetPerMD.setPowerSetting(new Double(valor)); 
                    }
                    if ((int) campos[cp.DesiredPowerSetting][1]==j) {
                        //oSlideSheetPerMD.setDesiredPowerSetting(new Double(valor));
                        //Como es lo mismo (powerSetting y desiredPowerSetting lo pongo en el primero)
                        oSlideSheetPerMD.setPowerSetting(new Double(valor));
                    }
                    if ((int) campos[cp.TFAngle][1]==j) {
                        oSlideSheetPerMD.setTfAngle(new Long(valor)); 
                    }
                    if ((int) campos[cp.DesiredTfAngles][1]==j) {
                        oSlideSheetPerMD.setDesiredTfAngles(new Double(valor)); 
                    }
                    
                } catch (NumberFormatException | ParseException ex) {valor=null;};
                oSlideSheetPerMD.setSlideSheetId(slideSheetId);
            }
            if (oSlideSheetPerMD.getMdFrom()>=valorDesde && oSlideSheetPerMD.getMdTo()<valorHasta) {
                oBD.insert(oSlideSheetPerMD);
            }
        }
        procesarDiretionalDrillingSumary(i0,i1);
        cursorNormal();
        msgbox("SlideSheet cargado exitosamente");
        status=CARGADO;
        setearControles();
        this.jTextAreaArchivo.setText("");
    }
    
   private void procesarDiretionalDrillingSumary(SlideSheetPerMD[] aSlideSheetPerMD) {
       DrillingSummary oDS=new DrillingSummary();
       String tipo="";
       double deltaMd=0.0, totalDeltaMd=0.0, totalHours=0.0, totalRop=0.0;
       double elapsedTime=0.0, totalElapsedTime=0.0;
       Object [][] o=new Object[10][3];
       int p=0, nextP=0;
       boolean existe;
       for (p=0;p<=o.length-1;p++) {
           o[p][0]="";
           o[p][1]=0.0;
           o[p][2]=0.0;
       }
       for (int i=0;i<=aSlideSheetPerMD.length-1;i++) {
           if (aSlideSheetPerMD[i].getMdFrom()<=this.oRun.getFinalDepth()) {
                tipo=aSlideSheetPerMD[i].getDrillingMode().trim();
                if (tipo.isEmpty()) {
                   tipo=aSlideSheetPerMD[i].getOperationMode().trim();
                }
                deltaMd=aSlideSheetPerMD[i].getMdTo()-aSlideSheetPerMD[i].getMdFrom();
                elapsedTime=aSlideSheetPerMD[i].getEndTime().getTime()-aSlideSheetPerMD[i].getStartTime().getTime();
                existe=false;
                for (int j=0;j<=o.length-1;j++){
                    if (o[j][0].toString().isEmpty()) {
                        nextP=j;
                        break;
                    }
                    if (tipo.equals((o[j][0]).toString().trim())) {
                         o[j][0]=tipo;
                         o[j][1]=(double) o[j][1]+deltaMd;
                         o[j][2]=(double) o[j][2]+elapsedTime; 
                         existe=true;
                         break;
                    }
                }
                if (!existe) {
                     o[nextP][0]=tipo;
                     o[nextP][1]=(double) o[nextP][1]+deltaMd;
                     o[nextP][2]=(double) o[nextP][2]+elapsedTime;                
                }
                totalElapsedTime+=elapsedTime;
                totalDeltaMd+=deltaMd;
            }              
        }

       totalHours=(totalElapsedTime/1000)/3600;
       totalRop=totalDeltaMd/totalHours;
       try {
            for (p=0;p<=o.length-1;p++) {
                if (o[p][0].toString().isEmpty()) break;
                 oDS=new DrillingSummary(); 
                 oDS.setRunId(oRun.getId());
                 oDS.setNombre("");
                 oDS.setDrillMode(o[p][0].toString().trim()); 
                 oDS.setFootage((double)o[p][1]);
                 Double d=new Double(o[p][2].toString());
                 Double h=(d/1000)/3600;
                 oDS.setHrs(h);
                 oDS.setRop(oDS.getFootage()/oDS.getHrs());
                 oDS.setPercentDrill(oDS.getFootage()/totalDeltaMd);
                 oBD.insert(oDS);
            }
            oDS=new DrillingSummary(); 
            oDS.setRunId(oRun.getId());
            oDS.setNombre("");
            oDS.setDrillMode("Drilling"); 
            oDS.setFootage(totalDeltaMd);
            oDS.setHrs(totalHours);
            oDS.setRop(totalRop);
            oDS.setPercentDrill(1);
            oBD.insert(oDS);
            
       } catch (NullPointerException | NumberFormatException | StringIndexOutOfBoundsException ex) {}         
   }
    
   private void procesarDiretionalDrillingSumary(int i0, int i1) {
        String valor;
        DrillingSummary oDS=new DrillingSummary();

        i0=i1; // empieza donde quedo
        i1=oXL.getLimites()[0]; //llega hasta el final de la hoja
        for (int i=i0;i<i1;i++) {
            valor=oXL.valorCelda(i,1);
            if ("DIRECTIONAL DRILLING PERFORMANCE SUMMARY".equals(valor)) {
                i0=i; //establece nuevamente el inicio
                break;
            }
        }
        i0+=2;
        for (int i=i0;i<i1;i++) {
            valor=oXL.valorCelda(i,1).trim();
            if (!valor.isEmpty()) {
                try {
                    oDS=new DrillingSummary(); 
                    oDS.setRunId(oRun.getId());
                    oDS.setNombre("");
                    oDS.setDrillMode(oXL.valorCelda(i,1).trim()); 
                    oDS.setFootage(new Double(oXL.valorCelda(i,3).trim()));
                    oDS.setHrs(new Double(oXL.valorCelda(i,4).trim()));
                    oDS.setRop(new Double(oXL.valorCelda(i,5).trim()));
                    if (oXL.valorCelda(i,6).trim().length()>0)
                        oDS.setPercentDrill(new Double(oXL.valorCelda(i,6).trim().substring(0, oXL.valorCelda(i,6).trim().length()-1))); //quita el simbolo %
                    else 
                        oDS.setPercentDrill(0.0);
                    oBD.insert(oDS);
                } catch (NullPointerException | NumberFormatException | StringIndexOutOfBoundsException ex) {break;}
            } else break;
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(CargaSlideSheet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CargaSlideSheet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CargaSlideSheet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CargaSlideSheet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CargaSlideSheet dialog = new CargaSlideSheet(new javax.swing.JFrame(), true);
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
 
    
private int indexOfList(List list,String nombre) {
    int p=-1;
    for (int i=0;i<=list.size()-1;i++) {
        Element e=(Element) list.get(i);
        if (nombre.equals(e.getName())) {
            p=i;
            break;
        }
    }
    return p;
}
    
 private SlideSheetPerMD[] procesarXML() {
        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = selectedFile;
        SlideSheetPerMD[] aSlideSheetPerMD=null;
        int contador=0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement(); //SteeringSheet
            
            List listRoot = rootNode.getChildren();
            if (indexOfList(listRoot,"bhaRuns")==-1) return null;
            Element bhaRunsNode=(Element) listRoot.get(indexOfList(listRoot,"bhaRuns")); //bhaRuns
                    
            List listbhaRuns=bhaRunsNode.getChildren();
            if (indexOfList(listbhaRuns,"bhaRun")==-1) return null;
            Element bhaRunNode=(Element) listbhaRuns.get(indexOfList(listbhaRuns,"bhaRun")); //bhaRun
            
            List listbhaRun=bhaRunNode.getChildren();
            if (indexOfList(listbhaRun,"steeringSheetRows")==-1) return null;
            Element steeringSheetRowsNode=(Element) listbhaRun.get(indexOfList(listbhaRun,"steeringSheetRows")); //steeringSheetRows
            
            List listSteeringSheetRows=steeringSheetRowsNode.getChildren();
            
            aSlideSheetPerMD=new SlideSheetPerMD[listSteeringSheetRows.size()];
            
            String tipoMedida="";
            Double factor=1.0;
            
            for ( int i=0; i<=listSteeringSheetRows.size()-1; i++ ) {
                Element rowNode = (Element) listSteeringSheetRows.get(i);                
                List listRowAttributes=rowNode.getChildren();                
                oSlideSheetPerMD=new SlideSheetPerMD();
                for (int j=0;j<=listRowAttributes.size()-1;j++) {
                    Element atributo=(Element) listRowAttributes.get(j);
                    String nombreAtributo=atributo.getName();
                    String contenidoAtributo=atributo.getTextTrim();
                    if (contenidoAtributo.isEmpty()) continue;
                    if (atributo.getAttributes().size()>0) {
                        if ("uom".equals(atributo.getAttributes().get(0).getName())) {
                           tipoMedida=atributo.getAttributes().get(0).getValue();
                           if ("m".equals(tipoMedida)) {
                               factor=3.2808398950;
                               Double valor=new Double(contenidoAtributo)*factor;
                               contenidoAtributo=valor.toString();
                           }
                           if ("rad".equals(tipoMedida)) {
                               Double valor=new Double(contenidoAtributo);
                               valor=Math.toDegrees(valor);
                               contenidoAtributo=valor.toString();
                           }
                           if ("rad/m".equals(tipoMedida)) {
                              factor=17.4637535955701*100;
                              Double valor=new Double(contenidoAtributo)*factor;
                              contenidoAtributo=valor.toString();
                           }                               
                        }
                    }
                    switch (nombreAtributo) {
                        case "StartTime" :                        
                        try {
                            oSlideSheetPerMD.setStartTime(f.parse(contenidoAtributo.replace('T', ' ')));
                            oSlideSheetPerMD.setFecha(oSlideSheetPerMD.getStartTime());
                        } catch (ParseException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                        }                        
                        break;
                        case "EndTime" :                        
                        try {
                            oSlideSheetPerMD.setEndTime(f.parse(contenidoAtributo.replace('T', ' ')));
                        } catch (ParseException ex) {
                            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                        }                        
                        break;
                        case "StartDepth" :
                            oSlideSheetPerMD.setMdFrom(new Double(contenidoAtributo));
                        break;
                        case "EndDepth" :
                            oSlideSheetPerMD.setMdTo(new Double(contenidoAtributo));
                        break;
                        case "PowerSetting" :
                            oSlideSheetPerMD.setPowerSetting(new Double(contenidoAtributo));
                        break;
                        case "DesiredPowerSetting" :
                            oSlideSheetPerMD.setDesiredPowerSetting(new Double(contenidoAtributo));
                        break; 
                        case "OrientingMode" : //Hay que discriminar si es RSS o Motor
                            tipoDT=("MOTOR".equals(oBHA.getTipoDT()))?MOTOR:RSS;
                            if ("MOTOR".equals(tipoDT)) {
                                oSlideSheetPerMD.setDrillingMode(contenidoAtributo);
                            } else
                                oSlideSheetPerMD.setOperationMode(contenidoAtributo);
                        break;
                        case "Toolface" :
                            oSlideSheetPerMD.setTfAngle(Math.round(new Double(contenidoAtributo)));
                        break;
                        case "ToolfaceMode" :
                            oSlideSheetPerMD.setTfMode(contenidoAtributo);
                        break;                                
                        case "DesiredToolface" :
                            oSlideSheetPerMD.setDesiredTfAngles(Math.round(new Double(contenidoAtributo)));
                        break;
                        case "MudFlowrateIn" :
                            oSlideSheetPerMD.setFlow(Math.round(new Double(contenidoAtributo)));
                        break;
                        case "SPP_OnBottom" :
                            oSlideSheetPerMD.setSppOnBott(new Double(contenidoAtributo));
                        break;
                        case "SPP_OffBottom" :
                            oSlideSheetPerMD.setSppOffBott(new Double(contenidoAtributo));
                        break;
                        case "SurfaceTorqueOnBottom" :                            
                            oSlideSheetPerMD.setTorque(new Double(contenidoAtributo));
                        break; 
                        case "SurfaceTorqueOffBottom" :                            
                            oSlideSheetPerMD.setOffBotTorque(new Double(contenidoAtributo));
                        break; 
                        case "SurfaceWeightOnBit" :
                            oSlideSheetPerMD.setWob(new Double(contenidoAtributo));
                        break;
                        case "SurfaceRotarySpeed" :
                            oSlideSheetPerMD.setSrpm(Math.round(new Double(contenidoAtributo)));
                    }
                }
                aSlideSheetPerMD[contador++]=oSlideSheetPerMD;

            }
                      
        } catch ( IOException io ) {
            System.out.println( io.getMessage() );
        }catch ( JDOMException jdomex ) {
            System.out.println( jdomex.getMessage() );
        }
        return aSlideSheetPerMD;
    }
   
    
    private SlideSheetPerMD[] procesarXMLAnterior() {
        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = selectedFile;
        SlideSheetPerMD[] aSlideSheetPerMD=null;
        int contador=0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement();
            
            aSlideSheetPerMD=new SlideSheetPerMD[getCantRows(rootNode)];
            
            List listRoot = rootNode.getChildren("bhaRuns");
            Element bhaRunsNode=(Element) listRoot.get(0);
                    
            List listBhaRuns=bhaRunsNode.getChildren();
            
            String tipoMedida="";
            Double factor=1.0;
            
            for ( int i=0; i<=listBhaRuns.size()-1; i++ ) {
                Element bhaRunNode = (Element) listBhaRuns.get(i);
                
                List listSteeringSheetRows=bhaRunNode.getChildren("steeringSheetRows");
                Element steeringSheetRows=(Element) listSteeringSheetRows.get(0);
                
                List listRows=steeringSheetRows.getChildren();
                for ( int j=0; j<= listRows.size()-1; j++ ) {
                    Element row = (Element) listRows.get(j);                    
                    List listRowAttributes=row.getChildren();
                    oSlideSheetPerMD=new SlideSheetPerMD();
                    for (int k=0;k<=listRowAttributes.size()-1;k++) {
                        Element atributo=(Element) listRowAttributes.get(k);
                        String nombreAtributo=atributo.getName();
                        String contenidoAtributo=atributo.getTextTrim();
                        if (contenidoAtributo.isEmpty()) continue;
                        if (atributo.getAttributes().size()>0) {
                            if ("uom".equals(atributo.getAttributes().get(0).getName())) {
                               tipoMedida=atributo.getAttributes().get(0).getValue();
                               if ("m".equals(tipoMedida)) {
                                   factor=3.2808398950;
                                   Double valor=new Double(contenidoAtributo)*factor;
                                   contenidoAtributo=valor.toString();
                               }
                               if ("rad".equals(tipoMedida)) {
                                   Double valor=new Double(contenidoAtributo);
                                   valor=Math.toDegrees(valor);
                                   contenidoAtributo=valor.toString();
                               }
                               if ("rad/m".equals(tipoMedida)) {
                                  factor=17.4637535955701*100;
                                  Double valor=new Double(contenidoAtributo)*factor;
                                  contenidoAtributo=valor.toString();
                               }                               
                            }
                        }
                        switch (nombreAtributo) {
                            case "StartTime" :                        
                            try {
                                oSlideSheetPerMD.setStartTime(f.parse(contenidoAtributo.replace('T', ' ')));
                                oSlideSheetPerMD.setFecha(oSlideSheetPerMD.getStartTime());
                            } catch (ParseException ex) {
                                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                            }                        
                            break;
                            case "EndTime" :                        
                            try {
                                oSlideSheetPerMD.setEndTime(f.parse(contenidoAtributo.replace('T', ' ')));
                            } catch (ParseException ex) {
                                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                            }                        
                            break;
                            case "StartDepth" :
                                oSlideSheetPerMD.setMdFrom(new Double(contenidoAtributo));
                            break;
                            case "EndDepth" :
                                oSlideSheetPerMD.setMdTo(new Double(contenidoAtributo));
                            break;
                            case "PowerSetting" :
                                oSlideSheetPerMD.setPowerSetting(new Double(contenidoAtributo));
                            break;
                            case "DesiredPowerSetting" :
                                oSlideSheetPerMD.setDesiredPowerSetting(new Double(contenidoAtributo));
                            break; 
                            case "OrientingMode" : //Hay que discriminar si es RSS o Motor
                                tipoDT=("MOTOR".equals(oBHA.getTipoDT()))?MOTOR:RSS;
                                if ("MOTOR".equals(tipoDT)) {
                                    oSlideSheetPerMD.setDrillingMode(contenidoAtributo);
                                } else
                                    oSlideSheetPerMD.setOperationMode(contenidoAtributo);
                            break;
                            case "Toolface" :
                                oSlideSheetPerMD.setTfAngle(Math.round(new Double(contenidoAtributo)));
                            break;
                            case "ToolfaceMode" :
                                oSlideSheetPerMD.setTfMode(contenidoAtributo);
                            break;                                
                            case "DesiredToolface" :
                                oSlideSheetPerMD.setDesiredTfAngles(Math.round(new Double(contenidoAtributo)));
                            break;
                            case "MudFlowrateIn" :
                                oSlideSheetPerMD.setFlow(Math.round(new Double(contenidoAtributo)));
                            break;
                            case "SPP_OnBottom" :
                                oSlideSheetPerMD.setSppOnBott(new Double(contenidoAtributo));
                            break;
                            case "SPP_OffBottom" :
                                oSlideSheetPerMD.setSppOffBott(new Double(contenidoAtributo));
                            break;
                            case "SurfaceTorqueOnBottom" :                            
                                oSlideSheetPerMD.setTorque(new Double(contenidoAtributo));
                            break; 
                            case "SurfaceTorqueOffBottom" :                            
                                oSlideSheetPerMD.setOffBotTorque(new Double(contenidoAtributo));
                            break; 
                            case "SurfaceWeightOnBit" :
                                oSlideSheetPerMD.setWob(new Double(contenidoAtributo));
                            break;
                            case "SurfaceRotarySpeed" :
                                oSlideSheetPerMD.setSrpm(Math.round(new Double(contenidoAtributo)));
                        }
                    }
                    aSlideSheetPerMD[contador++]=oSlideSheetPerMD;
                }
            }
                      
        } catch ( IOException io ) {
            System.out.println( io.getMessage() );
        }catch ( JDOMException jdomex ) {
            System.out.println( jdomex.getMessage() );
        }
        return aSlideSheetPerMD;
    }
    
    private int getCantRows(Element rootNode) {
        int cantRows=0;

        List listRoot = rootNode.getChildren("bhaRuns");
        Element bhaRunsNode=(Element) listRoot.get(0);

        List listBhaRuns=bhaRunsNode.getChildren();
        for ( int i=0; i<=listBhaRuns.size()-1; i++ ) {
            Element bhaRunNode = (Element) listBhaRuns.get(i);

            List listSteeringSheetRows=bhaRunNode.getChildren("steeringSheetRows");
            Element steeringSheetRows=(Element) listSteeringSheetRows.get(0);

            List listRows=steeringSheetRows.getChildren();
            for ( int j=0; j<= listRows.size()-1; j++ ) {
                cantRows++;
            }
        } 
        
        return cantRows;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

class CamposEscojidosSS {
    final int StartTime=0;
    final int EndTime=1;
    final int MDFrom=2;
    final int MDTo=3;
    final int DrillingMode=4;
    final int TFMode=5;
    final int TFAngle=6;
    final int Flow=7;
    final int SPPOffBott=8;
    final int SPPOnBott=9;
    final int WOB=10;
    final int SRPM=11;
    final int Torque=12;
    final int OffBotTorque=13;
    final int OperationMode=14;
    final int PowerSetting=15;
    final int DesiredPowerSetting=16;
    final int DesiredTfAngles=17;
}

