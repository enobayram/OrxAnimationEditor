package orxanimeditor.animation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import orxanimeditor.io.AnimIO;

public class EditorData implements Serializable{
	private static final long serialVersionUID = 4254363262632560905L;
	public DefaultMutableTreeNode animationTree = new DefaultMutableTreeNode("Animations");
	public Project				project	= new Project();
	public LinkedList<AnimationSet> animationSets = new LinkedList<AnimationSet>();
	
	public void acquireFromData(EditorData newData, File projectFile) {
        animationTree.removeAllChildren();
        while(newData.animationTree.getChildCount()>0)
     	   animationTree.add((MutableTreeNode) newData.animationTree.getFirstChild());
        project = newData.project;
        project.projectFile = projectFile;
        animationSets = newData.animationSets;
        for(AnimationSet set: animationSets) set.init();
	}
		
	public Animation[] getAnimations() {
		Animation[] animations = new Animation[animationTree.getChildCount()];
		if(animations.length>0) {
			for(int ai = 0; ai<animationTree.getChildCount(); ai++)	animations[ai] = (Animation) animationTree.getChildAt(ai);
		}
		return animations;
	}

	public void removeAnimation(Animation animation) {
		animation.removeFromParent();
		for(AnimationSet set: animationSets) set.removeAnimation(animation);
	}

}
