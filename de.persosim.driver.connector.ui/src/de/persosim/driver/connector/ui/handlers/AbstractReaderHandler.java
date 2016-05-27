package de.persosim.driver.connector.ui.handlers;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;
import org.globaltester.logging.BasicLogger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import de.persosim.driver.connector.ui.Activator;
import de.persosim.driver.connector.ui.parts.ReaderPart;
import de.persosim.driver.connector.ui.parts.ReaderPart.ReaderType;

abstract class AbstractReaderHandler {
	private ServiceRegistration<?> _registration;

	protected void switchToBasicReader() {
		switchToReaderType(ReaderType.BASIC);
	}

	protected void switchToStandardReader() {
		switchToReaderType(ReaderType.STANDARD);
	}

	private void switchToReaderType(ReaderType type) {
		// ID of part as defined in fragment.e4xmi application model
		MPart readerPart = getEPartService().findPart("de.persosim.driver.connector.ui.parts.reader");

		if (readerPart.getObject() instanceof ReaderPart) {
			ReaderPart readerPartObject = (ReaderPart) readerPart.getObject();

			try {
				readerPartObject.switchToReaderType(type);
			} catch (IOException e) {
				BasicLogger.logException(this.getClass(), e, BasicLogger.ERROR);
			}
		}		
	}

	protected abstract EPartService getEPartService();

	protected abstract boolean enterPin(final String pin);

	protected abstract boolean isStandardReader();

	public AbstractReaderHandler(final Class<?> concreteReaderHandlerClass) {
	    final Bundle bundle = FrameworkUtil.getBundle(concreteReaderHandlerClass);
	    if (bundle == null) {
			BasicLogger.log(getClass(), "Cannot find bundle");

	    	return;
	    }

	    final BundleContext ctx = Activator.getContext();
	    if (ctx == null) {
			BasicLogger.log(getClass(), "Cannot find context");

	    	return;
	    }

	    final EventHandler handler = new EventHandler() {
	    	public void handleEvent(final Event event) {
	    		final String topic = event.getTopic();
	    		if ("repl_request/select-reader".equals(topic)) {
	    			handleSelectReaderRequest(event);
	    		}
	    		else if ("repl_request/enter-pin".equals(topic)) {
	    			handleEnterPinRequest(event);
	    		}
	    	}
	    };

	    final Dictionary<String, String> properties = new Hashtable<String, String>();
	    properties.put(EventConstants.EVENT_TOPIC, "repl_request/*");
	    _registration = ctx.registerService(EventHandler.class, handler, properties);
	}

	public void finalize() {
	    if (_registration != null) {
			_registration.unregister();
	    }
	}

	private void handleSelectReaderRequest(final Event event) {
		final Object val = event.getProperty("READER_TYPE");
		if (!"basic".equals(val) && !"standard".equals(val)) {
			return;
		}

		final ReaderType type = "basic".equals(val) ? ReaderType.BASIC : ReaderType.STANDARD;
		final Display display = Display.getDefault();
		if (display == null) {
			BasicLogger.log(getClass(), "Cannot find default display");
			sendResponse("error");

			return;
		}

		display.syncExec(new Runnable() {
			public void run() {
				switchToReaderType(type);

				sendResponse("ok");
			}
		});
	}

	private void handleEnterPinRequest(final Event event) {
		if (!isStandardReader()) {
			return;
		}

		final Object val = event.getProperty("PIN");
		if (val == null || !(val instanceof String)) {
			return;
		}

		final String pin = (String) val;
		final Display display = Display.getDefault();
		if (display == null) {
			BasicLogger.log(getClass(), "Cannot find default display");
			sendResponse("error");

			return;
		}

		display.syncExec(new Runnable() {
			public void run() {
				final boolean ok = enterPin(pin);
				sendResponse(ok ? "ok" : "error");
			}
		});
	}

	private void sendResponse(final String result) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("RESULT", result);

		final BundleContext ctx = FrameworkUtil.getBundle(AbstractReaderHandler.class).getBundleContext();
		final ServiceReference<EventAdmin> ref = ctx.getServiceReference(EventAdmin.class);
		final EventAdmin eventAdmin = ctx.getService(ref);
		eventAdmin.sendEvent(new Event("repl_response/result", properties));
	}
}