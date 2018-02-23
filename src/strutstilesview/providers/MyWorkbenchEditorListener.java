package strutstilesview.providers;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import strutstilesview.views.MyContentViewer;

public class MyWorkbenchEditorListener implements IPartListener2 {

	private ViewPart viewPart;
	private MyContentViewer viewer;
	
	public MyWorkbenchEditorListener(ViewPart viewPart, MyContentViewer viewer) {
		this.viewPart = viewPart;
		this.viewer = viewer;
	}
	
	/**
	 * Respond to change in focus (activation/deactivation) of Workbench editors.
	 * 
	 * @see IPartListener2#partActivated(IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(IWorkbenchPartReference workbenchPartReference) {
	//	viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
		viewer.setInput(viewPart.getSite().getPage().getInput());
//		int match = -1;
//		String activeFileName = ((IFile)arg0.getPart(false).getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class)).toString();
//		for (int index = 0; index < viewer.getList().getItems().length; index++) {
//			String itemName = viewer.getList().getItem(index);
//			if (itemName.equals(activeFileName)) {
//				match = index;
//				break;
//			}
//		}
//		if (match >= 0) {
//			viewer.setSelection(new StructuredSelection(viewer.getElementAt(match)));
//		}
	}

	/**
	 * @see IPartListener2#partBroughtToTop(IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference workbenchPartReference) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see IPartListener2#partClosed(IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(IWorkbenchPartReference workbenchPartReference) {
	//	viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
		viewer.setInput(viewPart.getSite().getPage().getInput());
	}

	/**
	 * @see IPartListener2#partDeactivated(IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference workbenchPartReference) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see IPartListener2#partHidden(IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(IWorkbenchPartReference workbenchPartReference) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see IPartListener2#partInputChanged(IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference workbenchPartReference) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see IPartListener2#partOpened(IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference workbenchPartReference) {
	//	viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
		viewer.setInput(viewPart.getSite().getPage().getInput());
	}

	/**
	 * @see IPartListener2#partVisible(IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference workbenchPartReference) {
		// TODO Auto-generated method stub
	}


}
