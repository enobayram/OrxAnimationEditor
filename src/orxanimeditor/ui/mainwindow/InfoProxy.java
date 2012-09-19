package orxanimeditor.ui.mainwindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class InfoProxy extends MouseAdapter {
	EditorMainWindow editor;
	String infoText;
	public InfoProxy(EditorMainWindow editor) {
		this.editor = editor;
	}
	public void setInfo(String text) {
		infoText = text;
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		pushInfoText();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		clearInfoText();
	}
	private void pushInfoText() {
		editor.setInfoText(infoText);
	}
	private void clearInfoText() {
		editor.setInfoText(null);
	}
}
