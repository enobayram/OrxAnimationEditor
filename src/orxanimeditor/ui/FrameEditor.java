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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import orxanimeditor.animation.Frame;

public class FrameEditor extends JTabbedPane implements TreeSelectionListener{
	EditorMainWindow editor;
	Frame selectedFrame = null;
	Map<File, JScrollPane> openedFiles = new HashMap<File, JScrollPane>();
	public FrameEditor(EditorMainWindow editorFrame) {
		editor = editorFrame;
		setMinimumSize(new Dimension(200, 200));
		setPreferredSize(getMinimumSize());
	}
	

	public void openImage(File file) {
		if(file==null) return;
		if(openedFiles.containsKey(file)) {
			setSelectedComponent(openedFiles.get(file));
		} else {
			FrameEditorView editorPanel = new FrameEditorView(file, editor);
			JScrollPane newPanel = new JScrollPane(editorPanel);
			add(newPanel,file.getName());
			openedFiles.put(file, newPanel);
		}
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		repaint();
	}
}
