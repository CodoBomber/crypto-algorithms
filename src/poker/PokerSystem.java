package poker;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PokerSystem {

    private final BigInteger P;
    private List<Integer> indexes;
    private List<PokerPlayer> players;
    private final HashMap<Integer, String> K;
    private int playersCounter = 1;

    public PokerSystem() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.P = BigInteger.probablePrime(20, random);
        this.indexes = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            indexes.add(i);
        }
        final String[] cardValues = {"two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack",
                "queen", "king", "ace"};
        final String[] cardLears = {" of spades", " of hearts", " of clubs", " of diamonds"};
        Collections.shuffle(indexes);
        //Маппинг исходной колоды для поиска
        this.K = new HashMap<>(IntStream.range(0, indexes.size())
                .boxed()
                .collect(Collectors.toMap(
                        indexes::get,
                        integer -> {
                            int border = indexes.get(integer) % cardValues.length;
                            int shift = indexes.get(integer) / 13;
                            int i = border == 0 && shift < 3 && indexes.get(integer) != 0 ? shift + 1 : shift;
                            return cardValues[border] + cardLears[i];
                        }
                        )
                ));
    }

    public void startDeskEncoding() {
        if (players != null && players.size() > 2) {
            players.forEach(PokerPlayer::encodeDesk);
        } else {
            throw new IllegalStateException("Нельзя стартовать из-за того, что собралось игроков меньше 2х человек");
        }
        startCardDealing();
    }

    private void startCardDealing() {
        //スタックに入れて二つずつ抜き渡す
        Stack<Integer> deskStack = new Stack<>();
        indexes.forEach(deskStack::push);
        ArrayList<Integer> twoCards = new ArrayList<>();
        //みんなに渡すループ
        for (int i = 0; i < players.size(); i++) {
            //カード2枚を本人以外みんなにキーを解錠しに渡す
            System.out.println("Колода в раздаче:\n" + deskStack);
            twoCards.add(0, deskStack.pop());
            twoCards.add(1, deskStack.pop());
            for (int j = 0; j < players.size(); j++) {
                if (j == i) {
                    continue;
                }
                twoCards = players.get(j).decodeCards(twoCards);
            }
            players.get(i).dealCards(twoCards.get(0), twoCards.get(1));
        }
        //５カード机に置いておくように
    }

    public void setPlayers(List<PokerPlayer> players) {
        this.players = players;
    }

    public BigInteger getP() {
        return P;
    }

    BigInteger getSubP() {
        return P.subtract(BigInteger.ONE);
    }

    public List<Integer> getDeck() {
        return indexes;
    }

    public String getCard(int hash) {
        return K.get(hash);
    }

    void updateDesk(int playerId, List<Integer> collect) {
        Collections.shuffle(collect);
        System.out.println("Кодирование колоды игроком №: " + playerId);
        System.out.println(collect);
        indexes = collect;
    }

    int incrementPlayerCounter() {
        return playersCounter++;
    }
}
