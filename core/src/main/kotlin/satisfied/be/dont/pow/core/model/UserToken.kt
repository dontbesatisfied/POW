package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.mongodb.core.mapping.Field
import satisfied.be.dont.pow.core.annotation.AutoDeleteBy
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoDeleteBy(User::class, "sys.createdBy")
data class UserToken(
    override val sys: Sys
): IUserToken {

    constructor(user: Refer<User>, accessToken: String, refreshToken: String)
            : this(
                Sys(createdBy = user, updatedBy = user, accessToken = accessToken, refreshToken = refreshToken)
            )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Sys(
        override val createdBy: Refer<User>,
        override val createdAt: Date = Date(),
        override val updatedBy: Refer<User>,
        override val updatedAt: Date = Date(),
        @field:Schema(description = "access token")
        override val accessToken: String,
        @field:Schema(description = "refresh token")
        override val refreshToken: String,
        @Field(name = "id")
        @field:Schema(description = "resource id", example = "Ci0SIv0cYtsy94cy6THeI6HIjG5fnm")
        override val id: String = Identity.generateUid(),
        @field:Schema(description = "resource type", example = "UserToken")
        override val type: String = UserToken::class.simpleName!!
    ): IUserToken.ISys
}
