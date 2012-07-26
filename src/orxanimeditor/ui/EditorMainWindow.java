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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;
import orxanimeditor.io.AnimIO;
import orxanimeditor.io.ImageManager;

@SuppressWarnings("serial")
public class EditorMainWindow extends JFrame {
	AnimationManager 	animationManager;
	FrameEditor 	 	frameEditor;
	AnimationViewer  	animationViewer;
	AnimationSetEditor 	animationSetEditor;

	JMenuBar 		 	menuBar;

	JMenu			 	fileMenu;
	JMenuItem		 	openAnimationDataItem;
	JMenuItem			saveAnimationDataItem;
	JMenuItem		 	setTargetItem;
	JMenuItem		 	writeToTargetItem;
	JMenuItem		 	appendToTargetItem;	
	JMenuItem		 	openImageItem;

	JMenu			 	editMenu;
	JMenuItem		 	increaseKeyValueItem;
	JMenuItem 		 	decreaseKeyValueItem;
	JMenuItem		 	flipXItem;
	JMenuItem		 	flipYItem;
	
	JFileChooser	 	imageChooser = new JFileChooser();
	JFileChooser	 	editorDataChooser = new JFileChooser();
	JFileChooser	 	iniChooser = new JFileChooser();
	
	EditorData 		 	data = new EditorData();
	
	ImageManager	 	imageManager = new ImageManager();
	
	LinkedList<EditListener> editListeners = new LinkedList<EditListener>();
	
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
		prepareTree();
		setLayout(new BorderLayout());
		animationManager 	= new AnimationManager(this);
		frameEditor      	= new FrameEditor(this);
		animationViewer  	= new AnimationViewer(this);
		animationSetEditor	= new AnimationSetEditor(this);
		
		animationManager.animationTree.addTreeSelectionListener(animationViewer);
		animationManager.animationTree.addTreeSelectionListener(frameEditor);
		animationManager.animationTree.addTreeSelectionListener(animationManager);
		addEditListneer(animationViewer);
		
		JSplitPane leftLowerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, animationViewer, animationSetEditor);
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, animationManager, leftLowerSplitPane);
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, frameEditor);
		prepareMenuBar();
		getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		pack();
		setMinimumSize(getSize());
		setSize(new Dimension(800, 600));
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
	}
	
	private void prepareTree() {
		
	}
	
	private void prepareMenuBar() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		openAnimationDataItem = new JMenuItem("Open Animation Data");
		fileMenu.add(openAnimationDataItem);
		openAnimationDataItem.addActionListener(openAnimationDataListener);		
		openAnimationDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK));
		
		saveAnimationDataItem = new JMenuItem("Save Animation Data");
		fileMenu.add(saveAnimationDataItem);
		saveAnimationDataItem.addActionListener(saveAnimationDataListener);
		
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
		
	}
	
	ActionListener openImageAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(imageChooser.showOpenDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
				frameEditor.openImage(imageChooser.getSelectedFile());
			}
		}
	};
	
	ActionListener openAnimationDataListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(editorDataChooser.showOpenDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
				AnimIO.readEditorData(editorDataChooser.getSelectedFile(),data);
				animationManager.reload();
				animationSetEditor.dataLoaded();
				repaint();
			}
		}
	};
	
	ActionListener saveAnimationDataListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(editorDataChooser.showSaveDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
				AnimIO.writeEditorData(data,editorDataChooser.getSelectedFile());
			}			
		}
	};
	
	ActionListener setTargetItemActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(iniChooser.showSaveDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
				data.targetIni=iniChooser.getSelectedFile();
				imageChooser.setCurrentDirectory(data.getTargetFolder());
				editorDataChooser.setCurrentDirectory(data.getTargetFolder());
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
				animationManager.applyEditVisitor(new FlipVisitor(true), true, false);
			}
			if(e.getSource()==flipYItem) {
				animationManager.applyEditVisitor(new FlipVisitor(false), true, false);
			}
			if(e.getSource()==increaseKeyValueItem) {
				animationManager.applyEditVisitor(new KeyDurationVisitor(1.2), true, false);
			}
			if(e.getSource()==decreaseKeyValueItem) {
				animationManager.applyEditVisitor(new KeyDurationVisitor(1/1.2), true, false);
			}
			fireEdit();
		}
	};
		
	public void addEditListneer(EditListener l) {editListeners.add(l);}

	public void fireEdit() {
		for(EditListener l: editListeners) l.edited();	
	}
	
	class FlipVisitor implements EditVisitor {
		boolean flipX;
		public FlipVisitor(boolean flipX) {this.flipX = flipX;}
		public void edit(Animation animation) {
			for(int fi=0; fi<animation.getChildCount(); fi++) edit((Frame) animation.getChildAt(fi));
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
