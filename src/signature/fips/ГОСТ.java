package signature.fips;

import signature.FileSignature;

import java.io.IOException;
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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class ГОСТ implements FileSignature {

    private final String baseFile;
    private final Random random = ThreadLocalRandom.current();
    private BigInteger Q, P, A;
    private BigInteger privateKey;
    private BigInteger publicKey;

    public ГОСТ(String baseFile) {
        this.baseFile = baseFile;
        this.Q = BigInteger.probablePrime(15, random);
        BigInteger b;
        //p = bq + 1 => b = (p-1)/q, where b is integer
        do {
            //15 + 16 = 31 bit
            b = new BigInteger(16, random);
            this.P = b.multiply(Q).add(ONE);
        } while(privateKey.isProbablePrime(25) && P.testBit(31));
        do {
            BigInteger g = new BigInteger(8, random);
            this.A = g.modPow(b, P);
        } while(A.equals(ONE));
        this.privateKey = new BigInteger(8, random);
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
        for (i = 1; true; i++) {
            try {
                current = shaBytesBuffer.getChar();
                k = new BigInteger(i % 15, random);
                s = k.multiply(BigInteger.valueOf(current))
                        .add(privateKey.multiply(r))
                        .mod(Q);
                if (s.equals(ZERO) || r.equals(ZERO)) {
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
