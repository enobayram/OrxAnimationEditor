package orxanimeditor.ui.mainwindow;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class InfoBar extends JLabel {
	ArrayList<String>   infoTexts = new ArrayList<String>();
	ArrayList<Object>   infoSources = new ArrayList<Object>();
	
	public InfoBar() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));
	}
	
	protected void setInfoText(String text, Object source) {
		removeSource(source);
		setText(text);
		infoSources.add(source);
		infoTexts.add(text);
	}	
	protected void removeSource(Object source) {
		if(infoSources.contains(source)) {
			int index = infoSources.indexOf(source);
			infoSources.remove(index);
			infoTexts.remove(index);
			if(index==infoTexts.size()) {
				if(infoTexts.size()>0)
					setText(infoTexts.get(index-1));
				else
					setText("");
			}
		}
	}
	}
