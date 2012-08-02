package orxanimeditor.animation;

import java.io.Serializable;
import java.util.LinkedList;

public class AnimationSet implements Serializable{
	private static final long serialVersionUID = 6664178554050675892L;
	
	public String name;
	public LinkedList<Animation> animations = new LinkedList<Animation>();
	public LinkedList<Link> 	  links 	 = new LinkedList<Link>();
	
	public AnimationSet(String name) {
		this.name = name;
	}
	
	public Link	getOrCreateLink(Animation source, Animation destination) {
		for(Link l:links)
			if(l.source==source && l.destination == destination)
				return l;
		Link newLink = new Link(source,destination);
		links.add(newLink);
		return newLink;
	}
	
	public boolean contains(Link link) {
		for(Link l: links) {
			if(l.source==link.source && l.destination == link.destination) return true;
		}
		return false;
	}
	
	public void removeAnimation(Animation animation) {
		animations.remove(animation);
		LinkedList<Link> toRemove = new LinkedList<Link>();
		for(Link l:links) {
			if(animation == l.source || animation == l.destination) toRemove.add(l);
		}
		links.removeAll(toRemove);
	}
	
	public static class Link implements Serializable {
		private static final long serialVersionUID = -2250408601770835844L;
		private Animation source;
		private Animation destination;
		Link(Animation source, Animation destination) {this.setSource(source); this.setDestination(destination);}
		public String getName() {
			if(source == destination) return source.getName()+"loop";
			else return source.getName() + "To" + destination.getName();
		}
		public Animation getSource() {
			return source;
		}
		public void setSource(Animation source) {
			this.source = source;
		}
		public Animation getDestination() {
			return destination;
		}
		public void setDestination(Animation destination) {
			this.destination = destination;
		}
	}
}
