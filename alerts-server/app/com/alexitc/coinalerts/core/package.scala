package com.alexitc.coinalerts

import com.alexitc.coinalerts.commons.FutureApplicationResult

package object core {

  type FuturePaginatedResult[A] = FutureApplicationResult[PaginatedResult[A]]
}
