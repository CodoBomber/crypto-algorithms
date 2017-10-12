package diffie_hellman;

import java.math.BigInteger;

public interface
DHUser {

    void onConnect(String sourceUsername, BigInteger connectionKey) throws IllegalAccessException;

    BigInteger getPublicKey();

    /**
     * Метод, для вычисления значения ключа для пересылки сообщения другому пользователю
     * Допустим, абонент A решил организовать сеанс связи с B , при этом обоим абонентам доступна открытая информация.
     * Абонент A сообщает B по открытому каналу, что он хочет передать ему сообщение. Затем абонент A вычисляет эту величину.
     * @param privateKey Секретный ключ пользователя А
     * @param publicKey Публичный ключ пользователя Б
     * @return Значение для соединения с пользователем Б Z(ab)
     */
    BigInteger getConnectionValue(BigInteger privateKey, BigInteger publicKey);

    String getUsername();
}
