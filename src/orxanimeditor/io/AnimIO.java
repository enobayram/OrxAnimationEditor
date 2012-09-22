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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.MutableTreeNode;

import orxanimeditor.data.Animation;
import orxanimeditor.data.AnimationSet;
import orxanimeditor.data.EditorData;
import orxanimeditor.data.Frame;
import orxanimeditor.data.AnimationSet.Link;
import orxanimeditor.data.Project.RelativeFile;
import orxanimeditor.ui.mainwindow.EditorMainWindow;

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
			ExportDiagnoser diagnoser = new ExportDiagnoser(fileOut);
			streamData(data, diagnoser);
			if(!diagnoser.isSuccessful())
				JOptionPane.showMessageDialog(editor, diagnoser.getDiagnosis(),"Export Problems",JOptionPane.WARNING_MESSAGE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
		
	private static void streamData(EditorData data, ExportDiagnoser d) {
		for(AnimationSet set: data.animationSets) {
			exportAnimationSet(d,set);
		}
		for(Animation animation: data.getAnimations()) {
			exportAnimation(d,animation);
			for(Frame frame:animation.getFrames()) {
				exportFrame(d,frame,data.project.getTargetFolder());
			}
		}
	}

	private static void exportAnimationSet(ExportDiagnoser d, AnimationSet set) {
		d.printSection(set.name);
		if(set.animations.size()>0) {
			String key = "AnimationList";
			String value = "";
			for(int ai=0; ai<set.animations.size(); ai++) {
				Animation animation = set.animations.get(ai);
				value+=animation.getName();
				if(ai!=set.animations.size()-1) value+="#";
			}
			d.printKeyValue(key, value);
		}
		d.printEmptyLine();
		if(set.links.size()>0) {
			String key = "LinkList";
			String value = "";
			for(int li = 0; li<set.links.size(); li++) {
				Link link = set.links.get(li);
				value+=link.getName();
				if(li!=set.links.size()-1) value+="#";
			}
			d.printKeyValue(key, value);
		}
		d.printEmptyLine();
		for(Link link: set.links) exportLink(d,link);
		
	}

	private static void exportLink(ExportDiagnoser d, Link link) {
		//Change -]
		d.printSection(link.getName());
		d.printKeyValue("Source",link.getSource().getName());
		d.printKeyValue("Destination",link.getDestination().getName());
	}

	private static void exportFrame(ExportDiagnoser d, Frame f, File baseDirectory) {
		Rectangle rect = f.properRectangle();
		Point     pivot = f.getPivot();
		d.printSection(f.getName());
		RelativeFile relativeImageFile = f.getImageFile();
		if(relativeImageFile!=null) {
			File imageFile = relativeImageFile.getAbsoluteFile();
			File imagePath = new File("");
			try {
				imagePath = getRelativeFile(imageFile, baseDirectory);
				d.printKeyValue("Texture", imagePath.toString());
			} catch (IOException e) {
				d.reportExternalError("There was a problem while exporting the texture of "+f.getName());
				e.printStackTrace();
			}
		} else d.reportExternalError(f.getName() + " has no texture");
		if(rect!=null) {
			d.printKeyValue("TextureCorner","("+rect.x+    ", "+rect.y+     ", 0)");
			d.printKeyValue("TextureSize","("+rect.width+", "+rect.height+", 0)");
		} else d.reportExternalError(f.getName() + " does not have a rectangle defined");
		if(pivot!=null) {
			pivot.x -= rect.x;
			pivot.y -= rect.y;
			d.printKeyValue("Pivot","("+pivot.x+   ", "+pivot.y+    ", 0)");
		} else  d.reportExternalError(f.getName() + " does not have a pivot defined");
		
		if(f.getFlipX() || f.getFlipY()) {
			String flip = "";
			if(f.getFlipX()) flip+="x";
			if(f.getFlipY()) flip+="y";
			d.printKeyValue("Flip",flip);
		}
			

	}

	private static void exportAnimation(ExportDiagnoser d,
			Animation animation) {
		d.printSection(animation.getName());
		d.printKeyValue("DefaultKeyDuration",""+animation.getDefaultKeyDuration());
		Frame[] frames = animation.getFrames();
		for(int fi=0; fi<frames.length; fi++) {
			Frame f = frames[fi];
			d.printKeyValue("KeyData"+(fi+1),f.getName());
			if(f.getKeyDuration()>0)
				d.printKeyValue("KeyDuration"+(fi+1),""+f.getKeyDuration());
		}
		double accummulatedTime = 0;
		int accumulatedEventCount = 1;
		for(int fi=0; fi<frames.length; fi++) {
			Frame f = frames[fi];
			Point offset = f.getOffset();
			if(offset.x!=0) {
				d.printKeyValue("KeyEventName"+accumulatedEventCount,"AR1");
				d.printKeyValue("KeyEventTime"+accumulatedEventCount,""+accummulatedTime);
				d.printKeyValue("KeyEventValue"+accumulatedEventCount,""+offset.x);
				accumulatedEventCount++; }
			if(offset.y!=0) {
				d.printKeyValue("KeyEventName"+accumulatedEventCount,"AR2");
				d.printKeyValue("KeyEventTime"+accumulatedEventCount,""+accummulatedTime);
				d.printKeyValue("KeyEventValue"+accumulatedEventCount,""+offset.y);
				accumulatedEventCount++;
			}
			accummulatedTime += f.getFinalFrameDuration();
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
