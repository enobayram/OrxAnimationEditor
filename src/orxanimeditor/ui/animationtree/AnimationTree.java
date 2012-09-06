package orxanimeditor.ui.animationtree;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import orxanimeditor.animation.HierarchicalData;

public class AnimationTree extends JTree implements TreeWillExpandListener{
	
	Object selectedNode = null;

	
	
	public AnimationTree() {
		AnimationTreeRenderer renderer = new AnimationTreeRenderer(this);
		setCellRenderer(renderer);
		setCellEditor(new AnimationTreeEditor(this, renderer));
		setEditable(true);
		//animationTree.getModel().addTreeModelListener(this);
		TreeSelectionModel selectionModel = new AnimationTreeSelectionModel(this);
		setSelectionModel(selectionModel);
		//animationTree.addTreeSelectionListener(this);
		addTreeWillExpandListener(this);
		//setTransferHandler(new AnimationTreeTransferHandler());
		setDragEnabled(true);
		setDropMode(DropMode.INSERT);
		setRootVisible(false);
	}

	@Override 
	public void treeWillExpand(TreeExpansionEvent arg0)	throws ExpandVetoException {}
	
	@Override
	public void treeWillCollapse(TreeExpansionEvent arg0)
			throws ExpandVetoException {
		if(arg0.getPath().getLastPathComponent()==getModel().getRoot())
			throw new ExpandVetoException(arg0);
	}
	
	public HierarchicalData[] getSelectedObjects() {
		TreePath[] selectionPaths = getSelectionPaths();
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
}
