package com.ubcsolar.ui;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;

import jssc.SerialPortList;

import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BTPhoneSelectDialog extends JFrame {
	private GlobalController mySession;
	private JComboBox comPortComboBox;
	
	public BTPhoneSelectDialog(GlobalController mySession) throws HeadlessException {
		this.mySession = mySession;
		setResizable(false);
		setTitleAndLogo();
		this.setBounds(500, 250, 200, 105);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_1.gridx = 0;
		gbc_horizontalStrut_1.gridy = 0;
		getContentPane().add(horizontalStrut_1, gbc_horizontalStrut_1);
		
		JLabel lblComPort = new JLabel("COM Port: ");
		GridBagConstraints gbc_lblComPort = new GridBagConstraints();
		gbc_lblComPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblComPort.anchor = GridBagConstraints.EAST;
		gbc_lblComPort.gridx = 1;
		gbc_lblComPort.gridy = 0;
		getContentPane().add(lblComPort, gbc_lblComPort);
		
		comPortComboBox = new JComboBox();
		comPortComboBox.setModel(new DefaultComboBoxModel(SerialPortList.getPortNames()));
		GridBagConstraints gbc_comPortComboBox = new GridBagConstraints();
		gbc_comPortComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comPortComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comPortComboBox.gridx = 2;
		gbc_comPortComboBox.gridy = 0;
		getContentPane().add(comPortComboBox, gbc_comPortComboBox);
		
		JButton btnOk = new JButton("OK");
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				connectToCar();
				closeWindow();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 5, 5);
		gbc_btnOk.gridx = 1;
		gbc_btnOk.gridy = 1;
		getContentPane().add(btnOk, gbc_btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeWindow();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 5, 5);
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 1;
		getContentPane().add(btnCancel, gbc_btnCancel);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridx = 3;
		gbc_horizontalStrut.gridy = 2;
		getContentPane().add(horizontalStrut, gbc_horizontalStrut);
	}

	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Connect to BT GPS?");	
	}
	private void closeWindow(){
		this.dispose();
	}
	private void connectToCar(){
		String comPort = this.comPortComboBox.getSelectedItem() + "";
		mySession.getMapController().connectToCellPhone(comPort);// (comPort);
	}
	
	private void disconnectCellPhone(){
		mySession.getMapController().disconnectCellPhone();
	}
	
}
