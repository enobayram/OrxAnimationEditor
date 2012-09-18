package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.print.attribute.standard.JobName;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SetProjectDialog extends JDialog implements WindowListener, ActionListener{
	EditorMainWindow editor;
	JButton okButton;
	JButton helpButton;
	JButton cancelButton;
	JRadioButton newProjectButton;
	JRadioButton openProjectButton;
	ButtonGroup group;
	JLabel		message;
	public SetProjectDialog(EditorMainWindow editor) {
		super(editor, "Set Project");
		this.editor=editor;
		setModal(true);
		addWindowListener(this);
		JPanel contentPane = new JPanel();
		message = new JLabel("Choose an action:");
		newProjectButton = new JRadioButton("New Animation Project");
//		newProjectButton.addActionListener(this);
		openProjectButton = new JRadioButton("Open Animation Project");
//		openProjectButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		
		helpButton = new JButton("Help");
		helpButton.addActionListener(this);
		
		buttonPanel.add(okButton);
		buttonPanel.add(helpButton);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(0);
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(message);
		contentPane.add(newProjectButton);
		contentPane.add(openProjectButton);
		contentPane.add(buttonPanel);
		group = new ButtonGroup();
		group.add(newProjectButton);
		group.add(openProjectButton);
		setContentPane(contentPane);
		pack();
		setResizable(false);
	}
	
	@Override	
	public void windowClosing(WindowEvent arg0) {
		if(editor.getData().project.projectFile == null) System.exit(0);
	}

	@Override	public void windowActivated(WindowEvent arg0) {}
	@Override	public void windowClosed(WindowEvent arg0) {}
	@Override	public void windowDeactivated(WindowEvent arg0) {}
	@Override	public void windowDeiconified(WindowEvent arg0) {}
	@Override	public void windowIconified(WindowEvent arg0) {}
	@Override	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton) {
			ButtonModel selection = group.getSelection();
			if(selection==null)
				JOptionPane.showMessageDialog(this, "Please choose an action...", "Error", JOptionPane.ERROR_MESSAGE);
			else {
				if(newProjectButton.isSelected()) {
					editor.newProjectAction();
				} else {
					editor.openProjectAction();
				}
				if(editor.getData().project.projectFile == null)
					JOptionPane.showMessageDialog(this, "Please set a valid project...", "Error", JOptionPane.ERROR_MESSAGE);				
				else
					setVisible(false);
			}
		} else if(e.getSource() == helpButton) {
			editor.helpViewer.setVisible(true);
		}
	}
}
