package cashFlowLoggerDBTest;

import static org.junit.Assert.*;

import org.junit.Test;

public class PhCurrencyTest2 {
	double DELTA = 0.009;

	@Test
	public void test() {
		double testVal1 = 100.0;
		double testVal2 = 36.0;
		
		PhCurrency val1 = new PhCurrency(testVal1);
		PhCurrency val2 = new PhCurrency(testVal2);
		
		// Get the difference of val1 from val2.
		val1.subtract(val2);
		PhCurrency ans1 = new PhCurrency(val1);
		
		// Reset val1 value.
		val1.setValue(testVal1);
		
		// Add the negative value of val2 to val1.
		val2.multiplyBy(-1);
		val1.add(val2);
		PhCurrency ans2 = new PhCurrency(val1);
		
		System.out.println("Ans1: " + ans1.toString() + "\nAns2: " + ans2.toString());
		
		assertEquals(ans1.toDouble(), ans2.toDouble(), DELTA);
	}

	@Test
	public void test2() {
		long num1 = 12234134L;
		int num2 = 567567;
		System.out.println("long String: " + num1 + ", " + String.valueOf(num1) +
				"\nint String: " + num2 + ", " + String.valueOf(num2));
		assertTrue(true);
	}
	
}
