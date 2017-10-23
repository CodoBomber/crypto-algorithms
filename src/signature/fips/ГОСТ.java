package signature.fips;

import signature.FileSignature;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ZERO;

public class ГОСТ implements FileSignature {

    private final String baseFile;
    private final Random random = ThreadLocalRandom.current();
    private BigInteger Q, P, A;
    private BigInteger privateKey;
    private BigInteger publicKey;

    public ГОСТ(String baseFile) {
        this.baseFile = baseFile;
        this.Q = BigInteger.probablePrime(256, random);
        BigDecimal b;
        BigDecimal dq = new BigDecimal(Q), dp;
        //p = bq + 1 => b = (p-1)/q, where b is integer
        do {
            dp = new BigDecimal(BigInteger.probablePrime(1024, random));
            b = dp.subtract(BigDecimal.ONE).divide(dq, 2, BigDecimal.ROUND_UP);
        } while (b.stripTrailingZeros().scale() > 0);
        this.P = dp.toBigInteger();
        do {
            this.A = new BigInteger(50, random);
        } while (!A.modPow(Q, P).equals(BigInteger.ONE));
        this.privateKey = new BigInteger(100, random);
        this.publicKey = A.modPow(privateKey, P);
    }

    @Override
    public int signFile() throws NoSuchAlgorithmException, IOException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        Path baseFile = Paths.get(this.baseFile);
        byte[] m = Files.readAllBytes(baseFile);
        System.out.println(Arrays.toString(m));
        ByteBuffer shaBytesBuffer = ByteBuffer.wrap(sha256.digest(m));
        shaBytesBuffer.position(0);
        ByteBuffer rsByteBuffer = ByteBuffer.allocate(shaBytesBuffer.limit() * Character.BYTES * 2);
        rsByteBuffer.position(0);
        BigInteger k, r, s;
        int i = 50;
        // TODO: 17/10/24 Переделать циклы так, чтобы GOTO был общий
        do {
            k = new BigInteger(i, random);
            r = A.modPow(k, P).mod(Q);
            i = i != 254 ? i + 1 : 50;
        } while (r.equals(ZERO));
        rsByteBuffer.putInt(r.intValueExact());
        char current;
        for (i = 50; true; i = i != 254 ? i + 1 : 50) {
            try {
                current = shaBytesBuffer.getChar();
                k = new BigInteger(i, random);
                s = k.multiply(BigInteger.valueOf(current))
                        .add(privateKey.multiply(r))
                        .mod(Q);
                if (s.equals(ZERO)) {
                    continue;
                }
                rsByteBuffer.putInt(s.intValueExact());
            } catch (BufferUnderflowException e) {
                break;
            }
        }

        return 0;
    }

    @Override
    public boolean isAccessVerified(int messageRange) throws NoSuchAlgorithmException, IOException {
        return false;
    }
}
