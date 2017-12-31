
# --- !Ups

CREATE TABLE daily_price_alerts(
  daily_price_alert_id BIGSERIAL NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  currency_id INT NOT NULL,
  created_on TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  -- constraints
  CONSTRAINT daily_price_alerts_alert_id_pk PRIMARY KEY (daily_price_alert_id),
  CONSTRAINT daily_price_alerts_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT daily_price_alerts_currency_id_fk FOREIGN KEY (currency_id) REFERENCES currencies(currency_id),
  CONSTRAINT daily_price_alerts_unique UNIQUE (user_id, currency_id)
);

CREATE INDEX daily_price_alerts_user_id_index ON daily_price_alerts USING BTREE (user_id); -- check alerts by user


# --- !Downs

DROP TABLE daily_price_alerts;
