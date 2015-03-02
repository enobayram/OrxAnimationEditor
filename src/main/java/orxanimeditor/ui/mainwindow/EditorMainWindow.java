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


package orxanimeditor.ui.mainwindow;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.EditorData;
import orxanimeditor.data.v1.Frame;
import orxanimeditor.data.v1.HierarchicalData;
import orxanimeditor.io.AnimIO;
import orxanimeditor.io.ImageManager;
import orxanimeditor.ui.EditVisitor;
import orxanimeditor.ui.HelpViewer;
import orxanimeditor.ui.SelectionListener;
import orxanimeditor.ui.animationmanager.AnimationManager;
import orxanimeditor.ui.animationmanager.AnimationTreeTransferHandler;
import orxanimeditor.ui.animationseteditor.AnimationSetEditor;
import orxanimeditor.ui.animationviewer.AnimationViewer;
import orxanimeditor.ui.animationviewer.SelectionFrameSequence;
import orxanimeditor.ui.frameeditor.FrameEditor;

@SuppressWarnings("serial")
public class EditorMainWindow extends JFrame {
	public AnimationManager 	animationManager;
	public FrameEditor 	 	frameEditor;
	AnimationViewer  	animationViewer;
	AnimationSetEditor 	animationSetEditor;

	JMenuBar 		 	menuBar;

	FileMenu			fileMenu;

	JMenu			 	editMenu;
	JMenuItem		 	increaseKeyValueItem;
	JMenuItem 		 	decreaseKeyValueItem;
	JMenuItem		 	flipXItem;
	JMenuItem		 	flipYItem;

	ProjectMenu			projectMenu;
	
	JMenu			 	helpMenu;
	JMenuItem		 	helpMenuItem;

	
	JFileChooser	 	imageChooser = new JFileChooser();
	JFileChooser	 	editorDataChooser = new JFileChooser();
	JFileChooser	 	iniChooser = new JFileChooser();
	JFileChooser		targetFolderChooser = new JFileChooser();
	
	SelectionFrameSequence selectionSequence;
	
	InfoBar				infoBar;
	
	private EditorData 	data;	
	public static final ImageManager	 	imageManager = new ImageManager();
		
	HelpViewer			helpViewer;
	
