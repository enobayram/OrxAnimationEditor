package orxanimeditor.ui;

import orxanimeditor.data.Animation;
import orxanimeditor.data.Frame;

public interface EditVisitor {
	void edit(Animation animation);
	void edit(Frame frame);
}
