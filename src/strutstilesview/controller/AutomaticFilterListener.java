package strutstilesview.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import strutstilesview.Utilities;
import strutstilesview.views.StrutsTilesView;

public class AutomaticFilterListener extends SelectionAdapter {

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

	/**
	 * (Filter By) Label instance.
	 */
	private Label filterByLabel;

	/**
	 * (Filter By) Text instance.
	 */
	private Text filterBy;
	
	/******************
	 * Constructor(s) *
	 ******************/
	
	public AutomaticFilterListener() {}
	public AutomaticFilterListener(StrutsTilesView viewer, Label filterByLabel, Text filterBy) {
		this.viewer = viewer;
		this.filterByLabel = filterByLabel;
		this.filterBy = filterBy;
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
	
	public Label getFilterByLabel() {
		return this.filterByLabel;
	}
	public void setFilterByLabel(Label filterByLabel) {
		this.filterByLabel = filterByLabel;
	}
	
	public Text getFilterBy() {
		return this.filterBy;
	}
	public void setFilterBy(Text filterBy) {
		this.filterBy = filterBy;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Respond to an event related to a checkbox selection change.
	 * 
	 * @param event SelectionEvent that has occurred on the component.
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		Utilities.changeCursor(viewer, true);
		Button btn = (Button)event.getSource();
		Font currentFont = filterBy.getFont();
		FontData currentFontData = currentFont.getFontData()[0];
		filterByLabel.setEnabled(!btn.getSelection());
		filterBy.setEnabled(!btn.getSelection());
		if (!btn.getSelection()) {
			filterBy.setText("");
			filterBy.setFont(new Font(currentFont.getDevice(), new FontData(currentFontData.getName(), currentFontData.getHeight(), SWT.NORMAL)));
		} else {
			filterBy.setText(Utilities.getLanguage("label.automatic.filter.default"));
			filterBy.setFont(new Font(currentFont.getDevice(), new FontData(currentFontData.getName(), currentFontData.getHeight(), SWT.ITALIC)));
		}
		Utilities.changeCursor(viewer, false);
	}

}
