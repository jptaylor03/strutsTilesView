package strutstilesview.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

/**
 * Extend BaseWorkbenchContentProvider in order to control what is actual workbench content
 * (The default content for WorkbenchContentProvider is projects only).
 */
public class MyWorkbenchContentProvider extends BaseWorkbenchContentProvider /*implements IResourceChangeListener*/ {

	@SuppressWarnings("unused")
	private Viewer viewer = null;
	private IWorkspaceRoot input = null;

	/**
	 * @see BaseWorkbenchContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	//	super.dispose();
		this.viewer = null;
		this.input = null;
	}

	/**
	 * @see BaseWorkbenchContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		this.input = (IWorkspaceRoot)newInput;
	}

	/**
	 * @see BaseWorkbenchContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object element) {
		Object[] result = null;
		if (input != null) {
			IResource branch = (IResource)element;
			if (branch == null) branch = input.getWorkspace().getRoot();
			result = this.searchResourceTree(branch, IFile.class, null);
		}
		return result;
	}

	/**
	 * @see BaseWorkbenchContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object element) {
		Object[] result = null;
		if (input != null) {
			IResource branch = (IResource)element;
			if (branch == null) branch = input.getWorkspace().getRoot();
			result = this.searchResourceTree(branch, IFile.class, null);
		}
		return result;
	}

	/**
	 * @see BaseWorkbenchContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		Object result = null;
		if (input != null) {
			IResource branch = (IResource)element;
			if (branch == null) branch = input.getWorkspace().getRoot();
			IResource[] temp = this.searchResourceTree(branch, branch.getClass(), branch);
			if (temp != null && temp.length > 0) {
				result = temp[0].getParent();
			}
		}
		return result;
	}

	/**
	 * @see BaseWorkbenchContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		boolean result = false;
		if (input != null) {
			IResource branch = (IResource)element;
			if (branch == null) branch = input.getWorkspace().getRoot();
			IResource[] temp = this.searchResourceTree(branch, branch.getClass(), branch);
			if (temp != null && temp.length > 0) {
				if (temp[0] instanceof IContainer) {
					try {
						result = ((IContainer)temp[0]).members().length > 0;
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private IResource[] searchResourceTree(IResource resource, Class matchType, IResource matchContent) {
		List<IResource> temp = new ArrayList<IResource>();
		this.searchResourceTree(resource, IFile.class, null, null, temp);
		return temp.toArray(new IResource[0]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void searchResourceTree(IResource base, Class matchType, IResource matchContent, IResource current, List<IResource> result) {
		if (current == null) {
			current = base;
		}
		if ((base         != current) &&
			(matchType    == null || matchType.isAssignableFrom(current.getClass())) &&
			(matchContent == null || matchContent.getLocationURI().equals(current.getLocationURI()))) {
			result.add(current);
		}
		if (current instanceof IWorkspaceRoot) {
			for (IProject project : ((IWorkspaceRoot)current).getProjects()) {
				if (project.exists() && project.isOpen()) { // Exclude closed projects
					this.searchResourceTree(base, matchType, matchContent, project, result);
				}
			}
		} else if (current instanceof IContainer) {
			try {
				for (IResource member : ((IContainer)current).members()) {
					this.searchResourceTree(base, matchType, matchContent, member, result);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if (current instanceof IFile) {
			IFile file = (IFile)current;
			if (!file.exists() || !this.existsInActiveEditor(file)) { // Exclude unopened files
				result.remove(current);
			}
		}
	}

	/**
	 * Scan all active Eclipse Editors for the specified file.
	 * 
	 * @param file
	 * @return Boolean indicating whether any active Eclipse Editors contain the specified file.
	 */
	private boolean existsInActiveEditor(IFile file) {
		boolean result = false;
		if (file != null && file.exists()) {
			for (IEditorReference activeEditor : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {
				Object editorFile = null;
				try {
					editorFile = activeEditor.getEditorInput().getAdapter(IFile.class);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
				if (editorFile instanceof IFile && file.equals(editorFile)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

//	/**
//	 * Respond to workspace change events (specifically when an open files content changes).
//	 */
//	@Override
//	public void resourceChanged(IResourceChangeEvent event) {
//		if (event...)
//	}
}
