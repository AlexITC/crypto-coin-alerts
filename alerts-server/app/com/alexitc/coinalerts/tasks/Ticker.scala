package com.alexitc.coinalerts.tasks

import com.alexitc.coinalerts.models.Book

case class Ticker(
    book: Book,
    currentPrice: BigDecimal
)
