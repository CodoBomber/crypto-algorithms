package vernam;

import crypto.FileCryptoAlgorithm;
import crypto.FileEncryptor;
import crypto.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class VUser implements FileCryptoAlgorithm<VUser> {

    private final Random random;
    private final String baseFile;

    public VUser(String baseFile) {
        this.baseFile = baseFile;
        random = ThreadLocalRandom.current();
    }

    private Pair<BigInteger, BigInteger> encryptMessage(BigInteger msg) {
        BigInteger k = new BigInteger(16, random);
        return new Pair<>(msg.xor(k), k);
    }

    private BigInteger decryptMessage(Pair<BigInteger, BigInteger> nk) {
        return nk.getFirst().xor(nk.getSecond());
    }

    @Override
    public void sendMessageTo(VUser opponent) throws IOException {
        ByteBuffer byteBuffer = FileEncryptor.bufferizeFile(baseFile);
        byteBuffer.position(0);
//        System.out.println(Arrays.toString(byteBuffer.array()));
        ByteBuffer encodedBuffer = ByteBuffer.allocate(byteBuffer.limit() * Character.BYTES * 2);
        char current;
        Pair<BigInteger, BigInteger> nk;
        Pair<Integer, Integer> nkPair;
        int capacity;
//        int[] chars = new int[byteBuffer.limit()];
        for (capacity = 0; true; capacity++) {
            try {
                current = byteBuffer.getChar();
//                chars[capacity] = currentByte;
//                System.out.println(currentByte);//BigInteger.valueOf(currentByte < 0 ? currentByte + 256 : currentByte));
                nk = encryptMessage(BigInteger.valueOf(current /*< 0 ? currentByte + 256 : currentByte*/));
                nkPair = new Pair<>(
                        nk.getFirst().intValueExact(),
                        nk.getSecond().intValueExact()
                );
                encodedBuffer.putInt(nkPair.getFirst());
                encodedBuffer.putInt(nkPair.getSecond());
            } catch (BufferUnderflowException e) {
                break;
            }
        }
//        System.out.println(Arrays.toString(chars));
        Path encrypt = Paths.get("src/vernam/encrypted");
        Files.deleteIfExists(encrypt);
        Files.createFile(encrypt);
        Files.write(encrypt, encodedBuffer.array(), StandardOpenOption.WRITE);
        opponent.receiveMessage(capacity);
    }

    @Override
    public void receiveMessage(int decodedCapacity) throws IOException {
        ByteBuffer encodedBuffer = FileEncryptor.bufferizeFile("src/vernam/encrypted");
        encodedBuffer.position(0);
        Path decript = Paths.get("src/vernam/decripted_file.png");
        Files.deleteIfExists(decript);
        Files.createFile(decript);
        ByteBuffer decodedBuffer = ByteBuffer.allocate(decodedCapacity * 2);
        decodedBuffer.position(0);
        char sign = 0;
//        int[] chars = new int[decodedCapacity];
        for (int i = 0; true; i++) {
            try {
                sign = (char) decryptMessage(
                        new Pair<>(
                                BigInteger.valueOf(encodedBuffer.getInt()),
                                BigInteger.valueOf(encodedBuffer.getInt())
                        )).intValueExact();
//               chars[i] = sign;
                decodedBuffer.putChar(sign);// < 0 ? sign + 256 : sign;
//                System.out.print("r= " + (int)encodedBuffer.getChar() + " ");
//                System.out.println("e= " + (int)encodedBuffer.getChar());
            } catch (BufferUnderflowException e) {
                break;
            }
        }
//        System.out.println(Arrays.toString(chars));
//        System.out.println(Arrays.toString(decodedBuffer.array()));
        Files.write(decript, decodedBuffer.array(), StandardOpenOption.APPEND);
    }
}
