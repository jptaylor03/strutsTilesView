package strutstilesview.controller;

import java.util.ArrayList;
import java.util.List;

import strutstilesview.model.SimpleTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import strutstilesview.util.TreeUtils;
import strutstilesview.views.StrutsTilesView;


/**
 * Respond to keys being pressed/released/type in the Tree component(s)
 * on each tab of the Viewer.
 */
public class TreeKeyListener extends KeyAdapter {
	
	/**********************
	 * Member variable(s) *
	 **********************/
	
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
	
	public TreeKeyListener() {}
	public TreeKeyListener(StrutsTilesView viewer) {
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
	 * Respond to a 'keyReleased' event.
	 * <ul>
	 *  NOTE: Upon each 'keyReleased' event, the pressed keys are evaluated...
	 *  <li>        [Control]+[C] == contents of selected node are copied to the clipboard.</li>
	 *  <li>[Shift]+[Control]+[C] == contents of selected node and all children are copied to the clipboard.</li>
	 * </ul>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see TreeUtils#traverseTreeNodes(SimpleTreeNode, StringBuffer)
	 */
	@Override
	public void keyReleased(KeyEvent event) {
		if ((event.stateMask & SWT.CTRL) == SWT.CTRL && (event.keyCode == 'c')) {
			Tree tree = (Tree)event.getSource();
			if (tree.getSelectionCount() > 0) {
				TreeItem[] selectedTreeItems = tree.getSelection();
				List<String> textData = new ArrayList<String>();
				for (TreeItem selectedTreeItem : selectedTreeItems) {
					SimpleTreeNode selectedNode = (SimpleTreeNode)selectedTreeItem.getData();
					String forClipboard = null;
					if ((event.stateMask & SWT.SHIFT) == SWT.SHIFT && (selectedNode.getChildCount() > 0)) {
						forClipboard = TreeUtils.traverseTreeNodes(selectedNode, null).toString();
					} else {
						forClipboard = selectedTreeItem.getText();
					}
					textData.add(forClipboard);
				}
				Clipboard clipboard = new Clipboard(tree.getDisplay());
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(textData.toArray(), new Transfer[]{ textTransfer });
			}
		}
	}

}
