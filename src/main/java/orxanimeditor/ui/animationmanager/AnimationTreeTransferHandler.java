package orxanimeditor.ui.animationmanager;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.EditorData;
import orxanimeditor.data.v1.Frame;
import orxanimeditor.data.v1.HierarchicalData;

public class AnimationTreeTransferHandler extends TransferHandler {
	
	@Override
	public int getSourceActions(JComponent c) {
		return COPY | MOVE | LINK;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		if(c instanceof AnimationTree) {
			AnimationTree tree = (AnimationTree) c;
			TransferableHierarchicalData transferable = new TransferableHierarchicalData(tree.getSelectedObjects());
			if(transferable.isPureAnimations())
				setDragImageIfSupported(AnimationManager.animationIcon.getImage());
			else if(transferable.isPureFrames())
				setDragImageIfSupported(AnimationManager.frameIcon.getImage());
			else
				setDragImageIfSupported(null);
			return transferable;
		} else
			return null;
	}
	
	public void setDragImageIfSupported(Image image) {
		try {
			Method m = getClass().getMethod("setDragImage", Image.class);
			m.invoke(this, image);
		} catch (Exception e) {
			return; // setDragImage is not available
		}
	}

	@Override
	public Icon getVisualRepresentation(Transferable t) {
		System.out.println("called");
		if(t instanceof TransferableHierarchicalData) {
			TransferableHierarchicalData thd = (TransferableHierarchicalData) t;
			if(thd.isPureAnimations()) {
				return AnimationManager.animationIcon;
			} else if (thd.isPureFrames()) {
				return AnimationManager.frameIcon;
			}
		}
		return super.getVisualRepresentation(t);
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
/*		if(action  == MOVE) {
			try {
				HierarchicalData[] toRemove = (HierarchicalData[]) data.getTransferData(ORXflavor);
				for(HierarchicalData datum: toRemove) datum.remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
	}

	@Override
	public boolean canImport(TransferSupport support) {
		JTree.DropLocation loc = (JTree.DropLocation) support.getDropLocation();
		Object parent = loc.getPath().getLastPathComponent();
		if(support.getUserDropAction() == LINK)
			support.setDropAction(COPY);
		if(parent instanceof HierarchicalData)
			return support.getTransferable().isDataFlavorSupported(FrameFlavor);
		else if(parent instanceof EditorData)
			return support.getTransferable().isDataFlavorSupported(AnimationFlavor);
		else
			return false;
	};
	
	@Override
	public boolean importData(TransferSupport support) {
		if(!support.isDrop()) return false;
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
				if(support.getDropAction() == COPY)
					for(Frame frame:frames) ((Animation) parent).addFrame(frame.clone(),index++);
				else if (support.getDropAction() == MOVE) {
					index--;
					for(Frame frame:frames) index = frame.move(parent,index);										
				}
				return true;
			} catch (Exception e) {
					e.printStackTrace();
					return false;
			}
		}
		else if(parent instanceof EditorData) {
			try {
				Animation[] animations = (Animation[]) support.getTransferable().getTransferData(AnimationFlavor);
				if(support.getDropAction() == COPY)
					for(Animation animation: animations) ((EditorData) parent).addAnimation(animation.clone(),index);
				else if (support.getDropAction() == MOVE) {
					index--;
					for(Animation animation: animations) index = animation.move(null,index);
				}
				return true;
			} catch (Exception e) {
			e.printStackTrace();
			return false;
			}
		}
		else return false;
	}

	public static String getLocalMimeType(Class cl) {
		return DataFlavor.javaJVMLocalObjectMimeType+"; class=\"" + cl.getName()+"\"";
	}
	
	public static final DataFlavor HierarchicalDataFlavor = new DataFlavor(getLocalMimeType(HierarchicalData[].class), "ORX Animation Editor data");
	public static final DataFlavor FrameFlavor = new DataFlavor(getLocalMimeType(Frame[].class), "Collection of frames");
	public static final DataFlavor AnimationFlavor = new DataFlavor(getLocalMimeType(Animation[].class), "Collection of animations");
	
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
			} else if (flavor.equals(HierarchicalDataFlavor)) {
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
			return new DataFlavor[] {AnimationFlavor, FrameFlavor, HierarchicalDataFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if(flavor.equals(HierarchicalDataFlavor) || flavor.equals(DataFlavor.stringFlavor))
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