package cashFlowLoggerDBTest;

import java.sql.*;

public class CashFlowLoggerDBTest {

	static final String DB_URL = "jdbc:sqlite:cash_flow_logger.db";

	private static IncomeItem[] incomeRecord = {
			new IncomeItem("2019-02-05 17:00:00", "Youtube", new PhCurrency(12120.00)),
			new IncomeItem("2019-02-15 17:00:00", "Salary", new PhCurrency(11500.00)),
			new IncomeItem("2019-02-20 17:00:00", "TuloyPoKayo site", new PhCurrency(3000.00)),
			new IncomeItem("2019-02-28 17:00:00", "Salary", new PhCurrency(12200.00)) };

	private static ExpenseItem[] expensesRecord = {
//			datetime, item, brand, unitPrice, size, unit, quantity, tag
//			February 01
			new ExpenseItem("2019-02-01 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter",
					"2", Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-01 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-01 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-01 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 02
			new ExpenseItem("2019-02-02 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-02 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-02 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-02 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 03
			new ExpenseItem("2019-02-03 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-03 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-03 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-03 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 04
			new ExpenseItem("2019-02-04 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-04 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-04 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-04 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 05
			new ExpenseItem("2019-02-05 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-05 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-05 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-05 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 06
			new ExpenseItem("2019-02-06 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "1",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-06 07:05:10", "Bangus", "NHA", new PhCurrency(180.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-06 07:20:10", "Tulingan", "NHA", new PhCurrency(140.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-06 07:25:10", "Baboy(Kasim)", "NHA", new PhCurrency(220.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "meat" }),
			new ExpenseItem("2019-02-06 07:30:10", "Repolyo", "NHA", new PhCurrency(80.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetable" }),
			new ExpenseItem("2019-02-06 07:32:10", "Patatas", "NHA", new PhCurrency(90.00), "1", "kilo", "1/4",
					Logger.FUNDS[0], new String[] { "food", "vegetable" }),
			new ExpenseItem("2019-02-06 07:34:10", "Ampalaya", "NHA", new PhCurrency(80.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "vegetable" }),
			new ExpenseItem("2019-02-06 07:36:10", "Kamatis", "NHA", new PhCurrency(80.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetable" }),
			new ExpenseItem("2019-02-06 07:38:10", "Sibuyas", "NHA", new PhCurrency(80.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetable" }),
			new ExpenseItem("2019-02-06 07:40:10", "Bawang", "NHA", new PhCurrency(100.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetable" }),
			new ExpenseItem("2019-02-06 07:42:12", "Palamig(Buko)", "NHA", new PhCurrency(10.00), "1", "cup", "1",
					Logger.FUNDS[0], new String[] { "food", "drinks" }),
//			February 07
			new ExpenseItem("2019-02-07 08:35:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "1",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-06 09:05:10", "Palamig(Pinya)", "Calauan", new PhCurrency(10.00), "1", "cup", "1",
					Logger.FUNDS[0], new String[] { "food", "drinks" }),
			new ExpenseItem("2019-02-06 09:15:10", "Bigas", "Calauan", new PhCurrency(1200.00), "25", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 08
			new ExpenseItem("2019-02-08 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-08 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-08 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-08 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 09
			new ExpenseItem("2019-02-09 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-09 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-09 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-09 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 10
			new ExpenseItem("2019-02-10 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-10 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-10 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-10 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 11
			new ExpenseItem("2019-02-11 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-11 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-11 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-11 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 12
			new ExpenseItem("2019-02-12 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-12 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-12 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-12 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
// 			February 13
			new ExpenseItem("2019-02-13 08:35:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "1",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-13 08:40:10", "LPG", "Solane", new PhCurrency(695.00), "11", "kilo", "1",
					Logger.FUNDS[0]),
			new ExpenseItem("2019-02-13 10:30:10", "Niyog", "Aling Jing", new PhCurrency(10.00), "1", "piece", "10",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
//			February 14
			new ExpenseItem("2019-02-14 08:15:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-14 09:25:10", "Internet Broadband", "Globe", new PhCurrency(1299.00), "1", "month", "1",
					Logger.FUNDS[0], new String[] { "utilities", "telecom" }),
			new ExpenseItem("2019-02-14 09:35:10", "Gabi", "SM", new PhCurrency(105.00), "1", "kilo", "0.86",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-14 10:05:10", "Manok(Hita)", "San Pablo", new PhCurrency(160.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "meat" }),
			new ExpenseItem("2019-02-14 10:08:10", "Tahong", "San Pablo", new PhCurrency(70.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "shellfish" }),
			new ExpenseItem("2019-02-14 10:10:10", "Galunggong", "San Pablo", new PhCurrency(150.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-14 10:12:10", "Manok(Paa)", "San Pablo", new PhCurrency(30.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "meat" }),
			new ExpenseItem("2019-02-14 10:16:10", "Alamang", "San Pablo", new PhCurrency(120.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "shellfish" }),
			new ExpenseItem("2019-02-14 10:18:10", "Saytoe", "San Pablo", new PhCurrency(25.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-14 10:20:10", "Sili(Pangsigang)", "San Pablo", new PhCurrency(110.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-14 10:24:10", "Palamig(Lemon)", "San Pablo", new PhCurrency(10.00), "1", "cup", "1",
					Logger.FUNDS[0], new String[] { "food", "drinks" }),
			new ExpenseItem("2019-02-14 10:28:10", "Kamatis", "San Pablo", new PhCurrency(80.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
//			February 15
			new ExpenseItem("2019-02-15 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-15 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-15 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-15 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 16
			new ExpenseItem("2019-02-16 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-16 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-16 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-16 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 17
			new ExpenseItem("2019-02-17 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-17 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-17 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-17 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 18
			new ExpenseItem("2019-02-18 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-18 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-18 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-18 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 19
			new ExpenseItem("2019-02-19 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-19 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-19 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-19 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 20
			new ExpenseItem("2019-02-20 08:25:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "1",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-20 08:45:10", "Baboy(Buto-buto)", "Calauan", new PhCurrency(190.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "meat" }),
			new ExpenseItem("2019-02-20 08:48:10", "Tilapia", "Calauan", new PhCurrency(120.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-20 08:50:10", "Tambakol", "Calauan", new PhCurrency(180.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-20 08:56:10", "Toge", "Calauan", new PhCurrency(10.00), "1", "pack", "1",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-20 08:58:10", "Tokwa", "Calauan", new PhCurrency(5.00), "1", "piece", "6",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-20 09:00:10", "Kangkong", "Calauan", new PhCurrency(5.00), "1", "bundle", "2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-20 09:02:10", "Kamatis", "Calauan", new PhCurrency(80.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-20 09:04:10", "Talong", "Calauan", new PhCurrency(80.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-20 09:06:10", "Sibuyas", "Calauan", new PhCurrency(80.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-20 09:08:10", "Bawang", "Calauan", new PhCurrency(100.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
//			February 21
			new ExpenseItem("2019-02-21 07:35:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "1",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-21 09:15:10", "Palamig(Melon)", "Calauan", new PhCurrency(10.00), "1", "cup", "1",
					Logger.FUNDS[0], new String[] { "food", "drinks" }),
			new ExpenseItem("2019-02-21 09:25:10", "Assorted Biscuit(Local)", "Calauan", new PhCurrency(5.00), "1", "pack", "20",
					Logger.FUNDS[0], new String[] { "food", "bread" }),
//			February 22
			new ExpenseItem("2019-02-22 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-22 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-22 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-22 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 23
			new ExpenseItem("2019-02-23 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-23 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-23 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-23 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 24
			new ExpenseItem("2019-02-24 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-24 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-24 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-24 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 25
			new ExpenseItem("2019-02-25 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-25 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-25 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-25 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 26
			new ExpenseItem("2019-02-26 05:05:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "2",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-26 09:05:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-26 12:05:10", "Lunch at Canteen", "n/a", new PhCurrency(65.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
			new ExpenseItem("2019-02-26 15:35:10", "Merienda at Canteen", "n/a", new PhCurrency(15.00), "1", "order", "1",
					Logger.FUNDS[0], new String[] { "food" }),
//			February 27
			new ExpenseItem("2019-02-27 07:45:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter", "1",
					Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-27 08:00:10", "Manok(Hita)", "NHA", new PhCurrency(160.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "meat" }),
			new ExpenseItem("2019-02-27 08:02:10", "Manok(Atay Balonan)", "NHA", new PhCurrency(150.00), "1", "kilo", "1/2",
					Logger.FUNDS[0], new String[] { "food", "meat" }),
			new ExpenseItem("2019-02-27 08:04:10", "Bangus", "NHA", new PhCurrency(160.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-27 08:06:10", "Tulingan", "NHA", new PhCurrency(140.00), "1", "kilo", "1",
					Logger.FUNDS[0], new String[] { "food", "fish" }),
			new ExpenseItem("2019-02-27 08:08:10", "Puso ng Saging", "NHA", new PhCurrency(10.00), "1", "piece", "2",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
			new ExpenseItem("2019-02-27 08:10:10", "Upo", "NHA", new PhCurrency(60.00), "1", "kilo", "3/4",
					Logger.FUNDS[0], new String[] { "food", "vegetables" }),
//			February 28
			new ExpenseItem("2019-02-28 08:45:10", "Gasoline", "Shell Unleaded 91", new PhCurrency(43.00), "1", "liter",
					"1", Logger.FUNDS[0], new String[] { "transportation" }),
			new ExpenseItem("2019-02-28 09:15:10", "Mantika", "Baguio Orchids", new PhCurrency(160.00), "2", "liter", "1",
					Logger.FUNDS[0], new String[] { "food", "condiment" }),
			new ExpenseItem("2019-02-28 09:16:10", "Toyo", "Datu Puti", new PhCurrency(143.75), "1", "gallon", "1",
					Logger.FUNDS[0], new String[] { "food", "condiment" }),
			new ExpenseItem("2019-02-28 09:17:10", "Suka", "Datu Puti", new PhCurrency(125.00), "1", "gallon", "1",
					Logger.FUNDS[0], new String[] { "food", "condiment" }) };

	public static void main(String[] args) {

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DB_URL);
			System.out.println("Opened database successfully \n");
			stmt = conn.createStatement();
			conn.setAutoCommit(false);

			// Enable foreign key support
			stmt.execute("PRAGMA foreign_keys = ON;");

			Logger logger = new Logger(conn);

			logger.initializeDB();

			System.out.println("~~~~~~~~~~~~~~~~~ Adding Income Record ~~~~~~~~~~~~~~~~\n");
			for (IncomeItem incomeItem : incomeRecord) {
				logger.logIncome(incomeItem);
			}
			System.out.println("Adding Income Record Done!!!\n");

			System.out.println("~~~~~~~~~~~~~~~~~ Adding Expense Record ~~~~~~~~~~~~~~~~\n");
			for (ExpenseItem expense : expensesRecord) {
				logger.logExpense(expense);
			}
			System.out.println("Adding Expense Record Done!!!\n");

			conn.commit();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
			System.out.println("SQLException");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.out.println("Exception");
			System.exit(0);

		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();

			} catch (SQLException se2) {
			} // nothing we can do

			try {
				if (conn != null)
					conn.close();

			} catch (SQLException se) {
				se.printStackTrace();

			} // end finally try
		} // end try

		System.out.println("Operation done successfully");
	}

}
