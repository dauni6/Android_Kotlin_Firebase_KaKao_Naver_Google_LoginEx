package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import android.content.Context
import com.kakao.auth.IApplicationConfig
import com.kakao.auth.KakaoAdapter

class KakaoSDKAdapter(private val applicationContext: Context): KakaoAdapter() {
    override fun getApplicationConfig() = IApplicationConfig{ applicationContext }

}