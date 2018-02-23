package strutstilesview.model;

import java.util.Set;

/**
 * Container for this view's details.
 * 
 * @author tayloj10
 */
public class ViewerDetails {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	private ViewerAppInfo appInfo;
	private ViewerConfig viewerConfig;
	private Set<String> missingResources;

	/******************
	 * Constructor(s) *
	 ******************/
	
	private ViewerDetails() {}
	private static ViewerDetails instance;
	public static synchronized ViewerDetails getInstance() {
		if (instance == null) {
			instance = new ViewerDetails();
		}
		return instance;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public ViewerAppInfo getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(ViewerAppInfo appInfo) {
		this.appInfo = appInfo;
	}
	public ViewerConfig getViewerConfig() {
		return viewerConfig;
	}
	public void setViewerConfig(ViewerConfig viewerConfig) {
		this.viewerConfig = viewerConfig;
	}
	public Set<String> getMissingResources() {
		return missingResources;
	}
	public void setMissingResources(Set<String> missingResources) {
		this.missingResources = missingResources;
	}

}
