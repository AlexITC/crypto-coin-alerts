
# --- !Ups

-- enable psql extensions
CREATE EXTENSION IF NOT EXISTS CITEXT;

CREATE TABLE users(
  user_id VARCHAR(40) NOT NULL,
  email CITEXT NOT NULL,
  password VARCHAR(64) NOT NULL,
  created_on TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  -- constraints
  CONSTRAINT users_user_id_pk PRIMARY KEY (user_id),
  CONSTRAINT users_email_is_unique UNIQUE (email),
  CONSTRAINT users_email_is_not_empty CHECK (email <> ''),
  CONSTRAINT users_password_is_not_empty CHECK (password <> '')
);
CREATE INDEX users_created_on_index ON users USING BTREE (created_on);

-- Represents a market like BITTREX, POLONIEX, BITSO
CREATE TABLE markets(
  market_id SERIAL NOT NULL,
  name CITEXT NOT NULL,
  -- constraints
  CONSTRAINT markets_market_id_pk PRIMARY KEY (market_id),
  CONSTRAINT markets_name_is_not_empty CHECK (name <> ''),
  CONSTRAINT markets_name_is_unique UNIQUE (name)
);

-- A fixed-price alert for a book in the given market
CREATE TABLE alerts(
  alert_id BIGSERIAL NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  created_on TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  book VARCHAR(10) NOT NULL, -- MXN_BTC
  market_id INT NOT NULL,
  is_greater_than BOOLEAN NOT NULL, -- FALSE means lower than
  price DECIMAL(16, 10) NOT NULL,
  triggered_on TIMESTAMP WITH TIME ZONE NULL,
  -- constraints
  CONSTRAINT alerts_alert_id_pk PRIMARY KEY (alert_id),
  CONSTRAINT alerts_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT alerts_market_id_fk FOREIGN KEY (market_id) REFERENCES markets(market_id),
  CONSTRAINT alerts_price_greater_than_0 CHECK(price > 0)
);

-- base_price means the price that the user adquired the coin, helps to give personalized messages
CREATE TABLE base_price_alerts(
  alert_id BIGINT NOT NULL,
  base_price DECIMAL(16, 10) NOT NULL,
  -- constraints
  CONSTRAINT base_price_alerts_alert_id_pk PRIMARY KEY (alert_id),
  CONSTRAINT base_price_alerts_alert_id_fk FOREIGN KEY (alert_id) REFERENCES alerts(alert_id),
  CONSTRAINT base_price_alerts_base_price_greater_than_0 CHECK(base_price > 0)
);

CREATE INDEX alerts_user_id_index ON alerts USING BTREE (user_id); -- check alerts by user
CREATE INDEX alerts_created_on_index ON alerts USING BTREE (created_on); -- order alerts by date
CREATE INDEX alerts_triggered_on_index ON alerts USING BTREE (triggered_on); -- check active alerts
CREATE INDEX alerts_price_index ON alerts USING BTREE (price); -- trigger alert by price


# --- !Downs

DROP TABLE base_price_alerts;
DROP TABLE alerts;
DROP TABLE markets;
DROP TABLE users;
