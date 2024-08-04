package satisfied.be.dont.pow.core.model

import com.fasterxml.jackson.annotation.JsonProperty

//data class GoogleAccountInfo(
////    @Field(name = "id")
//    override val id: String,
//    override val type: String = SocialAccountType.GOOGLE.value,
//    override val accessToken: String,
//    override val refreshToken: String,
//): SocialAccountInfo(id, type, accessToken, refreshToken)

class GoogleAccountInfo(
//    @JsonProperty("id")
    id: String,
//    @JsonProperty("type")
    type: String = SocialAccountType.GOOGLE.value,
    @JsonProperty("accessToken")
    accessToken: String,
    @JsonProperty("refreshToken")
    refreshToken: String,
): SocialAccountInfo(id, type, accessToken, refreshToken)