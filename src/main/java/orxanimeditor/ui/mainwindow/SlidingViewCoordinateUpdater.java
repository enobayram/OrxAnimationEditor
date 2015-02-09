package orxanimeditor.ui.mainwindow;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

import orxanimeditor.ui.SlidingView;
import sun.security.acl.WorldGroupImpl;

public class SlidingViewCoordinateUpdater extends MouseAdapter implements MouseMotionListener {

	SlidingView view;
	InfoBar infoBar;
	
	public SlidingViewCoordinateUpdater(SlidingView view, InfoBar infoBar) {
		this.infoBar = infoBar;
		this.view=view;
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
	}
	
	@Override public void mouseDragged(MouseEvent arg0) {}

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
