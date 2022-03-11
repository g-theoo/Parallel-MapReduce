public class Fibonacci {
	/**
	 * static function that generate Fibonacci numbers
	 */
	synchronized public static int fib(int n) {
		if (n <= 1)
			return n;
		return fib(n-1) + fib(n-2);
	}
}
