package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Strategy extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application. *Depreciated*.
	 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Strategy frame = new Strategy();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	/**
	 * Create the frame.
	 */
	public Strategy() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu Files = new JMenu("Files");
		menuBar.add(Files);
		
		JMenuItem mntmRegulations = new JMenuItem("Regulations");
		Files.add(mntmRegulations);
		
		JMenuItem mntmRules = new JMenuItem("Rules");
		Files.add(mntmRules);
		
		JMenuItem mntmInsuanceCar = new JMenuItem("Insuance & Car Info");
		Files.add(mntmInsuanceCar);
		
		JMenu mnNewMenu_1 = new JMenu("Maintenance");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmDailyMaintenance = new JMenuItem("Daily Maintenance");
		mnNewMenu_1.add(mntmDailyMaintenance);
		
		JMenuItem mntmNightlyMaintenance = new JMenuItem("Nightly Maintenance");
		mnNewMenu_1.add(mntmNightlyMaintenance);
		
		JMenuItem mntmPartsList = new JMenuItem("Parts List");
		mnNewMenu_1.add(mntmPartsList);
		
		JMenu menu = new JMenu("");
		menuBar.add(menu);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		setTitleAndLogo();
		}
		
		private void setTitleAndLogo(){
			//this.setIconImage(mySession.iconImage.getImage());
			//TODO renable this when I set up a session in Constructor. 
			this.setTitle("Strategy");
		}
}
