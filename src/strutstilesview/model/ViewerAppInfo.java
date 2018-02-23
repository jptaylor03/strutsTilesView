package strutstilesview.model;

/**
 * Container for this application's basic information.
 * 
 * @author tayloj10
 */
public class ViewerAppInfo {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String appName          = "Struts/Tiles View";
	private String appVersion       = "2.04";
	private String appBuildDate     = "11/06/2017";
	private String appAuthorContact = "james.p.taylor@jfs.ohio.gov";
	private String appIcon          = "struts.png";

	/******************
	 * Constructor(s) *
	 ******************/
	
	private ViewerAppInfo(){ }

	private static ViewerAppInfo instance;
	public static synchronized ViewerAppInfo getInstance() {
		if (instance == null) {
			instance = new ViewerAppInfo();
		}
		return instance;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getAppBuildDate() {
		return appBuildDate;
	}
	public void setAppBuildDate(String appBuildDate) {
		this.appBuildDate = appBuildDate;
	}
	public String getAppAuthorContact() {
		return appAuthorContact;
	}
	public void setAppAuthorContact(String appAuthorContact) {
		this.appAuthorContact = appAuthorContact;
	}
	public String getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

}
