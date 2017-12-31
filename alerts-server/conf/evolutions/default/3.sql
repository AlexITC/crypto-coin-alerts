
# --- !Ups

CREATE TABLE fixed_price_alerts(
  fixed_price_alert_id BIGSERIAL NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  currency_id INT NOT NULL,
  is_greater_than BOOLEAN NOT NULL, -- FALSE means lower than
  price DECIMAL(16, 10) NOT NULL,
  base_price DECIMAL(16, 10) NULL, -- means the price when the coin was adquired
  created_on TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  triggered_on TIMESTAMP WITH TIME ZONE NULL DEFAULT NULL,
  -- constraints
  CONSTRAINT fixed_price_alerts_alert_id_pk PRIMARY KEY (fixed_price_alert_id),
  CONSTRAINT fixed_price_alerts_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fixed_price_alerts_currency_id_fk FOREIGN KEY (currency_id) REFERENCES currencies(currency_id),
  CONSTRAINT fixed_price_alerts_price_greater_than_0 CHECK(price > 0),
  CONSTRAINT fixed_price_alerts_base_price_greater_than_0 CHECK(base_price > 0)
);

CREATE INDEX fixed_price_alerts_user_id_index ON fixed_price_alerts USING BTREE (user_id); -- check alerts by user
CREATE INDEX fixed_price_alerts_created_on_index ON fixed_price_alerts USING BTREE (created_on); -- order alerts by date
CREATE INDEX fixed_price_alerts_triggered_on_index ON fixed_price_alerts USING BTREE (triggered_on); -- check active alerts
CREATE INDEX fixed_price_alerts_price_index ON fixed_price_alerts USING BTREE (price); -- trigger alert by price


# --- !Downs

DROP TABLE fixed_price_alerts;
