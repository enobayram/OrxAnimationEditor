package orxanimeditor.ui.mainwindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AreaInfoProxy extends MouseAdapter {
	InfoBar infoBar;
	String infoText = "";
	public AreaInfoProxy(InfoBar infoBar) {
		this.infoBar = infoBar;
	}
	public void setInfo(String text) {
		infoText = text;
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		pushInfoText();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		infoBar.removeSource(this);
	}
	private void pushInfoText() {
		infoBar.setInfoText(infoText,this);
	}
}
