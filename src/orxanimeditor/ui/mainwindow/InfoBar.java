package orxanimeditor.ui.mainwindow;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class InfoBar extends JLabel {
	ArrayList<String>   infoTexts = new ArrayList<String>();
	ArrayList<Object>   infoSources = new ArrayList<Object>();
	
	public InfoBar() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(1,20));
	}
	
	protected void setInfoText(String text, Object source) {
		removeSource(source);
		infoSources.add(source);
		infoTexts.add(text);
		showLast();
	}	
	protected void removeSource(Object source) {
		if(infoSources.contains(source)) {
			int index = infoSources.indexOf(source);
			infoSources.remove(index);
			infoTexts.remove(index);
			showLast();
		}
	}

	public void injectText(String text, Object source) {
		if(infoSources.contains(source)) {
			int index = infoSources.indexOf(source);
			infoTexts.set(index, text);
			showLast();
		}
	}

	private void showLast() {
		if(infoTexts.size()>0) {
			setText(infoTexts.get(infoTexts.size()-1));
		} else
			setText("");
	}
}
