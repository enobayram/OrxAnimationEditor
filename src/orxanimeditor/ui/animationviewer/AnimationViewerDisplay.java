package orxanimeditor.ui.animationviewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import orxanimeditor.ui.EditorMainWindow;
import orxanimeditor.ui.SlidingView;

public class AnimationViewerDisplay extends SlidingView implements FrameDisplay {
	Frame currentFrame;
	Point currentOffset;
		
	public AnimationViewerDisplay() {
		setMinimumSize(new Dimension(160,100));
	}
	
	@Override
	public void paintContent(Graphics2D g) {
			if(currentFrame!=null) drawFrame(g,currentFrame, currentOffset);
	}
	
	private void drawFrame(Graphics g, Frame frame, Point offset) {
		if(frame!=null && frame.getImageFile()!=null && frame.properRectangle()!=null) {
			BufferedImage image = EditorMainWindow.imageManager.openImage(frame.getImageFile().getAbsoluteFile());
			Rectangle rect = frame.properRectangle();
			Point pivot = frame.getPivot();
			int offsetX = getWidth()/2-pivot.x+offset.x;
			int offsetY = getHeight()/2-pivot.y+offset.y;
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
	public void Display(Frame frame, Point offset) {
		currentFrame = frame;
		currentOffset = offset;
		repaint(20);
	}	
}
