package orxanimeditor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;

public class FrameEditorView extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener{
	BufferedImage 		image;
	File		  		imageFile;
	EditorMainWindow 	editorFrame;
	FrameEditor			parent;
	int					checkerSize = 20;
	int					zoom = 1;

	Rectangle			lastRect = new Rectangle(-1, -1, -1, -1);
	Point 				lastPivot = new Point(-1,-1);
	
	public FrameEditorView(File file, EditorMainWindow editorFrame) {
		this.editorFrame = editorFrame;
		imageFile = file;
		image = editorFrame.imageManager.openImage(file);
		parent = editorFrame.frameEditor;
		
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	LinkedList<EditListener> editListeners = new LinkedList<EditListener>();


	public void paint(java.awt.Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		drawCheckerPattern(g.create(0, 0, getViewWidth(), getViewHeight()));
		g.drawImage(image,0,0, image.getWidth()*zoom, image.getHeight()*zoom,0,0,image.getWidth(),image.getHeight(),null);
		TreePath[] selectionPaths = editorFrame.animationManager.animationTree.getSelectionPaths();
		if(selectionPaths!=null) {
			for(TreePath p: selectionPaths) {
				Object selected = p.getLastPathComponent();
				if(selected instanceof Animation) {
					Animation animation = (Animation) selected;
					if(animation.getChildCount()==0) continue;
					for(Frame frame = (Frame) animation.getFirstChild(); frame!=null; frame = (Frame) frame.getNextSibling()) {
						paintFrame(g, frame);
					}
				} 
				if (selected instanceof Frame) {
					Frame frame = (Frame) selected;
					paintFrame(g,frame);
				}
			}
		}
	}
	
	private int getViewWidth() {return image.getWidth()*zoom;}
	private int getViewHeight() {return image.getHeight()*zoom;}
	
	private void paintFrame(Graphics g, Frame frame) {
		if(frame.getImageFile()!= null && frame.getImageFile().getAbsoluteFile().equals(imageFile)) {
			if(frame.getRectangle()!=null){
				g.setColor(Color.BLACK);
				Rectangle rect = frame.properRectangle();
				g.drawRect(toScreen(rect.x), toScreen(rect.y), toScreen(rect.width), toScreen(rect.height));
				g.drawString(frame.getName(), toScreen(rect.x), toScreen(rect.y)+10);
				Point pivot = frame.getPivot();
				int r = 5;
				g.drawOval(toScreen(pivot.x-r), toScreen(pivot.y-r), toScreen(2*r), toScreen(2*r));
			}
		}
	}
	
	private int toScreen(int in) {
		return in*zoom;
	}

	private void drawCheckerPattern(Graphics g) {
		g.setColor(new Color(150,150,200));
		g.fillRect(0, 0, getViewWidth(), getViewHeight());
		
		g.setColor(Color.GRAY);
		for(int i = 0; i<getViewWidth(); i+=checkerSize)
			for(int j=0; j<getViewHeight(); j+=checkerSize) 
				if(((i+j)/checkerSize)%2==0)
				g.fillRect(i, j, checkerSize, checkerSize);
	}
	
	private int snap(int snapMe){
		return java.lang.Math.round( snapMe/parent.snapSize )*parent.snapSize;
	}
	
	@Override public void mouseDragged(MouseEvent e) {
		if(!parent.isUsingLastRect()) {
			Frame selected = getSelectedFrame();
			if(selected != null) {
				if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
					Rectangle rect = selected.getRectangle();
					int x = snap(rect.x), y = snap(rect.y);
					int dx = snap(getViewX(e)-x), dy = snap(getViewY(e)-y);
					Rectangle newRectangle = new Rectangle(x,y,dx,dy);
					selected.setRectangle(newRectangle);
					selected.setImageFile(editorFrame.data.project.getRelativeFile(imageFile));
					lastRect = (Rectangle) newRectangle.clone();
					lastPivot = selected.getPivot();
				}
				if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
					selected.setPivot(new Point(getViewX(e),getViewY(e)));
					lastPivot.x = getViewX(e); lastPivot.y = getViewY(e);

				}
				repaint();
				fireEdit();
			}	
		} else {
			Frame selected = getSelectedFrame();
			if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
				
				int x = snap(getViewX(e)), y = snap(getViewY(e));	
				
				selected.setRectangle(new Rectangle(x,y,lastRect.width,lastRect.height));
				selected.setImageFile(editorFrame.data.project.getRelativeFile(imageFile));
				
				int PivotDX = lastPivot.x - lastRect.x;
				int PivotDY = lastPivot.y - lastRect.y;
				selected.setPivot(new Point(x+PivotDX,y+PivotDY));
				
				repaint();
				fireEdit();
			}
		}
	}

	@Override public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!parent.isUsingLastRect()) {
			Frame selected = getSelectedFrame();
			if(selected != null) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					selected.setRectangle(new Rectangle(getViewX(e),getViewY(e),0,0));
					selected.setImageFile(editorFrame.data.project.getRelativeFile(imageFile));
					lastPivot = selected.getPivot();
					selected.setPivot(null);
				}
				if(e.getButton() == MouseEvent.BUTTON3) {
					selected.setPivot(new Point(getViewX(e),getViewY(e)));
					lastPivot.x = getViewX(e); lastPivot.x = getViewY(e);
				}
				
				repaint();
				fireEdit();
			}	
		}
		else 
		{
			if(e.getButton() == MouseEvent.BUTTON1) {
				Frame selected  = getSelectedFrame();
				selected.setRectangle(new Rectangle(getViewX(e),getViewY(e),lastRect.width,lastRect.height));
				selected.setImageFile(editorFrame.data.project.getRelativeFile(imageFile));
				int PivotDX = lastPivot.x - lastRect.x;
				int PivotDY = lastPivot.y - lastRect.y;
				selected.setPivot(new Point(getViewX(e)+PivotDX,getViewY(e)+PivotDY));
				
				repaint();
				fireEdit();
			}	
		}
	}

	private void fireEdit() {
		editorFrame.fireEdit();
	}

	private int getViewX(MouseEvent e) {
		return e.getX()/zoom;
	}

	private int getViewY(MouseEvent e) {
		return e.getY()/zoom;
	}	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private Frame getSelectedFrame() {
		return editorFrame.animationManager.getSelectedFrame();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation()>0) zoom*=2;
		else zoom = Math.max(1, zoom/2);		
		setPreferredSize(new Dimension(image.getWidth()*zoom, image.getHeight()*zoom));
		setSize(getPreferredSize());
		editorFrame.getContentPane().repaint();
		editorFrame.doLayout();
		repaint();
	}
}

