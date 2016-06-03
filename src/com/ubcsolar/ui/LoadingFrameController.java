package com.ubcsolar.ui;

import javax.swing.JFrame;

import com.ubcsolar.Main.GlobalController;

public class LoadingFrameController {
	
	protected GlobalController mySession; //reference to the Global Controller
	private JFrame loadFrame;//The Loading frame when loading a map or forecast or simulation to show a process is being done.


	public LoadingFrameController(GlobalController globalController) {
		mySession = globalController;
	}


	
	public void closeLoadFrame() {
		// TODO Auto-generated method stub
		loadFrame.setVisible(false);
	}


	public void lunchLoadFrame() {
		// TODO Auto-generated method stub
		loadFrame.setVisible(true);

	}

}
