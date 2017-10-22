package signature.el_gamal;

import crypto.Crypto;
import signature.FileSignature;

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

public class ElGamalSignature implements FileSignature {

    private final BigInteger p;
    private final BigInteger g;
    private final BigInteger privateKey;
    private final String baseFile;
    private final Random random = ThreadLocalRandom.current();
    private final BigInteger publicKey;
    private String signedFile = "src/signature/el_gamal/signature";

    public ElGamalSignature(String baseFile) {
        this.baseFile = baseFile;
        List<BigInteger> PG = Crypto.generatePG();
        p = PG.get(0);
        g = PG.get(1);
        this.privateKey = new BigInteger(8, random);
        this.publicKey = getG().modPow(privateKey, getP());
        System.out.println("G:" + getG() + " P:" + getP());
        System.out.println("pk:" + publicKey);
    }

    /**
     * 1 < h[i] < P
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
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
        BigInteger k, r, u, subP = p.subtract(ONE);
        List<BigInteger> gcdList;
        do {
            k = new BigInteger(8, random);
            gcdList = Crypto.gcd(k, subP);
        } while(!gcdList.get(0).equals(ONE));
        r = getG().modPow(k, getP());
        char current;
        while (true) {
            try {
                current = shaBytesBuffer.getChar();
                //u=h-xr%(p-1)
                u = BigInteger.valueOf(current).subtract(privateKey.multiply(r)).mod(subP);
                //s=k^-1*u%(p-1)
                rsByteBuffer.putChar((char) r.intValueExact());
                rsByteBuffer.putChar((char) Crypto.gcd(subP, k).get(2).multiply(u).mod(subP)
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
        Files.write(signed, rsByteBuffer.array(), StandardOpenOption.APPEND);
        return m.length;
    }

    @Override
    public boolean isAccessVerified(int messageRange) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(Paths.get(signedFile));
        System.out.println(Arrays.toString(bytes));
        byte[] m = Arrays.copyOfRange(bytes, 0, messageRange);
        ByteBuffer rsByteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(bytes, messageRange, bytes.length));
        rsByteBuffer.position(0);
        byte[] shaBytes = sha256.digest(m);
        ByteBuffer shaByteBuffer = ByteBuffer.wrap(shaBytes);
        ByteBuffer outByteBuffer1 = ByteBuffer.allocate(shaBytes.length);
        ByteBuffer outByteBuffer2 = ByteBuffer.allocate(shaBytes.length);
        outByteBuffer1.position(0);
        outByteBuffer2.position(0);
        BigInteger r, s;
        char h;
        while (true) {
            try {
                h = shaByteBuffer.getChar();
                r = BigInteger.valueOf(rsByteBuffer.getChar());
                s = BigInteger.valueOf(rsByteBuffer.getChar());
                outByteBuffer1.putChar(
                        (char) publicKey.pow(r.intValue())
                                .multiply(r.pow(s.intValue()))
                                .intValueExact()
                );
                outByteBuffer2.putChar(
                        (char) g.pow(h)
                                .mod(p)
                                .intValueExact()
                );
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        System.out.println("sha bytes: " + Arrays.toString(shaBytes));
        return Arrays.equals(outByteBuffer1.array(), outByteBuffer2.array());
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }
}
