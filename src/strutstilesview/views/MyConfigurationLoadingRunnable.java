package strutstilesview.views;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import strutstilesview.Utilities;

public class MyConfigurationLoadingRunnable implements IRunnableWithProgress {

	private IProgressMonitor monitor;

	private int amount;

	public MyConfigurationLoadingRunnable() {
	}

	/**
	 * Schedule runnable to run.
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		this.monitor = monitor;
		monitor.setTaskName(Utilities.getLanguage("loading.start"));
	}

	/**
	 * Set progress description and amount.
	 * 
	 * @param description
	 * @param amount
	 */
	public void setProgress(String description, int amount) {
		this.monitor.beginTask(description, amount);
		if (amount >= 100) {
			monitor.done();
		}
		this.amount = amount;
	}

	/**
	 * Update progress amount.
	 * 
	 * @param amount
	 */
	public void setProgress(int amount) {
		this.monitor.internalWorked((double)amount);
	}

	/**
	 * Increment progress amount (by 1).
	 */
	public void increment() {
		this.monitor.internalWorked((double)this.amount++);
	}

}
