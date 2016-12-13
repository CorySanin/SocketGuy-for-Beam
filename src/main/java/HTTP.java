
import java.io.*;
import java.util.*;

import javax.swing.DefaultListModel;

/**
 Handles general HTTP IO
 @author Cory
 */
public class HTTP 
{
   final private String CRLF = "\r\n";
   final private int CHUNKSIZE = 1024;
   private String reqFileName;
   private FileInputStream fileInput;
   private DefaultListModel activeRequests;
   
   public HTTP(DefaultListModel l)
   {
	   activeRequests = l;
   }
   
   /**
   Returns a status line
   @param code status code
   @param phrase status meaning
   @return a complete status line
   */
   private String statusLine(int code, String phrase)
   {
      return "HTTP/1.0 " + code + " " + phrase + CRLF;
   }
   
   /**
   Returns a content type header according to the filename
   @param fileName the file's name
   @return the content type header line
   */
   private String contentType(String fileNameMC)
   {
      String fileName = fileNameMC.toLowerCase();
      String returnOut = "Content-type: ";
      if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
         returnOut += "text/html";
      else if(fileName.endsWith(".xml"))
         returnOut += "application/xml";
      else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
         returnOut += "image/jpeg";
      else if(fileName.endsWith(".png"))
         returnOut += "image/png";
      else if(fileName.endsWith(".gif"))
         returnOut += "image/gif";
      else if(fileName.endsWith(".ico"))
         returnOut += "image/x-icon";
      else if(fileName.endsWith(".css"))
         returnOut += "text/css";
      else if(fileName.endsWith(".mp3"))
         returnOut += "audio/mpeg";
      else if(fileName.endsWith(".ogg"))
         returnOut += "audio/ogg";
      else if(fileName.endsWith(".mp4"))
         returnOut += "video/mp4";
      else if(fileName.endsWith(".otf"))
         returnOut += "application/x-font-otf";
      else if(fileName.endsWith(".ttf"))
         returnOut += "application/x-font-ttf";
      else if(fileName.endsWith(".woff"))
         returnOut += "application/x-font-woff";
      else
         returnOut += "application/octet-stream";
      return returnOut + CRLF;
   }
   
   /**
   Reads the request and responds with a byte stream
   @param lineInput the first line of the HTTP request
   @param httpOutput the output stream
   */
   public void processRequest(String lineInput, DataOutputStream httpOutput)
   {
      try{
         byte[] bytebuffer = new byte[CHUNKSIZE];
         StringTokenizer st = new StringTokenizer(lineInput);
         st.nextElement();
         reqFileName = "." + st.nextToken();
         System.out.println("GET request received: " + reqFileName);
         if(openfile(httpOutput))
         {
            int numBytes = fileInput.read(bytebuffer);
            while(numBytes == CHUNKSIZE)
            {
               httpOutput.write(bytebuffer, 0, numBytes);
               numBytes = fileInput.read(bytebuffer);
            }
            System.out.println("File sent: " + reqFileName);
         }
      }
      catch(IOException ex)
      {
         System.out.println("Error (" + ex.toString() +
               ") in request for file: " + reqFileName);
      }
   }
   
