import crypto.Crypto;
import diffie_hellman.System;
import diffie_hellman.User;

import java.math.BigInteger;

public class Application {

    public static void main(String[] args) throws IllegalAccessException {
        Crypto algorithms = new Crypto();
        // TODO: 17/09/11 Неправильно!!!
        /*java.lang.System.out.println(Crypto.binaryPow(
                new BigInteger("2988348162058574136915891421498819466320163312926952423791023078876139"),
                new BigInteger("2351399303373464486466122544523690094744975233415544072992656881240319"),
                new BigInteger("10000000000000000000000000000000000000000")));
        java.lang.System.out.println(
                new BigInteger("2988348162058574136915891421498819466320163312926952423791023078876139")
                        .modPow(
                                new BigInteger("2351399303373464486466122544523690094744975233415544072992656881240319"),
                                new BigInteger("10000000000000000000000000000000000000000"))
        );

        java.lang.System.out.println(Crypto.binaryPow(BigInteger.valueOf(21), BigInteger.valueOf(7), BigInteger.valueOf(23)));
        java.lang.System.out.println(Crypto.binaryPow(BigInteger.valueOf(17), BigInteger.valueOf(13), BigInteger.valueOf(23)));

        java.lang.System.out.println(BigInteger.valueOf(21).modPow(BigInteger.valueOf(7), BigInteger.valueOf(23)));
        java.lang.System.out.println(BigInteger.valueOf(17).modPow(BigInteger.valueOf(13), BigInteger.valueOf(23)));
*/

//        java.lang.System.out.println(algorithms.isPrime(BigInteger.valueOf(61)));
//        java.lang.System.out.println(BigInteger.valueOf(61).isProbablePrime(50));
//        java.lang.System.out.println(new BigInteger("618970019642690137449562111").isProbablePrime(50));
        /*for (int i = 1; i < 500; i += 2) {
//            java.lang.System.out.println(String.valueOf(i) + ": " + new BigInteger(String.valueOf(i)).isProbablePrime(50));
            BigInteger bigInteger = new BigInteger(String.valueOf(i));
            if (bigInteger.isProbablePrime(50) != algorithms.isMillerPrime(bigInteger)) {
                java.lang.System.out.println(String.valueOf(i) + ": " + bigInteger.isProbablePrime(50));
                java.lang.System.out.println(String.valueOf(i) + ": " + algorithms.isMillerPrime(bigInteger));

            }
//        java.lang.System.out.println(Crypto.binaryPow(BigInteger.valueOf(30), BigInteger.valueOf(2), BigInteger.valueOf(53)));
//            java.lang.System.out.println(String.valueOf(i) + ": " + algorithms.isMillerPrime(new BigInteger(String.valueOf(i))));
        }*/
        java.lang.System.out.println(new BigInteger("25").isProbablePrime(50));
//        java.lang.System.out.println(algorithms.isMillerPrime(new BigInteger("25")));

//91, 121, 133, 231, 257, 259, 341, 449

        /*System system = new System();
        User user1 = system.registerNewUser();
        User user2 = system.registerNewUser();
        user1.attemptConnectTo(user2.getUsername());
        */

        /*System.out.println(algorithms.binaryPow(BigInteger.valueOf(4),
                BigInteger.valueOf(2),
                BigInteger.valueOf(10)));
        System.out.println(algorithms.gcd(BigInteger.valueOf(28), BigInteger.valueOf(19)));
        System.out.println(algorithms.getPrimeNumber(23));*/
    }
}
