package orxanimeditor.ui;

import java.awt.BorderLayout;
import java.awt.Button;
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

import orxanimeditor.animation.AnimationSet;

public class AnimationSetEditor extends JPanel implements ActionListener, KeyListener{
	JTabbedPane animationSets;
	JToolBar	toolbar;
	JButton		newAnimationSetButton;
	HashMap<AnimationSet, AnimationSetViewer> setsTable = new HashMap<>();
	
	EditorMainWindow editor;
	
	public AnimationSetEditor(EditorMainWindow editor) {
		this.editor = editor;
		
		prepareToolbar(); 
		
		animationSets = new JTabbedPane();
		
		setLayout(new BorderLayout());
		
		add(toolbar, BorderLayout.NORTH);
		add(animationSets, BorderLayout.CENTER);
		animationSets.addKeyListener(this);
	}

	private void prepareToolbar() {
		toolbar = new JToolBar();
		newAnimationSetButton = new JButton(editor.getImageIcon("icons/newAnimationSet.png"));
		newAnimationSetButton.setToolTipText("Create new animation set");
		newAnimationSetButton.addActionListener(this);

		toolbar.add(newAnimationSetButton);
	}
	
	public void dataLoaded() {
		setsTable.clear();
		animationSets.removeAll();
		for(AnimationSet set: editor.data.animationSets) createNewViewer(set);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String newSetName = JOptionPane.showInputDialog("New Animation Set Name:");
		if(newSetName.isEmpty()) return;
		AnimationSet newSet = new AnimationSet(newSetName);
		editor.data.animationSets.add(newSet);
		createNewViewer(newSet);
	}

	private void createNewViewer(AnimationSet newSet) {
		AnimationSetViewer newViewer = new AnimationSetViewer(newSet);
		animationSets.add(newViewer,newSet.name);
		setsTable.put(newSet, newViewer);
	}

	private void deleteViewer(AnimationSetViewer view) {
		setsTable.remove(view.set);
		editor.data.animationSets.remove(view.set);
		animationSets.remove(view);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_DELETE:
			AnimationSetViewer view = (AnimationSetViewer) animationSets.getSelectedComponent();
			if(view!=null) deleteViewer(view);
			System.out.println("here");
			break;
		}
		System.out.println("here2");
	}

	@Override public void keyReleased(KeyEvent arg0) {}
	@Override public void keyTyped(KeyEvent arg0) {}
}
