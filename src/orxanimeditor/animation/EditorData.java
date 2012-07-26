package orxanimeditor.animation;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class EditorData implements Serializable{
	private static final long serialVersionUID = 4254363262632560905L;
	public DefaultMutableTreeNode animationTree = new DefaultMutableTreeNode("Animations");
	public File			 targetIni = null;
	public LinkedList<AnimationSet> animationSets = new LinkedList<>();
	
	public void acquireFromData(EditorData newData) {
        animationTree.removeAllChildren();
        while(newData.animationTree.getChildCount()>0)
     	   animationTree.add((MutableTreeNode) newData.animationTree.getFirstChild());
        targetIni = newData.targetIni;
        animationSets = newData.animationSets;
	}
	
	public File getTargetFolder() {
		return new File(targetIni.getParent());
	}

}
