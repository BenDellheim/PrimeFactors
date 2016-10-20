package util;

import java.math.BigInteger;

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
     * @requires BigInteger N. such that 2 <= N
     * @requires BigInteger low, hi. such that 1 <= low <= hi
     * @effects finds all prime BigIntegers x 
     *  such that low <= x <= hi AND x divides N evenly.
     *  Repeated prime factors will be found multiple times.
     */
    public static BigInteger[] primesOf(BigInteger n){
    	BigInteger low = BigInteger.ONE, high = sqrt(n);
    	BigInteger[] result = new BigInteger[high.intValue()];
    	int resultOffset = 0;
    	if(n.intValue() < 2) return null;
    	for( BigInteger x = low; x.compareTo(high) <= 0; x = x.add(new BigInteger("2")))
    	{
    		if(isPrime(x))
    		{
    			// While x divides evenly into n, add x to result then divide out x.
    			while(n.mod(x) == BigInteger.ZERO)
    			{
    				result[resultOffset] = x;
    				resultOffset++;
    				n = n.divide(x);
    			}
    		}
    	}
    	return result;
    }
    
    // Code thanks to a thread on CodeReview:
    // http://codereview.stackexchange.com/questions/43490/biginteger-prime-testing/
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
}
