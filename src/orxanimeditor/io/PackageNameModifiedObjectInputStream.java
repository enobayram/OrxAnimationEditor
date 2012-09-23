package orxanimeditor.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PackageNameModifiedObjectInputStream extends ObjectInputStream {
// I use this class to recover old project files when the package name for the editor data class is changed
	
	public PackageNameModifiedObjectInputStream(InputStream in) throws IOException {
		super(in);
	}
	
	Pattern pattern = Pattern.compile("orxanimeditor\\.animation\\.(.*)");
	
	@Override
	protected java.io.ObjectStreamClass readClassDescriptor() 
	        throws IOException, ClassNotFoundException {
	    ObjectStreamClass desc = super.readClassDescriptor();
	    Matcher matcher = pattern.matcher(desc.getName());
	    if(matcher.matches()) {
	    	System.out.println("Converting package name for: " +matcher.group(1));
	    	String className = "orxanimeditor.data.v1."+matcher.group(1);
	        return ObjectStreamClass.lookup(Class.forName(className));	    	
	    }
	    return desc;
	};


}
