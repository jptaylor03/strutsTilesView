package strutstilesview.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

import strutstilesview.Utilities;
import strutstilesview.views.StrutsTilesView;


/**
 * Respond to combo-box selection changes in the "Group By" component(s)
 * on each tab of the Viewer.
 */
public class GroupByListener extends SelectionAdapter {

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
	
	public GroupByListener() {}
	public GroupByListener(StrutsTilesView viewer) {
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
	 * Respond to an event related to a combo-box selection change.
	 * <p>
	 *  NOTE: Upon each event a call to {@link Viewer#groupBy(int, int)}
	 *        is triggered to automatically re-group the corresponding tabs output.
	 * </p>
	 * 
	 * @param event SelectionEvent that has occurred on the component.
	 * @see Viewer#groupBy(int, int)
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		Utilities.changeCursor(viewer, true);
		Combo source = (Combo)event.getSource();
		int x = Integer.parseInt(""+source.getData());
		int y = ((Combo)StrutsTilesView.getPanelInfoElement(x, StrutsTilesView.INFO_GROUP_BY)).getSelectionIndex();
		viewer.groupBy(x, y);
		Utilities.changeCursor(viewer, false);
	}

}
