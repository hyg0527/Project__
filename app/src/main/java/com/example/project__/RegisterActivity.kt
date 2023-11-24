package com.example.project__

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 연도, 월, 일 선택에 사용할 데이터 배열 생성
        val years = (1965 until 2010).map { it.toString() }.toTypedArray()
        val months = (1..12).map { it.toString().padStart(2, '0') }.toTypedArray()
        val days = (1..31).map { it.toString().padStart(2, '0') }.toTypedArray()

        // Spinner에 어댑터 설정
        val spinnerYear = findViewById<Spinner>(R.id.spinnerYear)
        val spinnerMonth = findViewById<Spinner>(R.id.spinnerMonth)
        val spinnerDay = findViewById<Spinner>(R.id.spinnerDay)

        val selectedYear = spinnerYear?.selectedItem.toString()
        val selectedMonth = spinnerMonth?.selectedItem.toString()
        val selectedDay = spinnerDay?.selectedItem.toString()

        // Ensure that month and day are represented as two digits
        val formattedMonth = selectedMonth.padStart(2, '0')
        val formattedDay = selectedDay.padStart(2, '0')

        val birthdate = "$selectedYear$formattedMonth$formattedDay"

        // 연도 스피너의 초기 선택 항목을 2000년으로 설정
        val defaultYearPosition = years.indexOf("2000")
        spinnerYear.setSelection(defaultYearPosition)


        spinnerYear.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)
        spinnerMonth.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)
        spinnerDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)

        findViewById<Button>(R.id.join).setOnClickListener {
            val newID = findViewById<EditText>(R.id.join_email)
            val newPW = findViewById<EditText>(R.id.join_password)
            val confirmPW = findViewById<EditText>(R.id.editPWConfirm)

            val idString = newID.text.toString()
            val pwString = newPW.text.toString()
            val cpwString = confirmPW.text.toString()
            if (pwString == cpwString) {
                signUp(idString, pwString, selectedYear, selectedMonth, selectedDay)
            }
            else {
                Toast.makeText(this, "비밀 번호가 일치 하지 않습 니다.", Toast.LENGTH_LONG).show()
                newPW.setText("")
                confirmPW.setText("")
            }
        }
    }
    //회원 가입 함수
    private fun signUp(email: String, password: String, year: String, month: String, day: String) {
        val name = findViewById<EditText>(R.id.join_name).text.toString()
        val nickname = findViewById<TextView>(R.id.join_nickname).text.toString()

        // Firebase Authentication을 사용하여 계정 생성
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 계정 생성 성공 시 사용자 정보 업데이트
                    val user = auth.currentUser
                    val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                        displayName = nickname
                    }

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                // 추가 정보를 받아와서 MainActivity로 전달
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("USER_NAME", name)
                                intent.putExtra(
                                    "USER_BIRTH",
                                    "$year$month$day"
                                )
                                showSuccessDialog()

                                Handler().postDelayed({
                                    startActivity(intent)
                                    finish()
                                }, 1000)
                            } else {
                                // 사용자 정보 업데이트 실패 시 에러 다이얼로그 표시
                                showErrorDialog("다른 닉네임을 선택하세요!")
                            }
                        }
                } else {
                    // FirebaseAuthUserCollisionException은 이미 가입된 이메일 주소인 경우 발생
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        showErrorDialog("이미 가입된 이메일 주소입니다.")
                    } else {
                        // 그 외의 경우에는 일반적인 회원 가입 실패 에러 표시
                        showErrorDialog("회원 가입 실패")
                    }
                }
            }
    }
    private fun showSuccessDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("회원 가입 성공")
        alertDialog.setPositiveButton("확인") { _, _ ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        alertDialog.show()
    }

    private fun showErrorDialog(errorMessage: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("회원 가입 실패")
        alertDialog.setMessage(errorMessage)
        alertDialog.setPositiveButton("확인") { _, _ ->
            findViewById<EditText>(R.id.join_email).text.clear()
            findViewById<EditText>(R.id.join_password).text.clear()
        }
        alertDialog.show()
    }
}