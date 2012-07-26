package orxanimeditor.animation;

import javax.swing.tree.DefaultMutableTreeNode;

public class Animation extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 171588831429924711L;
	private double defaultKeyDuration = 0.3;
	public Animation(String name) {
		super(name);
	}
	
	public String getName() {
		return (String) getUserObject();
	}

	public void setName(String name) {
		setUserObject(name);
	}

	public double getDefaultKeyDuration() {
		return defaultKeyDuration;
	}

	public void setDefaultKeyDuration(double defaultKeyDuration) {
		this.defaultKeyDuration = defaultKeyDuration;
	}
}
