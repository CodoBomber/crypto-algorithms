package digital_cache;

import crypto.Pair;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Shop {

    private final Bank bank;
    private final MessageDigest mesageDigest;

    public Shop(Bank bank) throws NoSuchAlgorithmException {
        this.bank = bank;
        mesageDigest = MessageDigest.getInstance("SHA-256");
    }

    public void buy(Pair<BigInteger, BigInteger> nsPair, Bank.Cost cost) {
        BigInteger hash = new BigInteger(1, mesageDigest.digest(nsPair.getFirst().toByteArray()));
        System.out.println("SHOP!!");
        BigInteger _s = nsPair.getSecond().modPow(bank.getKeysFor(cost.getCost()).getFirst(), bank.getNPublicKey());
        System.out.println("H=" +hash +" user_n=" + nsPair.getFirst() + " user_s=" + nsPair.getSecond()
                + " confirmed_signature=" + _s);
        if (!_s.equals(hash)) {
            throw new IllegalStateException("Купюра '" + cost.getCost() + "' с ключами: N="
                    + nsPair.getFirst() + " & S=" + nsPair.getSecond() + " Недействительна! За вами уже выехали");
        }
        System.out.println("УРА!!! Покупка произведена! С Вами приятно иметь дело!");
    }
}
