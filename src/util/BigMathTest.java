package util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import org.junit.Test;
import immutable.*;

public class BigMathTest {

	@Test
	public void PrimeTest() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean isRunning = true;
		while(isRunning){
		System.out.print("Tell me to factor something. 'factor n low high'\n>>> ");
		try
		{
			String str = reader.readLine();
			String[] input = str.split(" ");
			if(input[0].matches("factor") && input.length == 4)
			{
				BigInteger n    = new BigInteger(input[1]);
				BigInteger low  = new BigInteger(input[2]);
				BigInteger high = new BigInteger(input[3]);
				ImList<BigInteger> factors = new EmptyImList<BigInteger>();
				factors = BigMath.primesOf(n, low, high);
				if( factors != null)
				{
					System.out.println(factors);
					System.out.println("done " + n + " " + low + " " + high);
				}
				else System.out.println("invalid");
			}
			else isRunning = false;

		}catch( IOException e){e.printStackTrace();}
		catch( NumberFormatException e) {System.out.println("Um, those weren't all numbers.");}
//		BigInteger q = new BigInteger("1155327631386368387");
/*		BigInteger q = new BigInteger("1155327");
		System.out.println("For input " + q + ":");
		System.out.println("sqrt is " + BigMath.sqrt(q));
		System.out.println("Prime? " + BigMath.isPrime(q));
		System.out.println("Finding factors... ");
		
		ImList<BigInteger> factors = new EmptyImList<BigInteger>();
		factors = BigMath.primesOf(q, new BigInteger("2"), q);
		if( factors != null) System.out.println(factors);
		else System.out.println("invalid");
*/	}}

}
