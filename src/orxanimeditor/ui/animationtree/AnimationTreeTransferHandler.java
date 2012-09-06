package orxanimeditor.ui.animationtree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.lang.reflect.Array;

import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import orxanimeditor.animation.HierarchicalData;

public class AnimationTreeTransferHandler extends TransferHandler {
/*	@Override
	public int getSourceActions(JComponent c) {
		System.out.println("getting source actions");
		return COPY_OR_MOVE;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		System.out.println("creating transferable");
		AnimationTree tree = (AnimationTree) c;
		HierarchicalData[] selectedObjects = tree.getSelectedObjects();
		return new DataHandler(new TransferData(selectedObjects), TransferData.class.getSimpleName());
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		System.out.println("export done");
		if(action == MOVE)
			try {
				HierarchicalData[] objects = (HierarchicalData[]) data.getTransferData(new DataFlavor(TransferData.class.getSimpleName()));
				System.out.println("Just moved objects:"+ objects);
			} catch (Exception e){ 
				e.printStackTrace();
			}
			
	}
	
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		System.out.println("can import");
		return super.canImport(comp, transferFlavors);
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		System.out.println("can import");
		return true;
	}
	@Override
	public boolean importData(JComponent comp, Transferable t) {
		System.out.println("can import");
		return super.importData(comp, t);
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		System.out.println("importing data");
		try {
			Object data = support.getTransferable().getTransferData(new DataFlavor(TransferData.class.getSimpleName()));
			System.out.println("importing:"+data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	class TransferData {
		HierarchicalData [] data;
		public TransferData(HierarchicalData[] data) {
			this.data = data;
		}
	}*/
	
	@Override
	public int getSourceActions(JComponent c) {
		System.out.println("getting source actions");
		return super.getSourceActions(c);
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		System.out.println("creating transferable");
			return super.createTransferable(c);
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		System.out.println("importing data");
		return super.importData(support);
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		System.out.println("can import");
		return super.canImport(support);
	};
	
	@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			System.out.println("export done");
			super.exportDone(source, data, action);
		}
}
