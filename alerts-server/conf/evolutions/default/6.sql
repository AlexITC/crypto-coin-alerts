-- turns out there are currencies represented by 1 character only, like Revain (R).

# --- !Ups

ALTER TABLE currencies DROP CONSTRAINT currencies_currency_format;
ALTER TABLE currencies ADD CONSTRAINT currencies_currency_format CHECK (currency ~ '^[A-Z0-9]{1,10}$')

# --- !Downs

ALTER TABLE currencies DROP CONSTRAINT currencies_currency_format;
ALTER TABLE currencies ADD CONSTRAINT currencies_currency_format CHECK (currency ~ '^[A-Z0-9]{2,10}$')
