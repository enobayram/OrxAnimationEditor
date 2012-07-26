package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlType.DEFAULT;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;

public class AnimationManager extends JPanel implements ActionListener, KeyListener, TreeSelectionListener, TreeModelListener {
	EditorMainWindow editor;
	JToolBar  toolbar;
	JTree	  animationTree;
		
	//ImageIcon image = (new ImageIcon(getClass().getResource("yourpackage/mypackage/image.gif")));
	
	ImageIcon newFrameIcon;
	ImageIcon newAnimationIcon;
	ImageIcon frameIcon;
	ImageIcon animationIcon;		
	ImageIcon animationCollapsedIcon;		
	 
	JButton newFrameButton;
	JButton newAnimationButton;
	
	int newFrameSuffix = 0;
	int newAnimationSuffix = 0;

	Animation selectedAnimation = null;
	Frame selectedFrame = null;

	DefaultMutableTreeNode[] clipboard = new DefaultMutableTreeNode[0];
	
	public AnimationManager(EditorMainWindow editorFrame) {
		editor = editorFrame;
		loadIcons();
		prepareToolbar();
		prepareTree();
		
		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		JScrollPane pane = new JScrollPane(animationTree);
		add(pane, BorderLayout.CENTER);
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(getMinimumSize());
	}
	

	private void loadIcons() {
		newFrameIcon 			= editor.getImageIcon("icons/NewFrame.png");
		newAnimationIcon 		= editor.getImageIcon("icons/NewAnimation.png");
		frameIcon 				= editor.getImageIcon("icons/Frame.png");
		animationIcon 			= editor.getImageIcon("icons/Animation.png");		
		animationCollapsedIcon 	= editor.getImageIcon("icons/AnimationCollapsed.png");	
	}
	
	private void prepareToolbar() {
		toolbar = new JToolBar("Animation Manager Tools");
		
		toolbar.add(newFrameButton = new JButton(newFrameIcon));
		toolbar.add(newAnimationButton = new JButton(newAnimationIcon));
		
		newFrameButton.setToolTipText("Create New Frame");
		newAnimationButton.setToolTipText("Create New Animation");
		
		newFrameButton.addActionListener(this);
		newAnimationButton.addActionListener(this);
	}
	
	private void prepareTree() {
		animationTree = new JTree(editor.data.animationTree);
		animationTree.setCellRenderer(new AnimationTreeRenderer(this));
		animationTree.setCellEditor(new DefaultCellEditor(new JTextField()));
		animationTree.setEditable(true);
		animationTree.addKeyListener(this);
		animationTree.getModel().addTreeModelListener(this);
//		animationTree.addTreeSelectionListener(this); // this has to happen outside, otherwise the listener will be called last
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==newFrameButton) {
			Animation selectedAnimation = getSelectedAnimation();
			if(selectedAnimation == null) return;
			selectedAnimation.add(new Frame("NewFrame" + newFrameSuffix++));
			((DefaultTreeModel) animationTree.getModel()).reload(selectedAnimation);
		}
		if(e.getSource()==newAnimationButton) {
			editor.data.animationTree.add(new Animation("NewAnimation" + newAnimationSuffix++));
			((DefaultTreeModel) animationTree.getModel()).reload(editor.data.animationTree);
		}

		repaint();
	}
		
	public Frame getSelectedFrame() {
		return selectedFrame;
	}
	
	public Animation getSelectedAnimation() {
		return selectedAnimation;
	}


	public void reload() {
		((DefaultTreeModel) animationTree.getModel()).reload();		
	}
	
	public void reload(DefaultMutableTreeNode node) {
		((DefaultTreeModel)animationTree.getModel()).reload(node);
	}

	DefaultMutableTreeNode getRootNode() {
		return (DefaultMutableTreeNode) animationTree.getModel().getRoot();		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {

		if(e.isControlDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_C:
				clipboard = getSelectedNodes();
				break;
			case KeyEvent.VK_V:
				for(DefaultMutableTreeNode node: clipboard) {
					if(node instanceof Frame && selectedAnimation!=null) {
						selectedAnimation.add((Frame)node.clone());
						reload(selectedAnimation);
					}
					if(node instanceof Animation) {
						getRootNode().add((Animation)node.clone());
						reload(getRootNode());
					}
				}
				break;				
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				Frame selectedFrame = getSelectedFrame();
				if(selectedFrame!=null) editor.frameEditor.openImage(selectedFrame.getImageFile());
				break;
			case KeyEvent.VK_DELETE:
				applyEditVisitor(new DeleteVisitor(), true,true);
				break;
			}
		}
		
	}
	
	public DefaultMutableTreeNode[] getSelectedNodes() {
		DefaultMutableTreeNode[] nodes= new DefaultMutableTreeNode[animationTree.getSelectionCount()];
		if(nodes.length>0) {
			int i=0;
			for(TreePath p: animationTree.getSelectionPaths()) nodes[i++] = (DefaultMutableTreeNode) p.getLastPathComponent();
		}
		return nodes;
	}
	
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	
	public void applyEditVisitor(EditVisitor visitor, boolean selectionOnly, boolean structureChanges) {
		if(selectionOnly) {
			TreePath treePaths[] = animationTree.getSelectionPaths();
			if(treePaths == null) return;
			for(TreePath p: treePaths) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
				if(node instanceof Frame) {
					visitor.edit((Frame) node);
				}
				else if(node instanceof Animation) {
					visitor.edit((Animation) node);
				}
				if(structureChanges) reload(parent);
			}
		} else {
			for(int ai=0; ai<editor.data.animationTree.getChildCount(); ai++) {
				Animation animation = (Animation) editor.data.animationTree.getChildAt(ai);
				visitor.edit(animation);
				
				for(int fi=0; fi<animation.getChildCount(); fi++) {
					Frame frame = (Frame) animation.getChildAt(fi);
					visitor.edit(frame);
				}
			}
			if(structureChanges) reload();
		}
	}
	
	void removeNodeFromParent(DefaultMutableTreeNode node) {
		((DefaultTreeModel) animationTree.getModel()).removeNodeFromParent(node);
	}
	
	class DeleteVisitor implements EditVisitor {
		public void edit(Animation animation) {
			removeNodeFromParent(animation);
		}
		public void edit(Frame frame) {
			removeNodeFromParent(frame);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(!e.isAddedPath()) {
			selectedAnimation = null;
			selectedFrame = null;
		} else {
			Object selected = e.getPath().getLastPathComponent();
			if(selected instanceof Frame) {
				selectedFrame = (Frame) selected;
				selectedAnimation = null;
			}
			else if(selected instanceof Animation) {
				selectedFrame = null;
				selectedAnimation = (Animation) selected;
			}
			else {
				selectedFrame = null;
				selectedAnimation = null;
			}
		}
		animationTree.repaint(10);
		
	}


	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		editor.fireEdit();
	}


	@Override
	public void treeNodesInserted(TreeModelEvent e) {
	}


	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		for(Object removedObject: e.getChildren())
			if(removedObject instanceof Animation) 
				editor.data.removeAnimation((Animation) removedObject);
		editor.poke();
	}


	@Override
	public void treeStructureChanged(TreeModelEvent e) {
	}
	
}
