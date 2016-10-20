package echo.server;

import java.io.*;
import java.net.*;

/**
 * A simple server that will echo client inputs.
 */
public class EchoServer {

	private static boolean isStopped = false;
    /**
     * @param args String array containing Program arguments.  It should only 
     *      contain at most one String indicating the port it should connect to.
     *      The String should be parseable into an int.  
     *      If no arguments, we default to port 4444.
     */
	public static void main(String[] args) throws IOException {
		int portNumber = 0;
		try
		{
			portNumber = Integer.parseInt(args[0]);
		}
		catch(ArrayIndexOutOfBoundsException e){portNumber = 4444;}
		
		// I use a separate try block for the ServerSocket.
		// This way when the connection with the client closes,
		// the server socket will still be open.
		ServerSocket s = null;
		try
		{
			s = new ServerSocket(portNumber);
		}
		catch(Exception e){System.out.println( e + " Failed to listen on the assigned port. Oopsy!"); isStopped = true;}

		while( !isStopped){
		// Open a new connection with the client
		try(Socket incoming = s.accept();
			BufferedReader in = new BufferedReader( new InputStreamReader(incoming.getInputStream()));
			PrintWriter out = new PrintWriter( new OutputStreamWriter( incoming.getOutputStream())); )
		{
			isStopped = false;
			out.println("Hello! ....");;
			out.println("Enter BYE to exit.");
			out.flush();
			
			while (!isStopped)
			{
				String str = in.readLine();
				if( str == null)
				{
					isStopped = true;
					break; // client closed connection
				}
				else
				{
					out.println(">>> " + str);
					out.flush();
					if( str.trim().equals("BYE")) break;
				}
			}
			incoming.close();
		}catch(Exception e){System.out.println(e); isStopped = true;}
	}// End while( !isStopped )
		
	// isStopped is true; close the ServerSocket
	s.close();
	
}// End main()
}// End Class EchoServer