   /**
   Loads up fileInput, outputs a 404 doc if file can't be found
   @param httpOutput the output stream
   @return true if file exists
   @throws IOException in the case of an error
   */
   private boolean openfile(DataOutputStream httpOutput) throws IOException
   {
      try{
    	 if(reqFileName.equals("./"))
    	 {
    		 httpOutput.writeBytes(statusLine(200,"OK"));
    	      httpOutput.writeBytes(contentType("./index.html"));
    	      httpOutput.writeBytes(CRLF);
    	      httpOutput.writeBytes("<html><head>\n" +
    	         "<title>SocketGuy</title>\n"
    	         + "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://beam.pro/_latest/img/favicon/favicon.ico?build=5639e05\">"
    	         + "<style>"
    	         + "@keyframes flippy {"
    	         + "0%   {-webkit-transform: rotateX(-90deg); /* Safari */"
    	         + "transform: rotateX(-90deg);"
    	         + "opacity:0;}"
    	         + "1%  {opacity:1;}"
    	         + "98%  {opacity:1;}"
    	         + "99%  {opacity:0;}"
    	         + "100% {-webkit-transform: rotateX(90deg); /* Safari */"
    	         + "transform: rotateX(90deg);}"
    	         + "}"
    	         + "@keyframes zip {"
    	         + "0%      {opacity:0;}"
    	         + "5%      {opacity:1;}"
    	         + "85%     {opacity:1;}"
    	         + "95%     {opacity:0;"
    	         + "margin-bottom:0px;}"
    	         + "100%    {opacity:0;"
    	         + "margin-bottom:-55px;}"
    	         + "}"
    	         + "html,body{"
    	         + "font-family:Arial, Helvetica, sans-serif;"
    	         + "background-color:transparent;"
    	         + "padding:0px;"
    	         + "margin:0px;"
    	         + "overflow:hidden;"
    	         + "}"
    	         + "#nts{"
    	         + "overflow:hidden;"
    	         + "}"
    	         + "#nts>div>div"
    	         + "{"
    	         + "background-color:#212121;"
    	         + "color:#EEEEEE;"
    	         + "font-size:2em;"
    	         + "padding:10px;"
    	         + "animation-name: zip;"
    	         + "animation-duration: 5s;"
    	         + "animation-fill-mode:forwards;"
    	         + "margin:auto;"
    	         + "margin-bottom:0px;"
    	         + "overflow:hidden;"
    	         + "}"
    	         + "#nts>div::after"
    	         + "{"
    	         + "display:block;"
    	         + "height:15px;"
    	         + "content: \"\\00a0\";"
    	         + "}"
    	         + "</style>"
    	         + "<script>"
    	         + "function notify(newnote){"
    	         + "var main = document.getElementById('nts');"
    	         + "var newNote = document.createElement('div');"
    	         + "var container = document.createElement('div');"
    	         + "newNote.innerHTML = newnote;"
    	         + "container.appendChild(newNote);"
    	         + "main.appendChild(container);"
    	         + "setTimeout(function() {"
    	         + "main.removeChild(container);"
    	         + "}, 5000);"
    	         + "}"
    	         + "var connection = new WebSocket('ws://localhost:81');"
    	         + "connection.onopen = function () {"
    	         + "};"
    	         + "connection.onerror = function (error) {"
    	         + "console.log('WebSocket Error ' + error);"
    	         + "};"
    	         + "connection.onmessage = function (e) {"
    	         + "notify(e.data);"
    	         + "};"
    	         + "</script>"
    	         + "</head>"
    	         + "<body>"
    	         + "<div id=\"nts\" class=\"main\">"
    	         + "</div>"
    	         + "</body></html>\r\n");
    	      return false;
    	 }
    	 if(reqFileName.startsWith("./removeone?id="))
    	 {
    		 if(activeRequests.size() > 0)
    			 activeRequests.remove(0);
    		 httpOutput.writeBytes(statusLine(200,"OK"));
  			 httpOutput.writeBytes(contentType("./index.html"));
  			 httpOutput.writeBytes(CRLF);
  			 httpOutput.writeBytes("\r\n");
  			 return false;
    	 }
    	 else if(reqFileName.startsWith("./removebottom?id="))
    	 {
    		 if(activeRequests.size() > 0)
    			 activeRequests.remove(activeRequests.size()-1);
    		 httpOutput.writeBytes(statusLine(200,"OK"));
  			 httpOutput.writeBytes(contentType("./index.html"));
  			 httpOutput.writeBytes(CRLF);
  			 httpOutput.writeBytes("\r\n");
  			 return false;
    	 }
         /*fileInput = new FileInputStream(reqFileName);
         httpOutput.writeBytes(statusLine(200,"OK"));
         httpOutput.writeBytes(contentType(reqFileName));
         httpOutput.writeBytes(CRLF);*/
    	 output404(httpOutput);
    	 return false;
      }
      catch(FileNotFoundException ex)
      {
         try{
            output404(httpOutput);
         }
         catch(IOException why)
         {
            System.out.println("Something went wrong: " + why.toString());
         }
         return false;
      }
   }
   
   /**
   Outputs the 404 HTML doc
   @param httpOutput the output stream
   @throws IOException in the case of an error
   */
   private void output404(DataOutputStream httpOutput) throws IOException
   {
      System.out.println("File wasn't found: " + reqFileName);
      httpOutput.writeBytes(statusLine(404,"Not Found"));
      httpOutput.writeBytes(contentType("./404.html"));
      httpOutput.writeBytes(CRLF);
      httpOutput.writeBytes("<html><head>\n" +
         "<title>Not Found</title>\n" +
         "</head><body>\n" +
         "Not Found\n" +
         "</body></html>\r\n");
   }
}
