package rsa;

import crypto.Crypto;
import crypto.FileCryptoAlgorithm;
import crypto.FileEncryptor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
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

public class RsaUser implements FileCryptoAlgorithm<RsaUser> {

    private final BigInteger N;
    private final String baseFilename;
    private BigInteger c, d;

    public RsaUser(String baseFilename) {
        this.baseFilename = baseFilename;
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
    }

    @Override
    public void sendMessageTo(RsaUser opponent) throws IOException {
        ByteBuffer byteBuffer = FileEncryptor.bufferizeFile(baseFilename);
        byteBuffer.position(0);
//        System.out.println(Arrays.toString(byteBuffer.array()));
        ByteBuffer encodedBuffer = ByteBuffer.allocate(byteBuffer.limit() * Character.BYTES);
        char current;
        int encrypted;
        int capacity;
        int[] chars = new int[byteBuffer.limit()];
        for (capacity = 0; true; capacity++) {
            try {
                current = byteBuffer.getChar();
                chars[capacity] = current;
//                System.out.println(currentByte);//BigInteger.valueOf(currentByte < 0 ? currentByte + 256 : currentByte));
                encrypted = encryptMessage(
                        BigInteger.valueOf(current),
                        opponent.getNPublicKey(),
                        opponent.getDPublicKey()
                ).intValueExact();
                encodedBuffer.putInt(encrypted);
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        System.out.println(Arrays.toString(chars));
        Path encrypt = Paths.get("src/rsa/encrypt");
        Files.deleteIfExists(encrypt);
        Files.createFile(encrypt);
        Files.write(encrypt, encodedBuffer.array(), StandardOpenOption.WRITE);
        opponent.receiveMessage(capacity);
    }

    @Override
    public void receiveMessage(int decodedCapacity) throws IOException {
        ByteBuffer encodedBuffer = FileEncryptor.bufferizeFile("src/rsa/encrypt");
        encodedBuffer.position(0);
        Path decript = Paths.get("src/rsa/decripted_file");
        Files.deleteIfExists(decript);
        Files.createFile(decript);
        ByteBuffer decodedBuffer = ByteBuffer.allocate(decodedCapacity * 2);
        decodedBuffer.position(0);
        char sign = 0;
        int[] chars = new int[decodedCapacity];
        for (int i = 0; true; i++) {
            try {
                sign = (char) decryptMessage(
                                BigInteger.valueOf(encodedBuffer.getInt())
                        ).intValueExact();
               chars[i] = sign;
                decodedBuffer.putChar(sign);// < 0 ? sign + 256 : sign;
//                System.out.print("r= " + (int)encodedBuffer.getChar() + " ");
//                System.out.println("e= " + (int)encodedBuffer.getChar());
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        System.out.println(Arrays.toString(chars));
        System.out.println(Arrays.toString(decodedBuffer.array()));
        Files.write(decript, decodedBuffer.array(), StandardOpenOption.APPEND);
    }

    /**
     *
     * @param msg byte
     * @param pubKey1 user2 n key
     * @param pubKey2 user2 d key
     * @return encoded byte
     */
    private BigInteger encryptMessage(BigInteger msg, BigInteger pubKey1, BigInteger pubKey2) {
        return msg.modPow(pubKey1, pubKey2);
    }

    private BigInteger decryptMessage(BigInteger msg) {
        return msg.modPow(this.c, this.N);
    }

    public BigInteger getNPublicKey() {
        return N;
    }

    public BigInteger getDPublicKey() {
        return d;
    }

}
