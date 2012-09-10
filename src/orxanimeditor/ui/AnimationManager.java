package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.AnimationListener;
import orxanimeditor.animation.DataLoadListener;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.FrameListener;
import orxanimeditor.animation.HierarchicalData;
import orxanimeditor.ui.animationtree.AnimationTree;
import orxanimeditor.ui.animationtree.AnimationTreeEditor;
import orxanimeditor.ui.animationtree.AnimationTreeModel;
import orxanimeditor.ui.animationtree.AnimationTreeRenderer;
import orxanimeditor.ui.animationtree.AnimationTreeSelectionModel;

public class AnimationManager extends JPanel implements ActionListener, KeyListener,  AnimationListener, FrameListener, DataLoadListener {
	EditorMainWindow editor;
	JToolBar  toolbar;
	public AnimationTree	  animationTree;
	public AnimationTreeModel animationTreeModel;
		
	//ImageIcon image = (new ImageIcon(getClass().getResource("yourpackage/mypackage/image.gif")));
	
	public static ImageIcon newFrameIcon;
	public static ImageIcon newAnimationIcon;
	public static ImageIcon frameIcon;
	public static ImageIcon animationIcon;		
	public static ImageIcon animationCollapsedIcon;		
	 
	JButton newFrameButton;
	JButton newAnimationButton;
	
	int newFrameSuffix = 0;
	int newAnimationSuffix = 0;

	//Animation selectedAnimation = null;
	//Frame selectedFrame = null;

	Object[] clipboard = new Object[0];
	
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
		
		editor.data.addAnimationListener(this);
		editor.data.addFrameListener(this);
		editor.data.addDataLoadListener(this);
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
		JTextField tmp = new JTextField();
		tmp.setPreferredSize(new Dimension(200,50));
		tmp.setDragEnabled(true);
		//toolbar.add(tmp); //This is nice to have for experimenting with drag and drop
	}
	
	private void prepareTree() {
		animationTree = new AnimationTree();
		animationTreeModel = new AnimationTreeModel(editor.data);
		animationTree.setModel(animationTreeModel);
		animationTree.addKeyListener(this);
		animationTree.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "EditSelected");
		animationTree.getActionMap().put("EditSelected", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				HierarchicalData selected = getSelectedNode();
				if(selected!=null) animationTree.startEditingAtPath(new TreePath(selected.getPath()));
			}
		});
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==newFrameButton) {
			Animation selectedAnimation = getSelectedAnimation();
			Frame     selectedFrame = getSelectedFrame();
			if(selectedAnimation != null) {
				Frame newFrame = new Frame("NewFrame" + newFrameSuffix++);
				selectedAnimation.addFrame(newFrame);
			} else if(selectedFrame != null) {
				Frame newFrame = new Frame("NewFrame" + newFrameSuffix++);
				Animation parent = selectedFrame.getParent();
				parent.addFrame(newFrame, parent.getFrameIndex(selectedFrame)+1);
			} else return;
		}
		if(e.getSource()==newAnimationButton) {
			Animation newAnimation = new Animation("NewAnimation" + newAnimationSuffix++);
			editor.data.addAnimation(newAnimation);
		}
	}
	
			
	public Animation getSelectedAnimation() {
		if(animationTree.getSelectedNode() == null) return null;
		Object selectedObject = animationTree.getSelectedNode();
		if( selectedObject instanceof Animation)
			return (Animation) selectedObject;
		else 
			return null;
	}
	
	public Frame getSelectedFrame() {
		if(animationTree.getSelectedNode() == null) return null;
		Object selectedObject = animationTree.getSelectedNode();
		if( selectedObject instanceof Frame)
			return (Frame) selectedObject;
		else 
			return null;
	}


	public HierarchicalData getSelectedNode() {
		return animationTree.getSelectedNode();
	}

	private HierarchicalData[] getSelectedNodes() {
		HierarchicalData[] nodes= new HierarchicalData[animationTree.getSelectionCount()];
		if(nodes.length>0) {
			int i=0;
			for(TreePath p: animationTree.getSelectionPaths()) nodes[i++] = (HierarchicalData) p.getLastPathComponent();
		}
		return nodes;
	}


	@Override
	public void keyPressed(KeyEvent e) {

		if(e.isControlDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_C:
				clipboard = getSelectedNodes();
				break;
			case KeyEvent.VK_V:
				for(Object node: clipboard) {
					Object nodeObject = node;
					Animation selectedAnimation = getSelectedAnimation();
					if(nodeObject instanceof Frame && selectedAnimation!=null) {
						selectedAnimation.addFrame(((Frame)nodeObject).clone());
					}
					if(nodeObject instanceof Animation) {
						editor.data.addAnimation(((Animation)nodeObject).clone());
					}
				}
				break;				
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				Frame selectedFrame = getSelectedFrame();
				if(selectedFrame!=null) editor.frameEditor.openImage(selectedFrame.getImageFile().getAbsoluteFile());
				break;
			case KeyEvent.VK_DELETE:
				for(HierarchicalData data: animationTree.getSelectedObjects()) {
					data.remove();
				}
				break;
			}
		}
		
	}
	
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	
	public void applyEditVisitor(EditVisitor visitor) {
		for(HierarchicalData obj: animationTree.getSelectedObjects()) {
			if(obj instanceof Frame) {
				visitor.edit((Frame) obj);
			}
			else if(obj instanceof Animation) {
				visitor.edit((Animation) obj);
			}
		}
	}



	
	@Override
	public void dataLoaded() {
		animationTreeModel.fireTreeStructureChanged(new TreeModelEvent(this, new Object[]{editor.data}));
	}


	@Override
	public void frameAdded(Animation parent, Frame frame) {
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, frame.getPath()));
		animationTree.focusOnData(frame);
	}


	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		animationTreeModel.fireTreeNodesRemoved(new TreeModelEvent(this, 
				new Object[]{editor.data,parent,frame}));		
	}


	@Override
	public void frameEdited(Frame frame) {
		Animation parent = frame.getParent();
		animationTreeModel.fireTreeNodesChanged(new TreeModelEvent(this, 
				new Object[]{editor.data,parent},
				new int[]{animationTreeModel.getIndexOfChild(parent, frame)},
				new Object[]{frame}));
	}


	@Override
	public void animationAdded(Animation animation) {
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, animation.getPath()));
		animationTree.focusOnData(animation);
	}


	@Override
	public void animationRemoved(Animation animation) {
		animationTreeModel.fireTreeNodesRemoved(new TreeModelEvent(this, 
				new Object[]{editor.data,animation}));		
	}


	@Override
	public void animationEdited(Animation animation) {
		animationTreeModel.fireTreeNodesChanged(new TreeModelEvent(this, 
				new Object[]{editor.data},
				new int[]{animationTreeModel.getIndexOfChild(editor.data, animation)},
				new Object[]{animation}));
	}


	public HierarchicalData[] getSelectedObjects() {
		return animationTree.getSelectedObjects();
	}


	@Override
	public void frameMoved(Animation oldParent, Frame frame) {
		animationTreeModel.fireTreeNodesRemoved(new TreeModelEvent(this, 
				new Object[]{editor.data,oldParent,frame}));
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, frame.getPath()));
	}


	@Override
	public void animationMoved(Animation animation) {
		animationTreeModel.fireTreeStructureChanged(new TreeModelEvent(this, new Object[]{editor.data}));
	}
}
