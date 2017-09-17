package diffie_hellman;

import java.math.BigInteger;
import java.util.HashMap;

public interface DHSystem {

    String USERNAME_PREFIX = "user.No:";
    int pBitLength = 50, gBitLength = 15;
    HashMap<String, DHUser> users = new HashMap<>();

    /**
     * Регистрирует пользователя сети и выдаёт ему приватный ключ
     * @return Возвращает созданного юзера {@link User}
     */
    User registerNewUser();


    BigInteger getUserPublicKey(String username);

    void connectToUser(String srcUsername, String dstUsername, BigInteger connectionKey) throws IllegalAccessException;

    BigInteger getPValue();

    BigInteger getGValue();

}
