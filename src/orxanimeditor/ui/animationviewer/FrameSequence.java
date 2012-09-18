package orxanimeditor.ui.animationviewer;

import orxanimeditor.animation.Frame;

public interface FrameSequence {
	void setContentProvider(ContentProvider contentProvider);
	int getFrameCount();
	Frame getFrame(int index);
	long getFrameDelay(int index);
}
