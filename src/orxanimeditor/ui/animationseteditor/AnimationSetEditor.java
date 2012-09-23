package orxanimeditor.ui.animationseteditor;

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

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.AnimationListener;
import orxanimeditor.data.v1.AnimationSet;
import orxanimeditor.data.v1.DataLoadListener;
import orxanimeditor.ui.ToolBar;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class AnimationSetEditor extends JPanel implements ActionListener, DataLoadListener, AnimationListener{
	JTabbedPane animationSets;
	ToolBar	toolbar;
	JButton		newAnimationSetButton;
	JButton		deleteAnimationSetButton;
	JButton		addAnimationButton;
	
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
				
		toolbar.add(newAnimationSetButton);
		toolbar.add(deleteAnimationSetButton);
		toolbar.add(addAnimationButton);
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
						editor.getData().getAnimations(), null);
				if(chosen == null) return;
				view.addAnimation(chosen);
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
		editor.getData().removeAnimationSet(view.set);
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
	
}
