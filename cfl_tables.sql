  -- Cash Flow Logger Schema (SQLite DB)
  DROP TABLE IF EXISTS fraction_text;
  DROP TABLE IF EXISTS balance;
  DROP TABLE IF EXISTS funds;
  DROP TABLE IF EXISTS funds_allocation;
  DROP TABLE IF EXISTS funds_balance;
  DROP TABLE IF EXISTS source;
  DROP TABLE IF EXISTS income;
  DROP TABLE IF EXISTS product;
  DROP TABLE IF EXISTS tag;
  DROP TABLE IF EXISTS product_tags;
  DROP TABLE IF EXISTS brand;
  DROP TABLE IF EXISTS unit;
  DROP TABLE IF EXISTS product_size;
  DROP TABLE IF EXISTS product_variant;
  DROP TABLE IF EXISTS item;
  DROP TABLE IF EXISTS expense;


  CREATE TABLE fraction_text(
    -- Look-up table for text representation of fractional values entered by the user.
    -- This will be used to avoid confusion in redisplaying values entered by the user
    -- since the equivalent values are stored as float.
    id         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    val_as_txt TEXT NOT NULL UNIQUE);

  CREATE TABLE balance(
    -- Records the cash at hand.
    -- Updated everytime an income/expense entry is added
    id                INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,
    amountX100        INTEGER  NOT NULL,    -- Amount in Php times 10 to include centavo.
    income_update_id  INTEGER,              -- ID of income entry that is added to the balance.
    expense_update_id INTEGER,              -- ID of expense entry that is deducted from the balance.
    FOREIGN KEY(income_update_id) REFERENCES income(id),
    FOREIGN KEY(expense_update_id) REFERENCES expense(id),              -- Either income or expense
    CHECK((income_update_id != NULL AND expense_update_id == NULL) OR   -- should have value at a time
          (income_update_id == NULL AND expense_update_id != NULL)));   -- but not both.

  CREATE TABLE funds(
    -- Look-up table of all declared funds.
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);


  CREATE TABLE funds_allocation(
    -- Stores how the income is to be divided amongst the fund (in percent).
    -- This will also serve as how the allocation of fund changes over time.
    -- Unused fund will have an allocation of 0%.
    _date              DATE     DEFAULT CURRENT_DATE,
    fund_id            INTEGER  NOT NULL,
    percent_allocation INTEGER  NOT NULL,
    FOREIGN KEY(fund_id) REFERENCES funds(id),
    PRIMARY KEY(_date, fund_id));


  CREATE TABLE funds_balance(
    -- Records the current balance for each fund.
    -- Fund amount is increased as per fund allocation percentage when
    -- income is added.
    -- Expense amount is subtracted to the corresponding fund where it
    -- is to be deducted from.
    fund_id           INTEGER NOT NULL,
    balance_update_id INTEGER NOT NULL,   -- References which cuased the update.
    amountX100        INTEGER NOT NULL,   -- Amount in Php times 10 to include centavo.
    FOREIGN KEY(balance_update_id) REFERENCES balance(id),
    PRIMARY KEY(fund_id, balance_update_id));


  CREATE TABLE source(
    -- Source of income(e.g. commission, royalties, salary)
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);


  CREATE TABLE income( 
    -- Income record
    id          INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,
    datetime    DATETIME DEFAULT CURRENT_TIMESTAMP,
    source_id   INTEGER  NOT NULL,
    amountX100  INTEGER  NOT NULL,   -- Amount in Php times 10 to include centavo.
    FOREIGN KEY(source_id) REFERENCES source(id));


  CREATE TABLE product(
    -- Look_up table of product name/type(e.g. salt, shirt, internet)
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);


  CREATE TABLE tag(
    -- Look-up table of tags/categories of products as specified by the user.
    -- This will be used for searching a particular category of items for
    -- price comparison or analyzing the expense patern for that category.
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);


  CREATE TABLE product_tags(
    -- Intermediary table for products and tags since a product can have
    -- multiple tags.
    product_id INTEGER NOT NULL,
    tag_id     INTEGER NOT NULL,
    FOREIGN KEY(product_id) REFERENCES product(id),
    FOREIGN KEY(tag_id)     REFERENCES tag(id),
    PRIMARY KEY(product_id, tag_id));


  CREATE TABLE brand(
    -- Look-up table for product brands
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);


  CREATE TABLE unit(
    -- Look-up table for unit of measurement
    id   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL UNIQUE COLLATE NOCASE);


  CREATE TABLE product_size(
   -- Look-up table of packaging/serving size of products
   id       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   size     REAL    NOT NULL,
   size_txt INTEGER,          -- ID from look-up table fraction_text.
   unit_id  INTEGER NOT NULL,
   FOREIGN KEY(size_txt) REFERENCES fraction_text(id),
   FOREIGN KEY(unit_id) REFERENCES unit(id),
   CONSTRAINT package_size UNIQUE(size, unit_id));


  CREATE TABLE product_variant(
    -- List of product for each brand and package size
    id              INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    product_id      INTEGER NOT NULL,
    brand_id        INTEGER NOT NULL,
    product_size_id INTEGER NOT NULL,
    FOREIGN KEY(product_id)      REFERENCES product(id),
    FOREIGN KEY(brand_id)        REFERENCES brand(id),
    FOREIGN KEY(product_size_id) REFERENCES product_size(id),
    CONSTRAINT product_variant  UNIQUE(product_id, brand_id, product_size_id));


  CREATE TABLE item(
    -- Purchased item defined as the combination of a particular product and
    -- its price at a certain date. It will serve as a record of the change
    -- in price over time of a product.
    id                 INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,
    date               DATE     DEFAULT CURRENT_DATE,
    product_variant_id INTEGER  NOT NULL,
    priceX100          INTEGER  NOT NULL,   -- Price in Php times 10 to include centavo.
    FOREIGN KEY(product_variant_id) REFERENCES product_variant(id));


  CREATE TABLE expense(
    -- Expenses record
    id            INTEGER  PRIMARY KEY AUTOINCREMENT,
    datetime      DATETIME DEFAULT CURRENT_TIMESTAMP,
    item_id       INTEGER  NOT NULL,
    quantity      REAL     NOT NULL,
    quantity_txt  INTEGER,          -- ID from look-up table fraction_text.
    remarks       TEXT,
    FOREIGN KEY(quantity_txt) REFERENCES fraction_text(id),
    FOREIGN KEY(item_id) REFERENCES item(id));
