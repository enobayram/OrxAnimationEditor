/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * AUTHOR: Enis Bayramoglu
 * E-MAIL: enisbayramoglu@gmail.com
 * Thanks to KarloBob for his contributions
 * */


package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;
import orxanimeditor.io.AnimIO;
import orxanimeditor.io.ImageManager;
import orxanimeditor.ui.animationtree.AnimationTreeTransferHandler;

@SuppressWarnings("serial")
public class EditorMainWindow extends JFrame {
	AnimationManager 	animationManager;
	FrameEditor 	 	frameEditor;
	AnimationViewerDisplay  	animationViewer;
	AnimationSetEditor 	animationSetEditor;

	JMenuBar 		 	menuBar;

	JMenu			 	fileMenu;
	JMenuItem		 	newAnimationProjectItem;
	JMenuItem		 	openAnimationProjectItem;
	JMenuItem			saveAnimationProjectItem;
	JMenuItem		 	setTargetItem;
	JMenuItem		 	writeToTargetItem;
	JMenuItem		 	appendToTargetItem;	
	JMenuItem		 	openImageItem;
	JMenuItem			exitItem;

	JMenu			 	editMenu;
	JMenuItem		 	increaseKeyValueItem;
	JMenuItem 		 	decreaseKeyValueItem;
	JMenuItem		 	flipXItem;
	JMenuItem		 	flipYItem;
	
	JMenu			 	helpMenu;
	JMenuItem		 	helpMenuItem;

	
	JFileChooser	 	imageChooser = new JFileChooser();
	JFileChooser	 	editorDataChooser = new JFileChooser();
	JFileChooser	 	iniChooser = new JFileChooser();
	
	EditorData 		 	data;	
	ImageManager	 	imageManager = new ImageManager();
		
