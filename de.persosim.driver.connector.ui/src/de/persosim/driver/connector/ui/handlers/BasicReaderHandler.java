package de.persosim.driver.connector.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.BasicLogger;

public class BasicReaderHandler extends AbstractReaderHandler {

	@Inject
	private EPartService partService;

	protected EPartService getEPartService()
	{
		return partService;
	}

	@Execute
	public void execute(Shell shell) {
		switchToBasicReader();
	}

	public BasicReaderHandler()
	{
		super(BasicReaderHandler.class);
	}

	protected boolean enterPin(final String pin)
	{
		BasicLogger.log(this.getClass(), "Method enterPin: not supported");

		return false;
	}

	protected boolean isStandardReader()
	{
		return false;
	}
}