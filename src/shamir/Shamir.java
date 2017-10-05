package shamir;

import java.math.BigInteger;
import java.util.Random;

public class Shamir {

    private final BigInteger p;

    public Shamir() {
        this.p = BigInteger.probablePrime(30, new Random());

    }

    public BigInteger getP() {
        return p;
    }
}
