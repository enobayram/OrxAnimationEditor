package orxanimeditor.ui.animationviewer;

import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.sun.org.apache.xml.internal.utils.StopParseException;

import orxanimeditor.animation.Frame;

public class ContentProvider {
	FrameSequence sequence;
	FrameDisplay  display;
	
	Timer		timer = new Timer();
	Point		accumulatedOffset = new Point(0, 0);
	int nextFrameIndex;
	
	boolean enabled = false;
	
	public void enable() {
		enabled = true;
		restart();
	}

	public void disable() {
		enabled = false;
		stop();
	}
	
	private void stop() {
		timer.cancel();
		timer = new Timer();		
	}

	public ContentProvider(FrameSequence sequence, FrameDisplay display) {
		this.display = display;
		this.sequence = sequence;
	}
	Frame getCurrentFrame() {
		return null;
	}
	
	private void setupNextFrame() {
		timer.schedule(new TimerTask() { public void run() { 
				SwingUtilities.invokeLater(new Runnable() { public void run() {
					if(!enabled) return;
					if(sequence.getFrameCount()==0) return;
					if(nextFrameIndex>=sequence.getFrameCount()-1) 
						restart();
					else {
						nextFrameIndex++;
						Frame frame = sequence.getFrame(nextFrameIndex);
						Point offset = frame.getOffset();
						accumulatedOffset.x += offset.x; accumulatedOffset.y += offset.y;
						display.display(frame, accumulatedOffset);
						setupNextFrame();
					}
			}});
		}}, sequence.getFrameDelay(nextFrameIndex));
	}
	
	public void restart() {
		if(!enabled) return;
		timer.cancel();
		timer = new Timer();
		nextFrameIndex = 0;
		accumulatedOffset.x = 0; accumulatedOffset.y = 0;
		if(sequence.getFrameCount()>0) {
			Frame frame = sequence.getFrame(nextFrameIndex);
			accumulatedOffset = frame.getOffset();
			setupNextFrame();
		}
		pushFrame();
	}
	
	public void pushFrame() {
		if(sequence.getFrameCount()>0)
			display.display(sequence.getFrame(nextFrameIndex), accumulatedOffset);
		else
			display.clear();
	}
}
