package orxanimeditor.ui.animationtree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import orxanimeditor.animation.HierarchicalData;

public class AnimationTreeEditor extends DefaultTreeCellEditor {
	
	private JLabel nullEditor = new JLabel();
	AnimationEditor editor = new AnimationEditor();
	

	public AnimationTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
	}
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		Component c = renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, false);
		JLabel displayLabel = (JLabel) c;
		editor.setIcon(displayLabel.getIcon());
		Object userObject = value;
		if(userObject instanceof HierarchicalData)
			editor.setData((HierarchicalData) userObject);
		else editor.setData(null);
		return editor;
	}
	
	@Override
	public Object getCellEditorValue() {
		return editor.data;
	}
	
	@Override
	public boolean isCellEditable(EventObject event)     {
        if (!super.isCellEditable(event))
        {
            return false;
        }
        if (event != null && event.getSource() instanceof JTree && event instanceof MouseEvent)
        {
            MouseEvent mouseEvent = (MouseEvent)event;
            JTree tree = (JTree)event.getSource();
            TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            return path.getLastPathComponent() instanceof HierarchicalData; // root and direct children are not editable
        }
        return false;
	}
	
	private class AnimationEditor extends JPanel implements ActionListener, DocumentListener{
		HierarchicalData data;
		JTextField editor;
		JLabel iconLabel;
		public AnimationEditor() {
			FlowLayout layout = new FlowLayout();
			layout.setHgap(0);
			layout.setVgap(0);
			setLayout(layout);
			editor = new JTextField();
			editor.addActionListener(this);
			editor.getDocument().addDocumentListener(this);
			editor.setPreferredSize(new Dimension(200,30));
			iconLabel = new JLabel();
			add(iconLabel);
			add(editor);
			setOpaque(false);
		}
		
		public void setIcon(Icon icon) {
			iconLabel.setIcon(icon);
		}

		public void setData(HierarchicalData data) {
			this.data = data;
			if(data!=null)
				editor.setText(data.getName());
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			data.setName(editor.getText());
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			data.setName(editor.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			data.setName(editor.getText());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			stopCellEditing();			
		}
		
	}

}
