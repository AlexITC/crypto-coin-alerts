package com.alexitc.coinalerts.data.anorm.parsers

import java.time.{Instant, OffsetDateTime, ZoneId}

import anorm.SqlParser.get
import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
import org.postgresql.util.PGobject

object CommonParsers {

  val parseCreatedOn = get[OffsetDateTime]("created_on")(timestamptzToOffsetDateTime)

  def timestamptzToOffsetDateTime: Column[OffsetDateTime] = Column.nonNull {
    case (value, meta) =>
      val MetaDataItem(qualified, _, clazz) = meta
      value match {
        case timestamp: java.sql.Timestamp =>
          val instant = Instant.ofEpochMilli(timestamp.getTime)
          val offsetDateTime = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault())
          Right(offsetDateTime)

        case _ =>
          Left(TypeDoesNotMatch(
              s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"))
      }
  }

  def citextToString: Column[String] = Column.nonNull {
    case (value, meta) =>
      val MetaDataItem(qualified, _, clazz) = meta
      value match {
        case str: String => Right(str)
        case obj: PGobject if "citext" equalsIgnoreCase obj.getType => Right(obj.getValue)
        case _ =>
          Left(TypeDoesNotMatch(
              s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"))
      }
  }
}
