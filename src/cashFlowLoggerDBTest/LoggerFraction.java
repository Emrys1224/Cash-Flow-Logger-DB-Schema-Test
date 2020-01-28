package cashFlowLoggerDBTest;


public class LoggerFraction {
	// String fraction such as 1 1/2 and 1/4.....
	static final String FRACTION_REGEX = "\\d{1,}((\\s\\d{1,})?[/]\\d{1,})";

	private int mWholeNum;
	private int mNumerator;
	private int mDenominator;

	LoggerFraction(int numerator, int denominator) throws IllegalArgumentException {
		if (denominator == 0)
			throw new IllegalArgumentException("The denominator cannot be zero!");

		this.mWholeNum = 0;
		this.mNumerator = numerator;
		this.mDenominator = denominator;

		reduce();
	}

	LoggerFraction(int wholeNum, int numerator, int denominator) throws IllegalArgumentException {
		if (denominator == 0)
			throw new IllegalArgumentException("The denominator cannot be zero!");

		this.mWholeNum = wholeNum;
		this.mNumerator = numerator;
		this.mDenominator = denominator;

		reduce();
	}

	LoggerFraction(String fraction) throws IllegalArgumentException {
		this.mWholeNum = 0;

		// Checks if the String is in a valid fraction pattern.
		// Throws an IllegalArgumentException if String is not valid.
		if (!fraction.matches(FRACTION_REGEX)) {
			throw new IllegalArgumentException("This is not a valid fraction string.");
		}

		// Isolate the whole number by splitting the string at the space(\s) char and
		// assign to mWholeNum.
		String[] partialVal = fraction.split("\\s");
		if (partialVal.length > 1) {
			this.mWholeNum += Integer.parseInt(partialVal[0]);
			fraction = partialVal[1];
//			System.out.println("Whole Number: " + partialVal[0]);
//			System.out.println("Fractional Value: " + fraction);
		}

		// Isolate the remaining String by splitting at the slash(/) char to get the
		// numerator and the denominator and assign to mNumerator and mDenominator accordingly.
		String[] fractionParts = fraction.split("[/]");
//		System.out.println("Numerator: " + fractionParts[0]);
//		System.out.println("Denominator: " + fractionParts[1]);

		// Throws an IllegalArgumentException if the denominator is zero.
		if (Integer.parseInt(fractionParts[1]) == 0)
			throw new IllegalArgumentException("The denominator cannot be zero!");

		this.mNumerator = Integer.parseInt(fractionParts[0]);
		this.mDenominator = Integer.parseInt(fractionParts[1]);

		reduce();
	}

	int getWholeNumber() {
		return this.mWholeNum;
	}

	int getNumerator() {
		return this.mNumerator;
	}

	int getDenominator() {
		return this.mDenominator;
	}

	double toDouble() {
		return mWholeNum + (mNumerator / (double) mDenominator);
	}

	@Override
	public String toString() {
		return (mWholeNum != 0 ? String.valueOf(mWholeNum) + " " : "") + String.valueOf(mNumerator) + "/"
				+ String.valueOf(mDenominator);
	}

	private void reduce() {

		// If mNumerator is greater than the mDenominator divide mNumerator by
		// mDenominator then add the answer to mWholeNum.
		if (mNumerator > mDenominator) {
			int wholeNum = mNumerator / mDenominator;
			mWholeNum += wholeNum;

			// Assign the remainder to the mNumerator.
			mNumerator = mNumerator % mDenominator;
		}

		// Get the Greatest Common Factor (GCF) of mNumerator and mDenominator.
		int gcf = findGCF(mNumerator, mDenominator);

		if (gcf > 1) {
			// Divide mNumerator by the GCF then change its value by the answer.
			mNumerator /= gcf;
			// Divide mDenominator by the GCF then change its value by the answer.
			mDenominator /= gcf;
		}
	}

	private static int findGCF(int number1, int number2) {
//		System.out.println("number 1: " + number1 + "\t|| number 2: " + number2);
		// base case
		if (number2 == 0) {
//        	System.out.println("GCF: " + number1);
			return number1;
		}
		return findGCF(number2, number1 % number2);
	}
}
