package com.ubcsolar.Main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
