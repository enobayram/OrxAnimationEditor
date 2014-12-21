package orxanimeditor.ui;

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.Frame;

public interface EditVisitor {
	void edit(Animation animation);
	void edit(Frame frame);
}
