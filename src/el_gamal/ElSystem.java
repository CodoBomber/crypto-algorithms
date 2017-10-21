package el_gamal;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.List;

public class ElSystem {

    private BigInteger p, g;

    public ElSystem() {
        List<BigInteger> PG = Crypto.generatePG();
        p = PG.get(0);
        g = PG.get(1);
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }
}
