package com.ubcsolar.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		frame.setBounds(100, 100, 485, 347);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new SpringLayout());
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnModules = new JMenu("Modules");
		menuBar.add(mnModules);
		
		JMenuItem mntmMap = new JMenuItem("Map");
		mntmMap.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("test");
				JFrame frame = new Map();
				frame.setVisible(true);
			}
		});
		mnModules.add(mntmMap);
		
		JMenuItem mntmSimulator = new JMenuItem("Simulation");
		mntmSimulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame frame = new Simulation();
				frame.setVisible(true);
			}
		});
		mnModules.add(mntmSimulator);
		
		JMenuItem mntmPerformance = new JMenuItem("Performance");
		mntmPerformance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame frame = new Performance();
				frame.setVisible(true);
			}
		});
		mnModules.add(mntmPerformance);
		
		JMenuItem mntmStrategy = new JMenuItem("Strategy");
		mntmStrategy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new Strategy();
				frame.setVisible(true);
			}
		});
		mnModules.add(mntmStrategy);
	}
}
