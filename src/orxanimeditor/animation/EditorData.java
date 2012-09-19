package orxanimeditor.animation;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

public class EditorData implements Serializable{
	private static final long serialVersionUID = 4254363262632560905L;
	private LinkedList<Animation> animations = new LinkedList<Animation>();
	public Project				project	= new Project();
	public LinkedList<AnimationSet> animationSets = new LinkedList<AnimationSet>();
	
	private transient LinkedList<FrameListener> frameListeners = new LinkedList<FrameListener>();
	private transient LinkedList<AnimationListener> animationListeners = new LinkedList<AnimationListener>();
	private transient LinkedList<DataLoadListener> dataLoadListeners = new LinkedList<DataLoadListener>();
	
	
	public void acquireFromData(EditorData newData, File projectFile) {
//        animations.removeAllChildren();
//        while(newData.animations.getChildCount()>0)
//     	   animations.add((MutableTreeNode) newData.animations.getFirstChild());
		animations.clear(); 
		for(Animation animation:newData.animations) addAnimation(animation);
        project = newData.project;
        project.projectFile = projectFile;
        animationSets = newData.animationSets;
        for(AnimationSet set: animationSets) set.init();
        fireDataLoaded();
	}
	
	public void addAnimation(Animation animation) {
		addAnimation(animation,getAnimationCount());
	}
	
	public void addAnimation(Animation animation, int index) {
		animation.setParent(this);
		animations.add(index,animation);
		fireAnimationAdded(animation);
	}

	public Animation[] getAnimations() {
//		Animation[] animations_ = new Animation[animations.getChildCount()];
//		if(animations_.length>0) {
//			for(int ai = 0; ai<animations.getChildCount(); ai++)	animations_[ai] = (Animation) animations.getChildAt(ai);
//		}
		return animations.toArray(new Animation[0]);
	}
	public int getAnimationCount() {return animations.size();}
	public Animation getAnimation(int index) {return animations.get(index);}
	public int getAnimationIndex(Animation animation) {return animations.indexOf(animation);}

	protected void removeAnimation(Animation animation) {
		animations.remove(animation);
		for(AnimationSet set: animationSets) set.removeAnimation(animation);
		fireAnimationRemoved(animation);
	}
	
	public void addFrameListener(FrameListener fl) {frameListeners.add(fl); addDataLoadListener(fl);}
	public void addAnimationListener(AnimationListener al) {animationListeners.add(al);addDataLoadListener(al);}
	public void addDataLoadListener(DataLoadListener dll) {
		if(!dataLoadListeners.contains(dll))
			dataLoadListeners.add(dll);
	}
	
	protected void fireFrameAdded(Animation parent, Frame frame) {
		for(FrameListener fl: frameListeners) fl.frameAdded(parent,frame);
	}
	
	protected void fireFrameRemoved(Animation parent, Frame frame) {
		for(FrameListener fl: frameListeners) fl.frameRemoved(parent,frame);
	}
	
	protected void fireFrameEdited(Frame frame) {
		for(FrameListener fl: frameListeners) fl.frameEdited(frame);		
	}
	
	protected void fireAnimationAdded(Animation animation) {
		for(AnimationListener al: animationListeners) al.animationAdded(animation);
	}
	protected void fireAnimationRemoved(Animation animation) {
		for(AnimationListener al: animationListeners) al.animationRemoved(animation);
	}
	protected void fireAnimationEdited(Animation animation) {
		for(AnimationListener al: animationListeners) al.animationEdited(animation);
	}
	protected void fireDataLoaded() {
		for(DataLoadListener dll: dataLoadListeners) dll.dataLoaded();
	}
	protected void fireAnimationMoved(Animation animation) {
		for(AnimationListener al: animationListeners) al.animationMoved(animation);
	}
	
	
	public int moveAnimation(Animation animation, int currentIndexOfPreviousAnimation) {
		if(currentIndexOfPreviousAnimation==-1) {
			animations.remove(animation);
			animations.add(0, animation);
			fireAnimationMoved(animation);
			return 0;			
		}
		Animation prevAnimation = getAnimation(currentIndexOfPreviousAnimation);
		if(prevAnimation == animation) {
			return currentIndexOfPreviousAnimation;
		} else {
			animations.remove(animation);
			int newIndexOfPreviousAnimation = getAnimationIndex(prevAnimation);
			animations.add(newIndexOfPreviousAnimation+1, animation);
			fireAnimationMoved(animation);
			return newIndexOfPreviousAnimation+1;
		}
	}

	protected void fireFrameMoved(Animation oldParent, Frame frame) {
		for(FrameListener fl: frameListeners) fl.frameMoved(oldParent,frame);		
	}
}
