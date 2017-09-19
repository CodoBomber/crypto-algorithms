package crypto;

import java.math.BigInteger;
import java.util.Random;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class SolovayStrassen {

    private static BigInteger Jacobi(BigInteger a, BigInteger b)
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
            while (a.mod(Crypto.TWO).equals(ZERO)) {
                a = a.divide(Crypto.TWO);
                if (b.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(3))
                        || b.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(5)))
                    j = j.negate();
            }

            BigInteger temp = a;
            a = b;
            b = temp;

            if (a.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))
                    && b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                j = j.negate();
            }
            a = a.mod(b);
        }
        if (b.equals(ONE)) {
            return j;
        }
        return ZERO;
    }

    /**
     * Считает числа Кармайкла составными
     */
    public static boolean isPrime(BigInteger n, int iteration)
    {
        if (n.equals(ZERO) || n.equals(ONE)) {
            return false;
        }
        if (n.equals(Crypto.TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (n.mod(Crypto.TWO).equals(ZERO)) {
            return false;
        }

        Random random = new Random();
        for (int i = 0; i < iteration; i++)
        {
            BigInteger a, sub = n.subtract(ONE);
            for (a = new BigInteger(n.bitLength(), random);
                 a.compareTo(ONE) <= 0 || a.compareTo(sub) >= 0;
                 a = new BigInteger(n.bitLength(), random));
            BigInteger jacobian = n.add(Jacobi(a, n)).mod(n);
            BigInteger mod = Crypto.modPow(a, sub.divide(Crypto.TWO), n);
            if(jacobian.equals(ZERO) || !mod.equals(jacobian)) {
                return false;
            }
        }
        return true;
    }

    private static long Jacobi(long a, long b)
    {
        if (b <= 0 || b % 2 == 0) {
            return 0;
        }
        long j = 1;
        if (a < 0) {
            a = -a;
            if (b % 4 == 3) {
                j = -j;
            }
        }
        while (a != 0) {
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
    }

    /**
     * Считает числа Кармайкла составными
     */
    public static boolean isPrime(long n, int iteration)
    {
        if (n < 2)
            return false;
        if (n == 2 || n == 3)
            return true;
        if (n % 2 == 0)
            return false;

        Random rand = new Random();
        for (int i = 0; i < iteration; i++)
        {
            long r = Math.abs(rand.nextLong());
            long a = r % (n - 1) + 1;
            long jacobian = (n + Jacobi(a, n)) % n;
            long mod = Crypto.modPow(BigInteger.valueOf(a), BigInteger.valueOf((n - 1)/2),
                    BigInteger.valueOf(n)).longValue();
            if(jacobian == 0 || mod != jacobian)
                return false;
        }
        return true;
    }
}
