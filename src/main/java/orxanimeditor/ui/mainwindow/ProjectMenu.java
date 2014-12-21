package orxanimeditor.ui.mainwindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import orxanimeditor.io.AnimIO;


public class ProjectMenu extends JMenu {
	EditorMainWindow editor;
	JMenuItem		 	setTargetItem;
	JMenuItem		 	setTargetFolderItem;
	JMenuItem		 	writeToTargetItem;
	JMenuItem		 	appendToTargetItem;	

	public ProjectMenu(EditorMainWindow editorMainWindow) {
		super("Project");
		editor = editorMainWindow;
		
		setTargetItem = new JMenuItem("Set Target ini File");
		add(setTargetItem);
		setTargetItem.addActionListener(setTargetItemActionListener);

		setTargetFolderItem = new JMenuItem("Set Root Folder");
		add(setTargetFolderItem);
		setTargetFolderItem.addActionListener(setTargetFolderItemActionListener);
		
		add(new JSeparator());
		
		writeToTargetItem = new JMenuItem("Write to Target");
		add(writeToTargetItem);
		writeToTargetItem.addActionListener(exportToTargetActionListener);

		appendToTargetItem = new JMenuItem("Append to Target");
		add(appendToTargetItem);
		appendToTargetItem.addActionListener(exportToTargetActionListener);

	}
	
	private ActionListener setTargetFolderItemActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(editor.targetFolderChooser.showOpenDialog(editor)==JFileChooser.APPROVE_OPTION) {
				editor.getData().getProject().targetFolder = editor.getData().getProject().getRelativeFile(editor.targetFolderChooser.getSelectedFile());
			}			
		}
	};


	ActionListener exportToTargetActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			boolean append;
			if(e.getSource()==writeToTargetItem) append = false;
			else append = true;
			AnimIO.exportEditorData(editor,editor.getData(), append);
		}
	};

	ActionListener setTargetItemActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(editor.iniChooser.showSaveDialog(editor)==JFileChooser.APPROVE_OPTION) {
				editor.getData().getProject().targetIni=editor.getData().getProject().getRelativeFile(editor.iniChooser.getSelectedFile());
				editor.imageChooser.setCurrentDirectory(editor.getData().getProject().getTargetFolder());
				editor.editorDataChooser.setCurrentDirectory(editor.getData().getProject().getTargetFolder());
			}						
		}
	};


}
