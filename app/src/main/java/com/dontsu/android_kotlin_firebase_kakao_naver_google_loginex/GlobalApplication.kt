package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import android.app.Application
import com.kakao.auth.KakaoSDK

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //SDK 초기화
        instance = this
        KakaoSDK.init(KakaoSDKAdapter())
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    fun getGlobalApplicationContext(): GlobalApplication {
        checkNotNull(instance) { throw IllegalStateException("this application does not inherit com.kakao.GlobalApplication") }
        return instance!!
    }

    companion object {
        @Volatile
        var instance: GlobalApplication? = null
    }

}