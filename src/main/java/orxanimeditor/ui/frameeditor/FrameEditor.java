package orxanimeditor.ui.frameeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import orxanimeditor.data.v1.Frame;
import orxanimeditor.ui.SelectionListener;
import orxanimeditor.ui.ToolBar;
import orxanimeditor.ui.mainwindow.EditorMainWindow;
import orxanimeditor.ui.mainwindow.AreaInfoProxy;

public class FrameEditor extends JPanel implements SelectionListener, ActionListener {
	EditorMainWindow editor;
	JTabbedPane views;
	ToolBar toolbar;
	JSlider SnapSlider;
	LockRectangleButton lockRectButton;
	JToggleButton editRectButton;
	JToggleButton setPivotButton;
	JToggleButton setOffsetButton;
	JToggleButton relativeOffsetButton;
	TemporaryPivotButton temporaryPivotOffsetButton;
	AreaInfoProxy	infoProxy;
	ButtonGroup group;


	Map<File, JScrollPane> openedFiles = new HashMap<File, JScrollPane>();
	public FrameEditor(EditorMainWindow editorFrame) {
		editor = editorFrame;
		views = new JTabbedPane();
		infoProxy = editor.getInfoProxy();
		prepareToolbar();
		setLayout(new BorderLayout());
		add(toolbar,BorderLayout.NORTH);
		add(views,BorderLayout.CENTER);
		setMinimumSize(new Dimension(200, 200));
		setPreferredSize(new Dimension(400, 500));
		editor.addSelectionListener(this);
		setInfo();
		setTransferHandler(new FrameEditorViewTransferHandler());
		
	}

	private void setInfo() {
		if(editor.animationManager.getSelectedFrame()==null)
			infoProxy.setInfo("Select a frame to edit");
		else if(isEditingRectangle())
			infoProxy.setInfo("Left click & drag: Draw the rectangle for the selected frame");
		else if(isRectangleLocked()) 
			infoProxy.setInfo("Left click: Set the rectangle for the selected frame to the saved rectangle");
		else if(isSettingPivot())
			infoProxy.setInfo("Left click: Set the pivot of the selected frame");
		else if(isSettingOffsetDirectly())
			infoProxy.setInfo("Left click: Set the offset for this frame from a point");
		else if(isSettingOffsetWithRelativePos())
			infoProxy.setInfo("Left click & drag: Set the offset for this frame as the vector between two arbitrary points");
		else if(isSettingOffsetWithTemporaryPivot())
			{} //The relevant button handles the info message
		else
			infoProxy.setInfo("Choose an editing mode from the frame editor toolbar");
		
	}


	private void prepareToolbar() {
		toolbar = new ToolBar();

		SnapSlider = new JSlider(JSlider.HORIZONTAL, 1, 36, 1);
		SnapSlider.setOpaque(false);
		SnapSlider.setMajorTickSpacing(5);
		SnapSlider.setMinorTickSpacing(1);
		SnapSlider.setPaintTicks(true);
		SnapSlider.setPaintLabels(true);
		SnapSlider.setSnapToTicks(true);		

		SnapSlider.setToolTipText("<html>Change the snap size:<br>" +
				"The snap size is used while modifying <br>" +
				"the rectangle for a frame, if the snap <br>" +
				"size is n, the editing will snap to the nth pixel.</html>");		
		toolbar.add(SnapSlider);

		toolbar.addSeparator();
				
		group = new ButtonGroup();

		editRectButton = new JToggleButton(editor.getImageIcon("icons/EditRectangle.png"));
		toolbar.add(editRectButton);
		editRectButton.setToolTipText("Edit the frame rectangle");
		group.add(editRectButton);
		editRectButton.addActionListener(this);

		lockRectButton = new LockRectangleButton(editor.getImageIcon("icons/LockRectangle.png"),
		         editor.getImageIcon("icons/UnlockRectangle.png"));
		toolbar.add(lockRectButton);
		group.add(lockRectButton);
		editor.addSelectionListener(lockRectButton);
		lockRectButton.addActionListener(this);
		
		toolbar.addSeparator();

		setPivotButton = new JToggleButton(editor.getImageIcon("icons/SetPivot.png"));
		toolbar.add(setPivotButton);
		setPivotButton.setToolTipText("Set the frame pivot");
		group.add(setPivotButton);
		setPivotButton.addActionListener(this);
		
		toolbar.addSeparator();

		setOffsetButton = new JToggleButton(editor.getImageIcon("icons/SetOffset.png"));
		toolbar.add(setOffsetButton);
		setOffsetButton.setToolTipText("Set the frame offset vector");
		group.add(setOffsetButton);
		setOffsetButton.addActionListener(this);

		relativeOffsetButton = new JToggleButton(editor.getImageIcon("icons/RelativeOffset.png"));
		toolbar.add(relativeOffsetButton);
		relativeOffsetButton.setToolTipText(
				"<html>Set the frame offset vector as a relative" + 
				"<br>" +
				"position between two arbitrary points </html>"
				);
		group.add(relativeOffsetButton);
		relativeOffsetButton.addActionListener(this);


		temporaryPivotOffsetButton = new TemporaryPivotButton(editor,group,infoProxy,editor.getImageIcon("icons/TemporaryPivotOffset.png"));
		toolbar.add(temporaryPivotOffsetButton);
		group.add(temporaryPivotOffsetButton);
		temporaryPivotOffsetButton.addActionListener(this);
		editor.addSelectionListener(temporaryPivotOffsetButton);
		
	}

	boolean isEditingRectangle() {return editRectButton.isSelected();}
	boolean isRectangleLocked() {return lockRectButton.isSelected();}
	boolean isSettingPivot() {return setPivotButton.isSelected();}
	boolean isSettingOffsetDirectly() {return setOffsetButton.isSelected();}
	boolean isSettingOffsetWithRelativePos() {return relativeOffsetButton.isSelected();}
	boolean isSettingOffsetWithTemporaryPivot() {return temporaryPivotOffsetButton.isSelected();}

	public void openImage(Frame frame) {
		if(frame!=null && frame.getImageFile()!=null) 
			editor.frameEditor.openImage(frame.getImageFile().getAbsoluteFile());
	}
	
	public void openImage(File file) {
		if(file==null) return;
		if(openedFiles.containsKey(file)) {
			views.setSelectedComponent(openedFiles.get(file));
		} else {
			FrameEditorView editorPanel = new FrameEditorView(file, editor);
			JScrollPane newPanel = new JScrollPane(editorPanel);
			views.add(newPanel,file.getName());
			openedFiles.put(file, newPanel);
			editorPanel.addMouseListener(infoProxy);
		}
	}

	@Override
	public void selectionChanged(Object selectedObject) {
		setInfo();
		repaint(20);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// An editing button is pressed
		setInfo();
		repaint(20);
	}

}
