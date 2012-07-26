package orxanimeditor.ui;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;

public interface EditVisitor {
	void edit(Animation animation);
	void edit(Frame frame);
}
