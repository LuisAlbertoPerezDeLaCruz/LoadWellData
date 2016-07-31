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
public class CargaSurvey extends CargaArchivoExcel implements miLibreria.GlobalConstants{
    public ManejoBDI oBD;
    public SurveyPerMD oSurveyPerMD;
    public Run oRunFromMainForm=new Run();
    public Run oRun;
    private CamposEscojidosSurvey cp=new CamposEscojidosSurvey();
    private Object[][] campos;
    private java.awt.Frame parent1;
    private String tort,ahd,ddi,erd;

    private int i0=24,i1=0;
    
    public CargaSurvey(java.awt.Frame parent, boolean modal) {        
        super(parent, modal);
        initComponents();
        oRun=new Run();
                       
        parent1=parent;
    }
    
    public long getSurveyId() {
        ResultSet rs;
        long surveyId=-1; 
        Survey oSurvey=new Survey();
        rs=oBD.select(oSurvey, "runId="+oRun.getId());
        try {
            while (rs.next()){
                surveyId=rs.getLong("Id");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargaSurvey.class.getName()).log(Level.SEVERE, null, ex);
        }
        return surveyId;
    }
    
    @Override
    public void mostrar() {
        long surveyId=getSurveyId();
        
        if (surveyId>-1) {
            MuestraSurveyPerMD j=new MuestraSurveyPerMD(parent1,true);
            j.surveyId=surveyId;
            j.setParam(oBD);
            j.cargaTabla();
            j.setLocationRelativeTo(this);
            j.setVisible(true);
        }                
        else
            msgbox("No se puede mostrar el Survey de esta corrida porque no se ha cargado.");
    }
    
    @Override
    public void desHacer() {
        long surveyId=getSurveyId();
        
        if (surveyId!=valorNulo) {
            oBD.delete(new SurveyPerMD(), "surveyId="+surveyId);
            oBD.delete(new Survey(), "Id="+surveyId);
            msgbox("Accion DesHacer culminada con exito.");
            status=VACIO;
            this.jTextAreaArchivo.setText("");
            setearControles();
        }                
        else
            msgbox("No se puede dehacer el Survey de esta corrida porque no se ha cargado."); 
        oBD.setHuboCambios(true);
    }
    
