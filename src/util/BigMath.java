package util;

import java.math.BigInteger;
import java.util.Iterator;

import immutable.*;

public class BigMath {

    /**
     * Given a BigInteger input n, where n >= 0, returns the largest BigInteger r such that r*r <= n.
     * 
     * For n < 0, returns 0.
     * 
     * 
     * details: http://faruk.akgul.org/blog/javas-missing-algorithm-biginteger-sqrt
     * 
     * @param n BigInteger input.
     * @return for n >= 0: largest BigInteger r such that r*r <= n.
     *             n <  0: BigInteger 0
     */
    public static BigInteger sqrt(BigInteger n) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
        while(b.compareTo(a) >= 0) {
          BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
          if (mid.multiply(mid).compareTo(n) > 0) 
              b = mid.subtract(BigInteger.ONE);
          else 
              a = mid.add(BigInteger.ONE);
        }
        return a.subtract(BigInteger.ONE);
    }
    
    /**
     * @requires BigInteger n such that 2 <= n
     * @requires BigInteger low, high such that 1 <= low <= high
     * @effects finds all prime BigIntegers x 
     *  such that low <= x <= high AND x divides N evenly.
     *  Repeated prime factors will be found multiple times.
     *  Note that high need not be higher than sqrt(n), and will be set to sqrt(n) if so.
     */
    public static ImList<BigInteger> primesOf(BigInteger n, BigInteger low, BigInteger high){
    	// Confirm all arguments are in the proper range
    	if(low.compareTo(BigInteger.ONE) < 0) return null;    // If low<1 return null
    	if(low.compareTo(high) > 0) return null;			  // If low>high return null
    	if(n.compareTo(new BigInteger("2")) < 0) return null; // If n<2 return null
    	if(high.compareTo(sqrt(n)) > 0) high = sqrt(n);       // If high > sqrt(n), high=sqrt(n)
    	
    	
    	ImList<BigInteger> result = new EmptyImList<BigInteger>(); 
    	for( BigInteger x = low; x.compareTo(high) <= 0; x = x.nextProbablePrime())
    	{
    		if(isPrime(x))
    		{
    			// While x divides evenly into n, add x to result. Then, divide out x.
    			while(n.mod(x) == BigInteger.ZERO)
    			{
    				System.out.println("found " + n + " " + x);
    				result = result.add(x);
    				n = n.divide(x);
    			}
				if(n.equals(BigInteger.ONE)) break;
    		}
    	}
    	// If n still isn't 1 after all that and we covered the full range, n is a prime too.
    	if(!n.equals(BigInteger.ONE)
    	   && (low.compareTo(new BigInteger("2")) == 0)
    	   && (high.compareTo(sqrt(n)) >= 0))
    	{
    		result = result.add(n);
			System.out.println("Found factor " + n);
    	}
    	return result;
    }
    
    /**
     * @effects Returns true if prime, false if not prime.
     * The value in the first line, isProbablePrime(10), can be edited for improved accuracy
     * (increase 10 to something else) at the cost of significant overhead.
     * Code thanks to a thread on CodeReview:
     * http://codereview.stackexchange.com/questions/43490/biginteger-prime-testing/
     */
    public static Boolean isPrime(BigInteger n){
    	if(!n.isProbablePrime(10)) return false;
    	if(n.compareTo(BigInteger.ONE) == 0 || (n.compareTo(new BigInteger("2")) == 0)) return true;
    	BigInteger root = sqrt(n);
    	
    	for(BigInteger i = new BigInteger("3"); (i.compareTo(root) <= 0); i = i.nextProbablePrime() )
    	{
    		if(n.mod(i).equals(BigInteger.ZERO)) return false;
    	}
    	return true;
    }

    /**
     * @requires ImList<BigInteger> primes, the output of one or more primesOf() calls
     * @requires BigInteger n, the same number called with primesOf()
     * @effects Verifies the product of primes = n. At most one prime can be > sqrt(n),
	 * so this ensures a missing factor is prime and adds it to the list. 
	 * @returns Full list of prime factors for n, or a list containing 0 if something's wrong.
     */
	public static ImList<BigInteger> getVerifiedPrimes( ImList<BigInteger> primes, BigInteger n)
	{
		BigInteger p = BigInteger.ONE;
		Iterator<BigInteger> it = primes.iterator();
		while( it.hasNext() )
		{
			p = it.next().multiply(p);
		}
		// If factors multiply up to n, we're golden. Return the list with no change.
		if( p.compareTo(n) == 0) return primes;

		// If factors multiply to MORE than n, or if p ISN'T divisible by n, somebody goofed. Return an error value.
		if( p.compareTo(n) > 0 || n.mod(p).compareTo(BigInteger.ZERO) != 0) return new NonEmptyImList<BigInteger>(BigInteger.ZERO);
		
		// Otherwise, the product is <n and we're just missing a factor.
		BigInteger x = n.divide(p);
		if( x.isProbablePrime(10) && x.multiply(p).compareTo(n) == 0)return primes.add(x);
		else
			// If n/p isn't prime or x*p isn't n, somebody goofed. Return an error value.
			return new NonEmptyImList<BigInteger>(BigInteger.ZERO);
	}
	
    /**
     * @requires ImList<BigInteger> primes, the output of getVerifiedPrimes
     * @returns false if primes contains ZERO (the error value of getVerifiedPrimes),
     * or if primes is a list of size 0.
     */
	public static Boolean isValidPrimeList( ImList<BigInteger> primes)
	{
		return !primes.contains(BigInteger.ZERO) || primes.size() == 0;
	}
}
