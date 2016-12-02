package factors.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import immutable.EmptyImList;
import immutable.ImList;
import util.BigMath;

/**
 *  PrimeFactorsClient class for PrimeFactorsServer.  
 *  
 *  The PrimeFactorsClient class takes in Program arguments space-delimited
 *  indicating which PrimeFactorsServers it will connect to.
 *      ex. args of "localhost:4444 localhost:5555 localhost:6666"
 *          will connect the client to PrimeFactorsServers running on
 *          localhost:4444, localhost:5555, localhost:6666 
 *
 *  The client takes user input from standard input. Only numbers are processed;
 *  any other inputs are ignored with an "invalid" message.
 *  
 *  Afterwards, the client distributes the prime factor searches to the
 *  PrimeFactorsServer ports provided, then waits for and compiles the results for the user.
 */
public class PrimeFactorsClient {
    
    /**
     * @param args String array containing Program arguments.  Each String indicates a 
     *      PrimeFactorsServer location in the form "host:port"
     *      If no program arguments are inputted, this Client will terminate.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	// 1. Check for proper program arguments; return otherwise.
    	ArrayList<Integer> portList = new ArrayList<Integer>();
    	String host = "localhost";
		try
		{
			for(int i = 0; i < args.length; i++) portList.add(Integer.parseInt(args[i].replaceAll("[^0-9]", "")));
		}
		catch(IndexOutOfBoundsException e)
		{
			respond("Please use command-line arguments of the form localhost:4444 localhost:5555 localhost:6666");
			return;
		}
		catch(Exception e){return;}
		
		// Lists for factoring n and communicating with the servers
	   	ArrayList<Socket> socketList     = new ArrayList<Socket>(portList.size());
	   	ArrayList<BufferedReader> inList = new ArrayList<BufferedReader>(portList.size());
	   	ArrayList<PrintWriter> outList   = new ArrayList<PrintWriter>(portList.size());

	   	try
	    {
	      	// 2. Open sockets to the ports provided
	       	for(int i = 0; i < portList.size(); i++)
	       	{
	       		socketList.add(new Socket(host, portList.get(i)));
	       		inList.add(new BufferedReader( new InputStreamReader(socketList.get(i).getInputStream())));
	       		outList.add(new PrintWriter( new OutputStreamWriter( socketList.get(i).getOutputStream())));
			}
	    }	catch(Exception e){respond("unknown host");}
   	
	       	
		// 3. Main loop. Read in number to factor.
		respond("Hello!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean isRunning = true;
		while(isRunning)
		{
			respond("Please input the number to factor.");
			String input = null;
			BigInteger n;
			try
			{
				input = reader.readLine().trim();
				n = new BigInteger(input);
			} catch (NumberFormatException e) {
				if(input.length() == 0) isRunning = false;
				else respond("invalid");
				continue;
			} catch(IOException e){e.printStackTrace(); break;}
				

			try
			{
				// 4. Split up the work for factoring n, depending on portList's length
		       	// Each range is from [x, x+q-1] where
		       	//  q = sqrt(n)/#servers
		       	//  x = 1 then x += q for subsequent ranges
		       	// i.e. for 3 servers we have the ranges [1, q], [q+1, 2q], [2q+1, 3q]
		       	// (Pretty neat, huh?)
		       	String size = Integer.toString(portList.size());
		       	BigInteger one = BigInteger.ONE;
		       	BigInteger x = one;
		       	BigInteger q = BigMath.sqrt(n).divide(new BigInteger(size));
		       	for(int i = 0; i < portList.size(); i++)
		       	{
		       		outList.get(i).println("factor " + n + " " + x + " " + x.add(q).subtract(one));
		       		outList.get(i).flush();
		       		x = x.add(q);
		       	}
		       		
		       	// 5. Listen for "found factor"/"done" messages and aggregate them
		       	ImList<BigInteger> factors = new EmptyImList<BigInteger>();			//Holds factors returned from PrimeFactorsServer
		       	ArrayList<Boolean> portsOpen = new ArrayList<Boolean>();			//.get(i) TRUE -> port i open
		       	for(int i = 0; i < portList.size(); i++)portsOpen.add(Boolean.TRUE);//All ports initialized to TRUE (open)

		       	do
		       	{
		       		for(int i = 0; i < portList.size(); i++)
		       		{
		       			if(portsOpen.get(i))
		       			{
		       				// Parse the received input
		       				String[] line = inList.get(i).readLine().split(" ");
		       				if(line.length == 3 &&
		       				   line[0].matches("found") && 
		       				   n.equals(new BigInteger(line[1])))
		       				{	// "found n factor" -> add factor to list
		       					factors = factors.add(new BigInteger(line[2]));
		       				}
		       				else if(line.length == 4 &&
		       						line[0].matches("done") &&
		       						n.equals(new BigInteger(line[1])))
		       				{	// "done n low hi" -> mark port as closed
		          					portsOpen.set(i, Boolean.FALSE);
		       				}
		       				// Ignore any other messages; the servers aren't listening for feedback
		       			}
		       		}
				}while(Collections.frequency(portsOpen, Boolean.FALSE) != portList.size());
		       		
		       	// 6. Confirm factors and display answer to user
		       	factors = BigMath.getVerifiedPrimes(factors, n);
		       	if(BigMath.isValidPrimeList(factors))
		       	{
		       		Iterator<BigInteger> it = factors.iterator();
		       		String response = n + "=" + it.next();
		       		while( it.hasNext())
		       		{
		       			response += "*" + it.next();
		       		}
		       		respond(response);
		       	}
		       	else
		       	{
		       		respond("invalid");
		       	}
		    }catch(Exception e)
			{
		    	respond("invalid");
		    	for(int i = 0; i < portList.size(); i++)
		    	{
		    		if(inList.get(i) != null) inList.get(i).close();
		    		if(outList.get(i) != null) outList.get(i).close();
		    		if(socketList.get(i) != null) socketList.get(i).close();
		    	}
		    }
			
		}// End while( isRunning)
		return;
    }// End main

    /**
      * @param output message being displayed to User
      */
    public static void respond( String output)
    {    	System.out.println(">>> " + output);    }

}// End class PrimeFactorsClient
