package orxanimeditor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public abstract class SlidingView extends JPanel implements MouseWheelListener, MouseMotionListener, ZoomingView {
	protected double viewOffsetX = 0;
	protected double viewOffsetY = 0;
	protected double viewScale = 1;
	private Point lastMousePoint;
	protected boolean drawCheckerboard = true;
	private double minScale = 1;
	private double scaleMultiplier = Math.pow(2, 0.25);
	
	protected abstract void paintContent(Graphics2D g);
	
	public SlidingView(boolean drawCheckerboard, double minScale) {
		this.drawCheckerboard = drawCheckerboard;
		this.minScale = minScale;
		setBackground(Color.WHITE);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lastMousePoint = e.getPoint();
			}
		});
		setFocusable(true);
	}

	@Override
	public final void paint(Graphics g_) {
		Graphics2D g = (Graphics2D) g_;
		if(drawCheckerboard) Utilities.drawCheckerPattern(g, 20);
		else super.paint(g);
		g.translate(getWidth()/2, getHeight()/2);
		g.scale(viewScale, viewScale);
		g.translate(viewOffsetX, viewOffsetY);
		paintContent(g);
	}

	@Override
	public final void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getModifiers() == InputEvent.CTRL_MASK) {
			if(e.getWheelRotation()<=0) viewScale*=scaleMultiplier;
			else viewScale = Math.max(minScale, viewScale/scaleMultiplier);			
		} else if (e.getModifiers() == InputEvent.SHIFT_MASK) {
			addOffset(-e.getWheelRotation()*10, 0);
		} else {
			addOffset(0, -e.getWheelRotation()*10);			
		}
		repaint(20);
	}

	@Override
	public final void mouseDragged(MouseEvent e) {
		if((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
			Point newPoint = e.getPoint();
			lastMousePoint = newPoint;
			double mouseOffsetX = newPoint.x-lastMousePoint.x, mouseOffsetY = newPoint.y-lastMousePoint.y;
			addOffset(mouseOffsetX, mouseOffsetY);
			repaint(20);		
		}
	}
	
	private void addOffset(double xInScreen, double yInScreen) {	
		viewOffsetX += xInScreen/viewScale;
		viewOffsetY += yInScreen/viewScale;
		requestFocus();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public Point screenToWorld(Point point_) {
		Point point = (Point)point_.clone();
		point.x-=getWidth()/2; point.y-=getHeight()/2; // Remove screen center
		point.x/=viewScale; point.y/=viewScale;
		point.x-=viewOffsetX; point.y-=viewOffsetY;
		return point;
	}
	
}
