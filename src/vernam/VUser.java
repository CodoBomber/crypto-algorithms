package vernam;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class VUser {

    private final Random random;
    public VUser() {
        random = ThreadLocalRandom.current();
    }

    public List<BigInteger> encryptMessage(BigInteger msg) {
        //2 bytes to read from file
        BigInteger k = new BigInteger(16, random);
        return Arrays.asList(msg.xor(k), k);
    }

    public BigInteger decryptMessage(BigInteger msg, BigInteger k) {
        return msg.xor(k);
    }
}
