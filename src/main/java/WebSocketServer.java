import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class WebSocketServer implements Runnable{
	LinkedList<WebSocketConnection> websocks;
	
	public WebSocketServer(LinkedList<WebSocketConnection> socklist)
	{
		websocks = socklist;
	}
	
	@Override
	public void run() {
		try{
			ServerSocket servSock = new ServerSocket(81);
			while(true)
			{
				Socket sock = servSock.accept();
	            WebSocketConnection sockConnection = new WebSocketConnection(sock);
	            websocks.add(sockConnection);
            }
		}
		catch(IOException ex){
			System.out.println(ex);
		}
	}

}
