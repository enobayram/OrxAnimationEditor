package orxanimeditor.data.v1;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AnimationSet implements Serializable{
	private static final long serialVersionUID = 6664178554050675892L;
	
	private String name;
	private LinkedList<Animation> animations = new LinkedList<Animation>();
	private LinkedList<Link> 	  links 	 = new LinkedList<Link>();
	private Map<Animation, SetSpecificAnimationData> setSpecificAnimationData;
	
	protected transient EditorData editorData;
	
	public AnimationSet(String name) {
		this.setName(name);
		init();
	}
	
	public void addAnimation(Animation animation) {
		animations.add(animation);
		editorData.fireAnimationSetModified(this);
	}
	
	public boolean containsAnimation(Animation animation) {
		return animations.contains(animation);
	}
	
	public int indexOfAnimation(Animation animation) {
		return animations.indexOf(animation);
	}
	
	public int getAnimationCount() {
		return animations.size();
	}
	
	public void init() {
		if(setSpecificAnimationData == null)
			setSpecificAnimationData = new HashMap<Animation, SetSpecificAnimationData>();		
	}
	
	public Link getOrCreateLink(Animation source, Animation destination) {
		for(Link l:getLinks())
			if(l.source==source && l.destination == destination)
				return l;
		Link newLink = new Link(source,destination);
		addLink(newLink);
		return newLink;
	}
	
	private void addLink(Link link) {
		links.add(link);
		editorData.fireAnimationSetModified(this);
	}
	
	public void removeLink(Link link) {
		links.remove(link);
		editorData.fireAnimationSetModified(this);
	}
	
	public boolean contains(Link link) {
		for(Link l: getLinks()) {
			if(l.source==link.source && l.destination == link.destination) return true;
		}
		return false;
	}
	
	public void removeAnimation(Animation animation) {
		animations.remove(animation);
		LinkedList<Link> toRemove = new LinkedList<Link>();
		for(Link l:getLinks()) {
			if(animation == l.source || animation == l.destination) toRemove.add(l);
		}
		links.removeAll(toRemove);
		editorData.fireAnimationSetModified(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Animation[] getAnimations() {
		return animations.toArray(new Animation[animations.size()]);
	}

	public Link[] getLinks() {
		return links.toArray(new Link[links.size()]);
	}

	public SetSpecificAnimationData getSetSpecificAnimationData(Animation animation) {
		return setSpecificAnimationData.get(animation);
	}
	
	public boolean containsSetSpecificAnimationData(Animation animation) {
		return setSpecificAnimationData.containsKey(animation);
	}
	
	public class Link implements Serializable {
		private static final long serialVersionUID = -2250408601770835844L;
		private Animation source;
		private Animation destination;
		protected Link(Animation source, Animation destination) {
			this.setSource(source); 
			this.setDestination(destination);
		}
		public String getName() {
			if(source == destination) return source.getName()+"loop";
			else return source.getName() + "To" + destination.getName();
		}
		public Animation getSource() {
			return source;
		}
		public void setSource(Animation source) {
			this.source = source;
			editorData.fireAnimationSetModified(AnimationSet.this);
		}
		public Animation getDestination() {
			return destination;
		}
		public void setDestination(Animation destination) {
			this.destination = destination;
			editorData.fireAnimationSetModified(AnimationSet.this);
		}
		public boolean isConnectedTo(Animation animation) {
			return (animation==source || animation==destination);
		}
	}
	
	public class SetSpecificAnimationData implements Serializable {
		private static final long serialVersionUID = -4754354627090679467L;
		private Point center;
		protected SetSpecificAnimationData() {
			
		}
		public Point getCenter() {
			return center;
		}
		public void setCenter(Point center) {
			this.center = center;
			editorData.fireAnimationSetModified(AnimationSet.this);
		}
	}
	
	public SetSpecificAnimationData createSetSpecificAnimationData(Animation animation) {
		SetSpecificAnimationData newdata = new SetSpecificAnimationData();
		setSpecificAnimationData.put(animation, newdata);
		return newdata;
	}
}
