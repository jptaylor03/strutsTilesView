package strutstilesview.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import strutstilesview.Utilities;
import strutstilesview.model.AppState;
import strutstilesview.views.StrutsTilesView;


/**
 * Respond to button pressing of the "Reload Configuration" component
 * on the Configuration tab of the Viewer.
 */
public class ReloadConfigurationListener extends SelectionAdapter {
	
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
	
	public ReloadConfigurationListener() {}
	public ReloadConfigurationListener(StrutsTilesView viewer) {
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
	 * Respond to an 'actionPerformed' event related to a command button click.
	 * <p>
	 *  NOTE: Upon each 'actionPerformed' a call is made to set the {@link Viewer#appState}.
	 *        The {@link Viewer#main(String[])} method is designed to loop indefinitely
	 *        and respond to {@link Viewer#appState} changes.  Therefore, to respond to a
	 *        Reload Configuration button pressed event, this method only needs to update
	 *        the {@link Viewer#appState} to {@link AppState#RESTART}.
	 * </p>
	 * 
	 * @param event SelectionEvent that has occurred on the component.
	 * @see StrutsTilesView#appState
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		Utilities.changeCursor(viewer, true);
		StrutsTilesView.appState.setState(AppState.RESTART);
		Utilities.changeCursor(viewer, false);
	}

}
