package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.notification.NewSimulationReportNotification;
import com.ubcsolar.notification.Notification;
import java.awt.Font;

public class SimPanel extends JPanel implements Listener {

	private JPanel panel;
	private JLabel lblNoSim;
	private JLabel lblTime;
	
	protected GUImain parent;
	private GlobalController mySession;
	
	public SimPanel (GUImain parent, GlobalController session){
		mySession = session;
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		panel.add(panel_2, gbc_panel_2);
		
		JLabel lblNewLabel = new JLabel("Sim");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 3;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		
		JPanel panel_3 = new JPanel();
		add(panel_3, BorderLayout.CENTER);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		lblTime = new JLabel("");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.insets = new Insets(0, 0, 5, 0);
		gbc_lblTime.gridx = 0;
		gbc_lblTime.gridy = 1;
		panel_3.add(lblTime, gbc_lblTime);
		
		lblNoSim = new JLabel("No Simulation Has Run Yet");
		lblNoSim.setFont(new Font("Tahoma", Font.ITALIC, 11));
		GridBagConstraints gbc_lblNoSim = new GridBagConstraints();
		gbc_lblNoSim.insets = new Insets(0, 0, 5, 0);
		gbc_lblNoSim.gridx = 0;
		gbc_lblNoSim.gridy = 3;
		panel_3.add(lblNoSim, gbc_lblNoSim);
		
		
		JButton btnAdvanced_1 = new JButton("Advanced");
		GridBagConstraints gbc_btnAdvanced_1 = new GridBagConstraints();
		gbc_btnAdvanced_1.gridx = 0;
		gbc_btnAdvanced_1.gridy = 7;
		panel_3.add(btnAdvanced_1, gbc_btnAdvanced_1);
		btnAdvanced_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchSim();
			}
		});
		register();
	}
	
	protected void launchSim(){
		parent.launchSim();
	}

	private void updateAllLabels(String string){
		lblNoSim.setText("");
		lblTime.setText("Last run at "+string+" (hr::min::sec)");
		
	}
	
	
	@Override
	public void notify(Notification n) {
		if(n.getClass()==NewSimulationReportNotification.class && ((NewSimulationReportNotification) n).getSimReport ().getSimFrames ().size ()>0){
			updateAllLabels(GlobalValues.hourMinSec.format(n.getTimeCreated()));
		}
	}

	@Override
	public void register() {

		mySession.register(this, NewSimulationReportNotification.class);
	}
	
}
