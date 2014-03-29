/**
 * this Frame is the advanced window for the map. 
 * Will show advanced settings as well as advanced information that doesn't belong on the main window. 
 * Allows a user to load a new map
 */
package com.ubcsolar.ui;

import com.ubcsolar.common.Listener;
import com.ubcsolar.map.*;
import com.ubcsolar.notification.Notification;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JEditorPane;
import javax.swing.JButton;


public class Map extends JFrame implements Listener {

	private JPanel contentPane;
	private JTable table;
	private GlobalController mySession;
	private JLabel lblBlank = new JLabel("blank");

/**
	 * Launch the application.
	 *//*//commented out, don't need a MAIN in Map. 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Map frame = new Map();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public void labelUpdate(String labelupdate) {
		lblBlank.setText(labelupdate);
	}
	
	
	
	/**
	 * All notifications that this class has registered for will come here
	 */
	@Override
	public void notify(Notification n){
		//TODO add any notifications here
		if(n.getClass() == NewMapLoadedNotification.class){ //when a new map is loaded, propogate the new name. 
			labelUpdate(((NewMapLoadedNotification) n).getMapLoadedName()); 
			JOptionPane.showMessageDialog(this, "New map: " + (((NewMapLoadedNotification) n).getMapLoadedName()));
		}
	}
	
	/**
	 * register for any notifications that this class needs to
	 */
	@Override
	public void register(){
		mySession.register(this, NewMapLoadedNotification.class); //need this for the map label and tool bar.
		//TODO add any notifications you need to listen for here. 
	}
	
	/**
	 * constructor
	 * @param toAdd - the session to refer to for the controllers, and to register with
	 */
	public Map(GlobalController toAdd) {
		mySession = toAdd;
		register();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 538, 395);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("View Map");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmCoordinates = new JMenuItem("Coordinates");
		mnNewMenu.add(mntmCoordinates);
		
		JMenu mnLoadMap = new JMenu("Load Map");
		menuBar.add(mnLoadMap);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("ASC2014 Route Map");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					mySession.getMapController().load("res/EDCToHope.kml");
				} catch (IOException e) {
					JDialog dialog = new ErrorMessage();
					dialog.setVisible(true);
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				} 
				
				
				//TODO hardcoded, will need to update
			}
		});
		mnLoadMap.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Other Map");
		mnLoadMap.add(mntmNewMenuItem_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JPanel panel = new JPanel();
		
		JButton btnNewButton = new JButton("Refresh Map Name");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String dataname = mySession.getMapController().getLoadedMapName();
				labelUpdate(dataname); 
			}
		});
		
	
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 321, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addComponent(lblBlank)
							.addContainerGap())
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(btnNewButton)
								.addContainerGap())
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addGap(2)
							.addComponent(lblBlank)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton))
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		
		
		table = new JTable();
		scrollPane.setViewportView(table);
		contentPane.setLayout(gl_contentPane);
	}
}
