package orxanimeditor.ui.animationtree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;
import orxanimeditor.animation.HierarchicalData;
import orxanimeditor.ui.AnimationManager;

public class AnimationTreeTransferHandler extends TransferHandler {
	
	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		if(c instanceof AnimationTree) {
			AnimationTree tree = (AnimationTree) c;
			TransferableHierarchicalData transferable = new TransferableHierarchicalData(tree.getSelectedObjects());
			if(transferable.isPureAnimations())
				setDragImage(AnimationManager.animationIcon.getImage());
			else if(transferable.isPureFrames())
				setDragImage(AnimationManager.frameIcon.getImage());
			else
				setDragImage(null);
			return transferable;
		} else
			return null;
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if(action == MOVE) {
			try {
				HierarchicalData[] toRemove = (HierarchicalData[]) data.getTransferData(ORXflavor);
				for(HierarchicalData datum: toRemove) datum.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean canImport(TransferSupport support) {
		JTree.DropLocation loc = (JTree.DropLocation) support.getDropLocation();
		Object parent = loc.getPath().getLastPathComponent();
		if(parent instanceof HierarchicalData)
			return support.getTransferable().isDataFlavorSupported(FrameFlavor);
		else if(parent instanceof EditorData)
			return support.getTransferable().isDataFlavorSupported(AnimationFlavor);
		else
			return false;
	};
	
	@Override
	public boolean importData(TransferSupport support) {
		JTree.DropLocation loc = (JTree.DropLocation) support.getDropLocation();
		Object parent = loc.getPath().getLastPathComponent();
		int index = loc.getChildIndex();
		if(index==-1) { // We're dropping "ON" something
			if(parent instanceof Frame) {
				Frame frame = (Frame) parent;
				index = frame.getParent().getFrameIndex(frame)+1;
				parent = frame.getParent();
			} else if(parent instanceof Animation) {
				index = ((Animation) parent).getFrameCount();
			}
		}
		if(parent instanceof Animation) {
			try {
				Frame[] frames = (Frame[]) support.getTransferable().getTransferData(FrameFlavor);
				for(Frame frame:frames) ((Animation) parent).addFrame(frame.clone(),index++);
				return true;
			} catch (Exception e) {
					e.printStackTrace();
					return false;
			}
		}
		else if(parent instanceof EditorData) {
			try {
				Animation[] animations = (Animation[]) support.getTransferable().getTransferData(AnimationFlavor);
				for(Animation animation: animations) ((EditorData) parent).addAnimation(animation.clone(),index);
				return true;
			} catch (Exception e) {
			e.printStackTrace();
			return false;
			}
		}
		else return false;
	}

	DataFlavor ORXflavor = new DataFlavor(TransferableHierarchicalData.class, "ORX Animation Editor data");
	DataFlavor FrameFlavor = new DataFlavor(Frame[].class, "Collection of frames");
	DataFlavor AnimationFlavor = new DataFlavor(Animation[].class, "Collection of animations");
	
	class TransferableHierarchicalData implements Transferable {
		HierarchicalData[] data;
		public TransferableHierarchicalData(HierarchicalData[] data) {
			this.data = data;
		}
		
		boolean isPureAnimations() {
			for(HierarchicalData datum:data) if(!(datum instanceof Animation)) return false;
			return true;
		}
		
		boolean isPureFrames() {
			for(HierarchicalData datum:data) if(!(datum instanceof Frame)) return false;
			return true;
		}


		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if(flavor.equals(DataFlavor.stringFlavor)) {
				String result = "";
				for(HierarchicalData datum:data) result+=datum.toString() + " ";
				return result;
			} else if (flavor.equals(ORXflavor)) {
				return data;
			} else if (flavor.equals(AnimationFlavor)) {
				if(!isPureAnimations()) throw new UnsupportedFlavorException(flavor);
				return Arrays.asList(data).toArray(new Animation[data.length]);
			} else if (flavor.equals(FrameFlavor)) {
				if(!isPureFrames()) throw new UnsupportedFlavorException(flavor);
				return Arrays.asList(data).toArray(new Frame[data.length]);
			} else
				throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {AnimationFlavor, FrameFlavor, ORXflavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if(flavor.equals(ORXflavor) || flavor.equals(DataFlavor.stringFlavor))
				return true;
			else if(flavor.equals(AnimationFlavor) && isPureAnimations())
				return true;
			else if(flavor.equals(FrameFlavor) && isPureFrames())
				return true;
			else
				return false;
		}

	}
}