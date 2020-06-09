package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val intent = intent
        userNickname.text = intent.getStringExtra("name")
        userProfile.setText(intent.getStringExtra("profile"))

    }
}