package orxanimeditor.ui.animationviewer;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import orxanimeditor.ui.mainwindow.EditorMainWindow;
import orxanimeditor.ui.mainwindow.ZoomingViewCoordinateUpdater;

public class AnimationViewer extends JPanel {
	JSplitPane mainPane;
	AnimationQueue	animationQueue;
	AnimationViewerDisplay display;
	EditorMainWindow editor;
	ContentProvider selectionProvider;
	ContentProvider queueProvider;
	ZoomingViewCoordinateUpdater coordinateUpdater;
	
	public AnimationViewer(EditorMainWindow editor, SelectionFrameSequence selectionSequence) {
		this.editor = editor;
				
		animationQueue = new AnimationQueue(this);
		JScrollPane animationQueueScroller = new JScrollPane(animationQueue);
		animationQueueScroller.setMinimumSize(new Dimension(100, 100));
		animationQueueScroller.setPreferredSize(new Dimension(150, 100));

		
		display = new AnimationViewerDisplay();		
		display.setTransferHandler(animationQueue.getTransferHandler());
		coordinateUpdater = new ZoomingViewCoordinateUpdater(display, editor.getInfoBar());
		
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, animationQueueScroller, display);
		add(mainPane);
		
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
