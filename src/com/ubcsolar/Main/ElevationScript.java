/**
 * This Main class runs a limited program to import and add elevations to a KML file's coordinates
 * without starting up the entire program. As of this writing, this functionality is not contained within
 * the main program as it should be a case of 'convert once' and then use forever. 
 */
package com.ubcsolar.Main;

import javax.swing.ImageIcon;
import com.ubcsolar.ui.ElevationFrame;

public class ElevationScript {

	public ElevationScript() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ElevationFrame defaultFrame = new ElevationFrame(new ImageIcon("res/windowIcon.png"));
		defaultFrame.setVisible(true);
	}

}
