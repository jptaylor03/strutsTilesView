package strutstilesview.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;

import strutstilesview.Utilities;
import strutstilesview.concurrent.ParseDocumentJob;
import strutstilesview.concurrent.ThreadPoolFactory;
import strutstilesview.controller.AutomaticFilterListener;
import strutstilesview.controller.FilterByListener;
import strutstilesview.controller.GroupByListener;
import strutstilesview.controller.ReloadConfigurationListener;
import strutstilesview.controller.TreeKeyListener;
import strutstilesview.controller.TreeMouseListener;
import strutstilesview.model.AppState;
import strutstilesview.model.NodeInfo;
import strutstilesview.model.SimpleTreeNode;
import strutstilesview.model.StrutsActionFormBean;
import strutstilesview.model.StrutsActionForward;
import strutstilesview.model.StrutsActionMapping;
import strutstilesview.model.TilesDefinition;
import strutstilesview.model.ViewerAppInfo;
import strutstilesview.model.ViewerConfig;
import strutstilesview.providers.MyWorkbenchContentProvider;
import strutstilesview.providers.MyWorkbenchEditorListener;
import strutstilesview.providers.MyWorkbenchLabelProvider;
import strutstilesview.util.MiscUtils;
import strutstilesview.util.MissingResources;
import strutstilesview.util.ModelUtils;
import strutstilesview.util.ReflectUtils;
import strutstilesview.util.TreeUtils;


/**
 * Implement view for the plugin.
 */
public class StrutsTilesView extends ViewPart /*implements ISelectionChangedListener*/ {

	/***************
	 * Constant(s) *
	 ***************/

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "strutstilesview.views.StrutsTilesView";

//	/**
//	 * Object array containing basic information about the application.
//	 */
//	public static final Object[] appInfo = {
//		"Struts/Tiles View"				// [0] == Application Name
//	,	"2.04"							// [1] == Application Version
//	,	"10/01/2016"					// [2] == Application Build Date
//	,	"james.p.taylor@jfs.ohio.gov"	// [3] == Application Author Contact
//	,	"struts.png"					// [4] == Application Icon
//	};
	public static final ViewerAppInfo appInfo = ViewerAppInfo.getInstance();

//	/**
//	 * Obtain formatted version of the application info.
//	 *
//	 * @return String containing application info formatted for display.
//	 */
//	public static String getAppInfoFormatted() {
//		return	"Name: "	+ appInfo[0] + "\n" +
//				"Version: "	+ appInfo[1] + "\n" +
//				"Build: "	+ appInfo[2] + "\n" +
//				"Author: "	+ appInfo[3] + "\n";
//	}

	/*
	 * Panels
	 */

	// TODO[jpt] Convert all INFO constants from 'int' to 'byte'.
	// Panel Fields
	public static final int INFO_ID            = 0;
	public static final int INFO_NAME          = 1;
	public static final int INFO_ICON          = 2;
	public static final int INFO_ROOT          = 3;
	public static final int INFO_TREE          = 4;
	public static final int INFO_FILTER_BY     = 5;
	public static final int INFO_GROUP_BY      = 6;
	public static final int INFO_RELOAD_CONFIG = 7;
	public static final int INFO_AUTO_FILTER   = 8;
	// Panel Types
	public static final int INFO_ID_SAFB           = 0;
	public static final int INFO_ID_SAF            = 1;
	public static final int INFO_ID_SAM            = 2;
	public static final int INFO_ID_TD             = 3;
	public static final int INFO_ID_VIEWER_CONFIG  = 4;
	public static final int INFO_ID_VIEWER_CONSOLE = 5;
	// Panels
	// TODO[jpt] Add a Struts Global Exceptions tab
	private static final Object[][] PANEL_INFO = {
	//		  [0]id                   [1]name (key)             [2]icon  [3]root  [4]tree  [5]filterBy  [6]groupBy  [7]loadCfg  [8]autoFltr
/*[0] */	{ "strutsActionForms"   , "tab.action.form.beans" , "safb ", null   , null   , null       , null      , null      , null } /*[0]*/
/*[1] */,	{ "strutsActionForwards", "tab.action.forwards"   , "saf"  , null   , null   , null       , null      , null      , null } /*[1]*/
/*[2] */,	{ "strutsActionMapping" , "tab.action.mappings"   , "sam"  , null   , null   , null       , null      , null      , null } /*[2]*/
/*[3] */,	{ "tilesDefinitions"    , "tab.tiles.definitions" , "td"   , null   , null   , null       , null      , null      , null } /*[3]*/
/*[4] */,	{ "viewerConfig"        , "tab.view.configuration", "xml"  , null   , null   , null       , null      , null      , null } /*[4]*/
/*[5] */,	{ "viewerConsole"       , "tab.view.console"      , "out"  , null   , null   , null       , null      , null      , null } /*[5]*/
	//		  [0]id                   [1]name (key)             [2]icon  [3]root  [4]tree  [5]filterBy  [6]groupBy  [7]loadCfg  [8]autoFltr
	};

	/*
	 * Group By
	 */

	// TODO[jpt] Convert all GROUP_BY constants from 'int' to 'byte'.
	// Struts Action Form Beans
	public static final int GROUP_BY_SAFB_BY_NAME               = 0; // Default
	public static final int GROUP_BY_SAFB_BY_TYPE               = 1;
	// Struts Action Forwards
	public static final int GROUP_BY_SAF_BY_NAME                = 0;
	public static final int GROUP_BY_SAF_BY_PATH                = 1;
	public static final int GROUP_BY_SAF_BY_UNIQUE              = 2; // Default
	// Struts Action Mappings
	public static final int GROUP_BY_SAM_BY_SAF_NAME            = 0;
	public static final int GROUP_BY_SAM_BY_SAF_PATH            = 1;
	public static final int GROUP_BY_SAM_BY_NAME                = 2;
	public static final int GROUP_BY_SAM_BY_PATH                = 3; // Default
	public static final int GROUP_BY_SAM_BY_TYPE                = 4;
	// Tiles Definitions
	public static final int GROUP_BY_TD_BY_ATTR_PAGE_WORK       = 0;
	public static final int GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE = 1;
	public static final int GROUP_BY_TD_BY_EXTENDS              = 2;
	public static final int GROUP_BY_TD_BY_NAME                 = 3; // Default

	/**********************
	 * Member variable(s) *
	 **********************/

	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("view");

	/**
	 * AppState instance for maintaining the state of the application.
	 */
	public static final AppState appState = new AppState();

	/**
	 * Map of command-line arguments (keyed by argument name).
	 */
	private static final Map<String, String> commandLineArgs = new HashMap<String, String>();

