package el_gamal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;

public class ElUser {

    private BigInteger privateKey,
            publicKey;
    private ElSystem system;
    private Random random = ThreadLocalRandom.current();

    public ElUser(ElSystem system) {
        this.privateKey = new BigInteger(30, random);
        this.publicKey = system.getG()
                .modPow(privateKey, system.getP());
        this.system = system;
    }

    public List<BigInteger> encryptMessage(BigInteger msg, BigInteger userPK) {
        BigInteger k = new BigInteger(38, random);
        return new ArrayList<BigInteger>() {{
            add(system.getG().modPow(k, system.getP()));
            add(
                    userPK.modPow(k, system.getP())
                            .multiply(msg)
                            .mod(system.getP())
            );
        }};
    }

    public BigInteger decryptMessage(List<BigInteger> re) {
        return re.get(1)
                .multiply(
                        re.get(0).modPow(system.getP()
                                        .subtract(ONE)
                                        .subtract(privateKey),
                                system.getP()))
                .mod(system.getP());
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }
}
