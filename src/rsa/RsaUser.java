package rsa;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;

public class RsaUser {

    private final BigInteger N;
    private BigInteger c, d;

    public RsaUser() {
        Random random = ThreadLocalRandom.current();
        BigInteger P = BigInteger.probablePrime(20, random);
        BigInteger Q = BigInteger.probablePrime(30, random);
        N = P.multiply(Q);
        BigInteger f = P.subtract(ONE).multiply(Q.subtract(ONE));
        List<BigInteger> gcdList;
        do {
            c = new BigInteger(21, random);
            gcdList = Crypto.gcd(c, f);
        } while(!gcdList.get(0).equals(ONE));
        d = gcdList.get(1);
    }

    public BigInteger encryptMessage(BigInteger msg,
                                     BigInteger pubKey1,
                                     BigInteger pubKey2) {
        return msg.modPow(pubKey1, pubKey2);
    }

    public BigInteger decryptMessage(BigInteger msg) {
        return msg.modPow(this.c, this.N);
    }

    public BigInteger getN() {
        return N;
    }

    public BigInteger getD() {
        return d;
    }

}
