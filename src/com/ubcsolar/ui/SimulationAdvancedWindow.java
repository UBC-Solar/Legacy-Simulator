package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SimulationAdvancedWindow extends JFrame {

	private JPanel contentPane;
	private GlobalController mySession;
	
	/**
	 * Launch the application.
	 *//*//don't need a Main() here.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Simulation frame = new Simulation();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 * @param mySession 
	 */
	public SimulationAdvancedWindow(GlobalController mySession) {
		this.mySession = mySession;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenu mnNewMenu_1 = new JMenu("Results");
		menuBar.add(mnNewMenu_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JButton btnNewSimulation = new JButton("New Simulation");
		btnNewSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new NewSimulation();
				dialog.setVisible(true);
			}
		});
		GridBagConstraints gbc_btnNewSimulation = new GridBagConstraints();
		gbc_btnNewSimulation.gridx = 0;
		gbc_btnNewSimulation.gridy = 0;
		contentPane.add(btnNewSimulation, gbc_btnNewSimulation);
		
		setTitleAndLogo();
		}
		
		private void setTitleAndLogo(){
			this.setIconImage(mySession.iconImage.getImage());
			this.setTitle("Simulation");
		}

}
