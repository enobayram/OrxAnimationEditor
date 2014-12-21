package orxanimeditor.ui.animationmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

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

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.AnimationListener;
import orxanimeditor.data.v1.DataLoadListener;
import orxanimeditor.data.v1.Frame;
import orxanimeditor.data.v1.FrameListener;
import orxanimeditor.data.v1.HierarchicalData;
import orxanimeditor.ui.EditVisitor;
import orxanimeditor.ui.SelectionListener;
import orxanimeditor.ui.ToolBar;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class AnimationManager extends JPanel implements ActionListener, KeyListener,  AnimationListener, FrameListener, DataLoadListener {
	EditorMainWindow editor;
	ToolBar  toolbar;
	private AnimationTree	  animationTree;
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
	
	public void addSelectionListener(SelectionListener l) {animationTree.addSelectionListener(l);}
	
	public AnimationManager(EditorMainWindow editorFrame) {
		editor = editorFrame;
		loadIcons();
		prepareToolbar();
		prepareTree();
		
		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		JScrollPane pane = new JScrollPane(animationTree);
		add(pane, BorderLayout.CENTER);
		setMinimumSize(new Dimension(200, 100));
		setPreferredSize(new Dimension(300, 400));
		
		editor.getData().addAnimationListener(this);
		editor.getData().addFrameListener(this);
	}
	

	private void loadIcons() {
		newFrameIcon 			= editor.getImageIcon("icons/NewFrame.png");
		newAnimationIcon 		= editor.getImageIcon("icons/NewAnimation.png");
		frameIcon 				= editor.getImageIcon("icons/Frame.png");
		animationIcon 			= editor.getImageIcon("icons/Animation.png");		
		animationCollapsedIcon 	= editor.getImageIcon("icons/AnimationCollapsed.png");	
	}
	
	private void prepareToolbar() {
		toolbar = new ToolBar("Animation Manager Tools");
		
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
		animationTreeModel = new AnimationTreeModel(editor.getData());
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
			editor.getData().addAnimation(newAnimation);
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
						editor.getData().addAnimation(((Animation)nodeObject).clone());
					}
				}
				break;				
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				Frame selectedFrame = getSelectedFrame();
				editor.frameEditor.openImage(selectedFrame);
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
		animationTreeModel.fireTreeStructureChanged(new TreeModelEvent(this, new Object[]{editor.getData()}));
	}


	@Override
	public void frameAdded(Animation parent, Frame frame) {
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, frame.getPath()));
		animationTree.focusOnData(frame);
	}


	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		animationTreeModel.fireTreeNodesRemoved(new TreeModelEvent(this, 
				new Object[]{editor.getData(),parent,frame}));
		if(frame == getSelectedFrame())
			animationTree.setSelectedNode(null);
	}


	@Override
	public void frameEdited(Frame frame) {
		Animation parent = frame.getParent();
		animationTreeModel.fireTreeNodesChanged(new TreeModelEvent(this, 
				new Object[]{editor.getData(),parent},
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
				new Object[]{editor.getData(),animation}));
		if(animation == getSelectedAnimation())
			animationTree.setSelectedNode(null);
		if(animation == getSelectedFrame().getParent())
			animationTree.setSelectedNode(null);
	}


	@Override
	public void animationEdited(Animation animation) {
		animationTreeModel.fireTreeNodesChanged(new TreeModelEvent(this, 
				new Object[]{editor.getData()},
				new int[]{animationTreeModel.getIndexOfChild(editor.getData(), animation)},
				new Object[]{animation}));
	}


	public HierarchicalData[] getSelectedObjects() {
		return animationTree.getSelectedObjects();
	}


	@Override
	public void frameMoved(Animation oldParent, Frame frame) {
		animationTreeModel.fireTreeNodesRemoved(new TreeModelEvent(this, 
				new Object[]{editor.getData(),oldParent,frame}));
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, frame.getPath()));
	}


	@Override
	public void animationMoved(Animation animation) {
		animationTreeModel.fireTreeStructureChanged(new TreeModelEvent(this, new Object[]{editor.getData()}));
	}
}
