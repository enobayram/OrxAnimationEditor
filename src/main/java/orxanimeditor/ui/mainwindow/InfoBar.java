package orxanimeditor.ui.mainwindow;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class InfoBar extends JPanel {
	ArrayList<JLabel>   infoLabels = new ArrayList<JLabel>();
	ArrayList<Object>   infoSources = new ArrayList<Object>();
	JLabel coordinatesLabel = new JLabel("",SwingConstants.CENTER);
	JLabel infoLabel = null;
	JSeparator separator = new JSeparator(JSeparator.VERTICAL);

	public InfoBar() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(200,20));
		add(coordinatesLabel);
		add(separator);
	}
	
	public void doLayout() {
		coordinatesLabel.setBounds(0, 0, 150, getHeight());
		separator.setBounds(150,0,10,getHeight());
		if(infoLabel!=null) {
			infoLabel.setBounds(160, 0, getWidth()-160, getHeight());
		}
	}
	
	protected void setInfoText(String text, Object source) {
		setInfoLabel(new JLabel(text), source);
	}	
	
	protected void setInfoLabel(JLabel label, Object source) {
		removeSource(source);
		infoSources.add(source);
		infoLabels.add(label);
		showLast();		
	}
	
	protected void removeSource(Object source) {
		if(infoSources.contains(source)) {
			int index = infoSources.indexOf(source);
			infoSources.remove(index);
			infoLabels.remove(index);
			showLast();
		}
	}

	public void injectText(String text, Object source) {
		if(infoSources.contains(source)) {
			int index = infoSources.indexOf(source);
			infoLabels.get(index).setText(text);
			showLast();
		}
	}
	
	public void setCoordinates(int x, int y) {
		coordinatesLabel.setText("{x:"+x + ", y:" + y + "}");
	}
	
	public void clearCoordinates() {
		coordinatesLabel.setText("");
	}
	
	private void showInfoLabel(JLabel label) {
		removeInfoLabel();
		add(label);
		infoLabel = label;
	}
	
	private void removeInfoLabel() {
		if(infoLabel != null) {
			remove(infoLabel);
			infoLabel = null;
		}
	}

	private void showLast() {
		if(infoLabels.size()>0) {
			showInfoLabel(infoLabels.get(infoLabels.size()-1));
		} else {
			removeInfoLabel();
		}
		revalidate();
		repaint();
	}
}
