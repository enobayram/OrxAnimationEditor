package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
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
import orxanimeditor.ui.animationtree.AnimationTreeEditor;
import orxanimeditor.ui.animationtree.AnimationTreeModel;
import orxanimeditor.ui.animationtree.AnimationTreeRenderer;
import orxanimeditor.ui.animationtree.AnimationTreeSelectionModel;

public class AnimationManager extends JPanel implements ActionListener, KeyListener, TreeSelectionListener, AnimationListener, FrameListener, DataLoadListener {
	EditorMainWindow editor;
	JToolBar  toolbar;
	public JTree	  animationTree;
	public AnimationTreeModel animationTreeModel;
		
	//ImageIcon image = (new ImageIcon(getClass().getResource("yourpackage/mypackage/image.gif")));
	
	public ImageIcon newFrameIcon;
	public ImageIcon newAnimationIcon;
	public ImageIcon frameIcon;
	public ImageIcon animationIcon;		
	public ImageIcon animationCollapsedIcon;		
	 
	JButton newFrameButton;
	JButton newAnimationButton;
	
	int newFrameSuffix = 0;
	int newAnimationSuffix = 0;

	//Animation selectedAnimation = null;
	//Frame selectedFrame = null;
	Object selectedNode = null;

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
	}
	
	private void prepareTree() {
		animationTree = new JTree();
		AnimationTreeRenderer renderer = new AnimationTreeRenderer(this);
		animationTree.setCellRenderer(renderer);
		animationTree.setCellEditor(new AnimationTreeEditor(animationTree, renderer));
		animationTree.setEditable(true);
		animationTree.addKeyListener(this);
		//animationTree.getModel().addTreeModelListener(this);
		TreeSelectionModel selectionModel = new AnimationTreeSelectionModel();
		animationTree.setSelectionModel(selectionModel);
		//animationTree.addTreeSelectionListener(this);
		animationTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override public void treeWillExpand(TreeExpansionEvent arg0)	throws ExpandVetoException {}
			@Override
			public void treeWillCollapse(TreeExpansionEvent arg0)
					throws ExpandVetoException {
				if(arg0.getPath().getLastPathComponent()==editor.data)
					throw new ExpandVetoException(arg0);
			}
		});
		animationTreeModel = new AnimationTreeModel(editor.data);
		animationTree.setModel(animationTreeModel);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==newFrameButton) {
			Animation selectedAnimation = getSelectedAnimation();
			if(selectedAnimation == null) return;
			Frame newFrame = new Frame("NewFrame" + newFrameSuffix++);
			selectedAnimation.addFrame(newFrame);			
		}
		if(e.getSource()==newAnimationButton) {
			Animation newAnimation = new Animation("NewAnimation" + newAnimationSuffix++);
			editor.data.addAnimation(newAnimation);
		}
	}
	
			
	public Frame getSelectedFrame() {
		if(selectedNode == null) return null;
		Object selectedObject = selectedNode;
		if( selectedObject instanceof Frame)
			return (Frame) selectedObject;
		else 
			return null;
	}
	
	public Animation getSelectedAnimation() {
		if(selectedNode == null) return null;
		Object selectedObject = selectedNode;
		if( selectedObject instanceof Animation)
			return (Animation) selectedObject;
		else 
			return null;
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
				for(HierarchicalData data: getSelectedObjects()) {
					data.remove();
				}
				break;
			}
		}
		
	}
	
	private HierarchicalData[] getSelectedNodes() {
		HierarchicalData[] nodes= new HierarchicalData[animationTree.getSelectionCount()];
		if(nodes.length>0) {
			int i=0;
			for(TreePath p: animationTree.getSelectionPaths()) nodes[i++] = (HierarchicalData) p.getLastPathComponent();
		}
		return nodes;
	}
	
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	
	public void applyEditVisitor(EditVisitor visitor) {
		for(HierarchicalData obj: getSelectedObjects()) {
			if(obj instanceof Frame) {
				visitor.edit((Frame) obj);
			}
			else if(obj instanceof Animation) {
				visitor.edit((Animation) obj);
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		selectedNode = e.getPath().getLastPathComponent();	
	}



	
	public HierarchicalData[] getSelectedObjects() {
		TreePath[] selectionPaths = animationTree.getSelectionPaths();
		HierarchicalData[] result;
		if(selectionPaths!=null) {
			result = new HierarchicalData[selectionPaths.length];
			int index = 0;
			for(TreePath p: selectionPaths) {
				Object selectedObject = p.getLastPathComponent();
				if(selectedObject instanceof HierarchicalData)
					result[index] = (HierarchicalData) selectedObject;
				else throw new RuntimeException("Something strange is selected");
				index++;
			}
		} else {
			result = new HierarchicalData[0];
		}
		return result;
	}

	public Object getSelectedNode() {
		return selectedNode;
	}


	@Override
	public void dataLoaded() {
		animationTreeModel.fireTreeStructureChanged(new TreeModelEvent(this, new Object[]{editor.data}));
	}


	@Override
	public void frameAdded(Animation parent, Frame frame) {
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, 
				new Object[]{editor.data,parent,frame}));
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
		animationTreeModel.fireTreeNodesInserted(new TreeModelEvent(this, 
				new Object[]{editor.data,animation}));
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
	
}
