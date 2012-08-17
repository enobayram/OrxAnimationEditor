package orxanimeditor.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageManager {
	Map<File, BufferedImage> images = new HashMap<File, BufferedImage>();

	public BufferedImage openImage(File file) {
		BufferedImage result = null;
		result = images.get(file);
		if(result==null) {
			try {
				result = ImageIO.read(file);
				images.put(file, result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("File name:"+file.getPath());
			}
		}
		return result;
	}
}
