package diffie_hellman;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.Random;

public class User implements DHUser {

    private final System system;
    private final BigInteger privateKey, publicKey;
    private final String username;

    User(System system, String username) {
        this.system = system;
        this.username = username;
        this.privateKey = BigInteger.probablePrime(20, new Random());
        this.publicKey = Crypto.modPow(system.getGValue(), privateKey, system.getPValue());
        java.lang.System.out.println("public & private keys for user: " + username + " == " + publicKey + " & " + privateKey);
    }

    public void attemptConnectTo(String userName) throws IllegalAccessException {
        system.connectToUser(this.username, userName, getConnectionValue(privateKey, system.getUserPublicKey(userName)));
    }

    public BigInteger getPublicKey() {
        return this.publicKey;
    }

    @Override
    public BigInteger getConnectionValue(BigInteger privateKey, BigInteger publicKey) {
//        return publicKey.modPow(privateKey, system.getPValue());
        java.lang.System.out.println("Connection value for " + username + " == " +
                Crypto.modPow(publicKey, privateKey, system.getPValue()));
        return Crypto.modPow(publicKey, privateKey, system.getPValue());
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void onConnect(String sourceUsername, BigInteger connectionKey) throws IllegalAccessException {
        if (!getConnectionValue(privateKey, system.getUserPublicKey(sourceUsername)).equals(connectionKey)) {
            throw new IllegalAccessException("Сеанс связи между пользователями: " + sourceUsername + " -> "
            + this.username + " невозможен по причине несовпадения ключей соединения!!");
        }
        java.lang.System.out.println("万歳！！！　Меня зовут "+ this.username + " и со мной связался " + sourceUsername);
    }
}
