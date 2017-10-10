package shamir;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SUser {

    private Shamir system;
    private BigInteger ca, da;

    public SUser(Shamir shamir) {
        system = shamir;
        Random random = ThreadLocalRandom.current();
        BigInteger subP = shamir.getP()
                .subtract(BigInteger.ONE);
        List<BigInteger> gcdList;
        do {
            ca = new BigInteger(subP.bitLength() - 1, random);
            gcdList = Crypto.gcd(ca, subP);
        } while (!gcdList.get(0).equals(BigInteger.ONE));
        da = gcdList.get(1).compareTo(BigInteger.ZERO) < 0
                ? gcdList.get(1).add(subP) : gcdList.get(1);
    }

    public BigInteger sendToUser(BigInteger message) {
        return message.modPow(ca, system.getP());
    }

    public BigInteger receiveToUser(BigInteger message) {
        return message.modPow(ca, system.getP());
    }

    public BigInteger replyToUser(BigInteger message) {
        return message.modPow(da, system.getP());
    }

    public BigInteger decryptMessage(BigInteger message) {
        return message.modPow(da, system.getP());
    }



}
