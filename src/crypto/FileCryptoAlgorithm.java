package crypto;

import java.math.BigInteger;

public interface FileCryptoAlgorithm {

    BigInteger encryptMessage(BigInteger... bigIntegers);

    BigInteger decryptMessage(BigInteger... bigIntegers);
}