	/**
	 * Container for displaying all view content.
	 */
	private MyContentViewer viewer;

	/**
	 * IRunnableWithProgress instance (for displaying Progress Panel info).
	 */
	private static MyConfigurationLoadingRunnable job;

	/**
	 * Console (output) instance.
	 */
	private static StyledText console = null;
	public static StyledText getConsole() {
		if (console == null) {
			// Create the console output area and associate it with SysOut/SysErr
			console = (StyledText)PANEL_INFO[INFO_ID_VIEWER_CONSOLE][INFO_TREE]; //JTextArea(44, 88)
			//console.setPreferredSize(new Dimension(1024, 768));
			//console.setMinimumSize(new Dimension(800, 600));
			console.setEditable(false);
			//PrintStream stream = new PrintStream(new CustomOutputStream(console));
			//System.setOut(stream);
			//System.setErr(stream);
		}
		return console;
	}

	/**
	 * Handle to tabFolder.
	 */
	private TabFolder tabFolder;

	/*
	 * Handle(s) to action(s)
	 */
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/**
	 * Name of the primary configuration file for the application.
	 */
	private static String viewerConfigFileName = "viewer-config.xml";

	/**
	 * Path (and name) of the primary configuration file for the application.
	 */
	private static String viewerConfigFileTarget = "/resources/" + viewerConfigFileName;

	/**
	 * Container for the (parsed) xml configuration from the "viewer-config.xml" file.
	 */
	public static ViewerConfig viewerConfig = null;

	/*
	 * Special Indices
	 */

	/**
	 * Map of Struts Action Mappings keyed by Struts Action Forward Name.
	 *
	 * @see #parseSpecifiedFiles()
	 * @see #groupBy(int, int)
	 */
	private static final Map<String, SimpleTreeNode> samBySafName = new TreeMap<String, SimpleTreeNode>();

	/**
	 * Map of Struts Action Mappings keyed by Struts Action Forward Path.
	 *
	 * @see #parseSpecifiedFiles()
	 * @see #groupBy(int, int)
	 */
	private static final Map<String, SimpleTreeNode> samBySafPath = new TreeMap<String, SimpleTreeNode>();

	/******************
	 * Constructor(s) *
	 ******************/

