package cashFlowLoggerDBTest;

import java.util.Locale;

class ExpenseItem {
//	datetime, item, brand, unitPrice, size, unit, quantity, fund, tag
	String datetime, item, brand, size, unit, quantity, fund;
	PhCurrency unitPrice;
	String[] tag;

	ExpenseItem(String datetime, String item, String brand, PhCurrency unitPrice, String size, String unit, String quantity, String fund, String[] tag) {
		this.datetime  = datetime;
		this.item      = item;
		this.brand     = brand;
		this.unitPrice = unitPrice;
		this.size      = size;
		this.unit      = unit;
		this.quantity  = quantity;
		this.fund	   = fund;
		this.tag       = tag;
	}

	ExpenseItem(String datetime, String item, String brand, PhCurrency unitPrice, String size, String unit,String quantity, String fund) {
		this.datetime  = datetime;
		this.item      = item;
		this.brand     = brand;
		this.unitPrice = unitPrice;
		this.size      = size;
		this.unit      = unit;
		this.quantity  = quantity;
		this.fund	   = fund;
		this.tag       = null;
	}
	
	// for debugging purposes
	@Override
	public String toString() {
		String tags = "";
		for (int i = 0; i < tag.length; i++) {
			if (i < tag.length - 1) 
				tags += tag[i] + ", ";	// for all items before the last element add a comma
			else
				tags += tag[i];
		}
		
		return String.format(
				Locale.ENGLISH,
				"Date-time: %s\nItem: %s\nBrand: %s\nPrice per item: %s\nPackaging size: %s\nUnit: %s\nQuantity: %s\nTags: %s",
				this.datetime, this.item, this.brand, this.unitPrice.toString(), this.size, this.unit, this.quantity, tags);
	}

}
