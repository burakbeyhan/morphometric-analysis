
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
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

import javax.swing.*;

import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.MultiInputDialog;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;
import com.vividsolutions.jump.workbench.ui.plugin.FeatureInstaller;
import com.vividsolutions.jump.workbench.ui.DualPaneInputDialog;


public class MorphoPolygonPlugIn extends AbstractPlugIn{

    private MorphoPolygonEngine engine = new MorphoPolygonEngine();
    
    public MorphoPolygonPlugIn() {
        // empty constructor
    }

    public String getName() {
        return "PolyMorph-2D (Morphometric Analysis of Polygons)"; // set the name / label of plugin for tooltips ...
    } 
    
    private Layer layer;
    String attribute;
    private static String Katmanim = "Select a polygon layer:";
    
    boolean use_attribute;
    
    
    public void initialize(PlugInContext context) throws Exception {
        FeatureInstaller featureInstaller = new FeatureInstaller(context.getWorkbenchContext());
        featureInstaller.addMainMenuPlugin(this, 
        	 new String[] {"Plugins"}, //menu path
        	"PolyMorph-2D", //name
        	 false, //checkbox
     	 new ImageIcon(this.getClass().getResource("/images/pol.png")), //icon 
        	 new MultiEnableCheck().add(context.getCheckFactory().createTaskWindowMustBeActiveCheck()).add(context.getCheckFactory().createAtLeastNLayersMustExistCheck(1)));
        	 context.getWorkbenchFrame().getToolBar().addPlugIn(new ImageIcon(getClass().getResource("/images/polt.png")), //icon 
          this, new MultiEnableCheck().add(context.getCheckFactory().createTaskWindowMustBeActiveCheck()).add(context.getCheckFactory().createAtLeastNLayersMustExistCheck(1)),
          context.getWorkbenchContext());
    }
    
    public static MultiEnableCheck createEnableCheck(WorkbenchContext workbenchContext) {
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);

