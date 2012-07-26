package orxanimeditor.animation;

import java.io.Serializable;
import java.util.LinkedList;

public class AnimationSet implements Serializable{
	private static final long serialVersionUID = 6664178554050675892L;
	
	public String name;
	public LinkedList<Animation> animations = new LinkedList<>();
	public LinkedList<Link> 	  links 	 = new LinkedList<>();
	
	public AnimationSet(String name) {
		this.name = name;
	}
	
	static class Link {
		Animation source;
		Animation destination;
		Link(Animation source, Animation destination) {this.source = source; this.destination = destination;}
	}
}