	public static void main(String[] args) {
		final String projectFile = args.length > 0 ? args[0] : null;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new EditorMainWindow(projectFile);
			}
		});
	}
	
	public HierarchicalData[] getSelectedObjects() {
		return animationManager.getSelectedObjects();
	}
	
	private SingleCDockable create( String id, String title, String iconName, JPanel panel){
		DefaultSingleCDockable dockable = new DefaultSingleCDockable( id, title );
		dockable.setTitleText( title );
		dockable.setCloseable( false );
		ImageIcon icon = getImageIcon("icons/"+iconName);
		dockable.setTitleIcon(icon);
		dockable.add( panel );
		
		return dockable;
	}
	
	public EditorMainWindow(String projectFile) {
		super("Orx Animation Editor");
		setData(new EditorData());
		prepareTree();
		setLayout(new BorderLayout());
		
		infoBar		    = new InfoBar();
		
		animationManager 	= new AnimationManager(this);
		frameEditor      	= new FrameEditor(this);
		animationSetEditor	= new AnimationSetEditor(this);
		helpViewer 			= new HelpViewer(this);
		
		selectionSequence   = new SelectionFrameSequence(this);
		addSelectionListener(selectionSequence);
		animationViewer  	= new AnimationViewer(this, selectionSequence);

		CControl control = new CControl(this);
		
		CGrid grid = new CGrid(control);
		grid.add(0, 0, 1, 1, create("animationManager", "Animation Manager", "Tree.png", animationManager));
		grid.add(0, 1, 1, 1, create("animationSetEditor", "Animation Set Editor", "AnimSetSmall.png", animationSetEditor));
		grid.add(1, 0, 2, 2, create("frameEditor", "Frame Editor", "FrameSmall.png", frameEditor));
		grid.add(1, 1, 1, 1, create("animationViewer", "Animation Viewer", "OffsetIconSmall.png", animationViewer));
		control.getContentArea().deploy(grid);
		prepareMenuBar();
		getContentPane().add(control.getContentArea(), BorderLayout.CENTER);
		getContentPane().add(menuBar, BorderLayout.NORTH);
		getContentPane().add(infoBar, BorderLayout.SOUTH);
		pack();
		setMinimumSize(getSize());
		setSize(new Dimension(1000, 700));
		setVisible(true);
		
		editorDataChooser.setFileFilter(new FileNameExtensionFilter("Orx Animation Project", "oap"));
		targetFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowAdapter);
		
		if(projectFile == null) {
			SetProjectDialog setProjectDialog = new SetProjectDialog(this);
			setProjectDialog.setLocation(500, 350);
			setProjectDialog.setVisible(true);			
		} else {
			loadProject(new File(projectFile));
		}

	}
	
	public void addSelectionListener(SelectionListener l) {
		animationManager.addSelectionListener(l);		
	}
	
	public InfoBar getInfoBar() {
		return infoBar;
	}

	public AreaInfoProxy getInfoProxy() {
		return new AreaInfoProxy(infoBar);
	}
	
	WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			exit();
		}
	};
	
	protected int showSaveChangedProjectDialog() {
		int choice = JOptionPane.showConfirmDialog(EditorMainWindow.this
				,"Animation project changed, save the changes before exiting?\n"
				 , "Save Project"
				 , JOptionPane.YES_NO_CANCEL_OPTION
				 , JOptionPane.QUESTION_MESSAGE);
		if(choice == JOptionPane.YES_OPTION)
			saveProject();
		return choice;
	}
	
	void exit() {
		if(data.isDataChangedSinceLastSave()) {
			int choice = showSaveChangedProjectDialog();
			if(choice == JOptionPane.CANCEL_OPTION){
				return;
			}
		}
		System.exit(0);
	}
		
	private void prepareTree() {
		
	}
	
	private void prepareMenuBar() {
		menuBar = new JMenuBar();
		fileMenu = new FileMenu(this);
		menuBar.add(fileMenu);
				
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
		
		projectMenu = new ProjectMenu(this);
		menuBar.add(projectMenu);
		
		helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		helpMenuItem = new JMenuItem("Help Contents");
		helpMenu.add(helpMenuItem);
		helpMenuItem.addActionListener(helpContentsActionListener);
		
	}
	
	void projectChanged() {
		imageChooser.setCurrentDirectory(getData().getProject().projectFile.getParentFile());
		iniChooser.setCurrentDirectory(getData().getProject().projectFile.getParentFile());
		targetFolderChooser.setCurrentDirectory(getData().getProject().projectFile.getParentFile());
		repaint();		
	}
		
	public void openProjectAction() {
		if(editorDataChooser.showOpenDialog(EditorMainWindow.this)==JFileChooser.APPROVE_OPTION) {
			loadProject(editorDataChooser.getSelectedFile());
		}
	}
	
	public void loadProject(File projectFile) {
		AnimIO.readEditorData(projectFile,getData());
		projectChanged();		
	}
		
	public void saveProject() {
		AnimIO.writeEditorData(getData(),getData().getProject().projectFile);		
	}
			
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

	public ImageIcon getImageIcon(String path) {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(path);
			return new ImageIcon(ImageIO.read(in));
		} catch (Exception e) {
			// We're not running from a jar file
			return new ImageIcon(path);
		}
	}

	public EditorData getData() {
		return data;
	}

	private void setData(EditorData data) {
		this.data = data;
	}
	
	public void newProjectAction() {
		if(editorDataChooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
			File projectFile=editorDataChooser.getSelectedFile();
			if(!projectFile.exists() && !Pattern.matches(".*\\..*",projectFile.getName())) {
				projectFile = new File(projectFile.getPath()+".oap");
			}
			getData().acquireFromData(new EditorData(),projectFile);
			projectChanged();
		}
	}
}
