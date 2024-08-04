package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.mongodb.core.mapping.Field
import satisfied.be.dont.pow.core.annotation.AutoDeleteBy
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@AutoDeleteBy(User::class, "sys.user", true)
data class UserPassword(
    override val sys: Sys,
    override var password: String
): IUserPassword {

    constructor(user: Refer<User>, password: String): this(Sys(user), password)

    data class Sys(
        override val createdBy: Refer<User>,
        override val createdAt: Date,
        override val updatedBy: Refer<User>,
        override val updatedAt: Date,
        @field:JsonIgnore
        override val resetToken: String?,
        @Field(name = "id")
        @field:Schema(description = "resource id", example = "Ci0SIv0cYtsy94cy6THeI6HIjG5fnm")
        override val id: String = Identity.generateUid(),
        @field:Schema(description = "resource type", example = "User")
        override val type: String = UserPassword::class.simpleName!!,
        @field:Schema(description = "resource owner")
        override val user: Refer<User>
    ): IUserPassword.ISys {

        constructor(user: Refer<User>, date: Date = Date()): this(user, date, user, date, null, user = user)
    }
}