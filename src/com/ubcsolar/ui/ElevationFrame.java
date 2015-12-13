package com.ubcsolar.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom2.JDOMException;

import com.ubcsolar.map.JdomkmlInterface;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ElevationFrame extends JFrame {
	/**
	 * Added to get Eclipse to stop whining.
	 */
	private static final long serialVersionUID = 1L;
	protected JTextField textField;
	protected JSpinner spinner;
	protected JdomkmlInterface theKMLInterface;
	private final String DEFAULT_FILE_NAME = "";
	private final int DEFAULT_SPINNER_VALUE = 85;
	private final JdomkmlInterface DEFAULT_KML_INTERFACE = null;
	protected final File DEFAULT_OPEN_DIRECTORY = new File("res\\");
	
	
	public ElevationFrame(ImageIcon windowIcon) {
		setIconImage(windowIcon.getImage());
		this.setTitle("Elevation Utility");
		this.setBounds(500, 250, 180, 225);
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
		btnNewButton.addMouseListener(new BrowseButtonHandler(this));
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
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(85, 1, 85, 5));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 1;
		panel.add(spinner, gbc_spinner);
		
		JButton btnGo = new JButton("Save & Go!");
		btnGo.addMouseListener(new GoButtonClickHandler(this));
		
		
		GridBagConstraints gbc_btnGo = new GridBagConstraints();
		gbc_btnGo.insets = new Insets(0, 0, 5, 5);
		gbc_btnGo.gridx = 0;
		gbc_btnGo.gridy = 2;
		panel.add(btnGo, gbc_btnGo);
		
		JTextPane txtpnGooglesApi = new JTextPane();
		txtpnGooglesApi.setText("* Google's API allows only 10k calls per day. More coordinates per call = less calls, but also less accuracy. Max 2k characters per URL, so limited to 85 points per call");
		GridBagConstraints gbc_txtpnGooglesApi = new GridBagConstraints();
		gbc_txtpnGooglesApi.anchor = GridBagConstraints.NORTH;
		gbc_txtpnGooglesApi.gridwidth = 2;
		gbc_txtpnGooglesApi.insets = new Insets(0, 0, 0, 5);
		gbc_txtpnGooglesApi.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtpnGooglesApi.gridx = 0;
		gbc_txtpnGooglesApi.gridy = 3;
		panel.add(txtpnGooglesApi, gbc_txtpnGooglesApi);
		setAllToDefault();
	}
	
	protected void handleError(String message){
		displayMessage(message);
		setAllToDefault();
	}
	
	private void displayMessage(String errorMessage){
		JOptionPane.showMessageDialog(this, errorMessage);
	}
	private void setAllToDefault(){
		textField.setText(DEFAULT_FILE_NAME);;
		spinner.setValue(DEFAULT_SPINNER_VALUE);
		theKMLInterface = DEFAULT_KML_INTERFACE;
	}

	public void completeAndClose() {
		displayMessage("Complete!");
		this.dispose();
		this.setVisible(false);	
	}
}

class GoButtonClickHandler extends MouseAdapter{
	private final ElevationFrame parent;
	public GoButtonClickHandler(ElevationFrame parent){
		this.parent = parent;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		JFileChooser fc = new JFileChooser();
		File toSaveTo;
		if(parent.DEFAULT_OPEN_DIRECTORY.exists()){
			fc.setCurrentDirectory(parent.DEFAULT_OPEN_DIRECTORY);
		}
		 int returnVal = fc.showSaveDialog(parent);
		 if (returnVal == JFileChooser.APPROVE_OPTION) {  
			 toSaveTo = fc.getSelectedFile();
			 if(toSaveTo.exists()){
				 parent.handleError("File already exists, cannot overwrite");
				 return;
			 }
			 
	        }
		 else {
	           //cancelled by user, do nothing
	       	return;
	      }
		
		
			File testFile = new File(parent.textField.getText());
			if(!testFile.exists()){
				parent.handleError("Input file invalid");
				return;
			}
		
			try {
				parent.theKMLInterface = new JdomkmlInterface(parent.textField.getText());
			} catch (IOException e) {
				parent.handleError("IO Exception, check filename");
				e.printStackTrace();
				return;
			} catch (JDOMException e) {
				parent.handleError("parsing error, bad KML file");
				e.printStackTrace();
				return;
			} catch (Exception e){
				parent.handleError("" + e.getClass() + " " + e.getMessage());
				e.printStackTrace();
				return;
			}
			
			try{
				parent.theKMLInterface.getElevationsFromGoogle((int)parent.spinner.getValue());
			}
			catch(ClassCastException e){
				parent.handleError("Value from spinner not an integer");
				return;
			} catch (JDOMException e) {
				parent.handleError("Parsing exception");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				parent.handleError("IO Exception, check internet");
				e.printStackTrace();
				return;
			} catch(IllegalArgumentException e){
				parent.handleError("too many URLs in one try");
				e.printStackTrace();
				return;
			}
			
			try{
				parent.theKMLInterface.saveToFile(toSaveTo);
			}
			catch(IOException e){
				parent.handleError("failed to save, check filename/permissions");
				e.printStackTrace();
				return;
			}
			
			parent.completeAndClose();
			
	}
	
	
}


class BrowseButtonHandler extends MouseAdapter{
	private final ElevationFrame parent;
	public BrowseButtonHandler(ElevationFrame parent){
		this.parent = parent;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		JFileChooser fc = new JFileChooser();
		if(parent.DEFAULT_OPEN_DIRECTORY.exists()){
			fc.setCurrentDirectory(parent.DEFAULT_OPEN_DIRECTORY);
		}
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Google Map files", "KML", "kml"));
		fc.setAcceptAllFileFilterUsed(false); //makes the 'kml' one default. 
		fc.setAcceptAllFileFilterUsed(true);
		 int returnVal = fc.showOpenDialog(parent);
		 if (returnVal == JFileChooser.APPROVE_OPTION) {
	            parent.textField.setText(fc.getSelectedFile().getPath());
	            
	        } else {
	            //cancelled by user, do nothing
	        	return;
	        }
		
	}
	
	
}