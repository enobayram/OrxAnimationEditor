package orxanimeditor.ui.animationtree;

import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import orxanimeditor.animation.HierarchicalData;

public class AnimationTreeSelectionModel extends DefaultTreeSelectionModel {
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
}