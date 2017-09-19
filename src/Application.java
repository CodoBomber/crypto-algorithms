import crypto.Crypto;
import crypto.SolovayStrassen;
import diffie_hellman.System;
import diffie_hellman.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) throws IllegalAccessException {
        Crypto algorithms = new Crypto();
        List<BigInteger> bigIntegers;

        /* Простые числа Мерсенна *//*
        bigIntegers = Arrays.asList(
                new BigInteger("3"),
                new BigInteger("7"),
                new BigInteger("31"),
                new BigInteger("127"),
                new BigInteger("8191"),
                new BigInteger("131071"),
                new BigInteger("524287"),
                new BigInteger("2147483647"),
                new BigInteger("2305843009213693951"),
                new BigInteger("618970019642690137449562111"),
                new BigInteger("162259276829213363391578010288127"),
                new BigInteger("170141183460469231731687303715884105727"));
        *//* ----------------------------------------  */

        /* Простые числа Фиббоначи */
        /*bigIntegers = Arrays.asList(
                new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("5"),
                new BigInteger("13"),
                new BigInteger("89"),
                new BigInteger("233"),
                new BigInteger("1597"),
                new BigInteger("28657"),
                new BigInteger("514229"),
                new BigInteger("433494437"),
                new BigInteger("2971215073"),
                new BigInteger("99194853094755497"),
                new BigInteger("1066340417491710595814572169"),
                new BigInteger("19134702400093278081449423917"));*/
        /* -------------------------------------------- */

        /* Простые числа Кэрола */

        bigIntegers = Arrays.asList(
                new BigInteger("7"),
                new BigInteger("47"),
                new BigInteger("223"),
                new BigInteger("3967"),
                new BigInteger("16127"),
                new BigInteger("1046527"),
                new BigInteger("16769023"),
                new BigInteger("1073676287"),
                new BigInteger("68718952447"),
                new BigInteger("274876858367"),
                new BigInteger("4398042316799"),
                new BigInteger("1125899839733759"),
                new BigInteger("18014398241046527"),
                new BigInteger("1298074214633706835075030044377087"));
        /* --------------------------------------------- */


        /* Числа Кармайкла */
        /*bigIntegers = Arrays.asList(
                new BigInteger("561"),
                new BigInteger("1105"),
                new BigInteger("1729"),
                new BigInteger("2465"),
                new BigInteger("2821"),
                new BigInteger("6601"),
                new BigInteger("8911"),
                new BigInteger("10585"),
                new BigInteger("15841"),
                new BigInteger("29341"),
                new BigInteger("41041"),
                new BigInteger("46657"),
                new BigInteger("52633"),
                new BigInteger("62745"),
                new BigInteger("63973"),
                new BigInteger("75361"));*/
        /* -------------------------------------------- */
        /*for (BigInteger bigInteger : bigIntegers) {
            java.lang.System.out.println("lib: " + bigInteger.isProbablePrime(50) + "(" + bigInteger + ")");
            java.lang.System.out.println("impl: " + algorithms.isProbablePrime(bigInteger) + "(" + bigInteger + ")");
        }*/
        for (BigInteger bigInteger : bigIntegers) {
            java.lang.System.out.println("lib: " + bigInteger.isProbablePrime(50) + "(" + bigInteger + ")");
            java.lang.System.out.println("impl: " + SolovayStrassen.isPrime(bigInteger, 25) + "(" + bigInteger + ")");
        }

        /*System system = new System();
        User user1 = system.registerNewUser();
        User user2 = system.registerNewUser();
        user1.attemptConnectTo(user2.getUsername());
        */


    }
}
