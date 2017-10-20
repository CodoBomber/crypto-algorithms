package crypto;


import java.io.IOException;

public interface FileCryptoAlgorithm<T> {

    void sendMessageTo(T opponent) throws IOException;

    void receiveMessage(int decodedCapacity) throws IOException;
}
