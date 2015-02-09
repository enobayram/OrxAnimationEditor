package orxanimeditor.ui;

import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface ZoomingView {
	public Point screenToWorld(Point point);
	public void addMouseListener(MouseListener c);
	public void addMouseMotionListener(MouseMotionListener c);
}
