package strutstilesview.views;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

public class MyContentViewer extends ContentViewer {

	private Composite parent = null;
	private TabFolder child = null;

	public MyContentViewer(Composite parent, TabFolder child) {
		this.parent = parent;
		this.child = child;
	}

	@Override
	public Control getControl() {
		return this.child;
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(child.getSelection());
	}

	@Override
	public void refresh() {
		child.redraw();
	}

	@Override
	public void setSelection(ISelection selection, boolean selected) {
		super.setSelection(selection);
	}

}
