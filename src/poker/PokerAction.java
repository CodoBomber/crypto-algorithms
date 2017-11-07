package poker;

import java.util.ArrayList;

public interface PokerAction {

    void encodeDesk();

    void dealCards(int card1, int card2);

    void decodeCards(ArrayList<Integer> cards);

}
