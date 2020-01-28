package cashFlowLoggerDBTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class Logger {
	private static final int NOT_FOUND = -1;
	private static final int NULL_ID = -1;
	private static final int INITIAL_ID = 1;

	public static final String CREATE_TABLES = "  -- Cash Flow Logger Schema (SQLite DB)\r\n" + 
			"  DROP TABLE IF EXISTS fraction_text;\r\n" + 
			"  DROP TABLE IF EXISTS balance;\r\n" + 
			"  DROP TABLE IF EXISTS funds;\r\n" + 
			"  DROP TABLE IF EXISTS funds_allocation;\r\n" + 
			"  DROP TABLE IF EXISTS funds_balance;\r\n" + 
			"  DROP TABLE IF EXISTS source;\r\n" + 
			"  DROP TABLE IF EXISTS income;\r\n" + 
			"  DROP TABLE IF EXISTS product;\r\n" + 
			"  DROP TABLE IF EXISTS tag;\r\n" + 
			"  DROP TABLE IF EXISTS product_tags;\r\n" + 
			"  DROP TABLE IF EXISTS brand;\r\n" + 
			"  DROP TABLE IF EXISTS unit;\r\n" + 
			"  DROP TABLE IF EXISTS product_size;\r\n" + 
			"  DROP TABLE IF EXISTS product_variant;\r\n" + 
			"  DROP TABLE IF EXISTS item;\r\n" + 
			"  DROP TABLE IF EXISTS expense;\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE fraction_text(\r\n" + 
			"    -- Look-up table for text representation of fractional values entered by the user.\r\n" + 
			"    -- This will be used to avoid confusion in redisplaying values entered by the user\r\n" + 
			"    -- since the equivalent values are stored as float.\r\n" + 
			"    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    val_as_txt TEXT NOT NULL UNIQUE);\r\n" + 
			"\r\n" + 
			"  CREATE TABLE balance(\r\n" + 
			"    -- Records the cash at hand.\r\n" + 
			"    -- Updated everytime an income/expense entry is added\r\n" + 
			"    id                INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    amountX100        INTEGER  NOT NULL,    -- Amount in Php times 10 to include centavo.\r\n" + 
			"    income_update_id  INTEGER,              -- ID of income entry that is added to the balance.\r\n" + 
			"    expense_update_id INTEGER,              -- ID of expense entry that is deducted from the balance.\r\n" + 
			"    FOREIGN KEY(income_update_id) REFERENCES income(id),\r\n" + 
			"    FOREIGN KEY(expense_update_id) REFERENCES expense(id),              -- Either income or expense\r\n" + 
			"    CHECK((income_update_id != NULL AND expense_update_id == NULL) OR   -- should have value at a time\r\n" + 
			"          (income_update_id == NULL AND expense_update_id != NULL)));   -- but not both.\r\n" + 
			"\r\n" + 
			"  CREATE TABLE funds(\r\n" + 
			"    -- Look-up table of all declared funds.\r\n" + 
			"    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE funds_allocation(\r\n" + 
			"    -- Stores how the income is to be divided amongst the fund (in percent).\r\n" + 
			"    -- This will also serve as how the allocation of fund changes over time.\r\n" + 
			"    -- Unused fund will have an allocation of 0%.\r\n" + 
			"    _date              DATE     DEFAULT CURRENT_DATE,\r\n" + 
			"    fund_id            INTEGER  NOT NULL,\r\n" + 
			"    percent_allocation INTEGER  NOT NULL,\r\n" + 
			"    FOREIGN KEY(fund_id) REFERENCES funds(id),\r\n" + 
			"    PRIMARY KEY(_date, fund_id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE funds_balance(\r\n" + 
			"    -- Records the current balance for each fund.\r\n" + 
			"    -- Fund amount is increased as per fund allocation percentage when\r\n" + 
			"    -- income is added.\r\n" + 
			"    -- Expense amount is subtracted to the corresponding fund where it\r\n" + 
			"    -- is to be deducted from.\r\n" + 
			"    fund_id           INTEGER NOT NULL,\r\n" + 
			"    balance_update_id INTEGER NOT NULL,   -- References which cuased the update.\r\n" + 
			"    amountX100        INTEGER NOT NULL,   -- Amount in Php times 10 to include centavo.\r\n" + 
			"    FOREIGN KEY(balance_update_id) REFERENCES balance(id),\r\n" + 
			"    PRIMARY KEY(fund_id, balance_update_id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE source(\r\n" + 
			"    -- Source of income(e.g. commission, royalties, salary)\r\n" + 
			"    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE income( \r\n" + 
			"    -- Income record\r\n" + 
			"    id          INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    datetime    DATETIME DEFAULT CURRENT_TIMESTAMP,\r\n" + 
			"    source_id   INTEGER  NOT NULL,\r\n" + 
			"    amountX100  INTEGER  NOT NULL,   -- Amount in Php times 10 to include centavo.\r\n" + 
			"    FOREIGN KEY(source_id) REFERENCES source(id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE product(\r\n" + 
			"    -- Look_up table of product name/type(e.g. salt, shirt, internet)\r\n" + 
			"    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE tag(\r\n" + 
			"    -- Look-up table of tags/categories of products as specified by the user.\r\n" + 
			"    -- This will be used for searching a particular category of items for\r\n" + 
			"    -- price comparison or analyzing the expense patern for that category.\r\n" + 
			"    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE product_tags(\r\n" + 
			"    -- Intermediary table for products and tags since a product can have\r\n" + 
			"    -- multiple tags.\r\n" + 
			"    product_id INTEGER NOT NULL,\r\n" + 
			"    tag_id     INTEGER NOT NULL,\r\n" + 
			"    FOREIGN KEY(product_id) REFERENCES product(id),\r\n" + 
			"    FOREIGN KEY(tag_id)     REFERENCES tag(id),\r\n" + 
			"    PRIMARY KEY(product_id, tag_id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE brand(\r\n" + 
			"    -- Look-up table for product brands\r\n" + 
			"    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE unit(\r\n" + 
			"    -- Look-up table for unit of measurement\r\n" + 
			"    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE product_size(\r\n" + 
			"   -- Look-up table of packaging/serving size of products\r\n" + 
			"   id       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"   size     REAL    NOT NULL,\r\n" + 
			"   size_txt INTEGER,          -- ID from look-up table fraction_text.\r\n" + 
			"   unit_id  INTEGER NOT NULL,\r\n" + 
			"   FOREIGN KEY(size_txt) REFERENCES fraction_text(id),\r\n" + 
			"   FOREIGN KEY(unit_id) REFERENCES unit(id),\r\n" + 
			"   CONSTRAINT package_size UNIQUE(size, unit_id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE product_variant(\r\n" + 
			"    -- List of product for each brand and package size\r\n" + 
			"    id              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    product_id      INTEGER NOT NULL,\r\n" + 
			"    brand_id        INTEGER NOT NULL,\r\n" + 
			"    product_size_id INTEGER NOT NULL,\r\n" + 
			"    FOREIGN KEY(product_id)      REFERENCES product(id),\r\n" + 
			"    FOREIGN KEY(brand_id)        REFERENCES brand(id),\r\n" + 
			"    FOREIGN KEY(product_size_id) REFERENCES product_size(id),\r\n" + 
			"    CONSTRAINT product_variant  UNIQUE(product_id, brand_id, product_size_id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE item(\r\n" + 
			"    -- Purchased item defined as the combination of a particular product and\r\n" + 
			"    -- its price at a certain date. It will serve as a record of the change\r\n" + 
			"    -- in price over time of a product.\r\n" + 
			"    id                 INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    date               DATE     DEFAULT CURRENT_DATE,\r\n" + 
			"    product_variant_id INTEGER  NOT NULL,\r\n" + 
			"    priceX100          INTEGER  NOT NULL,   -- Price in Php times 10 to include centavo.\r\n" + 
			"    FOREIGN KEY(product_variant_id) REFERENCES product_variant(id));\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"  CREATE TABLE expense(\r\n" + 
			"    -- Expenses record\r\n" + 
			"    id            INTEGER  PRIMARY KEY AUTOINCREMENT,\r\n" + 
			"    datetime      DATETIME DEFAULT CURRENT_TIMESTAMP,\r\n" + 
			"    item_id       INTEGER  NOT NULL,\r\n" + 
			"    quantity      REAL     NOT NULL,\r\n" + 
			"    quantity_txt  INTEGER,          -- ID from look-up table fraction_text.\r\n" + 
			"    remarks       TEXT,\r\n" + 
			"    FOREIGN KEY(quantity_txt) REFERENCES fraction_text(id),\r\n" + 
			"    FOREIGN KEY(item_id) REFERENCES item(id));\r\n" + 
			"";

	public static final String[] FUNDS = { "Basic Necessity", "Education", "Investment", "Health", "Retirement",
			"Leisure" };
	public static final double[] PERCENT_ALLOCATION = { 0.5, 0.1, 0.1, 0.1, 0.1, 0.1 };
	public static final IncomeItem INITIAL_BALANCE = new IncomeItem("2019-02-01 00:00:00", "Initial Balance",
			new PhCurrency(10000.00));

	private Connection mConnection;

	public Logger(Connection connection) {
		this.mConnection = connection;
	}

	void initializeDB() throws SQLException {
		System.out.println("~~~~~~~~~~~~~~~~~~ INITIALIZING DATABASE ~~~~~~~~~~~~~~~~\n\n");

		Statement stmt = null;
		try {
			stmt = this.mConnection.createStatement();
			// Create the tables.
			System.out.println("~~~~~~~~~~~~~~~~~~~~~ Creating Tables ~~~~~~~~~~~~~~~~~~~\n");
			System.out.println(CREATE_TABLES + "\n\n");
			stmt.executeUpdate(CREATE_TABLES);

			// Add the initial `funds` and `funds_allocation` entries.
			System.out.println("~~~~~~~~~~~~ Adding Funds and Its Allocations ~~~~~~~~~~~\n");
			for (int i = 0; i < FUNDS.length; i++) {
				int fundId = insertThenGetID("funds", FUNDS[i]);

				List<SimpleEntry<String, Object>> colValPairs = new ArrayList<>(Arrays.asList(
						new SimpleEntry<>("_date", "2019-02-01"),
						new SimpleEntry<>("fund_id", String.valueOf(fundId)),
						new SimpleEntry<>("percent_allocation", String.valueOf((int) (PERCENT_ALLOCATION[i] * 100)))));
				insertThenGetID("funds_allocation", colValPairs);
			}
			System.out.println(); // Add blank line...

			// Set initial `balance` entry.
			System.out.println("~~~~~~~~~~~~~~~~~ Adding Initial Balance ~~~~~~~~~~~~~~~~\n");
			logIncome(INITIAL_BALANCE);

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
			System.out.println("SQLException");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.out.println("Exception");

		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();

			} catch (SQLException se2) {
			} // nothing we can do
		}

	}

	/**
	 * Update the amount of cash at hand.
	 * 
	 * @param amountDiff is the amount to be added or subtracted.
	 * @param incomeId   is the 'income' entry id for when the update is done by
	 *                   adding income, "null" (NULL_ID) otherwise.
	 * @param expenseId  is the 'expense' entry id for when the update is done by
	 *                   subtracting an expense, "null" (NULL_ID) otherwise.
	 * @throws IllegalArgumentException when both 'incomeId' and 'expenseId' has a
	 *                                  valid ID or both have no valid ID.
	 * @throws SQLException             when retrieving the amount for the last
	 *                                  entry failed for whatever reason.... :D ;
	 *                                  when inserting a new entry failed for
	 *                                  whatever reason.... :D ; when update is
	 *                                  unsuccessful for whatever reason, again....
	 *                                  XD
	 */
	void updateBalanceAndFunds(PhCurrency amountDiff, int incomeId, int expenseId, String fund)
			throws SQLException, IllegalArgumentException {

		if ((incomeId >= 0 && expenseId >= 0) || (incomeId < 0 && expenseId < 0))
			throw new IllegalArgumentException(
					"Either 'incomeId' or 'incomeId' should have a valid ID but not both....");

		int updateId;
		String incomeIdStr;
		String expenseIdStr;
		PhCurrency updatedBalance = new PhCurrency();

		// Get the current balance
		Statement stmt = mConnection.createStatement();
		String sql = "SELECT amountX100 FROM balance ORDER BY id DESC LIMIT 1;";
		ResultSet rs = stmt.executeQuery(sql);

		if (rs.next()) {
			updatedBalance.setValue(rs.getLong(1));
			System.out.println(">>> Current Balance: " + updatedBalance.toString() + "\n>>> Amount Diff: "
					+ amountDiff.toString());

			// Calculate the new balance amount.
			// Update value of local variables accordingly.
			if (incomeId >= 0) {
				updatedBalance.add(amountDiff);
				incomeIdStr = String.valueOf(incomeId);
				expenseIdStr = "NULL";

			} else {
				updatedBalance.subtract(amountDiff);
				incomeIdStr = "NULL";
				expenseIdStr = String.valueOf(expenseId);
			}
		}

		// For setting the initial value for `balance` table otherwise an
		// SQLExeption will be triggered by the next "else" statement in
		// this block....
		else if (incomeId == INITIAL_ID) {
			updatedBalance.setValue(amountDiff);
			incomeIdStr = String.valueOf(incomeId);
			expenseIdStr = "NULL";
		}

		else
			throw new SQLException("Something went wrong XD, check updateBalance()...");

		// Set the balance details.
		List<SimpleEntry<String, Object>> colValPairs = new ArrayList<>(
				Arrays.asList(new SimpleEntry<>("amountX100", String.valueOf(updatedBalance.getAmountX100())),
						new SimpleEntry<>("income_update_id", incomeIdStr),
						new SimpleEntry<>("expense_update_id", expenseIdStr)));

		try {
			// Insert entry for the updated balance and get the its ID.
			System.out.println(">>> Update Amount: " + updatedBalance.toString());
			updateId = insertThenGetID("balance", colValPairs);

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			throw new SQLException("Unable to update balance table, no row added...");

		}

		// Free up resources.
		rs.close();
		stmt.close();

		// Update funds allocation.
		System.out.println("....Is a specific fund given?....");
		int fundID = queryID("funds", fund);

		// Update by an IncomeItem
		if (incomeId != NULL_ID) {
			if (fundID == NOT_FOUND) {
				// Update all funds according to 'funds_allocation' table.
				// But since this is just a test will do short cut instead :p
				System.out.println("----- Updating All Funds Balance ------\n");
				PhCurrency allocationSum = new PhCurrency();
				for (int i = 0; i < PERCENT_ALLOCATION.length; i++) {
					// Compute the amount to be allocated
					PhCurrency allocation = new PhCurrency(amountDiff);
					allocation.multiplyBy(PERCENT_ALLOCATION[i]);

					// Allocate all the remaining amount for the last fund.
					if (i == PERCENT_ALLOCATION.length - 1)
						allocation.setValue(PhCurrency.differenceAbsolute(amountDiff, allocationSum));

					// Update fund balance.
					int fundsID = queryID("funds", FUNDS[i]);
					updateFund(fundsID, allocation, updateId);

					allocationSum.add(allocation);
				}
				System.out.println("Updating All Funds Balance Done!!!\n");
			}

			else
				updateFund(fundID, amountDiff, updateId);
		}

		// Update by an ExpenseItem
		else {
			if (fundID == NOT_FOUND)
				throw new SQLException(
						"Something went wrong XD, check 'funds_balance' table and make sure that the fund exist...");

			amountDiff.multiplyBy(-1); // updateFund() adds the amount thus a negative value
										// for expense will subtract it from the current amount/balance.
			updateFund(fundID, amountDiff, updateId);
		}

		System.out.println("Successfully updated balance!...............");
	}

	/**
	 * Updates the fund allocation amount when an IncomeItem or ExpenseItem is
	 * logged.
	 * 	
	 * @param fundID          of the fund to be updated.
	 * @param amountDiff      the amount to be added (for IncomeItem) or subtracted
	 *                        (for ExpenseItem). The value of this should be
	 *                        negative if the update is done by an ExpenseItem.
	 * @param balanceUpdateId the ID which relates to the total cash at hand that is
	 *                        updated along with this.
	 * @throws SQLException when trying to update a fund which does not exist; when
	 *                      update is unsuccessful for whatever reason :D
	 */
	void updateFund(int fundID, PhCurrency amountDiff, int balanceUpdateId) throws SQLException {
		// Get the current amount of the fund.
		PhCurrency fundAmt = new PhCurrency();
		Statement stmt = mConnection.createStatement();
		String sql = "SELECT amountX100 FROM funds_balance WHERE fund_id = '" + fundID
				+ "' ORDER BY balance_update_id DESC LIMIT 1;";
		ResultSet rs = stmt.executeQuery(sql);

		if (rs.next()) {
			fundAmt.setValue(rs.getLong(1));
			System.out.println(
					">>> Current fund balance: " + fundAmt.toString() + "\n>>> Amount Diff: " + amountDiff.toString());
			fundAmt.add(amountDiff);
		}

		else if (balanceUpdateId == INITIAL_ID)
			fundAmt.setValue(amountDiff);

		else
			throw new SQLException(
					"Something went wrong XD, check 'funds_balance' table and make sure that the fund exist...");

		// Set the fund allocation details.
		List<SimpleEntry<String, Object>> colValPairs = new ArrayList<>(
				Arrays.asList(new SimpleEntry<>("fund_id", String.valueOf(fundID)),
						new SimpleEntry<>("amountX100", String.valueOf(fundAmt.getAmountX100())),
						new SimpleEntry<>("balance_update_id", String.valueOf(balanceUpdateId))));

		try {
			System.out.println(">>> Updated Amount: " + fundAmt.toString());
			insertThenGetID("funds_balance", colValPairs);

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			throw new SQLException("Unable to update 'funds_balance' table, no row added...");
		}

		// Free up resources.
		rs.close();
		stmt.close();
	}

	/**
	 * Log an IncomeItem to the server
	 * 
	 * @param incomeItem which contains the income details.
	 * @throws SQLException when the IncomeItem has been logged or something went
	 *                      wrong during the logging process for which I am not what
	 *                      it could be :D
	 */
	void logIncome(IncomeItem incomeItem) throws SQLException {

		System.out.println("---------- Adding IncomeItem ----------\n");
		System.out.println("Logging IncomeItem for the date of " + incomeItem.datetime.substring(0, 10) + "...\n");

		// Retrieve the ID of income source from the look-up table.
		// Insert new item if none was found and get it ID.
		int sourceId = queryID("source", incomeItem.source);
		if (sourceId == NOT_FOUND)
			sourceId = insertThenGetID("source", incomeItem.source);

		// Add this entry to the database
		long amount = incomeItem.amount.getAmountX100();
		List<SimpleEntry<String, Object>> colValPairs = new ArrayList<>(Arrays.asList(
				new SimpleEntry<>("datetime", incomeItem.datetime), new SimpleEntry<>("source_id", sourceId),
				new SimpleEntry<>("amountX100", String.valueOf(amount))));

		// Make sure that this IncomeItem has not been logged yet.
		if (queryID("income", colValPairs) != NOT_FOUND)
			throw new SQLException("Item already logged, no row added...");

		int incomeId;
		try {
			System.out.println(">>> IncomeItem Amount: " + incomeItem.amount.toString());
			incomeId = insertThenGetID("income", colValPairs);
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			throw new SQLException("Income not logged..."); 
		}

		// Update the record for the cash at hand and funds allotment.
		updateBalanceAndFunds(incomeItem.amount, incomeId, NULL_ID, incomeItem.fund);

		System.out.println("Successfully logged income item!\n");
	}

	void logExpense(ExpenseItem expense) throws SQLException {
		System.out.println("Logging expense item for the date of " + expense.datetime.substring(0, 10) + "...");

//	    1.) Get the ID of the product from the 'product' table.
//          - If none was found insert this product into the table
//            and get its ID.

		System.out.println("Retrieving product_id\n");

		int productId = queryID("product", expense.item);
		if (productId == NOT_FOUND)
			productId = insertThenGetID("product", expense.item);

//      2.) Get the ID of the tags from the 'tag' table.
//          - If none was found insert the tags into the 'tag' table
//            and get the IDs.

		if (expense.tag != null) {

			System.out.println("Retrieving tag_id\n");

			int[] tagIDs = new int[expense.tag.length]; // to be used for checking if there is
														// a match for this tag and the product
			for (int i = 0; i < expense.tag.length; i++) {
				tagIDs[i] = queryID("tag", expense.tag[i]);
				if (tagIDs[i] == -1)
					tagIDs[i] = insertThenGetID("tag", expense.tag[i]);
			}

//          3.) Check if the tags are matched with the product in the 'product_tags'
//              intermediary table. Add an entry if not found.

			System.out.println("Matching product with tags....");

			Statement stmt = mConnection.createStatement();
			for (int tagID : tagIDs) {

				String sql = "SELECT EXISTS(SELECT 1 FROM product_tags WHERE product_id = '" + productId
						+ "' AND tag_id = '" + tagID + "') LIMIT 1;";
				ResultSet rs = stmt.executeQuery(sql);
				boolean matched = rs.getBoolean(1);

				if (!matched) {
					sql = "INSERT INTO `product_tags`(`product_id`, `tag_id`) VALUES('" + productId + "', '" + tagID
							+ "');";
					System.out.println("No match found....");
					System.out.println(sql);
					if (stmt.executeUpdate(sql) == 0) {
						throw new SQLException("Unable to match product with the tag, no row added...");
					}
				}

				else
					System.out.println("`product_tags` entry value(" + productId + ", " + tagID + ") found!");

				System.out.println();
				rs.close();
			}

			stmt.close();
		}

//      4.) Get the ID of the brand from the 'brand' table.
//          - If none was found insert the brand into the table
//            and get its ID.

		System.out.println("Retrieving brand_id\n");

		int brandId = queryID("brand", expense.brand);
		if (brandId == NOT_FOUND)
			brandId = insertThenGetID("brand", expense.brand);

//     5.) Get the ID for the unit of measurement in the 'unit' table.
//         - If none was found insert the unit of measurement into the 
//           table and get its ID.

		System.out.println("Retrieving unit_id\n");

		int unitId = queryID("unit", expense.unit);
		if (unitId == NOT_FOUND)
			unitId = insertThenGetID("unit", expense.unit);

//      6.) Get the ID for the packaging size with the corresponding unit
//          of measurement in the 'product_size' table.
//          - If none was found insert the packaging size with the 
//            corresponding unit of measurement into the table
//            and get its ID.

		System.out.println("Retrieving product_size_id\n");

		int productSizeId;
		{
			String[] productSizeDetails = getFractionTableDetails(expense.size);
			List<SimpleEntry<String, Object>> colValPair = new ArrayList<>(
					Arrays.asList(new SimpleEntry<>("size", productSizeDetails[0]),
							new SimpleEntry<>("size_txt", productSizeDetails[1]),
							new SimpleEntry<>("unit_id", String.valueOf(unitId))));

			productSizeId = queryID("product_size", colValPair);
			if (productSizeId == NOT_FOUND)
				productSizeId = insertThenGetID("product_size", colValPair);
		}

	//      7.) Get the ID for the product variant with the corresponding
	//          product ID, brand ID, and product size ID in the
	//          'product_variant' table.
//          - If none was found add an entry for it and get its ID.

		System.out.println("Retrieving product_variant_id\n");

		int productVariantId;
		{
			List<SimpleEntry<String, Object>> colValPair = new ArrayList<>(
					Arrays.asList(new SimpleEntry<>("product_id", String.valueOf(productId)),
							new SimpleEntry<>("brand_id", String.valueOf(brandId)),
							new SimpleEntry<>("product_size_id", String.valueOf(productSizeId))));

			productVariantId = queryID("product_variant", colValPair);
			if (productVariantId == NOT_FOUND)
				productVariantId = insertThenGetID("product_variant", colValPair);
		}

//      8.) Get the ID and latest price for this product variant in the
//          'item' table. If the price is different add a new entry with it's
//          current price for this product variant and get its ID.

		System.out.println("Retrieving item_id\n");

		int itemId;
		{

			List<SimpleEntry<String, Object>> colValPair = new ArrayList<>(
					Arrays.asList(new SimpleEntry<>("product_variant_id", String.valueOf(productVariantId)),
							new SimpleEntry<>("priceX100", String.valueOf(expense.unitPrice.getAmountX100()))));

//			For the app, the query should check if the latest previous price is 
//			the same as the current price as follows:
//				"SELECT id FROM item where product_variant_id = '" + productVariantId +
//				"' AND price = '" + expense.unitPrice + "' ORDER BY id DESC LIMIT 1;"

			itemId = queryID("item", colValPair);
			if (itemId == NOT_FOUND) {
				String date = expense.datetime.substring(0, 10); // get the date from the datetime
				colValPair.add(new SimpleEntry<>("date", date));
				itemId = insertThenGetID("item", colValPair);
			}
		}

//      9.) Add new entry with the given item ID, quantity, and remarks if 
//          there is any.

		int expenseId;
		{
			Statement stmt = mConnection.createStatement();
			String sql = "SELECT EXISTS(SELECT 1 FROM expense WHERE datetime = '" + expense.datetime + "') LIMIT 1;";
			ResultSet rs = stmt.executeQuery(sql);
			boolean isLogged = rs.getBoolean(1);

			if (isLogged)
				throw new SQLException("Item already logged, no row added...");

			String[] itemQuantityDetails = getFractionTableDetails(expense.quantity);

			// Add the 'expense' table entry.
			List<SimpleEntry<String, Object>> colValPairs = new ArrayList<>(
					Arrays.asList(new SimpleEntry<>("datetime", expense.datetime),
							new SimpleEntry<>("item_id", String.valueOf(itemId)),
							new SimpleEntry<>("quantity", itemQuantityDetails[0]),
							new SimpleEntry<>("quantity_txt", itemQuantityDetails[1])));

			expenseId = insertThenGetID("expense", colValPairs);

			// Free up resources.
			rs.close();
			stmt.close();
		}

//		10.) Update the 'balance' and 'funds_balance' tables.
		{
			// Get total price of expense item
			double quantity = fractionToDouble(expense.quantity);
			PhCurrency totalPrice = new PhCurrency(expense.unitPrice);
			totalPrice.multiplyBy(quantity);
			System.out.println("Total item price: " + totalPrice.toString() + " (" + expense.unitPrice.toString()
					+ " * " + String.valueOf(quantity) + ")");

			updateBalanceAndFunds(totalPrice, NULL_ID, expenseId, expense.fund);

			System.out.println("Successfully logged expense item!\n\n\n");
		}
	}

	/**
	 * Get the ID of an entry in the table
	 * 
	 * @param table       the table from where the entry is at
	 * @param colValPairs the list of column and value pairs of an entry
	 * @return the id of the entry; NULL_ID if none was found
	 * @throws SQLException
	 */
	int queryID(String table, List<SimpleEntry<String, Object>> colValPairs) throws SQLException {

		int id = NULL_ID;
		Statement stmt = mConnection.createStatement();

		// Build WHERE clause
		String whereClause = "";
		for (int i = 0; i < colValPairs.size(); i++) {
			SimpleEntry<String, Object> colValPair = colValPairs.get(i);

			if (i < colValPairs.size() - 1) {
				whereClause += " `" + colValPair.getKey() + "` = '" + colValPair.getValue() + "' AND";

			} else {
				whereClause += " `" + colValPair.getKey() + "` = '" + colValPair.getValue() + "'";
			}
		}

		String sql = "SELECT `id` FROM `" + table + "` WHERE" + whereClause + ";";
		System.out.println(sql);

		ResultSet rs = stmt.executeQuery(sql);

		if (rs.next()) {
			id = rs.getInt("id"); // assign ID to holding variable to be returned
			System.out.println("ID : " + id + "\n");
		}

		else
			System.out.println("No entry matched....\n");

		stmt.close();
		rs.close();

		return id;
	}

	/**
	 * Get the ID of an entry in a look-up table where there is an ID and a name
	 * column only
	 * 
	 * @param table the table where the entry is at
	 * @param name  the value of `name` column
	 * @return the ID of the entry; -1 if none was found
	 * @throws SQLException
	 */
	int queryID(String table, String name) throws SQLException {

		List<SimpleEntry<String, Object>> colValPair = new ArrayList<>(Arrays.asList(new SimpleEntry<>("name", name)));

		return queryID(table, colValPair);
	}

	/**
	 * Inserts a new entry then returns the ID for that entry
	 * 
	 * @param table       the table where the entry is to be added
	 * @param colValPairs the list of column and value pairs for the new entry
	 * @return the ID of the newly added entry
	 * @throws SQLException
	 */
	int insertThenGetID(String table, List<SimpleEntry<String, Object>> colValPairs) throws SQLException {

		int id = -1;
		Statement stmt = mConnection.createStatement();

		// Build sql
		String col = "";
		String val = "";

		for (int i = 0; i < colValPairs.size(); i++) {
			SimpleEntry<String, Object> colValPair = colValPairs.get(i);
			if (i < colValPairs.size() - 1) {
				col += "`" + colValPair.getKey() + "`, ";
				val += "'" + colValPair.getValue() + "', ";
			} else {
				col += "`" + colValPair.getKey() + "`";
				val += "'" + colValPair.getValue() + "'";
			}
		}

		String sql = "INSERT INTO `" + table + "`(" + col + ") VALUES(" + val + ");";
		System.out.println(sql);
		int affectedRows = stmt.executeUpdate(sql);

		if (affectedRows > 0) {

			// Get the ID
			sql = "SELECT last_insert_rowid() AS id;";
//			System.out.println(sql + "\n");
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				id = rs.getInt("id"); // assign ID to holding variable to be returned
				System.out.println("New `" + table + "` entry inserted successfully with ID: " + id);

			} else {
				throw new SQLException("Entry addition failed, no ID obtained.");
			}

			System.out.println();
			rs.close();

		} else {
			throw new SQLException("Entry addition failed, no rows affected.");
		}

		stmt.close();

		return id;
	}

	/**
	 * Inserts a new entry in a look-up table where there is ID and name column only
	 * 
	 * @param conn  the Connection object to connect with the database
	 * @param table the table where the entry is to be added
	 * @param name  the value of the name column
	 * @return the ID of the newly added entry
	 * @throws SQLException
	 */
	int insertThenGetID(String table, String name) throws SQLException {

		List<SimpleEntry<String, Object>> colValPair = new ArrayList<>(Arrays.asList(new SimpleEntry<>("name", name)));

		return insertThenGetID(table, colValPair);
	}

	private String[] getFractionTableDetails(String fraction) throws SQLException {
		String fractionTableDetails[] = new String[2];

		// Check if the given product size is a fraction and get the corresponding id
		// from the
		// look-up table, "NULL" otherwise.
		LoggerFraction fractionalSize = null;
		try {
			fractionalSize = new LoggerFraction(fraction);
			fractionTableDetails[0] = String.valueOf(fractionalSize.toDouble());

			List<SimpleEntry<String, Object>> colValPair = new ArrayList<>(
					Arrays.asList(new SimpleEntry<>("val_as_txt", fractionTableDetails[0].toString())));

			int fractionId = queryID("fraction_text", colValPair);
			if (fractionId == NULL_ID)
				fractionId = insertThenGetID("fraction_text", colValPair);

			fractionTableDetails[1] = String.valueOf(fractionId);
		}

		catch (IllegalArgumentException iae) {
			fractionTableDetails[0] = fraction;
			fractionTableDetails[1] = "NULL";
		}

		return fractionTableDetails;
	}

	/**
	 * Convert a string fraction representation into a double.
	 * 
	 * @param ratio the string to be parsed into a float.
	 * @return the equivalent value in double.
	 */
	static double fractionToDouble(String ratio) {
		double decValue = 0;

		if (ratio.contains("/")) {
			String[] rat = ratio.split("/");

			try {
				decValue = Double.parseDouble(rat[0]) / Double.parseDouble(rat[1]);

			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
			return decValue;
		}

		else {
			try {
				decValue = Double.parseDouble(ratio);

			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}

		return decValue;
	}
}
