package orxanimeditor.animation;

public interface AnimationListener {
	public void animationAdded(Animation animation);
	public void animationRemoved(Animation animation);
	public void animationEdited(Animation animation);
	public void animationMoved(Animation animation);
}

