package orxanimeditor.ui.animationviewer;

import orxanimeditor.data.v1.Frame;

public interface FrameSequence {
	void setContentProvider(ContentProvider contentProvider);
	int getFrameCount();
	Frame getFrame(int index);
	long getFrameDelay(int index);
}
