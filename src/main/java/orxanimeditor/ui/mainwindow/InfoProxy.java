package orxanimeditor.ui.mainwindow;

import java.awt.event.MouseAdapter;

public abstract class InfoProxy extends MouseAdapter {

	protected InfoBar infoBar;
	String infoText = "";

	public InfoProxy(InfoBar infoBar) {
		this.infoBar = infoBar;
	}

	public void setInfo(String text) {
		infoText = text;
		infoBar.injectText(text,this);
	}
	
	public void pushInfo(String text) {
		infoText = text;
		pushInfoText();
	}

	protected void pushInfoText() {
		infoBar.setInfoText(infoText,this);
	}

}