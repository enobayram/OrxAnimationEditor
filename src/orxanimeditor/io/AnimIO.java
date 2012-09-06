package orxanimeditor.io;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.MutableTreeNode;

import orxanimeditor.animation.Animation;
import orxanimeditor.animation.AnimationSet;
import orxanimeditor.animation.AnimationSet.Link;
import orxanimeditor.animation.EditorData;
import orxanimeditor.animation.Frame;
import orxanimeditor.ui.EditorMainWindow;

public class AnimIO {
	public static void writeEditorData(EditorData data, File file) {
	      try
	      {
	         FileOutputStream fileOut =
	         new FileOutputStream(file);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(data);
	         out.close();
	          fileOut.close();
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	public static void readEditorData(File file, EditorData data) {
        try
        {
           FileInputStream fileIn = new FileInputStream(file);
           ObjectInputStream in = new ObjectInputStream(fileIn);
           EditorData newData = (EditorData) in.readObject();
           in.close();
           fileIn.close();
           data.acquireFromData(newData,file);
           return;
       }catch(IOException i)
       {
           i.printStackTrace();
       }catch(ClassNotFoundException c)
       {
           c.printStackTrace();
       }
	}
	
	public static void exportEditorData(EditorMainWindow editor, EditorData data, boolean append) {
		if(data.project.targetIni==null) {
			JOptionPane.showMessageDialog(editor, "Target .ini file is not set");
			return;
		}
        try {
			FileOutputStream fileOut = new FileOutputStream(data.project.targetIni.getAbsoluteFile(), append);
			streamData(data, fileOut);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
		
	private static void streamData(EditorData data, OutputStream os) {
		PrintStream p = new PrintStream(os);
		for(AnimationSet set: data.animationSets) {
			exportAnimationSet(p,set);
		}
		for(Animation animation: data.getAnimations()) {
			exportAnimation(p,animation);
			for(Frame frame:animation.getFrames()) {
				exportFrame(p,frame,data.project.getTargetFolder());
			}
		}
	}

	private static void exportAnimationSet(PrintStream p, AnimationSet set) {
		p.println("["+set.name+"]");
		if(set.animations.size()>0) {
			p.print("AnimationList = ");
			for(int ai=0; ai<set.animations.size(); ai++) {
				Animation animation = set.animations.get(ai);
				p.print(animation.getName());
				if(ai!=set.animations.size()-1) p.print("#");
			}
		}
		p.println();
		if(set.links.size()>0) {
			p.print("LinkList =");
			for(int li = 0; li<set.links.size(); li++) {
				Link link = set.links.get(li);
				p.print(link.getName());
				if(li!=set.links.size()-1) p.print("#");
			}
		}
		p.println();
		for(Link link: set.links) exportLink(p,link);
		
	}

	private static void exportLink(PrintStream p, Link link) {
		//Change -]
		p.println("["+link.getName()+"]");
		p.println("Source      = " + link.getSource().getName());
		p.println("Destination = " + link.getDestination().getName());
	}

	private static void exportFrame(PrintStream p, Frame f, File baseDirectory) {
		Rectangle rect = f.properRectangle();
		Point     pivot = (Point) f.getPivot().clone();
		pivot.x -= rect.x;
		pivot.y -= rect.y;
		File imageFile = f.getImageFile().getAbsoluteFile();
		File imagePath = new File("");
		try {
			imagePath = getRelativeFile(imageFile, baseDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			p.println('['+f.getName()+']');
			p.println("Texture           = " + imagePath);
			p.println("TextureCorner     = ("+rect.x+    ", "+rect.y+     ", 0)");
			p.println("TextureSize       = ("+rect.width+", "+rect.height+", 0)");
			p.println("Pivot             = ("+pivot.x+   ", "+pivot.y+    ", 0)");
		if(f.getFlipX() || f.getFlipY()) {
			String flip = "";
			if(f.getFlipX()) flip+="x";
			if(f.getFlipY()) flip+="y";
			p.println("Flip              = "+flip);
		}
			

	}

	private static void exportAnimation(PrintStream p,
			Animation animation) {
		p.println('['+animation.getName()+']');
		p.println("DefaultKeyDuration   = "+animation.getDefaultKeyDuration());
		Frame[] frames = animation.getFrames();
		for(int fi=0; fi<frames.length; fi++) {
			Frame f = frames[fi];
			p.println("KeyData"+(fi+1)+"    = "+ f.getName());
		}
	}

	 public static File getRelativeFile(File target, File base) throws IOException
	 {
	   String[] baseComponents = base.getCanonicalPath().split(Pattern.quote(File.separator));
	   String[] targetComponents = target.getCanonicalPath().split(Pattern.quote(File.separator));

	   // skip common components
	   int index = 0;
	   for (; index < targetComponents.length && index < baseComponents.length; ++index)
	   {
	     if (!targetComponents[index].equals(baseComponents[index]))
	     break;
	   }

	   StringBuilder result = new StringBuilder();
	   if (index != baseComponents.length)
	   {
	     // backtrack to base directory
	     for (int i = index; i < baseComponents.length; ++i)
	       result.append(".." + File.separator);
	   }
	   for (; index < targetComponents.length; ++index)
	     result.append(targetComponents[index] + File.separator);
	   if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\"))
	   {
	     // remove final path separator
	     result.delete(result.length() - File.separator.length(), result.length());
	   }
	   return new File(result.toString());
	 }
}
