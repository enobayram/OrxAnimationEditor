package orxanimeditor.ui.animationmanager;

import java.util.LinkedList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;

public class AnimationTreeModel implements TreeModel {
	LinkedList<TreeModelListener> treeModelListeners = new LinkedList<TreeModelListener>();
	EditorData data;
	
	public AnimationTreeModel(EditorData data) {
		this.data=data;
	}

	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		treeModelListeners.add(arg0);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent instanceof EditorData)
			return ((EditorData) parent).getAnimation(index);
		if(parent instanceof Animation)
			return ((Animation) parent).getFrame(index);
		else 
			return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent instanceof EditorData)
			return ((EditorData) parent).getAnimationCount();
		if(parent instanceof Animation)
			return ((Animation) parent).getFrameCount();
		else 
			return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent instanceof EditorData)
			return ((EditorData) parent).getAnimationIndex((Animation) child) ;
		if(parent instanceof Animation)
			return ((Animation) parent).getFrameIndex((Frame) child);
		else 
			return 0;
	}

	@Override
	public Object getRoot() {
		return data;
	}

	@Override
	public boolean isLeaf(Object node) {
		if(node instanceof Frame) return true;
		if(node instanceof Animation) return getChildCount(node)==0;
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newvalue) {
		Object parent = path.getParentPath().getLastPathComponent();
		Object changed = path.getLastPathComponent();
		fireTreeNodesChanged(new TreeModelEvent(this, 
				path.getParentPath(),
				new int[]{getIndexOfChild(parent, changed)},
				new Object[]{changed}));
	}
	
	public void fireTreeNodesChanged(TreeModelEvent e) {
		for(TreeModelListener tml: treeModelListeners) tml.treeNodesChanged(e);
	}
	public void fireTreeNodesInserted(TreeModelEvent e) {
		for(TreeModelListener tml: treeModelListeners) tml.treeNodesInserted(e);
		fireTreeStructureChanged(new TreeModelEvent(e.getSource(), e.getTreePath().getParentPath()));
	}
	public void fireTreeNodesRemoved(TreeModelEvent e) {
		for(TreeModelListener tml: treeModelListeners) tml.treeNodesRemoved(e);
		fireTreeStructureChanged(new TreeModelEvent(e.getSource(), e.getTreePath().getParentPath()));
	}
	public void fireTreeStructureChanged(TreeModelEvent e) {
		for(TreeModelListener tml: treeModelListeners) tml.treeStructureChanged(e);
	}

}
