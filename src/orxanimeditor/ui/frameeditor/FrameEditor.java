package orxanimeditor.ui.frameeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;
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

import orxanimeditor.animation.Frame;
import orxanimeditor.ui.SelectionListener;
import orxanimeditor.ui.ToolBar;
import orxanimeditor.ui.mainwindow.EditorMainWindow;
import orxanimeditor.ui.mainwindow.AreaInfoProxy;

public class FrameEditor extends JPanel implements SelectionListener, ChangeListener {
	EditorMainWindow editor;
	Frame selectedFrame = null;
	JTabbedPane views;
	ToolBar toolbar;
	JSlider SnapSlider;
	int snapSize = 5;
	LockRectangleButton lockRectButton;
	JToggleButton editOffsetButton;
	AreaInfoProxy	infoProxy;


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
		setPreferredSize(getMinimumSize());
		infoProxy.setInfo("info here");
		editor.addSelectionListener(this);
		
	}
	

	private void prepareToolbar() {
		toolbar = new ToolBar();

		SnapSlider = new JSlider(JSlider.HORIZONTAL, 1, 36, 5);
		SnapSlider.setOpaque(false);
		toolbar.add(SnapSlider);

		toolbar.addSeparator();

		SnapSlider.setToolTipText("Change the snap size");
	
		SnapSlider.addChangeListener(this);
		
		SnapSlider.setMajorTickSpacing(5);
		SnapSlider.setMinorTickSpacing(1);
		SnapSlider.setPaintTicks(true);
		SnapSlider.setPaintLabels(true);
		
		lockRectButton = new LockRectangleButton(editor.getImageIcon("icons/LockRectangle.png"),
										         editor.getImageIcon("icons/UnlockRectangle.png"));

		toolbar.add(lockRectButton);	
		lockRectButton.setToolTipText("Lock the selected rectangle");		
		editor.addSelectionListener(lockRectButton);
		
		toolbar.addSeparator();

		ImageIcon editOffsetIcon = editor.getImageIcon("icons/OffsetIcon.png");
		editOffsetButton = new JToggleButton(editOffsetIcon);
		toolbar.add(editOffsetButton);
		editOffsetButton.setToolTipText("Edit the frame offset vector");
		
	}

	boolean isUsingLastRect() {return lockRectButton.isSelected();}
	boolean isEditingOffset() {return editOffsetButton.isSelected();}

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
	public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        source.setValue((int)source.getValue());
        snapSize = (int)source.getValue();
        
    }

	@Override
	public void selectionChanged(Object selectedObject) {
		repaint(20);
	}

}
