package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.mongodb.core.mapping.Field

//data class KakaoAccountInfo(
////    @Field(name = "id")
//    override val id: String,
//    override val type: String = SocialAccountType.KAKAO.value,
//    override val accessToken: String,
//    override val refreshToken: String,
//): SocialAccountInfo(id, type, accessToken, refreshToken)

class KakaoAccountInfo(
//    @Field(name = "id")
//    @JsonProperty("id")
    id: String,
//    @JsonProperty("type")
    type: String = SocialAccountType.KAKAO.value,
    @JsonProperty("accessToken")
    accessToken: String,
    @JsonProperty("refreshToken")
    refreshToken: String,
): SocialAccountInfo(id, type, accessToken, refreshToken)