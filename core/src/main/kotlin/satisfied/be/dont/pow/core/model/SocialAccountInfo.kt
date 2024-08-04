package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.*
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.mongodb.core.mapping.Field
import satisfied.be.dont.pow.core.ValuableEnum

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = KakaoAccountInfo::class, name = "kakao"),
    JsonSubTypes.Type(value = NaverAccountInfo::class, name = "naver"),
    JsonSubTypes.Type(value = GoogleAccountInfo::class, name = "google")
)
abstract class SocialAccountInfo(
    @Field(name = "id")
    @field:Schema(description = "social account id")
    override val id: String,
    @field:Schema(description = "social provider")
    override val type: String,
    @get:JsonIgnore
    @field:Schema(description = "social access token")
    val accessToken: String,
    @get:JsonIgnore
    @field:Schema(description = "social refresh token")
    val refreshToken: String
): Identity {

    enum class SocialAccountType(override val value: String): ValuableEnum {
        KAKAO("kakao"),
        NAVER("naver"),
        GOOGLE("google"),
    }
}

/**
 * 인터페이스로 구성시, 코틀린 문제로 mongodb에서 쿼리 및 역직렬화에 문제가 생김..
 * Id에 대한 어노테이션이 정상동작안하는것으로 여겨짐.
 */
//interface SocialAccountInfo: Identity {
//
//    enum class SocialAccountType(override val value: String): ValuableEnum {
//        KAKAO("kakao"),
//        NAVER("naver"),
//        GOOGLE("google")
//    }
//}
