package graph_painting;

import crypto.Pair;

import java.math.BigInteger;

public class GraphPurchaser {


    public void startZeroKnowledge(Painter returnPoint) {
        Edge randomEdge = returnPoint.getRandomEdge();
        Pair<BigInteger, BigInteger> cPair = returnPoint.getPrivateKeyForEdge(randomEdge);
        BigInteger _Z1 = randomEdge.getVertex1().getZ().modPow(cPair.getFirst(), randomEdge.getVertex1().getNPublicKey());
        BigInteger _Z2 = randomEdge.getVertex2().getZ().modPow(cPair.getSecond(), randomEdge.getVertex2().getNPublicKey());
        checkColorBits(_Z1, _Z2);
    }

    private void checkColorBits(BigInteger _z1, BigInteger _z2) {
        BigInteger trueBits = BigInteger.valueOf(3);
        System.out.println("_z1:" + _z1 + " _z2:" + _z2);
        System.out.println("last 2 bites: _z1:"  + (_z1.testBit(1) ? 1 : 0) + (_z1.testBit(0)  ? 1 : 0)
                + " _z2:" + (_z2.testBit(1)  ? 1 : 0) + (_z2.testBit(0) ? 1 : 0));
        if (_z1.and(trueBits).equals(_z2.and(trueBits))) {
            throw new IllegalStateException("БОБ БЫЛ ЗЛОСТНО ОБМАНУТ АЛИСОЙ");
        }
    }
}
