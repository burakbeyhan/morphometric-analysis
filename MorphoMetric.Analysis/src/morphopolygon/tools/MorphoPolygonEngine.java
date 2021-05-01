
// Morphometric Analysis of Polygons plugin for OpenJUMP - Java version
//
// Copyright (C) 2021 Burak Beyhan, Cüneyt Güler and Hidayet Tağa
// 
// This plugin is designed to analyse morphological properties of polygon features. For further
// explanations please refer to the following paper if you use this plugin in your studies;
//
// Güler, C., Beyhan, B. & Tağa, H. (2021) PolyMorph-2D: An open-source GIS plug-in for morphometric 
// analysis of vector-based 2D polygon features. Geomorphology. https://doi.org/10.1016/j.geomorph.2021.107755
//
// This program is free software; you can redistribute it and/or modify it under the terms of 
// the GNU General Public License as published by the Free Software Foundation; either version 2 
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
// See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, see http://www.gnu.org/licenses 


package morphopolygon.tools;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.model.LayerStyleUtil;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.task.TaskMonitorDialog;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.MinimumBoundingCircle;
import MIC.tools.MICFunction;

public class MorphoPolygonEngine {

    private Layer katmanim;
    private boolean BBL,MECL,MERL,BBPA,MECPA,MERPA,MICL,MICPA,CHUL,CHUPA,LTWRD,WTLRD,ELLFD,CIRRD,ZFORD,COMFD,MCIRD;
    private boolean DISMD,COMID,HFORD,ELOFD,LEMRD,REGFD,SHAFD,CONVD,CONCD,SOLID,RECTD,ROUND,SPHED,PPMSD,usefield;
    private String attribim;
    private String cunitim;
    private String runitim;
    private Integer precision;
       
    public MorphoPolygonEngine() {
    }
 
