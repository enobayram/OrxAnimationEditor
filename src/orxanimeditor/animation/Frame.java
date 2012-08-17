package orxanimeditor.animation;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import orxanimeditor.animation.Project.RelativeFile;;

public class Frame extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -2408945560259717838L;
	RelativeFile imageFile;
	Rectangle rect;
	private boolean flipX = false;
	boolean flipY = false;
	private Point pivot = null;
	private double keyDuration = -1;
	
	public Frame(String name) {
		super(name);
	}
	
	public void setImageFile(RelativeFile file) {
		imageFile = file;
	}
	
	public RelativeFile getImageFile() {
		return imageFile;
	}
	
	public void setRectangle(Rectangle rect) {
		this.rect = (Rectangle) rect.clone();
	}
	public Rectangle getRectangle() {
		return rect;
	}

	public String getName() {
		return (String) getUserObject();
	}

	public void setName(String name) {
		setUserObject(name);
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
	}

	public Point getPivot() {
		if(pivot==null) return new Point(rect.x+rect.width/2, rect.y+rect.height/2);
		else return pivot;
	}

	public void setPivot(Point pivot) {
		this.pivot = pivot;
	}

	public boolean getFlipX() {
		return flipX;
	}

	public void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}
	
	public boolean getFlipY() {
		return flipY;
	}

	public void setFlipY(boolean flipY) {
		this.flipY = flipY;
	}
	

}
