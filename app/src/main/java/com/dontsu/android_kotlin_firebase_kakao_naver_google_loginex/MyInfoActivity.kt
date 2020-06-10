package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.kakao.auth.KakaoSDK
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.UnLinkResponseCallback
import kotlinx.android.synthetic.main.activity_signup.*


//로그아웃 버튼을 누르고 앱연결 끊기를 누르면 앱연결끊기도 동작하지 않는 문제가 있음
//로그아웃 버튼 누르고 앱 연결끊기 버튼을 누르면 세션이 닫혀있어서 연결을 끊을 수 없다고 나옴
class MyInfoActivity : AppCompatActivity() {
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        context = applicationContext

        val intent = intent
        userNickname.text = intent.getStringExtra("name")
        userEmail.text = intent.getStringExtra("email")
        userProfile.setText(intent.getStringExtra("profile"))

        //로그아웃
        logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("로그아웃할까요?")
                .setPositiveButton("네") { dialog, which ->
                    UserManagement.getInstance().requestLogout(object: LogoutResponseCallback() {
                        //onCompleteLogout 작동은 안 하고 그냥 세션만 꺼짐
                        override fun onCompleteLogout() {}
                        override fun onSuccess(result: Long?) {
                            super.onSuccess(result)
                            Toast.makeText(context, "로그아웃합니다", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
                }
                .setNegativeButton("아니요") { dialog, which -> }
                .show()
        }

        //앱 연결해제
        unlink.setOnClickListener { 
            AlertDialog.Builder(this)
                .setTitle("연결끊기")
                .setPositiveButton("네") { dialog, which ->
                      UserManagement.getInstance().requestUnlink(object: UnLinkResponseCallback() {
                      override fun onSuccess(result: Long?) {
                          Toast.makeText(context, "앱과 카카오톡 연결을 끊었습니다 $result", Toast.LENGTH_SHORT).show()
                          finish()
                      }

                      override fun onSessionClosed(errorResult: ErrorResult?) {
                          Toast.makeText(context, "세션이 닫혀있어서 실패했습니다", Toast.LENGTH_SHORT).show()
                          Log.e("session closed", errorResult?.errorMessage)
                      }

                      override fun onFailure(errorResult: ErrorResult?) {
                          super.onFailure(errorResult)
                          Toast.makeText(context, "연결 해제에 실패했습니다", Toast.LENGTH_SHORT).show()
                          Log.e("session Unlink error", errorResult?.errorMessage)
                      }

                  })
                }.setNegativeButton("아니요") {dialog, which ->  }
                .show()
        }
    }
}