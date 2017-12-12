package rsa;

import crypto.Crypto;
import crypto.Pair;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;


public class RSACore {

    protected BigInteger N;
    private ThreadLocalRandom random;
    protected BigInteger P;
    protected BigInteger Q;
    private BigInteger f;
    protected BigInteger c, d;

    protected RSACore() {
        random = ThreadLocalRandom.current();
        P = BigInteger.probablePrime(8, random);
        Q = BigInteger.probablePrime(10, random);
        N = P.multiply(Q);
        f = P.subtract(ONE).multiply(Q.subtract(ONE));
        Pair<BigInteger, BigInteger> pair = generateAdditionalKeys();
        c = pair.getFirst();
        d = pair.getSecond();
        System.out.println("N:" + N + " P:" + P + " Q:" + Q + " c:" + c + " d:" + d);
    }

    protected RSACore(int pBitLength, int nBitLength) {
        random = ThreadLocalRandom.current();
        P = BigInteger.probablePrime(pBitLength, random);
        Q = BigInteger.probablePrime(nBitLength, random);
        N = P.multiply(Q);
        f = P.subtract(ONE).multiply(Q.subtract(ONE));
        Pair<BigInteger, BigInteger> pair = generateAdditionalKeys();
        c = pair.getFirst();
        d = pair.getSecond();
        System.out.println("N:" + N + " P:" + P + " Q:" + Q + " c:" + c + " d:" + d);
    }

    public Pair<BigInteger, BigInteger> generateAdditionalKeys() {
        List<BigInteger> gcdList;
        do {
            c = new BigInteger(16, random);
            gcdList = Crypto.gcd(c, f);
        } while(!gcdList.get(0).equals(ONE));
        d = gcdList.get(1).compareTo(ZERO) > 0 ? gcdList.get(1)
                : P.subtract(ONE)
                .multiply(Q.subtract(ONE))
                .add(gcdList.get(1));
        return new Pair<>(c, d);
    }

    public void regeneratePrivateKeys() {
        generateAdditionalKeys();
    }

    public BigInteger getNPublicKey() {
        return N;
    }

    public BigInteger getDPublicKey() {
        return d;
    }

}
