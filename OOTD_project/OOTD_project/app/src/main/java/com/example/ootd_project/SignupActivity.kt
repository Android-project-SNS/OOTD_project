package com.example.ootd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Integer.parseInt

class SignupActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    val db = Firebase.database
    val userRef = db.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val password1 = findViewById<EditText>(R.id.signPW)
        val password2 = findViewById<EditText>(R.id.signPW2)
        val pwCheck = findViewById<Button>(R.id.pwcheckbutton)
        val submit = findViewById<Button>(R.id.submitButton)

        auth = FirebaseAuth.getInstance()

        // 비밀번호 확인
        pwCheck.setOnClickListener{
            if(password1.text.toString().equals(password2.text.toString())){
                Toast.makeText(this, "비밀번호가 일치 합니다.", Toast.LENGTH_LONG).show()
                submit.isEnabled = true
            }
            else{
                Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show()
                submit.isEnabled = false
            }
        }

        // 회원가입 완료
        submit.setOnClickListener{
            addInformation()
            signUp()
        }
    }
    fun signUp(){
        val pwdValue = findViewById<EditText>(R.id.signPW)
        val emailAddress = findViewById<EditText>(R.id.signID)
        val id = emailAddress.text.toString()
        val password = pwdValue.text.toString()

        auth?.createUserWithEmailAndPassword(id, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 회원정보 생성
                addInformation()
                val signinIntent = Intent(this, LoginActivity::class.java)
                startActivity(signinIntent)
            } else {
                // 회원정보 생성 실패
                println("(${task.exception?.message})")
                when (task.exception?.message) {
                    "The email address is badly formatted."
                    -> Toast.makeText(this, "아이디 형식이 올바르지 않습니다.", Toast.LENGTH_LONG).show()
                    "The email address is already in use by another account."
                    -> Toast.makeText(this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_LONG).show()
                    "The given password is invalid. [ Password should be at least 6 characters ]"
                    -> Toast.makeText(this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 유저 정보 기입 (이름, 닉네임, 키)
    private fun addInformation() {
        val name = findViewById<EditText>(R.id.signName).text.toString()
        val nickname = findViewById<EditText>(R.id.signNickName).text.toString()
        val height = parseInt(findViewById<EditText>(R.id.signHeight).text.toString())

        // 유저 정보 맵
        val userMap = hashMapOf(
            "name" to name,
            "nickname" to nickname,
            "height" to height
        )

        val usersInfo = userRef.push()
        usersInfo.setValue(userMap)
    }
}