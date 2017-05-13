package com.ubcsolar.ui;

import com.ubcsolar.Main.GlobalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SimChangeSpeedsWindow extends JFrame {

    private GlobalController mySession;
    private SimulationAdvancedWindow myParent;
    private JPanel contentPane; //the root content holder

    public SimChangeSpeedsWindow(GlobalController mySession, SimulationAdvancedWindow parent) {
        this.myParent = parent;// TODO for loading Frame
        this.mySession = mySession;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 974, 780);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{30, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);
        
        JLabel lblTitle = new JLabel("Title");
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 0;
        gbc_lblTitle.gridy = 0;
        contentPane.add(lblTitle, gbc_lblTitle);
    }
}
