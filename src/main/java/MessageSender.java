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
		int curr = 0;
		while(iter.hasNext())
		{
			try{
				if(!iter.next().notify(message))
					socks.remove(curr);
				else
					curr++;
			}
			catch(Exception ex)
			{
				//Don't worry about it.
			}
		}
	}
	
}
