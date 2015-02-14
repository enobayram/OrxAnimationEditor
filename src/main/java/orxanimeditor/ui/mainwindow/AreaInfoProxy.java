package orxanimeditor.ui.mainwindow;

import java.awt.event.MouseEvent;


public class AreaInfoProxy extends InfoProxy {
	public AreaInfoProxy(InfoBar infoBar) {
		super(infoBar);
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		pushInfoText();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		infoBar.removeSource(this);
	}
}
