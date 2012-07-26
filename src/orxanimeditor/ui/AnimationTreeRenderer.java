package orxanimeditor.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;

public class AnimationTreeRenderer extends DefaultTreeCellRenderer {
	AnimationManager manager;
	public AnimationTreeRenderer(AnimationManager manager) {
		this.manager = manager;
	}
	@Override
	public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        if(value instanceof Animation) {
        	if(expanded)
        		setIcon(manager.animationIcon);
        	else
        		setIcon(manager.animationCollapsedIcon);
        	setToolTipText("An Animation");
        }
        if(value instanceof Frame) {
        	setIcon(manager.frameIcon);
        	setToolTipText("A Frame");
        }
		return this;
	}
}
