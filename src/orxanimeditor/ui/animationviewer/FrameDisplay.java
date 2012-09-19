package orxanimeditor.ui.animationviewer;

import java.awt.Point;

import orxanimeditor.animation.Frame;

public interface FrameDisplay {
	void display(Frame frame, Point offset);
	void clear();
}
