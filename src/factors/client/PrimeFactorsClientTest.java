package factors.client;

import static org.junit.Assert.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import immutable.EmptyImList;
import immutable.ImList;
import util.BigMath;


public class PrimeFactorsClientTest {

	@Test
	public void test() throws IOException {
    	ArrayList<Integer> portList = new ArrayList<Integer>();
    	// Run PrimeFactorsServer on these ports ahead of time
    	portList.add(4444);
    	portList.add(5555);
    	portList.add(6666);
    	String input = "18306";
    	BigInteger n = new BigInteger(input);
    	String host = "localhost";
		// 3. Split up the work for factoring n
   		ArrayList<Socket> socketList     = new ArrayList<Socket>(portList.size());
   		ArrayList<BufferedReader> inList = new ArrayList<BufferedReader>(portList.size());
   		ArrayList<PrintWriter> outList   = new ArrayList<PrintWriter>(portList.size());

   		try
       	{
       		// Open sockets to the ports provided
       		for(int i = 0; i < portList.size(); i++)
       		{
       			socketList.add(new Socket(host, portList.get(i)));
       			inList.add(new BufferedReader( new InputStreamReader(socketList.get(i).getInputStream())));
       			outList.add(new PrintWriter( new OutputStreamWriter( socketList.get(i).getOutputStream())));
			}
        	
        	// Send ranges to our servers ^_^
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
       		
       		// 4. Listen for "found factor"/"done" messages and aggregate them
       		ImList<BigInteger> factors = new EmptyImList<BigInteger>();			//Holds factors returned from PrimeFactorsServer
       		ArrayList<Boolean> portsOpen = new ArrayList<Boolean>();			//.get(i) TRUE -> port i open
       		for(int i = 0; i < portList.size(); i++)portsOpen.add(Boolean.TRUE);

/*       		Files.write(Paths.get("client-test-log.txt"),"open \n".getBytes("utf-8"),StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
       		do
       		{ // Test loop
       			for(int i = 0; i < portList.size(); i++)
       			{
       				if(portsOpen.get(i))
       				{
       					String in = inList.get(i).readLine() + "\n";
       					Files.write(Paths.get("client-test-log.txt"),in.getBytes("utf-8"),StandardOpenOption.APPEND);
       					if(in.contains("done")) portsOpen.set(i, Boolean.FALSE);
       				}
       			}
       		// Loop until all ports are closed (FALSE)
       		}while(Collections.frequency(portsOpen, Boolean.FALSE) != portList.size());
       		Files.write(Paths.get("client-test-log.txt"),"All ports closed.\n".getBytes("utf-8"),StandardOpenOption.APPEND);
*/       		
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
       					// Ignore any other messages
       				}
       			}
		}while(Collections.frequency(portsOpen, Boolean.FALSE) != portList.size());
       		
       	// 5. Confirm factors and display answer to user
       	factors = BigMath.getVerifiedPrimes(factors, n);
       	if(BigMath.isValidPrimeList(factors))
       	{
       		Iterator<BigInteger> it = factors.iterator();
       		System.out.print(">>> " + n + "=" + it.next());
       		for(; it.hasNext();)
       		{
       			System.out.print("*" + it.next());
       		}
       		System.out.print("\n");
       	}
       	else
       	{
       		System.out.println(">>> invalid");
       	}
       	
       	}catch(Exception e){System.err.println("Uh, what? " + e);}
    	finally
    	{
//    		System.err.println("TEST: Finally block");
    		for(int i = 0; i < portList.size(); i++)
    		{
    			if(inList.get(i) != null) inList.get(i).close();
    			if(outList.get(i) != null) outList.get(i).close();
    			if(socketList.get(i) != null) socketList.get(i).close();
    		}    		
    	}
	}

}
