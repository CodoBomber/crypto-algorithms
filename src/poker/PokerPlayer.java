package poker;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PokerPlayer implements PokerAction {

    private final BigInteger publicKey;
    private BigInteger C;
    private final PokerSystem system;
    private final int id;
    private ArrayList<Integer> ownCards;

    public PokerPlayer(PokerSystem pokerSystem) {
        this.system = pokerSystem;
        this.id = system.incrementPlayerCounter();
        this.C = new BigInteger(8, ThreadLocalRandom.current());
        //odd
        C = C.add(C.add(BigInteger.ONE))
                .mod(BigInteger.valueOf(2));
        while (!C.gcd(system.getSubP()).equals(BigInteger.ONE)) {
            C = C.add(BigInteger.valueOf(2));
        }
        this.publicKey = Crypto.gcd(system.getSubP(), C).get(1);
    }

    @Override
    public void encodeDesk() {
        system.updateDesk(this.id, system.getDeck()
                .stream()
                .map(k -> BigInteger.valueOf(k).modPow(C, system.getP()).intValueExact())
                .collect(Collectors.toList()));
    }

    @Override
    public void dealCards(int card1, int card2) {
        this.ownCards = new ArrayList<>(Arrays.asList(
                BigInteger.valueOf(card1).modPow(publicKey, system.getP()).intValueExact(),
                BigInteger.valueOf(card2).modPow(publicKey, system.getP()).intValueExact()
        )
        );
        System.out.println("Карты игрока номер " + id + " : " + Arrays.asList(system.getCard(ownCards.get(0)), system.getCard(ownCards.get(1))));
    }

    @Override
    public ArrayList<Integer> decodeCards(ArrayList<Integer> twoCards) {
        twoCards.set(0, BigInteger.valueOf(twoCards.get(0)).modPow(publicKey, system.getP()).intValueExact());
        twoCards.set(1, BigInteger.valueOf(twoCards.get(1)).modPow(publicKey, system.getP()).intValueExact());
        return twoCards;
    }

}
