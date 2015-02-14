package orxanimeditor.ui.mainwindow;

import java.awt.event.MouseEvent;

public class MousePressInfoProxy extends InfoProxy {

	int buttonID;
	
	public MousePressInfoProxy(InfoBar infoBar, int buttonID) {
		super(infoBar);
		this.buttonID = buttonID;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == buttonID) {
			pushInfoText();			
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == buttonID) {
			infoBar.removeSource(this);			
		}
	}	
}