        return new MultiEnableCheck()
        .add(checkFactory.createWindowWithLayerNamePanelMustBeActiveCheck());
    }

    
    
    public boolean execute(PlugInContext context) throws Exception {


    	// script for GUI
    	
    	final DualPaneInputDialog dialog = new DualPaneInputDialog(context.getWorkbenchFrame(), "PolyMorph-2D: Morphometric Analysis of 2D Polygon Features (PF)", true);
    	
    	dialog.setSideBarImage(IconLoader.icon("/images/11111.png"));
   	 	dialog.setSideBarDescription("PolyMorph-2D is a toolbox for morphometric analysis of vector-based polygon features. The plug-in allows researchers from various" +
		 		" disciplines to compute morphometric parameters of 2D input vector features forming a polygon. Further explanations can be found in the following paper:" + '\n' + '\n' + 
		 		"Güler, C., Beyhan, B. & Tağa, H. (2021) PolyMorph-2D: An open-source GIS plug-in for morphometric analysis of vector-based 2D polygon features." +
		 		" Geomorphology. https://doi.org/10.1016/j.geomorph.2021.107755"  + '\n' + '\n' + "Source code can be download from https://github.com/burakbeyhan/morphometric-analysis");
   	 	
        JCheckBox BALS = dialog.addCheckBox("<HTML><STRONG>"+"Basic Tasks"+"<HTML><STRONG>", true, "select all options");
//		dialog.addLabel("<HTML><STRONG>"+"Basic Tasks"+"<HTML><STRONG>");
        BALS.setPreferredSize(new Dimension(126, 17));
		dialog.addSeparator();
		
        JComboBox jcb_layer = dialog.addLayerComboBox(Katmanim, context.getCandidateLayer(0), "please select a layer", context.getLayerManager());
        jcb_layer.setPreferredSize(new Dimension(126, 20));

        JCheckBox cikti = dialog.addCheckBox("Use a field for a unique identifier for each PF", true, "for enumaration of PFs select a field");
        
        
        List fieldim = getFieldsFromLayerWithoutGeometry(context.getCandidateLayer(0));       
        
        Object val = fieldim.size() > 0 ? fieldim.iterator().next() : null;
        
        final JComboBox jcb_attribute = dialog.addComboBox("Select a field:", val, fieldim, "please select a field");
        jcb_attribute.setEnabled(true);
        jcb_attribute.setPreferredSize(new Dimension(126, 20));
//        jcb_attribute.getSelectedItem();
        
		JLabel LC = dialog.addLabel(" ");
        LC.setPreferredSize(new Dimension(12, 6));
		JButton LCO = dialog.addButton("<HTML><EM>"+"Layer Creation Operations"+"<HTML><EM>", "unselect all", "Click the desired layers to be created");
        JCheckBox BB = dialog.addCheckBox("Create Bounding Box (BOB) layer", true, "create and add BOBs to the map canvas");
        JCheckBox MER = dialog.addCheckBox("Create Minimum Enclosing Box (MEB) layer", true, "create and add MEBs to the map canvas");
		JCheckBox CHU = dialog.addCheckBox("Create Convex Hull (CHU) layer", true, "create and add CHUs to the map canvas");
        JCheckBox MEC = dialog.addCheckBox("Create Minimum Circumscribed Circle (MCC) layer", true, "create and add MCCs to the map canvas");
        JCheckBox MIC = dialog.addCheckBox("Create Maximum Inscribed Circle (MIC) layer", true, "create and add MICs to the map canvas");
        
		JLabel AT = dialog.addLabel(" "); // GUI
        AT.setPreferredSize(new Dimension(12, 6));
		JButton ATO = dialog.addButton("<HTML><EM>"+"Attribute Table Operations"+"<HTML><EM>", "unselect all", "Click the parameters to be added into the original attribute table");
        JCheckBox PPMS = dialog.addCheckBox("Add polygon parameters to attribute table", true, "add polygon parameters (perimeter and area) to attribute table");
        JCheckBox BBP = dialog.addCheckBox("Add BOB parameters to attribute table", true, "add BOB parameters (perimeter, area, width and height) to attribute table");
        JCheckBox MERP = dialog.addCheckBox("Add MEB parameters to attribute table", true, "add MEB parameters (perimeter, area, lengths of long and short sides and angle) to attribute table");
        JCheckBox CHUP = dialog.addCheckBox("Add CHU parameters to attribute table", true, "add CHU parameters (perimeter and area) to attribute table");
        JCheckBox MECP = dialog.addCheckBox("Add MCC parameters to attribute table", true, "add MCC parameters (perimeter, area and radius) to attribute table");
        JCheckBox MICP = dialog.addCheckBox("Add MIC parameters to attribute table", true, "add MIC parameters (perimeter, area and radius) to attribute table");

		JLabel UP = dialog.addLabel(" ");
		UP.setPreferredSize(new Dimension(12, 6));
		JLabel UT = dialog.addLabel("<HTML><EM>"+"Unit and Precision Specification for Display"+"<HTML><EM>");
		UT.setPreferredSize(new Dimension(UT.getPreferredSize().width, UT.getPreferredSize().height+4));
		List tips = new ArrayList();
		tips.add("millimeter (mm)");
		tips.add("centimeter (cm)");
		tips.add("decimeter (dm)");
        tips.add("meter (m)");
        tips.add("dekameter (dam)");
        tips.add("hectometer (hm)");
        tips.add("kilometer (km)");
  		final JComboBox CB = dialog.addComboBox("Unit of length:", "meter (m)", tips, "select a unit");
  		CB.setPreferredSize(new Dimension(126, 20));

        List cips = new ArrayList();
        cips.add("square mm (mm²)");
		cips.add("square cm (cm²)");
		cips.add("square dm (dm²)");
        cips.add("square m (m²)");
        cips.add("decare (daa)");
        cips.add("hectare (ha)");
        cips.add("square km (km²)");
  		final JComboBox AB = dialog.addComboBox("Unit of surface area:", "square m (m²)", cips, "select a unit");
  		AB.setPreferredSize(new Dimension(126, 20));

  		dialog.addIntegerField("Precision for display:", 3, 11, "precision field");  
        
		jcb_layer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	layer = dialog.getLayer(Katmanim);
                List list = getFieldsFromLayerWithoutGeometry(layer);
                if (list.size() == 0) {
                	jcb_attribute.setModel(new DefaultComboBoxModel(new String[0]));
                	jcb_attribute.setEnabled(true);
                }
                jcb_attribute.setModel(new DefaultComboBoxModel(list.toArray(new String[0])));
            }            
        });

		ActionListener disaver = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        boolean ciktim = dialog.getBoolean("Use a field for a unique identifier for each PF") ;
		        jcb_attribute.setEnabled(ciktim);
		      }
		};
		cikti.addActionListener(disaver);

		ActionListener goruver = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	int micm, merm, mccm, bbm, chum;
		    	if (dialog.getBoolean("Create Maximum Inscribed Circle (MIC) layer")) micm = 1; else micm = 0;
		    	if (dialog.getBoolean("Create Minimum Enclosing Box (MEB) layer")) merm = 1; else merm = 0;
		    	if (dialog.getBoolean("Create Minimum Circumscribed Circle (MCC) layer")) mccm = 1; else mccm = 0;
		    	if (dialog.getBoolean("Create Convex Hull (CHU) layer")) chum = 1; else chum = 0;
		    	if (dialog.getBoolean("Create Bounding Box (BOB) layer")) bbm = 1; else bbm = 0;
		    	dialog.setSideBarImage(IconLoader.icon("/images/"+micm+""+merm+""+mccm+""+chum+""+bbm+".png"));
		    	if (micm+merm+mccm+chum+bbm == 5) LCO.setText("unselect all");
		    	if (micm+merm+mccm+chum+bbm == 0) LCO.setText("select all");
		      }
		};
		MER.addActionListener(goruver);
		MIC.addActionListener(goruver);
		MEC.addActionListener(goruver);
		BB.addActionListener(goruver);
		CHU.addActionListener(goruver);
		LCO.addActionListener(goruver);
		BALS.addActionListener(goruver);
		
		ActionListener yapiver = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	int micm, merm, mccm, bbm, chum, ppm;
		    	if (dialog.getBoolean("Add polygon parameters to attribute table")) ppm = 1; else ppm = 0;
		    	if (dialog.getBoolean("Add BOB parameters to attribute table")) bbm = 1; else bbm = 0;
		    	if (dialog.getBoolean("Add MEB parameters to attribute table")) merm = 1; else merm = 0;
		    	if (dialog.getBoolean("Add CHU parameters to attribute table")) chum = 1; else chum = 0;
		    	if (dialog.getBoolean("Add MCC parameters to attribute table")) mccm = 1; else mccm = 0;
		    	if (dialog.getBoolean("Add MIC parameters to attribute table")) micm = 1; else micm = 0;
