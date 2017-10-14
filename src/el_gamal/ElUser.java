package el_gamal;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class ElUser {

    private BigInteger privateKey,
            publicKey;
    private ElSystem system;
    private Random random = ThreadLocalRandom.current();
    private final String baseFile;

    public ElUser(ElSystem system, String baseFileName) {
        this.baseFile = baseFileName;
        this.privateKey = new BigInteger(8, random);
        this.publicKey = system.getG()
                .modPow(privateKey, system.getP());
        this.system = system;
    }

    public void sendMessage(ElUser opponent) throws IOException {
        Path encrypt = Paths.get("El_gamal_encrypt");
        Files.deleteIfExists(encrypt);
        Files.createFile(encrypt);
        byte[] bytes = Files.readAllBytes(Paths.get(baseFile));
        BigIntegerPair re;
        for (byte b : bytes) {
            re = encryptMessage(BigInteger.valueOf(b), opponent.getPublicKey());
            Files.write(encrypt, re.toByteArray(), StandardOpenOption.APPEND);
        }
        opponent.receiveMessage(encrypt);
    }

    public void receiveMessage(Path encodedMessage) throws IOException {
        byte[] bytes = Files.readAllBytes(encodedMessage);
        if (bytes.length % 4 != 0) {
            throw new ArrayIndexOutOfBoundsException("В файле нехватает байтов, чтобы произвести десериализацию");
        }
        Path decript = Paths.get("src/el_gamal/decripted_file");
        Files.deleteIfExists(decript);
        Files.createFile(decript);
        for (int i = 0; i + 4 != bytes.length; i += 4) {
            Files.write(
                    decript,
                    decryptMessage(new BigIntegerPair(Arrays.copyOfRange(bytes, i, i + 4))).toByteArray(),
                    StandardOpenOption.APPEND
            );
        }
    }

    /*public byte[] encryptMessage(byte msg, BigInteger userPK) {
        BigInteger k = new BigInteger(8, random);
        ArrayList<BigInteger> re = new ArrayList<BigInteger>() {{
            add(system.getG().modPow(k, system.getP()));
            add(
                    userPK.modPow(k, system.getP())
                            .multiply(BigInteger.valueOf(msg))
                            .mod(system.getP())
            );
        }};
        byte[] bytes = new byte[4], partOne = re.get(0).toByteArray(), partTwo = re.get(1).toByteArray();
        bytes[0] = partOne[0];
        bytes[0] = partOne[1];
        bytes[0] = partTwo[0];
        bytes[0] = partTwo[1];
        return bytes;
    }*/

    /**
     *
     * @param msg
     * @param userPK
     * @return <r, e>, where r - g^(k)%P and p with g ~ 16bit => r = 2byte, r + e = 4byte
     */
    public BigIntegerPair encryptMessage(BigInteger msg, BigInteger userPK) {
        BigInteger k = new BigInteger(8, random);
        return new BigIntegerPair(
                system.getG().modPow(k, system.getP()),
                    userPK.modPow(k, system.getP())
                            .multiply(msg)
                            .mod(system.getP())
        );

    }

    public BigInteger decryptMessage(BigIntegerPair re) {
        return re.second
                .multiply(
                        re.first
                                .modPow(
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

        public BigIntegerPair(BigInteger first, BigInteger second) {
            this.first = first;
            this.second = second;
        }

        byte[] toByteArray() throws IOException {
            return concatenateArrays(first.toByteArray(), second.toByteArray());
        }

        public BigIntegerPair(byte[] bytes) {
            if (bytes.length < 4) {
                throw new ArrayIndexOutOfBoundsException("Передано недостаточное кол-во байтов");
            }
            byte[] first = new byte[] {bytes[0], bytes[1]};
            byte[] second = new byte[] {bytes[2], bytes[3]};
            this.first = new BigInteger(first);
            this.second = new BigInteger(second);
        }

        private byte[] concatenateArrays(byte[] a, byte[] b) {
            int aLen = a.length;
            int bLen = b.length;
            byte[] c = new byte[aLen+bLen];
            System.arraycopy(a, 0, c, 0, aLen);
            System.arraycopy(b, 0, c, aLen, bLen);
            return c;
        }
    }
}
