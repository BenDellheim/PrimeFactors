package echo.client;

import java.io.*;
import java.net.*;

/**
 * A simple client that will interact with an EchoServer.
 */
public class EchoClient {

	/**
	 * @param args String array containing Program arguments.  It should only 
	 *      contain exactly one String indicating which server to connect to.
	 *      We require that this string be in the form hostname:portnumber.
	 */
	public static void main(String[] args) throws IOException {
	    try{
	    	String host;
	    	if( args.length > 0){
	    		host = args[0];
	    	}
	    	else{
	    		host = "localhost";
	    	}
	    	Socket socket = new Socket(host, 4444);
			BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter( new OutputStreamWriter( socket.getOutputStream()));

			// Send data to the server
			for( int i = 1; i <= 1000; i++)
			{
				System.out.println("Sending: line " + i);
				out.println("line " + i);
				out.flush();
			}
			out.println("BYE");
			out.flush();
			
			// receive data from the server
			while( true){
				String str = in.readLine();
				if( str == null){
					break;
				}else {
					System.out.println(str);
				}
			}
			socket.close();
	    }catch( Exception e){}
	}

}
