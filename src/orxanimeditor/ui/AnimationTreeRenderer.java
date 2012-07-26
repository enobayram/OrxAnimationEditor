package orxanimeditor.ui;

import java.awt.Color;
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
        }
        if(value instanceof Frame) {
        	setIcon(manager.frameIcon);
        }
        
        if(value == manager.selectedAnimation || value == manager.selectedFrame)
        	setForeground(Color.BLUE);
        else
        	setForeground(Color.BLACK);
        
		return this;
	}
}
