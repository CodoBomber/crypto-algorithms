package el_gamal;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;

public class ElUser {

    private BigInteger privateKey,
            publicKey;
    private ElSystem system;
    private Random random = ThreadLocalRandom.current();

    public ElUser(ElSystem system) {
        this.privateKey = new BigInteger(8, random);
        this.publicKey = system.getG()
                .modPow(privateKey, system.getP());
        this.system = system;
    }

    public void encryptFile(String baseFile, BigInteger userPK) throws IOException {
        File file = new File(baseFile);
        File file2 = new File("El_gamal_encrypt");
        byte[] bytes = Files.readAllBytes(Paths.get(baseFile));
        for (byte b : bytes) {
            encryptMessage(b, userPK);
        }
    }

    public byte[] encryptMessage(byte msg, BigInteger userPK) {
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
    }

    public BigInteger decryptMessage(List<BigInteger> re) {
        return re.get(1)
                .multiply(
                        re.get(0).modPow(system.getP()
                                        .subtract(ONE)
                                        .subtract(privateKey),
                                system.getP()))
                .mod(system.getP());
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }
}
