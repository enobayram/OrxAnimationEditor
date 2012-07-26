package orxanimeditor.ui;

import java.awt.Color;

import javax.swing.JPanel;

import orxanimeditor.animation.AnimationSet;

public class AnimationSetViewer extends JPanel {

	AnimationSet set;
	public AnimationSetViewer(AnimationSet set) {
		this.set = set;
		setBackground(Color.WHITE);
	}
}
