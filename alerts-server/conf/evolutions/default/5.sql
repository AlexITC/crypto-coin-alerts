
# --- !Ups

CREATE TABLE new_currency_alerts(
  new_currency_alert_id SERIAL NOT NULL,
  user_id VARCHAR(40) NOT NULL,
  exchange VARCHAR(20) NOT NULL,
  -- constraints
  CONSTRAINT new_currency_alerts_alert_id_pk PRIMARY KEY (new_currency_alert_id),
  CONSTRAINT new_currency_alerts_user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT new_currency_alerts_exchange_format CHECK (exchange ~ '^[A-Z]{3,20}$'),
  CONSTRAINT new_currency_alerts_unique UNIQUE (user_id, exchange)
);

CREATE INDEX new_currency_alerts_user_id_index ON new_currency_alerts USING BTREE (user_id);


# --- !Downs

DROP TABLE new_currency_alerts;
