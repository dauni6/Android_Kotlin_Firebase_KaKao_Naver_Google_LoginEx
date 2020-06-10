package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import com.kakao.auth.*

class KakaoSDKAdapter : KakaoAdapter() {

    //카카오 로그인 관련 세션 설정
    override fun getSessionConfig(): ISessionConfig {
        return object: ISessionConfig {
            override fun isSaveFormData(): Boolean {
                return true
            }

            override fun getAuthTypes(): Array<AuthType> {
                return arrayOf(AuthType.KAKAO_TALK_ONLY)
            }

            override fun isSecureMode(): Boolean {
                return false
            }

            override fun getApprovalType(): ApprovalType? {
               return ApprovalType.INDIVIDUAL
            }

            override fun isUsingWebviewTimer(): Boolean {
                return false
            }

        }
    }
    
    //앱이 가진 정보를 얻기 위한 인터페이스
    override fun getApplicationConfig(): IApplicationConfig {
        return IApplicationConfig {
            GlobalApplication.instance?.getGlobalApplicationContext()
        }
    }
}