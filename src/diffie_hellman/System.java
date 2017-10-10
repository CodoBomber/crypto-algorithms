package diffie_hellman;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.List;

public class System implements DHSystem {

    private BigInteger p, g;

    public System() {
        List<BigInteger> PG = Crypto.generatePG();
        this.p = PG.get(0);
        this.g = PG.get(1);
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
