package orxanimeditor.ui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class HelpViewer extends JDialog {
	EditorMainWindow editor;
	JScrollPane		helpView;
	JTextPane		helpContents;

	public HelpViewer(EditorMainWindow editor) {
		super(editor,"Help Viewer");
		this.editor = editor;
		setModal(true);
		
		helpContents = new JTextPane();
		StyledDocument doc = helpContents.getStyledDocument();
		
		 try {
			doc.insertString(0, "kjhlhlkjh",
					 doc.getStyle("HTML"));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		helpView = new JScrollPane(helpContents);
		helpView.setPreferredSize(new Dimension(800, 600));
		
		getContentPane().add(helpView);
		
		pack();
	}
}
