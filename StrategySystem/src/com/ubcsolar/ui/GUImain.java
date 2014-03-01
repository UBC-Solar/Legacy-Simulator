package com.ubcsolar.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUImain {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUImain window = new GUImain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUImain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new SpringLayout());
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnModules = new JMenu("Modules");
		menuBar.add(mnModules);
		
		JMenuItem mntmMap = new JMenuItem("Map");
		mntmMap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new JFrame("Map").setVisible(true);
			}
		});
		mnModules.add(mntmMap);
		
		JMenuItem mntmSimulator = new JMenuItem("Simulator");
		mnModules.add(mntmSimulator);
		
		JMenuItem mntmPerformance = new JMenuItem("Performance");
		mnModules.add(mntmPerformance);
		
		JMenuItem mntmStrategy = new JMenuItem("Strategy");
		mnModules.add(mntmStrategy);
	}
}
