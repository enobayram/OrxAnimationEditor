package orxanimeditor.ui.animationviewer;

import java.awt.Dimension;
import java.awt.datatransfer.Transferable;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.HierarchicalData;
import orxanimeditor.ui.AnimationReceiver;
import orxanimeditor.ui.AnimationViewer;
import orxanimeditor.ui.animationtree.AnimationTreeTransferHandler;

public class AnimationQueue extends JList implements FrameSequence{
	DefaultListModel model;
	ContentProvider provider;
	AnimationViewer viewer;
	public AnimationQueue(final AnimationViewer viewer) {
		super(new DefaultListModel());
		this.viewer = viewer;
		model = (DefaultListModel) getModel();
		setMinimumSize(new Dimension(100, 100));
		setTransferHandler(new AnimationQueueTransferHandler());
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
	}

	private void queueModified() {
		viewer.queueModified();
		provider.restart();	
	}

	public void receiveObj(Object obj) {
		model.addElement(obj);
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

}

class AnimationQueueTransferHandler extends TransferHandler {
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
			AnimationQueue rec = (AnimationQueue) support.getComponent();
			for(Object obj:data)
				rec.receiveObj(obj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}