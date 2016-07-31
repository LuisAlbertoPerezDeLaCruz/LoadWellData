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
import javax.swing.JOptionPane;
import miLibreria.bd.*;

/**
 *
 * @author Luis
 */
public class CargaWellPlan extends CargaArchivoExcel  implements miLibreria.GlobalConstants {
    public ManejoBDI oBD;
    public WellPlanPerMD oWellPlanPerMD;
    public Run oRunFromMainForm=new Run();
    public Run oRun;
    private java.awt.Frame parent1;
    private CamposEscojidosPlan cp=new CamposEscojidosPlan();
    private Object[][] campos;
    private int i0=24,i1=0;
    private boolean vieneDelRpt004=false;
    
    public CargaWellPlan(java.awt.Frame parent, boolean modal) {        
        super(parent, modal);
        initComponents();
        oRun=new Run();                       
        parent1=parent;
        this.jToolBar1.add(jLabelProcesando);
        this.jToolBar1.addSeparator();
        this.jToolBar1.add(jProgressBar);
    }
    
    public long getWellPlanId() {
        ResultSet rs;
        long wellPlanId=-1; 
        WellPlan oWellPlan=new WellPlan();
        rs=oBD.select(oWellPlan, "runId="+oRun.getId());
        try {
            while (rs.next()){
                wellPlanId=rs.getLong("Id");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargaWellPlan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wellPlanId;
    }
    
    @Override
    public void mostrar() {
        long wellPlanId=getWellPlanId();
        
        if (wellPlanId>-1) {
            MuestraWellPlanPerMD j=new MuestraWellPlanPerMD(parent1,true);
            j.wellPlanId=wellPlanId;
            j.setParam(oBD);
            j.cargaTabla();
            j.setLocationRelativeTo(this);
            j.setVisible(true);
        }                
        else
            msgbox("No se puede mostrar el WellPlan de esta corrida porque no se ha cargado.");
    }
    
    @Override
    public void desHacer() {
        long wellPlanId=getWellPlanId();
        cursorEspera();
        if (wellPlanId!=valorNulo) {
            oBD.delete(new WellPlanPerMD(), "wellPlanId="+wellPlanId);
            oBD.delete(new WellPlan(), "Id="+wellPlanId);
            msgbox("Accion DesHacer culminada con exito.");
            status=VACIO;
            this.jTextAreaArchivo.setText("");
            setearControles();
        }                
        else
            msgbox("No se puede dehacer el WellPlan de esta corrida porque no se ha cargado.");
        cursorNormal();
        oBD.setHuboCambios(true);
    }
    
    public void setParam(ManejoBDI oBDIn) {
        oBD=oBDIn;
        if (!vieneDelRpt004) {
            try {
                oRun=(Run) oBD.select(Run.class, "id="+oRunFromMainForm.getId())[0];
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(CargaWellPlan.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (getWellPlanId()==-1) {
                status=VACIO;
            } else {
              status=CARGADO;  
            }
        } else {
            oRun.setId(989898989);
            oRun.setInitialDepth(0);
            oRun.setFinalDepth(1000000);
            status=VACIO;
        }
        setearControles();
    }
    
    public void setVieneDelRpt004(boolean v) {
        vieneDelRpt004=v;
    }
    
    @Override
    public void procesar() {
        WellPlan oWellPlan=new WellPlan();
        double desde=0,hasta=0;
        String valor;
        ResultSet rs;     
        long wellPlanId=valorNulo;
        cursorEspera();
        jLabelProcesando.setText("inicializando...");
        establecerColumnas(i0);
        
        if (oXL.abrirArchivo()) {
            oXL.establecerHoja(0);
            rs=oBD.select(oWellPlan, "runId="+oRun.getId());
            try {
                while (rs.next()){
                    wellPlanId=rs.getLong("Id");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CargaWellPlan.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (wellPlanId==valorNulo) {
               oWellPlan=new WellPlan();
               oWellPlan.setRunId(oRun.getId());
               if (vieneDelRpt004) oWellPlan.setId(oBD.ultimaClave(oWellPlan)+1);
               if (oBD.insert(oWellPlan,vieneDelRpt004)) {
                   try {
                       oWellPlan=(WellPlan) oBD.select(WellPlan.class, "runId="+oRun.getId())[0];
                       wellPlanId=oWellPlan.getId();
                   } catch (InstantiationException | IllegalAccessException ex) {
                       Logger.getLogger(CargaWellPlan.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            }else {
                oBD.delete(new WellPlanPerMD(), "wellPlanId="+wellPlanId);
            }
            if (wellPlanId!=valorNulo) {
                procesarWellPlanDeLaSeccion(i0, i1, wellPlanId, this.oRun.getInitialDepth(), this.oRun.getFinalDepth());
            }
            else
                msgbox("No se pudo incluir el WellPlan. Error al insertar WellPlan en BD.");
        }  
        oBD.setHuboCambios(true);
    }
    
    private void establecerColumnas(int i0) {
        String valor;
        if (oXL.abrirArchivo()) {
            oXL.establecerHoja(0); 
            for (int j=0;j<=campos.length-1;j++) {
                for (int j1=0;j1<=100;j1++) {
                    valor=oXL.valorCelda(i0-2,j1);
                    if (valor.startsWith((String) campos[j][0])) {
                        campos[j][1]=j1;
                        break;                        
                    }
                }
            }
        }            
    }
    
    @Override
    public boolean archivoValido() {
        boolean ok=false;
        String valor;
        cursorEspera();
        try {
        if (oXL.abrirArchivo()) {
            oXL.establecerHoja(0);
            campos=new Object [][] {
                {"MD",-1},
                {"Incl",-1},
                {"Azim Grid",-1},
                {"TVD",-1},
                {"VSEC",-1},
                {"NS",-1},
                {"EW",-1},
                {"DLS",-1},
                {"BR",-1},
                {"TR",-1},
                {"TF",-1},
            };
            for (int i=i0;i<=64000;i++) {
                valor=oXL.valorCelda(i,0);
                if ("Survey Type:".equals(valor.trim())) {
                    valor=oXL.valorCelda(i,2);
                    if ("Def Plan".equals(valor.trim())) {
                        i1=i-4;
                        cursorNormal();
                        return true;
                    }
                    else {
                        cursorNormal();
                        return false;
                    }
                }               
            }
        }
        } catch (NullPointerException ex) {}
        cursorNormal();
        return false;
    }
    
    public void msgbox(String s){
        JOptionPane.showMessageDialog(null, s);        
    }
    
    public void procesarWellPlanDeLaSeccion(int i0, int i1, long wellPlanId, double valorDesde, double valorHasta) {
        String valor; 
        cursorEspera();
        String sBulk=" SELECT * FROM ( ";
        int i=0,p=0,clave=0;
        jProgressBar.setMinimum(i0);
        jProgressBar.setMaximum(i1-i0);
        jProgressBar.setVisible(true);
        jLabelProcesando.setText("cargando registros ...");
        for (i=i0;i<i1;i++) {
            oWellPlanPerMD=new WellPlanPerMD();
            oWellPlanPerMD.setWellPlanId(wellPlanId);
            for (int j=0;j<=12;j++) {
                valor=oXL.valorCelda(i,j);
                if ("N/A".equals(valor)) valor="0";
                try {
                    if ((int) campos[cp.Md][1]==j) {
                       oWellPlanPerMD.setMd(new Double(valor)); 
                    }
                    if ((int) campos[cp.Incl][1]==j) {
                       oWellPlanPerMD.setIncl(new Double(valor)); 
                    }
                    if ((int) campos[cp.Azim][1]==j) {
                       oWellPlanPerMD.setAzim(new Double(valor)); 
                    }
                    if ((int) campos[cp.Vsec][1]==j) {
                       oWellPlanPerMD.setVsec(new Double(valor)); 
                    }
                    if ((int) campos[cp.Tvd][1]==j) {
                       oWellPlanPerMD.setTvd(new Double(valor)); 
                    }
                    if ((int) campos[cp.Ew][1]==j) {
                       oWellPlanPerMD.setEw(new Double(valor)); 
                    }
                    if ((int) campos[cp.Dls][1]==j) {
                       oWellPlanPerMD.setDls(new Double(valor)); 
                    }
                    if ((int) campos[cp.Ns][1]==j) {
                       oWellPlanPerMD.setNs(new Double(valor)); 
                    }     
                    if ((int) campos[cp.BR][1]==j) {
                       oWellPlanPerMD.setBr(new Double(valor)); 
                    }
                    if ((int) campos[cp.TR][1]==j) {
                       oWellPlanPerMD.setTr(new Double(valor)); 
                    }
                    if ((int) campos[cp.TF][1]==j) {
                       oWellPlanPerMD.setTf(valor); 
                    }
                } catch (NumberFormatException ex) {
                    valor=null;
                    break;
                };
            }
            if (oWellPlanPerMD.getMd()>=valorDesde && oWellPlanPerMD.getMd()<valorHasta) {
                clave++;
                sBulk+="select " +oWellPlanPerMD.getWellPlanId()+" as wellPlanId,";
                sBulk+=oWellPlanPerMD.getMd()+" as md,";
                sBulk+=oWellPlanPerMD.getIncl()+" as incl,";                
                sBulk+=oWellPlanPerMD.getAzim()+" as azim,";
                sBulk+=oWellPlanPerMD.getTvd()+" as tvd,";
                sBulk+=oWellPlanPerMD.getVsec()+" as vsec,";
                sBulk+=oWellPlanPerMD.getNs()+" as ns,";
                sBulk+=oWellPlanPerMD.getEw()+" as ew,";
                sBulk+=oWellPlanPerMD.getDls()+" as dls,";
                sBulk+=oWellPlanPerMD.getBr()+" as br,";
                sBulk+=oWellPlanPerMD.getTr()+" as tr,";
                sBulk+="'"+oWellPlanPerMD.getTf()+"' as tf FROM DDD UNION ALL ";                
                p++;
            }
            if (p>=50) {
                sBulk=sBulk.substring(0, sBulk.length()-10);
                sBulk+=")";
                oBD.insert(new WellPlanPerMD(), sBulk);
                p=0;
                sBulk=" SELECT * FROM ( ";
                jProgressBar.setValue(i);
            }
        }
        sBulk=sBulk.substring(0, sBulk.length()-10);
        sBulk+=")";
        oBD.insert(new WellPlanPerMD(), sBulk);
        jProgressBar.setValue(i);
        cursorNormal();
        jLabelProcesando.setText("");
        jProgressBar.setVisible(false);
        if (this.vieneDelRpt004) {
            this.dispose();
            return;
        }
        msgbox("WellPlan cargado exitosamente");
        status=CARGADO;
        setearControles();
        this.jTextAreaArchivo.setText("");
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
        setMinimumSize(new java.awt.Dimension(400, 200));
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
            java.util.logging.Logger.getLogger(CargaWellPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CargaWellPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CargaWellPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CargaWellPlan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CargaWellPlan dialog = new CargaWellPlan(new javax.swing.JFrame(), true);
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

class CamposEscojidosPlan {
    final int Md=0;
    final int Incl=1;
    final int Azim=2;
    final int Tvd=3;
    final int Vsec=4;
    final int Ns=5;
    final int Ew=6;
    final int Dls=7;
    final int BR=8;
    final int TR=9;
    final int TF=10;
}