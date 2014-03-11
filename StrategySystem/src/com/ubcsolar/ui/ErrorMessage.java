package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ErrorMessage extends JDialog {

private ErrorMessage thisone = this	;
	
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ErrorMessage dialog = new ErrorMessage();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ErrorMessage() {
		setBounds(100, 100, 345, 186);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Error: IO Exception file was not loaded. ");
			lblNewLabel.setBounds(68, 10, 193, 14);
			contentPanel.add(lblNewLabel);
		}
		{
			JButton btnOkay = new JButton("      Okay");
			btnOkay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					thisone.setVisible(false);
				}
			});
			btnOkay.setBounds(118, 87, 89, 23);
			contentPanel.add(btnOkay);
		}
	}

}
