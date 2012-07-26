package orxanimeditor.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;

public class AnimationTreeRenderer extends DefaultTreeCellRenderer {
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
        		setIcon(AnimationManager.animationIcon);
        	else
        		setIcon(AnimationManager.animationCollapsedIcon);
        	setToolTipText("An Animation");
        }
        if(value instanceof Frame) {
        	setIcon(AnimationManager.frameIcon);
        	setToolTipText("A Frame");
        }
		return this;
	}
}
