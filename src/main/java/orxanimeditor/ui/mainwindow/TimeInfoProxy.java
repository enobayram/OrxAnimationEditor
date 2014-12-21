package orxanimeditor.ui.mainwindow;

import java.util.Timer;
import java.util.TimerTask;

public class TimeInfoProxy extends TimerTask{
	InfoBar infoBar;
	Timer timer;
	public TimeInfoProxy(InfoBar infoBar) {
		this.infoBar = infoBar;
	}
	
	public void sendInfo(String text, long duration) {
		infoBar.setInfoText(text, this);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
