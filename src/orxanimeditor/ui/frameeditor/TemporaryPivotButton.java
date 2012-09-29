package orxanimeditor.ui.frameeditor;

import java.awt.Point;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import orxanimeditor.data.v1.Frame;
import orxanimeditor.data.v1.HierarchicalData;
import orxanimeditor.ui.SelectionListener;
import orxanimeditor.ui.mainwindow.AreaInfoProxy;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class TemporaryPivotButton extends JToggleButton implements SelectionListener {
	EditorMainWindow editor;
	ButtonGroup group;
	Frame selectedFrame;
	HierarchicalData[] objects;
	AreaInfoProxy infoProxy;
         
	String tooltipText = "Set the frame offset vector by <br>" +
	"defining two arbitrary points in two <br> " +
	"frames as a temporary pivot";
	
	private Point pivot1;
	private Point pivot2;
	
    public TemporaryPivotButton(EditorMainWindow editor, ButtonGroup group, AreaInfoProxy infoProxy, ImageIcon icon) {
        super(icon);
		this.editor=editor;
		this.group=group;
		this.infoProxy = infoProxy;
		setModel(new TemporaryPivotButtonModel());
		enableIfValid();
	}

	private void enableIfValid() {
		objects = editor.getSelectedObjects();
		if(objects.length==2 && 
				selectedFrame != null &&
				isValidFrame(objects[0]) && 
				isValidFrame(objects[1]))
			setEnabled(true);
		else
			setEnabled(false);
		restart();
	}
	
	private boolean isValidFrame(Object object) {
		if(object instanceof Frame) {
			Frame frame = (Frame) object;
			if(frame.getRectangle() == null) return false;
			else return true;
		} else
			return false;
	}
	
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if(b) {
			setToolTipText("<html>" + tooltipText + "</html>");
		}
		else
			setToolTipText("<html>" + tooltipText+ "<br> (Enabled if and only if two <br> frames with valid rectangles <br> are selected)</html>");
		if(isSelected() && !b) {
			group.clearSelection();
			doClick();
		}
	}
	
	public Point getPrevFramePivot() {
		int index;
		if(objects[0]!=selectedFrame)
			index=0;
		else
			index=1;
		Frame frame = (Frame) objects[index];
		return frame.getPivot();
	}
	
	public Point getCurFramePivot() {
		return selectedFrame.getPivot();
	}


	@Override
	public void selectionChanged(Object selectedObject) {
		if(selectedObject instanceof Frame) selectedFrame = (Frame) selectedObject;
		else selectedFrame = null;
		enableIfValid();
	}

	void restart() {
		setPivot1(null);
		setPivot2(null);
		if(isSelected())
			infoProxy.setInfo("Right click: Indicate the temporary pivot on the \"previous\" frame");
	}

	public Point getOffset() {
		Point framePivot1 = getPrevFramePivot();
		Point framePivot2 = getCurFramePivot();
		Point offset = new Point(getPivot1().x+framePivot2.x-getPivot2().x-framePivot1.x,
								 getPivot1().y+framePivot2.y-getPivot2().y-framePivot1.y);
		return offset;
	}

	public Point getPivot1() {
		return pivot1;
	}

	public void setPivot1(Point pivot1) {
		this.pivot1 = pivot1;
		infoProxy.setInfo("Right click: Indicate the temporary pivot on the current frame");
	}

	public Point getPivot2() {
		return pivot2;
	}

	public void setPivot2(Point pivot2) {
		this.pivot2 = pivot2;
	}
	
	class TemporaryPivotButtonModel extends JToggleButton.ToggleButtonModel {
		@Override
		public void setSelected(boolean b) {
			if(b) {
				infoProxy.setInfo("Right click: Indicate the temporary pivot on the \"previous\" frame");
			}
			else {
			}
			super.setSelected(b);
		}
	}

}
