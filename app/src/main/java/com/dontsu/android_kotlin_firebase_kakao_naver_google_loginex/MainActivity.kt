package com.dontsu.android_kotlin_firebase_kakao_naver_google_loginex

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext

        val result = getApplicationSignature()
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("결과", "${Base64.getEncoder().encode(result.toString().toByteArray())}")
        }*/


    }

    private fun getApplicationSignature(packageName: String = context.packageName): List<String> {
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
        val something = String(Base64.encode(bytes, 0))
        Log.d("결과", something)
        val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}