package signature;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface FileSignature {

    int signFile() throws NoSuchAlgorithmException, IOException;
    boolean isAccessVerified(int messageRange) throws NoSuchAlgorithmException, IOException;

}
