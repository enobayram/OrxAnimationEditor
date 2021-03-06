package orxanimeditor.ui.animationseteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import orxanimeditor.data.v1.Animation;
import orxanimeditor.data.v1.AnimationSet;
import orxanimeditor.data.v1.AnimationSet.Link;
import orxanimeditor.data.v1.AnimationSet.SetSpecificAnimationData;
import orxanimeditor.data.v1.AnimationSetListener;
import orxanimeditor.ui.AnimationReceiver;
import orxanimeditor.ui.SlidingView;
import orxanimeditor.ui.animationmanager.AnimationTreeTransferHandler;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class AnimationSetViewer extends SlidingView implements MouseListener, AnimationReceiver {
	
	final int ANIMATIONRADIUS = 30;
	final int CONNECTIONDOTRADIUS = 3;

	AnimationSet set;
	EditorMainWindow editor;
	
	Animation selectedAnimation = null;
	Link 	  selectedLink		= null;
	
	public AnimationSetViewer(EditorMainWindow editor, AnimationSet set) {
		super(false,0.25);
		this.editor = editor;
		this.set = set;
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(moveAnimationListener);
		setFocusable(true);
		getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "DeleteSelected");
		getActionMap().put("DeleteSelected", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				deleteAnimation(selectedAnimation);
				deleteLink(selectedLink);
			}
		});
		
		setTransferHandler(new AnimationSetViewerTransferHandler());
	}

	public void addAnimation(Animation chosen) {
		if(set.containsAnimation(chosen)) return;
		else {
			set.addAnimation(chosen);
		}
	}
		@Override
	public void paintContent(Graphics2D g) {
		for(Animation animation: set.getAnimations()) {
			if(animation==selectedAnimation) g.setColor(Color.BLUE);
			else g.setColor(Color.BLACK);
			Point center = getCenter(animation);
			g.drawOval(center.x-ANIMATIONRADIUS, center.y-ANIMATIONRADIUS, 2*ANIMATIONRADIUS, 2*ANIMATIONRADIUS);
			g.drawString(animation.getName(), center.x-ANIMATIONRADIUS, center.y-5);
		}
		for(Link link: set.getLinks()) {
			g.setColor(Color.BLACK);
			drawLink(g, link);
		}
		if(selectedLink!=null) {
			g.setColor(Color.BLUE);
			drawLink(g, selectedLink);
		}
		
	}
	
	void drawLink(Graphics g, Link link) {
		Color ovalColor = g.getColor();
		if(link.getProperty()==Link.IMMEDIATE_PROPERTY)
			ovalColor = Color.RED;
		if(link.getSource()!= link.getDestination()) {
			Point sourceCenter = getCenter(link.getSource());
			Point destinationCenter = getCenter(link.getDestination());
			Point bias = biasVector(sourceCenter, destinationCenter);
			int sx = sourceCenter.x+bias.x, sy = sourceCenter.y+bias.y;
			int dx = destinationCenter.x-bias.x, dy = destinationCenter.y-bias.y;
			g.drawLine(sx,sy ,dx ,dy );
			g.setColor(ovalColor);
			g.fillOval(dx-CONNECTIONDOTRADIUS, dy-CONNECTIONDOTRADIUS, 2*CONNECTIONDOTRADIUS, 2*CONNECTIONDOTRADIUS); 
		} else {
			Point center = getCenter(link.getSource());
			double theta = Math.atan2(center.y-getHeight()/2, center.x-getWidth()/2);
			Point start = circumferenceOnAngle(center, theta-0.3);
			Point end = circumferenceOnAngle(center, theta+0.3);
			Point arcCenter = new Point((start.x+end.x)/2, (start.y+end.y)/2);
			int arcRadius = (int) arcCenter.distance(end);
			int thetaDeg = (int) (-theta*180/Math.PI);
			g.drawArc(arcCenter.x-arcRadius, arcCenter.y-arcRadius, 2*arcRadius, 2*arcRadius, thetaDeg-90, 180);
			g.setColor(ovalColor);
			g.fillOval(end.x-CONNECTIONDOTRADIUS, end.y-CONNECTIONDOTRADIUS, 2*CONNECTIONDOTRADIUS, 2*CONNECTIONDOTRADIUS);
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
	public Dimension getMinimumSize() {
		int displayRadius = (int) getScatterRadius() + ANIMATIONRADIUS;
		return new Dimension(2*displayRadius, 2*displayRadius);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	@Override
	public void receiveAnimation(Animation animation) {
		addAnimation(animation);		
	}

	
	private Point getCenter(Animation animation) {
		if(set.containsSetSpecificAnimationData(animation)) {
			SetSpecificAnimationData animationData = set.getSetSpecificAnimationData(animation);
			if(animationData.getCenter()!=null) 
				return animationData.getCenter();
		}
		
		return getDefaultCenter(animation);
	}
	
	private Point getDefaultCenter(Animation animation) {
		return centerOfCircle(set.indexOfAnimation(animation));
	}


	Point circumferenceOnAngle(Point center, double theta) {
		return new Point(center.x+(int)(Math.cos(theta)*ANIMATIONRADIUS), center.y+(int)(Math.sin(theta)*ANIMATIONRADIUS));
	}
	
	Point centerOfCircle(int index) {
		double r = getScatterRadius();
		double theta = 2*Math.PI*index/set.getAnimationCount();
		Point relativeCenter = new Point((int)(r*Math.cos(theta)), (int) (r*Math.sin(theta)));
		Point center = new Point(relativeCenter.x, relativeCenter.y);
		return center;
	}
	
	double getScatterRadius() {
		if(set.getAnimationCount()<2) return 0;
		return 2*ANIMATIONRADIUS/Math.sin(2*Math.PI/(2*set.getAnimationCount()));
	}
	@Override
	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(selectedAnimation == null) {
				selectedAnimation = pickAnimation(screenToWorld(e.getPoint()));
				selectedLink      = null;
			} else {
				Animation otherAnimation = pickAnimation(screenToWorld(e.getPoint()));
				if(otherAnimation!=null) {
					selectedLink = set.getOrCreateLink(selectedAnimation,otherAnimation);
					selectedAnimation = null;
				} else {
					selectedAnimation = null;
				}
			}
		}
		if(e.getButton() == MouseEvent.BUTTON2){
			selectedAnimation = null;
			selectedLink      = null;
		}
		repaint(20);
	}
	private Animation pickAnimation(Point point) {
		Animation[] animations = set.getAnimations();
		for(int ai=0; ai<animations.length; ai++) {
			Animation animation = animations[ai];
			if(getCenter(animation).distance(point)<ANIMATIONRADIUS) {
				return animation;
			}
		}
		return null;
	}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}

	MouseMotionAdapter moveAnimationListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) == MouseEvent.BUTTON3_DOWN_MASK) {
				Animation draggingAnimation = pickAnimation(screenToWorld(e.getPoint()));
				if ( draggingAnimation != null ){
					SetSpecificAnimationData selectedData;
					if(!set.containsSetSpecificAnimationData(draggingAnimation)) {
						selectedData = set.createSetSpecificAnimationData(draggingAnimation);
					} else {
						selectedData = set.getSetSpecificAnimationData(draggingAnimation);
					}
					selectedData.setCenter(screenToWorld(e.getPoint()));
					repaint(20);
				}
			}
		}
	};

	public void deleteAnimation(Animation animation) {
		if(animation!=null) {
			set.removeAnimation(animation);
			if(animation==selectedAnimation) selectedAnimation = null;
			if(selectedLink!=null && selectedLink.isConnectedTo(animation)) selectedLink = null; 
		}
	}
	public void deleteLink(Link link) {
		if(link!=null) {
			set.removeLink(link);
			if(link==selectedLink) selectedLink=null;
		}
	}
}

class AnimationSetViewerTransferHandler extends TransferHandler {
	@Override
	public boolean canImport(TransferSupport support) {
		Transferable t = support.getTransferable();
		if(!support.isDrop())
			return false;
		if(support.isDataFlavorSupported(AnimationTreeTransferHandler.AnimationFlavor)) {
			support.setDropAction(LINK);
			return true;
		} else
			return false;
	}
	@Override
	public boolean importData(TransferSupport support) {
		Transferable t = support.getTransferable();
		try {
			Animation[] animations = (Animation[]) t.getTransferData(AnimationTreeTransferHandler.AnimationFlavor);
			AnimationReceiver rec = (AnimationReceiver) support.getComponent();
			for(Animation animation: animations)
				rec.receiveAnimation(animation);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}