	/**
	 * The constructor.
	 */
	public StrutsTilesView() {
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/

	public static ViewerConfig getViewerConfig() {
		return viewerConfig;
	}
//	public static void setViewerConfig(ViewerConfig viewerConfig) {
//		Viewer.viewerConfig = viewerConfig;
//	}

	public static Object[][] getPanelInfo() {
		return PANEL_INFO;
	}
//	public static void setPanelInfo(Object[][] panelInfo) {
//		PANEL_INFO = panelInfo;
//	}

	public static Object getPanelInfoElement(int infoField) {
		return PANEL_INFO[infoField];
	}
	public static void setPanelInfoElement(Object[] element, int infoField) {
		PANEL_INFO[infoField] = element;
	}

	public static Object getPanelInfoElement(int infoField, int infoType) {
		return PANEL_INFO[infoField][infoType];
	}
	public static void setPanelInfoElement(Object element, int infoField, int infoType) {
		PANEL_INFO[infoField][infoType] = element;
	}

	/********************
	 * Member method(s) *
	 ********************/

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		// Create/show the Progress Bar
		job = new MyConfigurationLoadingRunnable();
		boolean shouldFork = true;
		boolean isCancellable = false;
		try {
			getSite().getWorkbenchWindow().getWorkbench().getProgressService().run(shouldFork, isCancellable, (IRunnableWithProgress)job);
		} catch (Exception e) {
			logger.error("Error: Unable to schedule configuration loading job - " + e.getMessage(), e);
		}
		
//		while (!appState.is(AppState.SHUTDOWN)) {
//			if (appState.is(AppState.STARTUP) || appState.is(AppState.RESTART)) {
				Utilities.changeCursor(this, true);
				this.createAndShowGUI(parent);
				Utilities.changeCursor(this, false);
				appState.setState(AppState.ACTIVE);
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (Throwable t) {
//				logger.error(t);
//			}
//		}
//		appState.setState(AppState.INACTIVE);
	}

	/**
	 * [Re]Group the data contents of the tree
	 * on the specified 'panelIndex' by the specified 'groupByIndex'.
	 *
	 * @param panelIndex   Integer identifying the panel containing the tree to update.
	 * @param groupByIndex Integer identifying the type of group by to perform.
	 */
    @SuppressWarnings("unchecked")
	public void groupBy(int panelIndex, int groupByIndex) {
    	final String MISSING_GROUP_BY_KEY = "(undefined)";
    	String                      groupByKey     = null;
    	List<Object>                groupByList    = null;
    	Map<String, SimpleTreeNode> groupByDefault = (Map<String, SimpleTreeNode>)((SimpleTreeNode)PANEL_INFO[panelIndex][INFO_ROOT]).getUserObject();
    	Map<String, SimpleTreeNode> groupByResult  = new TreeMap<String, SimpleTreeNode>();
    	SimpleTreeNode              groupByNode[]  = { null, null };

		// Create new root node based on the old one
		SimpleTreeNode oldRoot = (SimpleTreeNode)PANEL_INFO[panelIndex][INFO_ROOT];
		SimpleTreeNode newRoot = new SimpleTreeNode("root", null, groupByResult, null, null, oldRoot.getInfo());

		switch (panelIndex) {
			case INFO_ID_VIEWER_CONFIG:
				// The default "aggregate" for this panel
				groupByResult.putAll(groupByDefault);
				break;
			case INFO_ID_SAFB:
				switch (groupByIndex) {
					case GROUP_BY_SAFB_BY_NAME: {
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
					}
					case GROUP_BY_SAFB_BY_TYPE: {
						for (Iterator<String> it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String               key   = (String)it.next();
							StrutsActionFormBean value = (StrutsActionFormBean)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							groupByKey     = StringUtils.defaultString(value.getType(), MISSING_GROUP_BY_KEY);
							groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
							if (groupByNode[0] == null) {
								groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
							}
							groupByList = (ArrayList<Object>)groupByNode[0].getUserObject();
							if (groupByList == null) {
								groupByList = new ArrayList<Object>();
							}
							groupByNode[1] = new SimpleTreeNode(value.getName(), groupByNode[0], value, "safb");
							if (!groupByList.contains(groupByNode[1])) {
								groupByList.add(groupByNode[1]);
							}
							groupByNode[0].setUserObject(groupByList);
							groupByResult.put(groupByKey, groupByNode[0]);
						}
						break;
					}
				}
				break;
			case INFO_ID_SAF:
				switch (groupByIndex) {
					case GROUP_BY_SAF_BY_NAME:
						groupByResult.putAll(samBySafName);
						break;
					case GROUP_BY_SAF_BY_PATH:
						groupByResult.putAll(samBySafPath);
						break;
					case GROUP_BY_SAF_BY_UNIQUE:
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
				}
				break;
			case INFO_ID_SAM:
				switch (groupByIndex) {
					case GROUP_BY_SAM_BY_SAF_NAME:
					case GROUP_BY_SAM_BY_SAF_PATH: {
						for (Iterator<String> it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String key = (String)it.next();
							StrutsActionMapping value = (StrutsActionMapping)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							Map actionForwards = value.getActionForwards();
							for (Iterator itSub = actionForwards.values().iterator(); itSub.hasNext();) {
								StrutsActionForward saf = (StrutsActionForward)((SimpleTreeNode)itSub.next()).getUserObject();
								switch (groupByIndex) {
									case GROUP_BY_SAM_BY_SAF_NAME:
										groupByKey = StringUtils.defaultString(saf.getName(), MISSING_GROUP_BY_KEY);
										break;
									case GROUP_BY_SAM_BY_SAF_PATH:
										groupByKey = StringUtils.defaultString(saf.getPath(), MISSING_GROUP_BY_KEY);
										break;
								}
								groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
								if (groupByNode[0] == null) {
									groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
								}
								groupByList = (ArrayList)groupByNode[0].getUserObject();
								if (groupByList == null) {
									groupByList = new ArrayList();
								}
								groupByNode[1] = new SimpleTreeNode(value.getPath(), groupByNode[0], value, "sam");
								if (!groupByList.contains(groupByNode[1])) {
									groupByList.add(groupByNode[1]);
								}
								groupByNode[0].setUserObject(groupByList);
								groupByResult.put(groupByKey, groupByNode[0]);
							}
						}
						break;
					}
					case GROUP_BY_SAM_BY_NAME:
					case GROUP_BY_SAM_BY_TYPE: {
						for (Iterator it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String key = (String)it.next();
							StrutsActionMapping value = (StrutsActionMapping)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							switch (groupByIndex) {
								case GROUP_BY_SAM_BY_NAME:
									groupByKey = StringUtils.defaultString(value.getName(), MISSING_GROUP_BY_KEY);
									break;
								case GROUP_BY_SAM_BY_TYPE:
									groupByKey = StringUtils.defaultString(value.getType(), MISSING_GROUP_BY_KEY);
									break;
							}
							groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
							if (groupByNode[0] == null) {
								groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
							}
							groupByList = (ArrayList)groupByNode[0].getUserObject();
							if (groupByList == null) {
								groupByList = new ArrayList();
							}
							groupByNode[1] = new SimpleTreeNode(value.getPath(), groupByNode[0], value, "sam");
							if (!groupByList.contains(groupByNode[1])) {
								groupByList.add(groupByNode[1]);
							}
							groupByNode[0].setUserObject(groupByList);
							groupByResult.put(groupByKey, groupByNode[0]);
						}
						break;
					}
					case GROUP_BY_SAM_BY_PATH: {
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
					}
				}
				break;
			case INFO_ID_TD:
				switch (groupByIndex) {
					case GROUP_BY_TD_BY_ATTR_PAGE_WORK:
					case GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE:
					case GROUP_BY_TD_BY_EXTENDS: {
						for (Iterator it = groupByDefault.keySet().iterator(); it.hasNext();) {
							String          key       = (String)it.next();
							TilesDefinition value     = (TilesDefinition)((SimpleTreeNode)groupByDefault.get(key)).getUserObject();
							String          attrValue = null;
							switch (groupByIndex) {
								case GROUP_BY_TD_BY_ATTR_PAGE_WORK:
									attrValue  = value.getAttributeValue(TilesDefinition.ATTR_PAGE_WORK);
									groupByKey = StringUtils.defaultString(attrValue, MISSING_GROUP_BY_KEY);
									break;
								case GROUP_BY_TD_BY_ATTR_PAGE_WORK_TITLE:
									attrValue  = value.getAttributeValue(TilesDefinition.ATTR_PAGE_WORK_TITLE);
									groupByKey = StringUtils.defaultString(attrValue, MISSING_GROUP_BY_KEY);
									break;
								case GROUP_BY_TD_BY_EXTENDS:
									groupByKey = StringUtils.defaultString((String)value.getExtends(), MISSING_GROUP_BY_KEY);
									break;
							}
							groupByNode[0] = (SimpleTreeNode)groupByResult.get(groupByKey);
							if (groupByNode[0] == null) {
								groupByNode[0] = new SimpleTreeNode(groupByKey, newRoot, null);
							}
							groupByList = (ArrayList)groupByNode[0].getUserObject();
							if (groupByList == null) {
								groupByList = new ArrayList();
							}
							groupByNode[1] = new SimpleTreeNode(value.getName(), groupByNode[0], value, "td");
							if (!groupByList.contains(groupByNode[1])) {
								groupByList.add(groupByNode[1]);
							}
							groupByNode[0].setUserObject(groupByList);
							groupByResult.put(groupByKey, groupByNode[0]);
						}
						break;
					}
					case GROUP_BY_TD_BY_NAME: {
						// The default "aggregate" for this panel
						groupByResult.putAll(groupByDefault);
						break;
					}
				}
				break;
		}

		if (!((Button)PANEL_INFO[panelIndex][INFO_AUTO_FILTER]).getSelection()) {
			this.filterByRegex(groupByResult, ((Text)PANEL_INFO[panelIndex][INFO_FILTER_BY]).getText());
		}

		// Update the appropriate Tree Model and force a refresh
		Tree tree = (Tree)PANEL_INFO[panelIndex][INFO_TREE];
		tree.removeAll();
	//	tree.setData(new SimpleTreeModel(newRoot));
		TreeUtils.populateTree(tree, newRoot);
	//	tree.invalidate();
    }

	private void createAndShowGUI(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NULL); //SWT.BORDER
		viewer = new MyContentViewer(parent, tabFolder); // SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
		viewer.setContentProvider(new MyWorkbenchContentProvider());
		viewer.setLabelProvider(new MyWorkbenchLabelProvider());
	//	viewer.setSorter(new MyWorkbenchNameSorter());
		viewer.setInput(getViewSite().getPage().getInput());
		getSite().setSelectionProvider(viewer);

		// Register editor part listener (workbench files opened/closed/selected)
		final IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
        workbenchWindow.getPartService().addPartListener(new MyWorkbenchEditorListener(this, viewer));

        // Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "strutsTilesView.viewer");

		// Create UI and add functionality
		job.setProgress(Utilities.getLanguage("loading.parsing.command.line.args"), 1);
		this.parseCommandLineArguments(Platform.getCommandLineArgs());
		job.setProgress(Utilities.getLanguage("loading.processing.base.configuration"), 2);
		this.parseViewerConfiguration();
		job.setProgress(Utilities.getLanguage("loading.processing.strutstiles.configuration"), 3);
		this.parseSpecifiedFiles();
		job.setProgress(Utilities.getLanguage("loading.initializing.display"), 90);
		this.prepareViewer(parent/*, tabFolder*/);
		job.setProgress(Utilities.getLanguage("loading.releasing.resources"), 94);
		ReflectUtils.releaseResources();
		job.setProgress(Utilities.getLanguage("loading.logging.missing.resources"), 95);
		MissingResources.logMissingResources();
		job.setProgress(Utilities.getLanguage("loading.creating.actions"), 96);
		this.makeActions();
		job.setProgress(Utilities.getLanguage("loading.creating.context.menus"), 97);
		this.hookContextMenu();
		job.setProgress(Utilities.getLanguage("loading.hooking.double.clicks"), 98);
		this.hookDoubleClickAction();
		job.setProgress(Utilities.getLanguage("loading.creating.action.bars"), 99);
		this.contributeToActionBars();
		job.setProgress(Utilities.getLanguage("loading.complete"), 100);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

//	/*********************************************
//	 * Respond to selection changes on this view *
//	 *********************************************/
//
//	/**
//	 * Respond to a selection change on this view.
//	 * 
//	 * @param event
//	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
//	 */
//	@Override
//	public void selectionChanged(SelectionChangedEvent event) {
//		String selection = event.getSelection().toString();
//	}

	/******************
	 * Helper Methods *
	 ******************/

	private void prepareViewer(Composite parent) {
        // Tab Folder
	//	tabFolder = new TabFolder(parent, SWT.NULL); //SWT.BORDER
		for (int tabIndex = 0; tabIndex < PANEL_INFO.length; tabIndex++) {
			if (PANEL_INFO[tabIndex][INFO_ID] != null) {
				if (StrutsTilesView.appState.is(AppState.STARTUP)) {
					GridLayout tabPanelLayout = new GridLayout();

					Composite tabPanel = new Composite(tabFolder, SWT.NULL); //SWT.BORDER
					tabPanel.setLayout(tabPanelLayout);

					TabItem tabItem = new TabItem(tabFolder, SWT.NULL); //SWT.NULL
					tabItem.setControl(tabPanel);
					
					createTabSpecificSubPanel(tabItem, tabPanel, tabIndex);
				} else /*if (StrutsTilesView.appState.is(AppState.RESTART))*/ {
					Combo groupBy = (Combo)PANEL_INFO[tabIndex][INFO_GROUP_BY];
					groupBy(tabIndex, groupBy.getSelectionIndex());
				}
			}
	    }
	}

	/**
	 * Obtain a map of parsed data based on the specified 'dom' and
	 * filtered by the specified 'tagName'.
	 *
	 * @param configFile String identifying the name of the xml file which was the source of the data.
	 * @param dom        Document containing the parsed xml data from the xml file.
	 * @param mapNames   String array containing the names of the maps into which to store the data.
	 * @param tagNames   String array containing the names of xml tags to focus on (ignoring all other nodes).
	 * @param parents    SimpleTreeNode array identifying the parents to associate with all resulting data elements.
	 * @return Map containing the corresponding data elements keyed accordingly.
	 */
//	private Map<String, Map<String, SimpleTreeNode>> parseDocument(String configFile, Document dom, String[] mapNames, String[] tagNames, SimpleTreeNode[] parents) {
	private Future<Map<String, Map<String, SimpleTreeNode>>> parseDocument(String configFile, Document dom, String[] mapNames, String[] tagNames, SimpleTreeNode[] parents) {
		Future<Map<String, Map<String, SimpleTreeNode>>> result = null;
//		Map<String, Map<String, SimpleTreeNode>> result = null;
		result = ThreadPoolFactory.getInstance().getThreadPool().submit(new ParseDocumentJob(configFile, dom, mapNames, tagNames, parents));
//		try {
//			result = new ParseDocumentJob(configFile, dom, mapNames, tagNames, parents).call();
//		} catch (Exception e) {
//			logger.error("Error: Failed to parse document - " + e.getMessage(), e);
//		}
		return result;
	}

	/**
	 * Create a tab-specific sub panel (one for each tab of the main Viewer panel).
	 * <ul>
	 *  NOTE: Currently, there are 6 tabs.  While the last 2 tabs are different, the
	 *        first 4 tabs are virtually identical to each other...
	 *  <li>[0] == Action Form Beans tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[1] == Action Forwards   tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[2] == Action Mappings   tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[3] == Tiles Definitions tab == A standard "Filter By" control and a "Group By" control w/ type-specific options</li>
	 *  <li>[4] == Viewer Config     tab == A "Reload" button instead of "Filter By" and "Group By" controls</li>
	 *  <li>[5] == Viewer Console    tab == A display of console output (System.out and System.err)</li>
	 * </ul>
	 *
	 * @param tabItem    TabItem of the target tab. 
	 * @param tabPanel   Composite container of the target tab.
	 * @param panelIndex Integer identifying the target tab for which this panel is destined.
	 * @return JPanel containing the newly created panel.
	 */
	private void createTabSpecificSubPanel(TabItem tabItem, Composite tabPanel, int tabIndex) {
		int addRibbonPanelToTab = 0; // [0] == None, [1] == Filter/Group, [2] == Reload View Configuration
		List<String> groupByChoices = null; // [0] == Default choice

		/*
		 * Basic Tab Info and Ribbon
		 */

		switch (tabIndex) {
			case 0: { // Action Form Beans
		        tabItem.setText(Utilities.getLanguage("tab.name.action.form.beans"));
		        tabItem.setToolTipText(Utilities.getLanguage("tab.tooltip.action.form.beans"));
		        tabItem.setImage(Utilities.getImage("formbean_obj.gif"));
		        addRibbonPanelToTab = 1;
		        groupByChoices = new ArrayList<String>();
		        groupByChoices.add(0, "group.by.form.bean.name"); // Default choice
		        groupByChoices.add("group.by.form.bean.name");
		        groupByChoices.add("group.by.form.bean.type");
				break;
			}
			case 1: { // Action Forwards
		        tabItem.setText(Utilities.getLanguage("tab.name.action.forwards"));
		        tabItem.setToolTipText(Utilities.getLanguage("tab.tooltip.action.forwards"));
		        tabItem.setImage(Utilities.getImage("forward_obj.gif"));
		        addRibbonPanelToTab = 1;
		        groupByChoices = new ArrayList<String>();
		        groupByChoices.add(0, "group.by.mapping.path.and.forward.name"); // Default choice
		        groupByChoices.add("group.by.forward.name");
		        groupByChoices.add("group.by.forward.path");
		        groupByChoices.add("group.by.mapping.path.and.forward.name");
		        groupByChoices.add("group.by.form.bean.name");
				break;
			}
			case 2: { // Action Mappings
		        tabItem.setText(Utilities.getLanguage("tab.name.action.mappings"));
		        tabItem.setToolTipText(Utilities.getLanguage("tab.tooltip.action.mappings"));
		        tabItem.setImage(Utilities.getImage("actionmapping_obj.gif"));
		        addRibbonPanelToTab = 1;
		        groupByChoices = new ArrayList<String>();
		        groupByChoices.add(0, "group.by.mapping.path"); // Default choice
		        groupByChoices.add("group.by.forward.name");
		        groupByChoices.add("group.by.forward.path");
		        groupByChoices.add("group.by.form.bean.name");
		        groupByChoices.add("group.by.mapping.path");
		        groupByChoices.add("group.by.mapping.type");
				break;
			}
			case 3: { // Tiles Definitions
		        tabItem.setText(Utilities.getLanguage("tab.name.tiles.definitions"));
		        tabItem.setToolTipText(Utilities.getLanguage("tab.tooltip.tiles.definitions"));
		        tabItem.setImage(Utilities.getImage("apache-tiles-logo.jpg"));
		        addRibbonPanelToTab = 1;
		        groupByChoices = new ArrayList<String>();
		        groupByChoices.add(0, "group.by.tiles.definition.name"); // Default choice
		        groupByChoices.add("group.by.view.file.name");
		        groupByChoices.add("group.by.view.page.title");
		        groupByChoices.add("group.by.tiles.definition.parent");
		        groupByChoices.add("group.by.tiles.definition.name");
				break;
			}
			case 4: { // Viewer Configuration
		        tabItem.setText(Utilities.getLanguage("tab.name.view.configuration"));
		        tabItem.setToolTipText(Utilities.getLanguage("tab.tooltip.view.configuration"));
		        tabItem.setImage(Utilities.getImage("xml_obj.jpg"));
		        addRibbonPanelToTab = 2;
				break;
			}
			case 5: { // Viewer Console
		        tabItem.setText(Utilities.getLanguage("tab.name.view.console"));
		        tabItem.setToolTipText(Utilities.getLanguage("tab.tooltip.view.console"));
		        tabItem.setImage(Utilities.getImage("console_obj.gif"));
		        addRibbonPanelToTab = 0;
				break;
			}
		}

		/*
		 * Adding Ribbon (when necessary)
		 */

		switch (addRibbonPanelToTab) {
			case 0: { // None
				break;
			}
			case 1: { // Filter/Group
				GridLayout filterGroupLayout = new GridLayout(5, false);
	
				Group filterGroup = new Group(tabPanel, SWT.NULL);
				filterGroup.setText(Utilities.getLanguage("label.filter.group"));
				filterGroup.setToolTipText(Utilities.getLanguage("tooltip.filter.group"));
				filterGroup.setLayout(filterGroupLayout);
			    GridDataFactory.fillDefaults().grab(true, false).hint(400, 30).applyTo(filterGroup);
	
				final Button filterAutomatic = new Button(filterGroup, SWT.CHECK);
				filterAutomatic.setText(Utilities.getLanguage("label.automatic.filter"));
				filterAutomatic.setToolTipText(Utilities.getLanguage("tooltip.automatic.filter"));
				filterAutomatic.setData(tabIndex);
				filterAutomatic.setEnabled(true);
				filterAutomatic.setSelection(true);
				// ..Store a reference to the checkBox's handle (for easy reference w/in Listeners)
				PANEL_INFO[tabIndex][INFO_AUTO_FILTER] = filterAutomatic;
	
				final Label filterByLabel = new Label(filterGroup, SWT.RIGHT);
				filterByLabel.setText(Utilities.getLanguage("label.filter.by"));
				filterByLabel.setToolTipText(Utilities.getLanguage("tooltip.filter.by"));
				filterByLabel.setEnabled(false);
	
				final Text filterBy = new Text(filterGroup, SWT.SINGLE | SWT.BORDER);
				filterBy.setData(tabIndex);
				filterBy.setEnabled(false);
				filterBy.setText(Utilities.getLanguage("label.automatic.filter.default") /*+ new String(new char[50]).replace("\0", " ")*/);
				filterBy.setToolTipText(Utilities.getLanguage("tooltip.filter.by"));
			    GridDataFactory.fillDefaults().grab(true, true).hint(100, filterGroup.getSize().y).applyTo(filterBy);
				final Font currentFont = filterBy.getFont();
				final FontData currentFontData = currentFont.getFontData()[0];
				filterBy.setFont(new Font(currentFont.getDevice(), new FontData(currentFontData.getName(), currentFontData.getHeight(), SWT.ITALIC)));
				filterBy.addKeyListener(new FilterByListener(this));
				// ..Store a reference to the filter's handle (for easy reference w/in Listeners)
				PANEL_INFO[tabIndex][INFO_FILTER_BY] = filterBy;
	
				final Label groupByLabel = new Label(filterGroup, SWT.RIGHT);
				groupByLabel.setText(Utilities.getLanguage("label.group.by"));
				groupByLabel.setToolTipText(Utilities.getLanguage("tooltip.group.by"));
				groupByLabel.setEnabled(false);
	
				final Combo groupBy = new Combo(filterGroup, SWT.READ_ONLY);
				for (int x = 1; x < groupByChoices.size(); x++) {
					String key = groupByChoices.get(x);
					String value = Utilities.getLanguage(key);
					groupBy.add(value);
					groupBy.setData(key, value);
				}
				groupBy.select(groupBy.indexOf(groupBy.getItem(0)));
				groupBy.setData(tabIndex);
				groupBy.setEnabled(true);
				groupBy.setToolTipText("tooltip.group.by");
				groupBy.addSelectionListener(new GroupByListener(this));
				// ..Store a reference to the comboBox's handle (for easy reference w/in Listeners)
				PANEL_INFO[tabIndex][INFO_GROUP_BY] = groupBy;
	
				// Add a selection listener for the checkbox now that the Label and Text have been defined
				filterAutomatic.addSelectionListener(new AutomaticFilterListener(this, filterByLabel, filterBy));
				break;
			}
			case 2: { // Reload View Configuration
				GridLayout viewConfigurationLayout = new GridLayout(1, false);
				
				final Button reloadConfiguration = new Button(tabPanel, SWT.PUSH);
				reloadConfiguration.setText(Utilities.getLanguage("label.refresh.reload"));
				reloadConfiguration.setToolTipText(Utilities.getLanguage("tooltip.refresh.reload"));
				reloadConfiguration.setData(tabIndex);
				reloadConfiguration.setEnabled(true);
				reloadConfiguration.addSelectionListener(new ReloadConfigurationListener(this));
				// ..Store a reference to the checkBox's handle (for easy reference w/in Listeners)
				PANEL_INFO[tabIndex][INFO_RELOAD_CONFIG] = reloadConfiguration;
	
				break;
			}
		}

		/*
		 * Primary Tab Content
		 */

		GridLayout tabContentLayout = new GridLayout();
		tabContentLayout.marginHeight = 0;
		tabContentLayout.marginWidth = 0;
		Composite tabContent = new Composite(tabPanel, SWT.BORDER_DASH | SWT.BORDER);
		tabContent.setLayout(tabContentLayout);
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 100).applyTo(tabContent);

		switch (tabIndex) {
			case 0: // Action Form Beans
			case 1: // Action Forwards
			case 2: // Action Mappings
			case 3: // Tiles Definitions
			{
				if ((Tree)PANEL_INFO[tabIndex][INFO_TREE] == null) {
					Tree tree = new Tree(tabContent, SWT.NULL); // SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
					tree.addKeyListener(new TreeKeyListener(this));
					tree.addMouseListener(new TreeMouseListener(this));
				//	tree.setData((SimpleTreeNode)PANEL_INFO[tabIndex][INFO_ROOT]);
					TreeUtils.populateTree(tree, (SimpleTreeNode)PANEL_INFO[tabIndex][INFO_ROOT]);
				//	ToolTipManager.sharedInstance().registerComponent(tree);
					PANEL_INFO[tabIndex][INFO_TREE] = tree;
			        GridDataFactory.fillDefaults().grab(true, true).hint(400, 100).applyTo(tree);
				}
				break;
			}
			case 4: { // Viewer Configuration
		        Tree configTree = new Tree(tabContent, SWT.V_SCROLL | SWT.H_SCROLL); // SWT.BORDER
		        GridDataFactory.fillDefaults().grab(true, true).hint(400, 100).applyTo(configTree);
				TreeUtils.populateTree(configTree, (SimpleTreeNode)PANEL_INFO[tabIndex][INFO_ROOT]);
				PANEL_INFO[tabIndex][INFO_TREE] = configTree;
				break;
			}
			case 5: { // Viewer Console
				StyledText consoleText = new StyledText(tabContent, SWT.V_SCROLL | SWT.H_SCROLL); // SWT.BORDER | SWT.READ_ONLY
				consoleText.setEditable(false);
				PANEL_INFO[tabIndex][INFO_TREE] = consoleText;
				break;
			}
		}
	}