    public void aktarim0(Layer katman){katmanim = katman;}
    public void aktarim1(boolean bo1){BBL = bo1;}
    public void aktarim2(boolean bo2){MECL = bo2;}
    public void aktarim3(boolean bo3){MERL = bo3;}
    public void aktarim4(boolean bo4){BBPA = bo4;}
    public void aktarim5(boolean bo5){MECPA = bo5;}
    public void aktarim6(boolean bo6){MERPA = bo6;}
    public void aktarim7(String st1){attribim = st1;}
    public void aktarim8(String st2){cunitim = st2;}
    public void aktarim9(String st3){runitim = st3;} 
    public void aktarim14(boolean bo10){CHUPA = bo10;}
    public void aktarim10(boolean bo0){usefield = bo0;}
    public void aktarim11(boolean bo7){MICL = bo7;}
    public void aktarim12(boolean bo8){MICPA = bo8;}
    public void aktarim13(boolean bo9){CHUL = bo9;}
    public void aktarim15(Integer in1){precision = in1;} 
    public void aktarim16(boolean bo11){LTWRD = bo11;}
    public void aktarim17(boolean bo12){WTLRD = bo12;}
    public void aktarim18(boolean bo13){ELLFD = bo13;}
    public void aktarim19(boolean bo14){CIRRD = bo14;}
    public void aktarim20(boolean bo15){ZFORD = bo15;}
    public void aktarim21(boolean bo16){COMFD = bo16;}
    public void aktarim22(boolean bo17){MCIRD = bo17;}
    public void aktarim23(boolean bo18){DISMD = bo18;}
    public void aktarim24(boolean bo19){COMID = bo19;}
    public void aktarim25(boolean bo20){HFORD = bo20;}
    public void aktarim26(boolean bo21){ELOFD = bo21;}
    public void aktarim27(boolean bo22){LEMRD = bo22;}
    public void aktarim28(boolean bo23){REGFD = bo23;}
    public void aktarim29(boolean bo24){SHAFD = bo24;}
    public void aktarim30(boolean bo25){CONVD = bo25;}
    public void aktarim31(boolean bo26){CONCD = bo26;}
    public void aktarim32(boolean bo27){SOLID = bo27;}
    public void aktarim33(boolean bo28){RECTD = bo28;}
    public void aktarim34(boolean bo29){ROUND = bo29;}
    public void aktarim35(boolean bo30){SPHED = bo30;}
    public void aktarim36(boolean bo31){PPMSD = bo31;}

    
    public void execute(PlugInContext context) throws Exception {

      	final LayerManager layerManager = context.getLayerManager();
      	
        // create a Jframe for the completed task and warning message
        JFrame tframe = new JFrame("ShowMessageDialog for the completed task");
        String comptask = "Calculation has been finished!"+'\n'
                +"Please check attribute tables"+'\n'+"for the results of analysis.";
        String headmsag =  "Task Message";
        
      	boolean mp = true; 
        FeatureCollection fcm = katmanim.getFeatureCollectionWrapper().getWrappee();
        for (Iterator iter = fcm.iterator(); iter.hasNext();) { // multipolygon test
        	Feature element = (Feature) iter.next();
            Geometry geom = element.getGeometry();
            if(geom instanceof MultiPolygon){ 
                comptask = "There are multipolygon PFs in your layer!"+'\n'
                        +"Please convert them into single PFs";
                headmsag =  "Warning Message";
                mp = false;
                break;
            }
        }
        
        if (mp) {
        final JFrame desktop = (JFrame) context.getWorkbenchFrame();
        final TaskMonitorDialog progressDialog = new TaskMonitorDialog(desktop, null);
        progressDialog.setTitle("Processing Polygon Features");
        progressDialog.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        try {

    String activefilename = katmanim.getName();

    Layer actualLayer = katmanim;	
    FeatureCollection fc = actualLayer.getFeatureCollectionWrapper().getWrappee();
    FeatureSchema fs = fc.getFeatureSchema();
  
    double P;
    double A;
    int obj = fc.size();
    
    String strob = new String(new char[precision]).replace("\0", "#");
    
    DecimalFormat df = new DecimalFormat("#."+strob);
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    df.setDecimalFormatSymbols(custom);

    double CBU; // unit of measurement that will be used in displaying the perimeter and radius of MIC in the attribute table
    if (cunitim == "millimeter (mm)") CBU = 0.001;
    else if (cunitim == "centimeter (cm)") CBU = 0.01;
    else if (cunitim == "decimeter (dm)") CBU = 0.1;
    else if (cunitim == "meter (m)") CBU = 1;
    else if (cunitim == "dekameter (dam)") CBU = 10;
    else if (cunitim == "hectometer (hm)") CBU = 100;
    else CBU = 1000;
	double ABU; // unit of measurement that will be used in displaying the area of MIC in the attribute table
    if (runitim == "square mm (mm²)") ABU = 0.000001;
    else if (runitim == "square cm (cm²)")  ABU = 0.0001;
    else if (runitim == "square dm (dm²)")  ABU = 0.01;
    else if (runitim == "square m (m²)")  ABU = 1;
    else if (runitim == "decare (daa)")  ABU = 1000;
    else if (runitim == "hectare (ha)")  ABU = 10000;
    else ABU = 1000000;
    
   	int onlem = fs.getAttributeCount();

    FeatureSchema merSchema = new FeatureSchema();
    merSchema.addAttribute(fs.getAttributeName(fs.getGeometryIndex()), AttributeType.GEOMETRY);
    FeatureSchema mecSchema = new FeatureSchema();
    mecSchema.addAttribute(fs.getAttributeName(fs.getGeometryIndex()), AttributeType.GEOMETRY);
    FeatureSchema bbSchema = new FeatureSchema();
    bbSchema.addAttribute(fs.getAttributeName(fs.getGeometryIndex()), AttributeType.GEOMETRY);
    FeatureSchema micSchema = new FeatureSchema();
    micSchema.addAttribute(fs.getAttributeName(fs.getGeometryIndex()), AttributeType.GEOMETRY);   
    FeatureSchema chuSchema = new FeatureSchema();
    chuSchema.addAttribute(fs.getAttributeName(fs.getGeometryIndex()), AttributeType.GEOMETRY);  
    
    Object typim = AttributeType.INTEGER;
    
    if (usefield) typim = fs.getAttributeType(attribim);
    
    String cg = "ID"; // id operation
    if (!usefield) {
    	if (fs.hasAttribute("ID") == false) fs.addAttribute("ID", AttributeType.INTEGER);
    	else {
    		if (fs.hasAttribute("NID") == false) fs.addAttribute("NID", AttributeType.INTEGER);
    		cg = "NID";
    	}
    }
    
// adding parameters as new fields to the attribute tables

    if (PPMSD) {
        if (fs.hasAttribute("XPOL") == false) fs.addAttribute("XPOL", AttributeType.DOUBLE);
        if (fs.hasAttribute("YPOL") == false) fs.addAttribute("YPOL", AttributeType.DOUBLE);
        if (fs.hasAttribute("APOL") == false) fs.addAttribute("APOL", AttributeType.DOUBLE);
        if (fs.hasAttribute("PPOL") == false) fs.addAttribute("PPOL", AttributeType.DOUBLE);
    }  
    
    if (MERPA) {
        if (fs.hasAttribute("LPOL") == false) fs.addAttribute("LPOL", AttributeType.DOUBLE);
        if (fs.hasAttribute("WPOL") == false) fs.addAttribute("WPOL", AttributeType.DOUBLE);
        if (fs.hasAttribute("AZIM") == false) fs.addAttribute("AZIM", AttributeType.DOUBLE);
        if (fs.hasAttribute("AMEB") == false) fs.addAttribute("AMEB", AttributeType.DOUBLE);
        if (fs.hasAttribute("PMEB") == false) fs.addAttribute("PMEB", AttributeType.DOUBLE);
//        if (fs.hasAttribute("orient") == false) fs.addAttribute("orient", AttributeType.INTEGER);	
    }
    if (MERL) {
    	merSchema.addAttribute("ID", (AttributeType) typim);
    	merSchema.addAttribute("LPOL", AttributeType.DOUBLE);
    	merSchema.addAttribute("WPOL", AttributeType.DOUBLE);
    	merSchema.addAttribute("AZIM", AttributeType.DOUBLE);
    	merSchema.addAttribute("AMEB", AttributeType.DOUBLE);
    	merSchema.addAttribute("PMEB", AttributeType.DOUBLE);
//    	merSchema.addAttribute("orient", AttributeType.INTEGER);
    }
    FeatureCollection resultmer = new FeatureDataset(merSchema);
	
    if (BBPA) {
    	if (fs.hasAttribute("ABOB") == false) fs.addAttribute("ABOB", AttributeType.DOUBLE);
    	if (fs.hasAttribute("PBOB") == false) fs.addAttribute("PBOB", AttributeType.DOUBLE);
//  if (fs.hasAttribute("WMBB") == false) fs.addAttribute("WMBB", AttributeType.DOUBLE);
//  if (fs.hasAttribute("HMBB") == false) fs.addAttribute("HMBB", AttributeType.DOUBLE);
    }
    if (BBL) {
    	bbSchema.addAttribute("ID", (AttributeType) typim);
    	bbSchema.addAttribute("ABOB", AttributeType.DOUBLE);
    	bbSchema.addAttribute("PBOB", AttributeType.DOUBLE);
//   	bbSchema.addAttribute("WMBB", AttributeType.DOUBLE);
//   	bbSchema.addAttribute("HMBB", AttributeType.DOUBLE);
    }
    FeatureCollection resultbb = new FeatureDataset(bbSchema);

    if (CHUPA) {
        if (fs.hasAttribute("ACHU") == false) fs.addAttribute("ACHU", AttributeType.DOUBLE);
        if (fs.hasAttribute("PCHU") == false) fs.addAttribute("PCHU", AttributeType.DOUBLE);
    }
    if (CHUL) {
    	chuSchema.addAttribute("ID", (AttributeType) typim);
    	chuSchema.addAttribute("ACHU", AttributeType.DOUBLE);
    	chuSchema.addAttribute("PCHU", AttributeType.DOUBLE);
    }
    FeatureCollection resultchu = new FeatureDataset(chuSchema);
    
    if (MECPA) {
        if (fs.hasAttribute("AMCC") == false) fs.addAttribute("AMCC", AttributeType.DOUBLE);
        if (fs.hasAttribute("PMCC") == false) fs.addAttribute("PMCC", AttributeType.DOUBLE);
        if (fs.hasAttribute("RMCC") == false) fs.addAttribute("RMCC", AttributeType.DOUBLE);
    }
    if (MECL) {
    	mecSchema.addAttribute("ID", (AttributeType) typim);
    	mecSchema.addAttribute("AMCC", AttributeType.DOUBLE);
    	mecSchema.addAttribute("PMCC", AttributeType.DOUBLE);
    	mecSchema.addAttribute("RMCC", AttributeType.DOUBLE);
    }
    FeatureCollection resultmec = new FeatureDataset(mecSchema);
    
    if (MICPA) {
        if (fs.hasAttribute("AMIC") == false) fs.addAttribute("AMIC", AttributeType.DOUBLE);
        if (fs.hasAttribute("PMIC") == false) fs.addAttribute("PMIC", AttributeType.DOUBLE);
        if (fs.hasAttribute("RMIC") == false) fs.addAttribute("RMIC", AttributeType.DOUBLE);
//        if (fs.hasAttribute("XMIC") == false) fs.addAttribute("XMIC", AttributeType.DOUBLE);
//        if (fs.hasAttribute("YMIC") == false) fs.addAttribute("YMIC", AttributeType.DOUBLE);
    }
    if (MICL) {
    	micSchema.addAttribute("ID", (AttributeType) typim);
    	micSchema.addAttribute("AMIC", AttributeType.DOUBLE);
    	micSchema.addAttribute("PMIC", AttributeType.DOUBLE);
    	micSchema.addAttribute("RMIC", AttributeType.DOUBLE);
    }
    FeatureCollection resultmic = new FeatureDataset(micSchema);
    
    if (LTWRD && !fs.hasAttribute("LTWR")) fs.addAttribute("LTWR", AttributeType.DOUBLE);
    if (WTLRD && !fs.hasAttribute("WTLR")) fs.addAttribute("WTLR", AttributeType.DOUBLE);
    if (ELLFD && !fs.hasAttribute("ELLF")) fs.addAttribute("ELLF", AttributeType.DOUBLE);
    if (CIRRD && !fs.hasAttribute("CIRR")) fs.addAttribute("CIRR", AttributeType.DOUBLE);
    if (ZFORD && !fs.hasAttribute("ZFOR")) fs.addAttribute("ZFOR", AttributeType.DOUBLE);
    if (COMFD && !fs.hasAttribute("COMF")) fs.addAttribute("COMF", AttributeType.DOUBLE);
    if (MCIRD && !fs.hasAttribute("MCIR")) fs.addAttribute("MCIR", AttributeType.DOUBLE);
    if (DISMD && !fs.hasAttribute("DISM")) fs.addAttribute("DISM", AttributeType.DOUBLE);
    if (COMID && !fs.hasAttribute("COMI")) fs.addAttribute("COMI", AttributeType.DOUBLE);
    if (HFORD && !fs.hasAttribute("HFOR")) fs.addAttribute("HFOR", AttributeType.DOUBLE);
    if (ELOFD && !fs.hasAttribute("ELOF")) fs.addAttribute("ELOF", AttributeType.DOUBLE);
    if (LEMRD && !fs.hasAttribute("LEMR")) fs.addAttribute("LEMR", AttributeType.DOUBLE);
    if (REGFD && !fs.hasAttribute("REGF")) fs.addAttribute("REGF", AttributeType.DOUBLE);
    if (SHAFD && !fs.hasAttribute("SHAF")) fs.addAttribute("SHAF", AttributeType.DOUBLE);
    if (CONVD && !fs.hasAttribute("CONV")) fs.addAttribute("CONV", AttributeType.DOUBLE);
    if (CONCD && !fs.hasAttribute("CONC")) fs.addAttribute("CONC", AttributeType.DOUBLE);
    if (SOLID && !fs.hasAttribute("SOLI")) fs.addAttribute("SOLI", AttributeType.DOUBLE);
    if (RECTD && !fs.hasAttribute("RECT")) fs.addAttribute("RECT", AttributeType.DOUBLE);
    if (ROUND && !fs.hasAttribute("ROUN")) fs.addAttribute("ROUN", AttributeType.DOUBLE);
    if (SPHED && !fs.hasAttribute("SPHE")) fs.addAttribute("SPHE", AttributeType.DOUBLE);

    FeatureCollection newDataset = null;
    newDataset = new FeatureDataset(fs);
 
    
// main script calculating BB, CH, MER, MEC and MIC
    int sayar = 1;
    for (Iterator iter = fc.iterator(); iter.hasNext();) {
    	Feature element = (Feature) iter.next();
        BasicFeature nf = null;
    	nf = new BasicFeature(fs);
        Geometry geom = element.getGeometry();
        
        for (int i = 0 ; i < (onlem) ; i++) {
            nf.setAttribute(i, element.getAttribute(i));
        }

        P = geom.getLength();
        A = geom.getArea();
        
        if (PPMSD) {
      	    nf.setAttribute("PPOL", Double.parseDouble(df.format(P/CBU)));
            nf.setAttribute("APOL", Double.parseDouble(df.format(A/ABU)));
            nf.setAttribute("XPOL", Double.parseDouble(df.format(geom.getCentroid().getX())));
            nf.setAttribute("YPOL", Double.parseDouble(df.format(geom.getCentroid().getY())));
        }        

  	    Object value = sayar;
    	progressDialog.report(value+". Polygon is being processed ...");
  	    if (usefield) value = element.getAttribute(attribim);
  	    sayar++;
  	    
        if (!usefield) {
        	nf.setAttribute(cg, value);
        }
        
	    // Calculation of bounding boxes (BB) and writing BB parameters to the attribute table of input layer
	    Geometry BBS = geom.getEnvelope();
	    double bbcevrem = BBS.getLength();
	    double bbaream = BBS.getArea();
	    Envelope BBT = BBS.getEnvelopeInternal();
	    double bbwidth = BBT.getWidth();
	    double bbheight = BBT.getHeight();
	    if (BBPA) {
	      nf.setAttribute("PBOB", Double.parseDouble(df.format(bbcevrem/CBU)));
	      nf.setAttribute("ABOB", Double.parseDouble(df.format(bbaream/ABU)));
//	      nf.setAttribute("WMBB", Double.parseDouble(df.format(bbwidth/CBU)));
//	      nf.setAttribute("HMBB", Double.parseDouble(df.format(bbheight/CBU)));
	    }
	    // Creation of BB and writing BB parameters to the attribute table of the BB layer
	    if (BBL) {
	      Feature feature = new BasicFeature(bbSchema);
	      feature.setGeometry(BBS);
	      feature.setAttribute("ID", value);
	      feature.setAttribute("PBOB", Double.parseDouble(df.format(bbcevrem/CBU)));
	      feature.setAttribute("ABOB", Double.parseDouble(df.format(bbaream/ABU)));
//	      feature.setAttribute("WMBB", Double.parseDouble(df.format(bbwidth/CBU)));
//	      feature.setAttribute("HMBB", Double.parseDouble(df.format(bbheight/CBU)));
	      resultbb.add(feature);
 	    } 
	    
        // Calculation of convex hull (CH) and writing CHU parameters to the attribute table of input layer
        Geometry CH = geom.convexHull();
  	    double PCHU = CH.getLength();
  	    double ACHU = CH.getArea();
        if (CHUL) {
    	    Polygon convexHullum = new GeometryFactory().createPolygon(geom.convexHull().getCoordinates());
   	        Feature feature = new BasicFeature(chuSchema);
   	        feature.setGeometry(convexHullum);
  	        feature.setAttribute("ID", value);
   	        feature.setAttribute("PCHU", Double.parseDouble(df.format(PCHU/CBU)));
   	        feature.setAttribute("ACHU", Double.parseDouble(df.format(ACHU/ABU)));
   	        resultchu.add(feature);
        }
        // Creation of CH and writing CHU parameters to the attribute table of the CH layer
	    if (CHUPA) {
	          nf.setAttribute("PCHU", Double.parseDouble(df.format(PCHU/CBU)));
	          nf.setAttribute("ACHU", Double.parseDouble(df.format(ACHU/ABU)));
		}
	    

        // Calculation of minimum enclosing rectangle (MER) and writing MER parameters to the attribute table of input layer 
   	    Geometry MER = MEBFunction.getMEB(CH); // function is created 16/01/2021
    	double[] belo = MEBFunction.calMEB(MER); // function is created 16/01/2021
    	double recperim = belo[0];
    	double recarea = belo[1];
    	double uzunken = belo[2];
    	double darken = belo[3];
    	double fullAnglem = belo[4];
    	
    	
    	
  	    if (MERPA) {
  	       nf.setAttribute("PMEB", Double.parseDouble(df.format(recperim/CBU)));
  	       nf.setAttribute("AMEB", Double.parseDouble(df.format(recarea/ABU)));
  	       nf.setAttribute("LPOL", Double.parseDouble(df.format(uzunken/CBU)));
  	       nf.setAttribute("WPOL", Double.parseDouble(df.format(darken/CBU)));
  	       nf.setAttribute("AZIM", Double.parseDouble(df.format(fullAnglem)));
//  	       nf.setAttribute("orient", sol);
  	    }    

        // Creation of MER and writing MER parameters to the attribute table of the MER layer 
  	    if (MERL) {
  	      Feature feature = new BasicFeature(merSchema);
  	      feature.setGeometry(MER);
	      feature.setAttribute("ID", value);
  	      feature.setAttribute("PMEB", Double.parseDouble(df.format(recperim/CBU)));
  	      feature.setAttribute("AMEB", Double.parseDouble(df.format(recarea/ABU)));
  	      feature.setAttribute("LPOL", Double.parseDouble(df.format(uzunken/CBU)));
  	      feature.setAttribute("WPOL", Double.parseDouble(df.format(darken/CBU)));
  	      feature.setAttribute("AZIM", Double.parseDouble(df.format(fullAnglem)));
//  	      feature.setAttribute("orient", sol);
  	      resultmer.add(feature);
   	    }  

  	    
  	    // Calculation of minimum enclosing circle (MEC) and writing MEC parameters to the attribute table of input layer
  	    MinimumBoundingCircle mbc = new MinimumBoundingCircle(geom);
        GeometryFactory gf = new GeometryFactory(); // geometry factory ...
  	    com.vividsolutions.jts.geom.Point mecnt = gf.createPoint(mbc.getCentre()); // creating centroid of MIC
        double mCircleRad = mbc.getRadius();
  	    Geometry mCircle = BufferOp.bufferOp(mecnt, mCircleRad, 128); // creating MEC
	    if (MECPA) {
          nf.setAttribute("PMCC", Double.parseDouble(df.format(2*Math.PI*mCircleRad/CBU)));
          nf.setAttribute("AMCC", Double.parseDouble(df.format(Math.PI*mCircleRad*mCircleRad/ABU)));
          nf.setAttribute("RMCC", Double.parseDouble(df.format(mCircleRad/CBU)));
	    }
	    // Creation of MEC and writing MEC parameters to the attribute table of the MEC layer
 	    if (MECL) {
 	      Feature feature = new BasicFeature(mecSchema);
 	      feature.setGeometry(mCircle);
	      feature.setAttribute("ID", value);
 	      feature.setAttribute("PMCC", Double.parseDouble(df.format(2*Math.PI*mCircleRad/CBU)));
 	      feature.setAttribute("AMCC", Double.parseDouble(df.format(Math.PI*mCircleRad*mCircleRad/ABU)));
 	      feature.setAttribute("RMCC", Double.parseDouble(df.format(mCircleRad/CBU)));
 	      resultmec.add(feature);
  	    }  

 	    
 	    // Calculation of maximum inscribed circles (MIC) and writing MIC parameters to the attribute table of input layer
//	    if (MICL || MICPA) {
	    	Polygon Poly = (Polygon) element.getGeometry();
	    	double[] gelo = MICFunction.getMIC(Poly, BBT, CH, 0.75, 2500); // using MIC function ... 
	  	    com.vividsolutions.jts.geom.Point micnt = gf.createPoint(new Coordinate(gelo[0], gelo[1])); // creating centroid of MIC
	  	    Geometry ebid = BufferOp.bufferOp(micnt, gelo[2], 128); // creating MIC
	        if (MICPA) {
	          nf.setAttribute("PMIC", Double.parseDouble(df.format(2*gelo[2]*Math.PI/CBU)));
	          nf.setAttribute("AMIC", Double.parseDouble(df.format(gelo[2]*gelo[2]*Math.PI/ABU)));
	          nf.setAttribute("RMIC", Double.parseDouble(df.format(gelo[2]/CBU)));
//	          nf.setAttribute("XMIC", gelo[0]);
//	          nf.setAttribute("YMIC", gelo[1]);
	        }
	 	    // Creation of MIC and writing MIC parameters to the attribute table of the MIC layer
	        if (MICL) {
	  	      Feature feature = new BasicFeature(micSchema);
		      feature.setGeometry(ebid);
    	      feature.setAttribute("ID", value);
	 	      feature.setAttribute("PMIC", Double.parseDouble(df.format(feature.getGeometry().getLength()/CBU)));
	 	      feature.setAttribute("AMIC", Double.parseDouble(df.format(feature.getGeometry().getArea()/ABU)));
	 	      feature.setAttribute("RMIC", Double.parseDouble(df.format(gelo[2]/CBU)));
		      resultmic.add(feature);
	        }
//	    }
	    
	 	// Calculations of the desired parameters and adding them into the attribute table of the input layer
  	    if (LTWRD) nf.setAttribute("LTWR", Double.parseDouble(df.format(uzunken/darken)));
  	    if (WTLRD) nf.setAttribute("WTLR", Double.parseDouble(df.format(darken/uzunken)));
  	    if (ELLFD) nf.setAttribute("ELLF", Double.parseDouble(df.format(Math.abs(uzunken-darken)/(uzunken+darken))));
	    if (COMFD) nf.setAttribute("COMF", Double.parseDouble(df.format(P/Math.sqrt(4*Math.PI*A))));
	    if (ELOFD) nf.setAttribute("ELOF", Double.parseDouble(df.format(Math.sqrt((4*A)/Math.PI)/uzunken)));
	    if (REGFD) nf.setAttribute("REGF", Double.parseDouble(df.format(Math.PI*uzunken*darken/(4*A))));
	    if (SHAFD) nf.setAttribute("SHAF", Double.parseDouble(df.format(((4*Math.PI*A)/(P*P))*(uzunken/darken))));
	    if (LEMRD) nf.setAttribute("LEMR", Double.parseDouble(df.format(Math.PI*uzunken*uzunken/(4*A))));
	    if (MCIRD) nf.setAttribute("MCIR", Double.parseDouble(df.format((4*Math.PI*A)/(P*P))));
	    if (CIRRD) nf.setAttribute("CIRR", Double.parseDouble(df.format((P*P)/A)));
	    if (ZFORD) nf.setAttribute("ZFOR", Double.parseDouble(df.format(16*A/(P*P))));
	    if (HFORD) nf.setAttribute("HFOR", Double.parseDouble(df.format(A/(uzunken*uzunken))));
	    if (CONVD) nf.setAttribute("CONV", Double.parseDouble(df.format(PCHU/P)));
	    if (CONCD) nf.setAttribute("CONC", Double.parseDouble(df.format((ACHU-A)/ABU)));
	    if (SOLID) nf.setAttribute("SOLI", Double.parseDouble(df.format(A/ACHU)));
	    if (RECTD) nf.setAttribute("RECT", Double.parseDouble(df.format(A/recarea)));
	    if (ROUND) nf.setAttribute("ROUN", Double.parseDouble(df.format((4*Math.PI*A)/(PCHU*PCHU))));
	    if (DISMD) nf.setAttribute("DISM", Double.parseDouble(df.format(1-(Math.sqrt(4*Math.PI*A)/P))));
	    if (COMID)nf.setAttribute("COMI", Double.parseDouble(df.format(1-((4*Math.PI*A)/(P*P)))));
	    if (SPHED) nf.setAttribute("SPHE", Double.parseDouble(df.format(gelo[2]/mCircleRad))); // Math.sqrt((4*Math.PI)/A)/2*micCircleRad

	    newDataset.add(nf);
        
    }
   
    
    actualLayer.setFeatureCollection(newDataset);
    if (MERL || MERL || MECL || BBL || MICL || CHUL) {
    	LayerStyleUtil.setLinearStyle(actualLayer, Color.black, 1, 0);
        actualLayer.fireAppearanceChanged();
    }

    
 	// Adding the desired layers into the map canvas 
	if (MICL) {
		Layer MIClyr = layerManager.addLayer("Morphometric Analysis for "+activefilename, "Maximum Inscribed Circles", resultmic);
	    LayerStyleUtil.setLinearStyle(MIClyr, Color.red, 1, 0);
	    MIClyr.fireAppearanceChanged();
	}
	if (MECL) {
		Layer MEClyr = layerManager.addLayer("Morphometric Analysis for "+activefilename, "Minimum Circumscribed Circle", resultmec);
	    LayerStyleUtil.setLinearStyle(MEClyr, Color.orange, 1, 0);
	    MEClyr.fireAppearanceChanged();
	}
	if (MERL) {
		Layer MERlyr = layerManager.addLayer("Morphometric Analysis for "+activefilename, "Minimum Enclosing Boxes", resultmer);
	    LayerStyleUtil.setLinearStyle(MERlyr, Color.blue, 1, 0);
	    MERlyr.fireAppearanceChanged();
	}
	if (BBL) {
		Layer BBlyr = layerManager.addLayer("Morphometric Analysis for "+activefilename, "Bounding Boxes", resultbb);
	    LayerStyleUtil.setLinearStyle(BBlyr, Color.cyan, 1, 0);
	    BBlyr.fireAppearanceChanged();
	}
	if (CHUL) {
		Layer CHUlyr = layerManager.addLayer("Morphometric Analysis for "+activefilename, "Convex Hulls", resultchu); 
	    LayerStyleUtil.setLinearStyle(CHUlyr, Color.green, 1, 0);
	    CHUlyr.fireAppearanceChanged();
	}

	
                        } catch (Exception e) {

                        } finally {
                            progressDialog.setVisible(false);
                            progressDialog.dispose();
                        }
                    }
                }).start();
            }
        });
        
        GUIUtil.centreOnWindow(progressDialog);
        progressDialog.setVisible(true);
        
        }

        // create a Jframe for the completed task
    	JOptionPane.showMessageDialog(tframe, comptask,
        headmsag, JOptionPane.PLAIN_MESSAGE);
    	
    return;

	}
}