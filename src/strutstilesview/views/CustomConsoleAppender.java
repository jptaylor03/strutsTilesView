package strutstilesview.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * Custom Log4j message appender which writes to both java swing component and sysOut/sysErr.
 * 
 * @author TAYLOJ10
 * @see log4j.properties
 */
public class CustomConsoleAppender extends AppenderSkeleton {

	/**
	 * Define level --> color mappings.
	 */
	private static Map<String, Integer> LEVEL_COLORS = null;
	static {
		LEVEL_COLORS = new HashMap<String, Integer>();
		LEVEL_COLORS.put(Level.FATAL.toString(), SWT.COLOR_MAGENTA);
		LEVEL_COLORS.put(Level.ERROR.toString(), SWT.COLOR_RED);
		LEVEL_COLORS.put(Level.WARN.toString() , SWT.COLOR_DARK_YELLOW); // PINK or ORANGE?
		LEVEL_COLORS.put(Level.INFO.toString() , SWT.COLOR_BLACK);
		LEVEL_COLORS.put(Level.DEBUG.toString(), SWT.COLOR_DARK_GRAY);
		LEVEL_COLORS.put(Level.TRACE.toString(), SWT.COLOR_GRAY);
	}

	/**
	 * Cache level --> attribute sets.
	 */
	private static Map<String, StyleRange> LEVEL_ATTRS = new HashMap<String, StyleRange>();

	/**
	 * Container for the log4j.properties file.
	 */
	private static Properties LOG4J_PROPERTIES = null;
	private static Properties getLog4jProperties() {
		if (LOG4J_PROPERTIES == null) {
			final String LOG4J_PROPERTIES_FILE = "/log4j.properties";
			Properties properties = new Properties();
			InputStream inputStream = null;
			try {
				inputStream = CustomConsoleAppender.class.getResourceAsStream(LOG4J_PROPERTIES_FILE);
				properties.load(inputStream);
				LOG4J_PROPERTIES = properties;
			} catch (IOException e) {
				System.err.println("Error: Cannot load '" + LOG4J_PROPERTIES_FILE + "' file ");
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return LOG4J_PROPERTIES;
	}

	/**
	 * Container for the ConversionPattern (from the log4j.properties).
	 */
	private static String CONVERSION_PATTERN = null;
	private static String getConversionPattern() {
		if (CONVERSION_PATTERN == null) {
			CONVERSION_PATTERN = getLog4jProperties().getProperty("log4j.appender.console.layout.ConversionPattern");
		}
		return CONVERSION_PATTERN;
	}

	/**
	 * Container for the PatternLayout (based on the ConversionPattern).
	 */
	private static PatternLayout PATTERN_LAYOUT = null;
	private static PatternLayout getPatternLayout() {
		if (PATTERN_LAYOUT == null) {
			PATTERN_LAYOUT = new PatternLayout(getConversionPattern());
		}
		return PATTERN_LAYOUT;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLogger().isEnabledFor(event.getLevel())) {
			// Grab handle to the console component
			StyledText console = StrutsTilesView.getConsole();

			// Obtain style information for the current message
			StyleRange styleInfo = new StyleRange();
			if (!LEVEL_ATTRS.containsKey(event.getLevel().toString())) {
				// Determine level-specific color
				Integer color = LEVEL_COLORS.get(event.getLevel().toString());
				if (color == null) {
					color = SWT.COLOR_BLACK;
				}

				// Determine (font) attributes for the current message
				styleInfo.font = new Font(console.getDisplay(), new FontData("Lucida Console", 10, SWT.NORMAL));
		        styleInfo.foreground = console.getDisplay().getSystemColor(color);
		        LEVEL_ATTRS.put(event.getLevel().toString(), styleInfo);
			}
			styleInfo = LEVEL_ATTRS.get(event.getLevel().toString());

			// Write message to the console component
			String message = getPatternLayout().format(event);
			styleInfo.start = console.getText().length();
			styleInfo.length = console.getText().length();
			console.setStyleRange(styleInfo);
			console.setCaretOffset(styleInfo.start);
			console.append(message);

			// Write message to sysOut/sysErr
			if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
				System.err.append(message);
			} else {
				System.out.append(message);
			}
		}
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
