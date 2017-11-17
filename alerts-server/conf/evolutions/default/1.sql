
# --- !Ups

-- enable psql extensions
CREATE EXTENSION IF NOT EXISTS CITEXT;

CREATE TABLE users(
  user_id VARCHAR(40) NOT NULL,
  email CITEXT NOT NULL,
  password VARCHAR(64) NOT NULL,
  created_on TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  verified_on TIMESTAMP WITH TIME ZONE NULL,
  -- constraints
  CONSTRAINT users_user_id_pk PRIMARY KEY (user_id),
  CONSTRAINT users_email_is_unique UNIQUE (email),
  CONSTRAINT users_email_is_not_empty CHECK (email <> ''),
  CONSTRAINT users_password_is_not_empty CHECK (password <> ''),
  CONSTRAINT verified_on_is_after_created_on CHECK (verified_on > created_on)
);
CREATE INDEX users_email_index ON users USING BTREE (email);
CREATE INDEX users_created_on_index ON users USING BTREE (created_on);


CREATE TABLE user_verification_tokens(
  user_id VARCHAR(40) NOT NULL,
  token VARCHAR(150) NOT NULL,
  requested_on TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  -- constraints
  CONSTRAINT user_verification_tokens_user_id_pk PRIMARY KEY (user_id),
  CONSTRAINT user_verification_tokens_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT user_verification_tokens_token_is_not_empty CHECK (token <> ''),
  CONSTRAINT user_verification_tokens_token_is_unique UNIQUE (token)
);
CREATE INDEX user_verification_tokens_token_index ON user_verification_tokens USING BTREE (TOKEN);


CREATE TABLE user_preferences(
  user_id VARCHAR(40) NOT NULL,
  lang VARCHAR(10) NOT NULL DEFAULT 'en',
  -- constraint
  CONSTRAINT user_preferences_user_id_pk PRIMARY KEY (user_id),
  CONSTRAINT user_preferences_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT user_preferences_lang_length CHECK (char_length(lang) >= 2)
);

# --- !Downs

DROP TABLE user_preferences;
DROP TABLE user_verification_tokens;
DROP TABLE users;
