package orxanimeditor.ui.animationviewer;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class AnimationViewer extends JPanel {
	JSplitPane mainPane;
	AnimationQueue	animationQueue;
	AnimationViewerDisplay display;
	EditorMainWindow editor;
	ContentProvider selectionProvider;
	ContentProvider queueProvider;
	
	public AnimationViewer(EditorMainWindow editor, SelectionFrameSequence selectionSequence) {
		this.editor = editor;
		
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		add(mainPane);
		
		animationQueue = new AnimationQueue(this);
		JScrollPane animationQueueScroller = new JScrollPane(animationQueue);

		
		display = new AnimationViewerDisplay();
		display.setTransferHandler(animationQueue.getTransferHandler());
		mainPane.add(animationQueueScroller);
		mainPane.add(display);		
		
		selectionProvider = new ContentProvider(selectionSequence, display);
		selectionSequence.setContentProvider(selectionProvider);
		selectionProvider.enable();
		
		queueProvider = new ContentProvider(animationQueue, display);
		animationQueue.setContentProvider(queueProvider);
		editor.getData().addAnimationListener(animationQueue);
		editor.getData().addFrameListener(animationQueue);
	}
	
	@Override
	public void doLayout() {
		mainPane.setLocation(0, 0);
		mainPane.setSize(getWidth(), getHeight());
	}

	public void queueModified() {
		if(animationQueue.getFrameCount()>0) {
			selectionProvider.disable();
			queueProvider.enable();
		} else {
			selectionProvider.enable();
			queueProvider.disable();			
		}
	}
}
