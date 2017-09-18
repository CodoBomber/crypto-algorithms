package crypto;

import java.math.BigInteger;
import java.util.Random;

import static java.math.BigInteger.*;

public class SolovayStrassen {
    /*public BigInteger Jacobi(BigInteger a, BigInteger b)
    {
        if (b.compareTo(ZERO) <= 0 || b.mod(Crypto.TWO).equals(ZERO)) {
            return ZERO;
        }
        BigInteger j = ONE;
        if (a.compareTo(ZERO) < 0) {
            a = a.negate();
            if (b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                j = j.negate();
            }
        }
        while (!a.equals(ZERO)) {
            while (a % 2 == 0)
            {
                a /= 2;
                if (b % 8 == 3 || b % 8 == 5)
                    j = -j;
            }

            long temp = a;
            a = b;
            b = temp;

            if (a % 4 == 3 && b % 4 == 3)
                j = -j;
            a %= b;
        }
        if (b == 1)
            return j;
        return 0;
    }*/

    /**
     * Считает числа Кармайкла составными
     */
    /*public boolean isPrime(long n, int iteration)
    {
        *//** base case **//*
        if (n == 0 || n == 1)
            return false;
        *//** base case - 2 is prime **//*
        if (n == 2)
            return true;
        *//** an even number other than 2 is composite **//*
        if (n % 2 == 0)
            return false;

        Random rand = new Random();
        for (int i = 0; i < iteration; i++)
        {
            long r = Math.abs(rand.nextLong());
            long a = r % (n - 1) + 1;
            long jacobian = (n + Jacobi(a, n)) % n;
            long mod = modPow(a, (n - 1)/2, n);
            if(jacobian == 0 || mod != jacobian)
                return false;
        }
        return true;
    }*/
}
