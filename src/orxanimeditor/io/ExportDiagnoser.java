package orxanimeditor.io;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ExportDiagnoser {
	PrintStream p;
	String currentSection=null;
	ArrayList<String> sections = new ArrayList<String>();
	ArrayList<String> keys = new ArrayList<String>();
	ArrayList<String> faultySections = new ArrayList<String>();
	ArrayList<SectionKeyPair> faultyKeys = new ArrayList<SectionKeyPair>();
	ArrayList<String> externalErrors = new ArrayList<String>();
	
	public ExportDiagnoser(OutputStream os) {
		this.p=new PrintStream(os);
	}
	public void printSection(String section) {
		keys.clear();
		p.println("["+section+"]");
		if(sections.contains(section))
			faultySections.add(section);
		else
			sections.add(section);
		currentSection = section;
	}
	public void printKeyValue(String key, String value) {
		p.println(key + " = " + value);
		if(keys.contains(key))
			faultyKeys.add(new SectionKeyPair(currentSection, key));
		else
			keys.add(key);
	}
	public void printEmptyLine() {
		p.println();
	}
	
	public void reportExternalError(String error) {
		externalErrors.add(error);
	}
	
	public boolean isSuccessful() {
		return faultySections.isEmpty() && faultyKeys.isEmpty() && externalErrors.isEmpty();
	}
	
	public String getDiagnosis() {
		StringBuilder result = new StringBuilder();
		if(!faultySections.isEmpty()) {
			result.append("The following sections were written more than once:\n");
			for(String s: faultySections) result.append(s+"\n");
		}
		if(!faultyKeys.isEmpty()) {
			result.append("The following keys were written more than once in the same section:\n");
			for(SectionKeyPair p: faultyKeys) result.append(p.section+" : "+p.key+"\n");			
		}		
		if(!externalErrors.isEmpty()) {
			result.append("The following errors were also reported:\n");
			for(String s: externalErrors) result.append(s+"\n");						
		}
		return result.toString();
	}

}

class SectionKeyPair {
	String section;
	String key;
	SectionKeyPair(String section, String key) {
		this.section=section;
		this.key=key;
	}
}