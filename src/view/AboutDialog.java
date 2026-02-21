/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 *
 * @author jesse
 */
public class AboutDialog extends JDialog{
    public AboutDialog(Window parent, String title, String message){
        super(parent, title, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JLabel headerLabel= new JLabel(title, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        JTextArea textArea= new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(getBackground());
        textArea.setFocusable(false);
        
        JScrollPane scrollPane= new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton Ok= new JButton("OK");
        Ok.setPreferredSize(new Dimension(80, 30));
        Ok.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(Ok);
        
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        JPanel buttonPanel= new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(Ok);
        add(headerLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(450, 350);
        setLocationRelativeTo(parent);
    }
    
}
