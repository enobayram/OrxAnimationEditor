package orxanimeditor.ui.frameeditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.FrameListener;
import orxanimeditor.ui.SelectionListener;

public class LockRectangleButton extends JToggleButton implements ActionListener, SelectionListener, FrameListener {
	ImageIcon lockedIcon;
	ImageIcon unlockedIcon;
	Frame     selectedFrame;
	
	protected Rectangle			lastRect = new Rectangle(-1, -1, -1, -1);
	protected Point 				lastPivot = new Point(-1,-1);


	public LockRectangleButton(ImageIcon lockedIcon, ImageIcon unlockedIcon) {
		super(unlockedIcon);
		this.lockedIcon=lockedIcon;
		this.unlockedIcon=unlockedIcon;
		addActionListener(this);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(isSelected()) {
			setIcon(lockedIcon);
			lastRect = selectedFrame.getRectangle();
			lastPivot = selectedFrame.getPivot();
			
		}
		else {
			setIcon(unlockedIcon);
			if(!frameHasProperRectangle())
				setEnabled(false);
		}
	}

	@Override
	public void selectionChanged(Object selectedObject) {
		if(selectedObject instanceof Frame) {
			selectedFrame = (Frame) selectedObject;
			if(frameHasProperRectangle())
				setEnabled(true);
		}
		else {
			selectedFrame = null;
			if(!isSelected()) setEnabled(false);
		}
	}
	
	private boolean frameHasProperRectangle() {
		if(selectedFrame==null) return false;
		if(selectedFrame.getRectangle()==null) return false;
		return true;
	}

	@Override
	public void frameEdited(Frame frame) {
		if(frame == selectedFrame && frameHasProperRectangle()) {
			setEnabled(true);
		}
	}

	@Override
	public void dataLoaded() {
		setEnabled(false);
	}

	@Override
	public void frameAdded(Animation parent, Frame frame) {}
	@Override
	public void frameRemoved(Animation parent, Frame frame) {}
	@Override
	public void frameMoved(Animation oldParent, Frame frame) {}
	
}