	/**
	 * Parse the command-line arguments into the 'commandLineArgs' map.
	 *
	 * @param args String array containing any/all command-line arguments.
	 */
    private void parseCommandLineArguments(String args[]) {
		logger.info("Parsing command-line arguments: START");
    	if (args != null) {
    		for (int x = 0; x < args.length; x++) {
    			String[] keyValue = null;
    			if (args[x].startsWith("-") && !args[x].contains("=") && args.length > x + 1) {
    				keyValue = new String[]{ args[x], args[++x] };
    			} else {
    				keyValue = args[x].split("=");
    			}
    			commandLineArgs.put(keyValue[0], keyValue[1]);
    		}
    		logger.debug("Command-line arguments: " + Arrays.toString(args));
    	}
		logger.info("Parsing command-line arguments: END");
    }

    /**
     * Parse the "viewer-config.xml" file.
     */
    private void parseViewerConfiguration() {
		logger.info("Parsing viewer configuration: START");

    	// Override configuration file name (when necessary)
    	if (commandLineArgs.containsKey("configurationFileTarget")) {
    		viewerConfigFileTarget = (String)commandLineArgs.get("configurationFileTarget");
    	} else if (MiscUtils.getFileSystemObjectLastModified(viewerConfigFileName) != null) {
    		viewerConfigFileTarget = viewerConfigFileName;
    	}

    	// Parse the XML file into a document object
//   	Document domViewerConfig = MiscUtils.parseXmlFile(this.getClass(), viewerConfigFileTarget);
		Document domViewerConfig = MiscUtils.buildViewerConfig();

    	SimpleTreeNode vc = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_VIEWER_CONFIG, -1, -1, null));

