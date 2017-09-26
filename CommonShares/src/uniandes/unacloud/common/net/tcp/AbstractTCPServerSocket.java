package uniandes.unacloud.common.net.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Abstract class to be implemented by processes to receive messages from agents.
 * @author CesarF
 *
 */
public abstract class AbstractTCPServerSocket extends Thread {
	
	/**
	 * Thread pool to process sockets in background
	 */
	private ExecutorService threadPool;
	
	/**
	 * Port to listen
	 */
	private int listenPort;
	
	public ServerSocket ss;
	
	/**
	 * Creates a new TCP server socket
	 * @param listenPort port 
	 * @param threads quantity
	 */
	public AbstractTCPServerSocket(int listenPort, int threads) {
		this.listenPort = listenPort;
		threadPool = Executors.newFixedThreadPool(threads);
	}
	
	@Override
	public void run() {
		System.out.println("starting ss on port " + listenPort);
		
		try (ServerSocket ss2 = new ServerSocket(listenPort)) {
			ss = ss2;
			ss2.close();
			while (true) {
				Socket s = ss.accept();
				try {		
					threadPool.submit(processSocket(s));
				} catch (Exception e) {
					e.printStackTrace();
				}
						
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the runnable where socket is processed
	 * @param socket
	 * @return runnable process to be added to thread pool
	 */
	protected abstract Runnable processSocket(Socket socket) throws Exception;
}
