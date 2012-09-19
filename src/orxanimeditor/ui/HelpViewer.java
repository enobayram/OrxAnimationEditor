package orxanimeditor.ui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import orxanimeditor.ui.mainwindow.EditorMainWindow;

public class HelpViewer extends JDialog implements HyperlinkListener {
	EditorMainWindow editor;
	JEditorPane		helpContents;
	JScrollPane		helpView;
	
	final String	helpFile = "help/index.html";

	public HelpViewer(EditorMainWindow editor) {
		super(editor,"Help Viewer");
		this.editor = editor;
		setModal(true);
		
		helpContents = new JTextPane();
		helpContents.setEditable(false);
		helpContents.setContentType("text/html");
		try {
			File helpInFile = new File(helpFile);
			if(!helpInFile.exists()) {
				throw new IOException();
			}			
			helpContents.setPage(helpInFile.toURI().toURL());
		} catch (IOException e) {
			try {
				URL helpInJar = ClassLoader.getSystemResource(helpFile);
				helpContents.setPage(helpInJar);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		}

		helpContents.addHyperlinkListener(this);
		
//		helpContents.setPreferredSize(new Dimension(800,600));
		
		helpView = new JScrollPane(helpContents);
		helpView.setPreferredSize(new Dimension(800, 600));
		
		getContentPane().add(helpView);
		
		pack();
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if(e.getURL().sameFile(helpContents.getPage()))
				helpContents.scrollToReference(e.getURL().getRef());
			else
				try {
					helpContents.setPage(e.getURL());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
	}
}