//		List<Map<String, Map<String, SimpleTreeNode>>> futureResults = new ArrayList<Map<String, Map<String, SimpleTreeNode>>>();
    	List<Future<Map<String, Map<String, SimpleTreeNode>>>> futureResults = new ArrayList<Future<Map<String, Map<String, SimpleTreeNode>>>>();

    	futureResults.add(this.parseDocument(viewerConfigFileTarget, domViewerConfig, new String[]{ "vc" }, new String[]{ "viewer-config" }, new SimpleTreeNode[]{ vc }));

//    	for (Map<String, Map<String, SimpleTreeNode>> futureResult : futureResults) {
    	for (Future<Map<String, Map<String, SimpleTreeNode>>> futureResult : futureResults) {
    		while (!futureResult.isDone()) {
    			try {
					Thread.currentThread().sleep(1000);
					logger.debug("Blocking viewerConfig:" + futureResult + " until done.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
			try {
//    			for (Map.Entry<String, Map<String, SimpleTreeNode>> result : futureResult.entrySet()) {
				for (Map.Entry<String, Map<String, SimpleTreeNode>> result : futureResult.get().entrySet()) {
					((Map)vc.getUserObject()).putAll(result.getValue());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}

    	// Reset current thread pool so that any configuration changes are applied
    	ThreadPoolFactory.getInstance().resetThreadPool();

    	PANEL_INFO[INFO_ID_VIEWER_CONFIG][INFO_ROOT] = vc;

		logger.info("Parsing viewer configuration: END");
    }

    /**
     * Parse all configuration files that have been specified in the "viewer-config.xml" file.
     * <p>
     *  NOTE: After parsing all of the configuration files, the sorting/grouping-related
     *        maps are then created.
     * </p>
     */
    private void parseSpecifiedFiles() {
		logger.info("Parsing specified files: START");

		SimpleTreeNode treeNode = null;

    	// Create the 4 main maps (keyed by an actual primary key)
		SimpleTreeNode safbByName  = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_SAFB, GROUP_BY_SAFB_BY_NAME , -1, "safb"));
		SimpleTreeNode safByUnique = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_SAF , GROUP_BY_SAF_BY_UNIQUE, -1, "saf" ));
		SimpleTreeNode samByPath   = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_SAM , GROUP_BY_SAM_BY_PATH  , -1, "sam" ));
		SimpleTreeNode tdByName    = new SimpleTreeNode("root", null, new TreeMap(), null, null, new NodeInfo(INFO_ID_TD  , GROUP_BY_TD_BY_NAME   , -1, "td"  ));

