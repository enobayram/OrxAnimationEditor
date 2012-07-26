package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneLayout;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;

public class AnimationManager extends JPanel implements ActionListener, KeyListener {
	EditorMainWindow editor;
	JToolBar  toolbar;
	JTree	  animationTree;
	static ImageIcon newFrameIcon = new ImageIcon("icons/NewFrame.png");
	static ImageIcon newAnimationIcon = new ImageIcon("icons/NewAnimation.png");
	static ImageIcon frameIcon = new ImageIcon("icons/Frame.png");
	static ImageIcon animationIcon = new ImageIcon("icons/Animation.png");		
	static ImageIcon animationCollapsedIcon = new ImageIcon("icons/AnimationCollapsed.png");		
	
	JButton newFrameButton;
	JButton newAnimationButton;
	
	int newFrameSuffix = 0;
	int newAnimationSuffix = 0;

	
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
		animationTree.setCellRenderer(new AnimationTreeRenderer());
		animationTree.setCellEditor(new DefaultCellEditor(new JTextField()));
		animationTree.setEditable(true);
		animationTree.addKeyListener(this);
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
		TreePath selectionPath = animationTree.getSelectionPath();
		if(selectionPath==null) return null;
		Object lastSelected = selectionPath.getLastPathComponent();
		if(lastSelected instanceof Frame) return (Frame) lastSelected;
		else return null;
	}
	
	public Animation getSelectedAnimation() {
		TreePath selectionPath = animationTree.getSelectionPath();
		if(selectionPath==null) return null;
		Object lastSelected = selectionPath.getLastPathComponent();
		if(lastSelected instanceof Animation) return (Animation) lastSelected;
		else return null;
	}


	public void reload() {
		((DefaultTreeModel) animationTree.getModel()).reload();		
	}
	
	public void reload(DefaultMutableTreeNode node) {
		((DefaultTreeModel)animationTree.getModel()).reload(node);
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if(e.isControlDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_C:
				
				break;
			case KeyEvent.VK_V:
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
	
	class DeleteVisitor implements EditVisitor {
		public void edit(Animation animation) {
			animation.removeFromParent();
		}
		public void edit(Frame frame) {
			frame.removeFromParent();
		}
	}
	
}
