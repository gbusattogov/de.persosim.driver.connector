package de.persosim.driver.connector.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.persosim.driver.connector.service.NativeDriverConnectorImpl;

public class StandardReaderHandler extends AbstractReaderHandler {
	NativeDriverConnectorImpl connector;

	@Inject
	private EPartService partService;

	protected EPartService getEPartService()
	{
		return partService;
	}

	@Execute
	public void execute(Shell shell) {
		switchToStandardReader();
	}

	public StandardReaderHandler()
	{
		super(StandardReaderHandler.class);
	}
}