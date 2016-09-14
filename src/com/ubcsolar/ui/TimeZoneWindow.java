package com.ubcsolar.ui;

import java.awt.GridBagLayout;

import javax.swing.JFrame;

import com.ubcsolar.Main.GlobalController;
import javax.swing.JTextPane;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JFormattedTextField;
import java.awt.Font;
import javax.swing.SwingConstants;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JButton;

public class TimeZoneWindow extends JFrame{
	private GlobalController mySession;
	
	public TimeZoneWindow(GlobalController mySession){
		this.mySession = mySession;
		setTitle("Change timezone");
		setBounds(100, 100, 300, 170);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JFormattedTextField timeDisplayField = new JFormattedTextField();
		timeDisplayField.setEditable(false);
		timeDisplayField.setText("GMT -07:00");
		timeDisplayField.setHorizontalAlignment(SwingConstants.CENTER);
		timeDisplayField.setFont(new Font("Tahoma", Font.PLAIN, 40));
		GridBagConstraints gbc_timeDisplayField = new GridBagConstraints();
		gbc_timeDisplayField.insets = new Insets(0, 0, 5, 0);
		gbc_timeDisplayField.gridwidth = 2;
		gbc_timeDisplayField.fill = GridBagConstraints.BOTH;
		gbc_timeDisplayField.gridx = 0;
		gbc_timeDisplayField.gridy = 0;
		getContentPane().add(timeDisplayField, gbc_timeDisplayField);
		
		JButton btnIncrementTime = new JButton("+00:30");
		GridBagConstraints gbc_btnIncrementTime = new GridBagConstraints();
		gbc_btnIncrementTime.fill = GridBagConstraints.VERTICAL;
		gbc_btnIncrementTime.insets = new Insets(0, 0, 5, 5);
		gbc_btnIncrementTime.gridx = 0;
		gbc_btnIncrementTime.gridy = 1;
		getContentPane().add(btnIncrementTime, gbc_btnIncrementTime);
		
		JButton btnDecrementTime = new JButton("-00:30");
		GridBagConstraints gbc_btnDecrementTime = new GridBagConstraints();
		gbc_btnDecrementTime.fill = GridBagConstraints.VERTICAL;
		gbc_btnDecrementTime.insets = new Insets(0, 0, 5, 0);
		gbc_btnDecrementTime.weightx = 1.0;
		gbc_btnDecrementTime.gridx = 1;
		gbc_btnDecrementTime.gridy = 1;
		getContentPane().add(btnDecrementTime, gbc_btnDecrementTime);
		
		JButton btnOk = new JButton("OK");
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.fill = GridBagConstraints.VERTICAL;
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 0;
		gbc_btnOk.gridy = 2;
		getContentPane().add(btnOk, gbc_btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.fill = GridBagConstraints.VERTICAL;
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 2;
		getContentPane().add(btnCancel, gbc_btnCancel);
	
	}
}
