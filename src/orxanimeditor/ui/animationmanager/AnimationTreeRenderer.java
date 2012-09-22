package orxanimeditor.ui.animationmanager;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;

public class AnimationTreeRenderer extends DefaultTreeCellRenderer {
	AnimationTree animationTree;
	public AnimationTreeRenderer(AnimationTree manager) {
		this.animationTree = manager;
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
        	if(expanded || leaf)
        		setIcon(AnimationManager.animationIcon);
        	else
        		setIcon(AnimationManager.animationCollapsedIcon);
        } else if(valueData instanceof Frame) {
        	setIcon(AnimationManager.frameIcon);
        } else if(valueData instanceof EditorData) {
        	setText("Animations");
        }
        
        if(value == animationTree.getSelectedNode())
        	setForeground(Color.BLUE);
        else
        	setForeground(Color.BLACK);
        
		return this;
	}
}
