package orxanimeditor.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.AnimationListener;
import orxanimeditor.animation.DataLoadListener;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.FrameListener;

public class AnimationViewer extends JPanel implements TreeSelectionListener, DataLoadListener, AnimationListener, FrameListener{

	EditorMainWindow editor;
	
	Animation 	selectedAnimation=null;
	Frame 		selectedFrame=null;
	int			nextFrameIndex=0;

	Timer		timer = new Timer();
	Point		accumulatedOffset = new Point(0, 0);
	
	public AnimationViewer(EditorMainWindow editor) {
		setMinimumSize(new Dimension(100,100));
		this.editor = editor;
		editor.data.addAnimationListener(this);
		editor.data.addFrameListener(this);
		editor.data.addDataLoadListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(selectedFrame!=null) drawFrame(g,selectedFrame);
		if(selectedAnimation!=null && nextFrameIndex<selectedAnimation.getFrameCount()) {
			drawFrame(g,selectedAnimation.getFrame(nextFrameIndex));
		}

	}
	
	private void setupNextFrame() {
		timer.schedule(new TimerTask() { public void run() { 
				SwingUtilities.invokeLater(new Runnable() { public void run() {
					if(selectedAnimation==null || selectedAnimation.getFrameCount()==0) return;
					if(nextFrameIndex>=selectedAnimation.getFrameCount()-1) 
						restart();
					else {
						nextFrameIndex++;
						Frame frame = selectedAnimation.getFrame(nextFrameIndex);
						Point offset = frame.getOffset();
						accumulatedOffset.x += offset.x; accumulatedOffset.y += offset.y;
						repaint(10);
						setupNextFrame();
					}
			}});
		}}, getFrameDelay());
	}
	
	private void restart() {
		timer.cancel();
		timer = new Timer();
		nextFrameIndex = 0;
		accumulatedOffset.x = 0; accumulatedOffset.y = 0;
		if(selectedAnimation!=null && selectedAnimation.getFrameCount()>0) {
			Frame frame = selectedAnimation.getFrame(nextFrameIndex);
			accumulatedOffset = frame.getOffset();
			setupNextFrame();
		}
		repaint(10);
	}

	private long getFrameDelay() {
		Frame nextFrame = selectedAnimation.getFrame(nextFrameIndex);
		if(nextFrame.getKeyDuration()>0) return (long) (nextFrame.getKeyDuration()*1000);
		else return (long) (selectedAnimation.getDefaultKeyDuration() * 1000);
	}

	private void drawFrame(Graphics g, Frame frame) {
		if(frame!=null && frame.getImageFile()!=null && frame.properRectangle()!=null) {
			BufferedImage image = editor.imageManager.openImage(frame.getImageFile().getAbsoluteFile());
			Rectangle rect = frame.properRectangle();
			Point pivot = frame.getPivot();
			int offsetX = getWidth()/2-pivot.x+accumulatedOffset.x;
			int offsetY = getHeight()/2-pivot.y+accumulatedOffset.y;
			int dx1 = offsetX+rect.x, dy1 = offsetY+rect.y;
			int dx2 = offsetX+rect.x+rect.width, dy2 = offsetY+rect.y+rect.height;
			if(frame.getFlipX()) {int tmp = dx1; dx1=dx2; dx2=tmp;}
			if(frame.getFlipY()) {int tmp = dy1; dy1=dy2; dy2=tmp;}
			g.drawImage(image, dx1,dy1,
					           dx2,dy2,  
					           rect.x, rect.y, 
					           rect.x+rect.width, rect.y+rect.height, null);
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Animation ani = editor.animationManager.getSelectedAnimation();
		Frame frame = editor.animationManager.getSelectedFrame();
		if(ani!=null) {
			selectedAnimation=ani;
			selectedFrame = null;
		} else if(frame!=null) {
			selectedAnimation = null;
			selectedFrame = frame;
		} else {
			selectedAnimation = null;
			selectedFrame = null;
		}
		restart();
	}

	@Override
	public void frameAdded(Animation parent, Frame frame) {
		if(parent == selectedAnimation) restart();
	}

	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		if(parent == selectedAnimation) restart();
		if(frame == selectedFrame) {
			selectedFrame = null;
			restart();
		}
	}

	@Override
	public void frameEdited(Frame frame) {
		repaint(10);
	}

	@Override
	public void animationAdded(Animation animation) {
	}

	@Override
	public void animationRemoved(Animation animation) {
		if(animation==selectedAnimation) {
			selectedAnimation = null;
			restart();
		}
	}

	@Override
	public void animationEdited(Animation animation) {
		repaint(10);
	}

	@Override
	public void dataLoaded() {
		selectedAnimation = null;
		selectedFrame = null;
		restart();
	}
}
