package com.ubcsolar.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.TelemDataPacket;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CarTelemPacketWindow extends JFrame {
	private JTextField txtSpeed;
	private JTextField txtBms;
	private JTextField txtPack0;
	private JTextField txttotalcellvolt;
	private JTextField timeField;
	private GlobalController mySession;
	private DateFormat standardTimeFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
	private JTextField txtPack2;
	private JTextField txtMotor;
	private JTextField txtPack1;
	private JTextField txtPack3;
	private JTextField txtCharge;

	public CarTelemPacketWindow(GlobalController mySession) throws HeadlessException {
		setResizable(true);
		this.mySession = mySession;
		setTitleAndLogo();
		this.setBounds(500, 250, 400, 355);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 83, 0, 70, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblStateOfCharge = new JLabel("State Of Charge (%):");
		GridBagConstraints gbc_lblStateOfCharge = new GridBagConstraints();
		gbc_lblStateOfCharge.anchor = GridBagConstraints.EAST;
		gbc_lblStateOfCharge.insets = new Insets(0, 0, 5, 5);
		gbc_lblStateOfCharge.gridx = 1;
		gbc_lblStateOfCharge.gridy = 1;
		getContentPane().add(lblStateOfCharge, gbc_lblStateOfCharge);
		
		txtCharge = new JTextField();
		txtCharge.setHorizontalAlignment(SwingConstants.CENTER);
		txtCharge.setText("95.5");
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 5);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 2;
		gbc_textField_4.gridy = 1;
		getContentPane().add(txtCharge, gbc_textField_4);
		txtCharge.setColumns(10);
		
		JLabel lblSpeed = new JLabel("Speed (KM/H):");
		GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
		gbc_lblSpeed.anchor = GridBagConstraints.EAST;
		gbc_lblSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpeed.gridx = 1;
		gbc_lblSpeed.gridy = 2;
		getContentPane().add(lblSpeed, gbc_lblSpeed);
		
		txtSpeed = new JTextField();
		txtSpeed.setHorizontalAlignment(SwingConstants.CENTER);
		txtSpeed.setText("5.0");
		GridBagConstraints gbc_txtCarName = new GridBagConstraints();
		gbc_txtCarName.insets = new Insets(0, 0, 5, 5);
		gbc_txtCarName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCarName.gridx = 2;
		gbc_txtCarName.gridy = 2;
		getContentPane().add(txtSpeed, gbc_txtCarName);
		txtSpeed.setColumns(10);
		
		JLabel lblnewTotalVoltage = new JLabel("Total Voltage (V):");
		GridBagConstraints gbc_lblnewTotalVoltage = new GridBagConstraints();
		gbc_lblnewTotalVoltage.anchor = GridBagConstraints.EAST;
		gbc_lblnewTotalVoltage.insets = new Insets(0, 0, 5, 5);
		gbc_lblnewTotalVoltage.gridx = 1;
		gbc_lblnewTotalVoltage.gridy = 3;
		getContentPane().add(lblnewTotalVoltage, gbc_lblnewTotalVoltage);
		
		txttotalcellvolt = new JTextField();
		txttotalcellvolt.setText("44");
		txttotalcellvolt.setHorizontalAlignment(SwingConstants.CENTER);
		txttotalcellvolt.setColumns(10);
		GridBagConstraints gbc_txtLongitude = new GridBagConstraints();
		gbc_txtLongitude.insets = new Insets(0, 0, 5, 5);
		gbc_txtLongitude.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLongitude.gridx = 2;
		gbc_txtLongitude.gridy = 3;
		getContentPane().add(txttotalcellvolt, gbc_txtLongitude);
		
		JLabel lblTemperatures = new JLabel("Temperatures (\u00B0C)");
		lblTemperatures.setFont(new Font("Tahoma", Font.ITALIC, 11));
		GridBagConstraints gbc_lblTemperatures = new GridBagConstraints();
		gbc_lblTemperatures.insets = new Insets(0, 0, 5, 5);
		gbc_lblTemperatures.gridx = 2;
		gbc_lblTemperatures.gridy = 4;
		getContentPane().add(lblTemperatures, gbc_lblTemperatures);
		
		JLabel lblBms = new JLabel("BMS:");
		GridBagConstraints gbc_lblBms = new GridBagConstraints();
		gbc_lblBms.insets = new Insets(0, 0, 5, 5);
		gbc_lblBms.anchor = GridBagConstraints.EAST;
		gbc_lblBms.gridx = 1;
		gbc_lblBms.gridy = 5;
		getContentPane().add(lblBms, gbc_lblBms);
		
		txtBms = new JTextField();
		txtBms.setText("2");
		txtBms.setHorizontalAlignment(SwingConstants.CENTER);
		txtBms.setColumns(10);
		GridBagConstraints gbc_txtBms = new GridBagConstraints();
		gbc_txtBms.insets = new Insets(0, 0, 5, 5);
		gbc_txtBms.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtBms.gridx = 2;
		gbc_txtBms.gridy = 5;
		getContentPane().add(txtBms, gbc_txtBms);
		
		JLabel lblMotor = new JLabel("Motor:");
		GridBagConstraints gbc_lblMotor = new GridBagConstraints();
		gbc_lblMotor.insets = new Insets(0, 0, 5, 5);
		gbc_lblMotor.anchor = GridBagConstraints.EAST;
		gbc_lblMotor.gridx = 1;
		gbc_lblMotor.gridy = 6;
		getContentPane().add(lblMotor, gbc_lblMotor);
		
		txtMotor = new JTextField();
		txtMotor.setHorizontalAlignment(SwingConstants.CENTER);
		txtMotor.setText("2");
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 6;
		getContentPane().add(txtMotor, gbc_textField_1);
		txtMotor.setColumns(10);
		
		JLabel lblPack0 = new JLabel("Pack 0:");
		GridBagConstraints gbc_lblPack0 = new GridBagConstraints();
		gbc_lblPack0.anchor = GridBagConstraints.EAST;
		gbc_lblPack0.insets = new Insets(0, 0, 5, 5);
		gbc_lblPack0.gridx = 1;
		gbc_lblPack0.gridy = 7;
		getContentPane().add(lblPack0, gbc_lblPack0);
		
		txtPack0 = new JTextField();
		txtPack0.setText("3");
		txtPack0.setHorizontalAlignment(SwingConstants.CENTER);
		txtPack0.setColumns(10);
		GridBagConstraints gbc_txtLatitude = new GridBagConstraints();
		gbc_txtLatitude.insets = new Insets(0, 0, 5, 5);
		gbc_txtLatitude.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLatitude.gridx = 2;
		gbc_txtLatitude.gridy = 7;
		getContentPane().add(txtPack0, gbc_txtLatitude);
		
		JLabel lblPack1 = new JLabel("Pack 1:");
		GridBagConstraints gbc_lblPack1 = new GridBagConstraints();
		gbc_lblPack1.insets = new Insets(0, 0, 5, 5);
		gbc_lblPack1.anchor = GridBagConstraints.EAST;
		gbc_lblPack1.gridx = 1;
		gbc_lblPack1.gridy = 8;
		getContentPane().add(lblPack1, gbc_lblPack1);
		
		txtPack1 = new JTextField();
		txtPack1.setText("4");
		txtPack1.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 2;
		gbc_textField_2.gridy = 8;
		getContentPane().add(txtPack1, gbc_textField_2);
		txtPack1.setColumns(10);
		
		JLabel lblPack2 = new JLabel("Pack 2:");
		GridBagConstraints gbc_lblPack2 = new GridBagConstraints();
		gbc_lblPack2.insets = new Insets(0, 0, 5, 5);
		gbc_lblPack2.anchor = GridBagConstraints.EAST;
		gbc_lblPack2.gridx = 1;
		gbc_lblPack2.gridy = 9;
		getContentPane().add(lblPack2, gbc_lblPack2);
		
		txtPack2 = new JTextField();
		txtPack2.setText("2");
		txtPack2.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.insets = new Insets(0, 0, 5, 5);
		gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_5.gridx = 2;
		gbc_textField_5.gridy = 9;
		getContentPane().add(txtPack2, gbc_textField_5);
		txtPack2.setColumns(10);
		
		JLabel lblPack3 = new JLabel("Pack 3:");
		GridBagConstraints gbc_lblPack3 = new GridBagConstraints();
		gbc_lblPack3.insets = new Insets(0, 0, 5, 5);
		gbc_lblPack3.anchor = GridBagConstraints.EAST;
		gbc_lblPack3.gridx = 1;
		gbc_lblPack3.gridy = 10;
		getContentPane().add(lblPack3, gbc_lblPack3);
		
		txtPack3 = new JTextField();
		txtPack3.setHorizontalAlignment(SwingConstants.CENTER);
		txtPack3.setText("5");
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.insets = new Insets(0, 0, 5, 5);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 2;
		gbc_textField_3.gridy = 10;
		getContentPane().add(txtPack3, gbc_textField_3);
		txtPack3.setColumns(10);
		
		JLabel lblTime = new JLabel("Time:");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.EAST;
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.gridx = 1;
		gbc_lblTime.gridy = 11;
		getContentPane().add(lblTime, gbc_lblTime);
		
		timeField = new JTextField();
		timeField.setText(this.standardTimeFormat.format(System.currentTimeMillis()));
		timeField.setHorizontalAlignment(SwingConstants.CENTER);
		timeField.setColumns(10);
		GridBagConstraints gbc_timeField = new GridBagConstraints();
		gbc_timeField.insets = new Insets(0, 0, 5, 5);
		gbc_timeField.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeField.gridx = 2;
		gbc_timeField.gridy = 11;
		getContentPane().add(timeField, gbc_timeField);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handleOkClick();
			}
		});
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				handleOkClick();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.anchor = GridBagConstraints.EAST;
		gbc_btnOk.insets = new Insets(0, 0, 5, 5);
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 12;
		getContentPane().add(btnOk, gbc_btnOk);
		
		btnOk.requestFocus();
		btnOk.requestFocusInWindow();
		getRootPane().setDefaultButton(btnOk);
		btnOk.requestFocus();
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeWindow();
			}
		});
		
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 5, 0);
		gbc_btnCancel.anchor = GridBagConstraints.WEST;
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = 12;
		getContentPane().add(btnCancel, gbc_btnCancel);
	}
	
	//------------------------------------------------------
	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}
	
	private void handleOkClick(){
		
		//Get all the text from the text fields
		String longval = this.txtSpeed.getText();
		String nextval = this.txttotalcellvolt.getText();
		String TempBms = this.txtBms.getText();
		String TempMotor = this.txtMotor.getText();
		String TempPack0 = this.txtPack0.getText();
		String TempPack1 = this.txtPack1.getText();
		String TempPack2 = this.txtPack2.getText();
		String TempPack3 = this.txtPack3.getText();
		
		HashMap<String, Integer> temperatures = new HashMap<String, Integer>();
		int BMSTemp;
		try{
			BMSTemp= Integer.parseInt(TempBms);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("BMS temperature formatted incorrectly");
			return;
		}
		
		int MOTORTemp;
		try{
			MOTORTemp= Integer.parseInt(TempMotor);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Motor temperature formatted incorrectly");
			return;
		}
		
		int Temp0;
		try{
			Temp0= Integer.parseInt(TempPack0);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Pack 0 temperature formatted incorrectly");
			return;
		}
		
		int Temp1;
		try{
			Temp1= Integer.parseInt(TempPack1);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Pack 1 temperature formatted incorrectly");
			return;
		}
		
		int Temp2;
		try{
			Temp2= Integer.parseInt(TempPack2);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Pack 2 temperature formatted incorrectly");
			return;
		}
		
		int Temp3;
		try{
			Temp3= Integer.parseInt(TempPack3);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Pack 3 temperature formatted incorrectly");
			return;
		}
		
		temperatures.put("bms", BMSTemp);
		temperatures.put("motor", MOTORTemp);
		temperatures.put("pack0", Temp0);
		temperatures.put("pack1", Temp1);
		temperatures.put("pack2", Temp2);
		temperatures.put("pack3", Temp3);
		
		double speedget;
		try{
			speedget= Double.parseDouble(longval);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Speed formatted incorrectly");
			return;
		}
		
		int totalVoltage;
		try{
			totalVoltage= Integer.parseInt(nextval);
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Total voltage formatted incorrectly");
			return;
		}
		
		double stateOfCharge;
		try{ 
			stateOfCharge = Double.parseDouble(this.txtCharge.getText()); // TODO
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("State of charge formatted incorrectly");
			return;
		}
		
		HashMap<Integer,ArrayList<Float>> cellvoltages = new HashMap<Integer,ArrayList<Float>>();
		for(int i = 0; i<4; i++){ //Current number of cells coming in pack is 4. Will probably have to adjust that.
			cellvoltages.put(i, generatePackVoltages(totalVoltage));
		}
		
		double time;
		try {
			time = this.standardTimeFormat.parse(this.timeField.getText()).getTime();
		} catch (ParseException e) {
			this.handleError("time formatted incorrectly");
			return;
		}
		
		TelemDataPacket newPacket = new TelemDataPacket(speedget, totalVoltage, temperatures, cellvoltages,stateOfCharge, time);
		TelemDataPacket testpacket = generateNewTelemDataPack();
		mySession.getMyCarController().adviseOfNewCarReport(newPacket);
		
		this.closeWindow();
	}
	
	
	
	private void closeWindow(){
		this.dispose();
	}
	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Add Car TelemPacket\r\n");
	}
