package orxanimeditor.animation;

public interface FrameListener {
	public void frameAdded(Animation parent, Frame frame);
	public void frameRemoved(Animation parent, Frame frame);
	public void frameEdited(Frame frame);
	public void frameMoved(Animation oldParent, Frame frame);
}
