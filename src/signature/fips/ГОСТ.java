package signature.fips;

import crypto.Crypto;
import crypto.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class ГОСТ {

    private final String baseFile;
    private final Random random = ThreadLocalRandom.current();
    private BigInteger Q, P, A;
    private BigInteger privateKey;
    private BigInteger publicKey;
    private String signedFile = "src/signature/fips/signature";

    public ГОСТ(String baseFile) {
        this.baseFile = baseFile;
        this.privateKey = new BigInteger(64, random);
        this.Q = BigInteger.probablePrime(256, random);
        BigInteger b;
        //p = bq + 1 => b = (p-1)/q, where b is integer
        do {
            //1024 + 768 = 1024 bit
            b = new BigInteger(768, random);
            this.P = b.multiply(Q).add(ONE);
        } while(privateKey.isProbablePrime(25) && P.testBit(1024));
        do {
            BigInteger g = new BigInteger(64, random);
            this.A = g.modPow(b, P);
        } while(A.equals(ONE));
        this.publicKey = A.modPow(privateKey, P);
    }

    public Pair<Integer, Integer> signFile() throws NoSuchAlgorithmException, IOException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        Path baseFile = Paths.get(this.baseFile);
        byte[] m = Files.readAllBytes(baseFile);
        System.out.println(Arrays.toString(m));
        byte[] shaBytes = sha256.digest(m);
        BigInteger hash = new BigInteger(shaBytes);
//        ByteBuffer shaBytesBuffer = ByteBuffer.wrap(sha256.digest(m));
//        shaBytesBuffer.position(0);
        ByteBuffer rsByteBuffer;// = ByteBuffer.allocate(shaBytes.length);
        BigInteger k, r, s;/*
        int i = 50;
        // TODO: 17/10/24 Переделать циклы так, чтобы GOTO был общий
        do {
            k = new BigInteger(i, random);
            r = A.modPow(k, P).mod(Q);
            i = i != 254 ? i + 1 : 50;
        } while (r.equals(ZERO));*/
//        rsByteBuffer.putInt(r.intValueExact());
        char current;
        for (int i = 64; true; i++) {
//            try {
//                current = shaBytesBuffer.getChar();
                k = new BigInteger(i % 127, random);
                r = A.modPow(k, P).mod(Q);
                s = k.multiply(hash)
                        .add(privateKey.multiply(r))
                        .mod(Q);
                if (!s.equals(ZERO) || !r.equals(ZERO)) {
                    break;
                }
            /*} catch (BufferUnderflowException e) {
                break;
            }*/
        }
        byte[] rBytes = r.toByteArray(), sBytes = s.toByteArray();
        System.out.println("before r bytes: "+ Arrays.toString(rBytes));
        System.out.println("before s bytes: "+ Arrays.toString(sBytes));
        rsByteBuffer = ByteBuffer.allocate(rBytes.length + sBytes.length);
        rsByteBuffer.put(rBytes);
        rsByteBuffer.put(sBytes);
        Path signed = Paths.get(signedFile);
        Files.deleteIfExists(signed);
        Files.copy(baseFile, signed, StandardCopyOption.REPLACE_EXISTING);
        Files.write(signed, rsByteBuffer.array(), StandardOpenOption.APPEND);
        return new Pair<> (m.length, rBytes.length);
    }

    /**
     *
     * @param mrRange m byte length and r byte length
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public boolean isAccessVerified(Pair<Integer, Integer> mrRange) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(Paths.get(signedFile));
        System.out.println(Arrays.toString(bytes));
        byte[] m = Arrays.copyOfRange(bytes, 0, mrRange.getFirst());
        //from m until m + r = range of r,
        BigInteger r = new BigInteger(
                Arrays.copyOfRange(
                        bytes,
                        mrRange.getFirst(),
                        mrRange.getFirst() + mrRange.getSecond()
                )
        );
        BigInteger s = new BigInteger(
                Arrays.copyOfRange(
                        bytes,
                        mrRange.getFirst() + mrRange.getSecond(),
                        bytes.length
                )
        );
        System.out.println("after r: " + Arrays.toString(r.toByteArray()));
        System.out.println("after s: " + Arrays.toString(s.toByteArray()));
        byte[] shaBytes = sha256.digest(m);
        BigInteger h = new BigInteger(shaBytes);
        assertBounds(r, Q);
        assertBounds(s, Q);

        BigInteger u1 = s.multiply(Crypto.gcd(Q, h).get(2)).mod(Q);
        BigInteger u2 = r.negate().multiply(Crypto.gcd(Q, h).get(2)).mod(Q);
        //a^u1 * y^u2 (% P) % Q
        BigInteger v = A.modPow(u1, P).multiply(publicKey.modPow(u2, P)).mod(Q);
        assertEquality(v, r);
        return true;
    }

    private void assertEquality(BigInteger v, BigInteger r) {
        byte[] vBytes = v.toByteArray(), rBytes = r.toByteArray();
        System.out.println("v: " + Arrays.toString(vBytes));
        System.out.println("r: " + Arrays.toString(rBytes));
        if (!Arrays.equals(vBytes, rBytes)) {
            throw new AssertionError("v и r не равны!");
        }
    }

    private void assertBounds(BigInteger i, BigInteger q) {
        if (i.compareTo(ZERO) < 0 && i.compareTo(q) > 0) {
            throw new AssertionError("Ошибка границ 0 < i < q, i = " + i.bitLength());
        }
    }
}
