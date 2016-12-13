import java.io.BufferedReader;
import java.io.Console;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import pro.beam.api.BeamAPI;
import pro.beam.api.exceptions.user.WrongPasswordException;
import pro.beam.interactive.net.packet.Protocol;
import pro.beam.interactive.net.packet.Protocol.Report.TactileInfo;
import pro.beam.interactive.robot.RobotBuilder;

public class SocketGuy {
	final private static boolean DEBUGGING = true;
	
	public static void main(String[] args) {
		Console console = System.console();
		BeamAPI beam = new BeamAPI();
		String username = null;
    	String chanID = null;
    	char[] password = null;
    	boolean websocketserv = false;
    	boolean webserver = false;
    	boolean ready = true;
    	WebSocketServer wsockserv;
    	Thread websockthread;
    	webserver web;
    	Thread webthread;
    	File f = new File("config.ini");
    	LinkedList<String> queue = new LinkedList<String>();
    	LinkedList<WebSocketConnection> websocks = new LinkedList<WebSocketConnection>();
    	if(DEBUGGING)
    	{
    		username = "WhoIsWORM";
    		chanID = "183839";
    		password = "PASSWORD".toCharArray();
    		websocketserv = true;
    		webserver = websocketserv && true;
    	}
    	else
		{
    		if(!f.exists())
			{
    			//console commands don't work in debugger. Just change DEBUGGING to true
				username = console.readLine("Enter your username: ");
				chanID = console.readLine("Enter your channel ID: ");
			}
			else
			{
				try
	        	{
					FileInputStream fstream = new FileInputStream(f);
		        	DataInputStream in = new DataInputStream(fstream);
		        	BufferedReader br = new BufferedReader(new InputStreamReader(in));
					username = br.readLine();
					chanID = br.readLine();
	        	}
	        	catch(IOException ex)
	        	{
	        		System.out.println("Something went wrong reading config.ini");
	        		username = console.readLine("Enter your username: ");
	    			chanID = console.readLine("Enter your channel ID: ");
	        	}
			}
			password = console.readPassword("Enter your password: ");
			System.out.println("Would you like to start extra web servers for stream notifications?");
			System.out.println("0) No additional servers/no stream notifications");
			System.out.println("1) Websocket server (only ideal for web devs)");
			System.out.println("2) Websocket and web server for stream notifications");
			String optionsel = console.readLine("enter a number 0-2: ");
			if(optionsel.equals("1") || optionsel.equals("2"))
				websocketserv = true;
			if(optionsel.equals("2"))
				webserver = true;
		}
		//everything is in
		
		try {
	        pro.beam.interactive.robot.Robot robot = new RobotBuilder()
	                .username(username)
	                .password(new String(password))
	                .channel(Integer.parseInt(chanID)).build(beam).get();
	
	        //what to do when one or several buttons are pushed
	        robot.on(Protocol.Report.class, report -> {
	        	int check = report.getTactileCount();
	        	if(check > 0)
	        	{
	        		List<TactileInfo> l = report.getTactileList();
	        		for(int i=0;i<check;i++)
	        		{
	        			for(int t=0;t<l.get(i).getPressFrequency();t++)
	        			{
	        				String buttonnum = l.get(i).getId()+"";
	        				System.out.println("Button "+buttonnum+" pushed");
	        				queue.add(buttonnum);
	        			}
	        		}
	        	}
	        });
		} catch (Exception e) {
			ready = false;
			if(e.getCause() instanceof WrongPasswordException)
				System.out.println("Wrong Password.");
			else
				e.printStackTrace();
		}
		
		if(ready)
		{
			//set up server(s)
			if(websocketserv)
			{
				websockthread = new Thread(wsockserv = new WebSocketServer(websocks));
				websockthread.start();
			}
			if(webserver)
			{
				webthread = new Thread(web = new webserver());
				webthread.start();
			}
			
			UDPServer serv = new UDPServer(queue,websocks);
			serv.run();
		}
	}
}
