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
import orxanimeditor.animation.Frame;

public class AnimationViewer extends JPanel implements TreeSelectionListener, EditListener{

	EditorMainWindow editor;
	
	Animation 	selectedAnimation=null;
	Frame 		selectedFrame=null;
	Frame		nextFrame=null;

	Timer		timer = new Timer();
	
	public AnimationViewer(EditorMainWindow editor) {
		setMinimumSize(new Dimension(100,100));
		this.editor = editor;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(selectedFrame!=null) drawFrame(g,selectedFrame);
		if(selectedAnimation!=null && nextFrame != null) {
			drawFrame(g,nextFrame);
		}

	}
	
	private void setupNextFrame() {
		timer.schedule(new TimerTask() { public void run() { 
				SwingUtilities.invokeLater(new Runnable() { public void run() {
					if(selectedAnimation==null || nextFrame == null) return;
					
					nextFrame = (Frame) selectedAnimation.getChildAfter(nextFrame);
					if(nextFrame==null) nextFrame = (Frame) selectedAnimation.getFirstChild();
					repaint();
					setupNextFrame();
			}});
		}}, getFrameDelay());
	}

	private long getFrameDelay() {
		if(nextFrame.getKeyDuration()>0) return (long) (nextFrame.getKeyDuration()*1000);
		else return (long) (selectedAnimation.getDefaultKeyDuration() * 1000);
	}

	private void drawFrame(Graphics g, Frame frame) {
		if(frame!=null && frame.getImageFile()!=null && frame.properRectangle()!=null) {
			BufferedImage image = editor.imageManager.openImage(frame.getImageFile().getAbsoluteFile());
			Rectangle rect = frame.properRectangle();
			Point pivot = frame.getPivot();
			int offsetX = getWidth()/2-pivot.x;
			int offsetY = getHeight()/2-pivot.y;
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
		timer.cancel();
		timer = new Timer();
		Animation ani = editor.animationManager.getSelectedAnimation();
		Frame frame = editor.animationManager.getSelectedFrame();
		if(ani!=null) {
			selectedAnimation=ani;
			selectedFrame = null;
			if(ani.getChildCount()>0) {
				nextFrame = (Frame) ani.getFirstChild();
				setupNextFrame();
			}
			else nextFrame = null;
		} else if(frame!=null) {
			selectedAnimation = null;
			selectedFrame = frame;
		} else {
			selectedAnimation = null;
			selectedFrame = null;
		}
		repaint();
	}

	@Override
	public void edited() {
		if(selectedFrame!=null) repaint();
	}
	
	

}
