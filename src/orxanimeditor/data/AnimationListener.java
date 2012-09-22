package orxanimeditor.data;

public interface AnimationListener extends DataLoadListener{
	public void animationAdded(Animation animation);
	public void animationRemoved(Animation animation);
	public void animationEdited(Animation animation);
	public void animationMoved(Animation animation);
}

