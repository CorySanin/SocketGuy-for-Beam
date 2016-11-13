import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class WebSocketConnection {
	Socket sock;
	DataOutputStream writeSock;
	BufferedReader readSock;
	InputStream SockInput;
	java.io.OutputStream SockOutput;
	String WSKey = null;
	
	public WebSocketConnection(Socket s)
	{
		sock = s;
		boolean failed = false;
		try {
			SockInput = sock.getInputStream();
			SockOutput = sock.getOutputStream();
			//writeSock = new PrintWriter(SockOutput, true);
			writeSock = new DataOutputStream(SockOutput);
			readSock = new BufferedReader(new InputStreamReader(SockInput));
			
			//failed = readSock.readLine().split(" ")[2].equals("HTTP/1.0");
			if(SockInput.available() == 0)
				Thread.sleep(2000);
			byte[] requestBytes = new byte[SockInput.available()];
			int bytesread = 0;
			while (bytesread < requestBytes.length)
				bytesread+= SockInput.read(requestBytes, bytesread, requestBytes.length);
			String request = new String(requestBytes);
			String[] reqLines = request.split("\r\n");
			for(int i=0;i<reqLines.length && !failed;i++)
			{
				String[] words = reqLines[i].split(" ");
				failed = (failed || (words[0].toUpperCase().equals("GET") && words[2].toUpperCase().equals("HTTP/1.0")));
				failed = (failed || (words[0].toUpperCase().equals("UPGRADE:") && !words[1].toUpperCase().equals("WEBSOCKET")));
				if(!failed && words[0].toUpperCase().equals("SEC-WEBSOCKET-KEY:"))
					WSKey = words[1] + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
			}
			failed = failed || WSKey == null;
			if(failed)
			{
				writeSock.writeBytes("HTTP/1.1 400 Bad Request");
				sock.close();
			}
			else
			{
				MessageDigest md = MessageDigest.getInstance("SHA1");
				md.update(WSKey.getBytes());
				byte[] shaout = md.digest();
				byte[] encodedBytes = Base64.encodeBase64(shaout);
				String key = new String(encodedBytes);
				if(!sock.isClosed())
					writeSock.writeBytes("HTTP/1.1 101 Switching Protocols\r\n"
						+ "Upgrade: websocket\r\n"
						+ "Connection: Upgrade\r\n"
						+ "Sec-WebSocket-Accept: "+ key + "\r\n\r\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean notify(String s)
	{
		if(sock.isClosed())
			return false;
		int rawstart = 2;
		byte[] message = s.getBytes();
		byte lengthbyte;
		int arrsize = 2 + message.length;
		if(message.length <= 125)
			lengthbyte = (byte)message.length;
		else if(message.length > 125 && message.length <= 65535)
		{
			lengthbyte = (byte)126;
			rawstart = 4;
			arrsize += 2;
		}
		else
		{
			lengthbyte = (byte)127;
			rawstart = 10;
			arrsize += 8;
		}
		
		byte[] output = new byte[arrsize];
		output[0] = (byte)129;
		output[1] = lengthbyte;
		if(rawstart == 4)
		{
			output[2] = (byte)(message.length >>> 8);
			output[3] = (byte)message.length;
		}
		else if(rawstart == 10)
		{
			output[2] = (byte)(message.length >>> 56);
			output[3] = (byte)(message.length >>> 48);
			output[4] = (byte)(message.length >>> 40);
			output[5] = (byte)(message.length >>> 32);
			output[6] = (byte)(message.length >>> 24);
			output[7] = (byte)(message.length >>> 16);
			output[8] = (byte)(message.length >>> 8);
			output[9] = (byte)message.length;
		}
		for(int i=0;i<message.length;i++)
			output[rawstart+i] = message[i];
		try {
			SockOutput.write(output);
		} catch (IOException e) {
			System.out.println("Attempting to close WebSocket");
			try {
				sock.close();
			} catch (IOException e1) {
				System.out.println("Couldn't close WebSocket");
			}
			return false;
		}
		return true;
	}
}
