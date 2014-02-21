package com.ubcsolar.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JDesktopPane;
import javax.swing.BoxLayout;
import java.awt.ScrollPane;
import java.awt.TextArea;
import javax.swing.JMenuBar;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.List;
import java.awt.Choice;

public class GUI {

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
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
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 805, 479);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTree tree = new JTree();
		tree.setEditable(true);
		frame.getContentPane().add(tree, BorderLayout.WEST);
		
		ScrollPane scrollPane = new ScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.SOUTH);
		
		Box verticalBox = Box.createVerticalBox();
		frame.getContentPane().add(verticalBox, BorderLayout.NORTH);
		
		table = new JTable();
		table.setCellSelectionEnabled(true);
		frame.getContentPane().add(table, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnStrategy = new JMenu("Map");
		menuBar.add(mnStrategy);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Route map");
		mnStrategy.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Weather Map");
		mnStrategy.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Filters");
		mnStrategy.add(mntmNewMenuItem_2);
		
		JMenu mnWeather = new JMenu("Weather");
		menuBar.add(mnWeather);
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Satellite image");
		mnWeather.add(mntmNewMenuItem_5);
		
		JMenuItem mntmNewMenuItem_7 = new JMenuItem("Forecast");
		mnWeather.add(mntmNewMenuItem_7);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Something else");
		mnWeather.add(mntmNewMenuItem_6);
		
		JMenu mnPerformance = new JMenu("Performance");
		menuBar.add(mnPerformance);
		
		JMenuItem mntmNewMenuItem_8 = new JMenuItem("Monitors");
		mnPerformance.add(mntmNewMenuItem_8);
		
		JMenuItem mntmNewMenuItem_9 = new JMenuItem("Projections");
		mnPerformance.add(mntmNewMenuItem_9);
		
		JMenuItem mntmNewMenuItem_10 = new JMenuItem("Maintenance");
		mnPerformance.add(mntmNewMenuItem_10);
		
		JMenu mnNewMenu = new JMenu("Strategy");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Simulation");
		mnNewMenu.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Results");
		mnNewMenu.add(mntmNewMenuItem_4);
		
		JMenuItem mntmRegulations = new JMenuItem("Regulations");
		mnNewMenu.add(mntmRegulations);
	}

	// This is center display in main window for map
	public JTable getMap() {
		return table;
	}
	/*
	public JTextArea getForecast() {
		return Forecast;
	}
	public JTree getPerformance() {
		return tree;
	}*/
}
