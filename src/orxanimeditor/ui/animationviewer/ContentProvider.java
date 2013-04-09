package orxanimeditor.ui.animationviewer;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sun.org.apache.xml.internal.utils.StopParseException;

import orxanimeditor.data.v1.Frame;

public class ContentProvider implements ActionListener {
	FrameSequence sequence;
	FrameDisplay  display;
	
	Timer		timer = new Timer(1,this);
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
		timer.stop();		
	}

	public ContentProvider(FrameSequence sequence, FrameDisplay display) {
		this.display = display;
		this.sequence = sequence;
		timer.setRepeats(false);
	}
	Frame getCurrentFrame() {
		return null;
	}
	
	private void setupNextFrame() {
		timer.setInitialDelay((int)sequence.getFrameDelay(nextFrameIndex));
		timer.restart(); // calls actionPerformed after the delay
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// Animation timer action
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
	}
	
	public void restart() {
		if(!enabled) return;
		timer.stop();
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
