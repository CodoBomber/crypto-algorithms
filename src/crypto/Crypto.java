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


    /**
     * https://www.cs.cornell.edu/courses/cs4820/2010sp/handouts/MillerRabin.pdf
     * @param n
     * @return
     */
    public boolean isProbablePrime(BigInteger n) {
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
