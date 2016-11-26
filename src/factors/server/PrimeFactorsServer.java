package factors.server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Iterator;

import immutable.EmptyImList;
import immutable.ImList;
import util.BigMath;

/**
 *  PrimeFactorsServer performs the "server-side" algorithm 
 *  for counting prime factors.
 *
 *  Your PrimeFactorsServer should take in a single Program Argument 
 *  indicating which port your Server will be listening on.
 *      ex. arg of "4444" will make your Server listen on 4444.
 *      
 *  Your server will only need to handle one client at a time.  If the 
 *  connected client disconnects, your server should go back to listening for
 *  future clients to connect to.
 *  
 *  The client messages that come in will indicate the value that is being
 *  factored and the range of values this server will be processing over.  
 *  Your server will take this in and message back all factors for our value.
 */
public class PrimeFactorsServer {
            
	private static boolean isStopped = false;
    /**
     * @param args String array containing Program arguments.  It should only 
     *      contain one String indicating the port it should connect to.
     *      Defaults to port 4444 if no Program argument is present.
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
//			out.println("Hello! ....");;
//			out.println("Enter BYE to exit.");
//			out.flush();
			
			while (!isStopped)
			{
				String str = in.readLine();
				ImList<BigInteger> factors = new EmptyImList<BigInteger>();
				if( str == null)
				{
					isStopped = true;
					break; // client closed connection
				}
				else
				{
					// Evaluate message from Client. Should be in format "factor n low high"
					String[] input = str.split(" ");
//				System.out.println("[" + input[0] + "]");
					if(input[0].matches("factor") && input.length == 4)
					{
						try
						{
							BigInteger n = new BigInteger(input[1]);
							BigInteger low = new BigInteger(input[2]);
							BigInteger high = new BigInteger(input[3]);
							// Where the magic happens
							factors = BigMath.primesOf(n, low, high);

							Iterator<BigInteger> it = factors.iterator();
							while(it.hasNext()) {out.println("found " + n + " " + it.next());}
							out.println("done " + n + " " + low + " " + high);
							out.flush();
							continue;
						}
						catch(Exception e){out.println("invalid!"); out.flush();}
					}
					else
					{
						out.println("invalid"); out.flush();
					}
				}
			}
			incoming.close();
		}catch(Exception e){System.out.println(e); isStopped = true;}
	}// End while( !isStopped )
		
	// isStopped is true; close the ServerSocket
	s.close();
	
    }
}
