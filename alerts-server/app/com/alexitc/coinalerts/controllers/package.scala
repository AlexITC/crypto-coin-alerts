package com.alexitc.coinalerts

import com.alexitc.coinalerts.commons.{AuthenticatedRequestContext, AuthenticatedRequestContextWithModel, PublicRequestContext, PublicRequestContextWithModel}

package object controllers {
  /**
   * These type alias help to not type the long request context names in the controllers
   */
  type PublicCtx = PublicRequestContext
  type PublicCtxModel[T] = PublicRequestContextWithModel[T]
  type AuthCtx = AuthenticatedRequestContext
  type AuthCtxModel[T] = AuthenticatedRequestContextWithModel[T]
}
