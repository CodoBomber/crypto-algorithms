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

    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);

//    @Override
    public List<BigInteger> gcd(BigInteger p, BigInteger q) {
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

    public boolean isPrime(BigInteger n) {

//        for (k = 0; m.mod(TWO).equals(BigInteger.ZERO); m = m.divide(TWO), k++);
//        for (int i = 0; i < rounds; i++) {
        // a = [2; n - 2)
        if (n.equals(TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (n.equals(BigInteger.ZERO) || n.equals(ONE) || n.mod(TWO).equals(BigInteger.ZERO)) {
            return false;
        }
        // TODO: 17/09/14 Запилить рекомендуемый двоичный логарифм
        int round = 25;

        BigInteger sub = n.subtract(ONE);
        int expo = sub.getLowestSetBit();
        BigInteger m = sub.shiftRight(expo);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < round; i++) {
            // a = [2; n - 2)
            BigInteger a;
            for (a = new BigInteger(n.bitLength(), random);
                 a.compareTo(ONE) <= 0 || a.compareTo(sub) >= 0;
                 a = new BigInteger(n.bitLength(), random))
                ;

            if (!a.modPow(sub, n).equals(ONE)) {
                return false;
            }
            BigInteger temp = a.modPow(m, n);
            if (temp.equals(ONE) || temp.equals(sub)) {
                return true;
            }
            for (int j = 0; j < expo; j++, temp = a.modPow(TWO, n)) {
                if (temp.equals(sub)) {
                    return true;
                }
                if (temp.equals(ONE)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * https://www.cs.cornell.edu/courses/cs4820/2010sp/handouts/MillerRabin.pdf
     * @param n
     * @return
     */
    public boolean isMillerPrime(BigInteger n) {
        if (n.equals(TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (n.equals(BigInteger.ZERO) || n.equals(ONE) || n.mod(TWO).equals(BigInteger.ZERO)) {
            return false;
        }
        // TODO: 17/09/14 Запилить рекомендуемый двоичный логарифм
            int round = 25;

            BigInteger sub = n.subtract(ONE);
            int expo = sub.getLowestSetBit();
            BigInteger m = sub.shiftRight(expo);
            ThreadLocalRandom random = ThreadLocalRandom.current();

            for(int i = 0; i < round; i++) {
                // a = [2; n - 2)
                BigInteger a;
                for (a = new BigInteger(n.bitLength(), random);
                     a.compareTo(ONE) <= 0 || a.compareTo(sub) >= 0;
                     a = new BigInteger(n.bitLength(), random));

                if (!a.modPow(sub, n).equals(ONE)) {
                    return false;
                }
                BigInteger temp = a.modPow(m, n);
                if (temp.equals(ONE) || temp.equals(sub)) {
                    return true;
                }
                for (BigInteger j = m; j.compareTo(sub) < 0; j = j.multiply(TWO), temp = temp.modPow(TWO, n)) {
                    if (temp.equals(sub)) {
                        return true;
                    }
                    if (temp.equals(ONE)) {
                        return false;
                    }
                }
                /*for (BigInteger temp = a.modPow(m, n); !((j == 0 && temp.equals(ONE)) || temp.equals(sub));
                    temp = temp.modPow(TWO, n)) {

                    if (j > 0 && temp.equals(ONE) || BigInteger.valueOf(j++).equals(m)) {
                        return false;
                    }
                }*/
            }

            return true;
    }

//    @Override
    public static BigInteger binaryPow(BigInteger x, BigInteger a, BigInteger mod) {
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

    /** Возврат простого чиста из рандомной статики
     * Альтернативой является перебор по алгоритму Решето Эратосфена
     * @return простое число
     */
//    @Override
    public BigInteger getPrimeNumber(int bitLength) {
        /*final int[] primeNumbers = {2,17,257,1297,65537,160001,331777,614657,1336337,
                4477457,5308417,8503057,9834497,29986577,40960001,
                45212177,59969537,65610001,126247697,193877777,
                303595777,384160001,406586897,562448657,655360001};*/
        return BigInteger.probablePrime(bitLength, new Random()).multiply(BigInteger.valueOf(2)).add(ONE);
    }


}