//		List<Map<String, Map<String, SimpleTreeNode>>> futureResults = new ArrayList<Map<String, Map<String, SimpleTreeNode>>>();
		List<Future<Map<String, Map<String, SimpleTreeNode>>>> futureResults = new ArrayList<Future<Map<String, Map<String, SimpleTreeNode>>>>();

    	for (Iterator<String> it = StrutsTilesView.getViewerConfig().getTargetAppConfigFilesByMask().keySet().iterator(); it.hasNext();) {
    		String targetAppConfigFileMask = it.next();
    		List<File> targetAppConfigFiles = StrutsTilesView.getViewerConfig().getTargetAppConfigFilesByMask().get(targetAppConfigFileMask);
    		for (Iterator<File> itSub = targetAppConfigFiles.iterator(); itSub.hasNext();) {
    			File configFile = (File)itSub.next();
    			String configFileTarget = configFile.getAbsolutePath();
    			Document workDocument = MiscUtils.parseXmlFile(this.getClass(), configFileTarget);
    			if (configFile.getName().startsWith("struts-config")) {
    				futureResults.add(this.parseDocument(configFileTarget, workDocument, new String[]{ "safbByName", "samByPath" }, new String[]{ "form-bean", "action" }, new SimpleTreeNode[]{ safbByName, samByPath }));
    			} else { // "tiles-defs"
    				futureResults.add(this.parseDocument(configFileTarget, workDocument, new String[]{ "tdByName" }, new String[]{ "definition" }, new SimpleTreeNode[]{ tdByName } ));
    			}
    		}
    	}

		// Update the Progress Bar
    	job.setProgress(5);

