package com.alexitc.coinalerts.core

import play.api.libs.json.{Json, OWrites, Writes}

case class PaginatedResult[T](offset: Offset, limit: Limit, total: Count, data: List[T])
object PaginatedResult {
  implicit def writes[T](implicit writesT: Writes[T]): Writes[PaginatedResult[T]] = OWrites[PaginatedResult[T]] { result =>
    Json.obj(
      "offset" -> result.offset,
      "limit" -> result.limit,
      "total" -> result.total,
      "data" -> result.data
    )
  }
}
