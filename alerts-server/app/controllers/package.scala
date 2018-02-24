
import com.alexitc.coinalerts.commons.{AuthenticatedRequestContext, AuthenticatedRequestContextWithModel, PublicRequestContext, PublicRequestContextWithModel}
import com.alexitc.coinalerts.models.UserId

package object controllers {
  /**
   * These type alias help to not type the long request context names in the controllers
   */
  type PublicCtx = PublicRequestContext
  type PublicCtxModel[T] = PublicRequestContextWithModel[T]
  type AuthCtx = AuthenticatedRequestContext[UserId]
  type AuthCtxModel[T] = AuthenticatedRequestContextWithModel[UserId, T]
}
