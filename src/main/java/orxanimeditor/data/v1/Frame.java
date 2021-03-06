package orxanimeditor.data.v1;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import orxanimeditor.data.v1.Project.RelativeFile;

public class Frame implements HierarchicalData, Serializable, Cloneable{
	private static final long serialVersionUID = -2408945560259717838L;
	private RelativeFile imageFile;
	private Rectangle rect;
	private boolean flipX = false;
	private boolean flipY = false;
	private Point pivot = null;
	private Point offset = null;
	private double keyDuration = -1;
	private String name;
	private Animation parent = null;
	
	public Frame(String name) {
		this.name = name;
		this.parent = null;
	}
	
	public void setImageFile(RelativeFile file) {
		imageFile = file;
		fireEdit();
	}
	
	public RelativeFile getImageFile() {
		return imageFile;
	}
	
	public void setRectangle(Rectangle rect) {
		this.rect = (Rectangle) rect.clone();
		fireEdit();
	}
	public Rectangle getRectangle() {
		if(rect==null) return null;
		return (Rectangle)rect.clone();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireEdit();
	}
	
	public Rectangle properRectangle() {
		if(rect==null) return null;
		Rectangle out = (Rectangle) rect.clone();
		if(out.width<0) {out.x+=out.width; out.width=-out.width;}
		if(out.height<0) {out.y+=out.height; out.height = -out.height;}
		return out;
	}

	public double getKeyDuration() {
		return keyDuration;
	}

	public void setKeyDuration(double keyDuration) {
		this.keyDuration = keyDuration;
		fireEdit();
	}

	public Point getPivot() {
		if(pivot==null) return getDefaultPivot();
		else return (Point) pivot.clone();
	}
	
	private Point getDefaultPivot() {
		if(rect == null) return null;
		return new Point(rect.x+rect.width/2, rect.y+rect.height/2);
	}
	
	public Point getOffset() {
		if(offset!=null) return (Point) offset.clone();
		else return new Point(0,0);
	}
	
	public void setOffset(Point offset) {
		if(offset==null) this.offset = null;
		else this.offset = (Point) offset.clone();
		fireEdit();
	}

	public void setPivot(Point pivot) {
		if(pivot == null) this.pivot = null;
		else this.pivot = (Point) pivot.clone();
		fireEdit();
	}

	public boolean getFlipX() {
		return flipX;
	}

	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
		fireEdit();
	}
	
	public boolean getFlipY() {
		return flipY;
	}

	public void setFlipY(boolean flipY) {
		this.flipY = flipY;
		fireEdit();
	}
	
	private void fireEdit() {
		parent.getParent().fireFrameEdited(this);
	}
	
	public Animation getParent() {
		return parent;
	}

	protected void setParent(Animation animation) {
		parent = animation;
	}

	@Override
	public void remove() {
		parent.removeFrame(this);
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Frame clone() {
		try {
			return (Frame) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	public double getFinalFrameDuration() {
		if(getKeyDuration()>0) return getKeyDuration();
		else return parent.getDefaultKeyDuration();
	}
	
	@Override
	public Object[] getPath() {
		Object[] parentPath = parent.getPath();
		Object[] result= new Object[parentPath.length+1];
		System.arraycopy(parentPath, 0, result, 0, parentPath.length);
		result[result.length-1] = this;
		return result;
	}

	@Override
	public int move(Object newParent_, int currentIndexOfPreviousItem) {
		Animation newParent = (Animation) newParent_;
		return newParent.moveFrame(this, currentIndexOfPreviousItem);
	}
}
