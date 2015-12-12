package com.ubcsolar.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

public class ElevationFrame extends JFrame {
	private JTextField textField;
	public ElevationFrame() {
		
		Dimension miniMax = new Dimension(180, 225);
		this.setMinimumSize(miniMax);
		this.setResizable(false);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 71, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JButton btnNewButton = new JButton("Browse");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		panel.add(btnNewButton, gbc_btnNewButton);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblCoordsPerUrl = new JLabel("Coords per URL*");
		GridBagConstraints gbc_lblCoordsPerUrl = new GridBagConstraints();
		gbc_lblCoordsPerUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblCoordsPerUrl.gridx = 0;
		gbc_lblCoordsPerUrl.gridy = 1;
		panel.add(lblCoordsPerUrl, gbc_lblCoordsPerUrl);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(300, 1, 500, 1));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 1;
		panel.add(spinner, gbc_spinner);
		
		JButton btnGo = new JButton("Go!");
		GridBagConstraints gbc_btnGo = new GridBagConstraints();
		gbc_btnGo.insets = new Insets(0, 0, 5, 5);
		gbc_btnGo.gridx = 0;
		gbc_btnGo.gridy = 2;
		panel.add(btnGo, gbc_btnGo);
		
		JTextPane txtpnGooglesApi = new JTextPane();
		txtpnGooglesApi.setText("* Google's API allows only 10k calls per day. More coordinates per call = less calls, but also less accuracy. Max 500");
		GridBagConstraints gbc_txtpnGooglesApi = new GridBagConstraints();
		gbc_txtpnGooglesApi.anchor = GridBagConstraints.NORTH;
		gbc_txtpnGooglesApi.gridwidth = 2;
		gbc_txtpnGooglesApi.insets = new Insets(0, 0, 0, 5);
		gbc_txtpnGooglesApi.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtpnGooglesApi.gridx = 0;
		gbc_txtpnGooglesApi.gridy = 3;
		panel.add(txtpnGooglesApi, gbc_txtpnGooglesApi);
	}

}
