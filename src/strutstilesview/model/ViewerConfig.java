package strutstilesview.model;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Container for this application's "viewer-config.xml" contents.
 * 
 * @author tayloj10
 */
public class ViewerConfig extends AbstractVO {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	private String                  targetAppConfigFileBasePath;
	private boolean                 targetAppConfigFileBasePathMissing;
	private List<String>            targetAppConfigFileMasks;
	private Map<String, List<File>> targetAppConfigFilesByMask;
	private String                  targetAppSourceCodeBasePath;
	private boolean                 targetAppSourceCodeBasePathMissing;
	private List<String>            targetAppSourceCodePathMasks;
	private Map<String, List<File>> targetAppSourceCodePathsByMask;
	private String                  targetAppObjectCodeBasePath;
	private boolean                 targetAppObjectCodeBasePathMissing;
	private List<String>            targetAppObjectCodePathMasks;
	private Map<String, List<File>> targetAppObjectCodePathsByMask;
	private String                  targetAppClasspathBasePath;
	private boolean                 targetAppClasspathBasePathMissing;
	private List<String>            targetAppClasspathPathMasks;
	private Map<String, List<File>> targetAppClasspathPathsByMask;
	private String                  targetAppBasePackage;
	private boolean                 targetAppBasePackageMissing;
//	private String                  textEditorExecutable;
//	private boolean                 textEditorExecutableMissing;
	private boolean                 recurseToShowSiblings;
	
	// ThreadPool Configuration
	
	private String threadPoolOptionPoolCoreSize;
	private String threadPoolOptionPoolMaximumSize;
	private String threadPoolOptionQueueMaximumSize;
	private String threadPoolOptionQueueFairOrder;
	private String threadPoolOptionPreStartCoreThreads;
	private String threadPoolOptionKeepAliveTime;
	private String threadPoolOptionQueueType;
	private String threadPoolOptionRejectionPolicy;
	private String threadPoolShutdownTimeoutThreshold;

	/******************
	 * Constructor(s) *
	 ******************/
	
	private ViewerConfig() {}

	private static ViewerConfig instance;
	public static synchronized ViewerConfig getInstance() {
		if (instance == null) {
			instance = new ViewerConfig();
		}
		return instance;
	}

	/***********************
	 * Getter(s)/Setter(s) *
	 ***********************/
	
