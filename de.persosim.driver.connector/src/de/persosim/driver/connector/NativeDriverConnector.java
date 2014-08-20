package de.persosim.driver.connector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;

import de.persosim.driver.connector.pcsc.PcscListener;

/**
 * This class handles the connection to the native part of the PersoSim driver
 * package via a socket Connection. In PCSC terms this would represent
 * functionality like an IFD Handler presents to the upper layers as described
 * in the PCSC specification part 3.
 * 
 * @author mboonk
 * 
 */
public class NativeDriverConnector {

	Collection<PcscListener> listeners = new HashSet<PcscListener>();
	private NativeDriverComm comm;
	private String hostName;
	private int dataPort;
	private int eventPort;

	public NativeDriverConnector(String hostName, int dataPort, int eventPort) throws UnknownHostException, IOException {
		this.hostName = hostName;
		this.dataPort = dataPort;
		this.eventPort = eventPort;
	}
	
	/**
	 * This method connects to the native driver part.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void connect() throws IOException {
		comm = new NativeDriverComm(hostName, dataPort, eventPort, listeners);
		comm.setDaemon(true);
		comm.start();
		comm.sendEventToDriver("Card Inserted");
	} 

	/**
	 * This method disconnects from the native driver part.
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void disconnect() throws IOException, InterruptedException {
		comm.sendEventToDriver("Card Removed");
		comm.interrupt();
		comm.join();
	}

	public void addListener(PcscListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PcscListener listener) {
		listeners.remove(listener);
	}

	public boolean isConnected() {
		return comm.isAlive() && !comm.isInterrupted();
	}
}
