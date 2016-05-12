package de.persosim.driver.connector.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

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
}