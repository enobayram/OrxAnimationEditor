package orxanimeditor.ui.frameeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.sun.corba.se.impl.interceptors.PICurrent;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.FrameListener;
import orxanimeditor.ui.Utilities;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class FrameEditorView extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, FrameListener{
	BufferedImage 		image;
	File		  		imageFile;
	EditorMainWindow 	editorFrame;
	FrameEditor			parent;
	int					checkerSize = 20;
	int					zoom = 1;

	Rectangle			lastRect = new Rectangle(-1, -1, -1, -1);
	Point 				lastPivot = new Point(-1,-1);
	Point				freeOffsetStart;
	Point				freeOffsetEnd;
	
	public FrameEditorView(File file, EditorMainWindow editorFrame) {
		this.editorFrame = editorFrame;
		imageFile = file;
		image = editorFrame.imageManager.openImage(file);
		parent = editorFrame.frameEditor;
		
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		editorFrame.getData().addFrameListener(this);
	}

	public void paint(java.awt.Graphics g_) {
		Graphics2D g = (Graphics2D) g_;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		Utilities.drawCheckerPattern(g.create(0, 0, getViewWidth(), getViewHeight()),checkerSize);
		g.drawImage(image,0,0, image.getWidth()*zoom, image.getHeight()*zoom,0,0,image.getWidth(),image.getHeight(),null);
		for(Object selected: editorFrame.animationManager.getSelectedObjects()) {
			if(selected instanceof Animation) {
				Animation animation = (Animation) selected;
				for(Frame frame: animation.getFrames()) {
					paintFrame(g, frame);
				}
			} 
			if (selected instanceof Frame) {
				Frame frame = (Frame) selected;
				paintFrame(g,frame);
			}		
		}
		if(freeOffsetStart != null && freeOffsetEnd != null) {
			Point base = toScreen(freeOffsetStart);
			Point vector = toScreen(new Point(freeOffsetEnd.x-freeOffsetStart.x, freeOffsetEnd.y-freeOffsetStart.y));
			g.setColor(Color.RED);
			drawArrow(g, vector, base);
		}
	}
	
	private int getViewWidth() {return image.getWidth()*zoom;}
	private int getViewHeight() {return image.getHeight()*zoom;}
	
	private void paintFrame(Graphics g_, Frame frame) {
		Graphics2D g = (Graphics2D) g_.create();
		if(zoom>1)
			g.setStroke(new BasicStroke(2));
		if(frame.getImageFile()!= null && frame.getImageFile().getAbsoluteFile().equals(imageFile)) {
			if(frame.getRectangle()!=null){
				g.setColor(Color.BLACK);
				Rectangle rect = frame.properRectangle();
				g.drawRect(toScreen(rect.x), toScreen(rect.y), toScreen(rect.width), toScreen(rect.height));
				g.drawString(frame.getName(), toScreen(rect.x), toScreen(rect.y)+10);
				Point pivot = frame.getPivot();
				int r = 5;
				g.drawOval(toScreen(pivot.x-r), toScreen(pivot.y-r), toScreen(2*r), toScreen(2*r));
				Point offset = frame.getOffset();
				if(!offset.equals(new Point(0,0))) {
					Point vector = toScreen(offset);
					Point base = toScreen(new Point(pivot.x-offset.x,pivot.y-offset.y));
					g.setColor(Color.YELLOW);
					drawArrow(g, vector, base);
				}
			}
		}
	}
	
	private void drawArrow(Graphics2D g, Point vector, Point base) {
		double arrowAngle = 145*Math.PI/180;
		double headLength = toScreen(10);
		double theta = Math.atan2(vector.y, vector.x);
		Point point0 = base;
		Point point1 = new Point(base.x+vector.x, base.y+vector.y);
		double point2dir = theta+arrowAngle;
		Point point2 = new Point((int)(point1.x+headLength*Math.cos(point2dir)) , (int)(point1.y+headLength*Math.sin(point2dir)));
		double point3dir = theta-arrowAngle;
		Point point3 = new Point((int)(point1.x+headLength*Math.cos(point3dir)) , (int)(point1.y+headLength*Math.sin(point3dir)));
		Point point4 = point1;
		g.drawPolyline(new int[] {point0.x,point1.x,point2.x,point3.x,point4.x}, 
					   new int[] {point0.y,point1.y,point2.y,point3.y,point4.y}, 5);

	}
	
	private int toScreen(int in) {
		return in*zoom;
	}
	
	private Point toScreen(Point in) {
		return new Point(toScreen(in.x), toScreen(in.y));
	}
	
	private int snap(int snapMe){
		return java.lang.Math.round( snapMe/parent.snapSize )*parent.snapSize;
	}
	
	@Override public void mouseDragged(MouseEvent e) {
		Frame selected = getSelectedFrame();
		if(selected==null) return;
		if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
			if(parent.isEditingOffset()) {
				if(freeOffsetStart!=null)
					freeOffsetEnd = getViewPoint(e);
				 else
					handleEditOffset(selected,e);			
			} else {
				selected.setPivot(new Point(getViewX(e),getViewY(e)));
				lastPivot.x = getViewX(e); lastPivot.y = getViewY(e);				
			}
		}
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
			if(parent.isUsingLastRect()) {
				int x = snap(getViewX(e)), y = snap(getViewY(e));	

				selected.setRectangle(new Rectangle(x,y,lastRect.width,lastRect.height));
				selected.setImageFile(editorFrame.getData().project.getRelativeFile(imageFile));

				int PivotDX = lastPivot.x - lastRect.x;
				int PivotDY = lastPivot.y - lastRect.y;
				selected.setPivot(new Point(x+PivotDX,y+PivotDY));

			} else {
				
				Rectangle rect = selected.getRectangle();
				int x = snap(rect.x), y = snap(rect.y);
				int dx = snap(getViewX(e)-x), dy = snap(getViewY(e)-y);
				Rectangle newRectangle = new Rectangle(x,y,dx,dy);
				selected.setRectangle(newRectangle);
				selected.setImageFile(editorFrame.getData().project.getRelativeFile(imageFile));
				lastRect = (Rectangle) newRectangle.clone();
				lastPivot = selected.getPivot();

			}
		}
		repaint(20);
	}

	private void handleEditOffset(Frame selected, MouseEvent e) {
		Point pivot = selected.getPivot();
		Point offset = new Point(pivot.x-getViewX(e), pivot.y-getViewY(e));
		selected.setOffset(offset);
	}

	@Override public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	private Point getViewPoint(MouseEvent e) {
		return new Point(getViewX(e),getViewY(e));
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Frame selected  = getSelectedFrame();
		if(selected==null) return;

		if(e.getButton() == MouseEvent.BUTTON3) {
			if(parent.isEditingOffset()) {
			    if (e.isShiftDown()) {
			    	freeOffsetStart = getViewPoint(e);
			    } else
			    	handleEditOffset(selected, e);
			} else {
				selected.setPivot(new Point(getViewX(e),getViewY(e)));
				lastPivot.x = getViewX(e); lastPivot.x = getViewY(e);
			}
		}
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			if (parent.isUsingLastRect()) {
				selected.setRectangle(new Rectangle(getViewX(e),getViewY(e),lastRect.width,lastRect.height));
				selected.setImageFile(editorFrame.getData().project.getRelativeFile(imageFile));
				int PivotDX = lastPivot.x - lastRect.x;
				int PivotDY = lastPivot.y - lastRect.y;
				selected.setPivot(new Point(getViewX(e)+PivotDX,getViewY(e)+PivotDY));					
			}
			else {
				selected.setRectangle(new Rectangle(getViewX(e),getViewY(e),0,0));
				selected.setImageFile(editorFrame.getData().project.getRelativeFile(imageFile));
				lastPivot = selected.getPivot();
				selected.setPivot(null);
			}
		}
	}

	private int getViewX(MouseEvent e) {
		return e.getX()/zoom;
	}

	private int getViewY(MouseEvent e) {
		return e.getY()/zoom;
	}	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		Frame frame = getSelectedFrame();
		if(e.getButton() == MouseEvent.BUTTON3 && freeOffsetEnd!=null) {
			if(frame!=null) {
				frame.setOffset(new Point(freeOffsetEnd.x-freeOffsetStart.x, freeOffsetEnd.y-freeOffsetStart.y));
			}
			freeOffsetEnd = null;
			freeOffsetStart = null;
			repaint(20);
		}
	}
	
	private Frame getSelectedFrame() {
		return editorFrame.animationManager.getSelectedFrame();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation()<=0) zoom*=2;
		else zoom = Math.max(1, zoom/2);		
		setPreferredSize(new Dimension(image.getWidth()*zoom, image.getHeight()*zoom));
		setSize(getPreferredSize());
		editorFrame.getContentPane().repaint(20);
		editorFrame.doLayout();
		repaint(20);
	}

	@Override
	public void frameAdded(Animation parent, Frame frame) {
		repaint(10);
	}

	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		repaint(10);
	}

	@Override
	public void frameEdited(Frame frame) {
		repaint(10);
	}

	@Override
	public void frameMoved(Animation oldParent, Frame frame) {
		// ignore
	}

	@Override
	public void dataLoaded() {
		repaint(10);
		
	}
}

