import java.util.Scanner;
// Put any necessary java8 imports you may need for solution

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isBrilliant = true;

        while (scanner.hasNext()) {
            int n = scanner.nextInt();


            if (isPrime(n)) {
                isBrilliant = false;
            } else if (!OnlyTwoPrimesForObtaining(n)) {
                isBrilliant = false;

            } else if (!primesOfOneLength(n)) {
                isBrilliant = false;
            }
            System.out.println(isBrilliant);
        }
    }

    public static boolean isPrime(int n) {
        boolean prime = true;
        if (n == 1) {
            prime = false;
        } else {
            for (int i = 2; i < 10000; i++) {
                if (n % i == 0 && n != i)
                    prime = false;
            }
        }
        return prime;
    }

    public static boolean OnlyTwoPrimesForObtaining(int n) {
        boolean onlyTwoPrimes = true;
        int countPrimes = 0;
        for (int i = 2; i < 10000; i++) {
            if (n % i == 0 && n != i) {
                if (!isPrime(i)) {
                    onlyTwoPrimes = false;
                } else {
                    countPrimes += 1;
                }
            }
        }
        return countPrimes == 1 || countPrimes == 2  && onlyTwoPrimes ? true : false;
    }

    public static boolean primesOfOneLength(int n) {
        int[] primes = new int[2];
        int count = 0;
        boolean pr = true;

        for (int i = 2; i < 10000; i++) {
            if (n % i == 0 && n != i) {
                primes[count] = i;
                count++;
            }
        if (primes[1] == 0) {
            primes[1] = primes[0];
        }

            if (primes[0] / 10 != primes[1] / 10) {
                pr = false;
            }
        }
        return pr;
    }
}