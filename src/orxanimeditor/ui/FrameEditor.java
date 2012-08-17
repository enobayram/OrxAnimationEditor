package orxanimeditor.ui;

import java.awt.BorderLayout;
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
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import orxanimeditor.animation.Frame;

public class FrameEditor extends JPanel implements TreeSelectionListener, ChangeListener, EditListener{
	EditorMainWindow editor;
	Frame selectedFrame = null;
	JTabbedPane views;
	JToolBar toolbar;
	JSlider SnapSlider;
	int snapSize = 5;
	JToggleButton useLastRectButton;


	Map<File, JScrollPane> openedFiles = new HashMap<File, JScrollPane>();
	public FrameEditor(EditorMainWindow editorFrame) {
		editor = editorFrame;
		views = new JTabbedPane();
		prepareToolbar();
		setLayout(new BorderLayout());
		add(toolbar,BorderLayout.NORTH);
		add(views,BorderLayout.CENTER);
		setMinimumSize(new Dimension(200, 200));
		setPreferredSize(getMinimumSize());
	}
	

	private void prepareToolbar() {
		toolbar = new JToolBar();
		toolbar.add(SnapSlider = new JSlider(JSlider.HORIZONTAL, 1, 36, 5));

		SnapSlider.setToolTipText("Change the snap size");
	
		SnapSlider.addChangeListener(this);
		
		SnapSlider.setMajorTickSpacing(5);
		SnapSlider.setMinorTickSpacing(1);
		SnapSlider.setPaintTicks(true);
		SnapSlider.setPaintLabels(true);
		
		ImageIcon useLastRectIcon = editor.getImageIcon("icons/oldRec.png");	
		toolbar.add(useLastRectButton = new JToggleButton(useLastRectIcon));	
		useLastRectButton.setToolTipText("Use your last rectangle");		


	}

	boolean isUsingLastRect() {return useLastRectButton.isSelected();}

	public void openImage(File file) {
		if(file==null) return;
		if(openedFiles.containsKey(file)) {
			views.setSelectedComponent(openedFiles.get(file));
		} else {
			FrameEditorView editorPanel = new FrameEditorView(file, editor);
			JScrollPane newPanel = new JScrollPane(editorPanel);
			views.add(newPanel,file.getName());
			openedFiles.put(file, newPanel);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        source.setValue((int)source.getValue());
        snapSize = (int)source.getValue();
        
    }

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		repaint();
	}


	@Override
	public void edited() {
		repaint();
	}
}
