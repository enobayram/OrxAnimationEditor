package orxanimeditor.data.v1;

public class EditorDataChangeListener implements AnimationListener,
		AnimationSetListener, FrameListener {
	
	EditorData data;
	
	public EditorDataChangeListener(EditorData data) {
		this.data=data;
	}

	@Override
	public void dataLoaded() {
		data.dataChangedSinceLastSave=true;
	}

	@Override
	public void frameAdded(Animation parent, Frame frame) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void frameRemoved(Animation parent, Frame frame) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void frameEdited(Frame frame) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void frameMoved(Animation oldParent, Frame frame) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationSetAdded(AnimationSet set) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationSetRemoved(AnimationSet set) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationSetModified(AnimationSet set) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationAdded(Animation animation) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationRemoved(Animation animation) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationEdited(Animation animation) {
		data.dataChangedSinceLastSave=true;

	}

	@Override
	public void animationMoved(Animation animation) {
		data.dataChangedSinceLastSave=true;

	}

}
