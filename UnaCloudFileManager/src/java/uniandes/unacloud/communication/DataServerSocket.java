package uniandes.unacloud.communication;

import java.io.DataInputStream;
import java.net.Socket;

import com.losandes.utils.UnaCloudConstants;

/**
 * Class used to receive messages to manage image files.
 * Sends and receives messages from agents, 
 * The kind of messages are send image files or request image file from agent.
 * @author CesarF
 *
 */
public class DataServerSocket extends AbstractServerSocket{	
	
	public DataServerSocket(int listenPort, int threads) {
		super(listenPort, threads);		
	}

	@Override
	protected Runnable processSocket(Socket s) throws Exception {
		DataInputStream ds = new DataInputStream(s.getInputStream());
		int byteOp=ds.readInt();					
		System.out.println("Request from: "+s.getInetAddress()+" - operation: "+byteOp);
		if(byteOp==UnaCloudConstants.REQUEST_IMAGE){//Agent request for image
			System.out.println("Start service to send file");
			return new FileTransferTask(s);
		}else if(byteOp==UnaCloudConstants.SEND_IMAGE){//Agent sends an image
			System.out.println("Start service to request file");
			return new FileReceiverTask(s);
		}
		return null;
	}
}