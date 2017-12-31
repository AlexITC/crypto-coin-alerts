package com.alexitc.coinalerts.data.anorm

import java.time.{Instant, OffsetDateTime, ZoneId}

import anorm.SqlParser._
import anorm._
import com.alexitc.coinalerts.models._
import org.postgresql.util.PGobject
import play.api.i18n.Lang

object AnormParsers {

  val parseUserId = str("user_id").map(UserId.apply)
  val parseEmail = str("email")(citextToString).map(UserEmail.apply)
  val parseUserVerificationToken = str("token").map(UserVerificationToken.apply)
  val parseLang = str("lang").map(Lang.apply)

  val parseFixedPriceAlertId = long("fixed_price_alert_id").map(FixedPriceAlertId.apply)
  val parseMarket = str("market").map(Exchange.fromDatabaseString)
  val parseBook = str("book").map(Book.fromString(_).get) // Assumes db value is always properly formatted.
  val parseisGreaterThan = bool("is_greater_than")
  val parsePrice = get[BigDecimal]("price")
  val parseBasePrice = get[BigDecimal]("base_price")

  val parseDailyPriceAlertId = long("daily_price_alert_id").map(DailyPriceAlertId.apply)
  val parseCreatedOn = get[OffsetDateTime]("created_on")(timestamptzToOffsetDateTime)

  val parseUser = (parseUserId ~ parseEmail).map {
    case userId ~ email => User.apply(userId, email)
  }

  val parseUserPreferences = (parseUserId ~ parseLang).map {
    case userId ~ lang => UserPreferences(userId, lang)
  }

  val parseAlert = (parseFixedPriceAlertId ~ parseUserId ~ parseMarket ~ parseBook ~ parseisGreaterThan ~ parsePrice ~ parseBasePrice.?).map {
    case alertId ~ userId ~ market ~ book ~ isGreaterThan ~ price ~ basePrice =>
      FixedPriceAlert(alertId, userId, market, book, isGreaterThan, price, basePrice)
  }

  val parseDailyPriceAlert = (parseDailyPriceAlertId ~ parseUserId ~ parseMarket ~ parseBook ~ parseCreatedOn).map {
    case alerId ~ userId ~ market ~ book ~ createdOn =>
      DailyPriceAlert(alerId, userId, market, book, createdOn)
  }

  val parsePassword = str("password").map(UserHiddenPassword.fromDatabase)

  private def citextToString: Column[String] = Column.nonNull { case (value, meta) =>
    val MetaDataItem(qualified, _, clazz) = meta
    value match {
      case str: String => Right(str)
      case obj: PGobject if "citext" equalsIgnoreCase obj.getType => Right(obj.getValue)
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"))
    }
  }

  private def timestamptzToOffsetDateTime: Column[OffsetDateTime] = Column.nonNull { case (value, meta) =>
    val MetaDataItem(qualified, _, clazz) = meta
    value match {
      case timestamp: java.sql.Timestamp =>
        val instant = Instant.ofEpochMilli(timestamp.getTime)
        val offsetDateTime = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault())
        Right(offsetDateTime)

      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"))
    }
  }
}
