package orxanimeditor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public abstract class SlidingView extends JPanel implements MouseWheelListener, MouseMotionListener{
	protected double viewOffsetX = 0;
	protected double viewOffsetY = 0;
	protected double viewScale = 1;
	private Point lastMousePoint;
	
	protected abstract void paintContent(Graphics2D g);
	
	public SlidingView() {
		setBackground(Color.WHITE);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lastMousePoint = e.getPoint();
			}
		});
	}

	@Override
	public void paint(Graphics g_) {
		super.paint(g_);
		Graphics2D g = (Graphics2D) g_;
		g.scale(viewScale, viewScale);
		g.translate(viewOffsetX, viewOffsetY);
		paintContent(g);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation()<=0) viewScale*=1.2;
		else viewScale = Math.max(1, viewScale/1.2);
		repaint(20);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if((e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
			Point newPoint = e.getPoint();
			double mouseOffsetX = newPoint.x-lastMousePoint.x, mouseOffsetY = newPoint.y-lastMousePoint.y;
			viewOffsetX += mouseOffsetX/viewScale;
			viewOffsetY += mouseOffsetY/viewScale;
			lastMousePoint = newPoint;
			repaint(20);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
