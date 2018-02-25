package com.alexitc.coinalerts

import com.alexitc.playsonify.core.FutureApplicationResult

package object core {

  type FuturePaginatedResult[A] = FutureApplicationResult[PaginatedResult[A]]
}
