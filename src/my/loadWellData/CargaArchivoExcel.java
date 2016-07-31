/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.loadWellData;

import java.awt.Cursor;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import miLibreria.xl.ManejoExcel;
import miLibreria.xl.ManejoXLS;
import miLibreria.xl.ManejoXLSX;

/**
 *
 * @author Luis
 */
public class CargaArchivoExcel extends javax.swing.JDialog {
    ManejoExcel oXL;
    ManejoXLS oXLS=new ManejoXLS();
    ManejoXLSX oXLSX=new ManejoXLSX();
    public File selectedFile;
    public File currDirectory;
    
    public static final int VACIO = 0;
    public static final int SELECCION_VALIDA = 1;
    public static final int SELECCION_INVALIDA = 2;    
    public static final int CARGADO = 3; 
    public static final int NADA=-1;
    
    public int status=NADA;
    public Timer timer; 
    
    public JProgressBar jProgressBar;
    public JLabel jLabelProcesando;
    
    public boolean noEsExcel=false;
    
    public CargaArchivoExcel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jLabelProcesando=new JLabel();
        jProgressBar=new JProgressBar();
        jProgressBar.setStringPainted(true);
        jProgressBar.setVisible(false);
        setearControles();
        currDirectory=new File(System.getProperty("user.home"));
    }
        
    public void procesar(){
        
    }
    
    public void desHacer() {
        
    }
    
    public void mostrar() {
        
    }
    
    public void setearControles() {
        switch (status) {
            case NADA:
                this.jButtonDeshacer.setEnabled(false);
                this.jButtonMostrar.setEnabled(false);
                this.jButtonProcesar.setEnabled(false);
                this.jButtonSeleccionar.setEnabled(false);
                break;
            case VACIO:
                this.jButtonDeshacer.setEnabled(false);
                this.jButtonMostrar.setEnabled(false);
                this.jButtonProcesar.setEnabled(false);
                this.jButtonSeleccionar.setEnabled(true);
                break;
            case SELECCION_VALIDA:
                this.jButtonProcesar.setEnabled(true);
                this.jButtonSeleccionar.setEnabled(true);
                break;
            case SELECCION_INVALIDA:
                this.jButtonProcesar.setEnabled(false);
                this.jButtonSeleccionar.setEnabled(true);
                break;
            case CARGADO:
                this.jButtonDeshacer.setEnabled(true);
                this.jButtonMostrar.setEnabled(true);
                this.jButtonProcesar.setEnabled(false);
                this.jButtonSeleccionar.setEnabled(true);
                break;            
        }
    }
    
    public boolean archivoValido() {
        return true;
    }
    public void cursorNormal(){
        this.setCursor(Cursor.getDefaultCursor());
    }
    public void cursorEspera(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    public void seleccionaArchivo(){
        int prev=status;
        String s,t;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos Excel/Xml", new String[] {"xls", "xlsx","xml"});
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
            if ("XML".equals(t)) {
                noEsExcel=true;
                if (archivoValido()) {
                    this.jTextAreaArchivo.setText("> "+selectedFile.getAbsolutePath());
                    status=SELECCION_VALIDA;
                } else status=SELECCION_INVALIDA;
            } else {
                noEsExcel=false;
                if ("XLS".equals(t)) {
                    oXL=oXLS;
                }
                if ("XLSX".equals(t)) {
                    oXL=oXLSX;
                }
                try {
                oXL.setArchivo(selectedFile);
                } catch (NullPointerException ex){}
                this.jTextAreaArchivo.setText("> "+selectedFile.getAbsolutePath());
                if (archivoValido()) {
                    status=SELECCION_VALIDA;
                } else status=SELECCION_INVALIDA;
            }
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

        jButtonSeleccionar = new javax.swing.JButton();
        jButtonProcesar = new javax.swing.JButton();
        jButtonDeshacer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaArchivo = new javax.swing.JTextArea();
        jButtonMostrar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(400, 120));
        setModal(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonSeleccionar.setText("Seleccionar");
        jButtonSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSeleccionarActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonSeleccionar, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 16, 96, -1));

        jButtonProcesar.setText("Procesar");
        jButtonProcesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcesarActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonProcesar, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 46, 96, -1));

        jButtonDeshacer.setText("Deshacer");
        jButtonDeshacer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeshacerActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonDeshacer, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 76, 96, -1));

        jTextAreaArchivo.setEditable(false);
        jTextAreaArchivo.setColumns(20);
        jTextAreaArchivo.setLineWrap(true);
        jTextAreaArchivo.setRows(5);
        jTextAreaArchivo.setWrapStyleWord(true);
        jTextAreaArchivo.setBorder(null);
        jTextAreaArchivo.setFocusable(false);
        jTextAreaArchivo.setOpaque(false);
        jScrollPane1.setViewportView(jTextAreaArchivo);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 270, 80));

        jButtonMostrar.setText("Mostrar");
        jButtonMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMostrarActionPerformed(evt);
            }
        });
        getContentPane().add(jButtonMostrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(294, 106, 96, -1));

        jLabel2.setText("Archivo:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 20));

        jToolBar1.setRollover(true);
        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonProcesarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProcesarActionPerformed
        Runnable runner=new Runnable(){
            public void run(){
                procesar();
            }
        };
        Thread t=new Thread(runner,"");
        t.start();
    }//GEN-LAST:event_jButtonProcesarActionPerformed

    private void jButtonSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSeleccionarActionPerformed
        seleccionaArchivo();       
    }//GEN-LAST:event_jButtonSeleccionarActionPerformed

    private void jButtonMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMostrarActionPerformed
        mostrar();
    }//GEN-LAST:event_jButtonMostrarActionPerformed

    private void jButtonDeshacerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeshacerActionPerformed
        desHacer();
    }//GEN-LAST:event_jButtonDeshacerActionPerformed

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
            java.util.logging.Logger.getLogger(CargaArchivoExcel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CargaArchivoExcel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CargaArchivoExcel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CargaArchivoExcel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CargaArchivoExcel dialog = new CargaArchivoExcel(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButtonDeshacer;
    private javax.swing.JButton jButtonMostrar;
    private javax.swing.JButton jButtonProcesar;
    private javax.swing.JButton jButtonSeleccionar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextAreaArchivo;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
