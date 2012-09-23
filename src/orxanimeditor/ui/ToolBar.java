package orxanimeditor.ui;

import java.awt.Dimension;

import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class ToolBar extends JToolBar {

	public ToolBar(String string) {
		super(string);
		setFloatable(false);
	}
	
	public ToolBar() {
		setFloatable(false);
	}
	
	@Override
	public void addSeparator() {
		super.addSeparator(new Dimension(5,1));
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setMaximumSize(new Dimension(10, 40));
		add(separator);
		super.addSeparator(new Dimension(5,1));
	}
}
