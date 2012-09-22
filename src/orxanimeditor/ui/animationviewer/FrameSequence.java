package orxanimeditor.ui.animationviewer;

import orxanimeditor.data.Frame;

public interface FrameSequence {
	void setContentProvider(ContentProvider contentProvider);
	int getFrameCount();
	Frame getFrame(int index);
	long getFrameDelay(int index);
}
