package orxanimeditor.animation;

import java.io.Serializable;
import java.util.LinkedList;

public class Animation implements HierarchicalData, Serializable, Cloneable{
	private static final long serialVersionUID = 171588831429924711L;
	private double defaultKeyDuration = 0.3;
	private String name;
	private LinkedList<Frame> frames = new LinkedList<Frame>();
	private transient EditorData parent = null;
	public Animation(String name) {
		this.name=name;
	}
	
	public EditorData getParent() {
		return parent;
	}
	
	protected void setParent(EditorData data) {
		this.parent = data;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
		parent.fireAnimationEdited(this);
	}

	public double getDefaultKeyDuration() {
		return defaultKeyDuration;
	}

	public void setDefaultKeyDuration(double defaultKeyDuration) {
		this.defaultKeyDuration = defaultKeyDuration;
		parent.fireAnimationEdited(this);
	}
	
	public Frame[] getFrames() {
		return frames.toArray(new Frame[0]);
	}
	
	public int getFrameCount() {
		return frames.size();
	}
	
	public Frame getFrame(int index) {
		return frames.get(index);
	}
	
	public int getFrameIndex(Frame frame) {return frames.indexOf(frame);}
	
	public void addFrame(Frame frame) {
		addFrame(frame, getFrameCount());
	}
	
	public void addFrame(Frame frame, int index) {
		addFrameSilent(frame, index);
		parent.fireFrameAdded(this, frame);
	}
	
	private void addFrameSilent(Frame frame, int index) {
		frame.setParent(this);
		frames.add(index,frame);		
	}

	public void removeFrame(Frame frame) {
		frames.remove(frame);
		parent.fireFrameRemoved(this, frame);
	}

	@Override
	public void remove() {
		for(Frame frame: getFrames()) removeFrame(frame);
		parent.removeAnimation(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Animation clone() {
		try {
			Animation clone = (Animation) super.clone();
			clone.frames = new LinkedList<Frame>();
			for(Frame frame: frames) clone.addFrameSilent(frame.clone(), clone.frames.size());
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	@Override
	public Object[] getPath() {
		return new Object[]{parent,this};
	}
//	public Frame getNextFrame(Frame frame) {
//		return frames.
//	}
}
