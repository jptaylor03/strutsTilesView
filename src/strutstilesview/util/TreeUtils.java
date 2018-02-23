package strutstilesview.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Node;

import strutstilesview.Utilities;
import strutstilesview.model.AbstractVO;
import strutstilesview.model.ClassTypeInfo;
import strutstilesview.model.Filename;
import strutstilesview.model.IteratorEnumeration;
import strutstilesview.model.SimpleTreeNode;
import strutstilesview.model.StrutsActionFormBean;
import strutstilesview.model.StrutsActionForward;
import strutstilesview.model.StrutsActionMapping;
import strutstilesview.model.TilesAttribute;
import strutstilesview.model.TilesDefinition;
import strutstilesview.model.ViewerConfig;
import strutstilesview.views.StrutsTilesView;


/**
 * Utilities for tree manipulation.
 * 
 * TODO[jpt] Cleanup this class (some of its methods can be refactored / combined / removed).
 */
public class TreeUtils {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");

	/**
	 * Whether to include class information (methods/fields/constructors/superclass/interfaces/etc).
	 */
	private static boolean INCLUDE_CLASS_INFO = false;

	/**
	 * Whether to include class information details.
	 */
	private static boolean INCLUDE_CLASS_INFO_DETAIL = false;

	/**
	 * Map containing information about each supported 'nodeCategory'.
	 * <ul>
	 *  NOTE: The information for each 'nodeCategory' is an Object array containing...
	 *  <li>[0] == Icon data</li>
	 *  <li>[1] == Description</li>
	 * </ul>
	 * 
	 * @see #getNodeCategory(Object)
	 */
	private static Map<String, Object[]> nodeCategorys;
	
	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	/**
	 * Obtain the 'nodeCategorys' map.
	 * <p>
	 *  NOTE: If the 'nodeCategorys' map is empty, then it is created.
	 * </p>
	 * 
	 * @return Map containing the 'nodeCategory' information.
	 */
	public static Map<String, Object[]> getNodeCategorys() {
		if (nodeCategorys == null) {
			nodeCategorys = new HashMap<String, Object[]>();
			nodeCategorys.put("attr"            , new Object[]{ Utilities.getImage("attribute-icon.jpg"       ), "Attribute" });
			nodeCategorys.put("td"              , new Object[]{ Utilities.getImage("apache-tiles-logo.jpg"    ), "Apache Tiles Definition" });
			nodeCategorys.put("td.alt"          , new Object[]{ Utilities.getImage("generic-tiles-icon.jpg"   ), "Tiles Definition" });
			nodeCategorys.put("webapp"          , new Object[]{ Utilities.getImage("webapp_obj.gif"           ), "Web application" });
			nodeCategorys.put("link"            , new Object[]{ Utilities.getImage("link_obj.gif"             ), "Link in a JSP file or an HTML file" });
			nodeCategorys.put("sam"             , new Object[]{ Utilities.getImage("actionmapping_obj.gif"    ), "Action Mapping defined in a Struts configuration file" });
			nodeCategorys.put("safb"            , new Object[]{ Utilities.getImage("formbean_obj.gif"         ), "Form-bean in a Struts configuration file" });
			nodeCategorys.put("classGS"         , new Object[]{ Utilities.getImage("dataformfield_getset.gif" ), "Form-bean field or a Dynaform field that has 'get' and 'set' methods" });
			nodeCategorys.put("classG"          , new Object[]{ Utilities.getImage("dataformfield_get.gif"    ), "Form-bean field that has a 'get' method only" });
			nodeCategorys.put("classS"          , new Object[]{ Utilities.getImage("dataformfield_set.gif"    ), "Form-bean field that has a 'set' method only" });
			nodeCategorys.put("sami"            , new Object[]{ Utilities.getImage("actionmapping_input.gif"  ), "Indicator that the 'input' attribute of the Action Mapping is set" });
			nodeCategorys.put("saf"             , new Object[]{ Utilities.getImage("forward_obj.gif"          ), "Forward in a Struts configuration file" });
			nodeCategorys.put("exception"       , new Object[]{ Utilities.getImage("exception_obj.gif"        ), "Indicator that the part in question has an exception" });
			nodeCategorys.put("gexh"            , new Object[]{ Utilities.getImage("globalexception_obj.gif"  ), "Exception handler defined in a Struts configuration file" });
			nodeCategorys.put("samsaf"          , new Object[]{ Utilities.getImage("actionmapping_forward.gif"), "Indicator that the Forward attribute of the Action Mapping is set" });
			nodeCategorys.put("module"          , new Object[]{ Utilities.getImage("module_obj.gif"           ), "(Struts 1.1) Module node" });
			nodeCategorys.put("modulet"         , new Object[]{ Utilities.getImage("module_transition.gif"    ), "(Struts 1.1) Module transition node" });

			nodeCategorys.put("debug"           , new Object[]{ Utilities.getImage("debug_obj.gif"            ), "Indicator that the part in question is for debugging" });
			nodeCategorys.put("info"            , new Object[]{ Utilities.getImage("info_obj.gif"             ), "Indicator that the part in question is informational" });
			nodeCategorys.put("warn"            , new Object[]{ Utilities.getImage("warning_obj.gif"          ), "Indicator that the part in question has a warning" });
			nodeCategorys.put("error"           , new Object[]{ Utilities.getImage("error_obj.gif"            ), "Indicator that the part in question has an error" });
			
			nodeCategorys.put("annotation"      , new Object[]{ Utilities.getImage("annotation_obj.jpg"       ), "Annotation" });
			nodeCategorys.put("class"           , new Object[]{ Utilities.getImage("class_obj.jpg"            ), "Class file" });
			nodeCategorys.put("enum"            , new Object[]{ Utilities.getImage("enum_obj.jpg"             ), "Enumeration" });
			nodeCategorys.put("interface"       , new Object[]{ Utilities.getImage("interface_obj.jpg"        ), "Interface file" });
			
			nodeCategorys.put("class.file"      , new Object[]{ Utilities.getImage("classf_obj.jpg"           ), "Class file" });
			nodeCategorys.put("exe"             , new Object[]{ Utilities.getImage("executable_obj.jpg"       ), "Executable file" });
			nodeCategorys.put("file"            , new Object[]{ Utilities.getImage("file_obj.gif"             ), "File" });
			nodeCategorys.put("folder"          , new Object[]{ Utilities.getImage("folder_obj.gif"           ), "Folder" });
			nodeCategorys.put("html"            , new Object[]{ Utilities.getImage("html_obj.jpg"             ), "HTML file" });
			nodeCategorys.put("jar"             , new Object[]{ Utilities.getImage("jar_obj.jpg"              ), "Java Archive file" });
			nodeCategorys.put("java"            , new Object[]{ Utilities.getImage("javaf_obj.jpg"            ), "Java file" });
			nodeCategorys.put("java.alt"        , new Object[]{ Utilities.getImage("java_obj.gif"             ), "Java file" });
			nodeCategorys.put("jsp"             , new Object[]{ Utilities.getImage("jsp_obj.gif"              ), "JSP file" });
			nodeCategorys.put("log"             , new Object[]{ Utilities.getImage("log_obj.gif"              ), "Log file" });
			nodeCategorys.put("out"             , new Object[]{ Utilities.getImage("console_obj.gif"          ), "Console output" });
			nodeCategorys.put("xml"             , new Object[]{ Utilities.getImage("xml_obj.jpg"              ), "XML file" });
			nodeCategorys.put("zip"             , new Object[]{ Utilities.getImage("zip_obj.gif"              ), "Zip Archive file" });
			
			nodeCategorys.put("field.default"   , new Object[]{ Utilities.getImage("field_default_obj.jpg"    ), "Default field (package visible)" });
			nodeCategorys.put("field.private"   , new Object[]{ Utilities.getImage("field_private_obj.jpg"    ), "Private field" });
			nodeCategorys.put("field.protected" , new Object[]{ Utilities.getImage("field_protected_obj.jpg"  ), "Protected field" });
			nodeCategorys.put("field.public"    , new Object[]{ Utilities.getImage("field_public_obj.jpg"     ), "Public field" });
			nodeCategorys.put("method.default"  , new Object[]{ Utilities.getImage("method_default_obj.jpg"   ), "Default method (package visible)" });
			nodeCategorys.put("method.private"  , new Object[]{ Utilities.getImage("method_private_obj.jpg"   ), "Private method" });
			nodeCategorys.put("method.protected", new Object[]{ Utilities.getImage("method_protected_obj.jpg" ), "Protected method" });
			nodeCategorys.put("method.public"   , new Object[]{ Utilities.getImage("method_public_obj.jpg"    ), "Public method" });
		}
		return nodeCategorys;
	}
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Convenience method used to obtain a String value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @return String containing the corresponding XML node value.
	 * @see #getNodeAttributeStringValue(Node, String, String)
	 */
	public static String getNodeAttributeStringValue(Node node, String attributeName) {
		return getNodeAttributeStringValue(node, attributeName, "");
	}
	
