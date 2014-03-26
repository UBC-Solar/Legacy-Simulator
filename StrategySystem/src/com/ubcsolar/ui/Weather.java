package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Weather extends JFrame {

	private JPanel contentPane;
	private GlobalController mySession;

	/**
	 * Launch the application.
	 *//*//don't need a main here.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Weather frame = new Weather();
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
	public Weather(GlobalController mySession) {
		this.mySession = mySession;
		setTitle("Weather");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 489, 358);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenuItem mntmWeatherMap = new JMenuItem("Weather map");
		menuBar.add(mntmWeatherMap);
		
		JMenuItem mntmForecasts = new JMenuItem("Forecasts");
		menuBar.add(mntmForecasts);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	}

}
