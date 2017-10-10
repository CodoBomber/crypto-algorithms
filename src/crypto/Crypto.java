package crypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class Crypto implements CryptoAlgorithms {

    public static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);

    /**
     * Метод, который возвращает возможное простое число по алгоритму Рабина Миллера или Соловья-Штрассена
     * @param bitLength Величина числа, которое мы подаём в рандомный генератор {@link BigInteger(int, Random)}
     * @return Возможное простое число
     */
    public static BigInteger getRandomProbablePrime(int bitLength) {
        Random random = ThreadLocalRandom.current();
        BigInteger prime;
        for (prime = new BigInteger(bitLength, random);
             !isProbablePrime(prime);
             prime = new BigInteger(bitLength, random));
        return prime;
    }
//    @Override

    public static List<BigInteger> generatePG() {
        BigInteger q, p, g;
        int pBitLength = 40, gBitLength = 30;
        do {
            q = Crypto.getRandomProbablePrime(pBitLength);
            p = q.multiply(BigInteger.valueOf(2))
                    .add(BigInteger.ONE);
        } while (!p.isProbablePrime(25));

        BigInteger b = BigInteger.ONE;
        g = new BigInteger(gBitLength, ThreadLocalRandom.current());
        for (b = Crypto.modPow(g, q, p);
             b.equals(BigInteger.ONE);
             g = new BigInteger(gBitLength, ThreadLocalRandom.current()),
             b = Crypto.modPow(g, q, p));
        return Arrays.asList(p, g);
    }

    public static List<BigInteger> gcd(BigInteger p, BigInteger q) {
        if (q.equals(BigInteger.ZERO)) {
            return new ArrayList<>(Arrays.asList(p, ONE, BigInteger.ZERO));
        }
        List<BigInteger> result = gcd(q, p.mod(q));
        return new ArrayList<> (Arrays.asList(result.get(0), result.get(2),
                result.get(1)
                        .subtract(p.divide(q)
                                .multiply(result.get(2))
                        )));
    }

    /*public static List<BigInteger> gcd(BigInteger p, BigInteger q) {
        if (p.compareTo(q) < 0) {
            BigInteger t = p;
            p = q;
            q = t;
        }
        ArrayList<BigInteger> U = new ArrayList<>(Arrays.asList(p, ONE, ZERO)),
                V = new ArrayList<>(Arrays.asList(q, ZERO, ONE)),
                T = new ArrayList<>(Arrays.asList(ZERO, ZERO, ZERO));
        BigInteger w;
        while (!V.get(0).equals(ZERO)) {
            w = U.get(0).divide(V.get(0));
            T.set(0, U.get(0).mod(V.get(0)));
            T.set(1, U.get(1).subtract(w.multiply(V.get(1))));
            T.set(2, U.get(2).subtract(w.multiply(V.get(2))));
            U = new ArrayList<>(V);
            V = new ArrayList<>(T);
        }
        return U;
    }*/


    /**
     * https://www.cs.cornell.edu/courses/cs4820/2010sp/handouts/MillerRabin.pdf
     * @param n
     * @return
     */
    public static boolean isProbablePrime(BigInteger n) {
        // TODO: 17/09/18 Использовать рекомендованный логарифм(2) от round
        int rounds = 25;
        if (n.equals(TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (n.equals(ZERO) || n.equals(ONE) || n.mod(TWO).equals(ZERO)) {
            return false;
        }
        /*while (s.mod(TWO).equals(ZERO)) {
            s = s.divide(TWO);
        }*/
        BigInteger sub = n.subtract(ONE);
        int expo = sub.getLowestSetBit();
        BigInteger s = sub.shiftRight(expo);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < rounds; i++) {
            BigInteger a;
            for (a = new BigInteger(n.bitLength(), random);
                 a.compareTo(ONE) <= 0 || a.compareTo(sub) >= 0;
                 a = new BigInteger(n.bitLength(), random));
            BigInteger m = s, temp = modPow(a, m, n);
            while (!m.equals(sub) && !temp.equals(ONE) && !temp.equals(sub)) {
                temp = temp.multiply(temp).mod(n);
                m = m.multiply(TWO);
            }
            if (!temp.equals(sub) && m.mod(TWO).equals(ZERO)) {
                return false;
            }
        }
        return true;
    }
//    @Override

    public static BigInteger modPow(BigInteger x, BigInteger a, BigInteger mod) {
        BigInteger result = ONE;
        x = x.mod(mod);
        for (int i = 0; i < a.bitLength(); ++i) {
            if (a.testBit(i)) {
                result = result.multiply(x)
                        .mod(mod);
            }
            x = x.multiply(x).mod(mod);
        }
        return result;
    }

    public static BigInteger power(BigInteger x, BigInteger a) {
        BigInteger result = ONE;
        for (int i = 0; i < a.bitLength(); ++i) {
            if (a.testBit(i)) {
                result = result.multiply(x);
            }
            x = x.multiply(x);
        }
        return result;
    }

}
