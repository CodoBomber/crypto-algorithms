package rsa;

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

public class RsaUser extends RSACore implements FileCryptoAlgorithm<RsaUser> {

    private final String baseFilename;

    public RsaUser(String baseFilename) {
        this.baseFilename = baseFilename;
    }

    @Override
    public void sendMessageTo(RsaUser opponent) throws IOException {
        ByteBuffer byteBuffer = FileEncryptor.bufferizeFile(baseFilename);
        byteBuffer.position(0);
        System.out.println("1:" + Arrays.toString(byteBuffer.array()));
        ByteBuffer encodedBuffer = ByteBuffer.allocate(byteBuffer.limit() * Character.BYTES);
        char current;
        int encrypted;
        int capacity;
//        int[] chars = new int[byteBuffer.limit()];
        for (capacity = 0; true; capacity++) {
            try {
                current = byteBuffer.getChar();
//                chars[capacity] = current;
//                System.out.println(currentByte);//BigInteger.valueOf(currentByte < 0 ? currentByte + 256 : currentByte));
                encrypted = encryptMessage(
                        BigInteger.valueOf(current),
                        opponent.getDPublicKey(),
                        opponent.getNPublicKey()
                        ).intValueExact();
                encodedBuffer.putInt(encrypted);
            } catch (BufferUnderflowException e) {
                break;
            }
        }
//        System.out.println("2:" + Arrays.toString(chars));
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
        Path decript = Paths.get("src/rsa/decripted_file.png");
        Files.deleteIfExists(decript);
        Files.createFile(decript);
        ByteBuffer decodedBuffer = ByteBuffer.allocate(decodedCapacity * 2);
        decodedBuffer.position(0);
        char sign;
//        int[] chars = new int[decodedCapacity];
        for (int i = 0; true; i++) {
            try {
                sign = (char) decryptMessage(
                                BigInteger.valueOf(encodedBuffer.getInt())
                        ).intValueExact();
//               chars[i] = sign;
                decodedBuffer.putChar(sign);// < 0 ? sign + 256 : sign;
//                System.out.print("r= " + (int)encodedBuffer.getChar() + " ");
//                System.out.println("e= " + (int)encodedBuffer.getChar());
            } catch (BufferUnderflowException e) {
                break;
            }
        }
//        System.out.println("3:" + Arrays.toString(chars));
//        System.out.println("4:" + Arrays.toString(decodedBuffer.array()));
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
        return msg.modPow(super.c, super.N);
    }

}
