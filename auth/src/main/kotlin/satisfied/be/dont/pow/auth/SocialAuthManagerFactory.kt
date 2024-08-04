package satisfied.be.dont.pow.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import satisfied.be.dont.pow.auth.service.*
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.model.SocialAccountInfo

@Component
class SocialAuthManagerFactory {

    @Autowired
    private lateinit var kakaoAccountManager: KakaoSocialAuthManager

    @Autowired
    private lateinit var naverAccountManager: NaverSocialAuthManager

    @Autowired
    private lateinit var googleAccountManager: GoogleSocialAuthManager



    fun get(type: SocialAccountInfo.SocialAccountType): ISocialAuthManager {
        return when(type) {
            SocialAccountInfo.SocialAccountType.KAKAO -> kakaoAccountManager
            SocialAccountInfo.SocialAccountType.NAVER -> naverAccountManager
            SocialAccountInfo.SocialAccountType.GOOGLE -> googleAccountManager
            else -> throw InternalServerError("Not supported social type.", InternalServerError.Codes.MissingData, mapOf("social" to type))
        }
    }
}