package poker;

import crypto.Crypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PokerPlayer implements PokerAction {

    private BigInteger publicKey;
    private BigInteger privateKey;
    private final PokerSystem system;
    private final int id;
    private ArrayList<Integer> ownCards;

    public PokerPlayer(PokerSystem pokerSystem) {
        this.system = pokerSystem;
        this.id = system.incrementPlayerCounter();
        Random random = ThreadLocalRandom.current();
        List<BigInteger> gcdList;
        do {
            privateKey = new BigInteger(system.getSubP().bitLength() - 1, random);
            gcdList = Crypto.gcd(privateKey, system.getSubP());
        } while (!gcdList.get(0).equals(BigInteger.ONE));
        publicKey = gcdList.get(1).compareTo(BigInteger.ZERO) < 0
                ? gcdList.get(1).add(system.getSubP()) : gcdList.get(1);
    }

    @Override
    public void encodeDesk() {
        system.updateDesk(this.id, system.getDeck()
                .stream()
                .map(
                        k -> BigInteger.valueOf(k)
                                .modPow(privateKey, system.getP())
                                .intValueExact()
                )
                .collect(Collectors.toList()));
    }

    @Override
    public void dealCards(int card1, int card2) {
        ArrayList<Integer> cards = new ArrayList<>(Arrays.asList(
                card1,
                card2
        ));
        try{

        } catch(Throwable t)  {

        }
        decodeCards(cards);
        this.ownCards = cards;
        System.out.println("Карты игрока номер " + id + " : " + Arrays.asList(system.getCard(ownCards.get(0)), system.getCard(ownCards.get(1))));
    }

    @Override
    public void decodeCards(ArrayList<Integer> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.set(i, BigInteger.valueOf(cards.get(i)).modPow(publicKey, system.getP()).intValueExact());
        }
    }

}
