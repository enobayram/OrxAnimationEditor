package orxanimeditor.ui.mainwindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import orxanimeditor.data.v1.EditorData;
import orxanimeditor.io.AnimIO;

public class FileMenu extends JMenu {

	JMenuItem		 	newAnimationProjectItem;
	JMenuItem		 	openAnimationProjectItem;
	JMenuItem			saveAnimationProjectItem;
	JMenuItem		 	openImageItem;
	JMenuItem			exitItem;

	EditorMainWindow	editor;
	
	public FileMenu(EditorMainWindow editor_) {
		super("File");
		editor = editor_;
		
		newAnimationProjectItem = new JMenuItem("New Animation Project");
		add(newAnimationProjectItem);
		newAnimationProjectItem.addActionListener(newAnimationProjectListener);
		newAnimationProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK));
		
		openAnimationProjectItem = new JMenuItem("Open Animation Project");
		add(openAnimationProjectItem);
		openAnimationProjectItem.addActionListener(openAnimationProjectListener);		
		openAnimationProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK));
		
		saveAnimationProjectItem = new JMenuItem("Save Animation Project");
		add(saveAnimationProjectItem);
		saveAnimationProjectItem.addActionListener(saveAnimationProjectListener);
		
		add(new JSeparator());

		openImageItem = new JMenuItem("Open Image");
		add(openImageItem);
		openImageItem.addActionListener(openImageAction);
		openImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));

		add(new JSeparator());

		exitItem = new JMenuItem("Exit");
		add(exitItem);
		exitItem.addActionListener(exitAction);

	}
	
	ActionListener openImageAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(editor.imageChooser.showOpenDialog(editor)==JFileChooser.APPROVE_OPTION) {
				editor.frameEditor.openImage(editor.imageChooser.getSelectedFile());
			}
		}
	};
	
	ActionListener newAnimationProjectListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(editor.getData().isDataChangedSinceLastSave()) {
				int choice = editor.showSaveChangedProjectDialog();
				if(choice == JOptionPane.CANCEL_OPTION)
					return;
			}
			editor.newProjectAction();
		}
	};
	

	ActionListener openAnimationProjectListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(editor.getData().isDataChangedSinceLastSave()) {
				int choice = editor.showSaveChangedProjectDialog();
				if(choice == JOptionPane.CANCEL_OPTION)
					return;
			}
			editor.openProjectAction();
		}
	};

	ActionListener saveAnimationProjectListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			editor.saveProject();
		}
	};
	
	private ActionListener exitAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			editor.exit();
		}
	};

}
