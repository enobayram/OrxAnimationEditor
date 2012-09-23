package orxanimeditor.ui.animationmanager;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import orxanimeditor.data.v1.HierarchicalData;
import orxanimeditor.ui.SelectionListener;

public class AnimationTreeSelectionModel extends DefaultTreeSelectionModel {
	AnimationTree tree;
	public AnimationTreeSelectionModel(AnimationTree tree) {
		this.tree = tree;
	}
	@Override public void addSelectionPath(TreePath path) {
		if(path.getLastPathComponent() instanceof HierarchicalData)
			super.addSelectionPath(path);
	}
	@Override 
	public void addSelectionPaths(TreePath[] paths) {
		LinkedList<TreePath> filteredPaths = new LinkedList<TreePath>();
		for(TreePath path: paths) {
			if(path.getLastPathComponent() instanceof HierarchicalData)
				filteredPaths.add(path);		
		}
		super.addSelectionPaths(filteredPaths.toArray(new TreePath[0]));
	}
	
	@Override 
	public void setSelectionPath(TreePath path) {
		if(path.getLastPathComponent() instanceof HierarchicalData)
			super.setSelectionPath(path);		
	}

	@Override 
	public void setSelectionPaths(TreePath[] paths) {
		LinkedList<TreePath> filteredPaths = new LinkedList<TreePath>();
		for(TreePath path: paths) {
			if(path.getLastPathComponent() instanceof HierarchicalData)
				filteredPaths.add(path);		
		}
		super.setSelectionPaths(filteredPaths.toArray(new TreePath[0]));
	}
	
	@Override
	protected void fireValueChanged(TreeSelectionEvent e) {
		HierarchicalData selectedObject = (HierarchicalData) e.getPath().getLastPathComponent(); 
		tree.setSelectedNode(selectedObject);
		super.fireValueChanged(e);
	}	
}
