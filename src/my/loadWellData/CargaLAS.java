/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import miLibreria.bd.*;
import static my.loadWellData.CargaArchivoExcel.SELECCION_INVALIDA;
import static my.loadWellData.CargaArchivoExcel.SELECCION_VALIDA;

/**
 *
 * @author Luis
 */

public class CargaLAS extends CargaArchivoExcel implements miLibreria.GlobalConstants{
    public ManejoBDI oBD;
    public LASPerMD oLASPerMD;
    public Run oRunFromMainForm=new Run();
    public Run oRun;
    private java.awt.Frame parent1;
    private int i0=0;
    private int posDEPT,posGR,posP40H;
    private int posTVDE, posCRPM, posAZIM_CONT, posINCL_CONT;
    

    
    public CargaLAS(java.awt.Frame parent, boolean modal) {        
        super(parent, modal);
        initComponents();
        oRun=new Run();                       
        parent1=parent;
        this.jToolBar1.add(jProgressBar); 
    }
    
    public long getLASId() {
        ResultSet rs;
        long lasId=-1; 
        LAS oLAS=new LAS();
        rs=oBD.select(oLAS, "runId="+oRun.getId());
        try {
            while (rs.next()){
                lasId=rs.getLong("Id");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargaLAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lasId;
    }
    
    @Override
    public void mostrar() {
        long lasId=getLASId();
        
        if (lasId>-1) {
            MuestraLASPerMD j=new MuestraLASPerMD(parent1,true);
            j.lasId=lasId;
            j.setParam(oBD);
            j.cargaTabla();
            j.setLocationRelativeTo(this);
            j.setVisible(true);
        }                
        else
            msgbox("No se puede mostrar el LAS de esta corrida porque no se ha cargado.");
    }
    
    @Override
    public void desHacer() {
        long lasId=getLASId();
        
        if (lasId!=valorNulo) {
            this.cursorEspera();
            oBD.delete(new LASPerMD(), "lasId="+lasId);
            oBD.delete(new LAS(), "Id="+lasId);
            this.cursorNormal();
            msgbox("Accion DesHacer culminada con exito.");
            status=VACIO;
            this.jTextAreaArchivo.setText("");
            setearControles();
        }                
        else
            msgbox("No se puede dehacer el LAS de esta corrida porque no se ha cargado."); 
        oBD.setHuboCambios(true);
    }
    
    public void setParam(ManejoBDI oBDIn) {
        oBD=oBDIn;
        try {
            oRun=(Run) oBD.select(Run.class, "id="+oRunFromMainForm.getId())[0];
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CargaLAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (getLASId()==-1) {
            status=VACIO;
        } else {
          status=CARGADO;  
        }
        setearControles();
    }
    
    @Override
    public void procesar() {
        LAS oLAS=new LAS();
        double desde=0,hasta=0;
        String valor;
        ResultSet rs;
   
        long lasId=valorNulo;
        boolean ok=false;
        cursorEspera();
        rs=oBD.select(oLAS, "runId="+oRun.getId());
        try {
            while (rs.next()){
                lasId=rs.getLong("Id");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargaLAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (lasId==valorNulo) {
           oLAS=new LAS();
           oLAS.setRunId(oRun.getId());
           if (oBD.insert(oLAS)) {
               try {
                   oLAS=(LAS) oBD.select(LAS.class, "runId="+oRun.getId())[0];
                   lasId=oLAS.getId();
               } catch (InstantiationException | IllegalAccessException ex) {
                   Logger.getLogger(CargaLAS.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        }else {
            oBD.delete(new LASPerMD(), "lasId="+lasId);
        }
        if (lasId!=valorNulo) {
            procesarLASDeLaSeccion(i0, lasId, this.oRun.getInitialDepth(), this.oRun.getFinalDepth());
        }
        else
            msgbox("No se pudo incluir el LAS. Error al insertar LAS en BD.");
        oBD.setHuboCambios(true);                
    }
    
    @Override
    public boolean archivoValido() {
        boolean ok=false;
        String valor;
        BufferedReader br = null;
        int i=0;
        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(selectedFile));

            while ((sCurrentLine = br.readLine()) != null) {
                i++;
                if (i==4) {
                    if (sCurrentLine.contains("LAS Producer")) {
                    } else {
                        br.close();
                        ok= false;
                        break;
                    }                            
                }
                if (sCurrentLine.contains("~ASCII")) {
                    i0=i+1;
                    br.close();
                    ok= true;
                    break;
                }
                if (sCurrentLine.contains("DEPT") && sCurrentLine.contains("GR") && sCurrentLine.contains("P40H")) {
                   posDEPT=sCurrentLine.indexOf("DEPT")-11+4;
                   posGR=sCurrentLine.indexOf("GR")-11+2;
                   posP40H=sCurrentLine.indexOf("P40H_UNC")-11+8;
                   posCRPM=sCurrentLine.indexOf("CRPM")-11+4;
                   posTVDE=sCurrentLine.indexOf("TVDE")-11+4;
                   posAZIM_CONT=sCurrentLine.indexOf("AZIM_CONT")-11+9;
                   posINCL_CONT=sCurrentLine.indexOf("INCL_CONT")-11+9;
                   if (posP40H<=0) posP40H=sCurrentLine.indexOf("P40H")-11+4;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok;
    }
    
    public void msgbox(String s){
        JOptionPane.showMessageDialog(null, s);        
    }
    
    public void procesarLASDeLaSeccion(int i0, long lasId, double valorDesde, double valorHasta) {
        String valor; 
        cursorEspera();
        BufferedReader br = null;
        double dept=valorNulo,gr=valorNulo,p40hunc=valorNulo;
        double tvde=valorNulo,crpm=valorNulo,azimCont=valorNulo,inclCont=valorNulo;
        int i=0,p=0;
        int cantRegistros=0;

        try {
            br = new BufferedReader(new FileReader(selectedFile));
            while ((br.readLine()) != null) {
                cantRegistros++;
            }
            br.close();
            jProgressBar.setMinimum(0);
            jProgressBar.setMaximum(cantRegistros);
            jProgressBar.setVisible(true);

            String sCurrentLine;
            String sBulk=" select * from ( ";
            br = new BufferedReader(new FileReader(selectedFile));
            while ((sCurrentLine = br.readLine()) != null) {
                i++;
                if (i>=i0) {
                   dept=new Double(sCurrentLine.substring(posDEPT, posDEPT+11)) ;
                   gr=new Double(sCurrentLine.substring(posGR, posGR+11)) ;
                   p40hunc=new Double(sCurrentLine.substring(posP40H, posP40H+11)) ;
                   tvde=valorNulo;crpm=valorNulo;azimCont=valorNulo;inclCont=valorNulo;
                   if (posTVDE>=0) tvde=new Double(sCurrentLine.substring(posTVDE, posTVDE+11)) ;
                   if (posCRPM>=0) crpm=new Double(sCurrentLine.substring(posCRPM, posCRPM+11)) ;
                   if (posAZIM_CONT>=0) azimCont=new Double(sCurrentLine.substring(posAZIM_CONT, posAZIM_CONT+11)) ;
                   if (posINCL_CONT>=0) inclCont=new Double(sCurrentLine.substring(posINCL_CONT, posINCL_CONT+11)) ;                   
                   if (dept>=valorDesde && dept<=valorHasta) {
                       sBulk+="select " +lasId+" as lasId,";
                       sBulk+=dept+" as dept,";
                       sBulk+=gr+" as gr,";
                       sBulk+=p40hunc+" as p40hunc,";
                       sBulk+=tvde+" as tvde,";
                       sBulk+=crpm+" as crpm,";
                       sBulk+=azimCont+" as azimCont,";
                       sBulk+=inclCont+" as inclCont from DDD union all "; 
                       p++;
                   }
                }
                if (p>=100) {
                    sBulk=sBulk.substring(0, sBulk.length()-10);
                    sBulk+=")";
                    oBD.insert(new LASPerMD(), sBulk);
                    p=0;
                    sBulk=" select * from ( ";
                    jProgressBar.setValue(i);
                }
            }
            sBulk=sBulk.substring(0, sBulk.length()-10);
            sBulk+=")";
            jProgressBar.setValue(i);
            oBD.insert(new LASPerMD(), sBulk);
            br.close();
        } catch (IOException e) {
        }
        cursorNormal();
        //this.jTextAreaArchivo.setText("");
        jProgressBar.setVisible(false);
        msgbox("LAS cargado exitosamente");
        status=CARGADO;
        setearControles();
    }
    
    @Override
    public void seleccionaArchivo(){
        int prev=status;
        String s,t;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos LAS", new String[] {"las"});
        fileChooser.setCurrentDirectory(currDirectory);
        fileChooser.setDialogTitle("Escoja el Archivo correspondiente a esta corrida");
        fileChooser.setFileFilter(null);      
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter); 
        int result = fileChooser.showOpenDialog(this);
        currDirectory=fileChooser.getCurrentDirectory();
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            s=selectedFile.toString();
            t=s.substring(s.indexOf('.')+1).toUpperCase();
            this.jTextAreaArchivo.setText("> "+selectedFile.getAbsolutePath());
            if (archivoValido()) {
                status=SELECCION_VALIDA;
            } else status=SELECCION_INVALIDA;
            setearControles();
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

        jToolBar1 = new javax.swing.JToolBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(400, 200));
        setMinimumSize(new java.awt.Dimension(400, 200));
        setPreferredSize(new java.awt.Dimension(400, 200));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setRollover(true);
        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 400, 30));

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
            java.util.logging.Logger.getLogger(CargaLAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CargaLAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CargaLAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CargaLAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CargaLAS dialog = new CargaLAS(new javax.swing.JFrame(), true);
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
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}


