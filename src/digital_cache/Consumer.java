package digital_cache;

import crypto.Crypto;
import crypto.Pair;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Consumer {

    private final Pair<BigInteger, BigInteger> nsPair;
    private final Bank bank;
    private BigInteger r;
    private int balance = 2_000_000;
    private Bank.Cost cost;

    public Consumer(Bank bank, Bank.Cost cost) throws NoSuchAlgorithmException {
        this.bank = bank;
        this.cost = cost;
        System.out.println("Хочу купить чего-нить на сумму " + cost.getCost() + " рублей");
        System.out.println("Сейчас у меня на руках " + balance + " рублей");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        Random random = ThreadLocalRandom.current();
        BigInteger n = new BigInteger(16, random);
        do {
            r = new BigInteger(16, random);
        } while (!Crypto.gcd(r, bank.getNPublicKey()).get(0).equals(BigInteger.ONE));
        //d, c
        Pair<BigInteger, BigInteger> keyPair = bank.getKeysFor(cost.getCost());
        BigInteger hash = new BigInteger(1, messageDigest.digest(n.toByteArray()));
        BigInteger _n = hash.multiply(r.modPow(keyPair.getFirst(), bank.getNPublicKey())).mod(bank.getNPublicKey());
        BigInteger _s = bank.signCache(_n, cost.getCost(), this);
        BigInteger _r = Crypto.inverse(r, bank.getNPublicKey());
        nsPair = new Pair<>(n, /*s*/_s.multiply(_r).mod(bank.getNPublicKey()));
        System.out.println("n=" + n + "\nr= " + r+ "\nd= " + keyPair.getFirst() + "\nc= " + keyPair.getSecond() +"\nH= "
                + hash + "\n_n= " + _n + "\n_s= " + _s + "\n_r= " + _r + "\ns=" + nsPair.getSecond());
    }

    void minusBalance(int cost) {
        this.balance -= cost;
        System.out.println("Я снял деньги, теперь мой баланс равен " + balance);
    }

    public void makePurchaseInShop(Shop shop) {
        shop.buy(nsPair, cost);
    }
}
