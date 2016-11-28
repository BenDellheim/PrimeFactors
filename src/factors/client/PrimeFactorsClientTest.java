package factors.client;

import static org.junit.Assert.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.math.BigInteger;
import java.net.Socket;

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
    	String input = "1289783";
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
       		// i.e. Divide [1, sqrt(n)] into 3: [1, sqrt(n)/3], [sqrt(n)/3+1, 2*sqrt(n)/3], [2*sqrt(n)/3+1, sqrt(n)]
       		// Start at 1 and count "size" times. Divide range into "size" ranges.
       		// x = 1
       		// q = sqrt(n)/size
       		// 1st range: [x, x+q-1] (1, q)
       		// x = x+q (1+q)
       		// 2nd range: [x, x+q-1] (q+1, 2q)
       		// x = x+q
       		// 3rd range: [x, x+q-1] (2q+1, 3q)
       		String size = Integer.toString(portList.size());
       		BigInteger one = BigInteger.ONE;
       		BigInteger x = one;
       		BigInteger q = BigMath.sqrt(n).divide(new BigInteger(size));
       		for(int i = 0; i < portList.size(); i++)
       		{
       				System.out.println("factor " + n + " " + x + " " + x.add(q).subtract(one));
       			outList.get(i).println("factor " + n + " " + x + " " + x.add(q).subtract(one));
       			outList.get(i).flush();
       			x = x.add(q);
       		}
       		
       		// 4. Listen for "found factor"/"done" messages and aggregate them
       		ImList<BigInteger> factors = new EmptyImList<BigInteger>();
   			ArrayList<String> inputs = new ArrayList<String>(portList.size());	
       		ArrayList<Boolean> portsOpen = new ArrayList<Boolean>(portList.size());
       		Collections.fill(portsOpen, Boolean.TRUE);
       		do
       		{ // Test loop
       			for(int i = 0; i < portList.size(); i++)
       			{
       				if(portsOpen.get(i))
       				{
       					String in = inList.get(i).readLine();
       					System.out.println(">>>" + in);
       					if(in.contains("done")) portsOpen.set(i, Boolean.FALSE);
       				}
       			}
       		// Loop until all ports are closed (FALSE)
       		}while(Collections.frequency(portsOpen, Boolean.FALSE) != portList.size());
       		
       		System.out.println("All ports closed?");
/*       		
       		while(stillWaiting)
       		{
       			ArrayList<String> inputs = new ArrayList<String>(portList.size());	
       			for(int i = 0; i < portList.size(); i++)
       			{
       				if(inList.get(i) != null)
       				{
       					inputs.set(i, inList.get(i).readLine());
       					// Parse the received input
       					String[] line = inputs.get(i).split(" ");
       					if(line.length == 3 &&
       					   line[0].matches("found") && 
       					   n.equals(new BigInteger(line[1])))
       					{
       						factors = factors.add(new BigInteger(line[2]));
       					}
       					else if(line.length == 4 &&
       							line[0].matches("done") &&
       							n.equals(new BigInteger(line[1])))
       					{
       						
       					}

       				}
       			}
       		}
*/    	}catch(Exception e){System.out.println("Uh, what? " + e);}
    	finally
    	{
    		System.out.println("TEST: Finally block");
    		for(int i = 0; i < portList.size(); i++)
    		{
    			if(inList.get(i) != null) inList.get(i).close();
    			if(outList.get(i) != null) outList.get(i).close();
    			if(socketList.get(i) != null) socketList.get(i).close();
    		}    		
    	}
   		System.out.println("OMG teh bai");

	}

}