	HelpViewer			helpViewer;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new EditorMainWindow();
			}
		});
	}
	
	public EditorMainWindow() {
		super("Orx Animation Editor");
		System.out.println(DataFlavor.javaJVMLocalObjectMimeType+"; class=" + Animation[].class.getName());
		System.out.println(AnimationTreeTransferHandler.AnimationFlavor.getMimeType());
		data = new EditorData();
		prepareTree();
		setLayout(new BorderLayout());
		animationManager 	= new AnimationManager(this);
		frameEditor      	= new FrameEditor(this);
		animationViewer  	= new AnimationViewerDisplay(this);
		animationSetEditor	= new AnimationSetEditor(this);
		helpViewer 			= new HelpViewer(this);
		
		animationManager.animationTree.addTreeSelectionListener(frameEditor);
		animationManager.animationTree.addTreeSelectionListener(animationViewer);

		JSplitPane leftLowerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, animationViewer, animationSetEditor);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, animationManager, leftLowerSplitPane);
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, frameEditor);
		prepareMenuBar();
		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		pack();
		setMinimumSize(getSize());
		setSize(new Dimension(1000, 700));
		setVisible(true);
		
		editorDataChooser.setFileFilter(new FileNameExtensionFilter("Orx Animation Project", "oap"));
		
		SetProjectDialog setProjectDialog = new SetProjectDialog(this);
		setProjectDialog.setLocation(500, 350);
		setProjectDialog.setVisible(true);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowAdapter);
		
	}
	
	WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			exit();
		}
	};
	
	private void exit() {
		int choice = JOptionPane.showConfirmDialog(EditorMainWindow.this
				,"Save the animation project before exiting?\n" +
				 "(Note that currently this message pops up regardless\n " +
				 "of any change to the animation project...)"
				 , "Save Project"
				 , JOptionPane.YES_NO_CANCEL_OPTION
				 , JOptionPane.QUESTION_MESSAGE);
		switch(choice) {
		case JOptionPane.YES_OPTION:
			saveProject();
			System.exit(0);
			break;
		case JOptionPane.NO_OPTION:
			System.exit(0);
			break;
		case JOptionPane.CANCEL_OPTION:
			break;
		}
	}
	
	private ActionListener exitAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			exit();
		}
	};
	
	private void prepareTree() {
		
	}
	
	private void prepareMenuBar() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		newAnimationProjectItem = new JMenuItem("New Animation Project");
		fileMenu.add(newAnimationProjectItem);
		newAnimationProjectItem.addActionListener(newAnimationProjectListener);
		newAnimationProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK));
		
		openAnimationProjectItem = new JMenuItem("Open Animation Project");
		fileMenu.add(openAnimationProjectItem);
		openAnimationProjectItem.addActionListener(openAnimationProjectListener);		
		openAnimationProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK));
		
		saveAnimationProjectItem = new JMenuItem("Save Animation Project");
		fileMenu.add(saveAnimationProjectItem);
		saveAnimationProjectItem.addActionListener(saveAnimationProjectListener);
		
		fileMenu.add(new JSeparator());
		
		setTargetItem = new JMenuItem("Set Target ini File");
		fileMenu.add(setTargetItem);
		setTargetItem.addActionListener(setTargetItemActionListener);
		
		writeToTargetItem = new JMenuItem("Write to Target");
		fileMenu.add(writeToTargetItem);
		writeToTargetItem.addActionListener(exportToTargetActionListener);

		appendToTargetItem = new JMenuItem("Append to Target");
		fileMenu.add(appendToTargetItem);
		appendToTargetItem.addActionListener(exportToTargetActionListener);
		
		fileMenu.add(new JSeparator());

		openImageItem = new JMenuItem("Open Image");
		fileMenu.add(openImageItem);
		openImageItem.addActionListener(openImageAction);
		openImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));

		fileMenu.add(new JSeparator());

		exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.addActionListener(exitAction);
		
		editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		increaseKeyValueItem = new JMenuItem("Increase Key Duration");
		editMenu.add(increaseKeyValueItem);
		increaseKeyValueItem.addActionListener(editActionListener);
		increaseKeyValueItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK));

		decreaseKeyValueItem = new JMenuItem("Decrease Key Duration");
		editMenu.add(decreaseKeyValueItem);
		decreaseKeyValueItem.addActionListener(editActionListener);
		decreaseKeyValueItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK));

		editMenu.add(new JSeparator());
		
		flipXItem = new JMenuItem("Flip Frame x");
		editMenu.add(flipXItem);
		flipXItem.addActionListener(editActionListener);
		flipXItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));
		
		flipYItem = new JMenuItem("Flip Frame y");
		editMenu.add(flipYItem);
		flipYItem.addActionListener(editActionListener);
		flipYItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_DOWN_MASK));
		
		helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		helpMenuItem = new JMenuItem("Help Contents");
		helpMenu.add(helpMenuItem);
		helpMenuItem.addActionListener(helpContentsActionListener);
		
	}
	
	void poke() {
		doLayout();
		repaint();		
	}
	
	ActionListener openImageAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(imageChooser.showOpenDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
				frameEditor.openImage(imageChooser.getSelectedFile());
			}
		}
	};
	
	ActionListener newAnimationProjectListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			newProjectAction();
		}
	};
	
	public void newProjectAction() {
		if(editorDataChooser.showSaveDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
			File projectFile=editorDataChooser.getSelectedFile();
			if(!projectFile.exists() && !Pattern.matches(".*\\..*",projectFile.getName())) {
				projectFile = new File(projectFile.getPath()+".oap");
			}
			data.acquireFromData(new EditorData(),projectFile);
			projectChanged();
		}
	}
	
	private void projectChanged() {
		imageChooser.setCurrentDirectory(data.project.projectFile.getParentFile());
		iniChooser.setCurrentDirectory(data.project.projectFile.getParentFile());
		repaint();		
	}
	
	ActionListener openAnimationProjectListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			openProjectAction();
		}
	};
	
	public void openProjectAction() {
		if(editorDataChooser.showOpenDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
			File projectFile = editorDataChooser.getSelectedFile();
			AnimIO.readEditorData(projectFile,data);
			projectChanged();
		}
	}
	
	
	ActionListener saveAnimationProjectListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			saveProject();
		}
	};
	
	public void saveProject() {
		AnimIO.writeEditorData(data,data.project.projectFile);		
	}
	
	ActionListener setTargetItemActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(iniChooser.showSaveDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
				data.project.targetIni=data.project.getRelativeFile(iniChooser.getSelectedFile());
				imageChooser.setCurrentDirectory(data.project.getTargetFolder());
				editorDataChooser.setCurrentDirectory(data.project.getTargetFolder());
			}						
		}
	};
	
	ActionListener exportToTargetActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			boolean append;
			if(e.getSource()==writeToTargetItem) append = false;
			else append = true;
			AnimIO.exportEditorData(EditorMainWindow.this,data, append);
		}
	};
	
	ActionListener editActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==flipXItem) {
				animationManager.applyEditVisitor(new FlipVisitor(true));
			}
			if(e.getSource()==flipYItem) {
				animationManager.applyEditVisitor(new FlipVisitor(false));
			}
			if(e.getSource()==increaseKeyValueItem) {
				animationManager.applyEditVisitor(new KeyDurationVisitor(1.2));
			}
			if(e.getSource()==decreaseKeyValueItem) {
				animationManager.applyEditVisitor(new KeyDurationVisitor(1/1.2));
			}
		}
	};
	
	ActionListener helpContentsActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			helpViewer.setModal(false);
			helpViewer.setVisible(true);
		}
	};

			
	class FlipVisitor implements EditVisitor {
		boolean flipX;
		public FlipVisitor(boolean flipX) {this.flipX = flipX;}
		public void edit(Animation animation) {
			for(Frame frame: animation.getFrames()) edit(frame);
		}
		public void edit(Frame frame) {
			if(flipX) frame.setFlipX(!frame.getFlipX());
			else      frame.setFlipY(!frame.getFlipY());
		}		
	}
	
	class KeyDurationVisitor implements EditVisitor {
		double multiplier;
		public KeyDurationVisitor(double multiplier) {this.multiplier=multiplier;}
		public void edit(Animation animation) {
			animation.setDefaultKeyDuration(animation.getDefaultKeyDuration()
					* multiplier);
		}
		public void edit(Frame frame) {
			if(frame.getKeyDuration() < 0) {
				Animation parent = (Animation) frame.getParent();
				frame.setKeyDuration(parent.getDefaultKeyDuration()*multiplier);
			}
			frame.setKeyDuration(frame.getKeyDuration() * multiplier);
		}	
	}

	ImageIcon getImageIcon(String path) {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(path);
			return new ImageIcon(ImageIO.read(in));
		} catch (Exception e) {
			// We're not running from a jar file
			return new ImageIcon(path);
		}
	}	
}
