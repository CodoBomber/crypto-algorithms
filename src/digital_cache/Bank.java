package digital_cache;

import crypto.Pair;
import rsa.RSACore;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Bank extends RSACore {

    public enum Cost {
        MILLION(1_000_000),
        TEN_THOUSAND(10000),
        FIVE_THOUSAND(5000),
        ONE_THOUSAND(1000),
        FIVE_HUNDRED(500),
        ONE_HUNDRED(100),
        FIFTY(50),
        TEN(10);

        private final int cost;

        Cost(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }
    }

    //混じれた金額は必要となるのならばこのリストが約にたつ。
    private List<Integer> denominations = new ArrayList<>(Arrays.asList(1_000_000, 10000, 5000, 1000, 500, 100, 50, 10));
    private Map<Integer, Pair<BigInteger, BigInteger>> digitalCache = new HashMap<>();

    public Bank() {
        super(256, 256);
        denominations.forEach((e) -> digitalCache.put(e, super.generateAdditionalKeys()));
        System.out.println("Digital Cache: " + digitalCache);
    }

    public BigInteger signCache(BigInteger _n, int cost, Consumer consumer) {
        consumer.minusBalance(cost);
        return _n.modPow(getKeysFor(cost).getSecond(), N);
    }

    public List<Integer> getCache(int cost) {
        int nokori = cost;
        List<Integer> caches = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
        for (int i = 0; i < denominations.size(); i++) {
            caches.add(nokori / denominations.get(i));
            nokori %= denominations.get(i);
        }
        return caches.stream().filter(i -> i != 0).collect(Collectors.toList());
    }

    public Pair<BigInteger, BigInteger> getKeysFor(int denomination) {
        return digitalCache.get(denomination);
    }
}