	/**
	 * Obtain a String value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @param defaultValue  String containing a default value to use if the value is empty.
	 * @return String containing the corresponding XML node value.
	 * @see #getNodeAttributeStringValue(Node, String)
	 */
	public static String getNodeAttributeStringValue(Node node, String attributeName, String defaultValue) {
		String temp = null;
		if (node != null && node.getAttributes() != null) {
			Node work = node.getAttributes().getNamedItem(attributeName);
			if (work != null) {
				temp = work.getNodeValue();
			}
		}
		if (temp == null && defaultValue != null) {
			temp = defaultValue;
		}
		return (temp == null?null:temp.trim());
	}
	
	/**
	 * Convenience method used to obtain a Boolean value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @return Boolean containing the corresponding XML node value.
	 * @see #getNodeAttributeBooleanValue(Node, String, Boolean)
	 */
	public static Boolean getNodeAttributeBooleanValue(Node node, String attributeName) {
		return getNodeAttributeBooleanValue(node, attributeName, null);
	}
	
	/**
	 * Obtain a Boolean value from an XML node.
	 * 
	 * @param node          Node containing the data.
	 * @param attributeName String identifying the name of the value to obtain.
	 * @param defaultValue  Boolean containing a default value to use if the value is empty.
	 * @return Boolean containing the corresponding XML node value.
	 * @see #getNodeAttributeBooleanValue(Node, String)
	 */
	public static Boolean getNodeAttributeBooleanValue(Node node, String attributeName, Boolean defaultValue) {
		String temp = null;
		if (node != null && node.getAttributes() != null) {
			Node work = node.getAttributes().getNamedItem(attributeName);
			if (work != null) {
				temp = work.getNodeValue();
			}
		}
		if (temp == null && defaultValue != null) {
			temp = defaultValue.booleanValue() + "";
		}
		return (temp == null?null:new Boolean(temp));
	}
	
//	public static void addBranch(Object emptyCheck, SimpleTreeNode parent, SimpleTreeNode child) {
//		if (emptyCheck instanceof String) {
//			if (StringUtils.isNotEmpty(""+emptyCheck)) {
//				parent.add(child);
//			}
//		} else if (emptyCheck != null) {
//			parent.add(child);
//		}
//	}
    
//	public static SimpleTreeNode recreateBranch(SimpleTreeNode parent, String key) {
//		removeChild(parent, key);
//		SimpleTreeNode newChild = new SimpleTreeNode(new NodeInfo(key, null, null));
//		if (parent != null) parent.add(newChild);
//		return newChild;
//	}

//	public static void removeChild(SimpleTreeNode parent, String key) {
//		int childIndex = getChildIndex(parent, key);
//		if (childIndex >= 0) {
//			parent.remove(childIndex);
//		}
//	}

//	public static void replaceChild(SimpleTreeNode parent, String key, SimpleTreeNode newChild) {
//		int childIndex = getChildIndex(parent, key);
//		if (childIndex >= 0) {
//			parent.remove(childIndex);
//			parent.insert(newChild, childIndex);
//		} else {
//			parent.add(newChild);
//		}
//	}

//	public static int getChildIndex(SimpleTreeNode parent, String key) {
//		int result = -1;
//		if (parent != null && parent.children() != null && key != null) {
//			int x = 0;
//			for (Enumeration children = parent.children(); children.hasMoreElements();) {
//				SimpleTreeNode child = (SimpleTreeNode)children.nextElement();
//				if (key.equals(((SimpleTreeNode)child.getUserObject()).getLabel())) {
//					result = x;
//					break;
//				}
//				x++;
//			}
//		}
//		return result;
//	}

    /**
     * Obtain a TreePath for the specified node.
     * 
     * @param node TreeNode for which to find the path.
     * @return TreePath for the specified node.
     */
    public static TreePath getTreePath(SimpleTreeNode node) {
        List<SimpleTreeNode> list = new ArrayList<SimpleTreeNode>();

        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = node.getParent();
        }
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }
    
    /**
     * Determine the 'nodeCategory' for the specified 'nodeObject'.
     * 
     * @param nodeObject Object containing a reference to the 'nodeObject' to evaluate.
     * @return String identifying the 'category' for the specified 'nodeObject'.
     */
	public static String getNodeCategory(Object nodeObject) {
		String result = null;
		SimpleTreeNode treeNode = getNodeInfo(nodeObject);
		if (treeNode != null) {
			result = treeNode.getCategory();
		}
		return result;
	}

	/**
	 * Obtain a StringBuffer representation of the 'base' node and all its children.
	 * 
	 * @param base SimpleTreeNode identifying the starting node from where to begin recursing.
	 * @param seed StringBuffer containing a starting value to use for the resulting value.
	 * @return StringBuffer containing the result of the recursion.
	 * @see #traverseTreeNodes(SimpleTreeNode, StringBuffer, byte)
	 */
	public static StringBuffer traverseTreeNodes(SimpleTreeNode base, StringBuffer seed) {
		return TreeUtils.traverseTreeNodes(base, seed, base.getDepth());
	}
	
	/**
	 * Determine the 'category' of the specified 'fileName' (either "folder" or "file").
	 * 
	 * @param fileName String containing the name of the file to evaluate.
	 * @see #getFileObjectCategory(File)
	 */
	public static String getFileObjectCategory(String fileName) {
		return getFileObjectCategory(new File(fileName));
	}

	/**
	 * Determine the 'category' of the specified 'file' (either "folder" or "file").
	 * 
	 * @param file File identifying the file to evaluate.
	 * @see #getFileObjectCategory(String)
	 */
	public static String getFileObjectCategory(File file) {
		String category = null;
		if (file != null) {
			if (file.isDirectory()) {
				category = "folder";
			} else if (file.isFile()) {
				category = "file";
				Filename fileName = new Filename(file.getAbsolutePath(), '/', '.');
				Object[] info = (Object[])getNodeCategorys().get(fileName.extension());
				if (info != null) category = fileName.extension();
			}
		}
		return category;
	}
	
	/**
	 * Determine whether 'lookForMe' already exists somewhere up the tree branch
	 * as an ancestor.
	 * <ul>
	 *  NOTE: A 'lookForMe' can be either...
	 *  <li>SimpleTreeNode              (SimpleTreeNode.getLabel()                 is compared)</li>
	 *  <li>Class                       (SimpleTreeNode.getUserObject().getClass() is compared)</li>
	 *  <li>SimpleTreeNode.userObject() (SimpleTreeNode.getUserObject()            is compared)</li>
	 * </ul>
	 * 
	 * @param lookForMe Object to look for as an ancestor.
	 * @param parent    SimpleTreeNode that is the parent of the specified nodeObject. 
	 * @return Boolean indicating whether 'lookForMe' was found as an ancestor.
	 */
	public static boolean existsAsAncestor(Object lookForMe, SimpleTreeNode parent) {
		boolean result = false;
		if (lookForMe != null) {
			SimpleTreeNode work = (SimpleTreeNode)(parent == null?null:parent.getParent());
			while (work != null && result == false) {
				if        (lookForMe instanceof SimpleTreeNode) {
					result = (((SimpleTreeNode)lookForMe).getLabel().equals(work.getLabel()));
				} else if (lookForMe instanceof Class && work.getUserObject() != null) {
					result = lookForMe.equals(work.getUserObject().getClass());
				} else if (lookForMe instanceof Object) {
					result = lookForMe.equals(work.getUserObject());
				}
				work = (SimpleTreeNode)work.getParent();
			}
		}
		return result;
	}
	
	/**
	 * Determine whether 'lookForMe' already exists somewhere up the tree branch
	 * as an ancestor.
	 * <ul>
	 *  NOTE: A 'lookForMe' can be either...
	 *  <li>TreeItem                    (TreeItem.getText()            is compared)</li>
	 *  <li>Class                       (TreeItem.getData().getClass() is compared)</li>
	 *  <li>SimpleTreeNode.userObject() (TreeItem.getData()            is compared)</li>
	 * </ul>
	 * 
	 * @param lookForMe Object to look for as an ancestor.
	 * @param parent    TreeItem that is the parent of the specified nodeObject. 
	 * @return Boolean indicating whether 'lookForMe' was found as an ancestor.
	 */
	public static boolean existsAsAncestor(Object lookForMe, TreeItem parent) {
		boolean result = false;
		if (lookForMe != null) {
			TreeItem work = (TreeItem)(parent == null?null:parent.getParentItem());
			while (work != null && result == false) {
				if        (lookForMe instanceof TreeItem) {
					result = (""        + ((TreeItem)lookForMe).getText()).equals(work.getText()) ||
							 ("name="   + ((TreeItem)lookForMe).getText()).equals(work.getText()) ||
							 ("unique=" + ((TreeItem)lookForMe).getText()).equals(work.getText());
				} else if (lookForMe instanceof Class && work.getData() != null) {
					result = lookForMe.equals(work.getData().getClass());
				} else if (lookForMe instanceof Object) {
					result = lookForMe.equals(work.getData());
				}
				work = (TreeItem)work.getParentItem();
			}
		}
		return result;
	}
	
