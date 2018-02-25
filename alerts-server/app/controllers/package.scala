
import com.alexitc.coinalerts.models.UserId
import com.alexitc.playsonify.models._

package object controllers {
  /**
   * These type alias help to not type the long request context names in the controllers
   */
  type PublicCtx = PublicContext
  type PublicCtxModel[T] = PublicContextWithModel[T]
  type AuthCtx = AuthenticatedContext[UserId]
  type AuthCtxModel[T] = AuthenticatedContextWithModel[UserId, T]
}
