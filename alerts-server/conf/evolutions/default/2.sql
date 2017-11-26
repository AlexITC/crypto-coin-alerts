
# --- !Ups

-- A fixed-price alert for a book in the given market
CREATE TABLE fixed_price_alerts(
  alert_id BIGSERIAL NOT NULL,
  alert_type VARCHAR(20) NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  market VARCHAR(20) NOT NULL, -- BITSO, BITTREX, etc
  book VARCHAR(10) NOT NULL, -- BTC_MXN, BTC_ETH, etc
  is_greater_than BOOLEAN NOT NULL, -- FALSE means lower than
  price DECIMAL(16, 10) NOT NULL,
  base_price DECIMAL(16, 10) NULL, -- means the price when the coin was adquired
  created_on TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  triggered_on TIMESTAMP WITH TIME ZONE NULL DEFAULT NULL,
  -- constraints
  CONSTRAINT fixed_price_alerts_alert_id_pk PRIMARY KEY (alert_id),
  CONSTRAINT fixed_price_alerts_alert_type_is_not_empty CHECK(alert_type <> ''),
  CONSTRAINT fixed_price_alerts_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fixed_price_alerts_market_is_formatted_properly CHECK(market ~ '^[A-Z]{3,20}$'),
  CONSTRAINT fixed_price_alerts_book_is_formatted_properly CHECK(book ~ '^[A-Z0-9]{3,8}_[A-Z0-9]{3,8}$'),
  CONSTRAINT fixed_price_alerts_greater_than_0 CHECK(price > 0),
  CONSTRAINT fixed_price_alerts_base_price_greater_than_0 CHECK(base_price > 0)
);

CREATE INDEX alerts_user_id_index ON fixed_price_alerts USING BTREE (user_id); -- check alerts by user
CREATE INDEX alerts_created_on_index ON fixed_price_alerts USING BTREE (created_on); -- order alerts by date
CREATE INDEX alerts_triggered_on_index ON fixed_price_alerts USING BTREE (triggered_on); -- check active alerts
CREATE INDEX alerts_price_index ON fixed_price_alerts USING BTREE (price); -- trigger alert by price

# --- !Downs

DROP TABLE fixed_price_alerts;
