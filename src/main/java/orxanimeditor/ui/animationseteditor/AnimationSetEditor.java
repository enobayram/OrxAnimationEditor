package orxanimeditor.ui.animationseteditor;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.AnimationListener;
import orxanimeditor.data.v1.AnimationSet;
import orxanimeditor.data.v1.AnimationSet.Link;
import orxanimeditor.data.v1.AnimationSetListener;
import orxanimeditor.data.v1.DataLoadListener;
import orxanimeditor.ui.ToolBar;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class AnimationSetEditor extends JPanel implements ActionListener, DataLoadListener, AnimationListener, AnimationSetListener{
	JTabbedPane animationSets;
	ToolBar	toolbar;
	JButton		newAnimationSetButton;
	JButton		deleteAnimationSetButton;
	JButton		addAnimationButton;
	JButton		immediateLinkButton;
	
	HashMap<AnimationSet, AnimationSetViewer> setsTable = new HashMap<AnimationSet,AnimationSetViewer>();
	
	EditorMainWindow editor;
	
	public AnimationSetEditor(EditorMainWindow editor) {
		this.editor = editor;
		
		prepareToolbar(); 
		
		animationSets = new JTabbedPane();
		
		setLayout(new BorderLayout());
		
		add(toolbar, BorderLayout.NORTH);
		add(animationSets, BorderLayout.CENTER);

		editor.getData().addAnimationListener(this);
		editor.getData().addAnimationSetListener(this);
		
		animationSets.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F2"), "RenameSet");
		animationSets.getActionMap().put("RenameSet", new AbstractAction() {			
			public void actionPerformed(ActionEvent arg0) {
				AnimationSetViewer view = (AnimationSetViewer) animationSets.getSelectedComponent();
				if(view==null) return;
				String newName = JOptionPane.showInputDialog("Change The Animation Set Name To:", view.set.getName());
				if(newName!=null) view.set.setName(newName);
			}
		});
	}

	private void prepareToolbar() {
		toolbar = new ToolBar();
		newAnimationSetButton = new JButton(editor.getImageIcon("icons/NewAnimationSet.png"));
		newAnimationSetButton.setToolTipText("Create new animation set");
		newAnimationSetButton.addActionListener(this);
		
		deleteAnimationSetButton = new JButton(editor.getImageIcon("icons/deleteAnimationSet.png"));
		deleteAnimationSetButton.setToolTipText("Delete the selected animation set");
		deleteAnimationSetButton.addActionListener(this);
		
		addAnimationButton    = new JButton(editor.getImageIcon("icons/NewAnimation.png"));
		addAnimationButton.setToolTipText("Add an animation to the current animation set");
		addAnimationButton.addActionListener(this);
		
		immediateLinkButton   = new JButton(editor.getImageIcon("icons/ImmediateLink.png"));
		immediateLinkButton.setToolTipText("Toggle immediate property for the selected animation link");
		immediateLinkButton.addActionListener(this);
		
		toolbar.add(newAnimationSetButton);
		toolbar.add(deleteAnimationSetButton);
		toolbar.add(addAnimationButton);
		toolbar.add(immediateLinkButton);
	}
	
	public void dataLoaded() {
		setsTable.clear();
		animationSets.removeAll();
		for(AnimationSet set: editor.getData().getAnimationSets()) createNewViewer(set);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AnimationSetViewer view = (AnimationSetViewer) animationSets.getSelectedComponent();
		if(e.getSource()==newAnimationSetButton) {
			String newSetName = JOptionPane.showInputDialog("New Animation Set Name:");
			if(newSetName==null || newSetName.isEmpty()) return;
			AnimationSet newSet = new AnimationSet(newSetName);
			editor.getData().addAnimationSet(newSet);
		} else  { // The rest of the buttons are related to a view
			if(view==null) { // So if no view is selected, an error is shown
				JOptionPane.showMessageDialog(editor, "No animation set selected!","Error!",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(e.getSource() == deleteAnimationSetButton) {
				editor.getData().removeAnimationSet(view.set);
			} else if(e.getSource() == addAnimationButton) {
				Animation chosen = (Animation) JOptionPane.showInputDialog(editor, "Choose the animation to add to the current set", "Add Animation", 
					    JOptionPane.QUESTION_MESSAGE, editor.animationManager.animationIcon, 
						editor.getData().getAnimations(), null);
				if(chosen == null) return;
				view.addAnimation(chosen);
			} else if(e.getSource() == immediateLinkButton) {
				Link selectedLink = view.selectedLink;
				if(selectedLink!=null) {
					if(selectedLink.getProperty()==Link.IMMEDIATE_PROPERTY)
						selectedLink.setProperty(Link.NONE_PROPERTY);
					else
						selectedLink.setProperty(Link.IMMEDIATE_PROPERTY);
					
				}
			}
		}
	}

	private void createNewViewer(AnimationSet newSet) {
		AnimationSetViewer newViewer = new AnimationSetViewer(editor, newSet);
		animationSets.add(newViewer,newSet.getName());
		setsTable.put(newSet, newViewer);
	}

	private void deleteViewer(AnimationSetViewer view) {
		setsTable.remove(view.set);
		animationSets.remove(view);
	}

	@Override
	public void animationAdded(Animation animation) {
		repaint(10);
	}

	@Override
	public void animationRemoved(Animation animation) {
		repaint(10);
	}

	@Override
	public void animationEdited(Animation animation) {
		repaint(10);
	}

	@Override
	public void animationMoved(Animation animation) {
		// ignore
	}

	@Override
	public void animationSetAdded(AnimationSet set) {
		createNewViewer(set);

		
	}

	@Override
	public void animationSetRemoved(AnimationSet set) {
		deleteViewer(setsTable.get(set));
	}

	@Override
	public void animationSetModified(AnimationSet set) {
		setsTable.get(set).repaint(20);
		int tabIndex = animationSets.indexOfComponent(setsTable.get(set));
		animationSets.setTitleAt(tabIndex, set.getName());
	}
}
