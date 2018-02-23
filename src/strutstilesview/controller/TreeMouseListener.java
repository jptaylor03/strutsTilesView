package strutstilesview.controller;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import strutstilesview.Utilities;
import strutstilesview.model.SimpleTreeNode;
import strutstilesview.util.MiscUtils;
import strutstilesview.views.StrutsTilesView;


/**
 * Respond to mouse clicks on nodes w/in the Tree component(s)
 * on each tab of the Viewer.
 */
public class TreeMouseListener extends MouseAdapter {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
	private boolean doubleClick;
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("controller");
	
	/**
	 * Viewer instance.
	 */
	private StrutsTilesView viewer;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public TreeMouseListener() {}
	public TreeMouseListener(StrutsTilesView viewer) {
		this.viewer = viewer;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public StrutsTilesView getViewer() {
		return this.viewer;
	}
	public void setViewer(StrutsTilesView viewer) {
		this.viewer = viewer;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Respond to a 'mouseDoubleClick' event.
	 * <p>
	 *  NOTE: Delegates actual logic to {@link #mouseUp(MouseEvent)}.
	 * </p>
	 */
	@Override
	public void mouseDoubleClick(MouseEvent event) {
		doubleClick = true;
		this.mouseUp(event);
	}
	
	/**
	 * Respond to a 'mouseUp' event.
	 * <ul>
	 *  NOTE: Upon each 'mousePressed' event, the number of clicks (and Shift key) are evaluated...
	 *  <li>        [Single-click] == Nothing happens.</li>
	 *  <li>        [Double-click] == Attempt to open the corresponding file/folder ONLY if the node is a leaf.</li>
	 *  <li>[Shift]+[Double-click] == Attempt to open the corresponding file/folder.</li>
	 * </ul>
	 * 
	 * @param event MouseEvent that has occurred on the component.
	 */
	@Override
	public void mouseUp(MouseEvent event) {
		if (doubleClick == true) {
			Utilities.changeCursor(viewer, true);
			Tree source = (Tree)event.getSource();
			if (source.getSelectionCount() > 0) {
				TreeItem[] selectedTreeItems = source.getSelection();
//				ViewerConfig vc = StrutsTilesView.getViewerConfig();
				for (TreeItem selectedTreeItem : selectedTreeItems) {
					SimpleTreeNode treeNode = (SimpleTreeNode)selectedTreeItem.getData();
					if (treeNode.isLeaf() || ((event.stateMask & SWT.SHIFT) == SWT.SHIFT)) {
						if (StringUtils.isNotEmpty(treeNode.getLinkTarget())) {
							String linkTarget = treeNode.getLinkTarget();
							// Create temp file (for resources)
							if (!new File(linkTarget).exists()) {
								linkTarget = MiscUtils.resourceToTempFile(StrutsTilesView.class, linkTarget);
							}
							linkTarget = linkTarget.replaceAll("/", "\\\\");
							if ("folder".equals(treeNode.getCategory())) {
								openExternalFileInOS(linkTarget);
							} else {
								openExternalFileInEditor(linkTarget);
							}
							logger.info("Attempting to open: " + linkTarget);
						}
					}
				}
			}
			Utilities.changeCursor(viewer, false);
		}
		// Reset double-click status
		doubleClick = false;
	}

	private void openExternalFileInOS(String linkTarget) {
		String command = "explorer /root," + " " + "\"" + linkTarget + "\"";
		try {
			Runtime.getRuntime().exec(command);
		//	Process p = Runtime.getRuntime().exec(command);
		//	p.waitFor(); // NOTE: If text editor not already open, waitFor() hangs indefinitely
		//	logger.debug("Exit value: " + p.exitValue());
		//	// Remove temp file (for resources)
		//	if (!linkTarget.equals(treeNode.getLinkTarget())) {
		//		new File(linkTarget).delete();
		//	}
		} catch (Throwable t) {
			logger.error(t);
		}
	}
	
	private void openExternalFileInEditor(String linkTarget) {
		File fileToOpen = new File(linkTarget);
		 
		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IPath path = new Path(linkTarget);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		//	IEditorDescriptor desc = PlatformUI.getWorkbench().
		//	        getEditorRegistry().getDefaultEditor(fileToOpen.getName());
			try {
				page.openEditor(new FileEditorInput(file), linkTarget);
			} catch (PartInitException e) {
				logger.error(e);
			}
		} else {
		    //Do something if the file does not exist
		}
	}

}
