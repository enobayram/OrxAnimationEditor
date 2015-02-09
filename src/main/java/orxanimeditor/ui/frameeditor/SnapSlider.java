package orxanimeditor.ui.frameeditor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;

import orxanimeditor.ui.mainwindow.AreaInfoProxy;

public class SnapSlider extends JSlider {
	AreaInfoProxy infoProxy;
	
	public SnapSlider(AreaInfoProxy infoProxy) {
		super(JSlider.HORIZONTAL, 1, 32, 1);
		this.infoProxy = infoProxy;
		setOpaque(false);
		setMinorTickSpacing(1);
		setPaintTicks(true);
		setPaintLabels(true);
		setSnapToTicks(true);
		Hashtable<Integer, JLabel> tickTable = new Hashtable<>();
		tickTable.put(1, new JLabel("1"));
		tickTable.put(2, new JLabel("2"));
		tickTable.put(4, new JLabel("4"));
		tickTable.put(8, new JLabel("8"));
		tickTable.put(16, new JLabel("16"));
		tickTable.put(32, new JLabel("32"));
		setLabelTable(tickTable);
		setUI(new SnapSliderUI());

		setToolTipText("<html>Change the snap size:<br>" +
				"The snap size is used while modifying <br>" +
				"the rectangle for a frame, if the snap <br>" +
				"size is n, the editing will snap to the nth pixel.</html>");		
		
		addMouseListener(infoProxy);
		infoProxy.setInfo("Set the snap value for the frame editor.");
	}
	
	class SnapSliderUI extends MetalSliderUI {
		public SnapSliderUI() {
			super();
		}
		@Override
		public void paintThumb(Graphics g) {
			super.paintThumb(g);
			Font font = new Font("Monospaced",Font.BOLD,8);
			String valueText = Integer.toString(getValue());
			g.setFont(font);
			FontMetrics fm = getFontMetrics( font );
			int width = fm.stringWidth(valueText);
			int offsetX = (thumbRect.width-width) / 2;
			g.drawString(valueText, thumbRect.x+offsetX, thumbRect.y+8);
		}		
	}
	
}
