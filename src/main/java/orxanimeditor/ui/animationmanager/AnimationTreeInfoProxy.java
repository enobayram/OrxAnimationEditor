package orxanimeditor.ui.animationmanager;

import java.awt.event.KeyEvent;

import orxanimeditor.data.v1.Frame;
import orxanimeditor.ui.SelectionListener;
import orxanimeditor.ui.mainwindow.AreaInfoProxy;

public class AnimationTreeInfoProxy implements SelectionListener {
	AreaInfoProxy infoProxy;
	AnimationTree tree;
	public AnimationTreeInfoProxy(AnimationTree tree, AreaInfoProxy infoProxy) {
		this.tree = tree;
		this.infoProxy = infoProxy;
		tree.addMouseListener(infoProxy);
		tree.addSelectionListener(this);
		infoProxy.setInfo("");
	}
	
	@Override
	public void selectionChanged(Object selectedObject) {
		if(selectedObject instanceof Frame) {
			Frame frame = (Frame) selectedObject;
			if(frame.getImageFile() == null) {
				infoProxy.setInfo("The selected frame has no image attached, use the frame editor to attach an image.");
			} else {
				infoProxy.setInfo("Press "+ KeyEvent.getKeyText(AnimationManager.JUMPTOFRAMEIMAGEKEY) 
						+ " to open the selected frame's image.");
			}
		} else {
			infoProxy.setInfo("");
		}
	}	
}
