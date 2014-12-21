package orxanimeditor.data.v1;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import orxanimeditor.io.AnimIO;

public class Project implements Serializable{
	private static final long serialVersionUID = -896630792781084533L;
	public File			 projectFile = null;
	public RelativeFile			 targetIni = null;
	public RelativeFile			 targetFolder = null;

	
	public class RelativeFile implements Serializable{

		private static final long serialVersionUID = 1541356514107556900L;
		File relativePath;
		public RelativeFile(File absoluteFile) {
			try {
				relativePath = AnimIO.getRelativeFile(absoluteFile, projectFile.getParentFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public File getAbsoluteFile() {
			return AppendFile(projectFile.getParentFile(), relativePath);
		}
		
		public File getRelativeFile() {
			return relativePath;
		}

		private File AppendFile(File base, File relative) {
			return new File(base.getAbsolutePath()+File.separator+relative.getPath());
		}
	}

	public RelativeFile getRelativeFile(File file) {
		return new RelativeFile(file);
	}
	
	public File getTargetFolder() {
		if(targetFolder!=null) return targetFolder.getAbsoluteFile();
		else return new File(targetIni.getAbsoluteFile().getParent());
	}

}
