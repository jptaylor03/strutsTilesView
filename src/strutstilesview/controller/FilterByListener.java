package strutstilesview.controller;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import strutstilesview.Utilities;
import strutstilesview.views.StrutsTilesView;


/**
 * Respond to keys being pressed/released/typed in the "Filter By" component(s)
 * on each tab of the Viewer.
 */
public class FilterByListener implements KeyListener {
	
	/***************
	 * Constant(s) *
	 ***************/
	
	private static final int[] CHARACTERS_TO_SUPPRESS = { '(', ')', '{', '}', '[', ']', '<', '>' };
	
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
	
	public FilterByListener() {}
	public FilterByListener(StrutsTilesView viewer) {
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
	 * Respond to a 'keyPressed' event.
	 * <p>
	 *  NOTE: Aside from potential character suppression,
	 *        currently nothing is done on 'keyPressed', instead,
	 *        a response is only generated on 'keyReleased'. 
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see #keyReleased(KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		this.checkToSuppressCharacter(event);
	}
	
	/**
	 * Respond to a 'keyReleased' event.
	 * <p>
	 *  NOTE: Upon each 'keyReleased' a call to {@link Viewer#groupBy(int, int)}
	 *        is triggered to automatically re-filter the corresponding tabs output.
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see Viewer#groupBy(int, int)
	 */
	@Override
	public void keyReleased(KeyEvent event) {
		if (!checkToSuppressCharacter(event)) {
			// (Allow character and) Proceed with processing
			Utilities.changeCursor(viewer, true);
			Text source = (Text)event.getSource();
			int x = Integer.parseInt(""+source.getData());
			int y = ((Combo)StrutsTilesView.getPanelInfoElement(x, StrutsTilesView.INFO_GROUP_BY)).getSelectionIndex();
			viewer.groupBy(x, y);
			Utilities.changeCursor(viewer, false);
		}
	}
	
	/**
	 * Respond to a 'keyTyped' event.
	 * <p>
	 *  NOTE: Aside from potential character suppression,
	 *        currently nothing is done on 'keyTyped', instead,
	 *        a response is only generated on 'keyReleased'. 
	 * </p>
	 * 
	 * @param event KeyEvent that has occurred on the component.
	 * @see #keyReleased(KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {
		checkToSuppressCharacter(event);
	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
	/**
	 * Determine whether to suppress the current character.
	 * 
	 * @param event KeyEvent to evaluate.
	 * @return Boolean indicating whether the character is being suppressed.
	 */
	private boolean checkToSuppressCharacter(KeyEvent event) {
		boolean suppressed = false;
		if (ArrayUtils.contains(CHARACTERS_TO_SUPPRESS, event.keyCode)) {
			// Suppress certain characters
			event.doit = false;
			suppressed = true;
		}
		return suppressed;
	}
}