	public String getTargetAppConfigFileBasePath() {
		return targetAppConfigFileBasePath;
	}
	public void setTargetAppConfigFileBasePath(String targetAppConfigFileBasePath) {
		this.targetAppConfigFileBasePath = targetAppConfigFileBasePath;
	}
	public boolean isTargetAppConfigFileBasePathMissing() {
		return targetAppConfigFileBasePathMissing;
	}
	public void setTargetAppConfigFileBasePathMissing(
			boolean targetAppConfigFileBasePathMissing) {
		this.targetAppConfigFileBasePathMissing = targetAppConfigFileBasePathMissing;
	}
	public List<String> getTargetAppConfigFileMasks() {
		return targetAppConfigFileMasks;
	}
	public void setTargetAppConfigFileMasks(List<String> targetAppConfigFileMasks) {
		this.targetAppConfigFileMasks = targetAppConfigFileMasks;
	}
	public Map<String, List<File>> getTargetAppConfigFilesByMask() {
		return targetAppConfigFilesByMask;
	}
	public void setTargetAppConfigFilesByMask(Map<String, List<File>> targetAppConfigFilesByMask) {
		this.targetAppConfigFilesByMask = targetAppConfigFilesByMask;
	}
	public String getTargetAppSourceCodeBasePath() {
		return targetAppSourceCodeBasePath;
	}
	public void setTargetAppSourceCodeBasePath(String targetAppSourceCodeBasePath) {
		this.targetAppSourceCodeBasePath = targetAppSourceCodeBasePath;
	}
	public boolean isTargetAppSourceCodeBasePathMissing() {
		return targetAppSourceCodeBasePathMissing;
	}
	public void setTargetAppSourceCodeBasePathMissing(
			boolean targetAppSourceCodeBasePathMissing) {
		this.targetAppSourceCodeBasePathMissing = targetAppSourceCodeBasePathMissing;
	}
	public List<String> getTargetAppSourceCodePathMasks() {
		return targetAppSourceCodePathMasks;
	}
	public void setTargetAppSourceCodePathMasks(List<String> targetAppSourceCodePathMasks) {
		this.targetAppSourceCodePathMasks = targetAppSourceCodePathMasks;
	}
	public Map<String, List<File>> getTargetAppSourceCodePathsByMask() {
		return targetAppSourceCodePathsByMask;
	}
	public void setTargetAppSourceCodePathsByMask(Map<String, List<File>> targetAppSourceCodePathsByMask) {
		this.targetAppSourceCodePathsByMask = targetAppSourceCodePathsByMask;
	}
	public String getTargetAppObjectCodeBasePath() {
		return targetAppObjectCodeBasePath;
	}
	public void setTargetAppObjectCodeBasePath(String targetAppObjectCodeBasePath) {
		this.targetAppObjectCodeBasePath = targetAppObjectCodeBasePath;
	}
	public boolean isTargetAppObjectCodeBasePathMissing() {
		return targetAppObjectCodeBasePathMissing;
	}
	public void setTargetAppObjectCodeBasePathMissing(
			boolean targetAppObjectCodeBasePathMissing) {
		this.targetAppObjectCodeBasePathMissing = targetAppObjectCodeBasePathMissing;
	}
	public List<String> getTargetAppObjectCodePathMasks() {
		return targetAppObjectCodePathMasks;
	}
	public void setTargetAppObjectCodePathMasks(List<String> targetAppObjectCodePathMasks) {
		this.targetAppObjectCodePathMasks = targetAppObjectCodePathMasks;
	}
	public Map<String, List<File>> getTargetAppObjectCodePathsByMask() {
		return targetAppObjectCodePathsByMask;
	}
	public void setTargetAppObjectCodePathsByMask(Map<String, List<File>> targetAppObjectCodePathsByMask) {
		this.targetAppObjectCodePathsByMask = targetAppObjectCodePathsByMask;
	}
	public String getTargetAppClasspathBasePath() {
		return targetAppClasspathBasePath;
	}
	public void setTargetAppClasspathBasePath(String targetAppClasspathBasePath) {
		this.targetAppClasspathBasePath = targetAppClasspathBasePath;
	}
	public boolean isTargetAppClasspathBasePathMissing() {
		return targetAppClasspathBasePathMissing;
	}
	public void setTargetAppClasspathBasePathMissing(
			boolean targetAppClasspathBasePathMissing) {
		this.targetAppClasspathBasePathMissing = targetAppClasspathBasePathMissing;
	}
	public List<String> getTargetAppClasspathPathMasks() {
		return targetAppClasspathPathMasks;
	}
	public void setTargetAppClasspathPathMasks(List<String> targetAppClasspathPathMasks) {
		this.targetAppClasspathPathMasks = targetAppClasspathPathMasks;
	}
	public Map<String, List<File>> getTargetAppClasspathPathsByMask() {
		return targetAppClasspathPathsByMask;
	}
	public void setTargetAppClasspathPathsByMask(Map<String, List<File>> targetAppClasspathPathsByMask) {
		this.targetAppClasspathPathsByMask = targetAppClasspathPathsByMask;
	}
	public String getTargetAppBasePackage() {
		return targetAppBasePackage;
	}
	public void setTargetAppBasePackage(String targetAppBasePackage) {
		this.targetAppBasePackage = targetAppBasePackage;
	}
	public boolean isTargetAppBasePackageMissing() {
		return targetAppBasePackageMissing;
	}
	public void setTargetAppBasePackageMissing(boolean targetAppBasePackageMissing) {
		this.targetAppBasePackageMissing = targetAppBasePackageMissing;
	}
//	public String getTextEditorExecutable() {
//		return textEditorExecutable;
//	}
//	public void setTextEditorExecutable(String textEditorExecutable) {
//		this.textEditorExecutable = textEditorExecutable;
//	}
//	public boolean isTextEditorExecutableMissing() {
//		return textEditorExecutableMissing;
//	}
//	public void setTextEditorExecutableMissing(boolean textEditorExecutableMissing) {
//		this.textEditorExecutableMissing = textEditorExecutableMissing;
//	}
	public boolean isRecurseToShowSiblings() {
		return recurseToShowSiblings;
	}
	public void setRecurseToShowSiblings(boolean recurseToShowSiblings) {
		this.recurseToShowSiblings = recurseToShowSiblings;
	}
	
