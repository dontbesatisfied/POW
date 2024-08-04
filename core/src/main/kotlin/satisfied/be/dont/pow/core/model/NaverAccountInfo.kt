package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

//data class NaverAccountInfo(
////    @Field(name = "id")
//    override val id: String,
//    override val type: String = SocialAccountType.NAVER.value,
//    override val accessToken: String,
//    override val refreshToken: String,
//): SocialAccountInfo(id, type, accessToken, refreshToken)

class NaverAccountInfo(
//    @Field(name = "id")
//    @JsonProperty("id")
    id: String,
//    @JsonProperty("type")
    type: String = SocialAccountType.NAVER.value,
    @JsonProperty("accessToken")
    accessToken: String,
    @JsonProperty("refreshToken")
    refreshToken: String
): SocialAccountInfo(id, type, accessToken, refreshToken)