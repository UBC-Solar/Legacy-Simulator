package com.ubcsolar.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.notification.Notification;
import javax.swing.JPanel;
import java.awt.BorderLayout;


public  class LoadingWindow extends JFrame implements Listener {
		
	private JFrame loadFrame;//The Loading frame when loading a map or forecast or simulation to show a process is being done.
	private GlobalController mySession ; 
	

	public LoadingWindow(GlobalController toAdd) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mySession = toAdd;
		loadFrame = new JFrame("Loading");

	    ImageIcon loading = new ImageIcon("C:/Users/Hooman/workspace/sim/ajax-loader.gif");
	    
	    loadFrame.getContentPane().add(new JLabel("Loading. Please wait for a moment... ", loading, JLabel.CENTER));

	    loadFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    loadFrame.setSize(555, 359);
	//    frame.setVisible(false);

	}

	@Override
	public void notify(Notification n) {
	// TODO Auto-generated method stub
			
	}

	@Override
	public void register() {
	// TODO Auto-generated method stub
			
	}
	public void lunchLoadFrame(){
		if(loadFrame == null){ //Shouldn't happen
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
					"Tried to open the Loading window, but was null");
			this.loadFrame = new LoadingWindow(this.mySession);
		}
		
		loadFrame.setVisible(true);
	}
	public void closeLoadFrame(){
		if(loadFrame == null){ //Shouldn't happen
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
					"Tried to open the Loading window, but was null");
			this.loadFrame = new LoadingWindow(this.mySession);
		}
		
		loadFrame.setVisible(false);
	}

		

}
