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
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Window.Type;
import java.awt.Dialog.ModalExclusionType;


public  class LoadingWindow extends JFrame  {
		
	private GlobalController mySession ; 
	

	public LoadingWindow(GlobalController toAdd) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mySession = toAdd;
		setBounds(760, 390, 400, 300);
		setTitleAndLogo();
	    ImageIcon loading = new ImageIcon("C:/Users/Hooman/workspace/sim/ajax-loader.gif");

	    getContentPane().add(new JLabel("Loading. Please wait for a moment... ", loading, JLabel.CENTER));
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		//this.setType(Type.POPUP);
		//this.setEnabled(false);
		setAlwaysOnTop(true);
		setResizable(false);

	}
	
	private void setTitleAndLogo() {
		setIconImage(mySession.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		setTitle("Loading");
	}
}