	// ThreadPool Configuration
	
	public String getThreadPoolOptionPoolCoreSize() {
		return threadPoolOptionPoolCoreSize;
	}
	public void setThreadPoolOptionPoolCoreSize(String threadPoolOptionPoolCoreSize) {
		this.threadPoolOptionPoolCoreSize = threadPoolOptionPoolCoreSize;
	}
	public String getThreadPoolOptionPoolMaximumSize() {
		return threadPoolOptionPoolMaximumSize;
	}
	public void setThreadPoolOptionPoolMaximumSize(
			String threadPoolOptionPoolMaximumSize) {
		this.threadPoolOptionPoolMaximumSize = threadPoolOptionPoolMaximumSize;
	}
	public String getThreadPoolOptionQueueMaximumSize() {
		return threadPoolOptionQueueMaximumSize;
	}
	public void setThreadPoolOptionQueueMaximumSize(
			String threadPoolOptionQueueMaximumSize) {
		this.threadPoolOptionQueueMaximumSize = threadPoolOptionQueueMaximumSize;
	}
	public String getThreadPoolOptionQueueFairOrder() {
		return threadPoolOptionQueueFairOrder;
	}
	public void setThreadPoolOptionQueueFairOrder(
			String threadPoolOptionQueueFairOrder) {
		this.threadPoolOptionQueueFairOrder = threadPoolOptionQueueFairOrder;
	}
	public String getThreadPoolOptionPreStartCoreThreads() {
		return threadPoolOptionPreStartCoreThreads;
	}
	public void setThreadPoolOptionPreStartCoreThreads(
			String threadPoolOptionPreStartCoreThreads) {
		this.threadPoolOptionPreStartCoreThreads = threadPoolOptionPreStartCoreThreads;
	}
	public String getThreadPoolOptionKeepAliveTime() {
		return threadPoolOptionKeepAliveTime;
	}
	public void setThreadPoolOptionKeepAliveTime(
			String threadPoolOptionKeepAliveTime) {
		this.threadPoolOptionKeepAliveTime = threadPoolOptionKeepAliveTime;
	}
	public String getThreadPoolOptionQueueType() {
		return threadPoolOptionQueueType;
	}
	public void setThreadPoolOptionQueueType(String threadPoolOptionQueueType) {
		this.threadPoolOptionQueueType = threadPoolOptionQueueType;
	}
	public String getThreadPoolOptionRejectionPolicy() {
		return threadPoolOptionRejectionPolicy;
	}
	public void setThreadPoolOptionRejectionPolicy(
			String threadPoolOptionRejectionPolicy) {
		this.threadPoolOptionRejectionPolicy = threadPoolOptionRejectionPolicy;
	}
	public String getThreadPoolShutdownTimeoutThreshold() {
		return threadPoolShutdownTimeoutThreshold;
	}
	public void setThreadPoolShutdownTimeoutThreshold(
			String threadPoolShutdownTimeoutThreshold) {
		this.threadPoolShutdownTimeoutThreshold = threadPoolShutdownTimeoutThreshold;
	}

}
