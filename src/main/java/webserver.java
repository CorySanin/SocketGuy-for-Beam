import java.io.*;
import java.net.*;

import javax.swing.DefaultListModel;


public class webserver implements Runnable{
	final int PORT = 8080;
	private DefaultListModel activeRequests;
	
	public webserver()
   {
		System.out.println("webserver instance created");
   }
	
	public void run(){
	      try{
	         ServerSocket servSock = new ServerSocket(PORT);
	         System.out.println("Server has started on port " + PORT);
	         while(true)
	         {
	            Socket sock = servSock.accept();
	            /*System.out.println("Got a connection from " +
	                  sock.getInetAddress() + " port " + sock.getPort());*/
	            HTTPRequest servThread = new HTTPRequest(sock,activeRequests);
	            servThread.run();
	         }
	      }
	      catch(IOException exc)
	      {
	         System.out.println("Web Server- Something went wrong: " + exc.toString());
	      }
	   }
}
