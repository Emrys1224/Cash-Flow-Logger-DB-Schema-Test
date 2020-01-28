package cashFlowLoggerDBTest;

import java.util.Locale;

public class IncomeItem {
	String datetime, source, fund;
    PhCurrency amount;

	IncomeItem(String datetime, String source, PhCurrency amount) {
		this.datetime = datetime;
		this.source = source;
		this.amount = amount;
		this.fund = null;
	}


	IncomeItem(String datetime, String source, PhCurrency amount, String fund) {
		this.datetime = datetime;
		this.source = source;
		this.amount = amount;
		this.fund = fund;
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ENGLISH,
				"Date-time: %s\nIncome Source: %s\nAmount: %s",
				this.datetime, this.source, this.amount
		);
	}
}
