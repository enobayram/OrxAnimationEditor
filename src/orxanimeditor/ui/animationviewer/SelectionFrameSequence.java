package orxanimeditor.ui.animationviewer;

import java.awt.Dimension;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.AnimationListener;
import orxanimeditor.animation.DataLoadListener;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.FrameListener;
import orxanimeditor.ui.EditorMainWindow;
import orxanimeditor.ui.SelectionListener;

public class SelectionFrameSequence implements FrameSequence, SelectionListener, DataLoadListener, AnimationListener, FrameListener {

	EditorMainWindow editor;
	ContentProvider contentProvider;
	
	Animation 	selectedAnimation=null;
	Frame 		selectedFrame=null;
	int			nextFrameIndex=0;

	public SelectionFrameSequence (EditorMainWindow editor) {
		this.editor = editor;
		editor.getData().addAnimationListener(this);
		editor.getData().addFrameListener(this);
	}

	@Override
	public void setContentProvider(ContentProvider contentProvider) {
		this.contentProvider=contentProvider;
	}
	
	@Override
	public int getFrameCount() {
		if(selectedAnimation!=null) return selectedAnimation.getFrameCount();
		if(selectedFrame!=null) return 1;
		return 0;
	}

	@Override
	public Frame getFrame(int index) {
		assert(index<getFrameCount());
		if(selectedAnimation !=null) return selectedAnimation.getFrame(index);
		if(selectedFrame != null) return selectedFrame;
		return null;
	}

	@Override
	public long getFrameDelay(int index) {
		Frame frame = getFrame(index);
		return (long) (frame.getFinalFrameDuration()*1000);
	}


	@Override
	public void frameAdded(Animation parent, Frame frame) {
		if(parent == selectedAnimation) contentProvider.restart();
	}

	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		if(parent == selectedAnimation) contentProvider.restart();
		if(frame == selectedFrame) {
			selectedFrame = null;
			contentProvider.restart();
		}
	}

	@Override
	public void frameEdited(Frame frame) {
		contentProvider.pushFrame();
	}

	@Override
	public void animationAdded(Animation animation) {
	}

	@Override
	public void animationRemoved(Animation animation) {
		if(animation==selectedAnimation) {
			selectedAnimation = null;
			contentProvider.restart();
		}
	}

	@Override
	public void animationEdited(Animation animation) {
		contentProvider.pushFrame();
	}

	@Override
	public void dataLoaded() {
		selectedAnimation = null;
		selectedFrame = null;
		contentProvider.restart();
	}

	@Override
	public void frameMoved(Animation oldParent, Frame frame) {
		Animation newParent = frame.getParent();
		if(newParent == selectedAnimation || oldParent == selectedAnimation)
			contentProvider.restart();
	}

	@Override
	public void animationMoved(Animation animation) {
		// ignore
	}

	@Override
	public void selectionChanged(Object selectedObject) {
		if(selectedObject instanceof Animation) selectedAnimation = (Animation) selectedObject;
		else selectedAnimation = null;
		if(selectedObject instanceof Frame) selectedFrame = (Frame) selectedObject;
		else selectedFrame = null;
		contentProvider.restart();
	}

}