//-------------------------------------------------------------------------------------------------------------	
	private TelemDataPacket generateNewTelemDataPack(){
		Random rng = new Random();
		double speed = 5;
		if(speed < 0)
			speed = 0;
		int totalV = 5;
		totalV = totalV > 50 ? 50 : totalV < 40 ? 40 : totalV;
		double stateOfCharge= 80;
		
		HashMap<String,Integer> temperatures = new HashMap<String,Integer>();
		temperatures.put("bms", 1);
		temperatures.put("motor", 2);
		temperatures.put("pack0", 3);
		temperatures.put("pack1", 4);
		temperatures.put("pack2", 5);
		temperatures.put("pack3", 6);
		HashMap<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(int i = 0; i<4; i++){ //Current number of cells coming in pack is 4. Will probably have to adjust that.
			cellVoltages.put(i, generatePackVoltages(totalV));
		}
		TelemDataPacket tempPacket = new TelemDataPacket(speed, (int)totalV, temperatures, cellVoltages, stateOfCharge);

		return tempPacket;
	}
	
	
	private ArrayList<Float> generatePackVoltages(double totalV){
		ArrayList<Float> cell1 = new ArrayList<Float>();
		Random rng = new Random();
		
		putPackVoltages(10, totalV, cell1);
		return cell1;
	}
	
	private void putPackVoltages(int nCells, double totalV, ArrayList<Float> voltages){
		if(nCells == 0){
			return;
		}else if(nCells == 1){
			voltages.add((float)totalV);
			return;
		}
		
		Random rng = new Random();
		int half1 = nCells / 2;
		double middleV = totalV * half1 / nCells;
		middleV = middleV * (1.0 + 0.2 * rng.nextFloat() + 0.1);
		putPackVoltages(half1, middleV, voltages);
		putPackVoltages(nCells-half1, totalV-middleV, voltages);
	}
}
