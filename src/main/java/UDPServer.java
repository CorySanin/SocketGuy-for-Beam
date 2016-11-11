import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;

public class UDPServer implements Runnable{
	final int SERVPORT = 7125;
	final int NUMBYTES = 512;
	LinkedList<String> queue = null;
	LinkedList<WebSocketConnection> socks = null;
	
	public UDPServer(LinkedList<String> q,LinkedList<WebSocketConnection> w)
	{
		queue = q;
		socks = w;
		
	}
	
	@Override
	public void run() {
		try
		{
			DatagramSocket datagramSocket = new DatagramSocket(SERVPORT);
	        //DatagramSocket dgSock = new DatagramSocket();
	        System.out.println("UDP Server running on port "+SERVPORT+"...");
	        
	        while(true)
	         {
	            try
	            {
	            	byte[] buffer = new byte[NUMBYTES];
	            	DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
	            	datagramSocket.receive(packet);
	            	String s = new String(packet.getData());
	            	char why = 0;
	            	s = s.replace(why+"", "");
	            	if(!s.equals("0"))
	            	{
	            		(new Thread(new MessageSender(socks,s))).start();
	            	}
	            	
	            	String payload = "-1";
	            	if(!queue.isEmpty())
	            	{
	            		payload = queue.removeFirst();
	            	}
	            	
	            	byte[] buffbuffer = payload.getBytes();
	            	DatagramPacket backpack = new DatagramPacket(buffbuffer,buffbuffer.length,packet.getAddress(),packet.getPort());
	            	datagramSocket.send(backpack);
	            }
	            catch(IOException ex)
	            {
	               System.out.println("IOException:  " + ex.toString());
	            }
	         }
		}
		catch(SocketException ex)
		{
			System.out.println("SocketException:  " + ex.toString());
		}
	}

}
