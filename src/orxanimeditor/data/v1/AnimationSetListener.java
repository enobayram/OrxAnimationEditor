package orxanimeditor.data.v1;

public interface AnimationSetListener extends DataLoadListener{
	void animationSetAdded(AnimationSet set);
	void animationSetRemoved(AnimationSet set);
	void animationSetModified(AnimationSet set);
}
