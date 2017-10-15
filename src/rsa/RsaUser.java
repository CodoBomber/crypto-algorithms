package rsa;

import crypto.Crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class RsaUser {

    private final BigInteger N;
    private final String baseFilename;
    private BigInteger c, d;

    public RsaUser(String baseFilename) {
        this.baseFilename = baseFilename;
        Random random = ThreadLocalRandom.current();
        BigInteger P = BigInteger.probablePrime(15, random);
        BigInteger Q = BigInteger.probablePrime(17, random);
        N = P.multiply(Q);
        BigInteger f = P.subtract(ONE).multiply(Q.subtract(ONE));
        List<BigInteger> gcdList;
        do {
            c = new BigInteger(16, random);
            gcdList = Crypto.gcd(c, f);
        } while(!gcdList.get(0).equals(ONE));
        d = gcdList.get(1).compareTo(ZERO) > 0 ? gcdList.get(1)
                : P.subtract(ONE)
                .multiply(Q.subtract(ONE))
                .add(gcdList.get(1));
    }

    public void sendMessageTo(RsaUser opponent) throws IOException {
        Path encrypt = Paths.get("src/rsa/encrypt");
        Files.deleteIfExists(encrypt);
        Files.createFile(encrypt);
        byte[] bytes = Files.readAllBytes(Paths.get(baseFilename));
        byte[] encodedBytes;
        for (byte b : bytes) {
            encodedBytes = encryptMessage(BigInteger.valueOf(b), opponent.getNPublicKey(), opponent.getDPublicKey());
//            serializer.serialize(re);
            System.out.println("Encoded bytes: " + Arrays.toString(encodedBytes));
            Files.write(encrypt, encodedBytes, StandardOpenOption.APPEND);
        }
        opponent.receiveMessage(encrypt);
    }

    public void receiveMessage(Path encrypt) throws IOException {
        byte[] bytes = Files.readAllBytes(encrypt);
        if (bytes.length % 4 != 0) {
            throw new ArrayIndexOutOfBoundsException("В файле нехватает байтов, чтобы произвести десериализацию");
        }
        Path decript = Paths.get("src/rsa/decripted_file");
        Files.deleteIfExists(decript);
        Files.createFile(decript);

        for (int i = 0; i != bytes.length; i += 4) {
            Files.write(
                    decript,
                    decryptMessage(new BigInteger(Arrays.copyOfRange(bytes, i, i + 4))).toByteArray(),
                    StandardOpenOption.APPEND
            );
        }
    }

    /**
     *
     * @param msg byte
     * @param pubKey1 user2 n key
     * @param pubKey2 user2 d key
     * @return encoded byte
     */
    public byte[] encryptMessage(BigInteger msg, BigInteger pubKey1, BigInteger pubKey2) {
        byte[] encoded = msg.modPow(pubKey1, pubKey2).toByteArray();
        //guarantee for 4 byte array size
        /*int i = 4 - encoded.length;
        if (i == 0) {
            return encoded;
        }
        byte[] additional = new byte[i];
        return concatenateArrays(encoded, additional);*/
        return encoded;
    }

    private byte[] concatenateArrays(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public BigInteger decryptMessage(BigInteger msg) {
        return msg.modPow(this.c, this.N);
    }

    public BigInteger getNPublicKey() {
        return N;
    }

    public BigInteger getDPublicKey() {
        return d;
    }

}
