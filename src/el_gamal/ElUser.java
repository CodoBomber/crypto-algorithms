package el_gamal;

import crypto.FileEncryptor;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;

public class ElUser {

    private BigInteger privateKey,
            publicKey;
    private ElSystem system;
    private Random random = ThreadLocalRandom.current();
    private final String baseFile;

    public ElUser(ElSystem system, String baseFileName) {
        this.baseFile = baseFileName;
        this.privateKey = new BigInteger(8, random);
        this.publicKey = system.getG().modPow(privateKey, system.getP());
        this.system = system;
        System.out.println("G:" + system.getG() + " P:" + system.getP());
        System.out.println("pk:" + publicKey);
    }

    public void sendMessageTo(ElUser opponent) throws IOException {
        ByteBuffer byteBuffer = FileEncryptor.bufferizeFile(baseFile);
        byteBuffer.position(0);
//        System.out.println(Arrays.toString(byteBuffer.array()));
        ByteBuffer encodedBuffer = ByteBuffer.allocate(byteBuffer.limit() * Character.BYTES * 2);
        char current;
        BigIntegerPair re;
        int[] rePair;
        int capacity;
//        int[] chars = new int[byteBuffer.limit()];
        for (capacity = 0; true; capacity++) {
            try {
                current = byteBuffer.getChar();
//                chars[capacity] = currentByte;
//                System.out.println(currentByte);//BigInteger.valueOf(currentByte < 0 ? currentByte + 256 : currentByte));
                re = encryptMessage(BigInteger.valueOf(current /*< 0 ? currentByte + 256 : currentByte*/), opponent.getPublicKey());
                rePair = re.toIntArray();
                encodedBuffer.putInt(rePair[0]);
                encodedBuffer.putInt(rePair[1]);
            } catch (BufferUnderflowException e) {
                break;
            }
        }
//        System.out.println(Arrays.toString(chars));
        Path encrypt = Paths.get("src/el_gamal_encrypted");
        Files.deleteIfExists(encrypt);
        Files.createFile(encrypt);
        Files.write(encrypt, encodedBuffer.array(), StandardOpenOption.WRITE);
        opponent.receiveMessage(capacity);
    }

    private void receiveMessage(int decodedCapacity) throws IOException {
        ByteBuffer encodedBuffer = FileEncryptor.bufferizeFile("src/el_gamal_encrypted");
        encodedBuffer.position(0);
        Path decript = Paths.get("src/el_gamal/decripted_file.png");
        Files.deleteIfExists(decript);
        Files.createFile(decript);
        ByteBuffer decodedBuffer = ByteBuffer.allocate(decodedCapacity * 2);
        decodedBuffer.position(0);
        char sign = 0;
//        int[] chars = new int[decodedCapacity];
        for (int i = 0; true; i++) {
            try {
               sign = (char) decryptMessage(
                        new BigIntegerPair(
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

    /**
     * あるメッセージのエンコーディングを行うメソッド
     * @param msg
     * @param userPK
     * @return <r, e>
     * @throws IllegalStateException 限定はmsgがPを越えないこと
     */
    private BigIntegerPair encryptMessage(BigInteger msg, BigInteger userPK) throws IllegalStateException {
        if (msg.compareTo(system.getP()) > 0) {
            throw new IllegalStateException("В метод было передано сообщение: " + msg + " которое больше чем P:" + system.getP());
        }
        BigInteger k, r, e;
            k = new BigInteger(8, random).add(BigInteger.valueOf(2));
            r = system.getG().modPow(k, system.getP());
            e = userPK.modPow(k, system.getP())
                    .multiply(msg)
                    .mod(system.getP());
//            System.out.println("K:"+ k + " r:" + r + " e:"+e);
        return new BigIntegerPair(r, e);
    }

    /**
     * @param re
     * @return
     */
    private BigInteger decryptMessage(BigIntegerPair re) {
        return re.second
                .multiply(
                        re.first.modPow(
                                system.getP()
                                        .subtract(ONE)
                                        .subtract(privateKey),
                                system.getP()
                        )
                )
                .mod(system.getP());
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    private static class BigIntegerPair implements Serializable {
        private final BigInteger first;
        private final BigInteger second;

        BigIntegerPair(BigInteger first, BigInteger second) {
            this.first = first;
            this.second = second;
        }

        int[] toIntArray() {
            return new int[] {this.first.intValueExact(), this.second.intValueExact()};
        }
    }
}
