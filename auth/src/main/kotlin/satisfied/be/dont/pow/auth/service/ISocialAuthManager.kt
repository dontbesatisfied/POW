package satisfied.be.dont.pow.auth.service

import satisfied.be.dont.pow.core.model.SocialAccountInfo

// TODO: 소셜로그인 추가 시 추상화
interface ISocialAuthManager {
    suspend fun authorize(): String
    suspend fun getUserInfo(token: String): SocialAccountInfo
    suspend fun getAccountInfo(session: String): SocialAccountInfo?
    // TODO: 구현해야함. 소셜쪽 액세스 토큰 만료시 갱신해서 재처리하는것 구현해야함. http 클라이언트도 교체해야할듯. 에러시 에러바디 역직렬화가 너무 불편한거 아닌가??
    suspend fun unlink(accessToken: String, refreshToken: String)
}