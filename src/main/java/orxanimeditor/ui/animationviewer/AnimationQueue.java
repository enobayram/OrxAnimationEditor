package orxanimeditor.ui.animationviewer;

import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.AnimationListener;
import orxanimeditor.data.v1.Frame;
import orxanimeditor.data.v1.FrameListener;
import orxanimeditor.data.v1.HierarchicalData;
import orxanimeditor.ui.animationmanager.AnimationTreeTransferHandler;

public class AnimationQueue extends JList implements FrameSequence, AnimationListener, FrameListener{
	DefaultListModel model;
	ContentProvider provider;
	AnimationViewer viewer;
	public AnimationQueue(final AnimationViewer viewer) {
		super(new DefaultListModel());
		this.viewer = viewer;
		model = (DefaultListModel) getModel();
		setMinimumSize(new Dimension(100, 100));
		
		setTransferHandler(new AnimationQueueTransferHandler(this));
		setDropMode(DropMode.INSERT);
		
		model.addListDataListener(new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent arg0) {
				queueModified();
			}
			
			@Override
			public void intervalAdded(ListDataEvent arg0) {
				queueModified();
			}
			
			@Override
			public void contentsChanged(ListDataEvent arg0) {
				queueModified();
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "RemoveSelected");
		getActionMap().put("RemoveSelected", new AbstractAction() {			
			public void actionPerformed(ActionEvent e) {
				for(Object obj: getSelectedValues())
						model.removeElement(obj);
			}
		});
	}
		
	public int getListSize() {
		return model.getSize();
	}

	private void queueModified() {
		viewer.queueModified();
		provider.restart();	
	}

	public void receiveObj(Object obj, int dropIndex) {
		model.add(dropIndex, obj);
	}

	@Override
	public void setContentProvider(ContentProvider contentProvider) {
		provider = contentProvider;
	}

	@Override
	public int getFrameCount() {
		int listSize = model.getSize();
		int frameCount = 0;
		for(int i=0; i<listSize; i++) {
			HierarchicalData data = (HierarchicalData) model.get(i);
			if(data instanceof Animation) {
				Animation animation = (Animation) data;
				frameCount+=animation.getFrameCount();
			} else {
				frameCount+=1;
			}
		}
		return frameCount;
	}

	@Override
	public Frame getFrame(int index) {
		assert(index<getFrameCount());
		int listSize = model.getSize();
		for(int i=0; i<listSize; i++) {
			HierarchicalData data = (HierarchicalData) model.get(i);
			if(data instanceof Animation) {
				Animation animation = (Animation) data;
				int frameCount = animation.getFrameCount();
				if(frameCount<=index) index-=frameCount;
				else return animation.getFrame(index);
			} else {
				if(index==0) return (Frame) data;
				else --index;
			}
		}
		return null;
	}

	@Override
	public long getFrameDelay(int index) {
		Frame frame = getFrame(index);
		return (long) (frame.getFinalFrameDuration()*1000);
	}

	@Override
	public void dataLoaded() {
		model.clear();
		queueModified();
	}

	@Override
	public void frameAdded(Animation parent, Frame frame) {
		if(model.contains(parent)) provider.restart();
	}

	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		if(model.contains(parent)) provider.restart();
		if(model.contains(frame)) {
			removeAll(frame);
			queueModified();
		}
	}

	@Override
	public void frameEdited(Frame frame) {
		if(model.contains(frame)) updateUI();
	}

	@Override
	public void frameMoved(Animation oldParent, Frame frame) {
		if(model.contains(oldParent)||model.contains(frame.getParent())) provider.restart();
	}

	@Override
	public void animationAdded(Animation animation) {
	}

	@Override
	public void animationRemoved(Animation animation) {
		if(model.contains(animation)) {
			removeAll(animation);
			queueModified();
		}
	}
	
	private void removeAll(Object obj) {
		while(model.contains(obj)) model.removeElement(obj);
	}

	@Override
	public void animationEdited(Animation animation) {
		if(model.contains(animation)) updateUI();
	}

	@Override
	public void animationMoved(Animation animation) {
	}

}

class AnimationQueueTransferHandler extends TransferHandler {
	
	AnimationQueue queue;
	
	public AnimationQueueTransferHandler(AnimationQueue queue) {
		this.queue = queue;
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		Transferable t = support.getTransferable();
		if(!support.isDrop())
			return false;
		if(support.isDataFlavorSupported(AnimationTreeTransferHandler.HierarchicalDataFlavor)) {
			support.setDropAction(LINK);
			return true;
		} else
			return false;
	}
	@Override
	public boolean importData(TransferSupport support) {
		Transferable t = support.getTransferable();
		try {
			HierarchicalData[] data = (HierarchicalData[]) t.getTransferData(AnimationTreeTransferHandler.HierarchicalDataFlavor);
			if(support.getComponent() == queue) {
				JList.DropLocation loc = (JList.DropLocation) support.getDropLocation();
				int dropIndex = loc.getIndex();
				for(Object obj:data)
					queue.receiveObj(obj,dropIndex++);
			}
			else {
				for(Object obj:data)
					queue.receiveObj(obj, queue.getListSize());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}