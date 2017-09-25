import crypto.Crypto;
import crypto.SolovayStrassen;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Алгоритм Гельфонда — Шенкса
 */
public class BabyStepGiantStep {

    private final BigInteger m, k, a, y, p;
    private ArrayList<BigInteger> baby = new ArrayList<>(),
            giant = new ArrayList<>();
    private BigInteger j = BigInteger.ZERO;
    private BigInteger giantValue;

    public BabyStepGiantStep(BigInteger a, BigInteger p, BigInteger y) {
        if (!SolovayStrassen.isPrime(p, 25)) {
            throw new IllegalArgumentException("P нифига не простое!!! Сам решай");
        }
        this.a = a;
        this.y = y;
        this.p = p;
        k = m = Newton.sqrt(p).add(BigInteger.ONE);
        /*int bitLenght = p.bitLength() / 3;
        k = new BigInteger(bitLenght > 3 ? bitLenght : bitLenght + 3, ThreadLocalRandom.current());*/
    }

    public BigInteger solve() {
        calculateBabyArray();
        if (findExponent()) {
            System.out.println("РЕШЕНИЕ НАЙДЕНО!!!");
            System.out.println("x = " + extractX());
            return extractX();
        } else {
            System.out.println("РЕШЕНИЕ NE НАЙДЕНО(((((((((((");
        }
        return null;
    }

    private BigInteger extractX() {
        int i;
        for (i = 0; i < baby.size() && !baby.get(i).equals(giantValue); i++);
        return BigInteger.valueOf(++i).multiply(m).subtract(j);
    }

    /**
     * Однопоточное решение в лоб. Нужно использовать HashSet/Map
     * @return решён
     */
    private boolean findExponent() {
        BigInteger km = k.multiply(m);
        for (BigInteger j = BigInteger.ONE; !j.equals(km); j = j.multiply(Crypto.TWO)) {
            BigInteger tempGiant = Crypto.power(a, j.multiply(m)).mod(p);
            giant.add(tempGiant);
            if (baby.contains(tempGiant)) {
                this.j = j;
                this.giantValue = tempGiant;
                return true;
            }
        }
        return false;
    }

    private void calculateBabyArray() {
        BigInteger sub = m.subtract(BigInteger.ONE);
        for (BigInteger i = BigInteger.ZERO; i.compareTo(sub) < 0; i = i.add(BigInteger.ONE)) {
            baby.add(
                    Crypto.power(a, i)
                            .multiply(y)
                            .mod(p)
            );
        }
    }

    static class Newton {

        /**
         * Алгоритм взят из книжки Java Programmers Guide To numerical Computing (Ronald Mak, 2003) 5 charter
         * @param x Квадрат
         * @param scale Точность (Размер BigDecimal)
         * @return Корень из числа
         */
        public static BigDecimal sqrt(BigDecimal x, int scale)
        {
            if (x.signum() < 0) {
                throw new IllegalArgumentException("Ты хоть смотри, что подаёшь в метод-то, Алло: x < 0");
            }

            // n = x*(10^(2*scale))
            BigInteger n = x.movePointRight(scale << 1)
                    .toBigInteger();

            // Первая аппроксимация больше половины n.
            int bits = (n.bitLength() + 1) >> 1;
            BigInteger ix = n.shiftRight(bits);
            BigInteger ixPrev;

            // Считаем до определённой EPSILON
            do {
                ixPrev = ix;
                // x = (x + n/x)/2
                ix = ix.add(n.divide(ix))
                        .shiftRight(1);
//                Thread.yield();
            } while (ix.compareTo(ixPrev) != 0);

            return new BigDecimal(ix, scale);
        }

        public static BigInteger sqrt(BigInteger x) {
            return sqrt(new BigDecimal(x), 15).toBigInteger();
        }
    }

}
