package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.kakao.auth.ApiErrorCode
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var sessionCallback: SessionCallback

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext
        sessionCallback = SessionCallback()
        Session.getCurrentSession().addCallback(sessionCallback)  // 세션 콜백 등록

        getAppHashKey()

        signInKaKao.setOnClickListener {

            //Session.getCurrentSession().open(AuthType.KAKAO_TALK_ONLY, this) //얘로해도 되고
            Session.getCurrentSession().checkAndImplicitOpen() //얘로 해도 된다. 얘는 앱에 유효한 카카오 로그인 토큰이 있다면 바로 로그인을 시켜주는 함수, 이전에 로그인한 기록이 있다면, 다음 번에 앱을 켰을 때 자동으로 로그인을 시켜주는 것
        }
    }

    private fun getAppHashKey() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //Pie 버전 이상 API level 28
                val sig = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
                 sig.apkContentsSigners.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    val bytes = digest.digest()
                    val hashKey = String(Base64.encode(bytes, Base64.NO_WRAP))
                     Log.d("getHashKey", hashKey)
                }
            } else {//Pie 버전 미만 API level 28 미만
                @Suppress("DEPRECATION")
                val sig = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
                sig.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    val bytes = digest.digest()
                    val hashKey = String(Base64.encode(bytes, Base64.NO_WRAP))
                    Log.d("getHashKey", hashKey)
                }
            }
        } catch (e: Exception) {
            Log.e("hashKey not found", e.message)
            e.printStackTrace()
        }
    }

    // 세션 콜백 구현
    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            UserManagement.getInstance().me(object: MeV2ResponseCallback() {
                override fun onSuccess(result: MeV2Response?) {
                    val intent = Intent(context, SignupActivity::class.java)
                    intent.putExtra("name", result?.nickname)
                    intent.putExtra("profile", result?.profileImagePath)
                    startActivity(intent)
                }

                override fun onSessionClosed(e: ErrorResult?) {
                    Toast.makeText(applicationContext, "세션이 닫힘", Toast.LENGTH_LONG).show()
                    Log.e("Kakao user info closed", e?.errorMessage)
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(e: ErrorResult?) {
                    super.onFailure(e)
                    e?.let {
                        val errorCode = it.errorCode
                        if (errorCode == ApiErrorCode.CLIENT_ERROR_CODE) {
                            Toast.makeText(applicationContext, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(applicationContext,"로그인에 실패하였습니다.: ${it.errorMessage}",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            })
        }

        @SuppressLint("LongLogTag")
        override fun onSessionOpenFailed(e: KakaoException?) {
            Toast.makeText(context, "네트워크 등의 오류로 세션을 불러오지 못 함", Toast.LENGTH_LONG).show()
            e?.printStackTrace()
            Log.e("Kakao Session Callback error", e?.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
       if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
           return
       }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        //세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback)

    }

    //스택오버플로우 참고한 해시키값 얻는 방법(bytesToHex() 는 왜 ???)
    /*private fun getApplicationSignature(packageName: String = context.packageName): List<String> {
        val signatureList: List<String>
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //Pie 버전 이상 API level 28
                // New signature
                val sig = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
                signatureList = if (sig.hasMultipleSigners()) {
                    // Send all with apkContentsSigners
                    sig.apkContentsSigners.map {
                        val digest = MessageDigest.getInstance("SHA")
                        digest.update(it.toByteArray())
                        bytesToHex(digest.digest())
                    }
                } else {
                    // Send one with signingCertificateHistory
                    sig.signingCertificateHistory.map {
                        val digest = MessageDigest.getInstance("SHA")
                        digest.update(it.toByteArray())
                        bytesToHex(digest.digest())
                    }
                }
            } else { //Pie 버전 미만 API level 28 미만
                val sig = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
                signatureList = sig.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    bytesToHex(digest.digest())
                }
            }

            return signatureList
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
        }
        return emptyList()
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hashKey = String(Base64.encode(bytes, 0))
        Log.d("결과", hashKey) //해시키 검거
        val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }*/
}