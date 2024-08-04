package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.bson.Document
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.stereotype.Component
import satisfied.be.dont.pow.core.extension.Json
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
    override val sys: Sys,
    @field:Schema(description = "user name", example = "powpow")
    override val name: String,
    @field:Schema(description = "user email", example = "benotsatisfied@gmail.com")
    override var email: String?
): IUser {

    constructor(name: String, sns: SocialAccountInfo?): this(Sys(sns = sns, isActivated = true), name, null)
    constructor(name: String, email: String, activateToken: String): this(Sys(activateToken = activateToken), name ,email)


    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Sys(
        @field:JsonIgnore
        override val isActivated: Boolean = false,
        @field:JsonIgnore
        override val activateToken: String? = null,
        @field:JsonIgnore
        override val isAdmin: Boolean = false,
        @Field(name = "id")
        @field:Schema(description = "resource id", example = "Ci0SIv0cYtsy94cy6THeI6HIjG5fnm")
        override val id: String = Identity.generateUid(),
        @field:Schema(description = "resource type", example = "User")
        override val type: String = User::class.simpleName!!,
        override val createdBy: Refer<User>,
        override val createdAt: Date,
        override val updatedBy: Refer<User>,
        override val updatedAt: Date,
        override val sns: SocialAccountInfo? = null,
    ): IUser.ISys {

         internal constructor(id: String = Identity.generateUid(), sns: SocialAccountInfo? = null, isActivated: Boolean = false, activateToken: String? = null, date: Date = Date()): this(
             isActivated, activateToken, false, id, User::class.simpleName!!, Refer.of(id), date, Refer.of(id), date, sns
         )
    }



    @Component
    @ReadingConverter
    class SocialAccountInfoConverter: Converter<Document, SocialAccountInfo?> {
        override fun convert(dbObject: Document): SocialAccountInfo? {
            return Json.fromJson<SocialAccountInfo>(dbObject.toJson())
        }
    }
}
