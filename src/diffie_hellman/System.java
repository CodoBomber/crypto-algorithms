package diffie_hellman;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.Random;

public class System implements DHSystem {

    private final BigInteger p, g;

    public System() {
        BigInteger q = BigInteger.probablePrime(pBitLength, new Random());
        this.p = q.multiply(BigInteger.valueOf(2))
                .add(BigInteger.ONE);
        BigInteger b = BigInteger.ONE;
        for (; b.equals(BigInteger.ONE); b = Crypto.binaryPow(BigInteger.probablePrime(gBitLength, new Random()), q, p));
        this.g = b;
        java.lang.System.out.println("Система Даффи и Хеллмана благополучно запущена со значениями: p=" +
            p + " and g=" + g);
    }


    @Override
    public User registerNewUser() {
        //名前を作る
        //マップに入れる
        //できあがったユーザーを返す
        String username = USERNAME_PREFIX + (users.size() + 1);
        User user =  new User(this, username);
        users.put(username, user);
        return user;
    }

    @Override
    public BigInteger getUserPublicKey(String username) {
        return users.get(username).getPublicKey();
    }

    @Override
    public void connectToUser(String sourceUsername, String destinationUsername, BigInteger connectionKey)
            throws IllegalAccessException {
        users.get(destinationUsername).onConnect(sourceUsername, connectionKey);
    }

    @Override
    public BigInteger getPValue() {
        return p;
    }

    @Override
    public BigInteger getGValue() {
        return g;
    }
}
