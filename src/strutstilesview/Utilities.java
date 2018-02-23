package strutstilesview;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import strutstilesview.views.StrutsTilesView;

/**
 * Utilities for the view.
 */
public class Utilities {

	private static Log log = LogFactory.getLog("util");

	private Utilities() {}

//	private static Utilities instance = null;
//	public static Utilities getInstance() {
//		if (instance == null) {
//			synchronized (Utilities.class) {
//				instance = new Utilities();
//			}
//		}
//		return instance;
//	}

	/**
	 * Show the specified message in a pop-up window.
	 * 
	 * @param message
	 */
	public static void showMessage(Viewer viewer, String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			Utilities.getLanguage("view.name"),
			message);
	}

//	/**
//	 * Container for all image descriptors.
//	 */
//	private static Map<String, ImageDescriptor> imageDescriptors = null;
//	/**
//	 * Returns all image descriptors.
//	 */
//	private static Map<String, ImageDescriptor> getImageDescriptors() {
//		if (imageDescriptors == null) {
//			synchronized (Utilities.class) {
//				imageDescriptors = new HashMap<String, ImageDescriptor>();
//			}
//		}
//		return imageDescriptors;
//	}
//	/**
//	 * Returns the image descriptor for the specified image file name.
//	 * 
//	 * @return ImageDescriptor for the specified image name.
//	 */
//	public static ImageDescriptor getImageDescriptor(String imageFileName) {
//		ImageDescriptor result = getImageDescriptors().get(imageFileName);
//		if (result == null) {
//			String iconsPath = "icons/";
//	        URL url = StrutsTilesView.class.getResource("/" + iconsPath + imageFileName);
//	        result = ImageDescriptor.createFromURL(url);
//	        imageDescriptors.put(imageFileName, result);
//		}
//		return result;
//	}
	/**
	 * Container for all images.
	 */
	private static Map<String, Image> images = null;
	/**
	 * Returns all images.
	 */
	private static Map<String, Image> getImages() {
		if (images == null) {
			synchronized (Utilities.class) {
				images = new HashMap<String, Image>();
			}
		}
		return images;
	}
	/**
	 * Returns the image for the specified image file name.
	 * 
	 * @return Image for the specified image name.
	 */
	public static Image getImage(String imageFileName) {
		Image result = getImages().get(imageFileName);
		if (result == null) {
			String imagePath = "/resources/images/";
	        ImageDescriptor temp = ImageDescriptor.createFromURL(Utilities.getResourceURL(imagePath + imageFileName));
	        if (temp != null) {
	        	result = temp.createImage();
	        	images.put(imageFileName, result);
	        }
		}
		return result;
	}

	/**
	 * Container for language.
	 */
	private static Properties language = null;
	/**
	 * Returns the language contents.
	 * 
	 * @return Properties file containing language info.
	 */
	private static Properties getLanguage() {
		if (language == null) {
			try {
				Properties temp = new Properties();
				String resourcePath = "/resources/";
				temp.load(Utilities.getResourceAsStream(resourcePath + "language.properties"));
				language = temp;
			} catch (IOException e) {
				log.error("Error: Unable to load language properties file - " + e.getMessage(), e);
			}
		}
		return language;
	}
	/**
	 * Returns (language) value for the specified (language) key.
	 * 
	 * @param key
	 * @return String containing (language) value for the specified (language) key.
	 */
	public static String getLanguage(String key) {
		return getLanguage().getProperty(key);
	}

//	/**
//	 * Returns handle to logger.
//	 * 
//	 * @return Log
//	 */
//	public static Log getLog() {
//		return log;
//	}

	/**
	 * Obtain a URL for the specified (local) resource.
	 * 
	 * @param resourceName
	 * @return URL
	 */
	public static URL getResourceURL(String resourceName) {
		return StrutsTilesView.class.getResource(resourceName);
	}

	/**
	 * Obtain an input stream for the specified (local) resource.
	 * 
	 * @param resourceName
	 * @return InputStream
	 */
	public static InputStream getResourceAsStream(String resourceName) {
		return StrutsTilesView.class.getResourceAsStream(resourceName);
	}

	/**
	 * Change the cursor.
	 * 
	 * @param viewPart
	 * @param wait
	 */
	public static void changeCursor(ViewPart viewPart, boolean wait) {
		Shell shell = viewPart.getSite().getShell();
		Display display = shell.getDisplay();
		Cursor cursor = shell.getCursor();

		if (cursor != null) {
            cursor.dispose();
		}

        cursor = wait ? new Cursor(display, SWT.CURSOR_WAIT) : new Cursor(display, SWT.CURSOR_ARROW);

        shell.setCursor(cursor);		
	}

}