//	public static int getDepth(TreeNode node) {
//		int depth = 0;
//		TreeNode work = node;
//		while (work.getParent() != null) {
//			depth++;
//			work = work.getParent();
//		}
//		return depth;
//	}
	
	/********************
	 * Helper method(s) *
	 ********************/
	
//	/**
//	 * Obtain icon data for the specified 'imageName'.
//	 * 
//	 * @param imageName String identifying the name of the image to load.
//	 * @return ImageIcon containing the icon data for the specified image.
//	 */
//	private static ImageIcon createImageIcon(String imageName) {
//	//	java.net.URL imageURL = StrutsTilesView.class.getResource("resources/images/"+imageName);
//		java.net.URL imageURL = Utilities.getResourceURL("/resources/images/"+imageName);
//		if (imageURL != null) {
//			return new ImageIcon(imageURL);
//		} else {
//			logger.error("Couldn't find file: " + imageName);
//			return null;
//		}
//	}
	
	/**
	 * Obtain 'nodeInfo' for the specified 'nodeObject'
	 * 
	 * @param nodeObject Object referencing the 'nodeObject' to evaluate.
	 * @return SimpleTreeNode containing the 'nodeInfo' for the specified 'nodeObject'.
	 */
	private static SimpleTreeNode getNodeInfo(Object nodeObject) {
		SimpleTreeNode result = null;
		if (nodeObject instanceof SimpleTreeNode) {
			result = (SimpleTreeNode)nodeObject;
//		} else if (nodeObject instanceof Object[]) {
//			result = null;
//		} else if (nodeObject instanceof List ||
//					nodeObject instanceof Map ||
//					nodeObject instanceof Set) {
//			if (nodeObject != null) {
//				List work = null;
//				if (       nodeObject instanceof List) {
//					work = (List)nodeObject;
//				} else if (nodeObject instanceof Map) {
//					work = new ArrayList(((Map)nodeObject).values());
//				} else if (nodeObject instanceof Set) {
//					work = new ArrayList((Set)nodeObject);
//				}
//				if (work != null && work.size() > 0) {
//					Object firstNodeObject = work.iterator().next();
//					result = TreeUtils.getNodeInfo(firstNodeObject);
//				}
//			}
//		} else if (nodeObject instanceof StrutsActionFormBean) {
//			result = ((StrutsActionFormBean)nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof StrutsActionForward ) {
//			result = ((StrutsActionForward )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof StrutsActionMapping ) {
//			result = ((StrutsActionMapping )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof TilesAttribute      ) {
//			result = ((TilesAttribute      )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof TilesDefinition     ) {
//			result = ((TilesDefinition     )nodeObject).getNodeInfo();
//		} else if (nodeObject instanceof ViewerConfig        ) {
//			result = ((ViewerConfig        )nodeObject).getNodeInfo();
//		} else {
//			logger.debug("nodeObject.class == " + nodeObject.getClass());
		}
		return result;
	}

	/**
	 * Convenience method used to obtain a StringBuffer representation of the 'base' node and all its children.
	 * 
	 * @param base SimpleTreeNode identifying the starting node from where to begin recursing.
	 * @param seed StringBuffer containing a starting value to use for the resulting value.
	 * @param offset Byte value (used internally) to determine the initial depth of the 'base' node.
	 * @return StringBuffer containing the result of the recursion.
	 * @see #traverseTreeNodes(SimpleTreeNode, StringBuffer)
	 */
	@SuppressWarnings("unchecked")
	private static StringBuffer traverseTreeNodes(SimpleTreeNode base, StringBuffer seed, byte offset) {
		if (base != null) {
			if (seed == null) {
				seed = new StringBuffer();
			} else {
				seed.append("\n");
			}
			if (seed.length() > 0) {
				seed.append(StringUtils.repeat("\t", base.getDepth() - offset) + "+ ");
			}
			seed.append(base.getLabel());
			if (base.getChildCount() > 0) {
				for (Enumeration<TreeNode> children = base.children(); children.hasMoreElements();) {
					SimpleTreeNode child = (SimpleTreeNode)children.nextElement();
					seed = traverseTreeNodes(child, seed, offset);
				}
			}
		}
		return seed;
	}

	/**
	 * Populate the specified tree based on the contents of the SimpleTreeNode.
	 * 
	 * @param tree
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	public static void populateTree(Tree tree, SimpleTreeNode root) {
		TreeUtils.populateTree(tree, root.children(), 0);
	}

	/**
	 * 
	 * @param branch
	 * @param enumeration
	 * @param depth
	 */
	@SuppressWarnings("unchecked")
	private static void populateTree(Object branch, Enumeration<Object> enumeration, int depth) {
		if (depth > 4) return;
		if (enumeration != null) {
			Object child = null;
			TreeItem subBranch = null;
			TreeItem folderBranch = null;
			TreeItem subFolderBranch = null;
			TreeItem subSubFolderBranch = null;
			TreeItem attribute = null;
			String childCategory = null;
			Object[] childCategoryData = null;
			Object userObject = null;
			ViewerConfig viewerConfig = null;
			for (Enumeration<Object> children = enumeration; children.hasMoreElements();) {
				child = children.nextElement();
				if (branch instanceof Tree) {
					subBranch = new TreeItem((Tree)branch, SWT.NULL);
				} else {
					subBranch = new TreeItem((TreeItem)branch, SWT.NULL);
				}
				userObject = child;
				if (child instanceof SimpleTreeNode && ((SimpleTreeNode)child).getUserObject() != null) {
					userObject = ((SimpleTreeNode)child).getUserObject();
				}
				if (userObject instanceof SimpleTreeNode) {
					childCategory = ((SimpleTreeNode)userObject).getCategory();
					childCategoryData = getNodeCategorys().get(childCategory);
					subBranch.setText(((SimpleTreeNode)userObject).getLabel());
					subBranch.setImage(childCategoryData == null?null:(Image)childCategoryData[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
					if ("viewer-about".equals(subBranch.getText())) {
						// viewer-about
						subBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						attribute = new TreeItem(subBranch, SWT.NULL);
						attribute.setText("applicationName=" + StrutsTilesView.appInfo.getAppName());
						attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
						attribute = new TreeItem(subBranch, SWT.NULL);
						attribute.setText("applicationVersion=" + StrutsTilesView.appInfo.getAppVersion());
						attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
						attribute = new TreeItem(subBranch, SWT.NULL);
						attribute.setText("applicationBuildDate=" + StrutsTilesView.appInfo.getAppBuildDate());
						attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
						attribute = new TreeItem(subBranch, SWT.NULL);
						attribute.setText("applicationAuthorContact=" + StrutsTilesView.appInfo.getAppAuthorContact());
						attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
						attribute = new TreeItem(subBranch, SWT.NULL);
						attribute.setText("applicationIcon=" + StrutsTilesView.appInfo.getAppIcon());
						attribute.setImage((Image)Utilities.getImage(""+StrutsTilesView.appInfo.getAppIcon()));
					} else if ("viewer-debug".equals(subBranch.getText())) {
						// viewer-debug
						subBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						//..Missing Resource(s)
						folderBranch = new TreeItem(subBranch, SWT.NULL);
						folderBranch.setText("Missing Resource(s) [" + MissingResources.getMissingResources().size() + "]");
						folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						for (Object missingResource : MissingResources.getMissingResources()) {
							attribute = new TreeItem(folderBranch, SWT.NULL);
							attribute.setText(""+missingResource);
							attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
						}
					} else {
					//	userObject = ((SimpleTreeNode)userObject).getUserObject();
						if (/*userObject == child*/ ((SimpleTreeNode)userObject) != null) {
							populateTree(subBranch, ((SimpleTreeNode)userObject).children(), depth + 1);
						}
					}
				} else if (userObject instanceof StrutsActionFormBean) {
					subBranch.setText(((StrutsActionFormBean)userObject).getName());
					subBranch.setImage((Image)getNodeCategorys().get("safb")[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("name=" + ((StrutsActionFormBean)userObject).getName());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("type=" + ((StrutsActionFormBean)userObject).getType());
					attribute.setImage((Image)getNodeCategorys().get("class")[0]);
					if (INCLUDE_CLASS_INFO) createBranchClassFromAbstractVO(attribute, (AbstractVO)userObject);
					if (((StrutsActionFormBean)userObject).getActionMappings() != null) {
						folderBranch = new TreeItem(subBranch, SWT.NULL);
						folderBranch.setText("Action Mapping(s)");
						folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						populateTree(folderBranch, new IteratorEnumeration(((StrutsActionFormBean)userObject).getActionMappings().iterator()), depth + 1);
						if (folderBranch.getItemCount() == 0) {
							folderBranch.dispose();
						}
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("source=" + ((StrutsActionFormBean)userObject).getConfigFileTarget());
					attribute.setImage((Image)getNodeCategorys().get("xml")[0]);
				} else if (userObject instanceof StrutsActionForward) {
					subBranch.setText(((StrutsActionMapping)((StrutsActionForward)userObject).getActionMapping()).getPath() + "@" + ((StrutsActionForward)userObject).getName());
					subBranch.setImage((Image)getNodeCategorys().get("saf")[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
				//	attribute = new TreeItem(subBranch, SWT.NULL);
				//	attribute.setText("unique=" + ((StrutsActionMapping)((StrutsActionForward)userObject).getActionMapping()).getPath() + "@" + ((StrutsActionForward)userObject).getName());
				//	attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("name=" + ((StrutsActionForward)userObject).getName());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("path=" + ((StrutsActionForward)userObject).getPath());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("redirect=" + ((StrutsActionForward)userObject).getRedirect());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					if (((StrutsActionForward)userObject).getTilesDefinition() != null) {
						populateTree(subBranch, new IteratorEnumeration(Arrays.asList(new TilesDefinition[]{ ((StrutsActionForward)userObject).getTilesDefinition() }).iterator()), depth + 1);
					}
					if (((StrutsActionForward)userObject).getActionMapping() != null) {
						populateTree(subBranch, new IteratorEnumeration(Arrays.asList(new StrutsActionMapping[]{ ((StrutsActionForward)userObject).getActionMapping() }).iterator()), depth + 1);
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("source=" + ((StrutsActionForward)userObject).getConfigFileTarget());
					attribute.setImage((Image)getNodeCategorys().get("xml")[0]);
				} else if (userObject instanceof StrutsActionMapping) {
					subBranch.setText(((StrutsActionMapping)userObject).getPath());
					subBranch.setImage((Image)getNodeCategorys().get("sam")[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("path=" + ((StrutsActionMapping)userObject).getPath());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("type=" + ((StrutsActionMapping)userObject).getType());
					attribute.setImage((Image)getNodeCategorys().get("class")[0]);
					if (INCLUDE_CLASS_INFO) createBranchClassFromAbstractVO(attribute, (AbstractVO)userObject);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("name=" + ((StrutsActionMapping)userObject).getName());
					attribute.setImage((Image)getNodeCategorys().get("safb")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("scope=" + ((StrutsActionMapping)userObject).getScope());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("validate=" + ((StrutsActionMapping)userObject).getValidate());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					if (((StrutsActionMapping)userObject).getActionForwards() != null) {
						folderBranch = new TreeItem(subBranch, SWT.NULL);
						folderBranch.setText("Action Forward(s)");
						folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						populateTree(folderBranch, new IteratorEnumeration(((StrutsActionMapping)userObject).getActionForwards().values().iterator()), depth + 1);
						if (folderBranch.getItemCount() == 0) {
							folderBranch.dispose();
						}
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("source=" + ((StrutsActionMapping)userObject).getConfigFileTarget());
					attribute.setImage((Image)getNodeCategorys().get("xml")[0]);
				} else if (userObject instanceof TilesDefinition) {
					subBranch.setText(((TilesDefinition)userObject).getName());
					subBranch.setImage((Image)getNodeCategorys().get("td")[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("name=" + ((TilesDefinition)userObject).getName());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("extends=" + ((TilesDefinition)userObject).getExtends());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("extendsDefinition=" + ((TilesDefinition)userObject).getExtendsDefinition());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					if (((TilesDefinition)userObject).getActionForwards() != null) {
						folderBranch = new TreeItem(subBranch, SWT.NULL);
						folderBranch.setText("Action Forward(s)");
						folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						populateTree(folderBranch, new IteratorEnumeration(((TilesDefinition)userObject).getActionForwards().iterator()), depth + 1);
						if (folderBranch.getItemCount() == 0) {
							folderBranch.dispose();
						}
					}
					if (((TilesDefinition)userObject).getAttributes() != null) {
						folderBranch = new TreeItem(subBranch, SWT.NULL);
						folderBranch.setText("Tiles Attribute(s)");
						folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						populateTree(folderBranch, new IteratorEnumeration(((TilesDefinition)userObject).getAttributes().values().iterator()), depth + 1);
						if (folderBranch.getItemCount() == 0) {
							folderBranch.dispose();
						}
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("source=" + ((TilesDefinition)userObject).getConfigFileTarget());
					attribute.setImage((Image)getNodeCategorys().get("xml")[0]);
				} else if (userObject instanceof TilesAttribute) {
					subBranch.setText(((TilesAttribute)userObject).getName() + "=" + ((TilesAttribute)userObject).getValue());
					subBranch.setImage("page.work".equalsIgnoreCase(((TilesAttribute)userObject).getName())?(Image)getNodeCategorys().get("jsp")[0]:(Image)getNodeCategorys().get("attr")[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
				} else if (userObject instanceof ViewerConfig) {
					viewerConfig = (ViewerConfig)userObject;
					// viewer-config
					subBranch.setText("viewer-config");
					subBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					// Check to prevent duplicates/infinite recursion
					if (TreeUtils.existsAsAncestor(subBranch, subBranch)) {
						subBranch.setForeground(subBranch.getDisplay().getSystemColor(SWT.COLOR_GRAY));
						continue; // End recursion
					}
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("targetAppBasePackage=" + viewerConfig.getTargetAppBasePackage());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
//					attribute = new TreeItem(subBranch, SWT.NULL);
//					attribute.setText("textEditorExecutable=" + viewerConfig.getTextEditorExecutable());
//					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("recurseToShowSiblings=" + viewerConfig.isRecurseToShowSiblings());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					//..Configuration File(s)
					folderBranch = new TreeItem(subBranch, SWT.NULL);
					folderBranch.setText("Configuration File(s)");
					folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("targetAppConfigFileBasePath=" + viewerConfig.getTargetAppConfigFileBasePath());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					subFolderBranch = new TreeItem(folderBranch, SWT.NULL);
					subFolderBranch.setText("Mask(s)");
					subFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					for (Object key : viewerConfig.getTargetAppConfigFilesByMask().keySet()) {
						List values = (List)viewerConfig.getTargetAppConfigFilesByMask().get(key);
						subSubFolderBranch = new TreeItem(subFolderBranch, SWT.NULL);
						subSubFolderBranch.setText(""+key);
						subSubFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						for (Object value : values) {
							attribute = new TreeItem(subSubFolderBranch, SWT.NULL);
							attribute.setText(""+value);
							childCategory = ((File)value).isDirectory()?"folder":((File)value).getName().endsWith(".xml")?"xml":((File)value).getName().endsWith(".jar")?"jar":((File)value).getName().endsWith(".class")?"class":"file";
							attribute.setImage((Image)getNodeCategorys().get(childCategory)[0]);
						}
					}
					//..Source Code Path(s)
					folderBranch = new TreeItem(subBranch, SWT.NULL);
					folderBranch.setText("Source Code Path(s)");
					folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("targetAppSourceCodeBasePath=" + viewerConfig.getTargetAppSourceCodeBasePath());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					subFolderBranch = new TreeItem(folderBranch, SWT.NULL);
					subFolderBranch.setText("Mask(s)");
					subFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					for (Object key : viewerConfig.getTargetAppSourceCodePathsByMask().keySet()) {
						List values = (List)viewerConfig.getTargetAppSourceCodePathsByMask().get(key);
						subSubFolderBranch = new TreeItem(subFolderBranch, SWT.NULL);
						subSubFolderBranch.setText(""+key);
						subSubFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						for (Object value : values) {
							attribute = new TreeItem(subSubFolderBranch, SWT.NULL);
							attribute.setText(""+value);
							childCategory = ((File)value).isDirectory()?"folder":((File)value).getName().endsWith(".xml")?"xml":((File)value).getName().endsWith(".jar")?"jar":((File)value).getName().endsWith(".class")?"class":"file";
							attribute.setImage((Image)getNodeCategorys().get(childCategory)[0]);
						}
					}
					//..Object Code Path(s)
					folderBranch = new TreeItem(subBranch, SWT.NULL);
					folderBranch.setText("Object Code Path(s)");
					folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("targetAppObjectCodeBasePath=" + viewerConfig.getTargetAppObjectCodeBasePath());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					subFolderBranch = new TreeItem(folderBranch, SWT.NULL);
					subFolderBranch.setText("Mask(s)");
					subFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					for (Object key : viewerConfig.getTargetAppObjectCodePathsByMask().keySet()) {
						List values = (List)viewerConfig.getTargetAppObjectCodePathsByMask().get(key);
						subSubFolderBranch = new TreeItem(subFolderBranch, SWT.NULL);
						subSubFolderBranch.setText(""+key);
						subSubFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						for (Object value : values) {
							attribute = new TreeItem(subSubFolderBranch, SWT.NULL);
							attribute.setText(""+value);
							childCategory = ((File)value).isDirectory()?"folder":((File)value).getName().endsWith(".xml")?"xml":((File)value).getName().endsWith(".jar")?"jar":((File)value).getName().endsWith(".class")?"class":"file";
							attribute.setImage((Image)getNodeCategorys().get(childCategory)[0]);
						}
					}
					//..Class Path Entry(s)
					folderBranch = new TreeItem(subBranch, SWT.NULL);
					folderBranch.setText("Class Path Entry(s)");
					folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("targetAppClasspathBasePath=" + viewerConfig.getTargetAppClasspathBasePath());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					subFolderBranch = new TreeItem(folderBranch, SWT.NULL);
					subFolderBranch.setText("Mask(s)");
					subFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					for (Object key : viewerConfig.getTargetAppClasspathPathsByMask().keySet()) {
						List values = (List)viewerConfig.getTargetAppClasspathPathsByMask().get(key);
						subSubFolderBranch = new TreeItem(subFolderBranch, SWT.NULL);
						subSubFolderBranch.setText(""+key);
						subSubFolderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
						for (Object value : values) {
							attribute = new TreeItem(subSubFolderBranch, SWT.NULL);
							attribute.setText(""+value);
							childCategory = ((File)value).isDirectory()?"folder":((File)value).getName().endsWith(".xml")?"xml":((File)value).getName().endsWith(".jar")?"jar":((File)value).getName().endsWith(".class")?"class":"file";
							attribute.setImage((Image)getNodeCategorys().get(childCategory)[0]);
						}
					}
					//..Thread Pool
					folderBranch = new TreeItem(subBranch, SWT.NULL);
					folderBranch.setText("Thread Pool");
					folderBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("poolCoreSize=" + viewerConfig.getThreadPoolOptionPoolCoreSize());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("poolMaximumSize=" + viewerConfig.getThreadPoolOptionPoolMaximumSize());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("queueMaximumSize=" + viewerConfig.getThreadPoolOptionQueueMaximumSize());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("queueFairOrder=" + viewerConfig.getThreadPoolOptionQueueFairOrder());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("preStartCoreThreads=" + viewerConfig.getThreadPoolOptionPreStartCoreThreads());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("keepAliveTime=" + viewerConfig.getThreadPoolOptionKeepAliveTime());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("queueType=" + viewerConfig.getThreadPoolOptionQueueType());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("rejectionPolicy=" + viewerConfig.getThreadPoolOptionRejectionPolicy());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(folderBranch, SWT.NULL);
					attribute.setText("shutdownTimeoutThreshold=" + viewerConfig.getThreadPoolShutdownTimeoutThreshold());
					attribute.setImage((Image)getNodeCategorys().get("attr")[0]);
					attribute = new TreeItem(subBranch, SWT.NULL);
					attribute.setText("source=" + viewerConfig.getConfigFileTarget());
					attribute.setImage((Image)getNodeCategorys().get("xml")[0]);
			//	} else {
			//		subBranch.setText(""+child);
				}
			}
		}
	}

	/**
	 * Create/add a <code>SimpleTreeNode</code> branch having <code>Class</code> information
	 * for the specified <code>AbstractVO</code> to the specified parent node.
	 * 
	 * @param parent SimpleTreeNode reference to the parent node.
	 * @param avo    AbstractVO containing the parsed data for the information to be used.
	 * @see #createBranchClass(SimpleTreeNode, Class, Class[], Class[], Constructor[], Field[], Method[])
	 */
    public static void createBranchClassFromAbstractVO(TreeItem parent, AbstractVO avo) {
		if (parent != null && avo != null) {
			Class         superClass           = null;
			Class[]       interfaces           = null;
			Class[]       declaredClasses      = null;
			Constructor[] declaredConstructors = null;
			Field[]       declaredFields       = null;
			Method[]      declaredMethods      = null;
			if (avo instanceof StrutsActionFormBean) {
				StrutsActionFormBean safb = (StrutsActionFormBean)avo;
				superClass                        = safb.getSuperClass();
				interfaces                        = safb.getInterfaces();
				declaredClasses                   = safb.getDeclaredClasses();
				declaredConstructors              = safb.getDeclaredConstructors();
				declaredFields                    = safb.getDeclaredFields();
				declaredMethods                   = safb.getDeclaredMethods();
			} else /*if (avo instanceof StrutsActionMapping)*/ {
				StrutsActionMapping sam = (StrutsActionMapping)avo;
				superClass                      = sam.getSuperClass();
				interfaces                      = sam.getInterfaces();
				declaredClasses                 = sam.getDeclaredClasses();
				declaredConstructors            = sam.getDeclaredConstructors();
				declaredFields                  = sam.getDeclaredFields();
				declaredMethods                 = sam.getDeclaredMethods();
			}
			createBranchClass(parent, superClass, interfaces, declaredClasses, declaredConstructors, declaredFields, declaredMethods);
		}
    }

	/********************
	 * Helper method(s) *
	 ********************/
	
    /**
	 * Create/add a <code>SimpleTreeNode</code> branch having <code>Class</code> information
	 * based on the specified information to the specified parent node.
	 * 
	 * @param parent               SimpleTreeNode reference to the parent node.
     * @param superClass           Class                 containing a reference to the superClass.
     * @param interfaces           Array of Interfaces   containing all interfaces.
     * @param declaredClasses      Array of Classes      containing all (inner) classes.
     * @param declaredConstructors Array of Constructors containing all constructors.
     * @param declaredFields       Array of Fields       containing all fields.
     * @param declaredMethods      Array of Methods      containing all methods.
     */
    private static void createBranchClass(TreeItem parent,
    		Class superClass, Class[] interfaces,
    		Class[] declaredClasses, Constructor[] declaredConstructors,
    		Field[] declaredFields, Method[] declaredMethods) {
    	if (parent != null) {
    		/***********
    		 * Methods *
    		 ***********/
    		Set getters = new TreeSet();
    		Set setters = new TreeSet();
    		if (declaredMethods != null /*&& declaredMethods.length > 0*/) {
    			TreeItem declaredMethodsBranch = new TreeItem(parent, SWT.NULL);
    			declaredMethodsBranch.setText("Method(s)");
    			declaredMethodsBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
    			for (int x = 0; x < declaredMethods.length; x++) {
    				createBranchClassMethod(declaredMethodsBranch, null, declaredMethods[x], getters, setters);
    			}
    		}
    		/**********
    		 * Fields *
    		 **********/
    		if (declaredFields != null /*&& declaredFields.length > 0*/) {
    			TreeItem declaredFieldsBranch = new TreeItem(parent, SWT.NULL);
    			declaredFieldsBranch.setText("Field(s)");
    			declaredFieldsBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
    			for (int x = 0; x < declaredFields.length; x++) {
    				createBranchClassField(declaredFieldsBranch, null, declaredFields[x], getters, setters);
    			}
    		}
    		/****************
    		 * Constructors *
    		 ****************/
    		if (declaredConstructors != null /*&& declaredConstructors.length > 0*/) {
    			TreeItem declaredConstructorsBranch = new TreeItem(parent, SWT.NULL);
    			declaredConstructorsBranch.setText("Constructor(s)");
    			declaredConstructorsBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
    			for (int x = 0; x < declaredConstructors.length; x++) {
    				createBranchClassConstructor(declaredConstructorsBranch, null, declaredConstructors[x]);
    			}
    		}
    		/***********
    		 * Classes *
    		 ***********/
    		if (declaredClasses != null /*&& declaredClasses.length > 0*/) {
    			TreeItem declaredClassesBranch = new TreeItem(parent, SWT.NULL);
    			declaredClassesBranch.setText("Class(es)");
    			declaredClassesBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
    			for (int x = 0; x < declaredClasses.length; x++) {
    				createBranchClassClass(declaredClassesBranch, null, declaredClasses[x]);
    			}
    		}
    		/**************
    		 * Interfaces *
    		 **************/
    		if (interfaces != null /*&& interfaces.length > 0*/) {
    			TreeItem interfacesBranch = new TreeItem(parent, SWT.NULL);
    			interfacesBranch.setText("Interface(s)");
    			interfacesBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
    			for (int x = 0; x < interfaces.length; x++) {
    				createBranchClassClass(interfacesBranch, null, interfaces[x]);
    			}
    		}
    		/*******************
    		 * Additional Info *
    		 *******************/
    		if (superClass != null) {
    			TreeItem additionalInfoBranch = new TreeItem(parent, SWT.NULL);
    			additionalInfoBranch.setText("Additional Info");
    			additionalInfoBranch.setImage((Image)getNodeCategorys().get("folder")[0]);
    			createBranchClassClass(additionalInfoBranch, "superClass="+superClass.getName(), superClass);
    		//	new TreeItem(additionalInfoBranch, SWT.NULL);
    		//	additionalInfoBranch.setText("classDepth="+getClassDepth(superClass));
    		}
    		/***********************************
    		 * Remove any/all empty branch(es) *
    		 ***********************************/
    		for (int x = parent.getItemCount() - 1; x >= 0; x--) {
    			if (parent.getItem(x).getItemCount() == 0) {
    				parent.getItem(x).dispose();
    			}
    		}
    	}
    }
    
//	/**
//	 * Determine the class depth (how many superclass ancestors exist above it).
//	 *  
//	 * @param clazz Class to evaluate.
//	 * @return Byte indicating the number of superclass levels that exist above the specified class.
//	 */
//	private static byte getClassDepth(Class clazz) {
//		byte result = 0;
//		Class work = clazz;
//		while (work != null) {
//			result++;
//			work = work.getSuperclass();
//		}
//		return result;
//	}

	/**
	 * Create/add a branch for the specified Method to the specified parent branch.
	 * 
	 * @param parent  SimpleTreeNode referencing the parent of the new branch.
	 * @param label   String containing a specific value for the label (when empty 'name' is used).
	 * @param method  Method containing information for the new branch.
	 * @param getters Set of getters which can be updated here if the 'method' is a getter method.
	 * @param setters Set of setters which can be updated here if the 'method' is a setter method.
	 */
    private static void createBranchClassMethod(TreeItem parent, String label, Method method, Set getters, Set setters) {
		if (parent != null && method != null) {
			String name = method.getName();
			// Update set of getters/setters accordingly
			boolean isGetterSetter = false;
			int offset = 3;
			if ((name.startsWith("get") || name.startsWith("is")) &&
					method.getParameterTypes().length == 0 &&
					method.getReturnType() != null) {
				if (name.startsWith("is")) offset = 2;
				getters.add(name.substring(offset).toLowerCase());
				isGetterSetter = true;
			} else
			if (name.startsWith("set") &&
					method.getParameterTypes().length == 1 &&
					method.getReturnType() == Void.TYPE) {
				setters.add(name.substring(offset).toLowerCase());
				isGetterSetter = true;
			}
			// Skip getters/setters in the method list
			if (!isGetterSetter) {
				ClassTypeInfo classTypeInfo = getClassTypeInfo(method.getReturnType(), true, true);
				String formattedName = method.getName().substring(method.getName().lastIndexOf('.') + 1) + "(" + getClassTypeArrayAsString(method.getParameterTypes(), false, false) + ") : " + classTypeInfo.getOverview();
				// Add branch for this method
				TreeItem declaredMethodBranch = new TreeItem(parent, SWT.NULL);
				declaredMethodBranch.setText(label != null?label:formattedName);
				declaredMethodBranch.setImage((Image)getNodeCategorys().get(getScopeBasedCategory(false, Modifier.toString(method.getModifiers())))[0]);
				// Add info branches
				if (!INCLUDE_CLASS_INFO_DETAIL) return;
				TreeItem attribute = null;
			//	declaredMethodBranch.add(new SimpleTreeNode("isAccessible="   + declaredMethod.isAccessible()                   , declaredMethodBranch));
			//	declaredMethodBranch.add(new SimpleTreeNode("declaringClass=" + declaredMethod.getDeclaringClass().getName()    , declaredMethodBranch));
/*				declaredMethodBranch.add(*/attribute = new TreeItem(declaredMethodBranch, SWT.NULL)/*)*/;
				attribute.setText("modifiers="      + Modifier.toString(method.getModifiers()));
/*				declaredMethodBranch.add(*/attribute = new TreeItem(declaredMethodBranch, SWT.NULL)/*)*/;
				attribute.setText("name="           + formattedName);
				createBranchClassTypeArray(method.getParameterTypes(), true, true, declaredMethodBranch, "parameterTypes=" + getClassTypeArrayAsString(method.getParameterTypes(), false, false));
				createBranchClassTypeArray(method.getExceptionTypes(), true, true, declaredMethodBranch, "exceptionTypes=" + getClassTypeArrayAsString(method.getExceptionTypes(), false, false));
/*				declaredMethodBranch.add(*/attribute = new TreeItem(declaredMethodBranch, SWT.NULL);
				attribute.setText("returnType=" + classTypeInfo.getOverview());
				attribute.setImage(classTypeInfo.getCategory() == null?null:(Image)getNodeCategorys().get(getScopeBasedCategory(false, classTypeInfo.getCategory()))[0]);
				attribute.setData(classTypeInfo.getLinkTarget());
			}
		}
    }
    
	/**
	 * Create/add a branch for the specified Field to the specified parent branch.
	 * 
	 * @param parent  SimpleTreeNode referencing the parent of the new branch.
	 * @param label   String containing a specific value for the label (when empty 'name' is used).
	 * @param field   Field containing information for the new branch.
	 * @param getters Set of getters which is used to determine if the 'field' contains a getter method.
	 * @param setters Set of setters which is used to determine if the 'field' contains a setter method.
	 */
    private static void createBranchClassField(TreeItem parent, String label, Field field, Set getters, Set setters) {
    	if (parent != null && field != null) {
			String name = field.getName();
			// Determine whether this field has getter/setter
			boolean hasGetter = getters.contains(name.toLowerCase());
			boolean hasSetter = setters.contains(name.toLowerCase());
			String  category = null;
			if (hasGetter && hasSetter) category = "classGS";
			else if (hasGetter) category = "classG";
			else if (hasSetter) category = "classS";
			// Add branch for this field
			ClassTypeInfo classTypeInfo = getClassTypeInfo(field.getType(), true, true);
			TreeItem fieldBranch = new TreeItem(parent, SWT.NULL);
			fieldBranch.setText(label != null?label:field.getName() + " : " + classTypeInfo.getOverview());
			fieldBranch.setImage((Image)getNodeCategorys().get(getScopeBasedCategory(true, Modifier.toString(field.getModifiers())))[0]);
			// Add info branches
			if (!INCLUDE_CLASS_INFO_DETAIL) return;
			TreeItem attribute = null;
		//	fieldBranch.add(new SimpleTreeNode("isAccessible="   + field.isAccessible()                   , parent));
		//	fieldBranch.add(new SimpleTreeNode("declaringClass=" + field.getDeclaringClass().getName()    , parent));
/*			fieldBranch.add(*/attribute = new TreeItem(fieldBranch, SWT.NULL);
			attribute.setText("name="           + name);
			attribute.setImage(category == null?null:(Image)getNodeCategorys().get(category)[0]);
/*			fieldBranch.add(*/attribute = new TreeItem(fieldBranch, SWT.NULL);
			attribute.setText("modifiers="      + Modifier.toString(field.getModifiers()));
/*			fieldBranch.add(*/attribute = new TreeItem(fieldBranch, SWT.NULL);
			attribute.setText("type=" + classTypeInfo.getOverview());
			attribute.setImage(getNodeCategorys().get(classTypeInfo.getCategory()) == null?null:(Image)getNodeCategorys().get(classTypeInfo.getCategory())[0]);
			attribute.setData(classTypeInfo.getLinkTarget());
    	}
    }
    
	/**
	 * Create/add a branch for the specified Constructor to the specified parent branch.
	 * 
	 * @param parent      SimpleTreeNode referencing the parent of the new branch.
	 * @param label       String containing a specific value for the label (when empty 'name' is used).
	 * @param constructor Constructor containing information for the new branch.
	 */
    private static void createBranchClassConstructor(TreeItem parent, String label, Constructor constructor) {
    	if (parent != null && constructor != null) {
			// Skip constructors w/o names
			if (StringUtils.isNotEmpty(constructor.getName())) {
				String formattedName = constructor.getName().substring(constructor.getName().lastIndexOf('.') + 1) + "(" + getClassTypeArrayAsString(constructor.getParameterTypes(), false, false) + ")";
				// Add branch for this method
				TreeItem constructorBranch = new TreeItem(parent, SWT.NULL);
				constructorBranch.setText(label != null?label:formattedName);
				constructorBranch.setImage((Image)getNodeCategorys().get(getScopeBasedCategory(false, Modifier.toString(constructor.getModifiers())))[0]);
				// Add info branches
				if (!INCLUDE_CLASS_INFO_DETAIL) return;
				TreeItem attribute = null;
			//	constructorBranch.add(new SimpleTreeNode("isArray="        + constructor.isAccessible()                   , constructorBranch));
			//	constructorBranch.add(new SimpleTreeNode("declaringClass=" + constructor.getDeclaringClass().getName()    , constructorBranch));
/*				constructorBranch.add(*/attribute = new TreeItem(constructorBranch, SWT.NULL);
				attribute.setText("modifiers="      + Modifier.toString(constructor.getModifiers()));
/*				constructorBranch.add(*/attribute = new TreeItem(constructorBranch, SWT.NULL);
				attribute.setText("name="           + formattedName);
				createBranchClassTypeArray(constructor.getParameterTypes(), true, true, constructorBranch, "parameterTypes=" + getClassTypeArrayAsString(constructor.getParameterTypes(), false, false));
				createBranchClassTypeArray(constructor.getExceptionTypes(), true, true, constructorBranch, "exceptionTypes=" + getClassTypeArrayAsString(constructor.getExceptionTypes(), false, false));
			}
    	}
    }
    
	/**
	 * Create/add a branch for the specified Class to the specified parent branch.
	 * 
	 * @param parent SimpleTreeNode referencing the parent of the new branch.
	 * @param label  String containing a specific value for the label (when empty 'name' is used).
	 * @param clazz  Class containing information for the new branch.
	 */
	private static void createBranchClassClass(TreeItem parent, String label, Class clazz) {
		if (parent != null && clazz != null) {
			// Skip classes w/o names
			if (StringUtils.isNotEmpty(clazz.getName())) {
				ClassTypeInfo classTypeInfo = getClassTypeInfo(clazz, true, true);
				String        className     = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
				String        classPackage  = null;
				if (clazz.getPackage() != null) classPackage = clazz.getPackage().getName().substring(clazz.getPackage().getName().lastIndexOf(' ') + 1);
				// Add branch for this method
				TreeItem classBranch = new TreeItem(parent, SWT.NULL);
				classBranch.setText(label != null?label:clazz.getName());
				classBranch.setImage(getNodeCategorys().get(classTypeInfo.getCategory()) == null?null:(Image)getNodeCategorys().get(classTypeInfo.getCategory())[0]);
				classBranch.setData(classTypeInfo.getLinkTarget());
				// Add info branches
				if (!INCLUDE_CLASS_INFO_DETAIL) return;
				TreeItem attribute = null;
			//	classBranch.add(new SimpleTreeNode("isArray="        + clazz.isArray()                        , classBranch));
			//	classBranch.add(new SimpleTreeNode("isPrimitive="    + clazz.isPrimitive()                    , classBranch));
			//	classBranch.add(new SimpleTreeNode("declaringClass=" + clazz.getDeclaringClass().getName()    , classBranch));
/*				classBranch.add(*/attribute = new TreeItem(classBranch, SWT.NULL);
				attribute.setText("modifiers="      + Modifier.toString(clazz.getModifiers()));
/*				classBranch.add(*/attribute = new TreeItem(classBranch, SWT.NULL);
				attribute.setText("name="           + className);
/*				classBranch.add(*/attribute = new TreeItem(classBranch, SWT.NULL);
				attribute.setText("package="        + classPackage);
/*				classBranch.add(*/attribute = new TreeItem(classBranch, SWT.NULL);
				attribute.setText("isInterface="    + clazz.isInterface());
				createBranchClassTypeArray(clazz.getInterfaces(), true, true, classBranch, "interfaces=" + getClassTypeArrayAsString(  clazz.getInterfaces(), false, false));
				createBranchObjectArray(   clazz.getSigners()               , classBranch, "signers="    + getObjectArrayAsString(clazz.getSigners()                 ));
/*				classBranch.add(*/attribute = new TreeItem(classBranch, SWT.NULL);
				attribute.setText("type=" + classTypeInfo.getOverview());
				attribute.setImage(getNodeCategorys().get(classTypeInfo.getCategory()) == null?null:(Image)getNodeCategorys().get(classTypeInfo.getCategory())[0]);
				attribute.setData(classTypeInfo.getLinkTarget());
			//	if (clazz.getSuperclass() != null && !TreeUtils.existsAsAncestor(new SimpleTreeNode(clazz.getSuperclass().getName()), parent)) { // End recursion
			//		createBranchClassProperties(classBranch, clazz.getSuperclass(), clazz.getInterfaces(), clazz.getDeclaredClasses(), clazz.getDeclaredConstructors(), clazz.getDeclaredFields(), clazz.getDeclaredMethods());
			//	}
			}
		}
	}
	
	/**
	 * Convert a types array into a <code>SimpleTreeNode</code> of nicely formatted values.
	 * 
	 * @param typeArray    Class array of types (parameters, exceptions, interfaces, etc).
	 * @param objectTypes  Boolean indicating whether to include object types (primitive, class, interface).
	 * @param packageNames Boolean indicating whether to include package names.
	 * @param parent       SimpleTreeNode reference to the parent of the new immediate child node.
	 * @param childLabel   String containing the label to use for the immediate child node.
	 * @see #getTypeAsString(Class, boolean, boolean)
	 * @see #getTypeLinkTarget(Class)
	 */
	private static void createBranchClassTypeArray(Class[] typeArray, boolean objectTypes, boolean packageNames, TreeItem parent, String childLabel) {
		if (typeArray != null && typeArray.length > 0 && parent != null) {
	    	TreeItem typesBranch = new TreeItem(parent, SWT.NULL);
	    	typesBranch.setText(childLabel);
	    	typesBranch.setImage((Image)getNodeCategorys().get("attr")[0]);
			for (int x = 0; x < typeArray.length; x++) {
				ClassTypeInfo classTypeInfo = getClassTypeInfo(typeArray[x], objectTypes, packageNames);
				TreeItem attribute = null;
/*				typesBranch.add(*/attribute = new TreeItem(typesBranch, SWT.NULL);
				attribute.setText(classTypeInfo.getOverview());
				attribute.setImage(getNodeCategorys().get(classTypeInfo.getCategory()) == null?null:(Image)getNodeCategorys().get(classTypeInfo.getCategory())[0]);
				attribute.setData(classTypeInfo.getLinkTarget());
			//	createBranchClass(typesBranch, typeArray[x]);
			//	createBranchClassProperties(typesBranch, typeArray[x].getSuperclass(), typeArray[x].getInterfaces(), typeArray[x].getDeclaredClasses(), typeArray[x].getDeclaredConstructors(), typeArray[x].getDeclaredFields(), typeArray[x].getDeclaredMethods());
			}
		}
	}

	/**
	 * Convert an object array into a <code>SimpleTreeNode</code> of nicely formatted values.
	 * 
	 * @param objectArray  Object array of generic values.
	 * @param parent       SimpleTreeNode reference to the parent of the new immediate child node.
	 * @param childLabel   String containing the label to use for the immediate child node.
	 * @see #createBranchClassTypeArray(Class[], boolean, boolean, SimpleTreeNode, String)
	 */
	private static void createBranchObjectArray(Object[] objectArray, TreeItem parent, String childLabel) {
		if (objectArray != null && objectArray.length > 0 && parent != null) {
	    	TreeItem objectsBranch = new TreeItem(parent, SWT.NULL);
	    	objectsBranch.setText(childLabel);
	    	objectsBranch.setData(objectArray);
		}
	}
	
	/**
	 * Obtain info pertaining to the specified type.
	 * 
	 * @param type         Class for the type.
	 * @param objectTypes  Boolean indicating whether to include object types (primitive, array, class, interface).
	 * @param packageNames Boolean indicating whether to include package names.
	 * @return String array containing the information described above.
	 */
	private static ClassTypeInfo getClassTypeInfo(Class type, boolean objectTypes, boolean packageNames) {
		ClassTypeInfo result = new ClassTypeInfo();
		if (type != null) {
			final String BRACKET_L = "[";
			final String BRACKET_R = "]";
			final String SPACE     = " ";
		//	final String DOLLAR    = "$";
			final String PERIOD    = ".";
			final String CLASS     = "class";
			final String PRIMITIVE = "primitive";
			final String ARRAY     = "array";
			final String INTERFACE = "interface";
			final String VOID      = "void";
			
			String objectType  = null;
			String objectClass = null;
			
			/************
			 * Overview *
			 ************/
			StringBuffer overview = new StringBuffer();
			if (type == Void.TYPE) {
				if (objectTypes) {
					overview.append(BRACKET_L + VOID + BRACKET_R);
				}
			} else {
				objectType  = type.isPrimitive()?PRIMITIVE:type.isArray()?ARRAY:type.isInterface()?INTERFACE:CLASS; 
				objectClass = type.getName();
				// Work-around for Object arrays
				if (type.isArray()) {
					final String ARRAY_PREFIX = "[L";
					final String ARRAY_SUFFIX = ";";
					int firstPrefix = objectClass.indexOf(ARRAY_PREFIX);
					if (firstPrefix >= 0) {
						int firstSuffix = objectClass.indexOf(ARRAY_SUFFIX, firstPrefix);
						if (firstSuffix >= 0) {
							objectClass = objectClass.substring(firstPrefix + ARRAY_PREFIX.length(), firstSuffix);
						}
					}
				}
				if (!packageNames) {
					int lastSeparator = -1; //objectClass.lastIndexOf(DOLLAR);
					if (lastSeparator < 0) lastSeparator = objectClass.lastIndexOf(PERIOD);
					objectClass = objectClass.substring(lastSeparator + 1);
				}
				if (objectTypes) {
					overview.append(BRACKET_L + objectType + BRACKET_R + SPACE);
				}
				overview.append(objectClass);
			}
			result.setOverview(overview.toString());
			
			/************
			 * Category *
			 ************/
    		result.setCategory(objectType);
			
    		/***************
    		 * Link Target *
    		 ***************/
    		String linkTarget = null;
    		if (type != Void.TYPE && !type.isPrimitive() && objectClass != null) {
   				final ViewerConfig vc = StrutsTilesView.getViewerConfig();
   				if (objectClass.startsWith(vc.getTargetAppBasePackage())) {
   					linkTarget = MiscUtils.findResource(vc.getTargetAppSourceCodePathMasks(), vc.getTargetAppSourceCodePathsByMask(), objectClass.replaceAll("[.]", "/") + ".java");
   					if (MiscUtils.getFileSystemObjectLastModified(linkTarget) == null) {
   						linkTarget = null;
   					}
    			}
    		}
    		result.setLinkTarget(linkTarget);
		}
		return result;
	}
	
//	/**
//	 * Obtain a link target for the specified class type.
//	 * 
//	 * @param type Class for the type.
//	 * @return String containing the link target for the specified type (or <code>null</code> when not applicable).
//	 */
//	private static String getClassTypeLinkTarget(Class type) {
//		String result = null;
//		if (type != null && type != Void.TYPE && !type.isPrimitive()) {
//			String[] typeInfo = type.toString().split(" ");
//			if (typeInfo.length >= 2) {
//				ViewerConfig vc = Viewer.getViewerConfig();
//				if (typeInfo[1].startsWith(vc.getTargetAppBasePackage())) {
//					result = vc.getTargetAppSourceCodeBasePath() + "/src/" + typeInfo[1].replaceAll("[.]", "/") + ".java";
//					if (MiscUtils.getFileSystemObjectLastModified(result) == null) {
//						result = null;
//					}
//				}
//			}
//		}
//		return result;
//	}

//	/**
//	 * Obtain the base category (primitive) for the specified class type.
//	 * 
//	 * @param type Class for the type.
//	 * @return String containing the category for the specified type (or <code>null</code> when not applicable).
//	 */
//	private static String getClassTypeCategory(Class type) {
//		String result = null;
//		if (type != null) {
//			String work = getClassTypeAsString(type, true, false);
//			if (work != null) {
//				String[] temp = work.split(" ");
//				result = temp[0].substring(1, temp[0].length() - 1);
//			}
//		}
//		return result;
//	}
    
//	/**
//	 * Convert a type into a nicely formatted value.
//	 * 
//	 * @param type         Class for the type.
//	 * @param objectTypes  Boolean indicating whether to include object types (primitive, array, class, interface).
//	 * @param packageNames Boolean indicating whether to include package names.
//	 * @return String containing a nicely formatted representation of the type.
//	 */
//	private static String getClassTypeAsString(Class type, boolean objectTypes, boolean packageNames) {
//		StringBuffer result = new StringBuffer();
//		if (type != null) {
//			final String BRACKET_L = "[";
//			final String BRACKET_R = "]";
//			final String SPACE     = " ";
//		//	final String DOLLAR    = "$";
//			final String PERIOD    = ".";
//			final String CLASS     = "class";
//			final String PRIMITIVE = "primitive";
//			final String ARRAY     = "array";
//			final String INTERFACE = "interface";
//			final String VOID      = "void";
//			if (type == Void.TYPE) {
//				if (objectTypes) {
//					result.append(BRACKET_L + VOID + BRACKET_R);
//				}
//			} else {
//				String objectType  = type.isPrimitive()?PRIMITIVE:type.isArray()?ARRAY:type.isInterface()?INTERFACE:CLASS;
//				String objectClass = type.getName();
//				// Work-around for Object arrays
//				if (type.isArray()) {
//					final String ARRAY_PREFIX = "[L";
//					final String ARRAY_SUFFIX = ";";
//					int firstPrefix = objectClass.indexOf(ARRAY_PREFIX);
//					if (firstPrefix >= 0) {
//						int firstSuffix = objectClass.indexOf(ARRAY_SUFFIX, firstPrefix);
//						if (firstSuffix >= 0) {
//							objectClass = objectClass.substring(firstPrefix + ARRAY_PREFIX.length(), firstSuffix);
//						}
//					}
//				}
//				if (!packageNames) {
//					int lastSeparator = -1; //objectClass.lastIndexOf(DOLLAR);
//					if (lastSeparator < 0) lastSeparator = objectClass.lastIndexOf(PERIOD);
//					objectClass = objectClass.substring(lastSeparator + 1);
//				}
//				if (objectTypes) {
//					result.append(BRACKET_L + objectType + BRACKET_R + SPACE);
//				}
//				result.append(objectClass);
//			}
//		}
//		return result.toString();
//	}
    
	/**
	 * Convert a class type array into a (comma-delimited) list of nicely formatted values.
	 * 
	 * @param typeArray    Class array of types (parameters, exceptions, interfaces, etc).
	 * @param objectTypes  Boolean indicating whether to include object types (primitive, class, interface).
	 * @param packageNames Boolean indicating whether to include package names.
	 * @return String containing a nicely formatted representation of the types.
	 * @see #getTypeAsString(Class, boolean, boolean)
	 */
	private static String getClassTypeArrayAsString(Class[] typeArray, boolean objectTypes, boolean packageNames) {
		StringBuffer result = new StringBuffer();
		if (typeArray != null && typeArray.length > 0) {
			final String SPACE = " ";
			final String COMMA = ",";
			for (int x = 0; x < typeArray.length; x++) {
				if (x > 0) result.append(COMMA + SPACE);
				result.append(getClassTypeInfo(typeArray[x], objectTypes, packageNames).getOverview());
			}
		}
    	return result.toString();
	}
	
	/**
	 * Convert an object array into a (comma-delimited) list of nicely formatted values.
	 * 
	 * @param objectArray Object array to convert.
	 * @return String containing a nicely formatted representation of the objects.
	 */
	private static String getObjectArrayAsString(Object[] objectArray) {
		String result = null;
		if (objectArray != null) {
			result = ArrayUtils.toString(objectArray);
			if (result != null && result.length() >= 2) {
				result = result.substring(1, result.length() - 1);
			}
		}
		return result;
	}

    /**
     * Determine the appropriate category for the field/method based on its modifierText.
     * 
     * @param isField      Boolean indicating whether it's a field (vs method).
     * @param modifierText String containing the field/method modifiers.
     * @return String containing the corresponding category.
     */
    private static String getScopeBasedCategory(boolean isField, String modifierText) {
    	StringBuffer category = new StringBuffer();
   		category.append(isField?"field":"method");
   		category.append(".");
   		if      (modifierText != null && modifierText.indexOf("private"  ) >= 0) category.append("private"  );
   		else if (modifierText != null && modifierText.indexOf("protected") >= 0) category.append("protected");
   		else if (modifierText != null && modifierText.indexOf("public"   ) >= 0) category.append("public"   );
   		else                                                                     category.append("default"  );
    	return category.toString();
    }
    
}
