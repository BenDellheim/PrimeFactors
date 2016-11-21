package factors.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *  PrimeFactorsClient class for PrimeFactorsServer.  
 *  
 *  Your PrimeFactorsClient class should take in Program arguments space-delimited
 *  indicating which PrimeFactorsServers it will connect to.
 *      ex. args of "localhost:4444 localhost:4445 localhost:4446"
 *          will connect the client to PrimeFactorsServers running on
 *          localhost:4444, localhost:4445, localhost:4446 
 *
 *  Your client should take user input from standard input.  The appropriate input
 *  that can be processed is a number.  If your input is not of the correct format,
 *  you should ignore it and continue to the next one.
 *  
 *  Your client should distribute to each server the appropriate range of values
 *  to look for prime factors through.
 */
public class PrimeFactorsClient {
    
    /**
     * @param args String array containing Program arguments.  Each String indicates a 
     *      PrimeFactorsServer location in the form "host:port"
     *      If no program arguments are inputted, this Client will terminate.
     */
    public static void main(String[] args) {
    	// 1. Check for proper program arguments; return otherwise.
    	ArrayList<Integer> portList = new ArrayList<Integer>();
		try
		{
			for(int i = 0; i < args.length; i++) portList.add(Integer.parseInt(args[i]));
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Please use command-line arguments of the form localhost:4444 localhost:5555 localhost:6666");
			return;
		}
		catch(Exception e){return;}
		
		// 2. Main loop. Read in number to factor.
		System.out.print("Hello! ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean isRunning = true;
		while(isRunning)
		{
			System.out.println("Please input the number to factor.");
			String input = null;
			try {
				input = reader.readLine().trim();
				BigInteger n = new BigInteger(input);
				
				// 3. Split up the work for factoring n, depending on portList's length
				
				
			} catch (NumberFormatException e) {
				if(input.length() == 0) isRunning = false;
				else System.out.println("That wasn't a number. Please check for mistypes and try again!\n");
			}
			catch(IOException e){
				e.printStackTrace();
			}

		}// End while
    }// End main
}// End class PrimeFactorsClient
