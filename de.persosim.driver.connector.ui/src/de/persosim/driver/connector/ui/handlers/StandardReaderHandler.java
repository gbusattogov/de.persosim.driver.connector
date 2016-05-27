package de.persosim.driver.connector.ui.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.persosim.driver.connector.service.NativeDriverConnectorImpl;
import de.persosim.driver.connector.ui.parts.ReaderPart;

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

	protected boolean enterPin(final String pin)
	{
		// ID of part as defined in fragment.e4xmi application model
		final MPart readerPart = getEPartService().findPart("de.persosim.driver.connector.ui.parts.reader");

		if (readerPart.getObject() instanceof ReaderPart) {
			final ReaderPart readerPartObject = (ReaderPart) readerPart.getObject();

			return readerPartObject.enterPin(pin);
		}
		else
		{
			return false;
		}
	}

	protected boolean isStandardReader()
	{
		return true;
	}
}