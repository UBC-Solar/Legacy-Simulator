package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;

import java.awt.GridBagLayout;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CarAdvancedWindow extends JFrame {

	private JPanel contentPane;
	private GlobalController mySession;

	/**
	 * Launch the application.
	 *//*//no main()needed here
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Performance frame = new Performance();
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
	public CarAdvancedWindow(GlobalController mySession) {
		this.mySession = mySession;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFiles = new JMenu("Files");
		menuBar.add(mnFiles);
		
		JMenu mnOther = new JMenu("Other");
		menuBar.add(mnOther);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JButton btnStartFakeCar = new JButton("Start Fake Car");
		GridBagConstraints gbc_btnStartFakeCar = new GridBagConstraints();
		gbc_btnStartFakeCar.gridx = 0;
		gbc_btnStartFakeCar.gridy = 0;
		contentPane.add(btnStartFakeCar, gbc_btnStartFakeCar);
		btnStartFakeCar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startFakeCar();
			}
		});
		setTitleAndLogo();
		
		JButton btnStartRealCar = new JButton("Try Connect to Real Car");
		GridBagConstraints gbc_btnStartRealCar = new GridBagConstraints();
		gbc_btnStartFakeCar.gridx = 0;
		gbc_btnStartFakeCar.gridy = 0;
		contentPane.add(btnStartRealCar, gbc_btnStartRealCar);
		btnStartRealCar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BTCarSelectDialog(mySession).setVisible(true);
			}
		});
		JButton btnStopCar = new JButton("Stop Car/Connection");
		GridBagConstraints gbc_btnbtnStopCar = new GridBagConstraints();
		gbc_btnStartFakeCar.gridx = 0;
		gbc_btnStartFakeCar.gridy = 0;
		contentPane.add(btnStopCar, gbc_btnbtnStopCar);
		btnStopCar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopRealCar();
			}
			
		});
	}
	
	private void setTitleAndLogo(){
		this.setIconImage(GlobalValues.iconImage.getImage());
		this.setTitle("Performance");
	}
	private void stopRealCar(){
		mySession.getMyCarController().stopListeningToCar();
	}
	private void startRealCar(){
		mySession.getMyCarController().establishNewConnection();
	}
	private void startFakeCar(){
		mySession.getMyCarController().startFakeCar();
	}

}
