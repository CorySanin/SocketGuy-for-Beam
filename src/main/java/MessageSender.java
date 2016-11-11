import java.util.Iterator;
import java.util.LinkedList;

public class MessageSender implements Runnable{
	LinkedList<WebSocketConnection> socks = null;
	String message;
	
	public MessageSender(LinkedList<WebSocketConnection> s,String m){
		socks = s;
		message = m;
	}
	
	@Override
	public void run() {
		Iterator<WebSocketConnection> iter = socks.iterator();
		while(iter.hasNext())
		{
			iter.next().notify(message);
		}
	}
	
}
