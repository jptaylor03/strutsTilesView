package strutstilesview.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Utilities for miscellaneous things.
 */
public class MiscUtils {

	/**
	 * Enumeration for possible server locations.
	 */
	public enum ServerLocation {
		BASE_INSTALL,
		HOT_DEPLOY,
		HOT_DEPLOY_WORKSPACE,
		HOT_DEPLOY_SERVER
	}

	/**
	 * Folder/File Names
	 */
	private static final String WORKSPACE_LOC = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + "/.metadata/.plugins";
	private static final String SERVER_PROFILES_INSTALLED_APPS = "/installedApps";
	private static final String SERVER_PROFILES_INSTALLED_APPS_TYPE = ".ear";
	private static final String WAS_INSTALL_CONF = WORKSPACE_LOC+"/com.ibm.ws.ast.st.core/wasInstallConfigCache.xml";
	private static final String SERVER_LOC = WORKSPACE_LOC+"/org.eclipse.wst.server.core";
	private static final String SERVER_TEMP_DATA = "/tmp-data.xml";
	private static final String SERVER_SETTINGS = "/servers.xml";

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Logger instance.
	 */
	protected static final Log logger = LogFactory.getLog("util");
	
	/**
	 * DocumentBuilderFactory instance.
	 */
	private static DocumentBuilderFactory dbf = null;
	
	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Obtain a resource (from either inside a JAR or on the filesystem) and
	 * output its contents to a temporary file on the filesystem.
	 * 
	 * @param baseClass    Class to be used as a location reference point to help find the resource.
	 * @param resourceName String containing the name of the resource.
	 * @return String containing the (path and) name of the temporary file
	 *         which has just been created using the contents of the resource.
	 */
	public static String resourceToTempFile(Class baseClass, String resourceName) {
		String result = null;
		InputStream  inputStream  = null;
		OutputStream outputStream = null;
		try {
			inputStream = baseClass.getResourceAsStream(resourceName);
			String[] resource = new File(resourceName).getName().split("[.]");
			File file = File.createTempFile(resource[0], "." + (resource.length == 0?"tmp":resource[1]));
			outputStream = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf,0,len);
			}
			result = file.getAbsolutePath();
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return result;
	}
	
	/**
	 * Parse the specified xml file/resource into a {@link org.w3c.dom.Document} object. 
	 * 
	 * @param baseClass     Class to be used as a location reference point to help find the resource.
	 * @param xmlFileTarget String containing the name of the file/resource.
	 * @return Document object based on the contents of the target xml file.
	 */
	public static Document parseXmlFile(Class baseClass, String xmlFileTarget){
		Document result = null;
		
		// Obtain the factory
		if (dbf == null) {
			dbf = DocumentBuilderFactory.newInstance();
		}
		
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setEntityResolver(new MyEntityResolver(baseClass)); // [1.01]

			// Parse using builder to get DOM representation of the XML file
			URL url = baseClass.getResource(xmlFileTarget);
			if (url != null) {
				result = db.parse(url.openStream()); // Accessing JAR content
			} else {
				result = db.parse(xmlFileTarget);    // Accessing external content
			}
			logger.info("Parsed file: " + xmlFileTarget);
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Find the specified resource by iterating through the pathMasks.
	 * <p>
	 *  NOTE: The first occurance of the resource will be used.  Therefore, the
	 *        iteration order is very important.  Because of this, the 'pathMasks'
	 *        value is used to control the iteration order.
	 * </p>
	 * 
	 * @param pathMasks    List containing masks of paths (to iterate).
	 * @param pathsByMask  Map  containing paths keyed by mask (to search).
	 * @param resourceName String identifying the name of the target resource.
	 * @return String containing the absolute path of the target resource.
	 */
	public static String findResource(List pathMasks, Map pathsByMask, String resourceName) {
		String result = null;
		if (pathMasks != null && pathsByMask != null && StringUtils.isNotEmpty(resourceName)) {
			for (Iterator it = pathMasks.iterator(); it.hasNext() && result == null;) {
				String mask  = (String)it.next();
				List   paths = (List)pathsByMask.get(mask);
				if (paths != null) {
					for (Iterator itSub = paths.iterator(); itSub.hasNext() && result == null;) {
						File path = (File)itSub.next();
						File resource = new File(path, resourceName); 
						if (resource.exists()) result = resource.getAbsolutePath();
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Determine whether the specified file/resource is missing.
	 * 
	 * @param baseClass    Class to be used as a location reference point to help find the resource.
	 * @param resourceName String containing the name of the resource.
	 * @return Boolean indicating whether the file/resource is missing.
	 */
    public static boolean confirmResourceMissing(Class baseClass, String resourceName) {
    	boolean result = false;
    	if (baseClass != null && StringUtils.isNotEmpty(resourceName)) {
    		// Does the resource exist as a file on the file-system?
    		File file = new File(resourceName);
    		if (!file.exists()) { // No
    			// Does the linkTarget exist as a resource (inside this apps JAR)?
    			URL url = baseClass.getResource(resourceName);
    			if (url == null) { // No
    				// Record the missing resource
    				MissingResources.addSource(resourceName);
    				// Set the return value
    				result = true;
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * Obtain the specified file's 'lastModified' timestamp.
     * 
     * @param fileSystemObjectName String containing the name of the file.
     * @return Date containing the specified file's 'lastModified' timestamp (or <code>null</code> if not found).
     */
    public static Date getFileSystemObjectLastModified(String fileSystemObjectName) {
    	Date result = null;
    	if (fileSystemObjectName != null) {
    		File fileSystemObject = new File(fileSystemObjectName);
    		if (fileSystemObject.exists()) {
        		result = new Date(fileSystemObject.lastModified());
    		}
    	}
    	return result;
    }
    
	/**
	 * Create a viewer-config.xml based on the contents of the workspace.
	 * 
	 * @return Document containing a DOM of a viewer-config.xml based on the contents of the workspace.
	 */
	@SuppressWarnings("unchecked")
	public static Document buildViewerConfig() {
		Document dom = null;

		try {
			ResourceBundle configComment = ResourceBundle.getBundle("configComment");
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projects = workspace.getRoot().getProjects();
			IFolder folder = null;
			File file = null;
			StringBuffer buffer = null;
			String basePath = null;
			Node node = null;
			String appId = null;
			if (projects.length > 0) {
				buffer = new StringBuffer();
				for (IProject project : projects) {
					folder = project.getFolder("/src/main/application");
					if (folder.exists()) {
						if (buffer.length() > 0) buffer.append(",\n");
						buffer.append(project.getName());
					}
				}
				if (buffer.length() == 0) {
					logger.error("[Config] No applications found within the workspace (expected one).");
					return null;
				} else if (buffer.toString().split(",\n").length > 1) {
					logger.error("[Config] More than one application found (expected one): " + buffer.toString().replaceAll("\\n", ""));
					return null;
				} else {
					appId = buffer.toString();
					logger.info("[Config] appId=" + appId);
				}
			}

			dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			dom.setXmlVersion("1.0");
			Node root = dom.appendChild(dom.createElement("viewer-config"));
			// Config Files
			node = root.appendChild(dom.createComment("configComment.configFiles"));
			node.setTextContent(configComment.getString("configComment.configFiles"));
			node = root.appendChild(dom.createComment("configComment.configFiles.targetAppConfigFileBasePath"));
			node.setTextContent(configComment.getString("configComment.configFiles.targetAppConfigFileBasePath"));
			node = root.appendChild(dom.createElement("target-app-config-file-base-path"));
			if (projects.length > 0) {
				buffer = new StringBuffer();
				for (IProject project : projects) {
					folder = project.getFolder("/src/main/webapp/WEB-INF/conf");
					if (folder.exists()) {
						if (buffer.length() > 0) buffer.append(",\n");
						buffer.append(folder.getLocation());
					}
				}
				node.setTextContent(basePath = buffer.toString());
			}
			node = root.appendChild(dom.createComment("configComment.configFiles.targetAppConfigFileMasks"));
			node.setTextContent(configComment.getString("configComment.configFiles.targetAppConfigFileMasks"));
			node = root.appendChild(dom.createElement("target-app-config-file-masks"));
			node.setTextContent("struts-config.*[.]xml,\ntiles-defs.*[.]xml");
			// Source Code
			node = root.appendChild(dom.createComment("configComment.sourceCode"));
			node.setTextContent(configComment.getString("configComment.sourceCode"));
			node = root.appendChild(dom.createComment("configComment.sourceCode.targetAppSourceCodeBasePath"));
			node.setTextContent(configComment.getString("configComment.sourceCode.targetAppSourceCodeBasePath"));
			node = root.appendChild(dom.createElement("target-app-source-code-base-path"));
			if (projects.length > 0) {
				node.setTextContent(basePath = workspace.getRoot().getProjects()[0].getLocation().toString());
			}
			node = root.appendChild(dom.createComment("configComment.sourceCode.targetAppSourceCodePathMasks"));
			node.setTextContent(configComment.getString("configComment.sourceCode.targetAppSourceCodePathMasks"));
			node = root.appendChild(dom.createElement("target-app-source-code-path-masks"));
			if (projects.length > 0) {
				buffer = new StringBuffer();
				for (IProject project : projects) {
					folder = project.getFolder("/src/main/java");
					if (folder.exists()) {
						if (buffer.length() > 0) buffer.append(",\n");
						buffer.append(folder.getFullPath());
					}
				}
				node.setTextContent(buffer.toString());
			}
			// Object Code
			node = root.appendChild(dom.createComment("configComment.objectCode"));
			node.setTextContent(configComment.getString("configComment.objectCode"));
			node = root.appendChild(dom.createComment("configComment.objectCode.targetAppObjectCodeBasePath"));
			node.setTextContent(configComment.getString("configComment.objectCode.targetAppObjectCodeBasePath"));
			node = root.appendChild(dom.createElement("target-app-object-code-base-path"));
			if (projects.length > 0) {
				node.setTextContent(basePath = workspace.getRoot().getProjects()[0].getLocation().toString());
			}
			node = root.appendChild(dom.createComment("configComment.objectCode.targetAppObjectCodePathMasks"));
			node.setTextContent(configComment.getString("configComment.objectCode.targetAppObjectCodePathMasks"));
			node = root.appendChild(dom.createElement("target-app-object-code-path-masks"));
			if (projects.length > 0) {
				buffer = new StringBuffer();
				for (IProject project : projects) {
					folder = project.getFolder("/target/classes");
					if (folder.exists()) {
						if (buffer.length() > 0) buffer.append(",\n");
						buffer.append(folder.getFullPath());
					}
				}
				node.setTextContent(buffer.toString());
			}
			// Classpath
			node = root.appendChild(dom.createComment("configComment.classpath"));
			node.setTextContent(configComment.getString("configComment.classpath"));
			node = root.appendChild(dom.createComment("configComment.classpath.targetAppClasspathBasePath"));
			node.setTextContent(configComment.getString("configComment.classpath.targetAppClasspathBasePath"));
			node = root.appendChild(dom.createElement("target-app-classpath-base-path"));
			if (projects.length > 0) {
//				node.setTextContent(basePath = MiscUtils.obtainServerLocation(appId, MiscUtils.ServerLocation.HOT_DEPLOY));
//				node.setTextContent(basePath = workspace.getRoot().getLocation().toOSString().replace('\\', '/'));
//				node.setTextContent(basePath = projects[0].getLocation().toOSString().replace('\\', '/').substring(0, projects[0].getLocation().toOSString().lastIndexOf('/')).replace('\\', '/'));
				node.setTextContent(basePath = StringUtils.EMPTY);
			}
			node = root.appendChild(dom.createComment("configComment.classpath.targetAppClasspathPathMasks"));
			node.setTextContent(configComment.getString("configComment.classpath.targetAppClasspathPathMasks"));
			node = root.appendChild(dom.createElement("target-app-classpath-path-masks"));
			if (projects.length > 0) {
				buffer = new StringBuffer();
				buffer.append(MiscUtils.obtainServerLocation(appId, MiscUtils.ServerLocation.BASE_INSTALL) + "/plugins/javax.j2ee.servlet.jar");
				File[] matchingFiles = null;
				List<File> matchingFolders = new ArrayList<File>();
				ZipFile zipFile = null;
				ZipEntry zipEntry = null;
				String zipEntryTarget = null;
				for (IProject project : projects) {
					file = new File(project.getLocation() + "/src/main/application");
					if (file.exists() && file.isDirectory()) {
						file = new File(project.getLocation() + "/target/");
						matchingFiles = file.listFiles(new MyRegexFileFilter(project.getName() + ".*", ResourceType.DIRECTORIES));
						if (matchingFiles.length == 1) {
							matchingFolders.add(matchingFiles[0]);
							matchingFolders.add(new File(matchingFiles[0], "lib"));
							file = new File(project.getLocation() + "/target/temp/");
							matchingFiles = file.listFiles(new MyRegexFileFilter(".*[.]war", ResourceType.DIRECTORIES));
							for (File entry : matchingFiles) {
								matchingFolders.add(new File(project.getLocation() + "/target/temp/" + entry.getName() + "/WEB-INF/lib"));
							}
							for (File matchingFolder : matchingFolders) {
								matchingFiles = matchingFolder.listFiles(new MyRegexFileFilter(".*[.]jar", ResourceType.FILES));
								for (File member : matchingFiles) {
									if (member.isFile()) {
										if (member.getName().endsWith(".war")) {
											zipFile = new ZipFile(member.getAbsolutePath());
											for (Enumeration<ZipEntry> zipEnum = (Enumeration<ZipEntry>)zipFile.entries(); zipEnum.hasMoreElements();) {
												zipEntry = zipEnum.nextElement();
												if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("WEB-INF/lib/") && zipEntry.getName().endsWith(".jar")) {
													zipEntryTarget = (member.getAbsolutePath().substring(basePath.length()) + "!" + zipEntry.getName()).replace('\\', '/');
													if (buffer.indexOf("/" + zipEntryTarget) < 0 &&
														buffer.indexOf("\\" + zipEntryTarget) < 0) {
														if (buffer.length() > 0) buffer.append(",\n");
														buffer.append(zipEntryTarget);
													}
												}
											}
										} else {
											if (buffer.indexOf("/" + member.getName()) < 0 &&
												buffer.indexOf("\\" + member.getName()) < 0) {
												if (buffer.length() > 0) buffer.append(",\n");
												buffer.append(member.getAbsolutePath().substring(basePath.length()).replace('\\', '/'));
											}
										}
									}
								}
							}
						}
					}
				}
				node.setTextContent(buffer.toString());
			}
			node = root.appendChild(dom.createComment("configComment.miscellaenous"));
			node.setTextContent(configComment.getString("configComment.miscellaneous"));
			node = root.appendChild(dom.createComment("configComment.miscellaneous.targetAppBasePackage"));
			node.setTextContent(configComment.getString("configComment.miscellaneous.targetAppBasePackage"));
			node = root.appendChild(dom.createElement("target-app-base-package"));
			node.setTextContent("us.oh.state.odjfs.sacwis");
//			node = root.appendChild(dom.createComment("configComment.miscellaneous.textEditorExecutable"));
//			node.setTextContent(cfgComments.getString("configComment.miscellaneous.textEditorExecutable"));
//			node = root.appendChild(dom.createElement("text-editor-executable"));
//			node.setTextContent("C:/windows/notepad.exe");
			node = root.appendChild(dom.createComment("configComment.miscellaneous.recurseToShowSiblings"));
			node.setTextContent(configComment.getString("configComment.miscellaneous.recurseToShowSiblings"));
			node = root.appendChild(dom.createElement("recurse-to-show-siblings"));
			node.setTextContent("false");
			node = root.appendChild(dom.createComment("configComment.miscellaneous.threadpool"));
			node.setTextContent(configComment.getString("configComment.miscellaneous.threadpool"));
			Node threadpool = root.appendChild(dom.createElement("threadpool"));
			node = threadpool.appendChild(dom.createElement("poolCoreSize"));
			node.setTextContent("4");
			node = threadpool.appendChild(dom.createElement("poolMaximumSize"));
			node.setTextContent("8");
			node = threadpool.appendChild(dom.createElement("queueMaximumSize"));
			node.setTextContent("64");
			node = threadpool.appendChild(dom.createElement("queueFairOrder"));
			node.setTextContent("true");
			node = threadpool.appendChild(dom.createElement("preStartCoreThreads"));
			node.setTextContent("2");
			node = threadpool.appendChild(dom.createElement("keepAliveTime"));
			node.setTextContent("60000");
			node = threadpool.appendChild(dom.createElement("queueType"));
			node.setTextContent("2");
			node = threadpool.appendChild(dom.createElement("rejectionPolicy"));
			node.setTextContent("1");
			node = threadpool.appendChild(dom.createElement("shutdownTimeoutThreshold"));
			node.setTextContent("60000");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return dom;
	}

	/**
	 * Determine a specific server location for the specified application id.
	 * 
	 * @param applicationId String identifying the application id.
	 * @param serverLocation ServerLocation indicating the desired server location.
	 * @return String containing a specific server location for the specified application id.
	 */
	public static String obtainServerLocation(String applicationId, ServerLocation serverLocation) {
		String result = null;
		if (StringUtils.isNotBlank(applicationId)) {
			File serversFile = new File(SERVER_LOC+SERVER_SETTINGS);
			logger.debug("[Config] serverSettings=" + serversFile.getAbsolutePath() + " (" + (serversFile.exists()?"found":"missing") + ")");
			Document serversDom = MiscUtils.parseXmlFile(MiscUtils.class, serversFile.getAbsolutePath());
			for (int serversIndex = 0; serversIndex < serversDom.getChildNodes().getLength() && result == null; serversIndex++) {
				Node servers = serversDom.getChildNodes().item(serversIndex);
				if (servers.getNodeType() != Node.ELEMENT_NODE) continue;
				for (int serverIndex = 0; serverIndex < servers.getChildNodes().getLength() && result == null; serverIndex++) {
					Node server = servers.getChildNodes().item(serverIndex);
					if (server.getNodeType() != Node.ELEMENT_NODE) continue;
					for (int listIndex = 0; listIndex < server.getChildNodes().getLength() && result == null; listIndex++) {
						Node list = server.getChildNodes().item(listIndex);
						if (list.getNodeType() != Node.ELEMENT_NODE) continue;
						if (list.getAttributes().getNamedItem("key").getNodeValue().equals("modules")) {
							String serverId = server.getAttributes().getNamedItem("id").getNodeValue();
							logger.debug("[Config] serverId=" + serverId);
							for (int attrIndex = 0; attrIndex < list.getAttributes().getLength() && result == null; attrIndex++) {
								Node attr = list.getAttributes().item(attrIndex);
							//	if (attr.getNodeType() != Node.ELEMENT_NODE) continue;
								if (attr.getNodeName().startsWith("value") && !attr.getNodeValue().isEmpty()) {
									String appName = attr.getNodeValue().substring(0, attr.getNodeValue().indexOf(":"));
									logger.debug("[Config] appName=" + appName);
									if (appName.equalsIgnoreCase(applicationId)) {
										boolean isRunServerWithWorkspaceResources = Boolean.parseBoolean(server.getAttributes().getNamedItem("isRunServerWithWorkspaceResources").getNodeValue());
										logger.debug("[Config] isRunServerWithWorkspaceResources=" + isRunServerWithWorkspaceResources);
										if (isRunServerWithWorkspaceResources &&
											(ServerLocation.HOT_DEPLOY.equals(serverLocation) ||
											 ServerLocation.HOT_DEPLOY_WORKSPACE.equals(serverLocation))) {
											// Run server with resources within Workspace
											File tempDirectoriesFile = new File(SERVER_LOC+SERVER_TEMP_DATA);
											logger.debug("[Config] tempDirectories=" + tempDirectoriesFile.getAbsolutePath() + " (" + (tempDirectoriesFile.exists()?"found":"missing") + ")");
											Document tempDirectoriesDom = MiscUtils.parseXmlFile(MiscUtils.class, tempDirectoriesFile.getAbsolutePath());
											for (int tempDirectoriesIndex = 0; tempDirectoriesIndex < tempDirectoriesDom.getChildNodes().getLength() && result == null; tempDirectoriesIndex++) {
												Node tempDirectories = tempDirectoriesDom.getChildNodes().item(tempDirectoriesIndex);
												if (tempDirectories.getNodeType() != Node.ELEMENT_NODE) continue;
												for (int tempDirectoryIndex = 0; tempDirectoryIndex < tempDirectories.getChildNodes().getLength() && result == null; tempDirectoryIndex++) {
													Node tempDirectory = tempDirectories.getChildNodes().item(tempDirectoryIndex);
													if (tempDirectory.getNodeType() != Node.ELEMENT_NODE) continue;
													if (tempDirectory.getAttributes().getNamedItem("key").getNodeValue().equals(serverId)) {
														result = SERVER_LOC+"/"+tempDirectory.getAttributes().getNamedItem("path").getNodeValue();
													}
												}
											}
										} else {
											// Run server with resources on Server
											File wasInstallFile = new File(WAS_INSTALL_CONF);
											logger.debug("[Config] wasInstallConf=" + wasInstallFile.getAbsolutePath() + " (" + (wasInstallFile.exists()?"found":"missing") + ")");
											Document wasInstallDom = MiscUtils.parseXmlFile(MiscUtils.class, wasInstallFile.getAbsolutePath());
											for (int wasInstallIndex = 0; wasInstallIndex < wasInstallDom.getChildNodes().getLength() && result == null; wasInstallIndex++) {
												Node wasInstall = wasInstallDom.getChildNodes().item(wasInstallIndex);
												if (wasInstall.getNodeType() != Node.ELEMENT_NODE) continue;
												for (int wasInstallsIndex = 0; wasInstallsIndex < wasInstall.getChildNodes().getLength() && result == null; wasInstallsIndex++) {
													Node wasInstalls = wasInstall.getChildNodes().item(wasInstallsIndex);
													if (wasInstalls.getNodeType() != Node.ELEMENT_NODE) continue;
													for (int profileIndex = 0; profileIndex < wasInstalls.getChildNodes().getLength() && result == null; profileIndex++) {
														Node profile = wasInstalls.getChildNodes().item(profileIndex);
														if (profile.getNodeType() != Node.ELEMENT_NODE) continue;
														String profileName = profile.getAttributes().getNamedItem("name").getNodeValue();
														if (Boolean.parseBoolean(profile.getAttributes().getNamedItem("active").getNodeValue()) &&
															profileName.equals(server.getAttributes().getNamedItem("webSphereProfileName").getNodeValue())) {
															logger.debug("[Config] profileName=" + profileName);
															String profileLocation = profile.getAttributes().getNamedItem("location").getNodeValue().replace('\\', '/');
															if (profileLocation.endsWith("/")) {
																profileLocation = profileLocation.substring(0, profileLocation.length() - 1);
															}
															logger.debug("[Config] profileLocation=" + profileLocation);
															if (ServerLocation.HOT_DEPLOY.equals(serverLocation) || ServerLocation.HOT_DEPLOY_SERVER.equals(serverLocation)) {
																result = profileLocation + SERVER_PROFILES_INSTALLED_APPS + "/" + profileName + "/" + appName + SERVER_PROFILES_INSTALLED_APPS_TYPE;
															} else /*if (ServerLocation.BASE_INSTALL.equals(serverLocation))*/ {
																result = wasInstalls.getAttributes().getNamedItem("location").getNodeValue();
															}
														}
													}
												}
											}
											serverId = StringUtils.EMPTY;
										}
									}
								}
							}
						}
					}
				}
			}
			if (result != null) {
				result = result.replace('\\', '/');
			}
		}
		logger.debug("[Config] serverLocation=" + result);
		return result;
	}

	/**
	 * Creates a map (keyed by each mask) with all matching files.
	 * 
	 * @param basePath     String identifying the base path to use.
	 * @param masks        List containing the values to iterate over.
	 * @param resourceType ResourceType indicating whether to only include directories or files (or anything).
	 * @return Map (keyed by each mask) with all matching files.
	 */
	public static Map<String, List<File>> createFileRelatedMapFromList(String basePath, List<String> masks, ResourceType resourceType) {
		Map<String, List<File>> result = new TreeMap<String, List<File>>();
		basePath = StringUtils.defaultString(basePath).replace('\\', '/');
    	for (String mask : masks) {
    		mask = StringUtils.defaultString(mask).replaceAll("\\\\", "<<<<").replace('\\', '/').replaceAll("<<<<", "\\\\");
   			if (mask.contains(":")) {
   				// Determine "startingPoint" as the first occurrence of regex, or the last occurrence of "/"
   				int startingPoint = StringUtils.indexOfAny(mask, new String[]{ "?", "*", "\\" });
   				if (startingPoint >= 0) {
   					while (startingPoint > 0 && mask.charAt(startingPoint) != '/' && mask.charAt(startingPoint) != '!') {
   	   					startingPoint--;
   					}
   				} else {
   					startingPoint = Math.min(mask.indexOf('!') < 0?mask.length():mask.indexOf('!'), mask.lastIndexOf('/'));
   				}
   				createFileRelatedMapFromListUsingRecursion(mask.substring(0, startingPoint + (mask.charAt(startingPoint) == '!'?0:1)), StringUtils.EMPTY, mask.substring(startingPoint + 1), 0, resourceType, (mask.charAt(startingPoint) == '!'?true:false), result);
   			} else {
   				createFileRelatedMapFromListUsingRecursion(basePath, StringUtils.EMPTY, mask, 0, resourceType, false, result);
   			}
    	}
    	return result;
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	private static void createFileRelatedMapFromListUsingRecursion(String basePath, String relPath, String mask, int maskIndex, ResourceType resourceType, boolean withinArchive, Map<String, List<File>> result) {
		if (StringUtils.isNotEmpty(basePath + mask)) {
			String key = null;
			FileFilter fileFilter = null;
			if (withinArchive) {
				fileFilter = new MyRegexFileFilter(mask);
				try {
					ZipFile zipFile = new ZipFile(basePath);
					ZipEntry zipEntry = null;
					for (Enumeration<ZipEntry> zipEnum = (Enumeration<ZipEntry>)zipFile.entries(); zipEnum.hasMoreElements();) {
						// Must evaluate each entry (in sequential order)
						zipEntry = zipEnum.nextElement();
						if (((MyRegexFileFilter)fileFilter).accept(zipEntry)) {
							if ((ResourceType.FILES.equals(resourceType) && !zipEntry.isDirectory()) || (ResourceType.DIRECTORIES.equals(resourceType) && zipEntry.isDirectory())) {
								if (mask.contains(".") && "|.ear|.jar|.war|.zip|".contains(mask.substring(mask.lastIndexOf(".")).toLowerCase())) {
									key = mask.contains("/")?mask.substring(mask.lastIndexOf('/') + 1):mask;
									key = key.contains("-")?key.substring(0, key.indexOf("-")) + key.substring(key.lastIndexOf(".")):key;
									key = key.substring(0, key.lastIndexOf(".")) + "*" + key.substring(key.lastIndexOf("."));
								} else {
									key = mask;
								}
								List<File> work = result.get(key);
								if (work == null) {
									work = new ArrayList<File>();
									result.put(key, work);
								}
								work.add(new File(zipFile.getName() + "!" + zipEntry.getName()));
							}
						}
					}
				} catch (Exception e) {
					logger.error("Unable to open archive file: " + basePath, e);
				}
			} else {
				int maskIndexBang  = mask.indexOf("!", maskIndex + 1);
				int maskIndexSlash = mask.indexOf("/", maskIndex + 1) < 0?mask.length():mask.indexOf("/", maskIndex + 1);
				int maskIndexEnd   = Math.min(maskIndexBang < 0?mask.length():maskIndexBang, maskIndexSlash);
				fileFilter = new MyRegexFileFilter(mask.substring(maskIndex + (mask.charAt(maskIndex) == '!' || mask.charAt(maskIndex) == '/'?1:0), maskIndexEnd));
				File   file          = new File(basePath + relPath);
				File[] matchingFiles = file.listFiles(fileFilter);
				if (matchingFiles != null && matchingFiles.length > 0) {
					for (File matchingFile : matchingFiles) {
						if ("|.apt_generated|.svn|.settings|cvs|".contains(matchingFile.getName().toLowerCase()) ||
							"|.classpath|.factorypath|.project|.tern-project|pom.properties|pom.xml|".contains(matchingFile.getName().toLowerCase())) {
							// Automatically excluded folders/files
						} else if (maskIndexEnd == mask.length()) {
							if ((ResourceType.FILES.equals(resourceType) && matchingFile.isFile()) || (ResourceType.DIRECTORIES.equals(resourceType) && matchingFile.isDirectory())) {
								if (mask.contains(".") && "|.ear|.jar|.war|.zip".contains(mask.substring(mask.lastIndexOf(".")).toLowerCase())) {
									key = mask.contains("/")?mask.substring(mask.lastIndexOf('/') + 1):mask;
									key = key.contains("-")?key.substring(0, key.indexOf("-")) + key.substring(key.lastIndexOf(".")):key;
									key = key.substring(0, key.lastIndexOf(".")) + "*" + key.substring(key.lastIndexOf("."));
								} else {
									key = mask;
								}
								List<File> work = result.get(key);
								if (work == null) {
									work = new ArrayList<File>();
									result.put(key, work);
								}
								work.add(matchingFile);
							}
						} else if (matchingFile.isDirectory()) {
							createFileRelatedMapFromListUsingRecursion(basePath, relPath + "/" + matchingFile.getName(), mask, maskIndexEnd, resourceType, withinArchive, result);
						} else if (matchingFile.isFile() /*&& maskIndexBang >= 0*/ && matchingFile.getName().contains(".") && "|.ear|.jar|.war|.zip|".contains(matchingFile.getName().toLowerCase().substring(matchingFile.getName().lastIndexOf(".")))) {
							withinArchive = true;
							createFileRelatedMapFromListUsingRecursion(basePath + relPath + "/" + matchingFile.getName(), StringUtils.EMPTY, mask.substring(maskIndexEnd + 1), 0, resourceType, withinArchive, result);
						}
					}
				}
			}
		}
	}

	/**
	 * Specifies resource type(s) that are allowed.
	 */
	public enum ResourceType {
		ANYTHING,
		DIRECTORIES,
		FILES;
	}

	/**
	 * Regular expression-based FileFilter.
	 */
	private static class MyRegexFileFilter implements FileFilter {
		private String pattern;
		private ResourceType resourceType;
		
		/**
		 * Default constructor for this class.
		 * 
		 * @param pattern String identifying the (regex) pattern to match against.
		 */
		public MyRegexFileFilter(String pattern) {
			this.pattern = pattern;
		}

		/**
		 * Alternate constructor for this class.
		 * 
		 * @param pattern String identifying the (regex) pattern to match against.
		 * @param resourceType ResourceType identifying a specific resource type to match against.
		 */
		public MyRegexFileFilter(String pattern, ResourceType resourceType) {
			this(pattern);
			this.resourceType = resourceType;
		}

		@Override
		public boolean accept(File file) {
			String comparisonValue = file.getName();
			if (ResourceType.DIRECTORIES.equals(resourceType)) {
				return Pattern.matches(this.pattern, comparisonValue) && file.isDirectory();
			} else if (ResourceType.FILES.equals(resourceType)) {
				return Pattern.matches(this.pattern, comparisonValue) && file.isFile();
			} else {
				return Pattern.matches(this.pattern, comparisonValue);
			}
		}

		public boolean accept(ZipEntry zipEntry) {
			String comparisonValue = zipEntry.getName();
			if (ResourceType.DIRECTORIES.equals(resourceType)) {
				return Pattern.matches(this.pattern, comparisonValue) && zipEntry.isDirectory();
			} else if (ResourceType.FILES.equals(resourceType)) {
				return Pattern.matches(this.pattern, comparisonValue) && !zipEntry.isDirectory();
			} else {
				return Pattern.matches(this.pattern, comparisonValue);
			}
		}
	}

}