//		    	dialog.setSideBarImage(IconLoader.icon("/images/"+micm+""+merm+""+mccm+""+chum+""+bbm+""+ppm+".png"));
		    	if (micm+merm+mccm+chum+bbm+ppm == 6) ATO.setText("unselect all");
		    	if (micm+merm+mccm+chum+bbm+ppm == 0) ATO.setText("select all");
		      }
		};
		MERP.addActionListener(yapiver);
		MICP.addActionListener(yapiver);
		MECP.addActionListener(yapiver);
		BBP.addActionListener(yapiver);
		CHUP.addActionListener(yapiver);
		PPMS.addActionListener(yapiver);
		ATO.addActionListener(yapiver);
		BALS.addActionListener(yapiver);

		ActionListener yahep = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  if (LCO.getText() == "unselect all") {
		    			MER.setSelected(false);
		    			MIC.setSelected(false);
		    			MEC.setSelected(false);
		    			BB.setSelected(false);
		    			CHU.setSelected(false);
		    			LCO.setText("select all");
		    	  }
		    	  else {
		    			MER.setSelected(true);
		    			MIC.setSelected(true);
		    			MEC.setSelected(true);
		    			BB.setSelected(true);
		    			CHU.setSelected(true);
		    			LCO.setText("unselect all");
		    	  }
		      }
		};
		LCO.addActionListener(yahep);
		
		ActionListener yahic = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  if (ATO.getText() == "select all") {
				    	PPMS.setSelected(true);
				  		MERP.setSelected(true);
						MICP.setSelected(true);
						MECP.setSelected(true);
						BBP.setSelected(true);
						CHUP.setSelected(true);
		    			ATO.setText("unselect all");
		    	  }
		    	  else {
				    	PPMS.setSelected(false);
				  		MERP.setSelected(false);
						MICP.setSelected(false);
						MECP.setSelected(false);
						BBP.setSelected(false);
						CHUP.setSelected(false);
		    			ATO.setText("select all");
		    	  }
		      }
		};
		ATO.addActionListener(yahic);
		
		ActionListener ediver = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  if (dialog.getBoolean("<HTML><STRONG>"+"Basic Tasks"+"<HTML><STRONG>")) {
						cikti.setSelected(true);
						jcb_attribute.setEnabled(true);
		    			MER.setSelected(true);
		    			MIC.setSelected(true);
		    			MEC.setSelected(true);
		    			BB.setSelected(true);
		    			CHU.setSelected(true);
				    	PPMS.setSelected(true);
				  		MERP.setSelected(true);
						MICP.setSelected(true);
						MECP.setSelected(true);
						BBP.setSelected(true);
						CHUP.setSelected(true);
		    	  }
		    	  else {
						cikti.setSelected(false);
						jcb_attribute.setEnabled(false);
		    			MER.setSelected(false);
		    			MIC.setSelected(false);
		    			MEC.setSelected(false);
		    			BB.setSelected(false);
		    			CHU.setSelected(false);
				    	PPMS.setSelected(false);
				  		MERP.setSelected(false);
						MICP.setSelected(false);
						MECP.setSelected(false);
						BBP.setSelected(false);
						CHUP.setSelected(false);
		    	  }
		      }
		};
		BALS.addActionListener(ediver);
		
		dialog.setRightPane();
