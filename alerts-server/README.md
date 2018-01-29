# Crypto Coin Alerts Server API
[![Build Status](https://travis-ci.org/AlexITC/crypto-coin-alerts.svg?branch=master)](https://travis-ci.org/AlexITC/crypto-coin-alerts) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/30e29dfe2d97459e8ceb12a4dd72f292)](https://www.codacy.com/app/AlexITC/crypto-coin-alerts?utm_source=github.com&utm_medium=referral&utm_content=AlexITC/crypto-coin-alerts&utm_campaign=Badge_Coverage) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/30e29dfe2d97459e8ceb12a4dd72f292)](https://www.codacy.com/app/AlexITC/crypto-coin-alerts?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlexITC/crypto-coin-alerts&amp;utm_campaign=Badge_Grade)

## Description
The alerts server API is built relaying on the following principal technologies:
- [Scala](https://www.scala-lang.org/).
- [PostgreSQL](https://www.postgresql.org/).
- [Play Framework](https://playframework.com/).
- [Anorm](https://github.com/playframework/anorm).
- [Scalactic](http://scalactic.org/).
- [Mailgun](https://www.mailgun.com/) - for sending emails.
- [Docker](https://www.docker.com/) - for testing only.
- [docker-it-scala](https://github.com/whisklabs/docker-it-scala) - for testing only.

At the moment it is supporting the following currency exchanges, more exchanges might be added in the future:
- [Binance](https://www.binance.com/).
- [Bitso](https://bitso.com/?l=en).
- [Bittrex](https://bittrex.com/).
- [HitBTC](https://hitbtc.com/).
- [KuCoin](https://www.kucoin.com/).


The following alerts are supported while more alert types should be added soon:
- Fixed price - Receive a notification when a currency gets above or below the given price, you can set a base price to receive a more detailed message.
- Daily price - Receive a daily notification with the price of the currencies of your choice (currently disabled).
- New currencies - Receive a notification when a currency is addded to the exchanges of your choice.

## Compile
Execute `sbt compile` command for compiling the application.

## Run
In order to run the application locally, you will need the following dependencies:
- A PostgreSQL instance (strictly necessary).
- A Mailgun API key (necessary for sending emails).

There are some environment specific configuration values that you need to set to run the application successfully, the recommended way is to create a file `.env` (which is ignored by git to avoid you pushing sensitive data by accident)), then take the following content and update it according to your needs:
```bash
# database
export CRYPTO_COIN_ALERTS_PSQL_HOST="localhost:5432"
export CRYPTO_COIN_ALERTS_PSQL_DATABASE="crypto_coin_alerts"
export CRYPTO_COIN_ALERTS_PSQL_USERNAME="postgres"
export CRYPTO_COIN_ALERTS_PSQL_PASSWORD="password"
# from mailgun
export MAILGUN_API_SECRET_KEY="REPLACE_ME"
export MAILGUN_DOMAIN="www.cryptocoinalerts.net"
```

Execute `source .env; sbt run` command for running the application.

A more flexible way would be to modify [application.conf](conf/application.conf) file to your needs and then execute `sbt run` command.

## Testing
In order to run the tests execute `sbt test` command, note that Database tests depend on docker to run.

Most of the tests belong to one of these categories:
- Simple tests.
- Database tests
- API tests.

### Simple tests
A simple test is like a relaxed unit test, it tests a single method without mocking external libraries, for example, see [JWTServiceSpec](test/com/alexitc/coinalerts/services/JWTServiceSpec.scala).

### Database tests
A database test is basically an integration test that ensures that a data handler is working as expected, it uses a real PostgreSQL instance provided by [Docker](https://www.docker.com/) with the help of [docker-it-scala](https://github.com/whisklabs/docker-it-scala) library.

The postgres image is created before running the test, we apply all the play evolutions to have the schema updated, and the image is destroyed after running the test.

All of these tests are extending [PostgresDataHandlerSpec](test/com/alexitc/coinalerts/commons/PostgresDataHandlerSpec.scala), for example, see [UserPostgresDataHandlerSpec](test/com/alexitc/coinalerts/data/anorm/UserPostgresDataHandlerSpec.scala).

### API tests
An API test is similar to an integration test because it is testing the whole system integration swapping external dependencies for internal ones:
- PostgreSQL is replaced by in-memory data implementations (see [UserInMemoryDataHandler](test/com/alexitc/coinalerts/data/UserInMemoryDataHandler.scala)).
- External services are replaced by a faked version (see [FakeEmailService](test/com/alexitc/coinalerts/commons/FakeEmailService.scala)).

These tests are useful for verifying that the API works as expected on the client side, they intention is to cover the use cases that are going to be used on the clients consuming the API.

## Development
Here are some details about the architecure design, they could be useful if you want to understand the code.

### Application results
Most results computed by our code base depend on the [ApplicationError](app/com/alexitc/coinalerts/errors/ApplicationError.scala) trait, it simply represents and error produced by our code that we should be able to handle.

There is a extensive use of [Scalatic Or and Every](http://www.scalactic.org/user_guide/OrAndEvery) for returning alternative error results, while it is similar to scala `Either[L, R]` type, it has a built-in mechanism for returning several errors instead of just 1 and it has a built-in non empty list make our code safer.

It could be tedious to be writing long types for our application results (like `Future[Unit Or Every[ApplicationResult]`), as most of our result types are similar, we have created the following custom types to avoid writing these long result types (see [commons package](app/com/alexitc/coinalerts/commons/package.scala)):
- `ApplicationErrors` - representing a non empty collection of errors.
- `ApplicationResult` - representing a blocking result that accumulates errors.
- `FutureApplicationResult` - representing a non-blocking result that accumulates errors (using scala `Future`).

When working with non-blocking results, the code could get an awful level of nesting while computing a result using several steps, we have created a simple monad transformed called [FutureOr](app/com/alexitc/coinalerts/commons/FutureOr.scala) (most of the code comes from this [gist](https://gist.github.com/atamborrino/5a6b7c014b1f7af0a6bd2c3922e5aec6#file-testscalactic-scala-L44)), you could an usage examplle at [UserService#create()](app/com/alexitc/coinalerts/services/UserService.scala#L26).

### Configuration
There are several components that are configurable, as we already are using play framework, it is very simple to integrate the [typesafe-config](https://github.com/lightbend/config) which loads the configuration from the `application.conf` file (hopefully we will switch to more typesafe configuration library in the future).

We are creating a base trait that could be easily extended for testing, and implementing the config depending on the `Configuration` from play framework, note that no sensitive information should be stored in the configuration files that are being tracked by git, a better approach is to declare them as environment variables or Java system variables.

See [JWTConfig](app/com/alexitc/coinalerts/config/JWTConfig.scala).

### Controllers
For building the HTTP API, we use the [controllers package](app/controllers), the controller classes make extensive use of the [JsonController](app/com/alexitc/coinalerts/commons/JsonController.scala) that help us to build a controllers that receives JSON and produces JSON (it handles application errors as well), it is still experimental and improving frequently.

The controllers could receive a model (that should be deserializable using play-json) and produce model (that is serializable using play-json).

A controller responsibility is very simple:
- Deserialize the input (if any).
- Delegate the action to the proper service.
- Serialize the result using a response HTTP Status that makes sense for the action.

See [UsersController](app/controllers/UsersController.scala).

### Services
The [services package](app/com/alexitc/coinalerts/services) is the one containing most application logic and rules, the controllers depend specifically on this package for performing most actions and retrieving data.
