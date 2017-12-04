
# --- !Ups

-- A daily-price alert for a book in the given market
CREATE TABLE daily_price_alerts(
  daily_price_alert_id BIGSERIAL NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  market VARCHAR(20) NOT NULL, -- BITSO, BITTREX, etc
  book VARCHAR(10) NOT NULL, -- BTC_MXN, BTC_ETH, etc
  created_on TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  -- constraints
  CONSTRAINT daily_price_alerts_alert_id_pk PRIMARY KEY (daily_price_alert_id),
  CONSTRAINT daily_price_alerts_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT daily_price_alerts_market_is_formatted_properly CHECK(market ~ '^[A-Z]{3,20}$'),
  CONSTRAINT daily_price_alerts_book_is_formatted_properly CHECK(book ~ '^[A-Z0-9]{3,8}_[A-Z0-9]{3,8}$'),
  CONSTRAINT daily_price_alerts_unique UNIQUE (user_id, market, book)
);

CREATE INDEX daily_price_alerts_user_id_index ON daily_price_alerts USING BTREE (user_id); -- check alerts by user

# --- !Downs

DROP TABLE daily_price_alerts;
