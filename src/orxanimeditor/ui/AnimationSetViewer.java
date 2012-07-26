package orxanimeditor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Transient;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.AnimationSet;
import orxanimeditor.animation.AnimationSet.Link;

public class AnimationSetViewer extends JScrollPane implements MouseListener {
	
	final int ANIMATIONRADIUS = 30;
	final int CONNECTIONDOTRADIUS = 3;

	AnimationSet set;
	DisplayPanel display;
	EditorMainWindow editor;
	
	Animation selectedAnimation = null;
	Link 	  selectedLink		= null;
	
	public AnimationSetViewer(EditorMainWindow editor, AnimationSet set) {
		super();
		this.editor = editor;
		display = new DisplayPanel();
		setViewportView(display);
		//getViewport().setView(display);
		this.set = set;
		display.setBackground(Color.WHITE);
		display.addMouseListener(this);
	}
	public void addAnimation(Animation chosen) {
		if(set.animations.contains(chosen)) return;
		else {
			set.animations.add(chosen);
			editor.poke();
		}
	}
	
	class DisplayPanel extends JPanel {
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			for(int ai=0; ai<set.animations.size(); ai++) {
				Animation animation = set.animations.get(ai);
				if(animation==selectedAnimation) g.setColor(Color.BLUE);
				else g.setColor(Color.BLACK);
				Point center = centerOfCircle(ai);
				g.drawOval(center.x-ANIMATIONRADIUS, center.y-ANIMATIONRADIUS, 2*ANIMATIONRADIUS, 2*ANIMATIONRADIUS);
				g.drawString(animation.getName(), center.x-ANIMATIONRADIUS, center.y-5);
			}
			for(Link link: set.links) {
				if(link == selectedLink) g.setColor(Color.BLUE);
				else 	g.setColor(Color.BLACK);

				Point sourceCenter = centerOfCircle(set.animations.indexOf(link.getSource()));
				Point destinationCenter = centerOfCircle(set.animations.indexOf(link.getDestination()));
				Point bias = biasVector(sourceCenter, destinationCenter);
				int sx = sourceCenter.x+bias.x, sy = sourceCenter.y+bias.y;
				int dx = destinationCenter.x-bias.x, dy = destinationCenter.y-bias.y;
				g.drawLine(sx,sy ,dx ,dy );
				g.fillOval(dx-CONNECTIONDOTRADIUS, dy-CONNECTIONDOTRADIUS, 2*CONNECTIONDOTRADIUS, 2*CONNECTIONDOTRADIUS);
			}
		}
		
		Point biasVector(Point source, Point dest) {
			Point result = new Point();
			int dx = dest.x - source.x, dy = dest.y - source.y;
			double r = source.distance(dest);
			result.x = (int) (ANIMATIONRADIUS*dx/r);
			result.y = (int) (ANIMATIONRADIUS*dy/r);
			return result;
		}
		
		@Override
		@Transient
		public Dimension getMinimumSize() {
			int displayRadius = (int) getScatterRadius() + ANIMATIONRADIUS;
			return new Dimension(2*displayRadius, 2*displayRadius);
		}
		
		@Override
		@Transient
		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

	}
	
	Point centerOfCircle(int index) {
		double r = getScatterRadius();
		double theta = 2*Math.PI*index/set.animations.size();
		Point relativeCenter = new Point((int)(r*Math.cos(theta)), (int) (r*Math.sin(theta)));
		Point center = new Point(relativeCenter.x+getWidth()/2, relativeCenter.y+getHeight()/2);
		return center;
	}
	
	double getScatterRadius() {
		if(set.animations.size()<2) return 0;
		return 2*ANIMATIONRADIUS/Math.sin(2*Math.PI/(2*set.animations.size()));
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(selectedAnimation == null) {
			selectedAnimation = pickAnimation(e.getPoint());
			selectedLink      = null;
		} else {
			Animation otherAnimation = pickAnimation(e.getPoint());
			if(otherAnimation!=null && otherAnimation!=selectedAnimation) {
				selectedLink = set.getOrCreateLink(selectedAnimation,otherAnimation);
				selectedAnimation = null;
			} else {
				selectedAnimation = otherAnimation;
			}
		}
		
		display.repaint();
	}
	private Animation pickAnimation(Point point) {
		for(int ai=0; ai<set.animations.size(); ai++) {
			if(centerOfCircle(ai).distance(point)<ANIMATIONRADIUS) {
				return set.animations.get(ai);
			}
		}
		return null;
	}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	public void deleteAnimation() {
		if(selectedAnimation!=null) {
			set.removeAnimation(selectedAnimation);
			editor.poke();
		}
	}
	public void deleteLink() {
		if(selectedLink!=null) {
			set.links.remove(selectedLink);
			editor.poke();
		}
	}
}
