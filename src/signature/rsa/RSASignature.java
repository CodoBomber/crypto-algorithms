package signature.rsa;

import crypto.Crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class RSASignature {

    private final String baseFile;
    private final BigInteger N, d;
    private BigInteger c;
    private String signedFile = "src/signature/rsa/signature";

    public RSASignature(String signatureFile) {
        this.baseFile = signatureFile;
        Random random = ThreadLocalRandom.current();
        BigInteger P = BigInteger.probablePrime(8, random);
        BigInteger Q = BigInteger.probablePrime(10, random);
        N = P.multiply(Q);
        BigInteger f = P.subtract(ONE).multiply(Q.subtract(ONE));
        List<BigInteger> gcdList;
        do {
            c = new BigInteger(8, random);
            gcdList = Crypto.gcd(c, f);
        } while(!gcdList.get(0).equals(ONE));
        d = gcdList.get(1).compareTo(ZERO) > 0 ? gcdList.get(1)
                : P.subtract(ONE)
                .multiply(Q.subtract(ONE))
                .add(gcdList.get(1));
        System.out.println("N:" + N + " P:" + P + " Q:" + Q + " c:" + c + " d:" + d);
    }

    public int signFile() throws NoSuchAlgorithmException, IOException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        Path baseFile = Paths.get(this.baseFile);
        byte[] m = Files.readAllBytes(baseFile);
        System.out.println(Arrays.toString(m));
        ByteBuffer shaBytesBuffer = ByteBuffer.wrap(sha256.digest(m));
        shaBytesBuffer.position(0);
        ByteBuffer sByteBuffer = ByteBuffer.allocate(shaBytesBuffer.limit() * Character.BYTES);
        sByteBuffer.position(0);
        char current;
        while (true) {
            try {
                current = shaBytesBuffer.getChar();
                sByteBuffer.putInt(BigInteger.valueOf(current)
                        .modPow(c, N)
                        .intValueExact());
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        System.out.println("sha bytes " + Arrays.toString(shaBytesBuffer.array()));
        Path signed = Paths.get(signedFile);
        Files.deleteIfExists(signed);
        Files.createFile(signed);
        Files.copy(baseFile, signed, StandardCopyOption.REPLACE_EXISTING);
        Files.write(signed, sByteBuffer.array(), StandardOpenOption.APPEND);
        return m.length;
    }

    public boolean isAccessVerified(int messageRange) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(Paths.get(signedFile));
        System.out.println(Arrays.toString(bytes));
        byte[] m = Arrays.copyOfRange(bytes, 0, messageRange);
        ByteBuffer sByteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(bytes, messageRange, bytes.length));
        sByteBuffer.position(0);
        byte[] shaBytes = sha256.digest(m);
        ByteBuffer wByteBuffer = ByteBuffer.allocate(shaBytes.length);
        wByteBuffer.position(0);
        int current;
        while (true) {
            try {
                current = sByteBuffer.getInt();
                wByteBuffer.putChar(
                        (char) BigInteger.valueOf(current)
                                .modPow(d, N)
                                .intValueExact()
                );
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        System.out.println("sha bytes: " + Arrays.toString(shaBytes));
        return Arrays.equals(shaBytes, wByteBuffer.array());
    }

    public BigInteger getN() {
        return N;
    }

    public BigInteger getD() {
        return d;
    }
}
