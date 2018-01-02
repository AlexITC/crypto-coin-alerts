
# --- !Ups

CREATE TABLE currencies(
  currency_id SERIAL NOT NULL,
  exchange VARCHAR(20) NOT NULL, -- BITSO, BITTREX, etc
  market VARCHAR(10) NOT NULL, -- BTC, ETH, etc
  currency VARCHAR(10) NOT NULL, -- ADA, XMR, etc
  deleted_on TIMESTAMPTZ NULL DEFAULT NULL, -- when the exchange removed support for this currency
  -- constraints
  CONSTRAINT currencies_currency_id_pk PRIMARY KEY (currency_id),
  CONSTRAINT currencies_unique UNIQUE (exchange, market, currency),
  CONSTRAINT currencies_exchange_format CHECK (exchange ~ '^[A-Z]{3,20}$'),
  CONSTRAINT currencies_market_format CHECK (market ~ '^[A-Z0-9]{2,10}$'),
  CONSTRAINT currencies_currency_format CHECK (currency ~ '^[A-Z0-9]{2,10}$')
);


# --- !Downs

DROP TABLE currencies;
