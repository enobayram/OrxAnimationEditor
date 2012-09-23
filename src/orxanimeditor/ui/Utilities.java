package orxanimeditor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Utilities {
	public static void drawCheckerPattern(Graphics g_, int checkerSize) {
		Graphics2D g = (Graphics2D) g_;
		g.setColor(new Color(150,150,200));
		Rectangle clipBounds = g.getClipBounds();
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		
		g.setColor(Color.GRAY);
		for(int i = floor(clipBounds.x,checkerSize); i<clipBounds.width+clipBounds.x; i+=checkerSize)
			for(int j= floor(clipBounds.y,checkerSize); j<clipBounds.height + clipBounds.y; j+=checkerSize) 
				if(((i+j)/checkerSize)%2==0)
				g.fillRect(i, j, checkerSize, checkerSize);
	}
	
	private static int floor(int val, int base) {
		return val/base*base;
	}
}
