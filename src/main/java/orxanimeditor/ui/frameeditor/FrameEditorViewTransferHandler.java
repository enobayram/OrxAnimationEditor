package orxanimeditor.ui.frameeditor;

import java.awt.datatransfer.Transferable;

import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.Frame;
import orxanimeditor.data.v1.HierarchicalData;
import orxanimeditor.data.v1.Project.RelativeFile;
import orxanimeditor.ui.animationmanager.AnimationTreeTransferHandler;
import orxanimeditor.ui.animationviewer.AnimationQueue;

class FrameEditorViewTransferHandler extends TransferHandler {
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
			FrameEditor rec = (FrameEditor) support.getComponent();
			for(Object obj:data)
				if(obj instanceof Animation) {
					Animation animation = (Animation) obj;
					for(Frame frame: animation.getFrames()) {
						rec.openImage(frame);
					}
				} else if(obj instanceof Frame) {
					rec.openImage((Frame) obj);
				}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