//		dialog.addLabel("<HTML><STRONG>"+"Derived Parameters"+"<HTML><STRONG>");
        JCheckBox ALLP = dialog.addCheckBox("<HTML><STRONG>"+"Derived Parameters"+"<HTML><STRONG>", true, "calculate all parameters");
//        ALLP.setLocation(ALLP.getLocation().x, BALS.getLocation().y);

		dialog.addSeparator();
        JCheckBox LTWR = dialog.addCheckBox("Length-to-Width Ratio (LTWR)", true, "calculate LTWR and add it to the attribute table");
        JCheckBox WTLR = dialog.addCheckBox("Width-to-Length Ratio (WTLR)", true, "calculate WTLR and add it to the attribute table");
        JCheckBox ELLF = dialog.addCheckBox("Ellipticity Factor (ELLF)", true, "calculate ELLF and add it to the attribute table");
        JCheckBox CIRR = dialog.addCheckBox("Circularity Ratio (CIRR)", true, "calculate CIRR and add it to the attribute table");
        JCheckBox ZFOR = dialog.addCheckBox("Zăvoianu's Form Factor (ZFOR)", true, "calculate ZFOR and add it to the attribute table");
        JCheckBox COMF = dialog.addCheckBox("Compactness Factor (COMF)", true, "calculate COMF and add it to the attribute table");
        JCheckBox MCIR = dialog.addCheckBox("Miller's Circularity Ratio (MCIR)", true, "calculate MCIR and add it to the attribute table");
        JCheckBox DISM = dialog.addCheckBox("Dispersion Measure (DISM)", true, "calculate DISM and add it to the attribute table");
        JCheckBox COMI = dialog.addCheckBox("Complexity Index (COMI)", true, "calculate COMI and add it to the attribute table");
        JCheckBox HFOR = dialog.addCheckBox("Horton's Form Factor (HFOR)", true, "calculate HFOR and add it to the attribute table");
        JCheckBox ELOF = dialog.addCheckBox("Elongation Factor (ELOF)", true, "calculate ELOF and add it to the attribute table");
        JCheckBox LEMR = dialog.addCheckBox("Lemniscate Ratio (LEMR)", true, "calculate LEMR and add it to the attribute table");
        JCheckBox REGF = dialog.addCheckBox("Regularity Factor (REGF)", true, "calculate REGF and add it to the attribute table");
        JCheckBox SHAF = dialog.addCheckBox("Shape Factor (SHAF)", true, "calculate SHAF and add it to the attribute table");
        JCheckBox CONV = dialog.addCheckBox("Convexity (CONV)", true, "calculate CONV and add it to the attribute table");
        JCheckBox CONC = dialog.addCheckBox("Concavity (CONC)", true, "calculate CONC and add it to the attribute table");
        JCheckBox SOLI = dialog.addCheckBox("Solidity (SOLI)", true, "calculate SOLI and add it to the attribute table");
        JCheckBox RECT = dialog.addCheckBox("Rectangularity (RECT)", true, "calculate RECT and add it to the attribute table");
        JCheckBox ROUN = dialog.addCheckBox("Roundness (ROUN)", true, "calculate ROUN and add it to the attribute table");
        JCheckBox SPHE = dialog.addCheckBox("Sphericity (SPHE)", true, "calculate SPHE and add it to the attribute table");

		ActionListener seciver = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  if (dialog.getBoolean("<HTML><STRONG>"+"Derived Parameters"+"<HTML><STRONG>")) {
		    	        LTWR.setSelected(true);
		    	        WTLR.setSelected(true);
		    	        ELLF.setSelected(true);
		    	        CIRR.setSelected(true);
		    	        ZFOR.setSelected(true);
		    	        COMF.setSelected(true);
		    	        MCIR.setSelected(true);
		    	        DISM.setSelected(true);
		    	        COMI.setSelected(true);
		    	        HFOR.setSelected(true);
		    	        ELOF.setSelected(true);
		    	        LEMR.setSelected(true);
		    	        REGF.setSelected(true);
		    	        SHAF.setSelected(true);
		    	        CONV.setSelected(true);
		    	        CONC.setSelected(true);
		    	        SOLI.setSelected(true);
		    	        RECT.setSelected(true);
		    	        ROUN.setSelected(true);
		    	        SPHE.setSelected(true);
		    	  }
		    	  else {
		    	        LTWR.setSelected(false);
		    	        WTLR.setSelected(false);
		    	        ELLF.setSelected(false);
		    	        CIRR.setSelected(false);
		    	        ZFOR.setSelected(false);
		    	        COMF.setSelected(false);
		    	        MCIR.setSelected(false);
		    	        DISM.setSelected(false);
		    	        COMI.setSelected(false);
		    	        HFOR.setSelected(false);
		    	        ELOF.setSelected(false);
		    	        LEMR.setSelected(false);
		    	        REGF.setSelected(false);
		    	        SHAF.setSelected(false);
		    	        CONV.setSelected(false);
		    	        CONC.setSelected(false);
		    	        SOLI.setSelected(false);
		    	        RECT.setSelected(false);
		    	        ROUN.setSelected(false);
		    	        SPHE.setSelected(false);
		    	  }
		      }
		};
		ALLP.addActionListener(seciver);

        GUIUtil.centreOnWindow(dialog);
        dialog.setVisible(true);

        if (!dialog.wasOKPressed()) {
            return false;
        }

        getDialogValues(dialog);
        engine.execute(context);

        return true;
    }



    private List getFieldsFromLayerWithoutGeometry(Layer lyr)
    {
      List fields = new ArrayList();
      FeatureSchema schema = lyr.getFeatureCollectionWrapper().getFeatureSchema();
      for (int i = 0; i < schema.getAttributeCount(); i++) {
        if (schema.getAttributeType(i) != AttributeType.GEOMETRY) {
          fields.add(schema.getAttributeName(i));
        }
      }
      return fields;
    }
   
    private void getDialogValues(MultiInputDialog dialog) {
        engine.aktarim0(dialog.getLayer(Katmanim));
        engine.aktarim1(dialog.getBoolean("Create Bounding Box (BOB) layer"));
        engine.aktarim2(dialog.getBoolean("Create Minimum Circumscribed Circle (MCC) layer"));
        engine.aktarim3(dialog.getBoolean("Create Minimum Enclosing Box (MEB) layer"));
        engine.aktarim4(dialog.getBoolean("Add BOB parameters to attribute table"));
        engine.aktarim5(dialog.getBoolean("Add MCC parameters to attribute table"));
        engine.aktarim6(dialog.getBoolean("Add MEB parameters to attribute table"));
        engine.aktarim7(dialog.getText("Select a field:"));
        engine.aktarim8(dialog.getText("Unit of length:"));
        engine.aktarim9(dialog.getText("Unit of surface area:"));
        engine.aktarim10(dialog.getBoolean("Use a field for a unique identifier for each PF"));
        engine.aktarim11(dialog.getBoolean("Create Maximum Inscribed Circle (MIC) layer"));
        engine.aktarim12(dialog.getBoolean("Add MIC parameters to attribute table"));  
        engine.aktarim13(dialog.getBoolean("Create Convex Hull (CHU) layer"));
        engine.aktarim14(dialog.getBoolean("Add CHU parameters to attribute table"));
        engine.aktarim15(dialog.getInteger("Precision for display:"));
        engine.aktarim16(dialog.getBoolean("Length-to-Width Ratio (LTWR)"));
        engine.aktarim17(dialog.getBoolean("Width-to-Length Ratio (WTLR)"));
        engine.aktarim18(dialog.getBoolean("Ellipticity Factor (ELLF)"));
        engine.aktarim19(dialog.getBoolean("Circularity Ratio (CIRR)"));
        engine.aktarim20(dialog.getBoolean("Zăvoianu's Form Factor (ZFOR)"));
        engine.aktarim21(dialog.getBoolean("Compactness Factor (COMF)"));
        engine.aktarim22(dialog.getBoolean("Miller's Circularity Ratio (MCIR)"));
        engine.aktarim23(dialog.getBoolean("Dispersion Measure (DISM)"));
        engine.aktarim24(dialog.getBoolean("Complexity Index (COMI)"));
        engine.aktarim25(dialog.getBoolean("Horton's Form Factor (HFOR)"));
        engine.aktarim26(dialog.getBoolean("Elongation Factor (ELOF)"));
        engine.aktarim27(dialog.getBoolean("Lemniscate Ratio (LEMR)"));
        engine.aktarim28(dialog.getBoolean("Regularity Factor (REGF)"));
        engine.aktarim29(dialog.getBoolean("Shape Factor (SHAF)"));
        engine.aktarim30(dialog.getBoolean("Convexity (CONV)"));
        engine.aktarim31(dialog.getBoolean("Concavity (CONC)"));
        engine.aktarim32(dialog.getBoolean("Solidity (SOLI)"));
        engine.aktarim33(dialog.getBoolean("Rectangularity (RECT)"));
        engine.aktarim34(dialog.getBoolean("Roundness (ROUN)"));
        engine.aktarim35(dialog.getBoolean("Sphericity (SPHE)"));
        engine.aktarim36(dialog.getBoolean("Add polygon parameters to attribute table"));
    }


    
}
