package orxanimeditor.ui.animationtree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;
import orxanimeditor.ui.AnimationManager;

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
                expanded, false, row,
                hasFocus);
        Object valueData = value;
        if(valueData instanceof Animation) {
        	if(expanded)
        		setIcon(manager.animationIcon);
        	else
        		setIcon(manager.animationCollapsedIcon);
        } else if(valueData instanceof Frame) {
        	setIcon(manager.frameIcon);
        } else if(valueData instanceof EditorData) {
        	setText("Animations");
        }
        
        if(value == manager.getSelectedNode())
        	setForeground(Color.BLUE);
        else
        	setForeground(Color.BLACK);
        
		return this;
	}
}