    public void setParam(ManejoBDI oBDIn) {
        oBD=oBDIn;
        try {
            oRun=(Run) oBD.select(Run.class, "id="+oRunFromMainForm.getId())[0];
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(CargaSurvey.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (getSurveyId()==-1) {
            status=VACIO;
        } else {
          status=CARGADO;  
        }
        setearControles();
    }
    
    @Override
    public void procesar() {
        Survey oSurvey=new Survey();
        double desde=0,hasta=0;
        String valor;
        ResultSet rs;       
        long surveyId=valorNulo;
        boolean ok=false;
        SurveyPerMD[] aSurveyPerMD;
        
        if (noEsExcel) {
            cursorEspera();
            aSurveyPerMD=procesarXML(); 
            rs=oBD.select(oSurvey, "runId="+oRun.getId());
            try {
                while (rs.next()){
                    surveyId=rs.getLong("Id");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CargaSurvey.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (surveyId==valorNulo) {
               oSurvey=new Survey();
               oSurvey.setRunId(oRun.getId());
               oSurvey.setTort(new Double(tort));
               oSurvey.setAhd(new Double(ahd));
               oSurvey.setDdi(new Double(ddi));
               oSurvey.setErd(new Double(erd));
               if (oBD.insert(oSurvey)) {
                   try {
                       oSurvey=(Survey) oBD.select(Survey.class, "runId="+oRun.getId())[0];
                       surveyId=oSurvey.getId();
                   } catch (InstantiationException | IllegalAccessException ex) {
                       Logger.getLogger(CargaSurvey.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            }else {
                oBD.delete(new SurveyPerMD(), "surveyId="+surveyId);
            }
            if (surveyId!=valorNulo) {
                for (int i=0;i<=aSurveyPerMD.length-1;i++) {
                    if(aSurveyPerMD[i].getMd()>=this.oRun.getInitialDepth() && aSurveyPerMD[i].getMd()<=this.oRun.getFinalDepth()) {
                       aSurveyPerMD[i].setSurveyId(surveyId);
                       oBD.insert(aSurveyPerMD[i]);
                    }
                }
            }
            oBD.setHuboCambios(true);
            cursorNormal();
            msgbox("Survey cargado exitosamente");
            status=CARGADO;
            setearControles();
            this.jTextAreaArchivo.setText("");
            return;
        }
        
        establecerColumnas(i0);
        
        if (oXL.abrirArchivo()) {
            
            oXL.establecerHoja(0);
           
            rs=oBD.select(oSurvey, "runId="+oRun.getId());
            try {
                while (rs.next()){
                    surveyId=rs.getLong("Id");
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CargaSurvey.class.getName()).log(Level.SEVERE, null, ex);
            }
            valor=oXL.valorCelda(13,2);
            String[] sDDI=valor.split("/");
            
            tort=sDDI[0].trim();
            tort=tort.substring(0, tort.length()-1);
            tort=tort.trim();
            
            ahd=sDDI[1].trim();
            ahd=ahd.substring(0, ahd.length()-2);
            ahd=ahd.trim();
            
            ddi=sDDI[2].trim();
            ddi=ddi.substring(0, ddi.length());
            ddi=ddi.trim();
            
            erd=sDDI[3].trim();
            erd=erd.substring(0, erd.length());
            erd=erd.trim();

            if (surveyId==valorNulo) {
               oSurvey=new Survey();
               oSurvey.setRunId(oRun.getId());
               oSurvey.setTort(new Double(tort));
               oSurvey.setAhd(new Double(ahd));
               oSurvey.setDdi(new Double(ddi));
               oSurvey.setErd(new Double(erd));
               if (oBD.insert(oSurvey)) {
                   try {
                       oSurvey=(Survey) oBD.select(Survey.class, "runId="+oRun.getId())[0];
                       surveyId=oSurvey.getId();
                   } catch (InstantiationException | IllegalAccessException ex) {
                       Logger.getLogger(CargaSurvey.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            }else {
                oBD.delete(new SurveyPerMD(), "surveyId="+surveyId);
            }
            if (surveyId!=valorNulo) {
                procesarSurveyDeLaSeccion(i0, i1, surveyId, this.oRun.getInitialDepth(), this.oRun.getFinalDepth());
            }
            else
                msgbox("No se pudo incluir el Survey. Error al insertar Survey en BD.");
        } 
        oBD.setHuboCambios(true);
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
    
    private SurveyPerMD[] procesarXML() {
        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = selectedFile;
        SurveyPerMD[] aSurveyPerMD=null;
        int contador=0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement(); //trajectorys
            
            List listtrajectorys=rootNode.getChildren();
            if (indexOfList(listtrajectorys,"trajectory")==-1) return null;
            Element trajectoryNode = (Element) listtrajectorys.get(indexOfList(listtrajectorys,"trajectory")); //trajectory
        
            List listtrajectories=trajectoryNode.getChildren();
            if (indexOfList(listtrajectories,"trajectoryStations")==-1) return null;
            Element trajectoryStationsNode = (Element) listtrajectories.get(indexOfList(listtrajectories,"trajectoryStations"));            
            List listtrajectoryRows=trajectoryStationsNode.getChildren();
            
            if (indexOfList(listtrajectories,"wellHeaderInformation")==-1) return null;
            Element wellHeaderInformation = (Element) listtrajectories.get(indexOfList(listtrajectories,"wellHeaderInformation"));
            List listNodeAttributes=wellHeaderInformation.getChildren();
            
            String tipoMedida="";
            Double factor=1.0;
            for (int k=0;k<=listNodeAttributes.size()-1;k++) {
                Element atributo=(Element) listNodeAttributes.get(k);
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
                if (contenidoAtributo.isEmpty()) continue;
                switch (nombreAtributo) {                            
                    case "Tortuosity" :
                        tort=contenidoAtributo.substring(0,contenidoAtributo.length()-2);
                    break;
                    case "AHD" :
                        ahd=contenidoAtributo.substring(0,contenidoAtributo.length()-3);
                    break;
                    case "DDI" :
                        ddi=contenidoAtributo;
                    break;
                    case "ERDRatio" :
                        erd=contenidoAtributo;
                    break;                            
                }
            }
            
            aSurveyPerMD=new SurveyPerMD[listtrajectoryRows.size()];
            
            tipoMedida="";
            factor=1.0;
            
            for (int i=0;i<=listtrajectoryRows.size()-1;i++) {
                Element trajectoryStationNode=(Element) listtrajectoryRows.get(i);
                if ("trajectoryStation".equals(trajectoryStationNode.getName())) {                    
                    listNodeAttributes=trajectoryStationNode.getChildren();
                    oSurveyPerMD=new SurveyPerMD();
                    for (int k=0;k<=listNodeAttributes.size()-1;k++) {
                        Element atributo=(Element) listNodeAttributes.get(k);
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
                        if (contenidoAtributo.isEmpty()) continue;
                        switch (nombreAtributo) {                            
                            case "md" :
                                oSurveyPerMD.setMd(new Double(contenidoAtributo));
                            break;
                            case "tvd" :
                                oSurveyPerMD.setTvd(new Double(contenidoAtributo));
                            break;
                            case "incl" :
                                oSurveyPerMD.setIncl(new Double(contenidoAtributo));
                            break;
                            case "azi" :
                                oSurveyPerMD.setAzim(new Double(contenidoAtributo));
                            break; 
                            case "dispNS" :
                                oSurveyPerMD.setNs(new Double(contenidoAtributo));
                            break;
                            case "dispEW" :
                                oSurveyPerMD.setEw(new Double(contenidoAtributo));
                            break;                                
                            case "dls" :
                                oSurveyPerMD.setDls(new Double(contenidoAtributo));
                            break;  
                            case "rateTurn" :
                                oSurveyPerMD.setTr(new Double(contenidoAtributo));
                            break;
                            case "rateBuild" :
                                oSurveyPerMD.setBr(new Double(contenidoAtributo));
                            break;
                            case "dispNs" :
                                oSurveyPerMD.setNs(new Double(contenidoAtributo));
                            break;
                            case "dispEw" :
                                oSurveyPerMD.setEw(new Double(contenidoAtributo));
                            break;                            
                        }
                    }
                    aSurveyPerMD[contador++]=oSurveyPerMD; 
                }
            }                     
        } catch ( IOException io ) {
            System.out.println( io.getMessage() );
        }catch ( JDOMException jdomex ) {
            System.out.println( jdomex.getMessage() );
        }
        return aSurveyPerMD;
    }

    
    private SurveyPerMD[] procesarXMLAnterior() {
        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = selectedFile;
        SurveyPerMD[] aSurveyPerMD=null;
        int contador=0;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement();
            
            aSurveyPerMD=new SurveyPerMD[getCantRows(rootNode)];
            
            List listtrajectorys=rootNode.getChildren();
            Element trajectoryNode = (Element) listtrajectorys.get(0);
        
            List listtrajectoryRows=trajectoryNode.getChildren();
            
            String tipoMedida="";
            Double factor=1.0;
            
            for (int i=0;i<=listtrajectoryRows.size()-1;i++) {
                Element trajectoryStationNode=(Element) listtrajectoryRows.get(i);
                if ("trajectoryStation".equals(trajectoryStationNode.getName())) {                    
                    List listNodeAttributes=trajectoryStationNode.getChildren();
                    oSurveyPerMD=new SurveyPerMD();
                    for (int k=0;k<=listNodeAttributes.size()-1;k++) {
                        Element atributo=(Element) listNodeAttributes.get(k);
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
                                  //Double valor=new Double(contenidoAtributo);
                                  //valor=Math.toDegrees(valor);
                                  //factor=3.2808398950;
                                  //valor/=factor;
                                  factor=17.4637535955701*100;
                                  Double valor=new Double(contenidoAtributo)*factor;
                                  contenidoAtributo=valor.toString();
                               }                               
                            }
                        }
                        if (contenidoAtributo.isEmpty()) continue;
                        switch (nombreAtributo) {                            
                            case "md" :
                                oSurveyPerMD.setMd(new Double(contenidoAtributo));
                            break;
                            case "tvd" :
                                oSurveyPerMD.setTvd(new Double(contenidoAtributo));
                            break;
                            case "incl" :
                                oSurveyPerMD.setIncl(new Double(contenidoAtributo));
                            break;
                            case "azi" :
                                oSurveyPerMD.setAzim(new Double(contenidoAtributo));
                            break; 
                            case "dispNS" :
                                oSurveyPerMD.setNs(new Double(contenidoAtributo));
                            break;
                            case "dispEW" :
                                oSurveyPerMD.setEw(new Double(contenidoAtributo));
                            break;                                
                            case "dls" :
                                oSurveyPerMD.setDls(new Double(contenidoAtributo));
                            break;  
                            case "rateTurn" :
                                oSurveyPerMD.setTr(new Double(contenidoAtributo));
                            break;
                            case "rateBuilt" :
                                oSurveyPerMD.setBr(new Double(contenidoAtributo));
                            break;
                            case "dispNs" :
                                oSurveyPerMD.setNs(new Double(contenidoAtributo));
                            break;
                            case "dispEw" :
                                oSurveyPerMD.setEw(new Double(contenidoAtributo));
                            break;                            
                        }
                    }
                    aSurveyPerMD[contador++]=oSurveyPerMD; 
                }
            }                     
        } catch ( IOException io ) {
            System.out.println( io.getMessage() );
        }catch ( JDOMException jdomex ) {
            System.out.println( jdomex.getMessage() );
        }
        return aSurveyPerMD;
    }

    private int getCantRows(Element rootNode) {
        int cantRows=0;
        
        List listtrajectorys=rootNode.getChildren();
        Element trajectoryNode = (Element) listtrajectorys.get(0);
        
        List listtrajectoryRows=trajectoryNode.getChildren();
        
        for (int i=0;i<=listtrajectoryRows.size()-1;i++) {
            Element trajectoryStationNode=(Element) listtrajectoryRows.get(i);
            if ("trajectoryStation".equals(trajectoryStationNode.getName())) {
                cantRows++;
            }
        }    
        return cantRows;
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
        if (noEsExcel) {
            //Se crea un SAXBuilder para poder parsear el archivo
            SAXBuilder builder = new SAXBuilder();
            File xmlFile = selectedFile;
            SurveyPerMD[] aSurveyPerMD=null;
            try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build( xmlFile );            
            Element rootNode = document.getRootElement();
            if ("trajectorys".equals(rootNode.getName())) {
                return true;
            }
            } catch (Exception ex) {
                return false;
            }
            return false;
        }
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
            };
            for (int i=i0;i<=64000;i++) {
                valor=oXL.valorCelda(i,0);
                if ("Survey Type:".equals(valor.trim())) {
                    valor=oXL.valorCelda(i,2);
                    if ("Non-Def Survey".equals(valor.trim()) || "Def Survey".equals(valor.trim())) {
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
    
    public void procesarSurveyDeLaSeccion(int i0, int i1, long surveyId, double valorDesde, double valorHasta) {
        String valor; 
        cursorEspera();
        for (int i=i0;i<i1;i++) {    
            oSurveyPerMD=new SurveyPerMD();
            for (int j=0;j<=12;j++) {
                valor=oXL.valorCelda(i,j).trim();
                if ("N/A".equals(valor)) valor="0";
                if (valor.length()>0) {
                    if ("-0123456789".indexOf(valor.trim().substring(0, 1))==-1)  {
                       valor=valor.substring(1).trim();
                    }
                }
                try {
                    if ((int) campos[cp.Md][1]==j) {
                       oSurveyPerMD.setMd(new Double(valor)); 
                    }
                    if ((int) campos[cp.Incl][1]==j) {
                       oSurveyPerMD.setIncl(new Double(valor)); 
                    }
                    if ((int) campos[cp.Azim][1]==j) {
                       oSurveyPerMD.setAzim(new Double(valor)); 
                    }
                    if ((int) campos[cp.Vsec][1]==j) {
                       oSurveyPerMD.setVsec(new Double(valor)); 
                    }
                    if ((int) campos[cp.Tvd][1]==j) {
                       oSurveyPerMD.setTvd(new Double(valor)); 
                    }
                    if ((int) campos[cp.Ew][1]==j) {
                       oSurveyPerMD.setEw(new Double(valor)); 
                    }
                    if ((int) campos[cp.Dls][1]==j) {
                       oSurveyPerMD.setDls(new Double(valor)); 
                    }
                    if ((int) campos[cp.Ns][1]==j) {
                       oSurveyPerMD.setNs(new Double(valor)); 
                    }
                    if ((int) campos[cp.Br][1]==j) {
                       oSurveyPerMD.setBr(new Double(valor)); 
                    }
                    if ((int) campos[cp.Tr][1]==j) {
                       oSurveyPerMD.setTr(new Double(valor)); 
                    }
                 } catch (NumberFormatException ex) {valor=null;};
                oSurveyPerMD.setSurveyId(surveyId);
            }
            if (oSurveyPerMD.getMd()>=valorDesde && oSurveyPerMD.getMd()<valorHasta) {
                oBD.insert(oSurveyPerMD);
            }
        }
        cursorNormal();
        msgbox("Survey cargado exitosamente");
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
            java.util.logging.Logger.getLogger(CargaSurvey.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CargaSurvey.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CargaSurvey.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CargaSurvey.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CargaSurvey dialog = new CargaSurvey(new javax.swing.JFrame(), true);
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
    // End of variables declaration//GEN-END:variables
}

class CamposEscojidosSurvey {
    final int Md=0;
    final int Incl=1;
    final int Azim=2;
    final int Tvd=3;
    final int Vsec=4;
    final int Ns=5;
    final int Ew=6;
    final int Dls=7;
    final int Br=8;
    final int Tr=9;
}
