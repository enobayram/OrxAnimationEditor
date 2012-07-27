package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.AnimationSet;

public class AnimationSetEditor extends JPanel implements ActionListener, EditListener{
	JTabbedPane animationSets;
	JToolBar	toolbar;
	JButton		newAnimationSetButton;
	JButton		deleteAnimationSetButton;
	JButton		addAnimationButton;
	JButton		deleteAnimButton;
	JButton		deleteLinkButton;
	
	HashMap<AnimationSet, AnimationSetViewer> setsTable = new HashMap<AnimationSet,AnimationSetViewer>();
	
	EditorMainWindow editor;
	
	public AnimationSetEditor(EditorMainWindow editor) {
		this.editor = editor;
		
		prepareToolbar(); 
		
		animationSets = new JTabbedPane();
		
		setLayout(new BorderLayout());
		
		add(toolbar, BorderLayout.NORTH);
		add(animationSets, BorderLayout.CENTER);
		
	}

	private void prepareToolbar() {
		toolbar = new JToolBar();
		newAnimationSetButton = new JButton(editor.getImageIcon("icons/NewAnimationSet.png"));
		newAnimationSetButton.setToolTipText("Create new animation set");
		newAnimationSetButton.addActionListener(this);
		
		deleteAnimationSetButton = new JButton(editor.getImageIcon("icons/deleteAnimationSet.png"));
		deleteAnimationSetButton.setToolTipText("Delete the selected animation set");
		deleteAnimationSetButton.addActionListener(this);
		
		addAnimationButton    = new JButton(editor.getImageIcon("icons/NewAnimation.png"));
		addAnimationButton.setToolTipText("Add an animation to the current animation set");
		addAnimationButton.addActionListener(this);
		
		deleteAnimButton = new JButton(editor.getImageIcon("icons/deleteAnimation.png"));
		deleteAnimButton.setToolTipText("Remove the currently selected animation from this set");
		deleteAnimButton.addActionListener(this);
		
		deleteLinkButton = new JButton(editor.getImageIcon("icons/deleteLink.png"));
		deleteLinkButton.setToolTipText("Delete the currently selected link");
		deleteLinkButton.addActionListener(this);
		
		toolbar.add(newAnimationSetButton);
		toolbar.add(deleteAnimationSetButton);
		toolbar.add(addAnimationButton);
		toolbar.add(deleteAnimButton);
		toolbar.add(deleteLinkButton);
	}
	
	public void dataLoaded() {
		setsTable.clear();
		animationSets.removeAll();
		for(AnimationSet set: editor.data.animationSets) createNewViewer(set);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AnimationSetViewer view = (AnimationSetViewer) animationSets.getSelectedComponent();
		if(e.getSource()==newAnimationSetButton) {
			String newSetName = JOptionPane.showInputDialog("New Animation Set Name:");
			if(newSetName==null || newSetName.isEmpty()) return;
			AnimationSet newSet = new AnimationSet(newSetName);
			editor.data.animationSets.add(newSet);
			createNewViewer(newSet);
		} else  { // The rest of the buttons are related to a view
			if(view==null) { // So if no view is selected, an error is shown
				JOptionPane.showMessageDialog(editor, "No animation set selected!","Error!",JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(e.getSource() == deleteAnimationSetButton) {
				deleteViewer(view);
			} else if(e.getSource() == addAnimationButton) {
				Animation chosen = (Animation) JOptionPane.showInputDialog(editor, "Choose the animation to add to the current set", "Add Animation", 
					    JOptionPane.QUESTION_MESSAGE, editor.animationManager.animationIcon, 
						editor.data.getAnimations(), null);
				if(chosen == null) return;
				view.addAnimation(chosen);
			} else if(e.getSource() == deleteAnimButton) {
				view.deleteAnimation();
			} else if(e.getSource() == deleteLinkButton) {
				view.deleteLink();
			}
		}
	}

	private void createNewViewer(AnimationSet newSet) {
		AnimationSetViewer newViewer = new AnimationSetViewer(editor, newSet);
		animationSets.add(newViewer,newSet.name);
		setsTable.put(newSet, newViewer);
	}

	private void deleteViewer(AnimationSetViewer view) {
		setsTable.remove(view.set);
		editor.data.animationSets.remove(view.set);
		animationSets.remove(view);
	}
	
	@Override
	public void edited() {
		repaint();		
	}
	
}
