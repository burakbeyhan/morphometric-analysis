
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
import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

/**
 * 
 *  - this class loads the PlugIn into OpenJUMP
 *
 *  - class has to be called "Extension" on the end of classname
 *    to use the PlugIn in OpenJUMP 
 */

public class MorphoPolygonExtension extends Extension{

	/**
	 * calls PlugIn using class method xplugin.initialize() 
	 */
    public String getName() {
        return "PolyMorph-2D (Morphometric Analysis of Polygons)"; // set the name / label of plugin for tooltips ...
    } 
    
    public String getVersion() {
        return "2.1.0 (2020-11-01)";
    }
    
	public void configure(PlugInContext context) throws Exception{
		new MorphoPolygonPlugIn().initialize(context);
	}
	
}
