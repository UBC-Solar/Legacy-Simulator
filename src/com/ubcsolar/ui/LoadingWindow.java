package com.ubcsolar.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.Notification;
import javax.swing.JPanel;
import java.awt.BorderLayout;


public class LoadingWindow extends JFrame implements Listener {
		
		private GlobalController mySession ; 
	

		public LoadingWindow(GlobalController toAdd) {
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			mySession = toAdd;
			
			JFrame frame = new JFrame("Loading");

		    ImageIcon loading = new ImageIcon("C:/Users/Hooman/workspace/sim/ajax-loader.gif");
		    frame.getContentPane().add(new JLabel("Please wait for a moment... ", loading, JLabel.CENTER));

		    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    frame.setSize(363, 186);
		    frame.setVisible(true);

		}

		@Override
		public void notify(Notification n) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void register() {
			// TODO Auto-generated method stub
			
		}

		

}