//    	for (Map<String, Map<String, SimpleTreeNode>> futureResult : futureResults) {
    	for (Future<Map<String, Map<String, SimpleTreeNode>>> futureResult : futureResults) {
    		while (!futureResult.isDone()) {
    			try {
					Thread.currentThread().sleep(1000);
					logger.debug("Blocking viewerConfig:" + futureResult + " until done.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
			try {
//    			for (Map.Entry<String, Map<String, SimpleTreeNode>> result : futureResult.entrySet()) {
				for (Map.Entry<String, Map<String, SimpleTreeNode>> result : futureResult.get().entrySet()) {
					if ("safbByName".equals(result.getKey())) {
						((Map)safbByName.getUserObject()).putAll(result.getValue());
	    			} else if ("samByPath".equals(result.getKey())) {
	    				((Map) samByPath.getUserObject()).putAll(result.getValue());
	    			} else if ("tdByName".equals(result.getKey())) {
	    				((Map)  tdByName.getUserObject()).putAll(result.getValue());
	    			}
	//    			// Update the Progress Bar
	//				if (!"safbByName".equals(result.getKey())) {
	//					// NOTE: There's a safbByName for each samByPath (don't double-increment)
	//	    			job.increment();
	//				}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}

		// Update the Progress Bar
    	job.setProgress(10);

    	// Tell threadpool we're done submitting threads
    	ThreadPoolFactory.getInstance().getThreadPool().shutdown();
    	// Wait for all threads to finish
    	try {
			while (!ThreadPoolFactory.getInstance().getThreadPool().awaitTermination(1, TimeUnit.SECONDS)) {
			  logger.debug("Awaiting completion of threads.");
			}
		} catch (InterruptedException e) {
			logger.error("ThreadPoolFactory.shutdown was interrupted - " + e.getMessage(), e);
		}

    	/************************************************************
    	 * Create/Populate Maps/Collections based on (Map)samByPath *
    	 * + (Map )safByUnique                                      *
    	 * + (Map )samsBySafPath                                    *
    	 * + (Map )samsBySafName                                    *
    	 * + (List)safbByName.getActionMappings()                   *
    	 * + (List)tdByName.getActionForwards()                     *
    	 * + (Map )classMemberBranches                              *
    	 ************************************************************/
    	int samsTotal = ((Map)samByPath.getUserObject()).size();
    	int samsCount = 0;
    	for (Iterator it = ((Map)samByPath.getUserObject()).keySet().iterator(); it.hasNext();) {
    		samsCount++;
    		// Update progress each 5% between 10% and 90%
    		if (samsCount / samsTotal * 100 % 5 == 0 && samsCount / samsTotal > .1 && samsCount / samsTotal < .9) {
    			job.setProgress(samsCount / samsTotal * 100 / 5);
    		}
    		String samKey = (String)it.next();
    		StrutsActionMapping sam = (StrutsActionMapping)((SimpleTreeNode)((Map)samByPath.getUserObject()).get(samKey)).getUserObject();
       		// ..safByUnique, samsBySafName, samsBySafPath, tdByName.getActionForwards()
       		for (Iterator itSub = sam.getActionForwards().keySet().iterator(); itSub.hasNext();) {
       			String safKey = (String)itSub.next();
     			treeNode = (SimpleTreeNode)sam.getActionForwards().get(safKey);
     			if (treeNode == null) continue;
     			StrutsActionForward saf = (StrutsActionForward)treeNode.getUserObject();
   				// ..safByUnique
				String uniqueKey = sam.getPath() + "@" + saf.getName();
				SimpleTreeNode uniqueValue = new SimpleTreeNode(uniqueKey, safByUnique, saf, "saf");
   				((Map)safByUnique.getUserObject()).put(uniqueKey, uniqueValue);
   				// ..samsBySafName
   				SimpleTreeNode samsBySafNameNode = (SimpleTreeNode)samBySafName.get(saf.getName());
   				if (samsBySafNameNode == null) {
   					samsBySafNameNode = new SimpleTreeNode(saf.getName());
   				}
   				List samsBySafNameList = (List)samsBySafNameNode.getUserObject();
   				if (samsBySafNameList == null) {
   					samsBySafNameList = new ArrayList();
   				}
   				if (!samsBySafNameList.contains(uniqueValue)) {
   					samsBySafNameList.add(uniqueValue);
      			} else {
      				logger.debug("[samsBySafName] samsBySafName.contains(" + uniqueKey + ")");
   				}
   				samsBySafNameNode.setUserObject(samsBySafNameList);
   				samBySafName.put(saf.getName(), samsBySafNameNode);
   				// ..samsBySafPath
   				SimpleTreeNode samsBySafPathNode = (SimpleTreeNode)samBySafPath.get(saf.getPath());
   				if (samsBySafPathNode == null) {
   					samsBySafPathNode = new SimpleTreeNode(saf.getPath());
   				}
   				List samsBySafPathList = (List)samsBySafPathNode.getUserObject();
   				if (samsBySafPathList == null) {
   					samsBySafPathList = new ArrayList();
   				}
   				if (!samsBySafPathList.contains(uniqueValue)) {
   					samsBySafPathList.add(uniqueValue);
      			} else {
      				logger.debug("[samsBySafPath] samsBySafPath.contains(" + uniqueKey + ")");
   				}
   				samsBySafPathNode.setUserObject(samsBySafPathList);
   				samBySafPath.put(saf.getPath(), samsBySafPathNode);
         		// ..tdByName.getActionForwards()
 				treeNode = (SimpleTreeNode)((Map)tdByName.getUserObject()).get(saf.getPath());
 				if (treeNode == null) continue;
 				TilesDefinition td = (TilesDefinition)treeNode.getUserObject();
   				if (td != null) {
   					List safs = td.getActionForwards();
   					if (safs == null) {
   						safs = new ArrayList();
   					}
   					if (!safs.contains(saf)) {
   						safs.add(saf);
          			} else {
          				logger.debug("[tdByName] safs.contains(" + uniqueKey + ")");
   					}
   					td.setActionForwards(safs);
       				/*****************
       				 * Add td to saf *
       				 *****************/
					saf.setTilesDefinition(td);
         		}
       		}
     		// ..safbByName.getActionMappings()
     		if (StringUtils.isNotEmpty(sam.getName())) {
     			treeNode = (SimpleTreeNode)((Map)safbByName.getUserObject()).get(sam.getName());
     			if (treeNode == null) continue;
     			StrutsActionFormBean safb = (StrutsActionFormBean)treeNode.getUserObject();
      			if (safb != null) {
      				List sams = safb.getActionMappings();
      				if (sams == null) {
      					sams = new ArrayList();
      				}
      				//if (!sams.contains(samKey)) {
      				if (!sams.contains(sam)) {
      					//sams.add(samKey);
      					sams.add(sam);
          			} else {
          				logger.debug("[safbByName] sams.contains(" + samKey + ")");
      				}
      				safb.setActionMappings(sams);
       				/*******************
       				 * Add safb to sam *
       				 *******************/
					sam.setFormBean(safb);
      			}
      		}
      		// ..classMemberBranches
      		ModelUtils.createBranchClassFromAbstractVO(null, sam);
      		if (StringUtils.isNotEmpty(sam.getName())) {
    			treeNode = (SimpleTreeNode)((Map)safbByName.getUserObject()).get(sam.getName());
    			if (treeNode == null) continue;
    			StrutsActionFormBean safb = (StrutsActionFormBean)treeNode.getUserObject();
      			if (safb != null) {
      				ModelUtils.createBranchClassFromAbstractVO(null, safb);
      			}
      		}
  		}

		// Update the Progress Bar
		job.setProgress(90);

    	/***********************************************
    	 * Type-specific structure for each type of VO *
    	 *	+ Struts Tiles Viewer (Configuration)      *
    	 *		- (n/a)                                *
x    	 *	+ Action Form Bean...                      *
x    	 *		- Action Mapping(s)                    *
x    	 *			- Action Forward(s)                *
x    	 *				- Tiles Definition             *
-    	 *					- Tiles Attribute(s)       *
x    	 *	+ Action Forward...                        *
x    	 *		- Tiles Definition                     *
-    	 *			- Tiles Attribute(s)               *
x    	 *		- Action Mapping                       *
x    	 *			- Action Form Bean                 *
x    	 *	+ Action Mapping...                        *
x    	 *		- Action Form Bean                     *
x    	 *		- Action Forward(s)                    *
x    	 *			- Tiles Definition                 *
-    	 *				- Tiles Attribute(s)           *
-    	 *	+ Tiles Definition...                      *
-    	 *		- Tiles Attribute(s)                   *
x    	 *		- Action Forward(s)                    *
x    	 *			- Action Mapping                   *
x    	 *				- Action Form Bean             *
    	 ***********************************************/

    	// Update the 4 main maps (keyed by an actual primary key)
		PANEL_INFO[INFO_ID_SAFB][INFO_ROOT] = safbByName;
		PANEL_INFO[INFO_ID_SAF ][INFO_ROOT] = safByUnique;
		PANEL_INFO[INFO_ID_SAM ][INFO_ROOT] = samByPath;
		PANEL_INFO[INFO_ID_TD  ][INFO_ROOT] = tdByName;

		logger.info("Parsing specified files: END");
    }

    /**
     * Remove entries from the specified map that don't have a key that matches
     * the specified regex pattern.
     *
     * @param map   Map onto which to apply the filter.
     * @param regex String containing the value with which to filter.
     */
	private void filterByRegex(Map map, String regex) {
		Pattern regexPattern = Pattern.compile(".*" + StringUtils.defaultString(regex) + ".*");
		Set work = new HashSet(map.keySet());
		for (Iterator it = work.iterator(); it.hasNext();) {
			String key = (String)it.next();
			// Remove this key if it doesn't match the pattern
			if (!regexPattern.matcher(key).matches()) {
				map.remove(key);
			}
		}
	}

	/**
	 * Obtain a count of files that will be processed by {@link #parseSpecifiedFiles()}.
	 * <p>
	 *  NOTE: This count is then used to help determine the total count of Progress Bar-related tasks.
	 * </p>
	 *
	 * @return Integer containing the total number of files to be processed by {@link #parseSpecifiedFiles()}.
	 */
	private static int getCountOfSpecifiedFiles() {
		int result = 0;
    	for (Iterator it = StrutsTilesView.getViewerConfig().getTargetAppConfigFilesByMask().keySet().iterator(); it.hasNext();) {
    		String targetAppConfigFileMask = (String)it.next();
    		List targetAppConfigFiles = (List)StrutsTilesView.getViewerConfig().getTargetAppConfigFilesByMask().get(targetAppConfigFileMask);
   			result += targetAppConfigFiles.size();
    	}
    	return result;
	}

	/**
	 * Add context menu to view.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				StrutsTilesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Add toolbar to view.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Add actions to the toolbars dropdown menu.
	 * 
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	/**
	 * Add actions to the context menu.
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Add actions to the toolbar.
	 * 
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	/**
	 * Create all actions.
	 */
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				Utilities.showMessage(viewer, "Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				Utilities.showMessage(viewer, "Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				Utilities.showMessage(viewer, "Double-click detected on "+obj.toString());
			}
		};
	}

	/**
	 * Implement double-click support.
	 */
	private void hookDoubleClickAction() {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
	}

}