package de.persosim.driver.connector.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.persosim.driver.connector.NativeDriverComm;
import de.persosim.driver.connector.VirtualReaderUi;
import de.persosim.driver.connector.pcsc.ConnectorEnabled;
import de.persosim.driver.connector.pcsc.PcscListener;
import de.persosim.driver.connector.pcsc.UiEnabled;

/**
 * This class handles the connection to the native part of the PersoSim driver
 * package via a socket Connection. In PCSC terms this would represent
 * functionality like an IFD Handler presents to the upper layers as described
 * in the PCSC specification part 3.
 * 
 * @author mboonk
 * 
 */
public class NativeDriverConnectorImpl implements NativeDriverConnector {

	private List<PcscListener> listeners = new LinkedList<PcscListener>();
	private List<VirtualReaderUi> userInterfaces = new LinkedList<VirtualReaderUi>();
	private Thread communicationThread;
	private NativeDriverComm communication;
	
	private String nativeDriverHostName = null;
	private int nativeDriverPort = Integer.MIN_VALUE;
	private int timeout = 5000;

	@Override
	public void connect(String nativeDriverHostName, int nativeDriverPort) throws IOException {
		this.nativeDriverHostName = nativeDriverHostName;
		this.nativeDriverPort = nativeDriverPort;
		
		communication = new NativeDriverComm(this.nativeDriverHostName, this.nativeDriverPort, listeners); 
		communicationThread = new Thread(communication);
		communicationThread.start();

		long timeOutTime = Calendar.getInstance().getTimeInMillis() + timeout;
		while (!communication.isConnected()){
			if (Calendar.getInstance().getTimeInMillis() > timeOutTime){
				throw new IOException("The communication thread has run into a timeout");
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				throw new IOException("The waiting for the communication thread was interrupted");
			}
		}
	}

	@Override
	public boolean isRunning() {
		return ((communicationThread != null) && communicationThread.isAlive());
	}

	@Override
	public void disconnect() throws IOException, InterruptedException {
		if (communication != null){
			communication.disconnect();
			communicationThread.join();
		}
	}

	@Override
	public void addListener(PcscListener listener) {
		listeners.add(0, listener);
		if (listener instanceof UiEnabled){
			((UiEnabled)listener).setUserInterfaces(userInterfaces);
		}
		if (listener instanceof ConnectorEnabled){
			((ConnectorEnabled)listener).setConnector(this);
		}
	}

	@Override
	public void addUi(VirtualReaderUi ui) {
		this.userInterfaces.add(ui);
	}

	@Override
	public Collection<VirtualReaderUi> getUserInterfaces(){
		return new HashSet<VirtualReaderUi>(userInterfaces);
	}

	@Override
	public List<PcscListener> getListeners() {
		return new LinkedList<PcscListener> (listeners);
	}


}
