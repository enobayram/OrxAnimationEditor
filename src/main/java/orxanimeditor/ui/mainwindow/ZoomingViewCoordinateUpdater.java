package orxanimeditor.ui.mainwindow;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import orxanimeditor.ui.ZoomingView;

public class ZoomingViewCoordinateUpdater extends MouseAdapter implements MouseMotionListener {

	ZoomingView view;
	InfoBar infoBar;
	
	public ZoomingViewCoordinateUpdater(ZoomingView view, InfoBar infoBar) {
		this.infoBar = infoBar;
		this.view=view;
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
	}
	
	@Override public void mouseDragged(MouseEvent arg0) {
		mouseMoved(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		Point worldPoint = view.screenToWorld(arg0.getPoint());
		infoBar.setCoordinates(worldPoint.x, worldPoint.y);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		infoBar.clearCoordinates();
	}

}
