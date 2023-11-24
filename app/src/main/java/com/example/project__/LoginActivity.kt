package com.example.project__

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login_join)?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.login)?.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.email)?.text.toString()
            val password = findViewById<EditText>(R.id.password)?.text.toString()

            // null 또는 빈 문자열이 입력되지 않도록 검증
            if (userEmail.isBlank() || password.isBlank()) {
                showErrorDialog("이메일과 비밀번호를 입력하세요.")
                return@setOnClickListener
            }

            doLogin(userEmail, password)
        }
    }

    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val errorMessage = getErrorMessage(task.exception)
                    showErrorDialog(errorMessage)
                }
            }
    }

    private fun getErrorMessage(exception: Exception?): String {
        return when (exception) {
            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "유효하지 않은 사용자입니다."
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "이메일 또는 비밀번호가 올바르지 않습니다."
            is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "이미 가입된 이메일 주소입니다."
            else -> "로그인 실패"
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("로그인 실패")
        alertDialog.setMessage(errorMessage)
        alertDialog.setPositiveButton("확인") { _, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        alertDialog.show()
    }
}